package tamedu.phap
import tamedu.phap.data.*
import dev.tiendung.tamedu.helpers.*

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import kotlin.math.absoluteValue
import kotlin.math.roundToInt
import kotlin.math.roundToLong

import android.net.Uri
import java.io.FileInputStream

import java.util.*
import kotlin.concurrent.schedule

import android.os.Environment
import java.io.File

// Init a mediaPlayer to play phap
private var _phapPlayer = MediaPlayer()
private var _phapIsLoading = false
private var _currentPhap: Phap? = null
private var _isThuGian = false
private var _currentPhapPosition: Int = 0
private var _currentPhapDuration: Int = 0
private var _currentPhapLimitPosition: Int = 0
private var _phapPlayerTimer = Timer()
var skipAutoPlay = false

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

fun isPause(): Boolean { return _currentPhapPosition > 5000 }

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
        if (isPlaying() || !isPause()) {
            finishPhap()
            _currentPhap = getRandomPhap()
            loadAndPlayPhap(context)
            return "Đang tải \"${currentTitle()}\""
        }
        loadAndPlayPhap(context)
    }
    return null
}

fun ngheVqdd(context: Context): String? {
    if (_phapIsLoading) { return null }
    finishPhap()
    _isThuGian = true
    _currentPhap = initPhap("nois/Vuot-qua-de-duoi.ogg", "Vượt qua dễ duôi (Hải Yến)")
    loadAndPlayPhap(context)
    return "Đang tải \"${currentTitle()}\""
}

fun ngheThuNoi(context: Context): String? {
    if (_phapIsLoading) { return null }
    finishPhap()
    _isThuGian = true
    _currentPhap = getRandomThuNoi()
    loadAndPlayPhap(context)
    return "Đang tải \"${currentTitle()}\""
}

fun pausePhap(context: Context): String? {
    __releaseCommonResources()
    if (skipAutoPlay) { return null }
    Timer("ContinuePhapAfter12.5mins", false).schedule(750000) {
        if (_currentPhapPosition < 750000*2)
             _currentPhapLimitPosition = _currentPhapPosition / 2
        else _currentPhapLimitPosition = _currentPhapPosition - 750000 // listen for 12.5 mins
        if (_currentPhapLimitPosition <= 300000) { _currentPhapLimitPosition = 0 }
        context.broadcastUpdateWidget(NGHE_PHAP)
    }
    return null
}

fun finishPhap(): String? {
    __releaseCommonResources()
    _isThuGian = false
    _currentPhapPosition = 0
    return APP_TITLE
}

private fun __releaseCommonResources() {
    _currentPhapLimitPosition = 0 // listen til the end
    _phapPlayerTimer.cancel()
    _phapPlayer.release()
    _phapIsLoading = false
}

fun currentTitle(): String { return _currentPhap!!.title }

fun checkAndRunTasks(context: Context, currH: Int) {
    if (skipAutoPlay || _phapIsLoading || isPlaying()) { return }
    // Auto play Phap
    if ((currH >= 21 && currH <= 22) ||
        (currH >=  3 && currH <=  5) ||
        (currH >= 10 && currH <= 12)) 
    { 
        context.broadcastUpdateWidget(NGHE_PHAP)
    }
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
        setOnCompletionListener { 
            __finishPlay(context);
            finishPhap();
            context.broadcastUpdateWidget(NGHE_PHAP_FINISH)
        }
    }

    // Every second, check progress
    _phapPlayerTimer = Timer("CheckNghePhapProgress", false)
    _phapPlayerTimer.schedule(1000, 1000) {
        _currentPhapPosition = _currentPhapDuration - _phapPlayer.getCurrentPosition()
        context.broadcastUpdateWidget(NGHE_PHAP_PROGRESS)
        if (_currentPhapLimitPosition > 1000 
                && _currentPhapLimitPosition > _currentPhapPosition) {
            pausePhap(context) // pause to continue later
            tamedu.reminder.playBell(context)
        }
    }

    return __preparePhapMedia(_currentPhap!!, context)
}


private fun __startPlay(mp: MediaPlayer, context: Context) {
    _phapIsLoading = false
    tamedu.reminder.stopAndMute()
    _currentPhapDuration = mp.getDuration()

    if (!_isThuGian) {
        val seekToPos: Double
        if (!isPause()) {
            seekToPos = (_currentPhapDuration - 600000) * Random().nextDouble() // any pos except 10 last min
            _currentPhapLimitPosition = _currentPhapDuration - seekToPos.roundToInt() - 750000 // listen for 12.5 mins
        } else  {
            seekToPos = (_currentPhapDuration - _currentPhapPosition).toDouble()
        }
        mp.seekTo(seekToPos.roundToLong(), MediaPlayer.SEEK_NEXT_SYNC)
    }

    mp.start()
    context.broadcastUpdateWidget(NGHE_PHAP_BEGIN)
}

private fun __finishPlay(context: Context) {
    val k = if (_isThuGian) THU_GIAN_COUNT_KEY else NGHE_PHAP_COUNT_KEY
    tamedu.count.inc(context, k, 1)
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

fun getRandomThuNoi(): Phap {
    val (id, title) = NOI_IDS_TO_TITLES.random()
    return initPhap(id, title)
}

fun getRandomThuGian(): Phap {
    val (id, title) = THU_GIAN_IDS_TO_TITLES.random()
    return initPhap(id, title)
}

fun getRandomPhap(): Phap {
    val (id, title) = PHAP_IDS_TO_TITLES.random()
    return initPhap(id, title)
}

private fun initPhap(id: String, title: String): Phap {
    return Phap(
            title = title,
            audioUrl = "https://thaytamphap.github.io/$id",
            audioFile = File(Environment.getExternalStorageDirectory(), "Documents/stp/$id")
    )
}
