package com.example.dailyhabittracker.data

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class PreferencesHelper(private val context: Context) {
    private val prefs = context.getSharedPreferences("wellness_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    var habits: MutableList<Habit>
        get() {
            val json = prefs.getString(KEY_HABITS, null) ?: return mutableListOf()
            val type = object : TypeToken<MutableList<Habit>>() {}.type
            return gson.fromJson(json, type)
        }
        set(value) {
            prefs.edit().putString(KEY_HABITS, gson.toJson(value)).apply()
        }

    var moods: MutableList<MoodEntry>
        get() {
            val json = prefs.getString(KEY_MOODS, null) ?: return mutableListOf()
            val type = object : TypeToken<MutableList<MoodEntry>>() {}.type
            return gson.fromJson(json, type)
        }
        set(value) {
            prefs.edit().putString(KEY_MOODS, gson.toJson(value)).apply()
        }

    var settings: Settings
        get() {
            val json = prefs.getString(KEY_SETTINGS, null) ?: return Settings()
            return gson.fromJson(json, Settings::class.java)
        }
        set(value) {
            prefs.edit().putString(KEY_SETTINGS, gson.toJson(value)).apply()
        }

    fun saveHabit(habit: Habit) {
        val list = habits
        val idx = list.indexOfFirst { it.id == habit.id }
        if (idx >= 0) list[idx] = habit else list.add(habit)
        habits = list
    }

    fun deleteHabit(id: Long) {
        habits = habits.filter { it.id != id }.toMutableList()
    }

    fun addMood(m: MoodEntry) {
        val list = moods
        list.add(0, m) // newest first
        moods = list
    }

    // Water tracking methods for hydration notifications
    fun getDailyWaterCount(): Int {
        val today = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(java.util.Date())
        return prefs.getInt("water_count_$today", 0)
    }

    fun incrementDailyWaterCount() {
        val today = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(java.util.Date())
        val currentCount = getDailyWaterCount()
        val maxGlasses = 8

        // Only increment if we haven't reached the maximum
        if (currentCount < maxGlasses) {
            prefs.edit().putInt("water_count_$today", currentCount + 1).apply()
        }
    }

    fun resetDailyWaterCount() {
        val today = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(java.util.Date())
        prefs.edit().putInt("water_count_$today", 0).apply()
    }

    companion object {
        private const val KEY_HABITS = "habits"
        private const val KEY_MOODS = "moods"
        private const val KEY_SETTINGS = "settings"
    }
}