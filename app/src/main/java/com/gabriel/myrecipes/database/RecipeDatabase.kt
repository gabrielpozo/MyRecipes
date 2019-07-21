package com.gabriel.myrecipes.database

import android.arch.persistence.room.*
import android.content.Context
import com.gabriel.myrecipes.models.Recipe

@Database(entities = [Recipe::class], version = 1)
@TypeConverters(Converters::class)
abstract class RecipeDatabase : RoomDatabase() {
    val database_name = "recipe_db"

    companion object {
        @Volatile
        private var instance: RecipeDatabase? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
            instance ?: buildDatabase(context).also { instance = it }
        }

        private fun buildDatabase(context: Context) = Room.databaseBuilder(
            context,
            RecipeDatabase::class.java, "database_name"
        ).build()
    }

}