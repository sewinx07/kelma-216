package com.example.kelma216.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.example.kelma216.ui.theme.GoldenDark
import com.example.kelma216.ui.theme.MidnightBlue
import com.example.kelma216.ui.theme.Yellow

/** A single stylised coin (yellow disc with a darker inner ring). */
@Composable
fun CoinDot(size: Int = 22, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .size(size.dp)
            .clip(CircleShape)
            .background(Yellow)
            .padding(3.dp)
            .clip(CircleShape)
            .background(GoldenDark),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        Row(
            modifier = Modifier
                .size((size / 3).dp)
                .clip(CircleShape)
                .background(Yellow),
        ) {}
    }
}

/**
 * Coin balance pill shown on home, map, governorate and puzzle screens so the player
 * always sees what they can spend (presentation pages 8 and 9).
 */
@Composable
fun CoinBadge(
    coins: Int,
    modifier: Modifier = Modifier,
    label: String = "pièces",
) {
    Row(
        modifier = modifier
            .clip(MaterialTheme.shapes.extraLarge)
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 14.dp, vertical = 8.dp)
            .semantics { contentDescription = "$coins $label" },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        CoinDot(size = 22)
        Text(
            text = coins.toString(),
            style = MaterialTheme.typography.titleMedium,
            color = MidnightBlue,
        )
    }
}
