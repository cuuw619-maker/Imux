#include "imux_abi.h"

#include "engine.h"

#include <cstdint>

namespace {

imux::Engine* fromHandle(ImuxEngineHandle handle) {
    return reinterpret_cast<imux::Engine*>(static_cast<std::uintptr_t>(handle));
}

ImuxEngineHandle toHandle(imux::Engine* engine) {
    return static_cast<ImuxEngineHandle>(reinterpret_cast<std::uintptr_t>(engine));
}

} // namespace

extern "C" ImuxEngineHandle imux_engine_create(void) {
    auto* engine = new imux::Engine();
    if (!engine->initialize()) {
        delete engine;
        return 0;
    }
    return toHandle(engine);
}

extern "C" void imux_engine_destroy(ImuxEngineHandle handle) {
    delete fromHandle(handle);
}

extern "C" void imux_engine_resize(ImuxEngineHandle handle, int32_t width, int32_t height) {
    if (auto* engine = fromHandle(handle)) {
        engine->resize({width, height});
    }
}

extern "C" void imux_engine_update(ImuxEngineHandle handle, float delta_seconds) {
    if (auto* engine = fromHandle(handle)) {
        engine->update(delta_seconds);
    }
}

extern "C" void imux_engine_render(ImuxEngineHandle handle) {
    if (auto* engine = fromHandle(handle)) {
        engine->render();
    }
}

extern "C" void imux_engine_pause(ImuxEngineHandle handle) {
    if (auto* engine = fromHandle(handle)) {
        engine->pause();
    }
}

extern "C" void imux_engine_resume(ImuxEngineHandle handle) {
    if (auto* engine = fromHandle(handle)) {
        engine->resume();
    }
}
