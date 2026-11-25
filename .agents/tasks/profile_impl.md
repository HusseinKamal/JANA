Tests intentionally skipped per user request

# Profile tab implementation log

## FEAT-001: runtime language switching and shared settings components (plan items 1-7)

Commit `97c5b38` on `develop` (local only).

Verification (all run with `--offline` from `d:\AndroidApps\JANA\JANA`):

| Command | Result |
|---|---|
| `.\gradlew.bat :core:designsystem:compileCommonMainKotlinMetadata` | BUILD SUCCESSFUL, exit 0 |
| `.\gradlew.bat :core:designsystem:compileKotlinJvm :core:navigation:compileCommonMainKotlinMetadata` | BUILD SUCCESSFUL, exit 0 |
| `.\gradlew.bat :core:datastore:jvmTest` | BUILD SUCCESSFUL, exit 0 |
| `.\gradlew.bat :composeApp:compileKotlinJvm :desktopApp:compileKotlin` | BUILD SUCCESSFUL, exit 0 |
| `.\gradlew.bat :feature:home:jvmTest` | BUILD SUCCESSFUL, exit 0, 127 tests, 0 failures |
| `.\gradlew.bat :feature:home:compileCommonMainKotlinMetadata :androidApp:assembleDebug` | BUILD SUCCESSFUL, exit 0 |
| `.\gradlew.bat :core:designsystem:compileIosMainKotlinMetadata` | SKIPPED by Gradle (Kotlin/Native not available on Windows); iOS actuals not compile-verified |

String keys: 388 in `values/strings.xml` and 388 in `values-ar/strings.xml`, same key set.

Deviations from the plan: `JanaConfirmDialog` has an extra `modifier` param; destructive settings rows use an `errorContainer` icon box; no coroutines dependency was added to designsystem (already on the classpath).

## FEAT-002: student Profile tab, Edit Profile, Syllabus, Progress (plan items 8-14)

Commit `43e0c7c` on `develop` (local only, not pushed). 43 files, nothing under `.agents/` staged.

### Files changed

Modified:
- `feature/home/.../HomeScreen.kt`: Profile tab gets `onOpenTab = { onIntent(HomeIntent.TabSelected(it)) }`.
- `feature/home/.../HomeEntryProvider.kt`: entries for `HomeRoute.EditProfile`, `HomeRoute.Syllabus`, `HomeRoute.Progress`.
- `feature/home/.../di/HomeModule.kt`: `includes(..., profileModule)`.
- `feature/home/.../tabs/profile/ProfileTabScreen.kt`: stub replaced by `ProfileTabScreen(onOpenTab)` delegating to `ProfileScreen(onOpenTeacherTab = { onOpenTab(HomeTab.MY_TEACHER_CHAT) })`.

New, under `feature/home/src/commonMain/kotlin/com/hussein/jana/feature/home/tabs/profile/`:
- `domain/model/`: `StudentProfile`, `NotificationPreferences` (+ `NotificationType`), `LearningProgress` (+ `CourseProgress`), `SyllabusCourse`, `ProfileValidation` (`ProfileFieldError`, `StudentProfileValidator`).
- `domain/repository/`: `StudentProfileRepository`, `AppSettingsRepository`.
- `domain/usecase/`: `GetStudentProfileUseCase`, `ObserveStudentProfileUseCase`, `UpdateStudentProfileUseCase`, `LogoutUseCase`, `ObserveAppLanguageUseCase`, `SetAppLanguageUseCase`, `ObserveNotificationPreferencesUseCase`, `UpdateNotificationPreferencesUseCase`, `GetSyllabusUseCase` (subscribed courses, units fetched concurrently), `GetLearningProgressUseCase`.
- `data/mock/MockStudentData.kt`, `data/repository/StudentProfileRepositoryImpl.kt`, `data/repository/AppSettingsRepositoryImpl.kt`.
- `di/ProfileModule.kt`.
- `presentation/common/ProfileFormatting.kt` (`percentText`, `lessonsProgressText`, `AppLanguage.label`, `SubscriptionStatus.label`, `ProfileFieldError.message`).
- `presentation/profile/`: `ProfileContract`, `ProfileViewModel`, `ProfileScreen`, `components/{ProfileHeaderCard, ProgressSummaryCard, NotificationSettingsSection}`.
- `presentation/edit/`: `EditProfileContract`, `EditProfileViewModel`, `EditProfileScreen`, `components/EditProfileForm`.
- `presentation/syllabus/`: `SyllabusContract`, `SyllabusViewModel`, `SyllabusScreen`, `components/SyllabusCourseHeader` (reuses `tabs/courses/.../CourseUnitSection` in place).
- `presentation/progress/`: `ProgressContract`, `ProgressViewModel`, `ProgressScreen`.

No string keys were added in FEAT-002; every key used was added by FEAT-001 or already existed. `tabs/progress/ProgressTabScreen.kt` is untouched.

### Profile tab sections

Header (photo, name, email, phone, Edit profile), Learning (progress ring summary, Progress row, Syllabus row), Notifications (master switch + 5 type switches, disabled while the master is off), Preferences (language row with current value, opens an English/Arabic selection sheet), Teacher platform (current teacher only, as "Mr./Mrs. name · subject"; My subscriptions with subscribed course count and live subscription status; both rows switch to the Teacher tab), About (app version), Log out (destructive row, confirm dialog with loading, then `navigator.replaceAll(AuthRoute.Login())`).

### WearVerse files used (read-only reference, D:\AndroidApps\WearVerse)

- `feature/profile/.../presentation/profile/ProfileScreen.kt`: `LazyListScope` section functions and a stateless `ProfileContent(state, onIntent)`.
- `.../profile/ProfileContract.kt`: intent/effect naming (`EditProfileClicked`, `NotificationsToggled`, `LanguageSelected`, `LogoutClicked`, `NavigateToLogin`).
- `.../profile/components/ProfileMenuSection.kt`, `ProfileSectionRow.kt`, `ProfileToggleRow.kt`, `ProfileLanguageRow.kt`, `ProfileLogoutButton.kt`, `ProfileAppVersionFooter.kt`: mapped to `JanaSettingsSection` / `JanaSettingsRow` / `JanaSwitchRow` (FEAT-001), the language row + selection sheet, the destructive logout row and the version row.
- `.../presentation/editprofile/EditProfileContract.kt`, `EditProfileScreen.kt`: per-field errors, `isSaving`, save action.

### Locale approach per platform

The language switch calls `SetAppLanguageUseCase` -> `AppSettingsRepositoryImpl.setLanguage` -> `AppLocaleManager.setLanguage`. The app root (FEAT-001) re-provides `LocalAppLocale` and layout direction inside `key(language)` while the back stack and ViewModel stores live above the key, so the student stays on the Profile tab. No restart code path.
- Android: `LocalAppLocale` updates `Locale.setDefault` and the `Configuration`; persisted in SharedPreferences. Live switch, no Activity recreate.
- Desktop (JVM): `Locale.setDefault` + composition local; persisted in `java.util.prefs`. Live switch, no process restart.
- iOS: JetBrains `AppleLanguages` pattern with NSUserDefaults key `jana_selected_language`. Not compile- or run-verified on Windows.

### Persistence

- Language: synchronous platform prefs via `AppLocaleManager`, so the root knows the layout direction before the first frame.
- Notification switches: DataStore `AppPreferences.notificationSettings` / `setNotificationSettings`, mapped to `NotificationPreferences`.
- Student profile: in-memory `MutableStateFlow` in the singleton `StudentProfileRepositoryImpl` (mock). Edit Profile updates it and the Profile tab observes it. Logout clears `TokenRepository` and resets the mock profile.

### TODOs left in code

- `GET /api/v1/me/profile`, `PUT /api/v1/me/profile`, `POST /api/v1/auth/logout` (+ clear cached courses/chat per teacher account) in `StudentProfileRepositoryImpl`.
- `PUT /api/v1/me/notification-settings` sync in `AppSettingsRepositoryImpl`.
- `GET /api/v1/me/progress` in `GetLearningProgressUseCase` (exams = completed lessons with an exam; attended = count of previous live lessons, hidden when that call fails).
- OS notification permission request (POST_NOTIFICATIONS / UNUserNotificationCenter) in `ProfileViewModel`.
- Image picker + `POST /api/v1/me/avatar` in `EditProfileForm` (after review: the "Change photo" button is hidden until upload exists; port WearVerse `ImagePicker.kt` then).
- Multi-account switching between saved teacher platforms in `ProfileViewModel` (only the current teacher is shown).

### Deviations from the plan

- Progress screen stat row: the first card shows the subscribed course count (`profile_progress_courses`) instead of a hardcoded `x/y` lessons value. Completed/total lessons stay under the ring through `profile_progress_lessons`.
- `ProgressSummaryCard` renders inside the Learning `JanaSettingsSection` card, followed by the Progress and Syllabus rows. Header and syllabus course header reuse `JanaSettingsSection(title = null)` as their bordered card.
- Edit Profile keeps `isSaving = true` after a successful save until the screen closes, to block a second save during the 900 ms feedback delay.

### Verification (all with `--offline`, from `d:\AndroidApps\JANA\JANA`)

| Command | Result |
|---|---|
| `.\gradlew.bat :core:designsystem:compileCommonMainKotlinMetadata` | BUILD SUCCESSFUL, exit 0 |
| `.\gradlew.bat :feature:home:compileCommonMainKotlinMetadata` | BUILD SUCCESSFUL, exit 0 |
| `.\gradlew.bat :feature:home:jvmTest` | BUILD SUCCESSFUL, exit 0, 127 tests, 0 failures, 0 errors (no test changes) |
| `.\gradlew.bat :androidApp:assembleDebug` | BUILD SUCCESSFUL, exit 0 |
| `.\gradlew.bat :core:datastore:jvmTest :composeApp:compileKotlinJvm :desktopApp:compileKotlin` | BUILD SUCCESSFUL, exit 0 |
| `.\gradlew.bat :core:designsystem:compileIosMainKotlinMetadata :composeApp:compileIosMainKotlinMetadata` (best effort) | exit 1. The designsystem and feature:home iOS tasks are SKIPPED (no Kotlin/Native on Windows). The failure is in `:core:database:compileCommonMainKotlinMetadata` (`JanaDatabaseConstructor` does not implement `initialize()`), which `:composeApp` iOS metadata pulls in. `core/database` was not touched by FEAT-001 or FEAT-002. Run alone, `:core:designsystem:compileIosMainKotlinMetadata` exits 0 with the task SKIPPED. |

## Cross-FEAT integration check (fixer, iteration 1)

Run on `develop` at `43e0c7c`, all with `--offline`, from `d:\AndroidApps\JANA\JANA`. No source changes were needed, so there is no fix commit.

| Command | Result |
|---|---|
| `.\gradlew.bat :core:designsystem:compileCommonMainKotlinMetadata` | BUILD SUCCESSFUL, exit 0 |
| `.\gradlew.bat :feature:home:compileCommonMainKotlinMetadata` | BUILD SUCCESSFUL, exit 0 |
| `.\gradlew.bat :feature:home:jvmTest` | BUILD SUCCESSFUL, exit 0, 127 tests, 0 failures, 0 errors (from `build/test-results/jvmTest`) |
| `.\gradlew.bat :androidApp:assembleDebug` | BUILD SUCCESSFUL, exit 0 |
| `.\gradlew.bat :core:datastore:jvmTest :composeApp:compileKotlinJvm :desktopApp:compileKotlin` | BUILD SUCCESSFUL, exit 0 |
| `.\gradlew.bat :core:designsystem:compileIosMainKotlinMetadata :composeApp:compileIosMainKotlinMetadata` (best effort) | exit 1, same pre-existing `:core:database:compileCommonMainKotlinMetadata` error (`JanaDatabaseConstructor` does not implement `initialize()`). designsystem/datastore/common iOS tasks SKIPPED (no Kotlin/Native on Windows). |

Seam checks between FEAT-001 and FEAT-002:
- Strings: 388 keys in `values/strings.xml` and 388 in `values-ar/strings.xml`, identical key sets, no duplicates. Every `Res.string.*` referenced in Kotlin (388 distinct) exists. `%N$s` / `%N$d` placeholders match between EN and AR for every key.
- DI: `profileModule` is included in `homeModule`. Every constructor dependency resolves: `AppLocaleManager` (shared `KoinInit`, used by Android, desktop and iOS `initKoin`), `AppPreferences` and `TokenRepository` (`coreModule`), `GetCoursesUseCase` / `GetCourseUnitsUseCase` (`coursesModule`), `GetActiveTeacherProfileUseCase` / `GetLiveLessonsSubscriptionUseCase` (`teacherModule`), `GetPreviousLiveLessonsUseCase` (`liveLessonsModule`). `ProfileViewModel` takes 11 params and gets 11 `get()` calls.
- Navigation: `HomeRoute.EditProfile`, `HomeRoute.Syllabus` and `HomeRoute.Progress` each have a `NavEntry` in `HomeEntryProvider` with `onBack = { navigator.back() }`. No polymorphic `NavKey` serializer registry exists that would need the new routes.
- No hardcoded `Text("...")`, `contentDescription = "..."` or `Color(0x...)` in `tabs/profile`.

## Review fixes (fixer, iteration 2)

Commit `cca5c20` on `develop` (local only, not pushed). Nothing under `.agents/` staged.

| Finding | Status |
|---|---|
| 1. Stale subscriptions and progress (blocking) | Fixed. `ProfileViewModel` takes `ObserveCoursesUseCase` (12 params, `profileModule` has 12 `get()`). It collects `observeCourses().drop(1)` (the replayed StateFlow value is covered by `Load`) and on each change updates `subscribedCoursesCount` and re-runs `loadProgress()`. New `ProfileIntent.ScreenShown`, sent from `LaunchedEffect(Unit)` in `ProfileScreen`, re-fetches `getLiveLessonsSubscription()` each time the tab is shown again (the first show is skipped because `Load` already fetched it). |
| 2. Phone reversed in Arabic (blocking) | Fixed. `JanaTextField` has an optional `textDirection` (default `TextDirection.Unspecified`, so other fields are unchanged), applied to the field text and the placeholder; when set, the text gets an absolute alignment matching the layout start edge so the phone field stays right-aligned in Arabic. `EditProfileForm` passes `TextDirection.Ltr` for the phone field. `ProfileHeaderCard` wraps the phone in LTR isolate marks via `String.ltrIsolated()` in `ProfileFormatting.kt` (blank phone still hides the line). |
| 3. OS notification permission | Kept as the TODO in `ProfileViewModel` (option allowed by the review); tracked in "TODOs left in code" above. |
| 4. "Change photo" stub | User chose option B (commit `8bb7b7c`): the "Change photo" button is hidden. The avatar still shows the photo URL or initials. Removed `EditProfileIntent.ChangePhotoClicked`, `EditProfileEffect.ShowPhotoComingSoon`, the `onChangePhotoClick` form param, and the now-unused keys `profile_change_photo` and `profile_photo_coming_soon` from both `values` and `values-ar`. A TODO above the avatar in `EditProfileForm` notes that the WearVerse `ImagePicker.kt` expect/actual can be ported once `POST /api/v1/me/avatar` exists. |
| 5. Unused `CourseClicked` / `NavigateToCourseDetail` | Removed from `ProfileContract`, `ProfileViewModel` and the `ProfileScreen` effect branch. |
| 6. iOS actuals not compile-verified | No code change. Still needs an iOS compile of `:core:designsystem` on macOS before release. |

Verification (all with `--offline`, from `d:\AndroidApps\JANA\JANA`):

| Command | Result |
|---|---|
| `.\gradlew.bat :core:designsystem:compileCommonMainKotlinMetadata` | exit 0 |
| `.\gradlew.bat :feature:home:compileCommonMainKotlinMetadata` | exit 0 |
| `.\gradlew.bat :feature:home:jvmTest` | BUILD SUCCESSFUL, exit 0, 127 tests, 0 failures, 0 errors (no test changes) |
| `.\gradlew.bat :androidApp:assembleDebug` | BUILD SUCCESSFUL, exit 0 |
| `.\gradlew.bat :core:datastore:jvmTest :composeApp:compileKotlinJvm :desktopApp:compileKotlin` | BUILD SUCCESSFUL, exit 0 |
| `.\gradlew.bat :core:designsystem:compileIosMainKotlinMetadata :composeApp:compileIosMainKotlinMetadata` (best effort) | exit 1, same pre-existing `:core:database:compileCommonMainKotlinMetadata` error (`JanaDatabaseConstructor` does not implement `initialize()`); designsystem and feature:home iOS tasks SKIPPED (no Kotlin/Native on Windows). |

String keys after `8bb7b7c`: 386 in `values/strings.xml` and 386 in `values-ar/strings.xml`, identical key sets (2 unused photo keys removed from both).

Verification after `8bb7b7c` (same commands, `--offline`): designsystem and feature:home commonMain metadata exit 0; `:feature:home:jvmTest` BUILD SUCCESSFUL, 127 tests, 0 failures, 0 errors; `:androidApp:assembleDebug` BUILD SUCCESSFUL; `:core:datastore:jvmTest :composeApp:compileKotlinJvm :desktopApp:compileKotlin` BUILD SUCCESSFUL; iOS best effort exit 1 with the same pre-existing `:core:database` error, designsystem iOS task SKIPPED.
