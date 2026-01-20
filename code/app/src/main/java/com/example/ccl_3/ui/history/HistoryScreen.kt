package com.example.ccl_3.ui.history

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.ccl_3.data.db.DatabaseProvider
import com.example.ccl_3.data.repository.RoundResultRepository
import com.example.ccl_3.model.AnswerResult
import com.example.ccl_3.model.GameMode
import com.example.ccl_3.model.RoundResult
import com.example.ccl_3.ui.components.AppTopBar
import com.example.ccl_3.ui.components.NavigationIcon
import com.example.ccl_3.ui.navigation.LocalAppNavigator
import com.example.ccl_3.ui.quiz.formatTime

@Composable
fun HistoryScreen() {
    val appNavigator = LocalAppNavigator.current
    val context = LocalContext.current
    val repository = remember {
        RoundResultRepository(DatabaseProvider.getDatabase(context).roundResultDao())
    }


    val viewModel: HistoryViewModel = viewModel(factory = HistoryViewModelFactory(repository))
    val history by viewModel.history.collectAsStateWithLifecycle()

    var showConfirmClear by remember { mutableStateOf(false) }

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.refresh()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
    Scaffold(
        topBar = {
            AppTopBar(
                title = "History",
                navigationIcon = NavigationIcon.Back,
                onNavigationClick = { appNavigator.popBackStack() },
                actions = {
                    if (history.isNotEmpty()) {
                        IconButton(onClick = { showConfirmClear = true }) {
                            Icon(Icons.Default.DeleteSweep, contentDescription = "Clear history")
                        }
                    }
                }
            )
        }
    ) { padding ->
        if (history.isEmpty()) {
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "No rounds yet",
                    style = MaterialTheme.typography.titleLarge
                )
                Text(
                    text = "Play a round to see your history here.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize(),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(history, key = { it.id }) { result ->
                    HistoryCard(
                        result = result,
                        onDelete = { viewModel.deleteResult(result.id) }
                    )
                }
            }
        }
        if (showConfirmClear) {
            AlertDialog(
                onDismissRequest = { showConfirmClear = false },
                title = { Text("Confirm clear") },
                text = { Text("Are you sure you want to clear the history?") },
                confirmButton = {
                    TextButton(onClick = {
                        viewModel.clearAll()
                        showConfirmClear = false
                    }) {
                        Text("Yes")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showConfirmClear = false }) {
                        Text("No")
                    }
                }
            )
        }
    }
}

@Composable
private fun HistoryCard(
    result: RoundResult,
    onDelete: () -> Unit
) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    var showConfirmDelete by remember { mutableStateOf(false) }

    val accuracy = if (result.totalGuesses > 0) result.correctCount.toFloat() / result.totalGuesses else 0f

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
//        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        if(result.completed) CardDefaults.cardColors(Color(0xFFE6F4EA)) else CardDefaults.cardColors(Color(
            0xFFFAA7A7
        )
        )
//        if(result.completed) CardDefaults.cardColors(Color(0xFF1B5E20)) else CardDefaults.cardColors(Color(0xFFB71C1C))
    ) {
        Column(
            modifier = Modifier
                .clickable { expanded = !expanded }
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = headerText(result),
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = subHeaderText(result),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    IconButton(onClick = { showConfirmDelete = true }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete entry")
                    }
                    IconButton(onClick = { expanded = !expanded }) {
                        Icon(
                            imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = if (expanded) "Collapse" else "Expand"
                        )
                    }
                }
            }

            LinearProgressIndicator(
                progress = { accuracy },
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
            )
            Text(
                text = "Accuracy ${(accuracy * 100).toInt()}%",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (expanded) {
                Spacer(modifier = Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    StatChip(label = "Correct", value = result.correctCount)
                    StatChip(label = "Wrong", value = result.wrongCount)
                    StatChip(label = "Total Guesses", value = result.totalGuesses)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Time: ${result.timeTakenMillis?.let { formatTime(it) } ?: "--:--"}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                result.livesLeft?.let {
                    Text(
                        text = "Lives left: $it",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text("Round summary", style = MaterialTheme.typography.labelLarge)
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    val pairs = result.countryCodes.zip(result.answers)
                    pairs.forEachIndexed { index, (code, answer) ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                // Generate image URL directly based on game mode
                                val imageUrl = when (result.gameMode) {
                                    GameMode.GUESS_COUNTRY -> "file:///android_asset/all/${code.lowercase()}/256.png"
                                    else -> "https://flagcdn.com/w320/${code.lowercase()}.png"
                                }

                                AsyncImage(
                                    model = imageUrl,
                                    contentDescription = code,
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(RoundedCornerShape(6.dp)),
                                    contentScale = ContentScale.Crop
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("${index + 1}. $code", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(start = 8.dp))
                            }

                            Text(
                                if (answer == AnswerResult.CORRECT) "✓" else "✗",
                                color = if (answer == AnswerResult.CORRECT) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
            if (showConfirmDelete) {
                AlertDialog(
                    onDismissRequest = { showConfirmDelete = false },
                    title = { Text("Confirm delete") },
                    text = { Text("Are you sure you want to delete this entry?") },
                    confirmButton = {
                        TextButton(onClick = {
                            onDelete()
                            showConfirmDelete = false
                        }) {
                            Text("Yes")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showConfirmDelete = false }) {
                            Text("No")
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun StatChip(label: String, value: Int) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(label, style = MaterialTheme.typography.labelMedium)
            Text(value.toString(), style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
        }
    }
}

private fun headerText(result: RoundResult): String {
    val regionLabel = if (result.isGlobal) "Global" else result.region ?: "Unknown"
    return "#${result.id} · $regionLabel"
}

private fun subHeaderText(result: RoundResult): String {
    val modeLabel = when (result.gameMode) {
        GameMode.GUESS_FLAG -> "Flag Guessing"
        GameMode.GUESS_COUNTRY -> "Country Guessing"
    }
    val typeLabel = result.roundType.name.lowercase().replaceFirstChar { it.uppercase() }
    return "$modeLabel · $typeLabel"
}
