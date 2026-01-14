package com.example.ccl_3.data.repository

import com.example.ccl_3.data.db.BookmarkDao
import com.example.ccl_3.data.db.BookmarkEntity
import kotlinx.coroutines.flow.Flow

class BookmarkRepository(private val dao: BookmarkDao) {
    fun observeBookmarks(): Flow<List<BookmarkEntity>> = dao.observeAll()

    suspend fun addBookmark(code: String, name: String, flagUrl: String, shapeUrl: String?) {
        dao.upsert(
            BookmarkEntity(
                countryCode = code,
                countryName = name,
                flagUrl = flagUrl,
                shapeUrl = shapeUrl
            )
        )
    }

    suspend fun removeBookmark(code: String, name: String, flagUrl: String, shapeUrl: String?) {
        dao.delete(
            BookmarkEntity(
                countryCode = code,
                countryName = name,
                flagUrl = flagUrl,
                shapeUrl = shapeUrl
            )
        )
    }

    suspend fun clear() = dao.clear()
}
