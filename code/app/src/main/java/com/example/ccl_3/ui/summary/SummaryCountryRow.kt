package com.example.ccl_3.ui.summary

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.ccl_3.model.AnswerResult
import com.example.ccl_3.model.RoundResult

@Composable
fun SummaryCountryRow(
    index: Int,
    code: String,
    answer: AnswerResult,
    result: RoundResult,
    viewModel: SummaryViewModel
) {
    val imageUrl = viewModel.getImageForCountry(code, result)
    val name = viewModel.getCountryName(code)

    val isCorrect = answer == AnswerResult.CORRECT

    val color = if (isCorrect) Color(0xFF4CAF50) else Color(0xFFF44336)
    val symbol = if (isCorrect) "✔" else "✖"

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {

        AsyncImage(
            model = imageUrl ?: "",
            contentDescription = name,
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(6.dp))
        )

        Spacer(Modifier.width(12.dp))

        Text(
            text = "$symbol $name",
            color = color,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

