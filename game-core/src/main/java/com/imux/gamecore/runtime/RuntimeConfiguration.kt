package com.imux.gamecore.runtime

import kotlinx.serialization.Serializable

@Serializable
data class RuntimeConfiguration(
    val memoryMb: Int,
    val gameVersionId: String,
    val installationPath: String? = null,
    val launchArguments: List<String> = emptyList(),
    val graphicsBackend: GraphicsBackendType = GraphicsBackendType.AUTO,
    val graphicsQuality: GraphicsQuality = GraphicsQuality.LOW,
    val fpsLimit: Int = 60,
    val renderDistance: Int = 8,
    val controlLayoutId: String = "default",
    val extraArguments: Map<String, String> = emptyMap()
)

@Serializable
enum class GraphicsBackendType { AUTO, OPENGL_ES, VULKAN }

@Serializable
enum class GraphicsQuality { LOW, MEDIUM, HIGH, CUSTOM }

@Serializable
data class GameVersion(
    val id: String,
    val name: String,
    val version: String,
    val installed: Boolean = false
)

@Serializable
data class GameInstallation(
    val versionId: String,
    val path: String,
    val installedSizeMb: Long = 0
)
