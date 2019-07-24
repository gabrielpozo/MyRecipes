package com.gabriel.myrecipes.request

import androidx.lifecycle.LiveData
import com.gabriel.myrecipes.request.responses.ApiResponse
import com.gabriel.myrecipes.request.responses.RecipeResponse
import com.gabriel.myrecipes.request.responses.RecipeSearchResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface RecipeApi {

    //SEARCH
    @GET("api/search")
    fun searchRecipe(
        @Query("key") key: String,
        @Query("q") query: String,
        @Query("page") page: String
    ): LiveData<ApiResponse<RecipeSearchResponse>>

    //GET RECIPE REQUEST
    @GET("api/get")
    fun getRecipe(
        @Query("key") key: String,
        @Query("rId") recipe_id: String
    ): LiveData<ApiResponse<RecipeResponse>>
}