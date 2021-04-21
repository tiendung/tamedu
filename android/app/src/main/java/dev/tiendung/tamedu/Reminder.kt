package tamedu.reminder
import tamedu.reminder.data.*
import dev.tiendung.tamedu.helpers.*

import android.content.Context
import android.content.res.AssetFileDescriptor
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.graphics.Color
import android.os.Environment

import java.io.File
import java.io.FileDescriptor
import java.io.FileInputStream

private var _player: MediaPlayer = MediaPlayer()
private var _allowToSpeak: Boolean = false
private var _current: Reminder? = null

fun stopAndMute() {
    finishPlaying()
    _allowToSpeak = false
}

fun toggle() {
    _allowToSpeak = !_allowToSpeak
}

fun initCurrentIfNeeded(context: Context) { 
    if (_current == null) newCurrent(context)
    while (_current!!.text.replace(" ","").length == 0) {
        toast(context, "${_current!!.id}")
        newCurrent(context)
    }
}

fun speakCurrent(context: Context, must: Boolean = false): String? {
    finishPlaying()
    if (_allowToSpeak || must) {
        speakCurrentAudio(context)
        return _current!!.text
    }
    return null
}

fun playBellOrSpeakCurrent(context: Context) {
    finishPlaying()
    if (_allowToSpeak) speakCurrentAudio(context)
    else playBell(context)
}

fun playBell(context: Context) {
    val file = externalFile(BELL_FILE_NAME)
    if (file.exists()) playAudioFile(null, FileInputStream(file).fd)
    else playAudioFile(assetFd(context, BELL_FILE_NAME))
}

fun finishPlaying() {
    _player.release()
}

private fun speakCurrentAudio(context: Context) {
    initCurrentIfNeeded(context)
    if (_current!!.audioFile!!.exists())
        playAudioFile(null, FileInputStream(_current!!.audioFile!!).fd)
    else
        playAudioFile(_current!!.audioAssetFd)
}

private fun playAudioFile(assetFd: AssetFileDescriptor?, fd: FileDescriptor? = null) {
    _player = MediaPlayer().apply {
        setAudioAttributes(
                AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
        )
    }

    if (fd != null) {
        _player.setDataSource(fd)
    } else 
        try {
            _player.setDataSource(assetFd!!.fileDescriptor, assetFd.startOffset, assetFd.length)
        } catch (e: kotlin.KotlinNullPointerException) {
            // toast(context, "AudioFile not found")
            playBell(context)
            return
        }

    _player.prepare()
    _player.start()
}

// Reminder data including quotes, teachings and practices
data class Reminder(
    val id: Int,
    val text: String, 
    val audioFile: File?,
    val audioAssetFd: AssetFileDescriptor?, 
    val bgColor: Int
)

fun newCurrent(context: Context, teachingId: Int = -1) {
    val bgColor: String; val txt: String; val fileName: String
    var id: Int = teachingId
    if (id < 0 && tamedu.phap.isThuGian()) {
        id = THOAI_MAI_IDS.random()
    }
    if (id < 0 && Math.random() < 0.55) {
        id = TEACHINGS.indices.random()
    }
    if (id >= 0) {
        txt = TEACHINGS[id]
        fileName = "teachings/$id.ogg"
        bgColor = TEACHING_BG_COLORS.random()
    } else {
        id = QUOTE_IDS_SORTED_BY_LEN.random()
        txt = QUOTES[id]
        fileName = "quotes/$id.ogg"
        bgColor = QUOTE_BG_COLORS.random()
    }
    _current = Reminder(
            id = id,
            text = txt,
            audioAssetFd = assetFd(context, fileName),
            audioFile = externalFile(fileName),
            bgColor = Color.parseColor(bgColor) )
}
private fun assetFd(context: Context, fileName: String): AssetFileDescriptor? {
    val am = context.assets
    val ls = am.list(fileName)
    val fileNotExists = (ls == null || ls.isEmpty())
    return if (fileNotExists) null else am.openFd(fileName)
}
private fun externalFile(fileName: String): File {
    return  File(Environment.getExternalStorageDirectory(),
            "Documents/tamedu/assets/${fileName}")
}

fun currentText(): String { initCurrentIfNeeded(); return _current!!.text }
fun currentBgColor(): Int { return if (_current == null)  0 else _current!!.bgColor }
fun toggleText(): String {
    return when (_allowToSpeak) {
        true  -> "Dừng"
        false -> "Đọc"
    }
}
