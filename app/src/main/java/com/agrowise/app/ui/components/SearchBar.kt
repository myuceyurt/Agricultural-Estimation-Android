package com.agrowise.app.ui.components

import LocationItem
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.agrowise.app.utils.matches

@Composable
fun LocationSearchBar(
    modifier: Modifier = Modifier,
    query: String,
    currentLocation: String,
    onQueryChange: (String) -> Unit,
    allLocations: List<LocationItem>,
    onSuggestionClick: (LocationItem) -> Unit,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    onClearClick: () -> Unit
) {
    var showSuggestions by remember { mutableStateOf(false) }

    val suggestions = remember(query, allLocations) {
        if (query.length < 2) {
            emptyList()
        } else {
            allLocations
                .filter { it.matches(query) }
                .take(3)
        }
    }

    LaunchedEffect(suggestions) {
        showSuggestions = suggestions.isNotEmpty()
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
    ) {
        OutlinedTextField(
            value = query,
            onValueChange = { newValue ->
                onQueryChange(newValue)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            placeholder = { Text(currentLocation) },
            leadingIcon = {
                androidx.compose.material3.Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search Icon"
                )
            },
            trailingIcon = {
                if (query.isNotEmpty()) {
                    androidx.compose.material3.IconButton(onClick = onClearClick) {
                        androidx.compose.material3.Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Clear Search"
                        )
                    }
                }
            },
            shape = RoundedCornerShape(50),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                disabledContainerColor = Color.White,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color(0xFFE0E0E0),
            ),
            singleLine = true
        )

        if (showSuggestions) {
            Spacer(modifier = Modifier.height(4.dp))

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(16.dp),
                color = Color.White.copy(alpha = 0.97f),
                tonalElevation = 4.dp,
                shadowElevation = 8.dp
            ) {
                Column {
                    suggestions.forEachIndexed { index, item ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onQueryChange("${item.district} / ${item.city}")
                                    showSuggestions = false
                                    onSuggestionClick(item)
                                }
                                .padding(horizontal = 16.dp, vertical = 12.dp)
                        ) {
                            Text(
                                text = item.district,
                                fontWeight = FontWeight.SemiBold
                            )

                            Text(
                                text = " / ",
                                color = Color.Gray
                            )

                            Text(
                                text = item.city,
                                color = Color.Gray
                            )
                        }

                        if (index < suggestions.lastIndex) {
                            HorizontalDivider(
                                modifier = Modifier.padding(horizontal = 16.dp),
                                thickness = 1.dp,
                                color = Color(0xFFF0F0F0)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LocationSearchBarPreview() {
    var text by remember { mutableStateOf("altine") }
    val sampleLocations = listOf(
        LocationItem(city = "KONYA", district = "ALTINEKİN"),
        LocationItem(city = "KONYA", district = "MERAM")
    )

    LocationSearchBar(
        query = text,
        currentLocation = "İstanbul, Türkiye",
        onQueryChange = { text = it },
        allLocations = sampleLocations,
        onSuggestionClick = { },
        onClearClick = { text = "" }
    )
}
