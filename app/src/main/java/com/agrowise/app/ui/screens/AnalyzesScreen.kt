package com.agrowise.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.agrowise.app.data.model.Analysis
import com.agrowise.app.ui.theme.AgroWiseTheme
import com.agrowise.app.ui.viewmodel.AnalyzesViewModel

@Composable
fun AnalyzesScreen(
    viewModel: AnalyzesViewModel = viewModel()
) {
    val analyzes by viewModel.analyzes.collectAsState()

    AnalyzesScreenContent(analyzes = analyzes)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyzesScreenContent(
    analyzes: List<Analysis>
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Analizler",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                },
                actions = {
                    IconButton(onClick = { }) {
                        Icon(Icons.Default.Search, contentDescription = "Ara")
                    }
                    IconButton(onClick = { }) {
                        Icon(Icons.Default.Add, contentDescription = "Ekle")
                    }
                    IconButton(onClick = { }) {
                        Icon(Icons.Default.Delete, contentDescription = "Sil")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        containerColor = Color.White
    ) { padding ->

        if(analyzes.isEmpty()){
            EmptyAnalyzesContent()
        }
        else{
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFF5F5F5))
                    .padding(padding)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Tüm Analizler",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black
                    )
                    Text(
                        text = "12 dönüm",
                        fontSize = 16.sp,
                        color = Color.Gray
                    )
                }

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp)
                ) {
                    items(analyzes) { analysis ->
                        AnalysisCard(analysis)
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
            }
        }

    }
}

@Composable
fun AnalysisCard(analysis: Analysis) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .background(Color(analysis.color), RoundedCornerShape(8.dp))
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = analysis.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = analysis.area,
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }

            Column(
                horizontalAlignment = Alignment.End
            ) {
                Box(
                    modifier = Modifier
                        .width(120.dp)
                        .height(8.dp)
                        .background(Color(0xFFE0E0E0), RoundedCornerShape(4.dp))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(analysis.score)
                            .background(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(
                                        Color(0xFF8D6E63),
                                        Color(0xFFFFEB3B),
                                        Color(0xFF4CAF50)
                                    )
                                ),
                                shape = RoundedCornerShape(4.dp)
                            )
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = String.format("%.2f", analysis.score),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }
        }
    }
}

@Composable
fun EmptyAnalyzesContent(
    onAddClick: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFAFAFA))
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .background(
                        color = Color(0xFFF5F5F5),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    modifier = Modifier.size(56.dp),
                    tint = Color(0xFFBDBDBD)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Henüz analiz yok",
                fontSize = 24.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF212121),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "İlk analizinizi oluşturarak başlayın",
                fontSize = 16.sp,
                color = Color(0xFF757575),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(40.dp))

            Button(
                onClick = onAddClick,
                modifier = Modifier
                    .height(56.dp)
                    .widthIn(min = 200.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF4CAF50)
                ),
                shape = RoundedCornerShape(12.dp),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 0.dp
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Analiz Oluştur",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AnalyzesScreenPreview() {
    AgroWiseTheme {
        AnalyzesScreenContent(
            analyzes = listOf(
                Analysis(1, "Analysis 1", "8.3 ha", "Complete", 0.73f, 0xFF4CAF50),
                Analysis(2, "Analysis 2", "3.7 ha", "Pending", 0.10f, 0xFF8D6E63),
                Analysis(3, "Analysis 3", "5.2 ha", "Complete", 0.85f, 0xFF66BB6A),
                Analysis(4, "Analysis 4", "2.1 ha", "In Progress", 0.45f, 0xFFFFB74D)
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun AnalyzesScreenEmptyPreview() {
    AgroWiseTheme {
        AnalyzesScreenContent(
            analyzes = emptyList()
        )
    }
}