package com.imux.game.launcher.services

import android.content.Context
import java.io.File

data class ImuxDirectories(
    val root: File,
    val runtime: File,
    val resources: File,
    val logs: File,
    val cache: File,
    val profiles: File,
    val controls: File,
    val screenshots: File
)

class ImuxDirectoryService(private val context: Context) {
    fun directories(): ImuxDirectories {
        val root = File(context.filesDir, "imux")
        return ImuxDirectories(
            root = root,
            runtime = File(root, "runtime"),
            resources = File(root, "resources"),
            logs = File(root, "logs"),
            cache = File(root, "cache"),
            profiles = File(root, "profiles"),
            controls = File(root, "controls"),
            screenshots = File(root, "screenshots")
        )
    }
}
