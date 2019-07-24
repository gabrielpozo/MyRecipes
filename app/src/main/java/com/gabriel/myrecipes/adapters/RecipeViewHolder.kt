package com.gabriel.myrecipes.adapters

import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.bumptech.glide.request.RequestOptions
import com.gabriel.myrecipes.R
import com.gabriel.myrecipes.models.Recipe

//import kotlinx.android.synthetic.main.layout_recipe_list_item.view.*

class RecipeViewHolder(
    itemView: View,
    private val onRecipeListener: OnRecipeListener,
    private val requestManager: RequestManager
) :
    RecyclerView.ViewHolder(itemView),
    View.OnClickListener {
    var title: TextView = itemView.findViewById(R.id.recipe_title)
    var publisher: TextView = itemView.findViewById(R.id.recipe_publisher)
    var socialScore: TextView = itemView.findViewById(R.id.recipe_social_score)
    var image: AppCompatImageView = itemView.findViewById(R.id.recipe_image)

    init {
        itemView.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        onRecipeListener.onRecipeClick(adapterPosition)
    }

    fun onBind(recipe: Recipe) {
        requestManager.load(recipe.image_url)
            .into(image)
        title.text = recipe.title
        publisher.text = recipe.publisher
        socialScore.text = Math.round(recipe.social_rank).toString()
    }

}
