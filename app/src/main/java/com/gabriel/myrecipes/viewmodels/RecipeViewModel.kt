package com.gabriel.myrecipes.viewmodels

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.gabriel.myrecipes.models.Recipe
import com.gabriel.myrecipes.repository.RecipeRepository

class RecipeViewModel : ViewModel() {
    private val mRecipeRepository = RecipeRepository
    val mRecipe: MutableLiveData<Recipe> = mRecipeRepository.mRecipe
    val mRecipeRequestTimeOut = mRecipeRepository.mRecipeRequestTimeOut
    var recipeId: String = ""

    fun searchRecipeById(id: String) {
        recipeId = id
        mRecipeRepository.searchRecipeById(id)
    }

}