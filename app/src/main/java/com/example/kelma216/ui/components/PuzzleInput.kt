package com.example.kelma216.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
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

private val SlotShape = RoundedCornerShape(16.dp)
private val TileShape = RoundedCornerShape(16.dp)

/**
 * One empty/filled answer box. [char] is null when empty.
 *
 * The visual language mirrors the chunky word-game tiles of the reference art: cream
 * face, deep midnight outline, and a short pop animation whenever a letter lands.
 */
@Composable
fun AnswerSlot(
    char: Char?,
    modifier: Modifier = Modifier,
    locked: Boolean = false,
    isError: Boolean = false,
    isCorrect: Boolean = false,
    onClick: (() -> Unit)? = null,
) {
    val filled = char != null

    val targetBackground = when {
        locked -> Yellow
        isCorrect -> Color(0xFFB9EBD3)
        isError -> TunisianRed.copy(alpha = 0.15f)
        filled -> Cream
        else -> Color.White
    }
    val targetBorder = when {
        isCorrect -> Color(0xFF2FA96B)
        isError -> TunisianRed
        locked -> Yellow
        filled -> MidnightBlue
        else -> MidnightBlue.copy(alpha = 0.45f)
    }

    val background by animateColorAsState(targetBackground, tween(180), label = "slotBg")
    val borderColor by animateColorAsState(targetBorder, tween(180), label = "slotBorder")

    // Small pop when a letter is placed; a quick pulse when it is returned to the tray.
    val pop = remember { Animatable(1f) }
    var wasFilled by remember { mutableStateOf(false) }
    LaunchedEffect(char) {
        if (char != null) {
            pop.snapTo(0.78f)
            pop.animateTo(
                targetValue = 1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMediumLow,
                ),
            )
        } else if (wasFilled) {
            pop.snapTo(1.06f)
            pop.animateTo(1f, tween(140))
        }
        wasFilled = char != null
    }

    Box(
        modifier = modifier
            .size(54.dp)
            .graphicsLayer {
                scaleX = pop.value
                scaleY = pop.value
            }
            .clip(SlotShape)
            .background(background)
            .border(width = 2.5.dp, color = borderColor, shape = SlotShape)
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
        if (!filled && !locked) {
            // Faint baseline so empty slots read as "waiting for a letter".
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.42f)
                    .height(3.dp)
                    .clip(RoundedCornerShape(50))
                    .background(MidnightBlue.copy(alpha = 0.22f)),
            )
        }
        Text(
            text = char?.toString() ?: "",
            color = MidnightBlue,
            fontSize = 25.sp,
            fontWeight = FontWeight.ExtraBold,
        )
    }
}

/**
 * A selectable letter tile in the tray. Disabled (faded and slightly shrunk) once used.
 * Keeps the 3D "button" feel of the reference art via a darker base under the face.
 */
@Composable
fun LetterTile(
    letter: Char,
    used: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val scale by animateFloatAsState(
        targetValue = if (used) 0.84f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "tileScale",
    )
    val faceColors = if (used) {
        listOf(LockedGrey.copy(alpha = 0.30f), LockedGrey.copy(alpha = 0.30f))
    } else {
        listOf(Color(0xFF74DEDE), Turquoise)
    }
    val shadowColor = if (used) LockedGrey.copy(alpha = 0.25f) else Color(0xFF2E9FA0)
    val content = if (used) MidnightBlue.copy(alpha = 0.30f) else MidnightBlue

    Box(
        modifier = modifier
            .size(56.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            // Used tiles stay clickable: tapping one again returns it to the tray.
            .clickable(onClick = onClick)
            .semantics {
                contentDescription = if (used) {
                    "Lettre $letter placée, toucher pour retirer"
                } else {
                    "Lettre $letter"
                }
            },
        contentAlignment = Alignment.TopCenter,
    ) {
        // Base / depth layer
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .offset(y = 4.dp)
                .clip(TileShape)
                .background(shadowColor),
        )
        // Face
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .clip(TileShape)
                .background(Brush.verticalGradient(faceColors))
                .border(
                    width = 2.5.dp,
                    color = if (used) LockedGrey.copy(alpha = 0.5f) else MidnightBlue,
                    shape = TileShape,
                ),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = letter.toString(),
                color = content,
                fontSize = 25.sp,
                fontWeight = FontWeight.ExtraBold,
            )
        }
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
        horizontalArrangement = Arrangement.spacedBy(9.dp, Alignment.CenterHorizontally),
        verticalArrangement = Arrangement.spacedBy(9.dp),
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
