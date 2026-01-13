package com.example.ccl_3.model



enum class RoundMode {
    GLOBAL,
    REGION
}

data class RoundConfig(
    val mode: RoundMode,
    val parameter: String? = null,
    val gameMode: GameMode,
    val roundType: RoundType
){
    fun id(): String =
        when(mode){
            RoundMode.GLOBAL -> "GLOBAL:${gameMode.name}:${roundType.name}"
            RoundMode.REGION -> "REGION$parameter:${gameMode.name}:${roundType.name}"
        }

    fun displayName(): String =
        when(mode){
            RoundMode.GLOBAL -> "Global"
            RoundMode.REGION -> parameter ?: "Region"
        }
}