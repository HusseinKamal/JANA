package com.hussein.jana.feature.home.common

import androidx.compose.runtime.Composable
import com.hussein.jana.core.designsystem.Res
import com.hussein.jana.core.designsystem.teacher_title_female
import com.hussein.jana.core.designsystem.teacher_title_male
import com.hussein.jana.core.designsystem.teacher_title_with_subject
import com.hussein.jana.feature.home.TeacherGender
import org.jetbrains.compose.resources.stringResource

/** Teacher name with honorific: "Mr. <name>" / "Mrs. <name>", or the plain name when gender is unknown. */
@Composable
fun teacherDisplayName(name: String, gender: TeacherGender): String = when (gender) {
    TeacherGender.MALE    -> stringResource(Res.string.teacher_title_male, name)
    TeacherGender.FEMALE  -> stringResource(Res.string.teacher_title_female, name)
    TeacherGender.UNKNOWN -> name
}

/** "<honorific name> · <subject>", or just the display name when [subject] is blank. */
@Composable
fun teacherTitleWithSubject(name: String, gender: TeacherGender, subject: String): String {
    val displayName = teacherDisplayName(name, gender)
    return if (subject.isBlank()) {
        displayName
    } else {
        stringResource(Res.string.teacher_title_with_subject, displayName, subject)
    }
}
