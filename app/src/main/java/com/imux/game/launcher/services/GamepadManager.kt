package com.imux.game.launcher.services

import android.view.InputDevice

data class GamepadInfo(
    val deviceId: Int,
    val name: String,
    val hasBattery: Boolean,
    val batteryPercent: Int?
)

interface GamepadManager {
    fun connectedGamepads(): List<GamepadInfo>
}

class AndroidGamepadManager : GamepadManager {
    override fun connectedGamepads(): List<GamepadInfo> {
        val ids: IntArray = InputDevice.getDeviceIds()
        return ids.toList()
            .mapNotNull { id: Int -> InputDevice.getDevice(id) }
            .filter { device: InputDevice ->
                val sources = device.sources
                (sources and InputDevice.SOURCE_GAMEPAD) == InputDevice.SOURCE_GAMEPAD ||
                    (sources and InputDevice.SOURCE_JOYSTICK) == InputDevice.SOURCE_JOYSTICK
            }
            .map { device: InputDevice ->
                GamepadInfo(
                    deviceId = device.id,
                    name = device.name,
                    hasBattery = false,
                    batteryPercent = null
                )
            }
    }
}
