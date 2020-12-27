package com.airstream.typhoon.data.library.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.airstream.typhoon.data.library.entities.Category
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {
    @Query("SELECT * FROM category")
    fun getAll(): Flow<List<Category>>

    @Insert
    fun insertAll(vararg categories: Category)

    @Delete
    fun delete(category: Category)
}