package com.agrowise.app.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import kotlinx.coroutines.delay

@Composable
fun MainScreen() {
    var isAreaSelected by remember { mutableStateOf(false) }
    var selectedPoints by remember { mutableStateOf<List<LatLng>>(emptyList()) }
    var showLoading by remember { mutableStateOf(false) }
    var showResultPrompt by remember { mutableStateOf(false) }

    val konya = LatLng(37.8741, 32.4932)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(konya, 10f)
    }

    Box(modifier = Modifier.fillMaxSize()) {

        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            uiSettings = MapUiSettings(zoomControlsEnabled = false),
            onMapClick = { latLng ->
                selectedPoints = selectedPoints + latLng
                isAreaSelected = selectedPoints.size > 2
            }
        ) {
            if (isAreaSelected) {
                Polygon(
                    points = selectedPoints,
                    strokeColor = Color(0xFF_006400),
                    fillColor = Color(0x55_3CB371
                ))
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Lütfen Tarlanızın Sınırlarını Seçiniz", style = MaterialTheme.typography.titleMedium, color = Color.Black)
            Spacer(modifier = Modifier.height(8.dp))
            Row {
                Button(
                    onClick = {
                        selectedPoints = emptyList()
                        isAreaSelected = false
                    },
                    enabled = selectedPoints.isNotEmpty()
                ) {
                    Text("Temizle")
                }
            }
        }

        AnimatedVisibility(
            visible = isAreaSelected,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp),
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Button(
                onClick = {
                    showLoading = true
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text("Analiz Et")
            }
        }

        if (showLoading) {
            LaunchedEffect(Unit) {
                delay(3000)
                showLoading = false
                showResultPrompt = true
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color.White)
            }
        }

        if (showResultPrompt) {
            AlertDialog(
                onDismissRequest = { showResultPrompt = false },
                title = { Text("Analiz Sonuçları") },
                text = {
                    Column {
                        Text("Tahmini Verim: 3.2 ton/hektar")
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Toprak Tipi: Killi Tın")
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Öneri: Buğday ekimi için uygun.")
                    }
                },
                confirmButton = {
                    Button(onClick = { showResultPrompt = false }) {
                        Text("Tamam")
                    }
                }
            )
        }
    }
}