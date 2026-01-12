package com.example.ccl_3.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface RoundStateDao {
    @Query("SELECT * FROM round_state WHERE roundId = :roundId")
    suspend fun getRoundState(roundId: String): RoundStateEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveRoundState(state: RoundStateEntity)

    @Query("DELETE FROM round_state WHERE roundId = :roundId")
    suspend fun clear(roundId: String)

    @Query("SELECT * FROM round_state")
    suspend fun getAllRounds(): List<RoundStateEntity>
}