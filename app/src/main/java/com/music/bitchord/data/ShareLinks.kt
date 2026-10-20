package com.music.bitchord.data

import com.music.bitchord.data.settings.AppSettings

/**
 * The links Share hands out. With short links on (the default) a song goes
 * out as `youtu.be` and a playlist as a plain `youtube.com` one, which open in
 * any YouTube or YouTube Music app and preview in chat apps; off, both stay on
 * `music.youtube.com`.
 */
object ShareLinks {
    fun song(videoId: String): String =
        if (AppSettings.shortShareLinks.value) "https://youtu.be/$videoId"
        else "https://music.youtube.com/watch?v=$videoId"

    fun playlist(playlistId: String): String =
        if (AppSettings.shortShareLinks.value) "https://www.youtube.com/playlist?list=$playlistId"
        else "https://music.youtube.com/playlist?list=$playlistId"
}
