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
    val widgetText = context.getString(R.string.appwidget_text)
    // Construct the RemoteViews object
    val views = RemoteViews(context.packageName, R.layout.app_widget)
    views.setTextViewText(R.id.appwidget_text, widgetText)

    // Show random quote
    val quoteIndex = (0..1673).random()

    // Load image from url
    val imgUrl = "https://tiendung.github.io/quotes/650x/${quoteIndex}.png"
    val appWidgetTarget = AppWidgetTarget(context, R.id.appwidget_image, views, appWidgetId)
//    val providerInfo = AppWidgetManager.getInstance(context).getAppWidgetInfo(appWidgetId)
    Glide.with(context)
            .asBitmap()
            .load(imgUrl)
            .override(800)
            .into(appWidgetTarget)

    val audioUrl = "https://tiendung.github.io/quotes/opus/${quoteIndex}.ogg"
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

    // Instruct the widget manager to update the widget
    appWidgetManager.updateAppWidget(appWidgetId, views)
}