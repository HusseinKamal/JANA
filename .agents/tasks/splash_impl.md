# Splash Screen Implementation

**Date:** Iteration 2 (review-finding fixes)  
**Status:** BUILD SUCCESSFUL — all 4 unit tests pass

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
