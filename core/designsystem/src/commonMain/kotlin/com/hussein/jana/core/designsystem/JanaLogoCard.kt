package com.hussein.jana.core.designsystem

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * JANA brand logo card — renders the open-book + broadcast + AI-sparkle icon
 * on a deep scholarly-teal radial-gradient background.
 */
@Composable
fun JanaLogoCard(
    size: Dp = 120.dp,
    modifier: Modifier = Modifier,
) {
    val cornerShape = RoundedCornerShape(size * 0.22f)
    Box(
        modifier = modifier
            .size(size)
            .shadow(
                elevation = 20.dp,
                shape = cornerShape,
                ambientColor = ScholarlyTeal,
                spotColor = ScholarlyTeal,
            )
            .clip(cornerShape)
            .background(
                Brush.radialGradient(
                    colors = listOf(Color(0xFF1B5248), Color(0xFF0A2520)),
                )
            ),
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = this.size.width
            val h = this.size.height
            val cx = w / 2f
            val cy = h / 2f

            // ── 1. Radial amber glow ──────────────────────────────────────────
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0x50D97706), Color.Transparent),
                    center = Offset(cx, cy),
                    radius = w * 0.55f,
                ),
                radius = w * 0.55f,
                center = Offset(cx, cy),
            )

            // ── 2. Golden open book ───────────────────────────────────────────
            val bookTop = h * 0.30f
            val bookBottom = h * 0.68f
            val bookLeft = w * 0.10f
            val bookRight = w * 0.90f
            val spineX = cx

            // Left page
            val leftPage = Path().apply {
                moveTo(spineX, bookBottom)
                cubicTo(
                    spineX - w * 0.04f, bookBottom - h * 0.04f,
                    bookLeft + w * 0.04f, bookBottom - h * 0.03f,
                    bookLeft, bookBottom - h * 0.05f,
                )
                lineTo(bookLeft, bookTop + h * 0.05f)
                cubicTo(
                    bookLeft, bookTop,
                    spineX - w * 0.10f, bookTop + h * 0.03f,
                    spineX, bookTop,
                )
                close()
            }
            drawPath(
                path = leftPage,
                brush = Brush.linearGradient(
                    colors = listOf(Color(0xFFD97706), Color(0xFFB45309)),
                    start = Offset(bookLeft, bookTop),
                    end = Offset(spineX, bookBottom),
                ),
            )

            // Right page
            val rightPage = Path().apply {
                moveTo(spineX, bookBottom)
                cubicTo(
                    spineX + w * 0.04f, bookBottom - h * 0.04f,
                    bookRight - w * 0.04f, bookBottom - h * 0.03f,
                    bookRight, bookBottom - h * 0.05f,
                )
                lineTo(bookRight, bookTop + h * 0.05f)
                cubicTo(
                    bookRight, bookTop,
                    spineX + w * 0.10f, bookTop + h * 0.03f,
                    spineX, bookTop,
                )
                close()
            }
            drawPath(
                path = rightPage,
                brush = Brush.linearGradient(
                    colors = listOf(Color(0xFFB45309), Color(0xFF92400E)),
                    start = Offset(spineX, bookTop),
                    end = Offset(bookRight, bookBottom),
                ),
            )

            // Spine rectangle
            drawRect(
                color = Color(0xFF92400E),
                topLeft = Offset(spineX - w * 0.022f, bookTop),
                size = Size(w * 0.044f, bookBottom - bookTop),
            )

            // Bottom arc
            drawArc(
                color = Color(0xFF92400E).copy(alpha = 0.55f),
                startAngle = 0f,
                sweepAngle = 180f,
                useCenter = false,
                topLeft = Offset(bookLeft + w * 0.08f, bookBottom - h * 0.05f),
                size = Size(bookRight - bookLeft - w * 0.16f, h * 0.07f),
                style = Stroke(width = 1.8.dp.toPx()),
            )

            // ── 3. Vertical stem + emitter ────────────────────────────────────
            val stemX = cx
            val emitterY = h * 0.10f
            val stemBottom = bookTop - h * 0.01f

            drawLine(
                color = Color(0xFFD97706),
                start = Offset(stemX, emitterY + h * 0.03f),
                end = Offset(stemX, stemBottom),
                strokeWidth = 2.2.dp.toPx(),
            )

            drawCircle(
                color = Color(0xFFD97706),
                radius = w * 0.038f,
                center = Offset(stemX, emitterY),
            )

            // ── 4. Broadcast wave arcs (upper-left of stem) ───────────────────
            repeat(3) { i ->
                val r = w * 0.07f * (i + 1)
                val alpha = 0.85f - i * 0.25f
                drawArc(
                    color = Color(0xFFD97706).copy(alpha = alpha),
                    startAngle = 195f,
                    sweepAngle = 65f,
                    useCenter = false,
                    topLeft = Offset(stemX - r, emitterY - r),
                    size = Size(r * 2f, r * 2f),
                    style = Stroke(width = 1.6.dp.toPx()),
                )
            }

            // ── 5. 4-point indigo sparkle star ────────────────────────────────
            val starCx = w * 0.70f
            val starCy = h * 0.23f
            val starR = w * 0.095f
            val starNarrow = starR * 0.28f

            // Vertical diamond (taller, narrower)
            drawPath(
                path = Path().apply {
                    moveTo(starCx, starCy - starR)
                    lineTo(starCx + starNarrow, starCy)
                    lineTo(starCx, starCy + starR)
                    lineTo(starCx - starNarrow, starCy)
                    close()
                },
                color = Color(0xFF818CF8),
            )
            // Horizontal diamond (wider, shorter)
            drawPath(
                path = Path().apply {
                    moveTo(starCx - starR, starCy)
                    lineTo(starCx, starCy - starNarrow)
                    lineTo(starCx + starR, starCy)
                    lineTo(starCx, starCy + starNarrow)
                    close()
                },
                color = Color(0xFF6366F1),
            )
            // White center dot
            drawCircle(
                color = Color.White,
                radius = w * 0.022f,
                center = Offset(starCx, starCy),
            )

            // ── 6. Small cyan mini-star (top-right of main star) ──────────────
            val miniCx = starCx + w * 0.13f
            val miniCy = starCy - h * 0.07f
            val miniR = w * 0.048f
            val miniNarrow = miniR * 0.28f

            drawPath(
                path = Path().apply {
                    moveTo(miniCx, miniCy - miniR)
                    lineTo(miniCx + miniNarrow, miniCy)
                    lineTo(miniCx, miniCy + miniR)
                    lineTo(miniCx - miniNarrow, miniCy)
                    close()
                },
                color = Color(0xFF22D3EE),
            )
            drawPath(
                path = Path().apply {
                    moveTo(miniCx - miniR, miniCy)
                    lineTo(miniCx, miniCy - miniNarrow)
                    lineTo(miniCx + miniR, miniCy)
                    lineTo(miniCx, miniCy + miniNarrow)
                    close()
                },
                color = Color(0xFF06B6D4),
            )

            // ── 7. Red LIVE pill badge (bottom-right of book) ─────────────────
            val badgeCx = bookRight - w * 0.14f
            val badgeCy = bookBottom - h * 0.10f
            val badgeW = w * 0.30f
            val badgeH = h * 0.14f
            val badgeLeft = badgeCx - badgeW / 2f
            val badgeTop = badgeCy - badgeH / 2f

            drawRoundRect(
                color = Color(0xFFDC2626),
                topLeft = Offset(badgeLeft, badgeTop),
                size = Size(badgeW, badgeH),
                cornerRadius = CornerRadius(badgeH / 2f),
                style = Fill,
            )

            // White ring
            val dotCx = badgeLeft + badgeH * 0.65f
            drawCircle(
                color = Color.White.copy(alpha = 0.65f),
                radius = badgeH * 0.28f,
                center = Offset(dotCx, badgeCy),
                style = Stroke(width = 1.4.dp.toPx()),
            )

            // Pink dot
            drawCircle(
                color = Color(0xFFFF8080),
                radius = badgeH * 0.14f,
                center = Offset(dotCx, badgeCy),
            )
        }
    }
}
