#pragma once

#include <stdint.h>

#ifdef __cplusplus
extern "C" {
#endif

typedef uint64_t ImuxEngineHandle;

ImuxEngineHandle imux_engine_create(void);
void imux_engine_destroy(ImuxEngineHandle handle);
void imux_engine_resize(ImuxEngineHandle handle, int32_t width, int32_t height);
void imux_engine_update(ImuxEngineHandle handle, float delta_seconds);
void imux_engine_render(ImuxEngineHandle handle);
void imux_engine_pause(ImuxEngineHandle handle);
void imux_engine_resume(ImuxEngineHandle handle);

#ifdef __cplusplus
}
#endif
