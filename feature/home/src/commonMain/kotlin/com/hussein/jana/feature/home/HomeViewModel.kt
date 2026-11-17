package com.hussein.jana.feature.home

import com.hussein.jana.core.common.mvi.BaseMviViewModel

class HomeViewModel : BaseMviViewModel<HomeState, HomeIntent, HomeEffect>(HomeState()) {
    override fun onIntent(intent: HomeIntent) {
        when (intent) {
            is HomeIntent.TabSelected -> setState { copy(selectedTab = intent.tab) }
        }
    }
}
