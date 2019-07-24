package com.gabriel.myrecipes.adapters

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.RequestManager
import com.gabriel.myrecipes.R
import com.gabriel.myrecipes.models.Recipe
import com.gabriel.myrecipes.util.Constants

class RecipeRecyclerViewAdapter(
    private val mOnRecipeListener: OnRecipeListener,
    private val requestManager: RequestManager
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var mRecipes: MutableList<Recipe>? = null
    private val recipeType = 1
    private val loadingType = 2
    private val categoryType = 3
    private val exhaustedType = 4

    override fun onCreateViewHolder(viewGroup: ViewGroup, itemType: Int): RecyclerView.ViewHolder {
        val view: View
        when (itemType) {
            recipeType -> {
                view =
                    LayoutInflater.from(viewGroup.context).inflate(R.layout.layout_recipe_list_item, viewGroup, false)
                return RecipeViewHolder(view, mOnRecipeListener, requestManager)
            }
            loadingType -> {
                view =
                    LayoutInflater.from(viewGroup.context).inflate(R.layout.layout_loading_list_item, viewGroup, false)
                return LoadingViewHolder(view)
            }

            exhaustedType -> {
                view =
                    LayoutInflater.from(viewGroup.context).inflate(R.layout.layout_search_exhausted, viewGroup, false)
                return SearchExhaustedViewHolder(view)
            }

            categoryType -> {
                view =
                    LayoutInflater.from(viewGroup.context).inflate(R.layout.layout_category_list_item, viewGroup, false)
                return CategoryViewHolder(view, mOnRecipeListener, requestManager)
            }
            else -> {
                view =
                    LayoutInflater.from(viewGroup.context).inflate(R.layout.layout_recipe_list_item, viewGroup, false)
                return RecipeViewHolder(view, mOnRecipeListener, requestManager)
            }
        }

    }

    override fun getItemCount(): Int {
        mRecipes?.let {
            return it.size
        }
        return 0
    }


    override fun getItemViewType(position: Int) = mRecipes?.let { recipes ->
        val value = when {
            recipes[position].social_rank.toInt() == -1 -> categoryType
            recipes[position].title == "LOADING..." -> loadingType
            recipes[position].title == "EXHAUSTED..." -> exhaustedType
            position == (recipes.size - 1) && position != 0 && recipes[position].title != "EXHAUSTED..." -> loadingType
            else -> recipeType
        }
        value
    } ?: 0

    //display only loading during search request
    fun displayOnlyLoading() {
        clearRecipesList()
        val recipe = Recipe("LOADING...")
        mRecipes?.add(recipe)
        notifyDataSetChanged()
    }

    private fun clearRecipesList() {
        mRecipes?.clear()
        notifyDataSetChanged()
    }

    //pagination loading
    fun displayLoading() {
        if (!isLoading()) {
            val recipe = Recipe("LOADING...")
            mRecipes?.add(recipe)
            notifyDataSetChanged()
        }
    }

    fun setQueryExhausted() {
        hideLoading()
        val recipeExhausted = Recipe("EXHAUSTED...")
        mRecipes?.add(recipeExhausted)
        notifyDataSetChanged()
    }

    fun hideLoading() {
        if (isLoading()) {
            mRecipes?.let { recipes ->
                if (recipes[0].title == "LOADING...") {
                    recipes.removeAt(0)
                } else if (recipes[recipes.size - 1].title == "LOADING...") {
                    recipes.removeAt(recipes.size - 1)
                }
                notifyDataSetChanged()
            }

        }
    }

    fun displaySearchCategories() {
        val categories = arrayListOf<Recipe>()
        for (category: String in Constants.default_search_category_images) {
            val recipe = Recipe(
                title = category,
                image_url = category,
                social_rank = (-1).toFloat()
            )
            categories.add(recipe)
        }
        mRecipes = categories
        notifyDataSetChanged()
    }

    fun getSelectedItem(position: Int): Recipe? {
        mRecipes?.let { recipe ->
            if (recipe.isNotEmpty()) {
                return recipe[position]
            }
        }
        return null
    }

    private fun isLoading(): Boolean {
        mRecipes?.let { recipes ->
            if (recipes.isNotEmpty() && recipes[recipes.size - 1].title.equals("LOADING...", true)) {
                return true
            }
        }
        return false
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        val itemViewType = getItemViewType(position)
        if (itemViewType == recipeType) {
            mRecipes?.let { recipes ->
                (viewHolder as RecipeViewHolder).onBind(recipes[position])
            }

        } else if (itemViewType == categoryType) {
            mRecipes?.let { recipes ->
                (viewHolder as CategoryViewHolder).onBind(recipes[position])
            }
        }

    }

    fun setRecipes(recipes: MutableList<Recipe>) {
        mRecipes = recipes
        notifyDataSetChanged()
    }
}