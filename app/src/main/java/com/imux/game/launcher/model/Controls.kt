package com.imux.game.launcher.model

enum class ControlType {
    JOYSTICK,
    BUTTON,
    LOOK,
    JUMP,
    CROUCH,
    INTERACT,
    INVENTORY,
    ACTION
}

data class ControlPosition(val x: Float, val y: Float)
data class ControlSize(val width: Float, val height: Float)

data class ControlBinding(
    val actionId: String,
    val keyCode: Int? = null,
    val axis: String? = null
)

data class ControlElement(
    val id: String,
    val type: ControlType,
    val position: ControlPosition,
    val size: ControlSize,
    val binding: ControlBinding
)

data class ControlLayout(
    val id: String,
    val name: String,
    val elements: List<ControlElement>
)
