package dev.tiendung.tamedu.helpers

import android.content.Context
import android.content.Intent
import android.widget.Toast
import java.text.SimpleDateFormat
import java.util.*

const val APP_TITLE = "Thoải mái  -  Tích cực  -  Hết mình"

private const val PATH = "dev.tiendung.tamedu."
const val NGHE_PHAP_FINISH = PATH + "action.NGHE_PHAP_FINISH"
const val NGHE_PHAP_BEGIN = PATH + "action.NGHE_PHAP_BEGIN"
const val NGHE_PHAP_PROGRESS = PATH + "action.NGHE_PHAP_PROGRESS"
const val NGHE_PHAP = PATH + "action.NGHE_PHAP"
const val THU_GIAN = PATH + "action.THU_GIAN"
const val SPEAK_REMINDER_TOGGLE = PATH + "action.SPEAK_REMINDER_TOGGLE"
const val NEW_REMINDER = PATH + "action.NEW_REMINDER"

const val TODAY_SQUAT = PATH + "action.TODAY_SQUAT"
const val TODAY_PUSH = PATH + "action.TODAY_PUSH"
const val TODAY_PULL = PATH + "action.TODAY_PULL"
const val TODAY_ABS = PATH + "action.TODAY_ABS"

const val COUNT_TOTAL = PATH + "action.COUNT_TOTAL"
const val COUNT_5 = PATH + "action.COUNT_5"
const val COUNT_10 = PATH + "action.COUNT_10"
const val COUNT_RESET = PATH + "action.COUNT_RESET"

const val PREFERENCE_FILE_KEY = PATH + "PREFERENCE_FILE_KEY"
const val NGHE_PHAP_COUNT_KEY = "nghePhapCount"
const val THU_GIAN_COUNT_KEY = "thuGianCount"
const val SQUAT_COUNT_KEY = "squatCount"
const val PUSH_COUNT_KEY = "pushCount"
const val PULL_COUNT_KEY = "pullCount"
const val ABS_COUNT_KEY = "absCount"

val COUNT_KEY_TO_LABEL: HashMap<String, String> = hashMapOf(
        SQUAT_COUNT_KEY to "Chân",
        PUSH_COUNT_KEY to "Đẩy",
        PULL_COUNT_KEY to "Kéo",
        ABS_COUNT_KEY to "Bụng",
        THU_GIAN_COUNT_KEY to "Thư giãn",
        NGHE_PHAP_COUNT_KEY to "Nghe pháp"
)
val COUNT_KEY_TO_GOAL: HashMap<String, Int> = hashMapOf(
        NGHE_PHAP_COUNT_KEY to 5,
        THU_GIAN_COUNT_KEY to 3,
        SQUAT_COUNT_KEY to 150,
        PUSH_COUNT_KEY to 150,
        PULL_COUNT_KEY to 80,
        ABS_COUNT_KEY to 150
)
val COUNT_KEYS = COUNT_KEY_TO_LABEL.keys

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

fun dateStr(delta: Int = 0): String {
    val cal = Calendar.getInstance()
    if (delta != 0) { 
        cal.add(Calendar.DATE, delta) 
    }
    return SimpleDateFormat("yyyy-MM-dd").format(cal.getTime())
}
