package com.imux.game

import android.content.Context
import android.view.Choreographer
import android.view.SurfaceHolder
import android.view.SurfaceView

class EngineSurface(context: Context) : SurfaceView(context), SurfaceHolder.Callback, Choreographer.FrameCallback {
    private var engineHandle: Long = 0L
    private var surfaceReady = false
    private var running = false
    private var lastFrameNanos = 0L

    init {
        holder.addCallback(this)
        isFocusable = true
        keepScreenOn = true
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        surfaceReady = true
        ensureEngine()
        resizeFromSurface()
        resumeEngineIfReady()
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        if (width > 0 && height > 0) {
            ensureEngine()
            NativeEngine.resize(engineHandle, width, height)
        }
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        stopFrameLoop()
        surfaceReady = false
        pauseEngine()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        ensureEngine()
    }

    override fun onDetachedFromWindow() {
        shutdown()
        super.onDetachedFromWindow()
    }

    override fun doFrame(frameTimeNanos: Long) {
        if (!running || !surfaceReady || engineHandle == 0L) return

        val delta = if (lastFrameNanos == 0L) {
            0f
        } else {
            ((frameTimeNanos - lastFrameNanos).coerceIn(0L, 250_000_000L) / 1_000_000_000f)
        }
        lastFrameNanos = frameTimeNanos

        NativeEngine.update(engineHandle, delta)
        NativeEngine.render(engineHandle)
        Choreographer.getInstance().postFrameCallback(this)
    }

    fun pauseEngine() {
        stopFrameLoop()
        if (engineHandle != 0L) NativeEngine.pause(engineHandle)
    }

    fun resumeEngineIfReady() {
        if (!surfaceReady) return
        ensureEngine()
        NativeEngine.resume(engineHandle)
        startFrameLoop()
    }

    fun shutdown() {
        stopFrameLoop()
        if (engineHandle != 0L) {
            NativeEngine.destroy(engineHandle)
            engineHandle = 0L
        }
        surfaceReady = false
    }

    private fun ensureEngine() {
        if (engineHandle == 0L) engineHandle = NativeEngine.create()
    }

    private fun resizeFromSurface() {
        if (surfaceReady && width > 0 && height > 0 && engineHandle != 0L) {
            NativeEngine.resize(engineHandle, width, height)
        }
    }

    private fun startFrameLoop() {
        if (running) return
        running = true
        lastFrameNanos = 0L
        Choreographer.getInstance().postFrameCallback(this)
    }

    private fun stopFrameLoop() {
        if (!running) return
        running = false
        Choreographer.getInstance().removeFrameCallback(this)
        lastFrameNanos = 0L
    }
}
