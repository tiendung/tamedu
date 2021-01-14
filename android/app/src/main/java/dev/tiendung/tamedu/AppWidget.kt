package dev.tiendung.tamedu

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.widget.RemoteViews

import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.AppWidgetTarget

import android.media.MediaPlayer
import android.media.AudioAttributes
import android.net.Uri

import android.content.Intent
import android.app.PendingIntent

/**
 * Implementation of App Widget functionality.
 */
class AppWidget : AppWidgetProvider() {
    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

internal fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {
    // Construct the RemoteViews object
    val views = RemoteViews(context.packageName, R.layout.app_widget)     

    val appWidgetTarget = AppWidgetTarget(context, R.id.appwidget_image, views, appWidgetId)
    
    showRandomQuote(context, appWidgetTarget)

    // https://developer.android.com/guide/topics/appwidgets/index.html#java
    val intent = Intent(context, MainActivity::class.java)
    val pendingIntent = PendingIntent.getActivity(context, 0, intent, 0)
    views.setOnClickPendingIntent(R.id.appwidget_image, pendingIntent)
    // Instruct the widget manager to update the widget
    appWidgetManager.updateAppWidget(appWidgetId, views)
}


fun showRandomQuote(context: Context, appWidgetTarget: AppWidgetTarget) {
    val randomQuoteId = (0..1673).random()
    showQuoteById(randomQuoteId, context, appWidgetTarget)
}

fun showQuoteById(quoteId: Int, context: Context, appWidgetTarget: AppWidgetTarget) {
    // Load image and audio from url
    val imgUrl = "https://tiendung.github.io/quotes/650x/$quoteId.png"
    val audioUrl = "https://tiendung.github.io/quotes/opus/$quoteId.ogg"

    Glide.with(context)
            .asBitmap()
            .load(imgUrl)
            .override(1200)
            .into(appWidgetTarget)

    val myUri: Uri = Uri.parse(audioUrl)
    val mediaPlayer: MediaPlayer? = MediaPlayer().apply {
        setAudioAttributes(
                AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
        )
        setDataSource(context, myUri)
        prepare()
        start()
    }
}