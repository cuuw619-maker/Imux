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
    val visible: Boolean = true,
    val action: String? = null
)

@Serializable
enum class ControlElementType {
    JOYSTICK, BUTTON, ACTION_BUTTON, JUMP, SNEAK, ATTACK, INTERACT, INVENTORY, MENU, CUSTOM
}

private fun defaultControlElements() = listOf(
    ControlElement("move", ControlElementType.JOYSTICK, 0.08f, 0.72f, 0.2f, 0.2f),
    ControlElement("jump", ControlElementType.JUMP, 0.82f, 0.68f),
    ControlElement("attack", ControlElementType.ATTACK, 0.72f, 0.80f),
    ControlElement("interact", ControlElementType.INTERACT, 0.60f, 0.80f),
    ControlElement("menu", ControlElementType.MENU, 0.90f, 0.06f, 0.08f, 0.08f)
)
