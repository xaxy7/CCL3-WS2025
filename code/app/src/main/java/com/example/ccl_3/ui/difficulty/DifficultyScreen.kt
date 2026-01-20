package com.example.ccl_3.ui.difficulty

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.ccl_3.model.Difficulty
import com.example.ccl_3.ui.components.AppTopBar
import com.example.ccl_3.ui.components.NavigationIcon
import com.example.ccl_3.ui.navigation.LocalAppNavigator

@Composable
@Suppress("UNUSED_PARAMETER")
fun DifficultyScreen(
    regionName: String,
    isGlobal: Boolean,
    onDifficultySelected: (Difficulty) -> Unit
) {
    val appNavigator = LocalAppNavigator.current
    val items = Difficulty.entries

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Select Difficulty",
                navigationIcon = NavigationIcon.Back,
                onNavigationClick = { appNavigator.popBackStack() }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 32.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Choose how challenging your round will be",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(16.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(items) { difficulty ->
                    DifficultyCard(
                        difficulty = difficulty,
                        onClick = { onDifficultySelected(difficulty) }
                    )
                }
            }
        }
    }
}
