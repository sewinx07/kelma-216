package com.example.kelma216.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.unit.dp
import com.example.kelma216.ui.theme.Turquoise
import com.example.kelma216.ui.theme.TunisianRed
import com.example.kelma216.ui.theme.Yellow

/**
 * A repeating zigzag / triangle band inspired by Tunisian decorative borders.
 * Purely geometric ornament (NOT a geographic map), used to add local colour without
 * inventing misleading geography (requirement 4B).
 */
@Composable
fun PatternStrip(
    modifier: Modifier = Modifier,
    height: androidx.compose.ui.unit.Dp = 26.dp,
    colors: List<Color> = listOf(TunisianRed, Yellow, Turquoise),
) {
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(height),
    ) {
        val w = size.width
        val h = size.height
        val step = h * 1.6f
        var x = 0f
        var colorIndex = 0
        while (x < w) {
            val path = Path().apply {
                moveTo(x, h)
                lineTo(x + step / 2f, 0f)
                lineTo(x + step, h)
                close()
            }
            drawPath(path, color = colors[colorIndex % colors.size])
            x += step
            colorIndex++
        }
    }
}
