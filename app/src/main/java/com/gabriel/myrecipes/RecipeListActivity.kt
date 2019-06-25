package com.gabriel.myrecipes

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.widget.Button
import com.gabriel.myrecipes.util.Testing
import com.gabriel.myrecipes.viewmodels.RecipeListViewModel


class RecipeListActivity : BaseActivity() {

    private val mRecipeListViewModel by lazy {
        ViewModelProviders.of(this).get(RecipeListViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe_list)
        subscribeObservers()
        findViewById<Button>(R.id.test).setOnClickListener {
            testRecipeRequest()
        }
    }

    private fun subscribeObservers() {
        mRecipeListViewModel.mRecipes.observe(this, Observer { recipes ->
            recipes?.let { Testing.printRecipes(recipes, "Gabriel") }
        })
    }

    private fun searchRecipesApi(query: String, pageNumber: Int) {
        val page = if (pageNumber == 0) 1 else pageNumber
        mRecipeListViewModel.searchRecipesApi(query, page)
    }


    private fun testRecipeRequest() {
        searchRecipesApi("chicken breast", 1)
    }
}
