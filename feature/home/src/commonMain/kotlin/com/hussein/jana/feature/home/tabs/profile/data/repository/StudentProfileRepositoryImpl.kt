package com.hussein.jana.feature.home.tabs.profile.data.repository

import com.hussein.jana.core.datastore.TokenRepository
import com.hussein.jana.feature.home.tabs.profile.data.mock.MockStudentData
import com.hussein.jana.feature.home.tabs.profile.domain.model.StudentProfile
import com.hussein.jana.feature.home.tabs.profile.domain.repository.StudentProfileRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/** Keeps the profile in memory so Edit Profile and the Profile tab always show the same data. */
class StudentProfileRepositoryImpl(
    private val tokenRepository: TokenRepository,
) : StudentProfileRepository {

    private val profile = MutableStateFlow(MockStudentData.profile)

    // TODO: replace with real API call — GET /api/v1/me/profile
    override suspend fun getProfile(): Result<StudentProfile> {
        delay(400)
        return Result.success(profile.value)
    }

    override fun observeProfile(): Flow<StudentProfile> = profile.asStateFlow()

    // TODO: replace with real API call — PUT /api/v1/me/profile
    override suspend fun updateProfile(
        fullName: String,
        email: String,
        phone: String,
    ): Result<StudentProfile> {
        delay(700)
        profile.update {
            it.copy(fullName = fullName.trim(), email = email.trim(), phone = phone.trim())
        }
        return Result.success(profile.value)
    }

    // TODO: replace with real API call — POST /api/v1/auth/logout, and clear real tokens and
    //  cached data (courses, chat) for each teacher account.
    override suspend fun logout(): Result<Unit> {
        delay(400)
        tokenRepository.clearSession()
        profile.value = MockStudentData.profile
        return Result.success(Unit)
    }
}
