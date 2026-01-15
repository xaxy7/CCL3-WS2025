package com.example.ccl_3.data.db

import androidx.room.TypeConverter
import com.example.ccl_3.model.BookmarkType

class Converters {
    @TypeConverter
    fun fromStringList(value: List<String>): String =
        value.joinToString(",")

    @TypeConverter
    fun toStringList(value: String): List<String> =
        if (value.isEmpty()) emptyList() else value.split(",")

    @TypeConverter
    fun fromBookmarkType(value: BookmarkType): String = value.name

    @TypeConverter
    fun toBookmarkType(value: String): BookmarkType = BookmarkType.valueOf(value)
}