package com.example.ccl_3.model


fun rulesFor(config: RoundConfig): RoundRules =
    when (config.roundType){
        RoundType.PRACTICE -> RoundRules(
            null,
            null
        )
        RoundType.COMPETITIVE -> RoundRules(
            3,
            5 * 60 * 1000L // 5 minutes
        )
    }