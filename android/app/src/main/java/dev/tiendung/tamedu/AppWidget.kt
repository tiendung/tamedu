package dev.tiendung.tamedu

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.view.View
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

    private fun countHabit(habitCountKey: String) {
        _currentCountKey = habitCountKey
        _currentCountAdded = 0
        _showHabitsBar = false
        _resetPressedCount = 0
    }

    override fun onReceive(context: Context, intent: Intent) {
        var txt: String? = null

        when (intent.action) {
            TODAY_SQUAT -> countHabit(SQUAT_COUNT_KEY)
            TODAY_PUSH -> countHabit(PUSH_COUNT_KEY)
            TODAY_PULL -> countHabit(PULL_COUNT_KEY)
            TODAY_ABS -> countHabit(ABS_COUNT_KEY)
            COUNT_TOTAL -> {
                tamedu.count.inc(context, _currentCountKey, _currentCountAdded)
                _showHabitsBar = true
                _resetPressedCount = 0
            }
            COUNT_1 ->  { _currentCountAdded +=  5; _resetPressedCount = 0 }
            COUNT_10 -> { _currentCountAdded += 10; _resetPressedCount = 0 }
            COUNT_RESET -> {
                _resetPressedCount += 1
                if (_resetPressedCount == 4) toast(context, "Press \"Reset\" one more to reset all counters")
                if (_resetPressedCount == 5) {
                    tamedu.count.reset()
                    _showHabitsBar = true
                }
                _currentCountAdded = 0
            }

            SPEAK_REMINDER_TOGGLE -> {
                if (tamedu.phap.isPlaying()) {
                    txt = tamedu.phap.finishPhap()
                } else {
                    tamedu.reminder.toggle()
                    txt = tamedu.reminder.speakCurrent()
                }
                if (txt == null) txt = APP_TITLE
            }

            NEW_REMINDER -> {
                tamedu.reminder.newCurrent(context)
                txt = tamedu.reminder.speakCurrent()
            }

            NGHE_PHAP -> txt = tamedu.phap.startPlayPhap(context)
            THU_GIAN -> txt = tamedu.phap.startPlayPhap(context)
            NGHE_PHAP_BEGIN -> txt = "Đang nghe \"${tamedu.phap.currentTitle()}\""
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

private var _resetPressedCount = 0
private var _currentCountKey: String = TODAY_SQUAT
private var _currentCountAdded: Int = 0
private var _showHabitsBar = true
private val _habitsCountVisibilities = arrayOf(View.GONE, View.VISIBLE)
private fun hideOrShow(i: Int): Int {
    return if (_showHabitsBar) _habitsCountVisibilities[i] else _habitsCountVisibilities[1-i]
}

fun updateViews(context: Context, views: RemoteViews, marqueeTxt: String?) {
    views.setTextViewText(R.id.reminder_text, tamedu.reminder.currentText())
    views.setTextViewText(R.id.thu_gian_button, tamedu.phap.thuGianButtonText(context))
    views.setTextViewText(R.id.speak_reminder_toggle_button, tamedu.phap.docButtonText())
    if (marqueeTxt != null) views.setTextViewText(R.id.marquee_status, marqueeTxt)

    views.setViewVisibility(R.id.habits_bar, hideOrShow(1))
    views.setViewVisibility(R.id.counts_bar, hideOrShow(0))

    views.setTextViewText(R.id.today_squat_button, "Chân ${tamedu.count.get(context, SQUAT_COUNT_KEY)}")
    views.setTextViewText(R.id.today_push_button, "Đẩy ${tamedu.count.get(context, PUSH_COUNT_KEY)}")
    views.setTextViewText(R.id.today_pull_button, "Kéo ${tamedu.count.get(context, PULL_COUNT_KEY)}")
    views.setTextViewText(R.id.today_abs_button, "Bụng ${tamedu.count.get(context, ABS_COUNT_KEY)}")
    views.setTextViewText(R.id.count_total_button, "${COUNT_KEY_TO_LABEL[_currentCountKey]} + $_currentCountAdded")

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
    setupIntent(context, views, COUNT_1, R.id.count_1_button)
    setupIntent(context, views, COUNT_10, R.id.count_10_button)
    setupIntent(context, views, COUNT_RESET, R.id.count_reset_button)

    if (!tamedu.phap.isPlaying()) {
        tamedu.reminder.newCurrent(context)
        tamedu.phap.checkTimeToPlay(context)
        tamedu.reminder.playBellOrSpeakCurrent(context)
        updateViews(context, views, APP_TITLE)
    }

    // Instruct the widget manager to update the widget
    appWidgetManager.updateAppWidget(appWidgetId, views)
 }
