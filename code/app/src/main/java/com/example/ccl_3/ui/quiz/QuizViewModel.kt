package com.example.ccl_3.ui.quiz

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ccl_3.data.db.RoundStateEntity
import com.example.ccl_3.data.repository.QuizRepository
import com.example.ccl_3.data.repository.RoundRepository
import com.example.ccl_3.model.Country
import com.example.ccl_3.model.FlagQuestion
import com.example.ccl_3.model.RoundConfig
import com.example.ccl_3.model.RoundMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


private const val TAG ="QuizViewModel"
class QuizViewModel(
    private val repository: QuizRepository,
    private val roundRepository: RoundRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(QuizUiState())
    val uiState: StateFlow<QuizUiState> = _uiState.asStateFlow()

//    private val roundConfig = RoundConfig(RoundMode.GLOBAL)
    private var currentConfig: RoundConfig? = null
    private var allCountries: List<Country> = emptyList()
    private var remainingCountries = mutableListOf<Country>()

    private var currentCountry: Country? = null

//    init {
//        Log.d(TAG, "ViewModel created")
//        viewModelScope.launch {
//            loadCountries()
//            loadRoundState()
//            loadNextQuestion()
//            delay(2500)
//            hideResumedBanner()
//        }
//    }
    fun  setRoundConfig(config: RoundConfig){
        if(currentConfig == config) return
        currentConfig = config
        initializeRound(config)
    }
    private fun initializeRound(config: RoundConfig){
        viewModelScope.launch {
            loadCountries(config)
            loadRoundState(config)
            loadNextQuestion()
        }
    }

    private suspend fun loadCountries(config: RoundConfig) {
        allCountries = when(config.mode){
            RoundMode.GLOBAL -> repository.getAllCountries()
            RoundMode.REGION -> repository.getAllCountries()
                .filter{it.region == config.parameter}
        }
        resetRotation()
    }

    private suspend fun loadRoundState(config: RoundConfig){
        val saved = roundRepository.load(config)

        if(saved == null){
//            resetRotation()
            Log.d(TAG, "No saved round state found")
            return
        }
        Log.d(
            TAG,
            "Restored round: used=${saved.usedCountryCodes.size}, " +
                    "correct=${saved.correctCount}, wrong=${saved.wrongCount}"
        )
        val usedCodes = saved.usedCountryCodes.toSet()

        remainingCountries = allCountries
            .filterNot {it.code in usedCodes}
            .shuffled()
            .toMutableList()

        _uiState.value = _uiState.value.copy(
            correctCount = saved.correctCount,
            wrongCount = saved.wrongCount,
            answeredCount = usedCodes.size,
            totalCount = saved.totalCount,
            showResumedBanner = true,
        )
    }
    private fun hideResumedBanner(){
        _uiState.value = _uiState.value.copy(showResumedBanner = false)
    }


    private fun resetRotation() {
        remainingCountries = allCountries.shuffled().toMutableList()

        _uiState.value = _uiState.value.copy(
            answeredCount = 0,
            correctCount = 0,
            wrongCount = 0,
            totalCount = allCountries.size
        )
    }

    private  fun clearRoundState() {
        Log.d(TAG, "Clearing round state from DB")
        viewModelScope.launch {
            roundRepository.clear(currentConfig!!)
        }
    }

    private fun loadNextQuestion() {
        if (remainingCountries.isEmpty()) {
            Log.d(TAG, "Round completed — clearing saved state")
            clearRoundState()
            resetRotation()
        }
        currentCountry = remainingCountries.first()
        val correct = currentCountry!!

        val answered = allCountries.size - remainingCountries.size

        val wrongOptions = allCountries
            .filter { it.code != correct.code }
            .shuffled()
            .take(3)
            .map { it.name }

        val options = (wrongOptions + correct.name).shuffled()
        val correctIndex = options.indexOf(correct.name)

        _uiState.value = _uiState.value.copy(
            question = FlagQuestion(
                countryCode = correct.code,
                flagUrl = correct.flagUrl,
                options = options,
                correctIndex = correctIndex
            ),
            answeredCount = answered,
            totalCount = allCountries.size,
            isLoading = false,
            isCorrect = null,
            showFeedback = false,
            selectedIndex = null,
        )
    }

    fun onAnswerSelected(index: Int) {
        val country = currentCountry ?: return
        remainingCountries.remove(country)
        val question = _uiState.value.question ?: return
        val isCorrect = index == question.correctIndex

        _uiState.value = _uiState.value.copy(
            selectedIndex = index,
            isCorrect = isCorrect,
            showFeedback = true,
            correctCount = if (isCorrect) _uiState.value.correctCount +1 else _uiState.value.correctCount,
            wrongCount = if (!isCorrect) _uiState.value.wrongCount +1 else _uiState.value.wrongCount
        )
        persistRoundState()

    }

    private fun persistRoundState(){
        val used = allCountries
            .map{it.code}
            .minus(remainingCountries.map{it.code})
        val usedCount = allCountries.size - remainingCountries.size
        Log.d(
            TAG,
            "Persisting round: used=$usedCount, " +
                    "correct=${_uiState.value.correctCount}, " +
                    "wrong=${_uiState.value.wrongCount}"
        )

        viewModelScope.launch {
            roundRepository.save(
                currentConfig!!,
                RoundStateEntity(
                    roundId = currentConfig!!.id(),
                    usedCountryCodes = used,
                    correctCount = _uiState.value.correctCount,
                    wrongCount = _uiState.value.wrongCount,
                    totalCount = allCountries.size
                )
            )
        }
    }
    fun onNextClicked() {
        loadNextQuestion()
        persistRoundState()
    }

    fun dismissFeedback() {
        _uiState.value = _uiState.value.copy(
            selectedIndex = null,
            isCorrect = null,
            showFeedback = false
        )
    }

    fun onResetClicked(){
        _uiState.value = _uiState.value.copy(showResetConfirm = true)
    }

    fun onResetDismissed(){
        _uiState.value = _uiState.value.copy(showResetConfirm = false)
    }
    fun onResetConfirmed(){
        viewModelScope.launch {
            roundRepository.clear(currentConfig!!)
        }
        resetRotation()
        loadNextQuestion()

        _uiState.value = _uiState.value.copy(
            showResetConfirm = false,
            answeredCount = 0,
            correctCount = 0,
            wrongCount = 0,
        )
    }

}
