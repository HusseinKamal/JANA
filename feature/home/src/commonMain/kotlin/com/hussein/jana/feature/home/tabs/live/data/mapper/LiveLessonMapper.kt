package com.hussein.jana.feature.home.tabs.live.data.mapper

import com.hussein.jana.feature.home.tabs.live.data.remote.dto.LessonExamDto
import com.hussein.jana.feature.home.tabs.live.data.remote.dto.LessonFileDto
import com.hussein.jana.feature.home.tabs.live.data.remote.dto.LiveLessonDto
import com.hussein.jana.feature.home.tabs.live.domain.model.LessonExam
import com.hussein.jana.feature.home.tabs.live.domain.model.LessonFile
import com.hussein.jana.feature.home.tabs.live.domain.model.LessonFileType
import com.hussein.jana.feature.home.tabs.live.domain.model.LessonStatus
import com.hussein.jana.feature.home.tabs.live.domain.model.LiveLesson

fun LiveLessonDto.toDomain(): LiveLesson = LiveLesson(
    id = id,
    subject = subject,
    level = level,
    title = title,
    teacherName = teacherName,
    dateText = dateText,
    timeText = timeText,
    startEpochMs = startEpochMs,
    status = when (status.uppercase()) {
        "LIVE_NOW" -> LessonStatus.LIVE_NOW
        "ENDED"    -> LessonStatus.ENDED
        else       -> LessonStatus.UPCOMING
    },
    minsUntilStart = minsUntilStart,
    zoomUrl = zoomUrl,
    description = description,
)

fun LessonFileDto.toDomain(): LessonFile = LessonFile(
    id = id,
    title = title,
    sizeText = sizeText,
    fileType = when (fileType.uppercase()) {
        "DOCX" -> LessonFileType.DOCX
        "PPTX" -> LessonFileType.PPTX
        else   -> LessonFileType.PDF
    },
    downloadUrl = downloadUrl,
)

fun LessonExamDto.toDomain(): LessonExam = LessonExam(
    id = id,
    title = title,
    isTimed = isTimed,
    durationMinutes = durationMinutes,
    examUrl = examUrl,
)
