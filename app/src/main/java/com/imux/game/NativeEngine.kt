package com.imux.game

internal object NativeEngine {
    init {
        System.loadLibrary("imux")
    }

    external fun create(): Long
    external fun destroy(handle: Long)
    external fun resize(handle: Long, width: Int, height: Int)
    external fun update(handle: Long, deltaSeconds: Float)
    external fun render(handle: Long)
    external fun pause(handle: Long)
    external fun resume(handle: Long)
}
