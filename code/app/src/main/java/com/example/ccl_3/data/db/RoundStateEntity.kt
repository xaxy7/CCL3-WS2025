package com.example.ccl_3.data.db

import androidx.room.PrimaryKey
import androidx.room.Entity


@Entity(tableName = "round_state")
data class RoundStateEntity(
    @PrimaryKey val  roundId: String,

    val usedCountryCodes: List<String>,
    val correctCount: Int,
    val wrongCount: Int,
    val totalCount: Int,

    val lastUpdated: Long = System.currentTimeMillis()
)
