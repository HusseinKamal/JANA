package com.hussein.jana.feature.home.tabs.profile.di

import com.hussein.jana.feature.home.tabs.profile.data.repository.AppSettingsRepositoryImpl
import com.hussein.jana.feature.home.tabs.profile.data.repository.StudentProfileRepositoryImpl
import com.hussein.jana.feature.home.tabs.profile.domain.repository.AppSettingsRepository
import com.hussein.jana.feature.home.tabs.profile.domain.repository.StudentProfileRepository
import com.hussein.jana.feature.home.tabs.profile.domain.usecase.GetLearningProgressUseCase
import com.hussein.jana.feature.home.tabs.profile.domain.usecase.GetStudentProfileUseCase
import com.hussein.jana.feature.home.tabs.profile.domain.usecase.GetSyllabusUseCase
import com.hussein.jana.feature.home.tabs.profile.domain.usecase.LogoutUseCase
import com.hussein.jana.feature.home.tabs.profile.domain.usecase.ObserveAppLanguageUseCase
import com.hussein.jana.feature.home.tabs.profile.domain.usecase.ObserveNotificationPreferencesUseCase
import com.hussein.jana.feature.home.tabs.profile.domain.usecase.ObserveStudentProfileUseCase
import com.hussein.jana.feature.home.tabs.profile.domain.usecase.SetAppLanguageUseCase
import com.hussein.jana.feature.home.tabs.profile.domain.usecase.UpdateNotificationPreferencesUseCase
import com.hussein.jana.feature.home.tabs.profile.domain.usecase.UpdateStudentProfileUseCase
import com.hussein.jana.feature.home.tabs.profile.presentation.edit.EditProfileViewModel
import com.hussein.jana.feature.home.tabs.profile.presentation.profile.ProfileViewModel
import com.hussein.jana.feature.home.tabs.profile.presentation.progress.ProgressViewModel
import com.hussein.jana.feature.home.tabs.profile.presentation.syllabus.SyllabusViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.bind
import org.koin.dsl.module

val profileModule = module {
    // Single so Edit Profile and the Profile tab share the same in-memory profile.
    singleOf(::StudentProfileRepositoryImpl) bind StudentProfileRepository::class
    singleOf(::AppSettingsRepositoryImpl) bind AppSettingsRepository::class

    factoryOf(::GetStudentProfileUseCase)
    factoryOf(::ObserveStudentProfileUseCase)
    factoryOf(::UpdateStudentProfileUseCase)
    factoryOf(::LogoutUseCase)
    factoryOf(::ObserveAppLanguageUseCase)
    factoryOf(::SetAppLanguageUseCase)
    factoryOf(::ObserveNotificationPreferencesUseCase)
    factoryOf(::UpdateNotificationPreferencesUseCase)
    factoryOf(::GetSyllabusUseCase)
    factoryOf(::GetLearningProgressUseCase)

    viewModel {
        ProfileViewModel(get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get())
    }
    viewModel { EditProfileViewModel(get(), get()) }
    viewModel { SyllabusViewModel(get()) }
    viewModel { ProgressViewModel(get()) }
}
