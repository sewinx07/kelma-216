package com.example.kelma216.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

/**
 * KELMA 216 theme built from the presentation palette (page 21). Dynamic (monet) colour
 * is intentionally disabled so the brand Tunisian red / midnight blue / turquoise always
 * applies consistently across every screen (requirement 3: "Consistent design across
 * every screen").
 */
private val LightColors = lightColorScheme(
    primary = TunisianRed,
    onPrimary = Cream,
    primaryContainer = TunisianRed,
    onPrimaryContainer = Cream,
    secondary = Turquoise,
    onSecondary = MidnightBlue,
    secondaryContainer = Turquoise,
    onSecondaryContainer = MidnightBlue,
    tertiary = Yellow,
    onTertiary = MidnightBlue,
    tertiaryContainer = Yellow,
    onTertiaryContainer = MidnightBlue,
    background = Cream,
    onBackground = MidnightBlue,
    surface = SoftCream,
    onSurface = MidnightBlue,
    surfaceVariant = Cream,
    onSurfaceVariant = MidnightBlue,
    error = ErrorRed,
    onError = Cream,
    outline = MidnightBlue,
)

private val DarkColors = darkColorScheme(
    primary = TunisianRed,
    onPrimary = Cream,
    secondary = Turquoise,
    onSecondary = MidnightBlue,
    tertiary = Yellow,
    onTertiary = MidnightBlue,
    background = MidnightBlue,
    onBackground = Cream,
    surface = SkyBlue,
    onSurface = Cream,
    surfaceVariant = SkyBlue,
    onSurfaceVariant = Cream,
    error = TunisianRed,
    onError = Cream,
)

@Composable
fun Kelma216Theme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Brand palette is fixed: dynamic colour would override it.
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        darkTheme -> DarkColors
        else -> LightColors
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = KelmaTypography,
        shapes = KelmaShapes,
        content = content,
    )
}
