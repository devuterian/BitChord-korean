package com.music.bitchord.data.lyrics

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.abs

/**
 * Line-synced lyrics from ALSong (알송), the Korean desktop player's lyric
 * database — user-submitted, so a title usually has several candidates with
 * different timings, and none of them says how long the song is.
 *
 * One unauthenticated SOAP call over plain HTTP; the host is allowed in
 * network_security_config.xml and nowhere else. The envelope and the fixed
 * client token follow yhsphd/ESLyric-ALSong (MIT).
 */
object ALSong {

    private const val ENDPOINT = "http://lyrics.alsong.co.kr/alsongwebservice/service1.asmx"
    private const val ENC_DATA =
        "8456ec35caba5c981e705b0c5d76e4593e020ae5e3d469c75d1c6714b6b1244c0732f1f19cc32ee5123ef7de574fc8bc6d3b6bd38dd3c097f5a4a1aa1b438fea0e413baf8136d2d7d02bfcdcb2da4990df2f28675a3bd621f8234afa84fb4ee9caa8f853a5b06f884ea086fd3ed3b4c6e14f1efac5a4edbf6f6cb475445390b0"

    /** How far past the song's length the last stamp may sit and still be believed. */
    private const val DURATION_SLACK_MS = 5_000L

    /** Fewer stamped lines than this is a stub, not a timed transcript. */
    private const val MIN_TIMED_LINES = 5

    /** Hangul syllables, Jamo and compatibility Jamo: ALSong is a Korean catalogue, so anything else is a wasted (cleartext) query. */
    fun hasHangul(text: String): Boolean = text.any {
        it in '\uAC00'..'\uD7A3' || it in '\u1100'..'\u11FF' || it in '\u3130'..'\u318F'
    }

    suspend fun lyrics(title: String, artist: String, durationMs: Long): List<LyricLine>? =
        withContext(Dispatchers.IO) {
            val cleanTitle = title.replace(PARENTHETICAL, "").trim().ifBlank { title }
            val body = lyricsPost(
                url = ENDPOINT,
                body = envelope(cleanTitle, artist),
                contentType = "application/soap+xml; charset=utf-8",
                headers = mapOf(
                    "SOAPAction" to "ALSongWebServer/GetResembleLyric2",
                    "User-Agent" to "gSOAP/2.7",
                ),
            ) ?: return@withContext null
            val chosen = pick(parse(body), title, artist, durationMs) ?: return@withContext null
            LrcLib.parseLrc(chosen.lrc).takeIf { it.isNotEmpty() }
        }

    class Candidate(
        val id: String,
        val title: String,
        val artist: String,
        /** Standard LRC, one stamped line per row. */
        val lrc: String,
    )

    private fun envelope(title: String, artist: String) =
        """<?xml version="1.0" encoding="UTF-8"?>""" +
            """<SOAP-ENV:Envelope xmlns:SOAP-ENV="http://www.w3.org/2003/05/soap-envelope" """ +
            """xmlns:SOAP-ENC="http://www.w3.org/2003/05/soap-encoding" """ +
            """xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" """ +
            """xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:ns1="ALSongWebServer">""" +
            """<SOAP-ENV:Body><ns1:GetResembleLyric2><ns1:encData>$ENC_DATA</ns1:encData>""" +
            """<ns1:stQuery><ns1:strTitle>${escape(title)}</ns1:strTitle>""" +
            """<ns1:strArtistName>${escape(artist)}</ns1:strArtistName>""" +
            """<ns1:nCurPage>0</ns1:nCurPage></ns1:stQuery>""" +
            """</ns1:GetResembleLyric2></SOAP-ENV:Body></SOAP-ENV:Envelope>"""

    private fun escape(text: String) = text
        .replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")

    /** Candidates in the order the service listed them; blocks with no lyric are dropped. */
    fun parse(xml: String): List<Candidate> =
        BLOCK.findAll(xml).mapNotNull { block ->
            val inner = block.groupValues[1]
            val lrc = tag(inner, "strLyric")?.let(::toLrc)?.takeIf { it.isNotBlank() }
                ?: return@mapNotNull null
            Candidate(
                id = tag(inner, "strInfoID").orEmpty(),
                title = tag(inner, "strTitle")?.let(::decode).orEmpty(),
                artist = tag(inner, "strArtistName")?.let(::decode).orEmpty(),
                lrc = lrc,
            )
        }.toList()

    private fun tag(block: String, name: String): String? =
        Regex("<$name(?:\\s[^>]*)?>([\\s\\S]*?)</$name>").find(block)?.groupValues?.get(1)

    /** The line breaks arrive as an entity-escaped `<br>`; turn them into newlines before decoding the rest. */
    private fun toLrc(raw: String): String =
        decode(raw.replace(BREAK, "\n")).replace("\r\n", "\n").replace('\r', '\n')

    private fun decode(text: String): String = text
        .replace(NUMERIC_ENTITY) { m ->
            val code = m.groupValues[2].toIntOrNull(if (m.groupValues[1].isEmpty()) 10 else 16)
            code?.takeIf { Character.isValidCodePoint(it) }?.let { String(Character.toChars(it)) } ?: m.value
        }
        .replace("&lt;", "<").replace("&gt;", ">")
        .replace("&quot;", "\"").replace("&apos;", "'").replace("&amp;", "&")

    /**
     * The best candidate for this recording, or null when none is believable.
     *
     * The title must match (equal, or one contains the other, once
     * normalised). A matching artist ranks higher but is not required: the
     * service has already searched by it, and a romanised name rarely equals a
     * Hangul one. Candidates with fewer than [MIN_TIMED_LINES] stamped lines,
     * or whose last stamp runs more than [DURATION_SLACK_MS] past the end of
     * the song, are dropped — a different edit would drift out of sync, which
     * is worse than no lyrics. Of the rest, the closer match wins, then the one
     * whose last stamp sits nearest the end of the song.
     */
    fun pick(candidates: List<Candidate>, title: String, artist: String, durationMs: Long): Candidate? {
        val wantTitle = normalize(title)
        if (wantTitle.isEmpty()) return null
        val wantArtists = artistTokens(artist)
        return candidates.mapNotNull { candidate ->
            val gotTitle = normalize(candidate.title)
            val titleScore = when {
                gotTitle.isEmpty() -> return@mapNotNull null
                gotTitle == wantTitle -> 2
                gotTitle.contains(wantTitle) || wantTitle.contains(gotTitle) -> 1
                else -> return@mapNotNull null
            }
            val gotArtists = artistTokens(candidate.artist)
            val artistScore = when {
                wantArtists.any { it in gotArtists } -> 2
                wantArtists.any { w -> gotArtists.any { g -> g.contains(w) || w.contains(g) } } -> 1
                else -> 0
            }
            val stamps = stamps(candidate.lrc)
            if (stamps.size < MIN_TIMED_LINES || stamps.last() <= 0L) return@mapNotNull null
            val last = stamps.last()
            if (durationMs > 0 && last > durationMs + DURATION_SLACK_MS) return@mapNotNull null
            val gap = if (durationMs > 0) abs(durationMs - last) else 0L
            Ranked(candidate, titleScore * 3 + artistScore, gap)
        }.sortedWith(compareByDescending<Ranked> { it.match }.thenBy { it.gap })
            .firstOrNull()?.candidate
    }

    private class Ranked(val candidate: Candidate, val match: Int, val gap: Long)

    /** Stamp times of the lines that have one, in file order. */
    private fun stamps(lrc: String): List<Long> = lrc.lineSequence().mapNotNull { line ->
        STAMP.find(line)?.let { m ->
            val (min, sec, frac) = m.destructured
            val fracMs = when (frac.length) {
                2 -> frac.toLong() * 10
                3 -> frac.toLong()
                else -> 0L
            }
            min.toLong() * 60_000 + sec.toLong() * 1_000 + fracMs
        }
    }.toList()

    private fun artistTokens(artist: String): List<String> =
        artist.split(ARTIST_SEPARATOR).map(::normalize).filter { it.isNotEmpty() }

    /** Lower case, bracketed asides and "feat." tails removed, everything but letters and digits dropped. */
    fun normalize(text: String): String = text.lowercase()
        .replace(PARENTHETICAL, "")
        .replace(FEATURING, "")
        .replace(NOT_ALNUM, "")

    private val BLOCK = Regex("<ST_GET_RESEMBLELYRIC2_RETURN>([\\s\\S]*?)</ST_GET_RESEMBLELYRIC2_RETURN>")
    private val BREAK = Regex("<br\\s*/?>|&lt;br\\s*/?&gt;", RegexOption.IGNORE_CASE)
    private val NUMERIC_ENTITY = Regex("&#(x?)([0-9a-fA-F]+);")
    private val STAMP = Regex("""\[(\d{1,3}):(\d{2})\.(\d{2,3})]""")
    private val PARENTHETICAL = Regex("""[(（\[{].*?[)）\]}]""")
    private val FEATURING = Regex("""\s(?:feat|ft)\.?\s.*""")
    private val NOT_ALNUM = Regex("""[^\p{L}\p{N}]""")
    private val ARTIST_SEPARATOR = Regex("""[,&/·、]|\s+(?:feat\.?|ft\.?|x)\s+""", RegexOption.IGNORE_CASE)
}
