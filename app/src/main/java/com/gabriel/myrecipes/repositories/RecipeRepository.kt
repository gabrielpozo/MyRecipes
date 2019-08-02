package com.gabriel.myrecipes.repositories

import androidx.lifecycle.LiveData
import android.content.Context
import com.gabriel.myrecipes.AppExecutors
import com.gabriel.myrecipes.database.RecipeDatabase
import com.gabriel.myrecipes.models.Recipe
import com.gabriel.myrecipes.request.ServiceGenerator
import com.gabriel.myrecipes.request.responses.ApiResponse
import com.gabriel.myrecipes.request.responses.RecipeResponse
import com.gabriel.myrecipes.request.responses.RecipeSearchResponse
import com.gabriel.myrecipes.util.Constants
import com.gabriel.myrecipes.util.NetworkBoundResource
import com.gabriel.myrecipes.util.RepoRateLimitPagination
import com.gabriel.myrecipes.util.ResourceData

class RecipeRepository(context: Context) {
    private val recipeDao = RecipeDatabase.invoke(context).getRecipeDao()
    private val repoRateLimitPagination = RepoRateLimitPagination()

    fun searchRecipes(query: String, pageNumber: Int): LiveData<ResourceData<List<Recipe>>> {
        return object : NetworkBoundResource<List<Recipe>, RecipeSearchResponse>(AppExecutors()) {
            override fun saveCallResult(item: RecipeSearchResponse) {
                // recipe list will be null if the api key is expired
                //  val myRecipes: Array<Recipe> = item.recipes.toTypedArray()
                for ((index, rowId) in recipeDao.insertRecipes(ArrayList(item.recipes)).withIndex()) {
                    if (rowId.toInt() == -1) {
                        //if the recipe already exists, I don't want to set the ingredients or timestamp b/c
                        //they will be erased
                        recipeDao.updateRecipe(
                            item.recipes[index].recipe_id,
                            item.recipes[index].title,
                            item.recipes[index].publisher,
                            item.recipes[index].image_url,
                            item.recipes[index].social_rank
                        )
                    }
                }
            }

            override fun shouldFetch(data: List<Recipe>?): Boolean {
                return true //always query the network since the queries can be anything
            }

            override fun loadFromDb(): LiveData<List<Recipe>> {
                return recipeDao.searchRecipes(query, pageNumber, repoRateLimitPagination.pageNumberLimit)
            }

            override fun createCall(): LiveData<ApiResponse<RecipeSearchResponse>> {
                return ServiceGenerator.recipeApi.searchRecipe(Constants.API_KEY5, query, pageNumber.toString())
            }

            override fun isApiCallExhausted(recipeSearchResponse: RecipeSearchResponse): Boolean {
                if (recipeSearchResponse.recipes.isEmpty()) {
                    return true
                }
                return false
            }

        }.asLiveData()
    }


    fun searchRecipeApi(recipeId: String): LiveData<ResourceData<Recipe>> {
        return object : NetworkBoundResource<Recipe, RecipeResponse>(AppExecutors()) {
            override fun saveCallResult(item: RecipeResponse) {
                item.recipe.timestamp = (System.currentTimeMillis() / 1000).toInt()
                recipeDao.insertRecipe(item.recipe)
            }

            override fun shouldFetch(data: Recipe?): Boolean {
                val currentTime = (System.currentTimeMillis() / 1000).toInt()
                if (data != null) {
                    if ((currentTime - data.timestamp) >= Constants.recipe_refresh_time) {
                        return true
                    }
                }
                return false
            }

            override fun loadFromDb(): LiveData<Recipe> {
                return recipeDao.getRecipe(recipeId)
            }

            override fun createCall(): LiveData<ApiResponse<RecipeResponse>> {
                return ServiceGenerator.recipeApi.getRecipe(Constants.API_KEY5, recipeId)
            }

        }.asLiveData()
    }
}