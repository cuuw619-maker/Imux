package com.imux.gamecore.renderer.opengl

import android.opengl.GLES20
import com.imux.gamecore.renderer.GraphicsBackend
import com.imux.gamecore.renderer.RenderContext
import com.imux.gamecore.renderer.GraphicsBackendType

class OpenGlBackend : GraphicsBackend {
    override val name: String = "OpenGL ES 2.0+"
    override val type: GraphicsBackendType = GraphicsBackendType.OPENGL_ES
    private var width = 0
    private var height = 0

    override fun initialize(context: RenderContext) {
        GLES20.glDisable(GLES20.GL_DITHER)
        GLES20.glEnable(GLES20.GL_CULL_FACE)
        GLES20.glCullFace(GLES20.GL_BACK)
    }

    override fun resize(width: Int, height: Int) {
        this.width = width
        this.height = height
        GLES20.glViewport(0, 0, width, height)
    }

    override fun beginFrame() {
        GLES20.glClearColor(0.035f, 0.04f, 0.05f, 1f)
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)
    }

    override fun endFrame() = Unit

    override fun shutdown() = Unit
}
