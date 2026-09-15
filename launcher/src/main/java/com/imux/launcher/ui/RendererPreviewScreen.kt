package com.imux.launcher.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.imux.gamecore.renderer.opengl.createOpenGlSurface

@androidx.compose.runtime.Composable
fun RendererPreviewScreen() {
    val lifecycleOwner = LocalLifecycleOwner.current
    val view = remember { mutableStateOf<android.opengl.GLSurfaceView?>(null) }
    var ready by remember { mutableStateOf(false) }

    DisposableEffect(lifecycleOwner, view.value) {
        val surface = view.value
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> surface?.onResume()
                Lifecycle.Event.ON_PAUSE -> surface?.onPause()
                else -> Unit
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            surface?.onPause()
        }
    }

    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        AndroidView(
            factory = { context -> createOpenGlSurface(context).also { view.value = it; ready = true } },
            modifier = Modifier.fillMaxSize()
        )
        if (!ready) Text("Initializing OpenGL ES renderer…")
    }
}
