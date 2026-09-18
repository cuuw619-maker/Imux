package com.imux.game.launcher.services

import com.imux.game.launcher.model.ResourcePackage
import com.imux.game.launcher.model.ResourceInstallState

interface ResourceManager {
    fun listPackages(): List<ResourcePackage>
    fun install(packageId: String): ResourceInstallState
    fun remove(packageId: String): ResourceInstallState
}

class UnavailableResourceManager : ResourceManager {
    override fun listPackages(): List<ResourcePackage> = emptyList()

    override fun install(packageId: String): ResourceInstallState =
        ResourceInstallState.FAILED

    override fun remove(packageId: String): ResourceInstallState =
        ResourceInstallState.FAILED
}
