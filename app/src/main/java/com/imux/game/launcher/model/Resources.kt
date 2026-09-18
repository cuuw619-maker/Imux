package com.imux.game.launcher.model

enum class ResourceInstallState {
    NOT_INSTALLED,
    DOWNLOADING,
    VERIFYING,
    INSTALLED,
    FAILED
}

data class ResourceVersion(
    val id: String,
    val label: String
)

data class ResourcePackage(
    val id: String,
    val name: String,
    val version: ResourceVersion,
    val category: String,
    val installState: ResourceInstallState = ResourceInstallState.NOT_INSTALLED
)
