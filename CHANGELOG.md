# Imux Development Changelog

This file records development versions only. It is not a release changelog.

## Imux Development Version 0.0.4

### Changed
- Reworked the launcher from the previous large Home action-card layout into a compact, landscape-first launcher shell.
- Application version is now 0.0.4.
- Replaced the previous launcher color palette with the centralized ImuxTheme dark rose/brown Material 3 palette.
- The right launch/profile panel is now a persistent rounded surface with guest profile, runtime state, launch settings and the primary Play action.
- Launcher navigation is now a persistent seven-item vertical sidebar with animated selected state and section dividers.
- The top bar now provides compact icon actions for Imux Directory, Accounts and Launcher Settings.
- Removed Minecraft-specific or legacy launcher concepts from the visible UI, including game version labels and unrelated tools.

### Added
- LauncherSidebar component with touch-friendly horizontal icon/label entries.
- Responsive sizing for landscape phones, wide phones and tablets using Compose constraints instead of absolute coordinates.
- Compact Imux runtime status block using the existing icon.webp.
- Guest account placeholder with a future-compatible Accounts entry point.
- Dedicated center-content states for Renderer, Game, Controls, Gamepad, Launcher, Layouts, About, Accounts and Imux Directory.
- Calm content and selection transitions using Compose animation APIs.
- Material Icons Extended dependency for the launcher icon vocabulary.

### Architecture
- The launcher remains isolated under com.imux.game.launcher.
- Compose launcher code still does not call JNI or C++ engine systems directly.
- The native EngineSurface, C ABI and C++ engine remain untouched by this redesign.
- No authentication service, networking stack or game runtime was added.

### Build / CI
- Temporary CI signing remains ephemeral: a test keystore is generated only on the runner, the release APK is assembled and verified, the artifact is uploaded, and the keystore is removed.
- The workflow continues to verify the APK with the explicit Android SDK Build Tools 35.0.0/apksigner path.

### Validation
- Final CI validation is recorded after the 0.0.4 GitHub Actions workflow finishes.