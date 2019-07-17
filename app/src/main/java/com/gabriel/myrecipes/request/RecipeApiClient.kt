package com.gabriel.myrecipes.request

import android.arch.lifecycle.MutableLiveData
import com.gabriel.myrecipes.AppExecutors
import com.gabriel.myrecipes.models.Recipe
import com.gabriel.myrecipes.request.responses.RecipeResponse
import com.gabriel.myrecipes.request.responses.RecipeSearchResponse
import com.gabriel.myrecipes.util.Constants
import retrofit2.Call
import java.io.IOException

import java.util.concurrent.Future
import java.util.concurrent.TimeUnit

object RecipeApiClient {
    val mRecipes = MutableLiveData<MutableList<Recipe>>()
    private var retrieveRecipesRunnable: RetrieveRecipesRunnable? = null
    val mRecipe = MutableLiveData<Recipe>()
    val mRecipeRequestTimeOut = MutableLiveData<Boolean>()
    private var retrieveRecipeRunnable: RetrieveRecipeRunnable? = null

    fun searchRecipesApi(query: String, pageNumber: Int) {
        retrieveRecipesRunnable?.let { retrieveRecipesRunnable = null }
        retrieveRecipesRunnable = RetrieveRecipesRunnable(query, pageNumber)
        val handler: Future<*> = AppExecutors.mNetworkIO.submit(retrieveRecipesRunnable)

        AppExecutors.mNetworkIO.schedule({
            //let the user know it is timed out
            handler.cancel(true)
        }, Constants.network_timeout, TimeUnit.MILLISECONDS)
    }

    fun searchRecipeById(recipeId: String) {
        retrieveRecipeRunnable?.let { retrieveRecipeRunnable = null }
        retrieveRecipeRunnable = RetrieveRecipeRunnable(recipeId)
        val handler: Future<*> = AppExecutors.mNetworkIO.submit(retrieveRecipeRunnable)

        mRecipeRequestTimeOut.value = false
        AppExecutors.mNetworkIO.schedule({
            mRecipeRequestTimeOut.postValue(true)
            handler.cancel(true)
        }, Constants.network_timeout, TimeUnit.MILLISECONDS)
    }

    private class RetrieveRecipesRunnable(
        private val query: String,
        private val pageNumber: Int,
        private var cancelRequest: Boolean = false
    ) : Runnable {
        override fun run() {
            try {
                val response = getRecipes(query, pageNumber).execute()
                if (cancelRequest) return
                if (response.code() == 200) {
                    response.body()?.let { recipeSearchResponse ->
                        val listRecipes: MutableList<Recipe> = ArrayList(recipeSearchResponse.recipes)
                        if (pageNumber == 1) {
                            mRecipes.postValue(listRecipes)
                        } else {
                            val currentRecipes: MutableList<Recipe>? = mRecipes.value
                            currentRecipes?.addAll(listRecipes)
                            mRecipes.postValue(currentRecipes)
                        }
                    }
                } else {
                    mRecipe.postValue(null)
                }
            } catch (e: IOException) {
                e.printStackTrace()
                mRecipe.postValue(null)
            }
        }

        private fun getRecipes(query: String, pageNumber: Int): Call<RecipeSearchResponse> {
            return ServiceGenerator.recipeApi.searchRecipe(Constants.API_KEY5, query, pageNumber.toString())
        }

        fun cancelRequest() {
            cancelRequest = true
        }

    }

    private class RetrieveRecipeRunnable(
        private val recipeId: String,
        private var cancelRequest: Boolean = false
    ) : Runnable {
        override fun run() {
            try {
                val response = getRecipe(recipeId).execute()
                if (cancelRequest) return
                if (response.code() == 200) {
                    response.body()?.let { recipeResponse ->
                        mRecipe.postValue(recipeResponse.recipe)
                    }
                } else {
                    mRecipes.postValue(null)
                }
            } catch (e: IOException) {
                e.printStackTrace()
                mRecipes.postValue(null)
            }
        }

        private fun getRecipe(recipeId: String): Call<RecipeResponse> {
            return ServiceGenerator.recipeApi.getRecipe(Constants.API_KEY5, recipeId)
        }

        fun cancelRequest() {
            cancelRequest = true
        }

    }

    fun cancelRequest() {
        retrieveRecipesRunnable?.cancelRequest()
    }
}