package com.example.ccl_3.ui.quiz

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.ccl_3.data.db.DatabaseProvider
import com.example.ccl_3.data.repository.BookmarkRepository
import com.example.ccl_3.data.repository.QuizRepository
import com.example.ccl_3.data.repository.RoundRepository
import com.example.ccl_3.data.repository.RoundResultRepository
import com.example.ccl_3.model.BookmarkType
import com.example.ccl_3.model.Difficulty
import com.example.ccl_3.model.GameMode
import com.example.ccl_3.model.QuizSource
import com.example.ccl_3.model.RoundConfig
import com.example.ccl_3.model.RoundMode
import com.example.ccl_3.model.RoundType
import com.example.ccl_3.ui.components.AppTopBar
import com.example.ccl_3.ui.components.NavigationIcon
import com.example.ccl_3.ui.navigation.LocalAppNavigator
import com.example.ccl_3.ui.theme.AppColors


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizScreen(
    quizSource: QuizSource,
    onBack: () -> Unit,
    onSummary: () -> Unit
) {
    val appNavigator = LocalAppNavigator.current
    val context = LocalContext.current
    val roundType = when (quizSource) {
        is QuizSource.Standard -> if (quizSource.difficulty == Difficulty.PRACTICE) RoundType.PRACTICE else RoundType.TIMED
        is QuizSource.Bookmarks -> RoundType.PRACTICE
    }
    val roundConfig = when (quizSource) {
        is QuizSource.Standard -> RoundConfig(
            mode = if (quizSource.isGlobal) RoundMode.GLOBAL else RoundMode.REGION,
            parameter = quizSource.regionName,
            gameMode = quizSource.gameMode,
            roundType = roundType,
            difficulty = quizSource.difficulty
        )
        is QuizSource.Bookmarks -> RoundConfig(
            mode = RoundMode.BOOKMARKS,
            parameter = quizSource.contentType.name,
            gameMode = GameMode.GUESS_FLAG,
            roundType = roundType,
            difficulty = Difficulty.PRACTICE
        )
    }

    val quizRepository = remember { QuizRepository(context.applicationContext) }
    val roundRepository = remember {
        RoundRepository(
            DatabaseProvider.getDatabase(context).roundStateDao()
        )
    }
    val roundResultRepository = remember {
        RoundResultRepository(
            DatabaseProvider.getDatabase(context).roundResultDao()
        )

    }
    val bookmarkRepository = remember {
        BookmarkRepository(
            DatabaseProvider.getDatabase(context).bookmarkDao()
        )
    }
    val viewModel: QuizViewModel = viewModel(
        factory = QuizViewModelFactory(
            quizRepository = quizRepository,
            roundRepository = roundRepository,
            roundResultRepository = roundResultRepository,
            bookmarkRepository = bookmarkRepository,
            appContext = context.applicationContext
        )
    )

    var showConfirmExit by remember { mutableStateOf(false) }

    DisposableEffect(Unit) {
        onDispose {
            viewModel.persistRoundState()
        }
    }

    val uiState by viewModel.uiState.collectAsState()
//    uiState.errorMessage?.let { message ->
//        QuizErrorScreen(
//            message = message,
//            onRetry = { viewModel.retry() }
//        )
//        return
//    }
    BackHandler {
        if (uiState.roundFinished) {
            onSummary()
        } else {
            showConfirmExit = true
        }
    }

    if (showConfirmExit) {
        AlertDialog(
            onDismissRequest = { showConfirmExit = false },
            title = { Text("Confirm exit") },
            text = { Text("Are you sure you want to exit? Your progress will be saved.") },
            confirmButton = {
                TextButton(onClick = {
                    showConfirmExit = false
                    appNavigator.navigateToMain()
                }) {
                    Text("Yes")
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmExit = false }) {
                    Text("No")
                }
            }
        )
    }

    if (uiState.isRoundFailed) {
        RoundFailedOverlay(
            livesLeft = uiState.remainingLives ?: 0,
            correct = uiState.correctCount,
            wrong = uiState.wrongCount,
            onGoToSummary = onSummary,
            onRestart = { viewModel.onRetryRound() }
        )
        return
    }

    LaunchedEffect(uiState.roundFinished) {
        if (uiState.roundFinished) {
            onSummary()
        }
    }

    if (uiState.roundFinished) {
        // Will navigate via LaunchedEffect
        return
    }

// 3. No question yet → loading
    val question = uiState.question

    if (question == null && !uiState.isLoading) {
        Text("Loading...")
        return
    }

    LaunchedEffect(roundConfig) {
        viewModel.setRoundConfig(roundConfig)
    }

    androidx.compose.material3.Scaffold(
        topBar = {
            AppTopBar(
                title = when (roundConfig.mode) {
                    RoundMode.REGION -> roundConfig.parameter ?: "Region"
                    RoundMode.GLOBAL -> "Global"
                    RoundMode.BOOKMARKS -> "Bookmarks"
                },
                navigationIcon = NavigationIcon.Home,
                onNavigationClick = {
                    if (uiState.roundFinished) {
                        onSummary()
                    } else {
                        showConfirmExit = true
                    }
                }
            )
        },
        containerColor = AppColors.Primary
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f),
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
                        )
                    )
                )
        ) {

        if (uiState.isLoading || question == null) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            return@Box
        }

        val promptUrl = if (roundConfig.gameMode == GameMode.GUESS_COUNTRY) {
            uiState.shapeUrl
        } else {
            question.prompt
        }

        val imageRequest = ImageRequest.Builder(context)
            .data(promptUrl)
            .size(512)
            .crossfade(true)
            .diskCachePolicy(coil.request.CachePolicy.ENABLED)
            .memoryCachePolicy(coil.request.CachePolicy.ENABLED)
            .networkCachePolicy(coil.request.CachePolicy.ENABLED)
            .build()

        val optionPalette = listOf(
            Color(0xFF5B8DEF), // blue
            Color(0xFFFF6B6B), // red
            Color(0xFF51CF66), // green
            Color(0xFFFFCC00)  // yellow
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(
                    text = "${uiState.answeredCount} / ${uiState.totalCount} ",
                    style = MaterialTheme.typography.bodyMedium,
                    color = AppColors.TextWhite
                )
            }

            item {
                LinearProgressIndicator(
                    progress = { uiState.answeredCount.toFloat() / uiState.totalCount },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween

                ){
                    Text(
                        text = "✅ ${uiState.correctCount}   ❌ ${uiState.wrongCount}",
                        style = MaterialTheme.typography.bodySmall,
                        color = AppColors.TextWhite
                    )
                    uiState.remainingLives?.let { lives ->
                        Row {
                            repeat(lives) {
                                Icon(
                                    Icons.Default.Favorite, tint = Color.Red,
                                    contentDescription = "hearts",
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(Modifier.width(8.dp))
                            }
                        }
                    }

                    Text(
                        text = com.example.ccl_3.ui.quiz.formatTime(uiState.elapsedTimeMillis),
                        style = MaterialTheme.typography.titleMedium,
                        color = AppColors.TextWhite
                    )
                }
            }

            item {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    tonalElevation = 2.dp,
                    color = AppColors.Secondary,
                    modifier = Modifier.fillMaxWidth(),
                    border = BorderStroke(1.dp, color = AppColors.Stroke)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        AsyncImage(
                            model = imageRequest,
                            contentDescription = if (roundConfig.gameMode == GameMode.GUESS_COUNTRY) "Country shape" else "Flag",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                        )
                    }
                }
            }

            item {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    userScrollEnabled = false
                ) {
                    itemsIndexed(question.options) { index, answer ->
                        val isSelected = uiState.selectedIndex == index
                        val paletteIndex = index % optionPalette.size
                        val baseColor = optionPalette[paletteIndex]
                        OptionButton(
                            text = answer,
                            containerColor = baseColor,
                            isSelected = isSelected,
                            showFeedback = uiState.showFeedback,
                            onClick = { viewModel.onAnswerSelected(index) },
                            enabled = !uiState.showFeedback
                        )
                    }
                }
            }

            item {
                Button(
                    onClick = {viewModel.onResetClicked()},
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Red
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 20.dp)
                ) {
                    Text("Restart Round")
                }
            }
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
        AnimatedVisibility(
            visible = uiState.showResumedBanner,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 50.dp),
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
        val bookmarkType = if (roundConfig.gameMode == GameMode.GUESS_COUNTRY) {
            BookmarkType.SHAPE
        } else {
            BookmarkType.FLAG
        }

        val bookmarkLabel = if (bookmarkType == BookmarkType.SHAPE) "Add flag to learning notebook" else "Add shape to learning notebook"
        val showBookmark = roundConfig.mode != RoundMode.BOOKMARKS

        if (uiState.showFeedback) {
            BackHandler(enabled = true) {}
            FeedbackDialog(
                isCorrect = uiState.isCorrect!!,
                correctAnswer = question.options[question.correctIndex],
                bookmarkLabel = bookmarkLabel,
                showBookmark = showBookmark,
                isBookmarked = uiState.isBookmarked,
                onToggleBookmark = { viewModel.toggleBookmark() },
                onNext = {
                    viewModel.dismissFeedback()
                    viewModel.onNextClicked()
                }
            )
        }
    }
    }
}

@Composable
private fun OptionButton(
    text: String,
    containerColor: Color,
    isSelected: Boolean,
    showFeedback: Boolean,
    enabled: Boolean,
    onClick: () -> Unit
) {
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val targetScale = when {
        isSelected -> 1.08f
        pressed -> 1.03f
        else -> 1f
    }
    val scale by animateFloatAsState(
        targetValue = targetScale,
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = "pressScale"
    )
    val border = if (isSelected && !showFeedback) BorderStroke(2.dp, Color.White.copy(alpha = 0.85f)) else null

    ElevatedButton(
        onClick = onClick,
        enabled = enabled,
        interactionSource = interaction,
        shape = RoundedCornerShape(22.dp),
        border = border,
        colors = ButtonDefaults.elevatedButtonColors(
            containerColor = containerColor,
            contentColor = Color.White,
            disabledContainerColor = containerColor,
            disabledContentColor = Color.White
        ),
        modifier = Modifier
            .fillMaxWidth()
            .height(132.dp)
            .graphicsLayer { scaleX = scale; scaleY = scale }
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = text,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f)
            )
        }
    }
}
//@Composable
//fun QuizErrorScreen(message: String, onRetry: () -> Unit) {
//    Column(
//        modifier = Modifier.fillMaxSize(),
//        verticalArrangement = Arrangement.Center,
//        horizontalAlignment = Alignment.CenterHorizontally
//    ) {
//        Text(message, color = MaterialTheme.colorScheme.error)
//        Spacer(Modifier.height(12.dp))
//        Button(onClick = onRetry) {
//            Text("Retry")
//        }
//    }
//}