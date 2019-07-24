package com.gabriel.myrecipes.util

import androidx.lifecycle.LiveData
import com.gabriel.myrecipes.request.responses.ApiResponse
import retrofit2.CallAdapter
import retrofit2.Retrofit
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

class LiveDataCallAdapterFactory : CallAdapter.Factory() {

    /**
     * Ths method performs number of checks and then returns the Response type for the Retrofit Request
     * (@bodyType is the ResponseType. It can be recipe Response or RecipeSearch Response)
     *
     * CHECK #1? returnType return LiveData
     * CHECK #2? Type LiveData<T> is ApiResponse.class
     * CHECK #3? Make sure ApiResponse is parameterized. AKA: ApiResponse<T> exists
     */

    override fun get(returnType: Type, annotations: Array<Annotation>, retrofit: Retrofit): CallAdapter<*, *>? {
        //check number 1 -> Make sure the callAdapter is returning a type of LiveData
        if (getRawType(returnType) != LiveData::class.java) {
            return null
        }
        //check number 2 -> Type that LiveData is wrapping
        val observableType = getParameterUpperBound(0, returnType as ParameterizedType)
        //check if it's of Type ApiResponse
        val rawObservableType = getRawType(observableType)
        if (rawObservableType != ApiResponse::class.java) {
            throw  IllegalArgumentException("type must be defined resource")
        }
        // check 3 -> check if ApiResponse is parameterized. AKA: Does ApiResponse<T> exist?(must wrap around T)
        // FYI -> T is either a recipeResponse or T will be a RecipeSearchResponse
        if (observableType !is ParameterizedType) {
            throw  IllegalArgumentException("resource must be parameterized")
        }

        val bodyType = getParameterUpperBound(0, observableType)
        return LiveDataCallAdapter<Type>(bodyType)

    }
}