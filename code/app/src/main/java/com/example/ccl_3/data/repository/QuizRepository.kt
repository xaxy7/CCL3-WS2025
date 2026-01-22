package com.example.ccl_3.data.repository

import android.util.Log
import com.example.ccl_3.data.api.CountriesApi
import com.example.ccl_3.model.Country
import com.example.ccl_3.model.RoundConfig
import com.example.ccl_3.model.RoundMode

class QuizRepository(
    private val api: CountriesApi
) {


        var cachedCountries: List<Country>? = null




    suspend fun getAllCountries(): List<Country> {

        // 1. Return cache if available
        cachedCountries?.let {
            return it
        }

        return try {
            val countries = api.getAllCountries()
                .filter { it.flags.png.isNotEmpty() }
                .map {
                    Country(
                        code = it.cca2,
                        name = it.name.common,
                        flagUrl = it.flags.png,
                        region = it.region
                    )
                }

            cachedCountries = countries
            Log.d("CACHE_DEBUG", "QuizRepository cachedCountries set size = ${countries.size}")

            countries
        } catch (e: Exception) {
            Log.e("CACHE_DEBUG", "Failed to load countries", e)

            // 2. Fallback to cache if exists
            cachedCountries ?: emptyList()
        }
    }
    fun filterByConfig(
        countries: List<Country>,
        config: RoundConfig
    ): List <Country>{
        return when (config.mode){
            RoundMode.GLOBAL -> countries
            RoundMode.REGION -> countries.filter { it.region == config.parameter }
            RoundMode.BOOKMARKS -> countries // Bookmarks mode doesn't filter by config
        }
    }
    suspend fun ensureCountriesLoaded(): List<Country> {
        return if (cachedCountries.isNullOrEmpty()) {
            getAllCountries()
        } else {
            cachedCountries!!
        }
    }

//    fun getCachedCountries(): List<Country>{
//        return cachedCountries ?: emptyList()
//    }
}