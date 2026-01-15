package com.example.ccl_3.ui.summary

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.ccl_3.model.RoundResult

@Composable
fun SummaryCountryRow(
    index: Int,
    code: String,
    result: RoundResult,
    viewModel: SummaryViewModel
) {
    val imageUrl = viewModel.getImageForCountry(code, result)
    val name = viewModel.getCountryName(code)
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("${index + 1}. $name")

        Spacer(Modifier.width(12.dp))

        AsyncImage(
            model = imageUrl ?: "",
            contentDescription = code,
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(6.dp))
        )

    }
}


