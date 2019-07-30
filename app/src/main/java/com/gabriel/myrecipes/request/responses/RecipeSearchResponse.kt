package com.gabriel.myrecipes.request.responses

import com.gabriel.myrecipes.models.Recipe
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class RecipeSearchResponse(
    @SerializedName("count") @Expose val count: Int, @SerializedName("error") @Expose val error: String?,
    @SerializedName("recipes") @Expose val recipes: MutableList<Recipe>
) {
}