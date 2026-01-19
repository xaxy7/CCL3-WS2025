package com.example.ccl_3.ui.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.ccl_3.data.db.RoundStateEntity
import com.example.ccl_3.model.RoundConfig
import com.example.ccl_3.ui.quiz.formatTime

@Composable
fun ActiveRoundCard(
    state: RoundStateEntity,
    config: RoundConfig,
    onResume: () -> Unit,
    onEndRound: () -> Unit
) {
    Surface(
        shape = MaterialTheme.shapes.large,
        tonalElevation = 3.dp,
        color = MaterialTheme.colorScheme.secondaryContainer,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Unfinished round", style = MaterialTheme.typography.labelLarge)
                    Text(
                        text = buildString {
                            append(config.displayName())
                            append(" · ")
                            append(config.gameMode.name.replace('_', ' ').lowercase().replaceFirstChar { it.uppercase() })
                            append(" · ")
                            append(config.difficulty.name.lowercase().replaceFirstChar { it.uppercase() })
                        },
                        style = MaterialTheme.typography.titleMedium
                    )
                }
                Icon(imageVector = Icons.Default.PlayArrow, contentDescription = "Resume", tint = MaterialTheme.colorScheme.primary)
            }

            val answered = state.usedCountryCodes.size
            val progress = if (state.totalCount == 0) 0f else answered.toFloat() / state.totalCount
            LinearProgressIndicator(
                progress = progress,
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
                    text = "Progress ${answered}/${state.totalCount}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
                Text(
                    text = formatTime(state.elapsedTimeMillis),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = onResume) {
                    Text("Resume")
                }
                OutlinedButton(onClick = onEndRound) {
                    Text("End round")
                }
            }
        }
    }
}
