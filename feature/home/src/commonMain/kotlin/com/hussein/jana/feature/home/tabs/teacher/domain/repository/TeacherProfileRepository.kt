package com.hussein.jana.feature.home.tabs.teacher.domain.repository

import com.hussein.jana.feature.home.tabs.teacher.domain.model.TeacherProfile

interface TeacherProfileRepository {
    /** The active teacher is resolved from the session, so no id is passed. */
    suspend fun getActiveTeacherProfile(): Result<TeacherProfile>
}
