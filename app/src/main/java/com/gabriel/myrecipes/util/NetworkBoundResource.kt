package com.gabriel.myrecipes.util

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.annotation.MainThread
import androidx.annotation.WorkerThread
import com.gabriel.myrecipes.AppExecutors
import com.gabriel.myrecipes.request.responses.ApiEmptyResponse
import com.gabriel.myrecipes.request.responses.ApiErrorResponse
import com.gabriel.myrecipes.request.responses.ApiResponse
import com.gabriel.myrecipes.request.responses.ApiSuccessResponse

// CacheObject: Type for the Resource data.
// RequestObject: Type for the API response.
abstract class NetworkBoundResource<CacheObject, RequestObject>(private val appExecutors: AppExecutors) {
    private val results = MediatorLiveData<ResourceData<CacheObject>>()

    init {
        //update LiveData for loading status
        results.value = ResourceData.loading(null)
        //Observe live data from cache
        val dbSource = loadFromDb()

        results.addSource(dbSource) { cacheObject ->
            //stop observing the local db
            results.removeSource(dbSource)
            if (shouldFetch(cacheObject)) {
                //get data from network
                fetchFromNetwork(dbSource)
            } else {
                results.addSource(dbSource) { cacheObject ->
                    setValueResource(ResourceData.success(cacheObject))
                }
            }
        }
    }

    /**
     * 1) observe the local database
     * 2) if <condition> then query the network
     * 3) stop observing the local db
     * 4) insert new data into local database
     * 5) begin observing local database to see the refreshed data from network
     * @param dbSource
     */
    private fun fetchFromNetwork(dbSource: LiveData<CacheObject>) {
        //updating the data for the loading status
        results.addSource(dbSource) { cacheObject ->
            setValueResource(ResourceData.loading(cacheObject))
        }
        val apiResponse: LiveData<ApiResponse<RequestObject>> = createCall()

        results.addSource(apiResponse) { requestApiResponse ->
            results.removeSource(dbSource)
            results.removeSource(apiResponse)
            /**
             * 1) ApiSuccessResponse
             * 2) ApiErrorResponse
             * 3) ApiEmptyResponse
             */
            when (requestApiResponse) {
                is ApiSuccessResponse -> {
                    appExecutors.mDiskIO.execute {
                        //save the response to the local database
                        saveCallResult(processResponse(requestApiResponse))
                        //We request again a new live data
                        appExecutors.mainThreadExecutor.execute {
                            results.addSource(loadFromDb()) { newCacheObject ->
                                if (!isApiCallExhausted(requestApiResponse.body)) {
                                    setValueResource(ResourceData.success(newCacheObject))
                                } else {
                                    setValueResource(ResourceData.exhausted(newCacheObject))
                                }
                            }
                        }
                    }
                }

                is ApiEmptyResponse -> {
                    appExecutors.mainThreadExecutor.execute {
                        results.addSource(loadFromDb()) { cacheData ->
                            setValueResource(ResourceData.success(cacheData))
                        }
                    }
                }

                is ApiErrorResponse -> {
                    results.addSource(dbSource) { cacheData ->
                        setValueResource(ResourceData.error(requestApiResponse.errorMessage, cacheData))
                    }
                }
            }

        }

    }

    open fun isApiCallExhausted(cacheObject: RequestObject): Boolean {
        return false
    }

    @WorkerThread
    private fun processResponse(response: ApiSuccessResponse<RequestObject>): RequestObject {
        return response.body
    }

    private fun setValueResource(newValue: ResourceData<CacheObject>) {
        if (results.value != newValue) {
            results.value = newValue
        }
    }

    // Called to save the result of the API response into the database
    @WorkerThread
    internal abstract fun saveCallResult(item: RequestObject)

    // Called with the data in the database to decide whether to fetch
    // potentially updated data from the network.
    @MainThread
    protected abstract fun shouldFetch(data: CacheObject?): Boolean

    // Called to get the cached data from the database.
    @MainThread
    protected abstract fun loadFromDb(): LiveData<CacheObject>

    // Called to create the API call.
    @MainThread
    protected abstract fun createCall(): LiveData<ApiResponse<RequestObject>>

    // Returns a LiveData object that represents the resource that's implemented
    // in the base class.
    fun asLiveData(): LiveData<ResourceData<CacheObject>> = results
}