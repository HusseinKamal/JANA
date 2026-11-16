package com.hussein.jana.core.datastore

import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * Unit tests for [TokenRepository] read/write logic using the JVM (java.util.prefs) actual
 * of [SecureStorage]. Each test clears the prefs node before and after to avoid state leakage.
 */
class TokenRepositoryTest {

    private lateinit var secureStorage: SecureStorage
    private lateinit var tokenRepository: TokenRepository

    @BeforeTest
    fun setup() {
        secureStorage = SecureStorage()
        secureStorage.clear()
        tokenRepository = TokenRepository(secureStorage)
    }

    @AfterTest
    fun teardown() {
        secureStorage.clear()
    }

    @Test
    fun `isLoggedIn returns false when no access token stored`() {
        assertFalse(tokenRepository.isLoggedIn())
    }

    @Test
    fun `saveTokens stores access and refresh tokens`() {
        tokenRepository.saveTokens("acc_abc", "ref_xyz")

        assertEquals("acc_abc", tokenRepository.getAccessToken())
        assertEquals("ref_xyz", tokenRepository.getRefreshToken())
    }

    @Test
    fun `isLoggedIn returns true after saving tokens`() {
        tokenRepository.saveTokens("acc_abc", "ref_xyz")

        assertTrue(tokenRepository.isLoggedIn())
    }

    @Test
    fun `saveUserId stores and retrieves user id`() {
        tokenRepository.saveUserId("user_42")

        assertEquals("user_42", tokenRepository.getUserId())
    }

    @Test
    fun `clearSession removes all tokens`() {
        tokenRepository.saveTokens("acc_abc", "ref_xyz")
        tokenRepository.saveUserId("user_42")

        tokenRepository.clearSession()

        assertNull(tokenRepository.getAccessToken())
        assertNull(tokenRepository.getRefreshToken())
        assertFalse(tokenRepository.isLoggedIn())
    }

    @Test
    fun `getAccessToken returns null before any save`() {
        assertNull(tokenRepository.getAccessToken())
    }
}
