package com.example.ccl_3.data.repository

import com.example.ccl_3.data.db.RoundResultDao
import com.example.ccl_3.data.db.RoundResultEntity
import com.example.ccl_3.data.db.mappers.toEntity
import com.example.ccl_3.data.db.mappers.toModel
import com.example.ccl_3.model.RoundResult

class RoundResultRepository(private val dao: RoundResultDao) {

    suspend fun save(result: RoundResult){
        dao.insert(
//            RoundResultEntity(
//                roundId = result.roundId,
//                region = result.region,
//                isGlobal = result.isGlobal,
//                gameMode = result.gameMode.name,
//                roundType = result.roundType.name,
//                totalGuesses = result.totalGuesses,
//                correctCount = result.correctCount,
//                wrongCount = result.wrongCount,
//                completed = result.completed,
//                timeTakenMillis = result.timeTakenMillis,
//                livesLeft = result.livesLeft,
//                countryCodes = result.countryCodes.joinToString(",")
//            )
            result.toEntity()
        )
    }
    suspend fun insertDebugRound(){
        dao.insert(
            RoundResultEntity(
                roundId = "REGION:Oceania:GUESS_FLAG:PRACTICE",
                region = "Oceania",
                isGlobal = false,
                gameMode = "GUESS_FLAG",
                roundType = "PRACTICE",

                totalGuesses = 27,
                correctCount = 8,
                wrongCount = 18,

                completed = true,

                timeTakenMillis = 1768390355666,
                livesLeft = null,

                countryCodes = "TV,AU,VU,NF,SB,TK,CC,NU,WF,PF,PN,CK,NZ,PW,PG,MP,CX,KI,MH,FM,WS,NC,FJ,NR,GU,AS"

            )
        )
    }
    suspend fun getLastResult(): RoundResult?{
        return dao.getLast()?.toModel()
    }
}
