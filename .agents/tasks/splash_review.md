# JANA Splash Screen — Iteration 3 (Final Review)

All three findings from the previous pass are confirmed resolved. The stray `-` is gone, `isLoading` has been removed from the contract entirely, and the `app_version` asymmetry is eliminated by replacing both static strings with a KMP `expect val appVersion`. Build evidence is present in the impl note (`compileKotlinJvm` and `jvmTest` both SUCCESSFUL, 4/4 tests pass, 0 failures). No new blocking concerns introduced.

**Watch for:** The desktop `jvmArgs` fallback for `app.version` and the `packageVersion` constant in the same build file are two separate hardcoded `"2.4.0"` strings — if `project.version` is not set at the root level, they will drift independently on the next version bump (possible). A logged-in user who taps "Get Started" before the 2s auto-redirect timer fires will receive two effects in sequence — `NavigateToAuth` immediately and `NavigateToHome` two seconds later; whether the second effect reaches the collector after navigation cancels the `LaunchedEffect` is a timing race (possible).

**Verdict**: APPROVED

---

## High-level view

The three prior findings are cleared: the EN `strings.xml` `password_label` line no longer carries the stray character (confirmed in diff), `SplashState` is now a no-property `class` with no dead fields, and both locale files are `app_version`-free — the displayed version now comes from a KMP `expect val appVersion` backed by platform actuals (`BuildConfig.VERSION_NAME` on Android, `CFBundleShortVersionString` on iOS, `System.getProperty("app.version")` on JVM). `initAppVersion` is wired before `initKoin` in `JanaApp.onCreate()`, so Android reads the real value from first composition. Build and test evidence in the impl note is present and credible.

The `app_version` replacement introduces a minor versioning bookkeeping surface on desktop: `jvmArgs` tries `project.version` first and falls back to `"2.4.0"`, but `packageVersion` in the same file is a separate hardcoded constant that does not read `project.version`. When a developer bumps the version they must update both independently or they drift.

The ViewModel's init-block timer and user-intent handlers are both live from the moment the composable enters composition. A logged-in user who taps a CTA in under two seconds produces two effects on the same channel. The `NavigateToHome` effect arrives after the `LaunchedEffect` collector has likely been cancelled by navigation, so the second effect is typically dropped — but the cancellation race is not tested.

---

<details>
<summary>Issues (2)</summary>

1. **Desktop version string split across two constants** — `jvmArgs` fallback `"2.4.0"` and `packageVersion = "2.4.0"` in `desktopApp/build.gradle.kts` are independent; a version bump that updates one but not the other will silently show the wrong version at runtime. Centralise both behind `project.version` at the root build script, or extract a `val appVersion` local to the desktop build file and reference it from both places.

2. **Logged-in user double-navigation race** — `SplashFlow`'s `LaunchedEffect` collects effects for the full composition lifetime. A logged-in user tapping "Get Started" before 2 s generates `NavigateToAuth` immediately and `NavigateToHome` two seconds later; if the `LaunchedEffect` coroutine survives long enough after the first navigation, `replaceAll(HomeRoute.Root())` fires on the already-navigated stack. Add a `Job` cancel on first effect delivery, or debounce by clearing the ViewModel's effect channel once it has fired.

</details>

<details>
<summary>Details</summary>

### Prior findings — all three resolved (confirmed)

The diff confirms all three fixes directly: `password_label` has no trailing character, `SplashContract.kt` is now `class SplashState : MviState` with no body (using `class` rather than `data class` because Kotlin disallows a zero-parameter `data class`), and neither locale file contains `app_version` — `SplashContent` reads `"v$appVersion"` from the platform actual instead.

### Desktop version drift

`desktopApp/build.gradle.kts` after this change:

```kotlin
jvmArgs("-Dapp.version=${project.version.takeIf { it != "unspecified" } ?: "2.4.0"}")
…
packageVersion = "2.4.0"
```

When `project.version` is set, `jvmArgs` picks it up and the displayed splash version matches the binary. `packageVersion` does not read `project.version`; it stays at `"2.4.0"` regardless. A developer bumping the version who updates only `packageVersion` will ship a correctly versioned installer but splash still shows `"2.4.0"`. The fix is either `packageVersion = project.version.toString()` or a local `val desktopAppVersion = "X.Y.Z"` referenced from both lines.

### Logged-in user double-navigation race

The ViewModel emits at most one auto-redirect (guarded by `isLoggedIn()`), and manual intents emit immediately. `SplashFlow` collects on `viewModel.effect` in a `LaunchedEffect(Unit)`:

```kotlin
LaunchedEffect(Unit) {
    viewModel.effect.collect { effect ->
        when (effect) {
            SplashEffect.NavigateToHome   -> onNavigateToHome()
            SplashEffect.NavigateToAuth   -> onNavigateToAuth()
            SplashEffect.NavigateToSignIn -> onNavigateToSignIn()
        }
    }
}
```

If a logged-in user taps "Get Started" at t=0.5s, `onNavigateToAuth()` fires and navigation replaces the splash entry. The composable leaves the tree and its coroutine scope is cancelled — but cancellation is cooperative and the effect channel might already have the `NavigateToHome` message queued when the 2s timer fires. In practice the LaunchedEffect is likely cancelled before the second effect arrives, but the scenario is untested. A simple guard (`var hasNavigated = false`) or cancelling the `LaunchedEffect`'s job after the first delivery would make the invariant explicit.

</details>

---

<details>
<summary>File map</summary>

| File | Change |
|------|--------|
| `core/designsystem/.../values/strings.xml` | JANA education splash keys; old fashion-app keys removed; `app_version` removed; sections commented |
| `core/designsystem/.../values-ar/strings.xml` | Matching Arabic translations; `app_version` removed |
| `core/designsystem/.../JanaLogoCard.kt` | New Canvas composable: open-book brand icon, radial glow, broadcast arcs, sparkle stars, LIVE badge |
| `core/common/.../AppVersion.kt` | `expect val appVersion: String` |
| `core/common/.../AppVersion.android.kt` | `actual` backed by `_appVersion` var; `initAppVersion(version)` for `Application.onCreate` wiring |
| `core/common/.../AppVersion.ios.kt` | `actual` reads `CFBundleShortVersionString`; falls back to `"1.0.0"` |
| `core/common/.../AppVersion.jvm.kt` | `actual` reads `System.getProperty("app.version", "1.0.0")` |
| `feature/splash/.../SplashContract.kt` | `SplashState` is now a no-property `class`; `SignInClicked` intent; `NavigateToSignIn` effect |
| `feature/splash/.../SplashViewModel.kt` | 2s init timer redirects logged-in users only; guests stay; `GetStartedClicked → NavigateToAuth`; `SignInClicked → NavigateToSignIn` |
| `feature/splash/.../SplashScreen.kt` | `SplashFlow` gains `onNavigateToSignIn`; all three effect branches mapped |
| `feature/splash/.../SplashEntryProvider.kt` | `NavigateToAuth → AuthRoute.Register`; `NavigateToSignIn → AuthRoute.Login` |
| `feature/splash/.../SplashContent.kt` | VellumCanvas background; 9-element JANA education layout; `JanaLogoCard`; `app_name` via `stringResource`; version via `appVersion` |
| `feature/splash/src/jvmTest/.../SplashViewModelTest.kt` | Guest-stays assertion corrected; `SignInClicked → NavigateToSignIn` test added; 4/4 pass |
| `androidApp/.../JanaApp` / `MainActivity.kt` | `initAppVersion(BuildConfig.VERSION_NAME)` wired before `initKoin` |
| `androidApp/build.gradle.kts` | `versionName` bumped to `"2.4.0"` |
| `desktopApp/build.gradle.kts` | `jvmArgs("-Dapp.version=…")` added; `packageVersion` bumped to `"2.4.0"` |
| `androidApp/.../values-ar/strings.xml` | `app_name` updated to `جَنَى` |

Full diff: `git diff 678cd75 HEAD`

</details>
