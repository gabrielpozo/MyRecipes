package com.gabriel.myrecipes.repository

import android.arch.lifecycle.MutableLiveData
import com.gabriel.myrecipes.models.Recipe
import com.gabriel.myrecipes.request.RecipeApiClient

object RecipeRepository {
    val mRecipes: MutableLiveData<List<Recipe>> = RecipeApiClient.mRecipe
}