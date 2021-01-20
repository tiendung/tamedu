package dev.tiendung.tamedu
import com.simplemobiletools.musicplayer.helpers.*
import android.app.Service
import android.content.Intent
import android.content.Context
import android.media.AudioManager
import android.media.AudioManager.*
import android.os.IBinder
import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import dev.tiendung.tamedu.helpers.*
import dev.tiendung.tamedu.data.*
import android.os.PowerManager

class PhapService : Service(),
        MediaPlayer.OnPreparedListener,
        MediaPlayer.OnErrorListener,
        MediaPlayer.OnCompletionListener
{
    companion object {
        private var mPlayer: MediaPlayer? = null
        private var mAudioManager: AudioManager? = null
        private var mOreoFocusHandler: OreoAudioFocusHandler? = null
        var mCurrPhap: Phap? = null
        var mIsPreparing: Boolean = false
        fun getIsPlaying() = mPlayer?.isPlaying == true
    }

    override fun onCreate() {
        super.onCreate()
        mAudioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
    }

    override fun onBind(intent: Intent) = null

    override fun onCompletion(mp: MediaPlayer) {
        TODO("Notice AppWidget to update nghe_phap button text")
    }

    override fun onError(mp: MediaPlayer, what: Int, extra: Int): Boolean {
        mPlayer!!.reset()
        return false
    }

    override fun onPrepared(mp: MediaPlayer) {
        mp.start()
        mIsPreparing = false
//        requestAudioFocus()
        broadcastUpdateWidgetPlayingPhap(mCurrPhap!!)
    }

    private fun initMediaPlayerIfNeeded() {
        if (mPlayer != null) {  return }
        mPlayer = MediaPlayer().apply {
            setWakeMode(applicationContext, PowerManager.PARTIAL_WAKE_LOCK)
            setAudioStreamType(AudioManager.STREAM_MUSIC)
            setOnPreparedListener(this@PhapService)
            setOnCompletionListener(this@PhapService)
            setOnErrorListener(this@PhapService)
        }
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val action = intent.action

        when (action) {
            PLAY_RANDOM_PHAP -> playRandomPhap()
            FINISH_PHAP -> stopPlayingPhap()
        }
        return START_NOT_STICKY
    }

    private fun playRandomPhap() {
        mCurrPhap = getRandomPhap()
        initMediaPlayerIfNeeded()
        mPlayer?.reset() ?: return
        mPlayer!!.setDataSource(applicationContext, mCurrPhap!!.audioUri)
        mPlayer!!.prepareAsync()
        mIsPreparing = true
    }

    private fun stopPlayingPhap() {
        mPlayer?.stop()
        mPlayer?.release()
        mPlayer = null
    }
}