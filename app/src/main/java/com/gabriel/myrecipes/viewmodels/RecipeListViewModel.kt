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
    val queryExhausted = "No more results..."
    //query extras
    private var isQueryExhausted: Boolean = false
    private var isPerformingQuery = false
    var pageNumber: Int = 0
    private var query: String = ""
    private var cancelRequest = false
    private var requestStartTime = 0L


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
        requestStartTime = System.currentTimeMillis()
        isPerformingQuery = true
        viewState.value = ViewState.RECIPES
        val repositorySource = recipeRepository.searchRecipes(query, pageNumber)
        recipes.addSource(repositorySource) { listResource ->
            if (!cancelRequest) {
                //react to the data, do something before send it back to the UI
                if (listResource != null) {
                    recipes.value = listResource
                    Log.d("Gabriel", "get the size of listResource of data FIRST - 1 ${listResource.data?.size} ")
                    Log.d(
                        "Gabriel",
                        "get the size of recipes.value.data.size of data FIRST - 2 ${recipes.value?.data?.size} "
                    )
                    when (listResource.status) {
                        ResourceData.Status.SUCCESS -> {
                            isPerformingQuery = false
                            if (listResource.data != null) {
                                Log.d(
                                    "Gabriel",
                                    "get the size of listResource of data SECOND - 1 ${listResource.data.size} "
                                )
                                Log.d(
                                    "Gabriel",
                                    "get the size of  recipes.value.data.size of data SECOND - 2 ${recipes.value?.data?.size} "
                                )
                                if (listResource.data.isEmpty()) {
                                    Log.d(
                                        "Gabriel",
                                        "FINALLY INSIDE value of recipe.value ${recipes.value?.data?.size} and value of listResource.data ${listResource.data.size} "
                                    )
                                    recipes.value =
                                        ResourceData(ResourceData.Status.ERROR, listResource.data, queryExhausted)
                                    isQueryExhausted = true
                                }
                            }
                            recipes.removeSource(repositorySource)
                        }

                        ResourceData.Status.ERROR -> {
                            Log.d(
                                "Gabriel",
                                "onChanged: Request Time ERROR CASE ${(System.currentTimeMillis() - requestStartTime) / 1000} seconds"
                            )
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