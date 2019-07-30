package com.gabriel.myrecipes.repositories

import androidx.lifecycle.LiveData
import android.content.Context
import android.util.Log
import com.gabriel.myrecipes.AppExecutors
import com.gabriel.myrecipes.database.RecipeDatabase
import com.gabriel.myrecipes.models.Recipe
import com.gabriel.myrecipes.request.ServiceGenerator
import com.gabriel.myrecipes.request.responses.ApiResponse
import com.gabriel.myrecipes.request.responses.RecipeResponse
import com.gabriel.myrecipes.request.responses.RecipeSearchResponse
import com.gabriel.myrecipes.util.Constants
import com.gabriel.myrecipes.util.NetworkBoundResource
import com.gabriel.myrecipes.util.ResourceData

class RecipeRepository(context: Context) {
    private val recipeDao = RecipeDatabase.invoke(context).getRecipeDao()

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
                return recipeDao.searchRecipes(query, pageNumber)
            }

            override fun createCall(): LiveData<ApiResponse<RecipeSearchResponse>> {
                return ServiceGenerator.recipeApi.searchRecipe(Constants.API_KEY7, query, pageNumber.toString())
            }

        }.asLiveData()
    }


    fun searchRecipeApi(recipeId: String): LiveData<ResourceData<Recipe>> {
        return object : NetworkBoundResource<Recipe, RecipeResponse>(AppExecutors()) {
            override fun saveCallResult(item: RecipeResponse) {
                item.recipe.timestamp = System.currentTimeMillis().toInt() / 1000
                recipeDao.insertRecipe(item.recipe)
            }

            override fun shouldFetch(data: Recipe?): Boolean {
                Log.d("Gabriel", "shouldFetch recipe: ${data.toString()}")
                val currentTime = (System.currentTimeMillis() / 100).toInt()
                Log.d("Gabriel", "currentTime $currentTime")
                if (data != null) {
                    val lastRefresh = data.timestamp
                    Log.d(
                        "Gabriel",
                        "shouldFetch: its been ${(currentTime - lastRefresh) / 60 / 60 / 24} days since this recipe was refresh." +
                                " 15 days must elapse before refreshing"
                    )
                    if (currentTime - lastRefresh >= Constants.recipe_refresh_time) {
                        Log.d("Gabriel", "shouldFetch: SHOULD REFRESH RECIPE")
                        return true

                    }
                }
                return false
            }

            override fun loadFromDb(): LiveData<Recipe> {
                return recipeDao.getRecipe(recipeId)
            }

            override fun createCall(): LiveData<ApiResponse<RecipeResponse>> {
                return ServiceGenerator.recipeApi.getRecipe(Constants.API_KEY7, recipeId)
            }

        }.asLiveData()
    }
}