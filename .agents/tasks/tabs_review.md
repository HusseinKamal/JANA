# Platform Code Fields on Login/Register + 5-Tab Home Nav

This change adds teacher platform code capture to the Login and Register flows, adds an email/username field to Registration, and rebuilds the Home bottom navigation bar from five wrong tabs (HOME, TEACHERS, JANA_AI, SCHEDULE, PROGRESS) to the five specified tabs (Live Lessons, My Teacher Chat, JANA AI, Courses, Profile). The motivation is that students authenticate against a specific teacher's platform — not a global directory — so Login must carry a platform identifier, and the Home nav must reflect the actual feature set students have. The approach is a clean MVI state/intent extension: new state fields, new intent variants, new validation guards, and updated tests. Watch for: **platformCode is captured and validated on the client but is not forwarded to the backend stub call in `handleSignIn()`** — the actual network call remains `delay(1_000) → NavigateToHome` with `tokenRepository` suppressed as unused. Same gap on the Register path.

**Verdict**: APPROVED

---

## High-level view

The Login path gets `platformCode: String` added to `LoginState`, `PlatformCodeChanged` to `LoginIntent`, and a new guard at the top of `handleSignIn()` that short-circuits with the platform error before checking identifier/password. The UI field uses the design-system `AuthTextField` with a `School` icon and ships with both English and Arabic string keys.

The Register path gets an equivalent platform code field plus an email/username field, both added to `RegisterState` and `RegisterIntent`, wired through `PersonalCredentialsCard.kt`. The email validation is intentionally permissive — it accepts any string containing "@" regardless of format, and any string ≥ 3 characters as a username fallback — because the backend is the authoritative validator per the spec. This means strings like "@" or "a@" pass client-side validation.

The `HomeTab` enum is replaced wholesale; the four stub screens follow the established `ProgressTabScreen` pattern. The `indicatorColor` moves from the hardcoded `ScholarlyTeal` to `MaterialTheme.colorScheme.primary`, removing the last color hardcode in that component. No teacher discovery surface appears anywhere in the new nav — `MyTeacherChatTabScreen` is a conversation stub scoped to the active teacher session.

Both `platformCode` and `email` are collected and validated in state but are not forwarded when the stub backend call fires. The `tokenRepository` remains `@Suppress("unused")` in both ViewModels. Any non-blank platform code satisfies the validation and produces a successful login. The backend wiring is a known open gap.

---

<details>
<summary>Issues (3)</summary>

1. **platformCode not forwarded to login stub** — `AuthViewModel.handleSignIn()` runs `delay(1_000)` and emits `NavigateToHome` without passing `platformCode` (or any credential) to `tokenRepository`. Wire `platformCode` and credentials into the actual login API call when backend integration is added.

2. **email and platformCode not sent to register stub** — `RegisterViewModel.handleRegister()` has the same gap: both new fields are validated and then dropped. Wire them into the register API request.

3. **Email validation accepts structurally invalid email strings** — `!s.email.contains("@") && s.email.length < 3` lets `"@"`, `"a@"`, and `"@domain.com"` pass without error because they all contain `"@"`. Confirm this permissiveness is intentional for the username-fallback case; if strict email format is required when `"@"` is present, add a stricter check before backend integration.

</details>

<details>
<summary>Details</summary>

### platformCode stub gap in Login

`handleSignIn()` after the platform code guard runs:

```kotlin
viewModelScope.launch {
    delay(1_000)
    setState { copy(isLoading = false) }
    sendEffect(LoginEffect.NavigateToHome)
}
```

The collected `platformCode` is never referenced inside that lambda. `tokenRepository` is `@Suppress("unused")`. Until the stub is replaced with a real API call, any non-blank platform code satisfies the validation and produces a successful login. Same pattern in `RegisterViewModel.handleRegister()` — `platformCode` and `email` are both dropped after the validation guard.

### Email validation edge case in Register

The validator `!s.email.contains("@") && s.email.length < 3` is permissive by design for the username fallback case. Structurally invalid emails like `"@"` (contains `"@"`, length 1) pass the check because the `contains("@")` branch fires. This is acceptable as long as the backend enforces format, but needs confirmation before API integration: does the backend also accept `"@"` as a valid email credential?

### Chat icon mirrors correctly for RTL

`Icons.AutoMirrored.Rounded.Chat` is the correct choice — it flips horizontally under RTL. The other four icons (Videocam, AutoAwesome, MenuBook, Person) are symmetric and don't require mirroring.

### Test coverage gaps

There is no test verifying that `platformCode` is forwarded in the sign-in request (because there is no real request yet), and no test for the email validation edge cases (`"@"`, `"a@"`). The existing suite covers the validation guard paths and the happy path, which is sufficient for the current stub architecture.

</details>

---

<details>
<summary>File map</summary>

| File | Change |
|------|--------|
| `core/designsystem/…/values/strings.xml` | +14 string keys: auth platform code, register email/platform, 4 tab labels |
| `core/designsystem/…/values-ar/strings.xml` | Same 14 keys in Arabic |
| `feature/auth/…/AuthContract.kt` | `platformCode` field + `PlatformCodeChanged` intent + `errorMissingPlatformCode` in `AuthStrings` |
| `feature/auth/…/AuthViewModel.kt` | `PlatformCodeChanged` handler; blank platform code guard in `handleSignIn()` |
| `feature/auth/…/AuthScreen.kt` | Resolves and injects `auth_error_missing_platform_code` into ViewModel |
| `feature/auth/…/components/StudentLoginContent.kt` | `School`-icon platform code `AuthTextField` added at top of form |
| `feature/auth/…/register/RegisterContract.kt` | `platformCode`, `email` fields + errors in state; two new intents; three new error strings |
| `feature/auth/…/register/RegisterViewModel.kt` | `PlatformCodeChanged` / `EmailChanged` handlers; both fields validated in `handleRegister()` guard |
| `feature/auth/…/register/RegisterScreen.kt` | Resolves and injects three new error strings into ViewModel |
| `feature/auth/…/register/components/PersonalCredentialsCard.kt` | `School`-icon platform code and `AlternateEmail`-icon email fields added at top of card |
| `feature/home/…/HomeContract.kt` | `HomeTab` enum replaced; default tab = `LIVE_LESSONS` |
| `feature/home/…/HomeScreen.kt` | 5-tab nav rewritten; `indicatorColor` → `primary`; content `when` updated; old string imports removed |
| `feature/home/…/tabs/live/LiveLessonsTabScreen.kt` | New stub composable |
| `feature/home/…/tabs/chat/MyTeacherChatTabScreen.kt` | New stub composable |
| `feature/home/…/tabs/courses/CoursesTabScreen.kt` | New stub composable |
| `feature/home/…/tabs/profile/ProfileTabScreen.kt` | New stub composable |
| `feature/home/…/HomeViewModelTest.kt` | All old enum values updated to new enum values |
| `feature/auth/…/AuthViewModelTest.kt` | `PlatformCodeChanged` added to success paths; new blank-platform test added |
| `feature/auth/…/register/RegisterViewModelTest.kt` | New platform code and email negative tests; all existing tests updated with new required fields |

Full diff base: commit `cc6ecb5` (origin/develop)

</details>
