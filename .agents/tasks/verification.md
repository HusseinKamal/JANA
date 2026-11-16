# JANA — Verification Record (Review Iteration)

## Commands run

```
# Primary compile check
.\gradlew.bat :desktopApp:compileKotlin :composeApp:compileKotlinJvm --no-daemon
# → BUILD SUCCESSFUL in 2m 59s — 55 tasks (41 executed, 14 up-to-date)

# Unit tests
.\gradlew.bat :core:datastore:jvmTest :feature:splash:jvmTest --no-daemon
# → BUILD SUCCESSFUL in 1m 3s — 38 tasks (6 executed, 32 up-to-date)
```

## Results

| Task | Outcome |
|------|---------|
| `:desktopApp:compileKotlin` | ✅ PASSED |
| `:composeApp:compileKotlinJvm` | ✅ PASSED |
| `:core:datastore:jvmTest` | ✅ PASSED (6 tests) |
| `:feature:splash:jvmTest` | ✅ PASSED (3 tests) |

## Review findings addressed

| # | Finding | Fix applied |
|---|---------|-------------|
| 1 | `initAppBaseUrl` never wired on Android | Added `initAppBaseUrl(BuildConfig.BASE_URL)` before `initKoin` in `JanaApp.onCreate()` |
| 2 | Room KSP absent — `JanaDatabase_Impl` not generated | Upgraded `ksp` from `2.1.0-1.0.29` to `2.3.11` (the spec's intended value; AGP 9 migration guide requires KSP ≥ 2.3.6) |
| 3 | Dead `AppKoin.initKoin` overload | Deleted `core/di/src/commonMain/.../AppKoin.kt` |
| 4 | iOS base URL hardcoded to production | Updated `AppConfig.ios.kt` to read `BASE_URL` from `NSBundle.mainBundle.infoDictionary` with fallback to `"https://api.jana.app"` — existing production behaviour unchanged |
| 5 | No tests | Added `jvmTest` source sets + `kotlinx-coroutines-test` to `core:datastore` and `feature:splash`; created `TokenRepositoryTest.kt` (6 tests) and `SplashViewModelTest.kt` (3 tests) |
| 6 | Orphaned template drawable | Deleted `shared/src/commonMain/composeResources/drawable/compose-multiplatform.xml` |

## Deviations from spec

None in this iteration. The only spec deviation resolved here was finding #2: the original implementation used `ksp = "2.1.0-1.0.29"` instead of the spec's `ksp=2.3.11`.

## Notes

- Android and iOS native compilation were not run (requires Android SDK and Xcode respectively). JVM and Desktop targets confirmed clean.
- One existing deprecation warning: `Locale(String)` constructor deprecated in Java — present in `AppLanguagePlatform.kt` (pre-existing, not introduced by this iteration).
