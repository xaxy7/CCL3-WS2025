package com.example.ccl_3.ui.summary


import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun StatItem(
    symbol: String,
    label: String,
    value: Int,
    color: androidx.compose.ui.graphics.Color
){
    Column(horizontalAlignment = Alignment.CenterHorizontally) {

        Box(
            modifier = Modifier.height(28.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(symbol, fontSize = 20.sp)
        }

        Box(
            modifier = Modifier.height(28.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = value.toString(),
                color = color,
                style = MaterialTheme.typography.titleMedium
            )
        }

        Box(
            modifier = Modifier.height(20.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}