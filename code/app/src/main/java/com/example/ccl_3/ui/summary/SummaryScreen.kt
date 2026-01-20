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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.ccl_3.data.api.ApiClient
import com.example.ccl_3.data.db.DatabaseProvider
import com.example.ccl_3.data.repository.QuizRepository
import com.example.ccl_3.data.repository.RoundResultRepository
import com.example.ccl_3.model.GameMode
import com.example.ccl_3.ui.navigation.Routes
import com.example.ccl_3.ui.quiz.formatTime


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SummaryScreen(navController: NavHostController ) {
    val context = LocalContext.current

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

    val timeText = resultSafe.timeTakenMillis?.let { formatTime(it) } ?: "--:--"


    if (result == null) {
        Text("Loading summary...")
        return
    }
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Summary") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {},
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Text("Round Summary", style = MaterialTheme.typography.headlineMedium)

            Spacer(Modifier.height(8.dp))
            Text(
                text = "#${result!!.id}",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = buildString {
                    if (result!!.isGlobal) append("Global")
                    else append(result!!.region)

                    append(" · ")

                    append(
                        when (result!!.gameMode) {
                            GameMode.GUESS_FLAG -> "Flag Guessing"
                            GameMode.GUESS_COUNTRY -> "Country Guessing"
                        }
                    )

                    append(" · ")

                    append(result!!.roundType.name.lowercase().replaceFirstChar { it.uppercase() })
                },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem("✔", "Correct", result!!.correctCount, Color(0xFF4CAF50))
                StatItem("✖", "Wrong", result!!.wrongCount, Color(0xFFF44336))
                StatItem("🎯", "Total", result!!.totalGuesses, MaterialTheme.colorScheme.primary)
            }
            val accuracy =
                if (result!!.totalGuesses > 0)
                    result!!.correctCount.toFloat() / result!!.totalGuesses
                else 0f
            val animated by animateFloatAsState(targetValue = accuracy)
            Spacer(Modifier.height(12.dp))

            Text("Accuracy: ${(accuracy * 100).toInt()}%")
            Spacer(Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = animated,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(50)),
                color = Color(0xFF4CAF50), // green
                trackColor = Color(0xFFF44336).copy(alpha = 0.3f) // subtle red
            )
            Spacer(Modifier.height(16.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Timer,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = timeText,
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }


            Text("Countries:")

            Spacer(Modifier.height(8.dp))

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f), // <-- makes only list scroll
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val pairs = result!!.countryCodes.zip(result!!.answers)

                itemsIndexed(pairs) { index, pair ->
                    val code = pair.first
                    val answer = pair.second

                    SummaryCountryRow(
                        index = index,
                        code = code,
                        answer = answer,
                        result = result!!,
                        viewModel = viewModel
                    )
                }
            }
        }
    }


}
