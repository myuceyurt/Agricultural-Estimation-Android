import android.location.Geocoder
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.agrowise.app.permissions.LocationPermissionHandler
import com.agrowise.app.ui.components.AppBottomBar
import com.agrowise.app.ui.components.BottomNavItem
import com.agrowise.app.ui.components.LocationSearchBar
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.launch
import java.util.Locale

@Composable
fun MainScreen() {
    var query by remember { mutableStateOf("") }
    var currentLocation by remember { mutableStateOf("") }
    var selectedBottomItemIndex by remember { mutableIntStateOf(0) }
    var userLocation by remember { mutableStateOf<LatLng?>(null) }
    val defaultLocation = remember { LatLng(41.0082, 28.9784) } // İstanbul

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(defaultLocation, 18f)
    }

    val context = LocalContext.current
    val geocoder = remember { Geocoder(context, Locale.getDefault()) }
    val focusManager = LocalFocusManager.current
    val coroutineScope = rememberCoroutineScope()


    val onPermissionGranted: () -> Unit = {
        try {
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                userLocation = if (location != null) {
                    LatLng(location.latitude, location.longitude)
                } else {
                    defaultLocation
                }
            }.addOnFailureListener {
                userLocation = defaultLocation
            }
        } catch (e: SecurityException) {
            userLocation = defaultLocation
        }
    }

    val onPermissionDenied: () -> Unit = {
        userLocation = defaultLocation
    }

    LocationPermissionHandler(
        onPermissionGranted = onPermissionGranted,
        onPermissionDenied = onPermissionDenied
    )

    LaunchedEffect(userLocation) {
        userLocation?.let {
            cameraPositionState.animate(
                CameraUpdateFactory.newCameraPosition(
                    CameraPosition.fromLatLngZoom(it, 18f)
                ),
                1000
            )
        }
    }

    LaunchedEffect(userLocation) {
        userLocation?.let {
            try {
                val addresses = geocoder.getFromLocation(it.latitude, it.longitude, 1)
                if (addresses?.isNotEmpty() == true) {
                    val address = addresses[0]
                    currentLocation = "${address.adminArea}, ${address.countryName}"
                }
            } catch (e: Exception) {
                Log.e("MainScreen", "Error getting location: ${e.message}")
            }
        }
    }

    val onSearch: (String) -> Unit = { searchText ->
        if (searchText.isNotBlank()) {
            coroutineScope.launch {
                try {
                    val addresses = geocoder.getFromLocationName(searchText, 1)
                    if (addresses?.isNotEmpty() == true) {
                        val location = addresses[0]
                        val latLng = LatLng(location.latitude, location.longitude)
                        cameraPositionState.animate(
                            CameraUpdateFactory.newLatLngZoom(latLng, 12f)
                        )
                        focusManager.clearFocus()
                    }
                } catch (e: Exception) {
                    Log.e("MainScreen", "Error searching location: ${e.message}")
                    // Optionally, show a toast or a snackbar to the user
                }
            }
        }
    }

    val navItems = listOf(
        BottomNavItem("Harita", Icons.Default.LocationOn),
        BottomNavItem("Market", Icons.Default.ShoppingCart),
        BottomNavItem("Tahliller", Icons.Default.AccountBox),
        BottomNavItem("Profil", Icons.Default.Person)
    )

    Box(
        modifier = Modifier
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .fillMaxSize()
    ) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            uiSettings = MapUiSettings(
                zoomControlsEnabled = false,
                mapToolbarEnabled = false,
                myLocationButtonEnabled = false
            ),
            properties = MapProperties(
                isMyLocationEnabled = userLocation != null,
                mapType = MapType.HYBRID,
                isBuildingEnabled = true
            )
        )

        LocationSearchBar(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 8.dp),
            query = query,
            currentLocation = currentLocation,
            onQueryChange = { query = it },
            onClearClick = { query = "" },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Search
            ),
            keyboardActions = KeyboardActions(
                onSearch = { onSearch(query) }
            )
        )

        SelectAreaButton(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 72.dp, end = 16.dp),
            onClick = { /* TODO: Handle area selection */ }
        )

        AppBottomBar(
            modifier = Modifier.align(Alignment.BottomCenter),
            items = navItems,
            selectedItemIndex = selectedBottomItemIndex,
            onItemSelected = { selectedBottomItemIndex = it }
        )
    }
}