package com.agrowise.app.data.api

import com.agrowise.app.data.model.PredictionRequest
import com.agrowise.app.data.model.PredictionResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {
    @POST("api/ml/predict")
    suspend fun startPrediction(@Body request: PredictionRequest): Response<PredictionResponse>

    @GET("api/ml/predictions/{id}")
    suspend fun getPredictionById(@Path("id") id: Long): Response<PredictionResponse>

    @GET("api/ml/predictions/createdAt")
    suspend fun getAllPredictions(): Response<List<PredictionResponse>>

    @DELETE("api/ml/predictions/delete/{id}")
    suspend fun deletePrediction(@Path("id") id: Long): Response<Unit>
}