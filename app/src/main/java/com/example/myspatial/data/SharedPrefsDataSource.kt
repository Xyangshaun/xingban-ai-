package com.example.myspatial.data

import android.content.Context
import com.example.myspatial.core.Constants
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken

class SharedPrefsDataSource(context: Context) :
    CompanionDataSource,
    ConversationDataSource,
    MoodDataSource {

    private val prefs = context.getSharedPreferences(Constants.SHARED_PREFS_NAME, Context.MODE_PRIVATE)
    private val gson = GsonBuilder()
        .registerTypeAdapter(VoiceProfile::class.java, VoiceProfileTypeAdapter())
        .create()

    override fun saveCompanion(companion: Companion) {
        prefs.edit().putString(Constants.KEY_COMPANION, gson.toJson(companion)).apply()
    }

    override fun getCompanion(): Companion? {
        val json = prefs.getString(Constants.KEY_COMPANION, null)
        return json?.let { gson.fromJson(json, Companion::class.java) }
    }

    override fun deleteCompanion() {
        prefs.edit().remove(Constants.KEY_COMPANION).apply()
    }

    override fun hasCompanion(): Boolean {
        return getCompanion() != null
    }

    override fun saveConversations(conversations: List<Conversation>) {
        prefs.edit().putString(Constants.KEY_MESSAGES, gson.toJson(conversations)).apply()
    }

    override fun getConversations(): List<Conversation> {
        val json = prefs.getString(Constants.KEY_MESSAGES, null)
        return json?.let {
            gson.fromJson(json, object : TypeToken<List<Conversation>>() {}.type)
        } ?: emptyList()
    }

    override fun addMessage(message: Message, companionId: String) {
        val conversations = getConversations().toMutableList()
        var conversation = conversations.find { it.companionId == companionId }
        if (conversation == null) {
            conversation = Conversation(id = message.id, companionId = companionId)
            conversations.add(conversation)
        }
        val updatedMessages = conversation.messages.toMutableList().apply { add(message) }
        val updatedConversation = conversation.copy(
            messages = updatedMessages,
            lastUpdated = System.currentTimeMillis()
        )
        val index = conversations.indexOfFirst { it.companionId == companionId }
        if (index >= 0) {
            conversations[index] = updatedConversation
        }
        saveConversations(conversations)
    }

    override fun getMessagesForCompanion(companionId: String): List<Message> {
        return getConversations().find { it.companionId == companionId }?.messages ?: emptyList()
    }

    override fun saveMoodRecords(records: List<MoodRecord>) {
        prefs.edit().putString(Constants.KEY_MOOD_RECORDS, gson.toJson(records)).apply()
    }

    override fun getMoodRecords(): List<MoodRecord> {
        val json = prefs.getString(Constants.KEY_MOOD_RECORDS, null)
        return json?.let {
            gson.fromJson(json, object : TypeToken<List<MoodRecord>>() {}.type)
        } ?: emptyList()
    }

    override fun addMoodRecord(record: MoodRecord) {
        val records = getMoodRecords().toMutableList()
        val existingIndex = records.indexOfFirst { it.date == record.date }
        if (existingIndex >= 0) {
            records[existingIndex] = record
        } else {
            records.add(record)
        }
        saveMoodRecords(records)
    }
}
