package com.gabriel.myrecipes.database

import android.arch.persistence.room.TypeConverter
import com.google.gson.Gson

class Converters {
    @TypeConverter
    fun fromString(value: String) = (Gson().fromJson(value, Array<String>::class.java) as Array<String>).toList()

    @TypeConverter
    fun fromArrayList(list: ArrayList<String>) = Gson().toJson(list)
}