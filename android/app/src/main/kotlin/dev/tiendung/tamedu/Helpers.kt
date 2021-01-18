package dev.tiendung.tamedu.helpers

import android.content.Context
import android.os.Build
import android.util.Log
import android.widget.Toast
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

fun isOreoPlus() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O

fun toast(context: Context, txt: String) {
    Toast.makeText(context, txt, Toast.LENGTH_LONG).show()
}

fun copyFromAssetsToFile(context: Context, asset : String, file : File) {
    try {
        val assetManager = context.getAssets()
        val ins = assetManager.open(asset)
        val os = FileOutputStream(file)
        val data = ByteArray(ins.available())
        ins.read(data)
        os.write(data)
        ins.close()
        os.close()
    } catch (e: IOException) {
        Log.w("ExternalStorage", "Error writing $file", e)
    }
}
const val PROGRESS = "progress"
const val PLAY_PHAP = "play_phap"
const val FINISH_PLAY_PHAP = "finish_play_phap"
