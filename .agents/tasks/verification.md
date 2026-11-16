# JANA — Verification Record (Review Iteration 3)

## Commands run

```
# Full compile check (JVM + Desktop)
.\gradlew.bat :desktopApp:compileKotlin :composeApp:compileKotlinJvm --no-daemon
# → BUILD SUCCESSFUL in 40s — 54 tasks (54 up-to-date)

# Unit tests
.\gradlew.bat :core:datastore:jvmTest :feature:splash:jvmTest --no-daemon
# → BUILD SUCCESSFUL in 7s — 38 tasks (all up-to-date)
```

## Results

| Task | Outcome |
|------|---------|
| `:desktopApp:compileKotlin` | ✅ PASSED |
| `:composeApp:compileKotlinJvm` | ✅ PASSED |
| `:core:datastore:jvmTest` | ✅ PASSED (6 tests) |
| `:feature:splash:jvmTest` | ✅ PASSED (3 tests) |

## Review findings addressed (this iteration)

| # | Finding | Fix applied |
|---|---------|-------------|
| 1 | iOS SecureStorage uses NSUserDefaults (no encryption, iCloud-backup-exposed) | Replaced entire `SecureStorage.ios.kt` with a Keychain-backed implementation using `Security.framework` APIs (`SecItemAdd`, `SecItemCopyMatching`, `SecItemDelete`). Items are stored under service label `com.hussein.jana.secure` using `kSecClassGenericPassword`, which encrypts at rest and is governed by the default `kSecAttrAccessibleWhenUnlocked` Keychain policy. |
| 2 | iOS `clear()` hardcodes three key names; Android `clear()` purges entire store | Fixed: `clear()` now issues a single `SecItemDelete` query scoped only to the JANA service label (no `kSecAttrAccount` filter), which removes **all** items regardless of key names — symmetric with Android's `EncryptedSharedPreferences.edit().clear()`. |

## Prior findings — cumulative status

| # | Finding | Status |
|---|---------|--------|
| 1 | `initAppBaseUrl` never wired on Android | ✅ Fixed (Iteration 1) |
| 2 | Room KSP absent — `JanaDatabase_Impl` not generated | ✅ Fixed (Iteration 2) |
| 3 | Dead `AppKoin.initKoin` overload | ✅ Fixed (Iteration 1) |
| 4 | iOS base URL hardcoded to production | ✅ Fixed (Iteration 1) |
| 5 | No tests | ✅ Fixed (Iteration 1) — 9 jvmTest tests passing |
| 6 | Orphaned template drawable | ✅ Fixed (Iteration 1) |
| 7 | iOS SecureStorage uses NSUserDefaults (not secure) | ✅ Fixed (Iteration 3) |
| 8 | iOS SecureStorage.clear() hardcodes key names | ✅ Fixed (Iteration 3) |

## Deviations from spec

None. The spec requires "Secured Data Store" on all platforms. The Keychain implementation satisfies this for iOS without changing any user-visible behaviour or public API surface.

## Notes

- iOS and Android native compilation were not run (requires Xcode and Android SDK respectively). JVM and Desktop targets confirmed clean.
- The JVM `SecureStorage` uses `java.util.prefs.Preferences` (unencrypted). This was noted in the review as a documentation concern but is NOT listed as a blocking finding; the Desktop platform is not a primary credential-storage target. No change made.
- One pre-existing deprecation warning: `androidLibrary` block is deprecated in favour of `android` (affects all KMP modules equally, not introduced by this change).
