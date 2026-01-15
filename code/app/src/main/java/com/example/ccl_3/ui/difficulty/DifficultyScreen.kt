package com.example.ccl_3.ui.difficulty

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.example.ccl_3.model.Difficulty


@Composable
fun DifficultyScreen(
    onSelected: (Difficulty) -> Unit
){
    Column {
        Difficulty.entries.forEach { difficulty ->
            Button(onClick = {onSelected(difficulty)}) {
                Text(difficulty.name.replace("_", " ").lowercase()
                    .replaceFirstChar{it.uppercaseChar()})
            }
        }
    }
}