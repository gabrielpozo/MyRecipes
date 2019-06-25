package com.gabriel.myrecipes.models

import android.os.Parcel
import android.os.Parcelable

data class Recipe(
    private val title: String,
    private val publisher: String,
    private val ingredients: Array<String>,
    private val recipe_id: String,
    private val image_url: String,
    private val social_rank: Float
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.createStringArray(),
        parcel.readString(),
        parcel.readString(),
        parcel.readFloat()
    )
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(title)
        parcel.writeString(publisher)
        parcel.writeStringArray(ingredients)
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