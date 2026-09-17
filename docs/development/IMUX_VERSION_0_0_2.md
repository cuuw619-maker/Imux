# Imux 0.0.2

## Purpose

Harden the Android/native foundation, repair the failing GitHub Actions toolchain setup, and add a small diagnostics layer without starting gameplay or voxel systems prematurely.

## Previous State

The repository already contained the Stage 1 Android/native foundation: Compose UI, an Android `SurfaceView` frame loop, a narrow C ABI, a C++ engine lifecycle, a renderer abstraction and a real headless backend. The project was configured for Android 35, NDK 27.0.12077973, CMake 3.31.6, JDK 17 and Gradle 8.11.1.

The referenced CI job failed before compilation. The failure occurred in `android-actions/setup-android@v3`, which attempted `sdkmanager tools` and received `Warning: Failed to find package 'tools'`.

## Changes

- Reworked Android CI SDK installation to use the current `android-actions/setup-android@v4` package input.
- Kept only the components required by the current project: platform-tools, Android 35, Build Tools 35.0.0, CMake 3.31.6 and NDK 27.0.12077973.
- Updated CI actions to `actions/checkout@v7`, `actions/setup-java@v6`, `android-actions/setup-android@v4` and `gradle/actions/setup-gradle@v6`.
- Preserved Gradle 8.11.1 and the existing Android/NDK/CMake versions.
- Added native engine diagnostics and renderer statistics.
- Made the renderer report `Headless` explicitly while Vulkan/OpenGL ES remain unimplemented.
- Hardened resize input by clamping negative dimensions at the native engine boundary.
- Added the CMake source directory as a private include path so the pre-existing `core/...` and `renderer/...` header paths resolve in the Android native build.
- Changed the application development version to 0.0.2.
- Added development documentation for 0.0.2 and a retrospective 0.0.1 foundation report because the previous stage had not created one.

## Fixed

The exact failure in workflow run `35263298899`, job `105344110559`, was traced to the `packages: tools platform-tools` default behavior of `android-actions/setup-android@v3`. The action successfully installed modern command-line tools, but then invoked `sdkmanager tools`; the current SDK repository no longer exposed that legacy package. The repository workflow no longer asks for `tools`.

The workflow also no longer uses the deprecated `actions/setup-java@v4`.

## Added

Native diagnostics contain:

- frame count;
- last frame time in milliseconds;
- FPS estimate;
- elapsed engine time;
- surface resolution;
- engine lifecycle state;
- renderer backend;
- draw-call count;
- GPU resource count;
- loaded-asset count.

The current headless backend reports zero GPU resources, draw calls and assets. These values are not fabricated GPU metrics.

## Modified Files

- `.github/workflows/android.yml`
- `README.md`
- `ARCHITECTURE.md`
- `app/build.gradle.kts`
- `app/src/main/cpp/CMakeLists.txt`
- `app/src/main/cpp/core/diagnostics.h`
- `app/src/main/cpp/core/diagnostics.cpp`
- `app/src/main/cpp/engine.h`
- `app/src/main/cpp/engine.cpp`
- `app/src/main/cpp/renderer/renderer.h`
- `app/src/main/cpp/renderer/renderer.cpp`
- `CHANGELOG.md`
- `docs/development/IMUX_VERSION_0_0_1.md`
- `docs/development/IMUX_VERSION_0_0_2.md`

## Dependencies

No runtime library dependency was added. The Android SDK toolchain installation was changed, but the application dependency set remains the Stage 1 Compose/AndroidX foundation.

## Build / CI

The workflow uses JDK 17 and Gradle 8.11.1. Android setup installs the exact Android platform, Build Tools, CMake and NDK versions declared by the project.

The repository does not currently contain a Gradle wrapper; CI therefore uses `gradle/actions/setup-gradle` with an explicit Gradle version.

The validated run `35266796122` completed successfully. Its `build` job `105355866227` passed Android SDK setup, Gradle setup and `:app:assembleDebug`, including the CMake/NDK native compilation for `arm64-v8a`. The debug APK was therefore assembled successfully; the workflow does not upload it as an artifact.

## Architecture Changes

The existing ownership model remains intact:

Compose -> Android Platform -> C ABI -> C++ Engine -> Core/Renderer/Game boundary.

The renderer backend interface now exposes backend identity and renderer statistics. The Engine consumes those values for diagnostics. No Android type was introduced into the C++ core or renderer API.

## Validation

The referenced failing CI job was inspected directly. It failed during Android SDK setup before the Gradle build began.

The first corrected run `35266410185` fixed the SDK setup problem: Android SDK setup, Gradle setup and toolchain provisioning all passed. That run then exposed a CMake include-path error in the newly added diagnostics/renderer sources. The CMake include-directory fix was committed as `80742ef397c586c9d0b21c26114f17beb05ee11e`.

The follow-up run `35266796122` / job `105355866227` completed successfully. This is the authoritative Stage 2 build validation.

The successful run emitted non-fatal toolchain warnings: SDK XML version 4 was newer than the XML version understood by part of the installed native tooling, and the NDK toolchain emitted CMake compatibility deprecation warnings. Neither affected the successful build. The setup action itself also reports that its underlying `sdkmanager` CLI is deprecated; this is internal to the current setup action and is not a repository request for the obsolete `tools` package.

Local compilation was not used as the authoritative validation because the development environment does not have a network-accessible GitHub checkout/toolchain mirror.

## Known Issues

- There is still no real Vulkan or OpenGL ES backend.
- The current renderer is intentionally headless; it does not clear or present a GPU surface.
- The C ABI exposes lifecycle operations only; diagnostics are currently native-internal rather than wired to a UI overlay.
- The repository still has no Gradle wrapper.
- Only `arm64-v8a` is configured.
- No voxel world, chunk system, asset pipeline, gameplay, networking, physics, audio or scripting system exists yet.

## Next Stage

The next renderer milestone should introduce native Android surface ownership at a narrow platform/renderer boundary and then implement one real GPU backend end-to-end: graphics initialization, surface/swapchain setup, clear, frame submission and present. A second backend should be added only after the first has a genuine implementation.
