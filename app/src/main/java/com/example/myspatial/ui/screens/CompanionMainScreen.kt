package com.example.myspatial.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myspatial.ai.AIProvider
import com.example.myspatial.data.*
import com.example.myspatial.domain.UseCaseProvider
import com.example.myspatial.ui.components.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun CompanionMainScreen(
    companion: Companion,
    context: android.content.Context,
    onOpenMoodDiary: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenARScene: () -> Unit,
    onOpenXRScene: () -> Unit,
    onOpenVoiceRecording: () -> Unit,
    onOpenLearningContent: () -> Unit
) {
    val messages = remember { mutableStateListOf<Message>() }
    val inputText = remember { mutableStateOf("") }
    val isTyping = remember { mutableStateOf(false) }
    val showConversation = remember { mutableStateOf(false) }
    val emotionAnalyzer = AIProvider.emotionAnalyzer
    val coroutineScope = rememberCoroutineScope()
    val scaleState = remember { mutableStateOf(1f) }
    val isRecording = remember { mutableStateOf(false) }
    val voiceService = remember { AIProvider.getVoiceService(context) }
    val dailyGreeting = remember { mutableStateOf("") }
    val showMoodCheckIn = remember { mutableStateOf(false) }
    val currentEmotion = remember { mutableStateOf<EmotionType?>(null) }

    LaunchedEffect(Unit) {
        val result = UseCaseProvider.getMessagesUseCase.execute(companion.id)
        when (result) {
            is com.example.myspatial.core.Result.Success -> {
                messages.addAll(result.data)
                if (messages.isEmpty()) {
                    val greeting = Message(
                        text = companion.getGreeting(),
                        type = MessageType.TEXT,
                        sender = MessageSender.COMPANION,
                        emotion = EmotionType.CALM
                    )
                    messages.add(greeting)
                    UseCaseProvider.receiveMessageUseCase.execute(greeting.text, companion.id, EmotionType.CALM)
                }
            }
            else -> {}
        }
    }

    LaunchedEffect(Unit) {
        while (true) {
            scaleState.value = 1f
            delay(1500)
            scaleState.value = 1.05f
            delay(200)
            scaleState.value = 1f
            delay(1500)
        }
    }

    LaunchedEffect(Unit) {
        dailyGreeting.value = generateDailyGreeting(companion)
        delay(3000)
        showMoodCheckIn.value = true
    }

    Box(modifier = Modifier.fillMaxSize().background(StarBrand.background)) {
        BackgroundDecoration()

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            MainTopBar(
                companion = companion,
                onOpenARScene = onOpenARScene,
                onOpenXRScene = onOpenXRScene,
                onOpenMoodDiary = onOpenMoodDiary,
                onOpenSettings = onOpenSettings,
                onOpenVoiceRecording = onOpenVoiceRecording,
                onOpenLearningContent = onOpenLearningContent
            )

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                androidx.compose.animation.AnimatedVisibility(
                    visible = !showConversation.value,
                    enter = fadeIn(initialAlpha = 0.3f) + scaleIn(initialScale = 0.9f),
                    exit = fadeOut(targetAlpha = 0.3f) + scaleOut(targetScale = 0.9f)
                ) {
                    CompanionDisplay(
                        companion = companion,
                        onClick = { showConversation.value = true },
                        scale = scaleState.value
                    )
                }

                androidx.compose.animation.AnimatedVisibility(
                    visible = showConversation.value,
                    enter = fadeIn() + slideInVertically(initialOffsetY = { 20 }),
                    exit = fadeOut() + slideOutVertically(targetOffsetY = { 20 })
                ) {
                    ConversationView(
                        messages = messages,
                        companion = companion,
                        onBack = { showConversation.value = false }
                    )
                }
            }

            AnimatedVisibility(
                visible = showConversation.value,
                enter = fadeIn() + slideInVertically(initialOffsetY = { 50 }),
                exit = fadeOut() + slideOutVertically(targetOffsetY = { 50 })
            ) {
                MessageInput(
                    inputText = inputText.value,
                    onInputChange = { inputText.value = it },
                    onSend = { text ->
                        if (text.isNotBlank()) {
                            val userMessage = Message(
                                text = text,
                                type = MessageType.TEXT,
                                sender = MessageSender.USER
                            )
                            messages.add(userMessage)
                            UseCaseProvider.sendMessageUseCase.execute(text, companion.id)
                            inputText.value = ""

                            isTyping.value = true
                            coroutineScope.launch {
                                val llmClient = AIProvider.getLLMClient(context)
                                val response = llmClient.generateResponse(
                                    text,
                                    companion.personality,
                                    companion.name,
                                    messages.toList()
                                )
                                when (response) {
                                    is com.example.myspatial.core.Result.Success -> {
                                        val emotion = emotionAnalyzer.analyze(response.data).getOrNull() ?: EmotionType.CALM
                                        val responseMessage = Message(
                                            text = response.data,
                                            type = MessageType.TEXT,
                                            sender = MessageSender.COMPANION,
                                            emotion = emotion
                                        )
                                        messages.add(responseMessage)
                                        UseCaseProvider.receiveMessageUseCase.execute(response.data, companion.id, emotion)
                                        voiceService.textToSpeech(response.data, companion.voiceProfile)
                                    }
                                    is com.example.myspatial.core.Result.Error -> {
                                        android.util.Log.e("CompanionMainScreen", "LLM response error: ${response.message}")
                                    }
                                    is com.example.myspatial.core.Result.Loading -> {}
                                }
                                isTyping.value = false
                            }
                        }
                    },
                    isTyping = isTyping.value,
                    onVoiceClick = {
                        if (!isRecording.value) {
                            isRecording.value = true
                            coroutineScope.launch {
                                val result = voiceService.speechToText()
                                when (result) {
                                    is com.example.myspatial.core.Result.Success -> {
                                        if (result.data.isNotBlank()) {
                                            inputText.value = result.data
                                            val userMessage = Message(
                                                text = result.data,
                                                type = MessageType.VOICE,
                                                sender = MessageSender.USER
                                            )
                                            messages.add(userMessage)
                                            UseCaseProvider.sendMessageUseCase.execute(result.data, companion.id)

                                            isTyping.value = true
                                            val llmClient = AIProvider.getLLMClient(context)
                                            val response = llmClient.generateResponse(
                                                result.data,
                                                companion.personality,
                                                companion.name,
                                                messages.toList()
                                            )
                                            when (response) {
                                                is com.example.myspatial.core.Result.Success -> {
                                                    val emotion = emotionAnalyzer.analyze(response.data).getOrNull() ?: EmotionType.CALM
                                                    val responseMessage = Message(
                                                        text = response.data,
                                                        type = MessageType.TEXT,
                                                        sender = MessageSender.COMPANION,
                                                        emotion = emotion
                                                    )
                                                    messages.add(responseMessage)
                                                    UseCaseProvider.receiveMessageUseCase.execute(response.data, companion.id, emotion)
                                                    voiceService.textToSpeech(response.data, companion.voiceProfile)
                                                }
                                                is com.example.myspatial.core.Result.Error -> {
                                                    android.util.Log.e("CompanionMainScreen", "LLM response error: ${response.message}")
                                                }
                                                is com.example.myspatial.core.Result.Loading -> {}
                                            }
                                            isTyping.value = false
                                        }
                                    }
                                    is com.example.myspatial.core.Result.Error -> {
                                        android.util.Log.e("CompanionMainScreen", "Speech recognition error: ${result.message}")
                                    }
                                    is com.example.myspatial.core.Result.Loading -> {}
                                }
                                isRecording.value = false
                            }
                        } else {
                            voiceService.stopSpeaking()
                            isRecording.value = false
                        }
                    },
                    isRecording = isRecording.value
                )
            }
        }

        if (dailyGreeting.value.isNotEmpty() && !showConversation.value) {
            AnimatedVisibility(
                visible = true,
                enter = fadeIn() + slideInVertically(initialOffsetY = { -20 }),
                exit = fadeOut() + slideOutVertically(targetOffsetY = { -20 })
            ) {
                DailyGreetingCard(dailyGreeting.value)
            }
        }

        if (showMoodCheckIn.value && !showConversation.value) {
            MoodCheckInDialog(
                onClose = { showMoodCheckIn.value = false },
                onSelectEmotion = { emotion ->
                    currentEmotion.value = emotion
                    UseCaseProvider.recordMoodUseCase.execute(
                        date = java.time.LocalDate.now(),
                        emotion = emotion,
                        intensity = 50,
                        note = ""
                    )
                }
            )
        }
    }
}

@Composable
fun MainTopBar(
    companion: Companion,
    onOpenARScene: () -> Unit,
    onOpenXRScene: () -> Unit,
    onOpenMoodDiary: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenVoiceRecording: () -> Unit,
    onOpenLearningContent: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 40.dp, start = 16.dp, end = 16.dp, bottom = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text("星伴 AI", fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF0F172A))
            Text("陪伴成长，温暖心灵", fontSize = 12.sp, color = Color(0xFF94A3B8))
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            TooltipIconButton(Icons.Filled.Camera, "AR场景", onOpenARScene, Color(0xFF6366F1))
            TooltipIconButton(Icons.Filled.ViewInAr, "XR场景", onOpenXRScene, Color(0xFF06B6D4))
            TooltipIconButton(Icons.Filled.CalendarToday, "心情日记", onOpenMoodDiary, Color(0xFF10B981))
            TooltipIconButton(Icons.Filled.Book, "学习陪伴", onOpenLearningContent, Color(0xFF8B5CF6))
            TooltipIconButton(Icons.Filled.Mic, "语音克隆", onOpenVoiceRecording, Color(0xFFEC4899))
            TooltipIconButton(Icons.Filled.Settings, "设置", onOpenSettings, Color(0xFF475569))
        }
    }
}

@Composable
fun BackgroundDecoration() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        Box(
            modifier = Modifier
                .width(400.dp)
                .height(400.dp)
                .clip(CircleShape)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            StarBrand.primary.copy(alpha = 0.08f),
                            StarBrand.secondary.copy(alpha = 0.06f),
                            Color.Transparent
                        ),
                        radius = 300f
                    )
                )
                .scale(1.5f)
        )
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomCenter
    ) {
        Box(
            modifier = Modifier
                .width(350.dp)
                .height(350.dp)
                .clip(CircleShape)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            StarBrand.tertiary.copy(alpha = 0.08f),
                            StarBrand.tertiaryLight.copy(alpha = 0.04f),
                            Color.Transparent
                        ),
                        radius = 250f
                    )
                )
                .scale(1.5f)
        )
    }
}

@Composable
fun DailyGreetingCard(greeting: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 100.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        Box(
            modifier = Modifier
                .background(
                    color = Color(0xFF0F172A).copy(alpha = 0.85f),
                    shape = RoundedCornerShape(24.dp)
                )
                .padding(20.dp, 16.dp)
                .shadow(16.dp, RoundedCornerShape(24.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(greeting, fontSize = 16.sp, color = Color.White, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
fun CompanionDisplay(
    companion: Companion,
    onClick: () -> Unit,
    scale: Float
) {
    Column(
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(280.dp)
                .clip(CircleShape)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            companion.character.getCharacterColor().copy(alpha = 1f),
                            companion.character.getCharacterColor().copy(alpha = 0.85f),
                            companion.character.getCharacterColor().copy(alpha = 0.6f),
                            companion.character.getCharacterColor().copy(alpha = 0.4f)
                        ),
                        radius = 200f
                    )
                )
                .scale(scale)
                .shadow(40.dp, CircleShape, ambientColor = companion.character.getCharacterColor().copy(alpha = 0.4f)),
            contentAlignment = Alignment.Center
        ) {
            Box(modifier = Modifier.floatUpDown(duration = 3000)) {
                Text(companion.character.getCharacterIcon(), fontSize = 120.sp)
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(Color.White.copy(alpha = 0.6f), Color.White.copy(alpha = 0.2f), Color.Transparent),
                            radius = 140f,
                            center = androidx.compose.ui.geometry.Offset(40f, 40f)
                        )
                    )
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(Color.Transparent, Color.Transparent, Color.Black.copy(alpha = 0.15f)),
                            radius = 160f,
                            center = androidx.compose.ui.geometry.Offset(220f, 220f)
                        )
                    )
            )

            for (i in 0..5) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.6f))
                        .scaleIn(delay = i * 150)
                )
            }
        }

        Spacer(modifier = Modifier.height(56.dp))

        Text(
            companion.name,
            fontSize = 52.sp,
            fontWeight = FontWeight.ExtraBold,
            color = StarBrand.textPrimary,
            style = TextStyle(letterSpacing = (-2).sp),
            modifier = Modifier.scaleIn(delay = 100)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .background(
                    brush = Brush.linearGradient(colors = listOf(Color(0xFF6366F1), Color(0xFFEC4899))),
                    shape = RoundedCornerShape(9999.dp)
                )
                .padding(24.dp, 12.dp)
                .shadow(8.dp, RoundedCornerShape(9999.dp), ambientColor = Color(0xFF6366F1).copy(alpha = 0.3f))
        ) {
            Text(
                companion.personality.getPersonalityName(),
                fontSize = 18.sp,
                color = Color.White,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 1.sp
            )
        }

        Spacer(modifier = Modifier.height(64.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("点击开始聊天~", fontSize = 20.sp, color = Color(0xFF94A3B8), fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.width(12.dp))
            Icon(Icons.Filled.ChatBubble, contentDescription = "", tint = Color(0xFF6366F1), modifier = Modifier.size(24.dp).popIn(delay = 200))
        }

        Spacer(modifier = Modifier.height(40.dp))

        Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
            FeatureCard(Icons.Filled.Favorite, "暖心陪伴", Color(0xFFEC4899))
            Spacer(modifier = Modifier.width(32.dp))
            FeatureCard(Icons.Filled.Lightbulb, "智能对话", Color(0xFF6366F1))
            Spacer(modifier = Modifier.width(32.dp))
            FeatureCard(Icons.Filled.Book, "心理辅导", Color(0xFF06B6D4))
        }
    }
}

@Composable
fun FeatureCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    color: Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.12f))
                .shadow(4.dp, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = "", tint = color, modifier = Modifier.size(24.dp))
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(label, fontSize = 13.sp, color = Color(0xFF475569), fontWeight = FontWeight.Medium)
    }
}

@Composable
fun ConversationView(
    messages: List<Message>,
    companion: Companion,
    onBack: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.85f)
            .clip(RoundedCornerShape(24.dp))
            .background(Color(0xFFFFFFFF))
            .shadow(24.dp, RoundedCornerShape(24.dp))
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(companion.character.getCharacterColor().copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(companion.character.getCharacterIcon(), fontSize = 24.sp)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(companion.name, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF0F172A))
                        Text("在线", fontSize = 12.sp, color = Color(0xFF10B981))
                    }
                }
                IconButton(onClick = onBack) {
                    Icon(Icons.Filled.KeyboardArrowDown, contentDescription = "收起", tint = Color(0xFF94A3B8))
                }
            }

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(16.dp),
                reverseLayout = true
            ) {
                items(messages.reversed()) { message ->
                    MessageBubbleNew(message, message.sender == MessageSender.USER)
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}

@Composable
fun MessageBubbleNew(message: Message, isUser: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        Box(
            modifier = Modifier
                .clip(
                    if (isUser) {
                        RoundedCornerShape(20.dp, 4.dp, 20.dp, 20.dp)
                    } else {
                        RoundedCornerShape(4.dp, 20.dp, 20.dp, 20.dp)
                    }
                )
                .background(if (isUser) Color(0xFF6366F1) else Color(0xFFFFFFFF))
                .padding(16.dp, 12.dp)
                .animateContentSize()
                .shadow(2.dp, RoundedCornerShape(20.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                message.text,
                fontSize = 15.sp,
                color = if (isUser) Color.White else Color(0xFF0F172A),
                lineHeight = 22.sp
            )
        }
    }
}

@Composable
fun MessageInput(
    inputText: String,
    onInputChange: (String) -> Unit,
    onSend: (String) -> Unit,
    isTyping: Boolean,
    onVoiceClick: () -> Unit,
    isRecording: Boolean
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFFFFFFFF))
            .shadow(8.dp, RoundedCornerShape(16.dp))
            .padding(8.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(
                onClick = onVoiceClick,
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(if (isRecording) Color(0xFFEF4444).copy(alpha = 0.15f) else Color(0xFFEC4899).copy(alpha = 0.1f))
            ) {
                Icon(
                    imageVector = if (isRecording) Icons.Filled.Stop else Icons.Filled.Mic,
                    contentDescription = if (isRecording) "停止录音" else "语音输入",
                    tint = if (isRecording) Color(0xFFEF4444) else Color(0xFFEC4899),
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFF1F5F9)),
                contentAlignment = Alignment.CenterStart
            ) {
                TextField(
                    value = inputText,
                    onValueChange = onInputChange,
                    placeholder = { Text("和${companionName()}聊天...", color = Color(0xFFCBD5E1)) },
                    modifier = Modifier.fillMaxSize(),
                    textStyle = TextStyle(color = Color(0xFF0F172A), fontSize = 15.sp),
                    colors = TextFieldDefaults.textFieldColors(
                        backgroundColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    trailingIcon = {
                        if (isTyping) {
                            Row(modifier = Modifier.padding(end = 8.dp)) {
                                repeat(3) { i ->
                                    Box(
                                        modifier = Modifier
                                            .size(6.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFF6366F1))
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                }
                            }
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(
                onClick = { if (inputText.isNotBlank()) onSend(inputText) },
                enabled = inputText.isNotBlank() && !isTyping,
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(if (inputText.isNotBlank() && !isTyping) Color(0xFF6366F1) else Color(0xFFF1F5F9))
            ) {
                Icon(Icons.Filled.Send, contentDescription = "发送", tint = if (inputText.isNotBlank() && !isTyping) Color.White else Color(0xFFCBD5E1), modifier = Modifier.size(20.dp))
            }
        }
    }
}

fun companionName(): String {
    return "星伴"
}

@Composable
fun MoodCheckInDialog(
    onClose: () -> Unit,
    onSelectEmotion: (EmotionType) -> Unit
) {
    androidx.compose.ui.window.Dialog(
        onDismissRequest = onClose,
        properties = androidx.compose.ui.window.DialogProperties(dismissOnBackPress = true)
    ) {
        Box(
            modifier = Modifier
                .width(340.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(Color(0xFFFFFFFF))
                .shadow(24.dp, RoundedCornerShape(24.dp))
        ) {
            Column(modifier = Modifier.padding(32.dp)) {
                Text("今天心情怎么样？", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A), modifier = Modifier.padding(bottom = 16.dp))

                Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()) {
                    EmotionButton(EmotionType.HAPPY, "开心", "😊", onSelectEmotion)
                    EmotionButton(EmotionType.CALM, "平静", "😌", onSelectEmotion)
                    EmotionButton(EmotionType.SAD, "难过", "😢", onSelectEmotion)
                    EmotionButton(EmotionType.ANGRY, "生气", "😠", onSelectEmotion)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()) {
                    EmotionButton(EmotionType.SCARED, "害怕", "😨", onSelectEmotion)
                    EmotionButton(EmotionType.MISSING, "想念", "🥺", onSelectEmotion)
                    EmotionButton(EmotionType.LONELY, "孤单", "🥀", onSelectEmotion)
                    EmotionButton(EmotionType.CONFUSED, "困惑", "😕", onSelectEmotion)
                }

                Spacer(modifier = Modifier.height(24.dp))

                GhostButton(
                    text = "稍后再说",
                    onClick = onClose
                )
            }
        }
    }
}

@Composable
fun EmotionButton(
    emotion: EmotionType,
    label: String,
    emoji: String,
    onSelect: (EmotionType) -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .size(70.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFF1F5F9))
            .clickable { onSelect(emotion) }
            .padding(8.dp)
    ) {
        Text(emoji, fontSize = 28.sp)
        Spacer(modifier = Modifier.height(4.dp))
        Text(label, fontSize = 12.sp, color = Color(0xFF475569), fontWeight = FontWeight.Medium)
    }
}

fun generateDailyGreeting(companion: Companion): String {
    val hour = java.time.LocalTime.now().hour

    return when {
        hour < 6 -> "${companion.name}：夜深了，要好好休息哦~"
        hour < 9 -> "${companion.name}：早上好！新的一天开始啦，今天也要加油哦！"
        hour < 12 -> "${companion.name}：上午好！学习累了就休息一下吧~"
        hour < 14 -> "${companion.name}：中午好！记得吃饭和午睡哦~"
        hour < 18 -> "${companion.name}：下午好！今天过得怎么样？"
        hour < 21 -> "${companion.name}：晚上好！今天辛苦了，和我说说今天发生了什么吧~"
        else -> "${companion.name}：夜深了，准备休息了吗？"
    }
}