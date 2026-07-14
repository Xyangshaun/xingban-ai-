package com.example.myspatial.data

import java.time.LocalDate

class CompanionRepository(
    private val companionDataSource: CompanionDataSource,
    private val conversationDataSource: ConversationDataSource,
    private val moodDataSource: MoodDataSource
) {

    fun saveCompanion(companion: Companion) {
        companionDataSource.saveCompanion(companion)
    }

    fun getCompanion(): Companion? {
        return companionDataSource.getCompanion()
    }

    fun hasCompanion(): Boolean {
        return companionDataSource.hasCompanion()
    }

    fun deleteCompanion() {
        companionDataSource.deleteCompanion()
    }

    fun addMessage(message: Message, companionId: String) {
        conversationDataSource.addMessage(message, companionId)
    }

    fun getMessagesForCompanion(companionId: String): List<Message> {
        return conversationDataSource.getMessagesForCompanion(companionId)
    }

    fun addMoodRecord(record: MoodRecord) {
        moodDataSource.addMoodRecord(record)
    }

    fun getMoodRecords(): List<MoodRecord> {
        return moodDataSource.getMoodRecords()
    }

    fun getMoodTrend(days: Int): MoodTrend {
        val records = getMoodRecords()
        val dates = mutableListOf<LocalDate>()
        val emotions = mutableListOf<EmotionType>()
        val intensities = mutableListOf<Int>()

        val today = LocalDate.now()
        for (i in 0 until days) {
            val date = today.minusDays(i.toLong())
            dates.add(date)
            val record = records.find { it.date == date }
            emotions.add(record?.emotion ?: EmotionType.CALM)
            intensities.add(record?.intensity ?: 0)
        }

        return MoodTrend(dates.reversed(), emotions.reversed(), intensities.reversed())
    }
}
