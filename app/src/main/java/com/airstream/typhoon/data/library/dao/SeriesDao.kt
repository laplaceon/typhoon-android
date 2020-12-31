package com.airstream.typhoon.data.library.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.airstream.typhoon.data.library.entities.Series

@Dao
interface SeriesDao {
    @Query("SELECT * FROM series WHERE categoryId = :categoryId")
    fun getAll(categoryId: Int): List<Series>

    @Insert
    fun insertAll(vararg series: Series)

    @Query("DELETE FROM series WHERE categoryId = :categoryId AND sourceId = :sourceId AND seriesId = :seriesId")
    fun delete(categoryId: Int, sourceId:String, seriesId: String)
}