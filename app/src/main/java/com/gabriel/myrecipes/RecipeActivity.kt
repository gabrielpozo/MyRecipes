package com.gabriel.myrecipes

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.gabriel.myrecipes.models.Recipe
import com.gabriel.myrecipes.util.ResourceData
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
        getIncomeIntent()
    }


    private fun getIncomeIntent() {
        if (intent.hasExtra("recipe")) {
            val recipe = intent.getParcelableExtra<Recipe>("recipe")
            subscribeObservers(recipe.recipe_id)
        }
    }

    private fun subscribeObservers(recipeId: String) {
        mRecipeViewModel.searchRecipeApi(recipeId).observe(this, Observer { recipeResource ->
            if (recipeResource != null) {
                if (recipeResource.data != null) {
                    when (recipeResource.status) {
                        ResourceData.Status.LOADING -> {

                        }
                        ResourceData.Status.ERROR -> {
                            showParent()
                            showProgressBar(false)
                            setRecipeProperties(recipeResource.data)
                        }
                        ResourceData.Status.SUCCESS -> {
                            showParent()
                            showProgressBar(false)
                            setRecipeProperties(recipeResource.data)
                        }
                    }
                }
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
        setIngredients(recipe)
    }

    private fun setIngredients(recipe: Recipe) {
        if (recipe.ingredients != null) {
            ingredientsContainer.removeAllViews()
            recipe.ingredients?.forEach { ingredient ->
                val textView = TextView(this)
                textView.text = ingredient
                textView.textSize = 15f
                textView.layoutParams =
                    LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                ingredientsContainer.addView(textView)
            }
        } else {
            displayErrorScreen("Error retrieving ingredients...\n check network connection")
        }
    }

    private fun showParent() {
        scrollView.visibility = View.VISIBLE
    }

    private fun displayErrorScreen(errorMessage: String?) {
        recipeTitle.text = errorMessage
        recipeSocialScore.text = ""
        val textView = TextView(this)
        if (errorMessage != null) {
            textView.text = errorMessage
        } else {
            textView.text = "error"
        }

        textView.textSize = 15f
        textView.layoutParams =
            LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        ingredientsContainer.addView(textView)
        /* val requestOptions = RequestOptions()
             .placeholder(R.drawable.ic_launcher_background)

         Glide.with(this)
             .setDefaultRequestOptions(requestOptions)
             .load(R.drawable.ic_launcher_background)
             .into(recipeImage)*/

    }


}