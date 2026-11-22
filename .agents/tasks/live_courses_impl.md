# Live Lessons + Courses Implementation Log

## Build Commands Run

| Command | Result |
|---|---|
| `.\gradlew.bat :core:designsystem:compileCommonMainKotlinMetadata` | ✅ BUILD SUCCESSFUL (1m 9s) |
| `.\gradlew.bat :feature:home:compileCommonMainKotlinMetadata` (after System.currentTimeMillis fix) | ✅ BUILD SUCCESSFUL (36s) |
| `.\gradlew.bat :androidApp:assembleDebug` | ✅ BUILD SUCCESSFUL (1m 17s) |

---

## What Was Built

### REQUIREMENT 1 — Live Lessons: 3 Inner Tabs (Upcoming / Next / Previous)

**Domain Layer additions:**
- `LessonFileType` — added `OTHER` variant (was PDF/DOCX/PPTX only)
- `LiveLessonRepository` interface — added `getPreviousLiveLessons()`
- `LiveLessonRepositoryImpl` — full rewrite with:
  - 1 LIVE_NOW lesson, 1 UPCOMING lesson (Upcoming tab hero)
  - 2 NEXT lessons (Next tab)
  - 3 ENDED previous lessons (Previous tab)
  - All times use a fixed base `1_700_000_000_000L` (Nov 2023 epoch) to avoid `System.currentTimeMillis()` which is JVM-only
  - Each method: `delay(400)` + TODO comment + mock data
- New use case: `GetPreviousLiveLessonsUseCase`

**MVI Contract — rewritten:**
- Added `enum class LiveTab { UPCOMING, NEXT, PREVIOUS }`
- State: `selectedTab`, `upcomingLesson: LiveLesson?` (hero), `nextLessons`, `previousLessons`
- Intent: `TabSelected(tab)`, `LoadLessons`, `Refresh`, `JoinZoom(lessonId, url)`, `OpenLessonDetail`, `SyncCalendarClicked`
- Effect: `OpenZoomUrl`, `NavigateToDetail`, `ShowSnackbar`, `SyncCalendarSuccess`

**ViewModel — rewritten:**
- Loads upcoming + next + previous concurrently via `async { }`
- Hero selection: first `LIVE_NOW` else first `UPCOMING` by `startEpochMs`
- `JoinZoom` emits `OpenZoomUrl` if url non-blank, else `ShowSnackbar`
- `SyncCalendar` syncs all future lessons

**Screen — rewritten:**
- `LiveLessonsScreen` (stateful): collects state + handles effects
- `LiveLessonsContent` (stateless): `Column` with `JanaInnerTabRow` + content area
- `UpcomingTab`: hero `UpcomingLessonCard` + `CalendarSyncCard` (or empty state)
- `NextTab`: `LazyColumn` of `NextLessonItem` (or empty state)
- `PreviousTab`: `LazyColumn` of `PreviousLessonItem` (or empty state)

**New component:**
- `PreviousLessonItem.kt` — shows title, subject, date/time, Ended chip

**Updated components (teacher name removed per spec):**
- `UpcomingLessonCard.kt` — removed `teacherName` row
- `NextLessonItem.kt` — changed `"${teacherName} • ${subject}"` to just `lesson.subject`

**Koin:**
- `LiveLessonsModule` updated to include `GetPreviousLiveLessonsUseCase`
- `LiveLessonsViewModel` now takes 4 params (added `getPreviousLiveLessons`)

**Detail screen fixes:**
- `LessonDetailViewModel` — `LessonFileType.OTHER` case added (`application/octet-stream`)
- `LessonDetailScreen` — `ShowExamSnackbar` now shows `lesson_exam_coming_soon` string resource instead of hardcoded message
- `LessonDetailContract` — `ShowExamSnackbar` carries `examId` (unused, screen resolves string itself)

### Shared UI — JanaLiveLessonComponents.kt additions
- `JanaInnerTabRow(tabs, selectedIndex, onTabSelected)` — `TabRow` with primary indicator using `SecondaryIndicator` + `tabIndicatorOffset`; tab labels styled with SemiBold when selected

### REQUIREMENT 2 — Lesson Detail Screen
(Was already built in a prior step; this step fixed/completed it.)
- `LessonDetailScreen` — fixed to use `lesson_exam_coming_soon` string resource
- `LessonDetailViewModel` — handles `OTHER` file type

### REQUIREMENT 3 — Teacher Platform Name in App Bar
- `HomeContract` — added `teacherPlatformName: String = "JANA Platform"` with TODO comment
- `HomeViewModel` — initialises state with `teacherPlatformName = "JANA Platform"` + TODO
- `HomeScreen` — `topBar` now shows `state.teacherPlatformName` for all tabs (no per-tab switching)

### REQUIREMENT 4 — Courses Screen (Course of Study)

**Domain models:**
- `Course(id, title, description, imageUrl, totalLessons, completedLessons, subjects)`
- `CourseUnit(id, title, order, lessons)`
- `CourseLesson(id, title, durationMinutes, isCompleted, hasExam)`

**Repository:**
- `CourseRepository` interface: `getCourses()`, `getCourseDetail(id)`, `getCourseUnits(id)`
- `CourseRepositoryImpl`: 3 courses × 2 units × 3 lessons; delay(400) + TODO comments

**Use cases:**
- `GetCoursesUseCase`, `GetCourseDetailUseCase`, `GetCourseUnitsUseCase`

**Courses list MVI:**
- `CoursesUiState(isLoading, courses, error)`, `CoursesIntent.Load/OpenCourse`, `CoursesEffect.NavigateToCourseDetail`
- `CoursesViewModel` loads in `init`

**Course detail MVI:**
- `CourseDetailUiState(isLoading, course, units, error)`, `CourseDetailIntent.Load/OpenLesson/Back`, `CourseDetailEffect.ShowMessage/NavigateBack`
- `CourseDetailViewModel` loads detail + units concurrently in `init`

**Screens:**
- `CoursesScreen` (stateful) + `CoursesContent` (stateless): `LazyVerticalGrid(Fixed(2))`
- `CourseDetailScreen` (stateful) + `CourseDetailContent` (stateless): hero header + expandable `CourseUnitSection` list
- `CourseUnitSection` — expandable with `AnimatedVisibility`, using `rememberSaveable`
- `CourseLessonItem` — checkmark for completed, play icon for incomplete, Exam chip, duration label

**Shared card:**
- `JanaCourseCard` — image placeholder, title, `FlowRow` subject chips, `LinearProgressIndicator`, progress label, Start/Continue button

**Koin:**
- `coursesModule` — single repo, 3 factory use cases, viewModel for list + viewModel { (courseId) → detail }
- `homeModule` — updated to `includes(liveLessonsModule, coursesModule)`

### Navigation
- `Routes.kt` — added `@Serializable data class CourseDetail(val courseId: String) : HomeRoute`
- `HomeEntryProvider` — added `is HomeRoute.CourseDetail →` case dispatching to `CourseDetailScreen`

### String Resources
Added to both EN (`values/strings.xml`) and AR (`values-ar/strings.xml`):
`live_tab_upcoming`, `live_tab_next`, `live_tab_previous`, `live_next_sessions_title`, `live_previous_title`, `live_no_next`, `live_no_previous`, `lesson_exam_coming_soon`, `courses_title`, `courses_no_courses`, `course_detail_title`, `course_progress_label`, `course_start_btn`, `course_continue_btn`, `course_unit_label`, `course_lesson_duration`, `course_lesson_coming_soon`, `course_exam_chip`, `course_completed_chip`

---

## Files Created (new)
- `core/designsystem/src/commonMain/kotlin/.../JanaLiveLessonComponents.kt` *(modified — added JanaInnerTabRow)*
- `feature/home/.../tabs/live/domain/usecase/GetPreviousLiveLessonsUseCase.kt`
- `feature/home/.../tabs/live/presentation/list/components/PreviousLessonItem.kt`
- `feature/home/.../tabs/courses/domain/model/Course.kt`
- `feature/home/.../tabs/courses/domain/model/CourseUnit.kt`
- `feature/home/.../tabs/courses/domain/model/CourseLesson.kt`
- `feature/home/.../tabs/courses/domain/repository/CourseRepository.kt`
- `feature/home/.../tabs/courses/domain/usecase/GetCoursesUseCase.kt`
- `feature/home/.../tabs/courses/domain/usecase/GetCourseDetailUseCase.kt`
- `feature/home/.../tabs/courses/domain/usecase/GetCourseUnitsUseCase.kt`
- `feature/home/.../tabs/courses/data/repository/CourseRepositoryImpl.kt`
- `feature/home/.../tabs/courses/di/CoursesModule.kt`
- `feature/home/.../tabs/courses/presentation/list/CoursesContract.kt`
- `feature/home/.../tabs/courses/presentation/list/CoursesViewModel.kt`
- `feature/home/.../tabs/courses/presentation/list/CoursesScreen.kt`
- `feature/home/.../tabs/courses/presentation/list/components/JanaCourseCard.kt`
- `feature/home/.../tabs/courses/presentation/detail/CourseDetailContract.kt`
- `feature/home/.../tabs/courses/presentation/detail/CourseDetailViewModel.kt`
- `feature/home/.../tabs/courses/presentation/detail/CourseDetailScreen.kt`
- `feature/home/.../tabs/courses/presentation/detail/components/CourseUnitSection.kt`
- `feature/home/.../tabs/courses/presentation/detail/components/CourseLessonItem.kt`

## Files Modified
- `core/designsystem/.../composeResources/values/strings.xml` (19 new keys)
- `core/designsystem/.../composeResources/values-ar/strings.xml` (19 new keys)
- `core/navigation/.../Routes.kt` (added CourseDetail route)
- `feature/home/.../HomeContract.kt` (added teacherPlatformName)
- `feature/home/.../HomeViewModel.kt` (initialise platform name)
- `feature/home/.../HomeScreen.kt` (topBar shows teacherPlatformName)
- `feature/home/.../HomeEntryProvider.kt` (CourseDetail route)
- `feature/home/.../di/HomeModule.kt` (includes coursesModule)
- `feature/home/.../tabs/courses/CoursesTabScreen.kt` (calls CoursesScreen)
- `feature/home/.../tabs/live/domain/model/LessonFileType.kt` (added OTHER)
- `feature/home/.../tabs/live/domain/repository/LiveLessonRepository.kt` (added getPreviousLiveLessons)
- `feature/home/.../tabs/live/data/repository/LiveLessonRepositoryImpl.kt` (full rewrite)
- `feature/home/.../tabs/live/di/LiveLessonsModule.kt` (GetPreviousLiveLessonsUseCase + ViewModel sig)
- `feature/home/.../tabs/live/presentation/list/LiveLessonsContract.kt` (3-tab MVI)
- `feature/home/.../tabs/live/presentation/list/LiveLessonsViewModel.kt` (3-tab loading)
- `feature/home/.../tabs/live/presentation/list/LiveLessonsScreen.kt` (3-tab UI)
- `feature/home/.../tabs/live/presentation/list/components/UpcomingLessonCard.kt` (removed teacherName)
- `feature/home/.../tabs/live/presentation/list/components/NextLessonItem.kt` (removed teacherName)
- `feature/home/.../tabs/live/presentation/detail/LessonDetailScreen.kt` (exam snackbar via string res)
- `feature/home/.../tabs/live/presentation/detail/LessonDetailViewModel.kt` (OTHER file type)

## Commit
`1986078` — feat: live lessons 3-tabs, teacher platform title, courses screen

---

## Review Iteration 2 — Fixing live_courses_review.md findings

### Build Commands Run

| Command | Result |
|---|---|
| `.\gradlew.bat :core:designsystem:compileCommonMainKotlinMetadata` | ✅ BUILD SUCCESSFUL (25s) |
| `.\gradlew.bat :feature:home:compileCommonMainKotlinMetadata` | ✅ BUILD SUCCESSFUL (23s) |
| `.\gradlew.bat :androidApp:assembleDebug` | ✅ BUILD SUCCESSFUL (38s) |
| `.\gradlew.bat :feature:home:compileTestKotlinJvm` | ✅ BUILD SUCCESSFUL (5s) |
| `.\gradlew.bat :feature:home:jvmTest` | ✅ BUILD SUCCESSFUL — all tests pass (4s) |

### Finding 1 — Hardcoded snackbar strings fixed
- Added `live_no_zoom_link` and `live_calendar_sync_failed` string keys to both EN and AR strings.xml
- Added `ShowNoZoomLink` and `ShowCalendarSyncFailed` as typed `data object` effects in `LiveLessonsContract`
- `LiveLessonsViewModel` now emits typed effects instead of `ShowSnackbar("...")` for both messages
- `LiveLessonsScreen` resolves both strings via `stringResource()` and shows them via snackbar in the `LaunchedEffect`

### Finding 2 — LiveLessonsViewModelTest compilation errors fixed
- Added `getPreviousLiveLessons()` to fakeRepo in `LiveLessonsViewModelTest`
- Updated `buildViewModel()` to pass 4 params (added `getPreviousLiveLessons`)
- Changed state assertion from `upcomingLessons.size` to `upcomingLesson != null` / id check
- Changed `JoinZoom("url")` to `JoinZoom("l1", "url")` (2-arg)
- Changed `OpenLessonDetail` effect to `NavigateToDetail`
- Added a new `JoinZoom with blank url emits ShowNoZoomLink` test to cover Finding 1
- Also fixed `LessonDetailViewModelTest` fakeRepo (same missing `getPreviousLiveLessons`)

### Finding 3 — Error state in CoursesContent and CourseDetailContent
- `CoursesContent`: added `state.error != null` branch between loading and empty-state; shows `error_generic` text + Retry button
- `CourseDetailContent`: added `state.error != null` branch after loading branch; prevents the previously blank body when `course == null` after failed load

### Finding 4 — Partial load failure in LiveLessonsViewModel
- After `upcomingResult` succeeds, checks `nextResult` and `previousResult` for failure
- Sets `error` to the failure message rather than `null` when partial failure occurs
- `LiveLessonsScreen` Next/Previous tabs now show `live_error_load_failed` in error colour when `hasPartialError = true` and the list is empty
- Full-failure path (upcomingResult fails + all lists empty) added to `LiveLessonsContent`

### Finding 5 — Dead Refresh intent removed
- Removed `data object Refresh` from `LiveLessonsContract`
- Removed `LiveLessonsIntent.Refresh -> loadLessons()` handler from `LiveLessonsViewModel`

### Finding 6 — courses_title now referenced
- `CoursesScreen` imports and uses `courses_title` via `JanaSectionHeader` above the `LazyVerticalGrid`

### Files Modified (review iteration 2)
- `core/designsystem/.../values/strings.xml` (3 new keys: live_no_zoom_link, live_calendar_sync_failed, live_error_load_failed)
- `core/designsystem/.../values-ar/strings.xml` (same 3 AR keys)
- `feature/home/.../tabs/live/presentation/list/LiveLessonsContract.kt`
- `feature/home/.../tabs/live/presentation/list/LiveLessonsViewModel.kt`
- `feature/home/.../tabs/live/presentation/list/LiveLessonsScreen.kt`
- `feature/home/.../tabs/courses/presentation/list/CoursesScreen.kt`
- `feature/home/.../tabs/courses/presentation/detail/CourseDetailScreen.kt`
- `feature/home/src/jvmTest/.../tabs/live/LiveLessonsViewModelTest.kt`
- `feature/home/src/jvmTest/.../tabs/live/LessonDetailViewModelTest.kt`

### Commit
`d2235e0` — fix: address live lessons + courses review findings
