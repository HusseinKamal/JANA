# Implementation Plan: Fix Login/Register + Rebuild Home 5-Tab Nav

## Codebase Findings (read before changing anything)

- **Package root**: `com.hussein.jana`
- **Feature modules**: `feature/auth`, `feature/home`, `feature/splash`
- **Core modules**: `core/common`, `core/designsystem`, `core/di`, `core/navigation`, `core/datastore`, `core/database`
- **String resources**: single source in `core/designsystem/src/commonMain/composeResources/values/strings.xml` (EN) and `values-ar/strings.xml` (AR). All string keys are auto-generated as Kotlin properties in `com.hussein.jana.core.designsystem.Res`.
- **MVI base**: `BaseMviViewModel<State, Intent, Effect>` in `core/common`. State via `StateFlow`, Effects via `SharedFlow`. Pattern: `setState { copy(...) }`, `sendEffect(...)`, `currentState()`.
- **Koin DI**: ViewModels registered with `viewModel { ... }` DSL in feature-level `*Module.kt` files. `koinViewModel()` used in composables. `koinInject()` for non-ViewModel dependencies.
- **Navigation**: Navigation 3 (`androidx.navigation3`). `AppNavHost` manages a `mutableStateListOf<NavKey>` back stack. Each feature has an `*EntryProvider` registered as `FeatureEntryProvider` with a named qualifier. Routes are `@Serializable data object/class` implementing `NavKey`.
- **Design system components in scope**: `AuthTextField`, `PasswordField` (in `feature/auth/components`); `JanaButton`, `JanaCard`, `JanaTopBar`, `JanaLogoCard` (in `core/designsystem`); colours `ScholarlyTeal`, `AmberDark`, `OnSurfaceVariant`, `VellumCanvas`, `OutlineVariant`.
- **Icons**: `compose.material.icons.extended` is already a dependency in both `feature/auth` and `feature/home`.
- **Current Login state**: `LoginState(identifier, password, isPasswordVisible, rememberDevice, isLoading, error)` — **missing `platformCode`**.
- **Current Register state**: `RegisterState(fullName, phone, password, confirmPassword, …)` — **missing `platformCode` and `email`**.
- **Current HomeTab enum**: `HOME, TEACHERS, JANA_AI, SCHEDULE, PROGRESS` — **wrong tabs**.
- **Existing tests**: `HomeViewModelTest` references `HomeTab.HOME`, `HomeTab.TEACHERS`, `HomeTab.SCHEDULE`, `HomeTab.PROGRESS` — these will **fail to compile** if the enum is changed without updating the tests. `AuthViewModelTest` and `RegisterViewModelTest` will produce runtime failures (not compile failures) unless updated.
- **Build command**: `.\gradlew.bat :androidApp:assembleDebug` from `d:\AndroidApps\JANA\JANA`.

---

## Plan

- [ ] 1. Add all missing string resources to both values files.

  Add the following keys to `values/strings.xml` (English) **and** `values-ar/strings.xml` (Arabic). Use the
  exact key names listed; the values follow.

  **English additions** (`values/strings.xml`):
  ```xml
  <!-- Auth – teacher platform code (login + register) -->
  <string name="auth_platform_code_label">Teacher Platform Code</string>
  <string name="auth_platform_code_hint">e.g. ahmed_edu</string>
  <string name="auth_error_missing_platform_code">Please enter the teacher platform code</string>
  <!-- Register – email/username -->
  <string name="register_email_label">Email or Username</string>
  <string name="register_email_hint">student@example.com</string>
  <string name="register_platform_code_label">Teacher Platform Code</string>
  <string name="register_platform_code_hint">e.g. ahmed_edu</string>
  <string name="error_platform_code_required">Teacher platform code is required</string>
  <string name="error_email_required">Email or username is required</string>
  <string name="error_email_invalid">Please enter a valid email or username</string>
  <!-- Home – new 5-tab bottom navigation -->
  <string name="tab_live_lessons">Live Lessons</string>
  <string name="tab_my_teacher_chat">My Teacher Chat</string>
  <string name="tab_jana_ai">JANA AI</string>
  <string name="tab_courses">Courses</string>
  <!-- tab_profile already exists -->
  ```

  **Arabic additions** (`values-ar/strings.xml`):
  ```xml
  <string name="auth_platform_code_label">كود منصة المعلم</string>
  <string name="auth_platform_code_hint">مثال: ahmed_edu</string>
  <string name="auth_error_missing_platform_code">يرجى إدخال كود منصة المعلم</string>
  <string name="register_email_label">البريد الإلكتروني أو اسم المستخدم</string>
  <string name="register_email_hint">student@example.com</string>
  <string name="register_platform_code_label">كود منصة المعلم</string>
  <string name="register_platform_code_hint">مثال: ahmed_edu</string>
  <string name="error_platform_code_required">كود منصة المعلم مطلوب</string>
  <string name="error_email_required">البريد الإلكتروني أو اسم المستخدم مطلوب</string>
  <string name="error_email_invalid">يرجى إدخال بريد إلكتروني أو اسم مستخدم صحيح</string>
  <string name="tab_live_lessons">دروس مباشرة</string>
  <string name="tab_my_teacher_chat">محادثة المعلم</string>
  <string name="tab_jana_ai">جنى AI</string>
  <string name="tab_courses">الكورسات</string>
  ```

  Files:
  - `d:\AndroidApps\JANA\JANA\core\designsystem\src\commonMain\composeResources\values\strings.xml`
  - `d:\AndroidApps\JANA\JANA\core\designsystem\src\commonMain\composeResources\values-ar\strings.xml`

  Verify: `.\gradlew.bat :core:designsystem:generateCommonMainComposeResources` exits 0. If that task doesn't exist, `.\gradlew.bat :androidApp:assembleDebug` at the end will verify.

---

- [ ] 2. Add `platformCode` to `LoginState` and `LoginIntent` in `AuthContract.kt`.

  Add `platformCode: String = ""` to `LoginState` (before `isLoading`).
  Add `PlatformCodeChanged(val value: String) : LoginIntent` to the sealed interface.
  Add `errorMissingPlatformCode: String = "Please enter the teacher platform code"` to `AuthStrings`.

  File: `d:\AndroidApps\JANA\JANA\feature\auth\src\commonMain\kotlin\com\hussein\jana\feature\auth\AuthContract.kt`

  Verify: `.\gradlew.bat :feature:auth:compileCommonMainKotlinMetadata` exits 0, or defer to final assembleDebug.

---

- [ ] 3. Update `AuthViewModel` to handle `PlatformCodeChanged` and validate `platformCode`.

  In `onIntent`:
  ```kotlin
  is LoginIntent.PlatformCodeChanged -> setState { copy(platformCode = intent.value) }
  ```
  In `handleSignIn()`, add a guard before the loading block:
  ```kotlin
  if (state.platformCode.isBlank() || state.identifier.isBlank() || state.password.isBlank()) {
      val msg = if (state.platformCode.isBlank()) strings.errorMissingPlatformCode
                else strings.errorMissingFields
      setState { copy(error = msg) }
      return
  }
  ```
  Also inject the new string from the composition layer: add `errorMissingPlatformCode` resolution in `AuthScreen.kt`'s `LaunchedEffect` and pass it into `updateStrings(...)`.

  Files:
  - `d:\AndroidApps\JANA\JANA\feature\auth\src\commonMain\kotlin\com\hussein\jana\feature\auth\AuthViewModel.kt`
  - `d:\AndroidApps\JANA\JANA\feature\auth\src\commonMain\kotlin\com\hussein\jana\feature\auth\AuthScreen.kt`

---

- [ ] 4. Add the platform code field to the Login UI in `StudentLoginContent.kt`.

  Insert a new `AuthTextField` block **at the top of the form**, before the identifier field:
  - Label: `stringResource(Res.string.auth_platform_code_label) + " *"`
  - Value: `state.platformCode`
  - OnValueChange: `onIntent(LoginIntent.PlatformCodeChanged(it))`
  - Placeholder: `stringResource(Res.string.auth_platform_code_hint)`
  - LeadingIcon: `Icons.Rounded.School` (tinted `OnSurfaceVariant`)
  - Add `Spacer(Modifier.height(16.dp))` after the field, before the identifier field.

  File: `d:\AndroidApps\JANA\JANA\feature\auth\src\commonMain\kotlin\com\hussein\jana\feature\auth\components\StudentLoginContent.kt`

  Verify: Screen compiles and shows the platform code field at the top (visual check at step 14).

---

- [ ] 5. Add `platformCode` and `email` to `RegisterState` and `RegisterIntent` in `RegisterContract.kt`.

  Add to `RegisterState` (after the existing `fullName`/`fullNameError` pair, before `phone`):
  ```kotlin
  val platformCode: String = "",
  val platformCodeError: String? = null,
  val email: String = "",
  val emailError: String? = null,
  ```
  Add to `RegisterIntent`:
  ```kotlin
  data class PlatformCodeChanged(val value: String) : RegisterIntent
  data class EmailChanged(val value: String) : RegisterIntent
  ```
  Add to `RegisterStrings`:
  ```kotlin
  val errorPlatformCodeRequired: String = "",
  val errorEmailRequired: String = "",
  val errorEmailInvalid: String = "",
  ```

  File: `d:\AndroidApps\JANA\JANA\feature\auth\src\commonMain\kotlin\com\hussein\jana\feature\auth\register\RegisterContract.kt`

---

- [ ] 6. Update `RegisterViewModel` to handle new intents and validate `platformCode` and `email`.

  In `onIntent`, add:
  ```kotlin
  is RegisterIntent.PlatformCodeChanged -> setState { copy(platformCode = intent.value, platformCodeError = null) }
  is RegisterIntent.EmailChanged        -> setState { copy(email = intent.value, emailError = null) }
  ```

  In `handleRegister()`, add validation:
  ```kotlin
  val platformCodeError = if (s.platformCode.isBlank()) strings.errorPlatformCodeRequired else null
  val emailError = when {
      s.email.isBlank()                              -> strings.errorEmailRequired
      !s.email.contains("@") && s.email.length < 3  -> strings.errorEmailInvalid
      else                                            -> null
  }
  ```
  Include `platformCodeError` and `emailError` in the guard block that calls `setState { copy(...) }` and returns early.

  In `RegisterFlow` composable in `RegisterScreen.kt`, resolve the two new strings with `stringResource(...)` and pass them through `viewModel.updateStrings(...)`.

  Files:
  - `d:\AndroidApps\JANA\JANA\feature\auth\src\commonMain\kotlin\com\hussein\jana\feature\auth\register\RegisterViewModel.kt`
  - `d:\AndroidApps\JANA\JANA\feature\auth\src\commonMain\kotlin\com\hussein\jana\feature\auth\register\RegisterScreen.kt`

---

- [ ] 7. Add the platform code and email fields to the Register UI in `PersonalCredentialsCard.kt`.

  Insert two new `AuthTextField` blocks **at the top of the card's Column**, before the existing full-name field:

  **Platform Code field** (first):
  - Label: `stringResource(Res.string.register_platform_code_label)`
  - Value: `state.platformCode`
  - OnValueChange: `onIntent(RegisterIntent.PlatformCodeChanged(it))`
  - Placeholder: `stringResource(Res.string.register_platform_code_hint)`
  - LeadingIcon: `Icons.Rounded.School`
  - Inline error: `if (state.platformCodeError != null) Text(...)` in `MaterialTheme.colorScheme.error`
  - `Spacer(Modifier.height(16.dp))` after the field

  **Email / Username field** (second, before full name):
  - Label: `stringResource(Res.string.register_email_label)`
  - Value: `state.email`
  - OnValueChange: `onIntent(RegisterIntent.EmailChanged(it))`
  - Placeholder: `stringResource(Res.string.register_email_hint)`
  - LeadingIcon: `Icons.Rounded.AlternateEmail`
  - Inline error: `if (state.emailError != null) Text(...)` in error colour
  - `Spacer(Modifier.height(16.dp))` after the field

  The existing full-name and phone fields remain below these two new fields.

  File: `d:\AndroidApps\JANA\JANA\feature\auth\src\commonMain\kotlin\com\hussein\jana\feature\auth\register\components\PersonalCredentialsCard.kt`

---

- [ ] 8. Update the `HomeTab` enum and `HomeState` default in `HomeContract.kt`.

  Replace the existing `HomeTab` enum entirely:
  ```kotlin
  enum class HomeTab { LIVE_LESSONS, MY_TEACHER_CHAT, JANA_AI, COURSES, PROFILE }
  ```
  Change `HomeState.selectedTab` default to `HomeTab.LIVE_LESSONS`.

  File: `d:\AndroidApps\JANA\JANA\feature\home\src\commonMain\kotlin\com\hussein\jana\feature\home\HomeContract.kt`

  **Why this ordering matters**: This change will cause compile errors in `HomeScreen.kt`, `HomeViewModelTest.kt`, and any file that references the old enum values. Steps 9–11 fix those in dependency order.

---

- [ ] 9. Create four stub tab screens for the new bottom nav tabs.

  Each file follows the exact pattern of the existing
  `d:\AndroidApps\JANA\JANA\feature\home\src\commonMain\kotlin\com\hussein\jana\feature\home\tabs\progress\ProgressTabScreen.kt`:
  a full-screen `Box` with centred `Text` using the corresponding tab string resource.

  **File 1** — Live Lessons stub:
  ```
  d:\AndroidApps\JANA\JANA\feature\home\src\commonMain\kotlin\com\hussein\jana\feature\home\tabs\live\LiveLessonsTabScreen.kt
  ```
  Package: `com.hussein.jana.feature.home.tabs.live`
  String resource: `Res.string.tab_live_lessons`

  **File 2** — My Teacher Chat stub:
  ```
  d:\AndroidApps\JANA\JANA\feature\home\src\commonMain\kotlin\com\hussein\jana\feature\home\tabs\chat\MyTeacherChatTabScreen.kt
  ```
  Package: `com.hussein.jana.feature.home.tabs.chat`
  String resource: `Res.string.tab_my_teacher_chat`

  **File 3** — Courses stub:
  ```
  d:\AndroidApps\JANA\JANA\feature\home\src\commonMain\kotlin\com\hussein\jana\feature\home\tabs\courses\CoursesTabScreen.kt
  ```
  Package: `com.hussein.jana.feature.home.tabs.courses`
  String resource: `Res.string.tab_courses`

  **File 4** — Profile stub:
  ```
  d:\AndroidApps\JANA\JANA\feature\home\src\commonMain\kotlin\com\hussein\jana\feature\home\tabs\profile\ProfileTabScreen.kt
  ```
  Package: `com.hussein.jana.feature.home.tabs.profile`
  String resource: `Res.string.tab_profile`

  All four screens are `@Composable fun`, no ViewModel, no state. `OnSurfaceVariant` for text colour, `MaterialTheme.typography.headlineSmall` for text style, same as `ProgressTabScreen`.

---

- [ ] 10. Rewrite `HomeScreen.kt` to use the new 5-tab structure.

  **Imports to remove**: `HomeTab.TEACHERS`, `HomeTab.HOME`, `HomeTab.SCHEDULE`, `HomeTab.PROGRESS`, and all old tab screen imports (`HomeTabScreen`, `TeachersTabScreen`, `ScheduleTabScreen`, `ProgressTabScreen`).

  **Imports to add**: the four new stub screens from step 9, plus `JanaAiTabScreen` (stays).

  **`HomeScreen` composable — `topBar`**: Change `when (state.selectedTab)` to:
  ```kotlin
  val topBarTitle = when (state.selectedTab) {
      HomeTab.JANA_AI -> stringResource(Res.string.tab_jana_ai)
      HomeTab.COURSES -> stringResource(Res.string.tab_courses)
      else            -> stringResource(Res.string.app_name)   // "JANA" / "جَنَى"
  }
  ```
  Pass `showAvatar = true` to `JanaTopBar`. Keep `credits = state.credits`.

  **`HomeScreen` composable — content `when` block**:
  ```kotlin
  when (state.selectedTab) {
      HomeTab.LIVE_LESSONS     -> LiveLessonsTabScreen()
      HomeTab.MY_TEACHER_CHAT  -> MyTeacherChatTabScreen()
      HomeTab.JANA_AI          -> JanaAiTabScreen()
      HomeTab.COURSES          -> CoursesTabScreen()
      HomeTab.PROFILE          -> ProfileTabScreen()
  }
  ```

  **`HomeBottomNav` composable** — replace all five `NavigationBarItem` blocks with:
  ```kotlin
  // (1) Live Lessons
  NavigationBarItem(
      selected = selectedTab == HomeTab.LIVE_LESSONS,
      onClick = { onTabSelected(HomeTab.LIVE_LESSONS) },
      icon = { Icon(Icons.Rounded.Videocam, contentDescription = null) },
      label = { Text(stringResource(Res.string.tab_live_lessons)) },
      colors = itemColors,
  )
  // (2) My Teacher Chat
  NavigationBarItem(
      selected = selectedTab == HomeTab.MY_TEACHER_CHAT,
      onClick = { onTabSelected(HomeTab.MY_TEACHER_CHAT) },
      icon = { Icon(Icons.AutoMirrored.Rounded.Chat, contentDescription = null) },
      label = { Text(stringResource(Res.string.tab_my_teacher_chat)) },
      colors = itemColors,
  )
  // (3) JANA AI
  NavigationBarItem(
      selected = selectedTab == HomeTab.JANA_AI,
      onClick = { onTabSelected(HomeTab.JANA_AI) },
      icon = { Icon(Icons.Rounded.AutoAwesome, contentDescription = null) },
      label = { Text(stringResource(Res.string.tab_jana_ai)) },
      colors = itemColors,
  )
  // (4) Courses
  NavigationBarItem(
      selected = selectedTab == HomeTab.COURSES,
      onClick = { onTabSelected(HomeTab.COURSES) },
      icon = { Icon(Icons.Rounded.MenuBook, contentDescription = null) },
      label = { Text(stringResource(Res.string.tab_courses)) },
      colors = itemColors,
  )
  // (5) Profile
  NavigationBarItem(
      selected = selectedTab == HomeTab.PROFILE,
      onClick = { onTabSelected(HomeTab.PROFILE) },
      icon = { Icon(Icons.Rounded.Person, contentDescription = null) },
      label = { Text(stringResource(Res.string.tab_profile)) },
      colors = itemColors,
  )
  ```

  **`itemColors`**: Change `indicatorColor` to `MaterialTheme.colorScheme.primary` (task requirement).
  Keep `selectedIconColor = Color.White`, `selectedTextColor = MaterialTheme.colorScheme.primary`,
  `unselectedIconColor = OnSurfaceVariant`, `unselectedTextColor = OnSurfaceVariant`.

  **Remove old string resource imports** that are no longer needed:
  `teachers_title`, `nav_home`, `nav_teachers`, `nav_schedule`, `nav_progress`, `schedule_title`, `home_student_label`.
  Add new imports: `tab_live_lessons`, `tab_my_teacher_chat`, `tab_jana_ai`, `tab_courses`, `tab_profile`, `app_name`.

  File: `d:\AndroidApps\JANA\JANA\feature\home\src\commonMain\kotlin\com\hussein\jana\feature\home\HomeScreen.kt`

---

- [ ] 11. Update `HomeViewModelTest.kt` to reference the new `HomeTab` enum values.

  The existing test class references `HomeTab.HOME`, `HomeTab.TEACHERS`, `HomeTab.SCHEDULE`, `HomeTab.PROGRESS` — all removed in step 8. Replace as follows:

  - `initial state has HOME tab selected` → assert `HomeTab.LIVE_LESSONS`
  - `TabSelected TEACHERS updates selectedTab` → `HomeTab.MY_TEACHER_CHAT`
  - `TabSelected JANA_AI updates selectedTab` → keep (enum value unchanged)
  - `TabSelected SCHEDULE updates selectedTab` → `HomeTab.COURSES`
  - `TabSelected PROGRESS updates selectedTab` → `HomeTab.PROFILE`
  - `TabSelected HOME returns to HOME tab` → rewrite as `TabSelected LIVE_LESSONS returns to LIVE_LESSONS tab`, switching away to `MY_TEACHER_CHAT` then back to `LIVE_LESSONS`

  File: `d:\AndroidApps\JANA\JANA\feature\home\src\jvmTest\kotlin\com\hussein\jana\feature\home\HomeViewModelTest.kt`

---

- [ ] 12. Update `AuthViewModelTest.kt` to supply `platformCode` in sign-in success tests.

  The two tests that call `SignInClicked` with valid credentials and expect `NavigateToHome` or null error
  must add `vm.onIntent(LoginIntent.PlatformCodeChanged("test_platform"))` before calling `SignInClicked`.

  Specifically update:
  - `SignInClicked with valid credentials emits NavigateToHome` — add `PlatformCodeChanged("test_platform")` before `SignInClicked`
  - `SignInClicked clears previous error` — add `PlatformCodeChanged("test_platform")` before the second `SignInClicked` call

  Also add a new test to cover the new validation path:
  ```kotlin
  @Test
  fun `SignInClicked with blank platformCode sets error`() = runTest(testDispatcher) {
      val vm = buildViewModel()
      vm.onIntent(LoginIntent.IdentifierChanged("user@test.com"))
      vm.onIntent(LoginIntent.PasswordChanged("pass123"))
      // platformCode is blank
      vm.onIntent(LoginIntent.SignInClicked)
      assertEquals("Please enter the teacher platform code", vm.state.value.error)
  }
  ```

  File: `d:\AndroidApps\JANA\JANA\feature\auth\src\jvmTest\kotlin\com\hussein\jana\feature\auth\AuthViewModelTest.kt`

---

- [ ] 13. Update `RegisterViewModelTest.kt` to supply `platformCode` and `email` in the success-path test.

  The test `RegisterClicked with all valid inputs emits NavigateToHome` currently only sets
  `FullNameChanged`, `PhoneChanged`, `PasswordChanged`, `ConfirmPasswordChanged`, `ToggleTerms`.
  It must also set:
  ```kotlin
  vm.onIntent(RegisterIntent.PlatformCodeChanged("test_platform"))
  vm.onIntent(RegisterIntent.EmailChanged("hussein@example.com"))
  ```
  before `RegisterClicked`, otherwise the new validation will block navigation.

  Add two new negative tests covering the new required fields:
  ```kotlin
  @Test
  fun `RegisterClicked with blank platformCode sets errorPlatformCodeRequired`() = ...
      // set all valid fields except platformCode; expect vm.state.value.platformCodeError != null

  @Test
  fun `RegisterClicked with blank email sets errorEmailRequired`() = ...
      // set all valid fields including platformCode, leave email blank; expect emailError != null
  ```

  File: `d:\AndroidApps\JANA\JANA\feature\auth\src\jvmTest\kotlin\com\hussein\jana\feature\auth\register\RegisterViewModelTest.kt`

---

- [ ] 14. Run the build, fix all compile errors, and iterate until clean.

  From `d:\AndroidApps\JANA\JANA`:
  ```
  .\gradlew.bat :androidApp:assembleDebug
  ```

  **Common errors to watch for and fixes**:
  - Unresolved reference to old string keys (e.g. `nav_home`, `nav_teachers`) in `HomeScreen.kt` — verify step 10 removed all old imports and replaced them with new keys.
  - Unresolved reference `HomeTab.HOME` / `.TEACHERS` / `.SCHEDULE` / `.PROGRESS` — verify step 11 updated the test file.
  - `PlatformCodeChanged` not found — verify step 2 added it to `LoginIntent`.
  - `EmailChanged` / `PlatformCodeChanged` not in `RegisterIntent` — verify step 5.
  - Missing import for `Icons.Rounded.Videocam`, `Icons.Rounded.MenuBook`, `Icons.AutoMirrored.Rounded.Chat`, `Icons.Rounded.Person` — these are all in `compose.material.icons.extended`; add the explicit import at the top of `HomeScreen.kt`.

  Run the build command after every set of fixes until it exits 0 and prints `BUILD SUCCESSFUL`.

---

## Files Summary

### Modified
| File | Change |
|------|--------|
| `core/designsystem/src/commonMain/composeResources/values/strings.xml` | +13 string keys |
| `core/designsystem/src/commonMain/composeResources/values-ar/strings.xml` | +13 string keys |
| `feature/auth/src/.../AuthContract.kt` | Add `platformCode` to `LoginState`; add `PlatformCodeChanged` intent; add `errorMissingPlatformCode` to `AuthStrings` |
| `feature/auth/src/.../AuthViewModel.kt` | Handle `PlatformCodeChanged`; validate `platformCode` in `handleSignIn()` |
| `feature/auth/src/.../AuthScreen.kt` | Resolve + inject new error string |
| `feature/auth/src/.../components/StudentLoginContent.kt` | Add platform code `AuthTextField` at top of form |
| `feature/auth/src/.../register/RegisterContract.kt` | Add `platformCode`, `email` to state; add intents; add error strings to `RegisterStrings` |
| `feature/auth/src/.../register/RegisterViewModel.kt` | Handle new intents; validate platform code and email |
| `feature/auth/src/.../register/RegisterScreen.kt` | Resolve + inject new error strings |
| `feature/auth/src/.../register/components/PersonalCredentialsCard.kt` | Add platform code and email fields at top |
| `feature/home/src/.../HomeContract.kt` | Replace `HomeTab` enum; default tab = `LIVE_LESSONS` |
| `feature/home/src/.../HomeScreen.kt` | New 5-tab bottom nav; new content `when`; updated top bar; updated `itemColors` |
| `feature/home/src/jvmTest/.../HomeViewModelTest.kt` | Replace old `HomeTab` references |
| `feature/auth/src/jvmTest/.../AuthViewModelTest.kt` | Add `platformCode` to success-path tests; add blank-platformCode test |
| `feature/auth/src/jvmTest/.../register/RegisterViewModelTest.kt` | Add `platformCode`+`email` to success-path test; add two new negative tests |

### Created (new files)
| File | Description |
|------|-------------|
| `feature/home/src/.../tabs/live/LiveLessonsTabScreen.kt` | Stub full-screen composable, centred `tab_live_lessons` text |
| `feature/home/src/.../tabs/chat/MyTeacherChatTabScreen.kt` | Stub full-screen composable, centred `tab_my_teacher_chat` text |
| `feature/home/src/.../tabs/courses/CoursesTabScreen.kt` | Stub full-screen composable, centred `tab_courses` text |
| `feature/home/src/.../tabs/profile/ProfileTabScreen.kt` | Stub full-screen composable, centred `tab_profile` text |

### Not touched (deliberately)
- `HomeEntryProvider.kt` — no route change
- `HomeModule.kt` — existing ViewModels (TeachersTabViewModel, ScheduleTabViewModel, HomeTabViewModel) stay registered; they still compile and their tests are unaffected
- `TeachersTabScreen.kt`, `ScheduleTabScreen.kt`, `HomeTabScreen.kt` — these orphaned screens are not shown in the new nav but their code remains compilable; removing them is out of scope
- `AuthEntryProvider.kt`, `AuthModule.kt` — no route or VM change needed
- `Routes.kt` — existing `HomeRoute.Root` and `AuthRoute` unchanged
- `AppNavHost.kt`, `KoinInit.kt`, `App.kt` — no change needed
