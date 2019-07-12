package com.gabriel.myrecipes.viewmodels

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.gabriel.myrecipes.models.Recipe
import com.gabriel.myrecipes.repository.RecipeRepository

class RecipeListViewModel : ViewModel() {
    private val mRecipeRepository = RecipeRepository
    var mIsViewingRecipes = false
    val mRecipes: MutableLiveData<MutableList<Recipe>> = mRecipeRepository.mRecipes
    var mIsPerformingQuery = false

    fun searchRecipesApi(query: String, pageNumber: Int) {
        mIsViewingRecipes = true
        mIsPerformingQuery = true
        val page = if (pageNumber == 0) 1 else pageNumber
        mRecipeRepository.searchRecipesApi(query, page)
    }

    fun onBackPressed(): Boolean {
        if (mIsPerformingQuery) {
            mRecipeRepository.cancelRequest()
            mIsPerformingQuery = false
        }
        if (mIsViewingRecipes) {
            mIsViewingRecipes = false
            return false
        }
        return true
    }
}