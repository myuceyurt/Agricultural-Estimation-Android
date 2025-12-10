package com.agrowise.app.ui.navigation

import MainScreen
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import com.google.accompanist.navigation.animation.AnimatedNavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.agrowise.app.R
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.agrowise.app.ui.components.AppBottomBar
import com.agrowise.app.ui.components.BottomNavItem
import com.agrowise.app.ui.screens.AnalyzesScreen
import com.agrowise.app.ui.screens.AnalysisDetailScreen
import com.agrowise.app.ui.screens.ProfileScreen
import com.agrowise.app.ui.viewmodel.AnalyzesViewModel
import com.agrowise.app.ui.viewmodel.NavigationUiEvent
import com.agrowise.app.ui.viewmodel.NavigationViewModel

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AppNavigation(
    viewModel: NavigationViewModel = hiltViewModel()
) {
    val navController = rememberAnimatedNavController()
    val uiState by viewModel.uiState.collectAsState()

    val analyzesViewModel: AnalyzesViewModel = hiltViewModel()

    val navItems = listOf(
        BottomNavItem("Harita", painterResource(R.drawable.map_tab_icon), Screen.Main.route),
        BottomNavItem("Analizler", painterResource(R.drawable.analysis_tab_icon), Screen.Analyses.route),
        BottomNavItem("Profil", painterResource(R.drawable.profile_tab_icon), Screen.Profile.route)
    )

    fun getIndex(route: String?): Int {
        if (route?.startsWith("analysis_detail") == true) return 1
        return navItems.indexOfFirst { it.route == route }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AnimatedNavHost(navController = navController, startDestination = Screen.Main.route) {
            composable(
                Screen.Main.route,
                enterTransition = {
                    val initialIndex = getIndex(initialState.destination.route)
                    val targetIndex = getIndex(targetState.destination.route)
                    slideInHorizontally(
                        initialOffsetX = { if (targetIndex > initialIndex) it else -it },
                        animationSpec = tween(300)
                    )
                },
                exitTransition = {
                    val initialIndex = getIndex(initialState.destination.route)
                    val targetIndex = getIndex(targetState.destination.route)
                    slideOutHorizontally(
                        targetOffsetX = { if (targetIndex > initialIndex) -it else it },
                        animationSpec = tween(300)
                    )
                }
            ) {
                MainScreen(
                    onAreaSelectionModeChange = {
                        viewModel.onEvent(NavigationUiEvent.OnBottomBarVisibilityChanged(!it))
                    },
                    onAnalyzeClick = { lat, lon, hectare ->
                        viewModel.onEvent(NavigationUiEvent.OnItemSelected(1))

                        navController.navigate(Screen.Analyses.createRoute(lat, lon, hectare)) {
                            popUpTo(Screen.Main.route) {
                                saveState = true
                            }
                            launchSingleTop = true
                        }
                    }
                )
            }
            composable(
                Screen.Analyses.route,
                arguments = listOf(
                    navArgument("lat") { type = NavType.FloatType; defaultValue = 0f },
                    navArgument("lon") { type = NavType.FloatType; defaultValue = 0f },
                    navArgument("hectare") { type = NavType.FloatType; defaultValue = 0f }
                ),
                enterTransition = {
                    val initialIndex = getIndex(initialState.destination.route)
                    val targetIndex = getIndex(targetState.destination.route)
                    slideInHorizontally(
                        initialOffsetX = { if (targetIndex > initialIndex) it else -it },
                        animationSpec = tween(300)
                    )
                },
                exitTransition = {
                    val initialIndex = getIndex(initialState.destination.route)
                    val targetIndex = getIndex(targetState.destination.route)
                    slideOutHorizontally(
                        targetOffsetX = { if (targetIndex > initialIndex) -it else it },
                        animationSpec = tween(300)
                    )
                }
            ) { backStackEntry ->
                val lat = backStackEntry.arguments?.getFloat("lat")?.toDouble() ?: 0.0
                val lon = backStackEntry.arguments?.getFloat("lon")?.toDouble() ?: 0.0
                val hectare = backStackEntry.arguments?.getFloat("hectare")?.toDouble() ?: 0.0

                AnalyzesScreen(
                    viewModel = analyzesViewModel,
                    navigateToMap = {
                        viewModel.onEvent(NavigationUiEvent.OnItemSelected(0))
                        navController.navigate(Screen.Main.route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    startAnalysisParams = if (lat != 0.0) Triple(lat, lon, hectare) else null,
                    onAnalysisClick = { analysisId ->
                        navController.navigate(Screen.AnalysisDetail.createRoute(analysisId))
                    }
                )
            }
            composable(
                Screen.Profile.route,
                enterTransition = {
                    val initialIndex = getIndex(initialState.destination.route)
                    val targetIndex = getIndex(targetState.destination.route)
                    slideInHorizontally(
                        initialOffsetX = { if (targetIndex > initialIndex) it else -it },
                        animationSpec = tween(300)
                    )
                },
                exitTransition = {
                    val initialIndex = getIndex(initialState.destination.route)
                    val targetIndex = getIndex(targetState.destination.route)
                    slideOutHorizontally(
                        targetOffsetX = { if (targetIndex > initialIndex) -it else it },
                        animationSpec = tween(300)
                    )
                }
            ) {
                ProfileScreen()
            }

            composable(
                route = Screen.AnalysisDetail.route,
                arguments = listOf(navArgument("analysisId") { type = NavType.IntType }),
                enterTransition = {
                    slideInHorizontally(
                        initialOffsetX = { it },
                        animationSpec = tween(300)
                    )
                },
                exitTransition = {
                    slideOutHorizontally(
                        targetOffsetX = { it },
                        animationSpec = tween(300)
                    )
                }
            ) { backStackEntry ->
                LaunchedEffect(Unit) {
                    viewModel.onEvent(NavigationUiEvent.OnBottomBarVisibilityChanged(false))
                }

                val analysisId = backStackEntry.arguments?.getInt("analysisId") ?: 0
                AnalysisDetailScreen(
                    analysisId = analysisId,
                    onBackClick = {
                        viewModel.onEvent(NavigationUiEvent.OnBottomBarVisibilityChanged(true))
                        navController.popBackStack()
                    }
                )
            }
        }

        AnimatedVisibility(
            visible = uiState.isBottomBarVisible,
            enter = slideInVertically(
                initialOffsetY = { fullHeight -> fullHeight },
                animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing)
            ),
            exit = slideOutVertically(
                targetOffsetY = { fullHeight -> fullHeight },
                animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing)
            ),
            modifier = Modifier
                .align(Alignment.BottomCenter)
        ) {
            AppBottomBar(
                modifier = Modifier
                    .windowInsetsPadding(WindowInsets.safeDrawing),
                items = navItems,
                selectedItemIndex = uiState.selectedBottomItemIndex,
                onItemSelected = { index ->
                    viewModel.onEvent(NavigationUiEvent.OnItemSelected(index))
                    if (navController.currentDestination?.route != navItems[index].route) {
                        if(navController.currentDestination?.route == Screen.Analyses.route){
                            navController.navigate(Screen.Analyses.route){
                                popUpTo(navController.graph.startDestinationId){
                                    saveState = false
                                }
                            }
                        }
                        navController.navigate(navItems[index].route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                        }
                    }
                }
            )
        }
    }
}