package tamedu.tasks
import android.content.Context
import java.util.*

fun checkAndRun(context: Context) {
    val currH = Calendar.getInstance()[Calendar.HOUR_OF_DAY] // currentTime[Calendar.MINUTE]

    // Reset counters
    if (currH >= 0 && currH <= 2) { tamedu.count._todayReseted = false }

    if (!tamedu.phap.isPlaying()) {
        tamedu.phap.checkAndRunTasks(context, currH)
        tamedu.reminder.playBellOrSpeakCurrent(context)
    }
}
