package com.gabriel.myrecipes.repository

import android.arch.lifecycle.MutableLiveData
import android.util.Log
import com.gabriel.myrecipes.models.Recipe
import com.gabriel.myrecipes.request.RecipeApiClient

object RecipeRepository {
    private val mRecipeClient = RecipeApiClient
    private lateinit var query: String
    private var page: Int = 1
    val mRecipes: MutableLiveData<MutableList<Recipe>> = mRecipeClient.mRecipes
    val mRecipeRequestTimeOut = RecipeApiClient.mRecipeRequestTimeOut

    val mRecipe: MutableLiveData<Recipe> = mRecipeClient.mRecipe

    fun searchRecipesApi(querySearch: String, pageNumber: Int) {
        query = querySearch
        page = pageNumber
        Log.d("Gabriel", "Number of page: $page")
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