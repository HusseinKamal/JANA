# Student Profile tab with runtime ar/en switching (review pass 2)

The change replaces the Profile tab stub with an MVI Profile screen (header with Edit profile, learning summary, Syllabus and Progress screens, notification switches, language sheet, current teacher platform, app version, logout). It also adds the runtime language switch the screen needs. The switch follows the JetBrains `LocalAppLocale` + `key(language)` pattern at the app root, and the back stack, per-entry ViewModel stores and `SaveableStateHolder` are hoisted above the key, so a switch keeps the student on the Profile tab without a restart or Activity recreate. This pass covers the fix commits `cca5c20` and `8bb7b7c`, which resolve both blocking findings from pass 1 and two of the non-blocking ones.

Watch for: each course-catalog change now re-runs `loadProgress()`, which flips the learning summary back to its loading spinner for about the mock delay (confirmed, non-blocking). The OS notification permission is still only a TODO, and the iOS actuals are still not compile-verified (both confirmed, non-blocking, carried from pass 1).

**Verdict**: APPROVED

## High-level view

The language switch is unchanged since pass 1. `App()` collects `AppLocaleManager.language`, provides `LocalAppLanguage`, `LocalAppLocale` and the layout direction, and rebuilds the theme and nav host inside `key(language)` while `rememberAppNavState` sits above the key. No reference to `exitProcess`, `setApplicationLocales`, `saveLanguageAndRestart`, `applyLocale` or `recreate()` remains in Kotlin sources.

`ProfileViewModel` now collects `ObserveCoursesUseCase` (skipping the replayed value that `Load` already covers), updating the subscribed-course count and reloading progress on each catalog change. The live subscription has no observable source, so a `ScreenShown` intent from `LaunchedEffect(Unit)` re-fetches it every time the Profile tab re-enters composition, which covers the "subscribed from the Teacher tab, came back to Profile" path. The cost is that every catalog emission shows the progress spinner again.

Phone numbers render left to right in Arabic. `JanaTextField` gained an optional `textDirection` (default `Unspecified`, so existing callers are untouched) that keeps the text on the layout start edge, and the header wraps the phone in LRI/PDI isolate marks. "Change photo" is hidden until avatar upload exists, its two string keys were removed from both locales, and the dead `CourseClicked` / `NavigateToCourseDetail` MVI path is gone.

On re-check, EN and AR each have 386 keys with the same key set. No new composable has a hardcoded `Text("...")`, `contentDescription = "..."` or `Color(0x...)` literal, and the Profile ViewModels contain no string literals. `profileModule` passes 12 `get()` calls to the 12-parameter `ProfileViewModel`. Logout goes to `replaceAll(AuthRoute.Login())`, only the active teacher is shown, no test files were added, and the first line of `profile_impl.md` is exactly `Tests intentionally skipped per user request`. `:feature:home:compileCommonMainKotlinMetadata` and `:core:designsystem:compileKotlinJvm` re-ran here with `BUILD SUCCESSFUL`.

<details>
<summary>Issues (3)</summary>

1. **Progress spinner on every catalog change** (non-blocking) — `loadProgress()` sets `isProgressLoading = true` on each `observeCourses()` emission, so the learning summary blinks to a spinner after a subscription or lesson change, and quick emissions start overlapping requests. Use a silent refresh that keeps the current `progress` visible and cancels the previous reload job.
2. **Notification permission not requested** (non-blocking) — "Allow notifications" only writes DataStore. Keep tracking the TODO in `ProfileViewModel` and add a permission expect/actual before notifications are delivered.
3. **iOS actuals not compile-verified** (non-blocking) — `AppLocale.ios.kt` and the NSUserDefaults `AppLanguagePlatform.kt` have only been SKIPPED on Windows. Compile `:core:designsystem` for iOS on macOS before release.

</details>

<details>
<summary>Details</summary>

### Background refresh reuses the initial-load path

```
CourseCatalogStore (StateFlow) ── observeCourses().drop(1) ──► subscribedCoursesCount + loadProgress()
TeacherSubscriptionRepositoryImpl (plain var) ◄── ScreenShown (2nd+ time) ── LaunchedEffect(Unit) in ProfileScreen
```

`loadProgress()` was written for the initial load and for retry, so it starts with `copy(isProgressLoading = true, progressError = false)`. Reused as a background refresh, it hides the current ring and percentage behind `JanaLoadingState` for the 400 ms mock delay (longer with a real backend) after any catalog change, including lesson-completion updates. Two quick emissions also start two overlapping requests with no cancellation, so whichever finishes last wins. With the mock both return the same data, so the overlap only matters once a real API can return different snapshots. (confirmed for the spinner, likely for the overlap; non-blocking)

</details>

<details>
<summary>File map</summary>

- `shared/.../App.kt`, `shared/.../di/KoinInit.kt`: locale and direction provided at the root, `key(language)`, hoisted nav state, `AppLocaleManager` singleton.
- `core/designsystem/.../AppLocale*.kt`, `AppLocaleManager.kt`, `AppLanguage.kt`, `AppLanguagePlatform.kt` (android/jvm/ios): `LocalAppLocale` actuals, language `StateFlow`, persist-only save.
- `core/designsystem/.../JanaSettingsComponents.kt`, `JanaSelectionSheet.kt`, `JanaConfirmDialog.kt`, `JanaProgressComponents.kt`, `JanaTextField.kt` (now with `textDirection`), `JanaBackTopBar.kt`: shared components.
- `core/designsystem/.../values{,-ar}/strings.xml`: 40 net new keys each (42 added, 2 photo keys removed).
- `core/navigation/.../AppNavHost.kt`, `EntryViewModelStoreDecorator.kt`, `Routes.kt`: `AppNavState`, hoisted store map, `HomeRoute.EditProfile` / `Syllabus` / `Progress`.
- `core/datastore/.../AppPreferences.kt`, `feature/home/build.gradle.kts`: notification settings in DataStore, datastore dependency.
- `feature/home/.../HomeScreen.kt`, `HomeEntryProvider.kt`, `di/HomeModule.kt`: Profile tab wiring, new entries, `profileModule` include.
- `feature/home/.../tabs/profile/**`: domain, data, DI and presentation for Profile, Edit Profile, Syllabus and Progress; pass-2 fixes in `ProfileViewModel`, `ProfileContract`, `ProfileScreen`, `ProfileHeaderCard`, `ProfileFormatting`, `EditProfile*`.

Full diff: `git diff 48ff667 HEAD`. Fix commits only: `git diff 43e0c7c HEAD`.

</details>
