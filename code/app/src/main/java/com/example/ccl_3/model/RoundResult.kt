package com.example.ccl_3.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class RoundResult(
    val id: Long = 0,
    val roundId: String,
    val region: String?,
    val isGlobal: Boolean,
    val gameMode: GameMode,
    val roundType: RoundType,

    val totalGuesses: Int,
    val correctCount: Int,
    val wrongCount: Int,

    val completed: Boolean,

    val timeTakenMillis: Long?,
    val livesLeft: Int?,

    val countryCodes: List<String>

) : Parcelable
