package com.airstream.typhoon.data.library

import android.app.Application
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.airstream.typhoon.data.library.dao.CategoryDao
import com.airstream.typhoon.data.library.dao.SeriesDao
import com.airstream.typhoon.data.library.entities.Category
import com.airstream.typhoon.data.library.entities.Series

@Database(entities = [Series::class, Category::class], version = 1)
abstract class Library: RoomDatabase() {
    abstract fun seriesDao(): SeriesDao
    abstract fun categoryDao(): CategoryDao

    companion object {
        @Volatile private var instance: Library? = null;

        fun getInstance(ctx: Context) =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(ctx, Library::class.java, "library").build().also { instance = it }
            }
    }
}