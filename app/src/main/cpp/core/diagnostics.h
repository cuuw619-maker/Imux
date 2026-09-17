#pragma once

#include <cstdint>

#include "core/types.h"
#include "renderer/renderer.h"

namespace imux::core {

struct Diagnostics final {
    std::uint64_t frameCount = 0;
    float frameTimeMs = 0.0f;
    float framesPerSecond = 0.0f;
    float elapsedSeconds = 0.0f;
    core::Size resolution{};
    LifecycleState engineState = LifecycleState::Created;
    renderer::BackendKind rendererBackend = renderer::BackendKind::Headless;
    std::uint32_t drawCalls = 0;
    std::uint32_t gpuResourceCount = 0;
    std::uint32_t loadedAssetCount = 0;
};

const char* lifecycleStateName(LifecycleState state) noexcept;

} // namespace imux::core
