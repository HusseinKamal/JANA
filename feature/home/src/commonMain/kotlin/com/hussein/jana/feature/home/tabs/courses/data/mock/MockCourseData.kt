package com.hussein.jana.feature.home.tabs.courses.data.mock

import com.hussein.jana.feature.home.tabs.courses.domain.model.Course
import com.hussein.jana.feature.home.tabs.courses.domain.model.CourseLesson
import com.hussein.jana.feature.home.tabs.courses.domain.model.CourseUnit
import com.hussein.jana.feature.home.tabs.courses.domain.model.Money

/** Mock catalogue used until the real courses API is wired. */
internal object MockCourseData {

    val courses: List<Course> = listOf(
        Course(
            id = "course_1",
            title = "Advanced Mathematics",
            description = "Comprehensive coverage of calculus, linear algebra, and differential equations for high school seniors.",
            imageUrl = null,
            totalLessons = 6,
            completedLessons = 4,
            subjects = listOf("Calculus", "Linear Algebra"),
            isSubscribed = true,
            price = Money(450.0, "EGP"),
        ),
        Course(
            id = "course_2",
            title = "Modern Physics",
            description = "From classical mechanics to quantum physics — a complete preparation course for university entrance.",
            imageUrl = null,
            totalLessons = 6,
            completedLessons = 0,
            subjects = listOf("Mechanics", "Quantum Physics"),
            isSubscribed = false,
            price = Money(400.0, "EGP"),
        ),
        Course(
            id = "course_3",
            title = "Organic Chemistry",
            description = "Master organic reactions, mechanisms, and stereochemistry with guided problem-solving sessions.",
            imageUrl = null,
            totalLessons = 6,
            completedLessons = 0,
            subjects = listOf("Reactions", "Mechanisms"),
            isSubscribed = false,
            price = Money(350.0, "EGP"),
        ),
    )

    /**
     * The first [completedLessons] lessons are marked done, matching the course progress.
     * Locked courses have 0 completed lessons, so none shows as done.
     */
    fun mockUnitsFor(courseId: String, completedLessons: Int): List<CourseUnit> {
        fun done(lessonNumber: Int) = lessonNumber <= completedLessons
        return listOf(
            CourseUnit(
                id = "${courseId}_unit_1",
                title = "Foundations",
                order = 1,
                lessons = listOf(
                    CourseLesson("${courseId}_l1", "Introduction & Core Concepts", 45, done(1), false),
                    CourseLesson("${courseId}_l2", "Problem-Solving Techniques", 60, done(2), true),
                    CourseLesson("${courseId}_l3", "Applied Examples", 50, done(3), false),
                ),
            ),
            CourseUnit(
                id = "${courseId}_unit_2",
                title = "Advanced Topics",
                order = 2,
                lessons = listOf(
                    CourseLesson("${courseId}_l4", "Deeper Exploration", 55, done(4), false),
                    CourseLesson("${courseId}_l5", "Complex Problem Sets", 70, done(5), true),
                    CourseLesson("${courseId}_l6", "Final Review Session", 45, done(6), false),
                ),
            ),
        )
    }
}
