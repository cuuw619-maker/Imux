package com.imux.gamecore.renderer.opengl

import android.content.Context
import android.opengl.GLSurfaceView
import com.imux.gamecore.renderer.RenderContext
import com.imux.gamecore.renderer.Renderer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class OpenGlRenderer : Renderer, GLSurfaceView.Renderer {
    override val backend = OpenGlBackend()
    private var initialized = false

    override fun initialize(context: RenderContext) {
        backend.initialize(context)
        initialized = true
    }

    override fun resize(width: Int, height: Int) = backend.resize(width, height)

    override fun render(frameTimeNanos: Long) {
        if (!initialized) initialize(RenderContext())
        backend.beginFrame()
        // Geometry is intentionally minimal at this stage. The backend/lifecycle
        // boundary is ready for the future mesh and render-pass pipeline.
        backend.endFrame()
    }

    override fun shutdown() {
        if (initialized) backend.shutdown()
        initialized = false
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) = initialize(RenderContext())
    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) = resize(width, height)
    override fun onDrawFrame(gl: GL10?) = render(System.nanoTime())
}

fun createOpenGlSurface(context: Context): GLSurfaceView = GLSurfaceView(context).apply {
    setEGLContextClientVersion(2)
    setRenderer(OpenGlRenderer())
    renderMode = GLSurfaceView.RENDERMODE_CONTINUOUSLY
}
