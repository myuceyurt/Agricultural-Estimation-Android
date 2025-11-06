
package com.agrowise.app.ui.navigation

sealed class Screen(val route: String) {
    object Main : Screen("main")
    object Analyses : Screen("analyses")
    object Profile : Screen("profile")
}
