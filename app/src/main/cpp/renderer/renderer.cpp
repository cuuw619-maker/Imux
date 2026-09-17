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
    BackendKind kind() const noexcept override { return BackendKind::Headless; }
    Statistics statistics() const noexcept override { return {}; }
};

} // namespace

const char* backendName(BackendKind kind) noexcept {
    switch (kind) {
        case BackendKind::Headless: return "Headless";
        case BackendKind::Vulkan: return "Vulkan";
        case BackendKind::OpenGLES: return "OpenGLES";
    }
    return "Unknown";
}

Renderer::~Renderer() {
    shutdown();
}

bool Renderer::initialize() {
    if (backend_) return true;

    // This stage keeps the real lifecycle-safe headless backend. GPU backends
    // are introduced only when their device/surface/presentation path exists.
    backend_ = std::make_unique<HeadlessBackend>();
    if (!backend_->initialize()) {
        backend_.reset();
        core::log(core::LogLevel::Error, "Renderer backend initialization failed");
        return false;
    }

    core::log(core::LogLevel::Info, "Renderer initialized: Headless");
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

BackendKind Renderer::backendKind() const noexcept {
    return backend_ ? backend_->kind() : BackendKind::Headless;
}

Statistics Renderer::statistics() const noexcept {
    return backend_ ? backend_->statistics() : Statistics{};
}

} // namespace imux::renderer
