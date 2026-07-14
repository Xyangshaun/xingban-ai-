package com.example.myspatial.data

import androidx.compose.ui.graphics.Color

enum class CompanionCharacter {
    STAR_BRIGHT,
    LUNA_GENTLE,
    SUNNY_ACTIVE,
    CLOUDY_SHY,
    FLAME_WARM;

    fun getCharacterColor(): Color {
        return when (this) {
            STAR_BRIGHT -> Color(0xFFE8D5B7)
            LUNA_GENTLE -> Color(0xFFB8D4E3)
            SUNNY_ACTIVE -> Color(0xFFFFE066)
            CLOUDY_SHY -> Color(0xFFD4D4D4)
            FLAME_WARM -> Color(0xFFFFB366)
        }
    }

    fun getCharacterName(): String {
        return when (this) {
            STAR_BRIGHT -> "星星"
            LUNA_GENTLE -> "月亮"
            SUNNY_ACTIVE -> "太阳"
            CLOUDY_SHY -> "云朵"
            FLAME_WARM -> "火焰"
        }
    }

    fun getCharacterIcon(): String {
        return when (this) {
            STAR_BRIGHT -> "⭐"
            LUNA_GENTLE -> "🌙"
            SUNNY_ACTIVE -> "☀️"
            CLOUDY_SHY -> "☁️"
            FLAME_WARM -> "🔥"
        }
    }
}

enum class CompanionPersonality {
    GENTLE,
    ACTIVE,
    QUIET,
    CURIOUS,
    CAREFUL;

    fun getPersonalityName(): String {
        return when (this) {
            GENTLE -> "温柔型"
            ACTIVE -> "活泼型"
            QUIET -> "安静型"
            CURIOUS -> "好奇型"
            CAREFUL -> "细心型"
        }
    }

    fun getPersonalityDescription(): String {
        return when (this) {
            GENTLE -> "温柔体贴，善解人意，总是用温暖的话语抚慰人心"
            ACTIVE -> "充满活力，乐观开朗，喜欢分享有趣的事情"
            QUIET -> "安静内敛，善于倾听，是最好的倾诉对象"
            CURIOUS -> "好奇心强，喜欢探索，总能发现新的乐趣"
            CAREFUL -> "心思细腻，考虑周全，会认真对待每一件事"
        }
    }

    fun getChatStyle(): String {
        return when (this) {
            GENTLE -> "语气温和，多用安慰和鼓励的话语，偶尔带点可爱的表情"
            ACTIVE -> "语气活泼，常用感叹号和表情，话题丰富多变"
            QUIET -> "语气平和，话不多但很真诚，善于引导对方表达"
            CURIOUS -> "语气好奇，喜欢提问和分享新知识，充满求知欲"
            CAREFUL -> "语气沉稳，表达清晰有条理，会认真回应每一个问题"
        }
    }

    fun getSpecialTopics(): String {
        return when (this) {
            GENTLE -> "情感话题、人际关系、音乐、自然"
            ACTIVE -> "游戏、运动、旅行、美食、流行文化"
            QUIET -> "阅读、艺术、哲学、内心感受"
            CURIOUS -> "科学、历史、新科技、未解之谜"
            CAREFUL -> "学习方法、时间管理、规划安排、生活技巧"
        }
    }
}

data class Companion(
    val id: String,
    val name: String,
    val character: CompanionCharacter,
    val personality: CompanionPersonality,
    val age: Int,
    val voiceProfile: VoiceProfile = personality.toVoiceProfile(),
    val likes: List<String> = emptyList(),
    val dislikes: List<String> = emptyList(),
    val importantDates: List<String> = emptyList(),
    val createdAt: Long = System.currentTimeMillis()
) {
    fun getGreeting(): String {
        return when (personality) {
            CompanionPersonality.GENTLE -> "你好呀，很高兴认识你~"
            CompanionPersonality.ACTIVE -> "嗨！我是${name}，一起玩吧！"
            CompanionPersonality.QUIET -> "你好...我是${name}，希望我们能成为朋友"
            CompanionPersonality.CURIOUS -> "哇，你好！我是${name}，你叫什么名字呀？"
            CompanionPersonality.CAREFUL -> "你好，我是${name}，很高兴见到你"
        }
    }
}