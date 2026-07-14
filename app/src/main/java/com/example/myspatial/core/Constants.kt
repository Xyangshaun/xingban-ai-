package com.example.myspatial.core

object Constants {
    const val APP_NAME = "星伴AI"
    const val SHARED_PREFS_NAME = "com.example.myspatial.prefs"
    const val KEY_COMPANION = "companion_data"
    const val KEY_MOOD_RECORDS = "mood_records"
    const val KEY_MESSAGES = "chat_messages"
    const val DATE_FORMAT = "yyyy-MM-dd HH:mm:ss"
    const val SIMPLE_DATE_FORMAT = "yyyy-MM-dd"
}

object ScreenNames {
    const val CREATION = "creation"
    const val MAIN = "main"
    const val MOOD_DIARY = "mood_diary"
    const val SETTINGS = "settings"
}

object CompanionDefaults {
    const val DEFAULT_NAME = "星伴"
    const val DEFAULT_AVATAR = "default"
    const val DEFAULT_PERSONALITY = "温柔、善良、充满正能量"
}

object AIConfig {
    const val TIMEOUT_SECONDS = 30L
    const val MAX_RETRY_COUNT = 3
}
