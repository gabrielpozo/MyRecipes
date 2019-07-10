package com.gabriel.myrecipes.adapters

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import com.gabriel.myrecipes.R
import de.hdodenhof.circleimageview.CircleImageView

class CategoryViewHolder(itemView: View, private val onRecipeListener: OnRecipeListener) :
    RecyclerView.ViewHolder(itemView), View.OnClickListener {

    var categoryImage: CircleImageView = itemView.findViewById(R.id.category_image)
    var categoryTitle: TextView = itemView.findViewById(R.id.category_title)

    init {
        itemView.setOnClickListener(this)
    }

    override fun onClick(p0: View?) {
        onRecipeListener.onCategoryClick(categoryTitle.text.toString())
    }
}