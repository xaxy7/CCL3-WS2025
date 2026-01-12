package com.example.ccl_3.ui.region

import com.example.ccl_3.R

fun regionToImage(region: String): Int{
    return when (region.lowercase()){
        "europe" -> R.drawable.europe_silhouette
        "asia" -> R.drawable.asia_silhouette
        "africa" -> R.drawable.africa_silhouette
        "americas" -> R.drawable.americas_silhouette
        "oceania" -> R.drawable.australia_silhouette
        else -> R.drawable.ic_launcher_background
    }
}