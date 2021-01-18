package dev.tiendung.tamedu

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import android.util.Log
import android.widget.RemoteViews
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.AppWidgetTarget

import dev.tiendung.tamedu.data.getRandomPhap
import dev.tiendung.tamedu.data.getRandomQuoteId
import dev.tiendung.tamedu.helpers.toast
import dev.tiendung.tamedu.helpers.copyFromAssetsToFile
import java.io.File

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

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)


        _speakQuoteToggleClicked = intent.action == "speakQuoteToggle"
        _newQuoteClicked = intent.action == "newQuote"

        _isInitOrAutoUpdate = intent.action == AppWidgetManager.ACTION_APPWIDGET_UPDATE

        if (_speakQuoteToggleClicked) {
            _allowToSpeakQuote = !_allowToSpeakQuote
        }
        
        var updateView = true

        if (intent.action == "saveQuoteImage") {
            updateView = false
            copyQuoteFromAssets(context, _currentQuoteId)
        }

        if (intent.action == "nghePhap") {
            _isInitOrAutoUpdate = false
            if (_phapIsPlaying) {
                _phapPlayer?.release()
                _phapIsPlaying = false
            } else {
                playRandomPhap(context)
            }
        }
        
        if (updateView) {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val ids = appWidgetManager.getAppWidgetIds(ComponentName(context, AppWidget::class.java))
            for (appWidgetId in ids) {
                updateAppWidget(context, appWidgetManager, appWidgetId)
            }
        }
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
var  _newQuoteClicked: Boolean = false
var _speakQuoteToggleClicked = false
var _allowToSpeakQuote: Boolean = false
var _isInitOrAutoUpdate: Boolean = true
var _currentQuoteId: Int = 0

internal fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {
    // Construct the RemoteViews object
    val views = RemoteViews(context.packageName, R.layout.app_widget)

    // Handle events
    views.setOnClickPendingIntent(R.id.nghe_phap_button,
            getPendingIntentWidget(context, "nghePhap"))

    views.setOnClickPendingIntent(R.id.speak_quote_toggle_button,
            getPendingIntentWidget(context, "speakQuoteToggle"))

    views.setOnClickPendingIntent(R.id.save_quote_button,
            getPendingIntentWidget(context, "saveQuoteImage"))

    views.setOnClickPendingIntent(R.id.appwidget_image,
            getPendingIntentWidget(context, "newQuote"))

    // Show and play random quote
    if (_isInitOrAutoUpdate || _newQuoteClicked) {
        _currentQuoteId = getRandomQuoteId()
        showQuoteById(_currentQuoteId, context, views, appWidgetId)
    }

    // Update speak_quote_toggle_button text
    var txt = if (_allowToSpeakQuote) "Dừng đọc" else "Đọc lời dạy"
    views.setTextViewText(R.id.speak_quote_toggle_button, txt)

    // Update nghe_phap_button text
    txt = if (_phapIsPlaying) "Dừng nghe" else "Nghe pháp"
    views.setTextViewText(R.id.nghe_phap_button, txt)

    // Instruct the widget manager to update the widget
    appWidgetManager.updateAppWidget(appWidgetId, views)

    _mediaPlayer?.release()
    // Play audio after update quote image to views
    if (_isInitOrAutoUpdate) {
        playQuoteById(-1, context) // play a bell
    } else if ((_newQuoteClicked || _speakQuoteToggleClicked) && _allowToSpeakQuote)
        playQuoteById(_currentQuoteId, context) // play quote
    
    _isInitOrAutoUpdate = true
}

fun copyQuoteFromAssets(context: Context, quoteId: Int) {
    File(context.getExternalFilesDir(null), "/quotes").mkdir()
    val file = File(context.getExternalFilesDir(null), "/quotes/quote$quoteId.png")
    toast(context, "Lưu lời dạy tại $file")
    copyFromAssetsToFile(context, "quotes/$quoteId.png", file)
}


fun showQuoteById(quoteId: Int, context: Context, views: RemoteViews, appWidgetId: Int) {
    val appWidgetTarget = AppWidgetTarget(context, R.id.appwidget_image, views, appWidgetId)
    Glide.with(context)
            .asBitmap()
            .load(Uri.parse("file:///android_asset/quotes/$quoteId.png"))
            .override(1200)
            .into(appWidgetTarget)
}

fun playQuoteById(quoteId: Int, context: Context) {
    // https://stackoverflow.com/questions/5747060/how-do-you-play-android-inputstream-on-mediaplayer
    val fileName = if (quoteId == -1) "bell.ogg" else "quotes/$quoteId.ogg"
    val fd = context.getAssets().openFd(fileName) // file descriptor

    _mediaPlayer = MediaPlayer().apply {
        setAudioAttributes(
                AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
        )
        setDataSource(fd.getFileDescriptor(), fd.getStartOffset(), fd.getLength())
        prepare()
        start()
    }
}

var _phapPlayer : MediaPlayer = MediaPlayer()
var _phapIsPlaying : Boolean = false
var _phapIsLoading : Boolean = false
var _currentPhapTitle : String = ""

fun playRandomPhap(context: Context) {
    if (_phapIsLoading) {
        toast(context, "Đang tải '${_currentPhapTitle}' ...")
        return
    }
    val (phapTitle, audioUrl) = getRandomPhap()
    _currentPhapTitle = phapTitle
    toast(context, "Đang tải '${_currentPhapTitle}' ...")

    _phapIsLoading = true
    _phapPlayer = MediaPlayer().apply {
        setAudioAttributes(
                AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
        )
        setDataSource(context, Uri.parse(audioUrl))
        setOnPreparedListener(MediaPlayer.OnPreparedListener { mp ->
            mp?.start()
            _phapIsPlaying = true
            _phapIsLoading = false
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val ids = appWidgetManager.getAppWidgetIds(ComponentName(context, AppWidget::class.java))

            Intent().also { intent ->
                intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
//                intent.action = "phapStartedToPlay"
//                intent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
//                PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT).send()
//                context.sendBroadcast(intent)
            }
        })
        prepareAsync()
    }
    _phapPlayer.setOnCompletionListener(MediaPlayer.OnCompletionListener { _ ->
        try {
            _phapIsPlaying = false
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val ids = appWidgetManager.getAppWidgetIds(ComponentName(context, AppWidget::class.java))
            Intent().also { intent ->
//                intent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
//                intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
//                context.sendBroadcast(intent)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    })
}
