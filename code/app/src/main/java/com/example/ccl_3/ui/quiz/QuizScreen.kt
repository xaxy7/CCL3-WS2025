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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.ccl_3.data.api.ApiClient
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
import com.example.ccl_3.ui.navigation.Routes


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizScreen(
    navController: NavHostController,
    regionName: String,
    isGlobal: Boolean,
    gameMode: GameMode,
//    roundType: RoundType,
    difficulty: Difficulty,
    source: QuizSource = QuizSource.NORMAL,
    bookmarkType: BookmarkType? = null

) {
    val context = LocalContext.current
    val roundType =
        if (difficulty == Difficulty.PRACTICE)
            RoundType.PRACTICE
        else
            RoundType.COMPETITIVE

    val navigateHome = {
        navController.navigate(Routes.MAIN) {
            popUpTo(navController.graph.startDestinationId) { inclusive = false }
            launchSingleTop = true
        }
    }

    val quizRepository = remember {
        QuizRepository(ApiClient.api)
    }
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
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
        confirmValueChange = { value -> value != SheetValue.Hidden }
    )

    DisposableEffect(Unit) {
        onDispose {
            viewModel.persistRoundState()
        }
    }

    val uiState by viewModel.uiState.collectAsState()

    BackHandler(onBack = navigateHome)

    if (uiState.isRoundFailed) {
        RoundFailedOverlay(
            livesLeft = uiState.remainingLives ?: 0,
            correct = uiState.correctCount,
            wrong = uiState.wrongCount,
            onGoToSummary = { navController.navigate(Routes.SUMMARY){
                popUpTo(Routes.QUIZ) {
                    inclusive = true
                }
            } },
            onRestart = { viewModel.onRetryRound() }
        )
        return
    }

    LaunchedEffect(uiState.roundFinished, uiState.lastResult) {
        if (uiState.roundFinished && uiState.lastResult != null) {
            navController.navigate(Routes.SUMMARY) {
                popUpTo(Routes.QUIZ) {
                    inclusive = true
                }
            }
            navController.currentBackStackEntry
                ?.savedStateHandle
                ?.set("round_result", uiState.lastResult)
        }
    }

// 2. Round finished normally
    if (uiState.roundFinished) {
        // navigate or show finished overlay
        return
    }

// 3. No question yet → loading
    val question = uiState.question

    if (question == null && !uiState.isLoading) {
        Text("Loading...")
        return
    }
    val roundConfig = if(source == QuizSource.BOOKMARK && bookmarkType != null){
        RoundConfig(
            mode = RoundMode.GLOBAL,
            parameter = null,
            gameMode = if (bookmarkType == BookmarkType.SHAPE) GameMode.GUESS_COUNTRY else GameMode.GUESS_FLAG,
            roundType = roundType,
            difficulty = difficulty,
            source = source,
            bookmarkType = bookmarkType
        )
    } else if(isGlobal){
        RoundConfig(RoundMode.GLOBAL, null, gameMode,  roundType, difficulty = difficulty)
    } else{
        RoundConfig(
            mode = RoundMode.REGION,
            parameter = regionName,
            gameMode = gameMode,
            roundType = roundType,
            difficulty = difficulty
        )
    }
    LaunchedEffect(roundConfig) {
        viewModel.setRoundConfig(roundConfig)
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f),
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
                    )
                )
            )
    ) {
        IconButton(
            onClick = navigateHome,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 8.dp, top = 8.dp)
        ) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
        }

        if (uiState.isLoading || question == null) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            return@Box
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .padding(top = 30.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "${uiState.answeredCount} / ${uiState.totalCount} ",
                style = MaterialTheme.typography.bodyMedium
            )
            LinearProgressIndicator(
                progress = { uiState.answeredCount.toFloat() / uiState.totalCount },
                modifier = Modifier.fillMaxWidth()
            )
            Text(
                text = "✅ ${uiState.correctCount}   ❌ ${uiState.wrongCount}",
                style = MaterialTheme.typography.bodySmall
            )
            Row{
                uiState.remainingLives?.let { lives ->
                    Row {
                        repeat(lives) {
                            Icon(
                                Icons.Default.Favorite, tint = Color.Red,
                                contentDescription = "hearts",
                                modifier = Modifier
                            )
                        }
                    }
                }
                Spacer(Modifier.width(8.dp))
                Text(
                    text = formatTime(uiState.elapsedTimeMillis),
                    style = MaterialTheme.typography.titleMedium
                )
            }

            val promptUrl = if (roundConfig.gameMode == GameMode.GUESS_COUNTRY) {
                uiState.shapeUrl
            } else {
                question.prompt
            }

            val imageRequest = ImageRequest.Builder(context)
                .data(promptUrl)
                .size(512) // limits decode size to roughly the view bounds
                .crossfade(true)
                .build()

            Surface(
                shape = RoundedCornerShape(20.dp),
                tonalElevation = 2.dp,
                color = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    AsyncImage(
                        model = imageRequest,
                        contentDescription = if (gameMode == GameMode.GUESS_COUNTRY) "Country shape" else "Flag",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                    )
                }
            }

            val optionPalette = listOf(
                Color(0xFF5B8DEF), // blue
                Color(0xFFFF6B6B), // red
                Color(0xFF51CF66), // green
                Color(0xFFFFCC00)  // yellow
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                userScrollEnabled = false //
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
        val bookmarkType = roundConfig.bookmarkType ?: if (roundConfig.gameMode == GameMode.GUESS_COUNTRY) {
            BookmarkType.SHAPE
        } else {
            BookmarkType.FLAG
        }

        val bookmarkLabel = if (bookmarkType == BookmarkType.SHAPE) "Bookmark shape" else "Bookmark flag"
        val showBookmark = roundConfig.source != QuizSource.BOOKMARK
//        if (uiState.showFeedback) {
//            FeedbackBottomSheet(
//                sheetState = sheetState,
//                isCorrect = uiState.isCorrect!!,
//                correctAnswer = question.options[question.correctIndex],
//                bookmarkLabel = bookmarkLabel,
//                showBookmark = showBookmark,
//                isBookmarked = uiState.isBookmarked,
//                onToggleBookmark = { viewModel.toggleBookmark() },
//                onNext = {
//                    viewModel.dismissFeedback()
//                    viewModel.onNextClicked()
//                },
//                onDismiss = {  }
//            )
//        }
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
