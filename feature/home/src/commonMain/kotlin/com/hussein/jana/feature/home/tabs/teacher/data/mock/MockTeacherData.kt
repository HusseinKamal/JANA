package com.hussein.jana.feature.home.tabs.teacher.data.mock

import com.hussein.jana.feature.home.TeacherGender
import com.hussein.jana.feature.home.tabs.courses.domain.model.Money
import com.hussein.jana.feature.home.tabs.teacher.domain.model.ChatMessage
import com.hussein.jana.feature.home.tabs.teacher.domain.model.SubscriptionStatus
import com.hussein.jana.feature.home.tabs.teacher.domain.model.TeacherProfile
import com.hussein.jana.feature.home.tabs.teacher.domain.model.TeacherSubscription

/** Mock data for the active teacher platform. The only place a teacher id appears. */
internal object MockTeacherData {

    val profile = TeacherProfile(
        id = "teacher_active",
        name = "Ahmed",
        gender = TeacherGender.MALE,
        subject = "Mathematics",
        bio = "Mathematics teacher with 10 years of experience preparing secondary students for their final exams.",
        photoUrl = "https://i.pravatar.cc/300?img=12",
    )

    val liveSubscription = TeacherSubscription(
        planId = "live_monthly",
        planName = "Monthly live lessons",
        status = SubscriptionStatus.NOT_SUBSCRIBED,
        expiresAtMs = null,
        price = Money(200.0, "EGP"),
    )

    private const val MINUTE_MS = 60_000L

    fun seedMessages(nowMs: Long): List<ChatMessage> = listOf(
        ChatMessage(
            id = "msg_1",
            text = "Welcome to the course! Ask me anything about the lessons.",
            sentAtMs = nowMs - 180 * MINUTE_MS,
            isFromStudent = false,
            isRead = true,
        ),
        ChatMessage(
            id = "msg_2",
            text = "Thank you! I have a question about quadratic equations.",
            sentAtMs = nowMs - 170 * MINUTE_MS,
            isFromStudent = true,
            isRead = true,
        ),
        ChatMessage(
            id = "msg_3",
            text = "Sure, which part is not clear?",
            sentAtMs = nowMs - 160 * MINUTE_MS,
            isFromStudent = false,
            isRead = true,
        ),
        ChatMessage(
            id = "msg_4",
            text = "How do I know when to use the discriminant?",
            sentAtMs = nowMs - 150 * MINUTE_MS,
            isFromStudent = true,
            isRead = true,
        ),
    )
}
