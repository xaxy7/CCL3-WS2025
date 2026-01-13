package com.example.ccl_3.model



enum class RoundMode {
    GLOBAL,
    REGION
}

data class RoundConfig(
    val mode: RoundMode,
    val parameter: String? = null,
    val gameMode: GameMode
){
    fun id(): String =
        when(mode){
            RoundMode.GLOBAL -> "GLOBAL:${gameMode.name}"
            RoundMode.REGION -> "REGION$parameter:${gameMode.name}"
        }

    fun displayName(): String =
        when(mode){
            RoundMode.GLOBAL -> "Global"
            RoundMode.REGION -> parameter ?: "Region"
        }
}