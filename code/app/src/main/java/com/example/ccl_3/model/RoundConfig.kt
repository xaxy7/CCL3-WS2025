package com.example.ccl_3.model



enum class RoundMode {
    GLOBAL,
    REGION
}

data class RoundConfig(
    val mode: RoundMode,
    val parameter: String? = null,
    val gameMode: GameMode,
    val roundType: RoundType,
    val source: QuizSource = QuizSource.NORMAL,
    val bookmarkType: BookmarkType? = null,
    val difficulty: Difficulty
){
    fun id(): String =
        when (source) {
            QuizSource.BOOKMARK ->
                "BOOKMARK:${bookmarkType?.name}:${gameMode.name}:${roundType.name}:${difficulty.name}"

            QuizSource.NORMAL -> when (mode) {
                RoundMode.GLOBAL ->
                    "GLOBAL:${gameMode.name}:${roundType.name}:${difficulty.name}"

                RoundMode.REGION ->
                    "REGION:$parameter:${gameMode.name}:${roundType.name}:${difficulty.name}"
            }
        }

    fun displayName(): String =
        when(source) {
            QuizSource.BOOKMARK -> "Bookmarks"
            QuizSource.NORMAL -> when(mode){
                RoundMode.GLOBAL -> "Global"
                RoundMode.REGION -> parameter ?: "Region"
            }
        }
}

fun parseRoundConfigFromId(roundId: String): RoundConfig? {
    val parts = roundId.split(":")
    return when (parts.firstOrNull()) {
        "BOOKMARK" -> {
            if (parts.size < 5) return null
            val bookmarkType = runCatching { BookmarkType.valueOf(parts[1]) }.getOrNull() ?: return null
            val gameMode = runCatching { GameMode.valueOf(parts[2]) }.getOrNull() ?: return null
            val roundType = runCatching { RoundType.valueOf(parts[3]) }.getOrNull() ?: return null
            val difficulty = runCatching { Difficulty.valueOf(parts[4]) }.getOrNull() ?: return null
            RoundConfig(
                mode = RoundMode.GLOBAL,
                parameter = null,
                gameMode = gameMode,
                roundType = roundType,
                source = QuizSource.BOOKMARK,
                bookmarkType = bookmarkType,
                difficulty = difficulty
            )
        }
        "GLOBAL" -> {
            if (parts.size < 4) return null
            val gameMode = runCatching { GameMode.valueOf(parts[1]) }.getOrNull() ?: return null
            val roundType = runCatching { RoundType.valueOf(parts[2]) }.getOrNull() ?: return null
            val difficulty = runCatching { Difficulty.valueOf(parts[3]) }.getOrNull() ?: return null
            RoundConfig(
                mode = RoundMode.GLOBAL,
                parameter = null,
                gameMode = gameMode,
                roundType = roundType,
                difficulty = difficulty
            )
        }
        "REGION" -> {
            if (parts.size < 5) return null
            val region = parts[1]
            val gameMode = runCatching { GameMode.valueOf(parts[2]) }.getOrNull() ?: return null
            val roundType = runCatching { RoundType.valueOf(parts[3]) }.getOrNull() ?: return null
            val difficulty = runCatching { Difficulty.valueOf(parts[4]) }.getOrNull() ?: return null
            RoundConfig(
                mode = RoundMode.REGION,
                parameter = region,
                gameMode = gameMode,
                roundType = roundType,
                difficulty = difficulty
            )
        }
        else -> null
    }
}
