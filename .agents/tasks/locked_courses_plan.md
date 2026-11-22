# Implementation Plan: Locked courses + Subscribe & Pay (Jana Student app)

All paths below are absolute under `d:\AndroidApps\JANA\JANA`. Abbreviations:
- `COURSES` = `feature/home/src/commonMain/kotlin/com/hussein/jana/feature/home/tabs/courses`
- `DS` = `core/designsystem/src/commonMain/kotlin/com/hussein/jana/core/designsystem`
- `STR_EN` = `core/designsystem/src/commonMain/composeResources/values/strings.xml`
- `STR_AR` = `core/designsystem/src/commonMain/composeResources/values-ar/strings.xml`
- `TEST` = `feature/home/src/jvmTest/kotlin/com/hussein/jana/feature/home/tabs/courses`

Build/test commands (Windows, run from repo root):
- Shared UI compile: `.\gradlew.bat :core:designsystem:compileKotlinJvm`
- Feature compile: `.\gradlew.bat :feature:home:compileKotlinJvm`
- Feature tests: `.\gradlew.bat :feature:home:jvmTest`
- Full app check (last item): `.\gradlew.bat :androidApp:assembleDebug` and `.\gradlew.bat :desktopApp:compileKotlin`

## Design decisions

1. **Single in-memory store as mock backend + cache.** A new `CourseCatalogStore` (Koin `single`) holds `MutableStateFlow<List<Course>>` seeded with mock data. `CourseRepositoryImpl` and the new `CoursePaymentRepositoryImpl` both read and write that one store. List and detail therefore always agree, and a verified payment shows up everywhere. When real APIs exist, the store becomes the cache filled by API responses.
2. **List refresh after purchase is reactive.** `CourseRepository` gets `fun observeCourses(): Flow<List<Course>>`, and `CoursesViewModel` collects it in `init`. When the payment repository marks a course subscribed (after verify returns `isSubscribed = true`), the Courses tab updates on its own. No lifecycle/resume hooks, no extra network call, no flicker. The existing `Load` intent keeps its spinner and error handling.
3. **Where the payment sheet opens.** Only Course Details owns checkout state. The Subscribe button on a list card navigates to `HomeRoute.CourseDetail(courseId, openCheckout = true)`, and the detail ViewModel opens the sheet automatically after the course loads (once, and only if the course is still locked). This keeps a single payment state machine in one ViewModel, and the student sees the syllabus behind the sheet. The alternative, a sheet on the list, would duplicate the state machine in `CoursesViewModel`.
4. **Unlock only after backend verification.** The ViewModel never sets `isSubscribed` itself. After `verifyPayment` returns `status == SUCCESS && isSubscribed`, the ViewModel re-fetches the course through `GetCourseDetailUseCase` and shows the fetched value. Tapping Pay only starts `createCheckout`.
5. **No `SubscribeToCourseUseCase`.** The ViewModel needs to show separate `PROCESSING` and `VERIFYING` states, so it calls `CreateCourseCheckoutUseCase` and then `VerifyCoursePaymentUseCase`. An orchestrating use case would hide those steps.
6. **Price model.** `data class Money(val amount: Double, val currencyCode: String)`. A value class cannot hold two fields. `Course.price: Money? = null` (null means free or unknown, and the price UI is hidden). Skip `originalPrice` because it is not trivial.
7. **Teacher scoping.** The codebase has no active-teacher abstraction (grep for `activeTeacher|teacherId` finds only the Teachers tab intents). Do not add teacherId parameters or hardcoded IDs/URLs. TODO comments say the real endpoints are scoped to the active teacher platform by the authenticated session. Endpoint paths appear only in TODO comments, as the existing `CourseRepositoryImpl` does.
8. **Strings formatting** follows the existing convention in `JanaCourseCard`/`CourseLessonItem`: `stringResource(key).replace("%1\$s", value)`. Shared designsystem composables take pre-formatted strings and lambdas, with no resource lookups inside them (same rule as `JanaCountdownBadge`).
9. **Typed effects.** Replace `CourseDetailEffect.ShowMessage(String)` (its string is ignored today) with typed effects. The Screen resolves every string via `stringResource`.

## Checkout state machine (CourseDetailViewModel)

```
enum class CheckoutStatus { IDLE, SELECTING_METHOD, PROCESSING, VERIFYING, SUCCESS, FAILED }
enum class PaymentFailure { FAILED, PENDING }

IDLE --SubscribeClicked / auto-open (course locked)--> SELECTING_METHOD
SELECTING_METHOD --PaymentMethodSelected(m)--> SELECTING_METHOD (selectedMethod = m)
SELECTING_METHOD --PayClicked--> PROCESSING            (createCheckout)
PROCESSING --checkout failure--> FAILED(FAILED)
PROCESSING --session ok--> VERIFYING                   (verifyPayment(session.id))
VERIFYING --SUCCESS && isSubscribed, refetch ok && course.isSubscribed--> SUCCESS
            + effect SubscriptionActivated, state.course = refetched course
VERIFYING --PENDING--> FAILED(PENDING)
VERIFYING --FAILED / error / SUCCESS w/o isSubscribed / refetch failure--> FAILED(FAILED)
FAILED --RetryPayment--> PROCESSING (same selected method)
FAILED --PaymentMethodSelected(m)--> FAILED (method changes, failure kept until retry)
SELECTING_METHOD | FAILED --DismissCheckout--> IDLE
PROCESSING | VERIFYING --DismissCheckout--> ignored (sheet not dismissable)
```
The sheet is visible when status is `SELECTING_METHOD`, `PROCESSING`, `VERIFYING`, or `FAILED`. `SUCCESS` hides it.

---

- [ ] 1. **Shared UI + strings (core/designsystem).**
      Add the reusable, state-hoisted pieces and every new EN+AR string.
      - Create `DS/JanaCommerceComponents.kt` with:
        - `@Composable fun JanaLockedBadge(text: String, modifier: Modifier = Modifier)`: a pill like `JanaStatusChip` (CircleShape `Surface`, `MaterialTheme.colorScheme.surfaceContainerHigh` container) holding `Icons.Rounded.Lock` (12.dp, `onSurfaceVariant`) and the label in `labelSmall` SemiBold.
        - `@Composable fun JanaPriceTag(priceText: String, modifier: Modifier = Modifier)`: price in `titleSmall` Bold, color `MaterialTheme.colorScheme.primary`.
        - `@OptIn(ExperimentalMaterial3Api::class) @Composable fun JanaBottomSheet(onDismissRequest: () -> Unit, modifier: Modifier = Modifier, dismissEnabled: Boolean = true, content: @Composable ColumnScope.() -> Unit)`: wraps `ModalBottomSheet`. Use `val canDismiss by rememberUpdatedState(dismissEnabled)`, `rememberModalBottomSheetState(skipPartiallyExpanded = true, confirmValueChange = { canDismiss || it != SheetValue.Hidden })`, `sheetGesturesEnabled = dismissEnabled`, and `onDismissRequest = { if (canDismiss) onDismissRequest() }`. Container color is `MaterialTheme.colorScheme.surface`. Content goes in a `Column` with `navigationBarsPadding()` and 24.dp horizontal / 8.dp top / 24.dp bottom padding.
      - Modify `DS/Components.kt` `JanaButton`: add `isLoading: Boolean = false`. When loading, the button is disabled and shows a 20.dp `CircularProgressIndicator(strokeWidth = 2.dp, color = onPrimary)` in place of the text. The default keeps every existing call site unchanged.
      - Add these keys to BOTH `STR_EN` and `STR_AR`, under a `<!-- Courses – subscription & payment -->` comment, before `</resources>`. Escape `&` as `&amp;`. Reuse the existing `btn_cancel` and `btn_retry`.

        | key | EN | AR |
        |---|---|---|
        | course_locked | Locked | مقفل |
        | course_subscribe_btn | Subscribe | اشترك |
        | course_subscribe_and_pay | Subscribe &amp; Pay | اشترك وادفع |
        | course_pay_amount | Pay %1$s | ادفع %1$s |
        | course_price_format | %1$s %2$s | %1$s %2$s |
        | currency_egp | EGP | ج.م |
        | course_unlock_full_access | Unlock all lessons, files and exams | افتح جميع الدروس والملفات والاختبارات |
        | course_subscribe_to_unlock | Subscribe to unlock this lesson | اشترك لفتح هذا الدرس |
        | course_locked_lesson_cd | Locked lesson | درس مقفل |
        | payment_sheet_title | Complete your subscription | أكمل اشتراكك |
        | payment_total_label | Total | الإجمالي |
        | payment_method_title | Payment method | طريقة الدفع |
        | payment_method_card | Credit / Debit Card | بطاقة ائتمان / خصم |
        | payment_method_wallet | Mobile Wallet | محفظة إلكترونية |
        | payment_method_fawry | Fawry | فوري |
        | payment_processing | Processing payment… | جارٍ معالجة الدفع… |
        | payment_verifying | Verifying payment… | جارٍ التحقق من الدفع… |
        | payment_subscription_activated | Subscription activated | تم تفعيل الاشتراك |
        | payment_failed | Payment failed. Please try again. | فشل الدفع. حاول مرة أخرى. |
        | payment_pending | Payment is pending confirmation | الدفع قيد التأكيد |

      Files: `DS/JanaCommerceComponents.kt` (new), `DS/Components.kt`, `STR_EN`, `STR_AR`
      Verify: `.\gradlew.bat :core:designsystem:compileKotlinJvm` succeeds. Run `(Select-String -Path <STR_EN> -Pattern '<string name=').Count` and the same for `STR_AR` and confirm the counts are equal.

- [ ] 2. **Domain + data layer (models, payment repository, shared store, DI).**
      - Modify `COURSES/domain/model/Course.kt`: add `val isSubscribed: Boolean = false` and `val price: Money? = null`.
      - Create `COURSES/domain/model/Money.kt`: `data class Money(val amount: Double, val currencyCode: String)`.
      - Create `COURSES/domain/model/Payment.kt` with `enum class PaymentMethod { CARD, WALLET, FAWRY }`, `enum class PaymentStatus { SUCCESS, PENDING, FAILED }`, `data class CheckoutSession(val id: String, val courseId: String, val amount: Double, val currencyCode: String, val checkoutUrl: String? = null)`, and `data class PaymentVerification(val checkoutId: String, val status: PaymentStatus, val isSubscribed: Boolean)`.
      - Modify `COURSES/domain/repository/CourseRepository.kt`: add `fun observeCourses(): Flow<List<Course>>`.
      - Create `COURSES/domain/repository/CoursePaymentRepository.kt`: `suspend fun createCheckout(courseId: String, method: PaymentMethod): Result<CheckoutSession>` and `suspend fun verifyPayment(checkoutId: String): Result<PaymentVerification>`.
      - Create these use cases in `COURSES/domain/usecase/`, matching the style of `GetCoursesUseCase`:
        - `ObserveCoursesUseCase(repo: CourseRepository)` with `operator fun invoke(): Flow<List<Course>>`
        - `CreateCourseCheckoutUseCase(repo: CoursePaymentRepository)` with `suspend operator fun invoke(courseId, method)`
        - `VerifyCoursePaymentUseCase(repo: CoursePaymentRepository)` with `suspend operator fun invoke(checkoutId)`
      - Create `COURSES/data/mock/MockCourseData.kt` (`internal object`). Move the 3 mock courses and `mockUnitsFor(courseId)` out of `CourseRepositoryImpl` into it:
        - course_1 "Advanced Mathematics": `isSubscribed = true`, completed 4/6, price `Money(450.0, "EGP")`
        - course_2 "Modern Physics": `isSubscribed = false`, `completedLessons = 0`, `Money(400.0, "EGP")`
        - course_3 "Organic Chemistry": `isSubscribed = false`, 0/6, `Money(350.0, "EGP")`

        In `mockUnitsFor`, a lesson's `isCompleted` must be false for locked courses. Add a `completed: Boolean` parameter and pass `course.isSubscribed`.
      - Create `COURSES/data/local/CourseCatalogStore.kt`. Add a KDoc saying it is a mock backend now and becomes the cache filled by API responses later:
        ```
        class CourseCatalogStore(initial: List<Course> = MockCourseData.courses) {
            private val _courses = MutableStateFlow(initial)
            val courses: StateFlow<List<Course>>
            fun find(courseId: String): Course?
            fun markSubscribed(courseId: String)   // _courses.update { list.map { if (id) copy(isSubscribed = true) else it } }
        }
        ```
      - Modify `COURSES/data/repository/CourseRepositoryImpl.kt` to take `(private val store: CourseCatalogStore)`.
        - `getCourses()`: `delay(400)`, then `Result.success(store.courses.value)`.
        - `getCourseDetail(id)`: `delay(400)`, then `store.find(id)`. If the course is missing, return `Result.failure(NoSuchElementException(...))` instead of silently returning `first()`.
        - `getCourseUnits` delegates to `MockCourseData.mockUnitsFor`.
        - `observeCourses() = store.courses`.
        - Keep the existing `// TODO: replace with real API call — GET ...` comments, and add "scoped to the active teacher platform via the authenticated session".
      - Create `COURSES/data/repository/CoursePaymentRepositoryImpl.kt` taking `(private val store: CourseCatalogStore)`.
        - `createCheckout`: `// TODO: replace with real API call — POST /api/v1/courses/{id}/checkout`. Call `delay(600)`. Find the course; if it is missing or has no price, fail. Otherwise return `CheckoutSession(id = "chk_${courseId}_${counter++}", courseId, price.amount, price.currencyCode, checkoutUrl = null)`. Keep the issued sessions in a private `mutableMapOf<String, CheckoutSession>()`.
        - `verifyPayment`: `// TODO: replace with real API call — POST /api/v1/payments/{id}/verify`. Call `delay(800)`. If the session is unknown, return `Result.success(PaymentVerification(id, FAILED, false))`. Otherwise call `store.markSubscribed(session.courseId)` and return `PaymentVerification(id, SUCCESS, true)`.
      - Modify `COURSES/di/CoursesModule.kt`:
        - `singleOf(::CourseCatalogStore)` (or `single { CourseCatalogStore() }`, because the constructor has a default argument)
        - `singleOf(::CoursePaymentRepositoryImpl) bind CoursePaymentRepository::class`
        - `factoryOf` for the 3 new use cases
        - Leave the ViewModel lines unchanged for now.

      Files: the domain/data/di files listed above.
      Verify: `.\gradlew.bat :feature:home:compileKotlinJvm` succeeds, and `.\gradlew.bat :feature:home:jvmTest` still passes (existing tests are untouched).

- [ ] 3. **Navigation param + Courses list (locked card, Subscribe → details with auto-open, reactive refresh).** Depends on 1 and 2.
      - Modify `core/navigation/src/commonMain/kotlin/com/hussein/jana/core/navigation/Routes.kt`: change the route to `@Serializable data class CourseDetail(val courseId: String, val openCheckout: Boolean = false) : HomeRoute`.
      - Modify `feature/home/src/commonMain/kotlin/com/hussein/jana/feature/home/HomeEntryProvider.kt`: pass `openCheckout = key.openCheckout` to `CourseDetailScreen`. To keep this item buildable, add the parameter `openCheckout: Boolean = false` to `CourseDetailScreen` now. Item 4 wires it to the ViewModel.
      - Modify `COURSES/presentation/list/CoursesContract.kt`: add `data class SubscribeClicked(val courseId: String) : CoursesIntent`, and change the effect to `NavigateToCourseDetail(val courseId: String, val openCheckout: Boolean = false)`.
      - Modify `COURSES/presentation/list/CoursesViewModel.kt`: the constructor becomes `(getCourses: GetCoursesUseCase, observeCourses: ObserveCoursesUseCase)`.
        - In `init`, keep `onIntent(Load)` and also `viewModelScope.launch { observeCourses().collect { list -> if (!currentState().isLoading) setState { copy(courses = list) } } }`. Only apply updates after the first load, so the spinner is not skipped.
        - Map `SubscribeClicked` to `sendEffect(NavigateToCourseDetail(id, openCheckout = true))`.
        - Update the Koin line to `viewModel { CoursesViewModel(get(), get()) }`.
      - Modify `COURSES/presentation/list/components/JanaCourseCard.kt`: add `onSubscribeClick: () -> Unit`. Keep the 1dp `outlineVariant` border and 0dp elevation.
        - If `course.isSubscribed` is false: overlay a centred `Icons.Rounded.Lock` (24.dp, `onSurfaceVariant`) on the thumbnail `Box`. Under the title, show a `Row` with `JanaLockedBadge(stringResource(course_locked))` and a `JanaPriceTag(formattedPrice)` aligned to the end (only if `price != null`). Replace the progress bar and progress label. The button label is `course_subscribe_btn` and its `onClick = onSubscribeClick`. Tapping the card body still calls `onClick`, which opens details.
        - If subscribed: the current UI is unchanged.
      - Create `COURSES/presentation/common/PriceFormatting.kt`:
        - `internal fun Money.amountText(): String`: whole numbers without decimals (`450`). Otherwise use two decimals, built from integer math: `val cents = (amount * 100).roundToLong()`, then `"${cents / 100}.${(cents % 100).toString().padStart(2, '0')}"`. `String.format` is not in commonMain.
        - `@Composable internal fun Money.formatted(): String`: `stringResource(course_price_format).replace("%1\$s", amountText()).replace("%2\$s", currencyLabel())`, where `currencyLabel()` is `"EGP" -> stringResource(currency_egp)`, `else -> currencyCode`.
      - Modify `COURSES/presentation/list/CoursesScreen.kt`: pass `onSubscribeClick = { onIntent(CoursesIntent.SubscribeClicked(course.id)) }`. Navigate with `HomeRoute.CourseDetail(effect.courseId, effect.openCheckout)`. Keep `contentWindowInsets = WindowInsets(0, 0, 0, 0)`.

      Files: `Routes.kt`, `HomeEntryProvider.kt`, `CourseDetailScreen.kt` (parameter only), `CoursesContract.kt`, `CoursesViewModel.kt`, `CoursesModule.kt`, `JanaCourseCard.kt`, `presentation/common/PriceFormatting.kt` (new), `CoursesScreen.kt`
      Verify: `.\gradlew.bat :feature:home:compileKotlinJvm` succeeds, and `.\gradlew.bat :feature:home:jvmTest` passes.

- [ ] 4. **Course Details: locked preview, subscribe bar, payment sheet, state machine.** Depends on 1–3.
      - Modify `COURSES/presentation/detail/CourseDetailContract.kt`:
        ```
        enum class CheckoutStatus { IDLE, SELECTING_METHOD, PROCESSING, VERIFYING, SUCCESS, FAILED }
        enum class PaymentFailure { FAILED, PENDING }
        data class CheckoutUiState(
            val status: CheckoutStatus = CheckoutStatus.IDLE,
            val selectedMethod: PaymentMethod = PaymentMethod.CARD,
            val availableMethods: List<PaymentMethod> = PaymentMethod.entries,
            val failure: PaymentFailure? = null,
        ) { val isVisible: Boolean  // SELECTING_METHOD, PROCESSING, VERIFYING, FAILED
            val isBusy: Boolean }   // PROCESSING, VERIFYING
        data class CourseDetailUiState(... existing ..., val checkout: CheckoutUiState = CheckoutUiState()) : MviState {
            val isLocked: Boolean get() = course?.isSubscribed == false }
        sealed interface CourseDetailIntent: Load, OpenLesson(lesson), Back, SubscribeClicked,
            PaymentMethodSelected(method: PaymentMethod), PayClicked, RetryPayment, DismissCheckout
        sealed interface CourseDetailEffect: ShowLessonComingSoon, ShowLessonLocked,
            SubscriptionActivated, NavigateBack
        ```
        Delete `ShowMessage(String)`.
      - Modify `COURSES/presentation/detail/CourseDetailViewModel.kt`: the constructor becomes `(courseId: String, openCheckout: Boolean, getCourseDetail, getCourseUnits, createCourseCheckout: CreateCourseCheckoutUseCase, verifyCoursePayment: VerifyCoursePaymentUseCase)`. Implement the state machine above.
        - Use `private var pendingAutoOpen = openCheckout`. After a successful load, if `pendingAutoOpen && course.isSubscribed == false`, set `SELECTING_METHOD`. In both cases set `pendingAutoOpen = false`.
        - `OpenLesson`: `if (state.isLocked) ShowLessonLocked else ShowLessonComingSoon`.
        - `SubscribeClicked` is ignored unless the course is locked and the status is `IDLE`.
        - `PayClicked`/`RetryPayment` are guarded to `SELECTING_METHOD`/`FAILED`. They run `startPayment()` in `viewModelScope`:
          1. Set `PROCESSING` with `failure = null`.
          2. Call `createCourseCheckout(courseId, selectedMethod)`.
          3. Add a comment `// TODO: when session.checkoutUrl != null, emit an effect to launch it via openUrl() and verify on return`.
          4. Set `VERIFYING`, then call `verifyCoursePayment(session.id)`.
          5. Only if `status == SUCCESS && isSubscribed`: call `getCourseDetail(courseId)`. If the refetch returns `isSubscribed == true`, set `course = refetched`, `checkout = CheckoutUiState(status = SUCCESS)`, and send `SubscriptionActivated`. Otherwise set `FAILED(FAILED)`. `PENDING` maps to `FAILED(PENDING)`.
        - `DismissCheckout`: if not busy, reset to `CheckoutUiState()`.
        - Never mutate `isSubscribed` locally.
        - Update the Koin line in `CoursesModule.kt` to `viewModel { (courseId: String, openCheckout: Boolean) -> CourseDetailViewModel(courseId, openCheckout, get(), get(), get(), get()) }`.
      - Modify `COURSES/presentation/detail/components/CourseLessonItem.kt`: add `isLocked: Boolean = false`.
        - When locked, the leading icon is `Icons.Rounded.Lock` tinted `onSurfaceVariant`, with `contentDescription = stringResource(course_locked_lesson_cd)`. Title color is `onSurfaceVariant`.
        - The row stays clickable so the VM can show the snackbar. The exam chip and duration are still shown (syllabus preview).
      - Modify `COURSES/presentation/detail/components/CourseUnitSection.kt`: add `isLocked: Boolean = false` and pass it to every `CourseLessonItem`.
      - Create `COURSES/presentation/detail/components/CourseSubscribeBar.kt`: `CourseSubscribeBar(priceText: String?, onSubscribeClick: () -> Unit, modifier)`.
        - A `Surface` (tonalElevation 3.dp, `surfaceContainerLow`) with `navigationBarsPadding()`.
        - Row: on the start side, a column with `JanaPriceTag` (if `priceText` is non-null) and the `course_unlock_full_access` text (bodySmall, `onSurfaceVariant`). On the end side, a `Button` with the `course_subscribe_and_pay` label.
      - Create `COURSES/presentation/detail/components/CourseCheckoutSheetContent.kt`: `CourseCheckoutSheetContent(courseTitle: String, priceText: String?, checkout: CheckoutUiState, onMethodSelected: (PaymentMethod) -> Unit, onPay: () -> Unit, onRetry: () -> Unit, onCancel: () -> Unit)`. Content, top to bottom:
        - `payment_sheet_title` (titleMedium Bold), then the course title.
        - A total row: `payment_total_label` on the start side, `JanaPriceTag` on the end side.
        - The `payment_method_title` header.
        - One row per `availableMethods` entry, using `Modifier.selectable(selected, role = Role.RadioButton, enabled = !isBusy)` plus a `RadioButton(onClick = null)`, a leading icon (CARD `Icons.Rounded.CreditCard`, WALLET `Icons.Rounded.AccountBalanceWallet`, FAWRY `Icons.Rounded.Storefront`), and the label from `payment_method_card` / `_wallet` / `_fawry`. Put the enum-to-string-resource `when` in this file.
        - Status text: `payment_processing` or `payment_verifying` while busy, and `payment_failed` or `payment_pending` (in `colorScheme.error`) when failed.
        - The primary button is `JanaButton(text = pay label, isLoading = isBusy)`. The label is `course_pay_amount` with `%1$s` replaced by `priceText`. When the status is `FAILED`, the label becomes `btn_retry` and the button calls `onRetry`.
        - `JanaOutlinedButton(btn_cancel, enabled = !isBusy, onClick = onCancel)`.
      - Modify `COURSES/presentation/detail/CourseDetailScreen.kt`:
        - `koinViewModel { parametersOf(courseId, openCheckout) }`.
        - Effects: `ShowLessonComingSoon` shows the `course_lesson_coming_soon` snackbar, `ShowLessonLocked` shows `course_subscribe_to_unlock`, and `SubscriptionActivated` shows `payment_subscription_activated`. Resolve all strings at the top with `stringResource`.
        - In `CourseDetailContent`, add `bottomBar = { if (state.isLocked) CourseSubscribeBar(price, onSubscribeClick = { onIntent(SubscribeClicked) }) }`.
        - In the hero header, when locked, replace the `LinearProgressIndicator` and progress label with `JanaLockedBadge(course_locked)`.
        - Pass `isLocked = state.isLocked` to `CourseUnitSection`.
        - When `state.checkout.isVisible`, render `JanaBottomSheet(onDismissRequest = { onIntent(DismissCheckout) }, dismissEnabled = !state.checkout.isBusy) { CourseCheckoutSheetContent(...) }`.
        - Keep the top bar showing `state.course?.title`.

      Files: `CourseDetailContract.kt`, `CourseDetailViewModel.kt`, `CourseDetailScreen.kt`, `CourseLessonItem.kt`, `CourseUnitSection.kt`, `CourseSubscribeBar.kt` (new), `CourseCheckoutSheetContent.kt` (new), `CoursesModule.kt`
      Verify: `.\gradlew.bat :feature:home:compileKotlinJvm` succeeds, and `.\gradlew.bat :feature:home:jvmTest` passes.

- [ ] 5. **jvmTest coverage.** Depends on 2–4. Follow `feature/home/src/jvmTest/.../live/LessonDetailViewModelTest.kt`: `StandardTestDispatcher`, `Dispatchers.setMain/resetMain` in `@BeforeTest/@AfterTest`, `runTest(testDispatcher)`, `advanceUntilIdle()`, `vm.effect.first()`, and `kotlin.test` assertions.
      - Create `TEST/FakeCourseRepositories.kt`:
        - `FakeCourseRepository(store: CourseCatalogStore)`: no delays; `observeCourses = store.courses`.
        - `FakePaymentRepository(store, var verifyStatus: PaymentStatus = SUCCESS, var failCheckout: Boolean = false)`. It records `verifyCalls`, and calls `store.markSubscribed` only when `verifyStatus == SUCCESS`.
        - Use a small fixture list: one subscribed course and one locked course priced `Money(400.0, "EGP")`.
      - Create `TEST/CoursesViewModelTest.kt`:
        - (a) After load, state contains the locked and the subscribed course with the correct `isSubscribed` and `price`.
        - (b) `SubscribeClicked("c2")` emits `NavigateToCourseDetail("c2", openCheckout = true)`.
        - (c) After `store.markSubscribed("c2")` and `advanceUntilIdle()`, list state shows c2 subscribed. This is the reactive refresh.
      - Create `TEST/CourseDetailViewModelTest.kt`:
        - (a) A locked course gives `isLocked == true`, and `OpenLesson` emits `ShowLessonLocked`. A subscribed course emits `ShowLessonComingSoon`.
        - (b) `openCheckout = true` on a locked course gives `SELECTING_METHOD` after load. On a subscribed course the status stays `IDLE`.
        - (c) Success path: Subscribe, select `WALLET`, then Pay. Run `runCurrent()` step by step if needed. Before verify completes, the course is still locked. After `advanceUntilIdle()`: `checkout.status == SUCCESS`, `course.isSubscribed == true`, `verifyCalls == 1`, and the effect is `SubscriptionActivated`. To pin the ordering ("subscribed only after verify"), the fake `verifyPayment` asserts that `store.find(id)!!.isSubscribed == false` before it marks the course.
        - (d) Verify FAILED gives status `FAILED`, failure `FAILED`, course still locked, and the store still unsubscribed. Then set `verifyStatus = SUCCESS`, send `RetryPayment`, and expect `SUCCESS`.
        - (e) `PENDING` gives `FAILED(PENDING)` and the course stays locked.
        - (f) `DismissCheckout` while `PROCESSING` is ignored. While `SELECTING_METHOD` it gives `IDLE`.
      - Create `TEST/CoursePaymentRepositoryImplTest.kt`: using the real `CourseCatalogStore` + `CourseRepositoryImpl` + `CoursePaymentRepositoryImpl` under `runTest` (virtual time skips the delays), `createCheckout("course_2", CARD)` alone leaves `getCourseDetail("course_2").isSubscribed == false`. After `verifyPayment(session.id)` returns SUCCESS/true, both `getCourseDetail` and `getCourses()` report it subscribed.

      Files: the 4 test files above.
      Verify: `.\gradlew.bat :feature:home:jvmTest`. All new and existing tests pass. Check `feature/home/build/test-results/jvmTest/` for the three new test classes.

- [ ] 6. **Full build + manual check.**
      Verify:
      - `.\gradlew.bat :androidApp:assembleDebug` and `.\gradlew.bat :desktopApp:compileKotlin` succeed.
      - Optionally run `.\gradlew.bat :desktopApp:run` and check:
        - The Courses tab shows Advanced Mathematics with progress, and the other two courses with a lock, a Locked chip, a price, and a Subscribe button.
        - Subscribe on a card opens details with the sheet open. Pay shows a loading state, then "Verifying", then the sheet closes with a "Subscription activated" snackbar. The lessons unlock.
        - Back on the Courses tab, the card now shows progress and Start.
        - A locked lesson tap shows "Subscribe to unlock this lesson".
        - Switching to Arabic shows every new string translated, and the layout is mirrored.

## Assumptions / gaps
- No active-teacher/session abstraction exists, so teacher scoping is documented in TODOs and not coded. Adding one is out of scope.
- The real payment gateway (opening `checkoutUrl`, return deep link) is out of scope. There is a TODO in `CourseDetailViewModel.startPayment()`.
- A wallet or Fawry flow that is truly pending is represented only by `PaymentStatus.PENDING` → `FAILED(PENDING)` with a retry. There is no polling.
