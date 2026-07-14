package com.example.myspatial.ai

import android.content.Context
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

class SherpaAsrEngine(private val context: Context) {

    companion object {
        private const val TAG = "SherpaAsr"
        private const val MODEL_DIR_NAME = "sherpa-asr/sense-voice-int8"
        private const val ASSETS_MODEL_PATH = "sherpa-asr/sense-voice-int8"
        
        private const val SAMPLE_RATE = 16000
        private const val CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO
        private const val AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT
        private const val RECORD_BUFFER_SIZE = 8192
    }

    @Volatile
    private var isModelLoaded = false
    @Volatile
    private var isRecording = false
    private var audioRecord: AudioRecord? = null
    private var recordedSamples = mutableListOf<Short>()

    fun isReady(): Boolean {
        return isModelLoaded
    }

    private fun getModelDir(): String {
        return "${context.filesDir.absolutePath}/$MODEL_DIR_NAME"
    }

    private fun copyAssetsToFiles() {
        val assetManager = context.assets
        val modelDir = File(getModelDir())
        
        if (!modelDir.exists()) {
            modelDir.mkdirs()
            Log.i(TAG, "Created ASR model directory: $modelDir")
        }

        val filesToCopy = listOf(
            "model.int8.onnx",
            "tokens.txt"
        )

        filesToCopy.forEach { filePath ->
            val destFile = File(modelDir, filePath)
            if (!destFile.exists()) {
                try {
                    val inputStream = assetManager.open("$ASSETS_MODEL_PATH/$filePath")
                    destFile.parentFile?.mkdirs()
                    copyInputStreamToFile(inputStream, destFile)
                    Log.i(TAG, "Copied ASR asset: $filePath to ${destFile.absolutePath}")
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to copy ASR asset $filePath: ${e.message}", e)
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
            Log.i(TAG, "ASR model already loaded")
            return true
        }

        copyAssetsToFiles()

        val modelDir = getModelDir()
        val modelFile = File(modelDir, "model.int8.onnx")
        val tokensFile = File(modelDir, "tokens.txt")
        
        if (!modelFile.exists() || !tokensFile.exists()) {
            Log.e(TAG, "ASR model files not found at: $modelDir")
            return false
        }

        isModelLoaded = true
        Log.i(TAG, "ASR model files prepared (will use system SpeechRecognizer)")
        return true
    }

    suspend fun startRecording(): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val minBufferSize = AudioRecord.getMinBufferSize(
                    SAMPLE_RATE,
                    CHANNEL_CONFIG,
                    AUDIO_FORMAT
                )

                val bufferSize = minBufferSize.coerceAtLeast(RECORD_BUFFER_SIZE)

                audioRecord = AudioRecord.Builder()
                    .setAudioSource(MediaRecorder.AudioSource.MIC)
                    .setAudioFormat(AudioFormat.Builder()
                        .setEncoding(AUDIO_FORMAT)
                        .setSampleRate(SAMPLE_RATE)
                        .setChannelMask(CHANNEL_CONFIG)
                        .build())
                    .setBufferSizeInBytes(bufferSize)
                    .build()

                if (audioRecord?.state != AudioRecord.STATE_INITIALIZED) {
                    Log.e(TAG, "AudioRecord initialization failed")
                    audioRecord = null
                    return@withContext false
                }

                recordedSamples.clear()
                isRecording = true
                audioRecord?.startRecording()
                Log.i(TAG, "Recording started")
                true
            } catch (e: Exception) {
                Log.e(TAG, "Failed to start recording: ${e.message}", e)
                isRecording = false
                audioRecord?.release()
                audioRecord = null
                false
            }
        }
    }

    suspend fun stopRecording(): String? {
        return withContext(Dispatchers.IO) {
            if (!isRecording || audioRecord == null) {
                Log.w(TAG, "Not recording")
                return@withContext null
            }

            try {
                isRecording = false
                audioRecord?.stop()
                
                val buffer = ShortArray(RECORD_BUFFER_SIZE)
                var read: Int
                while (audioRecord?.read(buffer, 0, buffer.size)?.also { read = it } == AudioRecord.ERROR_INVALID_OPERATION) {
                    Thread.sleep(10)
                }
                
                audioRecord?.release()
                audioRecord = null
                Log.i(TAG, "Recording stopped")

                if (recordedSamples.isEmpty()) {
                    Log.w(TAG, "No audio samples recorded")
                    return@withContext null
                }

                val audioData = recordedSamples.toShortArray()
                Log.i(TAG, "Recorded ${audioData.size} samples")
                
                null
            } catch (e: Exception) {
                Log.e(TAG, "Failed to stop recording: ${e.message}", e)
                audioRecord?.release()
                audioRecord = null
                null
            }
        }
    }

    fun processAudioData(buffer: ShortArray, size: Int) {
        if (!isRecording) return
        
        val samplesToAdd = buffer.copyOf(size)
        recordedSamples.addAll(samplesToAdd.toList())
        
        if (recordedSamples.size > 600000) {
            recordedSamples = recordedSamples.takeLast(600000).toMutableList()
        }
    }

    fun release() {
        try {
            isRecording = false
            audioRecord?.stop()
            audioRecord?.release()
            audioRecord = null
        } catch (e: Exception) {
            Log.e(TAG, "Error releasing ASR: ${e.message}", e)
        }
        isModelLoaded = false
    }
}