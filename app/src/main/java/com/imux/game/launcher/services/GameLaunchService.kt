package com.imux.game.launcher.services

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

sealed interface LaunchState {
    data object Idle : LaunchState
    data object Checking : LaunchState
    data object NotInstalled : LaunchState
    data class Ready(val runtime: RuntimeStatus) : LaunchState
    data object Launching : LaunchState
    data object Running : LaunchState
    data class Failed(val message: String) : LaunchState
}

class GameLaunchService(
    private val runtimeManager: RuntimeManager,
    private val boundary: GameLaunchBoundary
) {
    suspend fun checkRuntime(): LaunchState = withContext(Dispatchers.IO) {
        when (val status = runtimeManager.inspect().availability) {
            RuntimeAvailability.NOT_INSTALLED -> LaunchState.NotInstalled
            RuntimeAvailability.INVALID ->
                LaunchState.Failed(
                    runtimeManager.inspect().detail ?: "Runtime integrity check failed"
                )
            RuntimeAvailability.READY ->
                LaunchState.Ready(runtimeManager.inspect())
        }
    }

    suspend fun launch(): LaunchState = withContext(Dispatchers.IO) {
        val status = runtimeManager.inspect()
        if (status.availability != RuntimeAvailability.READY || status.version == null || status.directory == null) {
            return@withContext LaunchState.NotInstalled
        }

        val configuration = LaunchConfiguration(
            runtimeId = status.runtimeId,
            runtimeVersion = status.version,
            runtimeDirectory = status.directory.absolutePath
        )

        when (val result = boundary.launch(configuration)) {
            LaunchResult.Started -> LaunchState.Running
            is LaunchResult.Unavailable -> LaunchState.Failed(result.reason)
            is LaunchResult.Failed -> LaunchState.Failed(result.reason)
        }
    }
}
