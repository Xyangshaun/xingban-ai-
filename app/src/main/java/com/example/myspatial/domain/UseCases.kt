package com.example.myspatial.domain

import com.example.myspatial.core.Result
import com.example.myspatial.data.Companion
import com.example.myspatial.data.CompanionRepository
import com.example.myspatial.data.EmotionType
import com.example.myspatial.data.Message
import com.example.myspatial.data.MessageSender
import com.example.myspatial.data.MessageType
import com.example.myspatial.data.MoodRecord
import com.example.myspatial.data.MoodTrend

class CreateCompanionUseCase(private val repository: CompanionRepository) {
    fun execute(
        name: String,
        character: com.example.myspatial.data.CompanionCharacter,
        personality: com.example.myspatial.data.CompanionPersonality,
        age: Int,
        likes: List<String> = emptyList(),
        dislikes: List<String> = emptyList(),
        importantDates: List<String> = emptyList()
    ): Result<Companion> {
        return try {
            val companion = Companion(
                id = java.util.UUID.randomUUID().toString(),
                name = name,
                character = character,
                personality = personality,
                age = age,
                likes = likes,
                dislikes = dislikes,
                importantDates = importantDates
            )
            repository.saveCompanion(companion)
            Result.Success(companion)
        } catch (e: Exception) {
            Result.Error<Companion>(e, "创建伙伴失败")
        }
    }
}

class GetCompanionUseCase(private val repository: CompanionRepository) {
    fun execute(): Result<Companion?> {
        return try {
            Result.Success(repository.getCompanion())
        } catch (e: Exception) {
            Result.Error<Companion?>(e, "获取伙伴失败")
        }
    }
}

class DeleteCompanionUseCase(private val repository: CompanionRepository) {
    fun execute(): Result<Unit> {
        return try {
            repository.deleteCompanion()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error<Unit>(e, "删除伙伴失败")
        }
    }
}

class SendMessageUseCase(private val repository: CompanionRepository) {
    fun execute(text: String, companionId: String): Result<Message> {
        return try {
            val message = Message(
                text = text,
                type = MessageType.TEXT,
                sender = MessageSender.USER
            )
            repository.addMessage(message, companionId)
            Result.Success(message)
        } catch (e: Exception) {
            Result.Error<Message>(e, "发送消息失败")
        }
    }
}

class ReceiveMessageUseCase(private val repository: CompanionRepository) {
    fun execute(text: String, companionId: String, emotion: EmotionType? = null): Result<Message> {
        return try {
            val message = Message(
                text = text,
                type = MessageType.TEXT,
                sender = MessageSender.COMPANION,
                emotion = emotion
            )
            repository.addMessage(message, companionId)
            Result.Success(message)
        } catch (e: Exception) {
            Result.Error<Message>(e, "接收消息失败")
        }
    }
}

class GetMessagesUseCase(private val repository: CompanionRepository) {
    fun execute(companionId: String): Result<List<Message>> {
        return try {
            Result.Success(repository.getMessagesForCompanion(companionId))
        } catch (e: Exception) {
            Result.Error<List<Message>>(e, "获取消息失败")
        }
    }
}

class RecordMoodUseCase(private val repository: CompanionRepository) {
    fun execute(
        date: java.time.LocalDate,
        emotion: EmotionType,
        intensity: Int,
        note: String = ""
    ): Result<MoodRecord> {
        return try {
            val record = MoodRecord(date = date, emotion = emotion, intensity = intensity, note = note)
            repository.addMoodRecord(record)
            Result.Success(record)
        } catch (e: Exception) {
            Result.Error<MoodRecord>(e, "记录心情失败")
        }
    }
}

class GetMoodTrendUseCase(private val repository: CompanionRepository) {
    fun execute(days: Int): Result<MoodTrend> {
        return try {
            Result.Success(repository.getMoodTrend(days))
        } catch (e: Exception) {
            Result.Error<MoodTrend>(e, "获取心情趋势失败")
        }
    }
}

class GetMoodRecordsUseCase(private val repository: CompanionRepository) {
    fun execute(): Result<List<MoodRecord>> {
        return try {
            Result.Success(repository.getMoodRecords())
        } catch (e: Exception) {
            Result.Error<List<MoodRecord>>(e, "获取心情记录失败")
        }
    }
}
