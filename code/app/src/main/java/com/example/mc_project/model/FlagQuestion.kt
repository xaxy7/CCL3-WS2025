package com.example.mc_project.model



data class FlagQuestion(
    val countryCode: String,
    val flagUrl: String,
    val options: List<String>,
    val correctIndex: Int
)
