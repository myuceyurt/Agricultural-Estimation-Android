package com.agrowise.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.agrowise.app.data.model.Analysis
import com.agrowise.app.data.repository.PredictionRepository
import com.agrowise.app.ui.state.PredictionUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class AnalyzesViewModel @Inject constructor(
    private val repository: PredictionRepository,
) : ViewModel() {

    private val _analyzes = MutableStateFlow<List<Analysis>>(emptyList())
    val analyzes: StateFlow<List<Analysis>> = _analyzes

    private val _predictionState = MutableStateFlow<PredictionUiState>(PredictionUiState.Idle)
    val predictionState: StateFlow<PredictionUiState> = _predictionState

    fun createAnalysis(lat: Double, lon: Double, hectare: Double) {
        viewModelScope.launch {
            _predictionState.value = PredictionUiState.Loading
            try {
                val response = repository.getPrediction(lat, lon, hectare)
                if (response.isSuccessful && response.body() != null) {
                    val apiData = response.body()!!.data!!
                    val newAnalysis = Analysis(
                        id = Random.nextInt(),
                        name = "Analiz #${_analyzes.value.size + 1}",
                        area = "$hectare ha",
                        status = "Tamamlandı",
                        score = apiData.yieldPerHectare,
                        color = if (apiData.soilIncluded) 0xFF4CAF50 else 0xFFFFB74D
                    )

                    val currentList = _analyzes.value.toMutableList()
                    currentList.add(0, newAnalysis)
                    _analyzes.value = currentList

                    resetState()
                }
                else {
                    _predictionState.value = PredictionUiState.Error("Sunucu Hatası: ${response.code()}")
                }
            }
            catch (e: Exception){
                _predictionState.value = PredictionUiState.Error("Hata: ${e.message}")
            }
        }
    }

    fun resetState() {
        _predictionState.value = PredictionUiState.Idle
    }

    fun deleteAnalysis(analysis: Analysis) {
        _analyzes.value = _analyzes.value.filter { it.id != analysis.id }
    }
}