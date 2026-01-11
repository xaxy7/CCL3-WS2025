package com.example.mc_project.data.api


import retrofit2.http.GET

interface CountriesApi {

    @GET("v3.1/all?fields=cca2,name,flags,region")
    suspend fun getAllCountries(): List<CountryDto>

}