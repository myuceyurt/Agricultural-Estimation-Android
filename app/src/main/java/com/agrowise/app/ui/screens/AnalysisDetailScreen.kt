package com.agrowise.app.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.agrowise.app.ui.theme.AgroWiseTheme

private val AgroGreen = Color(0xFF4CAF50)
private val BgGray = Color(0xFFF5F5F5)
private val CardWhite = Color.White
private val TextBlack = Color.Black
private val TextGray = Color(0xFF757575)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalysisDetailScreen(
    onBackClick: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Parsel A - Kuzey",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextBlack
                        )
                        Text(
                            text = "10 Eki 2023 • 14:30",
                            fontSize = 12.sp,
                            color = TextGray
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Geri",
                            tint = TextBlack
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { /* Share */ }) {
                        Icon(Icons.Default.Share, contentDescription = "Paylaş", tint = TextBlack)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = CardWhite)
            )
        },
        containerColor = BgGray,
        bottomBar = {
            Surface(
                color = CardWhite,
                shadowElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Button(
                        onClick = { /* Download PDF */ },
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AgroGreen.copy(alpha = 0.1f),
                            contentColor = AgroGreen
                        ),
                        shape = RoundedCornerShape(12.dp),
                        elevation = ButtonDefaults.buttonElevation(0.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Rapor İndir")
                    }

                    Button(
                        onClick = { /* Action */ },
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AgroGreen),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Öneri Al")
                    }
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .background(Color(0xFFE0E0E0))
            ) {
                SatelliteViewMockup()

                Surface(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(16.dp),
                    color = CardWhite.copy(alpha = 0.9f),
                    shape = RoundedCornerShape(50),
                    shadowElevation = 2.dp
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(AgroGreen, CircleShape)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Sağlıklı",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = AgroGreen
                        )
                    }
                }
            }

            Column(modifier = Modifier.padding(16.dp)) {

                ScoreCardSection()

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Detaylı Metrikler",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextBlack,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    DetailMetricCard(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.Add,
                        label = "Nem Oranı",
                        value = "%45",
                        color = Color(0xFF2196F3)
                    )
                    DetailMetricCard(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.Add,
                        label = "Azot (N)",
                        value = "Yüksek",
                        color = Color(0xFFFF9800)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    DetailMetricCard(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.Add,
                        label = "NDVI",
                        value = "0.72",
                        color = AgroGreen
                    )
                    DetailMetricCard(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.Add,
                        label = "Sıcaklık",
                        value = "24°C",
                        color = Color(0xFFF44336)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                TrendGraphCard()

                Spacer(modifier = Modifier.height(24.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = CardWhite),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.LocationOn, null, tint = TextGray, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Konum: 41.0082, 28.9784", color = TextGray, fontSize = 14.sp)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Share, null, tint = TextGray, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Son güncelleme: 2 gün önce", color = TextGray, fontSize = 14.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ScoreCardSection() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(100.dp)
            ) {
                CircularHealthIndicator(score = 0.85f)
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "85",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TextBlack
                    )
                    Text(
                        text = "Skor",
                        fontSize = 12.sp,
                        color = TextGray
                    )
                }
            }

            Spacer(modifier = Modifier.width(24.dp))

            Column {
                Text(
                    text = "Verim Tahmini",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextBlack
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Bitki sağlığı mevsim normallerinin üzerinde seyrediyor.",
                    fontSize = 14.sp,
                    color = TextGray,
                    lineHeight = 20.sp
                )
            }
        }
    }
}

@Composable
fun CircularHealthIndicator(score: Float) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val strokeWidth = 10.dp.toPx()
        val radius = size.minDimension / 2 - strokeWidth / 2

        drawCircle(
            color = Color(0xFFEEEEEE),
            radius = radius,
            style = Stroke(width = strokeWidth)
        )

        drawArc(
            brush = Brush.sweepGradient(
                colors = listOf(
                    Color(0xFF8D6E63),
                    Color(0xFFFFEB3B),
                    AgroGreen,
                    AgroGreen
                )
            ),
            startAngle = -90f,
            sweepAngle = 360 * score,
            useCenter = false,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
            topLeft = Offset(strokeWidth / 2, strokeWidth / 2),
            size = Size(size.width - strokeWidth, size.height - strokeWidth)
        )
    }
}

@Composable
fun DetailMetricCard(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    label: String,
    value: String,
    color: Color
) {
    Card(
        modifier = modifier.height(110.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(color.copy(alpha = 0.15f), RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(20.dp)
                )
            }

            Column {
                Text(
                    text = value,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextBlack
                )
                Text(
                    text = label,
                    fontSize = 13.sp,
                    color = TextGray
                )
            }
        }
    }
}

@Composable
fun TrendGraphCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Gelişim Grafiği (NDVI)",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextBlack
                )

                Text(
                    text = "Son 30 Gün",
                    fontSize = 12.sp,
                    color = AgroGreen,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val path = Path()
                    val width = size.width
                    val height = size.height

                    path.moveTo(0f, height * 0.8f)
                    path.cubicTo(
                        width * 0.3f, height * 0.9f,
                        width * 0.6f, height * 0.2f,
                        width, height * 0.4f
                    )

                    drawPath(
                        path = path,
                        color = AgroGreen,
                        style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                    )

                    path.lineTo(width, height)
                    path.lineTo(0f, height)
                    path.close()

                    drawPath(
                        path = path,
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                AgroGreen.copy(alpha = 0.3f),
                                Color.Transparent
                            )
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun SatelliteViewMockup() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height

        drawRect(color = Color(0xFF3E2723))

        drawRect(
            color = Color(0xFF2E7D32),
            topLeft = Offset(0f, 0f),
            size = Size(width * 0.6f, height)
        )
        drawRect(
            color = Color(0xFF558B2F),
            topLeft = Offset(width * 0.6f, 0f),
            size = Size(width * 0.4f, height * 0.5f)
        )
        drawRect(
            color = Color(0xFF8BC34A),
            topLeft = Offset(width * 0.6f, height * 0.5f),
            size = Size(width * 0.4f, height * 0.5f)
        )

        val gridSize = 50.dp.toPx()
        for (i in 0..10) {
            drawLine(
                color = Color.White.copy(alpha = 0.1f),
                start = Offset(i * gridSize, 0f),
                end = Offset(i * gridSize, height),
                strokeWidth = 1f
            )
            drawLine(
                color = Color.White.copy(alpha = 0.1f),
                start = Offset(0f, i * gridSize),
                end = Offset(width, i * gridSize),
                strokeWidth = 1f
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AnalysisDetailScreenPreview() {
    AgroWiseTheme {
        AnalysisDetailScreen()
    }
}