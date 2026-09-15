package com.imux.gamecore.renderer

interface Renderer {
    val backend: GraphicsBackend
    fun initialize(context: RenderContext)
    fun resize(width: Int, height: Int)
    fun render(frameTimeNanos: Long)
    fun shutdown()
}

interface GraphicsBackend {
    val name: String
    val type: GraphicsBackendType
    fun initialize(context: RenderContext)
    fun resize(width: Int, height: Int)
    fun beginFrame()
    fun endFrame()
    fun shutdown()
}

enum class GraphicsBackendType { AUTO, OPENGL_ES, VULKAN }

data class RenderContext(
    val lowPowerTarget: Boolean = true,
    val thermalAware: Boolean = true,
    val targetFrameRate: Int = 60,
    val maxTextureSize: Int = 2048,
    val maxTextureMemoryMb: Int = 384,
    val maxDrawCalls: Int = 500,
    val shaderComplexityLevel: Int = 0,
    val resolutionScale: Float = 0.85f
)

interface ShaderManager
interface TextureManager
interface BufferManager
interface FrameBuffer
interface RenderPass

interface PerformanceMonitor {
    val fps: Float
    val frameTimeMs: Float
    val drawCalls: Int
    val memoryMb: Long
    val rendererBackend: String
}
