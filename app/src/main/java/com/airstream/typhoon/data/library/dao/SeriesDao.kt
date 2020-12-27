package com.airstream.typhoon.data.library.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.airstream.typhoon.data.library.entities.Series
import kotlinx.coroutines.flow.Flow

@Dao
interface SeriesDao {
    @Query("SELECT * FROM series WHERE columnId = :categoryId")
    fun getAll(categoryId: Int): List<Series>

    @Insert
    fun insertAll(vararg series: Series)
}