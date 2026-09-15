package com.imux.launcher.runtime

import android.content.Context
import com.imux.gamecore.runtime.RuntimeConfiguration
import com.imux.launcher.performance.MemoryManager
import com.imux.launcher.settings.LauncherSettings

object RuntimeConfigurationFactory {
    fun create(context: Context, settings: LauncherSettings, gameVersionId: String): RuntimeConfiguration {
        val memory = MemoryManager(context).sanitize(settings.performance.memoryMb)
        return RuntimeConfiguration(
            memoryMb = memory,
            gameVersionId = gameVersionId,
            graphicsBackend = settings.graphics.renderer,
            graphicsQuality = com.imux.gamecore.runtime.GraphicsQuality.valueOf(settings.graphics.quality.name),
            fpsLimit = settings.game.fpsLimit,
            renderDistance = settings.game.renderDistance,
            simulationDistance = settings.game.simulationDistance,
            textureQuality = settings.game.textureQuality.name,
            shadows = settings.game.shadows,
            vsync = settings.game.vsync,
            antiAliasing = settings.game.antiAliasing.name,
            controlLayoutId = settings.controlLayoutId
        )
    }
}
