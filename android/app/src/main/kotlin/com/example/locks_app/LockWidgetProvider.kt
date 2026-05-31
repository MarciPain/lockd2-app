package com.example.locks_app

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.widget.RemoteViews
import org.json.JSONArray

class LockWidgetProvider : AppWidgetProvider() {
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (id in appWidgetIds) {
            updateWidget(context, appWidgetManager, id)
        }
    }

    companion object {
        fun updateWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {
            val prefs = context.getSharedPreferences("HomeWidgetPlugin", Context.MODE_PRIVATE)
            val locksJson = prefs.getString("locks_json", null)

            val views = RemoteViews(context.packageName, R.layout.lock_widget)

            if (locksJson != null) {
                try {
                    val locks = JSONArray(locksJson)
                    val sb = StringBuilder()
                    for (i in 0 until locks.length()) {
                        val lock = locks.getJSONObject(i)
                        val name = lock.optString("name", "?")
                        val state = lock.optString("state", "")
                        val icon = when (state) {
                            "UNLOCK", "Nyitva", "Open", "Opened", "Unlocked" -> "🔓"
                            "LOCK", "Zárva", "Closed", "Locked" -> "🔒"
                            else -> "?"
                        }
                        if (sb.isNotEmpty()) sb.append("\n")
                        sb.append("$icon $name")
                    }
                    views.setTextViewText(R.id.widget_locks_text, sb.toString())
                } catch (_: Exception) {
                    views.setTextViewText(R.id.widget_locks_text, "?")
                }
            } else {
                views.setTextViewText(R.id.widget_locks_text, "–")
            }

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}
