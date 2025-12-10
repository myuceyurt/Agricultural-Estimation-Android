package com.agrowise.app.data.repository

import com.agrowise.app.data.api.ApiService
import com.agrowise.app.data.model.PredictionRequest
import com.agrowise.app.data.model.PredictionResponse
import retrofit2.Response
import javax.inject.Inject

class PredictionRepository @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun getPrediction(lat: Double, lon: Double, hectar: Double): Response<PredictionResponse> {
        val request = PredictionRequest(lat, lon, hectar)
        return apiService.getPrediction(request)
    }

    suspend fun getAllPredictions(): Response<List<PredictionResponse>> {
        return apiService.getAllPredictions()
    }

    suspend fun deletePrediction(id: Long): Response<Unit> {
        return apiService.deletePrediction(id)
    }
}