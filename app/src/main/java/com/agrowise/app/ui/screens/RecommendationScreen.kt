package com.agrowise.app.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.agrowise.app.data.model.Analysis
import com.agrowise.app.ui.viewmodel.AnalyzesViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

private val AgroGreen = Color(0xFF4CAF50)
private val BgGray = Color(0xFFF5F5F5)
private val CardWhite = Color.White
private val TextBlack = Color.Black
private val TextGray = Color(0xFF757575)

data class ChatMessage(
    val id: String = UUID.randomUUID().toString(),
    val content: String,
    val isUser: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecommendationScreen(
    analysisId: Int,
    onBackClick: () -> Unit = {},
    viewModel: AnalyzesViewModel = hiltViewModel()
) {
    val analyzes by viewModel.analyzes.collectAsState()
    val analysis = analyzes.find { it.id == analysisId }

    var messages by remember { mutableStateOf(listOf<ChatMessage>()) }
    var inputText by remember { mutableStateOf("") }
    var isTyping by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(analysisId) {
        if (analysis == null) {
            viewModel.fetchAnalyzesById(analysisId.toLong())
        }
    }

    LaunchedEffect(analysis) {
        if (analysis != null && messages.isEmpty()) {
            delay(500)
            messages = messages + ChatMessage(
                content = "Merhaba! ${analysis.name} için yapay zeka destekli önerileriniz hazır. Size nasıl yardımcı olabilirim?",
                isUser = false
            )
            delay(1000)
            messages = messages + ChatMessage(
                content = generateInitialRecommendations(analysis),
                isUser = false
            )
        }
    }

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    if (analysis == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = AgroGreen)
        }
        return
    }

    Scaffold(
        topBar = {
            Surface(
                color = CardWhite,
                shadowElevation = 4.dp
            ) {
                Column {
                    TopAppBar(
                        title = {
                            Column {
                                Text(
                                    text = "Yapay Zeka Önerileri",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextBlack
                                )
                                Text(
                                    text = analysis.name,
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
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = CardWhite
                        )
                    )
                }
            }
        },
        containerColor = BgGray,
        bottomBar = {
            Surface(
                color = CardWhite,
                shadowElevation = 8.dp
            ) {
                Column {
                    AnimatedVisibility(
                        visible = isTyping,
                        enter = fadeIn() + slideInVertically(),
                        exit = fadeOut()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(AgroGreen)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Yapay zeka yanıt yazıyor...",
                                fontSize = 12.sp,
                                color = TextGray,
                                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                            )
                        }
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedTextField(
                            value = inputText,
                            onValueChange = { inputText = it },
                            modifier = Modifier.weight(1f),
                            placeholder = {
                                Text(
                                    text = "Soru sorun veya öneri isteyin...",
                                    color = TextGray,
                                    fontSize = 14.sp
                                )
                            },
                            shape = RoundedCornerShape(24.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = AgroGreen,
                                unfocusedBorderColor = Color(0xFFE0E0E0),
                                focusedContainerColor = CardWhite,
                                unfocusedContainerColor = CardWhite
                            ),
                            maxLines = 3
                        )

                        FloatingActionButton(
                            onClick = {
                                if (inputText.isNotBlank()) {
                                    val userMessage = ChatMessage(
                                        content = inputText,
                                        isUser = true
                                    )
                                    messages = messages + userMessage
                                    inputText = ""

                                    isTyping = true

                                    val response = generateAIResponse(inputText, analysis)

                                    coroutineScope.launch {
                                        delay(1500)
                                        messages = messages + ChatMessage(
                                            content = response,
                                            isUser = false
                                        )
                                        isTyping = false
                                    }
                                }
                            },
                            containerColor = AgroGreen,
                            contentColor = CardWhite,
                            modifier = Modifier.size(56.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Send,
                                contentDescription = "Gönder"
                            )
                        }
                    }
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            state = listState,
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(messages, key = { it.id }) { message ->
                ChatMessageBubble(message = message)
            }
        }
    }
}

@Composable
fun ChatMessageBubble(message: ChatMessage) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (message.isUser) Arrangement.End else Arrangement.Start
    ) {
        if (!message.isUser) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(AgroGreen),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "AI",
                    color = CardWhite,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
        }

        Column(
            modifier = Modifier.widthIn(max = 280.dp),
            horizontalAlignment = if (message.isUser) Alignment.End else Alignment.Start
        ) {
            Surface(
                shape = RoundedCornerShape(
                    topStart = if (message.isUser) 16.dp else 4.dp,
                    topEnd = if (message.isUser) 4.dp else 16.dp,
                    bottomStart = 16.dp,
                    bottomEnd = 16.dp
                ),
                color = if (message.isUser) AgroGreen else CardWhite,
                shadowElevation = 2.dp
            ) {
                Text(
                    text = message.content,
                    modifier = Modifier.padding(12.dp),
                    color = if (message.isUser) CardWhite else TextBlack,
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                )
            }

            Text(
                text = SimpleDateFormat("HH:mm", Locale.forLanguageTag("tr"))
                    .format(Date(message.timestamp)),
                fontSize = 10.sp,
                color = TextGray,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }

        if (message.isUser) {
            Spacer(modifier = Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF9E9E9E)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Sen",
                    color = CardWhite,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

fun generateInitialRecommendations(analysis: Analysis): String {
    val score = (analysis.score * 10).toInt().coerceIn(0, 100)

    return buildString {
        appendLine("📊 Analiz Sonuçlarınız:")
        appendLine()
        appendLine("• Verim Skoru: $score/100")
        appendLine("• Tahmini Verim: ${String.format(Locale.US, "%.2f", analysis.score)} ton/hektar")
        appendLine("• Alan: ${analysis.area}")
        appendLine()

        when {
            score >= 75 -> {
                appendLine("✅ Harika! Toprağınız çok iyi durumda.")
                appendLine()
                appendLine("🌱 Öneriler:")
                appendLine("• Mevcut gübreleme programınızı sürdürün")
                appendLine("• Düzenli sulama yapın")
                appendLine("• Haftalık bitki sağlığı kontrolü yapın")
            }
            score >= 50 -> {
                appendLine("⚠️ Toprağınız iyileştirilebilir durumda.")
                appendLine()
                appendLine("🌱 Öneriler:")
                appendLine("• Azot gübrelemesi yapılmalı")
                appendLine("• Sulama sıklığı artırılmalı")
                appendLine("• pH dengesi kontrol edilmeli")
            }
            else -> {
                appendLine("❗ Toprağınız acil müdahale gerektiriyor.")
                appendLine()
                appendLine("🌱 Acil Öneriler:")
                appendLine("• Toprak analizi yaptırın")
                appendLine("• Kompost ve organik gübre ekleyin")
                appendLine("• Uzman desteği alın")
            }
        }

        appendLine()
        appendLine("Daha detaylı bilgi için soru sorabilirsiniz! 🌾")
    }
}

fun generateAIResponse(userInput: String, analysis: Analysis): String {
    val input = userInput.lowercase()

    return when {
        input.contains("gübre") || input.contains("gübreleme") -> {
            """
            🌿 Gübreleme Önerileri:
            
            Toprağınızın mevcut durumuna göre:
            • NPK gübresi: 20-10-10 oranında
            • Uygulama: 150-200 kg/hektar
            • Dönem: Ekim öncesi ve gelişme döneminde
            
            Organik seçenekler:
            • Ahır gübresi: 3-5 ton/hektar
            • Kompost: 2-3 ton/hektar
            
            ⚠️ Mutlaka toprak analizi sonuçlarına göre ayarlayın.
            """.trimIndent()
        }
        input.contains("sulama") || input.contains("su") -> {
            """
            💧 Sulama Önerileri:
            
            Mevcut nem oranınıza göre:
            • Damla sulama sistemi önerilir
            • Sabah erken saatlerde sulayın
            • Haftada 2-3 kez düzenli sulama
            
            Su miktarı:
            • ${analysis.area} için: 30-40 m³/gün
            • Toprak tipine göre ayarlanmalı
            
            💡 Nem sensörü kullanarak optimize edebilirsiniz.
            """.trimIndent()
        }
        input.contains("hastalık") || input.contains("zararlı") -> {
            """
            🐛 Hastalık ve Zararlı Yönetimi:
            
            Önleyici tedbirler:
            • Haftalık kontrol yapın
            • Uygun bitki aralığı bırakın
            • Havalandırmaya dikkat edin
            
            Biyolojik mücadele:
            • Yararlı böcekler kullanın
            • Neem yağı spreyi
            • Sarımsak-biber karışımı
            
            🔬 Ciddi durumlarda uzman konsültasyonu alın.
            """.trimIndent()
        }
        input.contains("verim") || input.contains("üretim") -> {
            """
            📈 Verim Artırma Stratejileri:
            
            Kısa vadeli (1-2 ay):
            • Yaprak gübresi uygulaması
            • Mikro besin elementleri
            • Su yönetimi optimizasyonu
            
            Uzun vadeli (sezon):
            • Toprak ıslahı programı
            • Çeşit değişikliği değerlendirmesi
            • Ürün rotasyonu planlaması
            
            Tahmini veriminiz: ${String.format(Locale.US, "%.2f", analysis.score)} ton/hektar
            🎯 Hedef: ${String.format(Locale.US, "%.2f", analysis.score * 1.2)} ton/hektar
            """.trimIndent()
        }
        input.contains("maliyet") || input.contains("fiyat") -> {
            """
            💰 Maliyet Analizi:
            
            Tahmini sezonluk maliyetler:
            • Gübre: ~3,000-5,000 ₺/hektar
            • Sulama: ~2,000-3,000 ₺/hektar
            • İlaçlama: ~1,500-2,500 ₺/hektar
            • İşgücü: değişken
            
            Getiri tahmini:
            • ${String.format(Locale.US, "%.2f", analysis.score)} ton × piyasa fiyatı
            
            💡 Organik üretime geçerek premium fiyat alabilirsiniz.
            """.trimIndent()
        }
        input.contains("ne zaman") || input.contains("tarih") -> {
            """
            📅 Zaman Planlaması:
            
            Önümüzdeki adımlar:
            • Şimdi: Toprak hazırlığı ve gübreleme
            • 1 hafta: İlk sulama ve kontrol
            • 2 hafta: Yaprak gelişimi takibi
            • 1 ay: Ara değerlendirme
            
            Hasat zamanı:
            • Bitki tipine göre: 3-4 ay sonra
            • İklim koşullarına bağlı
            
            ⏰ Düzenli takip için uygulamamızı kullanmaya devam edin!
            """.trimIndent()
        }
        else -> {
            """
            Anladım, "${userInput}" hakkında yardımcı olmaya çalışayım.
            
            Sizin için şunları önerebilirim:
            
            🌾 Genel Öneriler:
            • Düzenli toprak analizi yaptırın
            • Bitki gelişimini takip edin
            • Su ve gübre kullanımını kayıt altına alın
            
            Daha spesifik sorular sorabilirsiniz:
            • Gübreleme hakkında
            • Sulama programı
            • Hastalık kontrolü
            • Verim artırma
            • Maliyet hesaplama
            
            Nasıl yardımcı olabilirim? 🌱
            """.trimIndent()
        }
    }
}

