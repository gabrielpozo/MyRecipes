package com.gabriel.myrecipes.models

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.os.Parcel
import android.os.Parcelable

@Entity(tableName = "recipes")
data class Recipe(
    @PrimaryKey
    val title: String,
    val recipe_id: String = "",
    val publisher: String = "Unknown",
    val image_url: String = "",
    val social_rank: Float = 0.0F,
    val ingredients: ArrayList<String> = arrayListOf(),
    val timestamp: Int = 0
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readFloat(),
        arrayListOf<String>().apply { parcel.readArrayList(String::class.java.classLoader) },
        parcel.readInt()

    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(title)
        parcel.writeString(publisher)
        parcel.writeStringList(ingredients)
        parcel.writeString(recipe_id)
        parcel.writeString(image_url)
        parcel.writeFloat(social_rank)
        parcel.writeInt(timestamp)
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun toString(): String {
        return "Recipe(title='$title', recipe_id='$recipe_id', publisher='$publisher', image_url='$image_url'" +
                ", social_rank=$social_rank, ingredients=$ingredients, timestamp=$timestamp)"
    }

    companion object CREATOR : Parcelable.Creator<Recipe> {
        override fun createFromParcel(parcel: Parcel): Recipe {
            return Recipe(parcel)
        }

        override fun newArray(size: Int): Array<Recipe?> {
            return arrayOfNulls(size)
        }
    }

}