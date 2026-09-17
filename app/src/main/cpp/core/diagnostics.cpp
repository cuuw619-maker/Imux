#include "core/diagnostics.h"

namespace imux::core {

const char* lifecycleStateName(LifecycleState state) noexcept {
    switch (state) {
        case LifecycleState::Created: return "Created";
        case LifecycleState::Paused: return "Paused";
        case LifecycleState::Running: return "Running";
        case LifecycleState::Destroyed: return "Destroyed";
    }
    return "Unknown";
}

} // namespace imux::core
