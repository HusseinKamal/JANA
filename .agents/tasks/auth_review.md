# `:feature:auth` Login Screen

The change adds the full Student/Teacher login screen as a new `:feature:auth` Gradle module wired into the existing multiplatform app via Koin, Navigation 3, and the established `FeatureEntryProvider` pattern. `AuthRoute.Login` is promoted from a `data object` to a `data class` so the tab hint can travel through the back-stack. A single `AuthViewModel` drives both student and teacher content views via an MVI contract, and 41 string keys cover English and Arabic. All four findings from the first review pass have been addressed.

Watch for: (1) **confirmed** — `"Loading…"` is hardcoded in two content composables, bypassing `stringResource()`; Arabic-locale users see English text on the loading button. (2) **confirmed** — Social sign-in effects (`TriggerGoogleSignIn`, `TriggerFacebookSignIn`) are silent no-ops with no user feedback, while the structurally identical `InstitutionalSSOClicked` path at least fires a snackbar.

**Verdict**: APPROVED

---

## High-level view

`AuthRoute.Login` is now a `data class` carrying `initialTab: String = "STUDENT"` — the correct serializable shape for Navigation 3 and the mechanism by which the splash screen's "Sign In" path passes a tab hint through the back-stack.

A single `AuthViewModel` owns both student and teacher state under one `LoginState`. `AuthFlow` owns the ViewModel, collects effects, and delegates rendering to a stateless `AuthScreen`. `LoginTabSwitcher` uses context-sensitive labels — the displayed text on both tabs changes based on `activeTab` — matching the plan's intent.

The localization fix from iteration 2 wires error messages through an `AuthStrings` data class pushed into the ViewModel by `AuthFlow` via `LaunchedEffect`. This keeps `LoginState.error: String?` a plain string while letting the composition layer resolve locale at render time. The `strings` field is a `var` on the ViewModel; because it is currently read only synchronously inside `onIntent`, there is no data race — but this becomes fragile if the `handleSignIn` coroutine body is later expanded to reference `strings`.

Build evidence in `auth_impl.md` shows `:composeApp:compileKotlinJvm BUILD SUCCESSFUL` and 17 jvmTest cases passing. The gate condition is satisfied.

---

<details>
<summary>Issues (4)</summary>

1. **Hardcoded loading label** — `StudentLoginContent` and `TeacherLoginContent` render the literal `"Loading…"` string during sign-in; Arabic-locale users see English on the loading button. Replace with `stringResource(Res.string.loading)` (key already exists in both XML files).

2. **Silent social sign-in stubs** — `TriggerGoogleSignIn` and `TriggerFacebookSignIn` are handled as `{ /* comment */ }` in `AuthFlow`, giving no visible feedback when the user taps either button. The `ShowSnackbar` effect already exists; a "coming soon" snackbar identical to the SSO path would prevent the UI from appearing broken.

3. **Unused `auth_apple` string** — `auth_apple` / `معرف آبل` are defined in both XML files but referenced by no component. Remove the dead keys or add an Apple button to `SocialLoginRow`.

4. **`Spacer` with `padding` instead of `width`** — `FacultyOnboardingCard` uses `Spacer(modifier = Modifier.padding(start = 8.dp))` to separate the graduation emoji from the title. The padding inflates the zero-size Spacer in the current Row context, but if parent constraints change the spacer collapses to zero. Replace with `Spacer(modifier = Modifier.width(8.dp))`.

</details>

<details>
<summary>Details</summary>

### `AuthStrings` mutation fragility

The `strings` field on `AuthViewModel` is a `var`, mutated by `updateStrings()` from outside any coroutine. Currently safe because `strings` is only read synchronously inside `onIntent` (specifically the `setState { copy(error = strings.errorMissingFields) }` path), before any coroutine launch. If a future iteration expands `handleSignIn`'s `viewModelScope.launch` block to reference `strings`, the value read inside the coroutine could differ from the value at dispatch time — a silent locale-mismatch bug.

### Social sign-in silence gap

`TriggerGoogleSignIn` and `TriggerFacebookSignIn` arrive at `AuthFlow.effect.collect` as silent no-ops:

```kotlin
LoginEffect.TriggerGoogleSignIn   -> { /* Google Sign-In coming soon */ }
LoginEffect.TriggerFacebookSignIn -> { /* Facebook Sign-In coming soon */ }
```

No snackbar, no toast. By contrast, `InstitutionalSSOClicked` dispatches `ShowSnackbar(strings.errorSsoComingSoon)` — still unrendered because the scaffold isn't wired, but the effect carries a message string. The student tab's Google and Facebook buttons need the same treatment before those buttons can ship.

### Hardcoded loading label

Both `StudentLoginContent` and `TeacherLoginContent` contain:

```kotlin
if (state.isLoading) {
    Text(text = "Loading…", ...)
}
```

The `loading` key (`"Loading…"` / `"جارٍ التحميل…"`) already exists in both XML files. This is the only visible-text string in the entire auth feature that bypasses `stringResource()`.

### Tab label context-switching

`LoginTabSwitcher` relabels both tabs simultaneously on every tab change:

| Active tab | Student-tab label | Teacher-tab label |
|---|---|---|
| STUDENT | `auth_tab_student` ("Student") | `auth_tab_teacher` ("Teacher / Faculty") |
| TEACHER | `auth_tab_student_portal` ("Student Portal") | `auth_tab_faculty` ("Faculty & Educator") |

The inactive student tab acquires a different label when the teacher tab is selected. This matches the plan but is non-obvious to maintain: a future developer editing one label row needs to know the other label changes at the same time.

### `FacultyOnboardingCard` spacer

```kotlin
Spacer(modifier = Modifier.padding(start = 8.dp))
```

This creates 8 dp of space today because `Modifier.padding` inflates the zero-size Spacer's reported layout bounds in a Row context. `Modifier.width(8.dp)` is the stable form.

### Test coverage

17 JVM unit tests cover all 14 intents, the validation branch (blank identifier, blank password), localized error strings via Arabic `AuthStrings`, the sign-in delay via `advanceTimeBy`, both tab routing outcomes, and `updateStrings` propagation.

Not tested: the `initialTab == "TEACHER"` path in `AuthFlow` (requires a UI test), `AuthEntryProvider` NavEntry wiring, and the Register placeholder redirect.

</details>

---

<details>
<summary>File map</summary>

| File | Change |
|---|---|
| `core/navigation/Routes.kt` | `AuthRoute.Login` → `data class` with `initialTab`; `ForgotPasswordRoute` added |
| `feature/splash/SplashEntryProvider.kt` | `AuthRoute.Login` → `AuthRoute.Login()` |
| `core/designsystem/values/strings.xml` | 41 `auth_*` English strings added |
| `core/designsystem/values-ar/strings.xml` | 41 `auth_*` Arabic strings added |
| `feature/auth/build.gradle.kts` | New module; mirrors `feature:splash` structure |
| `settings.gradle.kts` | `include(":feature:auth")` added |
| `shared/build.gradle.kts` | `implementation(project(":feature:auth"))` added |
| `shared/di/KoinInit.kt` | `authModule` imported and registered |
| `feature/auth/…/AuthContract.kt` | `LoginTab`, `AuthStrings`, `LoginState`, `LoginIntent`, `LoginEffect` |
| `feature/auth/…/AuthViewModel.kt` | Full MVI VM with `updateStrings`, `handleSignIn` |
| `feature/auth/…/AuthScreen.kt` | `AuthFlow` (stateful) + `AuthScreen` (stateless) |
| `feature/auth/…/AuthEntryProvider.kt` | NavKey → NavEntry wiring for Login + Register |
| `feature/auth/…/di/AuthModule.kt` | Koin `viewModel` + `single<FeatureEntryProvider>` |
| `feature/auth/…/components/AuthTextField.kt` | Stateless labelled field with leading/trailing/labelContent slots |
| `feature/auth/…/components/PasswordField.kt` | Password variant with lock icon, eye toggle, protected badge |
| `feature/auth/…/components/CountryCodeChip.kt` | `MutedSand` pill with country code |
| `feature/auth/…/components/LoginTabSwitcher.kt` | Segmented control with context-sensitive labels |
| `feature/auth/…/components/SocialLoginRow.kt` | Google + Facebook equal-width outlined buttons |
| `feature/auth/…/components/AuthTopBar.kt` | Student and teacher top bar variants |
| `feature/auth/…/components/StudentLoginContent.kt` | Full student login column, stateless |
| `feature/auth/…/components/TeacherLoginContent.kt` | Full teacher/faculty login column, stateless |
| `feature/auth/…/components/FacultyOnboardingCard.kt` | Onboarding card with call + WhatsApp CTAs |
| `feature/auth/…/AuthViewModelTest.kt` | 17 JVM unit tests |

Full diff: `git diff main -- feature/auth core/navigation shared feature/splash`

</details>
