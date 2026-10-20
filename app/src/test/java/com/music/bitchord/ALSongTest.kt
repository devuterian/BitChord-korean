package com.music.bitchord

import com.music.bitchord.data.lyrics.ALSong
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

/**
 * [ALSong]'s response parser and candidate picker. The fixture is a trimmed
 * `GetResembleLyric2` response for 밤편지 / 아이유: the first candidate is the
 * real timing, the second a user submission that runs far past the song.
 */
class ALSongTest {

    private val fixture = """
        <?xml version="1.0" encoding="utf-8"?><soap:Envelope xmlns:soap="http://www.w3.org/2003/05/soap-envelope"><soap:Body><GetResembleLyric2Response xmlns="ALSongWebServer"><GetResembleLyric2Result>
        <ST_GET_RESEMBLELYRIC2_RETURN><strInfoID>8125935</strInfoID><strOnlyLyricWord>0</strOnlyLyricWord><strTitle>밤편지</strTitle><strLyric>[00:02.08]이 밤 그날의 반딧불을&lt;br&gt;[00:09.55]당신의 창 가까이 보낼게요&lt;br&gt;[00:23.36]음 사랑한다는 말이에요&lt;br&gt;[00:45.48]나 우리의 첫 입맞춤을 떠올려&lt;br&gt;[00:57.47]그럼 언제든 눈을 감고 &amp; 떠나요&lt;br&gt;[01:06.86]음 가장 먼 곳으로 가요</strLyric><strArtistName>아이유 (IU)</strArtistName><strAlbumName>Palette</strAlbumName><strRegisterFirstName>a</strRegisterFirstName></ST_GET_RESEMBLELYRIC2_RETURN>
        <ST_GET_RESEMBLELYRIC2_RETURN><strInfoID>111</strInfoID><strTitle>밤편지 (Through the Night)</strTitle><strLyric>[00:05.00]a&lt;br&gt;[01:05.00]b&lt;br&gt;[02:05.00]c&lt;br&gt;[03:05.00]d&lt;br&gt;[09:05.00]e</strLyric><strArtistName>아이유</strArtistName><strAlbumName></strAlbumName><strRegisterFirstName>b</strRegisterFirstName></ST_GET_RESEMBLELYRIC2_RETURN>
        </GetResembleLyric2Result></GetResembleLyric2Response></soap:Body></soap:Envelope>
    """.trimIndent()

    @Test
    fun `parses candidates and decodes the escaped line breaks`() {
        val candidates = ALSong.parse(fixture)
        assertEquals(2, candidates.size)
        assertEquals("8125935", candidates[0].id)
        assertEquals("밤편지", candidates[0].title)
        assertEquals("아이유 (IU)", candidates[0].artist)
        val lines = candidates[0].lrc.lines()
        assertEquals(6, lines.size)
        assertEquals("[00:02.08]이 밤 그날의 반딧불을", lines[0])
        assertEquals("[00:57.47]그럼 언제든 눈을 감고 & 떠나요", lines[4])
    }

    @Test
    fun `skips a candidate whose last stamp runs past the song`() {
        val picked = ALSong.pick(ALSong.parse(fixture), "밤편지", "아이유", durationMs = 70_000)
        assertEquals("8125935", picked?.id)
        // The long submission is the only one left when the song really is that long.
        val long = ALSong.pick(ALSong.parse(fixture).drop(1), "밤편지", "아이유", durationMs = 70_000)
        assertNull(long)
    }

    @Test
    fun `without a duration the closer title match wins`() {
        val picked = ALSong.pick(ALSong.parse(fixture), "밤편지", "아이유", durationMs = 0)
        assertEquals("8125935", picked?.id)
    }

    @Test
    fun `rejects a different title and a stub with too few timed lines`() {
        assertNull(ALSong.pick(ALSong.parse(fixture), "Celebrity", "아이유", durationMs = 0))
        val stub = ALSong.Candidate("1", "밤편지", "아이유", "[00:01.00]a\n[00:02.00]b")
        assertNull(ALSong.pick(listOf(stub), "밤편지", "아이유", durationMs = 0))
    }

    @Test
    fun `normalizing drops brackets, featuring tails and punctuation`() {
        assertEquals(ALSong.normalize("Through the Night (feat. X)"), ALSong.normalize("through-the night"))
        assertNotNull(ALSong.pick(ALSong.parse(fixture), "밤 편지", "IU, 아이유", durationMs = 0))
    }
}
