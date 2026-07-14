package com.example.myspatial.ai

import com.example.myspatial.core.Result
import com.example.myspatial.data.EmotionType

interface EmotionAnalyzer {
    fun analyze(text: String): Result<EmotionType>
    fun analyzeFromHistory(messages: List<String>): Result<List<EmotionType>>
}

class KeywordEmotionAnalyzer : EmotionAnalyzer {
    private val emotionKeywords = mapOf(
        EmotionType.HAPPY to listOf("开心", "高兴", "快乐", "幸福", "笑", "棒", "好", "喜欢", "爱", "兴奋", "激动"),
        EmotionType.SAD to listOf("难过", "伤心", "不开心", "想哭", "失落", "沮丧", "郁闷", "孤单", "失望"),
        EmotionType.ANGRY to listOf("生气", "愤怒", "讨厌", "烦", "气死", "恨", "烦"),
        EmotionType.SCARED to listOf("害怕", "恐惧", "怕", "担心", "不安", "紧张", "焦虑"),
        EmotionType.MISSING to listOf("想", "想念", "思念", "怀念", "回家", "父母", "爸妈", "亲人"),
        EmotionType.LONELY to listOf("孤独", "孤单", "没人", "一个人", "寂寞"),
        EmotionType.CONFUSED to listOf("不知道", "困惑", "迷茫", "不懂", "怎么办", "纠结")
    )

    override fun analyze(text: String): Result<EmotionType> {
        return try {
            val lowerText = text.lowercase()
            for ((emotion, keywords) in emotionKeywords) {
                if (keywords.any { lowerText.contains(it) }) {
                    return Result.Success(emotion)
                }
            }
            Result.Success(EmotionType.CALM)
        } catch (e: Exception) {
            Result.Error(e, "情绪分析失败")
        }
    }

    override fun analyzeFromHistory(messages: List<String>): Result<List<EmotionType>> {
        return try {
            val emotions = messages.map { analyze(it).getOrNull() ?: EmotionType.CALM }
            Result.Success(emotions)
        } catch (e: Exception) {
            Result.Error(e, "历史情绪分析失败")
        }
    }
}