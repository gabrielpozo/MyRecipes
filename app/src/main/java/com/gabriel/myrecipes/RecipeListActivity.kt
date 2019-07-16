package com.gabriel.myrecipes

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import com.gabriel.myrecipes.adapters.OnRecipeListener
import com.gabriel.myrecipes.adapters.RecipeRecyclerViewAdapter
import com.gabriel.myrecipes.viewmodels.RecipeListViewModel
import kotlinx.android.synthetic.main.activity_recipe_list.*
import android.support.v7.widget.SearchView
import android.view.Menu
import android.view.MenuItem
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

        setSupportActionBar(toolbar)
    }

    private fun subscribeObservers() {
        mRecipeListViewModel.mRecipes.observe(this, Observer { recipes ->
            if (recipes != null && mRecipeListViewModel.mIsViewingRecipes) {
                mAdapter.setRecipes(recipes.toList())
                mRecipeListViewModel.mIsPerformingQuery = false
            }

        })
    }

    private fun initRecyclerView() {
        val itemDecorator = VerticalSpacingItemDecorator(22)
        recipeRecyclerView.addItemDecoration(itemDecorator)
        recipeRecyclerView.adapter = mAdapter
        recipeRecyclerView.layoutManager = LinearLayoutManager(this)
        recipeRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (!recyclerView.canScrollVertically(1)) {
                    mRecipeListViewModel.searchNextPage()
                }
            }
        })
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
        val intent = Intent(this, RecipeActivity::class.java)
        intent.putExtra("recipe", mAdapter.getSelectedItem(position))
        startActivity(intent)
    }

    override fun onCategoryClick(category: String) {
        mAdapter.displayLoading()
        mRecipeListViewModel.searchRecipesApi(category, 1)
        /**clear focus otherwise back-pressed button won't do its performance */
        searchView.clearFocus()
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

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.action_categories -> displaySearchCategories()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.recipes_search_menu, menu)
        return super.onCreateOptionsMenu(menu)

    }

}
