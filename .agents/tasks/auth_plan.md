# Implementation Plan — `:feature:auth` (Login Screen)

## Codebase Findings & Verified Names

### MVI base classes (all in `com.hussein.jana.core.common.mvi`)
| Spec name | Actual name | File |
|---|---|---|
| `BaseMviViewModel` | `BaseMviViewModel<S, I, E>` | `core/common/src/commonMain/…/mvi/BaseMviViewModel.kt` |
| `MviState` | `MviState` interface | `core/common/src/commonMain/…/mvi/MviContract.kt` |
| `MviIntent` | `MviIntent` interface | same file |
| `MviEffect` | `MviEffect` interface | same file |

Protected API on `BaseMviViewModel`: `setState { S.() -> S }`, `sendEffect(E)`, `currentState(): S`, `viewModelScope`.

### Existing Routes.kt
```
@Serializable data object SplashRoute : NavKey
sealed interface AuthRoute : NavKey {
    @Serializable data object Login : AuthRoute    ← must change to data class
    @Serializable data object Register : AuthRoute
}
sealed interface HomeRoute : NavKey {
    @Serializable data class Root(val initialTab: String = "HOME") : HomeRoute
}
```
`ForgotPasswordRoute` does **not** exist yet — must be added.

### Mismatch: `AuthRoute.Login` is a `data object`, not a `data class`
The spec requires `data class Login(val initialTab: String = "STUDENT")`. Changing this is a **breaking change** that invalidates the `SplashEntryProvider.kt` reference `AuthRoute.Login` (bare object reference) → must be updated to `AuthRoute.Login()` (constructor call) in the same step.

### TokenRepository — confirmed in `com.hussein.jana.core.datastore`
Already registered as `single { TokenRepository(get()) }` in `CoreModule.kt`. Auth's Koin `viewModel { AuthViewModel(tokenRepository = get()) }` will resolve it at runtime without any extra binding.

### FeatureEntryProvider — confirmed interface
```kotlin
interface FeatureEntryProvider {
    fun entryFor(key: NavKey): NavEntry<out NavKey>?
}
```
Entry providers are registered with `named("XyzEntryProvider")` qualifier. They are collected into `List<FeatureEntryProvider>` via `getAll<FeatureEntryProvider>()` in `KoinInit.kt`.

### Navigator API — confirmed `AppNavigator`
`navigate(route)`, `back()`, `replaceAll(route)`, `popTo(route, inclusive)`.

### Verified color names (all in `core/designsystem/src/commonMain/…/Color.kt`)
`VellumCanvas`, `ScholarlyTeal` (#0F3E36), `AmberGold` (#D97706), `AmberDark` (#B45309),
`SurfaceContainerLow` (#F0F3FF), `OutlineVariant` (#C0C8C5), `OnSurfaceVariant` (#404846),
`MutedSand` (#F3F4F1), `SuccessGreen` (#16A34A), `SurfaceContainer`, `Outline`, `Error`.
All are top-level `val`s importable from `com.hussein.jana.core.designsystem`.

### Compose resource import pattern
Package is `com.hussein.jana.core.designsystem` (`publicResClass = true`).
Usage: `import com.hussein.jana.core.designsystem.Res`, then `stringResource(Res.string.auth_welcome_student)`.
Per-string accessor imports like `import com.hussein.jana.core.designsystem.auth_welcome_student` are also generated but are not needed when using `Res.string.*` directly.

### Existing strings that differ from spec keys — no conflict
The existing `password_label = "Password"` and `email_label = "Email"` are old generic keys.
All new auth keys use the `auth_` prefix (`auth_password_label`, etc.) and are entirely distinct.

### `JanaLogoCard` component
Available in `core/designsystem` as `JanaLogoCard(size: Dp, modifier: Modifier)` — use in auth top bars to show the JANA brand logo icon.

### Key NavEntry closure pattern
In entry providers, the `when` branch captures the outer `key` by closure:
```kotlin
is AuthRoute.Login -> NavEntry(key) {
    val navigator = koinInject<AppNavigator>()
    AuthFlow(initialTab = key.initialTab, ...)   // key is the AuthRoute.Login instance
}
```

---

## Implementation Plan

- [ ] 1. Update `core/navigation/Routes.kt` — change `AuthRoute.Login` and add `ForgotPasswordRoute`.
      Change `@Serializable data object Login : AuthRoute` to
      `@Serializable data class Login(val initialTab: String = "STUDENT") : AuthRoute`.
      Add at the bottom: `@Serializable data object ForgotPasswordRoute : NavKey`.
      Files: `core/navigation/src/commonMain/kotlin/com/hussein/jana/core/navigation/Routes.kt`
      Verify: `.\gradlew :core:navigation:compileKotlinJvm` — BUILD SUCCESSFUL.

- [ ] 2. Fix `feature/splash/src/commonMain/…/SplashEntryProvider.kt` — update bare `AuthRoute.Login` reference.
      Line `onNavigateToSignIn = { navigator.replaceAll(AuthRoute.Login) }` becomes
      `onNavigateToSignIn = { navigator.replaceAll(AuthRoute.Login()) }`.
      (The `onNavigateToAuth` line references `AuthRoute.Register` which is still a data object — leave unchanged.)
      Files: `feature/splash/src/commonMain/kotlin/com/hussein/jana/feature/splash/SplashEntryProvider.kt`
      Verify: `.\gradlew :feature:splash:compileKotlinJvm` — BUILD SUCCESSFUL.

- [ ] 3. Add English auth strings to `core/designsystem/src/commonMain/composeResources/values/strings.xml`.
      Append a new `<!-- Auth — new feature:auth strings -->` block AFTER the existing Auth section.
      Add every key listed in the STRINGS section of the task spec (all `auth_*` keys):
      `auth_student_login_label`, `auth_faculty_studio`, `auth_tab_student`, `auth_tab_teacher`,
      `auth_tab_faculty`, `auth_tab_student_portal`, `auth_forgot_password`, `auth_password_label`,
      `auth_password_hint`, `auth_password_protected`, `auth_or_continue`, `auth_google`,
      `auth_facebook`, `auth_apple`, `auth_welcome_student`, `auth_subtitle_student`,
      `auth_identifier_label`, `auth_identifier_hint`, `auth_country_code`, `auth_remember_device`,
      `auth_btn_student`, `auth_no_account`, `auth_register_link`, `auth_welcome_teacher`,
      `auth_welcome_teacher_arabic`, `auth_subtitle_teacher`, `auth_studio_online`, `auth_cairo_node`,
      `auth_faculty_email_label`, `auth_faculty_email_hint`, `auth_institutional_login`,
      `auth_keep_session`, `auth_btn_teacher`, `auth_sso_link`, `auth_onboarding_title`,
      `auth_onboarding_warning`, `auth_onboarding_desc`, `auth_call_admissions`, `auth_whatsapp_faculty`.
      Verbatim English values as specified in the task (note: `auth_no_account` uses `\'` for the apostrophe).
      Files: `core/designsystem/src/commonMain/composeResources/values/strings.xml`
      Verify: `.\gradlew :core:designsystem:generateResourceAccessorsForCommonMain` — no errors.

- [ ] 4. Add Arabic translations to `core/designsystem/src/commonMain/composeResources/values-ar/strings.xml`.
      Append the same set of `auth_*` keys with Arabic translations matching the semantic meaning.
      Key translations to include:
      `auth_student_login_label` = "تسجيل دخول الطالب",
      `auth_faculty_studio` = "استوديو أعضاء هيئة التدريس",
      `auth_tab_student` = "الطالب",
      `auth_tab_teacher` = "المعلم / أعضاء هيئة التدريس",
      `auth_tab_faculty` = "أعضاء هيئة التدريس",
      `auth_tab_student_portal` = "بوابة الطالب",
      `auth_forgot_password` = "نسيت كلمة المرور؟",
      `auth_password_label` = "كلمة المرور *",
      `auth_password_hint` = "أدخل عبارة المرور الخاصة بك",
      `auth_password_protected` = "✦ محمي",
      `auth_or_continue` = "أو تابع باستخدام",
      `auth_google` = "جوجل",
      `auth_facebook` = "فيسبوك",
      `auth_apple` = "معرف آبل",
      `auth_welcome_student` = "مرحباً بعودتك إلى جَنَى",
      `auth_subtitle_student` = "سجّل الدخول للوصول إلى فصولك المباشرة وملاحظات المرشد ومسار الدراسة بالذكاء الاصطناعي.",
      `auth_identifier_label` = "رقم الهاتف أو البريد الإلكتروني",
      `auth_identifier_hint` = "010 1234 5678 أو student@...",
      `auth_country_code` = "EG +20",
      `auth_remember_device` = "تذكر هذا الجهاز",
      `auth_btn_student` = "تسجيل الدخول إلى الفصل المباشر",
      `auth_no_account` = "لا تمتلك حساب طالب في جَنَى؟",
      `auth_register_link` = "سجّل للحصول على تسجيل أكاديمي",
      `auth_welcome_teacher` = "أهلاً بك أستاذنا الموقر",
      `auth_welcome_teacher_arabic` = "مرحباً بك أستاذنا الفاضل",
      `auth_subtitle_teacher` = "أدر فصولك التفاعلية ذات المقاعد العشرين وخلاصات بث الاستوديو وصرف InstaPay الفوري.",
      `auth_studio_online` = "شبكة الاستوديو متصلة",
      `auth_cairo_node` = "عقدة القاهرة",
      `auth_faculty_email_label` = "البريد الأكاديمي / معرّف عضو هيئة التدريس",
      `auth_faculty_email_hint` = "dr.ahmed.hassan@jana.faculty.edu",
      `auth_institutional_login` = "تسجيل دخول مؤسسي",
      `auth_keep_session` = "الإبقاء على جلسة الاستوديو نشطة",
      `auth_btn_teacher` = "تسجيل الدخول إلى جَنَى",
      `auth_sso_link` = "الدخول الموحد المؤسسي",
      `auth_onboarding_title` = "إعداد أعضاء هيئة التدريس الجدد",
      `auth_onboarding_warning` = "لا يمكن إنشاء حسابات أعضاء هيئة التدريس من التطبيق",
      `auth_onboarding_desc` = "يُمنح وصول استوديو المعلم بمراجعة مؤسسية واستشارة مباشرة فقط. يرجى التواصل مع فريق علاقات أعضاء هيئة التدريس لبدء التحقق من بيانات الاعتماد وإعداد فصل الاستوديو.",
      `auth_call_admissions` = "اتصل بقبول أعضاء هيئة التدريس (+20 2 3749 8000)",
      `auth_whatsapp_faculty` = "واتساب مكتب أعضاء هيئة التدريس".
      Files: `core/designsystem/src/commonMain/composeResources/values-ar/strings.xml`
      Verify: `.\gradlew :core:designsystem:generateResourceAccessorsForCommonMain` — no errors, all 39 auth keys present in both files.

- [ ] 5. Create `feature/auth/build.gradle.kts`.
      Copy the structure of `feature/splash/build.gradle.kts` exactly.
      Change: `namespace = "com.hussein.jana.feature.auth"`,
      `packageOfResClass = "com.hussein.jana.feature.auth.generated.resources"`.
      Keep all same dependencies (`:core:common`, `api(:core:designsystem)`, `:core:navigation`,
      `:core:datastore`, `:shared-models`, compose, navigation3, koin, serialization).
      Files: `feature/auth/build.gradle.kts`
      Verify: file exists with correct namespace.

- [ ] 6. Register the new module in `settings.gradle.kts`.
      Add `include(":feature:auth")` immediately after the line `include(":feature:splash")`.
      Files: `settings.gradle.kts`
      Verify: `.\gradlew :feature:auth:tasks` — module is recognised by Gradle.

- [ ] 7. Create `AuthContract.kt` — MVI State, Intent, and Effect types.
      File location: `feature/auth/src/commonMain/kotlin/com/hussein/jana/feature/auth/AuthContract.kt`
      Package: `com.hussein.jana.feature.auth`
      Contents exactly as specified:
      ```
      enum class LoginTab { STUDENT, TEACHER }
      data class LoginState(
          val activeTab: LoginTab = LoginTab.STUDENT,
          val identifier: String = "",
          val password: String = "",
          val isPasswordVisible: Boolean = false,
          val rememberDevice: Boolean = false,
          val keepSession: Boolean = false,
          val isLoading: Boolean = false,
          val error: String? = null,
      ) : MviState
      sealed interface LoginIntent : MviIntent { … }
      sealed interface LoginEffect : MviEffect { … }
      ```
      `LoginIntent` members (all as `data class` or `data object` as appropriate):
      `TabSelected(val tab: LoginTab)`, `IdentifierChanged(val value: String)`,
      `PasswordChanged(val value: String)`, `TogglePasswordVisibility`, `ToggleRememberDevice`,
      `ToggleKeepSession`, `SignInClicked`, `GoogleSignInClicked`, `FacebookSignInClicked`,
      `ForgotPasswordClicked`, `RegisterClicked`, `InstitutionalSSOClicked`,
      `CallAdmissionsClicked`, `WhatsAppFacultyClicked`.
      `LoginEffect` members: `NavigateToStudentHome`, `NavigateToTeacherHome`, `NavigateToRegister`,
      `NavigateToForgotPassword`, `TriggerGoogleSignIn`, `TriggerFacebookSignIn`,
      `OpenDialer(val number: String)`, `OpenWhatsApp(val number: String)`, `ShowSnackbar(val message: String)`.
      Files: `feature/auth/src/commonMain/kotlin/com/hussein/jana/feature/auth/AuthContract.kt`
      Verify: `.\gradlew :feature:auth:compileKotlinJvm` — compiles without errors.

- [ ] 8. Create `AuthViewModel.kt`.
      File location: `feature/auth/src/commonMain/kotlin/com/hussein/jana/feature/auth/AuthViewModel.kt`
      Package: `com.hussein.jana.feature.auth`
      Imports: `BaseMviViewModel`, `TokenRepository`, `viewModelScope`, `kotlinx.coroutines.delay`, `kotlinx.coroutines.launch`.
      Class signature: `class AuthViewModel(private val tokenRepository: TokenRepository) : BaseMviViewModel<LoginState, LoginIntent, LoginEffect>(LoginState())`
      `onIntent` switch:
      - `TabSelected` → `setState { copy(activeTab = intent.tab, error = null) }`
      - `IdentifierChanged` → `setState { copy(identifier = intent.value) }`
      - `PasswordChanged` → `setState { copy(password = intent.value) }`
      - `TogglePasswordVisibility` → `setState { copy(isPasswordVisible = !isPasswordVisible) }`
      - `ToggleRememberDevice` → `setState { copy(rememberDevice = !rememberDevice) }`
      - `ToggleKeepSession` → `setState { copy(keepSession = !keepSession) }`
      - `SignInClicked` → call private `handleSignIn()`
      - `GoogleSignInClicked` → `sendEffect(LoginEffect.TriggerGoogleSignIn)`
      - `FacebookSignInClicked` → `sendEffect(LoginEffect.TriggerFacebookSignIn)`
      - `ForgotPasswordClicked` → `sendEffect(LoginEffect.NavigateToForgotPassword)`
      - `RegisterClicked` → `sendEffect(LoginEffect.NavigateToRegister)`
      - `InstitutionalSSOClicked` → `sendEffect(LoginEffect.ShowSnackbar("SSO coming soon"))`
      - `CallAdmissionsClicked` → `sendEffect(LoginEffect.OpenDialer("+20237498000"))`
      - `WhatsAppFacultyClicked` → `sendEffect(LoginEffect.OpenWhatsApp("+20237498000"))`
      `handleSignIn()` logic:
      If `currentState().identifier.isBlank() || currentState().password.isBlank()`:
        `setState { copy(error = "Please fill in all required fields") }; return`
      Else:
        `setState { copy(isLoading = true, error = null) }`
        `viewModelScope.launch { delay(1000); setState { copy(isLoading = false) }; if (currentState().activeTab == LoginTab.STUDENT) sendEffect(LoginEffect.NavigateToStudentHome) else sendEffect(LoginEffect.NavigateToTeacherHome) }`
      Files: `feature/auth/src/commonMain/kotlin/com/hussein/jana/feature/auth/AuthViewModel.kt`
      Verify: `.\gradlew :feature:auth:compileKotlinJvm` — compiles without errors.

- [ ] 9. Create the nine component files in `feature/auth/src/commonMain/…/feature/auth/components/`.
      All are `@Composable` functions that are fully stateless (receive primitives + lambdas only).
      All import colors from `com.hussein.jana.core.designsystem.*`, strings via `Res.string.*`.
      
      **9a. `AuthTextField.kt`** — `AuthTextField(label: String, value: String, onValueChange: (String) -> Unit, placeholder: String, leadingContent: @Composable (() -> Unit)? = null, trailingContent: @Composable (() -> Unit)? = null, modifier: Modifier = Modifier)`.
      Renders: `Column { Text(label, bodySmall, OnSurfaceVariant); Surface(1dp OutlineVariant border, RoundedCornerShape(12dp)) { Row { leadingContent; BasicTextField/OutlinedTextField with placeholder; trailingContent } } }`.

      **9b. `PasswordField.kt`** — `PasswordField(label: String, value: String, onValueChange: (String) -> Unit, isVisible: Boolean, onToggleVisibility: () -> Unit, placeholder: String = "••••••••", protectedBadge: Boolean = false, trailingLabel: @Composable (() -> Unit)? = null, modifier: Modifier = Modifier)`.
      Renders: a Row for the label area (label Text on left, `trailingLabel` composable on right), then `AuthTextField` with lock icon leading, eye toggle trailing (and `✦ PROTECTED` teal badge as trailing when `protectedBadge = true`), `visualTransformation = if (isVisible) VisualTransformation.None else PasswordVisualTransformation()`.

      **9c. `CountryCodeChip.kt`** — `CountryCodeChip(text: String = "EG +20 ▾", modifier: Modifier = Modifier)`.
      Renders: `Surface(shape=CircleShape, color=MutedSand) { Text(text, labelMedium, ScholarlyTeal, padding 8dp×4dp) }`.
      Used as `leadingContent` in the identifier `AuthTextField`.

      **9d. `LoginTabSwitcher.kt`** — `LoginTabSwitcher(activeTab: LoginTab, onTabSelected: (LoginTab) -> Unit, modifier: Modifier = Modifier)`.
      Renders: outer `Surface(shape=RoundedCornerShape(50), border=BorderStroke(2dp, OutlineVariant))` containing a `Row`.
      Each tab: `Surface(modifier=Modifier.weight(1f), shape=RoundedCornerShape(50), color= if selected ScholarlyTeal else Color.Transparent)` containing `Row { Icon(…); Spacer(4dp); Text(tabLabel, labelLarge, if selected Color.White else OnSurfaceVariant) }`.
      Student tab icon: `Icons.Rounded.School`; Teacher tab icon: `Icons.Rounded.Person`.
      Labels: `stringResource(Res.string.auth_tab_student)` / `stringResource(Res.string.auth_tab_teacher)` for student tab, `stringResource(Res.string.auth_tab_faculty)` / `stringResource(Res.string.auth_tab_student_portal)` for teacher tab (teacher view shows Faculty first).

      **9e. `SocialLoginRow.kt`** — `SocialLoginRow(onGoogleClick: () -> Unit, onFacebookClick: () -> Unit, modifier: Modifier = Modifier)`.
      Renders: `Row(horizontalArrangement=Arrangement.spacedBy(12dp)) { OutlinedButton(modifier=Modifier.weight(1f), height=48dp, shape=RoundedCornerShape(12dp), border=BorderStroke(1dp, OutlineVariant), containerColor=Color.White) { Text("G", color=Color(0xFF4285F4), bold); Spacer; Text(stringResource(Res.string.auth_google)) }; second button same pattern for Facebook with "f" in blue }`.

      **9f. `AuthTopBar.kt`** — `AuthTopBar(isTeacherTab: Boolean, onNavigateBack: () -> Unit, modifier: Modifier = Modifier)`.
      Student variant (isTeacherTab=false): `Row { IconButton(back arrow Icons.AutoMirrored.Rounded.ArrowBack, onNavigateBack); JanaLogoCard(size=28dp); Spacer(8dp); Text("JANA", titleMedium, ScholarlyTeal, bold); Spacer(weight=1f); Text(stringResource(Res.string.auth_student_login_label), labelMedium, OnSurfaceVariant); Spacer(12dp); Icon(Icons.Rounded.Person, tint=OnSurfaceVariant) }`.
      Teacher variant (isTeacherTab=true): `Column { Row { IconButton(back); JanaLogoCard(28dp); Column { Text("JANA", titleMedium, ScholarlyTeal, bold); Text(stringResource(Res.string.auth_faculty_studio), labelSmall, AmberDark) }; Spacer(weight=1f); Icon(Icons.Rounded.HelpOutline, OnSurfaceVariant); Icon(Icons.Rounded.Person, OnSurfaceVariant) }; Row(status chips) { Box(8dp, circle, SuccessGreen); Text(stringResource(Res.string.auth_studio_online), labelSmall, SuccessGreen); Surface(SurfaceContainerLow, border=1dp Outline, RoundedCornerShape(50)) { Text(stringResource(Res.string.auth_cairo_node), labelSmall, ScholarlyTeal) } } }`.

      **9g. `StudentLoginContent.kt`** — `StudentLoginContent(state: LoginState, onIntent: (LoginIntent) -> Unit, modifier: Modifier = Modifier)`.
      Stateless. Renders in a `Column(verticalScroll)`:
      avatar card (72dp circle, `Icons.Rounded.Person`, amber online dot overlay),
      Text(stringResource(Res.string.auth_welcome_student), headlineMedium, SerifFamily/MaterialTheme.typography.headlineMedium, ScholarlyTeal),
      Text(stringResource(Res.string.auth_subtitle_student), bodySmall, OnSurfaceVariant),
      `LoginTabSwitcher(activeTab=state.activeTab, onTabSelected={ onIntent(LoginIntent.TabSelected(it)) })`,
      `AuthTextField` for identifier (label=stringResource(Res.string.auth_identifier_label), leadingContent=`CountryCodeChip()`, trailingContent=`Icon(Icons.Rounded.AlternateEmail)`),
      `PasswordField` (label=stringResource(Res.string.auth_password_label), protectedBadge=true),
      Row(checkbox "Remember this device", Spacer, TextButton "Forgot Password?" in AmberDark),
      primary Button (ScholarlyTeal, pill, 56dp, icon+stringResource(Res.string.auth_btn_student)),
      Divider row (stringResource(Res.string.auth_or_continue)),
      `SocialLoginRow`,
      footer Row (Text(stringResource(Res.string.auth_no_account)), AnnotatedString(stringResource(Res.string.auth_register_link)) with AmberDark+underline, clickable → `onIntent(LoginIntent.RegisterClicked)`).
      Error text if `state.error != null`.

      **9h. `TeacherLoginContent.kt`** — `TeacherLoginContent(state: LoginState, onIntent: (LoginIntent) -> Unit, modifier: Modifier = Modifier)`.
      Stateless. Renders in a `Column(verticalScroll)`:
      Text(stringResource(Res.string.auth_welcome_teacher), headlineMedium, ScholarlyTeal),
      Text(stringResource(Res.string.auth_welcome_teacher_arabic), headlineSmall, AmberGold),
      Text(stringResource(Res.string.auth_subtitle_teacher), bodySmall, OnSurfaceVariant),
      `LoginTabSwitcher(activeTab=state.activeTab, …)`,
      `AuthTextField` for email (label row with label text + teal "Institutional login" link on right, envelope icon leading),
      `PasswordField` (label on left, "Forgot Password?" amber link on right via trailingLabel slot),
      Checkbox "Keep studio session active",
      primary Button (ScholarlyTeal, pill, 56dp, stringResource(Res.string.auth_btn_teacher)),
      Text(stringResource(Res.string.auth_sso_link), teal, underlined, centered, clickable → `onIntent(LoginIntent.InstitutionalSSOClicked)`),
      `FacultyOnboardingCard(onCallAdmissions = { onIntent(LoginIntent.CallAdmissionsClicked) }, onWhatsApp = { onIntent(LoginIntent.WhatsAppFacultyClicked) })`.
      Error text if `state.error != null`.

      **9i. `FacultyOnboardingCard.kt`** — `FacultyOnboardingCard(onCallAdmissions: () -> Unit, onWhatsApp: () -> Unit, modifier: Modifier = Modifier)`.
      Stateless. Renders: `Surface(color=SurfaceContainerLow, shape=RoundedCornerShape(20dp)) { Column(padding 20dp) { Row { Text("🎓"); Spacer(8dp); Text(stringResource(Res.string.auth_onboarding_title), titleMedium, SerifFamily/MaterialTheme.typography.titleMedium, ScholarlyTeal) }; Text(stringResource(Res.string.auth_onboarding_warning), labelSmall, AmberDark, uppercase); Text(stringResource(Res.string.auth_onboarding_desc), bodySmall, OnSurfaceVariant); Button(onCallAdmissions, ScholarlyTeal, fullWidth) { Text("📞 "+stringResource(Res.string.auth_call_admissions)) }; Spacer(8dp); OutlinedButton(onWhatsApp, fullWidth, 1dp border OutlineVariant) { Text("💬 "+stringResource(Res.string.auth_whatsapp_faculty)) } } }`.

      Files: `feature/auth/src/commonMain/kotlin/com/hussein/jana/feature/auth/components/AuthTextField.kt`, `PasswordField.kt`, `CountryCodeChip.kt`, `LoginTabSwitcher.kt`, `SocialLoginRow.kt`, `AuthTopBar.kt`, `StudentLoginContent.kt`, `TeacherLoginContent.kt`, `FacultyOnboardingCard.kt`
      Verify: `.\gradlew :feature:auth:compileKotlinJvm` — all 9 component files compile cleanly.

- [ ] 10. Create `AuthScreen.kt` — stateful `AuthFlow` and stateless `AuthScreen`.
      File: `feature/auth/src/commonMain/kotlin/com/hussein/jana/feature/auth/AuthScreen.kt`
      Package: `com.hussein.jana.feature.auth`

      **`AuthFlow` composable** (stateful, owns koinViewModel):
      ```kotlin
      @Composable
      fun AuthFlow(
          initialTab: String = "STUDENT",
          onNavigateToHome: () -> Unit,
          onNavigateToRegister: () -> Unit,
          onNavigateBack: () -> Unit,
      ) {
          val viewModel: AuthViewModel = koinViewModel()

          // Apply initialTab hint once
          LaunchedEffect(initialTab) {
              if (initialTab == "TEACHER") {
                  viewModel.onIntent(LoginIntent.TabSelected(LoginTab.TEACHER))
              }
          }

          val state by viewModel.state.collectAsState()

          LaunchedEffect(Unit) {
              viewModel.effect.collect { effect ->
                  when (effect) {
                      LoginEffect.NavigateToStudentHome -> onNavigateToHome()
                      LoginEffect.NavigateToTeacherHome -> onNavigateToHome()
                      LoginEffect.NavigateToRegister    -> onNavigateToRegister()
                      LoginEffect.NavigateToForgotPassword -> { /* TODO: hook when ForgotPasswordRoute is wired */ }
                      LoginEffect.TriggerGoogleSignIn   -> { /* snackbar placeholder */ }
                      LoginEffect.TriggerFacebookSignIn -> { /* snackbar placeholder */ }
                      is LoginEffect.OpenDialer   -> { /* platform intent - no-op for now */ }
                      is LoginEffect.OpenWhatsApp -> { /* platform intent - no-op for now */ }
                      is LoginEffect.ShowSnackbar -> { /* snackbar via scaffold - no-op for now */ }
                  }
              }
          }

          AuthScreen(
              state = state,
              onIntent = viewModel::onIntent,
              onNavigateBack = onNavigateBack,
          )
      }
      ```

      **`AuthScreen` composable** (stateless):
      ```kotlin
      @Composable
      fun AuthScreen(
          state: LoginState,
          onIntent: (LoginIntent) -> Unit,
          onNavigateBack: () -> Unit,
          modifier: Modifier = Modifier,
      ) {
          Box(modifier = modifier.fillMaxSize().background(VellumCanvas)) {
              Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
                  AuthTopBar(
                      isTeacherTab = state.activeTab == LoginTab.TEACHER,
                      onNavigateBack = onNavigateBack,
                  )
                  when (state.activeTab) {
                      LoginTab.STUDENT -> StudentLoginContent(state = state, onIntent = onIntent)
                      LoginTab.TEACHER -> TeacherLoginContent(state = state, onIntent = onIntent)
                  }
              }
          }
      }
      ```
      Files: `feature/auth/src/commonMain/kotlin/com/hussein/jana/feature/auth/AuthScreen.kt`
      Verify: `.\gradlew :feature:auth:compileKotlinJvm` — no errors.

- [ ] 11. Create `AuthEntryProvider.kt`.
      File: `feature/auth/src/commonMain/kotlin/com/hussein/jana/feature/auth/AuthEntryProvider.kt`
      Package: `com.hussein.jana.feature.auth`
      ```kotlin
      class AuthEntryProvider : FeatureEntryProvider {
          override fun entryFor(key: NavKey): NavEntry<out NavKey>? = when (key) {
              is AuthRoute.Login -> NavEntry(key) {
                  val navigator = koinInject<AppNavigator>()
                  AuthFlow(
                      initialTab      = key.initialTab,
                      onNavigateToHome     = { navigator.replaceAll(HomeRoute.Root()) },
                      onNavigateToRegister = { navigator.navigate(AuthRoute.Register) },
                      onNavigateBack       = { navigator.back() },
                  )
              }
              is AuthRoute.Register -> NavEntry(key) {
                  val navigator = koinInject<AppNavigator>()
                  // Register screen placeholder — navigate straight to Home
                  LaunchedEffect(Unit) { navigator.replaceAll(HomeRoute.Root()) }
              }
              else -> null
          }
      }
      ```
      Imports needed: `NavEntry`, `NavKey`, `FeatureEntryProvider`, `AppNavigator`, `AuthRoute`,
      `HomeRoute`, `koinInject`, `AuthFlow`, `LaunchedEffect`.
      Files: `feature/auth/src/commonMain/kotlin/com/hussein/jana/feature/auth/AuthEntryProvider.kt`
      Verify: `.\gradlew :feature:auth:compileKotlinJvm` — no errors.

- [ ] 12. Create `di/AuthModule.kt`.
      File: `feature/auth/src/commonMain/kotlin/com/hussein/jana/feature/auth/di/AuthModule.kt`
      Package: `com.hussein.jana.feature.auth.di`
      ```kotlin
      val authModule = module {
          viewModel { AuthViewModel(tokenRepository = get()) }
          single<FeatureEntryProvider>(named("AuthEntryProvider")) { AuthEntryProvider() }
      }
      ```
      Imports: `com.hussein.jana.core.navigation.FeatureEntryProvider`, `AuthViewModel`,
      `AuthEntryProvider`, `org.koin.core.module.dsl.viewModel`, `org.koin.core.qualifier.named`, `org.koin.dsl.module`.
      Files: `feature/auth/src/commonMain/kotlin/com/hussein/jana/feature/auth/di/AuthModule.kt`
      Verify: `.\gradlew :feature:auth:compileKotlinJvm` — no errors.

- [ ] 13. Update `shared/build.gradle.kts` — add `:feature:auth` dependency.
      In `commonMain.dependencies` block, add after `implementation(project(":feature:home"))`:
      `implementation(project(":feature:auth"))`.
      Files: `shared/build.gradle.kts`
      Verify: `.\gradlew :composeApp:compileKotlinJvm` — module is on the classpath, no unresolved reference errors.

- [ ] 14. Update `shared/src/commonMain/kotlin/com/hussein/jana/di/KoinInit.kt` — register `authModule`.
      Add import: `import com.hussein.jana.feature.auth.di.authModule`.
      Add `authModule,` to the `modules(...)` call, after `homeModule`.
      Files: `shared/src/commonMain/kotlin/com/hussein/jana/di/KoinInit.kt`
      Verify: `.\gradlew :composeApp:compileKotlinJvm` — `authModule` symbol resolves, BUILD SUCCESSFUL.

- [ ] 15. Full JVM compilation check and fix.
      Run `.\gradlew :composeApp:compileKotlinJvm` from `d:\AndroidApps\JANA\JANA`.
      This compiles all modules transitively.
      Fix any import errors, unresolved references, or type mismatches that surface.
      Common things to watch for:
      - `LoginIntent.TogglePasswordVisibility` / `ToggleRememberDevice` / `ToggleKeepSession` are
        `data object` inside a `sealed interface` — declare them as `data object` not `data class`.
      - `LoginEffect.NavigateToStudentHome` etc. are `data object` members of a `sealed interface`.
      - `PasswordVisualTransformation` requires import from `androidx.compose.ui.text.input`.
      - `collectAsState()` requires `import androidx.compose.runtime.collectAsState`.
      - `@Suppress("unused")` on unused lambda parameters where the compiler warns.
      Re-run until `BUILD SUCCESSFUL`.
      Files: (any file flagged by the compiler)
      Verify: `.\gradlew :composeApp:compileKotlinJvm` — `BUILD SUCCESSFUL`.

- [ ] 16. Write documentation to `.agents/tasks/auth_impl.md`.
      Create the file summarising: what was built, the module structure, MVI contract summary,
      key design decisions (why state-hoisted in one ViewModel, why strings live in core:designsystem,
      how initialTab flows from NavKey through AuthFlow to the ViewModel intent),
      and known stubs (social sign-in, dialer/WhatsApp, forgot-password route handler).
      Files: `.agents/tasks/auth_impl.md`
      Verify: file exists and is non-empty.

---

## Files to Create (new)

```
feature/auth/
  build.gradle.kts
  src/commonMain/kotlin/com/hussein/jana/feature/auth/
    AuthContract.kt
    AuthViewModel.kt
    AuthScreen.kt
    AuthEntryProvider.kt
    components/
      AuthTextField.kt
      PasswordField.kt
      CountryCodeChip.kt
      LoginTabSwitcher.kt
      SocialLoginRow.kt
      AuthTopBar.kt
      StudentLoginContent.kt
      TeacherLoginContent.kt
      FacultyOnboardingCard.kt
    di/
      AuthModule.kt
.agents/tasks/auth_impl.md
```

## Files to Modify (existing)

| File | Change |
|---|---|
| `core/navigation/src/commonMain/…/Routes.kt` | `AuthRoute.Login`: data object → data class with `initialTab`; add `ForgotPasswordRoute` |
| `feature/splash/src/commonMain/…/SplashEntryProvider.kt` | `AuthRoute.Login` → `AuthRoute.Login()` |
| `core/designsystem/src/commonMain/composeResources/values/strings.xml` | Append 39 `auth_*` English strings |
| `core/designsystem/src/commonMain/composeResources/values-ar/strings.xml` | Append 39 `auth_*` Arabic strings |
| `settings.gradle.kts` | `include(":feature:auth")` |
| `shared/build.gradle.kts` | `implementation(project(":feature:auth"))` |
| `shared/src/commonMain/…/di/KoinInit.kt` | `import authModule` + add to `modules(…)` |

---

## Flags and Assumptions

1. **`AuthRoute.Login` data object → data class**: Breaking change. Steps 1 and 2 must be applied together before any other module that imports `AuthRoute.Login` is compiled. Only `SplashEntryProvider.kt` is affected in the current codebase.

2. **ForgotPasswordRoute navigation**: `AuthEntryProvider` wires `NavigateToForgotPassword` effect as a no-op comment. A full forgot-password screen is out of scope for this task. The route and effect are fully defined so a future feature step can wire them without touching auth.

3. **Social sign-in**: Effects `TriggerGoogleSignIn` and `TriggerFacebookSignIn` are defined and dispatched correctly from the ViewModel. `AuthFlow` handles them as stubs (comment/snackbar) since platform SDKs are not yet integrated. This is consistent with spec note 5.

4. **`LaunchedEffect` in `AuthEntryProvider` Register branch**: The Register placeholder calls `navigator.replaceAll(HomeRoute.Root())` inside a `LaunchedEffect(Unit)`. This is consistent with how other screens trigger one-shot navigation at entry — it avoids calling navigation from a non-composable context.

5. **`auth_no_account` apostrophe**: The XML value must escape the apostrophe as `\'` → `Don\'t have a JANA student account?` as specified.

6. **Existing `password_label` / `email_label` keys in strings.xml**: These old generic keys must not be removed — they may be used by other parts of the app. The new `auth_password_label` and `auth_faculty_email_label` keys are additions, not replacements.

7. **`compose.resources` block in `feature/auth/build.gradle.kts`**: Included with `packageOfResClass = "com.hussein.jana.feature.auth.generated.resources"` even though auth has no own string resources. This matches the splash/home convention and is harmless.
