package com.hussein.jana.feature.home.tabs.profile.domain.model

/** Lesson completion for one subscribed course. */
data class CourseProgress(
    val courseId: String,
    val title: String,
    val completedLessons: Int,
    val totalLessons: Int,
) {
    val fraction: Float
        get() = if (totalLessons > 0) completedLessons.toFloat() / totalLessons else 0f
}

/**
 * Learning summary across the student's subscribed courses.
 * [attendedLiveLessons] is null when the live lessons history could not be loaded.
 */
data class LearningProgress(
    val courses: List<CourseProgress>,
    val completedLessons: Int,
    val totalLessons: Int,
    val attendedLiveLessons: Int?,
    val examsCompleted: Int,
) {
    val overallFraction: Float
        get() = if (totalLessons > 0) completedLessons.toFloat() / totalLessons else 0f
}
