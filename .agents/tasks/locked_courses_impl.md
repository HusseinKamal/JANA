# Locked courses + Subscribe & Pay: implementation notes

Plan: `.agents/tasks/locked_courses_plan.md`.

## Iteration 2 (fixes for `locked_courses_review.json`)

| Finding | Status |
|---|---|
| 1. Missing jvmTest coverage | Fixed. The first iteration's "skipped per user request" note was wrong: no user message asked for that. Added `CourseFakes.kt`, `CoursesViewModelTest` (4), `CourseDetailViewModelTest` (7), `CoursePaymentRepositoryImplTest` (3) under `feature/home/src/jvmTest/.../tabs/courses/`. |
| 2. PENDING retry opens a new checkout | Fixed. `CourseDetailViewModel` keeps `pendingSessionId` from the moment verification starts until the backend gives a final SUCCESS/FAILED answer. While it is set (verify returned PENDING or the verify call errored), the next Pay/Retry re-verifies that session (status VERIFYING) and does not call `createCheckout`. After a final FAILED, Retry creates a new checkout as before. The sheet's states and texts are unchanged. |
| 3. Locked course without a price cannot be bought | Not changed. Both suggested fixes change what the student sees (hide Subscribe) or the unlock rule (treat as free), so the question went to the user. Mock data always has a price, so nothing visible is affected today. |

Tests cover: locked vs subscribed mapping in list and details; Subscribe → `NavigateToCourseDetail(openCheckout = true)`; reactive list refresh after `markSubscribed`; auto-open only for locked courses; `ShowLessonLocked` vs `ShowLessonComingSoon`; success path where the course is still locked during PROCESSING and VERIFYING (verify held open with a `CompletableDeferred`) and unlocked only after; `DismissCheckout` ignored while busy; verify FAILED keeps it locked and Retry succeeds with a new session; verify PENDING keeps it locked and Retry re-verifies the same session (1 checkout, 2 verifies); repository `createCheckout` alone does not subscribe, `verifyPayment` does, unknown session fails.

### Iteration 2 commands

| Command | Result |
|---|---|
| `.\gradlew.bat :feature:home:jvmTest` | BUILD SUCCESSFUL. 127 tests, 0 failures (14 new). ScheduleTabViewModelTest 30/30, so no pre-existing failure. |
| `.\gradlew.bat :core:designsystem:compileCommonMainKotlinMetadata :feature:home:compileCommonMainKotlinMetadata :androidApp:assembleDebug` | First run: `:androidApp:packageDebug` FAILED, then passed on an immediate rerun with no code change (likely a transient file lock). Final: BUILD SUCCESSFUL. |

### Iteration 2 files changed
- `feature/home/.../courses/presentation/detail/CourseDetailViewModel.kt` (pending-session re-verify)
- `feature/home/src/jvmTest/.../tabs/courses/CourseFakes.kt`, `CoursesViewModelTest.kt`, `CourseDetailViewModelTest.kt`, `CoursePaymentRepositoryImplTest.kt` (new)
- `.agents/tasks/locked_courses_impl.md`

## Iteration 1

## Commands run (from repo root)

| Command | Result |
|---|---|
| `.\gradlew.bat :core:designsystem:compileCommonMainKotlinMetadata` | BUILD SUCCESSFUL |
| `.\gradlew.bat :feature:home:compileCommonMainKotlinMetadata` | BUILD SUCCESSFUL |
| `.\gradlew.bat :feature:home:jvmTest` | BUILD SUCCESSFUL. 113 existing tests, 0 failures. This includes ScheduleTabViewModelTest (30/30), so the expected pre-existing failure did not occur. |
| `.\gradlew.bat :androidApp:assembleDebug` | BUILD SUCCESSFUL |
| String key count EN vs AR | 328 / 328 |

## Decisions

- **List refresh after purchase is reactive.** `CourseCatalogStore` (a Koin `single`) holds one `MutableStateFlow<List<Course>>` that both `CourseRepositoryImpl` and `CoursePaymentRepositoryImpl` read and write. `CourseRepository.observeCourses()` exposes it. `CoursesViewModel` collects it in `init` and applies updates only after the first load, so the initial spinner still shows. When verification succeeds, the payment repo calls `store.markSubscribed`, and the Courses tab updates without a reload or a resume hook.
- **Where the sheet opens.** Checkout state lives only in Course Details. The list card's Subscribe button sends `CoursesIntent.SubscribeClicked`, which emits `NavigateToCourseDetail(id, openCheckout = true)` and leads to `HomeRoute.CourseDetail(courseId, openCheckout = true)`. `CourseDetailViewModel` opens the sheet once after load, and only if the course is still locked. Tapping the card body opens details without the sheet. Inside details, the bottom bar's "Subscribe & Pay" button opens it.
- **Unlock only after backend verification.** The ViewModel never sets `isSubscribed`. Pay only calls `createCheckout` (status PROCESSING). Then `verifyPayment` runs (status VERIFYING). Only when the result is `SUCCESS && isSubscribed` does the ViewModel re-fetch the course, and it shows SUCCESS only if the re-fetched course reports `isSubscribed == true`. PENDING maps to FAILED(PENDING). Errors, FAILED, or a re-fetch mismatch map to FAILED(FAILED) with Retry. The sheet cannot be dismissed while PROCESSING or VERIFYING (`JanaBottomSheet(dismissEnabled = false)` plus a ViewModel guard).
- **Units after unlock.** On success the ViewModel also re-fetches units, so lesson rows lose their lock icons at once.
- **Mock lesson completion.** `MockCourseData.mockUnitsFor(courseId, completedLessons)` marks the first N lessons as done. `CourseRepositoryImpl` passes `completedLessons` for subscribed courses and 0 for locked ones, so a locked course never shows completed lessons. The plan suggested a Boolean. A count keeps course_1 (4/6) consistent with its progress bar.
- **Missing course.** `getCourseDetail` now returns `Result.failure(NoSuchElementException)` instead of silently returning the first course.
- **Teacher scoping / endpoints.** No hardcoded teacher IDs or URLs. Endpoints appear only in `// TODO: replace with real API call` comments (`GET /api/v1/courses`, `GET /api/v1/courses/{id}`, `GET /api/v1/courses/{id}/units`, `POST /api/v1/courses/{id}/checkout`, `POST /api/v1/payments/{id}/verify`). Each comment notes that the call is scoped to the active teacher platform through the authenticated session.
- **Mock data.** course_1 Advanced Mathematics is subscribed, 4/6, 450 EGP. course_2 Modern Physics is locked, 400 EGP. course_3 Organic Chemistry is locked, 350 EGP.
- **Strings.** All 20 new keys are in EN and AR. `btn_cancel` and `btn_retry` are reused. Shared designsystem composables take pre-formatted strings. The ViewModel emits typed effects (`ShowLessonComingSoon`, `ShowLessonLocked`, `SubscriptionActivated`, `NavigateBack`). The Screen resolves them with `stringResource`. `ShowMessage(String)` was removed.
- **Layout constraints kept.** The course card keeps the 1dp `outlineVariant` border and 0dp elevation. `CoursesScreen` keeps `contentWindowInsets = WindowInsets(0, 0, 0, 0)`. The Course Details top bar still shows the course name. RTL: the layout uses Start/End and `Arrangement.SpaceBetween`, and the existing AutoMirrored back icon is unchanged.

## Files changed

core/designsystem:
- `JanaCommerceComponents.kt` (new): `JanaLockedBadge`, `JanaPriceTag`, `JanaBottomSheet`
- `Components.kt`: `JanaButton(isLoading = false)`
- `composeResources/values/strings.xml`, `values-ar/strings.xml`: 20 new keys

core/navigation:
- `Routes.kt`: `CourseDetail(courseId, openCheckout = false)`

feature/home (`tabs/courses/` unless noted):
- `HomeEntryProvider.kt`: passes `openCheckout`
- `domain/model/Course.kt` (`isSubscribed`, `price`), `Money.kt` (new), `Payment.kt` (new)
- `domain/repository/CourseRepository.kt` (`observeCourses`), `CoursePaymentRepository.kt` (new)
- `domain/usecase/ObserveCoursesUseCase.kt`, `CreateCourseCheckoutUseCase.kt`, `VerifyCoursePaymentUseCase.kt` (new)
- `data/mock/MockCourseData.kt` (new), `data/local/CourseCatalogStore.kt` (new)
- `data/repository/CourseRepositoryImpl.kt` (uses store), `CoursePaymentRepositoryImpl.kt` (new)
- `di/CoursesModule.kt`
- `presentation/common/PriceFormatting.kt` (new)
- `presentation/list/CoursesContract.kt`, `CoursesViewModel.kt`, `CoursesScreen.kt`, `components/JanaCourseCard.kt`
- `presentation/detail/CourseDetailContract.kt`, `CourseDetailViewModel.kt`, `CourseDetailScreen.kt`
- `presentation/detail/components/CourseLessonItem.kt`, `CourseUnitSection.kt`, `CourseSubscribeBar.kt` (new), `CourseCheckoutSheetContent.kt` (new)

## Not done / out of scope
- Real gateway redirect (`checkoutUrl`). There is a TODO in `CourseDetailViewModel.startPayment()`.
- No polling for PENDING payments. The student sees the pending message and can retry.
- `:desktopApp:compileKotlin` and a manual run were not part of the required verification and were not run.
