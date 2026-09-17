# Imux

Imux is an independent voxel sandbox game built around a custom 3D runtime and engine.

The engine, runtime architecture, data formats, renderer, gameplay code, launcher UI, networking and assets are developed independently.

## Current development version

**0.0.3** — Imux Launcher foundation with a responsive landscape Compose/Material 3 interface.

See [CHANGELOG.md](CHANGELOG.md) and [docs/development/IMUX_VERSION_0_0_3.md](docs/development/IMUX_VERSION_0_0_3.md) for the factual changes in this stage.

## Architecture

```text
Imux Launcher (Compose / Material 3)
             |
      Launcher Services
             |
    Game Launch Boundary
             |
   Android Platform Layer
             |
           C ABI
             |
      C++ Imux Engine
        /    |     \
     Core Renderer Game
```

The Launcher owns Android presentation and launcher state. The native runtime owns engine state and resource lifetime. Launcher composables do not call JNI or C++ gameplay systems directly. The future Game Launch Boundary will connect a real runtime to the launcher before `Play Imux` is enabled.

## Launcher

The current launcher is landscape-first and uses a responsive two-column home layout. It includes a dark Material 3 theme, Guest profile state, Settings, About Imux, Control Layouts, Imux Directory, Share Logs, Diagnostics and an explicit unavailable-runtime state.

`Play Imux` remains disabled until a real game runtime installation and launch service exists.

The project-provided `icon.webp` is used as the Android launcher/Compose branding resource.

## Build

GitHub Actions uses JDK 17, Gradle 8.11.1, Android SDK 35, NDK 27.0.12077973 and CMake 3.31.6. The workflow installs only the SDK components required by the current Gradle/CMake configuration and builds with `gradle --no-daemon :app:assembleDebug`.

A local Android SDK installation with the same toolchain can use the same Gradle command. The repository does not currently contain a Gradle wrapper, so CI pins the Gradle distribution through `gradle/actions/setup-gradle`.

## Architecture documentation

See [ARCHITECTURE.md](ARCHITECTURE.md) for the engineering boundaries and development sequence.
