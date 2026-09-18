package com.imux.game.launcher.model

data class GameSettings(
    val gameplayEnabled: Boolean = true,
    val masterVolume: Float = 1f,
    val interfaceScale: Float = 1f,
    val accessibilityEnabled: Boolean = false,
    val performanceMode: Boolean = false
)
