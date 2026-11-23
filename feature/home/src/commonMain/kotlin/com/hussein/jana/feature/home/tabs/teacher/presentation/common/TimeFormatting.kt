package com.hussein.jana.feature.home.tabs.teacher.presentation.common

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.ExperimentalTime

// Uses the same kotlinx.datetime API as HomeTabViewModel: the JVM target resolves an older
// kotlinx-datetime where kotlin.time.Instant overloads and LocalDateTime.day are missing.

/** "dd/MM/yyyy" in the device time zone. */
internal fun formatShortDate(epochMs: Long): String {
    val dt = epochMs.toLocalDateTime()
    return "${dt.dayOfMonth.pad()}/${dt.monthNumber.pad()}/${dt.year}"
}

/** "HH:mm" in the device time zone. */
internal fun formatTimeOfDay(epochMs: Long): String {
    val dt = epochMs.toLocalDateTime()
    return "${dt.hour.pad()}:${dt.minute.pad()}"
}

@OptIn(ExperimentalTime::class)
private fun Long.toLocalDateTime(): LocalDateTime =
    Instant.fromEpochMilliseconds(this).toLocalDateTime(TimeZone.currentSystemDefault())

private fun Int.pad(): String = toString().padStart(2, '0')
