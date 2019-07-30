package com.gabriel.myrecipes.models


import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.gabriel.myrecipes.database.Converters
import kotlinx.android.parcel.Parcelize


@Entity(tableName = "recipes")
@TypeConverters(Converters::class)
@Parcelize
data class Recipe(
    @PrimaryKey
    val recipe_id: String = "",
    val title: String,
    val publisher: String = "Unknown",
    val image_url: String = "",
    val social_rank: Float = 0.0F,
    val ingredients: List<String>? = null,
    var timestamp: Int = 0
) : Parcelable