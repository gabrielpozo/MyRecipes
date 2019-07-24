package com.gabriel.myrecipes.viewmodels

import android.app.Application
import androidx.lifecycle.*
import com.gabriel.myrecipes.models.Recipe
import com.gabriel.myrecipes.repositories.RecipeRepository
import com.gabriel.myrecipes.util.Resource
import com.gabriel.myrecipes.util.ResourceData

class RecipeListViewModel(application: Application) : AndroidViewModel(application) {
    enum class ViewState { CATEGORIES, RECIPES }

    val viewState = MutableLiveData<ViewState>()
    val recipes = MediatorLiveData<ResourceData<List<Recipe>>>()
    private val recipeRepository = RecipeRepository(application)

    init {
        viewState.value = ViewState.CATEGORIES
    }


    fun searchRecipesApi(query: String, pageNumber: Int) {
        val repositorySource = recipeRepository.searchRecipes(query, pageNumber)
        recipes.addSource(repositorySource) { listResource ->
            //react to the data, do something before send it back to the UI
            recipes.value = listResource

        }
    }
    /****
     *
     */

}