package com.agrowise.app.data.model

data class Analysis(
    val id: Int,
    val name: String,
    val area: String,
    val status: String,
    val score: Double,
    val color: Long,
    val lat: Double = 0.0,
    val lon: Double = 0.0,
    val hectare: Double = 0.0,
    val totalYieldTon: String = "",
    val soilIncluded: Boolean = false
)
