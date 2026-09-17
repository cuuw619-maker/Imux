#include "engine.h"

#include "core/logger.h"

#include <algorithm>

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
    surfaceSize_ = size;
    renderer_.resize(size);
}

void Engine::update(float deltaSeconds) {
    if (state_ != core::LifecycleState::Running) return;

    // Clamp long gaps caused by Android backgrounding or debugger pauses.
    const float dt = std::clamp(deltaSeconds, 0.0f, 0.25f);
    elapsedSeconds_ += dt;
}

void Engine::render() {
    if (state_ != core::LifecycleState::Running) return;
    renderer_.render();
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
        core::log(core::LogLevel::Info, "Engine resumed");
    }
}

} // namespace imux
