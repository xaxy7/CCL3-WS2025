package com.example.ccl_3.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bookmarks")
data class BookmarkEntity(
    @PrimaryKey val countryCode: String,
    val countryName: String,
    val flagUrl: String,
    val shapeUrl: String? = null
)
