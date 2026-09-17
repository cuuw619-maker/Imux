# Imux Development Changelog

This file records development versions only. It is not a release changelog.

## Imux Development Version 0.0.3

### Fixed
- Replaced the previous engine-only Compose root with a dedicated Launcher presentation layer while keeping the native engine integration isolated for the future game runtime.
- Removed the previous unspecified screen orientation from the launcher entry activity and made the primary launcher experience landscape.

### Changed
- Application version is now `0.0.3`.
- Android application branding now uses the repository-provided `icon.webp` as the launcher icon and Compose branding resource.
- Architecture documentation now explicitly separates Launcher, Game Launch Boundary and native Imux Engine.

### Added
- Two-column adaptive landscape Launcher Home built with Jetpack Compose and Material 3.
- Dark Material 3 theme with centralized colors.
- Minimal `GuestProfile` model showing `Гость` / `Без аккаунта` without authentication.
- State-based launcher navigation for Home, Settings, About Imux, Control Layouts, Imux Directory, Share Logs and Diagnostics.
- Imux runtime status card showing that the game runtime is not installed.
- Disabled `Play Imux` action so the UI never pretends that a runtime launch is available.
- Launch Settings entry point.
- Moderate Home entrance and navigation animations.
- Development report: [IMUX_VERSION_0_0_3.md](docs/development/IMUX_VERSION_0_0_3.md).

### Build
- Added the Compose Material icon core dependency required by the launcher action icons.
- Existing Android SDK, NDK, CMake, Java and Gradle toolchain versions remain unchanged.

### Architecture
- Launcher UI is isolated under `com.imux.game.launcher`.
- Composables do not call the C++ engine or JNI directly.
- Existing `EngineSurface` and native engine code remain available for the future game runtime path.
- No authentication, gameplay, voxel world or fake runtime implementation was introduced.

### Validation
- Source/resource integration and Android manifest changes were prepared for CI validation.
- Final CI result is recorded in the 0.0.3 development report only after the workflow completes.

### Next
- Validate the launcher build on GitHub Actions and then establish a real Game Launch Boundary before enabling `Play Imux`. The next graphics milestone remains the first genuine GPU backend behind the native renderer boundary.

## Imux Development Version 0.0.2

### Fixed
- Removed the obsolete `tools` package request from Android CI. `android-actions/setup-android` now receives only the SDK components required by the current project.
- Hardened native resize handling by clamping invalid dimensions to non-negative values.
- Added the native CMake source include directory so the existing `core/...` and `renderer/...` include paths resolve correctly under the Android CMake/Ninja build.

### Changed
- Updated GitHub Actions to current compatible stable releases: `actions/checkout@v7`, `actions/setup-java@v6`, `android-actions/setup-android@v4`, and `gradle/actions/setup-gradle@v6`.
- Android CI now installs platform-tools, Android 35, Build Tools 35.0.0, CMake 3.31.6 and NDK 27.0.12077973 through the Android setup action instead of a separate legacy SDK package request.
- Development application version is now `0.0.2`.
- Renderer diagnostics now identify the actual current backend as Headless rather than implying that Vulkan or OpenGL ES is already implemented.

### Added
- Core diagnostics structure covering frame count, frame time, FPS estimate, elapsed time, resolution, engine state and resource counters.
- Renderer statistics abstraction for draw calls, GPU resource count and loaded asset count.
- Periodic native diagnostics logging every 120 rendered frames.
- Development report: [IMUX_VERSION_0_0_2.md](docs/development/IMUX_VERSION_0_0_2.md).
- Retrospective foundation report: [IMUX_VERSION_0_0_1.md](docs/development/IMUX_VERSION_0_0_1.md).

### Build
- CI remains pinned to JDK 17 and Gradle 8.11.1.
- Android SDK/CMake/NDK versions remain aligned with the Gradle Android module.
- The debug APK build completes successfully in GitHub Actions.

### Architecture
- The existing Compose -> Android platform -> C ABI -> C++ Engine boundary is preserved.
- Renderer backend ownership remains RAII-based through `std::unique_ptr`.
- No gameplay, voxel world, networking, Rust, physics, scripting or heavy runtime dependency was introduced.

### Validation
- The failing workflow run `35263298899` / job `105344110559` was inspected directly. Its failure occurred inside `android-actions/setup-android@v3` while it tried to install the obsolete `tools` package.
- Corrected workflow run `35266796122` / job `105355866227` completed successfully.

### Next
- Implement the first real GPU backend behind the renderer boundary.
