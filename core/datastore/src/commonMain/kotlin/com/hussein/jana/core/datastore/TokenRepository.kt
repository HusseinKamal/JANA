package com.hussein.jana.core.datastore

private const val KEY_ACCESS_TOKEN = "access_token"
private const val KEY_REFRESH_TOKEN = "refresh_token"
private const val KEY_USER_ID = "user_id"

class TokenRepository(private val secureStorage: SecureStorage) {

    fun saveTokens(accessToken: String, refreshToken: String) {
        secureStorage.putString(KEY_ACCESS_TOKEN, accessToken)
        secureStorage.putString(KEY_REFRESH_TOKEN, refreshToken)
    }

    fun getAccessToken(): String? = secureStorage.getString(KEY_ACCESS_TOKEN)
    fun getRefreshToken(): String? = secureStorage.getString(KEY_REFRESH_TOKEN)

    fun saveUserId(userId: String) {
        secureStorage.putString(KEY_USER_ID, userId)
    }

    fun getUserId(): String? = secureStorage.getString(KEY_USER_ID)

    fun isLoggedIn(): Boolean = getAccessToken() != null

    fun clearSession() = secureStorage.clear()
}
