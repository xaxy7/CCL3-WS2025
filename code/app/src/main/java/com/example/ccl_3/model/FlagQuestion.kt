package com.example.ccl_3.model



data class CountryQuestion(
    val countryCode: String,
    val prompt: String?,
    val options: List<String>,
    val correctIndex: Int
)

data class FlagQuestion(
    val countryCode: String,
    val flagUrl: String,
    val options: List<String>,
    val correctIndex: Int
)
