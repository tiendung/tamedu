package dev.tiendung.tamedu.helpers

import android.content.Context
import android.content.Intent
import android.widget.Toast

private const val PATH = "dev.tiendung.tamedu."
const val NGHE_PHAP_FINISH = PATH + "action.NGHE_PHAP_FINISH"
const val NGHE_PHAP_BEGIN = PATH + "action.NGHE_PHAP_BEGIN"
const val NGHE_PHAP = PATH + "action.NGHE_PHAP"
const val THU_GIAN = PATH + "action.THU_GIAN"
const val SPEAK_REMINDER_TOGGLE = PATH + "action.SPEAK_REMINDER_TOGGLE"
const val NEW_REMINDER = PATH + "action.NEW_REMINDER"

const val PREFERENCE_FILE_KEY = PATH + "PREFERENCE_FILE_KEY"
const val THU_GIAN_COUNT_KEY = "thuGianCount"

const val APP_TITLE = "SuTamPhap.com in Practice"
val QUOTE_BG_COLORS = arrayOf("#400D00", "#442F19", "#42275E", "#5C2B29")
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