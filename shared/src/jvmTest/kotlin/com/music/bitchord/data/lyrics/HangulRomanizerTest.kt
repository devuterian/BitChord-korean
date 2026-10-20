package com.music.bitchord.data.lyrics

import kotlin.test.Test
import kotlin.test.assertEquals

class HangulRomanizerTest {
    private fun check(expected: String, hangul: String) =
        assertEquals(expected, HangulRomanizer.romanize(hangul), hangul)

    @Test
    fun plainSyllablesAndLiaison() {
        check("bampyeonji", "밤편지")
        check("joa", "좋아")
        check("saranghae", "사랑해")
        check("hangugeo", "한국어")
        check("ilgeo", "읽어")
        check("anja", "앉아")
        check("sireo", "싫어")
        check("masisseo", "맛있어")
        check("bakke", "밖에")
        check("gapsi", "값이")
        check("ui", "의")
    }

    @Test
    fun assimilations() {
        check("silla", "신라")
        check("gachi", "같이")
        check("haedoji", "해돋이")
        check("gungmul", "국물")
        check("bomnal", "봄날")
        check("simni", "심리")
        check("jongno", "종로")
        check("gungnyeok", "국력")
        check("kallal", "칼날")
        check("noko", "놓고")
        check("anta", "않다")
        check("japyeo", "잡혀")
        check("motada", "못하다")
        check("nonneun", "놓는")
    }

    @Test
    fun mixedTextKeepsNonHangul() {
        check("i love you, saranghae!", "i love you, 사랑해!")
        check("bamedo (oh)", "밤에도 (oh)")
    }
}
