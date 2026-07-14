package com.example.myspatial.data

import com.example.myspatial.data.CompanionPersonality.*

sealed class VoiceProfile {
    data class Preset(val voiceTone: VoiceTone) : VoiceProfile()
    data class Cloned(
        val cloneId: String,
        val displayName: String,
        val relationship: ParentalRelationship,
        val audioSamplePath: String,
        val createdAt: Long = System.currentTimeMillis()
    ) : VoiceProfile()
}

enum class ParentalRelationship {
    FATHER,
    MOTHER,
    GRANDFATHER,
    GRANDMOTHER,
    OTHER_PARENT_FIGURE;

    fun getDisplayName(): String {
        return when (this) {
            FATHER -> "爸爸"
            MOTHER -> "妈妈"
            GRANDFATHER -> "爷爷"
            GRANDMOTHER -> "奶奶"
            OTHER_PARENT_FIGURE -> "亲人"
        }
    }
}

fun CompanionPersonality.toVoiceProfile(): VoiceProfile {
    return VoiceProfile.Preset(VoiceTone.fromPersonality(this))
}