package app.sen.musics.module.player

import android.net.Uri
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import app.sen.musics.module.log.L
import app.sen.musics.utils.GlobalContext
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util

import kotlinx.coroutines.*
import java.io.File
import java.util.*
import java.util.concurrent.atomic.AtomicReference
import kotlin.math.max
import kotlin.math.min

private const val TAG = "NottaPlayer"

object NottaPlayer : CoroutineScope by MainScope() {

    private var player: SimpleExoPlayer? = null
    
    private var playerListener: NottaPlayerEventListener? = null

    private var currentPlayerTask: AtomicReference<PlayerTask?> = AtomicReference(null)


    private val dataSourceFactory = DefaultDataSourceFactory(
        GlobalContext.instance,
        Util.getUserAgent(GlobalContext.instance, "Langogo"),
        null
    )


    private val liveData = MutableLiveData<PlayerEvent>()

    var currentState: PlayerState = PlayerState.RELEASE
        private set

    init {
        liveData.observeForever {
            when (it) {
                is PlayerEvent.PlayerStateChangedEvent -> {
                    currentState = it.state
                }
                is PlayerEvent.PlayerErrorEvent -> {
                    L.i(TAG, "PlayerErrorEvent: ${it}")
//                    release()
                }
            }
        }
    }

    fun observePlayerEvent(owner: LifecycleOwner?, observer: Observer<in PlayerEvent>) {
        if (owner == null) {
            liveData.observeForever(observer)
        } else {
            liveData.observe(owner, observer)
        }
    }

    fun removeObserve(observer: Observer<in PlayerEvent>) {
        liveData.removeObserver(observer)
    }

    fun prepare(localFile: File, playWhenReady: Boolean = true) {
        release()
        currentPlayerTask.set(
            PlayerTask(
                playWhenReady = playWhenReady
            )
        )


        player = SimpleExoPlayer.Builder(GlobalContext.instance).build().also {
            NottaPlayerEventListener( it, liveData).let { listener ->
                it.addListener(listener)
                playerListener = listener
            }
            it.playWhenReady = playWhenReady
        }
        val isLocal=localFile.exists()
        if (isLocal  ) {
            L.i(TAG, "prepare: isLocal ${isLocal},path=${localFile.absolutePath}")
            val dataSource =
                ProgressiveMediaSource.Factory(dataSourceFactory)
                    .createMediaSource(Uri.parse(localFile.toURI().toString()))
            player?.prepare(dataSource)
        } else {
            playerListener?.onPlayerStateChanged(playWhenReady, ExoPlayer.STATE_BUFFERING)
            launch(Dispatchers.IO) {
                withContext(Dispatchers.Main) {
                    try {
                        val dataSource =
                            ProgressiveMediaSource.Factory(dataSourceFactory)
                                .createMediaSource(Uri.parse(localFile.toURI().toString()))
                        player?.prepare(dataSource)
                    } catch (e: ExoPlaybackException) {
                        playerListener?.onPlayerError(e)
                    } catch (e: RuntimeException) {
                        playerListener?.onPlayerError(ExoPlaybackException.createForUnexpected(
                            RuntimeException("unknown error, url may unexpected null", e)
                        ))
                    }
                }
            }

        }


    }

    /**
     * 设置播放速度
     */
    fun setPlaySpeed(speed:Float) {
        player?.setPlaybackParameters(PlaybackParameters(speed,1f,false))
    }


    fun seekTo(ms: Long) {
//        player?.seekTo(ms)

        player?.let { player ->
            player.seekTo(ms)

            currentPlayerTask.get()?.let {
                liveData.value = PlayerEvent.PlayerProgressEvent(
                    player.currentPosition,
                    player.duration
                )
            }
        }
    }

    fun seekForward(ms: Long = 15000) {
        player?.let {
            seekTo(min(it.currentPosition + ms, it.duration))
        }
    }

    fun seekBackward(ms: Long = 15000) {
        player?.let {
            seekTo(max(it.currentPosition - ms, 0))
        }
    }


    fun togglePlayOrPause() {
        if (isPlaying()) {
            pause()
        } else {
            start()
        }
    }

    fun start() {
        L.i(TAG, "start")

        player?.let {
            if (it.playbackState == ExoPlayer.STATE_ENDED) {
                it.seekTo(0)
            }
            it.playWhenReady = true
        }
    }

    fun pause() {
        L.i(TAG, "pause")
        player?.playWhenReady = false
    }

    fun release() {
        L.i(TAG, "release")
        currentPlayerTask.getAndSet(null)?.let {
            liveData.value = PlayerEvent.PlayerStateChangedEvent(
                PlayerState.RELEASE
            )
        }

        playerListener?.let {
            it.release()
            player?.removeListener(it)
        }

        player?.release()
        player = null
    }

    fun isPlaying(): Boolean {
        return player?.isPlaying ?: false
    }


}


private class NottaPlayerEventListener(
    private val player: ExoPlayer,
    private val liveData: MutableLiveData<PlayerEvent>
) : Player.EventListener {

    private var playerProgressTask: PlayerProgressTask? = null


    private fun createAndStartProgressTask() {
        if (null != playerProgressTask) {
            return
        }
        playerProgressTask = PlayerProgressTask(liveData, player, 100)
        playerProgressTask?.start()
    }

    private fun releaseProgressTask() {
        playerProgressTask?.release()
        playerProgressTask = null
    }

    fun release() {
        releaseProgressTask()
    }

    override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {

        val state = when (playbackState) {
            ExoPlayer.STATE_IDLE -> PlayerEvent.PlayerStateChangedEvent(
                PlayerState.IDLE
            )
            ExoPlayer.STATE_BUFFERING -> {
                releaseProgressTask()
                PlayerEvent.PlayerStateChangedEvent(
                    PlayerState.BUFFERING
                )
            }
            ExoPlayer.STATE_READY -> {
                if (playWhenReady) {
                    //seek 也会回调这个
                    createAndStartProgressTask()
                    PlayerEvent.PlayerStateChangedEvent(
                        PlayerState.PLAYING
                    )
                } else {
                    releaseProgressTask()
                    PlayerEvent.PlayerStateChangedEvent( PlayerState.PAUSE)
                }
            }
            ExoPlayer.STATE_ENDED -> {
                releaseProgressTask()

                PlayerEvent.PlayerStateChangedEvent(
                    
                    PlayerState.ENDED
                )
            }
            else -> null
        }

        L.i(
            TAG,
            "onPlayerStateChanged $playWhenReady ${getStateString(playbackState)} ${state?.state}"
        )

        state?.let {
            liveData.value = it
        }
    }

    private fun getStateString(state: Int): String {
        return when (state) {
            ExoPlayer.STATE_IDLE -> "IDLE"
            ExoPlayer.STATE_BUFFERING -> "BUFFERING"
            ExoPlayer.STATE_READY -> "READY"
            ExoPlayer.STATE_ENDED -> "ENDED"
            else -> "UNKNOWN"
        }
    }

    override fun onPlayerError(error: ExoPlaybackException) {
        L.e(TAG, "onPlayerError $error")
        release()
        liveData.value = PlayerEvent.PlayerErrorEvent( error)
    }

}


private data class PlayerTask(
    val playWhenReady: Boolean
)

private class PlayerProgressTask(
    private val liveData: MutableLiveData<PlayerEvent>,
    private val player: ExoPlayer,
    private val interval: Long
) {

    private val timer = Timer()
    private val handler = Handler(Looper.getMainLooper())

    private val timerTask = object : TimerTask() {
        override fun run() {
            handler.post {
                liveData.value = PlayerEvent.PlayerProgressEvent(
                    player.currentPosition,
                    player.duration
                )
            }
        }

    }

    fun start() {
        timer.schedule(timerTask, 0, interval)
    }

    fun release() {
        handler.removeCallbacksAndMessages(null)
        timerTask.cancel()
        timer.cancel()
    }
}


sealed class PlayerEvent() {

    data class PlayerProgressEvent(
        val progress: Long,
        val duration: Long
    ) : PlayerEvent()

    data class PlayerErrorEvent(val error: Exception) :
        PlayerEvent()

    data class PlayerStateChangedEvent(
        val state: PlayerState
    ) :
        PlayerEvent()

}


enum class PlayerState {
    IDLE,
    BUFFERING,
    PLAYING,
    PAUSE,
    ENDED,
    RELEASE
}