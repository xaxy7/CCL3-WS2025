package com.example.ccl_3.ui.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.ccl_3.BuildConfig
import com.example.ccl_3.data.db.DatabaseProvider
import com.example.ccl_3.data.repository.RoundResultRepository
import com.example.ccl_3.ui.debug.DebugViewModel
import com.example.ccl_3.ui.debug.DebugViewModelFactory
import com.example.ccl_3.ui.navigation.Routes


@Composable
fun MainScreen(
    onRegionSelected: (String, Boolean) -> Unit,
    navController: NavHostController,
) {
    val context = LocalContext.current

    val repository = remember {
        RoundResultRepository(
            DatabaseProvider.getDatabase(context).roundResultDao()
        )
    }

    val debugViewModel: DebugViewModel = viewModel(
        factory = DebugViewModelFactory(repository)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ){

        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.primaryContainer,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Game Modes",
                modifier = Modifier.padding(vertical = 12.dp),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleLarge
            )
        }

        if (BuildConfig.DEBUG) {
            Button(onClick = {
//                debugViewModel.insertDebugRound()
                navController.navigate(Routes.SUMMARY)
            }) {
                Text("Insert Debug Round & Open Summary")
            }
        }

        Surface(
            shape = RoundedCornerShape(16.dp),
            tonalElevation = 2.dp,
            color = MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                "Latest round Global 102/250",
                modifier= Modifier.padding(16.dp),
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(regions) { region ->
                RegionCard(region = region, isGlobal = region.isGlobal) {
                    onRegionSelected(region.name, region.isGlobal)
                }
            }
        }
    }
}
