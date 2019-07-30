package com.gabriel.myrecipes.request

import com.gabriel.myrecipes.util.Constants
import com.gabriel.myrecipes.util.LiveDataCallAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ServiceGenerator {
    private val client = OkHttpClient().newBuilder()
        //establish the connection with the server
        .connectTimeout(Constants.connection_timeout, TimeUnit.SECONDS)
        //time between each byte read from server
        .readTimeout(Constants.read_timeout, TimeUnit.SECONDS)
        //time between each byte sent to servery
        .writeTimeout(Constants.write_timeout, TimeUnit.SECONDS)
        //retry on Connection Failure set false
        .retryOnConnectionFailure(false)
        .build()
    val recipeApi: RecipeApi by lazy {
        val retrofit = Retrofit.Builder().baseUrl(Constants.base_url)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .addCallAdapterFactory(LiveDataCallAdapterFactory())
            .build()
        return@lazy retrofit.create(RecipeApi::class.java)
    }
}