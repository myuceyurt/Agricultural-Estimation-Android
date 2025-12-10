package com.agrowise.app.ui.navigation

sealed class Screen(val route: String) {
    object Main : Screen("main")
    object Analyses : Screen("analyses?lat={lat}&lon={lon}&hectare={hectare}&timestamp={timestamp}") {
        fun createRoute(lat: Double, lon: Double, hectare: Double): String {
            return "analyses?lat=$lat&lon=$lon&hectare=$hectare&timestamp=${System.currentTimeMillis()}"
        }
    }
    object Profile : Screen("profile")
    object AnalysisDetail : Screen("analysis_detail/{analysisId}") {
        fun createRoute(analysisId: Int): String = "analysis_detail/$analysisId"
    }
}