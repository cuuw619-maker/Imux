package com.imux.launcher.settings

import com.imux.gamecore.runtime.GraphicsBackendType
import kotlinx.serialization.Serializable

@Serializable
data class GameSettings(
    val selectedVersionId: String? = null,
    val launchMode: LaunchMode = LaunchMode.NORMAL,
    val fpsLimit: Int = 60,
    val renderDistance: Int = 8,
    val simulationDistance: Int = 6,
    val particles: ParticleQuality = ParticleQuality.DECREASED,
    val shadows: Boolean = false,
    val lighting: LightingQuality = LightingQuality.MEDIUM,
    val textureQuality: TextureQuality = TextureQuality.MEDIUM,
    val vsync: Boolean = true,
    val antiAliasing: AntiAliasing = AntiAliasing.NONE
)

@Serializable
enum class LaunchMode { NORMAL, SAFE_MODE, DEBUG }
@Serializable
enum class ParticleQuality { MINIMAL, DECREASED, ALL }
@Serializable
enum class LightingQuality { LOW, MEDIUM, HIGH }
@Serializable
enum class TextureQuality { LOW, MEDIUM, HIGH }
@Serializable
enum class AntiAliasing { NONE, FXAA }

@Serializable
data class GraphicsSettings(
    val renderer: GraphicsBackendType = GraphicsBackendType.AUTO,
    val quality: GraphicsQuality = GraphicsQuality.LOW,
    val experimental: Boolean = false,
    val resolutionScale: Float = 0.85f,
    val maxTextureMemoryMb: Int = 384,
    val shaderComplexity: ShaderComplexity = ShaderComplexity.LOW
)

@Serializable
enum class GraphicsQuality { LOW, MEDIUM, HIGH, CUSTOM }
@Serializable
enum class ShaderComplexity { LOW, MEDIUM, HIGH }
