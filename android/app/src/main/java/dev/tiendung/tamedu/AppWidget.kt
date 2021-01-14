package dev.tiendung.tamedu

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import android.widget.RemoteViews
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.AppWidgetTarget

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
    
    // Show and play random quote on widget view
    showAndPlayRandomQuote(context, appWidgetTarget)

    // Click on the quote image to update the widget
    val intent = Intent(context, AppWidget::class.java)
    intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE)
    intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, intArrayOf(appWidgetId))
    val pendingIntent = PendingIntent.getBroadcast(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT)
    views.setOnClickPendingIntent(R.id.appwidget_image, pendingIntent)

    // Click on the quote image to open the main app
    // val intent = Intent(context, MainActivity::class.java)
    // val pendingIntent = PendingIntent.getActivity(context, 0, intent, 0)
    // views.setOnClickPendingIntent(R.id.appwidget_image, pendingIntent)

    // Instruct the widget manager to update the widget
    appWidgetManager.updateAppWidget(appWidgetId, views)
}


fun showAndPlayRandomQuote(context: Context, appWidgetTarget: AppWidgetTarget) {
    val randomQuoteId = (0..1673).random()
    showQuoteById(randomQuoteId, context, appWidgetTarget) // 1314 -> longest quote
    playQuoteById(randomQuoteId, context)
}

fun showQuoteById(quoteId: Int, context: Context, appWidgetTarget: AppWidgetTarget) {
    // Load image from url
    val imgUrl = "https://tiendung.github.io/quotes/650x/$quoteId.png"
    Glide.with(context)
            .asBitmap()
            .load(imgUrl)
            .override(1500)
            .into(appWidgetTarget)
}

//private var _mediaPlayer: MediaPlayer? = null
private var _mediaPlayer: MediaPlayer = MediaPlayer()
fun playQuoteById(quoteId: Int, context: Context) {
    // Load audio from url
    val audioUrl = "https://tiendung.github.io/quotes/opus/$quoteId.ogg"
    val myUri: Uri = Uri.parse(audioUrl)

    _mediaPlayer.stop()
    _mediaPlayer = MediaPlayer().apply {
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