package com.music.bitchord.ui.components

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.music.bitchord.R

/**
 * The audio engine publishes its status as English strings (device names,
 * fallback reasons, Bluetooth codec labels). This maps the known ones to string
 * resources at the point they are displayed, and leaves anything it does not
 * recognise (device names, vendor diagnostics) untouched.
 */
private val PIPELINE_TEXT_EXACT: Map<String, Int> = mapOf(
    "System default" to R.string.pipeline_system_default,
    "System Managed" to R.string.pipeline_bt_system_managed,
    "Not exposed by Android" to R.string.bt_not_exposed,
    "Bluetooth Device" to R.string.bt_device_default_name,
    "High Quality (990 kbps nominal)" to R.string.bt_ldac_high_quality,
    "Standard (660 kbps nominal)" to R.string.bt_ldac_standard,
    "Connection Priority (330 kbps nominal)" to R.string.bt_ldac_connection_priority,
    "Adaptive Bitrate (ABR)" to R.string.bt_ldac_adaptive,
    "Hearing Aid" to R.string.transport_hearing_aid,
    "16-bit fallback" to R.string.fallback_16bit,
    "Speaker output capped at 16-bit to avoid OEM mixer distortion" to R.string.fallback_speaker_16bit,
    "Speaker output capped at 16-bit PCM to prevent OEM mixer distortion" to R.string.fallback_speaker_pcm16,
    "Route exposes direct 24-bit PCM (Float32 converted to packed 24-bit)" to R.string.fallback_route_24bit,
    "Route does not expose direct Float32 or 24-bit PCM (using direct 16-bit PCM)" to R.string.fallback_route_16bit_a,
    "Route does not expose direct 24-bit PCM (using direct 16-bit PCM)" to R.string.fallback_route_16bit_b,
    "Direct playback unavailable for active USB device" to R.string.fallback_direct_unavailable,
)

private val PIPELINE_TEXT_PATTERNS: List<Pair<Regex, Int>> = listOf(
    Regex("Bluetooth route(.*) does not advertise Float32 output") to R.string.fallback_bt_no_float,
    Regex("Bluetooth route(.*) advertises 16-bit PCM only") to R.string.fallback_bt_pcm16,
    Regex("Direct playback unavailable for active (.+) device") to R.string.fallback_direct_unavailable,
    Regex("(.+) → 16-bit; this route cannot open a float track") to R.string.fallback_no_float_route,
)

fun Context.localizePipelineText(text: String): String {
    PIPELINE_TEXT_EXACT[text]?.let { id ->
        // The USB case carries its device kind as an argument.
        return if (id == R.string.fallback_direct_unavailable) getString(id, "USB") else getString(id)
    }
    for ((regex, id) in PIPELINE_TEXT_PATTERNS) {
        val match = regex.matchEntire(text) ?: continue
        return getString(id, match.groupValues[1])
    }
    return text
}

@Composable
fun localizedPipelineText(text: String): String = LocalContext.current.localizePipelineText(text)
