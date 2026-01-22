package com.example.ccl_3.model

sealed class QuizSource {
    data class Standard(
        val regionName: String,
        val isGlobal: Boolean,
        val gameMode: GameMode,
        val difficulty: Difficulty
    ) : QuizSource()

    data class Bookmarks(
        val contentType: BookmarkType
    ) : QuizSource()
}
