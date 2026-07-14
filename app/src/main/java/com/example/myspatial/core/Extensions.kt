package com.example.myspatial.core

import android.content.Context
import java.text.SimpleDateFormat
import java.util.*

fun Context.getSharedPrefs() = getSharedPreferences(Constants.SHARED_PREFS_NAME, Context.MODE_PRIVATE)

fun Date.format(pattern: String = Constants.DATE_FORMAT): String {
    return SimpleDateFormat(pattern, Locale.getDefault()).format(this)
}

fun String.toDate(pattern: String = Constants.DATE_FORMAT): Date? {
    return try {
        SimpleDateFormat(pattern, Locale.getDefault()).parse(this)
    } catch (e: Exception) {
        null
    }
}

fun <T> MutableList<T>.removeFirstOrNull(): T? {
    return if (isNotEmpty()) removeAt(0) else null
}

fun <T> MutableList<T>.removeLastOrNull(): T? {
    return if (isNotEmpty()) removeAt(size - 1) else null
}
