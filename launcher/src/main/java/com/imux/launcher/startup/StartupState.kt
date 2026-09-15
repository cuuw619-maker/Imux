package com.imux.launcher.startup

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class StartupTaskState(
    val id: String,
    val title: String,
    val progress: Float = 0f,
    val state: StartupTaskStatus = StartupTaskStatus.PENDING
)

enum class StartupTaskStatus { PENDING, RUNNING, COMPLETED, FAILED }

class StartupStateStore(tasks: List<StartupTask>) {
    private val _tasks = MutableStateFlow(tasks.map { StartupTaskState(it.id, it.label) })
    val tasks: StateFlow<List<StartupTaskState>> = _tasks.asStateFlow()

    fun update(id: String, progress: Float, state: StartupTaskStatus) {
        _tasks.value = _tasks.value.map {
            if (it.id == id) it.copy(progress = progress.coerceIn(0f, 1f), state = state) else it
        }
    }
}
