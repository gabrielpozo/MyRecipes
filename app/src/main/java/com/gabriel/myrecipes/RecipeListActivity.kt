package com.gabriel.myrecipes

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.util.Log
import android.widget.Button
import com.gabriel.myrecipes.models.Recipe
import com.gabriel.myrecipes.request.ServiceGenerator
import com.gabriel.myrecipes.request.responses.RecipeSearchResponse
import com.gabriel.myrecipes.util.Constants
import com.gabriel.myrecipes.viewmodels.RecipeListViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException


class RecipeListActivity : BaseActivity() {

    private val mRecipeListViewModel by lazy {
        ViewModelProviders.of(this).get(RecipeListViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe_list)
        findViewById<Button>(R.id.test).setOnClickListener {
            testRecipeRequest()
        }

        mRecipeListViewModel.mRecipes.observe(this, Observer {

        })
    }


    private fun testRecipeRequest() {
        val recipeApi = ServiceGenerator.recipeApi
        val responseCall = recipeApi.searchRecipe(
            Constants.api_key,
            "chicken breast",
            "1"
        )
        // Log.d("Gabriel", "response: Here")

        responseCall.enqueue(object : Callback<RecipeSearchResponse> {
            override fun onResponse(call: Call<RecipeSearchResponse>, response: Response<RecipeSearchResponse>) {
                Log.d("Gabriel", "response: $response")
                if (response.code() == 200) {
                    Log.d("Gabriel ", "on Response: ${response.body()}")
                    response.body()?.let { recipeSearchResponse ->
                        val recipes: List<Recipe> = ArrayList<Recipe>(recipeSearchResponse.recipes)
                        recipes.forEach {
                            Log.d("Gabriel", "$it")
                        }
                    }

                } else {
                    try {
                        Log.d("Gabriel", "onResponse Error Body ${response.errorBody().toString()}")

                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }

            }

            override fun onFailure(call: Call<RecipeSearchResponse>, t: Throwable) {
                Log.d("Gabriel ", "")
            }
        })

    }
}
