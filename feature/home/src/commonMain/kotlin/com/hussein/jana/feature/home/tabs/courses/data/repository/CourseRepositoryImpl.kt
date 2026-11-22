package com.hussein.jana.feature.home.tabs.courses.data.repository

import com.hussein.jana.feature.home.tabs.courses.domain.model.Course
import com.hussein.jana.feature.home.tabs.courses.domain.model.CourseLesson
import com.hussein.jana.feature.home.tabs.courses.domain.model.CourseUnit
import com.hussein.jana.feature.home.tabs.courses.domain.repository.CourseRepository
import kotlinx.coroutines.delay

class CourseRepositoryImpl : CourseRepository {

    // ---------------------------------------------------------------------------
    // Mock data — TODO: replace each method with real API call
    // ---------------------------------------------------------------------------

    private val mockCourses = listOf(
        Course(
            id = "course_1",
            title = "Advanced Mathematics",
            description = "Comprehensive coverage of calculus, linear algebra, and differential equations for high school seniors.",
            imageUrl = null,
            totalLessons = 6,
            completedLessons = 4,
            subjects = listOf("Calculus", "Linear Algebra"),
        ),
        Course(
            id = "course_2",
            title = "Modern Physics",
            description = "From classical mechanics to quantum physics — a complete preparation course for university entrance.",
            imageUrl = null,
            totalLessons = 6,
            completedLessons = 1,
            subjects = listOf("Mechanics", "Quantum Physics"),
        ),
        Course(
            id = "course_3",
            title = "Organic Chemistry",
            description = "Master organic reactions, mechanisms, and stereochemistry with guided problem-solving sessions.",
            imageUrl = null,
            totalLessons = 6,
            completedLessons = 0,
            subjects = listOf("Reactions", "Mechanisms"),
        ),
    )

    private fun mockUnitsFor(courseId: String): List<CourseUnit> = listOf(
        CourseUnit(
            id = "${courseId}_unit_1",
            title = "Foundations",
            order = 1,
            lessons = listOf(
                CourseLesson("${courseId}_l1", "Introduction & Core Concepts", 45, true, false),
                CourseLesson("${courseId}_l2", "Problem-Solving Techniques", 60, true, true),
                CourseLesson("${courseId}_l3", "Applied Examples", 50, true, false),
            ),
        ),
        CourseUnit(
            id = "${courseId}_unit_2",
            title = "Advanced Topics",
            order = 2,
            lessons = listOf(
                CourseLesson("${courseId}_l4", "Deeper Exploration", 55, false, false),
                CourseLesson("${courseId}_l5", "Complex Problem Sets", 70, false, true),
                CourseLesson("${courseId}_l6", "Final Review Session", 45, false, false),
            ),
        ),
    )

    // TODO: replace with real API call — GET /api/v1/courses
    override suspend fun getCourses(): Result<List<Course>> {
        delay(400)
        return Result.success(mockCourses)
    }

    // TODO: replace with real API call — GET /api/v1/courses/{id}
    override suspend fun getCourseDetail(courseId: String): Result<Course> {
        delay(400)
        val course = mockCourses.find { it.id == courseId } ?: mockCourses.first()
        return Result.success(course)
    }

    // TODO: replace with real API call — GET /api/v1/courses/{id}/units
    override suspend fun getCourseUnits(courseId: String): Result<List<CourseUnit>> {
        delay(400)
        return Result.success(mockUnitsFor(courseId))
    }
}
