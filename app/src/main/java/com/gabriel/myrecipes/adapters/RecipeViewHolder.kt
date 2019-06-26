package com.gabriel.myrecipes.adapters

import android.support.v7.widget.AppCompatImageView
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import com.gabriel.myrecipes.R
//import kotlinx.android.synthetic.main.layout_recipe_list_item.view.*

class RecipeViewHolder(itemView: View, private val onRecipeListener: OnRecipeListener) :
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

}