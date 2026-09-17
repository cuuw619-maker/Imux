# Imux 0.0.1

## Purpose

Establish the first buildable Android/native foundation for Imux without implementing voxel gameplay prematurely.

## Previous State

The repository initially contained only project documentation and no Android, Gradle, CMake or native engine implementation.

## Changes

- Added the Android Gradle project and application module.
- Added Kotlin/Compose Android UI foundation.
- Added a `SurfaceView`-based frame loop driven by `Choreographer`.
- Added a narrow C ABI and JNI bridge for engine lifecycle operations.
- Added the C++20 engine foundation with lifecycle, timing and ownership boundaries.
- Added core logging and shared types.
- Added a backend-neutral renderer interface with a lifecycle-safe headless backend.
- Added GitHub Actions Android build workflow and project documentation.

## Fixed

No prior implementation existed to fix. The foundation established explicit lifecycle handling for create, resize, update, render, pause, resume and destroy.

## Added

- `ARCHITECTURE.md`
- Android application module
- C++ engine and renderer foundation
- GitHub Actions workflow
- Build documentation

## Modified Files

The Stage 1 implementation introduced the files documented by the 0.0.1 parent commits, including Gradle configuration, Android Kotlin sources, C++ engine/core/renderer sources, CMake configuration and CI configuration.

## Dependencies

The foundation uses AndroidX/Compose and the Android native toolchain. No physics, audio, networking, scripting, compression or Rust runtime dependency was introduced.

## Build / CI

The intended CI toolchain was JDK 17, Gradle 8.11.1, Android SDK 35, CMake 3.31.6 and NDK 27.0.12077973.

## Architecture Changes

Established:

Compose -> Android Platform -> C ABI/JNI -> C++ Engine -> Core/Renderer/Game boundary.

## Validation

The repository structure and configuration were created, but the Stage 1 workflow had not successfully completed a build before the Stage 2 investigation. The later CI failure is documented in the 0.0.2 report.

## Known Issues

- GPU rendering was not implemented; the renderer was headless.
- No Gradle wrapper was present.
- No voxel/world/gameplay systems were present.

## Next Stage

Harden CI/toolchain setup and continue renderer foundation work without coupling game code to a graphics API.
