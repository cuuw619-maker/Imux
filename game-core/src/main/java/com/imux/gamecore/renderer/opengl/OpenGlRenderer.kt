package com.imux.gamecore.renderer.opengl

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.util.Log
import com.imux.gamecore.renderer.RenderContext
import com.imux.gamecore.renderer.Renderer
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class OpenGlRenderer : Renderer, GLSurfaceView.Renderer {
    override val backend = OpenGlBackend()
    private var initialized = false
    private var program = 0
    private var positionHandle = -1
    private var colorHandle = -1
    private var vertexBuffer: FloatBuffer? = null

    override fun initialize(context: RenderContext) {
        backend.initialize(context)
        if (program == 0) program = createProgram(VERTEX_SHADER, FRAGMENT_SHADER)
        positionHandle = GLES20.glGetAttribLocation(program, "aPosition")
        colorHandle = GLES20.glGetUniformLocation(program, "uColor")
        vertexBuffer = ByteBuffer.allocateDirect(TRIANGLE.size * 4).order(ByteOrder.nativeOrder()).asFloatBuffer().apply {
            put(TRIANGLE).position(0)
        }
        initialized = true
    }

    override fun resize(width: Int, height: Int) = backend.resize(width, height)

    override fun render(frameTimeNanos: Long) {
        if (!initialized) initialize(RenderContext())
        backend.beginFrame()
        val buffer = vertexBuffer ?: return
        GLES20.glUseProgram(program)
        GLES20.glEnableVertexAttribArray(positionHandle)
        buffer.position(0)
        GLES20.glVertexAttribPointer(positionHandle, 3, GLES20.GL_FLOAT, false, 0, buffer)
        GLES20.glUniform4f(colorHandle, 0.25f, 0.75f, 1f, 1f)
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 3)
        GLES20.glDisableVertexAttribArray(positionHandle)
        GLES20.glUseProgram(0)
        backend.endFrame()
    }

    override fun shutdown() {
        if (program != 0) {
            GLES20.glDeleteProgram(program)
            program = 0
        }
        vertexBuffer = null
        backend.shutdown()
        initialized = false
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) = initialize(RenderContext())
    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) = resize(width, height)
    override fun onDrawFrame(gl: GL10?) = render(System.nanoTime())

    private fun createProgram(vertexSource: String, fragmentSource: String): Int {
        val vertex = compileShader(GLES20.GL_VERTEX_SHADER, vertexSource)
        val fragment = compileShader(GLES20.GL_FRAGMENT_SHADER, fragmentSource)
        val linked = GLES20.glCreateProgram()
        GLES20.glAttachShader(linked, vertex)
        GLES20.glAttachShader(linked, fragment)
        GLES20.glLinkProgram(linked)
        val status = IntArray(1)
        GLES20.glGetProgramiv(linked, GLES20.GL_LINK_STATUS, status, 0)
        GLES20.glDeleteShader(vertex)
        GLES20.glDeleteShader(fragment)
        if (status[0] == 0) {
            val message = GLES20.glGetProgramInfoLog(linked)
            GLES20.glDeleteProgram(linked)
            throw IllegalStateException("OpenGL program link failed: $message")
        }
        return linked
    }

    private fun compileShader(type: Int, source: String): Int {
        val shader = GLES20.glCreateShader(type)
        GLES20.glShaderSource(shader, source)
        GLES20.glCompileShader(shader)
        val status = IntArray(1)
        GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, status, 0)
        if (status[0] == 0) {
            val message = GLES20.glGetShaderInfoLog(shader)
            GLES20.glDeleteShader(shader)
            Log.e("ImuxRenderer", "Shader compile failed: $message")
            throw IllegalStateException("OpenGL shader compile failed: $message")
        }
        return shader
    }

    companion object {
        private val TRIANGLE = floatArrayOf(0f, 0.65f, 0f, -0.65f, -0.55f, 0f, 0.65f, -0.55f, 0f)
        private const val VERTEX_SHADER = "attribute vec4 aPosition; void main() { gl_Position = aPosition; }"
        private const val FRAGMENT_SHADER = "precision mediump float; uniform vec4 uColor; void main() { gl_FragColor = uColor; }"
    }
}

fun createOpenGlSurface(context: Context): GLSurfaceView = GLSurfaceView(context).apply {
    setEGLContextClientVersion(2)
    setRenderer(OpenGlRenderer())
    renderMode = GLSurfaceView.RENDERMODE_CONTINUOUSLY
}
