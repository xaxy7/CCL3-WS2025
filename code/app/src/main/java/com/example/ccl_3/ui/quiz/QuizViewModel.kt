package com.example.ccl_3.ui.quiz

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ccl_3.data.db.RoundStateEntity
import com.example.ccl_3.data.repository.QuizRepository
import com.example.ccl_3.data.repository.RoundRepository
import com.example.ccl_3.data.repository.RoundResultRepository
import com.example.ccl_3.model.Country
import com.example.ccl_3.model.CountryQuestion
import com.example.ccl_3.model.GameMode
import com.example.ccl_3.model.RoundConfig
import com.example.ccl_3.model.RoundMode
import com.example.ccl_3.model.RoundResult
import com.example.ccl_3.model.RoundSession
import com.example.ccl_3.model.rulesFor
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


private const val TAG ="QuizViewModel"
class QuizViewModel(
    private val quizRepository: QuizRepository,
    private val roundRepository: RoundRepository,
    private val roundResultRepository: RoundResultRepository,
    private val appContext: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(QuizUiState())
    val uiState: StateFlow<QuizUiState> = _uiState.asStateFlow()
    private var usedCountryCodes = mutableListOf<String>()

    private var session: RoundSession? = null

//    private val roundConfig = RoundConfig(RoundMode.GLOBAL)
    private var currentConfig: RoundConfig? = null
    private var allCountries: List<Country> = emptyList()
    private var remainingCountries = mutableListOf<Country>()

    private var currentCountry: Country? = null

    fun  setRoundConfig(config: RoundConfig){
        if(currentConfig == config) return
        currentConfig = config
        initializeRound(config)
    }

    private fun initializeRound(config: RoundConfig){
        val rules = rulesFor(config)

        session = RoundSession(
            remainingLives = rules.lives,
            remainingTimeMillis = rules.timeMillis
        )

        viewModelScope.launch {
            loadCountries(config)
            Log.d("CACHE_DEBUG", "QuizViewModel allCountries size = ${allCountries.size}")
            Log.d("CACHE_DEBUG", "Repo cache size = ${quizRepository.cachedCountries?.size}")
            loadRoundState(config)
            loadNextQuestion()
            delay(3000)
            hideResumedBanner()
        }
    }
    private fun hasSilhouette(code: String): Boolean{
        return try{
            val path = "all/${code.lowercase()}/256.png"
            appContext.assets.open(path).close()
            true
        } catch (e: Exception){

            false
        }
    }
    private suspend fun loadCountries(config: RoundConfig) {

        val base = when (config.mode){
            RoundMode.GLOBAL -> quizRepository.getAllCountries()
            RoundMode.REGION -> quizRepository.getAllCountries()
                .filter { it.region == config.parameter }
        }

        allCountries = when (config.gameMode){
            GameMode.GUESS_COUNTRY ->
                base.filter { hasSilhouette(it.code) }
            else -> base
        }
//        Log.d(TAG, "Countries after filtering: ${allCountries.size}")


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
        usedCountryCodes = saved.usedCountryCodes as MutableList<String>
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
        usedCountryCodes.clear()
        viewModelScope.launch {
            roundRepository.clear(currentConfig!!)
        }
    }

    private fun loadNextQuestion() {
        Log.d(TAG, "$usedCountryCodes")
        if (remainingCountries.isEmpty()) {
            Log.d(TAG, "Round completed — clearing saved state")
            clearRoundState()
            resetRotation()
        }
        currentCountry = remainingCountries.first()
        val correct = currentCountry!!
        usedCountryCodes.add(correct.code)


        val answered = allCountries.size - remainingCountries.size +1

        val wrongOptions = allCountries
            .filter { it.code != correct.code }
            .shuffled()
            .take(3)
            .map { it.name }

        val options = (wrongOptions + correct.name).shuffled()
        val correctIndex = options.indexOf(correct.name)

        val shapeUrl = when (currentConfig?.gameMode) {
            GameMode.GUESS_COUNTRY -> "file:///android_asset/all/${correct.code.lowercase()}/256.png"
            else -> null
        }

        _uiState.value = _uiState.value.copy(
            question = CountryQuestion(
                countryCode = correct.code,
                prompt = correct.flagUrl,
                options = options,
                correctIndex = correctIndex
            ),
            shapeUrl = shapeUrl,
            answeredCount = answered,
            totalCount = allCountries.size,
            isLoading = false,
            isCorrect = null,
            showFeedback = false,
            selectedIndex = null,
        )
    }

    fun onAnswerSelected(index: Int) {
        if (session?.isFailed == true) return
        val country = currentCountry ?: return

        remainingCountries.remove(country)
        Log.d(TAG, "$remainingCountries")
//        if (remainingCountries.isEmpty()) {
//            onRoundCompleted()
//            return
//        }
        val question = _uiState.value.question ?: return
        val isCorrect = index == question.correctIndex


        if(!isCorrect && session?.remainingLives != null){
            val newLives = session!!.remainingLives!! -1

            session = session!!.copy(
                remainingLives = newLives,
                isFailed = newLives <= 0
            )
        }
        if (session?.isFailed == true) {
            onRoundFailed()
            return
        }


        _uiState.value = _uiState.value.copy(
            selectedIndex = index,
            isCorrect = isCorrect,
            showFeedback = true,
            correctCount = if (isCorrect) _uiState.value.correctCount +1 else _uiState.value.correctCount,
            wrongCount = if (!isCorrect) _uiState.value.wrongCount +1 else _uiState.value.wrongCount
        )
        persistRoundState()

    }
    private fun onRoundFailed(){
        val result = buildRoundResult(completed = false)

        viewModelScope.launch {
            roundResultRepository.save(result)
        }


        _uiState.value = _uiState.value.copy(
            showFeedback = true,
            isRoundFailed = true
        )
    }
    private fun onRoundCompleted() {
        val result = buildRoundResult(completed = true)
        Log.d(TAG, "roundCompleted")
        Log.d("SUMMARY_DEBUG", "Round finished, result = $result")

        viewModelScope.launch {
            roundResultRepository.save(result)
        }

        _uiState.value = _uiState.value.copy(
            roundFinished = true,
            lastResult = result
        )
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
    private fun buildRoundResult(completed: Boolean): RoundResult{
        return RoundResult(

            roundId = currentConfig!!.id(),
            region = currentConfig!!.parameter,
            isGlobal = currentConfig!!.mode == RoundMode.GLOBAL,
            gameMode = currentConfig!!.gameMode,
            roundType = currentConfig!!.roundType,

            totalGuesses = _uiState.value.answeredCount,
            correctCount = _uiState.value.correctCount,
            wrongCount = _uiState.value.wrongCount,

            completed = completed,

            timeTakenMillis = null,
            livesLeft = session?.remainingLives,

            countryCodes = usedCountryCodes.toList()
        )

    }

    fun onNextClicked() {
        if (remainingCountries.isEmpty()) {
//            usedCountryCodes.add(currentCountry!!.code)
            onRoundCompleted()
            return
        }else{
            loadNextQuestion()
            persistRoundState()
        }
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
