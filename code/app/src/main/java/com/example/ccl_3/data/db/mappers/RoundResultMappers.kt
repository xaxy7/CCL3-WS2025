package com.example.ccl_3.data.db.mappers

import com.example.ccl_3.data.db.RoundResultEntity
import com.example.ccl_3.model.AnswerResult
import com.example.ccl_3.model.GameMode
import com.example.ccl_3.model.RoundResult
import com.example.ccl_3.model.RoundType


fun RoundResultEntity.toModel(): RoundResult {
    return RoundResult(
        id = id,

        roundId = roundId,
        region = region,
        isGlobal = isGlobal,

        gameMode = GameMode.valueOf(gameMode),
        roundType = RoundType.valueOf(roundType),

        totalGuesses = totalGuesses,
        correctCount = correctCount,
        wrongCount = wrongCount,

        completed = completed,

        timeTakenMillis = timeTakenMillis,
        livesLeft = livesLeft,

        countryCodes = if (countryCodes.isBlank()) emptyList() else countryCodes.split(","),
        answers = if (answers.isBlank()) emptyList() else answers.split(",").map { AnswerResult.valueOf(it) }
    )
}
fun RoundResult.toEntity(): RoundResultEntity {
    return RoundResultEntity(
        id = id, // 0 for new inserts

        roundId = roundId,
        region = region,
        isGlobal = isGlobal,

        gameMode = gameMode.name,
        roundType = roundType.name,

        totalGuesses = totalGuesses,
        correctCount = correctCount,
        wrongCount = wrongCount,

        completed = completed,

        timeTakenMillis = timeTakenMillis,
        livesLeft = livesLeft,

        countryCodes = if (countryCodes.isEmpty()) "" else countryCodes.joinToString(","),
        answers = if (answers.isEmpty()) "" else answers.joinToString(",") { it.name }
    )
}