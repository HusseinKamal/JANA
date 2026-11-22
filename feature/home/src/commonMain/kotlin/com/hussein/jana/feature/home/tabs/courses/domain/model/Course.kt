package com.hussein.jana.feature.home.tabs.courses.domain.model

data class Course(
    val id: String,
    val title: String,
    val description: String,
    val imageUrl: String? = null,
    val totalLessons: Int,
    val completedLessons: Int,
    val subjects: List<String>,
    /** True only once the backend has confirmed the student's subscription. */
    val isSubscribed: Boolean = false,
    /** Null means free or unknown; price UI is hidden. */
    val price: Money? = null,
)
