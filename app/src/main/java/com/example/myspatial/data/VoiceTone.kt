package com.example.myspatial.data

import com.example.myspatial.data.CompanionPersonality.*

enum class VoiceTone(
    val displayName: String,
    val pitch: Float,
    val speed: Float,
    val language: String = "zh-CN"
) {
    GENTLE_WARM(
        displayName = "温柔暖音",
        pitch = 1.1f,
        speed = 0.9f
    ),
    ACTIVE_CHEERFUL(
        displayName = "活泼欢悦",
        pitch = 1.3f,
        speed = 1.2f
    ),
    QUIET_SOFT(
        displayName = "安静轻柔",
        pitch = 0.95f,
        speed = 0.85f
    ),
    CURIOUS_INQUISITIVE(
        displayName = "好奇灵动",
        pitch = 1.2f,
        speed = 1.05f
    ),
    CAREFUL_SERENE(
        displayName = "细心平和",
        pitch = 1.0f,
        speed = 0.9f
    );

    companion object {
        fun fromPersonality(personality: CompanionPersonality): VoiceTone {
            return when (personality) {
                GENTLE -> GENTLE_WARM
                ACTIVE -> ACTIVE_CHEERFUL
                QUIET -> QUIET_SOFT
                CURIOUS -> CURIOUS_INQUISITIVE
                CAREFUL -> CAREFUL_SERENE
            }
        }
    }
}
