package com.gabriel.myrecipes.viewmodels

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.gabriel.myrecipes.models.Recipe
import com.gabriel.myrecipes.repository.RecipeRepository

class RecipeListViewModel : ViewModel() {
    val mRecipes: MutableLiveData<List<Recipe>> = RecipeRepository.mRecipes
}