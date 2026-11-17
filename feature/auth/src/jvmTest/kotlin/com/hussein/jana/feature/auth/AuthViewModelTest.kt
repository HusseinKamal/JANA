package com.hussein.jana.feature.auth

import com.hussein.jana.core.datastore.SecureStorage
import com.hussein.jana.core.datastore.TokenRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * Unit tests for [AuthViewModel] MVI state/effect transitions.
 *
 * Uses [StandardTestDispatcher] so [androidx.lifecycle.viewModelScope] coroutines run under the
 * test scheduler; [advanceTimeBy] skips the 1-second stub delay in [AuthViewModel.handleSignIn].
 */
@OptIn(ExperimentalCoroutinesApi::class)
class AuthViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var secureStorage: SecureStorage
    private lateinit var tokenRepository: TokenRepository

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        secureStorage = SecureStorage()
        secureStorage.clear()
        tokenRepository = TokenRepository(secureStorage)
    }

    @AfterTest
    fun teardown() {
        Dispatchers.resetMain()
        secureStorage.clear()
    }

    // ── State helpers ──────────────────────────────────────────────────────────

    private fun buildViewModel() = AuthViewModel(
        tokenRepository = tokenRepository,
        strings = AuthStrings(
            errorMissingFields = "Please fill in all required fields",
            errorSsoComingSoon = "SSO coming soon",
        ),
    )

    // ── Tab selection ──────────────────────────────────────────────────────────

    @Test
    fun `TabSelected changes activeTab and clears error`() = runTest(testDispatcher) {
        val viewModel = buildViewModel()
        viewModel.onIntent(LoginIntent.IdentifierChanged(""))   // ensure error could exist
        viewModel.onIntent(LoginIntent.SignInClicked)           // sets error

        viewModel.onIntent(LoginIntent.TabSelected(LoginTab.TEACHER))
        assertEquals(LoginTab.TEACHER, viewModel.state.value.activeTab)
        assertNull(viewModel.state.value.error)
    }

    @Test
    fun `TabSelected back to STUDENT clears error too`() = runTest(testDispatcher) {
        val viewModel = buildViewModel()
        viewModel.onIntent(LoginIntent.TabSelected(LoginTab.TEACHER))
        viewModel.onIntent(LoginIntent.TabSelected(LoginTab.STUDENT))
        assertEquals(LoginTab.STUDENT, viewModel.state.value.activeTab)
        assertNull(viewModel.state.value.error)
    }

    // ── Field changes ──────────────────────────────────────────────────────────

    @Test
    fun `IdentifierChanged updates identifier in state`() = runTest(testDispatcher) {
        val viewModel = buildViewModel()
        viewModel.onIntent(LoginIntent.IdentifierChanged("user@example.com"))
        assertEquals("user@example.com", viewModel.state.value.identifier)
    }

    @Test
    fun `PasswordChanged updates password in state`() = runTest(testDispatcher) {
        val viewModel = buildViewModel()
        viewModel.onIntent(LoginIntent.PasswordChanged("secret123"))
        assertEquals("secret123", viewModel.state.value.password)
    }

    // ── Toggles ────────────────────────────────────────────────────────────────

    @Test
    fun `TogglePasswordVisibility flips isPasswordVisible`() = runTest(testDispatcher) {
        val viewModel = buildViewModel()
        assertEquals(false, viewModel.state.value.isPasswordVisible)

        viewModel.onIntent(LoginIntent.TogglePasswordVisibility)
        assertEquals(true, viewModel.state.value.isPasswordVisible)

        viewModel.onIntent(LoginIntent.TogglePasswordVisibility)
        assertEquals(false, viewModel.state.value.isPasswordVisible)
    }

    @Test
    fun `ToggleRememberDevice flips rememberDevice`() = runTest(testDispatcher) {
        val viewModel = buildViewModel()
        assertEquals(false, viewModel.state.value.rememberDevice)
        viewModel.onIntent(LoginIntent.ToggleRememberDevice)
        assertEquals(true, viewModel.state.value.rememberDevice)
    }

    @Test
    fun `ToggleKeepSession flips keepSession`() = runTest(testDispatcher) {
        val viewModel = buildViewModel()
        assertEquals(false, viewModel.state.value.keepSession)
        viewModel.onIntent(LoginIntent.ToggleKeepSession)
        assertEquals(true, viewModel.state.value.keepSession)
    }

    // ── Sign-in validation ─────────────────────────────────────────────────────

    @Test
    fun `SignInClicked with blank identifier sets error`() = runTest(testDispatcher) {
        val viewModel = buildViewModel()
        viewModel.onIntent(LoginIntent.PasswordChanged("pass123"))
        viewModel.onIntent(LoginIntent.SignInClicked)

        assertEquals("Please fill in all required fields", viewModel.state.value.error)
        assertEquals(false, viewModel.state.value.isLoading)
    }

    @Test
    fun `SignInClicked with blank password sets error`() = runTest(testDispatcher) {
        val viewModel = buildViewModel()
        viewModel.onIntent(LoginIntent.IdentifierChanged("user@test.com"))
        viewModel.onIntent(LoginIntent.SignInClicked)

        assertEquals("Please fill in all required fields", viewModel.state.value.error)
        assertEquals(false, viewModel.state.value.isLoading)
    }

    @Test
    fun `SignInClicked with blank fields uses localized error from AuthStrings`() =
        runTest(testDispatcher) {
            val viewModel = AuthViewModel(
                tokenRepository = tokenRepository,
                strings = AuthStrings(
                    errorMissingFields = "يرجى ملء جميع الحقول المطلوبة",
                    errorSsoComingSoon = "الدخول الموحد قريباً",
                ),
            )
            viewModel.onIntent(LoginIntent.SignInClicked)
            assertEquals("يرجى ملء جميع الحقول المطلوبة", viewModel.state.value.error)
        }

    @Test
    fun `SignInClicked with valid credentials emits NavigateToStudentHome for STUDENT tab`() =
        runTest(testDispatcher) {
            val viewModel = buildViewModel()

            val effects = mutableListOf<LoginEffect>()
            val collector = launch { viewModel.effect.collect { effects.add(it) } }

            viewModel.onIntent(LoginIntent.IdentifierChanged("student@test.com"))
            viewModel.onIntent(LoginIntent.PasswordChanged("pass123"))
            viewModel.onIntent(LoginIntent.SignInClicked)

            assertEquals(true, viewModel.state.value.isLoading)

            advanceTimeBy(1_500)

            assertEquals(false, viewModel.state.value.isLoading)
            assertEquals(LoginEffect.NavigateToStudentHome, effects.firstOrNull())
            collector.cancel()
        }

    @Test
    fun `SignInClicked on TEACHER tab emits NavigateToTeacherHome`() =
        runTest(testDispatcher) {
            val viewModel = buildViewModel()

            val effects = mutableListOf<LoginEffect>()
            val collector = launch { viewModel.effect.collect { effects.add(it) } }

            viewModel.onIntent(LoginIntent.TabSelected(LoginTab.TEACHER))
            viewModel.onIntent(LoginIntent.IdentifierChanged("teacher@jana.edu"))
            viewModel.onIntent(LoginIntent.PasswordChanged("secure"))
            viewModel.onIntent(LoginIntent.SignInClicked)

            advanceTimeBy(1_500)

            assertEquals(LoginEffect.NavigateToTeacherHome, effects.firstOrNull())
            collector.cancel()
        }

    // ── Effect dispatches ──────────────────────────────────────────────────────

    @Test
    fun `GoogleSignInClicked emits TriggerGoogleSignIn`() = runTest(testDispatcher) {
        val viewModel = buildViewModel()
        val effects = mutableListOf<LoginEffect>()
        val collector = launch { viewModel.effect.collect { effects.add(it) } }

        viewModel.onIntent(LoginIntent.GoogleSignInClicked)
        advanceTimeBy(100)

        assertEquals(LoginEffect.TriggerGoogleSignIn, effects.firstOrNull())
        collector.cancel()
    }

    @Test
    fun `FacebookSignInClicked emits TriggerFacebookSignIn`() = runTest(testDispatcher) {
        val viewModel = buildViewModel()
        val effects = mutableListOf<LoginEffect>()
        val collector = launch { viewModel.effect.collect { effects.add(it) } }

        viewModel.onIntent(LoginIntent.FacebookSignInClicked)
        advanceTimeBy(100)

        assertEquals(LoginEffect.TriggerFacebookSignIn, effects.firstOrNull())
        collector.cancel()
    }

    @Test
    fun `ForgotPasswordClicked emits NavigateToForgotPassword`() = runTest(testDispatcher) {
        val viewModel = buildViewModel()
        val effects = mutableListOf<LoginEffect>()
        val collector = launch { viewModel.effect.collect { effects.add(it) } }

        viewModel.onIntent(LoginIntent.ForgotPasswordClicked)
        advanceTimeBy(100)

        assertEquals(LoginEffect.NavigateToForgotPassword, effects.firstOrNull())
        collector.cancel()
    }

    @Test
    fun `RegisterClicked emits NavigateToRegister`() = runTest(testDispatcher) {
        val viewModel = buildViewModel()
        val effects = mutableListOf<LoginEffect>()
        val collector = launch { viewModel.effect.collect { effects.add(it) } }

        viewModel.onIntent(LoginIntent.RegisterClicked)
        advanceTimeBy(100)

        assertEquals(LoginEffect.NavigateToRegister, effects.firstOrNull())
        collector.cancel()
    }

    @Test
    fun `InstitutionalSSOClicked emits ShowSnackbar with localized message`() =
        runTest(testDispatcher) {
            val viewModel = buildViewModel()
            val effects = mutableListOf<LoginEffect>()
            val collector = launch { viewModel.effect.collect { effects.add(it) } }

            viewModel.onIntent(LoginIntent.InstitutionalSSOClicked)
            advanceTimeBy(100)

            val effect = effects.firstOrNull()
            assertTrue(effect is LoginEffect.ShowSnackbar)
            assertEquals("SSO coming soon", (effect as LoginEffect.ShowSnackbar).message)
            collector.cancel()
        }

    @Test
    fun `InstitutionalSSOClicked uses localized message from AuthStrings`() =
        runTest(testDispatcher) {
            val viewModel = AuthViewModel(
                tokenRepository = tokenRepository,
                strings = AuthStrings(errorSsoComingSoon = "الدخول الموحد قريباً"),
            )
            val effects = mutableListOf<LoginEffect>()
            val collector = launch { viewModel.effect.collect { effects.add(it) } }

            viewModel.onIntent(LoginIntent.InstitutionalSSOClicked)
            advanceTimeBy(100)

            val effect = effects.firstOrNull() as? LoginEffect.ShowSnackbar
            assertEquals("الدخول الموحد قريباً", effect?.message)
            collector.cancel()
        }

    @Test
    fun `CallAdmissionsClicked emits OpenDialer with correct number`() = runTest(testDispatcher) {
        val viewModel = buildViewModel()
        val effects = mutableListOf<LoginEffect>()
        val collector = launch { viewModel.effect.collect { effects.add(it) } }

        viewModel.onIntent(LoginIntent.CallAdmissionsClicked)
        advanceTimeBy(100)

        assertEquals(LoginEffect.OpenDialer("+20237498000"), effects.firstOrNull())
        collector.cancel()
    }

    @Test
    fun `WhatsAppFacultyClicked emits OpenWhatsApp with correct number`() =
        runTest(testDispatcher) {
            val viewModel = buildViewModel()
            val effects = mutableListOf<LoginEffect>()
            val collector = launch { viewModel.effect.collect { effects.add(it) } }

            viewModel.onIntent(LoginIntent.WhatsAppFacultyClicked)
            advanceTimeBy(100)

            assertEquals(LoginEffect.OpenWhatsApp("+20237498000"), effects.firstOrNull())
            collector.cancel()
        }

    // ── updateStrings ──────────────────────────────────────────────────────────

    @Test
    fun `updateStrings changes subsequent error messages`() = runTest(testDispatcher) {
        val viewModel = buildViewModel()

        viewModel.updateStrings(
            AuthStrings(errorMissingFields = "يرجى ملء جميع الحقول المطلوبة"),
        )
        viewModel.onIntent(LoginIntent.SignInClicked)

        assertEquals("يرجى ملء جميع الحقول المطلوبة", viewModel.state.value.error)
    }
}
