package com.gabriel.myrecipes.viewmodels

import android.app.Application
import androidx.lifecycle.*
import com.gabriel.myrecipes.models.Recipe
import com.gabriel.myrecipes.repositories.RecipeRepository
import com.gabriel.myrecipes.util.ResourceData

class RecipeListViewModel(application: Application) : AndroidViewModel(application) {
    enum class ViewState { CATEGORIES, RECIPES }

    val viewState = MutableLiveData<ViewState>()
    val recipes = MediatorLiveData<ResourceData<List<Recipe>>>()
    private val recipeRepository = RecipeRepository(application)
    //query extras
    private var isQueryExhausted: Boolean = false
    private var isPerformingQuery = false
    var pageNumber: Int = 0
    private var query: String = ""
    private var cancelRequest = false


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
            if (!cancelRequest) {
                if (listResource != null) {
                    recipes.value = listResource
                    when (listResource.status) {
                        ResourceData.Status.SUCCESS -> {
                            isPerformingQuery = false
                            recipes.removeSource(repositorySource)
                        }

                        ResourceData.Status.ERROR -> {
                            if (listResource.data == null) {
                                isQueryExhausted = true
                            }
                            isPerformingQuery = false
                            recipes.removeSource(repositorySource)
                        }

                        ResourceData.Status.EXHAUSTED -> {
                            isQueryExhausted = true
                            isPerformingQuery = false
                            recipes.removeSource(repositorySource)
                        }
                    }
                } else {
                    recipes.removeSource(repositorySource)
                }
            } else {
                recipes.removeSource(repositorySource)
            }
        }

    }

    fun cancelSearchRequest() {
        if (isPerformingQuery) {
            cancelRequest = true
            isPerformingQuery = false
            pageNumber = 1
        }
    }
}