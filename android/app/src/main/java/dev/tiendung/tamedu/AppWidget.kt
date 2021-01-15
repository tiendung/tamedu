package dev.tiendung.tamedu

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.MediaPlayer.OnPreparedListener
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
            updateAppWidget(context, appWidgetManager, appWidgetId, false)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        val newQuoteClicked = intent.action == "newQuote"
        val views = RemoteViews(context.packageName, R.layout.app_widget)
        val appWidgetManager = AppWidgetManager.getInstance(context)
        val man = AppWidgetManager.getInstance(context)
        val ids = man.getAppWidgetIds(ComponentName(context, AppWidget::class.java))
        for (appWidgetId in ids)
        {
            updateAppWidget(context, appWidgetManager, appWidgetId, newQuoteClicked)
        }
        val appWidget = ComponentName(context, AppWidget::class.java)
        appWidgetManager.updateAppWidget(appWidget, views)
    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

private fun getPendingIntentWidget(context: Context, action: String): PendingIntent
{
    // Construct an Intent which is pointing this class.
    val intent = Intent(context, AppWidget::class.java)
    intent.action = action
    // And this time we are sending a broadcast with getBroadcast
    return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
}

// Init a mediaPlayer to play quote audio
var _mediaPlayer: MediaPlayer = MediaPlayer()

internal fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int, newQuoteClicked: Boolean) {
    // Construct the RemoteViews object
    val views = RemoteViews(context.packageName, R.layout.app_widget)
   
    if (newQuoteClicked) {
      // Stop and release mediaPlayer before doing anything to stop previous audio if playing
        _mediaPlayer.stop()
        _mediaPlayer.release()
    } else {
        // Handle events only one
        views.setOnClickPendingIntent(R.id.new_quote_button, getPendingIntentWidget(context, "newQuote"))

        // Click on the quote image to open the main app
        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(context, 0, intent, 0)
        views.setOnClickPendingIntent(R.id.appwidget_image, pendingIntent)
    }

    // Show then play random quote
    val randomQuoteId = (0..1673).random() // 1314 -> longest quote
    showQuoteById(randomQuoteId, context, views, appWidgetId)

    // Instruct the widget manager to update the widget
    appWidgetManager.updateAppWidget(appWidgetId, views)

    // Play quote after update quote image to views
    if (newQuoteClicked) {
        playQuoteById(randomQuoteId, context)
    } else {
        // play a bell
        playQuoteById(-1, context)
    }
}

fun showQuoteById(quoteId: Int, context: Context, views: RemoteViews, appWidgetId: Int) {
    // Load image from url
    val imgUrl = "https://tiendung.github.io/quotes/650x/$quoteId.png"
    val appWidgetTarget = AppWidgetTarget(context, R.id.appwidget_image, views, appWidgetId)
    Glide.with(context)
            .asBitmap()
            .load(imgUrl)
            .override(1500)
            .into(appWidgetTarget)
}

fun playQuoteById(quoteId: Int, context: Context) {
    // Load audio from url
    val audioUrl: String
    if (quoteId == -1) {
        // Play a bell
        audioUrl = "https://raw.githubusercontent.com/tiendung/tiendung.github.io/main/_save/bell.ogg"
    } else {
        audioUrl = "https://tiendung.github.io/quotes/opus/$quoteId.ogg"
    }

    _mediaPlayer = MediaPlayer().apply {
        setAudioAttributes(
                AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
        )
        setDataSource(context, Uri.parse(audioUrl))
        setOnPreparedListener(OnPreparedListener { mp -> mp.start() })
        prepareAsync()
    }
}