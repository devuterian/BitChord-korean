package com.music.bitchord.desktop

/**
 * The links Share copies. With short links on (the default) a song goes out as
 * `youtu.be` and a playlist as a plain `youtube.com` one, which open in any
 * YouTube or YouTube Music app and preview in chat apps; off, both stay on
 * `music.youtube.com`. Mirrors the phone's `ShareLinks`.
 */
internal object DesktopShareLinks {
    fun song(videoId: String): String =
        if (DesktopAppearanceSettings.shortShareLinks.value) "https://youtu.be/$videoId"
        else "https://music.youtube.com/watch?v=$videoId"

    fun playlist(playlistId: String): String =
        if (DesktopAppearanceSettings.shortShareLinks.value && !musicOnly(playlistId)) {
            "https://www.youtube.com/playlist?list=$playlistId"
        } else {
            "https://music.youtube.com/playlist?list=$playlistId"
        }

    /** Mixes ("RD…") and Liked Music ("LM") only open on YouTube Music. */
    private fun musicOnly(playlistId: String): Boolean = playlistId.startsWith("RD") || playlistId == "LM"
}
