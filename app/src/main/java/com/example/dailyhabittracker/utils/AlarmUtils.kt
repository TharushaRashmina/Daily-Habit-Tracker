package com.example.dailyhabittracker.utils

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.example.dailyhabittracker.receivers.HydrationReceiver

object AlarmUtils {
    private const val REQUEST_CODE = 1001

    fun scheduleHydration(context: Context, minutes: Int) {
        val alarmMgr = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, HydrationReceiver::class.java)
        val pending = PendingIntent.getBroadcast(context, REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        val interval = minutes * 60_000L
        val start = System.currentTimeMillis() + interval
        alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP, start, interval, pending)
    }

    fun cancelHydration(context: Context) {
        val alarmMgr = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, HydrationReceiver::class.java)
        val pending = PendingIntent.getBroadcast(context, REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        alarmMgr.cancel(pending)
    }
}