package com.imux.game.launcher.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

object ImuxColors {
    val background = Color(0xFF151110)
    val surface = Color(0xFF1E1917)
    val surfaceVariant = Color(0xFF2B2321)
    val selected = Color(0xFF553331)
    val primary = Color(0xFFFFB9B5)
    val text = Color(0xFFF1E4E1)
    val textMuted = Color(0xFFD0C2BF)
}

object ImuxSpacing {
    val xs = 4.dp
    val sm = 8.dp
    val md = 12.dp
    val lg = 16.dp
    val xl = 24.dp
    val xxl = 32.dp
}

object ImuxDimensions {
    val touchTarget = 48.dp
    val topBar = 58.dp
    val sidebarCompact = 168.dp
    val sidebarNormal = 184.dp
}

object ImuxShapes {
    val small = RoundedCornerShape(10.dp)
    val medium = RoundedCornerShape(16.dp)
    val large = RoundedCornerShape(20.dp)
    val extraLarge = RoundedCornerShape(28.dp)
}

private val ImuxDarkColors = darkColorScheme(
    primary = ImuxColors.primary,
    onPrimary = Color(0xFF5E1110),
    primaryContainer = Color(0xFF7B2F2B),
    onPrimaryContainer = Color(0xFFFFDAD7),
    secondary = Color(0xFFE6BBB7),
    onSecondary = Color(0xFF43201E),
    secondaryContainer = ImuxColors.selected,
    onSecondaryContainer = Color(0xFFFFDAD7),
    background = ImuxColors.background,
    surface = ImuxColors.surface,
    surfaceVariant = ImuxColors.surfaceVariant,
    onBackground = ImuxColors.text,
    onSurface = ImuxColors.text,
    onSurfaceVariant = ImuxColors.textMuted,
    outline = Color(0xFFA18D89),
    outlineVariant = Color(0xFF514543)
)

private val ImuxTypography = Typography()

@Composable
fun ImuxLauncherTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = ImuxDarkColors,
        typography = ImuxTypography,
        shapes = Shapes(
            small = ImuxShapes.small,
            medium = ImuxShapes.medium,
            large = ImuxShapes.large,
            extraLarge = ImuxShapes.extraLarge
        ),
        content = content
    )
}
