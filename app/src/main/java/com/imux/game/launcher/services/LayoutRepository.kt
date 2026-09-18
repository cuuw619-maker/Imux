package com.imux.game.launcher.services

import com.imux.game.launcher.model.ControlLayout

interface LayoutRepository {
    fun list(): List<ControlLayout>
    fun save(layout: ControlLayout)
    fun delete(layoutId: String)
}

interface LayoutSerializer {
    fun encode(layout: ControlLayout): String
    fun decode(value: String): ControlLayout
}
