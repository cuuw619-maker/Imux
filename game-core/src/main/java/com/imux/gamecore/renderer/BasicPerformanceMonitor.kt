package com.imux.gamecore.renderer

class BasicPerformanceMonitor : PerformanceMonitor {
    override var fps: Float = 0f
        private set
    override var frameTimeMs: Float = 0f
        private set
    override var drawCalls: Int = 0
        private set
    override var memoryMb: Long = 0L
        private set
    override var rendererBackend: String = "unknown"
        private set

    private var frames = 0
    private var windowStart = 0L

    fun frame(frameTimeNanos: Long, drawCalls: Int, memoryMb: Long, backend: String) {
        if (windowStart == 0L) windowStart = frameTimeNanos
        frames++
        this.drawCalls = drawCalls
        this.memoryMb = memoryMb
        this.rendererBackend = backend
        val elapsed = frameTimeNanos - windowStart
        if (elapsed >= 1_000_000_000L) {
            fps = frames * 1_000_000_000f / elapsed
            frameTimeMs = if (fps > 0f) 1000f / fps else 0f
            frames = 0
            windowStart = frameTimeNanos
        }
    }
}
