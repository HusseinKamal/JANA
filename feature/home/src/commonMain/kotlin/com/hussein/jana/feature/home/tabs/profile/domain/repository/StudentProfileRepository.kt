package com.hussein.jana.feature.home.tabs.profile.domain.repository

import com.hussein.jana.feature.home.tabs.profile.domain.model.StudentProfile
import kotlinx.coroutines.flow.Flow

interface StudentProfileRepository {
    suspend fun getProfile(): Result<StudentProfile>

    /** Emits the latest known profile, including local edits after [updateProfile]. */
    fun observeProfile(): Flow<StudentProfile>

    suspend fun updateProfile(fullName: String, email: String, phone: String): Result<StudentProfile>

    suspend fun logout(): Result<Unit>
}
