package com.hussein.jana.feature.home.tabs.live.di

import com.hussein.jana.feature.home.tabs.live.data.remote.LiveLessonApi
import com.hussein.jana.feature.home.tabs.live.data.repository.LiveLessonRepositoryImpl
import com.hussein.jana.feature.home.tabs.live.domain.repository.LiveLessonRepository
import com.hussein.jana.feature.home.tabs.live.domain.usecase.GetLessonDetailsUseCase
import com.hussein.jana.feature.home.tabs.live.domain.usecase.GetLessonExamsUseCase
import com.hussein.jana.feature.home.tabs.live.domain.usecase.GetLessonFilesUseCase
import com.hussein.jana.feature.home.tabs.live.domain.usecase.GetNextLiveLessonsUseCase
import com.hussein.jana.feature.home.tabs.live.domain.usecase.GetPreviousLiveLessonsUseCase
import com.hussein.jana.feature.home.tabs.live.domain.usecase.GetUpcomingLiveLessonsUseCase
import com.hussein.jana.feature.home.tabs.live.domain.usecase.SyncCalendarUseCase
import com.hussein.jana.feature.home.tabs.live.presentation.detail.LessonDetailViewModel
import com.hussein.jana.feature.home.tabs.live.presentation.list.LiveLessonsViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.bind
import org.koin.dsl.module

val liveLessonsModule = module {
    singleOf(::LiveLessonApi)
    singleOf(::LiveLessonRepositoryImpl) bind LiveLessonRepository::class

    factoryOf(::GetUpcomingLiveLessonsUseCase)
    factoryOf(::GetNextLiveLessonsUseCase)
    factoryOf(::GetPreviousLiveLessonsUseCase)
    factoryOf(::GetLessonDetailsUseCase)
    factoryOf(::GetLessonFilesUseCase)
    factoryOf(::GetLessonExamsUseCase)
    factoryOf(::SyncCalendarUseCase)

    viewModel { LiveLessonsViewModel(get(), get(), get(), get()) }
    viewModel { (lessonId: String) -> LessonDetailViewModel(lessonId, get(), get(), get()) }
}
