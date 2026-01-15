package com.example.ccl_3.ui.summary

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
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
import com.example.ccl_3.data.api.ApiClient
import com.example.ccl_3.data.db.DatabaseProvider
import com.example.ccl_3.data.repository.QuizRepository
import com.example.ccl_3.data.repository.RoundResultRepository

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


    Log.d("SUMMARY_DEBUG", "SummaryScreen VM result = $result")

    if (result == null) {
        Text("Loading summary...")
        return
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Round Summary", style = MaterialTheme.typography.headlineMedium)

        Spacer(Modifier.height(8.dp))

        Text("Correct: ${result!!.correctCount}")
        Text("Wrong: ${result!!.wrongCount}")
        Text("Total: ${result!!.totalGuesses}")

        Spacer(Modifier.height(16.dp))

        Text("Countries:")

        Spacer(Modifier.height(8.dp))

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f), // <-- makes only list scroll
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            itemsIndexed(result!!.countryCodes) { index, code ->
                SummaryCountryRow(
                    index = index,
                    code = code,
                    result = result!!,
                    viewModel = viewModel
                )
            }
        }
    }


}

