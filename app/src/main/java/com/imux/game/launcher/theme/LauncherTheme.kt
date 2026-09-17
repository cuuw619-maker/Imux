package com.imux.game.launcher.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val ImuxDarkColors = darkColorScheme(
    primary = Color(0xFF9CCBFF),
    onPrimary = Color(0xFF003258),
    primaryContainer = Color(0xFF174A73),
    onPrimaryContainer = Color(0xFFD0E6FF),
    secondary = Color(0xFFB9CADB),
    secondaryContainer = Color(0xFF3B4A59),
    background = Color(0xFF0B1015),
    surface = Color(0xFF111820),
    surfaceVariant = Color(0xFF27313A),
    onBackground = Color(0xFFE1E8EE),
    onSurface = Color(0xFFE1E8EE),
    onSurfaceVariant = Color(0xFFBEC8D1)
)

@Composable
fun ImuxLauncherTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = ImuxDarkColors,
        content = content
    )
}
