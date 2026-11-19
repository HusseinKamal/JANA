# Teachers / Discover tab – implementation evidence

## Iteration

This document covers both the initial implementation and the second-iteration review-fix pass.

---

## Initial implementation (first pass)

### Files created / replaced

| File | Description |
|------|-------------|
| `feature/home/.../tabs/teachers/TeachersTabContract.kt` | MVI types, models, mock data, `formatK` extension |
| `feature/home/.../tabs/teachers/TeachersTabViewModel.kt` | `TeachersStrings`, `updateStrings`, `applyFilters`, intent dispatch |
| `feature/home/.../tabs/teachers/TeachersTabScreen.kt` | Stateful entry + stateless `TeachersTabContent` LazyColumn |
| `feature/home/.../tabs/teachers/components/TeacherSearchBar.kt` | `BasicTextField` with Box-overlay hint |
| `feature/home/.../tabs/teachers/components/SubjectFilterRow.kt` | Horizontally scrollable Row of filter chips |
| `feature/home/.../tabs/teachers/components/TeacherCard.kt` | Full teacher card (avatar, stats, badges, schedule, price, action buttons) |
| `feature/home/.../tabs/teachers/components/TeacherBadgeChip.kt` | Per-BadgeStyle colour chip |
| `feature/home/.../tabs/teachers/components/HowJanaWorksCard.kt` | Amber info card with AnnotatedString bold spans |
| `feature/home/src/jvmTest/.../TeachersTabViewModelTest.kt` | 25 ViewModel unit tests |
| `core/designsystem/.../values/strings.xml` | +31 `teachers_*` EN string resources |
| `core/designsystem/.../values-ar/strings.xml` | +31 `teachers_*` AR string resources |
| `feature/home/.../di/HomeModule.kt` | Added `viewModel { TeachersTabViewModel() }` |

---

## Review-fix pass (second iteration)

### Findings addressed

**1 – Hard-coded English words in `TeacherCard` stats row**

- Added `teachers_stats_format` to both `values/strings.xml` and `values-ar/strings.xml`:
  - EN: `%1$s active • %2$d yrs exp`
  - AR: `%1$s طالب نشط • %2$d سنوات خبرة`
- Updated `TeacherCard.kt` to call `stringResource(Res.string.teachers_stats_format, teacher.activeStudents.formatK(), teacher.yearsExp)` instead of the hard-coded template.

**2 – `Spacer(Modifier.padding(horizontal = X.dp))` creates 2×X gap**

- `TeachersTabScreen.kt` `TeachersHeader`: replaced `Modifier.padding(horizontal = 8.dp)` with `Modifier.width(8.dp)` on the spacer between headline text and "100% Verified" badge.
- Same file: replaced `Modifier.padding(horizontal = 2.dp)` with `Modifier.width(2.dp)` on the spacer between the check icon and badge text.
- Added missing `import androidx.compose.foundation.layout.width`.

**3 – `SubjectFilter.label` dead field**

- Removed `label: String` from `SubjectFilter` data class in `TeachersTabContract.kt`.
- Updated `defaultFilters` to use single-arg `SubjectFilter(key)` constructor calls.
- `SubjectFilterRow` already resolved chip text entirely from `filterLabelResource(key)` so no changes needed there.

**4 – `filterLabelResource` silent fallback**

- Changed the `else` branch in `SubjectFilterRow.kt` from `Res.string.teachers_filter_all` (silent wrong output) to `throw IllegalArgumentException("No string resource mapped for filter key: \"$key\"")`.
- Added explanatory comment so future contributors know the throw is intentional.

**5 – `teachers_title` string resource unused**

- Added `import com.hussein.jana.core.designsystem.teachers_title` to `TeachersTabScreen.kt`.
- Read the value inside `TeachersTabContent` as `val screenTitle = stringResource(Res.string.teachers_title)` and applied it as `Modifier.semantics { contentDescription = screenTitle }` on the `LazyColumn`.
- Comment documents it as the future navigation-destination label.

**6 – "Learn more" `TextButton` is a non-functional affordance**

- Added `enabled = false` to `TextButton` in `HowJanaWorksCard.kt`; added comment explaining the destination is not yet wired.

**Bonus – unused `assertNotNull` import in test**

- Removed `import kotlin.test.assertNotNull` from `TeachersTabViewModelTest.kt`.

---

## Build & test commands

```
.\gradlew :composeApp:compileKotlinJvm   # BUILD SUCCESSFUL
.\gradlew :feature:home:jvmTest          # BUILD SUCCESSFUL (25 tests pass)
```

Both commands run from `d:/AndroidApps/JANA/JANA`.
