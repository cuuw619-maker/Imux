package com.imux.gamecore.renderer

class RenderPipeline(
    private val renderer: Renderer,
    private val context: RenderContext = RenderContext()
) {
    fun initialize() = renderer.initialize(context)
    fun resize(width: Int, height: Int) = renderer.resize(width, height)
    fun render(frameTimeNanos: Long) = renderer.render(frameTimeNanos)
    fun shutdown() = renderer.shutdown()
}

class FixedStepGameLoop(
    private val tickRate: Int = 60,
    private val onTick: (Long) -> Unit
) {
    private val tickNanos = 1_000_000_000L / tickRate
    private var accumulator = 0L
    private var lastNanos = 0L

    fun reset(nowNanos: Long) { lastNanos = nowNanos; accumulator = 0L }

    fun update(nowNanos: Long) {
        if (lastNanos == 0L) reset(nowNanos)
        val delta = (nowNanos - lastNanos).coerceAtMost(tickNanos * 5)
        lastNanos = nowNanos
        accumulator += delta
        while (accumulator >= tickNanos) {
            onTick(tickNanos)
            accumulator -= tickNanos
        }
    }
}
