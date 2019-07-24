package com.gabriel.myrecipes.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.gabriel.myrecipes.models.Recipe
import com.gabriel.myrecipes.repositories.RecipeRepository
import com.gabriel.myrecipes.util.ResourceData

class RecipeListViewModel(application: Application) : AndroidViewModel(application) {
    enum class ViewState { CATEGORIES, RECIPES }

    val viewState = MutableLiveData<ViewState>()
    val recipes = MediatorLiveData<ResourceData<List<Recipe>>>()
    private val recipeRepository = RecipeRepository(application)
    val queryExhausted = "No more results"
    //query extras
    private var isQueryExhausted: Boolean = false
    private var isPerformingQuery = false
    var pageNumber: Int = 0
    private var query: String = ""


    init {
        viewState.value = ViewState.CATEGORIES
    }


    fun searchRecipesApi(queryRecipes: String, page: Int) {
        if (!isPerformingQuery) {
            pageNumber = if (page == 0) 1 else page
            query = queryRecipes
            isQueryExhausted = false
            executeSearch()
        }
    }

    fun searchNextPage() {
        if (!isQueryExhausted && !isPerformingQuery) {
            pageNumber++
            executeSearch()
        }
    }

    private fun executeSearch() {
        isPerformingQuery = true
        viewState.value = ViewState.RECIPES
        val repositorySource = recipeRepository.searchRecipes(query, pageNumber)
        recipes.addSource(repositorySource) { listResource ->
            //react to the data, do something before send it back to the UI
            if (listResource != null) {
                recipes.value = listResource
                when (listResource.status) {
                    ResourceData.Status.SUCCESS -> {
                        isPerformingQuery = false
                        if (listResource.data != null) {
                            if (listResource.data.isEmpty()) {
                                Log.d("Gabriel", "Query is exhausted!")
                                recipes.value =
                                    ResourceData(ResourceData.Status.ERROR, listResource.data, queryExhausted)
                            }
                        }
                        recipes.removeSource(repositorySource)
                    }

                    ResourceData.Status.ERROR -> {
                        isPerformingQuery = false
                        recipes.removeSource(repositorySource)
                    }
                }

            } else {
                recipes.removeSource(repositorySource)
            }

        }
    }
    /****
     *
     */

}