# `:feature:auth` Implementation Notes

## Verification

```
.\gradlew :composeApp:compileKotlinJvm
BUILD SUCCESSFUL in 11s
57 actionable tasks: 2 executed, 55 up-to-date
```

No errors, no warnings (deprecated `HelpOutline` fixed to `Icons.AutoMirrored.Rounded.HelpOutline`).

---

## What Was Built

### Module Structure

```
feature/auth/
  build.gradle.kts                              (copy of feature:splash, namespace = com.hussein.jana.feature.auth)
  src/commonMain/kotlin/com/hussein/jana/feature/auth/
    AuthContract.kt         — MVI State, Intent, Effect
    AuthViewModel.kt        — single VM, handles all intents
    AuthScreen.kt           — AuthFlow (stateful) + AuthScreen (stateless)
    AuthEntryProvider.kt    — NavKey → NavEntry wiring for Login + Register
    components/
      AuthTextField.kt      — stateless labelled text field with leading/trailing slots
      PasswordField.kt      — builds on AuthTextField, adds lock icon + eye toggle + protected badge
      CountryCodeChip.kt    — MutedSand pill showing "EG +20 ▾"
      LoginTabSwitcher.kt   — ScholarlyTeal/transparent segmented control
      SocialLoginRow.kt     — two equal-width outlined buttons (Google / Facebook)
      AuthTopBar.kt         — student or faculty variant top bar
      StudentLoginContent.kt — full student login column
      TeacherLoginContent.kt — full teacher/faculty login column
      FacultyOnboardingCard.kt — SurfaceContainerLow card with call/WhatsApp CTAs
    di/
      AuthModule.kt         — Koin module: viewModel + single<FeatureEntryProvider>
```

### MVI Contract Summary

| Type | Members |
|---|---|
| `LoginState` | `activeTab`, `identifier`, `password`, `isPasswordVisible`, `rememberDevice`, `keepSession`, `isLoading`, `error` |
| `LoginIntent` | `TabSelected`, `IdentifierChanged`, `PasswordChanged`, `Toggle*` (3), `SignInClicked`, `GoogleSignInClicked`, `FacebookSignInClicked`, `ForgotPasswordClicked`, `RegisterClicked`, `InstitutionalSSOClicked`, `CallAdmissionsClicked`, `WhatsAppFacultyClicked` |
| `LoginEffect` | `NavigateTo*` (4), `Trigger*` (2), `OpenDialer`, `OpenWhatsApp`, `ShowSnackbar` |

### Key Design Decisions

1. **Single AuthViewModel**: Both student and teacher tabs share one VM and one `LoginState`. The active tab field drives which content composable renders. This keeps the state-hoisting model simple — `AuthScreen` is fully stateless, `AuthFlow` owns the VM.

2. **initialTab via NavKey → LaunchedEffect**: `AuthRoute.Login(initialTab = "TEACHER")` is a data class so the tab hint travels through the back-stack serialization. `AuthFlow` reads `initialTab` and fires `LoginIntent.TabSelected(TEACHER)` once via `LaunchedEffect(initialTab)`.

3. **Strings in `:core:designsystem`**: All 39 `auth_*` keys are in `core/designsystem/src/commonMain/composeResources/values/strings.xml` (English) and `values-ar/strings.xml` (Arabic). The designsystem module has `publicResClass = true`, so all feature modules that depend on it via `api()` can reference `Res.string.auth_*` directly.

4. **Routes.kt breaking change**: `AuthRoute.Login` was changed from `data object` to `data class Login(val initialTab: String = "STUDENT")`. The only callsite, `SplashEntryProvider.kt`, was updated from `AuthRoute.Login` to `AuthRoute.Login()`.

5. **ForgotPasswordRoute**: Added as `@Serializable data object ForgotPasswordRoute : NavKey` in Routes.kt. The `NavigateToForgotPassword` effect is dispatched correctly from the VM but handled as a no-op comment in `AuthFlow` — a future step can wire the route without touching auth.

6. **Social sign-in stubs**: `TriggerGoogleSignIn` and `TriggerFacebookSignIn` effects are defined and dispatched. `AuthFlow` handles them as comments for now — platform SDK integration is out of scope.

7. **Dialer / WhatsApp**: `OpenDialer("+20237498000")` and `OpenWhatsApp("+20237498000")` are defined and dispatched. No-op in `AuthFlow`; platform implementations should be added in `androidMain`/`iosMain` interceptors when ready.

### Existing Files Modified

| File | Change |
|---|---|
| `core/navigation/Routes.kt` | `AuthRoute.Login` → data class with `initialTab`; added `ForgotPasswordRoute` |
| `feature/splash/SplashEntryProvider.kt` | `AuthRoute.Login` → `AuthRoute.Login()` |
| `core/designsystem/values/strings.xml` | Added 39 `auth_*` English strings |
| `core/designsystem/values-ar/strings.xml` | Added 39 `auth_*` Arabic strings |
| `settings.gradle.kts` | Added `include(":feature:auth")` |
| `shared/build.gradle.kts` | Added `implementation(project(":feature:auth"))` |
| `shared/di/KoinInit.kt` | Imported `authModule`, added to `modules(...)` |

### Known Stubs / Future Work

- Social sign-in (Google, Facebook): effects fire but no SDK is wired
- ForgotPassword route: effect fires, navigation handler is a no-op comment
- Dialer / WhatsApp: effects fire but platform intents not wired
- Register screen: placeholder `LaunchedEffect` navigates straight to `HomeRoute.Root()`
