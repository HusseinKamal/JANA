# Implementation Plan — Live Lessons Feature

> This plan is decomposed into 3 FEATs. The workflow's coder steps read the FEAT JSON files
> under `.agents/tasks/task-live-lessons/features/`. The plain-list steps below are the
> **same content** expressed as a single sequential list for the fallback loop.
>
> FEAT artifacts root: `d:/AndroidApps/JANA/JANA/.agents/tasks/task-live-lessons/`
> context.json: `d:/AndroidApps/JANA/JANA/.agents/tasks/task-live-lessons/context.json`

---

## Architectural decisions (made during exploration)

**Strings live in designsystem, not home.** All string resource keys are in
`core/designsystem/src/commonMain/composeResources/values/strings.xml` (and `values-ar/`) — this
is the existing pattern used by every tab.

**Platform actions extend core/designsystem.** `openUrl`, `nativeCalendarUrl` and their
per-platform actuals already live in `core/designsystem/src/{androidMain,iosMain,jvmMain}`. The
new `syncCalendar` and `previewFile` expect/actual functions go in the same files. iOS
`syncCalendar` falls back to `openUrl("calshow://")` — full EventKit cinterop requires Xcode
project changes that are out of scope.

**Domain + data layer nested under `tabs/live/`.** The other tabs (schedule, teachers) keep
everything in one Contract file, but the task spec requires data/domain sub-directories. The
feature module's single `commonMain` source set is used throughout — no androidMain/iosMain code
in the home feature.

**Navigation uses HomeRoute.LessonDetail.** `Routes.kt` gains
`@Serializable data class LessonDetail(val lessonId: String) : HomeRoute`. HomeEntryProvider is
updated to dispatch this key to `LessonDetailScreen`. The detail ViewModel receives `lessonId`
via Koin `parametersOf`, which is the standard Koin 4 pattern. `AppNavigator` is injected into
`LiveLessonsScreen` via `koinInject()` — not into the ViewModel — so the ViewModel stays
testable without Koin.

**Koin wiring.** A new `liveLessonsModule` file inside `tabs/live/di/` holds all repository,
use-case, and ViewModel bindings. It is included by `homeModule` via `includes(liveLessonsModule)`.

**ViewModel strings pattern.** Neither `LiveLessonsViewModel` nor `LessonDetailViewModel` takes a
strings parameter — effects carry typed data (URL, lessonId) and the screen resolves string
resources itself. This is simpler than the Schedule/Teachers pattern because there are no
"action snackbar messages" that differ by string — only structured effects.

**Build + test commands** (from actual `feature/home/build.gradle.kts`):
- Compile check: `.\gradlew :feature:home:compileCommonMainKotlinMetadata`
- Tests: `.\gradlew :feature:home:jvmTest`
- Designsystem compile check: `.\gradlew :core:designsystem:compileCommonMainKotlinMetadata`

---

## Implementation Plan

### FEAT-001 — Foundation (strings, shared UI, platform actions)

- [ ] 1. Add 28 new string keys to `core/designsystem/src/commonMain/composeResources/values/strings.xml`.
      Keys: `live_lessons_title`, `live_upcoming_title`, `live_next_title`, `live_join_zoom`,
      `live_starts_in` (%1$d min), `live_status_upcoming`, `live_status_live_now`, `live_status_ended`,
      `live_calendar_sync_title`, `live_calendar_sync_desc`, `live_calendar_sync_btn`,
      `live_calendar_synced`, `live_no_upcoming`, `live_no_lessons`, `lesson_detail_title`,
      `lesson_files_title`, `lesson_exams_title`, `lesson_file_preview`, `lesson_file_download`,
      `lesson_exam_timed`, `lesson_exam_open`, `lesson_exam_start`, `lesson_no_files`,
      `lesson_no_exams`, `live_refresh`, `live_error_retry`, `lesson_exam_view`, `live_sync_success`.
      Files: `core/designsystem/src/commonMain/composeResources/values/strings.xml`
      Verify: `.\gradlew :core:designsystem:compileCommonMainKotlinMetadata`

- [ ] 2. Add the same 28 string keys with Arabic translations to the AR resource file.
      Files: `core/designsystem/src/commonMain/composeResources/values-ar/strings.xml`
      Verify: `.\gradlew :core:designsystem:compileCommonMainKotlinMetadata`

- [ ] 3. Add `expect fun syncCalendar(titles: List<String>, startEpochMs: List<Long>): Boolean`
      and `expect fun previewFile(url: String, mimeType: String)` to commonMain PlatformActions.
      Add convenience `fun openZoomUrl(url: String) = openUrl(url)`.
      Files: `core/designsystem/src/commonMain/kotlin/com/hussein/jana/core/designsystem/PlatformActions.kt`
      Verify: `.\gradlew :core:designsystem:compileCommonMainKotlinMetadata`

- [ ] 4. Add `actual fun syncCalendar` (CalendarContract.Events intent per lesson) and
      `actual fun previewFile` (ACTION_VIEW intent) to Android platform file.
      Files: `core/designsystem/src/androidMain/kotlin/com/hussein/jana/core/designsystem/PlatformActions.android.kt`
      Verify: same compile check

- [ ] 5. Add `actual fun syncCalendar` (delegates to `openUrl("calshow://")`, returns true) and
      `actual fun previewFile` (delegates to `openUrl(url)`) to iOS platform file.
      Files: `core/designsystem/src/iosMain/kotlin/com/hussein/jana/core/designsystem/PlatformActions.ios.kt`
      Verify: same compile check

- [ ] 6. Add `actual fun syncCalendar` (write .ics to temp, `Desktop.open()`, return true) and
      `actual fun previewFile` (delegates to `openUrl(url)`) to JVM platform file.
      Files: `core/designsystem/src/jvmMain/kotlin/com/hussein/jana/core/designsystem/PlatformActions.jvm.kt`
      Verify: `.\gradlew :core:designsystem:compileCommonMainKotlinMetadata`

- [ ] 7. Create `JanaLiveLessonComponents.kt` with 5 shared composables: `JanaStatusChip`,
      `JanaDateTimeLabel`, `JanaFileTypeIcon`, `JanaSectionHeader`, `JanaCountdownBadge`.
      All use `MaterialTheme.colorScheme` / named color tokens. No hardcoded colors or strings.
      Files: `core/designsystem/src/commonMain/kotlin/com/hussein/jana/core/designsystem/JanaLiveLessonComponents.kt`
      Verify: `.\gradlew :core:designsystem:compileCommonMainKotlinMetadata`

---

### FEAT-002 — Domain + data layer + list screen MVI

*All files in `feature/home/src/commonMain/kotlin/com/hussein/jana/feature/home/tabs/live/`
unless otherwise stated.*

- [ ] 8. Create domain models: `LessonFileType.kt`, `LessonFile.kt`, `LessonExam.kt`, `LiveLesson.kt`
      (with nested `LessonStatus` enum: UPCOMING, LIVE_NOW, ENDED).
      Files: `domain/model/{LessonFileType,LessonFile,LessonExam,LiveLesson}.kt`
      Verify: `.\gradlew :feature:home:compileCommonMainKotlinMetadata`

- [ ] 9. Create `LiveLessonRepository` interface with 6 suspend methods returning `Result<T>`.
      Files: `domain/repository/LiveLessonRepository.kt`
      Verify: same

- [ ] 10. Create 6 use case classes, each wrapping one repository method.
       Files: `domain/usecase/{GetUpcomingLiveLessonsUseCase,GetNextLiveLessonsUseCase,
       GetLessonDetailsUseCase,GetLessonFilesUseCase,GetLessonExamsUseCase,SyncCalendarUseCase}.kt`
       Verify: same

- [ ] 11. Create 3 `@Serializable` DTO data classes.
       Files: `data/remote/dto/{LiveLessonDto,LessonFileDto,LessonExamDto}.kt`
       Verify: same

- [ ] 12. Create `LiveLessonApi` stub class with 4 suspend functions, all returning empty
       collections and each marked `// TODO: replace with real API call`.
       Files: `data/remote/LiveLessonApi.kt`
       Verify: same

- [ ] 13. Create `LiveLessonMapper.kt` with 3 top-level extension functions mapping DTOs to domain.
       Files: `data/mapper/LiveLessonMapper.kt`
       Verify: same

- [ ] 14. Create `LiveLessonRepositoryImpl` with hardcoded mock data (3 lessons, 2 files each,
       1 exam for lesson 1). Each function does `delay(300)` then `Result.success(mockData)`.
       `syncCalendar` calls `syncCalendar(titles, epochMsList)` from PlatformActions.
       All functions marked `// TODO: replace with real API call`.
       Files: `data/repository/LiveLessonRepositoryImpl.kt`
       Verify: same

- [ ] 15. Create `LiveLessonsContract.kt` with `LiveLessonsUiState`, `LiveLessonsIntent` (5 cases),
       `LiveLessonsEffect` (4 cases).
       Files: `presentation/list/LiveLessonsContract.kt`
       Verify: same

- [ ] 16. Create `LiveLessonsViewModel` extending `BaseMviViewModel`. Loads both use cases
       concurrently in `init`. Emits typed effects for navigation/zoom/sync — no string
       messages in the ViewModel.
       Files: `presentation/list/LiveLessonsViewModel.kt`
       Verify: same

- [ ] 17. Create 3 list-screen components: `UpcomingLessonCard`, `NextLessonItem`, `CalendarSyncCard`.
       All stateless, use tokens from Color.kt, components from Components.kt and
       JanaLiveLessonComponents.kt.
       Files: `presentation/list/components/{UpcomingLessonCard,NextLessonItem,CalendarSyncCard}.kt`
       Verify: same

- [ ] 18. Create stateful `LiveLessonsScreen` + stateless `LiveLessonsContent`. Screen injects
       `AppNavigator` via `koinInject()`. Collects effects and routes: detail → navigator.navigate,
       zoom → openZoomUrl, sync → snackbar, error → snackbar.
       Files: `presentation/list/LiveLessonsScreen.kt`
       Verify: same

- [ ] 19. Replace stub content of `LiveLessonsTabScreen.kt` with a single call: `LiveLessonsScreen()`.
       Files: `tabs/live/LiveLessonsTabScreen.kt`
       Verify: same

- [ ] 20. Add `@Serializable data class LessonDetail(val lessonId: String) : HomeRoute` to Routes.kt.
       Files: `core/navigation/src/commonMain/kotlin/com/hussein/jana/core/navigation/Routes.kt`
       Verify: `.\gradlew :core:navigation:compileCommonMainKotlinMetadata`

- [ ] 21. Create `LiveLessonsModule.kt` with repository (single), use cases (factory ×6),
       `LiveLessonsViewModel` (viewModel), and a stub binding for `LessonDetailViewModel`
       (viewModel { (lessonId: String) → LessonDetailViewModel(lessonId, get(), get(), get()) }).
       Update `HomeModule.kt` to `includes(liveLessonsModule)`.
       Files: `tabs/live/di/LiveLessonsModule.kt`,
              `feature/home/src/commonMain/kotlin/com/hussein/jana/feature/home/di/HomeModule.kt`
       Verify: `.\gradlew :feature:home:compileCommonMainKotlinMetadata`

- [ ] 22. Write `LiveLessonsViewModelTest` covering: initial state, loadLessons success, refresh,
       JoinLiveLesson effect, OpenLessonDetail effect, SyncCalendar effect. Use fake use cases
       that return `Result.success()` synchronously.
       Files: `feature/home/src/jvmTest/kotlin/.../tabs/live/LiveLessonsViewModelTest.kt`
       Verify: `.\gradlew :feature:home:jvmTest`

---

### FEAT-003 — Detail screen MVI + navigation integration

- [ ] 23. Create `LessonDetailContract.kt` with `LessonDetailUiState` (isLoading, lesson, files,
       exams, error), `LessonDetailIntent` (4 cases), `LessonDetailEffect` (4 cases).
       Files: `tabs/live/presentation/detail/LessonDetailContract.kt`
       Verify: `.\gradlew :feature:home:compileCommonMainKotlinMetadata`

- [ ] 24. Create `LessonDetailViewModel(lessonId, getLessonDetails, getLessonFiles, getLessonExams)`.
       Loads all 3 use cases concurrently in `init`. Maps `LessonFileType` to MIME type for
       `OpenFilePreview`. Emits `ShowExamSnackbar` for `StartExam` (exam server integration is a TODO).
       Files: `tabs/live/presentation/detail/LessonDetailViewModel.kt`
       Verify: same

- [ ] 25. Create 5 detail-screen components: `LessonInfoHeader`, `LessonFilesSection`,
       `LessonFileItem`, `LessonExamSection`, `LessonExamCard`. All stateless.
       `LessonFileItem` uses `JanaFileTypeIcon`. `LessonExamCard` uses `JanaStatusChip`-style
       chips for timed/open state.
       Files: `tabs/live/presentation/detail/components/{LessonInfoHeader,LessonFilesSection,
       LessonFileItem,LessonExamSection,LessonExamCard}.kt`
       Verify: same

- [ ] 26. Create stateful `LessonDetailScreen(lessonId, onBack)` using
       `koinViewModel { parametersOf(lessonId) }`. Collect effects: OpenFilePreview →
       `previewFile(url, mimeType)`, DownloadFile → `openUrl(url)`, ShowExamSnackbar/ShowError →
       snackbar. Stateless `LessonDetailContent` uses `Scaffold` + `JanaTopBar(onBackClick=onBack)`.
       Files: `tabs/live/presentation/detail/LessonDetailScreen.kt`
       Verify: same

- [ ] 27. Update `HomeEntryProvider` to handle `HomeRoute.LessonDetail`:
       `is HomeRoute.LessonDetail → NavEntry(key) { val nav: AppNavigator = koinInject(); LessonDetailScreen(key.lessonId) { nav.back() } }`.
       Files: `feature/home/src/commonMain/kotlin/com/hussein/jana/feature/home/HomeEntryProvider.kt`
       Verify: `.\gradlew :feature:home:compileCommonMainKotlinMetadata`

- [ ] 28. Write `LessonDetailViewModelTest` using fake use cases returning pre-built mock data.
       Tests: initial state, loadDetail success populates all fields, PreviewFile PDF/DOCX/PPTX
       MIME types, DownloadFile effect, StartExam snackbar effect.
       Files: `feature/home/src/jvmTest/kotlin/.../tabs/live/LessonDetailViewModelTest.kt`
       Verify: `.\gradlew :feature:home:jvmTest`

---

## Cross-cutting rules (enforced in all FEATs)

- **No hardcoded strings** in composables — all labels from `Res.string.*` from core/designsystem.
- **No hardcoded colors** — use named tokens (Color.kt) or `MaterialTheme.colorScheme.*`.
- **RTL-aware** — use `Modifier.fillMaxWidth()` + `Arrangement.Start` / `Arrangement.End`;
  use `Icons.AutoMirrored.*` where direction matters.
- **State hoisting** — all stateless `Content` composables receive `state` + `onIntent` from their
  stateful wrapper. Components receive typed values + lambdas, never ViewModels.
- **MVI only** — no business logic in Composables. ViewModel state is immutable data class.
  Effects use `Channel.BUFFERED` (via BaseMviViewModel).
- **Mock data** — `LiveLessonRepositoryImpl` returns hardcoded data; each function marked
  `// TODO: replace with real API call`.
- **Koin only** — no manual DI, no `remember { MyClass() }` for dependencies.
