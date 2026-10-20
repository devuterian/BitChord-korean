package com.music.bitchord.download

import androidx.annotation.StringRes

/**
 * A download failure whose message is shown to the user. Carries a string
 * resource instead of text so it can be resolved in the user's language where
 * it is displayed; [message] is only a stable marker for logs.
 */
internal class DownloadError(
    @StringRes val res: Int,
    vararg val args: Any,
) : IllegalStateException("download error $res ${args.joinToString()}")

internal fun downloadError(@StringRes res: Int, vararg args: Any): Nothing =
    throw DownloadError(res, *args)
