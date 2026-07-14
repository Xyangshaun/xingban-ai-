package com.example.myspatial.ai

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.util.Log
import com.k2fsa.sherpa.onnx.OfflineTts
import com.k2fsa.sherpa.onnx.OfflineTtsConfig
import com.k2fsa.sherpa.onnx.OfflineTtsModelConfig
import com.k2fsa.sherpa.onnx.OfflineTtsVitsModelConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import kotlin.coroutines.resume

class SherpaTtsEngine(private val context: Context) {

    companion object {
        private const val TAG = "SherpaTts"
        private const val MODEL_DIR_NAME = "sherpa-tts/vits-melo-tts-zh_en"
        private const val ASSETS_MODEL_PATH = "sherpa-tts/vits-melo-tts-zh_en"
        
        const val DEFAULT_SPEED = 1.0f
        const val DEFAULT_PITCH = 1.0f
        const val DEFAULT_VOLUME = 1.0f
        
        const val SPEED_SLOW = 0.7f
        const val SPEED_NORMAL = 1.0f
        const val SPEED_FAST = 1.3f
        
        const val PITCH_LOW = 0.8f
        const val PITCH_NORMAL = 1.0f
        const val PITCH_HIGH = 1.2f
    }

    private var tts: OfflineTts? = null
    private var audioTrack: AudioTrack? = null
    @Volatile
    private var isModelLoaded = false
    @Volatile
    private var isPlaying = false
    
    private var currentSpeed = DEFAULT_SPEED
    private var currentPitch = DEFAULT_PITCH
    private var currentVolume = DEFAULT_VOLUME
    
    private var preloadedTexts = mutableMapOf<String, Pair<FloatArray, Int>>()

    fun isModelAvailable(): Boolean {
        val modelDir = getModelDir()
        val modelFile = File(modelDir, "model.onnx")
        val lexiconFile = File(modelDir, "lexicon.txt")
        val tokensFile = File(modelDir, "tokens.txt")
        return modelFile.exists() && lexiconFile.exists() && tokensFile.exists()
    }

    fun isReady(): Boolean {
        return isModelLoaded && tts != null
    }

    fun setSpeed(speed: Float) {
        currentSpeed = speed.coerceIn(0.5f, 2.0f)
        Log.i(TAG, "Speed set to $currentSpeed")
    }

    fun setPitch(pitch: Float) {
        currentPitch = pitch.coerceIn(0.5f, 2.0f)
        Log.i(TAG, "Pitch set to $currentPitch")
    }

    fun setVolume(volume: Float) {
        currentVolume = volume.coerceIn(0.0f, 2.0f)
        Log.i(TAG, "Volume set to $currentVolume")
    }

    fun getSpeed(): Float = currentSpeed
    fun getPitch(): Float = currentPitch
    fun getVolume(): Float = currentVolume

    fun preload(text: String) {
        if (!isModelLoaded || tts == null || preloadedTexts.containsKey(text)) return
        try {
            val audio = tts?.generate(text, speed = currentSpeed) ?: return
            preloadedTexts[text] = Pair(audio.samples, audio.sampleRate)
            Log.i(TAG, "Preloaded text: ${text.take(20)}...")
        } catch (e: Exception) {
            Log.e(TAG, "Preload failed: ${e.message}")
        }
    }

    fun clearPreload() {
        preloadedTexts.clear()
    }

    private fun getModelDir(): String {
        return "${context.filesDir.absolutePath}/$MODEL_DIR_NAME"
    }

    private fun copyAssetsToFiles() {
        val assetManager = context.assets
        val modelDir = File(getModelDir())
        
        if (!modelDir.exists()) {
            modelDir.mkdirs()
            Log.i(TAG, "Created model directory: $modelDir")
        }

        val filesToCopy = listOf(
            "model.onnx",
            "tokens.txt",
            "lexicon.txt",
            "phone.fst",
            "date.fst",
            "number.fst",
            "new_heteronym.fst"
        )

        filesToCopy.forEach { filePath ->
            val destFile = File(modelDir, filePath)
            if (!destFile.exists()) {
                try {
                    val inputStream = assetManager.open("$ASSETS_MODEL_PATH/$filePath")
                    destFile.parentFile?.mkdirs()
                    copyInputStreamToFile(inputStream, destFile)
                    Log.i(TAG, "Copied asset: $filePath to ${destFile.absolutePath}")
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to copy asset $filePath: ${e.message}", e)
                }
            }
        }
    }

    private fun copyInputStreamToFile(inputStream: InputStream, destFile: File) {
        inputStream.use { input ->
            FileOutputStream(destFile).use { output ->
                val buffer = ByteArray(8192)
                var bytesRead: Int
                while (input.read(buffer).also { bytesRead = it } != -1) {
                    output.write(buffer, 0, bytesRead)
                }
            }
        }
    }

    fun init(): Boolean {
        if (isModelLoaded) {
            Log.i(TAG, "Model already loaded")
            return true
        }

        copyAssetsToFiles()

        val modelDir = getModelDir()
        if (!isModelAvailable()) {
            Log.e(TAG, "Model files not found after copy at: $modelDir")
            return false
        }

        return try {
            Log.i(TAG, "Initializing sherpa-onnx TTS, modelDir=$modelDir")

            val ruleFsts = listOf(
                "date.fst", "number.fst", "phone.fst", "new_heteronym.fst"
            ).filter { File(modelDir, it).exists() }
                .joinToString(",") { "$modelDir/$it" }

            val config = OfflineTtsConfig(
                model = OfflineTtsModelConfig(
                    vits = OfflineTtsVitsModelConfig(
                        model = "$modelDir/model.onnx",
                        lexicon = "$modelDir/lexicon.txt",
                        tokens = "$modelDir/tokens.txt",
                        noiseScale = 0.5f,
                        noiseScaleW = 0.6f,
                        lengthScale = 1.0f,
                    ),
                    numThreads = 4,
                    debug = false,
                    provider = "cpu",
                ),
                ruleFsts = ruleFsts,
                maxNumSentences = 3,
                silenceScale = 0.3f,
            )

            tts = OfflineTts(config = config)
            isModelLoaded = true
            Log.i(TAG, "TTS initialized successfully. sampleRate=${tts?.sampleRate()}, numSpeakers=${tts?.numSpeakers()}")
            true
        } catch (e: UnsatisfiedLinkError) {
            Log.e(TAG, "Failed to load sherpa-onnx native library: ${e.message}", e)
            isModelLoaded = false
            false
        } catch (e: Exception) {
            Log.e(TAG, "TTS init failed: ${e.message}", e)
            isModelLoaded = false
            false
        }
    }

    suspend fun speak(text: String, speed: Float = currentSpeed): Boolean {
        if (!isModelLoaded || tts == null) {
            Log.e(TAG, "TTS not initialized, cannot speak")
            return false
        }

        if (text.isBlank()) {
            Log.w(TAG, "Empty text, skipping TTS")
            return true
        }

        isPlaying = true
        try {
            val (samples, sampleRate) = preloadedTexts[text] 
                ?: run {
                    val audio = tts?.generate(text, speed = speed) ?: return false
                    Pair(audio.samples, audio.sampleRate)
                }
            
            Log.i(TAG, "TTS generated ${samples.size} samples at $sampleRate Hz, preloaded=${preloadedTexts.containsKey(text)}")
            
            return playAudio(samples, sampleRate)
        } catch (e: Exception) {
            Log.e(TAG, "TTS generate/play failed: ${e.message}", e)
            return false
        } finally {
            isPlaying = false
        }
    }

    private suspend fun playAudio(samples: FloatArray, sampleRate: Int): Boolean {
        if (samples.isEmpty()) {
            Log.w(TAG, "Empty audio samples")
            return true
        }

        val adjustedSamples = if (currentVolume != 1.0f) {
            samples.map { it * currentVolume }.toFloatArray()
        } else {
            samples
        }

        val pcmSamples = FloatToPCM16(adjustedSamples)
        val dataSize = pcmSamples.size * 2
        val bufferSize = AudioTrack.getMinBufferSize(
            sampleRate,
            AudioFormat.CHANNEL_OUT_MONO,
            AudioFormat.ENCODING_PCM_16BIT
        ).coerceAtLeast(dataSize)
        
        Log.i(TAG, "playAudio: sampleRate=$sampleRate, bufferSize=$bufferSize, samples.length=${samples.size}, dataSize=$dataSize, volume=$currentVolume")

        val durationMs = (pcmSamples.size.toLong() * 1000) / sampleRate
        Log.i(TAG, "playAudio: pcmSamples.length=${pcmSamples.size}, durationMs=$durationMs")

        return withContext(Dispatchers.IO) {
            var track: AudioTrack? = null
            try {
                track = AudioTrack.Builder()
                    .setAudioAttributes(AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_ASSISTANCE_ACCESSIBILITY)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                        .build())
                    .setAudioFormat(AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(sampleRate)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build())
                    .setBufferSizeInBytes(bufferSize)
                    .setTransferMode(AudioTrack.MODE_STATIC)
                    .build()

                Log.i(TAG, "AudioTrack created, state=${track.state}")

                val written = track.write(pcmSamples, 0, pcmSamples.size)
                Log.i(TAG, "AudioTrack.write() returned: $written (expected: ${pcmSamples.size})")

                track.play()
                Log.i(TAG, "AudioTrack.play() called, state=${track.playState}")

                withTimeoutOrNull((durationMs + 2000).toLong()) {
                    while (track.playState == AudioTrack.PLAYSTATE_PLAYING && isPlaying) {
                        delay(100)
                    }
                }

                Log.i(TAG, "Audio playback completed")
                true
            } catch (e: Exception) {
                Log.e(TAG, "Audio playback failed: ${e.message}", e)
                false
            } finally {
                runCatching {
                    track?.stop()
                    track?.release()
                }
                audioTrack = null
            }
        }
    }

    private fun FloatToPCM16(floatSamples: FloatArray): ShortArray {
        val pcm16 = ShortArray(floatSamples.size)
        for (i in floatSamples.indices) {
            val sample = floatSamples[i]
            val pcm = (sample * Short.MAX_VALUE).toInt()
            pcm16[i] = pcm.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }
        return pcm16
    }

    fun stopSpeaking() {
        isPlaying = false
        audioTrack?.stop()
        audioTrack?.release()
        audioTrack = null
    }

    fun release() {
        stopSpeaking()
        tts?.release()
        tts = null
        isModelLoaded = false
    }
}