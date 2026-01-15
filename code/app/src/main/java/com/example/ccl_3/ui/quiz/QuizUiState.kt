package com.example.ccl_3.ui.quiz

import com.example.ccl_3.model.CountryQuestion
import com.example.ccl_3.model.RoundResult

data class QuizUiState(
    val question: CountryQuestion? = null,
    val shapeUrl: String? = null,
    val selectedIndex: Int? = null,
    val isCorrect: Boolean? = null,
    val showFeedback: Boolean = false,
    val isLoading: Boolean = true,

    //for progress display
    val answeredCount: Int = 0,
    val totalCount: Int = 0,

    //for tracking correct and incorrect guesses
    val correctCount: Int = 0,
    val wrongCount: Int = 0,

    //for round reset popup
    val showResetConfirm : Boolean = false,

    //for round resumed popup
    val showResumedBanner: Boolean = false,


    val isRoundFailed: Boolean = false,
    val remainingLives: Int? = null,
    val roundFinished: Boolean = false,
    val lastResult: RoundResult? = null

)
