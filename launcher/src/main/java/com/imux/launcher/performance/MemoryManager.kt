package com.imux.launcher.performance

import android.app.ActivityManager
import android.content.Context
import kotlin.math.max
import kotlin.math.min

class MemoryManager(context: Context) {
    private val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager

    fun availableMemoryMb(): Int {
        val info = ActivityManager.MemoryInfo()
        activityManager.getMemoryInfo(info)
        return (info.availMem / (1024 * 1024)).toInt()
    }

    fun safeRangeMb(): IntRange {
        val deviceClass = activityManager.memoryClass
        val available = availableMemoryMb()
        val upper = min(available * 60 / 100, deviceClass * 75 / 100).coerceAtMost(4096)
        val safeMax = max(512, upper).coerceAtMost(max(512, available - 256))
        val safeMin = min(512, safeMax).coerceAtLeast(256)
        return safeMin..safeMax
    }

    fun recommendedMemoryMb(): Int {
        val range = safeRangeMb()
        val target = (availableMemoryMb() * 35 / 100).coerceIn(range.first, range.last)
        return snapTo256(target).coerceIn(range)
    }

    fun sanitize(requestedMb: Int): Int = snapTo256(requestedMb).coerceIn(safeRangeMb())

    fun formatMb(mb: Int): String = when {
        mb % 1024 == 0 -> "${mb / 1024} GB"
        mb >= 1024 -> "${mb / 1024f} GB"
        else -> "$mb MB"
    }

    private fun snapTo256(value: Int) = ((value + 128) / 256) * 256
}
