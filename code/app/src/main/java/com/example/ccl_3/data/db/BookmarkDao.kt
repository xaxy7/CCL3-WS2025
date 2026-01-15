package com.example.ccl_3.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.ccl_3.model.BookmarkType
import kotlinx.coroutines.flow.Flow

@Dao
interface BookmarkDao {
    @Query("SELECT * FROM bookmarks ORDER BY countryName")
    fun observeAll(): Flow<List<BookmarkEntity>>

    @Query("SELECT * FROM bookmarks WHERE contentType = :type ORDER BY countryName")
    suspend fun getByType(type: BookmarkType): List<BookmarkEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(bookmark: BookmarkEntity)

    @Delete
    suspend fun delete(bookmark: BookmarkEntity)

    @Query("DELETE FROM bookmarks WHERE countryCode = :code AND contentType = :type")
    suspend fun deleteByKey(code: String, type: BookmarkType)

    @Query("DELETE FROM bookmarks")
    suspend fun clear()
}
