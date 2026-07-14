package com.example.myspatial.ui.screens

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.media.MediaPlayer
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.myspatial.data.ParentalRelationship
import com.example.myspatial.data.VoiceProfile
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException

sealed class RecordingStep {
    object SelectRelationship : RecordingStep()
    object Recording : RecordingStep()
    object Preview : RecordingStep()
    object Confirmation : RecordingStep()
    object Processing : RecordingStep()
    object Completed : RecordingStep()
}

@Composable
fun VoiceRecordingScreen(
    onComplete: (VoiceProfile.Cloned) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    
    var currentStep by remember { mutableStateOf<RecordingStep>(RecordingStep.SelectRelationship) }
    var selectedRelationship by remember { mutableStateOf<ParentalRelationship?>(null) }
    var isRecording by remember { mutableStateOf(false) }
    var recordingDuration by remember { mutableStateOf(0) }
    var audioFile by remember { mutableStateOf<File?>(null) }
    var isPlaying by remember { mutableStateOf(false) }
    var processingProgress by remember { mutableStateOf(0) }
    var clonedVoice by remember { mutableStateOf<VoiceProfile.Cloned?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    var mediaRecorder: MediaRecorder? = null
    var mediaPlayer: MediaPlayer? = null

    val hasAudioPermission = ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.RECORD_AUDIO
    ) == PackageManager.PERMISSION_GRANTED

    fun startRecording(context: Context) {
        if (!hasAudioPermission) return

        val fileName = "voice_sample_${System.currentTimeMillis()}.mp4"
        val file = File(context.cacheDir, fileName)

        mediaRecorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setOutputFile(file.absolutePath)

            try {
                prepare()
                start()
                isRecording = true
                
                coroutineScope.launch {
                    while (isRecording) {
                        kotlinx.coroutines.delay(1000)
                        recordingDuration++
                    }
                }
            } catch (e: IOException) {
                errorMessage = "录音失败: ${e.message}"
                isRecording = false
            }
        }

        coroutineScope.launch {
            while (isRecording) {
                kotlinx.coroutines.delay(100)
            }
            audioFile = file
            recordingDuration = 0
            currentStep = RecordingStep.Preview
        }
    }

    LaunchedEffect(currentStep) {
        if (currentStep == RecordingStep.Recording && !isRecording) {
            startRecording(context)
        }
    }

    fun stopRecording() {
        mediaRecorder?.apply {
            try {
                stop()
                release()
            } catch (e: Exception) {}
        }
        mediaRecorder = null
        isRecording = false
    }

    fun playRecording() {
        audioFile?.let { file ->
            mediaPlayer = MediaPlayer().apply {
                setDataSource(file.absolutePath)
                prepare()
                start()
                isPlaying = true

                setOnCompletionListener {
                    isPlaying = false
                    release()
                    mediaPlayer = null
                }
            }
        }
    }

    fun stopPlaying() {
        mediaPlayer?.apply {
            stop()
            release()
        }
        mediaPlayer = null
        isPlaying = false
    }

    fun startCloning() {
        currentStep = RecordingStep.Processing
        processingProgress = 0

        coroutineScope.launch {
            repeat(10) {
                kotlinx.coroutines.delay(200)
                processingProgress += 10
            }

            audioFile?.let { file ->
                clonedVoice = VoiceProfile.Cloned(
                    cloneId = "cloned_voice_${System.currentTimeMillis()}",
                    displayName = selectedRelationship?.getDisplayName() ?: "亲人",
                    relationship = selectedRelationship ?: ParentalRelationship.MOTHER,
                    audioSamplePath = file.absolutePath,
                    createdAt = System.currentTimeMillis()
                )
                currentStep = RecordingStep.Completed
            } ?: run {
                errorMessage = "音频文件不存在"
                currentStep = RecordingStep.Preview
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("录制父母声音") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "返回")
                    }
                },
                backgroundColor = Color(0xFF667EEA)
            )
        },
        content = { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .background(Color(0xFFF5F7FA)),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                errorMessage?.let {
                    Text(
                        text = it,
                        color = Color.Red,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }

                when (currentStep) {
                    is RecordingStep.SelectRelationship -> {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                Icons.Filled.Favorite,
                                contentDescription = null,
                                modifier = Modifier.size(80.dp),
                                tint = Color(0xFF667EEA)
                            )
                            Text(
                                text = "请选择录制的亲人关系",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(top = 24.dp, bottom = 32.dp)
                            )

                            LazyColumn(
                                modifier = Modifier.fillMaxWidth(0.8f),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                ParentalRelationship.values().forEach { relationship ->
                                    item {
                                        Card(
                                            modifier = Modifier.fillMaxWidth(),
                                            shape = RoundedCornerShape(12.dp),
                                            elevation = 4.dp,
                                            backgroundColor = if (selectedRelationship == relationship) {
                                                Color(0xFF667EEA)
                                            } else {
                                                Color.White
                                            }
                                        ) {
                                            Button(
                                                onClick = { selectedRelationship = relationship },
                                                modifier = Modifier.fillMaxWidth(),
                                                colors = ButtonDefaults.buttonColors(
                                                    backgroundColor = Color.Transparent
                                                )
                                            ) {
                                                Text(
                                                    text = relationship.getDisplayName(),
                                                    fontSize = 18.sp,
                                                    color = if (selectedRelationship == relationship) {
                                                        Color.White
                                                    } else {
                                                        Color.Black
                                                    }
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            Button(
                                onClick = {
                                    if (selectedRelationship != null) {
                                        if (!hasAudioPermission) {
                                            val activity = context as? android.app.Activity
                                            activity?.requestPermissions(
                                                arrayOf(Manifest.permission.RECORD_AUDIO),
                                                1001
                                            )
                                        } else {
                                            currentStep = RecordingStep.Recording
                                        }
                                    }
                                },
                                modifier = Modifier.padding(top = 32.dp),
                                enabled = selectedRelationship != null,
                                colors = ButtonDefaults.buttonColors(
                                    backgroundColor = Color(0xFF667EEA),
                                    disabledBackgroundColor = Color.Gray
                                )
                            ) {
                                Text("下一步", color = Color.White, fontSize = 18.sp)
                            }
                        }
                    }

                    is RecordingStep.Recording -> {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "正在录制...",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(bottom = 32.dp)
                            )

                            Text(
                                text = String.format("%02d:%02d", recordingDuration / 60, recordingDuration % 60),
                                fontSize = 36.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(bottom = 48.dp)
                            )

                            Button(
                                onClick = {
                                    stopRecording()
                                    currentStep = RecordingStep.Preview
                                },
                                modifier = Modifier
                                    .size(120.dp)
                                    .clip(CircleShape),
                                colors = ButtonDefaults.buttonColors(
                                    backgroundColor = Color(0xFFFF6B6B)
                                )
                            ) {
                                Icon(
                                    Icons.Filled.Stop,
                                    contentDescription = "停止录制",
                                    modifier = Modifier.size(48.dp),
                                    tint = Color.White
                                )
                            }

                            Text(
                                text = "请说一段话，让AI学习您的声音",
                                fontSize = 14.sp,
                                color = Color.Gray,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(32.dp, 32.dp)
                            )
                        }
                    }

                    is RecordingStep.Preview -> {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "录制完成",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(bottom = 32.dp)
                            )

                            Button(
                                onClick = {
                                    if (isPlaying) {
                                        stopPlaying()
                                    } else {
                                        playRecording()
                                    }
                                },
                                modifier = Modifier
                                    .size(100.dp)
                                    .clip(CircleShape),
                                colors = ButtonDefaults.buttonColors(
                                    backgroundColor = if (isPlaying) Color(0xFFFF6B6B) else Color(0xFF667EEA)
                                )
                            ) {
                                Icon(
                                    if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                                    contentDescription = if (isPlaying) "暂停" else "播放",
                                    modifier = Modifier.size(40.dp),
                                    tint = Color.White
                                )
                            }

                            Text(
                                text = String.format("%02d:%02d", recordingDuration / 60, recordingDuration % 60),
                                fontSize = 18.sp,
                                color = Color.Gray,
                                modifier = Modifier.padding(top = 16.dp)
                            )

                            Row(
                                modifier = Modifier.padding(top = 32.dp),
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Button(
                                    onClick = {
                                        currentStep = RecordingStep.Recording
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        backgroundColor = Color.Gray
                                    )
                                ) {
                                    Text("重新录制", color = Color.White)
                                }

                                Button(
                                    onClick = {
                                        currentStep = RecordingStep.Confirmation
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        backgroundColor = Color(0xFF667EEA)
                                    )
                                ) {
                                    Text("下一步", color = Color.White)
                                }
                            }
                        }
                    }

                    is RecordingStep.Confirmation -> {
                        Column(
                            modifier = Modifier.fillMaxWidth(0.8f),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                Icons.Filled.Shield,
                                contentDescription = null,
                                modifier = Modifier.size(64.dp),
                                tint = Color(0xFF667EEA)
                            )
                            Text(
                                text = "隐私确认",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(top = 24.dp, bottom = 16.dp)
                            )
                            Text(
                                text = "您正在录制${selectedRelationship?.getDisplayName()}的声音用于AI克隆。\n\n我们承诺：\n1. 仅用于为您提供个性化陪伴服务\n2. 不会分享给任何第三方\n3. 您可以随时删除已克隆的声音",
                                fontSize = 16.sp,
                                color = Color.Gray,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(bottom = 32.dp)
                            )

                            Button(
                                onClick = {
                                    startCloning()
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(
                                    backgroundColor = Color(0xFF667EEA)
                                )
                            ) {
                                Text("确认并克隆", color = Color.White, fontSize = 18.sp)
                            }

                            Button(
                                onClick = {
                                    currentStep = RecordingStep.Preview
                                },
                                colors = ButtonDefaults.buttonColors(
                                    backgroundColor = Color.Transparent
                                )
                            ) {
                                Text("返回修改", color = Color.Gray)
                            }
                        }
                    }

                    is RecordingStep.Processing -> {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(
                                progress = processingProgress / 100f,
                                modifier = Modifier.size(80.dp),
                                strokeWidth = 8.dp,
                                color = Color(0xFF667EEA)
                            )
                            Text(
                                text = "正在克隆声音...",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(top = 24.dp)
                            )
                            Text(
                                text = "$processingProgress%",
                                fontSize = 24.sp,
                                color = Color(0xFF667EEA),
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                    }

                    is RecordingStep.Completed -> {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.Filled.CheckCircle,
                                contentDescription = null,
                                modifier = Modifier.size(80.dp),
                                tint = Color(0xFF52C41A)
                            )
                            Text(
                                text = "声音克隆成功！",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(top = 24.dp)
                            )
                            Text(
                                text = "现在${selectedRelationship?.getDisplayName()}可以一直陪伴你了",
                                fontSize = 16.sp,
                                color = Color.Gray,
                                modifier = Modifier.padding(top = 8.dp, bottom = 32.dp)
                            )

                            Button(
                                onClick = {
                                    clonedVoice?.let {
                                        onComplete(it)
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(
                                    backgroundColor = Color(0xFF667EEA)
                                )
                            ) {
                                Text("开始使用", color = Color.White, fontSize = 18.sp)
                            }
                        }
                    }
                }
            }
        }
    )
}