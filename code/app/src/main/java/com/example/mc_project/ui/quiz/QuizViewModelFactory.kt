package com.example.mc_project.ui.quiz


import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.mc_project.data.repository.QuizRepository
import com.example.mc_project.data.repository.RoundRepository

class QuizViewModelFactory(
    private  val quizRepository: QuizRepository,
    private val roundRepository: RoundRepository
) : ViewModelProvider.Factory{

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>):T{
        if (modelClass.isAssignableFrom(QuizViewModel::class.java)) {
            return QuizViewModel(quizRepository, roundRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }

}