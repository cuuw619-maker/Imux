package com.imux.game.launcher

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.fillMaxSize
import com.imux.game.launcher.model.GuestProfile
import com.imux.game.launcher.model.LauncherDestination
import com.imux.game.launcher.screens.HomeScreen
import com.imux.game.launcher.screens.SecondaryScreen
import com.imux.game.launcher.theme.ImuxLauncherTheme

@Composable
fun LauncherApp() {
    ImuxLauncherTheme {
        var destination by remember { mutableStateOf(LauncherDestination.HOME) }
        val profile = remember { GuestProfile() }

        AnimatedContent(
            targetState = destination,
            modifier = Modifier.fillMaxSize(),
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            label = "launcher-navigation"
        ) { target ->
            if (target == LauncherDestination.HOME) {
                HomeScreen(profile = profile, onNavigate = { destination = it })
            } else {
                SecondaryScreen(
                    destination = target,
                    onBack = { destination = LauncherDestination.HOME }
                )
            }
        }
    }
}
