# Teachers Tab Implementation

## Status
✅ BUILD SUCCESSFUL  
✅ 25/25 tests passing  

---

## Overview

Replaced the placeholder `TeachersTabScreen.kt` with a full MVI implementation of the Teachers / Discover tab for the JANA KMP app. Follows the existing MVI/clean architecture pattern (BaseMviViewModel, MviState/MviIntent/MviEffect) and matches the code conventions established in HomeTab and AuthScreen.

---

## Files Created / Modified

### New Files

| File | Purpose |
|------|---------|
| `feature/home/.../tabs/teachers/TeachersTabContract.kt` | MVI contract: enums, data models, mock data, `formatK` extension, State/Intent/Effect types |
| `feature/home/.../tabs/teachers/TeachersTabViewModel.kt` | ViewModel with `applyFilters`, `updateStrings`, intent handling |
| `feature/home/.../tabs/teachers/TeachersTabScreen.kt` | Stateful screen + stateless `TeachersTabContent` LazyColumn |
| `feature/home/.../tabs/teachers/components/TeacherSearchBar.kt` | BasicTextField search bar (state-hoisted, Box overlay hint) |
| `feature/home/.../tabs/teachers/components/SubjectFilterRow.kt` | Horizontally-scrollable Row filter chips (string resources via key mapping) |
| `feature/home/.../tabs/teachers/components/TeacherCard.kt` | Full teacher card: avatar, stats, badges, schedule, price, action buttons |
| `feature/home/.../tabs/teachers/components/TeacherBadgeChip.kt` | Badge chip with per-BadgeStyle color pairs |
| `feature/home/.../tabs/teachers/components/HowJanaWorksCard.kt` | Amber info card with AnnotatedString bold spans |
| `feature/home/src/jvmTest/.../tabs/teachers/TeachersTabViewModelTest.kt` | 25 unit tests |

### Modified Files

| File | Change |
|------|--------|
| `core/designsystem/.../values/strings.xml` | Added 31 `teachers_*` English string resources |
| `core/designsystem/.../values-ar/strings.xml` | Added 31 `teachers_*` Arabic string resources |
| `feature/home/.../di/HomeModule.kt` | Added `viewModel { TeachersTabViewModel() }` |

---

## Architecture

### MVI Contract
```
TeachersTabState       – searchQuery, selectedFilter, filters, allTeachers, filteredTeachers
TeachersTabIntent      – SearchQueryChanged, FilterSelected, ProfileClicked, ReserveClicked, FilterButtonClicked  
TeachersTabEffect      – ShowSnackbar(message: String)
```

### ViewModel (`TeachersTabViewModel`)
- Extends `BaseMviViewModel<TeachersTabState, TeachersTabIntent, TeachersTabEffect>`
- `applyFilters(query, filter)` – called by both `SearchQueryChanged` and `FilterSelected`. Each updates its own axis while preserving the other from `currentState()`. Filtering is AND-logic: subject key + case-insensitive text search across name, subjectDetail, and subjectKey.
- `updateStrings(TeachersStrings)` – locale injection following the AuthViewModel pattern. Called from a `LaunchedEffect` in the composable so locale changes propagate without VM recreation.
- Effect messages carry plain `String` (already resolved by composition before entering VM).

### Screen (`TeachersTabScreen`)
- `koinViewModel<TeachersTabViewModel>()` for DI.
- Strings injected via `LaunchedEffect(profileComingSoon, reserveComingSoon, filtersComingSoon)` → `viewModel.updateStrings(...)`.
- Effects collected in a separate `LaunchedEffect(viewModel)` block; `snackbarHostState.showSnackbar()` called directly (message is already a String).
- `TeachersTabContent` is `internal` (stateless) and receives `state` + `onIntent`.

### Search Bar (`TeacherSearchBar`)
- `BasicTextField` with full state hoisting via `query`/`onQueryChange`.
- `decorationBox` uses a `Box` to overlay the hint text directly on the input position, preventing layout shifting. This addresses the previous "unable to edit text field" issue.

---

## String Resources Added (English / Arabic)

| Key | EN | AR |
|-----|----|----|
| `teachers_title` | Teacher Discovery | اكتشاف المعلمين |
| `teachers_hub_label` | DIRECT MENTORSHIP HUB | مركز الإرشاد المباشر |
| `teachers_headline` | Reserve Live Teaching | احجز تدريساً مباشراً |
| `teachers_verified_label` | 100% Verified | موثّق 100% |
| `teachers_search_hint` | Search teachers, subjects, or topics… | ابحث عن معلمين أو مواد أو مواضيع… |
| `teachers_filter_all` | All Subjects | جميع المواد |
| `teachers_filter_math` | Mathematics | الرياضيات |
| `teachers_filter_physics` | Physics | الفيزياء |
| `teachers_filter_chemistry` | Chemistry | الكيمياء |
| `teachers_filter_biology` | Biology | الأحياء |
| `teachers_filter_english` | English | اللغة الإنجليزية |
| `teachers_tuition` | Tuition Investment | رسوم التعليم |
| `teachers_class_format` | Class Format | صيغة الفصل |
| `teachers_profile_btn` | Profile | الملف الشخصي |
| `teachers_reserve_btn` | Reserve Slot | احجز موعداً |
| `teachers_how_title` | How JANA Subscriptions Work | كيف تعمل اشتراكات جَنَى |
| `teachers_how_desc_part1` | Subscribe directly to a trusted educator or use universal | اشترك مباشرةً مع معلم موثوق أو استخدم |
| `teachers_how_bold1` | Session Credits | اعتمادات الجلسة |
| `teachers_how_desc_part2` | across any subject… | العالمية عبر أي مادة… |
| `teachers_how_bold2` | AI companion | مرافق الذكاء الاصطناعي |
| `teachers_how_desc_part3` | recaps synchronized with your teacher's curriculum. | الشخصي متزامناً مع منهج معلمك. |
| `teachers_attendance_tracking` | Real-time attendance tracking | تتبع الحضور الفوري |
| `teachers_learn_more` | Learn more | اعرف أكثر |
| `teachers_no_results` | No teachers found.\nTry a different search or filter. | لم يُعثر على معلمين.\nجرّب بحثاً أو فلتراً مختلفاً. |
| `teachers_profile_coming_soon` | Teacher profile coming soon | الملف الشخصي للمعلم قريباً |
| `teachers_reserve_coming_soon` | Reservation coming soon | الحجز قريباً |
| `teachers_filters_coming_soon` | Advanced filters coming soon | الفلاتر المتقدمة قريباً |

---

## Tests (`TeachersTabViewModelTest`) — 25 tests, all passing

| # | Test description |
|---|-----------------|
| 1 | Initial state has all mock teachers |
| 2 | Initial `selectedFilter` is "all" |
| 3 | Initial `searchQuery` is empty |
| 4 | Initial `filters` matches `defaultFilters` |
| 5 | `SearchQueryChanged` filters by teacher name |
| 6 | `SearchQueryChanged` filters by subject detail |
| 7 | `FilterSelected("mathematics")` returns only math teachers |
| 8 | `FilterSelected("physics")` returns only physics teachers |
| 9 | Combined filter + search narrows results correctly |
| 10 | `SearchQueryChanged` with no match returns empty list |
| 11 | `FilterSelected` with no matching teachers returns empty list |
| 12 | Search is case-insensitive for teacher names |
| 13 | Search is case-insensitive for subject key |
| 14 | Blank query after search restores all teachers |
| 15 | `ProfileClicked` emits `ShowSnackbar` with default profile string |
| 16 | `ReserveClicked` emits `ShowSnackbar` with default reserve string |
| 17 | `FilterButtonClicked` emits `ShowSnackbar` with default filters string |
| 18 | `updateStrings` changes `ProfileClicked` snackbar message |
| 19 | `FilterSelected` preserves existing `searchQuery` in state |
| 20 | `SearchQueryChanged` preserves existing `selectedFilter` in state |
| 21 | `FilterSelected("all")` after subject filter restores full list |
| 22 | `formatK` formats 1250 as "1.2k" |
| 23 | `formatK` formats 820 as "820" |
| 24 | `formatK` formats 1000 as "1.0k" |
| 25 | `formatK` formats 999 as "999" |

---

## Build Commands

```powershell
# From d:/AndroidApps/JANA/JANA

# Compile JVM target
.\gradlew :composeApp:compileKotlinJvm
# Result: BUILD SUCCESSFUL

# Run all home-feature JVM tests
.\gradlew :feature:home:jvmTest
# Result: BUILD SUCCESSFUL — 25 teachers + 19 home tab + 8 home VM = 52 total
```
