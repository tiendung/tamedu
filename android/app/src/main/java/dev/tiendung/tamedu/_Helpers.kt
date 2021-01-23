package dev.tiendung.tamedu.helpers

import android.content.Context
import android.content.Intent
import android.widget.Toast

private const val PATH = "dev.tiendung.tamedu.action."
const val FINISH_PHAP = PATH + "FINISH_PHAP"
const val PLAY_PHAP_BEGIN = PATH + "PLAY_PHAP_BEGIN"
const val NGHE_PHAP = PATH + "NGHE_PHAP"
const val SPEAK_QUOTE_TOGGLE = PATH + "SPEAK_QUOTE_TOGGLE"
const val SAVE_QUOTE_IMAGE = PATH + "SAVE_QUOTE_IMAGE"
const val NEW_QUOTE = PATH + "NEW_QUOTE"

fun Context.broadcastUpdateWidget(actionName: String) {
    Intent(this, dev.tiendung.tamedu.AppWidget::class.java).apply {
        action = actionName
        sendBroadcast(this)
    }
}

fun toast(context: Context, txt: String) {
    Toast.makeText(context, txt, Toast.LENGTH_LONG).show()
}
