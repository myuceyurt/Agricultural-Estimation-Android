package com.agrowise.app.data.model

import com.google.gson.annotations.SerializedName

data class PredictionRequest(
    val lat: Double,
    val lon: Double,
    val hectare: Double
)

data class PredictionResponse(
    val status: String,
    val data: PredictionData?
)

data class PredictionData(
    val lat: Double,
    val lon: Double,

    @SerializedName("yield_per_hektar")
    val yieldPerHectare: Double,

    @SerializedName("total_yield_ton")
    val totalYieldTon: String,

    @SerializedName("soil_included")
    val soilIncluded: Boolean
)
