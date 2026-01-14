package com.example.ccl_3.ui.summary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.ccl_3.data.api.ApiClient
import com.example.ccl_3.data.repository.QuizRepository
import com.example.ccl_3.data.repository.RepositoryProvider
import com.example.ccl_3.data.repository.RoundResultRepository

class SummaryViewModelFactory(
    private val roundResultRepository: RoundResultRepository,
    private val quizRepository: QuizRepository
): ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(SummaryViewModel::class.java)){
             RepositoryProvider.provideQuizRepository(ApiClient.api)
            return SummaryViewModel(roundResultRepository, quizRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}