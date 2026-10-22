package com.example.kelma216.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.example.kelma216.ui.theme.MidnightBlue
import com.example.kelma216.ui.theme.Turquoise
import com.example.kelma216.ui.theme.Yellow

/**
 * Rounded progress bar with a clear label (presentation page 9: "question actuelle / 9
 * et les questions déjà réussies").
 */
@Composable
fun ProgressBar(
    solved: Int,
    total: Int,
    modifier: Modifier = Modifier,
    label: String = "$solved / $total",
) {
    val fraction = if (total > 0) solved.toFloat() / total else 0f
    val animated by animateFloatAsState(targetValue = fraction, label = "progress")

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MidnightBlue,
            )
            Text(
                text = "Enigmes résolues",
                style = MaterialTheme.typography.labelMedium,
                color = MidnightBlue.copy(alpha = 0.7f),
            )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(14.dp)
                .clip(RoundedCornerShape(50))
                .background(Turquoise.copy(alpha = 0.35f))
                .semantics { contentDescription = "$solved sur $total résolues" },
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(animated)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(50))
                    .background(Yellow),
            )
        }
    }
}

/**
 * Nine dots representing the nine puzzle slots of a governorate
 * (presentation page 9: fixed 9 questions per level).
 */
@Composable
fun PuzzleDots(
    totalSlots: Int,
    solvedCount: Int,
    hasContent: Boolean,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        repeat(totalSlots) { i ->
            val filled = i < solvedCount
            Box(
                modifier = Modifier
                    .width(14.dp)
                    .height(14.dp)
                    .clip(RoundedCornerShape(50))
                    .background(
                        when {
                            filled -> Yellow
                            hasContent -> Turquoise.copy(alpha = 0.6f)
                            else -> Color.LightGray.copy(alpha = 0.6f)
                        },
                    ),
            )
        }
    }
}

/** Thin static bar used inline where a full label isn't needed. */
@Composable
fun MiniProgress(fraction: Float, modifier: Modifier = Modifier) {
    LinearProgressIndicator(
        progress = { fraction.coerceIn(0f, 1f) },
        modifier = modifier
            .fillMaxWidth()
            .height(8.dp)
            .clip(RoundedCornerShape(50)),
        color = Yellow,
        trackColor = Turquoise.copy(alpha = 0.35f),
    )
}
