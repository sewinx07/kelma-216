package com.example.kelma216.ui.screens.governorate

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kelma216.data.content.Governorates
import com.example.kelma216.data.model.Governorate
import com.example.kelma216.domain.GameConfig
import com.example.kelma216.domain.GameRules
import com.example.kelma216.ui.components.ButtonVariant
import com.example.kelma216.ui.components.CheckIcon
import com.example.kelma216.ui.components.CoinBadge
import com.example.kelma216.ui.components.KelmaButton
import com.example.kelma216.ui.components.KelmaTopBar
import com.example.kelma216.ui.components.LockIcon
import com.example.kelma216.ui.components.PatternStrip
import com.example.kelma216.ui.components.ProgressBar
import com.example.kelma216.ui.theme.Cream
import com.example.kelma216.ui.theme.LockedGrey
import com.example.kelma216.ui.theme.MidnightBlue
import com.example.kelma216.ui.theme.SoftCream
import com.example.kelma216.ui.theme.Turquoise
import com.example.kelma216.ui.theme.TunisianRed
import com.example.kelma216.ui.theme.Yellow
import com.example.kelma216.viewmodel.GameViewModel

/**
 * Level / governorate presentation (presentation pages 7 and 9): name, themed header,
 * nine puzzle positions with status, a progress indicator, and a start button.
 */
@Composable
fun GovernorateScreen(
    viewModel: GameViewModel,
    govId: String,
    onBack: () -> Unit,
    onStartPuzzle: (String) -> Unit,
    onCompletion: (String) -> Unit,
) {
    val gameState by viewModel.gameState.collectAsState()
    val governorate: Governorate = Governorates.byId(govId) ?: run {
        // Unknown id: bail out gracefully to the map rather than crash.
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .safeDrawingPadding()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text("Gouvernorat introuvable", color = MidnightBlue)
            KelmaButton(text = "Retour à la carte", onClick = onBack, variant = ButtonVariant.Primary)
        }
        return
    }

    val progress = GameRules.governorateProgress(govId, gameState)
    val nextPuzzle = GameRules.firstAvailablePuzzle(govId, gameState)
    val unlocked = GameRules.isGovernorateUnlocked(govId, gameState)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .safeDrawingPadding(),
    ) {
        KelmaTopBar(
            title = governorate.name,
            onBack = onBack,
            trailing = { CoinBadge(coins = gameState.coins) },
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // Themed header
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(22.dp))
                    .background(TunisianRed)
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = "Niveau %02d".format(governorate.order),
                    style = MaterialTheme.typography.labelLarge,
                    color = Yellow,
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = governorate.name,
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color(0xFFFFF4E8),
                    textAlign = TextAlign.Center,
                )
                Spacer(Modifier.height(10.dp))
                PatternStrip(height = 16.dp, colors = listOf(Yellow, Turquoise, Color(0xFFFFF4E8)))
            }

            Spacer(Modifier.height(18.dp))

            ProgressBar(
                solved = progress.displaySolved,
                total = progress.displayTotal,
                modifier = Modifier.fillMaxWidth(),
            )

            if (progress.totalPlayable == 0) {
                Spacer(Modifier.height(24.dp))
                Text(
                    text = "Le contenu de ce gouvernorat n'est pas encore disponible.\n" +
                        "Revenez après l'ajout des énigmes.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MidnightBlue.copy(alpha = 0.75f),
                    textAlign = TextAlign.Center,
                )
                Spacer(Modifier.height(16.dp))
                return@Column
            }

            Spacer(Modifier.height(20.dp))

            // Nine puzzle positions
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                (1..GameConfig.PUZZLES_PER_GOVERNORATE).forEach { position ->
                    val puzzle = com.example.kelma216.data.content.PuzzleCatalog
                        .puzzleAt(govId, position)
                    val completed = puzzle != null && gameState.isPuzzleCompleted(puzzle.id)
                    val available = puzzle != null &&
                        !completed &&
                        unlocked &&
                        GameRules.isPuzzlePositionAvailable(govId, position, gameState)

                    PuzzleSlotRow(
                        position = position,
                        label = puzzle?.answer?.let { if (completed) it else null },
                        hasContent = puzzle != null,
                        completed = completed,
                        available = available,
                        onClick = { if (available) puzzle?.let { onStartPuzzle(it.id) } },
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            if (progress.complete) {
                KelmaButton(
                    text = "Voir le bilan du niveau",
                    onClick = { onCompletion(govId) },
                    variant = ButtonVariant.Accent,
                    modifier = Modifier.fillMaxWidth(),
                    leading = { CheckIcon(tint = MidnightBlue) },
                )
            } else {
                KelmaButton(
                    text = if (nextPuzzle != null) "Commencer l'énigme" else "Aucune énigme disponible",
                    onClick = { nextPuzzle?.let { onStartPuzzle(it.id) } },
                    variant = ButtonVariant.Primary,
                    enabled = nextPuzzle != null,
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            Spacer(Modifier.height(28.dp))
        }
    }
}

@Composable
private fun PuzzleSlotRow(
    position: Int,
    label: String?,
    hasContent: Boolean,
    completed: Boolean,
    available: Boolean,
    onClick: () -> Unit,
) {
    val container = when {
        completed -> Yellow
        available -> SoftCream
        else -> Cream
    }
    val border = when {
        completed -> Yellow
        available -> Turquoise
        else -> LockedGrey
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(container)
            .border(2.dp, border, RoundedCornerShape(16.dp))
            .clickable(enabled = available, onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(MidnightBlue),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = position.toString(),
                color = Color(0xFFFFF4E8),
                fontWeight = FontWeight.Bold,
            )
        }

        Column(modifier = Modifier.weight(1f)) {
            val title = when {
                completed -> "Résolu"
                !hasContent -> "À venir"
                available -> "Enigme $position"
                else -> "Verrouillé"
            }
            Text(title, style = MaterialTheme.typography.titleMedium, color = MidnightBlue)
            val sub = when {
                completed -> label ?: "Terminé"
                !hasContent -> "Énigme à venir"
                available -> "Disponible"
                else -> "Termine l'énigme précédente"
            }
            Text(
                text = sub,
                style = MaterialTheme.typography.bodyMedium,
                color = MidnightBlue.copy(alpha = 0.65f),
            )
        }

        when {
            completed -> CheckIcon(tint = MidnightBlue)
            available -> Text(
                text = "Jouer",
                style = MaterialTheme.typography.labelLarge,
                color = Turquoise,
            )
            !hasContent -> Text(
                text = "…",
                style = MaterialTheme.typography.titleLarge,
                color = LockedGrey,
            )
            else -> LockIcon(tint = MidnightBlue.copy(alpha = 0.6f))
        }
    }
}
