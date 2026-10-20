package com.music.bitchord.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.LineBreak
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.music.bitchord.R
import com.music.bitchord.data.settings.AppSettings

// Apple Music's signature red. No longer the primary accent, but kept for the
// spots (Replay's rank badge) that want that specific red regardless of theme.
val AccentRed = Color(0xFFFA2D48)

private val DarkColors = darkColorScheme(
    primary = Color.White,
    onPrimary = Color.Black,
    background = Color.Black,
    onBackground = Color.White,
    surface = Color(0xFF0D0D0F),
    onSurface = Color.White,
    surfaceVariant = Color(0xFF1C1C1E),
    onSurfaceVariant = Color(0xFF8E8E93),
    outline = Color(0xFF2C2C2E),
)

private val LightColors = lightColorScheme(
    primary = Color.Black,
    onPrimary = Color.White,
    background = Color.White,
    onBackground = Color.Black,
    surface = Color(0xFFF7F7F9),
    onSurface = Color.Black,
    surfaceVariant = Color(0xFFF2F2F7),
    onSurfaceVariant = Color(0xFF6E6E73),
    outline = Color(0xFFE5E5EA),
)

/**
 * Pretendard JP, set for Korean first with the Japanese kana and kanji that
 * titles often carry. Only the weights the type scale actually asks for are
 * bundled; Compose synthesises nothing, so a missing weight would silently
 * fall back to the nearest one shipped.
 */
val PretendardJP = FontFamily(
    Font(R.font.pretendard_jp_regular, FontWeight.W400),
    Font(R.font.pretendard_jp_medium, FontWeight.W500),
    Font(R.font.pretendard_jp_semibold, FontWeight.W600),
    Font(R.font.pretendard_jp_bold, FontWeight.W700),
    Font(R.font.pretendard_jp_extrabold, FontWeight.W800),
)

// Heavy, tight typography — the backbone of the Apple Music look.
private val BaseTypography = Typography(
    displayLarge = TextStyle(fontWeight = FontWeight.W800, fontSize = 34.sp, letterSpacing = (-0.8).sp),
    headlineLarge = TextStyle(fontWeight = FontWeight.W800, fontSize = 30.sp, letterSpacing = (-0.7).sp),
    headlineMedium = TextStyle(fontWeight = FontWeight.W700, fontSize = 22.sp, letterSpacing = (-0.4).sp),
    titleLarge = TextStyle(fontWeight = FontWeight.W700, fontSize = 20.sp, letterSpacing = (-0.3).sp),
    titleMedium = TextStyle(fontWeight = FontWeight.W600, fontSize = 16.sp, letterSpacing = (-0.2).sp),
    bodyLarge = TextStyle(fontWeight = FontWeight.W400, fontSize = 16.sp),
    bodyMedium = TextStyle(fontWeight = FontWeight.W400, fontSize = 14.sp),
    labelMedium = TextStyle(fontWeight = FontWeight.W600, fontSize = 12.sp),
    labelSmall = TextStyle(fontWeight = FontWeight.W600, fontSize = 11.sp),
)

private val PretendardTypography = BaseTypography.withFamily(PretendardJP)
private val SystemTypography = BaseTypography.withFamily(FontFamily.Default)

/**
 * Applies [family] to every style in the scale, so nothing is left on Roboto,
 * and breaks lines between Korean phrases rather than mid-word ("BitChord에서"
 * stays whole). Phrase breaking only takes effect on Android 13+.
 */
private fun Typography.withFamily(family: FontFamily) = Typography(
    displayLarge = displayLarge.copy(fontFamily = family, lineBreak = PhraseLineBreak),
    displayMedium = displayMedium.copy(fontFamily = family, lineBreak = PhraseLineBreak),
    displaySmall = displaySmall.copy(fontFamily = family, lineBreak = PhraseLineBreak),
    headlineLarge = headlineLarge.copy(fontFamily = family, lineBreak = PhraseLineBreak),
    headlineMedium = headlineMedium.copy(fontFamily = family, lineBreak = PhraseLineBreak),
    headlineSmall = headlineSmall.copy(fontFamily = family, lineBreak = PhraseLineBreak),
    titleLarge = titleLarge.copy(fontFamily = family, lineBreak = PhraseLineBreak),
    titleMedium = titleMedium.copy(fontFamily = family, lineBreak = PhraseLineBreak),
    titleSmall = titleSmall.copy(fontFamily = family, lineBreak = PhraseLineBreak),
    bodyLarge = bodyLarge.copy(fontFamily = family, lineBreak = PhraseLineBreak),
    bodyMedium = bodyMedium.copy(fontFamily = family, lineBreak = PhraseLineBreak),
    bodySmall = bodySmall.copy(fontFamily = family, lineBreak = PhraseLineBreak),
    labelLarge = labelLarge.copy(fontFamily = family, lineBreak = PhraseLineBreak),
    labelMedium = labelMedium.copy(fontFamily = family, lineBreak = PhraseLineBreak),
    labelSmall = labelSmall.copy(fontFamily = family, lineBreak = PhraseLineBreak),
)

private val PhraseLineBreak = LineBreak.Paragraph.copy(wordBreak = LineBreak.WordBreak.Phrase)

@Composable
fun BitChordTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val useSystemFont by AppSettings.useSystemFont.collectAsState()
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = if (useSystemFont) SystemTypography else PretendardTypography,
        content = content,
    )
}

/**
 * Draws the status and navigation bar glyphs dark or light.
 *
 * `enableEdgeToEdge()` decides this from the *system* dark-mode setting, which
 * is the wrong input the moment the in-app theme disagrees with it: Light theme
 * on a phone in dark mode left white icons on a white bar, invisible. The bars
 * have to follow the content the app is actually painting. The player supplies
 * its own stable light-icon value and paints contrast behind it; every other
 * surface follows the theme. Hence a parameter rather than reading it here.
 */
@Composable
fun SystemBarIcons(dark: Boolean) {
    val view = LocalView.current
    if (view.isInEditMode) return
    val window = findWindow(view) ?: return
    SideEffect {
        WindowCompat.getInsetsController(window, view).apply {
            isAppearanceLightStatusBars = dark
            isAppearanceLightNavigationBars = dark
        }
    }
}

