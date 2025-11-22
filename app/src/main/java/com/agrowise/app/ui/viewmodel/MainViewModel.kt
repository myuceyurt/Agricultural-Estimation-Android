package com.agrowise.app.ui.viewmodel

import LocationItem
import android.location.Geocoder
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.agrowise.app.data.repository.LocationRepository
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val fusedLocationClient: FusedLocationProviderClient,
    private val geocoder: Geocoder,
    private val locationRepository: LocationRepository
) : ViewModel() {

    private val defaultLocation = LatLng(41.025771, 28.889349) // YTU Ortabahçe

    var initialLocation by mutableStateOf<LatLng?>(null)
        private set

    var currentLocation by mutableStateOf("")
        private set

    var allLocations by mutableStateOf<List<LocationItem>>(emptyList())
        private set

    init {
        loadLocations()
    }

    private fun loadLocations() {
        viewModelScope.launch(Dispatchers.IO) {
            val list = locationRepository.getAllLocations()
            withContext(Dispatchers.Main) {
                allLocations = list
                Log.d("MainViewModel", "Loaded locations: ${list.size}")
            }
        }
    }

    fun onPermissionGranted() {
        try {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location ->
                    initialLocation = if (location != null) {
                        LatLng(location.latitude, location.longitude)
                    } else {
                        defaultLocation
                    }
                }
                .addOnFailureListener {
                    initialLocation = defaultLocation
                }
        } catch (e: SecurityException) {
            initialLocation = defaultLocation
        }
    }

    fun onPermissionDenied() {
        initialLocation = defaultLocation
    }

    fun getAddressFromLocation(latLng: LatLng) {
        viewModelScope.launch {
            try {
                val addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
                if (addresses?.isNotEmpty() == true) {
                    val address = addresses[0]
                    currentLocation = "${address.adminArea}, ${address.countryName}"
                }
            } catch (e: Exception) {
                Log.e("MainViewModel", "Error getting location: ${e.message}")
            }
        }
    }

    fun searchLocation(searchText: String) {
        if (searchText.isNotBlank()) {
            viewModelScope.launch {
                try {
                    val addresses = geocoder.getFromLocationName(searchText, 1)
                    if (addresses?.isNotEmpty() == true) {
                        val location = addresses[0]
                        initialLocation = LatLng(location.latitude, location.longitude)
                    }
                } catch (e: Exception) {
                    Log.e("MainViewModel", "Error searching location: ${e.message}")
                }
            }
        }
    }

    fun onLocationSuggestionSelected(item: LocationItem) {
        val searchText = "${item.district} ${item.city}"
        searchLocation(searchText)
    }
}
