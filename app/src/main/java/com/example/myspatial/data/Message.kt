package com.example.myspatial.data

import java.util.UUID
import androidx.compose.ui.graphics.Color

enum class MessageType {
    TEXT,
    VOICE,
    IMAGE,
    SYSTEM
}

enum class MessageSender {
    USER,
    COMPANION,
    SYSTEM
}

enum class EmotionType {
    HAPPY,
    CALM,
    SAD,
    ANGRY,
    SCARED,
    MISSING,
    LONELY,
    CONFUSED;

    fun getColor(): Color {
        return when (this) {
            HAPPY -> Color(0xFFFFD700)
            CALM -> Color(0xFF87CEEB)
            SAD -> Color(0xFF6495ED)
            ANGRY -> Color(0xFFFF6347)
            SCARED -> Color(0xFF9370DB)
            MISSING -> Color(0xFFF08080)
            LONELY -> Color(0xFFA9A9A9)
            CONFUSED -> Color(0xFFFFA500)
        }
    }

    fun getEmoji(): String {
        return when (this) {
            HAPPY -> "😊"
            CALM -> "😌"
            SAD -> "😢"
            ANGRY -> "😠"
            SCARED -> "😨"
            MISSING -> "🥺"
            LONELY -> "😔"
            CONFUSED -> "😕"
        }
    }

    fun getEmotionName(): String {
        return when (this) {
            HAPPY -> "开心"
            CALM -> "平静"
            SAD -> "难过"
            ANGRY -> "生气"
            SCARED -> "害怕"
            MISSING -> "想念"
            LONELY -> "孤单"
            CONFUSED -> "困惑"
        }
    }
}

data class Message(
    val id: String = UUID.randomUUID().toString(),
    val text: String,
    val type: MessageType = MessageType.TEXT,
    val sender: MessageSender,
    val timestamp: Long = System.currentTimeMillis(),
    val emotion: EmotionType? = null,
    val duration: Long = 0,
    val riskLevel: RiskLevel? = null
)

data class Conversation(
    val id: String,
    val companionId: String,
    val messages: List<Message> = emptyList(),
    val createdAt: Long = System.currentTimeMillis(),
    val lastUpdated: Long = System.currentTimeMillis()
)