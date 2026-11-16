# JANA — Build Verification Report

## Commands Run

```
.\gradlew.bat :desktopApp:compileKotlin :composeApp:compileKotlinJvm
```
**Result:** BUILD SUCCESSFUL

Also verified:
```
.\gradlew.bat :androidApp:compileDebugSources
```
**Result:** BUILD SUCCESSFUL (all 69 tasks passed)

```
.\gradlew.bat :core:common:compileKotlinJvm :core:network:compileKotlinJvm \
              :core:navigation:compileKotlinJvm :core:datastore:compileKotlinJvm \
              :core:designsystem:compileKotlinJvm :core:database:compileKotlinJvm \
              :core:di:compileKotlinJvm :feature:splash:compileKotlinJvm \
              :feature:home:compileKotlinJvm
```
**Result:** BUILD SUCCESSFUL (all pass)

---

## Deviations from Spec

### 1. KSP version changed: `2.3.11` → `2.1.0-1.0.29`
- **Reason:** `com.google.devtools.ksp:2.3.11` is not a published artifact. KSP uses the format `{kotlin_version}-{ksp_patch}`. For Kotlin 2.1.0, the compatible stable version is `2.1.0-1.0.29`.

### 2. Room KSP / `@ConstructedBy` deferred — Room module has no KSP plugin
- **Reason:** KSP `2.1.0-1.0.29` is incompatible with `com.android.kotlin.multiplatform.library` AGP 9.1.0 (throws `ClassCastException: KotlinMultiplatformAndroidCompilationImpl cannot be cast to KotlinJvmAndroidCompilation`). `com.android.library` + `kotlinMultiplatform` is also blocked in AGP 9.0+ (error: "not compatible with the 'org.jetbrains.kotlin.multiplatform' plugin since AGP 9.0"). KSP code generation will work once a KSP version that supports AGP 9.1.0's multiplatform android compilation type is available (likely requires upgrading to KSP 2.2.x for Kotlin 2.2.x).
- **Impact:** `JanaDatabase` compiles with `@Database` / DAOs / entities intact. The Room runtime itself compiles fine. The KSP-generated DAO implementation is absent; `createDatabase()` will throw at runtime until KSP is re-enabled with a compatible version. Architecture is fully defined.
- **`@ConstructedBy`/`RoomDatabaseConstructor` removed** from `JanaDatabase` for the same reason.

### 3. `androidx-navigation3-runtime:1.1.1` removed
- **Reason:** This artifact does not exist in any public Maven repository. `NavKey` and runtime types are available transitively through `navigation3-ui:1.1.1`, matching the WearVerse reference pattern.

### 4. `security-crypto` added only to `core:datastore:androidMain`
- Follows spec; `EncryptedSharedPreferences` from `androidx.security:security-crypto:1.1.0-alpha06` is used for `SecureStorage.android.kt`.

### 5. `DataStoreFactory.android.kt` uses `PreferenceDataStoreFactory.createWithPath` not `preferencesDataStoreFile`
- **Reason:** `preferencesDataStoreFile()` is an extension on `Context` from `androidx.datastore:datastore-preferences` (Android-only). The module uses `datastore-preferences-core` (KMP artifact) which doesn't include this extension. `createWithPath` achieves the same result.

### 6. `core:di` gains explicit `ktor-client-core` and `datastore-preferences` api dependencies
- **Reason:** `CoreModule.kt` references `HttpClient` and `DataStore<Preferences>` types by inference. These types must be resolvable at compile time in `core:di`. Since `core:network` and `core:datastore` expose them only via `implementation`, they're added explicitly as `api` deps in `core:di`.

### 7. `ksp = "2.3.11"` from spec changed to `"2.1.0-1.0.29"` for Kotlin 2.1.0 compatibility
- Same as deviation #1 above.

### 8. `gradle.properties` gains `kotlin.native.ignoreDisabledTargets=true`
- Suppresses the iOS target disabled warnings on Windows (iOS cannot be built without macOS). This is a developer-experience improvement, not a behavioral change.

---

## Build Environment
- Gradle: 9.5.1
- JDK: Azul Zulu 21.0.11 (selected by Gradle toolchain)
- OS: Windows 11 / Win32
- iOS targets: disabled (requires macOS)
