package com.imux.gamecore

/**
 * Loads the launcher version catalog without exposing the HTTP client to the UI layer.
 * All network and resource-lifecycle concerns stay inside game-core.
 */
suspend fun loadVersionCatalog(): List<VersionInfo> {
    val latest = try {
        VersionManager.fetchLatest()
    } catch (_: Exception) {
        null
    }

    val archives = try {
        VersionManager.fetchArchives()
    } catch (_: Exception) {
        emptyList()
    }

    return buildList {
        if (latest != null) add(latest)
        addAll(archives)
        if (isEmpty()) add(fallbackVersion())
    }
}

private fun fallbackVersion() = VersionInfo(
    id = "main",
    name = "Imux • main",
    tagName = "main",
    repository = "cuuw619-maker/Imux",
    isCurrent = true
)
