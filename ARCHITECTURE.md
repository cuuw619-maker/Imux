# Imux Architecture

## Scope

Imux is an independent voxel sandbox game. Familiar voxel-sandbox concepts may be used, but the engine, runtime, data formats, renderer, launcher UI, gameplay systems and assets are developed independently.

## Current application architecture

```text
Imux Launcher (Kotlin / Compose / Material 3)
        |
Launcher state + services
        |
Game Launch Boundary
        |
Imux Engine (C ABI / JNI)
        |
C++ Core / Renderer / Game
```

The Launcher is an Android presentation and orchestration layer. It does not contain gameplay code or direct renderer calls. The existing native engine surface loop remains available for the future Game Activity/runtime path.

## Launcher

The Launcher is a dedicated Compose feature under `com.imux.game.launcher`. Version 0.0.5 extends the shell with explicit runtime and launch boundaries, fullscreen window control, settings routing, localization registry, directory/resource models, input/layout models, real Android gamepad discovery, and diagnostics routing.

The UI path remains:

`Compose UI -> Launcher services -> Game Launch Boundary -> Imux Runtime -> C ABI -> Native Engine`

Compose never invokes JNI/C++ directly. Runtime discovery is based on the private Imux application directory and a real `runtime.version` marker; no Minecraft version semantics are used.

## Launcher

The Launcher is a dedicated Compose feature under `com.imux.game.launcher`. It currently provides:

- adaptive landscape home layout with two primary columns;
- centralized dark Material 3 theme;
- guest profile model with no authentication dependency;
- small state-based navigation between Home and secondary launcher screens;
- explicit runtime availability state so Play cannot claim to launch a missing runtime;
- branding through the repository-provided `icon.webp` resource.

Launcher screens and services must remain independent from C++ gameplay and rendering. Future launch behavior should pass through a dedicated Game Launch Boundary rather than invoking native engine APIs from composables.

## Native engine ownership

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
3. Launcher foundation and navigation.
4. Real graphics backend and GPU resource ownership.
5. Input and timing infrastructure.
6. Voxel data model and chunk storage.
7. Meshing, streaming and procedural world generation.
8. Lighting, player and gameplay systems.
9. Persistence, entities and audio.
10. Networking and multiplayer.
11. Scripting/modding and tooling.

Each phase must preserve the buildable state and validate its own integration before the next phase expands the runtime.
