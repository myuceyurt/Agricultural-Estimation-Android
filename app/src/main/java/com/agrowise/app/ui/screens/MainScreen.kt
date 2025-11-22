import android.graphics.Point
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
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
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.animation.core.RepeatMode
import androidx.hilt.navigation.compose.hiltViewModel
import com.agrowise.app.permissions.LocationPermissionHandler
import com.agrowise.app.ui.components.LocationSearchBar
import com.agrowise.app.ui.components.SelectAreaButton
import com.agrowise.app.ui.viewmodel.MainViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Polygon
import com.google.maps.android.compose.rememberCameraPositionState
import kotlin.math.roundToInt

@Composable
fun MainScreen(
    viewModel: MainViewModel = hiltViewModel(),
    onAreaSelectionModeChange: (Boolean) -> Unit
) {
    var query by remember { mutableStateOf("") }
    var polygonPoints by remember { mutableStateOf<List<LatLng>>(emptyList()) }
    var isAreaSelectionMode by remember { mutableStateOf(false) }
    val selectionPoints = remember { mutableStateListOf<Offset>() }

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
        onPermissionGranted = { viewModel.onPermissionGranted() },
        onPermissionDenied = { viewModel.onPermissionDenied() }
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

    val selectionColor = MaterialTheme.colorScheme.primary
    val selectionFillColor = selectionColor.copy(alpha = 0.3f)

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
            onMapClick = {}
        ) {
            if (polygonPoints.size > 1) {
                Polygon(
                    points = polygonPoints,
                    fillColor = selectionFillColor,
                    strokeColor = selectionColor,
                    strokeWidth = 5f
                )
            }
        }

        if (isAreaSelectionMode) {
            val infiniteTransition = rememberInfiniteTransition()
            val phase by infiniteTransition.animateFloat(
                initialValue = 0f,
                targetValue = 1000f,
                animationSpec = infiniteRepeatable(
                    animation = tween(durationMillis = 1000, easing = LinearEasing),
                    repeatMode = RepeatMode.Restart
                )
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(isAreaSelectionMode) {
                        if (!isAreaSelectionMode) return@pointerInput

                        detectDragGestures(
                            onDragStart = { offset ->
                                selectionPoints.clear()
                                selectionPoints.add(offset)
                            },
                            onDrag = { change, _ ->
                                selectionPoints.add(change.position)
                                change.consume()
                            },
                            onDragEnd = {
                                val projection = cameraPositionState.projection
                                if (projection != null && selectionPoints.size > 2) {
                                    polygonPoints = selectionPoints.mapNotNull { offset ->
                                        val pt = Point(
                                            offset.x.roundToInt(),
                                            offset.y.roundToInt()
                                        )
                                        try {
                                            projection.fromScreenLocation(pt)
                                        } catch (_: Exception) {
                                            null
                                        }
                                    }
                                }
                                selectionPoints.clear()
                            }
                        )
                    }
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    if (selectionPoints.size > 1) {
                        val path = Path().apply {
                            moveTo(selectionPoints.first().x, selectionPoints.first().y)
                            for (i in 1 until selectionPoints.size) {
                                lineTo(selectionPoints[i].x, selectionPoints[i].y)
                            }
                        }

                        drawPath(
                            path = path,
                            color = selectionColor,
                            style = Stroke(
                                width = 4.dp.toPx(),
                                pathEffect = PathEffect.dashPathEffect(
                                    floatArrayOf(20f, 20f),
                                    phase
                                )
                            )
                        )
                    }
                }
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
                .align(Alignment.TopEnd)
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
                if (polygonPoints.size < 3) return@SelectAreaButton

                val centerPoint = getCenterPoint(polygonPoints)
                Toast.makeText(
                    context,
                    "${centerPoint.latitude}, ${centerPoint.longitude}",
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
    return LatLng(centerLat, centerLng)
}