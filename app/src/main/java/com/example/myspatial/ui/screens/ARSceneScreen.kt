package com.example.myspatial.ui.screens

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.myspatial.ai.AIProvider
import com.example.myspatial.ai.CompanionAI
import com.example.myspatial.ar.*
import com.example.myspatial.ui.components.*
import com.example.myspatial.data.Companion
import com.example.myspatial.data.CompanionPersonality
import com.example.myspatial.data.EmotionType
import com.example.myspatial.data.Message
import com.example.myspatial.data.MessageSender
import com.example.myspatial.data.MessageType
import com.example.myspatial.data.VoiceProfile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun ARSceneScreen(
    companion: Companion,
    context: Context,
    onBack: () -> Unit
) {
    val voiceService = remember { AIProvider.getVoiceService(context) }
    val voiceProfile = remember { companion.voiceProfile }
    val companionAI = remember { CompanionAI() }
    
    var arState by remember { mutableStateOf(ARState(companion = companion)) }
    val arCompanion = remember(companion) { ARCompanion(companion) }
    val coroutineScope = rememberCoroutineScope()
    val messages = remember { mutableStateListOf<Message>() }
    val inputText = remember { mutableStateOf("") }
    val currentArState by rememberUpdatedState(arState)
    val isTyping = remember { mutableStateOf(false) }
    val showPermissionDialog = remember { mutableStateOf(false) }
    val hasGreeted = remember { mutableStateOf(false) }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        arState = arReducer(arState, ARAction.SetCameraPermission(granted))
        if (granted) {
            arState = arReducer(arState, ARAction.StartCameraPreview(true))
        }
    }

    val audioPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        arState = arReducer(arState, ARAction.SetAudioPermission(granted))
        if (!granted) {
            showPermissionDialog.value = true
        }
    }

    LaunchedEffect(Unit) {
        val hasCameraPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
        if (hasCameraPermission) {
            arState = arReducer(arState, ARAction.SetCameraPermission(true))
            arState = arReducer(arState, ARAction.StartCameraPreview(true))
        } else {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }

        val hasAudioPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED
        if (hasAudioPermission) {
            arState = arReducer(arState, ARAction.SetAudioPermission(true))
        } else {
            audioPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }
    }

    LaunchedEffect(Unit) {
        android.util.Log.i("ARScene", "ARSceneScreen entered, waiting for permissions...")
        while (!arState.isCameraPermissionGranted || !arState.isAudioPermissionGranted) {
            android.util.Log.i("ARScene", "Waiting... camera=${arState.isCameraPermissionGranted} audio=${arState.isAudioPermissionGranted}")
            delay(500)
        }
        android.util.Log.i("ARScene", "Permissions ready. camera=${arState.isCameraPermissionGranted} audio=${arState.isAudioPermissionGranted}")

        if (!hasGreeted.value) {
            hasGreeted.value = true
            android.util.Log.i("ARScene", "Starting AI proactive greeting...")

            delay(500)

            arCompanion.setAnimation(CompanionAnimation.WAVING)

            delay(800)

            val greeting = generateGreeting(companion)
            android.util.Log.i("ARScene", "Generated greeting: $greeting")
            messages.add(Message(
                text = greeting,
                type = MessageType.TEXT,
                sender = MessageSender.COMPANION,
                emotion = EmotionType.HAPPY
            ))

            arCompanion.setAnimation(CompanionAnimation.TALKING)
            arState = arReducer(arState, ARAction.SetIsSpeaking(true))

            android.util.Log.i("ARScene", "Calling TTS for greeting...")
            val ttsResult = voiceService.textToSpeech(greeting, voiceProfile)
            android.util.Log.i("ARScene", "TTS result for greeting: $ttsResult")

            arCompanion.setAnimation(CompanionAnimation.IDLE)
            arState = arReducer(arState, ARAction.SetIsSpeaking(false))

            delay(2000)

            val followUp = getExplorationTopics().random()
            android.util.Log.i("ARScene", "Generated followUp: $followUp")
            messages.add(Message(
                text = followUp,
                type = MessageType.TEXT,
                sender = MessageSender.COMPANION,
                emotion = EmotionType.CALM
            ))

            arCompanion.setAnimation(CompanionAnimation.TALKING)
            arState = arReducer(arState, ARAction.SetIsSpeaking(true))

            android.util.Log.i("ARScene", "Calling TTS for followUp...")
            val ttsResult2 = voiceService.textToSpeech(followUp, voiceProfile)
            android.util.Log.i("ARScene", "TTS result for followUp: $ttsResult2")

            arCompanion.setAnimation(CompanionAnimation.IDLE)
            arState = arReducer(arState, ARAction.SetIsSpeaking(false))
        }
    }

    LaunchedEffect(arState.positionMode) {
        if (arState.positionMode == PositionMode.FOLLOW) {
            while (true) {
                delay(8000)
                
                if (!arState.showConversation && !arState.isCompanionSpeaking) {
                    val newX = 0.3f + (Math.random() * 0.4f).toFloat()
                    val newY = 0.5f + (Math.random() * 0.3f).toFloat()
                    
                    arState = arReducer(
                        arState,
                        ARAction.UpdateCompanionPosition(
                            CompanionPosition(x = newX, y = newY, scale = 1.0f)
                        )
                    )
                    
                    arCompanion.setAnimation(CompanionAnimation.WAVING)
                    delay(1000)
                    arCompanion.setAnimation(CompanionAnimation.IDLE)
                }
                delay(500)
            }
        }
    }

    LaunchedEffect(arState.isCompanionSpeaking) {
        if (!arState.isCompanionSpeaking && !arState.showConversation && hasGreeted.value) {
            delay(15000)
            
            if (!arState.showConversation && !arState.isCompanionSpeaking) {
                val topics = getExplorationTopics()
                val randomTopic = topics.random()
                
                messages.add(Message(
                    text = randomTopic,
                    type = MessageType.TEXT,
                    sender = MessageSender.COMPANION,
                    emotion = EmotionType.CALM
                ))
                
                arCompanion.setAnimation(CompanionAnimation.TALKING)
                arState = arReducer(arState, ARAction.SetIsSpeaking(true))
                
                voiceService.textToSpeech(randomTopic, voiceProfile)
                
                arCompanion.setAnimation(CompanionAnimation.IDLE)
                arState = arReducer(arState, ARAction.SetIsSpeaking(false))
            }
        }
    }

    LaunchedEffect(arState.companionAnimation) {
        while (true) {
            delay(16)
            arCompanion.updateAnimation(0.016f)
            arState = arReducer(arState, ARAction.SetCompanionAnimation(arCompanion.getCurrentAnimation()))
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        CameraPreview(
            isCameraPreviewStarted = arState.isCameraPreviewStarted,
            isCameraPermissionGranted = arState.isCameraPermissionGranted,
            onPermissionDenied = {
                arState = arReducer(arState, ARAction.SetErrorMessage("需要摄像头权限才能使用AR功能"))
            }
        )

        CompanionOverlay(
            arCompanion = arCompanion,
            position = arState.companionPosition,
            onClick = {
                arCompanion.setAnimation(CompanionAnimation.WAVING)
                arState = arReducer(arState, ARAction.ToggleConversation(!arState.showConversation))
            }
        )

        AnimatedVisibility(
            visible = arState.uiMode == UIMode.FULL,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            ARTopBar(
                companionName = companion.name,
                onBack = onBack,
                onMoodDiary = {},
                onSettings = {}
            )
        }

        AnimatedVisibility(
            visible = !arState.showConversation && arState.uiMode == UIMode.FULL,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            ARBottomBar(
                isRecording = arState.isRecording,
                positionMode = arState.positionMode,
                onRecordStart = {
                    if (!arState.isAudioPermissionGranted) {
                        audioPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                        return@ARBottomBar
                    }
                    arState = arReducer(arState, ARAction.ToggleRecording(true))
                    arCompanion.setAnimation(CompanionAnimation.LISTENING)
                },
                onRecordEnd = {
                    arState = arReducer(arState, ARAction.ToggleRecording(false))
                    arCompanion.setAnimation(CompanionAnimation.TALKING)
                    arState = arReducer(arState, ARAction.SetIsSpeaking(true))
                    coroutineScope.launch {
                        val result = voiceService.speechToText()
                        when (result) {
                            is com.example.myspatial.core.Result.Success -> {
                                if (result.data.isNotBlank()) {
                                    handleVoiceInput(result.data, companion, messages)
                                    inputText.value = result.data
                                    arState = arReducer(arState, ARAction.ToggleConversation(true))
                                    
                                    val llmResponse = AIProvider.getLLMClient(context).generateResponse(
                                        result.data,
                                        companion.personality,
                                        companion.name,
                                        messages.toList()
                                    )
                                    when (llmResponse) {
                                        is com.example.myspatial.core.Result.Success -> {
                                            val emotion = AIProvider.emotionAnalyzer.analyze(llmResponse.data).getOrNull() ?: EmotionType.CALM
                                            val responseMessage = Message(
                                                text = llmResponse.data,
                                                type = MessageType.TEXT,
                                                sender = MessageSender.COMPANION,
                                                emotion = emotion
                                            )
                                            messages.add(responseMessage)
                                            
                                            arCompanion.setAnimation(CompanionAnimation.TALKING)
                                            voiceService.textToSpeech(llmResponse.data, voiceProfile)
                                        }
                                        else -> {}
                                    }
                                }
                            }
                            else -> {}
                        }
                        arCompanion.setAnimation(CompanionAnimation.IDLE)
                        arState = arReducer(arState, ARAction.SetIsSpeaking(false))
                    }
                },
                onTextMode = {
                    arState = arReducer(arState, ARAction.ToggleConversation(true))
                },
                onPositionModeToggle = {
                    val newMode = if (arState.positionMode == PositionMode.FOLLOW) PositionMode.FIXED else PositionMode.FOLLOW
                    arState = arReducer(arState, ARAction.SetPositionMode(newMode))
                },
                onUIModeToggle = {
                    val newMode = if (arState.uiMode == UIMode.FULL) UIMode.SIMPLE else UIMode.FULL
                    arState = arReducer(arState, ARAction.SetUIMode(newMode))
                }
            )
        }

        AnimatedVisibility(
            visible = arState.uiMode == UIMode.SIMPLE,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            SimpleModeButton(
                onClick = {
                    arState = arReducer(arState, ARAction.SetUIMode(UIMode.FULL))
                }
            )
        }

        AnimatedVisibility(
            visible = arState.showConversation,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            ConversationOverlay(
                companion = companion,
                messages = messages,
                inputText = inputText.value,
                onInputChange = { inputText.value = it },
                onSend = { text ->
                    if (text.isNotBlank()) {
                        handleTextInput(text, companion, messages)
                        inputText.value = ""
                        arCompanion.setAnimation(CompanionAnimation.TALKING)
                        isTyping.value = true
                        coroutineScope.launch {
                            val response = AIProvider.getLLMClient(context).generateResponse(
                                text,
                                companion.personality,
                                companion.name,
                                messages.toList()
                            )
                            when (response) {
                                is com.example.myspatial.core.Result.Success -> {
                                    val emotion = AIProvider.emotionAnalyzer.analyze(response.data).getOrNull() ?: EmotionType.CALM
                                    messages.add(Message(
                                        text = response.data,
                                        type = MessageType.TEXT,
                                        sender = MessageSender.COMPANION,
                                        emotion = emotion
                                    ))
                                    voiceService.textToSpeech(response.data, voiceProfile)
                                }
                                else -> {}
                            }
                            isTyping.value = false
                            arCompanion.setAnimation(CompanionAnimation.IDLE)
                        }
                    }
                },
                onBack = {
                    arState = arReducer(arState, ARAction.ToggleConversation(false))
                },
                isTyping = isTyping.value
            )
        }

        if (arState.errorMessage != null) {
            AlertDialog(
                onDismissRequest = { arState = arReducer(arState, ARAction.SetErrorMessage(null)) },
                title = { Text("提示", fontWeight = FontWeight.Bold) },
                text = { Text(arState.errorMessage ?: "") },
                confirmButton = {
                    Button(onClick = {
                        arState = arReducer(arState, ARAction.SetErrorMessage(null))
                        cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                    }) {
                        Text("去设置")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { arState = arReducer(arState, ARAction.SetErrorMessage(null)) }) {
                        Text("取消")
                    }
                }
            )
        }

        if (showPermissionDialog.value) {
            AlertDialog(
                onDismissRequest = { showPermissionDialog.value = false },
                title = { Text("需要麦克风权限", fontWeight = FontWeight.Bold) },
                text = { Text("为了使用语音聊天功能，请授予麦克风权限") },
                confirmButton = {
                    Button(onClick = {
                        showPermissionDialog.value = false
                        audioPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                    }) {
                        Text("去设置")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showPermissionDialog.value = false }) {
                        Text("取消")
                    }
                }
            )
        }
    }
}

private suspend fun handleVoiceInput(text: String, companion: Companion, messages: MutableList<Message>) {
    withContext(Dispatchers.IO) {
        messages.add(Message(
            text = text,
            type = MessageType.VOICE,
            sender = MessageSender.USER
        ))
    }
}

private fun handleTextInput(text: String, companion: Companion, messages: MutableList<Message>) {
    messages.add(Message(
        text = text,
        type = MessageType.TEXT,
        sender = MessageSender.USER
    ))
}

private fun generateGreeting(companion: Companion): String {
    val hour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)
    val timeGreeting = when (hour) {
        in 5..11 -> "早上好呀！"
        in 12..14 -> "中午好！"
        in 15..17 -> "下午好！"
        in 18..20 -> "晚上好！"
        else -> "夜深了，你还没睡呀？"
    }
    
    return when (companion.personality) {
        CompanionPersonality.GENTLE -> timeGreeting + "我是" + companion.name + "，很高兴认识你~ 以后我会一直陪着你的！"
        CompanionPersonality.ACTIVE -> timeGreeting + "我是" + companion.name + "！超开心见到你！我们一起玩吧！"
        CompanionPersonality.QUIET -> timeGreeting + "...我是" + companion.name + "。嗯，我会在这里陪着你。"
        CompanionPersonality.CURIOUS -> timeGreeting + "我是" + companion.name + "！你是谁呀？我对你很好奇呢！"
        CompanionPersonality.CAREFUL -> timeGreeting + "你好，我是" + companion.name + "。我会认真倾听你的每一句话。"
    }
}

private fun getExplorationTopics(): List<String> {
    return listOf(
        "哇，我们来到了一个新地方！你看到了什么有趣的东西？",
        "这里看起来很棒！你想让我看看周围有什么吗？",
        "转动一下摄像头，让我看看你周围的世界吧！",
        "你看，那边有什么？我们一起去看看！",
        "这个地方感觉很特别，你觉得呢？",
        "让我看看你现在在哪里...哇，好漂亮！",
        "你想探索一下这个地方吗？我可以陪你一起！",
        "这里有什么让你觉得特别的东西吗？",
        "我看到了一些有趣的东西，你想听听吗？",
        "让我们一起看看周围有什么好玩的！",
        "你最喜欢这里的什么地方呀？",
        "这个房间看起来很温馨，你经常在这里吗？",
        "窗外的风景好美啊，你想出去走走吗？",
        "你今天想去哪里玩？我陪你一起去！",
        "这里有很多有趣的东西，我们一起发现吧！",
        "你能告诉我这个地方的故事吗？",
        "我觉得这里很适合聊天，你觉得呢？",
        "让我仔细看看周围...哇，发现了一个小秘密！",
        "你想不想玩一个游戏？我们来找找看有什么颜色！",
        "这个世界真奇妙，你想和我一起探索吗？"
    )
}

@Composable
fun CameraPreview(
    isCameraPreviewStarted: Boolean,
    isCameraPermissionGranted: Boolean,
    onPermissionDenied: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        if (!isCameraPermissionGranted) {
            Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.8f))) {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(Icons.Filled.CameraAlt, contentDescription = null, modifier = Modifier.size(80.dp), tint = Color.White.copy(alpha = 0.7f))
                    Spacer(modifier = Modifier.height(20.dp))
                    Text("正在请求摄像头权限...", fontSize = 20.sp, color = Color.White.copy(alpha = 0.9f))
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("请在弹出的权限请求中选择允许", fontSize = 14.sp, color = Color.White.copy(alpha = 0.6f))
                }
            }
        } else if (!isCameraPreviewStarted) {
            Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.8f))) {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(Icons.Filled.CameraAlt, contentDescription = null, modifier = Modifier.size(80.dp), tint = Color.White.copy(alpha = 0.7f))
                    Spacer(modifier = Modifier.height(20.dp))
                    Text("正在启动摄像头...", fontSize = 20.sp, color = Color.White.copy(alpha = 0.9f))
                }
            }
        } else {
            Box(
            modifier = Modifier.fillMaxSize().background(Color(0xFF0A0A0A))
        ) {
            Box(
                modifier = Modifier.fillMaxSize().background(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0xFF1A1A1A), Color(0xFF050505)),
                        radius = 800f,
                        center = androidx.compose.ui.geometry.Offset(0.5f, 0.5f)
                    )
                ),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Filled.Camera, contentDescription = null, modifier = Modifier.size(72.dp).fadeIn(delay = 300), tint = Color(0xFF444444))
                    Spacer(modifier = Modifier.height(24.dp))
                    Text("摄像头预览区域", fontSize = 18.sp, color = Color(0xFF777777), modifier = Modifier.fadeIn(delay = 500))
                    Text("(在真实设备上显示摄像头画面)", fontSize = 14.sp, color = Color(0xFF555555), modifier = Modifier.fadeIn(delay = 700))
                    Spacer(modifier = Modifier.height(32.dp))
                    Row(horizontalArrangement = Arrangement.Center) {
                        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(StarBrand.primary).pulse(duration = 1000))
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(StarBrand.secondary).pulse(duration = 1200))
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(StarBrand.tertiary).pulse(duration = 1400))
                    }
                }
            }
            
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(Color.Black.copy(alpha = 0.4f), Color.Black.copy(alpha = 0.1f), Color.Transparent, Color.Black.copy(alpha = 0.2f), Color.Black.copy(alpha = 0.5f)),
                            startY = 0f,
                            endY = Float.POSITIVE_INFINITY
                        )
                    )
            )
            
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(2.dp)
                        .background(StarBrand.primary.copy(alpha = 0.15f))
                        .padding(2.dp)
                        .background(Color(0xFF0A0A0A))
                )
            }
            
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White.copy(alpha = 0.05f))
                        .padding(1.dp)
                        .background(Color(0xFF0A0A0A))
                )
            }
        }
        }
    }
}

@Composable
fun CompanionOverlay(
    arCompanion: ARCompanion,
    position: CompanionPosition,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomCenter
    ) {
        Box(
            modifier = Modifier
                .padding(bottom = 160.dp)
                .size(200.dp)
                .clip(CircleShape)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            arCompanion.getCharacterColor().copy(alpha = 0.95f),
                            arCompanion.getCharacterColor().copy(alpha = 0.7f)
                        ),
                        radius = 120f
                    )
                )
                .clickable(onClick = onClick)
                .shadow(30.dp, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                arCompanion.getCharacterIcon(),
                fontSize = 90.sp,
                modifier = Modifier
                    .scale(arCompanion.getAnimationScale())
                    .rotate(arCompanion.getAnimationRotation())
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(Color.White.copy(alpha = 0.3f), Color.White.copy(alpha = 0.1f), Color.Transparent),
                            radius = 100f,
                            center = androidx.compose.ui.geometry.Offset(0.3f, 0.3f)
                        )
                    )
            )
        }
        Box(
            modifier = Modifier
                .padding(bottom = 80.dp)
                .background(StarBrand.surface.copy(alpha = 0.95f))
                .padding(20.dp, 12.dp)
                .clip(StarShapes.large)
                .shadow(12.dp, StarShapes.large),
            contentAlignment = Alignment.Center
        ) {
            Text(arCompanion.getGreeting(), fontSize = 16.sp, color = StarBrand.textPrimary, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
fun ARTopBar(
    companionName: String,
    onBack: () -> Unit,
    onMoodDiary: () -> Unit,
    onSettings: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 40.dp, start = 16.dp, end = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        TooltipIconButton(
            icon = Icons.Filled.ArrowBack,
            contentDescription = "返回",
            onClick = onBack,
            tint = Color(0xFF475569)
        )
        Box(
            modifier = Modifier
                .background(Color(0xFFFFFFFF).copy(alpha = 0.9f))
                .padding(12.dp, 8.dp)
                .clip(RoundedCornerShape(12.dp))
                .shadow(4.dp, RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text("星伴 AI", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
        }
        Row {
            TooltipIconButton(
                icon = Icons.Filled.CalendarToday,
                contentDescription = "心情日记",
                onClick = onMoodDiary,
                tint = StarBrand.success
            )
            TooltipIconButton(
                icon = Icons.Filled.Settings,
                contentDescription = "设置",
                onClick = onSettings,
                tint = StarBrand.textSecondary
            )
        }
    }
}

@Composable
fun ARBottomBar(
    isRecording: Boolean,
    positionMode: PositionMode,
    onRecordStart: () -> Unit,
    onRecordEnd: () -> Unit,
    onTextMode: () -> Unit,
    onPositionModeToggle: () -> Unit,
    onUIModeToggle: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 40.dp, start = 24.dp, end = 24.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        TooltipBottomButton(
            tooltip = if (positionMode == PositionMode.FOLLOW) "跟随模式" else "固定模式",
            onClick = onPositionModeToggle
        ) {
            val icon = if (positionMode == PositionMode.FOLLOW) Icons.Filled.Favorite else Icons.Filled.PinDrop
            val tint = if (positionMode == PositionMode.FOLLOW) StarBrand.secondary else StarBrand.primary
            Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(24.dp))
        }

        TooltipBottomButton(
            tooltip = "文字模式",
            onClick = onTextMode
        ) {
            Icon(Icons.Filled.Message, contentDescription = null, tint = StarBrand.success, modifier = Modifier.size(24.dp))
        }

        if (!isRecording) {
            TooltipBottomButton(
                tooltip = "按住说话",
                onClick = onRecordStart,
                isLarge = true,
                gradientColors = listOf(StarBrand.warning, StarBrand.accentOrange)
            ) {
                Icon(Icons.Filled.Mic, contentDescription = null, tint = Color.White, modifier = Modifier.size(36.dp))
            }
        } else {
            TooltipBottomButton(
                tooltip = "停止录音",
                onClick = onRecordEnd,
                isLarge = true,
                gradientColors = listOf(StarBrand.success, StarBrand.secondaryDark)
            ) {
                Icon(Icons.Filled.Stop, contentDescription = null, tint = Color.White, modifier = Modifier.size(36.dp))
            }
        }

        TooltipBottomButton(
            tooltip = "简洁模式",
            onClick = onUIModeToggle
        ) {
            Icon(Icons.Filled.Minimize, contentDescription = null, tint = StarBrand.textSecondary, modifier = Modifier.size(24.dp))
        }

        TooltipBottomButton(
            tooltip = "拍照",
            onClick = {}
        ) {
            Icon(Icons.Filled.Camera, contentDescription = null, tint = StarBrand.tertiary, modifier = Modifier.size(24.dp))
        }
    }
}

@Composable
fun SimpleModeButton(
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(Color(0xFFFFFFFF).copy(alpha = 0.95f))
                .clickable(onClick = onClick)
                .shadow(12.dp, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Filled.Menu, contentDescription = "展开菜单", tint = Color(0xFF475569), modifier = Modifier.size(24.dp))
        }
    }
}

@Composable
fun ConversationOverlay(
    companion: Companion,
    messages: List<Message>,
    inputText: String,
    onInputChange: (String) -> Unit,
    onSend: (String) -> Unit,
    onBack: () -> Unit,
    isTyping: Boolean
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(420.dp)
            .background(StarBrand.background)
            .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
            .shadow(20.dp, RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Filled.ArrowDownward, contentDescription = "收起", tint = Color(0xFF6366F1))
                }
                Spacer(modifier = Modifier.width(16.dp))
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(companion.character.getCharacterColor()),
                    contentAlignment = Alignment.Center
                ) {
                    Text(companion.name.first().toString(), fontSize = 22.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(companion.name, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = StarBrand.textPrimary)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(StarBrand.success))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("在线", fontSize = 12.sp, color = StarBrand.success)
                    }
                }
            }

            LazyColumn(
                modifier = Modifier.weight(1f).padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(messages) { message ->
                    MessageBubble(message = message, isUser = message.sender == MessageSender.USER)
                }
            }

            if (isTyping) {
                Row(
                    modifier = Modifier.padding(start = 16.dp, bottom = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(companion.character.getCharacterColor()),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(companion.name.first().toString(), fontSize = 16.sp)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Box(
                        modifier = Modifier
                            .background(Color(0xFFFFFFFF), RoundedCornerShape(12.dp))
                            .padding(14.dp)
                    ) {
                        Row {
                            TypingDot()
                            Spacer(modifier = Modifier.width(6.dp))
                            TypingDot()
                            Spacer(modifier = Modifier.width(6.dp))
                            TypingDot()
                        }
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextField(
                    value = inputText,
                    onValueChange = onInputChange,
                    placeholder = { Text("说点什么吧...", color = Color(0xFFCBD5E1)) },
                    shape = RoundedCornerShape(9999.dp),
                    modifier = Modifier.weight(1f),
                    textStyle = TextStyle(fontSize = 16.sp, color = Color(0xFF0F172A)),
                    trailingIcon = {
                            IconButton(onClick = { onSend(inputText) }) {
                                Icon(Icons.Filled.Send, contentDescription = "发送", tint = Color(0xFF6366F1))
                            }
                        }
                )
            }
        }
    }
}

@Composable
fun TypingDot() {
    Box(
        modifier = Modifier
            .size(8.dp)
            .clip(CircleShape)
            .background(Color(0xFF6366F1))
    )
}

@Composable
fun MessageBubble(message: Message, isUser: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(if (isUser) Color(0xFF6366F1) else Color(0xFFFFFFFF))
                .padding(14.dp, 12.dp)
                .animateContentSize()
                .shadow(2.dp, RoundedCornerShape(16.dp))
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

