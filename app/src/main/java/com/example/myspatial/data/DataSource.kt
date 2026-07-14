package com.example.myspatial.data

interface CompanionDataSource {
    fun saveCompanion(companion: Companion)
    fun getCompanion(): Companion?
    fun deleteCompanion()
    fun hasCompanion(): Boolean
}

interface ConversationDataSource {
    fun saveConversations(conversations: List<Conversation>)
    fun getConversations(): List<Conversation>
    fun addMessage(message: Message, companionId: String)
    fun getMessagesForCompanion(companionId: String): List<Message>
}

interface MoodDataSource {
    fun saveMoodRecords(records: List<MoodRecord>)
    fun getMoodRecords(): List<MoodRecord>
    fun addMoodRecord(record: MoodRecord)
}
