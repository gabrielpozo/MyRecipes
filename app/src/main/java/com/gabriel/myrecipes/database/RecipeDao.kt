package com.gabriel.myrecipes.database

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import com.gabriel.myrecipes.models.Recipe

@Dao
interface RecipeDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertRecipes(recipes: ArrayList<Recipe>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertRecipe(recipe: Recipe)

    @Query("UPDATE recipes SET title = :title, publisher = :publisher, image_url = :imageUrl, social_rank = :socialRank WHERE recipe_id = :recipeId")
    fun updateRecipe(recipeId: String, title: String, publisher: String, imageUrl: String, socialRank: Float)

    @Query("SELECT * FROM recipes WHERE title LIKE '%' || :query || '%' OR ingredients LIKE '%' || :query  || '%' ORDER BY social_rank DESC LIMIT (:pageNumber * 30) ")
    fun searchRecipes(query: String, pageNumber: Int): LiveData<List<Recipe>>

    @Query("SELECT *FROM recipes WHERE recipe_id = :recipeId")
    fun getRecipe(recipeId: String): LiveData<Recipe>

}