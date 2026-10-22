package com.example.kelma216.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Original, dependency-free icons drawn with Canvas.
 *
 * The project does not include a Material icon set, so rather than pull in a new
 * dependency (or use emoji, which the design brief forbids), the handful of icons the
 * game needs are drawn here. Each scales stroke weight to the drawn size.
 */

private fun DrawScope.strokeWidth(relative: Float = 0.09f): Float =
    size.minDimension * relative

// --- Back chevron ----------------------------------------------------------

@Composable
fun BackIcon(
    modifier: Modifier = Modifier.size(24.dp),
    tint: Color = Color.Unspecified,
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val path = Path().apply {
            moveTo(w * 0.60f, h * 0.22f)
            lineTo(w * 0.32f, h * 0.50f)
            lineTo(w * 0.60f, h * 0.78f)
        }
        drawPath(
            path = path,
            color = tint,
            style = Stroke(
                width = strokeWidth(0.11f),
                cap = StrokeCap.Round,
                join = StrokeJoin.Round,
            ),
        )
    }
}

// --- Play triangle ---------------------------------------------------------

@Composable
fun PlayIcon(
    modifier: Modifier = Modifier.size(24.dp),
    tint: Color = Color.Unspecified,
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val path = Path().apply {
            moveTo(w * 0.34f, h * 0.28f)
            lineTo(w * 0.76f, h * 0.50f)
            lineTo(w * 0.34f, h * 0.72f)
            close()
        }
        drawPath(path, color = tint)
    }
}

// --- Check -----------------------------------------------------------------

@Composable
fun CheckIcon(
    modifier: Modifier = Modifier.size(24.dp),
    tint: Color = Color.Unspecified,
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val path = Path().apply {
            moveTo(w * 0.22f, h * 0.54f)
            lineTo(w * 0.42f, h * 0.74f)
            lineTo(w * 0.78f, h * 0.30f)
        }
        drawPath(
            path = path,
            color = tint,
            style = Stroke(
                width = strokeWidth(0.12f),
                cap = StrokeCap.Round,
                join = StrokeJoin.Round,
            ),
        )
    }
}

// --- Lock ------------------------------------------------------------------

@Composable
fun LockIcon(
    modifier: Modifier = Modifier.size(24.dp),
    tint: Color = Color.Unspecified,
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val stroke = Stroke(
            width = strokeWidth(0.10f),
            cap = StrokeCap.Round,
            join = StrokeJoin.Round,
        )

        // Shackle: top half arc (dome) plus two short stems down to the body.
        val arcLeft = w * 0.34f
        val arcTop = h * 0.18f
        val arcW = w * 0.32f
        val arcH = h * 0.30f
        drawArc(
            color = tint,
            startAngle = 180f,
            sweepAngle = 180f,
            useCenter = false,
            topLeft = Offset(arcLeft, arcTop),
            size = Size(arcW, arcH),
            style = stroke,
        )
        val stemTop = arcTop + arcH / 2f
        val bodyTop = h * 0.46f
        drawLine(tint, Offset(arcLeft, stemTop), Offset(arcLeft, bodyTop), strokeWidth(0.10f), StrokeCap.Round)
        drawLine(tint, Offset(arcLeft + arcW, stemTop), Offset(arcLeft + arcW, bodyTop), strokeWidth(0.10f), StrokeCap.Round)

        // Body
        drawRoundRect(
            color = tint,
            topLeft = Offset(w * 0.24f, bodyTop),
            size = Size(w * 0.52f, h * 0.34f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(w * 0.08f),
        )
    }
}

// --- Coin ------------------------------------------------------------------

@Composable
fun CoinIcon(
    modifier: Modifier = Modifier.size(24.dp),
    tint: Color = Color.Unspecified,
    ring: Color = Color.Unspecified,
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val center = Offset(w / 2f, h / 2f)
        drawCircle(color = tint, radius = w * 0.44f, center = center)
        drawCircle(
            color = ring,
            radius = w * 0.30f,
            center = center,
            style = Stroke(width = strokeWidth(0.06f)),
        )
        // Small centre mark (stylised dinar dot)
        drawCircle(color = ring, radius = w * 0.10f, center = center)
    }
}

// --- Hint lightbulb --------------------------------------------------------

@Composable
fun HintIcon(
    modifier: Modifier = Modifier.size(24.dp),
    tint: Color = Color.Unspecified,
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        // Bulb
        drawCircle(
            color = tint,
            radius = w * 0.24f,
            center = Offset(w * 0.5f, h * 0.42f),
            style = Stroke(width = strokeWidth(0.09f)),
        )
        // Base
        drawLine(
            color = tint,
            start = Offset(w * 0.40f, h * 0.72f),
            end = Offset(w * 0.60f, h * 0.72f),
            strokeWidth = strokeWidth(0.09f),
            cap = StrokeCap.Round,
        )
        drawLine(
            color = tint,
            start = Offset(w * 0.44f, h * 0.84f),
            end = Offset(w * 0.56f, h * 0.84f),
            strokeWidth = strokeWidth(0.09f),
            cap = StrokeCap.Round,
        )
        drawLine(
            color = tint,
            start = Offset(w * 0.42f, h * 0.66f),
            end = Offset(w * 0.58f, h * 0.66f),
            strokeWidth = strokeWidth(0.09f),
            cap = StrokeCap.Round,
        )
    }
}

// --- Settings (sliders) ----------------------------------------------------

@Composable
fun SettingsIcon(
    modifier: Modifier = Modifier.size(24.dp),
    tint: Color = Color.Unspecified,
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val sw = strokeWidth(0.09f)
        fun row(y: Float, knobX: Float) {
            drawLine(tint, Offset(w * 0.16f, y), Offset(w * 0.84f, y), sw, StrokeCap.Round)
            drawCircle(tint, radius = w * 0.11f, center = Offset(knobX, y))
        }
        row(h * 0.32f, w * 0.64f)
        row(h * 0.68f, w * 0.38f)
    }
}

// --- Star (reward) ---------------------------------------------------------

@Composable
fun StarIcon(
    modifier: Modifier = Modifier.size(24.dp),
    tint: Color = Color.Unspecified,
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val cx = w / 2f
        val cy = h / 2f
        val outer = w * 0.46f
        val inner = w * 0.20f
        val path = Path()
        for (i in 0 until 10) {
            val angle = Math.toRadians((-90 + i * 36).toDouble())
            val r = if (i % 2 == 0) outer else inner
            val x = cx + (r * kotlin.math.cos(angle)).toFloat()
            val y = cy + (r * kotlin.math.sin(angle)).toFloat()
            if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }
        path.close()
        drawPath(path, color = tint)
    }
}
