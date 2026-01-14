package com.example.ccl_3.ui.quiz


import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.ccl_3.data.api.ApiClient
import com.example.ccl_3.data.repository.RepositoryProvider

class QuizViewModelFactory(
    private val appContext: Context
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(QuizViewModel::class.java)) {

            val quizRepository =
                RepositoryProvider.provideQuizRepository(ApiClient.api)

            val roundRepository =
                RepositoryProvider.provideRoundRepository(appContext)

            val roundResultRepository =
                RepositoryProvider.provideRoundResultRepository(appContext)

            return QuizViewModel(
                quizRepository,
                roundRepository,
                roundResultRepository,
                appContext
            ) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
