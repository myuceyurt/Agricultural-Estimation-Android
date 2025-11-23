import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.agrowise.app.ui.theme.AgroWiseTheme
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.SphericalUtil
import com.google.maps.android.compose.CameraPositionState
import java.text.DecimalFormat

@Composable
fun AreaSizeLabelOverlay(
    cameraPositionState: CameraPositionState,
    polygonPoints: List<LatLng>
) {
    if (polygonPoints.size < 3) return

    val projection = cameraPositionState.projection ?: return
    val density = LocalDensity.current

    val center = getCenterPoint(polygonPoints)
    val screenPoint = projection.toScreenLocation(center)

    val offsetX = with(density) { screenPoint.x.toDp() }
    val offsetY = with(density) { screenPoint.y.toDp() }

    val area = SphericalUtil.computeArea(polygonPoints)

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.TopStart
    ) {
        AreaLabel(
            area = area,
            modifier = Modifier
                .offset(x = offsetX - 60.dp, y = offsetY - 20.dp)
        )
    }
}

@Composable
fun AreaLabel(
    area: Double,
    modifier: Modifier = Modifier
) {
    val areaInHectares = area / 10_000.0
    val formattedArea = DecimalFormat("#.##").format(areaInHectares)

    Box(
        modifier = modifier
            .background(
                color = Color.White,
                shape = MaterialTheme.shapes.large
            )
            .padding(horizontal = 12.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "$formattedArea hektar",
            color = Color.Black,
            fontSize = 14.sp,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Preview
@Composable
fun AreaLabelPreview() {
    AgroWiseTheme {
        AreaLabel(area = 12345.67)
    }
}