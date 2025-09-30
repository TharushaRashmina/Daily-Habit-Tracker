package com.example.dailyhabittracker.receivers

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.dailyhabittracker.MainActivity
import com.example.dailyhabittracker.R
import com.example.dailyhabittracker.data.PreferencesHelper
import com.example.dailyhabittracker.utils.AlarmUtils
import kotlin.random.Random

class HydrationReceiver : BroadcastReceiver() {

    companion object {
        const val ACTION_DRINK_WATER = "action_drink_water"
        const val ACTION_SNOOZE = "action_snooze"
        const val ACTION_TURN_OFF = "action_turn_off"
        const val NOTIFICATION_ID = 1001

        private val motivationalMessages = arrayOf(
            "Stay hydrated, stay healthy! 💧",
            "Your body needs water to function properly 🌊",
            "Time for a refreshing drink! 💦",
            "Keep your energy up with some water! ⚡",
            "Hydration is key to better focus! 🧠",
            "A glass of water a day keeps fatigue away! 💪",
            "Your skin will thank you for staying hydrated! ✨",
            "Water: nature's energy drink! 🌿",
            "Stay cool, stay hydrated! 🧊",
            "Drink water and feel the difference! 🌟"
        )
    }

    override fun onReceive(context: Context, intent: Intent?) {
        when (intent?.action) {
            ACTION_DRINK_WATER -> {
                handleDrinkWaterAction(context)
                return
            }
            ACTION_SNOOZE -> {
                handleSnoozeAction(context)
                return
            }
            ACTION_TURN_OFF -> {
                handleTurnOffAction(context)
                return
            }
        }

        // Main hydration notification
        showHydrationNotification(context)

        // For Android 6+ exact alarms, reschedule the next reminder
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            AlarmUtils.rescheduleHydration(context)
        }
    }

    private fun showHydrationNotification(context: Context) {
        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "hydration_ch"

        // Create notification channel with enhanced settings
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Hydration Reminders",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Reminders to drink water throughout the day"
                enableLights(true)
                lightColor = Color.CYAN
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 250, 250, 250)
                setShowBadge(true)
            }
            nm.createNotificationChannel(channel)
        }

        // Create intents for notification actions
        val mainIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val mainPendingIntent = PendingIntent.getActivity(
            context, 0, mainIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val drinkIntent = Intent(context, HydrationReceiver::class.java).apply {
            action = ACTION_DRINK_WATER
        }
        val drinkPendingIntent = PendingIntent.getBroadcast(
            context, 1, drinkIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val snoozeIntent = Intent(context, HydrationReceiver::class.java).apply {
            action = ACTION_SNOOZE
        }
        val snoozePendingIntent = PendingIntent.getBroadcast(
            context, 2, snoozeIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val turnOffIntent = Intent(context, HydrationReceiver::class.java).apply {
            action = ACTION_TURN_OFF
        }
        val turnOffPendingIntent = PendingIntent.getBroadcast(
            context, 3, turnOffIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Get random motivational message
        val randomMessage = motivationalMessages[Random.nextInt(motivationalMessages.size)]

        // Get daily water count
        val prefs = PreferencesHelper(context)
        val waterCount = prefs.getDailyWaterCount()
        val targetGlasses = 8 // Daily target

        // Build enhanced notification
        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_water)
            .setContentTitle("💧 Hydration Time!")
            .setContentText(randomMessage)
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText("$randomMessage\n\nToday's progress: $waterCount/$targetGlasses glasses")
                .setBigContentTitle("💧 Time to Hydrate!")
                .setSummaryText("Daily Habit Tracker"))
            .setContentIntent(mainPendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            .setVibrate(longArrayOf(0, 250, 250, 250))
            .setLights(Color.CYAN, 1000, 1000)
            .addAction(
                R.drawable.ic_water,
                "Drink Water ✓",
                drinkPendingIntent
            )
            .addAction(
                R.drawable.ic_settings,
                "Snooze 15min",
                snoozePendingIntent
            )
            .addAction(
                R.drawable.ic_cancel,
                "Turn Off",
                turnOffPendingIntent
            )
            .setProgress(targetGlasses, waterCount, false)
            .build()

        nm.notify(NOTIFICATION_ID, notification)
    }

    private fun handleDrinkWaterAction(context: Context) {
        val prefs = PreferencesHelper(context)
        prefs.incrementDailyWaterCount()

        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Show success notification
        val successNotification = NotificationCompat.Builder(context, "hydration_ch")
            .setSmallIcon(R.drawable.ic_water)
            .setContentTitle("Great job! 🎉")
            .setContentText("Water logged successfully. Keep it up!")
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setTimeoutAfter(3000) // Auto dismiss after 3 seconds
            .build()

        nm.notify(NOTIFICATION_ID + 1, successNotification)
        nm.cancel(NOTIFICATION_ID) // Remove the original notification
    }

    private fun handleSnoozeAction(context: Context) {
        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nm.cancel(NOTIFICATION_ID)

        // Schedule reminder for 15 minutes later
        val snoozeNotification = NotificationCompat.Builder(context, "hydration_ch")
            .setSmallIcon(R.drawable.ic_water)
            .setContentTitle("Reminder Snoozed")
            .setContentText("We'll remind you again in 15 minutes")
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setTimeoutAfter(2000)
            .build()

        nm.notify(NOTIFICATION_ID + 2, snoozeNotification)

        // TODO: Schedule next reminder in 15 minutes
        // This would require AlarmUtils integration
    }

    private fun handleTurnOffAction(context: Context) {
        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nm.cancel(NOTIFICATION_ID)

        // Permanently disable hydration reminders
        val prefs = PreferencesHelper(context)
        val currentSettings = prefs.settings
        prefs.settings = currentSettings.copy(hydrationEnabled = false)

        // Cancel all future alarms
        AlarmUtils.cancelHydration(context)

        // Show turned off notification
        val turnedOffNotification = NotificationCompat.Builder(context, "hydration_ch")
            .setSmallIcon(R.drawable.ic_water)
            .setContentTitle("Hydration Reminders Turned Off")
            .setContentText("Reminders disabled. You can re-enable them in Settings.")
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setTimeoutAfter(4000) // Auto dismiss after 4 seconds
            .addAction(
                R.drawable.ic_settings,
                "Settings",
                PendingIntent.getActivity(
                    context,
                    4,
                    Intent(context, MainActivity::class.java),
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
            )
            .build()

        nm.notify(NOTIFICATION_ID + 3, turnedOffNotification)
    }
}