package dev.tiendung.tamedu

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews

import dev.tiendung.tamedu.helpers.*

/**
 * Implementation of App Widget
 */
class AppWidget : AppWidgetProvider() {
    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {

        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        var txt: String? = null

        when (intent.action) {
            SPEAK_REMINDER_TOGGLE -> {
                tamedu.reminder.toggle()
                txt = tamedu.reminder.speakCurrent()
            }
            NGHE_PHAP -> {
                txt = tamedu.phap.updatePlayPhap(context)
            }
            THU_GIAN -> {
                txt = tamedu.phap.updatePlayPhap(context, true)
            }
            NGHE_PHAP_BEGIN -> {
                txt = "Đang nghe pháp \"${tamedu.phap.currentTitle()}\""
                toast(context, txt)
            }
            NGHE_PHAP_FINISH -> {
                txt = APP_TITLE
                toast(context, "Kết thúc \"${tamedu.phap.currentTitle()}\"")
            }
            NEW_REMINDER -> {
                tamedu.reminder.newCurrent(context)
                txt = tamedu.reminder.speakCurrent()
            }
            else -> super.onReceive(context, intent)
        } // when

        val appWidgetManager = AppWidgetManager.getInstance(context)
        val ids = appWidgetManager.getAppWidgetIds(ComponentName(context, AppWidget::class.java))
        for (appWidgetId in ids) {
            val views = RemoteViews(context.packageName, R.layout.app_widget)
            updateViews(context, views, txt)
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }        
    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

fun updateViews(context: Context, views: RemoteViews, marqueeTxt: String?) {
    views.setTextViewText(R.id.speak_reminder_toggle_button, tamedu.reminder.toggleText())
    views.setTextViewText(R.id.nghe_phap_button, tamedu.phap.buttonText())
    views.setTextViewText(R.id.reminder_text, tamedu.reminder.currentText())
    views.setTextViewText(R.id.thu_gian_button, tamedu.phap.thuGianButtonText(context))
    views.setInt(R.id.reminder_area, "setBackgroundColor", tamedu.reminder.currentBgColor())
    if (marqueeTxt != null)
        views.setTextViewText(R.id.marquee_status, marqueeTxt)
}

private fun setupIntent(context: Context, views: RemoteViews, action: String, id: Int) {
    val intent = Intent(context, AppWidget::class.java)
    intent.action = action
    val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0)
    views.setOnClickPendingIntent(id, pendingIntent)
}

internal fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {
    // Construct the RemoteViews object
    val views = RemoteViews(context.packageName, R.layout.app_widget)

    // Handle events
    setupIntent(context, views, NGHE_PHAP, R.id.nghe_phap_button)
    setupIntent(context, views, THU_GIAN, R.id.thu_gian_button)
    setupIntent(context, views, SPEAK_REMINDER_TOGGLE, R.id.speak_reminder_toggle_button)
    setupIntent(context, views, NEW_REMINDER, R.id.reminder_area)

    tamedu.reminder.newCurrent(context)
    tamedu.phap.checkTimeToPlay(context)

    updateViews(context, views, APP_TITLE)
    // Instruct the widget manager to update the widget
    appWidgetManager.updateAppWidget(appWidgetId, views)

    tamedu.reminder.playBellOrSpeakCurrent(context)
 }
