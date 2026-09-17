# Imux Architecture

## Scope

Imux is an independent Minecraft-inspired voxel sandbox game. It uses familiar voxel-sandbox concepts without using Minecraft source code or proprietary assets. The engine, runtime, data formats, renderer, UI, gameplay systems and assets are developed independently.

## Phase 1 foundation

The first phase establishes a small, buildable Android/native runtime rather than implementing gameplay prematurely.

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

- Core: time, logging, lifecycle state and small shared types.
- Platform: Android surface/lifecycle integration and native library loading.
- Renderer: backend-neutral renderer contract. Backend implementations are isolated from game/world code.
- Game: future gameplay entry point; it currently remains intentionally minimal.

The first renderer milestone is an abstraction and lifecycle-safe backend boundary. Vulkan is the intended primary backend and OpenGL ES is the compatibility backend; neither is forced into the game layer. A backend can be implemented incrementally behind the same interface.

## Lifecycle

The native runtime follows:

`create -> resize -> update/render -> pause/resume -> destroy`

Surface changes are treated independently from engine lifetime. Repeated create/destroy and resize operations must be safe. Delta time is clamped after long background intervals so returning from the background cannot produce an invalid simulation step.

## C ABI

The ABI is intentionally small. Kotlin/Java code should call the platform bridge rather than know C++ classes. The C++ implementation owns engine objects and translates primitive values at the boundary.

## Future voxel architecture

Later phases will add independent systems for block registration, chunk storage, streaming, meshing, lighting, procedural generation and world persistence. They are deliberately not part of the foundation commit.

## Future Rust boundary

Rust will only be introduced for subsystems where it provides a concrete benefit, initially candidates include networking and serialization. Such modules will expose a narrow C ABI to C++. Rust is not a second general-purpose engine layer.

## Dependency policy

Dependencies are introduced only when a current milestone requires them. The foundation avoids physics, audio, scripting, compression, ECS and networking dependencies until their corresponding systems are actually implemented.

## Development sequence

1. Foundation and Android/native lifecycle.
2. Renderer backend implementation and GPU resource ownership.
3. Input and timing infrastructure.
4. Voxel data model and chunk storage.
5. Meshing, streaming and procedural world generation.
6. Lighting, player and gameplay systems.
7. Persistence, entities and audio.
8. Networking and multiplayer.
9. Scripting/modding and tooling.

Each phase must preserve the buildable state and validate its own integration before the next phase expands the runtime.
