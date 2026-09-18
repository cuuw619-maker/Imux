# Imux 0.0.4

## Purpose

Redesign the Imux Launcher into a compact, touch-friendly landscape shell while preserving the separation between Launcher UI and the native engine.

## UI structure

```text
┌──────────────────────────────────────────────────────────────┐
│ Imux Launcher                          folder  users      ⚙  │
├────────────────┬──────────────────────────────────┬─────────┤
│ navigation     │          open center             │ profile │
│                │                                  │ runtime │
│ Рендер         │                                  │ launch  │
│ Игра           │                                  │         │
│ Управление     │                                  │ Играть  │
│ Геймпад        │                                  │         │
│ Лаунчер        │                                  │         │
│ ─────────      │                                  │         │
│ Раскладки      │                                  │         │
│ ─────────      │                                  │         │
│ О проекте      │                                  │         │
└────────────────┴──────────────────────────────────┴─────────┘
```

The center is intentionally not filled with large home cards. It provides only lightweight context for the currently selected launcher section.

## Navigation

The sidebar contains exactly these primary sections:

- Рендер
- Игра
- Управление
- Геймпад
- Лаунчер
- Раскладки
- О проекте

The selected item receives an animated rounded container. Section dividers separate the primary settings group, layouts and project information.

The top bar contains compact actions for Imux Directory, Accounts and Launcher Settings. Downloads/Resources is intentionally absent while no real download/resource service exists.

## Profile and runtime

The right panel is a dedicated rounded Material 3 surface.

The current account state is Гость / Без аккаунта.

The Добавить аккаунт button is a placeholder entry point only. No external authentication provider is implemented.

The runtime block uses the existing project asset app/src/main/res/drawable/icon.webp, displays Imux, and reports Игровой runtime не установлен.

The Играть button remains disabled. It will be enabled only after a real Imux runtime installation and Game Launch Boundary exist.

The UI does not display Minecraft version numbers, Minecraft branding, game assets, skins, Forge, Fabric, OptiFine, Execute JAR or similar legacy concepts.

## Responsive behavior

The layout uses Compose constraints and a flexible center column:

- sidebar: approximately 168–184 dp depending on available width;
- right launch/profile card: approximately 260–332 dp depending on available width;
- center: consumes the remaining space.

No absolute screen coordinates are used.

The activity remains landscape-first and continues to use safe drawing insets.

## Animation

The redesign uses restrained Compose animation for selected navigation and content/status transitions. Material interactions provide standard touch feedback for clickable controls.

## Branding

The existing icon.webp is reused. No new logo or external game asset was introduced.

## Architecture

The presentation path remains MainActivity -> LauncherApp -> launcher shell/components/screens.

The future game path remains separate: Android Game surface -> JNI/C ABI -> C++ Imux Engine.

Launcher composables do not call JNI or C++ gameplay systems directly.

## Build / CI

The Android toolchain remains compile/target SDK 35, JDK 17, Gradle 8.11.1, NDK 27.0.12077973 and CMake 3.31.6.

Release CI uses temporary signing only. It generates a short-lived test keystore in the GitHub Actions runner, assembles :app:assembleRelease, verifies the resulting APK using the explicit $ANDROID_HOME/build-tools/35.0.0/apksigner path, uploads Imux-Launcher-release.apk, and removes the keystore afterward.

No permanent signing secret or keystore is required.

## Validation

Final validation for this stage is recorded in this document only after GitHub Actions finishes the Compose/resource/release path successfully.