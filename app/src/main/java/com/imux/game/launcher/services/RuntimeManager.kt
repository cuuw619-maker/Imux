package com.imux.game.launcher.services

import android.content.Context
import java.io.File

enum class RuntimeAvailability {
    NOT_INSTALLED,
    READY,
    INVALID
}

data class RuntimeStatus(
    val availability: RuntimeAvailability,
    val runtimeId: String = "imux-runtime",
    val version: String? = null,
    val directory: File? = null,
    val detail: String? = null
)

interface RuntimeManager {
    fun inspect(): RuntimeStatus
}

class FileRuntimeManager(
    private val context: Context
) : RuntimeManager {
    override fun inspect(): RuntimeStatus {
        val directory = File(context.filesDir, "imux/runtime")
        if (!directory.exists()) {
            return RuntimeStatus(
                availability = RuntimeAvailability.NOT_INSTALLED,
                directory = directory,
                detail = "Игровой runtime не установлен"
            )
        }

        val versionFile = File(directory, "runtime.version")
        if (!versionFile.isFile) {
            return RuntimeStatus(
                availability = RuntimeAvailability.INVALID,
                directory = directory,
                detail = "Runtime directory найден, но runtime.version отсутствует"
            )
        }

        val version = versionFile.readText().trim()
        if (version.isBlank()) {
            return RuntimeStatus(
                availability = RuntimeAvailability.INVALID,
                directory = directory,
                detail = "Runtime version не определён"
            )
        }

        return RuntimeStatus(
            availability = RuntimeAvailability.READY,
            version = version,
            directory = directory,
            detail = "Готов к проверке запуска"
        )
    }
}
