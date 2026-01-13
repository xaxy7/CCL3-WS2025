package com.example.ccl_3.ui.quiz


import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.ccl_3.data.repository.QuizRepository
import com.example.ccl_3.data.repository.RoundRepository

class QuizViewModelFactory(
    private val quizRepository: QuizRepository,
    private val roundRepository: RoundRepository,
    private val appContext: Context
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(QuizViewModel::class.java)) {
            return QuizViewModel(
                quizRepository,
                roundRepository,
                appContext
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
