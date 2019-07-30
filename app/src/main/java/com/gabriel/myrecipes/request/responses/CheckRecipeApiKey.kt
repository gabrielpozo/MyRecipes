package com.gabriel.myrecipes.request.responses

open class CheckRecipeApiKey {

    companion object {
        fun isRecipeKeyValid(response: RecipeSearchResponse): Boolean {
            return response.error == null
        }

        fun isRecipeKeyValid(response: RecipeResponse): Boolean {
            return response.error == null
        }
    }

}