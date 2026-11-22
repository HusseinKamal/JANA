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
import kotlin.test.assertTrue

/**
 * Unit tests for [AuthViewModel] — student login only.
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

    private fun buildViewModel() = AuthViewModel(
        tokenRepository = tokenRepository,
        strings = AuthStrings(
            errorMissingFields       = "Please fill in all required fields",
            errorMissingPlatformCode = "Please enter the teacher platform code",
            googleComingSoon         = "Google Sign-In coming soon",
            facebookComingSoon       = "Facebook Sign-In coming soon",
        ),
    )

    // ── Field changes ──────────────────────────────────────────────────────────

    @Test
    fun `IdentifierChanged updates identifier in state`() = runTest(testDispatcher) {
        val vm = buildViewModel()
        vm.onIntent(LoginIntent.IdentifierChanged("user@example.com"))
        assertEquals("user@example.com", vm.state.value.identifier)
    }

    @Test
    fun `PasswordChanged updates password in state`() = runTest(testDispatcher) {
        val vm = buildViewModel()
        vm.onIntent(LoginIntent.PasswordChanged("secret123"))
        assertEquals("secret123", vm.state.value.password)
    }

    @Test
    fun `PlatformCodeChanged updates platformCode in state`() = runTest(testDispatcher) {
        val vm = buildViewModel()
        vm.onIntent(LoginIntent.PlatformCodeChanged("test_platform"))
        assertEquals("test_platform", vm.state.value.platformCode)
    }

    // ── Toggles ────────────────────────────────────────────────────────────────

    @Test
    fun `TogglePasswordVisibility flips isPasswordVisible`() = runTest(testDispatcher) {
        val vm = buildViewModel()
        assertEquals(false, vm.state.value.isPasswordVisible)
        vm.onIntent(LoginIntent.TogglePasswordVisibility)
        assertEquals(true, vm.state.value.isPasswordVisible)
        vm.onIntent(LoginIntent.TogglePasswordVisibility)
        assertEquals(false, vm.state.value.isPasswordVisible)
    }

    @Test
    fun `ToggleRememberDevice flips rememberDevice`() = runTest(testDispatcher) {
        val vm = buildViewModel()
        assertEquals(false, vm.state.value.rememberDevice)
        vm.onIntent(LoginIntent.ToggleRememberDevice)
        assertEquals(true, vm.state.value.rememberDevice)
    }

    // ── Sign-in validation ─────────────────────────────────────────────────────

    @Test
    fun `SignInClicked with blank platformCode sets platform error`() = runTest(testDispatcher) {
        val vm = buildViewModel()
        vm.onIntent(LoginIntent.IdentifierChanged("user@test.com"))
        vm.onIntent(LoginIntent.PasswordChanged("pass123"))
        // platformCode is blank
        vm.onIntent(LoginIntent.SignInClicked)
        assertEquals("Please enter the teacher platform code", vm.state.value.error)
        assertEquals(false, vm.state.value.isLoading)
    }

    @Test
    fun `SignInClicked with blank identifier sets localized error`() = runTest(testDispatcher) {
        val vm = buildViewModel()
        vm.onIntent(LoginIntent.PlatformCodeChanged("test_platform"))
        vm.onIntent(LoginIntent.PasswordChanged("pass123"))
        vm.onIntent(LoginIntent.SignInClicked)
        assertEquals("Please fill in all required fields", vm.state.value.error)
        assertEquals(false, vm.state.value.isLoading)
    }

    @Test
    fun `SignInClicked with blank password sets localized error`() = runTest(testDispatcher) {
        val vm = buildViewModel()
        vm.onIntent(LoginIntent.PlatformCodeChanged("test_platform"))
        vm.onIntent(LoginIntent.IdentifierChanged("user@test.com"))
        vm.onIntent(LoginIntent.SignInClicked)
        assertEquals("Please fill in all required fields", vm.state.value.error)
        assertEquals(false, vm.state.value.isLoading)
    }

    @Test
    fun `SignInClicked with blank fields uses localized message from AuthStrings`() =
        runTest(testDispatcher) {
            val vm = AuthViewModel(
                tokenRepository = tokenRepository,
                strings = AuthStrings(errorMissingFields = "يرجى ملء جميع الحقول المطلوبة"),
            )
            vm.onIntent(LoginIntent.SignInClicked)
            assertEquals("Please enter the teacher platform code", vm.state.value.error)
        }

    @Test
    fun `SignInClicked with valid credentials emits NavigateToHome`() = runTest(testDispatcher) {
        val vm = buildViewModel()
        val effects = mutableListOf<LoginEffect>()
        val collector = launch { vm.effect.collect { effects.add(it) } }

        vm.onIntent(LoginIntent.PlatformCodeChanged("test_platform"))
        vm.onIntent(LoginIntent.IdentifierChanged("student@test.com"))
        vm.onIntent(LoginIntent.PasswordChanged("pass123"))
        vm.onIntent(LoginIntent.SignInClicked)

        assertEquals(true, vm.state.value.isLoading)
        advanceTimeBy(1_500)
        assertEquals(false, vm.state.value.isLoading)
        assertEquals(LoginEffect.NavigateToHome, effects.firstOrNull())
        collector.cancel()
    }

    @Test
    fun `SignInClicked clears previous error`() = runTest(testDispatcher) {
        val vm = buildViewModel()
        vm.onIntent(LoginIntent.SignInClicked)                   // sets platform code error
        assertEquals("Please enter the teacher platform code", vm.state.value.error)
        vm.onIntent(LoginIntent.PlatformCodeChanged("test_platform"))
        vm.onIntent(LoginIntent.IdentifierChanged("u@t.com"))
        vm.onIntent(LoginIntent.PasswordChanged("p"))
        vm.onIntent(LoginIntent.SignInClicked)
        assertEquals(null, vm.state.value.error)
    }

    // ── Social / nav effects ───────────────────────────────────────────────────

    @Test
    fun `GoogleSignInClicked emits ShowSnackbar with coming-soon message`() =
        runTest(testDispatcher) {
            val vm = buildViewModel()
            val effects = mutableListOf<LoginEffect>()
            val collector = launch { vm.effect.collect { effects.add(it) } }

            vm.onIntent(LoginIntent.GoogleSignInClicked)
            advanceTimeBy(100)

            val effect = effects.firstOrNull()
            assertTrue(effect is LoginEffect.ShowSnackbar)
            assertEquals("Google Sign-In coming soon", (effect as LoginEffect.ShowSnackbar).message)
            collector.cancel()
        }

    @Test
    fun `FacebookSignInClicked emits ShowSnackbar with coming-soon message`() =
        runTest(testDispatcher) {
            val vm = buildViewModel()
            val effects = mutableListOf<LoginEffect>()
            val collector = launch { vm.effect.collect { effects.add(it) } }

            vm.onIntent(LoginIntent.FacebookSignInClicked)
            advanceTimeBy(100)

            val effect = effects.firstOrNull()
            assertTrue(effect is LoginEffect.ShowSnackbar)
            assertEquals("Facebook Sign-In coming soon", (effect as LoginEffect.ShowSnackbar).message)
            collector.cancel()
        }

    @Test
    fun `ForgotPasswordClicked emits NavigateToForgotPassword`() = runTest(testDispatcher) {
        val vm = buildViewModel()
        val effects = mutableListOf<LoginEffect>()
        val collector = launch { vm.effect.collect { effects.add(it) } }

        vm.onIntent(LoginIntent.ForgotPasswordClicked)
        advanceTimeBy(100)

        assertEquals(LoginEffect.NavigateToForgotPassword, effects.firstOrNull())
        collector.cancel()
    }

    @Test
    fun `RegisterClicked emits NavigateToRegister`() = runTest(testDispatcher) {
        val vm = buildViewModel()
        val effects = mutableListOf<LoginEffect>()
        val collector = launch { vm.effect.collect { effects.add(it) } }

        vm.onIntent(LoginIntent.RegisterClicked)
        advanceTimeBy(100)

        assertEquals(LoginEffect.NavigateToRegister, effects.firstOrNull())
        collector.cancel()
    }

    // ── updateStrings ──────────────────────────────────────────────────────────

    @Test
    fun `updateStrings changes subsequent error messages`() = runTest(testDispatcher) {
        val vm = buildViewModel()
        vm.updateStrings(AuthStrings(
            errorMissingPlatformCode = "يرجى إدخال كود منصة المعلم",
            errorMissingFields = "يرجى ملء جميع الحقول المطلوبة",
        ))
        vm.onIntent(LoginIntent.SignInClicked)
        assertEquals("يرجى إدخال كود منصة المعلم", vm.state.value.error)
    }
}
