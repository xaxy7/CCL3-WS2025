package com.example.ccl_3.model


fun rulesFor(config: RoundConfig): RoundRules{
    return when (config.difficulty){
        Difficulty.PRACTICE -> RoundRules(null, null)
        Difficulty.EASY -> RoundRules(10, null)
        Difficulty.MEDIUM -> RoundRules(5, null)
        Difficulty.HARD -> RoundRules(3, null)
        Difficulty.VERY_HARD -> RoundRules(1,null)
    }
}
