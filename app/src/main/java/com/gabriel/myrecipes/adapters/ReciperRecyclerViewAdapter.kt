package com.gabriel.myrecipes.adapters

import android.net.Uri
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.gabriel.myrecipes.R
import com.gabriel.myrecipes.models.Recipe
import com.gabriel.myrecipes.util.Constants

class RecipeRecyclerViewAdapter(private val mOnRecipeListener: OnRecipeListener) :
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
                return RecipeViewHolder(view, mOnRecipeListener)
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
                return CategoryViewHolder(view, mOnRecipeListener)
            }
            else -> {
                view =
                    LayoutInflater.from(viewGroup.context).inflate(R.layout.layout_recipe_list_item, viewGroup, false)
                return RecipeViewHolder(view, mOnRecipeListener)
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

    fun displayLoading() {
        if (!isLoading()) {
            val recipe = Recipe("LOADING...")
            val loadingListItem = arrayListOf<Recipe>()
            loadingListItem.add(recipe)
            mRecipes = loadingListItem
            notifyDataSetChanged()
        }

    }

    fun setQueryExhausted() {
        hideLoading()
        val recipeExhausted = Recipe("EXHAUSTED...")
        mRecipes?.add(recipeExhausted)
        notifyDataSetChanged()
    }

    private fun hideLoading() {
        if (isLoading()) {
            mRecipes?.let { recipes ->
                recipes.forEach {
                    if (it.title == "LOADING...") {
                        recipes.remove(it)
                    }
                }
            }
            notifyDataSetChanged()
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
            val requestOptions = RequestOptions().placeholder(R.drawable.ic_launcher_background)
            mRecipes?.apply {
                Glide.with(viewHolder.itemView.context)
                    .setDefaultRequestOptions(requestOptions)
                    .load(this[position].image_url)
                    .into((viewHolder as RecipeViewHolder).image)

                viewHolder.title.text = this[position].title
                viewHolder.publisher.text = this[position].publisher
                viewHolder.socialScore.text = Math.round(this[position].social_rank).toString()
            }
        } else if (itemViewType == categoryType) {
            val requestOptions = RequestOptions().placeholder(R.drawable.ic_launcher_background)
            mRecipes?.apply {
                val path =
                    Uri.parse("android.resource://com.gabriel.myrecipes/drawable/" + get(position).image_url)
                Glide.with(viewHolder.itemView.context)
                    .setDefaultRequestOptions(requestOptions)
                    .load(path)
                    .into((viewHolder as CategoryViewHolder).categoryImage)

                viewHolder.categoryTitle.text = this[position].title
            }
        }

    }

    fun setRecipes(recipes: MutableList<Recipe>) {
        mRecipes = recipes
        notifyDataSetChanged()
    }
}