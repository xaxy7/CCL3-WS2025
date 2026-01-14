package com.example.ccl_3.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface RoundResultDao {

    @Insert
    suspend fun insert(result: RoundResultEntity)

    @Query("SELECT * FROM round_results ORDER BY id desc")
    suspend fun getAll(): List<RoundResultEntity>
}