package com.gabriel.myrecipes.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.gabriel.myrecipes.models.Recipe
import com.gabriel.myrecipes.repositories.RecipeRepository
import com.gabriel.myrecipes.util.ResourceData

class RecipeViewModel(application: Application) : AndroidViewModel(application) {
    private val recipeRepository = RecipeRepository(application)


    fun searchRecipeApi(recipeId: String): LiveData<ResourceData<Recipe>> {
        return recipeRepository.searchRecipeApi(recipeId)
    }

}