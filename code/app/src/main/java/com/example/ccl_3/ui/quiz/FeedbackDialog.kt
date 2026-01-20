package com.example.ccl_3.ui.quiz

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

@Composable
fun FeedbackDialog(
    isCorrect: Boolean,
    correctAnswer: String,
    bookmarkLabel: String,
    showBookmark: Boolean,
    isBookmarked: Boolean,
    onToggleBookmark: () -> Unit,
    onNext: () -> Unit
) {
    Dialog(onDismissRequest = {}, properties = DialogProperties(usePlatformDefaultWidth = false)) {

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.BottomCenter
        ) {

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                color = if (isCorrect) Color(0xFFE6F4EA) else Color(0xFFFDECEA)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Text(
                        text = if (isCorrect) "Correct!" else "Incorrect :(",
                        style = MaterialTheme.typography.headlineMedium,
                        color = if (isCorrect) Color(0xFF1B5E20)else Color(0xFFB71C1C)
                    )

                    if (!isCorrect) {
                        Text("Correct answer: $correctAnswer")

                    }

                    if (showBookmark) {
                        Button(
                            onClick = onToggleBookmark,
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isBookmarked) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
                                contentColor = if (isBookmarked) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.primary
                            )
                        ) {
                            val icon = if (isBookmarked) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder
                            Icon(imageVector = icon, contentDescription = bookmarkLabel)
                            Text(
                                text = if (isBookmarked) "Added to Learning Notebook!" else bookmarkLabel,
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                    }
                    Button(
                        onClick = onNext,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Next")
                    }
                }
            }
        }
    }
}
