package com.music.bitchord.desktop

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * The toggles read far from the Settings sheet that sets them.
 *
 * Mirrors Android's `AppSettings` for these few: a composable deep in the player or the lyrics
 * pane can collect one without it being threaded down through every caller.
 */
internal object DesktopAppearanceSettings {

    private fun flag(key: String, default: Boolean) =
        MutableStateFlow(DesktopPersistence().boolean(key, default))

    private val _reduceAnimation = flag(KEY_REDUCE_ANIMATION, false)
    private val _reduceDynamicBlur = flag(KEY_REDUCE_DYNAMIC_BLUR, false)
    private val _hideVolumeBar = flag(KEY_HIDE_VOLUME_BAR, false)
    private val _ambientBackdrop = flag(KEY_AMBIENT_BACKDROP, false)
    private val _hidePlayerHandle = flag(KEY_HIDE_PLAYER_HANDLE, false)
    private val _hideExplicitBadge = flag(KEY_HIDE_EXPLICIT_BADGE, false)
    private val _hideLikeButton = flag(KEY_HIDE_LIKE_BUTTON, false)
    private val _hideLyricStrip = flag(KEY_HIDE_LYRIC_STRIP, false)
    private val _hideTimeLabels = flag(KEY_HIDE_TIME_LABELS, false)
    private val _hideQualityBadge = flag(KEY_HIDE_QUALITY_BADGE, false)
    private val _hideMixingIndicator = flag(KEY_HIDE_MIXING_INDICATOR, false)
    private val _hideListenTogetherButton = flag(KEY_HIDE_LISTEN_TOGETHER_BUTTON, false)
    private val _hideOutputCaption = flag(KEY_HIDE_OUTPUT_CAPTION, false)
    private val _useSystemFont = flag(KEY_USE_SYSTEM_FONT, false)
    private val _shortShareLinks = flag(KEY_SHORT_SHARE_LINKS, true)

    /** Freezes the main player's gradient instead of drifting. */
    val reduceAnimation: StateFlow<Boolean> = _reduceAnimation

    /** Swaps frosted glass for solid fills across the app. */
    val reduceDynamicBlur: StateFlow<Boolean> = _reduceDynamicBlur

    /** Removes the volume slider from the main player. */
    val hideVolumeBar: StateFlow<Boolean> = _hideVolumeBar

    /** Diffuses current artwork colors under the window glass chrome like Apple Music. */
    val ambientBackdrop: StateFlow<Boolean> = _ambientBackdrop

    /** Hides the drag handle at the top of the main player. */
    val hidePlayerHandle: StateFlow<Boolean> = _hidePlayerHandle

    /** Hides the explicit badge beside the title in the main player. */
    val hideExplicitBadge: StateFlow<Boolean> = _hideExplicitBadge

    /** Hides the like button beside the title in the main player. */
    val hideLikeButton: StateFlow<Boolean> = _hideLikeButton

    /** Hides the single current lyric line above the scrubber. */
    val hideLyricStrip: StateFlow<Boolean> = _hideLyricStrip

    /** Hides the elapsed and remaining time under the scrubber. */
    val hideTimeLabels: StateFlow<Boolean> = _hideTimeLabels

    /** Hides the Lossless / Hi-Res badge under the scrubber. */
    val hideQualityBadge: StateFlow<Boolean> = _hideQualityBadge

    /** Hides the Automix sheen, transition marker and "Mixing" label. */
    val hideMixingIndicator: StateFlow<Boolean> = _hideMixingIndicator

    /** Hides the Listen Together half of the output capsule. */
    val hideListenTogetherButton: StateFlow<Boolean> = _hideListenTogetherButton

    /** Hides the output device or party name under the player's action row. */
    val hideOutputCaption: StateFlow<Boolean> = _hideOutputCaption

    /** Sets the app in the platform font instead of the bundled Pretendard JP. */
    val useSystemFont: StateFlow<Boolean> = _useSystemFont

    /** Share and copy hand out youtu.be / youtube.com links instead of music.youtube.com ones. */
    val shortShareLinks: StateFlow<Boolean> = _shortShareLinks

    fun setReduceAnimation(value: Boolean) = write(KEY_REDUCE_ANIMATION, value, _reduceAnimation)

    fun setReduceDynamicBlur(value: Boolean) = write(KEY_REDUCE_DYNAMIC_BLUR, value, _reduceDynamicBlur)

    fun setHideVolumeBar(value: Boolean) = write(KEY_HIDE_VOLUME_BAR, value, _hideVolumeBar)

    fun setAmbientBackdrop(value: Boolean) = write(KEY_AMBIENT_BACKDROP, value, _ambientBackdrop)

    fun setHidePlayerHandle(value: Boolean) = write(KEY_HIDE_PLAYER_HANDLE, value, _hidePlayerHandle)

    fun setHideExplicitBadge(value: Boolean) = write(KEY_HIDE_EXPLICIT_BADGE, value, _hideExplicitBadge)

    fun setHideLikeButton(value: Boolean) = write(KEY_HIDE_LIKE_BUTTON, value, _hideLikeButton)

    fun setHideLyricStrip(value: Boolean) = write(KEY_HIDE_LYRIC_STRIP, value, _hideLyricStrip)

    fun setHideTimeLabels(value: Boolean) = write(KEY_HIDE_TIME_LABELS, value, _hideTimeLabels)

    fun setHideQualityBadge(value: Boolean) = write(KEY_HIDE_QUALITY_BADGE, value, _hideQualityBadge)

    fun setHideMixingIndicator(value: Boolean) = write(KEY_HIDE_MIXING_INDICATOR, value, _hideMixingIndicator)

    fun setHideListenTogetherButton(value: Boolean) =
        write(KEY_HIDE_LISTEN_TOGETHER_BUTTON, value, _hideListenTogetherButton)

    fun setHideOutputCaption(value: Boolean) = write(KEY_HIDE_OUTPUT_CAPTION, value, _hideOutputCaption)

    fun setUseSystemFont(value: Boolean) = write(KEY_USE_SYSTEM_FONT, value, _useSystemFont)

    fun setShortShareLinks(value: Boolean) = write(KEY_SHORT_SHARE_LINKS, value, _shortShareLinks)

    private fun write(key: String, value: Boolean, into: MutableStateFlow<Boolean>) {
        DesktopPersistence().saveBoolean(key, value)
        into.value = value
    }

    internal const val KEY_REDUCE_ANIMATION = "reduce_animation"
    internal const val KEY_REDUCE_DYNAMIC_BLUR = "reduce_dynamic_blur"
    internal const val KEY_HIDE_VOLUME_BAR = "hide_volume_bar"
    internal const val KEY_AMBIENT_BACKDROP = "ambient_backdrop"
    internal const val KEY_HIDE_PLAYER_HANDLE = "hide_player_handle"
    internal const val KEY_HIDE_EXPLICIT_BADGE = "hide_explicit_badge"
    internal const val KEY_HIDE_LIKE_BUTTON = "hide_like_button"
    internal const val KEY_HIDE_LYRIC_STRIP = "hide_lyric_strip"
    internal const val KEY_HIDE_TIME_LABELS = "hide_time_labels"
    internal const val KEY_HIDE_QUALITY_BADGE = "hide_quality_badge"
    internal const val KEY_HIDE_MIXING_INDICATOR = "hide_mixing_indicator"
    internal const val KEY_HIDE_LISTEN_TOGETHER_BUTTON = "hide_listen_together_button"
    internal const val KEY_HIDE_OUTPUT_CAPTION = "hide_output_caption"
    internal const val KEY_USE_SYSTEM_FONT = "use_system_font"
    internal const val KEY_SHORT_SHARE_LINKS = "short_share_links"
}
