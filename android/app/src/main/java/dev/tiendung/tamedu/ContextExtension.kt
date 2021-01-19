package dev.tiendung.tamedu

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import dev.tiendung.tamedu.helpers.*
import dev.tiendung.tamedu.data.*
import com.simplemobiletools.musicplayer.helpers.*

@SuppressLint("NewApi")
fun Context.sendIntent(action: String) {
    Intent(this, PhapService::class.java).apply {
        this.action = action
        try {
            if (isOreoPlus()) {
                startForegroundService(this)
            } else {
                startService(this)
            }
        } catch (ignored: Exception) {
        }
    }
}

fun Context.broadcastUpdateWidgetPlayingPhap(phap: Phap) {
    Intent(this, AppWidget::class.java).apply {
//        putExtra(NEW_PHAP, phap)
        action = PLAY_PHAP_BEGIN
        sendBroadcast(this)
    }
}

fun Context.broadcastUpdateWidgetFinishPhap(phap: Phap) {
    Intent(this, AppWidget::class.java).apply {
        action = FINISH_PHAP
        sendBroadcast(this)
    }
}