package com.gabriel.myrecipes.request.responses

open class CheckRecipeApiKey {
    companion object {
        fun <T> isRecipeKeyValid(body: T?): Boolean {
            if (body is RecipeSearchResponse) {
                return body.error == null
            }
            if (body is RecipeResponse) {
                return body.error == null
            }
            return false
        }
    }

}