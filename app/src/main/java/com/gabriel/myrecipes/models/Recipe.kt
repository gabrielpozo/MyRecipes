package com.gabriel.myrecipes.models


import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.gabriel.myrecipes.database.Converters

@Entity(tableName = "recipes")
@TypeConverters(Converters::class)
data class Recipe(
    @PrimaryKey
    val title: String,
    val recipe_id: String = "",
    val publisher: String = "Unknown",
    val image_url: String = "",
    val social_rank: Float = 0.0F,
    val ingredients: List<String>? = null,
    val timestamp: Int = 0
) {


}