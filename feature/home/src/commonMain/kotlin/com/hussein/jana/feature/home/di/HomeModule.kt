package com.hussein.jana.feature.home.di

import com.hussein.jana.core.navigation.FeatureEntryProvider
import com.hussein.jana.feature.home.HomeEntryProvider
import com.hussein.jana.feature.home.HomeViewModel
import com.hussein.jana.feature.home.tabs.home.HomeTabViewModel
import com.hussein.jana.feature.home.tabs.janaai.AiChatViewModel
import com.hussein.jana.feature.home.tabs.schedule.ScheduleTabViewModel
import com.hussein.jana.feature.home.tabs.teachers.TeachersTabViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

val homeModule = module {
    viewModel { HomeViewModel() }
    viewModel { HomeTabViewModel() }
    viewModel { TeachersTabViewModel() }
    viewModel { AiChatViewModel() }
    viewModel { ScheduleTabViewModel() }
    single<FeatureEntryProvider>(named("HomeEntryProvider")) { HomeEntryProvider() }
}
