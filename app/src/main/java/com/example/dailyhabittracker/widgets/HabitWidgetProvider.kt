package com.example.dailyhabittracker.widgets

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.widget.RemoteViews
import com.example.dailyhabittracker.R
import com.example.dailyhabittracker.data.PreferencesHelper
import org.threeten.bp.LocalDate


class HabitWidgetProvider : AppWidgetProvider() {
    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        updateAllWidgets(context)
    }

    companion object {
        fun updateAllWidgets(context: Context) {
            val pm = PreferencesHelper(context)
            val percent = computePercent(pm)
            val views = RemoteViews(context.packageName, R.layout.widget_habit)
            views.setTextViewText(R.id.tvPercentage, "$percent%")
            views.setProgressBar(R.id.widgetProgress, 100, percent, false)
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val thisWidget = ComponentName(context, HabitWidgetProvider::class.java)
            val ids = appWidgetManager.getAppWidgetIds(thisWidget)
            for (id in ids) appWidgetManager.updateAppWidget(id, views)
        }

        private fun computePercent(prefs: PreferencesHelper): Int {
            val today = LocalDate.now().toString()
            val list = prefs.habits
            if (list.isEmpty()) return 0
            val done = list.count { it.isCompletedMap[today] == true }
            return done * 100 / list.size
        }
    }
}