# Schedule tab: placeholder stub, not a full implementation

The PR adds a single 27-line `ScheduleTabScreen` that renders a centered "Schedule Coming Soon" text and nothing else. `HomeScreen` routes `HomeTab.SCHEDULE` to this stub and falls into an `else` branch in the top-bar title switch (showing `home_student_label` instead of a schedule title). The `HomeModule` registers no `ScheduleTabViewModel`. No MVI contract, no components, no week-strip widget, no domain string resources, and no schedule-specific tests exist anywhere in the working tree.

The original request explicitly mirrors the AI chat and Teachers implementations: MVI contract, ViewModel, screen, componentized sub-sections, shared strings in both English and Arabic, and 15+ ViewModel tests. None of those were delivered.

**Watch for:** confirmed — the entire implementation is absent. Every required artifact is missing. This is not a partial implementation; it is a placeholder stub shipped as a complete feature.

**Verdict**: NEEDS_CHANGES

---

## High-level view

The only new schedule artifact is `ScheduleTabScreen`, a one-composable file that shows a coming-soon string. `HomeScreen` wires the tab entry-point but the top-bar title for the SCHEDULE case silently falls through to the generic student label because there is no branch for `HomeTab.SCHEDULE` in the `when` expression — the `else` arm covers it. The `HomeModule` DI wiring adds `HomeTabViewModel`, `TeachersTabViewModel`, and `AiChatViewModel` but no `ScheduleTabViewModel`, so any real ViewModel would fail to inject at runtime.

The spec's string resource requirement is met only at the navigation level (`nav_schedule`, `home_schedule_coming_soon`). The domain-level strings required for session cards, week sections, the credits banner, and the calendar integration card were never added in either locale.

There are zero `ScheduleTabViewModel` tests. The single SCHEDULE-related test in `HomeViewModelTest` checks that `TabSelected(HomeTab.SCHEDULE)` updates `selectedTab` on the host ViewModel — this is a tab-routing test, not a schedule-feature test, and counts for none of the 15 required cases.

---

<details>
<summary>Issues (8)</summary>

1. **ScheduleTabScreen is a stub** — The screen contains only a "coming soon" `Text`. The full screen (ScheduleHeaderSection, ScheduleWeekSection, ScheduleCreditsBanner, SessionCard, DaySectionHeader, CalendarIntegrationCard) was not implemented. Implement all required components per spec.

2. **No MVI contract** — `ScheduleTabContract.kt` (state, intent, effect types) does not exist. Without it there is no behavioral contract for the screen and no surface to test against. Add the contract before building the screen.

3. **No ScheduleTabViewModel** — No ViewModel exists for the schedule tab. The `HomeModule` does not register one. Any Koin injection attempt at runtime would crash. Create and register `ScheduleTabViewModel`.

4. **JanaWeekStrip missing from core:designsystem** — The week-strip widget required by the spec is absent from the design system entirely. It needs to be added as a shared composable before `ScheduleWeekSection` can use it.

5. **Top-bar title wrong for SCHEDULE tab** — `HomeScreen` has `else -> stringResource(Res.string.home_student_label)` as the catch-all; `HomeTab.SCHEDULE` hits this branch and displays the wrong title. Add an explicit `HomeTab.SCHEDULE -> stringResource(Res.string.nav_schedule)` branch (or the correct schedule-specific string).

6. **Domain string resources missing** — Only `nav_schedule` and `home_schedule_coming_soon` are defined. All domain strings for the schedule UI (week labels, session-card fields, credits banner copy, calendar integration prompts, etc.) are absent from both `values/strings.xml` and `values-ar/strings.xml`. Add the full `schedule_*` string set in both locales.

7. **ScheduleStrings LaunchedEffect injection absent** — The pattern used in `JanaAiTabScreen` to inject a string bundle into the ViewModel via `LaunchedEffect` is not present. Without it, any ViewModel-driven string logic cannot work. Implement once the ViewModel and strings exist.

8. **Zero ScheduleTabViewModel tests** — The spec requires 15+ tests covering schedule-specific cases. No test file for `ScheduleTabViewModel` exists. The single SCHEDULE assertion in `HomeViewModelTest` is a tab-routing test and does not substitute. Add a dedicated `ScheduleTabViewModelTest` file with the required coverage.

</details>

---

<details>
<summary>Details</summary>

### Placeholder screen shipped as complete feature

`ScheduleTabScreen` is exactly:

```kotlin
@Composable
fun ScheduleTabScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(
            text = stringResource(Res.string.home_schedule_coming_soon),
            style = MaterialTheme.typography.headlineSmall,
            color = OnSurfaceVariant,
        )
    }
}
```

None of the specified components — `ScheduleHeaderSection`, `ScheduleCreditsBanner`, `ScheduleWeekSection`, `SessionCard`, `DaySectionHeader`, `CalendarIntegrationCard` — exist in the working tree. `JanaWeekStrip` is also absent from `core:designsystem`. Searching the entire repo for any of these identifiers returns no matches. This is confirmed.

### Wrong top-bar title for SCHEDULE tab

The `when` block in `HomeScreen` reads:

```kotlin
val topBarTitle = when (state.selectedTab) {
    HomeTab.TEACHERS -> stringResource(Res.string.teachers_title)
    HomeTab.JANA_AI  -> stringResource(Res.string.nav_jana_ai)
    else             -> stringResource(Res.string.home_student_label)
}
```

`HomeTab.SCHEDULE` and `HomeTab.PROGRESS` both fall into `else` and show the generic student label. For the AI chat tab, the previous review cycle established that an explicit branch was needed; the same gap was recreated here for SCHEDULE. This is confirmed by reading the diff.

### MVI structure entirely absent

The other implemented tabs each have a `*Contract.kt`, `*ViewModel.kt`, and a componentized screen. The schedule tab has none of these. `HomeModule` registers `HomeTabViewModel`, `TeachersTabViewModel`, and `AiChatViewModel` — `ScheduleTabViewModel` is not in the list. If a future Koin injection for `ScheduleTabViewModel` is added to the screen without the registration, the app crashes at the injection site. Confirmed.

### String resources: navigation labels only

The only schedule strings added are `nav_schedule` ("Schedule" / "الجدول") and `home_schedule_coming_soon` ("Schedule Coming Soon" / "الجدول قريباً"). The full domain set required for a real schedule screen — week day labels, session time/subject/teacher fields, credits banner text, calendar integration CTA, empty-state copy — is entirely absent from both locale files. Confirmed.

### Test coverage gap

`HomeViewModelTest` contains one SCHEDULE-related assertion:

```kotlin
@Test
fun `TabSelected SCHEDULE updates selectedTab`() = runTest(testDispatcher) {
    vm.onIntent(HomeIntent.TabSelected(HomeTab.SCHEDULE))
    assertEquals(HomeTab.SCHEDULE, vm.state.value.selectedTab)
}
```

This tests the host router, not schedule behavior. No `ScheduleTabViewModelTest` file exists. The 15-case threshold specified in the review criteria is not approached. Confirmed.

</details>

---

<details>
<summary>File map</summary>

| File | Change |
|------|--------|
| `feature/home/.../tabs/schedule/ScheduleTabScreen.kt` | New — placeholder "coming soon" composable |
| `feature/home/.../HomeContract.kt` | `HomeTab` enum extended with SCHEDULE; `HomeState` updated |
| `feature/home/.../HomeScreen.kt` | Routes SCHEDULE to stub; SCHEDULE title falls to `else` branch |
| `feature/home/.../di/HomeModule.kt` | Adds HomeTabViewModel, TeachersTabViewModel, AiChatViewModel — no ScheduleTabViewModel |
| `core/designsystem/.../values/strings.xml` | Adds `nav_schedule`, `home_schedule_coming_soon` only |
| `core/designsystem/.../values-ar/strings.xml` | Same two strings in Arabic |

Full diff: `git diff master`

</details>
