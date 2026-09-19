package com.imux.game.launcher.services

import android.content.Context
import android.net.Uri
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
    private val preferences = context.getSharedPreferences("imux_directory", Context.MODE_PRIVATE)

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

    fun ensureDirectories(): ImuxDirectories {
        val directories = directories()
        listOf(
            directories.root,
            directories.runtime,
            directories.resources,
            directories.logs,
            directories.cache,
            directories.profiles,
            directories.controls,
            directories.screenshots
        ).forEach(File::mkdirs)
        return directories
    }

    fun selectedTreeUri(): Uri? =
        preferences.getString(KEY_TREE_URI, null)?.let(Uri::parse)

    fun setSelectedTreeUri(uri: Uri) {
        preferences.edit().putString(KEY_TREE_URI, uri.toString()).apply()
    }

    fun clearSelectedTreeUri() {
        preferences.edit().remove(KEY_TREE_URI).apply()
    }

    companion object {
        private const val KEY_TREE_URI = "tree_uri"
    }
}
