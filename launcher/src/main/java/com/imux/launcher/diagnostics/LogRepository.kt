package com.imux.launcher.diagnostics

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

@JvmInline
value class LogId(val value: Long)

enum class LogLevel { INFO, WARNING, ERROR }

data class LogEntry(val id: LogId, val level: LogLevel, val tag: String, val message: String, val timeMs: Long)

class LogRepository(private val capacity: Int = 500) {
    private var nextId = 0L
    private val _entries = MutableStateFlow<List<LogEntry>>(emptyList())
    val entries: StateFlow<List<LogEntry>> = _entries.asStateFlow()

    fun log(level: LogLevel, tag: String, message: String) {
        val entry = LogEntry(LogId(nextId++), level, tag, message, System.currentTimeMillis())
        _entries.value = (_entries.value + entry).takeLast(capacity)
    }

    fun clear() { _entries.value = emptyList() }
}
