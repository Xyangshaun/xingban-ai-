package com.example.myspatial.ai

import com.example.myspatial.core.Result
import com.example.myspatial.data.VoiceProfile

interface VoiceCloneService {
    suspend fun cloneVoice(audioPath: String, displayName: String): Result<String>
    suspend fun getCloneStatus(cloneId: String): Result<CloneStatus>
    suspend fun deleteClone(cloneId: String): Result<Unit>
    suspend fun listClones(): Result<List<VoiceProfile.Cloned>>
}

enum class CloneStatus {
    PENDING,
    PROCESSING,
    COMPLETED,
    FAILED
}

class VoiceCloneProvider {
    companion object {
        private var currentService: VoiceCloneService? = null

        fun initialize(service: VoiceCloneService) {
            currentService = service
        }

        fun getService(): VoiceCloneService {
            return currentService ?: DummyVoiceCloneService()
        }
    }
}

class DummyVoiceCloneService : VoiceCloneService {
    private val clones = mutableListOf<VoiceProfile.Cloned>()
    private var cloneIdCounter = 0

    override suspend fun cloneVoice(audioPath: String, displayName: String): Result<String> {
        val cloneId = "clone_${++cloneIdCounter}_${System.currentTimeMillis()}"
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

    override suspend fun deleteClone(cloneId: String): Result<Unit> {
        clones.removeAll { it.cloneId == cloneId }
        return Result.Success(Unit)
    }

    override suspend fun listClones(): Result<List<VoiceProfile.Cloned>> {
        return Result.Success(clones)
    }
}