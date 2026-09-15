package com.imux.gamecore.runtime

import org.junit.Assert.assertEquals
import org.junit.Test

class RuntimeConfigurationTest {
    @Test
    fun defaultsAreIndependentFromGamePlatform() {
        val config = RuntimeConfiguration(memoryMb = 1024, gameVersionId = "dev")
        assertEquals("dev", config.gameVersionId)
        assertEquals(GraphicsBackendType.AUTO, config.graphicsBackend)
        assertEquals(1024, config.memoryMb)
    }
}
