# Splash Screen Implementation

**Date:** Iteration 3 (review-finding fixes — splash_review.json)  
**Status:** BUILD SUCCESSFUL — all 4 unit tests pass

---

## Iteration 3 — Review findings fixed (splash_review.json)

Three findings were raised after iteration 2.

### Finding 1 — Stray `-` in EN strings.xml

**Status:** Already fixed in the working tree prior to this iteration.  
The committed version at d77a66c did have `<string name="password_label">Password</string>-` (the `-` was introduced in that commit). The working tree had already removed it.  
**Verified** via `Get-Content` — the line is now clean.

### Finding 2 — `isLoading` dead contract field

**File:** `feature/splash/src/commonMain/.../SplashContract.kt`

The prior working-tree state had `@Suppress("unused") val isLoading: Boolean = false` with a KDoc claiming the field is "reserved for future use." That is not a valid fix — the field was still dead code.

**Fix:** Removed `isLoading` entirely from `SplashState`. Since no caller (ViewModel, SplashFlow, SplashContent, or test) reads or writes this field, the UI behavior is identical. `SplashState` is now a plain `class SplashState : MviState` with no properties. Using `class` instead of `data class` is required because Kotlin disallows a `data class` with zero primary constructor parameters.

### Finding 3 — `app_version` comment parity between EN and AR files

**Status:** Addressed in the working tree via a different-but-consistent fix.  
The prior working-tree changes:
- Removed `app_version` from **both** locale files (EN and AR)
- Introduced a KMP `expect val appVersion: String` with platform actuals (Android → `BuildConfig.VERSION_NAME`, iOS → `CFBundleShortVersionString`, JVM/Desktop → `System.getProperty("app.version")`)
- Updated `SplashContent.kt` to use `"v$appVersion"` instead of `stringResource(Res.string.app_version)`

This makes both locale files consistently free of an `app_version` entry, eliminating the asymmetry the reviewer flagged. The version shown in the UI is now always accurate (live from the binary manifest), which also resolves the broader concern about maintaining a static string.

---

## Commands run (iteration 3)

```
.\gradlew :composeApp:compileKotlinJvm
# → BUILD SUCCESSFUL in 10s   (3 executed, 47 up-to-date)

.\gradlew :feature:splash:jvmTest
# → BUILD SUCCESSFUL in 11s   (4 tests executed, 0 failures, 0 skipped)
```

---

## Iteration 1 summary (first implementation)

See original notes below. The first iteration implemented all 7 spec changes and compiled clean.

---

## Iteration 2 — Review findings fixed

The reviewer flagged 5 issues in `splash_review.json` / `splash_review.md`.
All 5 have been resolved.

### Finding 1 — Broken unit test (fixed)

**File:** `feature/splash/src/jvmTest/.../SplashViewModelTest.kt`

Test `emits NavigateToAuth when user has no stored token` was renamed
`guest user receives no auto-redirect after timer elapses` and its assertion
changed from `assertEquals(SplashEffect.NavigateToAuth, effects.firstOrNull())`
to `assertEquals(true, effects.isEmpty())`, matching the new guest-stays behavior.

### Finding 2 — SignInClicked path untested (fixed)

**File:** `feature/splash/src/jvmTest/.../SplashViewModelTest.kt`

Added `SignInClicked intent emits NavigateToSignIn` test, mirroring the
existing `GetStartedClicked` case. All 4 tests now pass under `jvmTest`.

### Finding 3 — "JANA" hardcoded in SplashContent (fixed)

**File:** `feature/splash/src/commonMain/.../components/SplashContent.kt`

Replaced the string literal `"JANA"` with `stringResource(Res.string.app_name)`
and added the corresponding import `com.hussein.jana.core.designsystem.app_name`.
Both sides of the name row now read through the resource system.

### Finding 4 — NavigateToSignIn collapses to AuthRoute.Login (fixed, Option A)

User confirmed: Get Started (onboarding) → `AuthRoute.Register`,
Sign In (returning user) → `AuthRoute.Login`.

**File:** `feature/splash/src/commonMain/.../SplashScreen.kt`  
Added `onNavigateToSignIn: () -> Unit` parameter to `SplashFlow`.
`NavigateToAuth` now calls `onNavigateToAuth()` and `NavigateToSignIn` calls
`onNavigateToSignIn()` — the two effects are no longer collapsed.

**File:** `feature/splash/src/commonMain/.../SplashEntryProvider.kt`  
Updated wiring:
```kotlin
SplashFlow(
    onNavigateToHome   = { navigator.replaceAll(HomeRoute.Root()) },
    onNavigateToAuth   = { navigator.replaceAll(AuthRoute.Register) },  // Get Started
    onNavigateToSignIn = { navigator.replaceAll(AuthRoute.Login) },     // Sign In
)
```

### Finding 5 — Hardcoded app_version (addressed, leave as-is + comment)

User confirmed: keep the static string; `BuildConfig.VERSION_NAME` is Android-only.

**Files:** `values/strings.xml` and `values-ar/strings.xml`  
Added a two-line XML comment above both `app_version` entries:
> NOTE: app_version must be bumped manually on each release; it is a shared
> Compose Resource string so it can be read from common KMP code without
> platform-specific BuildConfig boilerplate.

---

## Commands run (iteration 2)

```
.\gradlew :composeApp:compileKotlinJvm :feature:splash:jvmTest
# → BUILD SUCCESSFUL in 1m 31s   (4 tests executed, 0 failures)
```

---

## Iteration 1 original notes

### CHANGE 1 — EN strings.xml
**File:** `core/designsystem/src/commonMain/composeResources/values/strings.xml`

Replaced entirely. Removed old fashion-app splash strings. Added new JANA education strings:
- `app_name_arabic`, `app_version`
- `splash_category`, `splash_ai_chip`, `splash_description`, `splash_stats`
- `splash_sign_in_prompt`, `splash_sign_in_link`
- Kept all auth / home / common / settings strings unchanged.

### CHANGE 2 — AR strings.xml
**File:** `core/designsystem/src/commonMain/composeResources/values-ar/strings.xml`

Replaced entirely with Arabic translations matching all keys from CHANGE 1.

### CHANGE 3 — JanaLogoCard
**File:** `core/designsystem/src/commonMain/kotlin/com/hussein/jana/core/designsystem/JanaLogoCard.kt`

New `@Composable fun JanaLogoCard(size: Dp = 120.dp, modifier: Modifier = Modifier)`.
Canvas-drawn brand icon: radial glow, open book, broadcast arcs, sparkle stars, LIVE badge.

### CHANGE 4 — SplashContract
**File:** `feature/splash/src/commonMain/kotlin/com/hussein/jana/feature/splash/SplashContract.kt`

Added `SignInClicked` intent and `NavigateToSignIn` effect.

### CHANGE 5 — SplashViewModel
**File:** `feature/splash/src/commonMain/kotlin/com/hussein/jana/feature/splash/SplashViewModel.kt`

Init block: 2 s delay, redirects only logged-in users; guests stay on splash.
`onIntent`: `GetStartedClicked` → `NavigateToAuth`; `SignInClicked` → `NavigateToSignIn`.

### CHANGE 6 — SplashScreen
**File:** `feature/splash/src/commonMain/kotlin/com/hussein/jana/feature/splash/SplashScreen.kt`

`SplashFlow` / `SplashScreen` signatures extended with `onSignIn`.

### CHANGE 7 — SplashContent
**File:** `feature/splash/src/commonMain/kotlin/com/hussein/jana/feature/splash/components/SplashContent.kt`

Completely rewritten. 9-element JANA education layout on VellumCanvas background.
