package com.example.ccl_3.ui.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.ccl_3.BuildConfig
import com.example.ccl_3.data.db.DatabaseProvider
import com.example.ccl_3.data.repository.RoundResultRepository
import com.example.ccl_3.model.GameMode
import com.example.ccl_3.model.RoundResult
import com.example.ccl_3.ui.debug.DebugViewModel
import com.example.ccl_3.ui.debug.DebugViewModelFactory
import com.example.ccl_3.ui.navigation.Routes
import com.example.ccl_3.ui.quiz.formatTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    onRegionSelected: (String, Boolean) -> Unit,
    navController: NavHostController,
) {
    val context = LocalContext.current

    val repository = remember {
        RoundResultRepository(
            DatabaseProvider.getDatabase(context).roundResultDao()
        )
    }

    val debugViewModel: DebugViewModel = viewModel(
        factory = DebugViewModelFactory(repository)
    )

    var latestRound by remember { mutableStateOf<RoundResult?>(null) }

    LaunchedEffect(Unit) {
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
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ){

            Surface(
                shape = RoundedCornerShape(24.dp),
                color = MaterialTheme.colorScheme.primaryContainer,
                tonalElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Game Modes",
                    modifier = Modifier.padding(vertical = 12.dp),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleLarge
                )
            }

            if (BuildConfig.DEBUG) {
                Button(onClick = {
//                debugViewModel.insertDebugRound()
                    navController.navigate(Routes.SUMMARY)
                }) {
                    Text("Insert Debug Round & Open Summary")
                }
            }

            LatestRoundCard(latestRound)

            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(regions) { region ->
                    RegionCard(region = region, isGlobal = region.isGlobal) {
                        onRegionSelected(region.name, region.isGlobal)
                    }
                }
            }
        }
    }
}

@Composable
private fun LatestRoundCard(result: RoundResult?) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        tonalElevation = 2.dp,
        color = MaterialTheme.colorScheme.surfaceVariant,
        modifier = Modifier.fillMaxWidth()
    ) {
        if (result == null) {
            Text(
                "Play a round to see it here",
                modifier= Modifier.padding(16.dp),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Latest round",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = buildString {
                        append("#${result.id} · ")
                        append(if (result.isGlobal) "Global" else result.region ?: "Unknown")
                        append(" · ")
                        append(
                            when (result.gameMode) {
                                GameMode.GUESS_FLAG -> "Flag Guessing"
                                GameMode.GUESS_COUNTRY -> "Country Guessing"
                            }
                        )
                        append(" · ")
                        append(result.roundType.name.lowercase().replaceFirstChar { it.uppercase() })
                    },
                    style = MaterialTheme.typography.titleMedium
                )

                val accuracy = if (result.totalGuesses > 0) result.correctCount.toFloat() / result.totalGuesses else 0f
                LinearProgressIndicator(
                    progress = accuracy,
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Accuracy ${(accuracy * 100).toInt()}%",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Timer, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Text(
                            text = result.timeTakenMillis?.let { formatTime(it) } ?: "--:--",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}
