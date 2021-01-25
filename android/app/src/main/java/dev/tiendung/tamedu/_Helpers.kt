package dev.tiendung.tamedu.helpers

import android.content.Context
import android.content.Intent
import android.widget.Toast

private const val PATH = "dev.tiendung.tamedu.action."
const val NGHE_PHAP_FINISH = PATH + "NGHE_PHAP_FINISH"
const val NGHE_PHAP_BEGIN = PATH + "NGHE_PHAP_BEGIN"
const val NGHE_PHAP = PATH + "NGHE_PHAP"
const val SPEAK_REMINDER_TOGGLE = PATH + "SPEAK_REMINDER_TOGGLE"
const val NEW_REMINDER = PATH + "NEW_REMINDER"

const val APP_TITLE = "SuTamPhap.com in Practice"
val QUOTE_BG_COLORS = arrayOf("#400D00", "#442F19")
val TEACHING_BG_COLORS = arrayOf("#2D555E", "#1E3A5F")

fun Context.broadcastUpdateWidget(actionName: String) {
    Intent(this, dev.tiendung.tamedu.AppWidget::class.java).apply {
        action = actionName
        sendBroadcast(this)
    }
}

fun toast(context: Context, txt: String) {
    Toast.makeText(context, txt, Toast.LENGTH_LONG).show()
}