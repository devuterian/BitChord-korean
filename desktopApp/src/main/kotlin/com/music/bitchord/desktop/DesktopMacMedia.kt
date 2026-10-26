package com.music.bitchord.desktop

import java.util.concurrent.atomic.AtomicBoolean

/**
 * macOS Now Playing — Control Center's media card, the lock screen, the media
 * keys, and the app's AppleScript commands (`playpause`, `next track`, …).
 *
 * The macOS counterpart of [DesktopWindowsMedia], and the same contract: the
 * player hands it state, it hands back button presses. What it talks to is
 * MediaPlayer and AppKit, which the JVM cannot reach, so the work happens in
 * `libbitchord_nowplaying.dylib` beside the analyser.
 *
 * The AppleScript commands are there for media key routers such as Threek,
 * which pick one app among several in Now Playing and need a way to reach that
 * app rather than whichever one macOS last made current.
 *
 * Absent, everything here is a no-op.
 */
internal object DesktopMacMedia {

    private val started = AtomicBoolean(false)

    private val shutdownHookAdded = AtomicBoolean(false)

    @Volatile
    private var controller: Controller? = null

    /** What a media key or script command does, filled in by the player. */
    internal class Controller(
        val onPlay: () -> Unit,
        val onPause: () -> Unit,
        val onPlayPause: () -> Unit,
        val onNext: () -> Unit,
        val onPrevious: () -> Unit,
        val onStop: () -> Unit,
        /** Moves playback to a position Control Center's scrubber was dragged to, in milliseconds. */
        val onSeek: (Long) -> Unit,
    )

    private val available: Boolean by lazy {
        if (!DesktopPlatform.isMac) return@lazy false
        runCatching { DesktopAnalysisRuntime.loadNative(LIBRARY) }
            .onFailure { DesktopTrackLog.log("macOS Now Playing unavailable: ${it.message}") }
            .isSuccess
    }

    fun start(controller: Controller) {
        // Held whether or not the library is there, so what a button does is
        // decided in one place and can be checked without macOS.
        this.controller = controller
        if (!available || !started.compareAndSet(false, true)) return
        if (!nativeStart()) {
            started.set(false)
            DesktopTrackLog.log("macOS Now Playing could not be started")
            return
        }
        // Quitting from the menu bar is exitProcess, which never reaches the window's onDispose.
        if (shutdownHookAdded.compareAndSet(false, true)) {
            Runtime.getRuntime().addShutdownHook(Thread(::stop, "bitchord-nowplaying-stop"))
        }
    }

    fun stop() {
        controller = null
        if (!started.compareAndSet(true, false)) return
        runCatching { nativeStop() }
    }

    /**
     * Publishes the current track. Called on every change Control Center would show, seeks
     * included: it runs the position on from here at [speed] by itself.
     */
    fun publish(state: DesktopPlaybackState, speed: Float) {
        if (!started.get()) return
        val song = state.song
        // The position was read off the sink a moment ago; carry it to now.
        val sinceSampledMs = if (state.isPlaying && state.positionSampledAtNanos > 0L) {
            ((System.nanoTime() - state.positionSampledAtNanos) / 1_000_000L * speed).toLong().coerceAtLeast(0L)
        } else {
            0L
        }
        runCatching {
            nativeUpdate(
                song?.title.orEmpty(),
                song?.artist.orEmpty(),
                song?.albumName.orEmpty(),
                song?.thumbnailUrl.orEmpty(),
                state.isPlaying,
                speed.toDouble(),
                state.positionMs + sinceSampledMs,
                state.durationMs,
            )
        }
    }

    /** Called from the native side when a media key, Control Center or a script is used. */
    @JvmStatic
    fun onButton(button: Int) {
        val target = controller ?: return
        when (button) {
            BUTTON_PLAY -> target.onPlay()
            BUTTON_PAUSE -> target.onPause()
            BUTTON_NEXT -> target.onNext()
            BUTTON_PREVIOUS -> target.onPrevious()
            BUTTON_STOP -> target.onStop()
            BUTTON_PLAY_PAUSE -> target.onPlayPause()
        }
    }

    /** Called from the native side when Control Center's scrubber is dragged to a new position. */
    @JvmStatic
    fun onSeek(positionMs: Long) {
        controller?.onSeek(positionMs)
    }

    private external fun nativeStart(): Boolean
    private external fun nativeStop()
    private external fun nativeUpdate(
        title: String,
        artist: String,
        album: String,
        artUrl: String,
        isPlaying: Boolean,
        rate: Double,
        positionMs: Long,
        durationMs: Long,
    )

    private const val LIBRARY = "bitchord_nowplaying"

    private const val BUTTON_PLAY = 0
    private const val BUTTON_PAUSE = 1
    private const val BUTTON_NEXT = 2
    private const val BUTTON_PREVIOUS = 3
    private const val BUTTON_STOP = 4
    private const val BUTTON_PLAY_PAUSE = 5
}
