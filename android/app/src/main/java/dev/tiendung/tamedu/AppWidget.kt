package dev.tiendung.tamedu

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.res.AssetFileDescriptor
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.widget.RemoteViews
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.AppWidgetTarget

import dev.tiendung.tamedu.data.*
import dev.tiendung.tamedu.helpers.*

/**
 * Implementation of App Widge
 */
class AppWidget : AppWidgetProvider() {
    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {

        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        when (action) {
            PLAY_RANDOM_PHAP -> {
                _phapIsLoading = true
                context.sendIntent(action)
            }
            PLAY_PHAP_BEGIN -> {
                _phapIsPlaying = true
                _phapIsLoading = false
                toast(context, "Đang nghe pháp ${_currentPhap.title}")
            }
            FINISH_PHAP -> {
                _phapIsPlaying = false
                toast(context, "Kết thúc nghe pháp ${_currentPhap.title}")
            }
            else -> super.onReceive(context, intent)
        }

        _speakQuoteToggleClicked = intent.action == "speakQuoteToggle"
        _newQuoteClicked = intent.action == "newQuote"

        _isInitOrAutoUpdate = intent.action == AppWidgetManager.ACTION_APPWIDGET_UPDATE

        if (_speakQuoteToggleClicked) {
            _allowToSpeakQuote = !_allowToSpeakQuote
        }
        
        var updateView = true

        if (intent.action == "saveQuoteImage") {
            updateView = false
            val file = saveQuoteImageToFile(context, _currentQuote!!)
            toast(context, "Lưu lời dạy tại $file")
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
        context.sendIntent(BROADCAST_STATUS)
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

private fun setupIntent(context: Context, views: RemoteViews, action: String, id: Int) {
    val intent = Intent(context, AppWidget::class.java)
    intent.action = action
    val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0)
    views.setOnClickPendingIntent(id, pendingIntent)
}

// Init a mediaPlayer to play quote audio
var _mediaPlayer: MediaPlayer = MediaPlayer()
var  _newQuoteClicked: Boolean = false
var _speakQuoteToggleClicked = false
var _allowToSpeakQuote: Boolean = false
var _isInitOrAutoUpdate: Boolean = true
var _currentQuote: Quote? = null

internal fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {
    // Construct the RemoteViews object
    val views = RemoteViews(context.packageName, R.layout.app_widget)

    // Handle events
    // setupIntent(context, views, PLAY_RANDOM_PHAP, R.id.nghe_phap_button)
    setupIntent(context, views, "nghePhap", R.id.nghe_phap_button)
    setupIntent(context, views, "speakQuoteToggle", R.id.speak_quote_toggle_button)
    setupIntent(context, views, "saveQuoteImage", R.id.save_quote_button)
    setupIntent(context, views, "newQuote", R.id.appwidget_image)

    // Show quote
    if (_isInitOrAutoUpdate || _newQuoteClicked) {
        _currentQuote = getRandomQuote(context)
        showQuote(_currentQuote!!, context, views, appWidgetId)
    }

    // Update speak_quote_toggle_button text
    var txt = if (_allowToSpeakQuote) "Dừng đọc" else "Đọc lời dạy"
    views.setTextViewText(R.id.speak_quote_toggle_button, txt)

    // Update nghe_phap_button text
    txt = if (_phapIsPlaying) "Dừng nghe" else "Nghe pháp"
    views.setTextViewText(R.id.nghe_phap_button, txt)

    // Instruct the widget manager to update the widget
    appWidgetManager.updateAppWidget(appWidgetId, views)

    // Play audio after update quote image to views
    playQuoteOrBell(context)

    _isInitOrAutoUpdate = true
}

fun showQuote(quote: Quote, context: Context, views: RemoteViews, appWidgetId: Int) {
    val appWidgetTarget = AppWidgetTarget(context, R.id.appwidget_image, views, appWidgetId)
    Glide.with(context)
            .asBitmap()
            .load(quote.imageUri)
            .override(1200)
            .into(appWidgetTarget)
}

fun playQuoteOrBell(context: Context) {
    _mediaPlayer?.release()
    if (_isInitOrAutoUpdate)
        playAudioFile(context.getAssets().openFd(BELL_FILE_NAME))
    else if ((_newQuoteClicked || _speakQuoteToggleClicked) && _allowToSpeakQuote)
        playAudioFile(_currentQuote!!.audioFd)
}

fun playAudioFile(fd: AssetFileDescriptor) {
    // https://stackoverflow.com/questions/5747060/how-do-you-play-android-inputstream-on-mediaplayer
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
var _currentPhap : Phap = getRandomPhap()

fun playRandomPhap(context: Context) {
//    return
    if (_phapIsLoading) {
        toast(context, "Đang tải '${_currentPhap.title}' ...")
        return
    }
    _currentPhap = getRandomPhap()
    toast(context, "Đang tải '${_currentPhap.title}' ...")

    _phapIsLoading = true
    _phapPlayer = MediaPlayer().apply {
        setAudioAttributes(
                AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
        )
        setDataSource(context, _currentPhap.audioUri)
        setOnPreparedListener(MediaPlayer.OnPreparedListener { mp ->
            mp?.start()
            context.broadcastUpdateWidgetPlayingPhap(_currentPhap)
        })
        setOnCompletionListener(MediaPlayer.OnCompletionListener { mp ->
            mp?.release()
            context.broadcastUpdateWidgetFinishPhap(_currentPhap)
        })
        prepareAsync()
    }
}
