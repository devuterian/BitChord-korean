package com.music.bitchord

import com.music.bitchord.data.innertube.InnertubeParser
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import org.junit.Assert.assertEquals
import org.junit.Test

class PodcastEpisodesTest {

    @Test
    fun `podcast show page lists its episodes under the show's author`() {
        val json = """
        {
          "contents": { "twoColumnBrowseResultsRenderer": {
            "tabs": [{ "tabRenderer": { "content": { "sectionListRenderer": { "contents": [{
              "musicResponsiveHeaderRenderer": {
                "title": { "runs": [{ "text": "침착맨의 이상형 월드컵" }] },
                "straplineTextOne": { "runs": [{ "text": "침착맨" }] }
              }
            }] } } } }],
            "secondaryContents": { "sectionListRenderer": { "contents": [{
              "musicShelfRenderer": { "contents": [{
                "musicMultiRowListItemRenderer": {
                  "thumbnail": { "musicThumbnailRenderer": { "thumbnail": { "thumbnails": [
                    { "url": "https://i.ytimg.com/vi/FE697-KzQSE/hq720.jpg", "width": 800, "height": 450 }
                  ] } } },
                  "onTap": { "watchEndpoint": { "videoId": "FE697-KzQSE" } },
                  "title": { "runs": [{ "text": "견디기 힘든 호들갑 월드컵 (with 허간민)" }] },
                  "subtitle": { "runs": [{ "text": "조회수 175만회" }, { "text": " • " }, { "text": "2일 전" }] }
                }
              }] }
            }] } }
          } }
        }
        """.trimIndent()

        val songs = InnertubeParser.collectSongsDeep(Json.parseToJsonElement(json).jsonObject)

        assertEquals(1, songs.size)
        assertEquals("FE697-KzQSE", songs[0].videoId)
        assertEquals("견디기 힘든 호들갑 월드컵 (with 허간민)", songs[0].title)
        assertEquals("침착맨", songs[0].artist)
        assertEquals("https://i.ytimg.com/vi/FE697-KzQSE/hq720.jpg", songs[0].thumbnailUrl)
    }
}
