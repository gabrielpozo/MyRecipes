package com.gabriel.myrecipes.viewmodels

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.gabriel.myrecipes.models.Recipe
import com.gabriel.myrecipes.repository.RecipeRepository

class RecipeListViewModel : ViewModel() {
    private val mRecipeRepository = RecipeRepository
    val mRecipes: MutableLiveData<MutableList<Recipe>> = mRecipeRepository.mRecipes

    fun searchRecipesApi(query: String, pageNumber: Int) {
        val page = if (pageNumber == 0) 1 else pageNumber
        mRecipeRepository.searchRecipesApi(query, page)
    }
}