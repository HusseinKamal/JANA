package com.hussein.jana.feature.home.tabs.profile.domain.model

/** The signed-in student on the active teacher platform. */
data class StudentProfile(
    val id: String,
    val fullName: String,
    val email: String,
    val phone: String,
    val photoUrl: String?,
)
