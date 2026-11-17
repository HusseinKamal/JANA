package com.hussein.jana.feature.home.tabs.home

import com.hussein.jana.core.common.mvi.BaseMviViewModel

class HomeTabViewModel : BaseMviViewModel<HomeTabState, HomeTabIntent, HomeTabEffect>(HomeTabState()) {

    // Static mock data is loaded via the default state values in HomeTabState.
    // No network call needed for the demo.

    override fun onIntent(intent: HomeTabIntent) = when (intent) {
        HomeTabIntent.JoinSessionClicked   -> sendEffect(HomeTabEffect.ShowSnackbar("Joining live session…"))
        HomeTabIntent.QuickQuizClicked     -> sendEffect(HomeTabEffect.ShowSnackbar("Quick quiz starting…"))
        HomeTabIntent.FormulaSheetClicked  -> sendEffect(HomeTabEffect.ShowSnackbar("Opening formula sheet…"))
        HomeTabIntent.BreakdownClicked     -> sendEffect(HomeTabEffect.ShowSnackbar("Loading concept breakdown…"))
        HomeTabIntent.ViewCalendarClicked  -> sendEffect(HomeTabEffect.ShowSnackbar("Opening calendar…"))
        HomeTabIntent.RefillPlanClicked    -> sendEffect(HomeTabEffect.ShowSnackbar("Opening refill plan…"))
        HomeTabIntent.NotificationClicked  -> sendEffect(HomeTabEffect.ShowSnackbar("Notifications"))
    }
}
