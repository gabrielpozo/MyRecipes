package com.gabriel.myrecipes

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gabriel.myrecipes.adapters.OnRecipeListener
import com.gabriel.myrecipes.adapters.RecipeRecyclerViewAdapter
import com.gabriel.myrecipes.viewmodels.RecipeListViewModel
import kotlinx.android.synthetic.main.activity_recipe_list.*
import androidx.appcompat.widget.SearchView
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.bumptech.glide.integration.recyclerview.RecyclerViewPreloader
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.util.ViewPreloadSizeProvider
import com.gabriel.myrecipes.util.ResourceData
import com.gabriel.myrecipes.util.VerticalSpacingItemDecorator

class RecipeListActivity : BaseActivity(), OnRecipeListener {
    private val viewPreloadSizeProvider = ViewPreloadSizeProvider<String>()
    private val mRecipeListViewModel by lazy {
        ViewModelProviders.of(this).get(RecipeListViewModel::class.java)
    }
    private val mAdapter: RecipeRecyclerViewAdapter by lazy {
        RecipeRecyclerViewAdapter(this, getGlideRequestManager(), viewPreloadSizeProvider)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe_list)
        initRecyclerView()
        initSearchView()
        subscribeObservers()
        setSupportActionBar(toolbar)
    }

    private fun subscribeObservers() {
        mRecipeListViewModel.recipes.observe(this, Observer { listResource ->
            if (listResource != null) {
                if (listResource.data != null) {
                    when (listResource.status) {
                        ResourceData.Status.LOADING -> {
                            if (mRecipeListViewModel.pageNumber > 1) {
                                mAdapter.displayLoading()
                            } else {
                                mAdapter.displayOnlyLoading()
                            }
                        }

                        ResourceData.Status.ERROR -> {
                            Toast.makeText(this, "${listResource.message}", Toast.LENGTH_LONG).show()
                            mAdapter.hideLoading()
                            mAdapter.setRecipes(listResource.data.toMutableList())
                        }

                        ResourceData.Status.EXHAUSTED -> {
                            mAdapter.hideLoading()
                            mAdapter.setQueryExhausted()
                        }

                        ResourceData.Status.SUCCESS -> {
                            mAdapter.hideLoading()
                            mAdapter.setRecipes(listResource.data.toMutableList())
                        }
                    }
                }
            }
        })
        mRecipeListViewModel.viewState.observe(this, Observer { viewState ->
            if (viewState != null) {
                when (viewState) {
                    RecipeListViewModel.ViewState.CATEGORIES -> displaySearchCategories()
                    RecipeListViewModel.ViewState.RECIPES -> {
                        //recipes will show automatically from the observer
                    }
                }

            }
        })
    }

    private fun getGlideRequestManager(): RequestManager {
        val options = RequestOptions()
            .placeholder(R.drawable.white_background)
            .error(R.drawable.white_background)
        return Glide.with(this)
            .setDefaultRequestOptions(options)
    }

    private fun initRecyclerView() {
        val itemDecorator = VerticalSpacingItemDecorator(22)
        recipeRecyclerView.addItemDecoration(itemDecorator)
        recipeRecyclerView.adapter = mAdapter
        recipeRecyclerView.layoutManager = LinearLayoutManager(this)
        val preloader = RecyclerViewPreloader(
            Glide.with(this),
            mAdapter, viewPreloadSizeProvider, 30
        )
        recipeRecyclerView.addOnScrollListener(preloader)
        recipeRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (!recyclerView.canScrollVertically(1)
                    && mRecipeListViewModel.viewState.value == RecipeListViewModel.ViewState.RECIPES
                ) {
                    mRecipeListViewModel.searchNextPage()
                }
            }
        })
    }

    private fun searchRecipesApi(query: String, pageNumber: Int) {
        recipeRecyclerView.smoothScrollToPosition(0)
        mRecipeListViewModel.searchRecipesApi(query, if (pageNumber == 0) 1 else pageNumber)
        searchView.clearFocus()
    }

    private fun initSearchView() {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
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
        // mAdapter.displayLoading()
        mRecipeListViewModel.searchRecipesApi(category, 1)
        //**clear focus otherwise back-pressed button won't do its performance *//*
        searchView.clearFocus()
    }

    private fun displaySearchCategories() {
        //mRecipeListViewModel.mIsViewingRecipes = false
        mAdapter.displaySearchCategories()
    }

    override fun onBackPressed() {
        if (mRecipeListViewModel.viewState.value == RecipeListViewModel.ViewState.CATEGORIES) {
            super.onBackPressed()
        } else {
            mRecipeListViewModel.cancelSearchRequest()
            mRecipeListViewModel.viewState.value = RecipeListViewModel.ViewState.CATEGORIES
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
