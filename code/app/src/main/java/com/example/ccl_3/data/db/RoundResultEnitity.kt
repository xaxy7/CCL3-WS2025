package com.example.ccl_3.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("round_results")
data class RoundResultEntity(
    @PrimaryKey(autoGenerate = true)val id: Long =0,

    val roundId: String,
    val region: String?,
    val isGlobal: Boolean,
    val gameMode: String,
    val roundType: String,

    val totalGuesses: Int,
    val correctCount: Int,
    val wrongCount: Int,

    val completed: Boolean,

    val timeTakenMillis: Long?,
    val livesLeft: Int?,

    val countryCodes: String
)
