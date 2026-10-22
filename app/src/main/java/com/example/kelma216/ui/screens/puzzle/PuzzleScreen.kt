package com.example.kelma216.ui.screens.puzzle

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kelma216.data.model.VerificationStatus
import com.example.kelma216.domain.GameConfig
import com.example.kelma216.domain.GameRules
import com.example.kelma216.ui.components.AnswerRow
import com.example.kelma216.ui.components.ButtonVariant
import com.example.kelma216.ui.components.CheckIcon
import com.example.kelma216.ui.components.CoinBadge
import com.example.kelma216.ui.components.HintIcon
import com.example.kelma216.ui.components.KelmaButton
import com.example.kelma216.ui.components.KelmaCard
import com.example.kelma216.ui.components.KelmaTopBar
import com.example.kelma216.ui.components.PatternStrip
import com.example.kelma216.ui.components.TileRow
import com.example.kelma216.ui.theme.Cream
import com.example.kelma216.ui.theme.ErrorRed
import com.example.kelma216.ui.theme.MidnightBlue
import com.example.kelma216.ui.theme.SuccessGreen
import com.example.kelma216.ui.theme.Turquoise
import com.example.kelma216.ui.theme.TunisianRed
import com.example.kelma216.ui.theme.Yellow
import com.example.kelma216.viewmodel.GameViewModel
import com.example.kelma216.viewmodel.PuzzlePhase
import com.example.kelma216.viewmodel.PuzzleSession

/**
 * The illustrated word puzzle (presentation pages 8 and 10): clue image, answer slots,
 * selectable letter tiles, submit, correct/incorrect feedback, paid hint and coin balance.
 *
 * Progress is written the moment an answer is accepted, so pressing back can never lose
 * a completed puzzle (requirement 4D).
 */
@Composable
fun PuzzleScreen(
    viewModel: GameViewModel,
    puzzleId: String,
    onBack: () -> Unit,
    onNextPuzzle: (String) -> Unit,
    onCompletion: (String) -> Unit,
) {
    LaunchedEffect(puzzleId) {
        viewModel.startPuzzle(puzzleId)
        viewModel.clearMessage()
    }

    val gameState by viewModel.gameState.collectAsState()
    val session by viewModel.session.collectAsState()
    val message by viewModel.message.collectAsState()
    var showHintDialog by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .safeDrawingPadding(),
    ) {
        when {
            session != null -> PuzzleContent(
                session = session!!,
                coins = gameState.coins,
                hintOwned = session != null && gameState.isPuzzleHinted(session!!.puzzle.id),
                message = message,
                onBack = onBack,
                onTile = viewModel::selectTile,
                onSlot = viewModel::deselectSlot,
                onSubmit = viewModel::submitAnswer,
                onRetry = viewModel::retryAfterMistake,
                onAskHint = { showHintDialog = true },
                onDismissMessage = viewModel::clearMessage,
                onNextPuzzle = onNextPuzzle,
                onCompletion = onCompletion,
                gameState = gameState,
            )

            message != null -> Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(message ?: "", color = MidnightBlue, textAlign = TextAlign.Center)
                Spacer(Modifier.height(16.dp))
                KelmaButton(text = "Retour", onClick = onBack, variant = ButtonVariant.Primary)
            }

            else -> Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                Text("Chargement…", color = MidnightBlue.copy(alpha = 0.6f))
            }
        }

        // Paid-hint confirmation (requirement 5.5: coins spent only on confirmation).
        if (showHintDialog && session != null) {
            HintConfirmDialog(
                cost = GameConfig.HINT_COST,
                coins = gameState.coins,
                onConfirm = {
                    showHintDialog = false
                    viewModel.buyHint()
                },
                onDismiss = { showHintDialog = false },
            )
        }
    }
}

@Composable
private fun PuzzleContent(
    session: PuzzleSession,
    coins: Int,
    hintOwned: Boolean,
    message: String?,
    gameState: com.example.kelma216.data.model.GameState,
    onBack: () -> Unit,
    onTile: (String) -> Unit,
    onSlot: (Int) -> Unit,
    onSubmit: () -> Unit,
    onRetry: () -> Unit,
    onAskHint: () -> Unit,
    onDismissMessage: () -> Unit,
    onNextPuzzle: (String) -> Unit,
    onCompletion: (String) -> Unit,
) {
    val puzzle = session.puzzle
    val phase = session.phase
    val allFilled = session.slots.none { it == null }
    val usedIds = session.slotSource.filterNotNull().toSet()

    Column(modifier = Modifier.fillMaxSize()) {
        KelmaTopBar(
            title = "Enigme ${puzzle.position} / ${GameConfig.PUZZLES_PER_GOVERNORATE}",
            onBack = onBack,
            trailing = { CoinBadge(coins = coins) },
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // --- Clue: photo when available, else a text prompt ------------
            KelmaCard(
                modifier = Modifier.fillMaxWidth(),
                containerColor = Color.White,
            ) {
                if (puzzle.clueRes != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(4f / 3f)
                            .clip(RoundedCornerShape(16.dp))
                            .background(MidnightBlue.copy(alpha = 0.06f)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Image(
                            painter = painterResource(id = puzzle.clueRes),
                            contentDescription = puzzle.clueCaption ?: "Photo de l'énigme",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize(),
                        )
                    }
                } else {
                    // No photo sourced yet: show the text clue clearly and honestly.
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Text(
                            text = puzzle.clueCaption ?: "Trouve le mot",
                            style = MaterialTheme.typography.headlineSmall,
                            color = MidnightBlue,
                            textAlign = TextAlign.Center,
                        )
                        Text(
                            text = "${puzzle.answer.length} lettres",
                            style = MaterialTheme.typography.titleMedium,
                            color = Turquoise,
                            textAlign = TextAlign.Center,
                        )
                    }
                }
                puzzle.clueCaption?.let { caption ->
                    if (puzzle.clueRes != null) {
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = caption,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MidnightBlue.copy(alpha = 0.7f),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                }
                if (puzzle.photoSourceUrl != null) {
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = listOfNotNull(puzzle.photoLicense, puzzle.photoAttribution)
                            .joinToString(" · "),
                        style = MaterialTheme.typography.labelSmall,
                        color = MidnightBlue.copy(alpha = 0.5f),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }

            Spacer(Modifier.height(14.dp))

            Text(
                text = if (puzzle.clueRes != null) {
                    "Observe l'image et forme le mot à partir des lettres ci-dessous."
                } else {
                    "Forme le mot à partir des lettres ci-dessous."
                },
                style = MaterialTheme.typography.bodyLarge,
                color = MidnightBlue,
                textAlign = TextAlign.Center,
            )

            Spacer(Modifier.height(18.dp))

            // --- Answer slots ------------------------------------------
            AnswerRow(
                slots = session.slots,
                lockedSlots = session.lockedSlots,
                showResult = phase == PuzzlePhase.INCORRECT || phase == PuzzlePhase.CORRECT,
                allCorrect = phase == PuzzlePhase.CORRECT,
                onSlotClick = onSlot,
            )

            Spacer(Modifier.height(6.dp))

            when (phase) {
                PuzzlePhase.INCORRECT -> Text(
                    text = "Pas encore… réessaie !",
                    style = MaterialTheme.typography.titleMedium,
                    color = ErrorRed,
                )
                else -> Text(
                    text = "Touche une lettre pour la placer",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MidnightBlue.copy(alpha = 0.6f),
                )
            }

            message?.let {
                Spacer(Modifier.height(6.dp))
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MidnightBlue,
                    textAlign = TextAlign.Center,
                )
            }

            Spacer(Modifier.height(18.dp))

            // --- Letter tiles ------------------------------------------
            TileRow(
                tiles = session.tiles,
                usedIds = usedIds,
                onTileClick = onTile,
            )

            Spacer(Modifier.height(24.dp))

            // --- Actions -----------------------------------------------
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                KelmaButton(
                    text = when {
                        hintOwned -> "Indice obtenu"
                        coins < GameConfig.HINT_COST -> "Indice · ${GameConfig.HINT_COST}"
                        else -> "Indice · ${GameConfig.HINT_COST}"
                    },
                    onClick = onAskHint,
                    variant = ButtonVariant.Accent,
                    enabled = phase == PuzzlePhase.INPUT && !hintOwned && coins >= GameConfig.HINT_COST,
                    modifier = Modifier.weight(1f),
                    leading = { HintIcon(tint = MidnightBlue) },
                )
                KelmaButton(
                    text = "Valider",
                    onClick = {
                        if (phase == PuzzlePhase.INCORRECT) onRetry() else onSubmit()
                    },
                    variant = ButtonVariant.Primary,
                    enabled = allFilled && (phase == PuzzlePhase.INPUT || phase == PuzzlePhase.INCORRECT),
                    modifier = Modifier.weight(1f),
                )
            }

            if (phase == PuzzlePhase.INCORRECT) {
                Spacer(Modifier.height(10.dp))
                KelmaButton(
                    text = "Réessayer",
                    onClick = onRetry,
                    variant = ButtonVariant.Secondary,
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            Spacer(Modifier.height(28.dp))
        }
    }

    // --- Correct-answer celebration (requirement 4E) --------------------
    AnimatedVisibility(
        visible = phase == PuzzlePhase.CORRECT,
        enter = fadeIn(tween(250)) + scaleIn(initialScale = 0.9f, animationSpec = tween(250)),
        exit = fadeOut(tween(150)),
    ) {
        CorrectOverlay(
            puzzle = puzzle,
            gameState = gameState,
            onNextPuzzle = onNextPuzzle,
            onCompletion = onCompletion,
            onBack = onBack,
        )
    }
}

@Composable
private fun CorrectOverlay(
    puzzle: com.example.kelma216.data.model.Puzzle,
    gameState: com.example.kelma216.data.model.GameState,
    onNextPuzzle: (String) -> Unit,
    onCompletion: (String) -> Unit,
    onBack: () -> Unit,
) {
    val govId = puzzle.governorateId
    val next = GameRules.firstAvailablePuzzle(govId, gameState)
    val scale by animateFloatAsState(targetValue = 1f, animationSpec = tween(300), label = "pop")

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MidnightBlue.copy(alpha = 0.96f))
            .padding(24.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier.scale(scale),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .clip(RoundedCornerShape(50))
                    .background(SuccessGreen),
                contentAlignment = Alignment.Center,
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(50)),
                    contentAlignment = Alignment.Center,
                ) {
                    CheckIcon(tint = Color.White)
                }
            }

            Text(
                text = "Bravo !",
                style = MaterialTheme.typography.displayLarge,
                color = Yellow,
            )

            Text(
                text = puzzle.answer,
                style = MaterialTheme.typography.headlineLarge,
                color = Color(0xFFFFF4E8),
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                CoinDotReward(amount = puzzle.reward)
            }

            PatternStrip(height = 16.dp)

            // Cultural explanation, with its verification status made explicit.
            if (!puzzle.explanation.isNullOrBlank()) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Text(
                        text = puzzle.explanation,
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color(0xFFFFF4E8),
                        textAlign = TextAlign.Center,
                    )
                    if (puzzle.explanationStatus != VerificationStatus.VERIFIED) {
                        Text(
                            text = "* Contenu culturel à vérifier",
                            style = MaterialTheme.typography.bodyMedium,
                            fontStyle = FontStyle.Italic,
                            color = Yellow.copy(alpha = 0.85f),
                            textAlign = TextAlign.Center,
                        )
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            KelmaButton(
                text = if (next != null) "Continuer" else "Voir le bilan du niveau",
                onClick = {
                    if (next != null) onNextPuzzle(next.id) else onCompletion(govId)
                },
                variant = ButtonVariant.Primary,
                modifier = Modifier.fillMaxWidth(),
            )
            KelmaButton(
                text = "Retour au niveau",
                onClick = onBack,
                variant = ButtonVariant.Outline,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun CoinDotReward(amount: Int) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        com.example.kelma216.ui.components.CoinDot(size = 26)
        Text(
            text = "+$amount pièces",
            style = MaterialTheme.typography.titleLarge,
            color = Yellow,
        )
    }
}

@Composable
private fun HintConfirmDialog(
    cost: Int,
    coins: Int,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    androidx.compose.ui.window.Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(22.dp))
                .background(Color.White)
                .padding(22.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            HintIcon(tint = Turquoise)
            Text(
                text = "Acheter un indice ?",
                style = MaterialTheme.typography.titleLarge,
                color = MidnightBlue,
            )
            Text(
                text = "Coût : $cost pièces (solde : $coins)",
                style = MaterialTheme.typography.bodyMedium,
                color = MidnightBlue.copy(alpha = 0.75f),
                textAlign = TextAlign.Center,
            )
            KelmaButton(
                text = "Acheter",
                onClick = onConfirm,
                variant = ButtonVariant.Accent,
                modifier = Modifier.fillMaxWidth(),
            )
            KelmaButton(
                text = "Annuler",
                onClick = onDismiss,
                variant = ButtonVariant.Outline,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}
