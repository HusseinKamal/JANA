package com.hussein.jana.feature.splash

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

/**
 * Unit tests for [SplashViewModel] MVI state/effect transitions.
 * Uses [StandardTestDispatcher] so [viewModelScope] coroutines run under the test scheduler,
 * and [advanceTimeBy] is used to skip the 2-second auto-redirect delay.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class SplashViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var secureStorage: SecureStorage

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        secureStorage = SecureStorage()
        secureStorage.clear()
    }

    @AfterTest
    fun teardown() {
        Dispatchers.resetMain()
        secureStorage.clear()
    }

    @Test
    fun `guest user receives no auto-redirect after timer elapses`() = runTest(testDispatcher) {
        // New behavior: unauthenticated users stay on the splash screen and choose their own path.
        val tokenRepository = TokenRepository(secureStorage)
        val viewModel = SplashViewModel(tokenRepository)

        val effects = mutableListOf<SplashEffect>()
        val collector = launch { viewModel.effect.collect { effects.add(it) } }

        advanceTimeBy(2_500)

        assertEquals(true, effects.isEmpty())
        collector.cancel()
    }

    @Test
    fun `emits NavigateToHome when user has a valid access token`() = runTest(testDispatcher) {
        secureStorage.putString("access_token", "valid_token")
        val tokenRepository = TokenRepository(secureStorage)
        val viewModel = SplashViewModel(tokenRepository)

        val effects = mutableListOf<SplashEffect>()
        val collector = launch { viewModel.effect.collect { effects.add(it) } }

        advanceTimeBy(2_500)

        assertEquals(SplashEffect.NavigateToHome, effects.firstOrNull())
        collector.cancel()
    }

    @Test
    fun `GetStartedClicked intent triggers immediate redirect to auth when not logged in`() =
        runTest(testDispatcher) {
            val tokenRepository = TokenRepository(secureStorage)
            val viewModel = SplashViewModel(tokenRepository)

            val effects = mutableListOf<SplashEffect>()
            val collector = launch { viewModel.effect.collect { effects.add(it) } }

            viewModel.onIntent(SplashIntent.GetStartedClicked)
            advanceTimeBy(100)

            assertEquals(SplashEffect.NavigateToAuth, effects.firstOrNull())
            collector.cancel()
        }

    @Test
    fun `SignInClicked intent emits NavigateToSignIn`() = runTest(testDispatcher) {
        val tokenRepository = TokenRepository(secureStorage)
        val viewModel = SplashViewModel(tokenRepository)

        val effects = mutableListOf<SplashEffect>()
        val collector = launch { viewModel.effect.collect { effects.add(it) } }

        viewModel.onIntent(SplashIntent.SignInClicked)
        advanceTimeBy(100)

        assertEquals(SplashEffect.NavigateToSignIn, effects.firstOrNull())
        collector.cancel()
    }
}
