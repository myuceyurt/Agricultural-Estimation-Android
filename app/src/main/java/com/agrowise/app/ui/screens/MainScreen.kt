import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.agrowise.app.permissions.LocationPermissionHandler
import com.agrowise.app.ui.components.LocationSearchBar
import com.agrowise.app.ui.components.SelectAreaButton
import com.agrowise.app.ui.viewmodel.MainViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.SphericalUtil
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.Polygon
import com.google.maps.android.compose.rememberCameraPositionState
import java.text.NumberFormat

@Composable
fun MainScreen(
    viewModel: MainViewModel = hiltViewModel(),
    onAreaSelectionModeChange: (Boolean) -> Unit
) {
    var query by remember { mutableStateOf("") }
    var polygonPoints by remember { mutableStateOf<List<LatLng>>(emptyList()) }
    var isAreaSelectionMode by remember { mutableStateOf(false) }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(41.0082, 28.9784), 18f)
    }

    val context = LocalContext.current
    val focusManager = LocalFocusManager.current

    val animatedTopPadding by animateDpAsState(
        targetValue = if (isAreaSelectionMode) 8.dp else 72.dp,
        animationSpec = tween(500, easing = FastOutSlowInEasing)
    )

    LocationPermissionHandler(
        onPermissionGranted = {viewModel.onPermissionGranted()},
        onPermissionDenied = {viewModel.onPermissionDenied()}
    )

    LaunchedEffect(viewModel.initialLocation) {
        viewModel.initialLocation?.let {
            cameraPositionState.animate(
                CameraUpdateFactory.newCameraPosition(
                    CameraPosition.fromLatLngZoom(it, 18f)
                ),
                1000
            )
            viewModel.getAddressFromLocation(it)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            uiSettings = MapUiSettings(
                zoomControlsEnabled = false,
                mapToolbarEnabled = false,
                myLocationButtonEnabled = false,
            ),
            properties = MapProperties(
                isMyLocationEnabled = viewModel.initialLocation != null,
                mapType = MapType.HYBRID,
                isBuildingEnabled = true
            ),
            onMapClick = { latLng ->
                if (isAreaSelectionMode) {
                    polygonPoints = polygonPoints + latLng
                }

            }
        ) {
            polygonPoints.forEach { point ->
                Marker(
                    state = MarkerState(position = point),
                    title = "Farm Point ${polygonPoints.indexOf(point) + 1}"
                )
            }

            if (polygonPoints.size > 1) {
                Polygon(
                    points = polygonPoints,
                    fillColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                    strokeColor = MaterialTheme.colorScheme.primary,
                    strokeWidth = 5f
                )
            }
        }

        AnimatedVisibility(
            visible = !isAreaSelectionMode,
            enter = slideInVertically(
                initialOffsetY = { fullHeight -> -fullHeight * 2 },
                animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing)
            ),
            exit = slideOutVertically(
                targetOffsetY = { fullHeight -> -fullHeight * 2 },
                animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing)
            ),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .padding(top = 8.dp)
        ) {
            LocationSearchBar(
                query = query,
                currentLocation = viewModel.currentLocation,
                onQueryChange = { query = it },
                onClearClick = { query = "" },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Search
                ),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        viewModel.searchLocation(query)
                        focusManager.clearFocus()
                    }
                )
            )
        }

        SelectAreaButton(
            modifier = Modifier
                .align(if (isAreaSelectionMode) Alignment.TopEnd else Alignment.TopEnd)
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .padding(top = animatedTopPadding, end = 16.dp),
            isAddActive = polygonPoints.size >= 3,
            isDeleteActive = polygonPoints.isNotEmpty(),
            isSelectionMode = isAreaSelectionMode,
            onClick = {
                isAreaSelectionMode = !isAreaSelectionMode
                onAreaSelectionModeChange(isAreaSelectionMode)
                if (!isAreaSelectionMode) {
                    polygonPoints = emptyList()
                }
            },
            onAddClick = {
                val centerPoint = getCenterPoint(polygonPoints)
                val area = calculatePolygonArea(polygonPoints)
                val formattedArea = NumberFormat.getNumberInstance().apply {
                    maximumFractionDigits = 2
                }.format(area)

                val formattedLat = String.format("%.2f", centerPoint.latitude)
                val formattedLon = String.format("%.2f", centerPoint.longitude)

                Toast.makeText(
                    context,
                    "Merkez: $formattedLat, $formattedLon\nAlan: $formattedArea dönüm",
                    Toast.LENGTH_LONG
                ).show()
                isAreaSelectionMode = false
                onAreaSelectionModeChange(isAreaSelectionMode)
                polygonPoints = emptyList()
            },
            onDeleteClick = {
                polygonPoints = emptyList()
            }
        )
    }
}

fun getCenterPoint(polygonPoints: List<LatLng>): LatLng {
    val centerLat = polygonPoints.map { it.latitude }.average()
    val centerLng = polygonPoints.map { it.longitude }.average()
    val centerPoint = LatLng(centerLat, centerLng)

    return centerPoint
}

fun calculatePolygonArea(polygonPoints: List<LatLng>): Double {
    val areaInSquareMeters = SphericalUtil.computeArea(polygonPoints)
    return areaInSquareMeters / 1000
}