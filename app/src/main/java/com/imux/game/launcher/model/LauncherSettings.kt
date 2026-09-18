package com.imux.game.launcher.model

data class LauncherSettings(
    val fullscreen: Boolean = true,
    val animations: Boolean = true,
    val reduceMotion: Boolean = false,
    val touchFeedback: Boolean = true,
    val uiScale: UiScale = UiScale.NORMAL,
    val languageTag: String = "ru"
)
