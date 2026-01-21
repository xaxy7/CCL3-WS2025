package com.example.ccl_3.ui.main

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.ccl_3.R
import com.example.ccl_3.model.Region
import com.example.ccl_3.ui.region.regionToImage
import com.example.ccl_3.ui.theme.AppColors


@Composable
fun RegionCard(
    region: Region,
    isGlobal: Boolean,
    onClick: () -> Unit
){
    val imageRes = if(isGlobal){
        R.drawable.global_silhouette
    }else{
        regionToImage(region.name)
    }


    Surface(
        shape = RoundedCornerShape(20.dp),
        tonalElevation = 4.dp,
        color = AppColors.Secondary,
        border = BorderStroke(1.dp, AppColors.Stroke),
        modifier = Modifier
            .fillMaxWidth()
            .clickable{onClick()}
    ,

    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ){
            Column {
                Text(region.name , style = MaterialTheme.typography.titleMedium, color = AppColors.TextWhite)
                Spacer(modifier = Modifier.height(6.dp))
                Text("Tap to choose region", style = MaterialTheme.typography.bodySmall, color = AppColors.TextWhite )
            }
            Image(
                painter = painterResource(imageRes),
                contentDescription = region.name,
                modifier = Modifier.size(64.dp)
            )
        }
    }
}