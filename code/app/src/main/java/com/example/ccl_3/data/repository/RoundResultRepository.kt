package com.example.ccl_3.data.repository

import com.example.ccl_3.data.db.RoundResultDao
import com.example.ccl_3.data.db.mappers.toEntity
import com.example.ccl_3.data.db.mappers.toModel
import com.example.ccl_3.model.RoundResult

class RoundResultRepository(private val dao: RoundResultDao) {

    suspend fun save(result: RoundResult){
        dao.insert(
            result.toEntity()
        )
    }
    suspend fun getLastResult(): RoundResult?{
        return dao.getLast()?.toModel()
    }
    suspend fun getAllResults(): List<RoundResult> {
        return dao.getAll().map { it.toModel() }
    }
}
