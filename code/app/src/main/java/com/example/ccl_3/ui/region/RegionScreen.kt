package com.example.ccl_3.ui.region

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.ccl_3.R
import com.example.ccl_3.model.GameMode
import com.example.ccl_3.ui.components.AppTopBar
import com.example.ccl_3.ui.components.NavigationIcon
import com.example.ccl_3.ui.navigation.LocalAppNavigator
import com.example.ccl_3.ui.theme.AppColors

@Composable
fun RegionScreen(
    regionName: String,
    isGlobal: Boolean = false,
    onModeSelected: (GameMode) -> Unit
){
    val appNavigator = LocalAppNavigator.current

    Scaffold(
        topBar = {
            AppTopBar(
                title = regionName,
                navigationIcon = NavigationIcon.Back,
                onNavigationClick = { appNavigator.popBackStack() }
            )
        },
        containerColor = AppColors.Primary

    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            contentPadding = PaddingValues(bottom = 24.dp)
        ){
//            Box(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .height(200.dp),
//                contentAlignment = Alignment.Center
//            ) {
//                val regionImageRes = if(isGlobal)
//                    R.drawable.global_silhouette
//                else
//                    regionToImage(regionName)
//                Image(
//                    contentDescription = "$regionName Map",
//                    painter = painterResource(id = regionImageRes),
//                    modifier = Modifier.fillMaxWidth()
//                )
//            }
            item {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    tonalElevation = 2.dp,
                    color = AppColors.NavBg,
                    modifier = Modifier.fillMaxWidth().height(200.dp),
//                border = BorderStroke(1.dp, color = AppColors.Stroke)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
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
                }
            }
            item {
                ModeCard(
                    title = "Guess the flag",
                    subtitle = "Choose country by flag",
                    imageUrl = "file:///android_asset/all/it/it.png",
                    onClick = {onModeSelected(GameMode.GUESS_FLAG)},

                )
            }
            item {
                ModeCard(
                    title = "Guess the country",
                    subtitle = "Choose flag by country",
                    imageUrl = "file:///android_asset/all/it/256.png",
                    onClick = {onModeSelected(GameMode.GUESS_COUNTRY)}
                )
            }

        }
    }
}