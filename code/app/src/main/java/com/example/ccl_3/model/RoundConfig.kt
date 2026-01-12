package com.example.ccl_3.model



enum class RoundMode {
    GLOBAL,
    REGION
}

data class RoundConfig(
    val mode: RoundMode,
    val parameter: String? = null
){
    fun id(): String =
        when(mode){
            RoundMode.GLOBAL -> "GLOBAL"
            RoundMode.REGION -> "REGION$parameter"
        }

    fun displayName(): String =
        when(mode){
            RoundMode.GLOBAL -> "Global"
            RoundMode.REGION -> parameter ?: "Region"
        }
}