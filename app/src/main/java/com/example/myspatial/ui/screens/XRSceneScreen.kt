package com.example.myspatial.ui.screens

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.myspatial.ai.AIProvider
import com.example.myspatial.data.*
import com.example.myspatial.xr.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun XRSceneScreen(
    companion: Companion,
    context: Context,
    onBack: () -> Unit
) {
    val voiceService = remember { AIProvider.getVoiceService(context) }
    val voiceProfile = remember { companion.voiceProfile }
    
    var xrState by remember { mutableStateOf(XRState(companion = companion, interactionMode = InteractionMode.VOICE)) }
    val xrCompanion = remember(companion) { XRCompanion(companion) }
    val coroutineScope = rememberCoroutineScope()
    val messages = remember { mutableStateListOf<Message>() }
    val inputText = remember { mutableStateOf("") }
    val isTyping = remember { mutableStateOf(false) }
    val gazeProgress = remember { mutableStateOf(0f) }
    val selectedMenuItem = remember { mutableStateOf<String?>(null) }
    val isListening = remember { mutableStateOf(false) }
    val showPermissionDialog = remember { mutableStateOf(false) }
    
    val GAZE_DURATION = 2000L

    val audioPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (!granted) {
            showPermissionDialog.value = true
            xrState = xrReducer(xrState, XRAction.SetInteractionMode(InteractionMode.GAZE))
        }
    }

    LaunchedEffect(Unit) {
        val hasAudioPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED
        if (!hasAudioPermission) {
            audioPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }

        messages.add(Message(
            text = xrCompanion.getGreeting(),
            type = MessageType.TEXT,
            sender = MessageSender.COMPANION,
            emotion = EmotionType.CALM
        ))
        
        coroutineScope.launch {
            voiceService.textToSpeech(xrCompanion.getGreeting(), voiceProfile)
        }
    }

    LaunchedEffect(xrState.companionAnimation) {
        while (true) {
            delay(16)
            xrCompanion.update(0.016f)
            xrState = xrReducer(xrState, XRAction.SetCompanionAnimation(xrCompanion.getCurrentAnimation()))
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFF1A1A2E))) {
        VirtualEnvironment()

        XRCompanion3D(
            xrCompanion = xrCompanion,
            onClick = {
                xrCompanion.setAnimation(CompanionAnimation.WAVING)
                xrState = xrReducer(xrState, XRAction.ToggleConversation(!xrState.showConversation))
            },
            onGazeEnter = {
                selectedMenuItem.value = "companion"
                coroutineScope.launch {
                    val startTime = System.currentTimeMillis()
                    while (selectedMenuItem.value == "companion" && System.currentTimeMillis() - startTime < GAZE_DURATION) {
                        gazeProgress.value = (System.currentTimeMillis() - startTime).toFloat() / GAZE_DURATION.toFloat()
                        delay(16)
                    }
                    if (selectedMenuItem.value == "companion" && gazeProgress.value >= 1f) {
                        xrCompanion.setAnimation(CompanionAnimation.WAVING)
                        xrState = xrReducer(xrState, XRAction.ToggleConversation(!xrState.showConversation))
                        gazeProgress.value = 0f
                    }
                }
            },
            onGazeExit = {
                if (selectedMenuItem.value == "companion") {
                    selectedMenuItem.value = null
                    gazeProgress.value = 0f
                }
            },
            gazeProgress = gazeProgress.value,
            isSelected = selectedMenuItem.value == "companion"
        )

        XRMenuPanel(
            onMenuClick = { menu ->
                when (menu) {
                    "conversation" -> xrState = xrReducer(xrState, XRAction.ToggleConversation(!xrState.showConversation))
                    "mood" -> xrState = xrReducer(xrState, XRAction.ToggleMoodPanel(!xrState.showMoodPanel))
                    "voice" -> {
                        if (xrState.interactionMode == InteractionMode.VOICE) {
                            xrState = xrReducer(xrState, XRAction.SetInteractionMode(InteractionMode.GAZE))
                        } else {
                            audioPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                            xrState = xrReducer(xrState, XRAction.SetInteractionMode(InteractionMode.VOICE))
                        }
                    }
                    "settings" -> {}
                    "exit" -> onBack()
                }
            },
            onGazeEnter = { selectedMenuItem.value = it },
            onGazeExit = { selectedMenuItem.value = null },
            selectedItem = selectedMenuItem.value,
            gazeProgress = gazeProgress.value,
            isVoiceMode = xrState.interactionMode == InteractionMode.VOICE,
            gazeDuration = GAZE_DURATION
        )

        if (xrState.interactionMode == InteractionMode.VOICE && !xrState.showConversation) {
            val voiceProfileName = when (voiceProfile) {
                is VoiceProfile.Preset -> voiceProfile.voiceTone.displayName
                is VoiceProfile.Cloned -> "${voiceProfile.displayName}的声音"
            }
            
            XRVoiceButton(
                isListening = isListening.value,
                voiceToneName = voiceProfileName,
                onStartListening = {
                    isListening.value = true
                    xrCompanion.setAnimation(CompanionAnimation.LISTENING)
                    coroutineScope.launch {
                        val result = voiceService.speechToText()
                        when (result) {
                            is com.example.myspatial.core.Result.Success -> {
                                if (result.data.isNotBlank()) {
                                    messages.add(Message(
                                        text = result.data,
                                        type = MessageType.VOICE,
                                        sender = MessageSender.USER
                                    ))
                                    inputText.value = result.data
                                    xrCompanion.setAnimation(CompanionAnimation.TALKING)
                                    isTyping.value = true
                                    delay(1500)
                                    val responses = getResponses(companion.personality)
                                    val responseText = responses[kotlin.random.Random.nextInt(responses.size)]
                                    val emotions = listOf(EmotionType.HAPPY, EmotionType.CALM, EmotionType.HAPPY, EmotionType.CALM)
                                    val emotion = emotions[kotlin.random.Random.nextInt(emotions.size)]
                                    messages.add(Message(
                                        text = responseText,
                                        type = MessageType.TEXT,
                                        sender = MessageSender.COMPANION,
                                        emotion = emotion
                                    ))
                                    voiceService.textToSpeech(responseText, voiceProfile)
                                    isTyping.value = false
                                    xrCompanion.setAnimation(CompanionAnimation.IDLE)
                                }
                            }
                            else -> {}
                        }
                        isListening.value = false
                        xrCompanion.setAnimation(CompanionAnimation.IDLE)
                    }
                }
            )
        }

        AnimatedVisibility(
            visible = xrState.showConversation,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            XRConversationPanel(
                companion = companion,
                messages = messages,
                inputText = inputText.value,
                onInputChange = { inputText.value = it },
                onSend = { text ->
                    if (text.isNotBlank()) {
                        messages.add(Message(
                            text = text,
                            type = MessageType.TEXT,
                            sender = MessageSender.USER
                        ))
                        inputText.value = ""
                        xrCompanion.setAnimation(CompanionAnimation.TALKING)
                        isTyping.value = true
                        coroutineScope.launch {
                            delay(1500)
                            val responses = getResponses(companion.personality)
                            val responseText = responses[kotlin.random.Random.nextInt(responses.size)]
                            val emotions = listOf(EmotionType.HAPPY, EmotionType.CALM, EmotionType.HAPPY, EmotionType.CALM)
                            val emotion = emotions[kotlin.random.Random.nextInt(emotions.size)]
                            messages.add(Message(
                                text = responseText,
                                type = MessageType.TEXT,
                                sender = MessageSender.COMPANION,
                                emotion = emotion
                            ))
                            voiceService.textToSpeech(responseText, voiceProfile)
                            isTyping.value = false
                            xrCompanion.setAnimation(CompanionAnimation.IDLE)
                        }
                    }
                },
                onClose = {
                    xrState = xrReducer(xrState, XRAction.ToggleConversation(false))
                },
                isTyping = isTyping.value
            )
        }

        AnimatedVisibility(
            visible = xrState.showMoodPanel,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            XRMoodPanel(
                companion = companion,
                onClose = {
                    xrState = xrReducer(xrState, XRAction.ToggleMoodPanel(false))
                },
                onMoodSelect = { emotion ->
                    messages.add(Message(
                        text = getMoodResponse(emotion, companion.name),
                        type = MessageType.TEXT,
                        sender = MessageSender.COMPANION,
                        emotion = emotion
                    ))
                    coroutineScope.launch {
                        voiceService.textToSpeech(getMoodResponse(emotion, companion.name), voiceProfile)
                    }
                    xrState = xrReducer(xrState, XRAction.ToggleMoodPanel(false))
                    xrCompanion.setAnimation(CompanionAnimation.HAPPY)
                }
            )
        }

        XRInteractionHint(interactionMode = xrState.interactionMode)

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

private fun getResponses(personality: CompanionPersonality): List<String> {
    return when (personality) {
        CompanionPersonality.GENTLE -> listOf(
            "你说得真好，我感受到了你的心意~",
            "谢谢你愿意和我分享，我会一直陪伴你的",
            "你的想法很温暖，我喜欢听你说话",
            "别担心，有我在呢，一切都会好起来的",
            "我理解你的感受，我们一起面对吧"
        )
        CompanionPersonality.ACTIVE -> listOf(
            "哇！太棒了！我也这么觉得！",
            "哈哈，这个想法好有趣！继续说继续说！",
            "耶！我们一起加油！",
            "太酷了！快跟我讲讲更多！",
            "没问题！冲鸭！"
        )
        CompanionPersonality.QUIET -> listOf(
            "嗯，我在听...",
            "原来如此...",
            "你说得很有道理",
            "我理解...",
            "谢谢你愿意告诉我"
        )
        CompanionPersonality.CURIOUS -> listOf(
            "真的吗？太神奇了！",
            "这是什么？快给我讲讲！",
            "哇！我从来没想过这个！",
            "太有趣了！还有呢？",
            "你的世界真精彩！"
        )
        CompanionPersonality.CAREFUL -> listOf(
            "我记下了，会好好记住的",
            "放心，我会认真对待的",
            "你说得很重要，我会注意的",
            "细节决定一切，你做得很好",
            "让我想想，这个需要仔细考虑"
        )
    }
}

private fun getMoodResponse(emotion: EmotionType, name: String): String {
    return when (emotion) {
        EmotionType.HAPPY -> "太好了！看到你开心我也很开心！🎉"
        EmotionType.CALM -> "平静的心情真好，让我们一起享受这一刻~"
        EmotionType.SAD -> "别难过，${name}会一直陪着你的，一切都会好起来的💝"
        EmotionType.ANGRY -> "深呼吸...冷静一下，生气对身体不好哦~"
        EmotionType.SCARED -> "别怕，${name}会保护你的！勇敢一点！"
        EmotionType.MISSING -> "我也很想念你呢！让我给你一个大大的拥抱！🤗"
        EmotionType.LONELY -> "你不孤单，${name}一直在这里陪伴着你！"
        EmotionType.CONFUSED -> "没关系，我们一起慢慢想，总会找到答案的！"
    }
}

@Composable
fun VirtualEnvironment() {
    Box(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = androidx.compose.ui.graphics.Brush.radialGradient(
                        colors = listOf(Color(0xFF3A3A5E), Color(0xFF252545), Color(0xFF1A1A30), Color(0xFF0D0D1A)),
                        center = androidx.compose.ui.geometry.Offset(0.5f, 0.3f)
                    )
                )
        )
        StarField()
        NebulaLayer()
    }
}

@Composable
fun StarField() {
    Box(modifier = Modifier.fillMaxSize()) {
        repeat(80) { index ->
            val size = (1 + kotlin.random.Random.nextInt(5)).dp
            val alpha = 0.2f + kotlin.random.Random.nextFloat() * 0.8f
            val x = kotlin.random.Random.nextFloat()
            val y = kotlin.random.Random.nextFloat()

            Box(
                modifier = Modifier
                    .size(size)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = alpha))
                    .offset((x * 400).dp, (y * 600).dp)
            )
        }
    }
}

@Composable
fun NebulaLayer() {
    Box(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .size(300.dp)
                .offset(50.dp, 100.dp)
                .clip(CircleShape)
                .background(Color(0xFF6366F1).copy(alpha = 0.15f))
        )
        Box(
            modifier = Modifier
                .size(250.dp)
                .offset(250.dp, 150.dp)
                .clip(CircleShape)
                .background(Color(0xFF8B5CF6).copy(alpha = 0.12f))
        )
        Box(
            modifier = Modifier
                .size(200.dp)
                .offset(150.dp, 300.dp)
                .clip(CircleShape)
                .background(Color(0xFFEC4899).copy(alpha = 0.1f))
        )
    }
}

@Composable
fun XRCompanion3D(
    xrCompanion: XRCompanion,
    onClick: () -> Unit,
    onGazeEnter: () -> Unit,
    onGazeExit: () -> Unit,
    gazeProgress: Float,
    isSelected: Boolean
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .padding(bottom = 120.dp)
                .size(200.dp)
                .clip(CircleShape)
                .background(
                    brush = androidx.compose.ui.graphics.Brush.radialGradient(
                        colors = listOf(
                            xrCompanion.getCharacterColor().copy(alpha = 0.95f),
                            xrCompanion.getCharacterColor().copy(alpha = 0.7f)
                        ),
                        radius = 120f
                    )
                )
                .clickable(onClick = onClick)
                .shadow(30.dp, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                xrCompanion.getCharacterIcon(),
                fontSize = 90.sp,
                modifier = Modifier.scale(xrCompanion.getAnimationScale())
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
                    .background(
                        brush = androidx.compose.ui.graphics.Brush.radialGradient(
                            colors = listOf(Color.White.copy(alpha = 0.3f), Color.White.copy(alpha = 0.1f), Color.Transparent),
                            radius = 100f,
                            center = androidx.compose.ui.geometry.Offset(0.3f, 0.3f)
                        )
                    )
            )

            if (isSelected) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                ) {
                    CircularProgressIndicator(
                        progress = gazeProgress,
                        modifier = Modifier.fillMaxSize(),
                        color = Color(0xFF6366F1),
                        strokeWidth = 5.dp
                    )
                }
            }
        }

        Box(
            modifier = Modifier
                .padding(bottom = 60.dp)
                .background(Color.Black.copy(alpha = 0.7f))
                .padding(16.dp, 10.dp)
                .clip(RoundedCornerShape(24.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(xrCompanion.companion.name, fontSize = 20.sp, color = Color.White, fontWeight = FontWeight.Bold)
        }

        if (isSelected) {
            LinearProgressIndicator(
                progress = gazeProgress,
                modifier = Modifier
                    .padding(bottom = 20.dp)
                    .width(140.dp)
                    .height(6.dp),
                color = Color(0xFF6366F1),
                backgroundColor = Color.White.copy(alpha = 0.2f)
            )
        }
    }
}

@Composable
fun BoxScope.XRMenuPanel(
    onMenuClick: (String) -> Unit,
    onGazeEnter: (String) -> Unit,
    onGazeExit: () -> Unit,
    selectedItem: String?,
    gazeProgress: Float,
    isVoiceMode: Boolean,
    gazeDuration: Long = 2000L
) {
    val coroutineScope = rememberCoroutineScope()
    
    Column(
        modifier = Modifier
            .padding(top = 60.dp, end = 20.dp)
            .align(Alignment.TopEnd),
        horizontalAlignment = Alignment.End
    ) {
        val menuItems = listOf(
            Triple("conversation", Icons.Filled.ChatBubble, "聊天"),
            Triple("voice", Icons.Filled.Mic, if (isVoiceMode) "语音开" else "语音关"),
            Triple("mood", Icons.Filled.SentimentSatisfied, "心情"),
            Triple("settings", Icons.Filled.Settings, "设置"),
            Triple("exit", Icons.Filled.ExitToApp, "退出")
        )

        menuItems.forEach { (id, icon, label) ->
            MenuButton(
                icon = icon,
                label = label,
                isSelected = selectedItem == id,
                onClick = { onMenuClick(id) },
                onGazeEnter = {
                    onGazeEnter(id)
                    coroutineScope.launch {
                        val startTime = System.currentTimeMillis()
                        while (selectedItem == id && System.currentTimeMillis() - startTime < gazeDuration) {
                            delay(16)
                        }
                        if (selectedItem == id) {
                            onMenuClick(id)
                        }
                    }
                },
                onGazeExit = onGazeExit,
                gazeProgress = if (selectedItem == id) gazeProgress else 0f
            )
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
fun MenuButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    onGazeEnter: () -> Unit,
    onGazeExit: () -> Unit,
    gazeProgress: Float
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(if (isSelected) Color(0xFF6366F1) else Color.White.copy(alpha = 0.2f))
                .clickable(onClick = onClick),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = label, tint = if (isSelected) Color.White else Color.White)
        }
        if (isSelected) {
            Text(label, fontSize = 12.sp, color = Color.White, modifier = Modifier.padding(top = 4.dp))
            LinearProgressIndicator(
                progress = gazeProgress,
                modifier = Modifier.width(40.dp).height(2.dp),
                color = Color(0xFF6366F1),
                backgroundColor = Color.White.copy(alpha = 0.3f)
            )
        }
    }
}

@Composable
fun BoxScope.XRVoiceButton(
    isListening: Boolean,
    voiceToneName: String,
    onStartListening: () -> Unit
) {
    Column(
        modifier = Modifier
            .padding(bottom = 80.dp)
            .align(Alignment.BottomCenter),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(
                    if (isListening) {
                        androidx.compose.ui.graphics.Brush.radialGradient(
                            colors = listOf(Color(0xFF69F0AE), Color(0xFF00E676)),
                            radius = 50f
                        )
                    } else {
                        androidx.compose.ui.graphics.Brush.radialGradient(
                            colors = listOf(Color(0xFFFF8A8A), Color(0xFFFF5252)),
                            radius = 50f
                        )
                    }
                )
                .clickable(onClick = onStartListening)
                .shadow(20.dp, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                if (isListening) Icons.Filled.Mic else Icons.Filled.Mic,
                contentDescription = if (isListening) "正在听..." else "点击说话",
                tint = Color.White,
                modifier = Modifier.size(32.dp)
            )
        }
        
        if (isListening) {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .padding(10.dp)
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.White.copy(alpha = 0.5f),
                    strokeWidth = 3.dp
                )
            }
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        Box(
            modifier = Modifier
                .background(Color.Black.copy(alpha = 0.6f))
                .padding(12.dp, 6.dp)
                .clip(RoundedCornerShape(20.dp))
        ) {
            Text(
                if (isListening) "🎤 正在聆听..." else "🎤 点击说话",
                fontSize = 13.sp,
                color = Color.White
            )
        }
        
        Spacer(modifier = Modifier.height(6.dp))
        
        Box(
            modifier = Modifier
                .background(Color(0xFF6366F1).copy(alpha = 0.8f))
                .padding(10.dp, 4.dp)
                .clip(RoundedCornerShape(12.dp))
        ) {
            Text(
                "${voiceToneName}",
                fontSize = 11.sp,
                color = Color.White,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun BoxScope.XRConversationPanel(
    companion: Companion,
    messages: List<Message>,
    inputText: String,
    onInputChange: (String) -> Unit,
    onSend: (String) -> Unit,
    onClose: () -> Unit,
    isTyping: Boolean
) {
    Box(
        modifier = Modifier
            .fillMaxWidth(0.85f)
            .height(500.dp)
            .align(Alignment.Center)
            .background(Color.Black.copy(alpha = 0.85f), RoundedCornerShape(24.dp))
            .padding(20.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(companion.character.getCharacterColor()),
                    contentAlignment = Alignment.Center
                ) {
                    Text(companion.character.getCharacterIcon(), fontSize = 22.sp)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(companion.name, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Spacer(modifier = Modifier.weight(1f))
                IconButton(onClick = onClose) {
                    Icon(Icons.Filled.Close, contentDescription = "Close", tint = Color.White)
                }
            }

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(messages) { message ->
                    XRMessageBubble(message = message, isUser = message.sender == MessageSender.USER)
                }
            }

            if (isTyping) {
                Row(
                    modifier = Modifier.padding(bottom = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(companion.character.getCharacterColor()),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(companion.character.getCharacterIcon(), fontSize = 14.sp)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .background(Color.White.copy(alpha = 0.1f), RoundedCornerShape(12.dp))
                            .padding(10.dp)
                    ) {
                        Row {
                            XRBoxTypingDot()
                            Spacer(modifier = Modifier.width(4.dp))
                            XRBoxTypingDot()
                            Spacer(modifier = Modifier.width(4.dp))
                            XRBoxTypingDot()
                        }
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextField(
                    value = inputText,
                    onValueChange = onInputChange,
                    placeholder = { Text("说点什么吧...", color = Color.White.copy(alpha = 0.5f)) },
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.weight(1f),
                    textStyle = TextStyle(fontSize = 15.sp, color = Color.White),
                    colors = TextFieldDefaults.textFieldColors(
                        backgroundColor = Color.White.copy(alpha = 0.1f),
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    trailingIcon = {
                        IconButton(onClick = { onSend(inputText) }) {
                            Icon(Icons.Filled.Send, contentDescription = "发送", tint = Color.White)
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun BoxScope.XRMoodPanel(
    companion: Companion,
    onClose: () -> Unit,
    onMoodSelect: (EmotionType) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth(0.85f)
            .height(400.dp)
            .align(Alignment.Center)
            .background(Color.Black.copy(alpha = 0.85f), RoundedCornerShape(24.dp))
            .padding(24.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("今天心情怎么样？", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Spacer(modifier = Modifier.weight(1f))
                IconButton(onClick = onClose) {
                    Icon(Icons.Filled.Close, contentDescription = "Close", tint = Color.White)
                }
            }

            androidx.compose.foundation.lazy.grid.LazyVerticalGrid(
                columns = androidx.compose.foundation.lazy.grid.GridCells.Fixed(4),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                EmotionType.values().forEach { emotion ->
                    item {
                        MoodOption(
                            emotion = emotion,
                            onClick = { onMoodSelect(emotion) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MoodOption(
    emotion: EmotionType,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .clickable(onClick = onClick)
            .background(Color.White.copy(alpha = 0.1f), RoundedCornerShape(16.dp))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(emotion.getEmoji(), fontSize = 32.sp)
        Spacer(modifier = Modifier.height(8.dp))
        Text(emotion.getEmotionName(), fontSize = 12.sp, color = Color.White)
    }
}

@Composable
fun XRMessageBubble(message: Message, isUser: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(if (isUser) Color(0xFF6366F1) else Color.White.copy(alpha = 0.15f))
                .padding(14.dp, 10.dp)
        ) {
            Column {
                Text(
                    message.text,
                    fontSize = 15.sp,
                    color = if (isUser) Color.White else Color.White,
                    lineHeight = 22.sp
                )
                if (message.emotion != null && !isUser) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(message.emotion.getEmoji(), fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
fun XRBoxTypingDot() {
    Box(
        modifier = Modifier
            .size(6.dp)
            .clip(CircleShape)
            .background(Color.White)
    )
}

@Composable
fun BoxScope.XRInteractionHint(interactionMode: InteractionMode) {
    Box(
        modifier = Modifier
            .padding(bottom = 30.dp)
            .align(Alignment.BottomCenter)
            .background(Color.Black.copy(alpha = 0.5f))
            .padding(12.dp, 6.dp)
    ) {
        Text(
            when (interactionMode) {
                InteractionMode.GAZE -> "👀 凝视选择 | 👆 点击确认"
                InteractionMode.HAND -> "🤚 手势交互模式"
                InteractionMode.VOICE -> "🎤 语音模式 - 点击按钮说话"
                InteractionMode.TOUCH -> "👆 触摸交互模式"
            },
            fontSize = 12.sp,
            color = Color.White.copy(alpha = 0.7f)
        )
    }
}
