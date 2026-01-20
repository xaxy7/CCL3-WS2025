package com.example.ccl_3.ui.summary

import android.util.Log
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ccl_3.data.api.ApiClient
import com.example.ccl_3.data.db.DatabaseProvider
import com.example.ccl_3.data.repository.QuizRepository
import com.example.ccl_3.data.repository.RoundResultRepository
import com.example.ccl_3.ui.components.AppTopBar
import com.example.ccl_3.ui.components.NavigationIcon
import com.example.ccl_3.ui.navigation.LocalAppNavigator


@Composable
fun SummaryScreen(
    onNewGame: () -> Unit
) {
    val context = LocalContext.current
    val appNavigator = LocalAppNavigator.current

    val roundResultRepository = remember {
        RoundResultRepository(
            DatabaseProvider.getDatabase(context).roundResultDao()
        )
    }
    val quizRepository = remember {
        QuizRepository(ApiClient.api)
    }

    val viewModel: SummaryViewModel = viewModel(
        factory = SummaryViewModelFactory(roundResultRepository,quizRepository)
    )

    val result by viewModel.result.collectAsState()

    val resultSafe = result

    if (resultSafe == null) {
        Text("Loading summary...")
        return
    }

    val timeText = resultSafe.timeTakenMillis?.let { com.example.ccl_3.ui.quiz.formatTime(it) } ?: "--:--"
    val accuracy = if (resultSafe.totalGuesses > 0) {
        (resultSafe.correctCount.toFloat() / resultSafe.totalGuesses) * 100
    } else 0f

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Summary",
                navigationIcon = NavigationIcon.Home,
                onNavigationClick = { appNavigator.navigateToMain() }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                SummaryStat(
                    label = "Correct",
                    value = resultSafe.correctCount.toString(),
                    color = MaterialTheme.colorScheme.primary
                )
                SummaryStat(
                    label = "Wrong",
                    value = resultSafe.wrongCount.toString(),
                    color = MaterialTheme.colorScheme.error
                )
                SummaryStat(
                    label = "Accuracy",
                    value = "${accuracy.toInt()}%",
                    color = MaterialTheme.colorScheme.secondary
                )
            }

            val progress by animateFloatAsState(
                targetValue = accuracy / 100f,
                label = "accuracy"
            )
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(Icons.Default.Timer, contentDescription = "Time")
                Text(
                    text = "Time: $timeText",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Text(
                text = "Round review",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(top = 16.dp)
            )

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                itemsIndexed(resultSafe.countryCodes) { index, code ->
                    val answer = resultSafe.answers.getOrNull(index)
                    val imageUrl = viewModel.getImageForCountry(code, resultSafe)
                    Log.d("SummaryScreen", "Image URL for $code: $imageUrl")
                    ReviewRow(
                        index = index + 1,
                        countryName = viewModel.getCountryName(code),
                        imageUrl = imageUrl,
                        isCorrect = answer == com.example.ccl_3.model.AnswerResult.CORRECT
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onNewGame,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("New Game")
            }
        }
    }
}

@Composable
private fun SummaryStat(
    label: String,
    value: String,
    color: androidx.compose.ui.graphics.Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.headlineMedium,
            color = color
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun ReviewRow(
    index: Int,
    countryName: String,
    imageUrl: String?,
    isCorrect: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = "$index.",
                style = MaterialTheme.typography.bodyMedium
            )
            if (imageUrl != null) {
                coil.compose.AsyncImage(
                    model = imageUrl,
                    contentDescription = countryName,
                    modifier = Modifier.height(24.dp)
                )
            }
            Text(
                text = countryName,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f)
            )
        }
        Text(
            text = if (isCorrect) "✓" else "✗",
            style = MaterialTheme.typography.titleMedium,
            color = if (isCorrect) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
        )
    }
}
