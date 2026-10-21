package com.music.bitchord.data.charts

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class KoreanChartsTest {
    @Test
    fun melonRowsKeepRankArtistsAndLength() {
        val body = """
            {"response":{"STATUS":"0","SONGLIST":[
              {"SONGNAME":"B side","ARTISTLIST":[{"ARTISTNAME":"X"}],"PLAYTIME":"180","CURRANK":"2"},
              {"SONGNAME":"NEON","ARTISTLIST":[{"ARTISTNAME":"A2O"},{"ARTISTNAME":"A2O LITE"}],"PLAYTIME":"208","CURRANK":"1"},
              {"ARTISTLIST":[],"CURRANK":"3"}
            ]}}
        """.trimIndent()
        val entries = KoreanCharts.parseMelon(body)
        assertEquals(listOf("NEON", "B side"), entries.map { it.title })
        assertEquals("A2O, A2O LITE", entries[0].artist)
        assertEquals(208, entries[0].durationSeconds)
        assertEquals(1, entries[0].rank)
    }

    @Test
    fun melonGarbageIsEmpty() {
        assertTrue(KoreanCharts.parseMelon("<html>").isEmpty())
        assertTrue(KoreanCharts.parseMelon("""{"response":{"STATUS":"1"}}""").isEmpty())
    }

    @Test
    fun circleListIsKeyedByIndexAndOrderedNumerically() {
        val body = """
            {"FormToMap":{},"List":{
              "10":{"SERVICE_RANKING":"11","SONG_NAME":"Eleven","ARTIST_NAME":"E"},
              "1":{"SERVICE_RANKING":"2","SONG_NAME":"Two","ARTIST_NAME":"소연 (SOYEON)"},
              "0":{"SERVICE_RANKING":"1","SONG_NAME":"Tic Tac Toe","ARTIST_NAME":"온유 (ONEW), 지코 (ZICO)"}
            }}
        """.trimIndent()
        val entries = KoreanCharts.parseCircle(body)
        assertEquals(listOf("Tic Tac Toe", "Two", "Eleven"), entries.map { it.title })
        assertEquals("온유 (ONEW), 지코 (ZICO)", entries[0].artist)
        assertEquals(null, entries[0].durationSeconds)
    }

    @Test
    fun circleErrorResponseIsEmpty() {
        assertTrue(KoreanCharts.parseCircle("""{"FormToMap":{},"ResultStatus":"Error"}""").isEmpty())
    }

    @Test
    fun durationGate() {
        assertTrue(KoreanCharts.durationAcceptable(208, 215))
        assertTrue(KoreanCharts.durationAcceptable(208, 223))
        assertFalse(KoreanCharts.durationAcceptable(208, 224))
        assertTrue(KoreanCharts.durationAcceptable(null, 500))
        assertTrue(KoreanCharts.durationAcceptable(208, 0))
    }

    @Test
    fun circleWeeksWrapIntoPreviousIsoYear() {
        // 2026-01-07 is ISO week 2 of 2026; 2025 has 52 weeks.
        assertEquals(
            listOf(2026 to 1, 2025 to 52, 2025 to 51, 2025 to 50),
            KoreanCharts.circleWeeks(java.time.LocalDate.of(2026, 1, 7)),
        )
        // 2021-01-06 is ISO week 1 of 2021; 2020 has 53 weeks.
        assertEquals(
            listOf(2020 to 53, 2020 to 52, 2020 to 51, 2020 to 50),
            KoreanCharts.circleWeeks(java.time.LocalDate.of(2021, 1, 6)),
        )
        assertEquals(
            listOf(2026 to 9, 2026 to 8, 2026 to 7, 2026 to 6),
            KoreanCharts.circleWeeks(java.time.LocalDate.of(2026, 3, 4)),
        )
    }
}
