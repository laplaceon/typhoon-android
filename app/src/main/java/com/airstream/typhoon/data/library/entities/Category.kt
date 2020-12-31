package com.airstream.typhoon.data.library.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity
data class Category @Ignore constructor(
    @PrimaryKey(autoGenerate = true) var id: Int,
    @ColumnInfo(name = "name") val name: String,
    var hasSeries: Boolean
) {
    constructor(name: String) : this(0, name, false)
}
