package com.example.ccl_3.data.repository

import com.example.ccl_3.data.db.RoundStateDao
import com.example.ccl_3.data.db.RoundStateEntity
import com.example.ccl_3.model.RoundConfig

class RoundRepository(
    private val dao: RoundStateDao
) {
    suspend fun load(config: RoundConfig): RoundStateEntity? =
        dao.getRoundState(config.id())
    suspend fun save(config: RoundConfig, state: RoundStateEntity) =
        dao.saveRoundState(state)
    suspend fun clear(config: RoundConfig) =
        dao.clear(config.id())
    suspend fun getAllRounds() : List <RoundStateEntity> =
        dao.getAllRounds()
    suspend fun getLatestRound(): RoundStateEntity? = dao.getLatestRound()
}