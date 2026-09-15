package com.imux.launcher.settings

data class LauncherSettings(
    val language: String = "system",
    val theme: LauncherTheme = LauncherTheme.MATERIAL_YOU,
    val dynamicColors: Boolean = true,
    val orientation: LauncherOrientation = LauncherOrientation.LANDSCAPE,
    val confirmLaunch: Boolean = false,
    val exitBehavior: ExitBehavior = ExitBehavior.STAY_IN_LAUNCHER,
    val cardCornerRadius: Int = 24,
    val compactDensity: Boolean = false,
    val interfaceTransparency: Float = 0f,
    val visualEffects: Boolean = true,
    val animations: AnimationSettings = AnimationSettings(),
    val loading: LoadingSettings = LoadingSettings(),
    val performance: PerformanceSettings = PerformanceSettings(),
    val controlLayoutId: String = "default"
)

enum class LauncherTheme { MATERIAL_YOU, MINIMAL, COMPACT, GAMING, GLASS, AMOLED }
enum class LauncherOrientation { LANDSCAPE, SENSOR, PORTRAIT }
enum class ExitBehavior { STAY_IN_LAUNCHER, CLOSE_LAUNCHER }

data class AnimationSettings(
    val animationEnabled: Boolean = true,
    val animationSpeed: Float = 1f,
    val transitionStyle: TransitionStyle = TransitionStyle.FADE_SCALE,
    val loadingAnimationStyle: LoadingAnimationStyle = LoadingAnimationStyle.CIRCULAR,
    val cardAnimationStyle: CardAnimationStyle = CardAnimationStyle.FADE,
    val buttonAnimationStyle: ButtonAnimationStyle = ButtonAnimationStyle.SCALE
)

enum class TransitionStyle { FADE, SLIDE, SCALE, FADE_SCALE, NONE }
enum class LoadingAnimationStyle { CIRCULAR, LINEAR, PULSING, WAVE, DOTS, SHIMMER }
enum class CardAnimationStyle { FADE, SLIDE, SCALE, NONE }
enum class ButtonAnimationStyle { SCALE, FADE, NONE }

data class LoadingSettings(
    val minDurationMs: Long = 500,
    val maxDurationMs: Long = 3000,
    val showProgress: Boolean = true,
    val showStatus: Boolean = true,
    val customText: String = ""
)

data class PerformanceSettings(
    val memoryMb: Int = 1024,
    val fpsLimit: Int = 60,
    val powerSaving: Boolean = false,
    val interfaceEffectsLevel: Int = 1
)
