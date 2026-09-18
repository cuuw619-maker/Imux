package com.imux.game.launcher.localization

data class ImuxLocale(
    val tag: String,
    val displayName: String
)

object LocaleRegistry {
    val supported: List<ImuxLocale> = listOf(
        ImuxLocale("en", "English"),
        ImuxLocale("ru", "Русский"),
        ImuxLocale("uk", "Українська"),
        ImuxLocale("de", "Deutsch"),
        ImuxLocale("fr", "Français"),
        ImuxLocale("es", "Español"),
        ImuxLocale("pt", "Português"),
        ImuxLocale("pl", "Polski"),
        ImuxLocale("tr", "Türkçe"),
        ImuxLocale("zh", "中文"),
        ImuxLocale("ja", "日本語"),
        ImuxLocale("ko", "한국어")
    )

    fun resolve(requestedTag: String): ImuxLocale {
        val normalized = requestedTag.lowercase()
        return supported.firstOrNull { it.tag == normalized }
            ?: supported.firstOrNull { normalized.startsWith(it.tag + "-") }
            ?: supported.first { it.tag == "en" }
    }
}
