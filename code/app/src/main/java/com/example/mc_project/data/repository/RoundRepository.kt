package com.example.mc_project.data.repository

import com.example.mc_project.data.db.RoundStateDao
import com.example.mc_project.data.db.RoundStateEntity
import com.example.mc_project.model.RoundConfig

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
}