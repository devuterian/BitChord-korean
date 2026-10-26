package com.music.bitchord.desktop

import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Which media key or script command does what.
 *
 * The native side hands back a number and nothing else, so a transposed pair
 * here means `previous track` skips forward, with no compiler error to say so.
 */
class DesktopMacMediaTest {

    private val pressed = mutableListOf<String>()

    private fun install() {
        DesktopMacMedia.start(
            DesktopMacMedia.Controller(
                onPlay = { pressed += "play" },
                onPause = { pressed += "pause" },
                onPlayPause = { pressed += "playpause" },
                onNext = { pressed += "next" },
                onPrevious = { pressed += "previous" },
                onStop = { pressed += "stop" },
                onSeek = { pressed += "seek $it" },
            ),
        )
    }

    @AfterTest
    fun tearDown() = DesktopMacMedia.stop()

    @Test
    fun everyButtonReachesItsOwnControl() {
        install()
        (0..5).forEach(DesktopMacMedia::onButton)
        assertEquals(listOf("play", "pause", "next", "previous", "stop", "playpause"), pressed)
    }

    @Test
    fun anUnknownButtonIsIgnoredRatherThanGuessed() {
        install()
        DesktopMacMedia.onButton(99)
        DesktopMacMedia.onButton(-1)
        assertEquals(emptyList(), pressed)
    }

    @Test
    fun aScrubberDragReachesSeekWithItsPosition() {
        install()
        DesktopMacMedia.onSeek(42_000L)
        assertEquals(listOf("seek 42000"), pressed)
    }

    @Test
    fun nothingIsReachedOnceStopped() {
        install()
        DesktopMacMedia.stop()
        DesktopMacMedia.onButton(5)
        assertEquals(emptyList(), pressed)
    }
}
