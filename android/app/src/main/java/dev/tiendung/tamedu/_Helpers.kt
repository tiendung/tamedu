package dev.tiendung.tamedu.helpers

import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.simplemobiletools.musicplayer.helpers.*
import dev.tiendung.tamedu.data.*

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