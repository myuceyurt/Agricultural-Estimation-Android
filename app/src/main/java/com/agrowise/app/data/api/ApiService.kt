package com.agrowise.app.data.api

import com.agrowise.app.data.model.PredictionRequest
import com.agrowise.app.data.model.PredictionResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("api/ml/predict")
    suspend fun getPrediction(@Body request: PredictionRequest): Response<PredictionResponse>
}