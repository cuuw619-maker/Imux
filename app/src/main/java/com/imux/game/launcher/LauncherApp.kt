package com.imux.game.launcher

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
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
import com.imux.game.launcher.services.ImuxDirectoryService
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
        val directoryService = remember(context) { ImuxDirectoryService(context.applicationContext) }
        val directories = remember(directoryService) { directoryService.ensureDirectories() }
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
        var selectedDirectoryUri by remember { mutableStateOf(directoryService.selectedTreeUri()) }

        val directoryPicker = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.OpenDocumentTree()
        ) { uri ->
            if (uri == null) return@rememberLauncherForActivityResult

            val flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or
                Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            try {
                context.contentResolver.takePersistableUriPermission(uri, flags)
            } catch (_: SecurityException) {
                // Some providers do not expose persistable permissions. Do not crash the launcher.
            }
            directoryService.setSelectedTreeUri(uri)
            selectedDirectoryUri = uri
        }

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
            transitionSpec = {
                (slideInHorizontally { it / 5 } + fadeIn()) togetherWith
                    (slideOutHorizontally { -it / 5 } + fadeOut())
            },
            label = "launcher-route"
        ) { targetRoute ->
            when (targetRoute) {
                LauncherRoute.HOME -> HomeScreen(
                    profile = profile,
                    destination = destination,
                    launchState = launchState,
                    directories = directories,
                    selectedDirectoryUri = selectedDirectoryUri,
                    onPickDirectory = { directoryPicker.launch(selectedDirectoryUri) },
                    onClearDirectory = {
                        directoryService.clearSelectedTreeUri()
                        selectedDirectoryUri = null
                    },
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
