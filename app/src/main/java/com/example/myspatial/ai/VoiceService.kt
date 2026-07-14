package com.example.myspatial.ai

import com.example.myspatial.core.Result
import com.example.myspatial.data.VoiceProfile
import com.example.myspatial.data.VoiceTone

interface VoiceService {
    suspend fun speechToText(): Result<String>
    suspend fun textToSpeech(text: String, voiceProfile: VoiceProfile): Result<Unit>
    suspend fun textToSpeech(text: String, voiceTone: VoiceTone): Result<Unit>
    fun hasRecordAudioPermission(): Boolean
    suspend fun requestRecordAudioPermission(): Boolean
    fun stopSpeaking()
    suspend fun cloneVoice(audioPath: String, displayName: String): Result<String>
    suspend fun getCloneStatus(cloneId: String): Result<CloneStatus>
    suspend fun listClones(): Result<List<VoiceProfile.Cloned>>
    suspend fun deleteClone(cloneId: String): Result<Unit>
}

class MockVoiceService : VoiceService {
    private val clones = mutableListOf<VoiceProfile.Cloned>()
    private var cloneIdCounter = 0

    override suspend fun speechToText(): Result<String> {
        return try {
            kotlinx.coroutines.delay(1000)
            Result.Success("这是模拟的语音识别结果")
        } catch (e: Exception) {
            Result.Error(e, "语音识别失败")
        }
    }

    override suspend fun textToSpeech(text: String, voiceProfile: VoiceProfile): Result<Unit> {
        return when (voiceProfile) {
            is VoiceProfile.Preset -> textToSpeech(text, voiceProfile.voiceTone)
            is VoiceProfile.Cloned -> {
                try {
                    kotlinx.coroutines.delay(500)
                    Result.Success(Unit)
                } catch (e: Exception) {
                    Result.Error(e, "语音合成失败")
                }
            }
        }
    }

    override suspend fun textToSpeech(text: String, voiceTone: VoiceTone): Result<Unit> {
        return try {
            kotlinx.coroutines.delay(500)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e, "语音合成失败")
        }
    }

    override fun hasRecordAudioPermission(): Boolean {
        return true
    }

    override suspend fun requestRecordAudioPermission(): Boolean {
        return true
    }

    override fun stopSpeaking() {}

    override suspend fun cloneVoice(audioPath: String, displayName: String): Result<String> {
        val cloneId = "mock_clone_${++cloneIdCounter}_${System.currentTimeMillis()}"
        clones.add(
            VoiceProfile.Cloned(
                cloneId = cloneId,
                displayName = displayName,
                relationship = com.example.myspatial.data.ParentalRelationship.MOTHER,
                audioSamplePath = audioPath,
                createdAt = System.currentTimeMillis()
            )
        )
        return Result.Success(cloneId)
    }

    override suspend fun getCloneStatus(cloneId: String): Result<CloneStatus> {
        return Result.Success(CloneStatus.COMPLETED)
    }

    override suspend fun listClones(): Result<List<VoiceProfile.Cloned>> {
        return Result.Success(clones)
    }

    override suspend fun deleteClone(cloneId: String): Result<Unit> {
        clones.removeAll { it.cloneId == cloneId }
        return Result.Success(Unit)
    }
}