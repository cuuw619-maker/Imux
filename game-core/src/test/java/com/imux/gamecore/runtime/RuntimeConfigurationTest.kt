package com.imux.gamecore.runtime

import kotlin.test.Test
import kotlin.test.assertEquals

class RuntimeConfigurationTest {
    @Test
    fun defaultsAreIndependentFromGamePlatform() {
        val config = RuntimeConfiguration(memoryMb = 1024, gameVersionId = "dev")
        assertEquals("dev", config.gameVersionId)
        assertEquals(GraphicsBackendType.AUTO, config.graphicsBackend)
        assertEquals(1024, config.memoryMb)
    }
}
