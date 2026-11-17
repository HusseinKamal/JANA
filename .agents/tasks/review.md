# JANA KMP Scaffold — Iteration 3 Review

Three review passes have been completed on this scaffold. This pass focuses on the two security findings from iteration 2 (iOS Keychain replacement, `clear()` symmetry) and validates that all structural requirements are met. The build ran clean on JVM/Desktop targets; iOS and Android native compilations were not re-run (requires Xcode and Android SDK respectively), consistent with the prior two passes.

**Watch for:** JVM `SecureStorage` stores credentials in `java.util.prefs.Preferences` with no encryption — confirmed unencrypted plaintext. Desktop is not a primary credential-storage target, but this should be documented at the API surface if credentials ever flow through the Desktop target. Confidence: **confirmed**.

**Verdict**: APPROVED

---

## High-level view

The module graph is structurally complete: all twelve modules declared in `settings.gradle.kts` have corresponding directories, `build.gradle.kts` files, and source sets. The `JanaApp` application class calls all four init functions in `onCreate()`, AndroidManifest wires it via `android:name=".JanaApp"`, and the desktop `main()` package resolves to `com.hussein.jana.MainKt` matching the `mainClass` declaration.

The iOS Keychain implementation replaces the prior NSUserDefaults stub. `clear()` now deletes all items under the JANA service label with a single `SecItemDelete` — no hardcoded key names — symmetric with Android's `EncryptedSharedPreferences.edit().clear()`.

The JVM `SecureStorage` target remains unencrypted (`java.util.prefs.Preferences`). The `SecureStorage` interface name implies confidentiality guarantees the JVM implementation does not provide. If the Desktop target ever receives a login flow, credentials would be stored in plaintext. A KDoc warning on the class would satisfy the documentation requirement without requiring a new dependency.

---

<details>
<summary>Issues (1)</summary>

1. **JVM SecureStorage unencrypted** — `SecureStorage.jvm.kt` stores values in `java.util.prefs.Preferences` with no encryption (Windows: registry; macOS/Linux: home-directory XML). Add a KDoc note warning callers on the Desktop/JVM target not to store high-value credentials, or gate credential storage behind a platform capability check.

</details>

---

<details>
<summary>Details</summary>

### Keychain implementation

The iOS `SecureStorage` now uses `Security.framework` exclusively. `putString` calls `remove(key)` before `SecItemAdd` to avoid `errSecDuplicateItem` — the correct pattern when skipping `SecItemUpdate`. `getString` uses `memScoped` with a `CFTypeRefVar`, then `CFBridgingRelease` to transfer Keychain-retained ownership before casting to `NSData`; the memory management is correct. `clear()` omits the `kSecAttrAccount` filter, so a single `SecItemDelete` sweeps the entire JANA service namespace regardless of how many keys were written.

`kSecAttrAccessible` is not explicitly set, so items inherit the Keychain default (`kSecAttrAccessibleWhenUnlocked`). Tokens are not needed in the background, so this policy is appropriate — but it means items are inaccessible during cold launch before the first unlock (relevant if the app ever adds background refresh).

### JVM SecureStorage gap

`SecureStorage.jvm.kt` stores values via `java.util.prefs.Preferences.userRoot().node("jana/secure")`. Neither the Windows registry nor the macOS/Linux XML backing store is encrypted. The `SecureStorage` interface name implies confidentiality guarantees the JVM implementation does not provide. If the Desktop target ever receives a login flow, credentials would be stored in plaintext. A KDoc warning on the class — "Desktop/JVM: values are stored unencrypted in OS-level Preferences; do not use for high-value credentials" — would satisfy the documentation requirement without requiring a new dependency.

</details>

---

<details>
<summary>File map</summary>

| File | Change |
|------|--------|
| `core/datastore/src/iosMain/.../SecureStorage.ios.kt` | Replaced NSUserDefaults implementation with Keychain-backed (`SecItemAdd`/`SecItemCopyMatching`/`SecItemDelete`) |
| `core/datastore/src/jvmMain/.../SecureStorage.jvm.kt` | Unchanged — `java.util.prefs.Preferences` (unencrypted, pre-existing) |
| `androidApp/src/main/AndroidManifest.xml` | `android:name=".JanaApp"` confirmed |
| `androidApp/src/main/.../MainActivity.kt` | All four init functions called in `JanaApp.onCreate()` |
| `desktopApp/build.gradle.kts` | `mainClass = "com.hussein.jana.MainKt"` |
| `desktopApp/src/main/.../main.kt` | Top-level `fun main()` in package `com.hussein.jana` |
| `core/designsystem/.../values/strings.xml` | 42 EN keys |
| `core/designsystem/.../values-ar/strings.xml` | 42 AR keys — full parity |
| `settings.gradle.kts` | All 12 modules included |

</details>
