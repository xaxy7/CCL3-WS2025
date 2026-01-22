package com.example.ccl_3.ui.components

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fitInside
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.ccl_3.ui.theme.AppColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    title: String,
    navigationIcon: NavigationIcon = NavigationIcon.None,
    onNavigationClick: () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {}
) {
    CenterAlignedTopAppBar(
        title = { Text(title, color = AppColors.TextWhite) },
        navigationIcon = {
            when (navigationIcon) {
                NavigationIcon.Home -> {
                    IconButton(onClick = onNavigationClick) {
                        Icon(Icons.Default.Home, contentDescription = "Home", tint = AppColors.TextWhite)
                    }
                }
                NavigationIcon.Back -> {
                    IconButton(onClick = onNavigationClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = AppColors.TextWhite)
                    }
                }
                NavigationIcon.None -> {}
            }
        },
        actions = actions,
//        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
//            containerColor = MaterialTheme.colorScheme.primaryContainer
//        )
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = AppColors.Secondary
        ),
        modifier = Modifier
            .height(70.dp)


    )
}

enum class NavigationIcon {
    Home,
    Back,
    None
}
