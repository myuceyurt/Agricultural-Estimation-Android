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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.navigation.animation.AnimatedNavHost
import androidx.navigation.compose.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.agrowise.app.ui.components.AppBottomBar
import com.agrowise.app.ui.components.BottomNavItem
import com.agrowise.app.ui.screens.AnalyzesScreen
import com.agrowise.app.ui.screens.ProfileScreen
import com.agrowise.app.ui.viewmodel.NavigationUiEvent
import com.agrowise.app.ui.viewmodel.NavigationViewModel

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AppNavigation(
    viewModel: NavigationViewModel = hiltViewModel()
) {
    val navController = rememberAnimatedNavController()
    val uiState by viewModel.uiState.collectAsState()

    val navItems = listOf(
        BottomNavItem("Harita", Icons.Default.LocationOn, Screen.Main.route),
        BottomNavItem("Analizler", Icons.Default.AccountBox, Screen.Analyses.route),
        BottomNavItem("Profil", Icons.Default.Person, Screen.Profile.route)
    )

    fun getIndex(route: String?): Int {
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
                    }
                )
            }
            composable(
                Screen.Analyses.route,
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
                AnalyzesScreen()
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
                        navController.navigate(navItems[index].route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            )
        }
    }
}