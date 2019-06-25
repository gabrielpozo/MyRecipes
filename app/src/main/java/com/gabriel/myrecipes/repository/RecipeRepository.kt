package com.gabriel.myrecipes.repository

import android.arch.lifecycle.MutableLiveData
import com.gabriel.myrecipes.models.Recipe
import com.gabriel.myrecipes.request.RecipeApiClient

object RecipeRepository {
    private val mRecipeClient = RecipeApiClient
    val mRecipes: MutableLiveData<MutableList<Recipe>> = mRecipeClient.mRecipe

    fun searchRecipesApi(query: String, pageNumber: Int) {
        val page = if (pageNumber == 0) 1 else pageNumber
        mRecipeClient.searchRecipesApi(query, page)
    }
}