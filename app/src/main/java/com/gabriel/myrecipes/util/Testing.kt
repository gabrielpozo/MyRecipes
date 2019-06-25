package com.gabriel.myrecipes.util

import android.util.Log
import com.gabriel.myrecipes.models.Recipe

class Testing {
    companion object {
        fun printRecipes(recipes: MutableList<Recipe>, tag: String) {
            recipes.forEach { recipe ->
                Log.d(tag, "onChanged ${recipe.title}")
            }
        }
    }
}