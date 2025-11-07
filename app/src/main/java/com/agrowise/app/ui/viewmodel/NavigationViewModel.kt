package com.agrowise.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class NavigationViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(NavigationUiState())
    val uiState: StateFlow<NavigationUiState> = _uiState

    fun onEvent(event: NavigationUiEvent) {
        when (event) {
            is NavigationUiEvent.OnBottomBarVisibilityChanged -> {
                _uiState.value = _uiState.value.copy(isBottomBarVisible = event.isVisible)
            }

            is NavigationUiEvent.OnItemSelected -> {
                _uiState.value = _uiState.value.copy(selectedBottomItemIndex = event.index)
            }
        }
    }
}

sealed class NavigationUiEvent {
    data class OnBottomBarVisibilityChanged(val isVisible: Boolean) : NavigationUiEvent()
    data class OnItemSelected(val index: Int) : NavigationUiEvent()
}

data class NavigationUiState(
    val isBottomBarVisible: Boolean = true,
    val selectedBottomItemIndex: Int = 0
)