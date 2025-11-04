import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun SelectAreaButton(modifier: Modifier = Modifier, onClick: () -> Unit) {
    IconButton(
        onClick = onClick,
        modifier = modifier,
        colors = IconButtonDefaults.iconButtonColors(
            containerColor = Color.White
        )
    ) {
        Icon(
            imageVector = Icons.Default.Create,
            contentDescription = "Select Area or Change Layer"
        )
    }
}

@Preview
@Composable
private fun SelectAreaButtonPreview() {
    SelectAreaButton(onClick = {})
}