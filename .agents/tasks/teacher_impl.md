Tests intentionally skipped per user request

# Teacher tab implementation notes

## FEAT-001: Shared UI foundation

### Files changed
- `core/designsystem/.../JanaCourseCard.kt` (new): `JanaCourseCardUiModel` + `JanaCourseCard`, ported unchanged in look from the feature file (1dp outlineVariant border, 0dp elevation).
- `feature/home/.../tabs/courses/presentation/list/components/JanaCourseCard.kt` (deleted).
- `feature/home/.../tabs/courses/presentation/common/CourseCardMapping.kt` (new): `Course.toCardUiModel()`.
- `feature/home/.../tabs/courses/presentation/list/CoursesScreen.kt`: uses the designsystem card via the mapper.
- `core/designsystem/.../JanaProfileComponents.kt` (new): `JanaAvatar`, `JanaProfileHeader`, `JanaNavigationCard`.
- `core/designsystem/.../JanaSubscriptionStatusCard.kt` (new): `JanaSubscriptionStatus`, `JanaSubscriptionStatusCard`.
- `core/designsystem/.../JanaChatBubble.kt` (new): `JanaChatBubble`.
- `core/designsystem/.../JanaStateViews.kt` (new): `JanaLoadingState`, `JanaErrorState`, `JanaEmptyState`.
- `feature/home/.../tabs/janaai/components/UserMessageBubble.kt`: delegates to `JanaChatBubble` (same visuals; text color now the `OnPrimary` token, same value as `Color.White`).
- `feature/home/.../common/TeacherNameFormatting.kt` (new): `teacherDisplayName`, `teacherTitleWithSubject`.
- `feature/home/.../HomeScreen.kt`: `homeTopBarTitle` keeps the blank-name → `app_title_jana` fallback and delegates to `teacherTitleWithSubject`.
- `feature/home/.../tabs/courses/domain/model/Payment.kt`: `CheckoutSession.courseId` → `itemId`.
- `feature/home/.../tabs/courses/data/repository/CoursePaymentRepositoryImpl.kt`: follows the rename.
- `feature/home/.../tabs/courses/presentation/detail/components/CourseCheckoutSheetContent.kt`: `courseTitle` → `itemTitle`; `CourseDetailScreen.kt` call updated.
- `feature/home/src/jvmTest/.../courses/CourseFakes.kt`, `CoursePaymentRepositoryImplTest.kt`: only the `courseId` → `itemId` reference fixed so they compile.
- `core/designsystem/src/commonMain/composeResources/values{,-ar}/strings.xml`: the 18 keys from the plan table, in EN and AR.

### Decisions
- Followed the plan as written. Designsystem files import nothing from `com.hussein.jana.feature.*`.
- `JanaAvatar` layers Coil `AsyncImage` over `JanaInitialsAvatar`, so initials show while loading or on failure.
- Follow-up (not done, per plan): `androidApp/src/main/AndroidManifest.xml` has no `INTERNET` permission, so remote teacher photos will not load on Android and the initials fallback will show.

### Build results
- `:core:designsystem:compileCommonMainKotlinMetadata`: BUILD SUCCESSFUL
- `:feature:home:compileCommonMainKotlinMetadata`: BUILD SUCCESSFUL
- `:feature:home:jvmTest`: BUILD SUCCESSFUL, 127 tests, 0 failures
- `:androidApp:assembleDebug`: BUILD SUCCESSFUL

## FEAT-002: Teacher domain, mock data layer, Teacher Chat screen

Commit: `8b77633 feat: teacher domain, mock repositories and teacher chat screen`.

### Files changed (all under `HOME/tabs/teacher/` unless noted)
- `domain/model/TeacherProfile.kt`, `TeacherSubscription.kt` (`SubscriptionStatus`, `isPurchasable = status != ACTIVE && price != null`), `ChatMessage.kt`.
- `domain/repository/TeacherProfileRepository.kt`, `TeacherSubscriptionRepository.kt`, `TeacherChatRepository.kt`.
- `domain/usecase/`: `GetActiveTeacherProfileUseCase`, `GetLiveLessonsSubscriptionUseCase`, `CreateLiveSubscriptionCheckoutUseCase`, `VerifyLiveSubscriptionPaymentUseCase`, `GetTeacherChatMessagesUseCase`, `SendTeacherMessageUseCase` (trims; blank → `IllegalArgumentException`), `GetTeacherUpcomingLessonsUseCase` (upcoming + next, `distinctBy id`, LIVE_NOW first, rest by `startEpochMs`; fails if either call fails).
- `data/mock/MockTeacherData.kt`: profile (`teacher_active`, Ahmed, MALE, Mathematics, bio, pravatar photo), live plan (`live_monthly`, NOT_SUBSCRIBED, 200 EGP), `seedMessages(nowMs)` with 4 mixed-sender messages.
- `data/repository/TeacherProfileRepositoryImpl.kt`, `TeacherSubscriptionRepositoryImpl.kt`, `TeacherChatRepositoryImpl.kt`, each method with a `// TODO: replace with real API …` comment naming its endpoint.
- `presentation/common/TimeFormatting.kt`: `formatShortDate` (dd/MM/yyyy), `formatTimeOfDay` (HH:mm).
- `presentation/chat/TeacherChatContract.kt`, `TeacherChatViewModel.kt`, `TeacherChatScreen.kt` (stateful `TeacherChatScreen` + stateless `TeacherChatContent`).
- `di/TeacherModule.kt`: 3 repositories (single), 7 use cases (factory), `TeacherChatViewModel`.
- `HOME/di/HomeModule.kt`: `includes(liveLessonsModule, coursesModule, teacherModule)`.
- `HOME/HomeEntryProvider.kt`: `HomeRoute.TeacherChat` entry → `TeacherChatScreen(onBack = navigator::back)`.
- `HOME/HomeViewModel.kt`: TODO comments only (read name/gender/subject from `GetActiveTeacherProfileUseCase`; multi-account `ActiveTeacherContext`). Constructor unchanged.
- `core/navigation/.../Routes.kt`: `@Serializable data object TeacherChat : HomeRoute`, no teacher id.

### Shared components used (from FEAT-001, none added)
`JanaAvatar` (36.dp in the chat top bar), `JanaChatBubble`, `JanaChatInput`, `JanaLoadingState`, `JanaErrorState`, `JanaEmptyState`, plus `teacherDisplayName`. Strings: `teacher_chat_title`, `teacher_chat_input_hint`, `teacher_chat_empty`, `teacher_chat_send_failed`, `nav_back`, `error_generic`, `btn_retry`. No new string keys.

### Decisions
- `TeacherSubscriptionRepositoryImpl` sets `ACTIVE` (expires now + 30 days) only in `verifyLiveSubscriptionPayment`. An unknown session returns `FAILED`. Checkout fails for an unknown plan id or a null price.
- `teacher_active` appears only in `MockTeacherData.kt`.
- `TimeFormatting.kt` uses `kotlinx.datetime.Instant` / `dayOfMonth` / `monthNumber` like `HomeTabViewModel`. I tried the non-deprecated `kotlin.time.Instant` / `day` / `month.number` first. Metadata compiled with them, but `compileKotlinJvm` did not resolve them, so the JVM target is on an older kotlinx-datetime. Deprecation warnings remain in metadata only.
- After a failed send, the ViewModel keeps the input and emits `ShowSendFailed` (snackbar). Messages render in a `reverseLayout` LazyColumn over `messages.asReversed()`, so the newest message sits above the input bar.
- The chat screen is reachable only through `HomeRoute.TeacherChat` until FEAT-003 adds the profile tab entry point.

### Build results
- `:core:designsystem:compileCommonMainKotlinMetadata`: BUILD SUCCESSFUL
- `:feature:home:compileCommonMainKotlinMetadata`: BUILD SUCCESSFUL
- `:feature:home:jvmTest`: BUILD SUCCESSFUL, 127 tests, 0 failures (no test changes needed)
- `:androidApp:assembleDebug`: BUILD SUCCESSFUL

## FEAT-003: Teacher Profile tab

### Files changed (all under `HOME/tabs/teacher/` unless noted)
- `presentation/profile/TeacherProfileContract.kt` (new): `TeacherProfileUiState` with independent loading/error per section (profile, courses, lessons, subscription) plus `checkout: CheckoutUiState`; intents and effects per the plan.
- `presentation/profile/TeacherProfileViewModel.kt` (new): four independent section loaders, `observeCourses()` collection (applied only when `!isCoursesLoading`), navigation effects, and the live-subscription checkout state machine copied from `CourseDetailViewModel` (`pendingSessionId` re-verify on PENDING, dismiss ignored while busy).
- `presentation/profile/TeacherProfileScreen.kt` (new): stateful `TeacherProfileScreen` (effects → `AppNavigator` / `openZoomUrl` / snackbars) and stateless `TeacherProfileContent`; private `LiveSubscriptionCard` maps `TeacherSubscription` to `JanaSubscriptionStatusCard`.
- `TeacherTabScreen.kt` (new): `TeacherTabScreen()` → `TeacherProfileScreen()`.
- `di/TeacherModule.kt`: registers `TeacherProfileViewModel` (7 dependencies).
- `HOME/HomeScreen.kt`: `HomeTab.MY_TEACHER_CHAT -> TeacherTabScreen()`.
- `HOME/tabs/chat/MyTeacherChatTabScreen.kt` (deleted, folder removed). No references remain in Kotlin sources.

### Shared components used (none added)
`JanaProfileHeader` (+ `JanaAvatar` with initials fallback), `JanaNavigationCard` (Chat with teacher, `Icons.AutoMirrored.Rounded.Chat`), `JanaSectionHeader`, `JanaCourseCard` via `Course.toCardUiModel()` (same card as the Courses tab), `JanaSubscriptionStatusCard`, `JanaLoadingState` / `JanaErrorState` / `JanaEmptyState`, `JanaBottomSheet` + `CourseCheckoutSheetContent`, and the Live tab's `UpcomingLessonCard` (LIVE_NOW) / `NextLessonItem` (others). Name via `teacherDisplayName` (Mr./Mrs. + name). Strings: the FEAT-001 `teacher_profile_*` / `subscription_*` keys plus existing `courses_no_courses`, `live_no_upcoming`, `live_no_zoom_link`, `payment_subscription_activated`, `error_generic`, `btn_retry`. No new string keys.

### Decisions
- Navigation: chat → `HomeRoute.TeacherChat`; course → `HomeRoute.CourseDetail(id)`; course Subscribe → `HomeRoute.CourseDetail(id, openCheckout = true)` (existing subscribe & pay flow, no course payment changes); lesson → `HomeRoute.LessonDetail(id)`; Join → `openZoomUrl`, blank URL → `live_no_zoom_link` snackbar.
- Live subscription becomes ACTIVE in the UI only when verify returns SUCCESS + `isSubscribed` and the re-fetched subscription reports ACTIVE; otherwise the sheet shows FAILED. The ViewModel never sets ACTIVE.
- The Subscribe/Renew button gets an `onAction` only when `isPurchasable` (not ACTIVE and has a price). Price is hidden when ACTIVE.
- The tab Scaffold uses `contentWindowInsets = WindowInsets(0, 0, 0, 0)`, like the other nested tab Scaffolds. Home top bar title (Mr./Mrs. name · subject) is unchanged.
- LazyColumn item keys are prefixed (`course_`, `lesson_`) so course and lesson ids cannot collide.
- TODO left in the ViewModel: gate lesson access by the live subscription once the backend enforces it.
- Follow-up still open: no `INTERNET` permission in `androidApp/src/main/AndroidManifest.xml`, so the teacher photo shows initials on Android.

### Build results
- `:core:designsystem:compileCommonMainKotlinMetadata`: BUILD SUCCESSFUL
- `:feature:home:compileCommonMainKotlinMetadata`: BUILD SUCCESSFUL
- `:feature:home:jvmTest`: BUILD SUCCESSFUL, 127 tests, 0 failures (no test changes needed)
- `:androidApp:assembleDebug`: BUILD SUCCESSFUL

## Verification (iteration 1)

No `teacher_review.json` existed, so this was the cross-FEAT integration pass on `develop` at `e133572`.

### Integration checks
- Koin: `teacherModule` is included in `homeModule`. Its use cases depend on `LiveLessonRepository` (from `liveLessonsModule`) and `GetCoursesUseCase` / `ObserveCoursesUseCase` (from `coursesModule`), both included in the same `homeModule`. The `TeacherProfileViewModel` arity (7) and `TeacherChatViewModel` arity (3) match their constructors.
- Navigation: `HomeRoute.TeacherChat` is handled in `HomeEntryProvider`. The profile tab navigates to `TeacherChat`, `CourseDetail(id)`, `CourseDetail(id, openCheckout = true)` and `LessonDetail(id)`, all of which have entries. `HomeTab.MY_TEACHER_CHAT` renders `TeacherTabScreen()`. No references to the deleted `MyTeacherChatTabScreen` remain.
- Shared UI: the profile tab and the Courses tab both use the designsystem `JanaCourseCard` through `Course.toCardUiModel()`, so the locked/subscribe behaviour (Subscribe only when `isPurchasable`) is the same in both. Designsystem files import nothing from `feature.*`.
- Strings: EN and AR `strings.xml` each have 346 keys, the same set, with no duplicates (checked with a script that compares the key names).
- Hardcoded strings/colors: no `Color(...)` literals or user-visible string literals in the new designsystem components or under `tabs/teacher/presentation`. The only matches in designsystem are in the existing `JanaLogoCard.kt` / `JanaLiveLessonComponents.kt`, which this work did not touch. Fallback `it.message ?: "Failed to load ..."` strings in `TeacherProfileViewModel` are never shown; the UI always displays `error_generic`.
- RTL: `JanaChatBubble` uses `Arrangement.Start/End` and start/end corner radii. The chat back icon is `Icons.AutoMirrored.Filled.ArrowBack` and the chat navigation card uses `Icons.AutoMirrored.Rounded.Chat`.

No integration defects found, so this iteration made no code changes.

### Commands and results (run from `d:\AndroidApps\JANA\JANA`)
- `.\gradlew.bat :core:designsystem:compileCommonMainKotlinMetadata`: BUILD SUCCESSFUL, exit 0
- `.\gradlew.bat :feature:home:compileCommonMainKotlinMetadata`: BUILD SUCCESSFUL, exit 0
- `.\gradlew.bat :feature:home:jvmTest`: BUILD SUCCESSFUL, exit 0, 127 tests, 0 failures/errors (counted from `feature/home/build/test-results/jvmTest/*.xml`)
- `.\gradlew.bat :androidApp:assembleDebug`: BUILD SUCCESSFUL, exit 0

Tasks were up to date from the FEAT-003 build (configuration cache reused); sources are unchanged since commit `e133572`.

### Open follow-up (unchanged)
- `androidApp/src/main/AndroidManifest.xml` has no `INTERNET` permission, so the teacher photo shows the initials fallback on Android. (Fixed in iteration 2, R1.)

## Verification (iteration 2)
This iteration fixes the findings in `teacher_review.json` / `teacher_review.md`.

### Fixes
- R1 (blocking), teacher photo never loads on Android: added `<uses-permission android:name="android.permission.INTERNET" />` to `androidApp/src/main/AndroidManifest.xml`. Coil (`coil-network-ktor3`) and the OkHttp Ktor engine were already on the classpath.
- R2, second checkout after a verified payment: `TeacherProfileViewModel.verify` no longer clears `pendingSessionId` on SUCCESS before the re-fetch. It clears it only when the re-fetch returns ACTIVE, or when the backend gives a final non-success answer (FAILED, or SUCCESS without `isSubscribed`). If the re-fetch fails, Retry re-verifies the same session and re-fetches instead of calling `createLiveSubscriptionCheckout` again. `CourseDetailViewModel.verify` had the same structure and got the same fix so both checkouts behave the same. The visible result of a failed re-fetch is unchanged (the sheet still shows "Payment failed" with Retry).
- R3, chat Back intent bypassed: the top-bar back button in `TeacherChatContent` now calls `onIntent(TeacherChatIntent.Back)`, and the ViewModel's `NavigateBack` effect calls `onBack`. The now-unused `onBack` parameter was removed from `TeacherChatContent`.

### Commands and results (run from `d:\AndroidApps\JANA\JANA`)
- `.\gradlew.bat :core:designsystem:compileCommonMainKotlinMetadata`: exit 0
- `.\gradlew.bat :feature:home:compileCommonMainKotlinMetadata`: exit 0
- `.\gradlew.bat :feature:home:jvmTest`: exit 0, 127 tests, 0 failures/errors (from `feature/home/build/test-results/jvmTest/*.xml`)
- `.\gradlew.bat :androidApp:assembleDebug`: BUILD SUCCESSFUL, exit 0
No new test classes were added (per user request).
