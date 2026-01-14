package com.example.ccl_3.ui.summary

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.ccl_3.data.db.DatabaseProvider
import com.example.ccl_3.data.db.RoundResultDao
import com.example.ccl_3.data.repository.RoundResultRepository
import com.example.ccl_3.model.RoundResult

@Composable
fun SummaryScreen(navController: NavHostController ) {
    val context = LocalContext.current

    val repository = remember {
        RoundResultRepository(
            DatabaseProvider.getDatabase(context).roundResultDao()
        )
    }

    val viewModel: SummaryViewModel = viewModel(
        factory = SummaryViewModelFactory(repository)
    )

    val result by viewModel.result.collectAsState()

    Log.d("SUMMARY_DEBUG", "SummaryScreen VM result = $result")

    if (result == null) {
        Text("Loading summary...")
        return
    }
    Column(Modifier.padding(16.dp)) {
        Text("Round Summary", style = MaterialTheme.typography.headlineMedium)

        Text("Correct: ${result!!.correctCount}")
        Text("Wrong: ${result!!.wrongCount}")
        Text("Total: ${result!!.totalGuesses}")
        Text("Completed: ${result!!.completed}")

        Spacer(Modifier.height(16.dp))

        Text("Countries:")
        result!!.countryCodes.forEachIndexed { index, element ->
            Text("${index}: $element")
        }
    }
}
