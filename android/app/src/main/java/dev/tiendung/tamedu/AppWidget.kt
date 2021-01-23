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

    fun updatePlayPhap(context: Context) {
        tamedu.phap.updatePlayPhap(context)
        updateViews(context, { it.setTextViewText(R.id.nghe_phap_button, tamedu.phap.buttonText()) })
    }

    fun speakQuoteToggle(context: Context) {
        tamedu.quote.toggle()
        tamedu.quote.speakCurrent()
        val txt = tamedu.quote.toggleText()
        updateViews(context, { it.setTextViewText(R.id.speak_quote_toggle_button, txt) })
    }

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            SPEAK_QUOTE_TOGGLE -> speakQuoteToggle(context)
            NGHE_PHAP -> updatePlayPhap(context)
            PLAY_PHAP_BEGIN -> {
                toast(context, "Đang nghe pháp '${tamedu.phap.currentTitle()}'")
                updateViews(context, { it.setTextViewText(R.id.nghe_phap_button, tamedu.phap.buttonText()) })
            }
            FINISH_PHAP -> {
                tamedu.phap.release()
                toast(context, "Kết thúc '${tamedu.phap.currentTitle()}'")
                updateViews(context, { it.setTextViewText(R.id.nghe_phap_button, tamedu.phap.buttonText()) })
            }
            SAVE_QUOTE_IMAGE -> {
                val file = tamedu.quote.saveCurrentToFile(context)
                toast(context, "Lưu lời dạy tại $file")
            }
            NEW_QUOTE -> {
                tamedu.quote.newCurrent(context)
                tamedu.quote.speakCurrent()
                updateViews(context, { it.setTextViewText(R.id.quote_text, tamedu.quote.currentText()) })
            }
            else -> super.onReceive(context, intent)
        }
    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

fun updateViews(context: Context, updateViews: (views: RemoteViews) -> Unit) {
    val appWidgetManager = AppWidgetManager.getInstance(context)
    val ids = appWidgetManager.getAppWidgetIds(ComponentName(context, AppWidget::class.java))
    for (appWidgetId in ids) {
        val views = RemoteViews(context.packageName, R.layout.app_widget)
        updateViews(views)
        appWidgetManager.updateAppWidget(appWidgetId, views)
    }
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
    setupIntent(context, views, SPEAK_QUOTE_TOGGLE, R.id.speak_quote_toggle_button)
    setupIntent(context, views, SAVE_QUOTE_IMAGE, R.id.save_quote_button)
    setupIntent(context, views, NEW_QUOTE, R.id.quote_content)

    tamedu.quote.newCurrent(context)
    tamedu.phap.checkToPlayInEarlyMorning(context)

    views.setTextViewText(R.id.speak_quote_toggle_button, tamedu.quote.toggleText())
    views.setTextViewText(R.id.nghe_phap_button, tamedu.phap.buttonText())
    views.setTextViewText(R.id.quote_text, tamedu.quote.currentText())

    // Instruct the widget manager to update the widget
    appWidgetManager.updateAppWidget(appWidgetId, views)
    tamedu.quote.playBellOrSpeakCurrent(context)
 }
