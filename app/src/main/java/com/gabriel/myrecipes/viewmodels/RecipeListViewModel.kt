package com.gabriel.myrecipes.viewmodels

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.gabriel.myrecipes.repository.RecipeRepository

class RecipeListViewModel : ViewModel() {
    enum class ViewState { CATEGORIES, RECIPES }

     val viewState = MutableLiveData<ViewState>()

    init {
        viewState.value = ViewState.CATEGORIES
    }

    /****
     *
     */
    private val mRecipeRepository = RecipeRepository
    var mIsViewingRecipes = false
    val mRecipes = mRecipeRepository.mRecipesMediatorLiveData
    var mIsPerformingQuery = false
    val isQueryExhausted = mRecipeRepository.mIsQueryExhausted


    fun searchRecipesApi(query: String, pageNumber: Int) {
        mIsViewingRecipes = true
        mIsPerformingQuery = true
        mRecipeRepository.searchRecipesApi(query, if (pageNumber == 0) 1 else pageNumber)
    }

    fun searchNextPage() {
        if (!mIsPerformingQuery && mIsViewingRecipes) {
            isQueryExhausted.value?.let { exhausted ->
                if (!exhausted) {
                    mRecipeRepository.searchNextPage()
                }
            }
        }
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