package com.hussein.jana.feature.home.tabs.courses.presentation.detail.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountBalanceWallet
import androidx.compose.material.icons.rounded.CreditCard
import androidx.compose.material.icons.rounded.Storefront
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.hussein.jana.core.designsystem.JanaButton
import com.hussein.jana.core.designsystem.JanaOutlinedButton
import com.hussein.jana.core.designsystem.JanaPriceTag
import com.hussein.jana.core.designsystem.Res
import com.hussein.jana.core.designsystem.btn_cancel
import com.hussein.jana.core.designsystem.btn_retry
import com.hussein.jana.core.designsystem.course_pay_amount
import com.hussein.jana.core.designsystem.payment_failed
import com.hussein.jana.core.designsystem.payment_method_card
import com.hussein.jana.core.designsystem.payment_method_fawry
import com.hussein.jana.core.designsystem.payment_method_title
import com.hussein.jana.core.designsystem.payment_method_wallet
import com.hussein.jana.core.designsystem.payment_pending
import com.hussein.jana.core.designsystem.payment_processing
import com.hussein.jana.core.designsystem.payment_sheet_title
import com.hussein.jana.core.designsystem.payment_total_label
import com.hussein.jana.core.designsystem.payment_verifying
import com.hussein.jana.feature.home.tabs.courses.domain.model.PaymentMethod
import com.hussein.jana.feature.home.tabs.courses.presentation.detail.CheckoutStatus
import com.hussein.jana.feature.home.tabs.courses.presentation.detail.CheckoutUiState
import com.hussein.jana.feature.home.tabs.courses.presentation.detail.PaymentFailure
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

/**
 * Course-specific checkout content, hosted inside the generic `JanaBottomSheet`.
 * Fully state-hoisted: all state comes from [checkout] and every action is a callback.
 */
@Composable
fun CourseCheckoutSheetContent(
    courseTitle: String,
    priceText: String?,
    checkout: CheckoutUiState,
    onMethodSelected: (PaymentMethod) -> Unit,
    onPay: () -> Unit,
    onRetry: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val isBusy = checkout.isBusy
    val isFailed = checkout.status == CheckoutStatus.FAILED
    val payLabel = stringResource(Res.string.course_pay_amount).replace("%1\$s", priceText.orEmpty())

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = stringResource(Res.string.payment_sheet_title),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = courseTitle,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Spacer(modifier = Modifier.height(16.dp))
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = stringResource(Res.string.payment_total_label),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
            if (priceText != null) JanaPriceTag(priceText = priceText)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(Res.string.payment_method_title),
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Spacer(modifier = Modifier.height(8.dp))

        Column(modifier = Modifier.selectableGroup()) {
            checkout.availableMethods.forEach { method ->
                PaymentMethodRow(
                    method = method,
                    selected = method == checkout.selectedMethod,
                    enabled = !isBusy,
                    onClick = { onMethodSelected(method) },
                )
            }
        }

        statusMessage(checkout)?.let { (message, isError) ->
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = stringResource(message),
                style = MaterialTheme.typography.bodyMedium,
                color = if (isError) {
                    MaterialTheme.colorScheme.error
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                },
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        JanaButton(
            text = if (isFailed) stringResource(Res.string.btn_retry) else payLabel,
            onClick = if (isFailed) onRetry else onPay,
            isLoading = isBusy,
        )
        Spacer(modifier = Modifier.height(8.dp))
        JanaOutlinedButton(
            text = stringResource(Res.string.btn_cancel),
            onClick = onCancel,
            enabled = !isBusy,
        )
    }
}

@Composable
private fun PaymentMethodRow(
    method: PaymentMethod,
    selected: Boolean,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .selectable(
                selected = selected,
                enabled = enabled,
                role = Role.RadioButton,
                onClick = onClick,
            )
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        RadioButton(selected = selected, onClick = null, enabled = enabled)
        Icon(
            imageVector = method.icon(),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(22.dp),
        )
        Text(
            text = stringResource(method.label()),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}

private fun PaymentMethod.label(): StringResource = when (this) {
    PaymentMethod.CARD -> Res.string.payment_method_card
    PaymentMethod.WALLET -> Res.string.payment_method_wallet
    PaymentMethod.FAWRY -> Res.string.payment_method_fawry
}

private fun PaymentMethod.icon(): ImageVector = when (this) {
    PaymentMethod.CARD -> Icons.Rounded.CreditCard
    PaymentMethod.WALLET -> Icons.Rounded.AccountBalanceWallet
    PaymentMethod.FAWRY -> Icons.Rounded.Storefront
}

/** Status line under the methods: message resource + whether it is an error. */
private fun statusMessage(checkout: CheckoutUiState): Pair<StringResource, Boolean>? =
    when (checkout.status) {
        CheckoutStatus.PROCESSING -> Res.string.payment_processing to false
        CheckoutStatus.VERIFYING -> Res.string.payment_verifying to false
        CheckoutStatus.FAILED -> when (checkout.failure) {
            PaymentFailure.PENDING -> Res.string.payment_pending to true
            else -> Res.string.payment_failed to true
        }
        else -> null
    }
