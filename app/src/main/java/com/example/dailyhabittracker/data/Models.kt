package com.example.dailyhabittracker.data

data class Habit(
    var id: Long,
    var title: String,
    var notes: String? = null,
    // map date-string (yyyy-MM-dd) -> completed flag
    var isCompletedMap: MutableMap<String, Boolean> = mutableMapOf()
)

data class MoodEntry(
    var id: Long,
    var emoji: String,
    var note: String? = null,
    var timestamp: Long
)

data class Settings(
    var hydrationIntervalMinutes: Int = 120,
    var hydrationEnabled: Boolean = true
)