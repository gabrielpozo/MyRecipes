package com.gabriel.myrecipes

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import com.gabriel.myrecipes.adapters.OnRecipeListener
import com.gabriel.myrecipes.adapters.RecipeRecyclerViewAdapter
import com.gabriel.myrecipes.viewmodels.RecipeListViewModel
import kotlinx.android.synthetic.main.activity_recipe_list.*
import android.support.v7.widget.SearchView
import android.util.Log
import com.gabriel.myrecipes.util.VerticalSpacingItemDecorator

class RecipeListActivity : BaseActivity(), OnRecipeListener {
    private val mRecipeListViewModel by lazy {
        ViewModelProviders.of(this).get(RecipeListViewModel::class.java)
    }
    private val mAdapter: RecipeRecyclerViewAdapter by lazy {
        RecipeRecyclerViewAdapter(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe_list)
        initRecyclerView()
        subscribeObservers()
        initSearchView()
        if (!mRecipeListViewModel.mIsViewingRecipes) {
            displaySearchCategories()
        }
    }

    private fun subscribeObservers() {
        mRecipeListViewModel.mRecipes.observe(this, Observer { recipes ->
            if (recipes != null) {
                if (mRecipeListViewModel.mIsViewingRecipes) {
                    mAdapter.setRecipes(recipes.toList())
                    mRecipeListViewModel.mIsPerformingQuery = false
                }
            }
        })
    }

    private fun initRecyclerView() {
        val itemDecorator = VerticalSpacingItemDecorator(22)
        recipeRecyclerView.addItemDecoration(itemDecorator)
        recipeRecyclerView.adapter = mAdapter
        recipeRecyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun searchRecipesApi(query: String, pageNumber: Int) {
        val page = if (pageNumber == 0) 1 else pageNumber
        mRecipeListViewModel.searchRecipesApi(query, page)
    }

    private fun initSearchView() {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                mAdapter.displayLoading()
                if (query != null) {
                    searchRecipesApi(query, 1)
                }
                return false
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                return true
            }
        })
    }

    override fun onRecipeClick(position: Int) {
    }

    override fun onCategoryClick(category: String) {
        mAdapter.displayLoading()
        mRecipeListViewModel.searchRecipesApi(category, 1)
    }

    private fun displaySearchCategories() {
        mRecipeListViewModel.mIsViewingRecipes = false
        mAdapter.displaySearchCategories()
    }

    override fun onBackPressed() {
        if (mRecipeListViewModel.onBackPressed()) {
            super.onBackPressed()
        } else {
            displaySearchCategories()
        }
    }

}
