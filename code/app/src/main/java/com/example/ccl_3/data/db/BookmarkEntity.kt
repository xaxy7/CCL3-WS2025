package com.example.ccl_3.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.ccl_3.model.BookmarkType

@Entity(tableName = "bookmarks", primaryKeys = ["countryCode", "contentType"])
data class BookmarkEntity(
    val countryCode: String,
    val countryName: String,
    val contentType: BookmarkType,
    val flagUrl: String? = null,
    val shapeUrl: String? = null
)
