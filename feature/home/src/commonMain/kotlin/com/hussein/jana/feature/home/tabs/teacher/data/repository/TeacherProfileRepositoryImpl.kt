package com.hussein.jana.feature.home.tabs.teacher.data.repository

import com.hussein.jana.feature.home.tabs.teacher.data.mock.MockTeacherData
import com.hussein.jana.feature.home.tabs.teacher.domain.model.TeacherProfile
import com.hussein.jana.feature.home.tabs.teacher.domain.repository.TeacherProfileRepository
import kotlinx.coroutines.delay

class TeacherProfileRepositoryImpl : TeacherProfileRepository {

    // TODO: replace with real API call — GET /api/v1/me/platform and GET /api/v1/me/branding
    //  (active teacher resolved from the session)
    // TODO: multi-account ActiveTeacherContext
    override suspend fun getActiveTeacherProfile(): Result<TeacherProfile> {
        delay(400)
        return Result.success(MockTeacherData.profile)
    }
}
