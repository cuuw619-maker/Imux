# Imux

Imux is an independent Minecraft-inspired voxel sandbox game built around a custom 3D runtime and engine.

Imux does not fork or redistribute Minecraft source code or proprietary assets. Gameplay concepts may be familiar, but engine systems, runtime architecture, data formats, renderer, gameplay code, UI, networking and assets are developed independently.

## Current development version

**0.0.2** — engine foundation hardening and Android CI stabilization.

See [CHANGELOG.md](CHANGELOG.md) and [docs/development/IMUX_VERSION_0_0_2.md](docs/development/IMUX_VERSION_0_0_2.md) for the factual changes in this stage.

## Architecture

```text
Jetpack Compose / Android UI
          |
   Android Platform Layer
          |
        C ABI
          |
     C++ Imux Engine
       /    |     \
    Core Renderer Game
          |
   future backend implementations
```

The Android layer owns Android lifecycle and surface objects. The native runtime owns engine state and resource lifetime. Game code remains independent of Compose and Android UI.

## Build

GitHub Actions uses JDK 17, Gradle 8.11.1, Android SDK 35, NDK 27.0.12077973 and CMake 3.31.6. The workflow installs only the SDK components required by the current Gradle/CMake configuration and builds with `gradle --no-daemon :app:assembleDebug`.

A local Android SDK installation with the same toolchain can use the same Gradle command. The repository does not currently contain a Gradle wrapper, so CI pins the Gradle distribution through `gradle/actions/setup-gradle`.

## Architecture documentation

See [ARCHITECTURE.md](ARCHITECTURE.md) for the engineering boundaries and development sequence.
