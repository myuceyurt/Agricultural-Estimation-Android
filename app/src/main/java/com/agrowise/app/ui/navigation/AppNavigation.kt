
package com.agrowise.app.ui.navigation

import MainScreen
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.agrowise.app.ui.screens.AnalysesScreen
import com.agrowise.app.ui.screens.ProfileScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Screen.Main.route) {
        composable(Screen.Main.route) {
            MainScreen()
        }
        composable(Screen.Analyses.route) {
            AnalysesScreen()
        }
        composable(Screen.Profile.route) {
            ProfileScreen()
        }
    }
}
