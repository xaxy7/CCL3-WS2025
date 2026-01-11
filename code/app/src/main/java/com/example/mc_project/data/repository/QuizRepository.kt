package com.example.mc_project.data.repository

import androidx.compose.foundation.layout.Column
import com.example.mc_project.data.api.CountriesApi
import com.example.mc_project.model.Country

class QuizRepository(
    private val api: CountriesApi
) {

    private var cachedCountries: List<Country>? = null

    suspend fun getAllCountries(): List<Country>{
        if(cachedCountries != null){
            return cachedCountries!!
        // double exlamation mark means that even though the value can be null, I'm sure it won't be here
        }
        val countries = api.getAllCountries()
            .filter { it.flags.png.isNotEmpty() }
            .map{
                Country(
                    code = it.cca2,
                    name = it.name.common,
                    flagUrl = it.flags.png,
                    region = it.region
                )
            }
        cachedCountries = countries
        return  countries
    }
}