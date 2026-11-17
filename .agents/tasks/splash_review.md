# JANA Splash Screen — Iteration 2 Fixes

All five findings from the prior pass have been addressed. The broken test is now correct, `SignInClicked` is covered, `app_name` reads through the resource system, `SplashFlow` properly separates the two auth routes, and `app_version` carries a maintenance comment in the AR file. One new XML defect was introduced in the process, and two pre-existing structural issues in the MVI contract surface for the first time now that the more urgent findings are cleared.

**Watch for:** A stray `-` character was introduced at the end of the `password_label` line in the EN `strings.xml` — confirmed by `Select-String` on the file. The XML parses through the Compose Resources toolchain (build passes), but the character is an editorial defect that shouldn't ship. The `isLoading` field in `SplashState` starts `true`, is never set to `false` after the 2s timer, and is never observed anywhere in `SplashFlow` or `SplashContent` — it is a dead contract slot.

**Verdict**: CHANGES_REQUESTED

---

## High-level view

All prior findings are verified fixed. Test suite passes with 4 tests, 0 failures — both the guest-stays behavior and the `SignInClicked → NavigateToSignIn` path are now regression-tested. `SplashFlow` correctly routes `NavigateToAuth → AuthRoute.Register` and `NavigateToSignIn → AuthRoute.Login` through distinct callbacks, and `SplashContent` reads `app_name` through `stringResource`.

The EN `strings.xml` has a stray `-` appended after the `password_label` closing tag. It survived the build because most XML parsers treat PCDATA between sibling resource elements as ignorable content, but it is an accidental character that makes the file malformed and should be removed.

`SplashState.isLoading` is initialized to `true` in the contract but never transitioned to `false` and never observed by any composable. The splash screen renders unconditionally regardless of this field. Either the loading state should drive a visual affordance during the 2s delay, or the field should be removed from the contract to avoid a misleading promise.

The app_version release-bump comment exists in the AR file but not in the EN file, contradicting the impl note's claim that it was added to both.

---

<details>
<summary>Issues (3)</summary>

1. **Stray `-` in EN strings.xml** — `password_label` line ends with `</string>-`; the trailing character is extraneous and makes the file formally malformed. Remove the `-`.
2. **`isLoading` dead contract field** — `SplashState.isLoading` is `true` on construction, never updated after the 2s delay, and not observed anywhere. Either drive a loading affordance from it or remove the field.
3. **app_version comment missing from EN file** — the release-bump reminder comment was added to `values-ar/strings.xml` but is absent from `values/strings.xml`, inconsistent with the impl note.

</details>

<details>
<summary>Details</summary>

### Prior findings — build evidence

`feature/splash/build/test-results/jvmTest/TEST-com.hussein.jana.feature.splash.SplashViewModelTest.xml` records 4 tests, 0 failures, 0 skipped against commit `d77a66c`. All five prior findings are confirmed fixed through direct code inspection.

### Stray character in EN strings.xml

`Select-String` on the live file returns:

```
    <string name="password_label">Password</string>-
```

The `-` appears after the element's closing tag and before the newline. The git diff confirms it was introduced in this change (`+    <string name="password_label">Password</string>-`). The Compose Resources toolchain and Android resource parser both pass on it because PCDATA between sibling elements is legal in XML and typically discarded, but the character is an accidental edit artifact that does not belong in a resource file.

### Dead `isLoading` state slot

`SplashState(val isLoading: Boolean = true)` is defined in the contract and initialized to `true`. The ViewModel never emits a state update:

```kotlin
init {
    viewModelScope.launch {
        delay(2_000)
        if (tokenRepository.isLoggedIn()) {
            sendEffect(SplashEffect.NavigateToHome)
        }
        // no updateState { copy(isLoading = false) }
    }
}
```

`SplashFlow` never subscribes to `viewModel.state`. `SplashContent` renders unconditionally. The field serves no purpose in the current implementation. Either use it to show a loading indicator during the 2s wait (the most natural read of `isLoading = true` as an initial value) or remove it to keep the MVI contract honest.

### app_version comment parity

The AR file has:

```xml
<!-- NOTE: app_version must be bumped manually on each release; it is a shared Compose Resource
     string so it can be read from common KMP code without platform-specific BuildConfig boilerplate. -->
<string name="app_version">v2.4.0</string>
```

The EN file does not. The impl note states the comment was added to both locale files.

</details>

---

<details>
<summary>File map</summary>

| File | Change |
|------|--------|
| `core/designsystem/.../values/strings.xml` | Added JANA splash strings, reorganised into sections, `app_name_arabic`/`app_version` added; trailing `-` introduced on `password_label` line |
| `core/designsystem/.../values-ar/strings.xml` | Arabic translations matching all EN keys; `app_version` comment added |
| `core/designsystem/.../JanaLogoCard.kt` | New Canvas composable: open-book brand icon with radial glow, broadcast arcs, sparkle stars, LIVE badge |
| `feature/splash/.../SplashContract.kt` | `SplashState` with `isLoading`, `SignInClicked` intent, `NavigateToSignIn` effect |
| `feature/splash/.../SplashViewModel.kt` | 2s init-block timer, logged-in users → `NavigateToHome`, guests stay; `SignInClicked → NavigateToSignIn` handler |
| `feature/splash/.../SplashScreen.kt` | `SplashFlow` gains `onNavigateToSignIn` callback; all three effect branches mapped distinctly |
| `feature/splash/.../SplashEntryProvider.kt` | `NavigateToAuth → AuthRoute.Register`, `NavigateToSignIn → AuthRoute.Login` |
| `feature/splash/.../SplashContent.kt` | VellumCanvas background, 9-element JANA education layout, `JanaLogoCard` integration, `app_name` via `stringResource` |
| `feature/splash/src/jvmTest/.../SplashViewModelTest.kt` | Guest-stays test corrected; `SignInClicked → NavigateToSignIn` test added; 4/4 pass |

Full diff: `git diff origin/master HEAD`

</details>
