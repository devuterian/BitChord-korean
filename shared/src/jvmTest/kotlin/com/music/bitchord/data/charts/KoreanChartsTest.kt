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
}
