package com.imux.launcher.settings

import kotlinx.serialization.Serializable

@Serializable
data class ControlLayout(
    val id: String = "default",
    val name: String = "Default",
    val elements: List<ControlElement> = defaultControlElements()
)

@Serializable
data class ControlElement(
    val id: String,
    val type: ControlElementType,
    val x: Float,
    val y: Float,
    val width: Float = 0.12f,
    val height: Float = 0.12f,
    val alpha: Float = 1f,
    val scale: Float = 1f,
    val visible: Boolean = true
)

@Serializable
enum class ControlElementType { BUTTON, JOYSTICK, DPAD, ACTION, TEXT }

private fun defaultControlElements() = listOf(
    ControlElement("move", ControlElementType.JOYSTICK, 0.08f, 0.72f, 0.2f, 0.2f),
    ControlElement("jump", ControlElementType.BUTTON, 0.82f, 0.68f),
    ControlElement("action", ControlElementType.ACTION, 0.72f, 0.80f),
    ControlElement("menu", ControlElementType.BUTTON, 0.90f, 0.06f, 0.08f, 0.08f)
)
