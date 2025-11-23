package com.agrowise.app.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.agrowise.app.R
import com.agrowise.app.ui.navigation.Screen

data class BottomNavItem(
    val label: String,
    val icon: Painter,
    val route: String
)

@Composable
fun AppBottomBar(
    modifier: Modifier = Modifier,
    items: List<BottomNavItem>,
    selectedItemIndex: Int,
    onItemSelected: (Int) -> Unit
) {
    Card(
        modifier = modifier
            .padding(horizontal = 24.dp, vertical = 16.dp),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        NavigationBar(
            containerColor = Color.Transparent,
            tonalElevation = 0.dp
        ) {
            items.forEachIndexed { index, item ->
                val isSelected = selectedItemIndex == index
                NavigationBarItem(
                    selected = isSelected,
                    onClick = { onItemSelected(index) },
                    label = {
                        Text(
                            text = item.label,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 12.sp
                        )
                    },
                    icon = { Icon(item.icon, contentDescription = "${item.label} Icon") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.Black,
                        selectedTextColor = Color.Black,
                        unselectedIconColor = Color.Gray,
                        unselectedTextColor = Color.Gray,
                        indicatorColor = Color.Transparent
                    )
                )
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFE0E0E0)
@Composable
fun AppBottomBarPreview() {
    val navItems = listOf(
        BottomNavItem("Harita", painterResource(R.drawable.map_tab_icon), Screen.Main.route),
        BottomNavItem("Analizler", painterResource(R.drawable.analysis_tab_icon), Screen.Analyses.route),
        BottomNavItem("Profil", painterResource(R.drawable.profile_tab_icon), Screen.Profile.route)
    )

    var selectedIndex by remember { mutableStateOf(0) }

    AppBottomBar(
        items = navItems,
        selectedItemIndex = selectedIndex,
        onItemSelected = { selectedIndex = it }
    )
}