package com.hussein.jana.feature.home.tabs.profile.data.mock

import com.hussein.jana.feature.home.tabs.profile.domain.model.StudentProfile

internal object MockStudentData {
    val profile = StudentProfile(
        id = "student_1",
        fullName = "Hussein Kamal",
        email = "hussein@example.com",
        phone = "+20 100 123 4567",
        photoUrl = null,
    )
}
