package com.example.myspatial.ai

import com.example.myspatial.core.Result
import com.example.myspatial.data.CompanionPersonality
import com.example.myspatial.data.Message

interface LLMClient {
    suspend fun generateResponse(
        userMessage: String,
        personality: CompanionPersonality,
        companionName: String,
        history: List<Message> = emptyList()
    ): Result<String>
}

class MockLLMClient(
    private val companionAI: CompanionAI = CompanionAI()
) : LLMClient {
    override suspend fun generateResponse(
        userMessage: String,
        personality: CompanionPersonality,
        companionName: String,
        history: List<Message>
    ): Result<String> {
        return try {
            kotlinx.coroutines.delay(800)
            val response = companionAI.generateResponse(userMessage, personality, companionName, history)
            Result.Success(response.text)
        } catch (e: Exception) {
            Result.Error(e, "生成响应失败")
        }
    }
}