package com.imux.launcher.startup

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay

interface StartupTask {
    val id: String
    val label: String
    suspend fun run()
}

class DelayedStartupTask(
    override val id: String,
    override val label: String,
    private val durationMs: Long,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Default
) : StartupTask {
    override suspend fun run() {
        kotlinx.coroutines.withContext(dispatcher) { delay(durationMs) }
    }
}

class StartupCoordinator(
    private val tasks: List<StartupTask>
) {
    suspend fun run(onProgress: suspend (index: Int, total: Int, task: StartupTask) -> Unit) {
        tasks.forEachIndexed { index, task ->
            onProgress(index, tasks.size, task)
            task.run()
        }
    }
}
