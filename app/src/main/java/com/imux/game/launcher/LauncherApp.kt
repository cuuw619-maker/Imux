package com.imux.game.launcher

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.imux.game.launcher.model.GuestProfile
import com.imux.game.launcher.model.LauncherDestination
import com.imux.game.launcher.screens.HomeScreen
import com.imux.game.launcher.theme.ImuxLauncherTheme

@Composable
fun LauncherApp() {
    ImuxLauncherTheme {
        var destination by remember { mutableStateOf(LauncherDestination.GAME) }
        val profile = remember { GuestProfile() }

        HomeScreen(
            profile = profile,
            destination = destination,
            onNavigate = { destination = it },
            modifier = Modifier.fillMaxSize()
        )
    }
}
