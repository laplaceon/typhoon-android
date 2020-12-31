package com.airstream.typhoon.data.library.entities

import androidx.room.*

@Entity(foreignKeys = [ForeignKey(
    entity = Category::class,
    parentColumns = ["id"],
    childColumns = ["categoryId"],
    onDelete = ForeignKey.CASCADE
)])
data class Series(
    val seriesId: String,
    val categoryId: Int,
    val sourceId: String,
    val uri: String,
    val title: String,
    val image: String?
) {
    @PrimaryKey(autoGenerate = true) var id: Int = 0
}