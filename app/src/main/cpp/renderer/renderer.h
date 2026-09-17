#pragma once

#include <memory>

#include "core/types.h"

namespace imux::renderer {

enum class BackendKind {
    Vulkan,
    OpenGLES,
};

class RendererBackend {
public:
    virtual ~RendererBackend() = default;

    virtual bool initialize() = 0;
    virtual void resize(core::Size size) = 0;
    virtual void beginFrame() = 0;
    virtual void endFrame() = 0;
    virtual void shutdown() = 0;
};

class Renderer {
public:
    Renderer() = default;
    ~Renderer();

    bool initialize();
    void resize(core::Size size);
    void render();
    void shutdown();

private:
    std::unique_ptr<RendererBackend> backend_;
    core::Size size_{};
};

} // namespace imux::renderer
