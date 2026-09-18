package com.imux.game.launcher

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.imux.game.launcher.model.GuestProfile
import com.imux.game.launcher.model.LauncherDestination
import com.imux.game.launcher.model.LauncherRoute
import com.imux.game.launcher.model.LauncherSettings
import com.imux.game.launcher.screens.HomeScreen
import com.imux.game.launcher.screens.SettingsScreen
import com.imux.game.launcher.services.FileRuntimeManager
import com.imux.game.launcher.services.GameLaunchService
import com.imux.game.launcher.services.LaunchState
import com.imux.game.launcher.services.UnavailableGameLaunchBoundary
import com.imux.game.launcher.theme.ImuxLauncherTheme
import kotlinx.coroutines.launch

@Composable
fun LauncherApp(
    onFullscreenChanged: (Boolean) -> Unit
) {
    ImuxLauncherTheme {
        val context = LocalContext.current
        val runtimeManager = remember(context) { FileRuntimeManager(context.applicationContext) }
        val launchService = remember(runtimeManager) {
            GameLaunchService(runtimeManager, UnavailableGameLaunchBoundary())
        }
        val profile = remember { GuestProfile() }
        val scope = rememberCoroutineScope()

        var destination by remember { mutableStateOf(LauncherDestination.GAME) }
        var route by remember { mutableStateOf(LauncherRoute.HOME) }
        var settings by remember { mutableStateOf(LauncherSettings()) }
        var launchState by remember { mutableStateOf<LaunchState>(LaunchState.Idle) }
        var showLaunchError by remember { mutableStateOf(false) }

        LaunchedEffect(launchService) {
            launchState = LaunchState.Checking
            launchState = launchService.checkRuntime()
        }

        LaunchedEffect(settings.fullscreen) {
            onFullscreenChanged(settings.fullscreen)
        }

        if (showLaunchError && launchState is LaunchState.Failed) {
            AlertDialog(
                onDismissRequest = { showLaunchError = false },
                title = { Text("Не удалось запустить Imux") },
                text = { Text((launchState as LaunchState.Failed).message) },
                confirmButton = {
                    TextButton(onClick = { showLaunchError = false }) {
                        Text("Закрыть")
                    }
                }
            )
        }

        AnimatedContent(
            targetState = route,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            label = "launcher-route"
        ) { targetRoute ->
            when (targetRoute) {
                LauncherRoute.HOME -> HomeScreen(
                    profile = profile,
                    destination = destination,
                    launchState = launchState,
                    onNavigate = { destination = it },
                    onOpenSettings = { route = LauncherRoute.SETTINGS },
                    onPlay = {
                        if (launchState is LaunchState.Ready) {
                            scope.launch {
                                launchState = LaunchState.Launching
                                launchState = launchService.launch()
                                if (launchState is LaunchState.Failed) {
                                    showLaunchError = true
                                }
                            }
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                )
                LauncherRoute.SETTINGS -> SettingsScreen(
                    settings = settings,
                    onSettingsChanged = { settings = it },
                    onBack = { route = LauncherRoute.HOME }
                )
            }
        }
    }
}
