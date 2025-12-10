package com.agrowise.app.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.agrowise.app.data.model.Analysis
import com.agrowise.app.data.model.PredictionResponse
import com.agrowise.app.data.repository.PredictionRepository
import com.agrowise.app.ui.state.PredictionUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

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
                val response = repository.startPrediction(lat, lon, hectare)

                if (response.isSuccessful && response.body() != null) {
                    val apiResponse = response.body()!!

                    if (apiResponse.status == "success" && apiResponse.data != null) {
                        val newAnalysis = mapToAnalysis(apiResponse)

                        val currentList = _analyzes.value.toMutableList()
                        currentList.add(0, newAnalysis)
                        _analyzes.value = currentList

                        resetState()
                    } else {
                        _predictionState.value = PredictionUiState.Error("AI Hatası")
                    }
                } else {
                    _predictionState.value = PredictionUiState.Error("Sunucu Hatası: ${response.code()}")
                }
            } catch (e: Exception) {
                _predictionState.value = PredictionUiState.Error("Hata: ${e.message}")
            }
        }
    }

    fun fetchAnalyzesById(id: Long) {
        viewModelScope.launch {
            try {
                val response = repository.getPredictionById(id)
                if (response.isSuccessful && response.body() != null) {
                    val apiResponse = response.body()!!
                    val analysis = mapToAnalysis(apiResponse)

                    val currentList = _analyzes.value.toMutableList()
                    currentList.add(0, analysis)
                    _analyzes.value = currentList
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun fetchAllAnalyzes() {
        viewModelScope.launch {
            try {
                val response = repository.getAllPredictions()
                if (response.isSuccessful && response.body() != null) {
                    val mappedList = response.body()!!.map { apiResponse ->
                        mapToAnalysis(apiResponse)
                    }
                    _analyzes.value = mappedList
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun resetState() {
        _predictionState.value = PredictionUiState.Idle
    }

    fun deleteAnalysis(analysis: Analysis) {
        viewModelScope.launch {
            try {
                val response = repository.deletePrediction(analysis.id.toLong())

                if (response.isSuccessful) {
                    _analyzes.value = _analyzes.value.filter { it.id != analysis.id }
                } else {
                    Log.e("AnalyzesViewModel", "Error deleting analysis: ${response.code()}")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun mapToAnalysis(response: PredictionResponse): Analysis {
        val data = response.data!!
        return Analysis(
            id = data.id.toInt(),
            name = "Analiz #${data.id}",
            area = "${data.hectare} ha",
            status = "Tamamlandı",
            score = data.yieldPerHectare,
            color = if (data.soilIncluded) 0xFF4CAF50 else 0xFFFFB74D,
            lat = data.lat,
            lon = data.lon,
            hectare = data.hectare,
            totalYieldTon = data.totalYieldTon,
            soilIncluded = data.soilIncluded
        )
    }
}