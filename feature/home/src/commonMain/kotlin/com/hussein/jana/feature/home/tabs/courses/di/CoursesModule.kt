package com.hussein.jana.feature.home.tabs.courses.di

import com.hussein.jana.feature.home.tabs.courses.data.repository.CourseRepositoryImpl
import com.hussein.jana.feature.home.tabs.courses.domain.repository.CourseRepository
import com.hussein.jana.feature.home.tabs.courses.domain.usecase.GetCourseDetailUseCase
import com.hussein.jana.feature.home.tabs.courses.domain.usecase.GetCoursesUseCase
import com.hussein.jana.feature.home.tabs.courses.domain.usecase.GetCourseUnitsUseCase
import com.hussein.jana.feature.home.tabs.courses.presentation.detail.CourseDetailViewModel
import com.hussein.jana.feature.home.tabs.courses.presentation.list.CoursesViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.bind
import org.koin.dsl.module

val coursesModule = module {
    singleOf(::CourseRepositoryImpl) bind CourseRepository::class

    factoryOf(::GetCoursesUseCase)
    factoryOf(::GetCourseDetailUseCase)
    factoryOf(::GetCourseUnitsUseCase)

    viewModel { CoursesViewModel(get()) }
    viewModel { (courseId: String) -> CourseDetailViewModel(courseId, get(), get()) }
}
