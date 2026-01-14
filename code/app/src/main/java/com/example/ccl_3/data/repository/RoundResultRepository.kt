package com.example.ccl_3.data.repository

import com.example.ccl_3.data.db.RoundResultDao
import com.example.ccl_3.data.db.RoundResultEntity
import com.example.ccl_3.model.RoundResult

class RoundResultRepository(private val dao: RoundResultDao) {

    suspend fun save(result: RoundResult){
        dao.insert(
            RoundResultEntity(
                roundId = result.roundId,
                region = result.region,
                isGlobal = result.isGlobal,
                gameMode = result.gameMode.name,
                roundType = result.roundType.name,
                totalGuesses = result.totalGuesses,
                correctCount = result.correctCount,
                wrongCount = result.wrongCount,
                completed = result.completed,
                timeTakenMillis = result.timeTakenMillis,
                livesLeft = result.livesLeft,
                countryCodes = result.countryCodes.joinToString(",")
            )
        )
    }
}