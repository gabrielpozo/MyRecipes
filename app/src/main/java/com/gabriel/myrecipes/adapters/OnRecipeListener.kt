package com.gabriel.myrecipes.adapters

interface OnRecipeListener {
    fun onRecipeClick(position: Int)
    fun onCategoryClick(category: String)
}