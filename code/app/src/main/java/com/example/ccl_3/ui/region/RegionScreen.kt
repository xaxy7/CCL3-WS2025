package com.example.ccl_3.ui.region

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.ccl_3.R
import com.example.ccl_3.model.GameMode
import com.example.ccl_3.ui.navigation.LocalAppNavigator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegionScreen(
    regionName: String,
    isGlobal: Boolean = false,
    onModeSelected: (GameMode) -> Unit,
    onBack: () -> Unit = {}
){
    val appNavigator = LocalAppNavigator.current

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(regionName) },
                navigationIcon = {
                    IconButton(onClick = { appNavigator.navigateToMain() }) {
                        Icon(Icons.Default.Home, contentDescription = "Home")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ){
//            Surface(
//                shape = RoundedCornerShape(24.dp),
//                color = MaterialTheme.colorScheme.primaryContainer,
//                modifier = Modifier.fillMaxWidth()
//            ) {
//                Text(
//                    text = regionName,
//                    modifier = Modifier.padding(vertical = 12.dp),
//                    textAlign = TextAlign.Center,
//                    style = MaterialTheme.typography.titleLarge
//                )
//            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentAlignment = Alignment.Center
            ) {
//            Text("\uD83D\uDDFA $regionName Map")
                val regionImageRes = if(isGlobal)
                    R.drawable.global_silhouette
                else
                    regionToImage(regionName)
                Image(
                    contentDescription = "$regionName Map",
                    painter = painterResource(id = regionImageRes),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            ModeCard(
                title = "Guess the flag",
                subtitle = "Choose country by flag",
                onClick = {onModeSelected(GameMode.GUESS_FLAG)}
            )
            ModeCard(
                title = "Guess the country",
                subtitle = "Choose flag by country",
                onClick = {onModeSelected(GameMode.GUESS_COUNTRY)}
            )
        }
    }
}