package com.imux.game.launcher.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val ImuxDarkColors = darkColorScheme(
    primary = Color(0xFFFFB9B5),
    onPrimary = Color(0xFF5E1110),
    primaryContainer = Color(0xFF7B2F2B),
    onPrimaryContainer = Color(0xFFFFDAD7),
    secondary = Color(0xFFE6BBB7),
    onSecondary = Color(0xFF43201E),
    secondaryContainer = Color(0xFF553331),
    onSecondaryContainer = Color(0xFFFFDAD7),
    background = Color(0xFF151110),
    surface = Color(0xFF1E1917),
    surfaceVariant = Color(0xFF2B2321),
    onBackground = Color(0xFFF1E4E1),
    onSurface = Color(0xFFF1E4E1),
    onSurfaceVariant = Color(0xFFD0C2BF),
    outline = Color(0xFFA18D89),
    outlineVariant = Color(0xFF514543),
)

private val ImuxShapes = Shapes(
    small = RoundedCornerShape(10.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(20.dp),
    extraLarge = RoundedCornerShape(28.dp)
)

@Composable
fun ImuxLauncherTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = ImuxDarkColors,
        shapes = ImuxShapes,
        content = content
    )
}
