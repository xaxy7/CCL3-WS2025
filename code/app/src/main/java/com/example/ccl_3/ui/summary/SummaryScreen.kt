package com.example.ccl_3.ui.summary

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.ccl_3.model.RoundResult

@Composable
fun SummaryScreen(navController: NavHostController) {
    val result = remember {
        navController.currentBackStackEntry
            ?.savedStateHandle
            ?.get<RoundResult>("round_result")
    }

    Log.d("SUMMARY_DEBUG", "SummaryScreen loaded result = $result")

    if (result == null) {
        Text("No summary available")
        return
    }
    Column(Modifier.padding(16.dp)) {
        Text("Round Summary", style = MaterialTheme.typography.headlineMedium)

        Text("Correct: ${result.correctCount}")
        Text("Wrong: ${result.wrongCount}")
        Text("Total: ${result.totalGuesses}")
        Text("Completed: ${result.completed}")

        Spacer(Modifier.height(16.dp))

        Text("Countries:")
        result.countryCodes.forEach {
            Text(it)
        }
    }
}
