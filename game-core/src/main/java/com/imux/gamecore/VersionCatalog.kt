package com.imux.gamecore

/**
 * Loads the launcher version catalog without exposing the HTTP client to the UI layer.
 * All network and resource-lifecycle concerns stay inside game-core.
 */
suspend fun loadVersionCatalog(): List<VersionInfo> {
    return try {
        val latest = runCatching { VersionManager.fetchLatest() }.getOrNull()
        val archives = VersionManager.fetchArchives()

        buildList {
            if (latest != null) add(latest)
            addAll(archives)
            if (isEmpty()) add(fallbackVersion())
        }
    } catch (_: Exception) {
        listOf(fallbackVersion())
    }
}

private fun fallbackVersion() = VersionInfo(
    id = "main",
    name = "Imux • main",
    tagName = "main",
    repository = "cuuw619-maker/Imux",
    isCurrent = true
)
