# Imux

Imux is an independent voxel sandbox game built around a custom 3D runtime and engine.

The engine, runtime architecture, data formats, renderer, gameplay code, launcher UI, networking and assets are developed independently.

## Current development version

**0.0.5** — Imux Launcher shell redesign with a compact landscape launcher layout.

See [CHANGELOG.md](CHANGELOG.md) and [docs/development/IMUX_VERSION_0_0_4.md](docs/development/IMUX_VERSION_0_0_4.md) for the factual changes in this stage.

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

The Launcher owns Android presentation and launcher state. The native runtime owns engine state and resource lifetime. Launcher composables do not call JNI or C++ gameplay systems directly. The future Game Launch Boundary will connect a real runtime to the launcher before Play Imux is enabled.

## Launcher

The launcher is landscape-first and uses a compact shell: a top bar, persistent vertical navigation, a deliberately open center region, and a dedicated profile/runtime launch card on the right.

Navigation entries are:

- Рендер
- Игра
- Управление
- Геймпад
- Лаунчер
- Раскладки
- О проекте

The top bar provides the actual launcher entry points for Imux Directory, Accounts and Launcher Settings. A Downloads/Resources action is not shown while no corresponding service exists.

The current profile is a local guest state: Гость / Без аккаунта. No Microsoft, Google, OAuth, Minecraft or external-account authentication is implemented.

The runtime block shows Игровой runtime не установлен, and Играть remains disabled until a real Imux Game Launch Boundary and runtime installation service exist. The launcher never pretends that an unavailable runtime can start.

The project-provided icon.webp remains the launcher branding asset used by the Android app and launcher UI. No Minecraft assets, logo, version or skin are used.

## Build

GitHub Actions uses JDK 17, Gradle 8.11.1, Android SDK 35, NDK 27.0.12077973 and CMake 3.31.6. The workflow builds the Android application and creates a temporary test-signed release APK for the current CI run.

Release signing is intentionally ephemeral for this development stage. CI generates a short-lived test .jks inside the runner, uses it for :app:assembleRelease, verifies the APK with the Android SDK apksigner, uploads the APK as an Actions artifact, and removes the test keystore in an always cleanup step. No permanent keystore and no signing secret are stored in the repository.

The release APK is uploaded as the Imux-Launcher-release.apk artifact and retained by GitHub Actions for 30 days.

For local builds, use a local Android SDK with the same toolchain. The repository does not currently contain a Gradle wrapper, so CI pins the Gradle distribution through gradle/actions/setup-gradle.

## Architecture documentation

See [ARCHITECTURE.md](ARCHITECTURE.md) for the engineering boundaries and development sequence.