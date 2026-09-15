package com.imux.launcher.settings

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json

private val Context.launcherDataStore by preferencesDataStore(name = "launcher_settings")

private val settingsJson = Json { ignoreUnknownKeys = true; encodeDefaults = true }

class LauncherSettingsRepository(private val context: Context) {
    private object Keys {
        val language = stringPreferencesKey("language")
        val theme = stringPreferencesKey("theme")
        val dynamicColors = booleanPreferencesKey("dynamic_colors")
        val orientation = stringPreferencesKey("orientation")
        val confirmLaunch = booleanPreferencesKey("confirm_launch")
        val exitBehavior = stringPreferencesKey("exit_behavior")
        val cardRadius = intPreferencesKey("card_radius")
        val compact = booleanPreferencesKey("compact")
        val transparency = floatPreferencesKey("transparency")
        val effects = booleanPreferencesKey("effects")
        val animationProfile = stringPreferencesKey("animation_profile")
        val animationEnabled = booleanPreferencesKey("animation_enabled")
        val animationSpeed = floatPreferencesKey("animation_speed")
        val transition = stringPreferencesKey("transition")
        val loadingAnimation = stringPreferencesKey("loading_animation")
        val cardAnimation = stringPreferencesKey("card_animation")
        val buttonAnimation = stringPreferencesKey("button_animation")
        val minLoading = longPreferencesKey("loading_min")
        val maxLoading = longPreferencesKey("loading_max")
        val showProgress = booleanPreferencesKey("show_progress")
        val showStatus = booleanPreferencesKey("show_status")
        val loadingText = stringPreferencesKey("loading_text")
        val memoryMb = intPreferencesKey("memory_mb")
        val fps = intPreferencesKey("fps")
        val powerSaving = booleanPreferencesKey("power_saving")
        val effectsLevel = intPreferencesKey("effects_level")
        val controlLayout = stringPreferencesKey("control_layout")
        val gameSettings = stringPreferencesKey("game_settings")
        val graphicsSettings = stringPreferencesKey("graphics_settings")
        val controlLayoutData = stringPreferencesKey("control_layout_data")
    }

    val settings: Flow<LauncherSettings> = context.launcherDataStore.data.map(::decode)

    suspend fun update(transform: (LauncherSettings) -> LauncherSettings) {
        context.launcherDataStore.edit { preferences -> write(preferences, transform(decode(preferences))) }
    }

    private fun decode(p: Preferences): LauncherSettings = LauncherSettings(
        language = p[Keys.language] ?: "system",
        theme = enumValue(p[Keys.theme], LauncherTheme.MATERIAL_YOU),
        dynamicColors = p[Keys.dynamicColors] ?: true,
        orientation = enumValue(p[Keys.orientation], LauncherOrientation.LANDSCAPE),
        confirmLaunch = p[Keys.confirmLaunch] ?: false,
        exitBehavior = enumValue(p[Keys.exitBehavior], ExitBehavior.STAY_IN_LAUNCHER),
        cardCornerRadius = p[Keys.cardRadius] ?: 24,
        compactDensity = p[Keys.compact] ?: false,
        interfaceTransparency = p[Keys.transparency] ?: 0f,
        visualEffects = p[Keys.effects] ?: true,
        animations = AnimationSettings(
            profile = enumValue(p[Keys.animationProfile], AnimationProfile.FULL),
            animationEnabled = p[Keys.animationEnabled] ?: true,
            animationSpeed = p[Keys.animationSpeed] ?: 1f,
            transitionStyle = enumValue(p[Keys.transition], TransitionStyle.FADE_SCALE),
            loadingAnimationStyle = enumValue(p[Keys.loadingAnimation], LoadingAnimationStyle.LINEAR),
            cardAnimationStyle = enumValue(p[Keys.cardAnimation], CardAnimationStyle.FADE),
            buttonAnimationStyle = enumValue(p[Keys.buttonAnimation], ButtonAnimationStyle.SCALE)
        ),
        loading = LoadingSettings(
            minDurationMs = p[Keys.minLoading] ?: 500,
            maxDurationMs = p[Keys.maxLoading] ?: 3000,
            showProgress = p[Keys.showProgress] ?: true,
            showStatus = p[Keys.showStatus] ?: true,
            customText = p[Keys.loadingText] ?: ""
        ),
        performance = PerformanceSettings(
            memoryMb = p[Keys.memoryMb] ?: 1024,
            fpsLimit = p[Keys.fps] ?: 60,
            powerSaving = p[Keys.powerSaving] ?: false,
            interfaceEffectsLevel = p[Keys.effectsLevel] ?: 1
        ),
        controlLayoutId = p[Keys.controlLayout] ?: "default",
        game = decodeJson(p[Keys.gameSettings], GameSettings()),
        graphics = decodeJson(p[Keys.graphicsSettings], GraphicsSettings()),
        controlLayout = decodeJson(p[Keys.controlLayoutData], ControlLayout())
    )

    private fun write(p: MutablePreferences, s: LauncherSettings) {
        p[Keys.language] = s.language
        p[Keys.theme] = s.theme.name
        p[Keys.dynamicColors] = s.dynamicColors
        p[Keys.orientation] = s.orientation.name
        p[Keys.confirmLaunch] = s.confirmLaunch
        p[Keys.exitBehavior] = s.exitBehavior.name
        p[Keys.cardRadius] = s.cardCornerRadius
        p[Keys.compact] = s.compactDensity
        p[Keys.transparency] = s.interfaceTransparency
        p[Keys.effects] = s.visualEffects
        p[Keys.animationProfile] = s.animations.profile.name
        p[Keys.animationEnabled] = s.animations.animationEnabled
        p[Keys.animationSpeed] = s.animations.animationSpeed
        p[Keys.transition] = s.animations.transitionStyle.name
        p[Keys.loadingAnimation] = s.animations.loadingAnimationStyle.name
        p[Keys.cardAnimation] = s.animations.cardAnimationStyle.name
        p[Keys.buttonAnimation] = s.animations.buttonAnimationStyle.name
        p[Keys.minLoading] = s.loading.minDurationMs
        p[Keys.maxLoading] = s.loading.maxDurationMs
        p[Keys.showProgress] = s.loading.showProgress
        p[Keys.showStatus] = s.loading.showStatus
        p[Keys.loadingText] = s.loading.customText
        p[Keys.memoryMb] = s.performance.memoryMb
        p[Keys.fps] = s.performance.fpsLimit
        p[Keys.powerSaving] = s.performance.powerSaving
        p[Keys.effectsLevel] = s.performance.interfaceEffectsLevel
        p[Keys.controlLayout] = s.controlLayoutId
        p[Keys.gameSettings] = settingsJson.encodeToString(GameSettings.serializer(), s.game)
        p[Keys.graphicsSettings] = settingsJson.encodeToString(GraphicsSettings.serializer(), s.graphics)
        p[Keys.controlLayoutData] = settingsJson.encodeToString(ControlLayout.serializer(), s.controlLayout)
    }

    private inline fun <reified T> decodeJson(value: String?, fallback: T): T =
        value?.let { runCatching { settingsJson.decodeFromString<T>(it) }.getOrNull() } ?: fallback

    private inline fun <reified T : Enum<T>> enumValue(value: String?, default: T): T =
        value?.let { runCatching { enumValueOf<T>(it) }.getOrNull() } ?: default
}
