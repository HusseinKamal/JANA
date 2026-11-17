# Icon & Theme Verification — Scholarly Luminary Design System

**Date:** Applied as part of Scholarly Luminary design system migration.

---

## 1. Build Results

| Task | Result |
|------|--------|
| `:composeApp:compileKotlinJvm` | ✅ BUILD SUCCESSFUL |
| `:desktopApp:compileKotlin`    | ✅ BUILD SUCCESSFUL |

---

## 2. File Checklist

### Design System — core/designsystem

| File | Status | Key Change |
|------|--------|------------|
| `Color.kt` | ✅ Updated | Full Scholarly Luminary palette — Deep Teal primary, Amber Gold secondary, Astral Indigo tertiary + extended brand tokens |
| `Theme.kt` | ✅ Updated | `JanaLightColors` / `JanaDarkColors` using new palette; removed unused `Color` import; added `scrim`, `surfaceTint`, `inverseSurface`, `inverseOnSurface`, `outlineVariant` |
| `Type.kt` | ✅ Updated | Scholarly Luminary scale — Serif (Newsreader fallback) for display/headline, SansSerif (Plus Jakarta Sans fallback) for body/label; generous line-heights for bilingual readability |
| `Components.kt` | ✅ Updated | Added `JanaAmberButton`, `JanaAiButton`, `JanaLiveBadge`; added all required imports (`BorderStroke`, `Row`, `Spacer`, `Box`, `background`, `clip`, `CircleShape`, `sp`, etc.) |

### Android Launcher Icons

| File | Status | Key Change |
|------|--------|------------|
| `drawable/ic_launcher_background.xml` | ✅ Updated | Deep scholarly teal `#0F3E36` fill with radial glow overlays |
| `drawable-v24/ic_launcher_foreground.xml` | ✅ Updated | Golden amber open book, broadcast arcs, stem + emitter, indigo 4-point AI sparkle star, cyan mini-star, red LIVE badge |
| `mipmap-anydpi-v26/ic_launcher.xml` | ✅ Updated | Added `<monochrome>` element (Android 13+) |
| `mipmap-anydpi-v26/ic_launcher_round.xml` | ✅ Updated | Added `<monochrome>` element (Android 13+) |

### Desktop Icon

| File | Status |
|------|--------|
| `desktopApp/src/main/resources/jana_icon.svg` | ✅ Created — 512×512 SVG with radial teal bg, golden book, broadcast arcs, indigo AI star, cyan accent star, LIVE badge |
| `desktopApp/build.gradle.kts` | ✅ Updated — `linux`, `macOS`, `windows` blocks each set `iconFile` to `jana_icon.svg` |

### iOS App Icon

| File | Status |
|------|--------|
| `iosApp/iosApp/Assets.xcassets/AppIcon.appiconset/Contents.json` | ✅ Updated — simplified to single universal 1024×1024 entry with filename `AppIcon-1024.png` |
| `iosApp/iosApp/Assets.xcassets/AppIcon.appiconset/README.md` | ✅ Created — instructions for placing `AppIcon-1024.png` |

> **Note:** The `AppIcon-1024.png` itself must be exported manually from `jana_icon.svg`
> (e.g. using Inkscape, Figma, or any SVG-to-PNG tool at 1024×1024 px with no transparency).

---

## 3. Colour Verification Spot-checks

| Token | Hex | Role |
|-------|-----|------|
| `Primary` | `#002721` | Deep Scholarly Teal — primary actions |
| `PrimaryContainer` | `#0F3E36` | Launcher background, card fills |
| `AmberGold` | `#D97706` | LIVE badges, secondary CTAs |
| `AstralIndigo` | `#6366F1` | AI Companion UI |
| `DarkBackground` | `#0D1B1A` | Dark mode root background |
| `SuccessGreen` | `#16A34A` | Success states |
| `LiveRed` | `#DC2626` | LIVE badge pill |

---

## 4. Pending Manual Steps

- [ ] Export `AppIcon-1024.png` (1024×1024, opaque) from `jana_icon.svg` and place in `iosApp/iosApp/Assets.xcassets/AppIcon.appiconset/`
- [ ] Drop custom font files into `core/designsystem/src/commonMain/composeResources/font/` and wire via `Font(Res.font.newsreader_semibold, FontWeight.SemiBold)` etc.
- [ ] Generate Android mipmap PNGs (hdpi/xhdpi/xxhdpi/xxxhdpi) from `jana_icon.svg` if needed for pre-API-26 devices
