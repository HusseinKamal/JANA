package com.hussein.jana.feature.home.tabs.courses.di

import com.hussein.jana.feature.home.tabs.courses.data.local.CourseCatalogStore
import com.hussein.jana.feature.home.tabs.courses.data.repository.CoursePaymentRepositoryImpl
import com.hussein.jana.feature.home.tabs.courses.data.repository.CourseRepositoryImpl
import com.hussein.jana.feature.home.tabs.courses.domain.repository.CoursePaymentRepository
import com.hussein.jana.feature.home.tabs.courses.domain.repository.CourseRepository
import com.hussein.jana.feature.home.tabs.courses.domain.usecase.CreateCourseCheckoutUseCase
import com.hussein.jana.feature.home.tabs.courses.domain.usecase.GetCourseDetailUseCase
import com.hussein.jana.feature.home.tabs.courses.domain.usecase.GetCoursesUseCase
import com.hussein.jana.feature.home.tabs.courses.domain.usecase.GetCourseUnitsUseCase
import com.hussein.jana.feature.home.tabs.courses.domain.usecase.ObserveCoursesUseCase
import com.hussein.jana.feature.home.tabs.courses.domain.usecase.VerifyCoursePaymentUseCase
import com.hussein.jana.feature.home.tabs.courses.presentation.detail.CourseDetailViewModel
import com.hussein.jana.feature.home.tabs.courses.presentation.list.CoursesViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.bind
import org.koin.dsl.module

val coursesModule = module {
    // One shared catalogue so the list and details always agree after a verified payment.
    single { CourseCatalogStore() }
    singleOf(::CourseRepositoryImpl) bind CourseRepository::class
    singleOf(::CoursePaymentRepositoryImpl) bind CoursePaymentRepository::class

    factoryOf(::GetCoursesUseCase)
    factoryOf(::GetCourseDetailUseCase)
    factoryOf(::GetCourseUnitsUseCase)
    factoryOf(::ObserveCoursesUseCase)
    factoryOf(::CreateCourseCheckoutUseCase)
    factoryOf(::VerifyCoursePaymentUseCase)

    viewModel { CoursesViewModel(get(), get()) }
    viewModel { (courseId: String, openCheckout: Boolean) ->
        CourseDetailViewModel(courseId, openCheckout, get(), get(), get(), get())
    }
}
