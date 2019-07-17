package com.gabriel.myrecipes.repository

import android.arch.lifecycle.MediatorLiveData
import android.arch.lifecycle.MutableLiveData
import com.gabriel.myrecipes.models.Recipe
import com.gabriel.myrecipes.request.RecipeApiClient

object RecipeRepository {
    private val mRecipeClient = RecipeApiClient
    private lateinit var query: String
    private var page: Int = 1
    val mRecipeRequestTimeOut = RecipeApiClient.mRecipeRequestTimeOut
    val mIsQueryExhausted: MutableLiveData<Boolean> = MutableLiveData()
    val mRecipe: MutableLiveData<Recipe> = mRecipeClient.mRecipe
    val mRecipesMediatorLiveData = MediatorLiveData<MutableList<Recipe>>()

    init {
        initMediators()
    }


    private fun initMediators() {
        mRecipesMediatorLiveData.addSource(mRecipeClient.mRecipes) { recipes ->
            mRecipesMediatorLiveData.value = recipes
            doneQuery(recipes)
        }
    }

    private fun doneQuery(list: MutableList<Recipe>?) {
        if (list != null) {
            /**It means the query is exhausted*/
            if ((list.size % 30) != 0) {
                mIsQueryExhausted.value = true
            }
        } else {
            mIsQueryExhausted.value = true
            TODO("search database cache")
        }
    }

    fun searchRecipesApi(querySearch: String, pageNumber: Int) {
        query = querySearch
        page = pageNumber
        mIsQueryExhausted.value = false
        mRecipeClient.searchRecipesApi(query, page)
    }

    fun searchNextPage() {
        searchRecipesApi(query, page + 1)
    }

    fun cancelRequest() {
        mRecipeClient.cancelRequest()
    }

    fun searchRecipeById(recipeId: String) {
        mRecipeClient.searchRecipeById(recipeId)
    }
}