# Imux 0.0.3

## Purpose

Create the first real Imux Launcher foundation as a dedicated landscape Compose/Material 3 experience while preserving the separation between launcher UI and the native game engine.

## Previous State

The repository had a small Compose entry point whose primary responsibility was hosting the native `EngineSurface`. The native C++ engine, C ABI, renderer abstraction and CI foundation from 0.0.2 remain intact. The Android manifest did not yet require landscape orientation, and no dedicated Launcher feature or navigation model existed.

The repository root already contained the project-provided `icon.webp`. It was added in the commit immediately preceding this stage and was not replaced with a generated logo.

## Changes

- Replaced the Compose root of `MainActivity` with a dedicated `LauncherApp` feature.
- Added a responsive two-column landscape Home screen using Compose layout weights rather than fixed coordinates.
- Added a centralized dark Material 3 launcher theme.
- Added the `GuestProfile` model with `Гость` and `Без аккаунта` state.
- Added state-based navigation between Home and Settings, About, Control Layouts, Imux Directory, Share Logs and Diagnostics screens.
- Added the Imux runtime status card and kept `Play Imux` disabled because no real game launch service/runtime installation exists yet.
- Added the Launch Settings entry point.
- Added moderate entrance and navigation animations.
- Moved the existing `icon.webp` Git blob into Android `res/drawable/icon.webp` through a Git tree operation so the same project-provided asset can be used by Android resources without creating a duplicate or replacement asset.
- Updated the Android application label and icon to Imux Launcher.
- Updated the manifest to use landscape orientation.
- Added the Material icon core dependency required by the launcher controls.
- Updated architecture documentation to distinguish Launcher, Game Launch Boundary and native engine responsibilities.

## Fixed

- The Android entry activity no longer renders the native game surface as the launcher Home screen.
- The launcher no longer has an unspecified orientation that could present as a portrait-style layout.
- The UI does not expose a fake game version or pretend that Play can launch an unavailable runtime.

## Added

Launcher screens currently implemented:

- Home
- Settings
- About Imux
- Control Layouts
- Imux Directory
- Share Logs
- Diagnostics

The secondary screens are intentionally informational/entry-point screens where the underlying service does not yet exist.

## Modified Files

- `app/build.gradle.kts`
- `app/src/main/AndroidManifest.xml`
- `app/src/main/java/com/imux/game/MainActivity.kt`
- `app/src/main/res/values/styles.xml`
- `ARCHITECTURE.md`
- `CHANGELOG.md`

## Added Files

- `app/src/main/java/com/imux/game/launcher/LauncherApp.kt`
- `app/src/main/java/com/imux/game/launcher/model/GuestProfile.kt`
- `app/src/main/java/com/imux/game/launcher/model/LauncherDestination.kt`
- `app/src/main/java/com/imux/game/launcher/theme/LauncherTheme.kt`
- `app/src/main/java/com/imux/game/launcher/components/LauncherTopBar.kt`
- `app/src/main/java/com/imux/game/launcher/components/LauncherActionCard.kt`
- `app/src/main/java/com/imux/game/launcher/screens/HomeScreen.kt`
- `app/src/main/java/com/imux/game/launcher/screens/SecondaryScreen.kt`
- `docs/development/IMUX_VERSION_0_0_3.md`

## Dependencies

Added only `androidx.compose.material:material-icons-core` for the launcher action icons. No navigation framework, authentication library, networking stack or game-runtime dependency was added.

## Build / CI

The project continues to use the existing 0.0.2 Android toolchain: compile/target SDK 35, JDK 17, Gradle 8.11.1, NDK 27.0.12077973 and CMake 3.31.6.

Final CI validation for this stage is recorded after the GitHub Actions run completes.

## Architecture Changes

The Android presentation path is now:

`MainActivity -> LauncherApp -> Launcher screens/components`

The native game path remains separate:

`Android Game surface -> JNI/C ABI -> C++ Imux Engine`

Launcher composables do not call JNI or C++ engine functions directly. A real Game Launch Boundary will be introduced before `Play Imux` is enabled.

## Validation

Static repository inspection confirmed the existing engine files were retained. The `icon.webp` file was verified to exist at repository root before the tree move, and its existing Git blob was reused rather than replaced. Android resource references now target `R.drawable.icon`.

## Known Issues

- `Play Imux` is intentionally disabled because a real runtime installation/launch service does not exist yet.
- Control Layouts, Imux Directory and Share Logs are entry points/placeholders, not fake implementations of their future services.
- Diagnostics remain native/internal; they are not yet rendered as a launcher overlay.
- Only the dark theme is implemented.
- The launcher is explicitly landscape at the activity level; adaptive two-column sizing uses Compose weights and safe drawing insets.

## Next Stage

First validate this Launcher through GitHub Actions. After the UI foundation is verified, introduce a small Game Launch Boundary and then continue the renderer milestone with a genuine Android surface/native-window path and one real GPU backend. Do not enable Play until a real launch path exists.
