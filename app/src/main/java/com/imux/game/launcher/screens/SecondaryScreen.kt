package com.imux.game.launcher.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.imux.game.launcher.model.LauncherDestination

@Composable
fun SecondaryScreen(
    destination: LauncherDestination,
    onBack: () -> Unit
) {
    val data = when (destination) {
        LauncherDestination.SETTINGS -> ScreenData("Settings", Icons.Default.Settings, "Launcher settings are prepared here. Theme and launch options will be added when their underlying services exist.")
        LauncherDestination.ABOUT -> ScreenData("About Imux", Icons.Default.Info, "Imux is an independent voxel sandbox project. Launcher version: 0.0.3.")
        LauncherDestination.CONTROL_LAYOUTS -> ScreenData("Control Layouts", Icons.Default.Person, "The control-layout entry point exists, but no virtual control editor is implemented yet.")
        LauncherDestination.IMUX_DIRECTORY -> ScreenData("Imux Directory", Icons.Default.Info, "The directory service is not implemented yet. No external game directory is used.")
        LauncherDestination.SHARE_LOGS -> ScreenData("Share Logs", Icons.Default.Share, "Log export is not wired yet. The native engine already maintains development diagnostics internally.")
        LauncherDestination.DIAGNOSTICS -> ScreenData("Diagnostics", Icons.Default.Settings, "Diagnostics are currently available in the native development logger, not as a launcher overlay.")
        LauncherDestination.HOME -> ScreenData("Imux", Icons.Default.Info, "")
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(horizontal = 28.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack, modifier = Modifier.size(48.dp)) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }
            Text(data.title, style = MaterialTheme.typography.headlineSmall)
        }
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.extraLarge,
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(Modifier.padding(24.dp)) {
                Icon(data.icon, contentDescription = null, modifier = Modifier.size(36.dp))
                Spacer(Modifier.height(14.dp))
                Text(data.title, style = MaterialTheme.typography.titleLarge)
                Spacer(Modifier.height(8.dp))
                Text(
                    data.description,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

private data class ScreenData(
    val title: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val description: String
)
