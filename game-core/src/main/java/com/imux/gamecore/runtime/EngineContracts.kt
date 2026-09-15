package com.imux.gamecore.runtime

import kotlinx.coroutines.flow.Flow

interface Renderer {
    fun render(world: World)
}

interface GraphicsBackend {
    val type: GraphicsBackendType
    fun createRenderer(): Renderer
    fun release()
}

interface GameLoop {
    fun start()
    fun stop()
    fun tick(deltaSeconds: Float)
}

interface InputSystem {
    val events: Flow<InputEvent>
}

sealed interface InputEvent {
    data class Button(val id: String, val pressed: Boolean) : InputEvent
    data class Pointer(val x: Float, val y: Float, val pressed: Boolean) : InputEvent
}

interface AudioSystem {
    fun play(id: String)
    fun stopAll()
}

interface ResourceManager {
    suspend fun load(id: String): Any?
    fun releaseAll()
}

interface AssetManager {
    suspend fun loadAsset(id: String): Any?
}

interface World {
    val entities: List<Entity>
}

interface Entity {
    val id: Long
}

interface GameRuntime {
    suspend fun prepare(configuration: RuntimeConfiguration)
    fun start()
    fun stop()
}
