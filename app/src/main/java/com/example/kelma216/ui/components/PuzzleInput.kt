package com.example.kelma216.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kelma216.data.model.LetterTile
import com.example.kelma216.ui.theme.Cream
import com.example.kelma216.ui.theme.LockedGrey
import com.example.kelma216.ui.theme.MidnightBlue
import com.example.kelma216.ui.theme.Turquoise
import com.example.kelma216.ui.theme.TunisianRed
import com.example.kelma216.ui.theme.Yellow

/** One empty/filled answer box. [char] is null when empty. */
@Composable
fun AnswerSlot(
    char: Char?,
    modifier: Modifier = Modifier,
    locked: Boolean = false,
    isError: Boolean = false,
    isCorrect: Boolean = false,
    onClick: (() -> Unit)? = null,
) {
    val background = when {
        locked -> Yellow
        isCorrect -> Color(0xFFB9EBD3)
        isError -> TunisianRed.copy(alpha = 0.15f)
        char != null -> Cream
        else -> Color.White
    }
    val borderColor = when {
        isCorrect -> Color(0xFF2FA96B)
        isError -> TunisianRed
        locked -> Yellow
        else -> MidnightBlue.copy(alpha = 0.55f)
    }

    Box(
        modifier = modifier
            .size(56.dp)
            .aspectRatio(1f)
            .clip(RoundedCornerShape(14.dp))
            .background(background)
            .border(width = 2.5.dp, color = borderColor, shape = RoundedCornerShape(14.dp))
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
            .semantics {
                contentDescription = when {
                    char != null -> "Case: $char"
                    locked -> "Case verrouillée par un indice"
                    else -> "Case vide"
                }
            },
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = char?.toString() ?: "",
            color = MidnightBlue,
            fontSize = 26.sp,
            fontWeight = FontWeight.ExtraBold,
        )
    }
}

/** A selectable letter tile in the tray. Disabled once used. */
@Composable
fun LetterTile(
    letter: Char,
    used: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val background = if (used) LockedGrey.copy(alpha = 0.35f) else Turquoise
    val content = if (used) MidnightBlue.copy(alpha = 0.35f) else MidnightBlue

    Box(
        modifier = modifier
            .size(56.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(background)
            .border(
                width = 2.5.dp,
                color = if (used) LockedGrey else MidnightBlue,
                shape = RoundedCornerShape(14.dp),
            )
            .clickable(enabled = !used, onClick = onClick)
            .semantics { contentDescription = "Lettre $letter" },
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = letter.toString(),
            color = content,
            fontSize = 26.sp,
            fontWeight = FontWeight.ExtraBold,
        )
    }
}

/** Row that lays out the answer slots, centred. */
@Composable
fun AnswerRow(
    slots: List<Char?>,
    lockedSlots: Set<Int>,
    showResult: Boolean,
    allCorrect: Boolean,
    onSlotClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    FlowRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        itemVerticalAlignment = Alignment.CenterVertically,
    ) {
        slots.forEachIndexed { index, char ->
            AnswerSlot(
                char = char,
                locked = index in lockedSlots,
                isCorrect = showResult && allCorrect,
                isError = showResult && !allCorrect,
                onClick = { onSlotClick(index) },
            )
        }
    }
}

/** Row that lays out the selectable letter tiles, wrapping when there are many. */
@Composable
fun TileRow(
    tiles: List<LetterTile>,
    usedIds: Set<String>,
    onTileClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    FlowRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        itemVerticalAlignment = Alignment.CenterVertically,
    ) {
        tiles.forEach { tile ->
            LetterTile(
                letter = tile.char,
                used = tile.id in usedIds,
                onClick = { onTileClick(tile.id) },
            )
        }
    }
}

/** Small helper text styled consistently. */
@Composable
fun HelperText(text: String, modifier: Modifier = Modifier, color: Color = MidnightBlue) {
    Text(
        text = text,
        modifier = modifier,
        style = MaterialTheme.typography.bodyMedium,
        color = color,
    )
}
