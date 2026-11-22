# Locked courses + Subscribe & Pay: implementation notes

**Tests intentionally skipped per user request.** Plan item 5 (jvmTest coverage) was not implemented. No new jvmTest files exist for this feature. Test files written before the user's decision were deleted before commit. Existing tests were run only to confirm they still compile and pass after the model changes.

Plan: `.agents/tasks/locked_courses_plan.md`. This is the first iteration (no `locked_courses_review.json` existed).

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
- Tests (skipped per user request).
- Real gateway redirect (`checkoutUrl`). There is a TODO in `CourseDetailViewModel.startPayment()`.
- No polling for PENDING payments. The student sees the pending message and can retry.
- `:desktopApp:compileKotlin` and a manual run were not part of the required verification and were not run.
