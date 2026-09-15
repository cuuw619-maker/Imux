package com.imux.launcher.diagnostics

object LogStore {
    val repository = LogRepository(capacity = 500)
}
