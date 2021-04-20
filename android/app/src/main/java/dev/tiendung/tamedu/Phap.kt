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
private var _isThuGian = false
private var _currentPhapPosition: Int = 0
private var _currentPhapLimitPosition: Int = 0
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

fun nghePhapButtonText(context: Context): String {
    val count = tamedu.count.get(context, NGHE_PHAP_COUNT_KEY)
    var txt = "Nghe pháp"
    if (count != 0) txt = "$txt $count"
    return txt
}

fun isPlaying(): Boolean {
    return try { _phapPlayer.isPlaying() }
    catch (e: java.lang.IllegalStateException) { false }
}

fun isThuGian(): Boolean {
    return _isThuGian
}

fun startPlayThuGian(context: Context): String? {
    if (!_phapIsLoading) { // load new thu gian
        finishPhap()
        _isThuGian = true
        tamedu.reminder.newCurrent(context, 15)
        _currentPhap = getRandomThuGian()
        loadAndPlayPhap(context)
    }
    return "Đang tải \"${currentTitle()}\""
}

fun startPlayPhap(context: Context): String? {
    if (!_phapIsLoading) { // load new phap
        if (_isThuGian || isPlaying() || _currentPhapPosition <= 5000) {
            finishPhap()
            _currentPhap = getRandomPhap()
        }
        loadAndPlayPhap(context)
        return "Đang tải \"${currentTitle()}\""
    }
    return null
}

fun pausePhap(): String? {
    _phapPlayerTimer.cancel()
    _phapPlayer.release()
    _phapIsLoading = false
    return null
}

private fun finishPhap(): String? {
    pausePhap()
    _isThuGian = false
    _currentPhapPosition = 0
    return null
}

fun currentTitle(): String { return _currentPhap!!.title }

fun checkAndRunTasks(context: Context, currH: Int) {
    if (_phapIsLoading || isPlaying()) { return }
    // Auto play Phap
    if ((currH >= 21) ||
        (currH >=  0 && currH <=  5) ||
        (currH >= 10 && currH <= 13)) 
    { context.broadcastUpdateWidget(NGHE_PHAP) }
}

private fun loadAndPlayPhap(context: Context): String {
    _phapIsLoading = true
    _phapPlayer = MediaPlayer().apply {
        setAudioAttributes(
                AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
        )
        setOnPreparedListener   { mp -> __startPlay(mp, context) }
        setOnCompletionListener { __finishPlay(context); finishPhap() }
    }

    // Every second, check progress
    _phapPlayerTimer = Timer("CheckNghePhapProgress", false)
    _phapPlayerTimer.schedule(1000, 1000) {
        val currPos = _phapPlayer.getCurrentPosition()
        if (currPos >= _currentPhapLimitPosition) {
            __finishPlay(context)
            pausePhap()
            tamedu.reminder.playBell(context)
        } else {
            _currentPhapPosition = _phapPlayer.getDuration() - currPos
            context.broadcastUpdateWidget(NGHE_PHAP_PROGRESS)
        }
    }

    return __preparePhapMedia(_currentPhap!!, context)
}


private fun __startPlay(mp: MediaPlayer, context: Context) {
    _phapIsLoading = false
    tamedu.reminder.stopAndMute()
    _currentPhapLimitPosition = mp.getDuration() // listen til the end
    if (!_isThuGian) {
        val x: Double
        if (_currentPhapPosition <= 5000) {
            x = (mp.getDuration() - 300000) * Random().nextDouble() // any pos except 5 last min
            _currentPhapLimitPosition = x.roundToInt() + 600000 // listen for 10 mins
        } else  {
            x = (mp.getDuration() - _currentPhapPosition).toDouble()
        }
        mp.seekTo(x.roundToLong(), MediaPlayer.SEEK_NEXT_SYNC)
    }
    mp.start()
    context.broadcastUpdateWidget(NGHE_PHAP_BEGIN)
}

private fun __finishPlay(context: Context) {
    val k = if (_isThuGian) THU_GIAN_COUNT_KEY else NGHE_PHAP_COUNT_KEY
    tamedu.count.inc(context, k, 1)    
    context.broadcastUpdateWidget(NGHE_PHAP_FINISH)
}

private fun __preparePhapMedia(phap: Phap, context: Context): String {
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