# Imux

Imux is an independent Minecraft-inspired voxel sandbox game built around a custom 3D runtime and engine.

Imux does not fork or redistribute Minecraft source code or proprietary assets. Gameplay concepts may be familiar, but engine systems, runtime architecture, data formats, renderer, gameplay code, UI, networking and assets are developed independently.

## Architecture

```text
Jetpack Compose / Android UI
          |
   Android Platform Layer
          |
        C ABI
          |
     C++ Imux Engine
          |
 Renderer / Core / Game
          |
 Rust modules where appropriate
```

The first development phase establishes the Android/native boundary, engine lifecycle, renderer abstraction and documentation. Voxel gameplay systems will be introduced incrementally in later phases.

## Build

The repository is built in GitHub Actions with JDK 17, Gradle 8.11.1, Android SDK 35, NDK 27.0.12077973 and CMake 3.31.6. The workflow runs `gradle --no-daemon :app:assembleDebug`.

A local Android SDK installation with the same toolchain can use the same Gradle command.

See [ARCHITECTURE.md](ARCHITECTURE.md) for the engineering plan.
