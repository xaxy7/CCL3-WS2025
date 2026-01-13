package com.example.ccl_3.model

data class RoundSession(
    val remainingLives: Int?,
    val remainingTimeMillis: Long?,
    val isFailed: Boolean = false
)
