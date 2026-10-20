package com.music.bitchord.data.lyrics

/**
 * On-device Revised Romanization of Korea (국어의 로마자 표기법) for lyrics.
 *
 * Hangul runs are decomposed into syllables, the pronunciation rules that the
 * spelling follows (final neutralisation, liaison, aspiration, palatalisation,
 * nasalisation, liquid assimilation) are applied across each run, and the
 * result is written in lowercase. Tensification is not written, as in the
 * official standard. Everything outside a Hangul run is copied unchanged.
 */
object HangulRomanizer {

    private const val SYLLABLE_FIRST = 0xAC00
    private const val SYLLABLE_LAST = 0xD7A3

    private const val CHO = "ㄱㄲㄴㄷㄸㄹㅁㅂㅃㅅㅆㅇㅈㅉㅊㅋㅌㅍㅎ"
    private val JONG = listOf(
        "", "ㄱ", "ㄲ", "ㄱㅅ", "ㄴ", "ㄴㅈ", "ㄴㅎ", "ㄷ", "ㄹ", "ㄹㄱ", "ㄹㅁ", "ㄹㅂ", "ㄹㅅ",
        "ㄹㅌ", "ㄹㅍ", "ㄹㅎ", "ㅁ", "ㅂ", "ㅂㅅ", "ㅅ", "ㅆ", "ㅇ", "ㅈ", "ㅊ", "ㅋ", "ㅌ", "ㅍ", "ㅎ",
    )

    private val ONSET_ROMAN = mapOf(
        'ㄱ' to "g", 'ㄲ' to "kk", 'ㄴ' to "n", 'ㄷ' to "d", 'ㄸ' to "tt", 'ㄹ' to "r", 'ㅁ' to "m",
        'ㅂ' to "b", 'ㅃ' to "pp", 'ㅅ' to "s", 'ㅆ' to "ss", 'ㅇ' to "", 'ㅈ' to "j", 'ㅉ' to "jj",
        'ㅊ' to "ch", 'ㅋ' to "k", 'ㅌ' to "t", 'ㅍ' to "p", 'ㅎ' to "h",
    )
    private val VOWEL_ROMAN = listOf(
        "a", "ae", "ya", "yae", "eo", "e", "yeo", "ye", "o", "wa", "wae", "oe", "yo", "u", "wo", "we",
        "wi", "yu", "eu", "ui", "i",
    )
    private val FINAL_ROMAN = mapOf(
        'ㄱ' to "k", 'ㄴ' to "n", 'ㄷ' to "t", 'ㄹ' to "l", 'ㅁ' to "m", 'ㅂ' to "p", 'ㅇ' to "ng",
    )
    private val ASPIRATED = mapOf('ㄱ' to 'ㅋ', 'ㄷ' to 'ㅌ', 'ㅂ' to 'ㅍ', 'ㅈ' to 'ㅊ')
    private const val VOWEL_I = 20

    private class Syllable(var onset: Char, val vowel: Int, var coda: String)

    fun isHangulSyllable(char: Char): Boolean = char.code in SYLLABLE_FIRST..SYLLABLE_LAST

    fun containsHangul(text: String): Boolean = text.any(::isHangulSyllable)

    fun romanize(text: String): String {
        val out = StringBuilder(text.length * 2)
        var index = 0
        while (index < text.length) {
            if (!isHangulSyllable(text[index])) {
                out.append(text[index++])
                continue
            }
            val start = index
            while (index < text.length && isHangulSyllable(text[index])) index++
            out.append(romanizeRun(text.substring(start, index)))
        }
        return out.toString()
    }

    private fun romanizeRun(run: String): String {
        val syllables = run.map { char ->
            val offset = char.code - SYLLABLE_FIRST
            Syllable(CHO[offset / 588], offset % 588 / 28, JONG[offset % 28])
        }
        for (i in 0 until syllables.size - 1) applyRules(syllables[i], syllables[i + 1])
        syllables.last().coda = neutralize(syllables.last().coda, null)

        val out = StringBuilder()
        var previousCoda = ""
        for (syllable in syllables) {
            // ㄹ is "l" only when it doubles a final ㄹ (신라 -> silla), otherwise "r".
            out.append(
                if (syllable.onset == 'ㄹ' && previousCoda == "ㄹ") "l" else ONSET_ROMAN.getValue(syllable.onset),
            )
            out.append(VOWEL_ROMAN[syllable.vowel])
            out.append(syllable.coda.firstOrNull()?.let { FINAL_ROMAN.getValue(it) }.orEmpty())
            previousCoda = syllable.coda
        }
        return out.toString()
    }

    /** Representative sound of a final consonant (받침 대표음 중화). */
    private fun neutralize(coda: String, nextOnset: Char?): String = when (coda) {
        "", "ㅇ", "ㄴ", "ㄹ", "ㅁ" -> coda
        "ㄱ", "ㄲ", "ㅋ", "ㄱㅅ" -> "ㄱ"
        "ㄴㅈ", "ㄴㅎ" -> "ㄴ"
        "ㄷ", "ㅅ", "ㅆ", "ㅈ", "ㅊ", "ㅌ", "ㅎ" -> "ㄷ"
        "ㄹㄱ" -> if (nextOnset == 'ㄱ') "ㄹ" else "ㄱ"
        "ㄹㅁ" -> "ㅁ"
        "ㄹㅂ", "ㄹㅅ", "ㄹㅌ", "ㄹㅎ" -> "ㄹ"
        "ㅂ", "ㅍ", "ㅂㅅ", "ㄹㅍ" -> "ㅂ"
        else -> coda
    }

    private fun applyRules(current: Syllable, next: Syllable) {
        val coda = current.coda
        if (coda.isEmpty()) return

        if (next.onset == 'ㅇ') {
            // 구개음화: ㄷ/ㅌ + 이
            if (next.vowel == VOWEL_I && (coda == "ㄷ" || coda == "ㅌ")) {
                next.onset = if (coda == "ㄷ") 'ㅈ' else 'ㅊ'
                current.coda = ""
                return
            }
            // 연음: a lone ㅇ stays, a lone ㅎ goes silent, otherwise the last consonant moves.
            when {
                coda == "ㅇ" -> Unit
                coda == "ㅎ" -> current.coda = ""
                coda.length == 2 && coda[1] == 'ㅎ' -> {
                    next.onset = coda[0]
                    current.coda = ""
                }
                coda.length == 2 -> {
                    next.onset = coda[1]
                    current.coda = coda.take(1)
                }
                else -> {
                    next.onset = coda[0]
                    current.coda = ""
                }
            }
            if (current.coda.isNotEmpty()) current.coda = neutralize(current.coda, null)
            return
        }

        val onset = next.onset
        // 격음화: ㅎ (also inside ㄶ/ㅀ) + ㄱㄷㅈ
        if (coda.last() == 'ㅎ' && onset in ASPIRATED) {
            next.onset = ASPIRATED.getValue(onset)
            current.coda = neutralize(if (coda.length == 2) coda.take(1) else "", null)
            return
        }
        var rep = neutralize(coda, onset)
        // 격음화: ㄱㄷㅂ + ㅎ (구개음화 when ㄷ meets 히)
        if (onset == 'ㅎ' && rep.isNotEmpty() && rep[0] in ASPIRATED) {
            next.onset = if (rep == "ㄷ" && next.vowel == VOWEL_I) 'ㅊ' else ASPIRATED.getValue(rep[0])
            current.coda = ""
            return
        }
        // 비음화: ㄱㄷㅂ + ㄴㅁ, and ㄹ after anything but ㄴㄹ becomes ㄴ
        if (onset == 'ㄹ' && rep != "ㄴ" && rep != "ㄹ") next.onset = 'ㄴ'
        if (next.onset == 'ㄴ' || next.onset == 'ㅁ') {
            rep = when (rep) {
                "ㄱ" -> "ㅇ"
                "ㄷ" -> "ㄴ"
                "ㅂ" -> "ㅁ"
                else -> rep
            }
        }
        // 유음화: ㄴㄹ and ㄹㄴ both sound as ㄹㄹ
        if ((rep == "ㄴ" && onset == 'ㄹ') || (rep == "ㄹ" && onset == 'ㄴ')) {
            rep = "ㄹ"
            next.onset = 'ㄹ'
        }
        current.coda = rep
    }
}
