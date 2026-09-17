#pragma once

#include <cstdint>

namespace imux::core {

struct Size {
    std::int32_t width = 0;
    std::int32_t height = 0;
};

enum class LifecycleState : std::uint8_t {
    Created,
    Running,
    Paused,
    Destroyed,
};

} // namespace imux::core
