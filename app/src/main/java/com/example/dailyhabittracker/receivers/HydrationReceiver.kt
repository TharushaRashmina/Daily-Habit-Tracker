package com.example.dailyhabittracker.receivers

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.dailyhabittracker.R

class HydrationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "hydration_ch"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val ch = NotificationChannel(channelId, "Hydration reminders", NotificationManager.IMPORTANCE_DEFAULT)
            nm.createNotificationChannel(ch)
        }
        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_water)
            .setContentTitle("Hydration Reminder")
            .setContentText("Time to drink water 💧")
            .setAutoCancel(true)
            .build()
        nm.notify(System.currentTimeMillis().toInt(), notification)
    }
}