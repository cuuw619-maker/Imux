package com.imux.game

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView

class MainActivity : ComponentActivity() {
    private lateinit var engineSurface: EngineSurface

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        engineSurface = EngineSurface(this)
        setContent {
            Surface(modifier = Modifier.fillMaxSize()) {
                AndroidView(
                    factory = { engineSurface },
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }

    override fun onPause() {
        engineSurface.pauseEngine()
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        if (::engineSurface.isInitialized) engineSurface.resumeEngineIfReady()
    }

    override fun onDestroy() {
        if (::engineSurface.isInitialized) engineSurface.shutdown()
        super.onDestroy()
    }
}
