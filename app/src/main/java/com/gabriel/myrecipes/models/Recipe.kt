package com.gabriel.myrecipes.models

import android.os.Parcel
import android.os.Parcelable

data class Recipe(
    val title: String,
    val publisher: String = "Unknown",
    val ingredients: ArrayList<String> = arrayListOf(),
    val recipe_id: String = "",
    val image_url: String = "",
    val social_rank: Float = 0.0F
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        arrayListOf<String>().apply { parcel.readArrayList(String::class.java.classLoader) },
        parcel.readString(),
        parcel.readString(),
        parcel.readFloat()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(title)
        parcel.writeString(publisher)
        parcel.writeStringList(ingredients)
        parcel.writeString(recipe_id)
        parcel.writeString(image_url)
        parcel.writeFloat(social_rank)
    }

    override fun describeContents(): Int {
        return 0
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