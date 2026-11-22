# Implementation Record: Login/Register Fix + Home 5-Tab Nav

## Build Command & Result

```
.\gradlew.bat :androidApp:assembleDebug
```
**Result: BUILD SUCCESSFUL**

```
.\gradlew.bat :feature:auth:jvmTest
```
**Result: BUILD SUCCESSFUL** (all auth tests pass)

```
.\gradlew.bat :feature:home:jvmTest --tests "com.hussein.jana.feature.home.HomeViewModelTest"
```
**Result: BUILD SUCCESSFUL** (HomeViewModelTest passes)

**Pre-existing test failures (NOT caused by this change):**
- `ScheduleTabViewModelTest.SyncCalendarClicked emits ShowSnackbar with default syncComingSoon` — ViewModel sends `OpenCalendar` but test expects `ShowSnackbar`. This was present before this change.
- `ScheduleTabViewModelTest.updateStrings changes SyncCalendarClicked snackbar message` — same root cause.

---

## Files Modified

| File | Change |
|------|--------|
| `core/designsystem/src/commonMain/composeResources/values/strings.xml` | Added 13 string keys: `auth_platform_code_label`, `auth_platform_code_hint`, `auth_error_missing_platform_code`, `register_email_label`, `register_email_hint`, `register_platform_code_label`, `register_platform_code_hint`, `error_platform_code_required`, `error_email_required`, `error_email_invalid`, `tab_live_lessons`, `tab_my_teacher_chat`, `tab_jana_ai`, `tab_courses` |
| `core/designsystem/src/commonMain/composeResources/values-ar/strings.xml` | Same 13 keys in Arabic |
| `feature/auth/src/.../AuthContract.kt` | Added `platformCode: String = ""` to `LoginState`; added `PlatformCodeChanged` to `LoginIntent`; added `errorMissingPlatformCode` to `AuthStrings` |
| `feature/auth/src/.../AuthViewModel.kt` | Handles `PlatformCodeChanged`; validates `platformCode` before identifier/password in `handleSignIn()` |
| `feature/auth/src/.../AuthScreen.kt` | Resolves and injects `auth_error_missing_platform_code` string into ViewModel |
| `feature/auth/src/.../components/StudentLoginContent.kt` | Added `School`-icon platform code field at top of form before identifier field |
| `feature/auth/src/.../register/RegisterContract.kt` | Added `platformCode`, `platformCodeError`, `email`, `emailError` to `RegisterState`; added `PlatformCodeChanged` and `EmailChanged` to `RegisterIntent`; added `errorPlatformCodeRequired`, `errorEmailRequired`, `errorEmailInvalid` to `RegisterStrings` |
| `feature/auth/src/.../register/RegisterViewModel.kt` | Handles `PlatformCodeChanged` and `EmailChanged`; validates both in `handleRegister()` |
| `feature/auth/src/.../register/RegisterScreen.kt` | Resolves and injects 3 new error strings into ViewModel |
| `feature/auth/src/.../register/components/PersonalCredentialsCard.kt` | Added `School`-icon platform code field and `AlternateEmail`-icon email field at top of card |
| `feature/home/src/.../HomeContract.kt` | Replaced `HomeTab` enum: `LIVE_LESSONS, MY_TEACHER_CHAT, JANA_AI, COURSES, PROFILE`; default tab = `LIVE_LESSONS` |
| `feature/home/src/.../HomeScreen.kt` | New 5-tab bottom nav; `indicatorColor = MaterialTheme.colorScheme.primary`; new content `when` block; top bar uses `app_name` for most tabs |
| `feature/home/src/jvmTest/.../HomeViewModelTest.kt` | Updated all enum references from old tabs to new tabs |
| `feature/auth/src/jvmTest/.../AuthViewModelTest.kt` | Added `PlatformCodeChanged` to success-path tests; added blank-platformCode test; fixed error expectations |
| `feature/auth/src/jvmTest/.../register/RegisterViewModelTest.kt` | Added `platformCode`+`email` to success-path test; added 2 new negative tests; fixed all existing tests to supply all required fields |

## Files Created

| File | Description |
|------|-------------|
| `feature/home/src/.../tabs/live/LiveLessonsTabScreen.kt` | Stub — centred `tab_live_lessons` text |
| `feature/home/src/.../tabs/chat/MyTeacherChatTabScreen.kt` | Stub — centred `tab_my_teacher_chat` text |
| `feature/home/src/.../tabs/courses/CoursesTabScreen.kt` | Stub — centred `tab_courses` text |
| `feature/home/src/.../tabs/profile/ProfileTabScreen.kt` | Stub — centred `tab_profile` text |

## Files Intentionally Not Touched

- `HomeEntryProvider.kt`, `HomeModule.kt` — no route/VM change needed
- `TeachersTabScreen.kt`, `ScheduleTabScreen.kt`, `HomeTabScreen.kt` — orphaned but still compilable
- `AuthEntryProvider.kt`, `AuthModule.kt` — no route/VM change needed
- `Routes.kt`, `AppNavHost.kt`, `KoinInit.kt`, `App.kt` — no change needed
- `ScheduleTabViewModel.kt`, `ScheduleTabViewModelTest.kt` — pre-existing test failures, out of scope
