package com.hussein.jana.feature.home.tabs.teacher.domain.model

import com.hussein.jana.feature.home.TeacherGender

/** Profile of the active teacher platform the student is logged into. */
data class TeacherProfile(
    val id: String,
    val name: String,
    val gender: TeacherGender,
    val subject: String,
    val bio: String,
    val photoUrl: String?,
)
