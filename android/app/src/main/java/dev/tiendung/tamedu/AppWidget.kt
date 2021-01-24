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

    private fun updatePlayPhap(context: Context) {
        var txt = tamedu.phap.updatePlayPhap(context)
        if (txt == null) txt = APP_TITLE
        updateViews(context, { 
            it.setTextViewText(R.id.nghe_phap_button, tamedu.phap.buttonText()) 
            it.setTextViewText(R.id.marquee_status, txt)
        })
    }

    private fun updateQuoteView(context: Context) {
        val txt = tamedu.quote.speakCurrent(context)
        updateViews(context, {
            it.setTextViewText(R.id.speak_quote_toggle_button, tamedu.quote.toggleText())
            it.setTextViewText(R.id.quote_text, tamedu.quote.currentText())
            it.setTextViewText(R.id.marquee_status, txt)
        })
    }

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            SPEAK_QUOTE_TOGGLE -> {
                tamedu.quote.toggle()
                updateQuoteView(context)
            }
            NGHE_PHAP -> updatePlayPhap(context)
            PLAY_PHAP_BEGIN -> {
                val txt = "Đang nghe pháp \"${tamedu.phap.currentTitle()}\""
                toast(context, txt)
                updateViews(context, { 
                    it.setTextViewText(R.id.nghe_phap_button, tamedu.phap.buttonText()) 
                    it.setTextViewText(R.id.marquee_status, txt) 
                })
            }
            FINISH_PHAP -> {
                toast(context, "Kết thúc \"${tamedu.phap.currentTitle()}\"")
                updateViews(context, { 
                    it.setTextViewText(R.id.marquee_status, APP_TITLE)
                    it.setTextViewText(R.id.nghe_phap_button, tamedu.phap.buttonText()) 
                })
            }
            SAVE_QUOTE_IMAGE -> {
                val file = tamedu.quote.saveCurrentToFile(context)
                if (file != null) toast(context, "Lưu lời dạy tại $file")
            }
            NEW_QUOTE -> {
                tamedu.quote.newCurrent(context)
                updateQuoteView(context)
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
    setupIntent(context, views, NEW_QUOTE, R.id.quote_area)

    tamedu.quote.newCurrent(context)
    tamedu.phap.checkTimeToPlay(context)

    views.setTextViewText(R.id.speak_quote_toggle_button, tamedu.quote.toggleText())
    views.setTextViewText(R.id.nghe_phap_button, tamedu.phap.buttonText())
    views.setTextViewText(R.id.quote_text, tamedu.quote.currentText())
    views.setTextViewText(R.id.marquee_status, APP_TITLE)

    // Instruct the widget manager to update the widget
    appWidgetManager.updateAppWidget(appWidgetId, views)
    tamedu.quote.playBellOrSpeakCurrent(context)
 }
