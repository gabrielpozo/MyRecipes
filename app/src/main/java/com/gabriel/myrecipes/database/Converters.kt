package com.gabriel.myrecipes.database

import androidx.room.TypeConverter

object Converters {
    @TypeConverter
    @JvmStatic
    fun stringToStringList(data: String?): List<String>? {
        return data?.split(",")
    }

    @TypeConverter
    @JvmStatic
    fun stringListToString(strings: List<String>?): String? {
        return strings?.joinToString(",")
    }
}

