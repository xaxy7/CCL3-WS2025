package com.example.ccl_3.ui.summary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ccl_3.data.repository.QuizRepository
import com.example.ccl_3.data.repository.RoundResultRepository
import com.example.ccl_3.model.Country
import com.example.ccl_3.model.GameMode
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


        return countries
            .firstOrNull { it.code == code }
            ?.flagUrl
    }
    fun getImageForCountry(code: String, result: RoundResult): String? {
        return when (result.gameMode) {
            GameMode.GUESS_FLAG -> getFlagUrl(code)
            GameMode.GUESS_COUNTRY -> getSilhouetteUrl(code)
        }
    }
    fun getSilhouetteUrl(code: String): String {
        return "file:///android_asset/all/${code.lowercase()}/256.png"
    }
    fun getCountryName(code: String): String {
        val countries = quizRepository.cachedCountries ?: emptyList()

        return countries
            .firstOrNull { it.code == code }
            ?.name
            ?: code // fallback
    }

}
