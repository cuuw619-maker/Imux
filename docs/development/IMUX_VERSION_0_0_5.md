# Imux 0.0.5 — Launcher Runtime Foundation

## Scope

This milestone continues the 0.0.4 launcher shell instead of replacing it.

## Launcher

- landscape-first immersive edge-to-edge window handling;
- existing Imux icon retained;
- top bar actions for Directory, Accounts, Resources, Diagnostics and Settings;
- settings is a separate animated route opened by the top-bar gear;
- responsive sidebar / center / right launch-card composition;
- Play remains disabled until a real Imux runtime is detected.

## Runtime and launch boundary

The launcher now separates:

Launcher UI -> Launcher Services -> Game Launch Boundary -> Imux Runtime

RuntimeManager inspects the private application directory and a real runtime.version marker. GameLaunchBoundary is intentionally unavailable until an executable Imux runtime exists, so the launcher never fakes a successful launch.

## Foundations

Added architecture models/services for:

- Imux accounts;
- RendererConfig and GameSettings;
- ControlLayout and ControlElement;
- LayoutRepository/LayoutSerializer;
- ResourceManager and ResourcePackage;
- Imux Directory;
- Android gamepad discovery;
- localization registry with English fallback.

## Design system

ImuxColors, ImuxSpacing, ImuxDimensions, ImuxShapes and the existing Material 3 theme now form the launcher design system.

## Native boundary

The existing C++ engine, C ABI, JNI bridge and CMake build remain unchanged. No Rust or Lua runtime is introduced prematurely; their future responsibility remains below the launcher/runtime boundary.

## Validation

GitHub Actions run 27 validated the release build after the launcher state delegate fix:

- :app:assembleRelease — success;
- signed APK verification — success;
- release APK artifact upload — success;
- temporary signing material cleanup — success.

Artifact: Imux-Launcher-release.apk.
