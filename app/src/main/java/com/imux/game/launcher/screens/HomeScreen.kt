package com.imux.game.launcher.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.imux.game.R
import com.imux.game.launcher.components.LauncherSidebar
import com.imux.game.launcher.components.LauncherTopBar
import com.imux.game.launcher.model.GuestProfile
import com.imux.game.launcher.model.LauncherDestination
import com.imux.game.launcher.services.LaunchState

@Composable
fun HomeScreen(
    profile: GuestProfile,
    destination: LauncherDestination,
    launchState: LaunchState,
    onNavigate: (LauncherDestination) -> Unit,
    onOpenSettings: () -> Unit,
    onPlay: () -> Unit,
    modifier: Modifier = Modifier
) {
    val safePadding = androidx.compose.foundation.layout.WindowInsets.safeDrawing.asPaddingValues()

    Surface(
        modifier = modifier.fillMaxSize().padding(safePadding),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            LauncherTopBar(
                onDirectory = { onNavigate(LauncherDestination.IMUX_DIRECTORY) },
                onAccounts = { onNavigate(LauncherDestination.ACCOUNTS) },
                onResources = { onNavigate(LauncherDestination.RESOURCES) },
                onSettings = onOpenSettings
            )

            BoxWithConstraints(
                modifier = Modifier.fillMaxWidth().weight(1f).padding(top = 2.dp)
            ) {
                val sidebarWidth = if (maxWidth < 700.dp) 168.dp else 184.dp
                val launchCardWidth = when {
                    maxWidth >= 1100.dp -> 332.dp
                    maxWidth >= 850.dp -> 304.dp
                    else -> 260.dp
                }

                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    LauncherSidebar(
                        selected = destination,
                        onNavigate = onNavigate,
                        modifier = Modifier.width(sidebarWidth).fillMaxHeight().padding(top = 2.dp, bottom = 4.dp)
                    )

                    Box(
                        modifier = Modifier.weight(1f).fillMaxHeight().padding(horizontal = 14.dp, vertical = 10.dp)
                    ) {
                        AnimatedContent(
                            targetState = destination,
                            transitionSpec = { fadeIn() togetherWith fadeOut() },
                            label = "launcher-content"
                        ) { target ->
                            LauncherContent(target)
                        }
                    }

                    LaunchProfileCard(
                        profile = profile,
                        launchState = launchState,
                        onAccount = { onNavigate(LauncherDestination.ACCOUNTS) },
                        onLaunchSettings = onOpenSettings,
                        onPlay = onPlay,
                        modifier = Modifier.width(launchCardWidth).fillMaxHeight()
                    )
                }
            }
        }
    }
}

@Composable
private fun LauncherContent(destination: LauncherDestination) {
    val title = when (destination) {
        LauncherDestination.RENDER -> "Рендер"
        LauncherDestination.GAME -> "Игра"
        LauncherDestination.CONTROL -> "Управление"
        LauncherDestination.GAMEPAD -> "Геймпад"
        LauncherDestination.LAUNCHER -> "Лаунчер"
        LauncherDestination.LAYOUTS -> "Раскладки"
        LauncherDestination.ABOUT -> "О проекте"
        LauncherDestination.ACCOUNTS -> "Аккаунты"
        LauncherDestination.IMUX_DIRECTORY -> "Imux Directory"
        LauncherDestination.RESOURCES -> "Resources"
    }

    val description = when (destination) {
        LauncherDestination.RENDER -> "Настройки Imux Renderer API будут подключаться к native renderer через service boundary."
        LauncherDestination.GAME -> "Параметры будущего игрового runtime подключаются через Game Launch Boundary."
        LauncherDestination.CONTROL -> "Touch controls будут описываться собственными ControlLayout и ControlElement моделями."
        LauncherDestination.GAMEPAD -> "Gamepad Manager будет работать с реальными Android input devices без фиктивного определения."
        LauncherDestination.LAUNCHER -> "Раздел запуска Imux: runtime status, launch configuration и будущие параметры запуска."
        LauncherDestination.LAYOUTS -> "Layout Manager будет хранить Default, Touch, Gamepad и Custom схемы независимо от UI."
        LauncherDestination.ABOUT -> "Imux — независимый voxel sandbox проект с собственным runtime и native engine."
        LauncherDestination.ACCOUNTS -> "Сейчас доступно локальное состояние Гость. Authentication provider будет отдельным Imux service."
        LauncherDestination.IMUX_DIRECTORY -> "Каталог Imux: Runtime, Resources, Logs, Cache, Profiles, Controls и будущие данные."
        LauncherDestination.RESOURCES -> "Resource Manager будет управлять собственными ResourcePackage и ресурсами Imux."
    }

    Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Top) {
        Spacer(Modifier.height(10.dp))
        Text(text = title, style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(8.dp))
        Text(
            text = description,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun LaunchProfileCard(
    profile: GuestProfile,
    launchState: LaunchState,
    onAccount: () -> Unit,
    onLaunchSettings: () -> Unit,
    onPlay: () -> Unit,
    modifier: Modifier = Modifier
) {
    val playEnabled = launchState is LaunchState.Ready
    val buttonLabel = when (launchState) {
        LaunchState.Launching -> "Запуск Imux..."
        LaunchState.Running -> "Imux запущен"
        else -> "Играть"
    }

    Card(
        modifier = modifier,
        shape = MaterialTheme.shapes.extraLarge,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Surface(
                modifier = Modifier.size(88.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.surfaceVariant
            ) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = "Профиль",
                        modifier = Modifier.size(42.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(Modifier.height(14.dp))
            Text(profile.displayName, style = MaterialTheme.typography.headlineSmall)
            Spacer(Modifier.height(2.dp))
            Text(
                profile.accountState,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(16.dp))

            OutlinedButton(
                onClick = onAccount,
                modifier = Modifier.fillMaxWidth().height(48.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(8.dp))
                Text("Добавить аккаунт")
            }

            Spacer(Modifier.height(24.dp))
            RuntimeBlock(launchState, onLaunchSettings)
            Spacer(Modifier.height(24.dp))

            Button(
                onClick = onPlay,
                enabled = playEnabled,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = MaterialTheme.shapes.extraLarge,
                colors = ButtonDefaults.buttonColors(
                    disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.18f),
                    disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            ) {
                Text(buttonLabel, style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}

@Composable
private fun RuntimeBlock(
    launchState: LaunchState,
    onLaunchSettings: () -> Unit
) {
    val warningTint by animateColorAsState(
        targetValue = when (launchState) {
            is LaunchState.Ready -> MaterialTheme.colorScheme.primary
            is LaunchState.Failed -> MaterialTheme.colorScheme.error
            else -> MaterialTheme.colorScheme.primary
        },
        label = "runtime-status-tint"
    )

    val statusText = when (launchState) {
        LaunchState.Idle, LaunchState.Checking -> "Проверка runtime..."
        LaunchState.NotInstalled -> "Игровой runtime не установлен"
        is LaunchState.Ready -> "Готов к запуску · " + launchState.runtime.version
        LaunchState.Launching -> "Запуск Imux..."
        LaunchState.Running -> "Imux запущен"
        is LaunchState.Failed -> launchState.message
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(50.dp),
                shape = MaterialTheme.shapes.large,
                color = MaterialTheme.colorScheme.background
            ) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Image(
                        painter = painterResource(R.drawable.icon),
                        contentDescription = "Imux",
                        modifier = Modifier.size(34.dp)
                    )
                }
            }

            Column(
                modifier = Modifier.weight(1f).padding(horizontal = 10.dp)
            ) {
                Text("Imux Runtime", style = MaterialTheme.typography.titleMedium)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.ErrorOutline,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = warningTint
                    )
                    Spacer(Modifier.width(5.dp))
                    Text(
                        text = statusText,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            IconButton(onClick = onLaunchSettings, modifier = Modifier.size(42.dp)) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Настройки запуска"
                )
            }
        }
    }
}
