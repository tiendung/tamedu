package dev.tiendung.tamedu.helpers

import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.simplemobiletools.musicplayer.helpers.*
import dev.tiendung.tamedu.data.*

private const val PATH = "dev.tiendung.tamedu.action."
const val FINISH_PHAP = PATH + "FINISH_PHAP"
const val PLAY_RANDOM_PHAP = PATH + "PLAY_RANDOM_PHAP"
const val PLAY_PHAP_BEGIN = PATH + "PLAY_PHAP_BEGIN"
const val BROADCAST_STATUS = PATH + "BROADCAST_STATUS"
const val NGHE_PHAP = PATH + "nghePhap"
const val SPEAK_QUOTE_TOGGLE = PATH + "speakQuoteToggle"
const val SAVE_QUOTE_IMAGE = PATH + "saveQuoteImage"
const val NEW_QUOTE = PATH + "newQuote"

fun getNghePhapButtonText(phapIsPlaying: Boolean, phapIsLoading: Boolean, stopPhapClicksCount: Int): String {
    return when (phapIsPlaying) {
        true  -> if (stopPhapClicksCount == 0) "Dừng nghe" else "Dừng nghe ($stopPhapClicksCount)"
        false -> when (phapIsLoading) {
            true  -> "Đang tải ..."
            false -> "Nghe pháp"
        }
    }
}

fun Context.broadcastUpdateWidgetPlayingPhap() {
    Intent(this, dev.tiendung.tamedu.AppWidget::class.java).apply {
        action = PLAY_PHAP_BEGIN
        sendBroadcast(this)
    }
}

fun Context.broadcastUpdateWidgetFinishPhap() {
    Intent(this, dev.tiendung.tamedu.AppWidget::class.java).apply {
        action = FINISH_PHAP
        sendBroadcast(this)
    }
}

fun toast(context: Context, txt: String) {
    Toast.makeText(context, txt, Toast.LENGTH_LONG).show()
}