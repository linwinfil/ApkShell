package com.maoxin.apkshell.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews

/** @author lmx
 * Created by lmx on 2020/2/25.
 */
class MainAppWidgetProvider : AppWidgetProvider() {
    companion object {
        val ACTION: String = "com.maoxin.apkshell.action.MainAppWidgetProvider"
    }

    override fun onUpdate(context: Context?, appWidgetManager: AppWidgetManager?, appWidgetIds: IntArray?) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        appWidgetIds?.also {
            for (i in appWidgetIds) {
                val id = appWidgetIds[i]
                onWidgetUpdate(context, appWidgetManager, id)
            }
        }

    }

    inline fun onWidgetUpdate(context: Context?, appWidgetManager: AppWidgetManager?, appWidgetId: Int) {
        val intent = Intent()
        intent.action = ACTION
        val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0)
        val removeViews = RemoteViews(context!!.packageName, /*layoutId*/0)
        removeViews.setOnClickPendingIntent(/*viewId*/0, pendingIntent)
        appWidgetManager?.updateAppWidget(appWidgetId, removeViews)
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context, intent)
        intent?.also {
            if (it.action.equals(ACTION)) {
                println("click action=$ACTION")

            }
        }
    }
}