package com.gabriel.myrecipes.request.responses

import com.gabriel.myrecipes.models.Recipe
import com.google.gson.annotations.SerializedName

data class RecipeResponse(@SerializedName("recipe") val recipe: Recipe) {}