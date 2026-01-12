package com.example.ccl_3.ui.quiz

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.ccl_3.data.api.ApiClient
import com.example.ccl_3.data.db.DatabaseProvider
import com.example.ccl_3.data.repository.QuizRepository
import com.example.ccl_3.data.repository.RoundRepository
import com.example.ccl_3.model.GameMode
import com.example.ccl_3.model.RoundConfig
import com.example.ccl_3.model.RoundMode


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizScreen(

    regionName: String,
    gameMode: GameMode

) {
    val context = LocalContext.current
    val quizRepository = remember {
        QuizRepository(ApiClient.api)
    }
    val roundRepository = remember {
        RoundRepository(
            DatabaseProvider.getDatabase(context).roundStateDao()
        )
    }
    val viewModel: QuizViewModel = viewModel(
        factory = QuizViewModelFactory(
            quizRepository = quizRepository,
            roundRepository = roundRepository
        )
    )

    val uiState by viewModel.uiState.collectAsState()
    val roundConfig = RoundConfig(
        mode = RoundMode.REGION,
        parameter = regionName,
        gameMode = gameMode
    )
    LaunchedEffect(roundConfig) {
        viewModel.setRoundConfig(roundConfig)
    }
    Box(modifier = Modifier.fillMaxSize()) {

        if (uiState.isLoading || uiState.question == null) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            return@Box
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .padding(top= 30.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
//            AnimatedVisibility(
//                visible = uiState.showResumedBanner,
//                enter = slideInVertically() + fadeIn(),
//                exit = slideOutVertically() + fadeOut(),
//            ) {
//                Surface(
//                    color = MaterialTheme.colorScheme.secondaryContainer,
//                    modifier = Modifier.fillMaxWidth()
//                ) {
//                    Text(
//                        text = "Round resumed",
//                        modifier = Modifier.padding(12.dp),
//                        style = MaterialTheme.typography.bodyMedium
//                    )
//                }
//            }
            Text(
                text = "${uiState.answeredCount} / ${uiState.totalCount} ",
                style = MaterialTheme.typography.bodyMedium
            )
            LinearProgressIndicator(
                progress = uiState.answeredCount.toFloat() / uiState.totalCount,
                modifier = Modifier.fillMaxWidth()
            )
            Text(
                text = "✅ ${uiState.correctCount}   ❌ ${uiState.wrongCount}",
                style = MaterialTheme.typography.bodySmall
            )

            AsyncImage(
                model = uiState.question!!.flagUrl,
                contentDescription = "Flag",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp, bottom = 20.dp)
                    .height(200.dp)
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                userScrollEnabled = false //
            ) {
                itemsIndexed(uiState.question!!.options) { index, answer ->
                    Button(
                        onClick = { viewModel.onAnswerSelected(index) },
                        enabled = !uiState.showFeedback,
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Blue
                        ),
                        modifier = Modifier
                            .aspectRatio(1f)
                            .fillMaxWidth()

                    ) {
                        Text(
                            text = answer,
                            fontSize = 20.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }



            Button(
                onClick = {viewModel.onResetClicked()},
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Red
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top= 20.dp)
            ) {
                Text("Restart Round")
            }

            if (uiState.showResetConfirm){
                AlertDialog(
                    onDismissRequest = {viewModel.onResetDismissed()},
                    title = {Text("Restart round?")},
                    text = { Text("Your current progress for this round will be lost.")},
                    confirmButton = {
                        TextButton(
                            onClick = {viewModel.onResetConfirmed()}
                        ) {
                            Text("Restart")
                        }
                    },
                    dismissButton = {
                        TextButton( onClick = {viewModel.onResetDismissed()} ) {
                            Text("Cancel")
                        }
                    }
                )
            }
        }
        AnimatedVisibility(
            visible = uiState.showResumedBanner,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top =50.dp),
            enter = slideInVertically() + fadeIn(),
            exit = slideOutVertically() + fadeOut()
        ) {
            Surface(
                tonalElevation = 2.dp,
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.secondaryContainer
            ) {
                Text(
                    text = "Round resumed",
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
        val sheetState = rememberModalBottomSheetState(
            skipPartiallyExpanded = true,
            confirmValueChange = {newValue ->
                newValue != SheetValue.Hidden
            }
        )
        if (uiState.showFeedback) {
            FeedbackBottomSheet(
                sheetState = sheetState,
                isCorrect = uiState.isCorrect!!,
                correctAnswer = uiState.question!!.options[uiState.question!!.correctIndex],
                onBookmark = {
                    // TODO: add Room bookmark logic later
                },
                onNext = {
                    viewModel.dismissFeedback()
                    viewModel.onNextClicked()
                },
                onDismiss = viewModel::dismissFeedback
            )
        }
    }
}
