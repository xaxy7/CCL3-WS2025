package com.example.ccl_3.data.local

import android.content.Context
import android.util.Log
import com.example.ccl_3.model.Country
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


fun loadCountriesFromAssets(context: Context): List<Country>{
    Log.d("ASSET_DEBUG", context.assets.list("countries")?.toList().toString())
    val json = context.assets.open("countries/countries.json")
        .bufferedReader()
        .use { it.readText() }

    val type = object : TypeToken<List<CountryAsset>>(){}.type
    val assets: List<CountryAsset> = Gson().fromJson(json,type)

    return assets.map{



        Country(
            code = it.code,
            name =  it.name,
            region = it.region,
            flagUrl = "file:///android_asset/flags/${it.code.lowercase()}.png"
            )

    }
}