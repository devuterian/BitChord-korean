package com.music.bitchord.data.lyrics

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ALSongTest {
    private fun block(title: String) =
        "<ST_GET_RESEMBLELYRIC2_RETURN><strInfoID>1</strInfoID><strTitle>$title</strTitle>" +
            "<strArtistName>A</strArtistName><strLyric>[00:01.00]x</strLyric></ST_GET_RESEMBLELYRIC2_RETURN>"

    @Test
    fun badNumericEntityDoesNotKillTheList() {
        val xml = block("bad &#99999999999; &#x110000; &#1114112;") + block("&#xAC00;")
        val parsed = ALSong.parse(xml)
        assertEquals(2, parsed.size)
        assertEquals("가", parsed[1].title)
    }

    @Test
    fun hangulGate() {
        assertTrue(ALSong.hasHangul("사랑해"))
        assertTrue(ALSong.hasHangul("ㅋㅋ"))
        assertFalse(ALSong.hasHangul("Hello 世界 こんにちは"))
    }
}
