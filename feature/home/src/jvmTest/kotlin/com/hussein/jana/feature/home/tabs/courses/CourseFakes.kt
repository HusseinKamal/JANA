package com.hussein.jana.feature.home.tabs.courses

import com.hussein.jana.feature.home.tabs.courses.domain.model.CheckoutSession
import com.hussein.jana.feature.home.tabs.courses.domain.model.Course
import com.hussein.jana.feature.home.tabs.courses.domain.model.CourseLesson
import com.hussein.jana.feature.home.tabs.courses.domain.model.CourseUnit
import com.hussein.jana.feature.home.tabs.courses.domain.model.Money
import com.hussein.jana.feature.home.tabs.courses.domain.model.PaymentMethod
import com.hussein.jana.feature.home.tabs.courses.domain.model.PaymentStatus
import com.hussein.jana.feature.home.tabs.courses.domain.model.PaymentVerification
import com.hussein.jana.feature.home.tabs.courses.domain.repository.CoursePaymentRepository
import com.hussein.jana.feature.home.tabs.courses.domain.repository.CourseRepository
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

internal val subscribedCourse = Course(
    id = "c_sub",
    title = "Algebra",
    description = "Subscribed course",
    totalLessons = 10,
    completedLessons = 4,
    subjects = listOf("Math"),
    isSubscribed = true,
    price = Money(300.0, "EGP"),
)

internal val lockedCourse = Course(
    id = "c_locked",
    title = "Physics",
    description = "Locked course",
    totalLessons = 8,
    completedLessons = 0,
    subjects = listOf("Physics"),
    isSubscribed = false,
    price = Money(450.0, "EGP"),
)

internal val fakeUnits = listOf(
    CourseUnit(
        id = "u1",
        title = "Unit 1",
        order = 1,
        lessons = listOf(
            CourseLesson(id = "l1", title = "Lesson 1", durationMinutes = 30, isCompleted = false, hasExam = false),
        ),
    ),
)

/** In-memory course backend. Only [markSubscribed] (called by the fake payment backend) unlocks. */
internal class FakeCourseRepository(
    initial: List<Course> = listOf(subscribedCourse, lockedCourse),
) : CourseRepository {

    val courses = MutableStateFlow(initial)

    fun markSubscribed(courseId: String) {
        courses.update { list -> list.map { if (it.id == courseId) it.copy(isSubscribed = true) else it } }
    }

    override suspend fun getCourses(): Result<List<Course>> = Result.success(courses.value)

    override suspend fun getCourseDetail(courseId: String): Result<Course> =
        courses.value.find { it.id == courseId }?.let { Result.success(it) }
            ?: Result.failure(NoSuchElementException(courseId))

    override suspend fun getCourseUnits(courseId: String): Result<List<CourseUnit>> =
        Result.success(fakeUnits)

    override fun observeCourses(): Flow<List<Course>> = courses
}

/**
 * Fake payment backend. Each verify call takes the next status from [verifyResults]
 * (defaults to SUCCESS) and unlocks the course only when that status is SUCCESS.
 * Set [verifyGate] to hold verification open and observe the in-between state.
 */
internal class FakeCoursePaymentRepository(
    private val courseRepository: FakeCourseRepository,
) : CoursePaymentRepository {

    val verifyResults = ArrayDeque<PaymentStatus>()
    var verifyGate: CompletableDeferred<Unit>? = null
    val createdSessions = mutableListOf<CheckoutSession>()
    val verifiedSessionIds = mutableListOf<String>()

    override suspend fun createCheckout(
        courseId: String,
        method: PaymentMethod,
    ): Result<CheckoutSession> {
        val session = CheckoutSession(
            id = "chk_${createdSessions.size}",
            itemId = courseId,
            amount = 450.0,
            currencyCode = "EGP",
        )
        createdSessions += session
        return Result.success(session)
    }

    override suspend fun verifyPayment(checkoutId: String): Result<PaymentVerification> {
        verifyGate?.await()
        verifiedSessionIds += checkoutId
        val status = verifyResults.removeFirstOrNull() ?: PaymentStatus.SUCCESS
        val session = createdSessions.first { it.id == checkoutId }
        if (status == PaymentStatus.SUCCESS) courseRepository.markSubscribed(session.itemId)
        return Result.success(
            PaymentVerification(checkoutId, status, isSubscribed = status == PaymentStatus.SUCCESS),
        )
    }
}
