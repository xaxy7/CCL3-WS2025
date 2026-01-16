package com.example.ccl_3.data.db

import androidx.room.Database
import androidx.room.RoomDatabase

import androidx.room.TypeConverters

@Database(
    entities = [RoundStateEntity::class, RoundResultEntity::class, BookmarkEntity::class],
    version = 9,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun roundStateDao(): RoundStateDao

    abstract fun roundResultDao(): RoundResultDao

    abstract fun bookmarkDao(): BookmarkDao
}