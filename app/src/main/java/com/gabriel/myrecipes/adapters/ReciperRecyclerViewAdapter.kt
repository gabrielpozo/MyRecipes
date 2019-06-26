package com.gabriel.myrecipes.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.gabriel.myrecipes.R
import com.gabriel.myrecipes.models.Recipe

class RecipeRecyclerViewAdapter(private val mOnRecipeListener: OnRecipeListener) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var mRecipes: List<Recipe>? = null


    override fun onCreateViewHolder(viewGroup: ViewGroup, p1: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.layout_recipe_list_item, viewGroup, false)
        return RecipeViewHolder(view, mOnRecipeListener)
    }

    override fun getItemCount(): Int {
        mRecipes?.let {
            return it.size
        }
        return 0
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
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
    }

    fun setRecipes(recipes: List<Recipe>) {
        mRecipes = recipes
        notifyDataSetChanged()
    }
}