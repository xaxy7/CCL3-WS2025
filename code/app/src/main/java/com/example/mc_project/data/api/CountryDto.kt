package com.example.mc_project.data.api

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CountryDto(
    val cca2: String,
    val name: NameDto,
    val flags: FlagsDto,
    val region: String?,
)
@JsonClass(generateAdapter = true)
data class NameDto(
    val common: String
)
@JsonClass(generateAdapter = true)
data class FlagsDto(
    val png: String,
    val svg: String
)