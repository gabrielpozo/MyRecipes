package com.gabriel.myrecipes.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.gabriel.myrecipes.models.Recipe

@Dao
interface RecipeDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertRecipes(recipes: ArrayList<Recipe>): List<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertRecipe(recipe: Recipe)

    @Query("UPDATE recipes SET title = :title, publisher = :publisher, image_url = :imageUrl, social_rank = :socialRank WHERE recipe_id = :recipeId")
    fun updateRecipe(recipeId: String, title: String, publisher: String, imageUrl: String, socialRank: Float)

    @Query("SELECT * FROM recipes WHERE title LIKE '%' || :query || '%' OR ingredients LIKE '%' || :query  || '%' ORDER BY social_rank DESC LIMIT (:pageNumber * :pageLimit) ")
    fun searchRecipes(query: String, pageNumber: Int, pageLimit: Int): LiveData<List<Recipe>>

    @Query("SELECT *FROM recipes WHERE recipe_id = :recipeId")
    fun getRecipe(recipeId: String): LiveData<Recipe>
}