package com.hussein.jana.feature.home.tabs.teacher.di

import com.hussein.jana.feature.home.tabs.teacher.data.repository.TeacherChatRepositoryImpl
import com.hussein.jana.feature.home.tabs.teacher.data.repository.TeacherProfileRepositoryImpl
import com.hussein.jana.feature.home.tabs.teacher.data.repository.TeacherSubscriptionRepositoryImpl
import com.hussein.jana.feature.home.tabs.teacher.domain.repository.TeacherChatRepository
import com.hussein.jana.feature.home.tabs.teacher.domain.repository.TeacherProfileRepository
import com.hussein.jana.feature.home.tabs.teacher.domain.repository.TeacherSubscriptionRepository
import com.hussein.jana.feature.home.tabs.teacher.domain.usecase.CreateLiveSubscriptionCheckoutUseCase
import com.hussein.jana.feature.home.tabs.teacher.domain.usecase.GetActiveTeacherProfileUseCase
import com.hussein.jana.feature.home.tabs.teacher.domain.usecase.GetLiveLessonsSubscriptionUseCase
import com.hussein.jana.feature.home.tabs.teacher.domain.usecase.GetTeacherChatMessagesUseCase
import com.hussein.jana.feature.home.tabs.teacher.domain.usecase.GetTeacherUpcomingLessonsUseCase
import com.hussein.jana.feature.home.tabs.teacher.domain.usecase.SendTeacherMessageUseCase
import com.hussein.jana.feature.home.tabs.teacher.domain.usecase.VerifyLiveSubscriptionPaymentUseCase
import com.hussein.jana.feature.home.tabs.teacher.presentation.chat.TeacherChatViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.bind
import org.koin.dsl.module

val teacherModule = module {
    singleOf(::TeacherProfileRepositoryImpl) bind TeacherProfileRepository::class
    singleOf(::TeacherSubscriptionRepositoryImpl) bind TeacherSubscriptionRepository::class
    singleOf(::TeacherChatRepositoryImpl) bind TeacherChatRepository::class

    factoryOf(::GetActiveTeacherProfileUseCase)
    factoryOf(::GetTeacherUpcomingLessonsUseCase)
    factoryOf(::GetLiveLessonsSubscriptionUseCase)
    factoryOf(::CreateLiveSubscriptionCheckoutUseCase)
    factoryOf(::VerifyLiveSubscriptionPaymentUseCase)
    factoryOf(::GetTeacherChatMessagesUseCase)
    factoryOf(::SendTeacherMessageUseCase)

    viewModel { TeacherChatViewModel(get(), get(), get()) }
}
