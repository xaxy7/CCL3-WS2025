package com.example.ccl_3.ui.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ccl_3.BuildConfig
import com.example.ccl_3.data.db.DatabaseProvider
import com.example.ccl_3.data.repository.RoundRepository
import com.example.ccl_3.data.repository.RoundResultRepository
import com.example.ccl_3.model.AnswerResult
import com.example.ccl_3.model.GameMode
import com.example.ccl_3.model.RoundConfig
import com.example.ccl_3.model.RoundMode
import com.example.ccl_3.model.RoundResult
import com.example.ccl_3.model.parseRoundConfigFromId
import com.example.ccl_3.ui.debug.DebugViewModel
import com.example.ccl_3.ui.debug.DebugViewModelFactory
import com.example.ccl_3.ui.navigation.LocalAppNavigator
import com.example.ccl_3.ui.navigation.Routes
import com.example.ccl_3.ui.quiz.formatTime
import com.example.ccl_3.ui.region.ModeCard
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    onRegionSelected: (String, Boolean) -> Unit,
) {
    val appNavigator = LocalAppNavigator.current
    val context = LocalContext.current

    val roundRepository = remember {
        RoundRepository(
            DatabaseProvider.getDatabase(context).roundStateDao()
        )
    }
    val repository = remember {
        RoundResultRepository(
            DatabaseProvider.getDatabase(context).roundResultDao()
        )
    }

    val debugViewModel: DebugViewModel = viewModel(
        factory = DebugViewModelFactory(repository)
    )

    var activeRound by remember { mutableStateOf<Pair<com.example.ccl_3.data.db.RoundStateEntity, RoundConfig>?>(null) }
    var latestRound by remember { mutableStateOf<RoundResult?>(null) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        val unfinished = roundRepository.getLatestRound()
        val config = unfinished?.let { parseRoundConfigFromId(it.roundId) }
        if (unfinished != null && config != null) {
            activeRound = unfinished to config
        }
        latestRound = repository.getLastResult()
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Home") },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            activeRound?.let { (state, config) ->
                item {
                    ActiveRoundCard(
                        state = state,
                        config = config,
                        onResume = {
                            val regionName = config.parameter ?: "Global"
                            val isGlobal = config.mode == RoundMode.GLOBAL
                            appNavigator.navigateToQuiz(
                                regionName,
                                isGlobal,
                                config.gameMode,
                                config.difficulty
                            )
                        },
                        onEndRound = {
                            scope.launch {
                                // Build a minimal RoundResult from saved state
                                val answered = state.usedCountryCodes.size
                                val answers = if (answered == 0) emptyList() else buildList {
                                    repeat(state.correctCount.coerceAtMost(answered)) { add(AnswerResult.CORRECT) }
                                    repeat((answered - size).coerceAtLeast(0)) { add(AnswerResult.WRONG) }
                                }
                                val result = RoundResult(
                                    roundId = state.roundId,
                                    region = config.parameter,
                                    isGlobal = config.mode == RoundMode.GLOBAL,
                                    gameMode = config.gameMode,
                                    roundType = config.roundType,
                                    totalGuesses = answered,
                                    correctCount = state.correctCount,
                                    wrongCount = state.wrongCount,
                                    completed = false,
                                    timeTakenMillis = state.elapsedTimeMillis,
                                    livesLeft = null,
                                    countryCodes = state.usedCountryCodes,
                                    answers = answers
                                )
                                repository.save(result)
                                roundRepository.clear(config)
                                activeRound = null
                                latestRound = repository.getLastResult()
                            }
                        }
                    )
                }
            }

            if (activeRound == null && latestRound != null) {
                item {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        tonalElevation = 2.dp,
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text("Last round ready", style = MaterialTheme.typography.titleMedium)
                                Text(
                                    text = "View it in History",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            }
                            OutlinedButton(onClick = { appNavigator.navigateToHistory() }) {
                                Text("Open history")
                            }
                        }
                    }
                }
            }

//            item {
//                Surface(
//                    shape = RoundedCornerShape(24.dp),
//                    color = MaterialTheme.colorScheme.primaryContainer,
//                    tonalElevation = 2.dp,
//                    modifier = Modifier.fillMaxWidth()
//                ) {
//                    Text(
//                        text = "Game Modes",
//                        modifier = Modifier.padding(vertical = 12.dp),
//                        textAlign = TextAlign.Center,
//                        style = MaterialTheme.typography.titleLarge
//                    )
//                }
//            }

            if (BuildConfig.DEBUG) {
                // debug button remains commented out
            }

            items(regions) { region ->
                RegionCard(region = region, isGlobal = region.isGlobal) {
                    onRegionSelected(region.name, region.isGlobal)
                }
            }
        }
    }
}
