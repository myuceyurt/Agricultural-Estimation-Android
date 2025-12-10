package com.agrowise.app.ui.screens

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.agrowise.app.R
import com.agrowise.app.data.model.Analysis
import com.agrowise.app.ui.components.shimmerEffect
import com.agrowise.app.ui.state.PredictionUiState
import com.agrowise.app.ui.theme.AgroWiseTheme
import com.agrowise.app.ui.viewmodel.AnalyzesViewModel
import kotlinx.coroutines.delay

@Composable
fun AnalyzesScreen(
    viewModel: AnalyzesViewModel = hiltViewModel(),
    navigateToMap: () -> Unit = {},
    onAnalysisClick: (Int) -> Unit,
    startAnalysisParams: Triple<Double, Double, Double>? = null
) {
    val analyzes by viewModel.analyzes.collectAsState()
    val predictionState by viewModel.predictionState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchAllAnalyzes()
    }

    LaunchedEffect(startAnalysisParams) {
        startAnalysisParams?.let { (lat, lon, hectare) ->
            viewModel.createAnalysis(lat, lon, hectare)
        }
    }

    AnalyzesScreenContent(
        analyzes = analyzes,
        predictionState = predictionState,
        onAddClick = navigateToMap,
        onDeleteClick = viewModel::deleteAnalysis,
        onAnalysisClick = onAnalysisClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyzesScreenContent(
    analyzes: List<Analysis>,
    predictionState: PredictionUiState = PredictionUiState.Idle,
    onAddClick: () -> Unit,
    onDeleteClick: (Analysis) -> Unit,
    initialDeleteMode: Boolean = false,
    onAnalysisClick: (Int) -> Unit = {}
) {
    var deleteMode by remember { mutableStateOf(initialDeleteMode) }

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
                        Icon(
                            painter = painterResource(R.drawable.search_icon),
                            contentDescription = "Ara"
                        )
                    }
                    IconButton(onClick = { onAddClick() }) {
                        Icon(
                            painter = painterResource(id = R.drawable.add_icon),
                            contentDescription = "Ekle"
                        )
                    }
                    IconButton(onClick = { deleteMode = !deleteMode }) {
                        Icon(
                            painter = if (!deleteMode || analyzes.isEmpty()) {
                                painterResource(id = R.drawable.open_delete_mode)
                            } else {
                                painterResource(id = R.drawable.exit_delete_mode)
                            },
                            contentDescription = "Kaldır",
                            tint = Color.Unspecified
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        containerColor = Color.White
    ) { padding ->
        if (analyzes.isEmpty() && predictionState is PredictionUiState.Idle) {
            EmptyAnalyzesContent(onAddClick)
        } else {
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
                    if (predictionState is PredictionUiState.Loading) {
                        item {
                            LoadingAnalysisCard()
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                    }

                    if (predictionState is PredictionUiState.Error) {
                        item {
                            val msg = predictionState.msg
                            Text(
                                text = "Hata oluştu: $msg",
                                color = Color.Red,
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                    }
                    items(items = analyzes, key = { it.id }) { analysis ->
                        Box(modifier = Modifier.clickable {
                            if (!deleteMode) onAnalysisClick(analysis.id)
                        }) {
                            AnalysisCard(
                                analysis = analysis,
                                deleteMode = deleteMode,
                                onDeleteClick = { onDeleteClick(analysis) }
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
            }
        }
    }
}

@SuppressLint("DefaultLocale")
@Composable
fun AnalysisCard(
    analysis: Analysis,
    deleteMode: Boolean,
    onDeleteClick: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition()
    val rawRotation by infiniteTransition.animateFloat(
        initialValue = -1f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 100,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
    )

    val rotation = if (deleteMode) rawRotation else 0f
    val scale = if (deleteMode) 1.02f else 1f

    var isVisible by remember { mutableStateOf(true) }

    LaunchedEffect(isVisible) {
        if (!isVisible) {
            delay(300)
            onDeleteClick()
        }
    }

    AnimatedVisibility(
        visible = isVisible,
        exit = slideOutHorizontally(
            targetOffsetX = { fullWidth -> fullWidth },
            animationSpec = tween(durationMillis = 300)
        ) + fadeOut(animationSpec = tween(durationMillis = 300))
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .graphicsLayer(
                    rotationZ = rotation,
                    scaleX = scale,
                    scaleY = scale,
                    transformOrigin = TransformOrigin(0.5f, 0.9f)
                ),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .background(Color(analysis.color), RoundedCornerShape(8.dp))
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = analysis.name,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Black,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = analysis.area,
                        fontSize = 14.sp,
                        color = Color.Gray,
                        maxLines = 1
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .width(100.dp)
                            .height(8.dp)
                            .background(Color(0xFFE0E0E0), RoundedCornerShape(4.dp))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .fillMaxWidth(analysis.score.toFloat())
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

                AnimatedVisibility(
                    visible = deleteMode,
                    enter = slideInHorizontally(initialOffsetX = { it }) +
                            expandHorizontally(expandFrom = Alignment.End) +
                            fadeIn(),
                    exit = slideOutHorizontally(targetOffsetX = { it }) +
                            shrinkHorizontally(shrinkTowards = Alignment.End) +
                            fadeOut()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Spacer(modifier = Modifier.width(24.dp))

                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = ripple(bounded = false),
                                    onClick = {
                                        // trigger slide-out, real delete happens after animation
                                        isVisible = false
                                    }
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.delete_analysis),
                                contentDescription = "Analiz Sil",
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
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
                    painter = painterResource(R.drawable.add_icon),
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
                    painter = painterResource(R.drawable.add_icon),
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
                Analysis(1, "Analysis 1", "8.3 ha", "Complete", 0.73, 0xFF4CAF50),
                Analysis(2, "Analysis 2", "3.7 ha", "Pending", 0.10, 0xFF8D6E63),
                Analysis(3, "Analysis 3", "5.2 ha", "Complete", 0.85, 0xFF66BB6A),
                Analysis(4, "Analysis 4", "2.1 ha", "In Progress", 0.45, 0xFFFFB74D)
            ),
            onAddClick = {},
            onDeleteClick = {}
        )
    }
}

@Composable
fun LoadingAnalysisCard() {
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
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .shimmerEffect()
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Metinler Yeri
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .height(20.dp)
                        .fillMaxWidth(0.7f)
                        .clip(RoundedCornerShape(4.dp))
                        .shimmerEffect()
                )
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .height(14.dp)
                        .fillMaxWidth(0.4f)
                        .clip(RoundedCornerShape(4.dp))
                        .shimmerEffect()
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .width(100.dp)
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .shimmerEffect()
                )
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .size(40.dp, 20.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .shimmerEffect()
                )
            }
        }
    }
}

@Preview
@Composable
private fun AnalyzesScreenDeleteModePreview() {
    AgroWiseTheme {
        AnalyzesScreenContent(
            analyzes = listOf(
                Analysis(1, "Analysis 1", "8.3 ha", "Complete", 0.73, 0xFF4CAF50),
                Analysis(2, "Analysis 2", "3.7 ha", "Pending", 0.10, 0xFF8D6E63),
                Analysis(3, "Analysis 3", "5.2 ha", "Complete", 0.85, 0xFF66BB6A),
                Analysis(4, "Analysis 4", "2.1 ha", "In Progress", 0.45, 0xFFFFB74D)
            ),
            onAddClick = {},
            onDeleteClick = { },
            initialDeleteMode = true
        )
    }

}

@Preview(showBackground = true)
@Composable
private fun AnalyzesScreenEmptyPreview() {
    AgroWiseTheme {
        AnalyzesScreenContent(
            analyzes = emptyList(),
            onAddClick = {},
            onDeleteClick = {}
        )
    }
}