package com.imux.game.launcher.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.weight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Gamepad
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.imux.game.R
import com.imux.game.launcher.components.LauncherActionCard
import com.imux.game.launcher.components.LauncherTopBar
import com.imux.game.launcher.model.GuestProfile
import com.imux.game.launcher.model.LauncherDestination

@Composable
fun HomeScreen(
    profile: GuestProfile,
    onNavigate: (LauncherDestination) -> Unit
) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn() + slideInVertically(initialOffsetY = { it / 18 })
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(WindowInsets.safeDrawing.asPaddingValues())
                .padding(horizontal = 20.dp, vertical = 14.dp)
        ) {
            LauncherTopBar(onSettings = { onNavigate(LauncherDestination.SETTINGS) })
            Spacer(Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth().weight(1f),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Column(
                    modifier = Modifier.weight(0.68f).fillMaxHeight(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        "Imux",
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Text(
                        "Launcher tools",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    LauncherActionCard(
                        Icons.Default.Info,
                        "About Imux",
                        "Launcher and project information",
                        { onNavigate(LauncherDestination.ABOUT) },
                        Modifier.weight(1f)
                    )
                    LauncherActionCard(
                        Icons.Default.Gamepad,
                        "Control Layouts",
                        "Virtual controls configuration entry point",
                        { onNavigate(LauncherDestination.CONTROL_LAYOUTS) },
                        Modifier.weight(1f)
                    )
                    LauncherActionCard(
                        Icons.Default.Folder,
                        "Imux Directory",
                        "Access the future Imux data directory",
                        { onNavigate(LauncherDestination.IMUX_DIRECTORY) },
                        Modifier.weight(1f)
                    )
                    LauncherActionCard(
                        Icons.Default.Share,
                        "Share Logs",
                        "Export launcher and engine diagnostics",
                        { onNavigate(LauncherDestination.SHARE_LOGS) },
                        Modifier.weight(1f)
                    )
                }

                Column(
                    modifier = Modifier.weight(0.32f).fillMaxHeight(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.extraLarge,
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Column(Modifier.padding(18.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(30.dp))
                                Spacer(Modifier.width(12.dp))
                                Column {
                                    Text(profile.displayName, style = MaterialTheme.typography.titleLarge)
                                    Text(
                                        profile.accountState,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }

                    Card(
                        modifier = Modifier.fillMaxWidth().weight(1f),
                        shape = MaterialTheme.shapes.extraLarge,
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize().padding(18.dp),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("Game Runtime", style = MaterialTheme.typography.titleMedium)
                                Spacer(Modifier.height(12.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Image(
                                        painter = painterResource(R.drawable.icon),
                                        contentDescription = "Imux",
                                        modifier = Modifier.size(44.dp)
                                    )
                                    Spacer(Modifier.width(12.dp))
                                    Column {
                                        Text("Imux", style = MaterialTheme.typography.titleLarge)
                                        Text(
                                            "Game runtime not installed",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                                Spacer(Modifier.height(14.dp))
                                HorizontalDivider()
                                Spacer(Modifier.height(12.dp))
                                Text(
                                    "The game runtime is not available yet. Play is disabled until a real runtime launch service exists.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Button(
                                    onClick = { },
                                    enabled = false,
                                    modifier = Modifier.fillMaxWidth().height(52.dp)
                                ) {
                                    Text("Play Imux")
                                }
                                Button(
                                    onClick = { onNavigate(LauncherDestination.SETTINGS) },
                                    modifier = Modifier.fillMaxWidth().height(48.dp)
                                ) {
                                    Icon(Icons.Default.Tune, contentDescription = null)
                                    Spacer(Modifier.width(8.dp))
                                    Text("Launch Settings")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
