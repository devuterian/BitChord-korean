package com.music.bitchord.desktop

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * How the Discord card is worded and what it carries.
 *
 * Keys and defaults are Android's, from `AppSettings`.
 */
internal object DesktopDiscordSettings {

    private val persistence = DesktopPersistence()

    private val _useDetails = MutableStateFlow(persistence.boolean(KEY_USE_DETAILS, false))
    private val _advancedMode = MutableStateFlow(persistence.boolean(KEY_ADVANCED, false))
    private val _status = MutableStateFlow(persistence.string(KEY_STATUS, "").ifBlank { "online" })
    private val _activityType = MutableStateFlow(persistence.string(KEY_ACTIVITY_TYPE, "").ifBlank { "listening" })
    private val _activityName = MutableStateFlow(persistence.string(KEY_ACTIVITY_NAME, ""))
    private val _button1Text = MutableStateFlow(persistence.string(KEY_BUTTON_1_TEXT, ""))
    private val _button1Visible = MutableStateFlow(persistence.boolean(KEY_BUTTON_1_VISIBLE, true))
    private val _button2Text = MutableStateFlow(persistence.string(KEY_BUTTON_2_TEXT, ""))
    private val _button2Visible = MutableStateFlow(persistence.boolean(KEY_BUTTON_2_VISIBLE, true))
    private val _showAudioQuality = MutableStateFlow(persistence.boolean(KEY_SHOW_AUDIO_QUALITY, true))

    /** Whether the song goes on the bold line instead of the artist. */
    val useDetails: StateFlow<Boolean> = _useDetails
    val advancedMode: StateFlow<Boolean> = _advancedMode
    val status: StateFlow<String> = _status
    val activityType: StateFlow<String> = _activityType
    val activityName: StateFlow<String> = _activityName
    val button1Text: StateFlow<String> = _button1Text
    val button1Visible: StateFlow<Boolean> = _button1Visible
    val button2Text: StateFlow<String> = _button2Text
    val button2Visible: StateFlow<Boolean> = _button2Visible
    val showAudioQuality: StateFlow<Boolean> = _showAudioQuality

    fun setUseDetails(value: Boolean) = flag(_useDetails, KEY_USE_DETAILS, value)
    fun setAdvancedMode(value: Boolean) = flag(_advancedMode, KEY_ADVANCED, value)
    fun setButton1Visible(value: Boolean) = flag(_button1Visible, KEY_BUTTON_1_VISIBLE, value)
    fun setButton2Visible(value: Boolean) = flag(_button2Visible, KEY_BUTTON_2_VISIBLE, value)
    fun setShowAudioQuality(value: Boolean) = flag(_showAudioQuality, KEY_SHOW_AUDIO_QUALITY, value)

    fun setStatus(value: String) = text(_status, KEY_STATUS, value)
    fun setActivityType(value: String) = text(_activityType, KEY_ACTIVITY_TYPE, value)
    fun setActivityName(value: String) = text(_activityName, KEY_ACTIVITY_NAME, value)
    fun setButton1Text(value: String) = text(_button1Text, KEY_BUTTON_1_TEXT, value)
    fun setButton2Text(value: String) = text(_button2Text, KEY_BUTTON_2_TEXT, value)

    private fun flag(flow: MutableStateFlow<Boolean>, key: String, value: Boolean) {
        persistence.saveBoolean(key, value)
        flow.value = value
    }

    private fun text(flow: MutableStateFlow<String>, key: String, value: String) {
        persistence.saveString(key, value)
        flow.value = value
    }

    /** `{song_name}`, `{artist_name}` and `{album_name}`, filled in. */
    fun resolveVariables(text: String, title: String, artist: String, album: String?): String = text
        .replace("{song_name}", title)
        .replace("{artist_name}", artist)
        .replace("{album_name}", album.orEmpty())

    /** Discord's activity type numbers. */
    fun activityNumber(type: String): Int = when (type) {
        "playing" -> 0
        "watching" -> 3
        "competing" -> 5
        else -> 2
    }

    val statuses get() = listOf(
        Choice("online", DesktopStrings["status_online", "Online"], DesktopStrings["status_online_detail", "Green dot"]),
        Choice("idle", DesktopStrings["status_idle", "Idle"], DesktopStrings["status_idle_detail", "Amber crescent, shown as away"]),
        Choice("dnd", DesktopStrings["status_dnd", "Do not disturb"], DesktopStrings["status_dnd_detail", "Red dash. Also suppresses notifications."]),
    )

    val activities get() = listOf(
        Choice("listening", DesktopStrings["activity_listening", "Listening"], DesktopStrings["verb_listening", "Listening to"]),
        Choice("playing", DesktopStrings["activity_playing", "Playing"], DesktopStrings["verb_playing", "Playing"]),
        Choice("watching", DesktopStrings["activity_watching", "Watching"], DesktopStrings["verb_watching", "Watching"]),
        Choice("competing", DesktopStrings["activity_competing", "Competing"], DesktopStrings["verb_competing", "Competing in"]),
    )

    /** One option in a choice list: what it is called and what it does. */
    internal data class Choice(val id: String, val label: String, val detail: String)

    fun statusLabel(id: String) = statuses.firstOrNull { it.id == id }?.label ?: id
    fun activityLabel(id: String) = activities.firstOrNull { it.id == id }?.label ?: id
    fun activityVerb(id: String) = activities.firstOrNull { it.id == id }?.detail ?: DesktopStrings["verb_listening", "Listening to"]

    const val DEFAULT_BUTTON_1 = "Listen on YouTube Music"
    const val DEFAULT_BUTTON_2 = "Visit BitChord"
    const val APP_NAME = "BitChord"

    private const val KEY_USE_DETAILS = "discord_use_details"
    private const val KEY_ADVANCED = "discord_advanced_mode"
    private const val KEY_STATUS = "discord_status"
    private const val KEY_ACTIVITY_TYPE = "discord_activity_type"
    private const val KEY_ACTIVITY_NAME = "discord_activity_name"
    private const val KEY_BUTTON_1_TEXT = "discord_button_1_text"
    private const val KEY_BUTTON_1_VISIBLE = "discord_button_1_visible"
    private const val KEY_BUTTON_2_TEXT = "discord_button_2_text"
    private const val KEY_BUTTON_2_VISIBLE = "discord_button_2_visible"
    private const val KEY_SHOW_AUDIO_QUALITY = "discord_show_audio_quality"
}
