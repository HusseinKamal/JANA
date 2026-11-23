package com.hussein.jana.feature.home.tabs.courses.presentation.common

import androidx.compose.runtime.Composable
import com.hussein.jana.core.designsystem.JanaCourseCardUiModel
import com.hussein.jana.feature.home.tabs.courses.domain.model.Course

/** Maps a domain [Course] to the shared designsystem course card model. */
@Composable
internal fun Course.toCardUiModel(): JanaCourseCardUiModel = JanaCourseCardUiModel(
    title = title,
    subjects = subjects,
    isLocked = !isSubscribed,
    isPurchasable = isPurchasable,
    priceText = price?.formatted(),
    completedLessons = completedLessons,
    totalLessons = totalLessons,
)
