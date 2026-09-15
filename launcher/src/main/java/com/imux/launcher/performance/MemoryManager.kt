package com.imux.launcher.performance

import android.app.ActivityManager
import android.content.Context
import kotlin.math.max
import kotlin.math.min

class MemoryManager(context: Context) {
    private val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager

    companion object {
        const val MINIMUM_MB = 256
        const val MAXIMUM_MB = 1536
        val PRESETS_MB = intArrayOf(256, 320, 384, 448, 512, 640, 768, 896, 1024, 1152, 1280, 1408, 1536)
    }

    fun availableMemoryMb(): Int {
        val info = ActivityManager.MemoryInfo()
        activityManager.getMemoryInfo(info)
        return (info.availMem / (1024L * 1024L)).toInt()
    }

    /** Conservative runtime budget for mobile devices. */
    fun safeRangeMb(): IntRange {
        val deviceClassMb = activityManager.memoryClass
        val availableMb = availableMemoryMb()
        val safeMax = min(
            MAXIMUM_MB,
            min(deviceClassMb * 75 / 100, max(MINIMUM_MB, availableMb - 256))
        ).coerceAtLeast(MINIMUM_MB)
        return MINIMUM_MB..safeMax
    }

    fun recommendedMemoryMb(): Int {
        val range = safeRangeMb()
        val target = (availableMemoryMb() * 35 / 100).coerceIn(range.first, range.last)
        return nearestPreset(target).coerceIn(range)
    }

    fun sanitize(requestedMb: Int): Int = nearestPreset(requestedMb).coerceIn(safeRangeMb())

    fun isSafe(requestedMb: Int): Boolean = requestedMb in safeRangeMb()

    fun formatMb(mb: Int): String = when {
        mb >= 1024 && mb % 1024 == 0 -> "${mb / 1024} GB"
        mb >= 1024 -> "%.1f GB".format(mb / 1024f)
        else -> "$mb MB"
    }

    private fun nearestPreset(value: Int): Int = PRESETS_MB.minByOrNull { kotlin.math.abs(it - value) } ?: MINIMUM_MB
}
