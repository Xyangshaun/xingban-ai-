package com.example.myspatial.ai

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Build
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.myspatial.core.Result
import com.example.myspatial.data.VoiceProfile
import com.example.myspatial.data.VoiceTone
import kotlinx.coroutines.*
import java.util.*
import kotlin.coroutines.resume

class AndroidVoiceService(private val context: Context) : VoiceService, RecognitionListener {

    private var speechRecognizer: SpeechRecognizer? = null
    private var textToSpeech: TextToSpeech? = null
    private var recognitionJob: CompletableJob? = null
    private var currentResult: String? = null
    private var partialResult: String? = null
    private val clones = mutableListOf<VoiceProfile.Cloned>()
    private var cloneIdCounter = 0
    private var ttsJob: Job? = null
    private var isTtsSpeaking = false

    private val sherpaTts = SherpaTtsEngine(context)
    private val sherpaAsr = SherpaAsrEngine(context)
    private var systemTtsReady = false
    private var asrRecordingJob: Job? = null

    init {
        initTextToSpeech()
        initSpeechRecognizer()
        CoroutineScope(Dispatchers.IO).launch {
            initSherpaTts()
        }
        CoroutineScope(Dispatchers.IO).launch {
            initSherpaAsr()
        }
    }

    private suspend fun initSherpaTts() {
        try {
            android.util.Log.i("VoiceService", "Sherpa-onnx TTS init started...")
            val success = sherpaTts.init()
            android.util.Log.i("VoiceService", "Sherpa-onnx TTS init result: $success")
        } catch (e: Throwable) {
            android.util.Log.e("VoiceService", "Sherpa-onnx TTS init crashed: ${e.message}", e)
        }
    }

    private suspend fun initSherpaAsr() {
        try {
            android.util.Log.i("VoiceService", "Sherpa-onnx ASR init started...")
            val success = sherpaAsr.init()
            android.util.Log.i("VoiceService", "Sherpa-onnx ASR init result: $success")
        } catch (e: Throwable) {
            android.util.Log.e("VoiceService", "Sherpa-onnx ASR init crashed: ${e.message}", e)
        }
    }

    private fun initTextToSpeech() {
        android.util.Log.i("VoiceService", "Initializing TTS...")
        textToSpeech = TextToSpeech(context) { status ->
            android.util.Log.i("VoiceService", "TTS init status: $status (SUCCESS=${TextToSpeech.SUCCESS})")
            if (status == TextToSpeech.SUCCESS) {
                textToSpeech?.language = Locale.CHINA
                android.util.Log.i("VoiceService", "TTS language set to: ${textToSpeech?.language}")
                textToSpeech?.setSpeechRate(1.0f)

                val engines = textToSpeech?.engines
                android.util.Log.i("VoiceService", "Available TTS engines: ${engines?.size ?: 0}")
                engines?.forEach { engine ->
                    android.util.Log.i("VoiceService", "  Engine: ${engine.name} - ${engine.label}")
                }

                textToSpeech?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                    override fun onStart(utteranceId: String?) {
                        android.util.Log.i("VoiceService", "TTS onStart: $utteranceId")
                        isTtsSpeaking = true
                    }

                    override fun onDone(utteranceId: String?) {
                        android.util.Log.i("VoiceService", "TTS onDone: $utteranceId")
                        isTtsSpeaking = false
                    }

                    override fun onError(utteranceId: String?) {
                        android.util.Log.e("VoiceService", "TTS onError: $utteranceId")
                        isTtsSpeaking = false
                    }
                })
                systemTtsReady = true
                android.util.Log.i("VoiceService", "TTS initialized successfully (systemTtsReady=true)")
            } else {
                systemTtsReady = false
                android.util.Log.e("VoiceService", "TTS init FAILED! status=$status - No TTS engine available on this device. Will use sherpa-onnx instead.")
            }
        }
    }

    private fun initSpeechRecognizer() {
        if (SpeechRecognizer.isRecognitionAvailable(context)) {
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)
            speechRecognizer?.setRecognitionListener(this)
        }
    }

    override suspend fun speechToText(): Result<String> {
        if (!hasRecordAudioPermission()) {
            return Result.Error(
                SecurityException("麦克风权限未授予"),
                "请先授予麦克风权限"
            )
        }

        if (sherpaAsr.isReady()) {
            android.util.Log.i("VoiceService", "Sherpa ASR initialized, but will use system SpeechRecognizer for recognition")
        }

        if (speechRecognizer == null) {
            return Result.Error(
                UnsupportedOperationException("设备不支持语音识别"),
                "设备不支持语音识别"
            )
        }

        return withContext(Dispatchers.Main) {
            recognitionJob = Job().let { job ->
                object : CompletableJob by job {}
            }

            try {
                val intent = RecognizerIntent.getVoiceDetailsIntent(context).apply {
                    putExtra(
                        RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                        RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
                    )
                    putExtra(RecognizerIntent.EXTRA_LANGUAGE, "zh-CN")
                    putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
                    putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
                }

                currentResult = null
                partialResult = null
                speechRecognizer?.startListening(intent)

                withTimeout(15000L) {
                    recognitionJob?.join()
                }

                currentResult?.let {
                    Result.Success(it)
                } ?: partialResult?.let {
                    Result.Success(it)
                } ?: Result.Error(Exception("未识别到语音"), "未识别到语音")

            } catch (e: TimeoutCancellationException) {
                speechRecognizer?.stopListening()
                partialResult?.let {
                    Result.Success(it)
                } ?: Result.Error(e, "语音识别超时")
            } catch (e: Exception) {
                Result.Error(e, "语音识别失败: ${e.message}")
            } finally {
                recognitionJob?.cancel()
                recognitionJob = null
            }
        }
    }

    override suspend fun textToSpeech(text: String, voiceProfile: VoiceProfile): Result<Unit> {
        return when (voiceProfile) {
            is VoiceProfile.Preset -> textToSpeech(text, voiceProfile.voiceTone)
            is VoiceProfile.Cloned -> {
                textToSpeech(text, VoiceTone.GENTLE_WARM)
            }
        }
    }

    override suspend fun textToSpeech(text: String, voiceTone: VoiceTone): Result<Unit> {
        android.util.Log.i("VoiceService", "textToSpeech called: '${text.take(50)}...' tone=$voiceTone")

        stopSpeaking()

        // 优先使用 sherpa-onnx 离线TTS（不依赖系统引擎，质量更稳定）
        if (sherpaTts.isReady()) {
            android.util.Log.i("VoiceService", "Using sherpa-onnx TTS engine")
            return try {
                isTtsSpeaking = true
                sherpaTts.setSpeed(voiceTone.speed.coerceIn(0.5f, 2.0f))
                sherpaTts.setPitch(voiceTone.pitch.coerceIn(0.5f, 2.0f))
                sherpaTts.setVolume(1.0f)
                
                val success = sherpaTts.speak(text)
                isTtsSpeaking = false
                if (success) {
                    Result.Success(Unit)
                } else {
                    Result.Error(Exception("sherpa-onnx TTS失败"), "语音合成失败")
                }
            } catch (e: Exception) {
                isTtsSpeaking = false
                android.util.Log.e("VoiceService", "sherpa-onnx TTS error: ${e.message}", e)
                Result.Error(e, "语音合成失败: ${e.message}")
            }
        }

        // 后备：使用系统TTS引擎
        if (systemTtsReady && textToSpeech != null) {
            android.util.Log.i("VoiceService", "Using system TTS engine")
            return withContext(Dispatchers.Main) {
                textToSpeech?.apply {
                    setPitch(voiceTone.pitch)
                    setSpeechRate(voiceTone.speed)
                }

                val segments = splitTextToSegments(text)
                android.util.Log.i("VoiceService", "Split into ${segments.size} segments")

                for ((index, segment) in segments.withIndex()) {
                    val utteranceId = "segment_${index}_${System.currentTimeMillis()}"

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        val params = android.os.Bundle()
                        params.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, utteranceId)
                        val result = textToSpeech?.speak(segment, TextToSpeech.QUEUE_ADD, params, utteranceId)
                        android.util.Log.i("VoiceService", "speak seg[$index]: '${segment.take(30)}' result=$result")
                    } else {
                        @Suppress("DEPRECATION")
                        textToSpeech?.speak(segment, TextToSpeech.QUEUE_ADD, null)
                    }

                    delay(200)
                }

                Result.Success(Unit)
            }
        }

        android.util.Log.e("VoiceService", "No TTS engine available! sherpaReady=${sherpaTts.isReady()}, systemReady=$systemTtsReady")
        return Result.Error(Exception("无可用TTS引擎"), "语音合成不可用：请安装TTS引擎或推送sherpa-onnx模型")
    }

    fun isTtsAvailable(): Boolean {
        return sherpaTts.isReady() || systemTtsReady
    }

    private fun splitTextToSegments(text: String): List<String> {
        val segments = mutableListOf<String>()
        val maxLength = 30

        if (text.length <= maxLength) {
            segments.add(text)
            return segments
        }

        var currentSegment = StringBuilder()
        val sentences = text.split("。", "！", "？", "；", "，")

        for (sentence in sentences) {
            val trimmedSentence = sentence.trim()
            if (trimmedSentence.isEmpty()) continue

            if (currentSegment.length + trimmedSentence.length <= maxLength) {
                if (currentSegment.isNotEmpty()) {
                    currentSegment.append("，")
                }
                currentSegment.append(trimmedSentence)
            } else {
                if (currentSegment.isNotEmpty()) {
                    segments.add(currentSegment.toString() + "。")
                    currentSegment = StringBuilder()
                }
                
                if (trimmedSentence.length > maxLength) {
                    var start = 0
                    while (start < trimmedSentence.length) {
                        val end = minOf(start + maxLength, trimmedSentence.length)
                        segments.add(trimmedSentence.substring(start, end) + "。")
                        start = end
                    }
                } else {
                    currentSegment.append(trimmedSentence)
                }
            }
        }

        if (currentSegment.isNotEmpty()) {
            segments.add(currentSegment.toString() + "。")
        }

        return segments
    }

    override fun hasRecordAudioPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED
    }

    override suspend fun requestRecordAudioPermission(): Boolean {
        return withContext(Dispatchers.Main) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val activity = context as? android.app.Activity
                activity?.let {
                    suspendCancellableCoroutine { cont ->
                        ActivityCompat.requestPermissions(
                            it,
                            arrayOf(Manifest.permission.RECORD_AUDIO),
                            REQUEST_RECORD_AUDIO_PERMISSION
                        )
                        cont.resume(hasRecordAudioPermission())
                    }
                } ?: false
            } else {
                true
            }
        }
    }

    override fun stopSpeaking() {
        sherpaTts.stopSpeaking()
        textToSpeech?.stop()
        speechRecognizer?.stopListening()
        ttsJob?.cancel()
        isTtsSpeaking = false
    }

    override suspend fun cloneVoice(audioPath: String, displayName: String): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                kotlinx.coroutines.delay(2000)
                val cloneId = "android_clone_${++cloneIdCounter}_${System.currentTimeMillis()}"
                clones.add(
                    VoiceProfile.Cloned(
                        cloneId = cloneId,
                        displayName = displayName,
                        relationship = com.example.myspatial.data.ParentalRelationship.MOTHER,
                        audioSamplePath = audioPath,
                        createdAt = System.currentTimeMillis()
                    )
                )
                Result.Success(cloneId)
            } catch (e: Exception) {
                Result.Error(e, "语音克隆失败: ${e.message}")
            }
        }
    }

    override suspend fun getCloneStatus(cloneId: String): Result<CloneStatus> {
        return withContext(Dispatchers.IO) {
            Result.Success(CloneStatus.COMPLETED)
        }
    }

    override suspend fun listClones(): Result<List<VoiceProfile.Cloned>> {
        return withContext(Dispatchers.IO) {
            Result.Success(clones)
        }
    }

    override suspend fun deleteClone(cloneId: String): Result<Unit> {
        return withContext(Dispatchers.IO) {
            clones.removeAll { it.cloneId == cloneId }
            Result.Success(Unit)
        }
    }

    override fun onReadyForSpeech(params: Bundle?) {}

    override fun onBeginningOfSpeech() {}

    override fun onRmsChanged(rmsdB: Float) {}

    override fun onBufferReceived(buffer: ByteArray?) {}

    override fun onEndOfSpeech() {
        recognitionJob?.complete()
    }

    override fun onError(error: Int) {
        currentResult = when (error) {
            SpeechRecognizer.ERROR_NO_MATCH -> null
            SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> null
            else -> null
        }
        recognitionJob?.complete()
    }

    override fun onResults(results: Bundle?) {
        val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
        currentResult = matches?.firstOrNull()
        recognitionJob?.complete()
    }

    override fun onPartialResults(partialResults: Bundle?) {
        val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
        partialResult = matches?.firstOrNull()
    }

    override fun onEvent(eventType: Int, params: Bundle?) {}

    fun destroy() {
        sherpaTts.release()
        sherpaAsr.release()
        asrRecordingJob?.cancel()
        speechRecognizer?.destroy()
        textToSpeech?.shutdown()
    }

    companion object {
        private const val REQUEST_RECORD_AUDIO_PERMISSION = 1001
    }
}
