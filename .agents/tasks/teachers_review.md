# Teachers / Discover Tab for JANA Student App

The change adds the full Teachers / Discover tab to the JANA KMP student app. It follows the established MVI pattern: a contract file holds all types and mock data, a ViewModel handles filtering and string injection, and a stateless `TeachersTabContent` composable owns the LazyColumn layout. All three user-action paths (view profile, reserve a slot, open advanced filters) stub to snackbar effects with localized strings. The tab is immediately reachable from `HomeScreen` at `HomeTab.TEACHERS`. Watch for: the disabled "Learn more" `TextButton` renders at full color despite `enabled = false` due to an explicit `Text(color = ...)` override — it looks interactive but won't respond to clicks; and mock-data badge and schedule strings are English-only and will not localize to Arabic.

**Verdict**: APPROVED

---

## High-level view

`applyFilters` combines filter and search as independent AND axes, reading from `currentState()` so each intent preserves the other axis. `TeachersStrings` defaults to English so the ViewModel works without a Compose context, with the screen pushing localized values via a `LaunchedEffect` keyed on the three resolved strings — the same pattern as the login screen. `filterLabelResource` in `SubjectFilterRow` throws `IllegalArgumentException` for any unmapped key rather than silently showing the wrong label; the risk is a composition-thread crash if a new filter key is ever added to `defaultFilters` without a matching `when` branch.

Fifteen user-visible strings per teacher (badge labels, schedule, session format, price sub-label, price note, session type label) are hard-coded English in `mockTeachers` with no Arabic counterparts in the resource files. Users in Arabic locale see English in those fields until real API data replaces the mock.

`HowJanaWorksCard` sets `TextButton(enabled = false)` but wraps a `Text(color = ScholarlyTeal)`. In Compose Material3, an explicit `color` on `Text` bypasses `LocalContentColor` unconditionally, so the disabled content alpha never applies and "Learn more →" renders at full teal opacity — visually indistinguishable from an active button.

The Arabic translations for `teachers_profile_btn` ("الملف الشخصي") and `teachers_reserve_btn` ("احجز موعداً") are both significantly longer than their English equivalents. The price `Column` uses `weight(1f)` so the buttons themselves won't clip, but on a 320 dp screen the price area shrinks to near-invisible width.

---

<details>
<summary>Issues (5)</summary>

1. **"Learn more" renders visually enabled** — `Text(color = ScholarlyTeal)` inside `TextButton(enabled = false)` overrides Material3's disabled content alpha; the text appears at full opacity and looks interactive. Remove the explicit `color` parameter from the inner `Text` and let the button's disabled state handle the appearance.

2. **Mock badge and schedule strings are English-only** — `TeacherBadgeData.label` values ("99% Attendance Rate", "Avg. Response: 15 min", etc.) and `TeacherData` schedule/session/price strings in `mockTeachers` are hard-coded English with no Arabic resource counterparts. Add `teachers_*` resource keys for each field or annotate the data class fields as "API-supplied, not localized via resources."

3. **"EGP" currency code is a bare string literal** — The currency label in `TeacherCard`'s price row is hard-coded rather than a string resource. Add a `teachers_currency_egp` or shared `currency_egp` key so it can be overridden per locale.

4. **Price/button row squeeze in Arabic on small screens** — Arabic button labels are substantially wider than English. On a 320 dp screen the price `Column` (which has `weight(1f)`) shrinks to near-invisible width. Add a `minWidth` constraint to the price column or switch to a stacked layout below a width threshold.

5. **`filterLabelResource` throw is a runtime crash risk** — The `else` branch throws `IllegalArgumentException` during composition on the UI thread. A sealed type for `SubjectFilter.key`, or a build-time test asserting all `defaultFilters` keys map to a resource, would make this constraint static rather than a production crash waiting on a future contributor.

</details>

<details>
<summary>Details</summary>

### "Learn more" disabled-state color bypass

`TextButton(onClick = {}, enabled = false)` wraps `Text(color = ScholarlyTeal)`. In Compose Material3, an explicit `color` parameter on `Text` reads directly from the value passed in, not from `LocalContentColor`. The disabled container/content color logic applied by `TextButton` only affects `LocalContentColor`, not explicitly-set colors. "Learn more →" therefore renders in full-opacity teal regardless of the `enabled` flag. Dropping the `color` parameter from the inner `Text` lets Material3 apply the standard 0.38 alpha to the content, making the disabled state visually clear.

### `filterLabelResource` fail-fast contract

`SubjectFilterRow.kt` throws `IllegalArgumentException` for any filter key not covered by the `when` expression. The comment documents the intent, and it's correct to fail loudly rather than silently show the wrong label. The gap is that `throw` executes during composition on the UI thread, turning a contributor mistake into a production crash rather than a build failure. A sealed interface for filter keys (eliminating the `else` branch entirely) or a unit test that iterates `defaultFilters` and calls `filterLabelResource` for each key would catch the mismatch before shipping.

### Test coverage gaps

Not tested: `filterLabelResource` throw path for an unknown key; `updateStrings` propagation for the reserve and filter-button message paths (only the profile path is covered by the custom-string test).

</details>

---

<details>
<summary>File map</summary>

| File | Change |
|------|--------|
| `feature/home/.../tabs/teachers/TeachersTabContract.kt` | New — MVI types, models, mock data, `formatK` |
| `feature/home/.../tabs/teachers/TeachersTabViewModel.kt` | New — `TeachersStrings`, `updateStrings`, `applyFilters`, intent dispatch |
| `feature/home/.../tabs/teachers/TeachersTabScreen.kt` | Placeholder replaced — stateful entry + stateless `TeachersTabContent` LazyColumn |
| `feature/home/.../tabs/teachers/components/TeacherSearchBar.kt` | New — `BasicTextField` with Box-overlay hint, state-hoisted |
| `feature/home/.../tabs/teachers/components/SubjectFilterRow.kt` | New — horizontally scrollable `Row` of filter chips with `filterLabelResource` |
| `feature/home/.../tabs/teachers/components/TeacherCard.kt` | New — avatar, stats, badges, schedule, price, action buttons |
| `feature/home/.../tabs/teachers/components/TeacherBadgeChip.kt` | New — per-`BadgeStyle` colour chip |
| `feature/home/.../tabs/teachers/components/HowJanaWorksCard.kt` | New — amber info card with `AnnotatedString` bold spans, disabled "Learn more" |
| `feature/home/src/jvmTest/.../TeachersTabViewModelTest.kt` | New — 25 ViewModel unit tests |
| `core/designsystem/.../values/strings.xml` | +32 `teachers_*` EN keys including `teachers_stats_format` |
| `core/designsystem/.../values-ar/strings.xml` | +32 `teachers_*` AR keys with correct Arabic |
| `feature/home/.../di/HomeModule.kt` | Added `viewModel { TeachersTabViewModel() }` |

Full diff: `git -C d:/AndroidApps/JANA/JANA diff origin/develop HEAD`

</details>
