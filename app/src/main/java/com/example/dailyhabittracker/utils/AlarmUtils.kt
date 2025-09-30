package com.example.dailyhabittracker.utils

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.example.dailyhabittracker.receivers.HydrationReceiver

object AlarmUtils {
    private const val REQUEST_CODE = 1001
    private const val TAG = "AlarmUtils"

    fun scheduleHydration(context: Context, minutes: Int) {
        try {
            val alarmMgr = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent(context, HydrationReceiver::class.java)
            val pending = PendingIntent.getBroadcast(
                context,
                REQUEST_CODE,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val intervalMillis = minutes * 60_000L
            val startTime = System.currentTimeMillis() + intervalMillis

            Log.d(TAG, "Scheduling hydration reminder every $minutes minutes")

            // Handle different Android versions for alarm scheduling
            when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
                    // Android 12+ - Check if app can schedule exact alarms
                    if (alarmMgr.canScheduleExactAlarms()) {
                        alarmMgr.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, startTime, pending)
                        // Schedule the next one after this one fires
                        scheduleNextAlarm(context, intervalMillis, pending, alarmMgr)
                    } else {
                        // Fallback to inexact alarms
                        alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP, startTime, intervalMillis, pending)
                        Log.w(TAG, "Using inexact alarms - exact alarms not permitted")
                    }
                }
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> {
                    // Android 6+ - Use setExactAndAllowWhileIdle for Doze mode compatibility
                    alarmMgr.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, startTime, pending)
                    scheduleNextAlarm(context, intervalMillis, pending, alarmMgr)
                }
                else -> {
                    // Older Android versions
                    alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, startTime, intervalMillis, pending)
                }
            }

            Log.d(TAG, "Hydration reminder scheduled successfully")

        } catch (e: Exception) {
            Log.e(TAG, "Failed to schedule hydration reminder", e)
        }
    }

    private fun scheduleNextAlarm(context: Context, intervalMillis: Long, pending: PendingIntent, alarmMgr: AlarmManager) {
        // For exact alarms, we need to reschedule after each alarm fires
        // This will be handled in the HydrationReceiver
    }

    fun cancelHydration(context: Context) {
        try {
            val alarmMgr = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent(context, HydrationReceiver::class.java)
            val pending = PendingIntent.getBroadcast(
                context,
                REQUEST_CODE,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            alarmMgr.cancel(pending)
            pending.cancel()
            Log.d(TAG, "Hydration reminder cancelled")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to cancel hydration reminder", e)
        }
    }

    fun rescheduleHydration(context: Context) {
        // Get the current interval from preferences and reschedule
        val prefs = context.getSharedPreferences("wellness_prefs", Context.MODE_PRIVATE)
        val settings = com.google.gson.Gson().fromJson(
            prefs.getString("settings", null),
            com.example.dailyhabittracker.data.Settings::class.java
        ) ?: com.example.dailyhabittracker.data.Settings()

        if (settings.hydrationEnabled) {
            scheduleHydration(context, settings.hydrationIntervalMinutes)
        }
    }
}