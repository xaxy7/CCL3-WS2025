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