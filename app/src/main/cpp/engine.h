#pragma once

#include "core/diagnostics.h"
#include "core/types.h"
#include "renderer/renderer.h"

namespace imux {

class Engine final {
public:
    Engine();
    ~Engine();

    bool initialize();
    void resize(core::Size size);
    void update(float deltaSeconds);
    void render();
    void pause();
    void resume();

    core::Diagnostics diagnostics() const noexcept;

private:
    renderer::Renderer renderer_;
    core::Size surfaceSize_{};
    core::LifecycleState state_ = core::LifecycleState::Created;
    float elapsedSeconds_ = 0.0f;
    float lastFrameTimeMs_ = 0.0f;
    std::uint64_t frameCount_ = 0;
};

} // namespace imux
