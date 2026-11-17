# JANA Student Home Screen — Review Findings Fixed

This document describes all 8 findings from `home_review.json` that were addressed in this iteration.

---

## Build Commands Run

```
.\gradlew :feature:home:compileKotlinJvm   → BUILD SUCCESSFUL
.\gradlew :feature:home:jvmTest            → BUILD SUCCESSFUL (all tests pass)
.\gradlew :composeApp:compileKotlinJvm     → BUILD SUCCESSFUL
```

---

## Finding 1 — Back-arrow contentDescription hardcoded

**File:** `core/designsystem/src/commonMain/kotlin/com/hussein/jana/core/designsystem/JanaTopBar.kt`  
**Change:** `contentDescription = "Back"` → `contentDescription = stringResource(Res.string.nav_back)`  
The `nav_back` key already existed in both locale files (`Back` / `رجوع`), so no string additions were needed.

---

## Finding 2 — Greeting always "morning"

**Files changed:**
- `feature/home/.../tabs/home/HomeTabContract.kt` — added `greetingRes: StringResource = Res.string.home_greeting_morning` to `HomeTabState`
- `feature/home/.../HomeTabViewModel.kt` — added `currentHour: Int = computeCurrentHour()` constructor param; `init {}` selects the right `StringResource` (morning/afternoon/evening) based on the hour
- `feature/home/.../components/GreetingHeader.kt` — now accepts `greetingRes: StringResource` and resolves it internally with `stringResource(greetingRes)`
- `feature/home/.../tabs/home/HomeTabScreen.kt` — passes `state.greetingRes` to `GreetingHeader`

**New dependency:** `kotlinx-datetime = "0.6.0"` added to `libs.versions.toml` and `feature/home/build.gradle.kts`. Used only in `computeCurrentHour()` via `Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).hour`. The `currentHour` parameter is injectable for testing (see finding 8).

---

## Finding 3 — Subject display strings not localized

**Strategy:** Changed data-class field types from `String` to `StringResource` for all localizable labels. Content data (course titles, teacher names) remain as `String` since they come from an API in production.

**Data-class changes in `HomeTabContract.kt`:**
| Old field | New field |
|---|---|
| `SubjectMastery.nameKey: String` | `nameRes: StringResource` |
| `SubjectMastery.trend: String` | `trendRes: StringResource?` |
| `LiveSessionData.subject: String` | `subjectRes: StringResource` |
| `LiveSessionData.level: String` | `levelRes: StringResource` |
| `UpcomingSessionData.dayLabel: String` | `dayLabelRes: StringResource` |

**Component changes:**
- `SubjectProgressRow.kt` — calls `stringResource(subject.nameRes)` and `subject.trendRes?.let { stringResource(it) }`
- `LiveSessionCard.kt` — calls `stringResource(session.subjectRes)` and `stringResource(session.levelRes)`
- `UpcomingSessionsSection.kt` — calls `stringResource(session.dayLabelRes)`

**New string keys added (en + ar):**
`home_subject_algebra`, `home_subject_geometry`, `home_subject_physics`,
`home_trend_algebra`, `home_trend_geometry`, `home_trend_physics`,
`home_day_tomorrow`, `home_day_thursday`,
`home_live_subject_math`, `home_live_level_senior`

---

## Finding 4 — aiMessage default is hardcoded English

**File:** `HomeTabContract.kt`  
**Change:** `aiMessage: String` field replaced with `aiMessageRes: StringResource = Res.string.home_ai_message_default`  
**New string key:** `home_ai_message_default` in both locale files.  
The composable resolves it in `HomeTabScreen.HomeTabContent` via `val aiMessage = stringResource(state.aiMessageRes)` before passing to `AiCompanionCard`.

---

## Finding 5 — Snackbar messages hardcoded in ViewModel

**Files changed:**
- `HomeTabContract.kt` — `HomeTabEffect.ShowSnackbar(message: String)` → `ShowSnackbar(messageRes: StringResource)`
- `HomeTabViewModel.kt` — all 7 `sendEffect(HomeTabEffect.ShowSnackbar("…"))` calls now use `Res.string.home_snack_*` keys
- `HomeTabScreen.kt` — cannot call `stringResource()` inside a `suspend` lambda. Used the two-step pattern:
  1. `LaunchedEffect(viewModel)` stores incoming `messageRes` into a `var pendingSnackbarRes by remember { mutableStateOf<StringResource?>(null) }`
  2. A second `LaunchedEffect(resolvedSnackbar)` runs in composable scope, resolves the string, shows the snackbar, then clears `pendingSnackbarRes`

**New string keys:** `home_snack_joining`, `home_snack_quiz`, `home_snack_formula`, `home_snack_breakdown`, `home_snack_calendar`, `home_snack_refill`, `home_snack_notifications` (en + ar).

---

## Finding 6 — KEY_PHRASES never matches Arabic

**Files changed:**
- `HomeTabContract.kt` — added `aiHighlightPhrases: List<StringResource>` to `HomeTabState`, defaulting to `listOf(Res.string.home_ai_phrase_discriminant, Res.string.home_ai_phrase_polynomial)`
- `AiCompanionCard.kt` — the hardcoded `private val KEY_PHRASES` list removed; replaced with `highlightPhrases: List<String>` parameter. The phrases are sourced from the same locale as the message, so Arabic phrases highlight Arabic text
- `HomeTabScreen.kt` — resolves `state.aiHighlightPhrases.map { stringResource(it) }` in composable scope before passing to `AiCompanionCard`

**New string keys:** `home_ai_phrase_discriminant`, `home_ai_phrase_polynomial` (en + ar).

---

## Finding 7 — Duplicate credits state

`HomeTabState.credits: Int` removed. `HomeTabScreen` now takes `credits: Int` as a constructor parameter (single source of truth from `HomeState.credits`).

**Files changed:**
- `HomeTabContract.kt` — removed `credits` field
- `HomeTabScreen.kt` — `fun HomeTabScreen(credits: Int)`, passes it to `CreditsBanner` and `HomeTabContent`
- `HomeScreen.kt` — `HomeTabScreen(credits = state.credits)`

---

## Finding 8 — No ViewModel tests

**New test source set declared in `feature/home/build.gradle.kts`:**
```kotlin
jvmTest.dependencies {
    implementation(libs.kotlin.testJunit)
    implementation(libs.kotlinx.coroutines.test)
}
```

**New test files:**
- `src/jvmTest/…/HomeViewModelTest.kt` — 7 tests: initial state, all 5 tab-selection intents
- `src/jvmTest/…/tabs/home/HomeTabViewModelTest.kt` — 18 tests: 8 greeting time-of-day boundary tests, 3 initial-state tests, 7 intent→effect tests

Assertions use `StringResource.key` (the resource name string, e.g. `"home_greeting_morning"`) rather than generated extension-property references, which are not on the jvmTest classpath.

---

## File Map (this iteration)

| File | Change |
|------|--------|
| `gradle/libs.versions.toml` | Added `kotlinx-datetime = "0.6.0"` |
| `feature/home/build.gradle.kts` | Added `kotlinx.datetime` dep + `jvmTest.dependencies` block |
| `core/designsystem/JanaTopBar.kt` | Fixed back-arrow `contentDescription` to use `nav_back` string resource |
| `core/designsystem/values/strings.xml` | Added 21 new home_* keys |
| `core/designsystem/values-ar/strings.xml` | Added 21 new home_* Arabic keys |
| `feature/home/HomeContract.kt` | Unchanged |
| `feature/home/HomeScreen.kt` | Pass `credits = state.credits` to `HomeTabScreen` |
| `feature/home/HomeViewModel.kt` | Unchanged |
| `feature/home/HomeEntryProvider.kt` | Unchanged |
| `feature/home/di/HomeModule.kt` | Unchanged |
| `feature/home/tabs/home/HomeTabContract.kt` | Full rewrite — StringResource types, removed credits, ShowSnackbar(messageRes) |
| `feature/home/tabs/home/HomeTabViewModel.kt` | Full rewrite — kotlinx-datetime greeting, Res.string snackbar effects |
| `feature/home/tabs/home/HomeTabScreen.kt` | Full rewrite — credits param, two-step snackbar resolution, resolve StringResources |
| `feature/home/tabs/home/components/GreetingHeader.kt` | Accept `greetingRes: StringResource` param |
| `feature/home/tabs/home/components/AiCompanionCard.kt` | Accept `highlightPhrases: List<String>` param |
| `feature/home/tabs/home/components/SubjectProgressRow.kt` | Use `stringResource(nameRes)` and `trendRes` |
| `feature/home/tabs/home/components/LiveSessionCard.kt` | Use `stringResource(subjectRes/levelRes)` |
| `feature/home/tabs/home/components/UpcomingSessionsSection.kt` | Use `stringResource(dayLabelRes)` |
| `src/jvmTest/…/HomeViewModelTest.kt` | New — 7 tests |
| `src/jvmTest/…/tabs/home/HomeTabViewModelTest.kt` | New — 18 tests |
