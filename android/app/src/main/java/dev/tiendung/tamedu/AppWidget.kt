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
            TODAY_SQUAT -> tamedu.count.countHabit(SQUAT_COUNT_KEY)
            TODAY_PUSH -> tamedu.count.countHabit(PUSH_COUNT_KEY)
            TODAY_PULL -> tamedu.count.countHabit(PULL_COUNT_KEY)
            TODAY_ABS -> tamedu.count.countHabit(ABS_COUNT_KEY)
            COUNT_TOTAL -> tamedu.count.countUpdateTotal(context)
            COUNT_5 ->  tamedu.count.addCurrentCount(5)
            COUNT_10 -> tamedu.count.addCurrentCount(10)
            COUNT_RESET -> tamedu.count.countReset(context)

            SPEAK_REMINDER_TOGGLE -> {
                if (tamedu.phap.isPlaying()) {
                    txt = tamedu.phap.pausePhap()
                } else {
                    tamedu.reminder.toggle()
                    // txt = tamedu.reminder.speakCurrent(context)
                    // if (txt == null) txt = APP_TITLE
                    tamedu.reminder.speakCurrent(context)
                }
            }

            NEW_REMINDER -> {
                tamedu.reminder.newCurrent(context)
                // txt = tamedu.reminder.speakCurrent(context)
                tamedu.reminder.speakCurrent(context)
            }

            NGHE_PHAP -> txt = tamedu.phap.startPlayPhap(context)
            THU_GIAN -> txt = tamedu.phap.startPlayThuGian(context)
            // NGHE_PHAP_BEGIN -> txt = "Đang nghe \"${tamedu.phap.currentTitle()}\""
            NGHE_PHAP_FINISH -> txt = APP_TITLE
            NGHE_PHAP_PROGRESS -> txt = "\"${tamedu.phap.currentTitle()}\" ${tamedu.phap.getCurrentPhapPosition()}"

            else -> {
                super.onReceive(context, intent)
                return
            }
        } // when

        // Update view for known events only
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

private fun updateViews(context: Context, views: RemoteViews, marqueeTxt: String?) {
    views.setTextViewText(R.id.reminder_text, tamedu.reminder.currentText())
    views.setTextViewText(R.id.thu_gian_button, tamedu.phap.thuGianButtonText(context))
    views.setTextViewText(R.id.nghe_phap_button, tamedu.phap.nghePhapButtonText(context))
    views.setTextViewText(R.id.speak_reminder_toggle_button, tamedu.phap.docButtonText())
    if (marqueeTxt != null) views.setTextViewText(R.id.marquee_status, marqueeTxt)

    views.setViewVisibility(R.id.habits_bar, tamedu.count.hideOrShow(1))
    views.setViewVisibility(R.id.counts_bar, tamedu.count.hideOrShow(0))

    views.setTextViewText(R.id.today_squat_button, "Chân ${tamedu.count.get(context, SQUAT_COUNT_KEY)}")
    views.setTextViewText(R.id.today_push_button, "Đẩy ${tamedu.count.get(context, PUSH_COUNT_KEY)}")
    views.setTextViewText(R.id.today_pull_button, "Kéo ${tamedu.count.get(context, PULL_COUNT_KEY)}")
    views.setTextViewText(R.id.today_abs_button, "Bụng ${tamedu.count.get(context, ABS_COUNT_KEY)}")
    views.setTextViewText(R.id.count_total_button, tamedu.count.currentCountLabel())

    views.setInt(R.id.today_squat_button, "setTextColor", tamedu.count.color(context, SQUAT_COUNT_KEY))
    views.setInt(R.id.today_push_button, "setTextColor", tamedu.count.color(context, PUSH_COUNT_KEY))
    views.setInt(R.id.today_pull_button, "setTextColor", tamedu.count.color(context, PULL_COUNT_KEY))
    views.setInt(R.id.today_abs_button, "setTextColor", tamedu.count.color(context, ABS_COUNT_KEY))
    views.setInt(R.id.thu_gian_button, "setTextColor", tamedu.count.color(context, THU_GIAN_COUNT_KEY))
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

    setupIntent(context, views, TODAY_SQUAT, R.id.today_squat_button)
    setupIntent(context, views, TODAY_PUSH, R.id.today_push_button)
    setupIntent(context, views, TODAY_PULL, R.id.today_pull_button)
    setupIntent(context, views, TODAY_ABS, R.id.today_abs_button)

    setupIntent(context, views, COUNT_TOTAL, R.id.count_total_button)
    setupIntent(context, views, COUNT_5, R.id.count_5_button)
    setupIntent(context, views, COUNT_10, R.id.count_10_button)
    setupIntent(context, views, COUNT_RESET, R.id.count_reset_button)

    tamedu.reminder.newCurrent(context)
    tamedu.tasks.checkAndRun(context)
    updateViews(context, views, APP_TITLE)

    // Instruct the widget manager to update the widget
    appWidgetManager.updateAppWidget(appWidgetId, views)
 }
