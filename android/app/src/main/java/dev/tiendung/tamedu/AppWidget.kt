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

    fun updatePlayPhap(context: Context) {
        if (_phapIsPlaying) {
            _phapPlayer.release()
            _phapIsPlaying = false
            updateViews(context, { it.setTextViewText(R.id.nghe_phap_button, "Nghe pháp") })
        }
        else if (!_phapIsLoading) {
            _currentPhap = getRandomPhap()
            loadAndPlayPhap(context, _currentPhap)
            updateViews(context, { it.setTextViewText(R.id.nghe_phap_button, "Đang tải ...") })
            toast(context, "Đang tải '${_currentPhap.title}' ...")
        }
    }

    fun speakQuoteToggle(context: Context) {
        _allowToSpeakQuote = !_allowToSpeakQuote
        _quotePlayer.release()
        if (_allowToSpeakQuote) {
            updateViews(context, { it.setTextViewText(R.id.speak_quote_toggle_button, "Dừng đọc") })
            playAudioFile(_currentQuote!!.audioFd)
        } else {
            updateViews(context, { it.setTextViewText(R.id.speak_quote_toggle_button, "Đọc lời dạy") })
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            "speakQuoteToggle" -> speakQuoteToggle(context)
            "nghePhap" -> updatePlayPhap(context)
            PLAY_PHAP_BEGIN -> {
                toast(context, "Đang nghe pháp '${_currentPhap.title}'")
                updateViews(context, { it.setTextViewText(R.id.nghe_phap_button, "Dừng nghe") })
            }
            FINISH_PHAP -> {
                _phapPlayer.release()
                toast(context, "Kết thúc '${_currentPhap.title}'")
                updateViews(context, { it.setTextViewText(R.id.nghe_phap_button, "Nghe pháp") })
            }
            "saveQuoteImage" -> {
                val file = saveQuoteImageToFile(context, _currentQuote!!)
                toast(context, "Lưu lời dạy tại $file")
            }
            "newQuote" -> {
                _currentQuote = getRandomQuote(context)
                if (_allowToSpeakQuote) {
                    if (_quotePlayer.isPlaying()) { _quotePlayer.release() }
                    playAudioFile(_currentQuote!!.audioFd)
                }

                updateViews(context, { it.setTextViewText(R.id.quote_text, _currentQuote!!.text) })
            }
//            PLAY_RANDOM_PHAP -> {
//                _phapIsLoading = true
//                context.sendIntent(PLAY_RANDOM_PHAP)
//            }
            else -> super.onReceive(context, intent)
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

// Init a mediaPlayer to play quote audio
var _quotePlayer: MediaPlayer = MediaPlayer()
var _allowToSpeakQuote: Boolean = false
var _currentQuote: Quote? = null

// Init a mediaPlayer to play phap
var _phapPlayer : MediaPlayer = MediaPlayer()
var _phapIsLoading : Boolean = false
var _phapIsPlaying : Boolean = false
var _currentPhap : Phap = getRandomPhap()

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
    // setupIntent(context, views, PLAY_RANDOM_PHAP, R.id.nghe_phap_button)
    setupIntent(context, views, "nghePhap", R.id.nghe_phap_button)
    setupIntent(context, views, "speakQuoteToggle", R.id.speak_quote_toggle_button)
    setupIntent(context, views, "saveQuoteImage", R.id.save_quote_button)
    setupIntent(context, views, "newQuote", R.id.quote_content)

    _currentQuote = getRandomQuote(context)
    views.setTextViewText(R.id.quote_text, _currentQuote!!.text)
    playAudioFile(context.getAssets().openFd(BELL_FILE_NAME))
    // Instruct the widget manager to update the widget
    appWidgetManager.updateAppWidget(appWidgetId, views)

}

fun playAudioFile(fd: AssetFileDescriptor) {
    // https://stackoverflow.com/questions/5747060/how-do-you-play-android-inputstream-on-mediaplayer
    _quotePlayer = MediaPlayer().apply {
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

fun loadAndPlayPhap(context: Context, phap: Phap) {
    _phapIsLoading = true
    _phapPlayer = MediaPlayer().apply {
        setAudioAttributes(
                AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
        )
        setDataSource(context, phap.audioUri)
        setOnPreparedListener(MediaPlayer.OnPreparedListener { mp ->
            _phapIsLoading = false
            _phapIsPlaying = true
            mp?.start()
            context.broadcastUpdateWidgetPlayingPhap(phap)
        })
        setOnCompletionListener(MediaPlayer.OnCompletionListener { mp ->
            mp.release()
            _phapIsPlaying = false
            context.broadcastUpdateWidgetFinishPhap(phap)
        })
        prepareAsync()
    }
}