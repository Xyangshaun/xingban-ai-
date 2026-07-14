package com.example.myspatial.ui.components

import androidx.compose.ui.graphics.Color
import com.example.myspatial.data.CompanionCharacter
import com.example.myspatial.data.CompanionPersonality
import com.example.myspatial.data.EmotionType

fun CompanionCharacter.getCharacterColor(): Color {
    return when (this) {
        CompanionCharacter.STAR_BRIGHT -> Color(0xFFFBBF24)
        CompanionCharacter.LUNA_GENTLE -> Color(0xFFA5B4FC)
        CompanionCharacter.SUNNY_ACTIVE -> Color(0xFFFF9F43)
        CompanionCharacter.CLOUDY_SHY -> Color(0xFFCBD5E1)
        CompanionCharacter.FLAME_WARM -> Color(0xFFFF6B6B)
    }
}

fun CompanionCharacter.getCharacterIcon(): String {
    return when (this) {
        CompanionCharacter.STAR_BRIGHT -> "⭐"
        CompanionCharacter.LUNA_GENTLE -> "🌙"
        CompanionCharacter.SUNNY_ACTIVE -> "☀️"
        CompanionCharacter.CLOUDY_SHY -> "☁️"
        CompanionCharacter.FLAME_WARM -> "🔥"
    }
}

fun CompanionPersonality.getPersonalityName(): String {
    return when (this) {
        CompanionPersonality.GENTLE -> "温柔"
        CompanionPersonality.ACTIVE -> "活泼"
        CompanionPersonality.QUIET -> "安静"
        CompanionPersonality.CURIOUS -> "好奇"
        CompanionPersonality.CAREFUL -> "细心"
    }
}

fun EmotionType.getEmoji(): String {
    return when (this) {
        EmotionType.HAPPY -> "😊"
        EmotionType.CALM -> "😌"
        EmotionType.SAD -> "😢"
        EmotionType.ANGRY -> "😠"
        EmotionType.SCARED -> "😨"
        EmotionType.MISSING -> "🥺"
        EmotionType.LONELY -> "😔"
        EmotionType.CONFUSED -> "😕"
    }
}

fun EmotionType.getEmotionName(): String {
    return when (this) {
        EmotionType.HAPPY -> "开心"
        EmotionType.CALM -> "平静"
        EmotionType.SAD -> "难过"
        EmotionType.ANGRY -> "生气"
        EmotionType.SCARED -> "害怕"
        EmotionType.MISSING -> "想念"
        EmotionType.LONELY -> "孤单"
        EmotionType.CONFUSED -> "困惑"
    }
}

fun EmotionType.getColor(): Color {
    return when (this) {
        EmotionType.HAPPY -> Color(0xFFFFD700)
        EmotionType.CALM -> Color(0xFF87CEEB)
        EmotionType.SAD -> Color(0xFF6B8DD6)
        EmotionType.ANGRY -> Color(0xFFFF6B6B)
        EmotionType.SCARED -> Color(0xFF9B59B6)
        EmotionType.MISSING -> Color(0xFFF39C12)
        EmotionType.LONELY -> Color(0xFF7F8C8D)
        EmotionType.CONFUSED -> Color(0xFFE67E22)
    }
}
