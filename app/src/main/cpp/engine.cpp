#include "engine.h"

#include "core/logger.h"

#include <algorithm>
#include <cstdio>

namespace imux {

Engine::Engine() = default;

Engine::~Engine() {
    renderer_.shutdown();
    state_ = core::LifecycleState::Destroyed;
}

bool Engine::initialize() {
    if (state_ == core::LifecycleState::Destroyed) return false;
    if (!renderer_.initialize()) return false;

    state_ = core::LifecycleState::Paused;
    core::log(core::LogLevel::Info, "Engine created");
    return true;
}

void Engine::resize(core::Size size) {
    if (state_ == core::LifecycleState::Destroyed) return;

    surfaceSize_.width = std::max(0, size.width);
    surfaceSize_.height = std::max(0, size.height);
    renderer_.resize(surfaceSize_);
}

void Engine::update(float deltaSeconds) {
    if (state_ != core::LifecycleState::Running) return;

    // Clamp long gaps caused by Android backgrounding or debugger pauses.
    const float dt = std::clamp(deltaSeconds, 0.0f, 0.25f);
    lastFrameTimeMs_ = dt * 1000.0f;
    elapsedSeconds_ += dt;
}

void Engine::render() {
    if (state_ != core::LifecycleState::Running) return;

    renderer_.render();
    ++frameCount_;

    if ((frameCount_ % 120u) == 0u) {
        const auto info = diagnostics();
        char message[320]{};
        std::snprintf(
            message,
            sizeof(message),
            "Diagnostics: state=%s backend=%s resolution=%dx%d fps=%.1f frame=%.2fms draws=%u gpuResources=%u assets=%u",
            core::lifecycleStateName(info.engineState),
            renderer::backendName(info.rendererBackend),
            info.resolution.width,
            info.resolution.height,
            info.framesPerSecond,
            info.frameTimeMs,
            info.drawCalls,
            info.gpuResourceCount,
            info.loadedAssetCount);
        core::log(core::LogLevel::Info, message);
    }
}

void Engine::pause() {
    if (state_ == core::LifecycleState::Destroyed) return;
    if (state_ == core::LifecycleState::Running) {
        state_ = core::LifecycleState::Paused;
        core::log(core::LogLevel::Info, "Engine paused");
    }
}

void Engine::resume() {
    if (state_ == core::LifecycleState::Destroyed) return;
    if (state_ != core::LifecycleState::Running) {
        state_ = core::LifecycleState::Running;
        lastFrameTimeMs_ = 0.0f;
        core::log(core::LogLevel::Info, "Engine resumed");
    }
}

core::Diagnostics Engine::diagnostics() const noexcept {
    const auto rendererStats = renderer_.statistics();
    const float fps = lastFrameTimeMs_ > 0.001f ? 1000.0f / lastFrameTimeMs_ : 0.0f;

    return core::Diagnostics{
        frameCount_,
        lastFrameTimeMs_,
        fps,
        elapsedSeconds_,
        surfaceSize_,
        state_,
        renderer_.backendKind(),
        rendererStats.drawCalls,
        rendererStats.gpuResourceCount,
        rendererStats.loadedAssetCount,
    };
}

} // namespace imux
