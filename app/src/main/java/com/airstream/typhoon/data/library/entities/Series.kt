package com.airstream.typhoon.data.library.entities

import androidx.room.*

@Entity(foreignKeys = [ForeignKey(
    entity = Category::class,
    parentColumns = ["id"],
    childColumns = ["columnId"],
    onDelete = ForeignKey.CASCADE
)])
data class Series(
    @PrimaryKey val id: Int,
    val seriesId: String,
    val columnId: Int,
    val source: String,
    val title: String,
    val image: String?
)