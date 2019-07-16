package com.gabriel.myrecipes

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.gabriel.myrecipes.models.Recipe
import com.gabriel.myrecipes.viewmodels.RecipeViewModel
import kotlinx.android.synthetic.main.activity_recipe.*
import kotlin.math.roundToInt

class RecipeActivity : BaseActivity() {

    private val mRecipeViewModel by lazy {
        ViewModelProviders.of(this).get(RecipeViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe)

        showProgressBar(true)
        subscribeObservers()
        getIncomeIntent()
    }

    private fun getIncomeIntent() {
        if (intent.hasExtra("recipe")) {
            val recipe = intent.getParcelableExtra<Recipe>("recipe")
            mRecipeViewModel.searchRecipeById(recipe.recipe_id)
        }
    }

    private fun subscribeObservers() {
        mRecipeViewModel.mRecipe.observe(this, Observer {
            it?.let { recipe ->
                if (recipe.recipe_id == mRecipeViewModel.recipeId) {
                    setRecipeProperties(it)
                }
            }
        })

        mRecipeViewModel.mRecipeRequestTimeOut.observe(this, Observer { timedOut ->
              if (timedOut != null && timedOut) {
                  Log.d("Gabriel", " It has timed out first")
              }

        })
    }

    private fun setRecipeProperties(recipe: Recipe) {
        val requestOptions = RequestOptions()
            .placeholder(R.drawable.ic_launcher_background)

        Glide.with(this)
            .setDefaultRequestOptions(requestOptions)
            .load(recipe.image_url)
            .into(recipeImage)

        recipeTitle.text = (recipe.title)
        recipeSocialScore.text = recipe.social_rank.roundToInt().toString()

        ingredientsContainer.removeAllViews()
        recipe.ingredients.forEach { ingredient ->
            val textView = TextView(this)
            textView.text = ingredient
            textView.textSize = 15f
            textView.layoutParams =
                LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            ingredientsContainer.addView(textView)
        }

        showParent()
        showProgressBar(false)
    }

    private fun showParent() {
        scrollView.visibility = View.VISIBLE
    }


}