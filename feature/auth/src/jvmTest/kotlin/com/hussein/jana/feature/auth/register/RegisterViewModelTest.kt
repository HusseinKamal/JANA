package com.hussein.jana.feature.auth.register

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
 * Unit tests for [RegisterViewModel] — validation, password strength, toggles, effects.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class RegisterViewModelTest {

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

    private fun buildViewModel() = RegisterViewModel(
        tokenRepository = tokenRepository,
        strings = RegisterStrings(
            errorNameRequired      = "Full name is required",
            errorNameTooShort      = "Name must be at least 2 characters",
            errorPhoneRequired     = "Phone number is required",
            errorPhoneInvalid      = "Please enter a valid phone number",
            errorPasswordTooShort  = "Password must be at least 10 characters",
            errorPasswordNoUppercase = "Password needs uppercase letters",
            errorPasswordsMismatch = "Passwords do not match",
            errorTermsRequired     = "Please accept the terms and conditions",
        ),
    )

    // ── Field change intents ───────────────────────────────────────────────────

    @Test
    fun `FullNameChanged updates fullName and clears error`() = runTest(testDispatcher) {
        val vm = buildViewModel()
        vm.onIntent(RegisterIntent.FullNameChanged("Hussein"))
        assertEquals("Hussein", vm.state.value.fullName)
        assertNull(vm.state.value.fullNameError)
    }

    @Test
    fun `PhoneChanged updates phone and clears error`() = runTest(testDispatcher) {
        val vm = buildViewModel()
        vm.onIntent(RegisterIntent.PhoneChanged("01012345678"))
        assertEquals("01012345678", vm.state.value.phone)
        assertNull(vm.state.value.phoneError)
    }

    @Test
    fun `ConfirmPasswordChanged updates confirmPassword and clears error`() = runTest(testDispatcher) {
        val vm = buildViewModel()
        vm.onIntent(RegisterIntent.ConfirmPasswordChanged("SomePass1!"))
        assertEquals("SomePass1!", vm.state.value.confirmPassword)
        assertNull(vm.state.value.confirmPasswordError)
    }

    // ── Toggles ────────────────────────────────────────────────────────────────

    @Test
    fun `TogglePasswordVisibility flips isPasswordVisible`() = runTest(testDispatcher) {
        val vm = buildViewModel()
        assertEquals(false, vm.state.value.isPasswordVisible)
        vm.onIntent(RegisterIntent.TogglePasswordVisibility)
        assertEquals(true, vm.state.value.isPasswordVisible)
        vm.onIntent(RegisterIntent.TogglePasswordVisibility)
        assertEquals(false, vm.state.value.isPasswordVisible)
    }

    @Test
    fun `ToggleConfirmPasswordVisibility flips isConfirmPasswordVisible`() = runTest(testDispatcher) {
        val vm = buildViewModel()
        assertEquals(false, vm.state.value.isConfirmPasswordVisible)
        vm.onIntent(RegisterIntent.ToggleConfirmPasswordVisibility)
        assertEquals(true, vm.state.value.isConfirmPasswordVisible)
    }

    @Test
    fun `ToggleTerms flips acceptedTerms and clears termsError`() = runTest(testDispatcher) {
        val vm = buildViewModel()
        assertEquals(false, vm.state.value.acceptedTerms)
        vm.onIntent(RegisterIntent.ToggleTerms)
        assertEquals(true, vm.state.value.acceptedTerms)
        assertNull(vm.state.value.termsError)
        vm.onIntent(RegisterIntent.ToggleTerms)
        assertEquals(false, vm.state.value.acceptedTerms)
    }

    // ── Password strength transitions ──────────────────────────────────────────

    @Test
    fun `empty password gives NONE strength`() = runTest(testDispatcher) {
        val vm = buildViewModel()
        vm.onIntent(RegisterIntent.PasswordChanged(""))
        assertEquals(PasswordStrength.NONE, vm.state.value.passwordStrength)
    }

    @Test
    fun `password with only 1 criterion gives WEAK`() = runTest(testDispatcher) {
        val vm = buildViewModel()
        // Only hasSymbol (digit) is true: single digit
        vm.onIntent(RegisterIntent.PasswordChanged("1"))
        assertEquals(PasswordStrength.WEAK, vm.state.value.passwordStrength)
    }

    @Test
    fun `password with 2 criteria gives MEDIUM`() = runTest(testDispatcher) {
        val vm = buildViewModel()
        // hasMinLength (10 chars) + hasSymbol (digit) — no uppercase
        vm.onIntent(RegisterIntent.PasswordChanged("abcdefghi1"))
        assertEquals(PasswordStrength.MEDIUM, vm.state.value.passwordStrength)
    }

    @Test
    fun `password with all 3 criteria gives STRONG`() = runTest(testDispatcher) {
        val vm = buildViewModel()
        // 10+ chars, uppercase, digit/symbol
        vm.onIntent(RegisterIntent.PasswordChanged("AbcDefGh1!"))
        assertEquals(PasswordStrength.STRONG, vm.state.value.passwordStrength)
    }

    @Test
    fun `PasswordChanged sets individual flags correctly`() = runTest(testDispatcher) {
        val vm = buildViewModel()
        vm.onIntent(RegisterIntent.PasswordChanged("AbcDefGh1!"))
        val s = vm.state.value
        assertTrue(s.passwordHasMinLength)
        assertTrue(s.passwordHasUpperCase)
        assertTrue(s.passwordHasSymbol)
    }

    // ── RegisterClicked validation ─────────────────────────────────────────────

    @Test
    fun `RegisterClicked with blank name sets errorNameRequired`() = runTest(testDispatcher) {
        val vm = buildViewModel()
        // Fill all other fields correctly
        vm.onIntent(RegisterIntent.PhoneChanged("01012345678"))
        vm.onIntent(RegisterIntent.PasswordChanged("AbcDefGh1!"))
        vm.onIntent(RegisterIntent.ConfirmPasswordChanged("AbcDefGh1!"))
        vm.onIntent(RegisterIntent.ToggleTerms)
        vm.onIntent(RegisterIntent.RegisterClicked)
        assertEquals("Full name is required", vm.state.value.fullNameError)
    }

    @Test
    fun `RegisterClicked with single-char name sets errorNameTooShort`() = runTest(testDispatcher) {
        val vm = buildViewModel()
        vm.onIntent(RegisterIntent.FullNameChanged("A"))
        vm.onIntent(RegisterIntent.PhoneChanged("01012345678"))
        vm.onIntent(RegisterIntent.PasswordChanged("AbcDefGh1!"))
        vm.onIntent(RegisterIntent.ConfirmPasswordChanged("AbcDefGh1!"))
        vm.onIntent(RegisterIntent.ToggleTerms)
        vm.onIntent(RegisterIntent.RegisterClicked)
        assertEquals("Name must be at least 2 characters", vm.state.value.fullNameError)
    }

    @Test
    fun `RegisterClicked with blank phone sets errorPhoneRequired`() = runTest(testDispatcher) {
        val vm = buildViewModel()
        vm.onIntent(RegisterIntent.FullNameChanged("Hussein"))
        vm.onIntent(RegisterIntent.PasswordChanged("AbcDefGh1!"))
        vm.onIntent(RegisterIntent.ConfirmPasswordChanged("AbcDefGh1!"))
        vm.onIntent(RegisterIntent.ToggleTerms)
        vm.onIntent(RegisterIntent.RegisterClicked)
        assertEquals("Phone number is required", vm.state.value.phoneError)
    }

    @Test
    fun `RegisterClicked with short phone sets errorPhoneInvalid`() = runTest(testDispatcher) {
        val vm = buildViewModel()
        vm.onIntent(RegisterIntent.FullNameChanged("Hussein"))
        vm.onIntent(RegisterIntent.PhoneChanged("123"))
        vm.onIntent(RegisterIntent.PasswordChanged("AbcDefGh1!"))
        vm.onIntent(RegisterIntent.ConfirmPasswordChanged("AbcDefGh1!"))
        vm.onIntent(RegisterIntent.ToggleTerms)
        vm.onIntent(RegisterIntent.RegisterClicked)
        assertEquals("Please enter a valid phone number", vm.state.value.phoneError)
    }

    @Test
    fun `RegisterClicked with short password sets errorPasswordTooShort`() = runTest(testDispatcher) {
        val vm = buildViewModel()
        vm.onIntent(RegisterIntent.FullNameChanged("Hussein"))
        vm.onIntent(RegisterIntent.PhoneChanged("01012345678"))
        vm.onIntent(RegisterIntent.PasswordChanged("Ab1!"))
        vm.onIntent(RegisterIntent.ConfirmPasswordChanged("Ab1!"))
        vm.onIntent(RegisterIntent.ToggleTerms)
        vm.onIntent(RegisterIntent.RegisterClicked)
        assertEquals("Password must be at least 10 characters", vm.state.value.passwordError)
    }

    @Test
    fun `RegisterClicked with no uppercase password sets errorPasswordNoUppercase`() = runTest(testDispatcher) {
        val vm = buildViewModel()
        vm.onIntent(RegisterIntent.FullNameChanged("Hussein"))
        vm.onIntent(RegisterIntent.PhoneChanged("01012345678"))
        vm.onIntent(RegisterIntent.PasswordChanged("abcdefgh1!"))
        vm.onIntent(RegisterIntent.ConfirmPasswordChanged("abcdefgh1!"))
        vm.onIntent(RegisterIntent.ToggleTerms)
        vm.onIntent(RegisterIntent.RegisterClicked)
        assertEquals("Password needs uppercase letters", vm.state.value.passwordError)
    }

    @Test
    fun `RegisterClicked with mismatched passwords sets errorPasswordsMismatch`() = runTest(testDispatcher) {
        val vm = buildViewModel()
        vm.onIntent(RegisterIntent.FullNameChanged("Hussein"))
        vm.onIntent(RegisterIntent.PhoneChanged("01012345678"))
        vm.onIntent(RegisterIntent.PasswordChanged("AbcDefGh1!"))
        vm.onIntent(RegisterIntent.ConfirmPasswordChanged("Different1!"))
        vm.onIntent(RegisterIntent.ToggleTerms)
        vm.onIntent(RegisterIntent.RegisterClicked)
        assertEquals("Passwords do not match", vm.state.value.confirmPasswordError)
    }

    @Test
    fun `RegisterClicked without accepting terms sets errorTermsRequired`() = runTest(testDispatcher) {
        val vm = buildViewModel()
        vm.onIntent(RegisterIntent.FullNameChanged("Hussein"))
        vm.onIntent(RegisterIntent.PhoneChanged("01012345678"))
        vm.onIntent(RegisterIntent.PasswordChanged("AbcDefGh1!"))
        vm.onIntent(RegisterIntent.ConfirmPasswordChanged("AbcDefGh1!"))
        // No ToggleTerms
        vm.onIntent(RegisterIntent.RegisterClicked)
        assertEquals("Please accept the terms and conditions", vm.state.value.termsError)
    }

    @Test
    fun `RegisterClicked with all valid inputs emits NavigateToHome`() = runTest(testDispatcher) {
        val vm = buildViewModel()
        val effects = mutableListOf<RegisterEffect>()
        val collector = launch { vm.effect.collect { effects.add(it) } }

        vm.onIntent(RegisterIntent.FullNameChanged("Hussein Al-Sayed"))
        vm.onIntent(RegisterIntent.PhoneChanged("01012345678"))
        vm.onIntent(RegisterIntent.PasswordChanged("AbcDefGh1!"))
        vm.onIntent(RegisterIntent.ConfirmPasswordChanged("AbcDefGh1!"))
        vm.onIntent(RegisterIntent.ToggleTerms)
        vm.onIntent(RegisterIntent.RegisterClicked)

        assertEquals(true, vm.state.value.isLoading)
        advanceTimeBy(1_500)
        assertEquals(false, vm.state.value.isLoading)
        assertEquals(RegisterEffect.NavigateToHome, effects.firstOrNull())
        collector.cancel()
    }

    // ── SignInClicked effect ───────────────────────────────────────────────────

    @Test
    fun `SignInClicked emits NavigateToSignIn`() = runTest(testDispatcher) {
        val vm = buildViewModel()
        val effects = mutableListOf<RegisterEffect>()
        val collector = launch { vm.effect.collect { effects.add(it) } }

        vm.onIntent(RegisterIntent.SignInClicked)
        advanceTimeBy(100)

        assertEquals(RegisterEffect.NavigateToSignIn, effects.firstOrNull())
        collector.cancel()
    }

    // ── updateStrings ──────────────────────────────────────────────────────────

    @Test
    fun `updateStrings changes subsequent validation error messages`() = runTest(testDispatcher) {
        val vm = buildViewModel()
        vm.updateStrings(
            RegisterStrings(
                errorNameRequired = "الاسم الكامل مطلوب",
                errorTermsRequired = "يرجى قبول الشروط والأحكام",
            ),
        )
        vm.onIntent(RegisterIntent.RegisterClicked)
        assertEquals("الاسم الكامل مطلوب", vm.state.value.fullNameError)
    }
}
