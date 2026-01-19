package com.example.ccl_3.data.repository

import com.example.ccl_3.data.db.BookmarkDao
import com.example.ccl_3.data.db.BookmarkEntity
import com.example.ccl_3.model.BookmarkType
import com.example.ccl_3.model.Country
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class BookmarkRepository(private val dao: BookmarkDao) {
    fun observeBookmarks(): Flow<List<BookmarkEntity>> = dao.observeAll()

    suspend fun addFlagBookmark(code: String, name: String, flagUrl: String) {
        dao.upsert(
            BookmarkEntity(
                countryCode = code,
                countryName = name,
                contentType = BookmarkType.FLAG,
                flagUrl = flagUrl,
                shapeUrl = null
            )
        )
    }

    suspend fun addShapeBookmark(code: String, name: String, shapeUrl: String) {
        dao.upsert(
            BookmarkEntity(
                countryCode = code,
                countryName = name,
                contentType = BookmarkType.SHAPE,
                flagUrl = null,
                shapeUrl = shapeUrl
            )
        )
    }

    suspend fun removeBookmark(code: String, type: BookmarkType) {
        dao.deleteByKey(code, type)
    }

    suspend fun clear() = dao.clear()

    suspend fun getBookmarks(type: BookmarkType): List<BookmarkEntity> = dao.getByType(type)

    suspend fun getBookmarksAsCountries(type: BookmarkType): List<Country> {
        return dao.getByType(type)
            .mapNotNull { entity ->
                val url = when (type) {
                    BookmarkType.SHAPE -> entity.shapeUrl
                    BookmarkType.FLAG -> entity.flagUrl
                } ?: return@mapNotNull null
                Country(
                    code = entity.countryCode,
                    name = entity.countryName,
                    flagUrl = url,
                    region = null
                )
            }
    }

    suspend fun isBookmarked(code: String, type: BookmarkType): Boolean = dao.isBookmarked(code, type)
}
