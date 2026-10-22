package com.music.bitchord.desktop

/** What this build is made of, and on what terms. */
internal data class DesktopLicense(
    val name: String,
    val license: String,
    val url: String,
    val note: String = "",
)

/** Grouped the way someone checking a licence actually reads. */
internal data class DesktopLicenseGroup(
    val title: String,
    val entries: List<DesktopLicense>,
)

internal object DesktopLicenses {

    val groups: List<DesktopLicenseGroup> = listOf(
        DesktopLicenseGroup(
            DesktopStrings["d_licenses_this_application", "This application"],
            listOf(
                DesktopLicense(
                    "BitChord",
                    "GPL-3.0-or-later",
                    "https://www.gnu.org/licenses/gpl-3.0.html",
                    DesktopStrings["d_licenses_bitchord_note", "Your music, your way."],
                ),
            ),
        ),
        DesktopLicenseGroup(
            DesktopStrings["d_licenses_audio", "Audio"],
            listOf(
                DesktopLicense(
                    "FFmpeg",
                    "LGPL-2.1-or-later",
                    "https://ffmpeg.org",
                    DesktopStrings["d_licenses_ffmpeg_note", "Decoding, demuxing and resampling. The LGPL build is used deliberately; the GPL-enabled variant is not shipped."],
                ),
                DesktopLicense(
                    "JavaCPP / JavaCPP Presets",
                    "Apache-2.0",
                    "https://github.com/bytedeco/javacpp",
                    DesktopStrings["d_licenses_javacpp_note", "The bridge from the JVM to FFmpeg."],
                ),
                DesktopLicense(
                    "OpenJFX",
                    "GPL-2.0-with-classpath-exception",
                    "https://openjfx.io",
                    DesktopStrings["d_licenses_openjfx_note", "Video playback for animated cover art. Audio no longer goes through it."],
                ),
            ),
        ),
        DesktopLicenseGroup(
            DesktopStrings["d_licenses_application_framework", "Application framework"],
            listOf(
                DesktopLicense("Kotlin and kotlinx", "Apache-2.0", "https://kotlinlang.org"),
                DesktopLicense(
                    "Compose Multiplatform",
                    "Apache-2.0",
                    "https://www.jetbrains.com/lp/compose-multiplatform/",
                    DesktopStrings["d_licenses_compose_note", "The user interface, shared with the Android application."],
                ),
                DesktopLicense("Ktor", "Apache-2.0", "https://ktor.io"),
                DesktopLicense(
                    "Haze",
                    "Apache-2.0",
                    "https://github.com/chrisbanes/haze",
                    DesktopStrings["d_licenses_haze_note", "The backdrop blur behind the floating bars, as on Android."],
                ),
                DesktopLicense("SLF4J", "MIT", "https://www.slf4j.org"),
                DesktopLicense(
                    "Pretendard JP",
                    "OFL-1.1",
                    "https://github.com/orioncactus/pretendard",
                    DesktopStrings["d_licenses_pretendard_note", "The interface font. Copyright (c) 2021, Kil Hyung-jin."],
                ),
            ),
        ),
        DesktopLicenseGroup(
            DesktopStrings["d_licenses_sources_and_integration", "Sources and integration"],
            listOf(
                DesktopLicense(
                    "NewPipeExtractor",
                    "GPL-3.0-or-later",
                    "https://github.com/TeamNewPipe/NewPipeExtractor",
                    DesktopStrings["d_licenses_newpipe_note", "Stream URL deobfuscation, the same component the Android build uses."],
                ),
                DesktopLicense(
                    "GraalVM JavaScript",
                    "UPL-1.0",
                    "https://www.graalvm.org",
                    DesktopStrings["d_licenses_graalvm_note", "Runs source modules in a sandbox with no access to host classes."],
                ),
                DesktopLicense(
                    "dbus-java",
                    "MIT",
                    "https://github.com/hypfvieh/dbus-java",
                    DesktopStrings["d_licenses_dbus_note", "MPRIS media controls and the system tray icon and menu."],
                ),
                DesktopLicense(
                    "junixsocket",
                    "Apache-2.0",
                    "https://github.com/kohlschutter/junixsocket",
                    DesktopStrings["d_licenses_junixsocket_note", "Unix domain sockets for the session bus."],
                ),
            ),
        ),
    )
}
