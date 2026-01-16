package com.example.ccl_3.ui.difficulty

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.ccl_3.model.Difficulty

@Composable
fun DifficultyCard(
    difficulty: Difficulty,
    onClick: () -> Unit
) {
    val (title, subtitle, color, icon) = when (difficulty) {
        Difficulty.PRACTICE ->
            Quad("Practice", "No pressure", Color(0xFF4CAF50), Icons.Default.School)
        Difficulty.EASY ->
            Quad("Easy", "10 lives", Color(0xFF8BC34A), Icons.Default.Favorite)
        Difficulty.MEDIUM ->
            Quad("Medium", "5 lives", Color(0xFFFFC107), Icons.Default.Whatshot)
        Difficulty.HARD ->
            Quad("Hard", "3 lives", Color(0xFFFF9800), Icons.Default.Warning)
        Difficulty.VERY_HARD ->
            Quad("Very Hard", "1 life", Color(0xFFF44336), Icons.Default.Close)
    }

    Card(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.12f)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {

            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(32.dp)
            )

            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = color
                )

                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
