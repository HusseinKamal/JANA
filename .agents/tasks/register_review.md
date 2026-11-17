# Student Registration Screen (Step 1 of 2)

The change delivers a full student registration screen wired into the existing MVI + Koin + Navigation 3
architecture. It introduces `RegisterContract` (state, intent, effect, `PasswordStrength`), `RegisterViewModel`
with real-time strength scoring and click-time field validation, a stateful/stateless `RegisterFlow`/`RegisterScreen`
pair, four sub-components (`RegisterTopBar`, `PersonalCredentialsCard`, `AccountSecurityCard`,
`PasswordStrengthIndicator`, `PhoneField`), DI registration in `AuthModule`, and route wiring in
`AuthEntryProvider`. All 30 user-visible strings are present in both `values/` (English) and `values-ar/`
(Arabic). This is the second iteration; the first-pass findings (PasswordField duplication, hardcoded
`"Back"` contentDescription, duplicate toggle strings) were all resolved.

Watch for: submit-time password validation enforces only two of the three stated criteria (length and
uppercase — confirmed); the symbol/digit requirement is shown in the strength indicator but not gated at
click time. Also: `PersonalCredentialsCard` section header uses a Lock icon instead of Person (confirmed,
cosmetic); phone validation threshold (7 chars) is likely too low for the EG +20 context.

**Verdict**: APPROVED

---

## High-level view

The strength indicator is purely informational with respect to submit-time gating. The UI shows three
criteria (10+ chars, uppercase, symbol/digit), but `handleRegister()` only blocks on the first two.
A MEDIUM password — 10 chars and uppercase, no symbol/digit — passes validation. This is consistent with
the specified error-string set (`errorPasswordTooShort` / `errorPasswordNoUppercase` only), so it appears
intentional, but it creates a visible asymmetry: the requirements hint text advertises three rules while
only two are enforced.

The 22-test suite covers all validation branches and strength transitions. The MEDIUM-password submit path
(10+ chars + uppercase, no symbol/digit) is the one scenario not covered, leaving the gap above untested.

---

<details>
<summary>Issues (3)</summary>

1. **Symbol/digit criterion not gated at submit** — `handleRegister()` blocks on `password.length < 10`
   and `!passwordHasUpperCase` but not on `!passwordHasSymbol`. A MEDIUM password (10 chars + uppercase,
   no symbol/digit) passes. Either add an `errorPasswordNoSymbol` check, or update
   `register_password_requirements` to reflect only the two enforced rules.

2. **Phone validation threshold too low** — the click-time check blocks on `phone.length < 7`. The EG +20
   chip implies Egyptian numbers (10 digits minimum). A 7-character input like `"1234567"` passes
   validation; consider raising the threshold to at least 10.

3. **Lock icon on Personal Credentials header** — `PersonalCredentialsCard` uses `Icons.Rounded.Lock`
   for its section header, the same icon as the Account Security card. `Icons.Rounded.Person` (already
   imported for the name-field leading icon) would better identify this section.

</details>

<details>
<summary>Details</summary>

## Submit-time vs indicator-time password contract

`updatePassword()` scores strength across three boolean flags:

```
hasMinLength = pw.length >= 10
hasUpperCase = pw.any { it.isUpperCase() }
hasSymbol    = pw.any { !it.isLetterOrDigit() } || pw.any { it.isDigit() }
```

`trueCount` determines WEAK / MEDIUM / STRONG. The strength bar and requirements label surface all three
to the user. `handleRegister()`, however, only blocks on `password.length < 10` and `!passwordHasUpperCase`.
A password like `"ABCDEFGHIJ"` (10 uppercase letters, no digit/symbol) scores MEDIUM and passes click
validation. Whether STRONG is required to submit is unspecified in the error-string contract, but the
inconsistency between indicator and gate is user-visible and needs to be resolved in either direction:
add an `errorPasswordNoSymbol` check, or update the requirements hint to say "10+ chars & uppercase."

## PhoneField validation threshold

The click-time phone check blocks on `phone.length < 7`. The EG +20 chip implies Egyptian numbers, where
the subscriber number is 10 digits. `"1234567"` (7 chars) passes. The threshold should match the
expected format — 10 digits for Egypt — or at minimum be raised to 9 to exclude implausible short inputs.

## Component icon in PersonalCredentialsCard

The section header row uses `Icons.Rounded.Lock` — the same icon as the Account Security section. The
imported `Icons.Rounded.Person` is only used for the name field's `leadingContent`. Swapping the header
icon to `Icons.Rounded.Person` (or `AccountCircle`) would correctly differentiate the two cards visually.

## Test coverage

Not tested: the MEDIUM password submit path (10+ chars + uppercase, no symbol/digit) is not covered by
any validation test, so the gap described above has no regression protection.

</details>

---

<details>
<summary>File map</summary>

| File | What changed |
|------|-------------|
| `feature/auth/…/register/RegisterContract.kt` | New: `RegisterStrings`, `PasswordStrength`, `RegisterState`, `RegisterIntent`, `RegisterEffect` |
| `feature/auth/…/register/RegisterViewModel.kt` | New: MVI ViewModel with real-time strength scoring and click-time validation |
| `feature/auth/…/register/RegisterScreen.kt` | New: `RegisterFlow` (stateful) + `RegisterScreen` (stateless) with sub-composables |
| `feature/auth/…/register/components/RegisterTopBar.kt` | New: back arrow + logo + app-name, `statusBarsPadding`, localized `nav_back` |
| `feature/auth/…/register/components/PersonalCredentialsCard.kt` | New: Full Legal Name (`AuthTextField`) + `PhoneField` in `JanaCard` |
| `feature/auth/…/register/components/AccountSecurityCard.kt` | New: two `PasswordField` usages (Key / Sync icons) + `PasswordStrengthIndicator` |
| `feature/auth/…/register/components/PasswordStrengthIndicator.kt` | New: strength badge pill + requirements hint + three-segment bar |
| `feature/auth/…/register/components/PhoneField.kt` | New: `AuthTextField` wrapper with EG +20 country-code chip, phone keyboard |
| `feature/auth/…/components/PasswordField.kt` | Added nullable `leadingIcon` parameter; default falls back to Lock icon |
| `feature/auth/…/di/AuthModule.kt` | Added `viewModel { RegisterViewModel(tokenRepository = get()) }` |
| `feature/auth/…/AuthEntryProvider.kt` | Replaced `Register` placeholder with `RegisterFlow`; removed unused `LaunchedEffect` import |
| `core/designsystem/…/values/strings.xml` | 30 new keys: `nav_back` + 29 registration/error strings |
| `core/designsystem/…/values-ar/strings.xml` | Same 30 keys in Arabic |
| `feature/auth/…/register/RegisterViewModelTest.kt` | New: 22 unit tests |

Full diff: `git diff origin/develop HEAD`

</details>
