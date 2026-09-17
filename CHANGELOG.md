# Imux Development Changelog

This file records development versions only. It is not a release changelog.

## Imux Development Version 0.0.2

### Fixed
- Removed the obsolete `tools` package request from Android CI. `android-actions/setup-android` now receives only the SDK components required by the current project.
- Hardened native resize handling by clamping invalid dimensions to non-negative values.

### Changed
- Updated GitHub Actions to current compatible stable releases: `actions/checkout@v7`, `actions/setup-java@v6`, `android-actions/setup-android@v4`, and `gradle/actions/setup-gradle@v6`.
- Android CI now installs platform-tools, Android 35, Build Tools 35.0.0, CMake 3.31.6 and NDK 27.0.12077973 through the Android setup action instead of a separate legacy SDK package request.
- Development application version is now `0.0.2`.
- Renderer diagnostics now identify the actual current backend as Headless rather than implying that Vulkan or OpenGL ES is already implemented.

### Added
- Core diagnostics structure covering frame count, frame time, FPS estimate, elapsed time, resolution, engine state, renderer backend and resource counters.
- Renderer statistics abstraction for draw calls, GPU resource count and loaded asset count.
- Periodic native diagnostics logging every 120 rendered frames.
- Development report: [IMUX_VERSION_0_0_2.md](docs/development/IMUX_VERSION_0_0_2.md).
- Retrospective foundation report for the previously completed 0.0.1 stage: [IMUX_VERSION_0_0_1.md](docs/development/IMUX_VERSION_0_0_1.md).

### Build
- CI remains pinned to JDK 17 and Gradle 8.11.1.
- Android SDK/CMake/NDK versions remain aligned with the Gradle Android module.

### Architecture
- The existing Compose -> Android platform -> C ABI -> C++ Engine boundary is preserved.
- Renderer backend ownership remains RAII-based through `std::unique_ptr`.
- No gameplay, voxel world, networking, Rust, physics, scripting or heavy runtime dependency was introduced.

### Validation
- The failing workflow run `35263298899` / job `105344110559` was inspected directly. Its failure occurred inside `android-actions/setup-android@v3` while it tried to install the obsolete `tools` package.
- A new CI run is triggered by the 0.0.2 commit; its result is recorded here after GitHub Actions completes.

### Next
- Implement the first real GPU backend behind the renderer boundary, starting with Android surface/native-window ownership and a minimal clear/present path. Do not expose fake Vulkan/GLES functionality before that path exists.
