#include "renderer/renderer.h"

#include "core/logger.h"

namespace imux::renderer {

namespace {

class HeadlessBackend final : public RendererBackend {
public:
    bool initialize() override { return true; }
    void resize(core::Size) override {}
    void beginFrame() override {}
    void endFrame() override {}
    void shutdown() override {}
};

} // namespace

Renderer::~Renderer() {
    shutdown();
}

bool Renderer::initialize() {
    if (backend_) return true;

    // Phase 1 intentionally uses a headless backend. GPU backends are added
    // behind this interface in the renderer implementation phases.
    backend_ = std::make_unique<HeadlessBackend>();
    if (!backend_->initialize()) {
        backend_.reset();
        core::log(core::LogLevel::Error, "Renderer backend initialization failed");
        return false;
    }

    core::log(core::LogLevel::Info, "Renderer foundation initialized (headless backend)");
    return true;
}

void Renderer::resize(core::Size size) {
    size_ = size;
    if (backend_) backend_->resize(size);
}

void Renderer::render() {
    if (!backend_) return;
    backend_->beginFrame();
    backend_->endFrame();
}

void Renderer::shutdown() {
    if (!backend_) return;
    backend_->shutdown();
    backend_.reset();
}

} // namespace imux::renderer
