import android.graphics.Point
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.agrowise.app.permissions.LocationPermissionHandler
import com.agrowise.app.ui.components.LocationSearchBar
import com.agrowise.app.ui.components.SelectAreaButton
import com.agrowise.app.ui.theme.*
import com.agrowise.app.ui.viewmodel.MainViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.SphericalUtil
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Polygon
import com.google.maps.android.compose.rememberCameraPositionState
import kotlin.math.max
import kotlin.math.roundToInt

@Composable
fun MainScreen(
    viewModel: MainViewModel = hiltViewModel(),
    onAreaSelectionModeChange: (Boolean) -> Unit,
    onAnalyzeClick: (Double, Double, Double) -> Unit
) {
    var query by remember { mutableStateOf("") }
    var polygonPoints by remember { mutableStateOf<List<LatLng>>(emptyList()) }
    var isAreaSelectionMode by remember { mutableStateOf(false) }
    val selectionPoints = remember { mutableStateListOf<Offset>() }

    var showSelectionFilter by remember { mutableStateOf(false) }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(41.0082, 28.9784), 18f)
    }

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

    val pulseTransition = rememberInfiniteTransition()

    val pulseAlpha by pulseTransition.animateFloat(
        initialValue = 0.25f,
        targetValue = 0.65f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    val pulseStrokeWidth by pulseTransition.animateFloat(
        initialValue = 15f,
        targetValue = 20f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

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
                    fillColor = Color.White.copy(alpha = pulseAlpha),
                    strokeColor = Color.White,
                    strokeWidth = pulseStrokeWidth,
                    zIndex = 1f
                )
            }
        }

        AnimatedVisibility(
            visible = isAreaSelectionMode,
            enter = fadeIn(animationSpec = tween(300)),
            exit = fadeOut(animationSpec = tween(300))
        ) {
            val explosionProgress = remember { Animatable(0f) }
            val animatedBrush = createAnimatedBrush()

            val glowTransition = rememberInfiniteTransition()
            val glowProgress by glowTransition.animateFloat(
                initialValue = 0f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(
                        durationMillis = 900,
                        easing = LinearEasing
                    ),
                    repeatMode = RepeatMode.Reverse
                )
            )

            LaunchedEffect(Unit) {
                showSelectionFilter = true
                explosionProgress.snapTo(0f)
                explosionProgress.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(
                        durationMillis = 4000,
                        easing = LinearEasing
                    )
                )
            }

            AnimatedVisibility(
                visible = showSelectionFilter,
                enter = fadeIn(),
                exit = fadeOut(animationSpec = tween(500))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(brush = animatedBrush)
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val maxRadius = max(size.width, size.height) * 1.2f
                        val radius = explosionProgress.value * maxRadius
                        val alpha = (1f - explosionProgress.value).coerceIn(0f, 1f)
                        drawCircle(
                            brush = animatedBrush,
                            center = center,
                            radius = radius,
                            alpha = alpha
                        )
                    }
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        isAreaSelectionMode = false
                        onAreaSelectionModeChange(false)
                        showSelectionFilter = false
                    }
                    .pointerInput(Unit) {
                        detectDragGestures(
                            onDragStart = { offset ->
                                showSelectionFilter = false
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

                        val minAlpha = 0.8f
                        val maxAlpha = 1.0f
                        val currentAlpha = minAlpha + (maxAlpha - minAlpha) * glowProgress

                        drawPath(
                            path = path,
                            color = Color.White.copy(alpha = currentAlpha),
                            style = Stroke(
                                width = pulseStrokeWidth,
                                cap = StrokeCap.Round,
                                join = StrokeJoin.Round
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
                onQueryChange = { newQuery ->
                    query = newQuery
                },
                allLocations = viewModel.allLocations,
                onSuggestionClick = { item ->
                    viewModel.onLocationSuggestionSelected(item)
                    focusManager.clearFocus()
                },
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Search,
                    keyboardType = KeyboardType.Text
                ),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        viewModel.searchLocation(query)
                        focusManager.clearFocus()
                    }
                ),
                onClearClick = {
                    query = ""
                }
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

                if (isAreaSelectionMode) {
                    showSelectionFilter = true
                } else {
                    polygonPoints = emptyList()
                    showSelectionFilter = false
                }
            },
            onAddClick = {
                if (polygonPoints.size < 3) return@SelectAreaButton

                val centerPoint = getCenterPoint(polygonPoints)
                val area = SphericalUtil.computeArea(polygonPoints)
                val areaInHectares = area / 10_000.0

                onAnalyzeClick(centerPoint.latitude, centerPoint.longitude, areaInHectares)

                isAreaSelectionMode = false
                onAreaSelectionModeChange(isAreaSelectionMode)
                polygonPoints = emptyList()
                showSelectionFilter = false
            },
            onDeleteClick = {
                polygonPoints = emptyList()
            }
        )

        if (polygonPoints.size >= 3) {
            AreaSizeLabelOverlay(
                cameraPositionState = cameraPositionState,
                polygonPoints = polygonPoints,

                )
        }
    }
}

@Composable
private fun createAnimatedBrush(): Brush {
    val infiniteTransition = rememberInfiniteTransition(label = "aurora_transition")
    val offset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(10000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    val size = with(LocalDensity.current) { 3000.dp.toPx() }

    return Brush.radialGradient(
        colors = listOf(
            NeonGreen.copy(alpha = 0.22f),
            AquaGreen.copy(alpha = 0.22f),
            BrightBlue.copy(alpha = 0.22f),
            DeepPurple.copy(alpha = 0.22f),
            Turquoise.copy(alpha = 0.22f),
            SteelBlue.copy(alpha = 0.22f),
            ElectricLime.copy(alpha = 0.22f)
        ),
        center = Offset(offset * size, offset * size),
        radius = size * 1.2f
    )
}

fun getCenterPoint(polygonPoints: List<LatLng>): LatLng {
    val centerLat = polygonPoints.map { it.latitude }.average()
    val centerLng = polygonPoints.map { it.longitude }.average()
    return LatLng(centerLat, centerLng)
}