# Imux Architecture

## Scope

Imux is an independent Minecraft-inspired voxel sandbox game. It uses familiar voxel-sandbox concepts without using Minecraft source code or proprietary assets. The engine, runtime, data formats, renderer, UI, gameplay systems and assets are developed independently.

## Current foundation

```text
Jetpack Compose
      |
Android Platform Layer
      |
C ABI / JNI bridge
      |
C++ Imux Engine
  |       |       |
 Core   Renderer  Game boundary
```

Android-specific APIs stay in the platform layer. The C++ engine exposes a narrow C ABI so the Android implementation can evolve without spreading JNI or Android types through engine code.

## Engine ownership

- Core: timing, diagnostics, logging, lifecycle state and small shared types.
- Platform: Android surface/lifecycle integration and native library loading.
- Renderer: backend-neutral renderer contract. Backend implementations are isolated from game/world code.
- Game: future gameplay entry point; it currently remains intentionally minimal.

The renderer currently has a real lifecycle-safe headless backend. Vulkan is the intended primary GPU backend and OpenGL ES is the compatibility backend; neither is represented as implemented until it has real device/surface/presentation code.

## Lifecycle

The native runtime follows:

`create -> resize -> update/render -> pause/resume -> destroy`

Surface changes are treated independently from engine lifetime. Repeated create/destroy and resize operations are guarded at the ABI boundary. Delta time is clamped after long background intervals so returning from the background cannot produce an invalid simulation step.

## C ABI

The ABI is intentionally small and uses opaque 64-bit engine handles plus primitive lifecycle parameters. Invalid or null handles are ignored. Kotlin owns the handle value and clears it after destruction; C++ owns the Engine object.

The lifecycle ABI remains the primary integration contract. Diagnostics are currently consumed internally by the native runtime and can be exposed to a future debug overlay without moving gameplay into Kotlin.

## Renderer foundation

The renderer owns a backend through `std::unique_ptr`. The backend contract includes initialization, resize, frame begin/end, shutdown, backend identification and renderer statistics. The current headless backend reports zero GPU resources/draw calls rather than fabricating GPU activity.

The next graphics milestone can add a real Vulkan backend behind this boundary. Android `Surface`/`ANativeWindow` ownership should be introduced at the platform/renderer boundary only when the first GPU backend is implemented.

## Diagnostics

Core diagnostics track frame count, frame time, approximate FPS, elapsed runtime, engine state, resolution and renderer counters. The native runtime periodically emits a compact diagnostic line during development. No gameplay state is stored in the diagnostics layer.

## Future voxel architecture

Later phases will add independent systems for block registration, chunk storage, streaming, meshing, lighting, procedural generation and world persistence. They are deliberately not part of the current foundation.

## Future Rust boundary

Rust will only be introduced for subsystems where it provides a concrete benefit, initially candidates include networking and serialization. Such modules will expose a narrow C ABI to C++. Rust is not a second general-purpose engine layer.

## Dependency policy

Dependencies are introduced only when a current milestone requires them. The foundation avoids physics, audio, scripting, compression, ECS and networking dependencies until their corresponding systems are actually implemented.

## Development sequence

1. Foundation and Android/native lifecycle.
2. CI/toolchain stability and renderer diagnostics.
3. Real graphics backend and GPU resource ownership.
4. Input and timing infrastructure.
5. Voxel data model and chunk storage.
6. Meshing, streaming and procedural world generation.
7. Lighting, player and gameplay systems.
8. Persistence, entities and audio.
9. Networking and multiplayer.
10. Scripting/modding and tooling.

Each phase must preserve the buildable state and validate its own integration before the next phase expands the runtime.
