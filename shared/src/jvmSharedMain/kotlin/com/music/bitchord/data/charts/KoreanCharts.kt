package com.music.bitchord.data.charts

import com.music.bitchord.data.Http
import com.music.bitchord.data.model.HomeShelf
import com.music.bitchord.data.model.ShelfItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import okhttp3.FormBody
import okhttp3.Request
import java.io.File
import java.time.LocalDate
import java.time.temporal.IsoFields
import java.util.concurrent.TimeUnit
import kotlin.math.abs

/**
 * Melon's real-time TOP 100 and Circle Chart's weekly digital chart, as plain
 * title/artist lists. Matching them to YouTube Music tracks happens in
 * [com.music.bitchord.data.YtMusicRepository.koreanChartShelves].
 *
 * Neither is a published API. Melon's is the endpoint its own mobile web
 * chart calls with a public client key; Circle Chart's is what its chart page
 * posts to. Both can change or disappear without notice, so every failure here
 * is an empty list and Home simply goes without the shelf.
 */
object KoreanCharts {
    const val MELON_TITLE = "Melon Top 100"
    const val CIRCLE_TITLE = "Circle Chart weekly"

    /** One chart row. [durationSeconds] is null when the chart doesn't state one. */
    data class Entry(val rank: Int, val title: String, val artist: String, val durationSeconds: Int?)

    /** How many entries of each chart are looked up on YouTube Music. */
    const val RESOLVE_COUNT = 30

    /** A resolved shelf is reused for this long before the charts are fetched again. */
    const val CACHE_TTL_MS = 6L * 60 * 60 * 1000

    /** A match whose length differs from the chart's by more than this is a different recording. */
    const val DURATION_TOLERANCE_SECONDS = 15

    /** Where resolved shelves are kept between launches. Null keeps nothing on disk. */
    @Volatile
    var cacheDir: File? = null

    private const val MELON_URL =
        "https://m2.melon.com/m6/chart/ent/songChartList.json?cpId=AS40&cpKey=14LNC3&appVer=6.0.0.0"
    private const val CIRCLE_URL = "https://circlechart.kr/data/api/chart/onoff"
    private const val CIRCLE_PAGE = "https://circlechart.kr/page_chart/onoff.circle"
    private const val CIRCLE_WEEKS_TRIED = 4
    private const val CACHE_VERSION = 1
    private const val CACHE_DIRECTORY = "korean_charts"
    private const val USER_AGENT =
        "Mozilla/5.0 (Linux; Android 14) AppleWebKit/537.36 Chrome/130 Mobile Safari/537.36"

    private val json = Json { ignoreUnknownKeys = true }

    private val client by lazy {
        Http.client.newBuilder()
            .callTimeout(12, TimeUnit.SECONDS)
            .connectTimeout(5, TimeUnit.SECONDS)
            .build()
    }

    fun parseMelon(body: String): List<Entry> = runCatching {
        json.parseToJsonElement(body).jsonObject["response"]?.jsonObject?.get("SONGLIST")?.jsonArray
            .orEmpty()
            .mapIndexedNotNull { index, element ->
                val row = element as? JsonObject ?: return@mapIndexedNotNull null
                val title = row.text("SONGNAME") ?: return@mapIndexedNotNull null
                val artist = (row["ARTISTLIST"] as? JsonArray).orEmpty()
                    .mapNotNull { (it as? JsonObject)?.text("ARTISTNAME") }
                    .joinToString(", ")
                Entry(
                    rank = row.text("CURRANK")?.toIntOrNull() ?: (index + 1),
                    title = title,
                    artist = artist,
                    durationSeconds = row.text("PLAYTIME")?.toIntOrNull()?.takeIf { it > 0 },
                )
            }
            .sortedBy { it.rank }
    }.getOrDefault(emptyList())

    /** `List` is an object keyed "0", "1", … on the wire; an array is accepted too. */
    fun parseCircle(body: String): List<Entry> = runCatching {
        val rows = when (val list = json.parseToJsonElement(body).jsonObject["List"]) {
            is JsonObject -> list.entries.sortedBy { it.key.toIntOrNull() ?: Int.MAX_VALUE }.map { it.value }
            is JsonArray -> list.toList()
            else -> emptyList()
        }
        rows.mapIndexedNotNull { index, element ->
            val row = element as? JsonObject ?: return@mapIndexedNotNull null
            val title = row.text("SONG_NAME") ?: return@mapIndexedNotNull null
            Entry(
                rank = row.text("SERVICE_RANKING")?.toIntOrNull() ?: (index + 1),
                title = title,
                artist = row.text("ARTIST_NAME").orEmpty(),
                durationSeconds = null,
            )
        }.sortedBy { it.rank }
    }.getOrDefault(emptyList())

    private fun JsonObject.text(key: String): String? =
        (this[key] as? JsonPrimitive)?.contentOrNull?.trim()?.takeIf { it.isNotEmpty() }

    /** True unless both lengths are known and differ by more than [DURATION_TOLERANCE_SECONDS]. */
    fun durationAcceptable(chartSeconds: Int?, matchSeconds: Int?): Boolean {
        if (chartSeconds == null || chartSeconds <= 0 || matchSeconds == null || matchSeconds <= 0) return true
        return abs(chartSeconds - matchSeconds) <= DURATION_TOLERANCE_SECONDS
    }

    suspend fun fetchMelon(): List<Entry> = withContext(Dispatchers.IO) {
        runCatching {
            val request = Request.Builder().url(MELON_URL).header("User-Agent", USER_AGENT).build()
            client.newCall(request).execute().use { if (it.isSuccessful) it.body?.string() else null }
        }.getOrNull()?.let(::parseMelon).orEmpty()
    }

    /**
     * Circle publishes a week or two behind the calendar, and its week numbers
     * don't line up with ours by a fixed offset, so start at last week and
     * step back until one answers with a chart.
     */
    suspend fun fetchCircle(today: LocalDate = LocalDate.now()): List<Entry> = withContext(Dispatchers.IO) {
        val year = today.get(IsoFields.WEEK_BASED_YEAR)
        val week = today.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR)
        for (target in (week - 1) downTo maxOf(1, week - CIRCLE_WEEKS_TRIED)) {
            val body = runCatching {
                val form = FormBody.Builder()
                    .add("nationGbn", "T")
                    .add("serviceGbn", "ALL")
                    .add("termGbn", "week")
                    .add("hitYear", year.toString())
                    .add("targetTime", target.toString())
                    .add("yearTime", "3")
                    .add("curUrl", "circlechart.kr/page_chart/onoff.circle")
                    .build()
                val request = Request.Builder()
                    .url(CIRCLE_URL)
                    .header("User-Agent", USER_AGENT)
                    .header("Referer", CIRCLE_PAGE)
                    .post(form)
                    .build()
                client.newCall(request).execute().use { if (it.isSuccessful) it.body?.string() else null }
            }.getOrNull() ?: continue
            val entries = parseCircle(body)
            if (entries.isNotEmpty()) return@withContext entries
        }
        emptyList()
    }

    // ---- disk cache -----------------------------------------------------

    @Serializable
    private data class CachedItem(
        val title: String,
        val subtitle: String,
        val thumbnailUrl: String?,
        val videoId: String,
    )

    @Serializable
    private data class CachedShelf(val version: Int = CACHE_VERSION, val savedAt: Long, val items: List<CachedItem>)

    /** The shelf saved under [name] if it is younger than [CACHE_TTL_MS]. */
    fun readCached(name: String, title: String, now: Long = System.currentTimeMillis()): HomeShelf? {
        val file = cacheFile(name) ?: return null
        return runCatching {
            val cached = json.decodeFromString<CachedShelf>(file.readText())
            if (cached.version != CACHE_VERSION || now - cached.savedAt !in 0..CACHE_TTL_MS) return@runCatching null
            HomeShelf(
                title = title,
                items = cached.items.map { ShelfItem(it.title, it.subtitle, it.thumbnailUrl, it.videoId, null) },
            ).takeIf { it.items.isNotEmpty() }
        }.getOrNull()
    }

    fun writeCached(name: String, shelf: HomeShelf, now: Long = System.currentTimeMillis()) {
        val file = cacheFile(name) ?: return
        runCatching {
            file.parentFile?.mkdirs()
            val items = shelf.items.mapNotNull { item ->
                CachedItem(item.title, item.subtitle, item.thumbnailUrl, item.videoId ?: return@mapNotNull null)
            }
            val temporary = File(file.parentFile, "$name.tmp")
            temporary.writeText(json.encodeToString(CachedShelf.serializer(), CachedShelf(savedAt = now, items = items)))
            if (!temporary.renameTo(file)) {
                temporary.copyTo(file, overwrite = true)
                temporary.delete()
            }
        }
    }

    private fun cacheFile(name: String): File? = cacheDir?.let { File(File(it, CACHE_DIRECTORY), "$name.json") }
}
