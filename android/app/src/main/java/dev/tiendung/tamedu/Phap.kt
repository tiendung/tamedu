package tamedu.phap
import tamedu.phap.data.*
import dev.tiendung.tamedu.helpers.*

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import java.util.*
import kotlin.concurrent.schedule
import kotlin.math.absoluteValue
import kotlin.math.roundToInt
import kotlin.math.roundToLong

import android.net.Uri
import java.io.FileInputStream

// Init a mediaPlayer to play phap
private var _phapPlayer: MediaPlayer = MediaPlayer()
private var _phapIsLoading: Boolean = false
private var _currentPhap: Phap? = null
private var _autoPlayed = false
private var _isThuGian = false
private var _currentPhapPosition: Int = 0
private var _phapPlayerTimer: Timer = Timer()

fun getCurrentPhapPosition():String {
    var s = _currentPhapPosition / 1000
    val m = s / 60
    s %= 60
    return "%02d:%02d".format(m, s.absoluteValue)
}

fun docButtonText(): String {
    return if (isPlaying()) "Dừng" else tamedu.reminder.toggleText()
}

fun thuGianButtonText(context: Context): String {
    val count = tamedu.count.get(context, THU_GIAN_COUNT_KEY)
    var txt = "Thư giãn"
    if (count != 0) txt = "$txt $count"
    return txt
}

fun isPlaying(): Boolean {
    return _phapPlayer.isPlaying()
}

fun isThuGian(): Boolean {
    return _isThuGian
}

fun startPlayThuGian(context: Context): String? {
    if (!_phapIsLoading) { // load new phap or thu gian
        finishPhap()
        _isThuGian = true
        tamedu.reminder.newCurrent(context, 15)
        _currentPhap = getRandomThuGian()
        loadAndPlayPhap(context)
    }
    return "Đang tải \"${currentTitle()}\""
}

fun startPlayPhap(context: Context): String? {
    if (!_phapIsLoading) { // load new phap or thu gian
        finishPhap()
        _currentPhap = getRandomPhap()
        loadAndPlayPhap(context)
    }
    return "Đang tải \"${currentTitle()}\""
}

fun finishPhap(): String? {
    _phapPlayerTimer.cancel()
    _phapPlayer.release()
    _phapIsLoading = false
    _autoPlayed = false
    _currentPhapPosition = 0
    _isThuGian = false
    return null
}

fun currentTitle(): String { return _currentPhap!!.title }

fun checkTimeToPlay(context: Context): String {
    val currentTime = Calendar.getInstance()
    val currH = currentTime[Calendar.HOUR_OF_DAY]
    val currM = currentTime[Calendar.MINUTE]
    // Reset counter
    if (currH >= 1 && currH <= 3) tamedu.count._todayReseted = false
    // Auto play Phap
    if (!_autoPlayed && !_phapIsLoading && !isPlaying() && (
                    (currH == 11 && currM > 15) || (currH == 12 && currM > 15) ||
                    (currH == 22 && currM > 15) || (currH == 23 && currM > 15) ||
                    (currH ==  0 && currM > 15) || (currH ==  1 && currM > 15) ||
                    (currH ==  2 && currM > 15) || (currH ==  3 && currM > 15) ||
                    (currH ==  4 && currM > 15) || (currH ==  5 && currM >  5)
    )) {
        context.broadcastUpdateWidget(NGHE_PHAP)
        _autoPlayed = true
    }
    return "$currH : $currM"
}

private fun loadAndPlayPhap(context: Context): String {
    val phap: Phap = _currentPhap!!
    _phapIsLoading = true
    _phapPlayer = MediaPlayer().apply {
        setAudioAttributes(
                AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
        )

        setOnPreparedListener { mp ->
            _phapIsLoading = false
            tamedu.reminder.stopAndMute()
            if (!_isThuGian) {
                var x = mp.getDuration() * (0.5+0.3*Random().nextDouble())
                mp.seekTo(x.roundToLong(), MediaPlayer.SEEK_NEXT_SYNC)
            }
            mp.start()
            context.broadcastUpdateWidget(NGHE_PHAP_BEGIN)

            // Every second, check progress
            _phapPlayerTimer = Timer("CheckNghePhapProgress", false)
            _phapPlayerTimer.schedule(1000, 1000) {
                _currentPhapPosition = mp.getDuration() - mp.getCurrentPosition()
                context.broadcastUpdateWidget(NGHE_PHAP_PROGRESS)
            }
        }

        setOnCompletionListener {
            val k = if (_isThuGian) THU_GIAN_COUNT_KEY else NGHE_PHAP_COUNT_KEY
            tamedu.count.inc(context, k, 1)
            finishPhap()
            context.broadcastUpdateWidget(NGHE_PHAP_FINISH)
        }
    }

    val txt: String
    if (phap.audioFile.exists()) {
        val fd = FileInputStream(phap.audioFile).fd
        _phapPlayer.setDataSource(fd)
        txt = "${phap.audioFile}".replace("/storage/emulated/0/","")
    } else {
        _phapPlayer.setDataSource(context, Uri.parse(phap.audioUrl))
        txt = phap.audioUrl
    }
    _phapPlayer.prepareAsync()

    return txt
}