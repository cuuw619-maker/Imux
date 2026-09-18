package com.imux.game.launcher.services

data class LaunchConfiguration(
    val runtimeId: String,
    val runtimeVersion: String,
    val runtimeDirectory: String
)

sealed interface LaunchResult {
    data object Started : LaunchResult
    data class Unavailable(val reason: String) : LaunchResult
    data class Failed(val reason: String) : LaunchResult
}

interface GameLaunchBoundary {
    fun launch(configuration: LaunchConfiguration): LaunchResult
}

class UnavailableGameLaunchBoundary : GameLaunchBoundary {
    override fun launch(configuration: LaunchConfiguration): LaunchResult {
        return LaunchResult.Unavailable(
            "Game Launch Boundary ещё не подключён к исполняемому Imux Runtime."
        )
    }
}
