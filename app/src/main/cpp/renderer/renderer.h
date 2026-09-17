#pragma once

#include <cstdint>
#include <memory>

#include "core/types.h"

namespace imux::renderer {

enum class BackendKind : std::uint8_t {
    Headless,
    Vulkan,
    OpenGLES,
};

struct Statistics final {
    std::uint32_t drawCalls = 0;
    std::uint32_t gpuResourceCount = 0;
    std::uint32_t loadedAssetCount = 0;
};

class RendererBackend {
public:
    virtual ~RendererBackend() = default;

    virtual bool initialize() = 0;
    virtual void resize(core::Size size) = 0;
    virtual void beginFrame() = 0;
    virtual void endFrame() = 0;
    virtual void shutdown() = 0;
    virtual BackendKind kind() const noexcept = 0;
    virtual Statistics statistics() const noexcept = 0;
};

class Renderer {
public:
    Renderer() = default;
    ~Renderer();

    bool initialize();
    void resize(core::Size size);
    void render();
    void shutdown();

    BackendKind backendKind() const noexcept;
    Statistics statistics() const noexcept;

private:
    std::unique_ptr<RendererBackend> backend_;
    core::Size size_{};
};

const char* backendName(BackendKind kind) noexcept;

} // namespace imux::renderer
