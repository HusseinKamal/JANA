package com.hussein.jana.core.designsystem

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/** Display status of a subscription shown by [JanaSubscriptionStatusCard]. */
enum class JanaSubscriptionStatus { ACTIVE, EXPIRED, NOT_SUBSCRIBED }

/**
 * Card showing a subscription plan: title + status chip, optional detail text and price,
 * and an optional action button (shown only when both [actionLabel] and [onAction] are set).
 */
@Composable
fun JanaSubscriptionStatusCard(
    title: String,
    status: JanaSubscriptionStatus,
    statusLabel: String,
    detailText: String?,
    priceText: String?,
    actionLabel: String?,
    onAction: (() -> Unit)?,
    modifier: Modifier = Modifier,
    isActionLoading: Boolean = false,
) {
    val (chipContainer, chipText) = when (status) {
        JanaSubscriptionStatus.ACTIVE ->
            SuccessGreen.copy(alpha = 0.15f) to SuccessGreen
        JanaSubscriptionStatus.EXPIRED ->
            MaterialTheme.colorScheme.errorContainer to MaterialTheme.colorScheme.onErrorContainer
        JanaSubscriptionStatus.NOT_SUBSCRIBED ->
            MaterialTheme.colorScheme.surfaceContainerHigh to MaterialTheme.colorScheme.onSurfaceVariant
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f),
                )
                Spacer(modifier = Modifier.width(8.dp))
                JanaStatusChip(
                    status = statusLabel,
                    containerColor = chipContainer,
                    textColor = chipText,
                )
            }

            if (!detailText.isNullOrBlank()) {
                Text(
                    text = detailText,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            priceText?.let { JanaPriceTag(priceText = it) }

            if (actionLabel != null && onAction != null) {
                Button(
                    onClick = onAction,
                    enabled = !isActionLoading,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = ScholarlyTeal),
                    shape = RoundedCornerShape(10.dp),
                ) {
                    if (isActionLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 2.dp,
                            color = OnPrimary,
                        )
                    } else {
                        Text(
                            text = actionLabel,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                }
            }
        }
    }
}
