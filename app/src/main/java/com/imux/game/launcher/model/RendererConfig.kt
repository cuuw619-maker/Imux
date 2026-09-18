package com.imux.game.launcher.model

data class RendererConfig(
    val backend: String = "auto",
    val graphicsQuality: Int = 2,
    val resolutionScale: Float = 1f,
    val fpsLimit: Int = 60,
    val vSync: Boolean = true,
    val framePacing: Boolean = true,
    val antiAliasing: Boolean = true,
    val textureFiltering: String = "linear",
    val lighting: Boolean = true,
    val particles: Boolean = true,
    val viewDistance: Int = 8,
    val performanceMode: Boolean = false
)
