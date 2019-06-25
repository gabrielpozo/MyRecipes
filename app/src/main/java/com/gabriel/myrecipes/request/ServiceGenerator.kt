package com.gabriel.myrecipes.request

import com.gabriel.myrecipes.util.Constants
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ServiceGenerator {
    val recipeApi: RecipeApi by lazy {
        val retrofit = Retrofit.Builder().baseUrl(Constants.base_url)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return@lazy retrofit.create(RecipeApi::class.java)
    }
}