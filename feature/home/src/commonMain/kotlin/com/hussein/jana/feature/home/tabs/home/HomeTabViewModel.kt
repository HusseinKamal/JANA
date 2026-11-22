package com.hussein.jana.feature.home.tabs.home

import com.hussein.jana.core.common.mvi.BaseMviViewModel
import com.hussein.jana.core.designsystem.Res
import com.hussein.jana.core.designsystem.home_greeting_afternoon
import com.hussein.jana.core.designsystem.home_greeting_evening
import com.hussein.jana.core.designsystem.home_greeting_morning
import com.hussein.jana.core.designsystem.home_snack_breakdown
import com.hussein.jana.core.designsystem.home_snack_calendar
import com.hussein.jana.core.designsystem.home_snack_formula
import com.hussein.jana.core.designsystem.home_snack_joining
import com.hussein.jana.core.designsystem.home_snack_notifications
import com.hussein.jana.core.designsystem.home_snack_quiz
import com.hussein.jana.core.designsystem.home_snack_refill
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

/**
 * ViewModel for the Home tab.
 *
 * Static mock data is loaded via [HomeTabState]'s default values.
 * The [currentHour] parameter is injectable so unit tests can control the
 * time-of-day without depending on the real wall clock.
 */
class HomeTabViewModel(
    private val currentHour: Int = computeCurrentHour(),
) : BaseMviViewModel<HomeTabState, HomeTabIntent, HomeTabEffect>(HomeTabState()) {

    init {
        // Set the time-appropriate greeting on first load.
        val greetingRes = when {
            currentHour < 12 -> Res.string.home_greeting_morning
            currentHour < 18 -> Res.string.home_greeting_afternoon
            else             -> Res.string.home_greeting_evening
        }
        setState { copy(greetingRes = greetingRes) }
    }

    override fun onIntent(intent: HomeTabIntent) = when (intent) {
        HomeTabIntent.JoinSessionClicked   -> sendEffect(HomeTabEffect.ShowSnackbar(Res.string.home_snack_joining))
        HomeTabIntent.QuickQuizClicked     -> sendEffect(HomeTabEffect.ShowSnackbar(Res.string.home_snack_quiz))
        HomeTabIntent.FormulaSheetClicked  -> sendEffect(HomeTabEffect.ShowSnackbar(Res.string.home_snack_formula))
        HomeTabIntent.BreakdownClicked     -> sendEffect(HomeTabEffect.ShowSnackbar(Res.string.home_snack_breakdown))
        HomeTabIntent.ViewCalendarClicked  -> sendEffect(HomeTabEffect.ShowSnackbar(Res.string.home_snack_calendar))
        HomeTabIntent.RefillPlanClicked    -> sendEffect(HomeTabEffect.ShowSnackbar(Res.string.home_snack_refill))
        HomeTabIntent.NotificationClicked  -> sendEffect(HomeTabEffect.ShowSnackbar(Res.string.home_snack_notifications))
    }
}

/** Returns the current local hour (0–23) using the system clock. */
@OptIn(ExperimentalTime::class)
private fun computeCurrentHour(): Int =
    Instant.fromEpochMilliseconds(Clock.System.now().toEpochMilliseconds())
        .toLocalDateTime(TimeZone.currentSystemDefault())
        .hour
