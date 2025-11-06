package com.agrowise.app.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun SelectAreaButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    onAddClick: () -> Unit,
    onDeleteClick: () -> Unit,
    isAddActive: Boolean = true,
    isDeleteActive: Boolean = true,
    isSelectionMode: Boolean = false
) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        IconButton(
            onClick = onClick,
            colors = IconButtonDefaults.iconButtonColors(
                containerColor = Color.White
            )
        ) {
            Icon(
                imageVector = Icons.Default.Create,
                contentDescription = "Select Area or Change Layer"
            )
        }

        AnimatedVisibility(visible = isSelectionMode) {
            Column {
                IconButton(
                    enabled = isAddActive,
                    onClick = onAddClick,
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = Color.White,
                        contentColor = Color.Black,
                        disabledContainerColor = Color.White,
                        disabledContentColor = Color.Gray.copy(alpha = 0.6f)
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Area"
                    )
                }
                IconButton(
                    enabled = isDeleteActive,
                    onClick = onDeleteClick,
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = Color.White,
                        contentColor = Color.Black,
                        disabledContainerColor = Color.White,
                        disabledContentColor = Color.Gray.copy(alpha = 0.6f)
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete Area"
                    )
                }
            }
        }
    }
}

@Preview(name = "Default State")
@Composable
private fun SelectAreaButtonPreview() {
    SelectAreaButton(onClick = {}, onAddClick = {}, onDeleteClick = {})
}

@Preview(name = "Expanded State")
@Composable
private fun SelectAreaButtonExpandedPreview() {
    SelectAreaButton(
        onClick = {},
        onAddClick = {},
        onDeleteClick = {},
        isSelectionMode = true
    )
}

@Preview(name = "Expanded State(Disabled)")
@Composable
private fun SelectAreaButtonExpandedDisabledPreview() {
    SelectAreaButton(
        onClick = {},
        onAddClick = {},
        onDeleteClick = {},
        isSelectionMode = true,
        isAddActive = false,
        isDeleteActive = false
    )
}