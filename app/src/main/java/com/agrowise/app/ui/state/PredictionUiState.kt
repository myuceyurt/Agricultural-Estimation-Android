package com.agrowise.app.ui.state

import com.agrowise.app.data.model.PredictionResponse

sealed interface PredictionUiState {

    data object Idle : PredictionUiState
    data object Loading : PredictionUiState
    data class Success(val data: PredictionResponse) : PredictionUiState
    data class Error(val msg: String) : PredictionUiState
}