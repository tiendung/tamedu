package dev.tiendung.tamedu.helpers

import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.Toast
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import dev.tiendung.tamedu.PhapService

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

fun isOreoPlus() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O

fun toast(context: Context, txt: String) {
    Toast.makeText(context, txt, Toast.LENGTH_LONG).show()
}
const val PROGRESS = "progress"
private const val PATH = "dev.tiendung.tamedu.action."
const val FINISH_PHAP = PATH + "FINISH_PHAP"
const val PLAY_PHAP = PATH + "PLAY_PHAP"
const val BROADCAST_STATUS = PATH + "BROADCAST_STATUS"
