package com.example.ccl_3.data.db.mappers

import com.example.ccl_3.data.db.RoundResultEntity
import com.example.ccl_3.model.GameMode
import com.example.ccl_3.model.RoundResult
import com.example.ccl_3.model.RoundType


fun RoundResultEntity.toModel(): RoundResult{
    return RoundResult(
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

        countryCodes = countryCodes.split(",").filter { it.isNotBlank() }
    )
}