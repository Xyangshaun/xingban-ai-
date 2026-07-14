package com.example.myspatial.data

import java.time.LocalDate

data class MoodRecord(
    val date: LocalDate,
    val emotion: EmotionType,
    val intensity: Int,
    val note: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

data class MoodTrend(
    val dates: List<LocalDate>,
    val emotions: List<EmotionType>,
    val intensities: List<Int>
)