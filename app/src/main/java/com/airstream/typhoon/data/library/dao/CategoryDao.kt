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

    @Query("SELECT id, name, categoryId IS NOT NULL as hasSeries FROM category c LEFT JOIN (SELECT categoryId FROM series s WHERE seriesId = :seriesId AND sourceId = :sourceId) e ON e.categoryId = c.id")
    fun getAllWithSeriesCheck(sourceId: String, seriesId: String): Flow<List<Category>>

    @Query("UPDATE category SET name = :newName WHERE id = :id")
    fun renameCategory(id: Int, newName: String)

    @Insert
    fun insertAll(vararg categories: Category)

    @Delete
    fun delete(category: Category)
}