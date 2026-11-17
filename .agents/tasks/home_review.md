# JANA Student Home Screen — Findings Remediation Pass

This pass re-reviews the second commit on the home screen feature (`fix: address all 8 home-screen review findings`). All 8 blocking findings from the first review have been addressed. The changes touch `core/designsystem/JanaTopBar.kt`, the full `HomeTabContract/ViewModel/Screen` stack, every component that previously rendered a hardcoded English string, and two new test files in `src/jvmTest`. Build evidence is present for all three targets (`compileKotlinJvm` and `jvmTest` both report SUCCESS).

Watch for: **possible** dropped snackbar message if two intents emit effects within a single composition frame — a consequence of the two-step `pendingSnackbarRes` staging pattern. Non-triggerable in this user-action-driven UI but worth knowing before the real data layer arrives.

**Verdict**: APPROVED

---

## High-level view

All eight original findings are confirmed fixed. `HomeTabViewModel` accepts an injectable `currentHour` parameter and selects the morning/afternoon/evening resource in `init`; eight boundary tests cover every branch. All previously hardcoded English display strings — subject names, trend blurbs, day labels, live-session fields, the AI message default, the highlight phrases, and all seven snackbar messages — are now `StringResource` fields with matching en/ar entries. `HomeViewModelTest` (7 tests) and `HomeTabViewModelTest` (18 tests) bring coverage to par with the auth feature.

`HomeTabState.credits` was removed; `HomeTabScreen` now takes `credits: Int` threaded from `HomeScreen`, making `HomeState.credits` the single source of truth.

The snackbar locale solution — `LaunchedEffect(viewModel)` stages an incoming `StringResource` into `pendingSnackbarRes: mutableStateOf`, a second `LaunchedEffect(resolvedSnackbar)` resolves the string in composable scope and calls `showSnackbar` — is correct for the common case. A rapid-fire double intent before recomposition runs overwrites the slot and drops the first message silently (see Details).

---

<details>
<summary>Issues (1)</summary>

1. **Possible dropped snackbar on rapid-fire intents** — `pendingSnackbarRes` is a single `mutableStateOf` slot; if two effects arrive before recomposition runs, the second assignment overwrites the first and the first message is never shown. Non-triggerable with single-user tap interaction but could manifest with programmatic intent emission. Consider replacing the single-slot staging with a `Channel<StringResource>(BUFFERED)` collected directly into `snackbarHostState` via a resolved string in a `produceState`-style helper.

</details>

---

<details>
<summary>Details</summary>

### Two-step snackbar staging and the dropped-message edge case

`pendingSnackbarRes` is a single `mutableStateOf` slot. Compose may batch multiple state writes before the next frame; if two effects arrive before recomposition runs, the second assignment overwrites the first and `resolvedSnackbar` never takes the first value. The first message is silently dropped. With the current single-tap UX this is **possible** but not reachable in practice. When a real data layer begins emitting effects programmatically, replace the slot with a `Channel<StringResource>(BUFFERED)` and collect directly into `snackbarHostState` after resolving the string with a `produceState`-style helper.

The 90dp `SnackbarHost` bottom padding that clears `CreditsBanner` is unchanged from the first commit. At large font scale the banner may grow past 90dp and the snackbar will overlap it (**possible**, pre-existing).

</details>

---

<details>
<summary>File map</summary>

| File | Change |
|------|--------|
| `core/designsystem/JanaTopBar.kt` | Back-arrow `contentDescription` → `stringResource(Res.string.nav_back)` |
| `core/designsystem/values/strings.xml` | 21 new home_snack_*, home_subject_*, home_trend_*, home_day_*, home_live_*, home_ai_* keys |
| `core/designsystem/values-ar/strings.xml` | Same 21 keys in Arabic |
| `feature/home/HomeScreen.kt` | Pass `credits = state.credits` to `HomeTabScreen` |
| `feature/home/tabs/home/HomeTabContract.kt` | Full rewrite — StringResource fields, removed credits, `ShowSnackbar(messageRes)` |
| `feature/home/tabs/home/HomeTabViewModel.kt` | `currentHour` injection, time-of-day greeting init, `Res.string.home_snack_*` effects |
| `feature/home/tabs/home/HomeTabScreen.kt` | `credits: Int` param, two-step snackbar resolution, `stringResource` calls in composable scope |
| `feature/home/tabs/home/components/GreetingHeader.kt` | `greetingRes: StringResource` param |
| `feature/home/tabs/home/components/AiCompanionCard.kt` | `highlightPhrases: List<String>` param, removed hardcoded `KEY_PHRASES` |
| `feature/home/tabs/home/components/SubjectProgressRow.kt` | `stringResource(nameRes)` and `trendRes` |
| `feature/home/tabs/home/components/LiveSessionCard.kt` | `stringResource(subjectRes/levelRes)` |
| `feature/home/tabs/home/components/UpcomingSessionsSection.kt` | `stringResource(dayLabelRes)` |
| `feature/home/build.gradle.kts` | `kotlinx.datetime` dep + `jvmTest.dependencies` block |
| `gradle/libs.versions.toml` | `kotlinx-datetime = "0.6.0"` |
| `src/jvmTest/…/HomeViewModelTest.kt` | New — 7 tests |
| `src/jvmTest/…/tabs/home/HomeTabViewModelTest.kt` | New — 18 tests |

Full diff: `git diff HEAD~1`

</details>
