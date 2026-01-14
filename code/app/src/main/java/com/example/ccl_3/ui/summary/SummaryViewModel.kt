package com.example.ccl_3.ui.summary

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ccl_3.data.repository.QuizRepository
import com.example.ccl_3.data.repository.RoundResultRepository
import com.example.ccl_3.model.Country
import com.example.ccl_3.model.RoundResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SummaryViewModel(
    private val roundResultRepository: RoundResultRepository,
    private val quizRepository: QuizRepository
) : ViewModel() {

    private val _result = MutableStateFlow<RoundResult?>(null)
    private val _countries = MutableStateFlow<List<Country>>(emptyList())
    val result: StateFlow<RoundResult?> = _result

    init {
        viewModelScope.launch {
            _countries.value = quizRepository.ensureCountriesLoaded()
            _result.value = roundResultRepository.getLastResult()
        }
    }
    fun getFlagUrl(code: String): String? {
        val countries = _countries.value

        Log.d("FLAG_DEBUG", "SummaryViewModel sees cache size = ${countries.size}")

        return countries
            .firstOrNull { it.code == code }
            ?.flagUrl
    }

}
