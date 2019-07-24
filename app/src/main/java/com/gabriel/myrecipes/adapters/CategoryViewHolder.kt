package com.gabriel.myrecipes.adapters

import android.net.Uri
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.widget.TextView
import com.bumptech.glide.RequestManager
import com.gabriel.myrecipes.R
import com.gabriel.myrecipes.models.Recipe
import de.hdodenhof.circleimageview.CircleImageView

class CategoryViewHolder(
    itemView: View,
    private val onRecipeListener: OnRecipeListener,
    private val requestManager: RequestManager
) :
    RecyclerView.ViewHolder(itemView), View.OnClickListener {

    var categoryImage: CircleImageView = itemView.findViewById(R.id.category_image)
    var categoryTitle: TextView = itemView.findViewById(R.id.category_title)

    init {
        itemView.setOnClickListener(this)
    }

    override fun onClick(p0: View?) {
        onRecipeListener.onCategoryClick(categoryTitle.text.toString())
    }

    public fun onBind(recipe: Recipe) {

        val path = Uri.parse("android.resource://com.gabriel.myrecipes/drawable/" + recipe.image_url)
        requestManager.load(path).into(categoryImage)

        categoryTitle.text = recipe.title
    }

}
