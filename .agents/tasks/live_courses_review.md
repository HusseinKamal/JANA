# Live Lessons 3-tab, teacher platform title, and Courses screen

This change delivers the full set of requirements from messages 7–8: a three-tab Live Lessons screen (Upcoming / Next / Previous) with calendar sync and Zoom join, a Lesson Detail screen with file preview and exams, the teacher platform name in the top app bar, and a new Courses flow (list → course detail with expandable units and lesson items). All mock data is behind TODO-annotated repository stubs and the build passes clean through `:androidApp:assembleDebug`. A second iteration fixed six review findings from the first pass.

Watch for: `CourseDetailEffect.ShowMessage` and `LessonDetailEffect.ShowExamSnackbar` both carry a payload field that the screen always ignores—this is new code in this PR and will mislead anyone who next adds a `ShowMessage` variant expecting the field to reach the UI. Neither the new `CoursesViewModel` nor `CourseDetailViewModel` has test coverage.

**Verdict**: APPROVED

---

## High-level view

The Live Lessons screen is split into three independently loaded tabs driven by a single `LiveLessonsViewModel` that fires three `async {}` calls concurrently, then promotes the first `LIVE_NOW` lesson (or the earliest `UPCOMING` by epoch) to a hero card in the Upcoming tab. Partial load failure (next or previous failing while upcoming succeeds) is correctly propagated into `state.error` and shown per-tab rather than wiping the whole screen.

The calendar sync and Zoom join paths are clean: both emit typed effects (`ShowNoZoomLink`, `ShowCalendarSyncFailed`, `SyncCalendarSuccess`) that the screen resolves to string resources; no raw English strings leave the ViewModel. The `GenericSnackbar` variant still exists in the contract as a fallback but is never emitted in this PR's code paths.

App-bar title is wired through `HomeState.teacherPlatformName`, which is initialised to `"JANA Platform"` with a marked TODO. All five bottom-nav tabs read their labels from string resources; teacher name is gone from lesson cards.

The Courses flow introduces a standard domain → repository → use case → ViewModel → Screen stack parallel to the Live Lessons stack, sharing `JanaCourseCard`, `CourseUnitSection`, and `CourseLessonItem` as new reusable components in the design system area. Navigation uses a typed `@Serializable` `CourseDetail(courseId)` route registered in `HomeEntryProvider`.

String coverage is complete: 22 new keys in both EN and AR, all resolved via `stringResource()`. Mock data uses a fixed November 2023 epoch (platform-safe) rather than `System.currentTimeMillis()`; `dateText`/`timeText` are pre-formatted stubs, which is acceptable for a mock layer.

---

<details>
<summary>Issues (4)</summary>

1. **Dead `ShowMessage.message` field** — `CourseDetailViewModel` emits `ShowMessage("lesson_coming_soon")` but `CourseDetailScreen` resolves `course_lesson_coming_soon` from string resources and discards `effect.message`. The field serves no purpose; anyone adding a second emission path will assume it reaches the UI. Rename the effect to a typed `data object ShowLessonComingSoon` or drop the field. — *confirmed*

2. **`ShowExamSnackbar.message` stores `exam.id`** — `LessonDetailContract` declares `data class ShowExamSnackbar(val message: String)` but `LessonDetailViewModel` populates it with `intent.exam.id`. The screen ignores the field. Rename to `examId: String` or remove the field if it truly serves no purpose. — *confirmed*

3. **No tests for new Courses screens** — `CoursesViewModel` and `CourseDetailViewModel` are entirely new in this PR. Neither has JVM test coverage. The load-success path, error path, and navigation effect for both should be tested to match the pattern already established in `LiveLessonsViewModelTest`. — *confirmed*

4. **Empty-but-failed ambiguity in full-failure gate** — The screen treats `error != null && upcomingLesson == null && nextLessons.isEmpty() && previousLessons.isEmpty()` as a full-failure screen. If the upcoming API legitimately returns an empty list (no lessons scheduled) while next/previous fail, the user sees the full retry screen rather than an upcoming empty-state plus a per-tab error. This is a possible edge case that only matters with a real backend. — *possible*

</details>

---

<details>
<summary>Details</summary>

## Three-tab Live Lessons architecture

`LiveLessonsViewModel` fires `getUpcomingLiveLessons`, `getNextLiveLessons`, and `getPreviousLiveLessons` concurrently via `async {}` inside a single `viewModelScope.launch`. Hero promotion logic (`firstOrNull { LIVE_NOW } ?: minByOrNull { startEpochMs }`) is correct and isolated. Partial failure propagation sets `state.error` to the first failing sub-request's message without discarding already-loaded data, so the Upcoming tab continues to render while Next/Previous tabs display `live_error_load_failed` in the error color.

The `LiveTab` enum drives tab selection via `state.selectedTab.ordinal` passed to `JanaInnerTabRow`. State hoisting is clean: `LiveLessonsScreen` is stateful, `LiveLessonsContent` is stateless, and each of `UpcomingTab`, `NextTab`, and `PreviousTab` is a private composable receiving only the slice of state it needs.

## Typed snackbar effects and the dead-field problem

`ShowNoZoomLink` and `ShowCalendarSyncFailed` are correct `data object` effects—no string in the ViewModel, resolved at the call site in the screen. This pattern is applied consistently in the Live Lessons flow.

`CourseDetailEffect.ShowMessage("lesson_coming_soon")` breaks the same pattern. The `message` field holds a string key, not a message, and `CourseDetailScreen` resolves `course_lesson_coming_soon` directly from `stringResource()` on every `ShowMessage` event, making the field a confirmed dead parameter. The same issue exists in `LessonDetailEffect.ShowExamSnackbar(val message: String)`, which is populated with `intent.exam.id` by the ViewModel. The screen resolves `lesson_exam_coming_soon` from string resources and ignores the field entirely. Neither is a user-visible bug, but both will mislead future maintainers.

## Courses flow completeness

`CoursesViewModel` and `CourseDetailViewModel` follow the same load-on-init pattern as the Live Lessons ViewModels. `CourseDetailViewModel` loads detail and units concurrently; if detail fails it sets `state.error`; if only units fail, units default to `emptyList()` with no error surfaced—acceptable for a stub layer where both calls share the same mock. The error branches are present in both `CoursesContent` and `CourseDetailContent`.

`JanaCourseCard` duplicates the progress-label `String.replace("%1$d", ...)` logic that also appears in `CourseDetailContent`. Both are correct, but extracting a shared helper when the backend integration lands would eliminate the duplication.

## Mock data epoch strategy

The fixed base `1_700_000_000_000L` avoids `System.currentTimeMillis()` which is JVM-only in KMP. Date strings (`"Today, 4:00 PM"`, `"Tomorrow, 2:00 PM"`) are pre-formatted stubs in the mock, so the displayed text will not reflect actual elapsed time, but that is correct for a mock layer: all date rendering logic belongs in a future formatter, not in mock data.

## Test coverage

`LiveLessonsViewModelTest` covers load-success, `JoinZoom` with and without URL, `OpenLessonDetail`, and `SyncCalendarClicked`. The `ShowNoZoomLink` typed effect is verified. `LessonDetailViewModelTest` compiles with the updated repo interface. `CoursesViewModel` and `CourseDetailViewModel` have no test coverage despite being entirely new classes. The load-error path in particular—which the screen now renders—goes untested.

</details>

---

<details>
<summary>File map</summary>

| File | Change |
|---|---|
| `core/designsystem/JanaLiveLessonComponents.kt` | Added `JanaInnerTabRow` composable |
| `core/designsystem/values/strings.xml` | 22 new string keys (EN) |
| `core/designsystem/values-ar/strings.xml` | 22 new string keys (AR) |
| `core/navigation/Routes.kt` | Added `CourseDetail(courseId)` serializable route |
| `feature/home/HomeContract.kt` | Added `teacherPlatformName` field |
| `feature/home/HomeViewModel.kt` | Initialises `teacherPlatformName` |
| `feature/home/HomeScreen.kt` | TopBar shows `state.teacherPlatformName` |
| `feature/home/HomeEntryProvider.kt` | Added `CourseDetail` route entry |
| `feature/home/di/HomeModule.kt` | Includes `coursesModule` |
| `feature/home/tabs/courses/CoursesTabScreen.kt` | Delegates to `CoursesScreen` |
| `feature/home/tabs/courses/data/repository/CourseRepositoryImpl.kt` | Mock courses data |
| `feature/home/tabs/courses/di/CoursesModule.kt` | Koin wiring for courses |
| `feature/home/tabs/courses/domain/model/{Course,CourseUnit,CourseLesson}.kt` | New domain models |
| `feature/home/tabs/courses/domain/repository/CourseRepository.kt` | Repository interface |
| `feature/home/tabs/courses/domain/usecase/{GetCourses,GetCourseDetail,GetCourseUnits}UseCase.kt` | Use cases |
| `feature/home/tabs/courses/presentation/list/{CoursesContract,CoursesViewModel,CoursesScreen}.kt` | Courses list MVI |
| `feature/home/tabs/courses/presentation/list/components/JanaCourseCard.kt` | Reusable course card |
| `feature/home/tabs/courses/presentation/detail/{CourseDetailContract,CourseDetailViewModel,CourseDetailScreen}.kt` | Course detail MVI |
| `feature/home/tabs/courses/presentation/detail/components/{CourseUnitSection,CourseLessonItem}.kt` | Expandable unit + lesson row |
| `feature/home/tabs/live/data/repository/LiveLessonRepositoryImpl.kt` | Full rewrite with 3-tab mock data |
| `feature/home/tabs/live/di/LiveLessonsModule.kt` | Added `GetPreviousLiveLessonsUseCase` |
| `feature/home/tabs/live/domain/model/LessonFileType.kt` | Added `OTHER` variant |
| `feature/home/tabs/live/domain/repository/LiveLessonRepository.kt` | Added `getPreviousLiveLessons()` |
| `feature/home/tabs/live/domain/usecase/GetPreviousLiveLessonsUseCase.kt` | New use case |
| `feature/home/tabs/live/presentation/detail/LessonDetailScreen.kt` | Exam snackbar via string resource |
| `feature/home/tabs/live/presentation/detail/LessonDetailViewModel.kt` | Added `OTHER` file type handling |
| `feature/home/tabs/live/presentation/list/LiveLessonsContract.kt` | 3-tab MVI contract |
| `feature/home/tabs/live/presentation/list/LiveLessonsViewModel.kt` | 3-tab loading + typed effects |
| `feature/home/tabs/live/presentation/list/LiveLessonsScreen.kt` | 3-tab UI + partial error display |
| `feature/home/tabs/live/presentation/list/components/PreviousLessonItem.kt` | New previous lesson row |
| `feature/home/tabs/live/presentation/list/components/{UpcomingLessonCard,NextLessonItem}.kt` | Teacher name removed |
| `feature/home/tabs/live/LiveLessonsViewModelTest.kt` | Fixed for 4-param constructor + new tests |
| `feature/home/tabs/live/LessonDetailViewModelTest.kt` | Fixed for updated repo interface |

Full diff: `git diff efcde34..HEAD`

</details>
