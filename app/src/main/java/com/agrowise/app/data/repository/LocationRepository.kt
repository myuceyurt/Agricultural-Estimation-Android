package com.agrowise.app.data.repository

import com.agrowise.app.R
import LocationItem
import ProvinceDto
import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocationRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val gson = Gson()

    private val locationsCache: List<LocationItem> by lazy {
        loadAllLocationsFromJson()
    }

    private fun loadAllLocationsFromJson(): List<LocationItem> {
        val json = context.resources.openRawResource(R.raw.turkey_districts)
            .bufferedReader()
            .use { it.readText() }

        val type = object : TypeToken<List<ProvinceDto>>() {}.type
        val provinces: List<ProvinceDto> = gson.fromJson(json, type)

        return provinces.flatMap { province ->
            province.districts.map { district ->
                LocationItem(
                    city = province.name,
                    district = district.name
                )
            }
        }
    }

    fun getAllLocations(): List<LocationItem> = locationsCache
}