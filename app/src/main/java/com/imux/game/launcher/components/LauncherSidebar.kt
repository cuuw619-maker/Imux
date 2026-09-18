package com.imux.game.launcher.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Gamepad
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.VideoSettings
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.imux.game.launcher.model.LauncherDestination

private data class NavigationEntry(
    val destination: LauncherDestination,
    val label: String,
    val icon: ImageVector,
    val dividerBefore: Boolean = false
)

private val navigationEntries = listOf(
    NavigationEntry(LauncherDestination.RENDER, "Рендер", Icons.Default.VideoSettings),
    NavigationEntry(LauncherDestination.GAME, "Игра", Icons.Default.RocketLaunch),
    NavigationEntry(LauncherDestination.CONTROL, "Управление", Icons.Default.Tune),
    NavigationEntry(LauncherDestination.GAMEPAD, "Геймпад", Icons.Default.Gamepad),
    NavigationEntry(LauncherDestination.LAUNCHER, "Лаунчер", Icons.Default.Settings),
    NavigationEntry(
        LauncherDestination.LAYOUTS,
        "Раскладки",
        Icons.Default.SportsEsports,
        dividerBefore = true
    ),
    NavigationEntry(
        LauncherDestination.ABOUT,
        "О проекте",
        Icons.Default.Info,
        dividerBefore = true
    )
)

@Composable
fun LauncherSidebar(
    selected: LauncherDestination,
    onNavigate: (LauncherDestination) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Spacer(Modifier.height(4.dp))

        navigationEntries.forEach { entry ->
            if (entry.dividerBefore) {
                HorizontalDivider(
                    modifier = Modifier
                        .padding(horizontal = 38.dp, vertical = 8.dp),
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.55f)
                )
            }

            NavigationItem(
                entry = entry,
                selected = entry.destination == selected,
                onClick = { onNavigate(entry.destination) }
            )
        }
    }
}

@Composable
private fun NavigationItem(
    entry: NavigationEntry,
    selected: Boolean,
    onClick: () -> Unit
) {
    val containerColor by animateColorAsState(
        targetValue = if (selected) {
            MaterialTheme.colorScheme.secondaryContainer
        } else {
            Color.Transparent
        },
        label = "navigation-selection"
    )
    val contentColor = if (selected) {
        MaterialTheme.colorScheme.onSecondaryContainer
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(54.dp)
            .clickable(onClick = onClick),
        shape = MaterialTheme.shapes.large,
        color = containerColor,
        contentColor = contentColor
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = entry.icon,
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
            Text(
                text = entry.label,
                modifier = Modifier.padding(start = 13.dp),
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}
