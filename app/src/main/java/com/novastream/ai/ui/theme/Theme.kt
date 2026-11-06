package com.novastream.ai.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import com.novastream.ai.data.model.ThemeMode

private val DarkGraphiteColorScheme = darkColorScheme(
    primary = ElectricOrange,
    onPrimary = TextWhitePrimary,
    secondary = NeonViolet,
    onSecondary = TextWhitePrimary,
    tertiary = CyberCyan,
    background = DarkGraphiteBg,
    onBackground = TextWhitePrimary,
    surface = DarkGraphiteSurface,
    onSurface = TextWhitePrimary,
    surfaceVariant = DarkGraphiteCard,
    onSurfaceVariant = TextMutedSecondary,
    outline = DarkGraphiteBorder
)

private val CyberVioletColorScheme = darkColorScheme(
    primary = NeonViolet,
    onPrimary = TextWhitePrimary,
    secondary = ElectricOrange,
    onSecondary = TextWhitePrimary,
    tertiary = CyberCyan,
    background = Color(0xFF090714),
    onBackground = TextWhitePrimary,
    surface = Color(0xFF140F28),
    onSurface = TextWhitePrimary,
    surfaceVariant = Color(0xFF1E173D),
    onSurfaceVariant = Color(0xFFA594FD),
    outline = Color(0xFF382A6E)
)

private val PureBlackColorScheme = darkColorScheme(
    primary = ElectricOrange,
    onPrimary = TextWhitePrimary,
    secondary = NeonViolet,
    onSecondary = TextWhitePrimary,
    tertiary = CyberCyan,
    background = Color(0xFF000000),
    onBackground = TextWhitePrimary,
    surface = Color(0xFF0A0A0A),
    onSurface = TextWhitePrimary,
    surfaceVariant = Color(0xFF141414),
    onSurfaceVariant = TextMutedSecondary,
    outline = Color(0xFF262626)
)

private val LightColorScheme = lightColorScheme(
    primary = ElectricOrange,
    onPrimary = TextWhitePrimary,
    secondary = NeonViolet,
    onSecondary = TextWhitePrimary,
    tertiary = CyberCyan,
    background = LightSurfaceBg,
    onBackground = TextDarkPrimary,
    surface = LightCardSurface,
    onSurface = TextDarkPrimary,
    surfaceVariant = Color(0xFFF1F5F9),
    onSurfaceVariant = TextDarkSecondary,
    outline = LightBorderColor
)

@Composable
fun NovaStreamAITheme(
    themeMode: ThemeMode = ThemeMode.DARK_GRAPHITE,
    content: @Composable () -> Unit
) {
    val colorScheme = when (themeMode) {
        ThemeMode.DARK_GRAPHITE -> DarkGraphiteColorScheme
        ThemeMode.CYBER_VIOLET -> CyberVioletColorScheme
        ThemeMode.PURE_BLACK -> PureBlackColorScheme
        ThemeMode.LIGHT -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
