package com.hussein.jana.feature.home.tabs.courses.presentation.common

import androidx.compose.runtime.Composable
import com.hussein.jana.core.designsystem.Res
import com.hussein.jana.core.designsystem.course_price_format
import com.hussein.jana.core.designsystem.currency_egp
import com.hussein.jana.feature.home.tabs.courses.domain.model.Money
import org.jetbrains.compose.resources.stringResource
import kotlin.math.roundToLong

/** "450" for whole amounts, otherwise two decimals ("449.50"). Integer math: no String.format in common code. */
internal fun Money.amountText(): String {
    val cents = (amount * 100).roundToLong()
    return if (cents % 100 == 0L) {
        (cents / 100).toString()
    } else {
        "${cents / 100}.${(cents % 100).toString().padStart(2, '0')}"
    }
}

@Composable
private fun Money.currencyLabel(): String = when (currencyCode) {
    "EGP" -> stringResource(Res.string.currency_egp)
    else -> currencyCode
}

/** Localised price text, e.g. "450 EGP" / "450 ج.م". */
@Composable
internal fun Money.formatted(): String =
    stringResource(Res.string.course_price_format)
        .replace("%1\$s", amountText())
        .replace("%2\$s", currencyLabel())
