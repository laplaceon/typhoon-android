package com.airstream.typhoon.data.library.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Category(
    @ColumnInfo(name = "name") val name: String,
) {
    @PrimaryKey(autoGenerate = true) var id: Int = 0
}
