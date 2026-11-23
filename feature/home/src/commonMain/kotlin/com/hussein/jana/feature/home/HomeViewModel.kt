package com.hussein.jana.feature.home

import com.hussein.jana.core.common.mvi.BaseMviViewModel

class HomeViewModel : BaseMviViewModel<HomeState, HomeIntent, HomeEffect>(
    // TODO: load teacherName from active teacher context. Mock value until then.
    // TODO: read teacherName/teacherGender/teacherSubject from GetActiveTeacherProfileUseCase
    //  (tabs/teacher); values below match MockTeacherData until then.
    // TODO: multi-account ActiveTeacherContext
    HomeState(
        teacherName = "Ahmed",
        teacherGender = TeacherGender.MALE,
        teacherSubject = "Mathematics",
    ),
) {
    override fun onIntent(intent: HomeIntent) {
        when (intent) {
            is HomeIntent.TabSelected -> setState { copy(selectedTab = intent.tab) }
        }
    }
}
