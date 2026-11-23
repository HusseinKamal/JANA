# Teacher Profile tab + Teacher Chat: implementation plan

Tests intentionally skipped per user request (message 10). Do not write new test classes. Only fix existing tests in `feature/home/src/jvmTest` if a change breaks their compilation.

Workspace: `d:/AndroidApps/JANA/JANA`, branch `develop`, no worktree. Paths below are relative to the workspace root.
Abbreviations: `HOME = feature/home/src/commonMain/kotlin/com/hussein/jana/feature/home`, `DS = core/designsystem/src/commonMain/kotlin/com/hussein/jana/core/designsystem`, `RES = core/designsystem/src/commonMain/composeResources`.

## Verification commands (run from the workspace root)

1. `.\gradlew.bat :core:designsystem:compileCommonMainKotlinMetadata`
2. `.\gradlew.bat :feature:home:compileCommonMainKotlinMetadata`
3. `.\gradlew.bat :feature:home:jvmTest`
4. `.\gradlew.bat :androidApp:assembleDebug`

A Kotlin incremental-cache stack trace may print while the build still succeeds. Judge by exit code / `BUILD SUCCESSFUL`.

## Findings that drive the decisions

- **Image loader:** Coil 3 (`coil = "3.0.0-rc01"`) is already an `api` dependency of `:core:designsystem` (`libs.coil.compose`, `libs.coil.network`), so `feature:home` sees it transitively. Nothing uses it yet. No new dependency is needed. `androidApp/src/main/AndroidManifest.xml` has no `INTERNET` permission, so on Android the remote photo will fail and the initials fallback will show. Do NOT edit the manifest in this task; record it in the impl notes as a follow-up.
- **Courses Subscribe flow** already lives in Course Details: the Courses list sends `NavigateToCourseDetail(courseId, openCheckout = true)` and `CourseDetailViewModel` runs createCheckout → verify. The Teacher tab reuses that exact path (navigate to `HomeRoute.CourseDetail(id, openCheckout = true)`). No course payment code changes.
- **Course catalogue** (`CourseCatalogStore`, `GetCoursesUseCase`, `ObserveCoursesUseCase`) is already scoped to the active teacher platform via the session (see TODOs in `CourseRepositoryImpl`). The Teacher tab uses those use cases directly, so a subscription verified in Course Details shows up in the Teacher tab through `observeCourses()`.
- **Live lessons:** `LiveLessonRepository.getUpcomingLiveLessons()` (live-now + upcoming) and `getNextLiveLessons()` give the teacher's future lessons. Components `UpcomingLessonCard(lesson, onLessonClick, onJoinZoomClick)` and `NextLessonItem(lesson, onClick)` take the domain `LiveLesson`.
- **Payment models** (`PaymentMethod`, `PaymentStatus`, `CheckoutSession`, `PaymentVerification`, `Money`) are in `HOME/tabs/courses/domain/model/`. `CheckoutSession.courseId` is the only course-specific field. `CheckoutUiState`/`CheckoutStatus`/`PaymentFailure` live in `HOME/tabs/courses/presentation/detail/CourseDetailContract.kt` and are imported by `CourseDetailViewModelTest`. `CourseCheckoutSheetContent(courseTitle, priceText, checkout, ...)` is generic apart from the parameter name.
- **Honorific title:** `homeTopBarTitle` in `HOME/HomeScreen.kt` is private. `TeacherGender` is declared in `HOME/HomeContract.kt`. `HomeViewModelTest` builds `HomeViewModel()` with no arguments.
- `JanaChatInput` (DS) is already a shared stateless input bar that disables Send when text is blank. `UserMessageBubble` (AI chat) is a simple bubble; `AiMessageBubble` has rich AI-only content.
- `HomeTabViewModel` shows the time pattern used in common code: `kotlin.time.Clock` with `@OptIn(ExperimentalTime::class)` plus `kotlinx.datetime` `Instant/TimeZone/toLocalDateTime`.
- The old `tabs/teachers/` package (Teacher Discovery) is a marketplace screen that is not wired into `HomeTab`. Do not reuse or touch it.

## Decisions

1. **Shared course card moves to designsystem** with a UI model, `JanaCourseCardUiModel`. Designsystem must not depend on `Course`. A feature-side composable mapper `Course.toCardUiModel()` formats the price with the existing `Money.formatted()`. Courses tab and Teacher tab both call it.
2. **Live lesson components stay in `tabs/live/presentation/list/components/`** and are reused from the teacher package. Both live in the same `feature:home` module and render the same domain `LiveLesson`. Moving them to designsystem would need a lesson UI model plus status→color/string mapping for three components and changes to the Live tab, and nothing outside `feature:home` needs them.
3. **Payment generalisation, kept small:** rename `CheckoutSession.courseId` → `itemId` (KDoc: course id or subscription plan id). Rename the `CourseCheckoutSheetContent` parameter `courseTitle` → `itemTitle`. Reuse `CheckoutUiState`/`CheckoutStatus`/`PaymentFailure` and `CourseCheckoutSheetContent` from their current packages; moving them would break test imports for no functional gain. The live-lessons subscription gets its own `TeacherSubscriptionRepository` (create checkout + verify). Only verify activates the subscription. `TeacherProfileViewModel` copies the `CourseDetailViewModel` state machine, including `pendingSessionId` re-verify on PENDING.
4. **Initials fallback via layering:** `JanaAvatar` draws `JanaInitialsAvatar` and puts Coil `AsyncImage` on top. While the image loads or after it fails, AsyncImage draws nothing, so the initials show. This needs no placeholder painters.
5. **Chat bubble generalised:** new `JanaChatBubble` in DS. `UserMessageBubble` delegates to it with identical visuals (80% width, teal, topEnd 4dp corner, long-press copy). `AiMessageBubble` stays as it is.
6. **Teacher name formatting is shared:** extract `teacherDisplayName()` / `teacherTitleWithSubject()` composables into `HOME/common/TeacherNameFormatting.kt`. HomeScreen's top bar, the profile header and the chat top bar use them. `TeacherGender` stays in `HomeContract.kt` (moving it breaks `HomeViewModelTest`'s package-level reference). `TeacherProfile` imports it.
7. **HomeViewModel is not rewired** to the new repository. Its constructor and test stay the same. The mock profile uses the same values (Ahmed, MALE, Mathematics). Add a TODO in `HomeViewModel` pointing at `GetActiveTeacherProfileUseCase`, and another TODO about a future multi-account `ActiveTeacherContext`.
8. **No teacher IDs anywhere outside mock data.** `HomeRoute.TeacherChat` is a `data object`. Repositories resolve "the active teacher" from the session (mock: a constant inside the mock data file). No lists, search, or discovery.
9. **Data layer shape:** teacher data uses `data/mock/` + `data/repository/`, like the courses feature. No `remote/`, `dto/`, or `mapper/` stubs (YAGNI). Each method carries a `// TODO: replace with real API call — <endpoint>` comment.
10. **Tab naming:** keep the `HomeTab.MY_TEACHER_CHAT` enum value (tests and the `tab_my_teacher_chat` key reference it). Replace the stub `HOME/tabs/chat/MyTeacherChatTabScreen.kt` with `HOME/tabs/teacher/TeacherTabScreen.kt` and delete the old file and its now-empty `tabs/chat` folder.
11. **Section state views are shared:** `JanaLoadingState`, `JanaErrorState`, `JanaEmptyState` go in DS. Profile sections and the chat screen use them. Existing Courses/Live screens are left as they are (no unrequested refactor).

## Shared component signatures (designsystem, package `com.hussein.jana.core.designsystem`)

```kotlin
// DS/JanaCourseCard.kt
@Immutable
data class JanaCourseCardUiModel(
    val title: String,
    val subjects: List<String>,
    val isLocked: Boolean,
    val isPurchasable: Boolean,   // Subscribe button shown only when locked && purchasable
    val priceText: String?,       // already formatted; null hides price
    val completedLessons: Int,
    val totalLessons: Int,
)
@Composable fun JanaCourseCard(model: JanaCourseCardUiModel, onClick: () -> Unit, onSubscribeClick: () -> Unit, modifier: Modifier = Modifier)

// DS/JanaProfileComponents.kt
@Composable fun JanaAvatar(name: String, imageUrl: String?, modifier: Modifier = Modifier, size: Dp = 40.dp, contentDescription: String? = null)
@Composable fun JanaProfileHeader(name: String, subtitle: String?, bio: String?, imageUrl: String?, modifier: Modifier = Modifier, avatarContentDescription: String? = null)
@Composable fun JanaNavigationCard(title: String, subtitle: String?, icon: ImageVector, onClick: () -> Unit, modifier: Modifier = Modifier)

// DS/JanaSubscriptionStatusCard.kt
enum class JanaSubscriptionStatus { ACTIVE, EXPIRED, NOT_SUBSCRIBED }
@Composable fun JanaSubscriptionStatusCard(
    title: String, status: JanaSubscriptionStatus, statusLabel: String,
    detailText: String?, priceText: String?, actionLabel: String?, onAction: (() -> Unit)?,
    modifier: Modifier = Modifier, isActionLoading: Boolean = false,
)

// DS/JanaChatBubble.kt
@Composable fun JanaChatBubble(text: String, metaText: String, isOutgoing: Boolean, modifier: Modifier = Modifier)

// DS/JanaStateViews.kt
@Composable fun JanaLoadingState(modifier: Modifier = Modifier)
@Composable fun JanaErrorState(message: String, retryLabel: String, onRetry: () -> Unit, modifier: Modifier = Modifier)
@Composable fun JanaEmptyState(message: String, modifier: Modifier = Modifier)
```

Rules for all of them: stateless (values + lambdas), MaterialTheme colors or existing tokens from `Color.kt` (use `OnPrimary` instead of `Color.White`), start/end shapes and paddings (RTL-aware), AutoMirrored icons for directional arrows, no string literals.
- `JanaAvatar`: `Box(size, clip(CircleShape))` holds `JanaInitialsAvatar(initials, size)`, plus `coil3.compose.AsyncImage(model = imageUrl, contentDescription, Modifier.matchParentSize().clip(CircleShape), contentScale = ContentScale.Crop)` only when `!imageUrl.isNullOrBlank()`. Private `initialsOf(name)` takes the first letter of up to two words, uppercased.
- `JanaProfileHeader`: centered Column: JanaAvatar 88.dp, name (`headlineSmall`, Bold, onSurface), subtitle (`bodyMedium`, onSurfaceVariant), bio (`bodyMedium`, onSurfaceVariant, `TextAlign.Center`). Null/blank subtitle and bio are hidden.
- `JanaNavigationCard`: Card with the same look as the course card (16.dp shape, `SurfaceContainerLowest`, 1.dp `outlineVariant` border, 0 elevation), clickable. Row: 40.dp circle (`primaryContainer` background, icon tinted `onPrimaryContainer`), title `titleSmall` SemiBold + subtitle `bodySmall`, trailing `Icons.AutoMirrored.Filled.NavigateNext`.
- `JanaSubscriptionStatusCard`: same card look. Row: title (`titleSmall` SemiBold, weight 1) + `JanaStatusChip(statusLabel, …)` with colors ACTIVE = `SuccessGreen.copy(alpha = 0.15f)`/`SuccessGreen`, EXPIRED = `errorContainer`/`onErrorContainer`, NOT_SUBSCRIBED = `surfaceContainerHigh`/`onSurfaceVariant`. Then optional detailText (`bodySmall`), optional `JanaPriceTag(priceText)`, and an optional full-width `ScholarlyTeal` Button when both `actionLabel` and `onAction` are non-null (shows a small `CircularProgressIndicator` while `isActionLoading`, disabled then).
- `JanaChatBubble`: copy of `UserMessageBubble` layout, generalised. Row fillMaxWidth, horizontal padding 16.dp, arrangement End (outgoing) / Start (incoming). Surface `fillMaxWidth(0.8f)` with long-press copy (`LocalClipboardManager` + `combinedClickable`, `@OptIn(ExperimentalFoundationApi::class)`). Outgoing: `ScholarlyTeal` with `OnPrimary` text, shape `RoundedCornerShape(topStart = 16, topEnd = 4, bottomStart = 16, bottomEnd = 16)`. Incoming: `SurfaceContainerLow` + 1.dp `OutlineVariant` border with `OnBackground` text, shape `topStart = 4, topEnd = 16, bottom 16/16`. Body `bodySmall` lineHeight 20.sp, meta `labelSmall` at alpha 0.7.
- `JanaStateViews`: `JanaLoadingState` = Box fillMaxWidth, padding 24.dp, centered `CircularProgressIndicator(color = ScholarlyTeal)`. `JanaErrorState` = centered Column with error-colored `bodyMedium` text + `TextButton(retryLabel)`. `JanaEmptyState` = centered `bodyMedium` onSurfaceVariant text. All use `fillMaxWidth`, not `fillMaxSize`, so they work inside LazyColumn items. Callers wrap them in `fillMaxSize` Boxes for full-screen use.

## New string keys (add to BOTH `RES/values/strings.xml` and `RES/values-ar/strings.xml`)

| key | English | Arabic |
|---|---|---|
| teacher_profile_photo_cd | Teacher photo | صورة المعلم |
| teacher_profile_chat_title | Chat with your teacher | تحدث مع معلمك |
| teacher_profile_chat_subtitle | Ask questions and get answers directly | اسأل واحصل على إجابات مباشرة |
| teacher_profile_courses_section | Courses &amp; subscriptions | الكورسات والاشتراكات |
| teacher_profile_live_section | Live lessons &amp; subscription | الحصص المباشرة والاشتراك |
| subscription_live_plan_title | Live lessons subscription | اشتراك الحصص المباشرة |
| subscription_status_active | Active | نشط |
| subscription_status_expired | Expired | منتهي |
| subscription_status_not_subscribed | Not subscribed | غير مشترك |
| subscription_active_until | Active until %1$s | نشط حتى %1$s |
| subscription_expired_on | Expired on %1$s | انتهى في %1$s |
| subscription_live_unlock_desc | Join all live lessons with your teacher | احضر كل الحصص المباشرة مع معلمك |
| subscription_subscribe_btn | Subscribe | اشترك |
| subscription_renew_btn | Renew | تجديد |
| teacher_chat_title | Chat | المحادثة |
| teacher_chat_input_hint | Write a message… | اكتب رسالة… |
| teacher_chat_empty | No messages yet. Say hello! | لا توجد رسائل بعد. ابدأ المحادثة! |
| teacher_chat_send_failed | Message not sent. Please try again. | لم يتم إرسال الرسالة. حاول مرة أخرى. |

Reuse existing keys: `courses_no_courses`, `live_no_upcoming`, `error_generic`, `btn_retry`, `nav_back`, `live_no_zoom_link`, `payment_subscription_activated`, `teacher_title_male/female/with_subject`, `tab_my_teacher_chat`. Placeholders are filled with `.replace("%1\$s", …)` as in existing code, or with `stringResource(res, arg)` as in HomeScreen. Pick one per call site; both patterns already exist.

## Routes and navigation

- `core/navigation/src/commonMain/kotlin/com/hussein/jana/core/navigation/Routes.kt`: add `@Serializable data object TeacherChat : HomeRoute` with KDoc saying the conversation is with the active teacher, so the route carries no teacher id.
- `HOME/HomeEntryProvider.kt`: `is HomeRoute.TeacherChat -> NavEntry(key) { val navigator: AppNavigator = koinInject(); TeacherChatScreen(onBack = { navigator.back() }) }`.
- Profile navigation (from `TeacherProfileScreen` effects via `AppNavigator` from `koinInject()`): chat → `HomeRoute.TeacherChat`, course → `HomeRoute.CourseDetail(id)`, course Subscribe → `HomeRoute.CourseDetail(id, openCheckout = true)`, lesson → `HomeRoute.LessonDetail(id)`, Join → `openZoomUrl(url)`.

## Koin

`HOME/tabs/teacher/di/TeacherModule.kt`:
```kotlin
val teacherModule = module {
    singleOf(::TeacherProfileRepositoryImpl) bind TeacherProfileRepository::class
    singleOf(::TeacherSubscriptionRepositoryImpl) bind TeacherSubscriptionRepository::class
    singleOf(::TeacherChatRepositoryImpl) bind TeacherChatRepository::class
    factoryOf(::GetActiveTeacherProfileUseCase)
    factoryOf(::GetTeacherUpcomingLessonsUseCase)
    factoryOf(::GetLiveLessonsSubscriptionUseCase)
    factoryOf(::CreateLiveSubscriptionCheckoutUseCase)
    factoryOf(::VerifyLiveSubscriptionPaymentUseCase)
    factoryOf(::GetTeacherChatMessagesUseCase)
    factoryOf(::SendTeacherMessageUseCase)
    viewModel { TeacherChatViewModel(get(), get(), get()) }
    viewModel { TeacherProfileViewModel(get(), get(), get(), get(), get(), get(), get()) }
}
```
`HOME/di/HomeModule.kt`: `includes(liveLessonsModule, coursesModule, teacherModule)`.

---

# Implementation Plan

- [ ] 1. Shared UI foundation in designsystem, plus the small generalisations (FEAT-001).
      a) Create `DS/JanaCourseCard.kt` with `JanaCourseCardUiModel` + `JanaCourseCard`. Port the body of `HOME/tabs/courses/presentation/list/components/JanaCourseCard.kt` unchanged in look, replacing `course.*` with model fields (`isLocked`, `isPurchasable`, `priceText?.let { JanaPriceTag(it) }`, progress from completed/total). Delete the feature file. Create `HOME/tabs/courses/presentation/common/CourseCardMapping.kt` with `@Composable internal fun Course.toCardUiModel()` (isLocked = !isSubscribed, priceText = price?.formatted()). Update `CoursesScreen.kt` to `JanaCourseCard(model = course.toCardUiModel(), …)`.
      b) Create `DS/JanaProfileComponents.kt` (JanaAvatar, JanaProfileHeader, JanaNavigationCard), `DS/JanaSubscriptionStatusCard.kt`, `DS/JanaChatBubble.kt`, `DS/JanaStateViews.kt` per the signatures above.
      c) Make `HOME/tabs/janaai/components/UserMessageBubble.kt` delegate to `JanaChatBubble(text = msg.content, metaText = "${msg.timestamp} • $userName", isOutgoing = true, modifier)`, keeping its `internal` signature.
      d) Add all string keys from the table to EN and AR.
      e) Create `HOME/common/TeacherNameFormatting.kt` (package `com.hussein.jana.feature.home.common`) with `@Composable fun teacherDisplayName(name: String, gender: TeacherGender): String` and `@Composable fun teacherTitleWithSubject(name: String, gender: TeacherGender, subject: String): String`, moving the logic out of `homeTopBarTitle`. `homeTopBarTitle` keeps the blank-name → `app_title_jana` fallback and calls the new function.
      f) In `HOME/tabs/courses/domain/model/Payment.kt` rename `CheckoutSession.courseId` → `itemId` (KDoc: course id or subscription plan id) and update `CoursePaymentRepositoryImpl` (2 usages). In `CourseCheckoutSheetContent` rename `courseTitle` → `itemTitle` and update the call in `CourseDetailScreen.kt`.
      Files: DS/JanaCourseCard.kt, DS/JanaProfileComponents.kt, DS/JanaSubscriptionStatusCard.kt, DS/JanaChatBubble.kt, DS/JanaStateViews.kt, RES/values/strings.xml, RES/values-ar/strings.xml, HOME/tabs/courses/presentation/list/components/JanaCourseCard.kt (delete), HOME/tabs/courses/presentation/common/CourseCardMapping.kt, HOME/tabs/courses/presentation/list/CoursesScreen.kt, HOME/tabs/janaai/components/UserMessageBubble.kt, HOME/common/TeacherNameFormatting.kt, HOME/HomeScreen.kt, HOME/tabs/courses/domain/model/Payment.kt, HOME/tabs/courses/data/repository/CoursePaymentRepositoryImpl.kt, HOME/tabs/courses/presentation/detail/components/CourseCheckoutSheetContent.kt, HOME/tabs/courses/presentation/detail/CourseDetailScreen.kt.
      Verify: commands 1–4 succeed, and `:feature:home:jvmTest` passes with the existing tests unchanged.

- [ ] 2. Teacher domain + mock data layer + Teacher Chat screen + route (FEAT-002). Depends on 1.
      a) Domain, package `com.hussein.jana.feature.home.tabs.teacher.domain`:
         - `model/TeacherProfile.kt`: `data class TeacherProfile(val id: String, val name: String, val gender: TeacherGender, val subject: String, val bio: String, val photoUrl: String?)`.
         - `model/TeacherSubscription.kt`: `enum class SubscriptionStatus { ACTIVE, EXPIRED, NOT_SUBSCRIBED }` and `data class TeacherSubscription(val planId: String, val planName: String, val status: SubscriptionStatus, val expiresAtMs: Long?, val price: Money?) { val isPurchasable get() = status != ACTIVE && price != null }`. `price == null` is never treated as free.
         - `model/ChatMessage.kt`: `data class ChatMessage(val id: String, val text: String, val sentAtMs: Long, val isFromStudent: Boolean, val isRead: Boolean)`.
         - `repository/TeacherProfileRepository` (`suspend fun getActiveTeacherProfile(): Result<TeacherProfile>`), `repository/TeacherSubscriptionRepository` (`getLiveLessonsSubscription(): Result<TeacherSubscription>`, `createLiveSubscriptionCheckout(planId: String, method: PaymentMethod): Result<CheckoutSession>`, `verifyLiveSubscriptionPayment(checkoutId: String): Result<PaymentVerification>`), `repository/TeacherChatRepository` (`getMessages(): Result<List<ChatMessage>>`, `sendMessage(text: String): Result<ChatMessage>`).
         - Use cases (one `operator fun invoke` each, constructor-injected, following `GetCoursesUseCase`): GetActiveTeacherProfileUseCase, GetLiveLessonsSubscriptionUseCase, CreateLiveSubscriptionCheckoutUseCase, VerifyLiveSubscriptionPaymentUseCase, GetTeacherChatMessagesUseCase, SendTeacherMessageUseCase (trims text and fails with IllegalArgumentException when blank), and GetTeacherUpcomingLessonsUseCase(LiveLessonRepository). The last one calls `getUpcomingLiveLessons()` + `getNextLiveLessons()`, returns failure if either fails, otherwise returns `(upcoming + next).distinctBy { it.id }` with LIVE_NOW first and the rest sorted by `startEpochMs`.
      b) Data, `tabs/teacher/data/mock/MockTeacherData.kt` (internal object): profile (id "teacher_active", name "Ahmed", MALE, "Mathematics", a short English bio, photoUrl "https://i.pravatar.cc/300?img=12"), live plan (planId "live_monthly", planName "Monthly live lessons", NOT_SUBSCRIBED, expiresAtMs null, `Money(200.0, "EGP")`), and 4 seed chat messages (mixed sender, sentAtMs relative to a `now` passed in).
         `data/repository/TeacherProfileRepositoryImpl` (delay(400); `// TODO: replace with real API call — GET /api/v1/me/platform and GET /api/v1/me/branding (active teacher resolved from the session)`).
         `TeacherSubscriptionRepositoryImpl`: in-memory current subscription + sessions map + counter, same shape as `CoursePaymentRepositoryImpl`. get: delay(400), TODO `GET /api/v1/me/subscriptions`. createCheckout: delay(600), fail if planId unknown or price null, TODO `GET /api/v1/subscriptions/plans` + `POST /api/v1/subscriptions/{planId}/checkout`. verify: delay(800), unknown session → FAILED; otherwise set status ACTIVE with `expiresAtMs = now + 30 days`, the ONLY place activation happens, TODO `POST /api/v1/payments/{id}/verify`. Use `kotlin.time.Clock.System.now().toEpochMilliseconds()` with `@OptIn(ExperimentalTime::class)` as in `HomeTabViewModel`.
         `TeacherChatRepositoryImpl`: MutableList seeded from mock data; getMessages delay(300); sendMessage delay(300) appends a student message with a generated id. Comment `// TODO: replace with real API / WebSocket — GET /api/v1/conversations, GET /api/v1/conversations/{id}/messages, POST /api/v1/conversations/{id}/messages`.
         Add `// TODO: multi-account ActiveTeacherContext` to the profile repository, and a TODO in `HomeViewModel` to read name/gender/subject from `GetActiveTeacherProfileUseCase` (no code change to its constructor).
      c) Create `tabs/teacher/presentation/common/TimeFormatting.kt` with internal pure functions `formatShortDate(epochMs: Long): String` ("dd/MM/yyyy") and `formatTimeOfDay(epochMs: Long): String` ("HH:mm"). Both use `kotlinx.datetime.Instant.fromEpochMilliseconds(...).toLocalDateTime(TimeZone.currentSystemDefault())` with `padStart(2, '0')`.
      d) Chat presentation, `tabs/teacher/presentation/chat/`:
         - `TeacherChatContract.kt`: `TeacherChatUiState(isLoading = true, teacher: TeacherProfile? = null, messages: List<ChatMessage> = emptyList(), input: String = "", isSending = false, error: String? = null) { val canSend get() = input.isNotBlank() && !isSending }`. Intents: Load, InputChanged(text), Send, Back. Effects: NavigateBack, ShowSendFailed.
         - `TeacherChatViewModel(getActiveTeacherProfile, getMessages, sendMessage)`: init → Load. Load fetches profile + messages with `async`. A profile failure sets error; a messages failure sets error. Send is a no-op unless canSend, sets isSending, and on success appends the message and clears input. On failure it keeps the input and emits ShowSendFailed.
         - `TeacherChatScreen.kt`: `fun TeacherChatScreen(onBack: () -> Unit, viewModel: TeacherChatViewModel = koinViewModel())`. It collects effects (NavigateBack → onBack, ShowSendFailed → snackbar `teacher_chat_send_failed`) and calls stateless `TeacherChatContent(state, snackbarHostState, onBack, onIntent)`. Scaffold with TopAppBar: navigationIcon `Icons.AutoMirrored.Filled.ArrowBack` (cd `nav_back`), title Row of `JanaAvatar(name, photoUrl, size = 36.dp)` + `teacherDisplayName(name, gender)` (fallback `teacher_chat_title` while loading), containerColor `MaterialTheme.colorScheme.background`. bottomBar: `JanaChatInput(text = state.input, onTextChange = InputChanged, onSendClick = Send, hint = teacher_chat_input_hint, enabled = !state.isSending)` wrapped with `.navigationBarsPadding().imePadding().padding(horizontal = 16.dp, vertical = 8.dp)`. Body: loading → JanaLoadingState in a fillMaxSize Box; error → JanaErrorState(error_generic, btn_retry, Load); empty → JanaEmptyState(teacher_chat_empty); else `LazyColumn(reverseLayout = true, verticalArrangement = spacedBy(8.dp), contentPadding = PaddingValues(vertical = 12.dp))` over `state.messages.asReversed()` keyed by id → `JanaChatBubble(text, metaText = formatTimeOfDay(sentAtMs), isOutgoing = isFromStudent)`.
      e) Add `HomeRoute.TeacherChat` to Routes.kt and the entry to HomeEntryProvider (see "Routes and navigation").
      f) Create `tabs/teacher/di/TeacherModule.kt` with the repositories, all use cases and `TeacherChatViewModel`. Include it in `homeModule`.
      Files: HOME/tabs/teacher/domain/**, HOME/tabs/teacher/data/**, HOME/tabs/teacher/presentation/common/TimeFormatting.kt, HOME/tabs/teacher/presentation/chat/**, HOME/tabs/teacher/di/TeacherModule.kt, HOME/di/HomeModule.kt, HOME/HomeEntryProvider.kt, HOME/HomeViewModel.kt (TODO comment only), core/navigation/.../Routes.kt.
      Verify: commands 2, 3, 4 succeed.

- [ ] 3. Teacher Profile tab screen, replacing the stub (FEAT-003). Depends on 1 and 2.
      a) `tabs/teacher/presentation/profile/TeacherProfileContract.kt`:
         `TeacherProfileUiState(isProfileLoading = true, profile: TeacherProfile? = null, profileError: String? = null, isCoursesLoading = true, courses: List<Course> = emptyList(), coursesError: String? = null, isLessonsLoading = true, lessons: List<LiveLesson> = emptyList(), lessonsError: String? = null, isSubscriptionLoading = true, subscription: TeacherSubscription? = null, subscriptionError: String? = null, checkout: CheckoutUiState = CheckoutUiState()) : MviState`.
         Intents: Load, RetryProfile, RetryCourses, RetryLessons, RetrySubscription, OpenChat, OpenCourse(courseId), SubscribeCourse(courseId), OpenLesson(lessonId), JoinZoom(url), SubscribeLiveLessons, PaymentMethodSelected(method), PayClicked, RetryPayment, DismissCheckout.
         Effects: NavigateToChat, NavigateToCourseDetail(courseId, openCheckout = false), NavigateToLessonDetail(lessonId), OpenZoomUrl(url), ShowNoZoomLink, LiveSubscriptionActivated.
      b) `TeacherProfileViewModel(getActiveTeacherProfile, getCourses: GetCoursesUseCase, observeCourses: ObserveCoursesUseCase, getTeacherUpcomingLessons, getLiveLessonsSubscription, createLiveSubscriptionCheckout, verifyLiveSubscriptionPayment)`. init → Load (four independent `viewModelScope.launch` section loaders) and collect `observeCourses()` (apply only when `!isCoursesLoading`, as `CoursesViewModel` does). Retry* reloads one section. Open*/Subscribe*/OpenChat send navigation effects; SubscribeCourse → `NavigateToCourseDetail(id, openCheckout = true)`. JoinZoom with blank url → ShowNoZoomLink.
         The live-subscription checkout is a copy of the `CourseDetailViewModel` logic. SubscribeLiveLessons opens the sheet only if `subscription?.isPurchasable == true` and checkout is IDLE. Pay creates the checkout with `subscription.planId`, then goes VERIFYING → verify. It keeps `pendingSessionId` until a final answer, and Retry re-verifies a PENDING session instead of creating a new one. On SUCCESS + isSubscribed it re-fetches the subscription and applies it only if status == ACTIVE, then sets `CheckoutUiState(status = SUCCESS)` and emits LiveSubscriptionActivated; otherwise FAILED. Dismiss is ignored while busy. The ViewModel never sets ACTIVE itself.
      c) `TeacherProfileScreen.kt`: `fun TeacherProfileScreen(viewModel: TeacherProfileViewModel = koinViewModel(), navigator: AppNavigator = koinInject())`. It collects effects (routes listed above, `openZoomUrl`, snackbars `live_no_zoom_link` / `payment_subscription_activated`) and renders stateless `TeacherProfileContent(state, snackbarHostState, onIntent)`. Scaffold has `contentWindowInsets = WindowInsets(0, 0, 0, 0)` and a snackbarHost. Inside, a LazyColumn with `contentPadding = PaddingValues(16.dp)` and `spacedBy(16.dp)` holds these items:
         1) Profile header: loading/error(RetryProfile)/`JanaProfileHeader(name = teacherDisplayName(name, gender), subtitle = subject, bio, imageUrl = photoUrl, avatarContentDescription = teacher_profile_photo_cd)`.
         2) `JanaNavigationCard(teacher_profile_chat_title, teacher_profile_chat_subtitle, Icons.AutoMirrored.Rounded.Chat) { OpenChat }`.
         3) `JanaSectionHeader(teacher_profile_courses_section)`, then loading / error(RetryCourses) / empty(`courses_no_courses`) / `items(courses, key = id) { JanaCourseCard(course.toCardUiModel(), OpenCourse, SubscribeCourse) }`.
         4) `JanaSectionHeader(teacher_profile_live_section)`, then the subscription card: loading / error(RetrySubscription) / `JanaSubscriptionStatusCard`. Map it in a private composable helper in the screen file: title = `subscription_live_plan_title`; status → `JanaSubscriptionStatus` + label (`subscription_status_*`); detailText = ACTIVE → `subscription_active_until` + `formatShortDate(expiresAtMs)`, EXPIRED → `subscription_expired_on`, NOT_SUBSCRIBED → `subscription_live_unlock_desc`; priceText = `price?.formatted()` when not ACTIVE; actionLabel = NOT_SUBSCRIBED → `subscription_subscribe_btn`, EXPIRED → `subscription_renew_btn`, ACTIVE → null; onAction only when isPurchasable; isActionLoading = checkout.isBusy.
         Then lessons: loading / error(RetryLessons) / empty(`live_no_upcoming`) / items keyed by id. A `LessonStatus.LIVE_NOW` lesson → `UpcomingLessonCard(lesson, onLessonClick = OpenLesson, onJoinZoomClick = JoinZoom(lesson.zoomUrl))`; others → `NextLessonItem(lesson, onClick = OpenLesson)`.
         After the Scaffold, when `state.checkout.isVisible && subscription != null`, show `JanaBottomSheet(onDismissRequest = DismissCheckout, dismissEnabled = !checkout.isBusy) { CourseCheckoutSheetContent(itemTitle = subscription.planName, priceText = subscription.price?.formatted(), checkout, …intents) }`.
      d) Create `HOME/tabs/teacher/TeacherTabScreen.kt` (`@Composable fun TeacherTabScreen() { TeacherProfileScreen() }`), delete `HOME/tabs/chat/MyTeacherChatTabScreen.kt`, and update the `HomeScreen.kt` import and `HomeTab.MY_TEACHER_CHAT -> TeacherTabScreen()`. Register `viewModel { TeacherProfileViewModel(get(), get(), get(), get(), get(), get(), get()) }` in `teacherModule`.
      e) Write `.agents/tasks/teacher_impl.md`. It starts with "Tests intentionally skipped per user request", then lists the decisions above, files changed, the INTERNET-permission follow-up, the mock/TODO endpoints, and the verification command results.
      Files: HOME/tabs/teacher/presentation/profile/{TeacherProfileContract.kt, TeacherProfileViewModel.kt, TeacherProfileScreen.kt}, HOME/tabs/teacher/TeacherTabScreen.kt, HOME/tabs/chat/MyTeacherChatTabScreen.kt (delete), HOME/HomeScreen.kt, HOME/tabs/teacher/di/TeacherModule.kt, .agents/tasks/teacher_impl.md.
      Verify: commands 1–4 succeed. Grep confirms that `MyTeacherChatTabScreen` has no remaining references, as a supporting check only.

## Gaps and assumptions

- "Live lessons and subscriptions" is not tied to lesson access. Lessons stay viewable and joinable regardless of the live subscription. Add a TODO in the ViewModel for backend-driven gating.
- The teacher bio and plan name come from mock data, not string resources, because they are backend content.
- No auto-reply from the teacher in the mock chat. Real-time delivery is behind the WebSocket TODO.
- The Live tab mock lessons show various teacher names in `LiveLesson.teacherName`. The Teacher tab does not display that field, and the backend scopes lessons to the active platform.
