package com.example.kelma216.ui.screens.completion

import androidx.compose.foundation.background
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
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kelma216.data.content.Governorates
import com.example.kelma216.data.content.PuzzleCatalog
import com.example.kelma216.domain.GameConfig
import com.example.kelma216.domain.GameRules
import com.example.kelma216.ui.components.ButtonVariant
import com.example.kelma216.ui.components.CheckIcon
import com.example.kelma216.ui.components.CoinDot
import com.example.kelma216.ui.components.KelmaButton
import com.example.kelma216.ui.components.KelmaCard
import com.example.kelma216.ui.components.KelmaTopBar
import com.example.kelma216.ui.components.PatternStrip
import com.example.kelma216.ui.components.PuzzleDots
import com.example.kelma216.ui.theme.MidnightBlue
import com.example.kelma216.ui.theme.SuccessGreen
import com.example.kelma216.ui.theme.Turquoise
import com.example.kelma216.ui.theme.TunisianRed
import com.example.kelma216.ui.theme.Yellow
import com.example.kelma216.viewmodel.GameViewModel

/**
 * End-of-level recap (presentation pages 9 and 17): completion state, puzzles solved,
 * rewards earned, and the next governorate when it has become available.
 */
@Composable
fun CompletionScreen(
    viewModel: GameViewModel,
    govId: String,
    onBackToMap: () -> Unit,
    onOpenGovernorate: (String) -> Unit,
) {
    val gameState by viewModel.gameState.collectAsState()
    val governorate = Governorates.byId(govId)

    if (governorate == null) {
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
            Spacer(Modifier.height(16.dp))
            KelmaButton(text = "Carte", onClick = onBackToMap, variant = ButtonVariant.Primary)
        }
        return
    }

    val progress = GameRules.governorateProgress(govId, gameState)
    val solvedPuzzles = PuzzleCatalog.playableByGovernorateId(govId)
        .filter { gameState.isPuzzleCompleted(it.id) }
    val earned = solvedPuzzles.sumOf { it.reward }
    val next = GameRules.nextGovernorate(govId)
    val nextUnlocked = next != null && GameRules.isGovernorateUnlocked(next.id, gameState)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .safeDrawingPadding(),
    ) {
        KelmaTopBar(title = "Bilan du niveau", onBack = onBackToMap)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            // Header
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(22.dp))
                    .background(SuccessGreen)
                    .padding(22.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Box(
                    modifier = Modifier
                        .size(74.dp)
                        .clip(RoundedCornerShape(50))
                        .background(Color.White),
                    contentAlignment = Alignment.Center,
                ) {
                    CheckIcon(tint = SuccessGreen)
                }
                Spacer(Modifier.height(10.dp))
                Text(
                    text = if (progress.complete) "Niveau terminé !" else "En cours",
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color.White,
                )
                Text(
                    text = governorate.name,
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White.copy(alpha = 0.9f),
                )
            }

            // Solved + dots
            KelmaCard(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Text(
                        text = "${progress.displaySolved} / ${progress.displayTotal}",
                        style = MaterialTheme.typography.displayLarge,
                        color = MidnightBlue,
                        fontSize = 40.sp,
                    )
                    Text(
                        text = "énigmes résolues",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MidnightBlue.copy(alpha = 0.7f),
                    )
                    PuzzleDots(
                        totalSlots = progress.displayTotal,
                        solvedCount = progress.displaySolved,
                        hasContent = progress.totalPlayable > 0,
                    )
                    if (progress.totalPlayable < GameConfig.PUZZLES_PER_GOVERNORATE) {
                        Text(
                            text = "Contenu disponible : ${progress.totalPlayable} énigmes sur 9 " +
                                "(les autres seront ajoutées).",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MidnightBlue.copy(alpha = 0.65f),
                            textAlign = TextAlign.Center,
                        )
                    }
                }
            }

            // Rewards earned
            KelmaCard(modifier = Modifier.fillMaxWidth(), containerColor = Yellow) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    CoinDot(size = 34)
                    Spacer(Modifier.height(0.dp))
                    Text(
                        text = "  +$earned pièces gagnées dans ce niveau",
                        style = MaterialTheme.typography.titleMedium,
                        color = MidnightBlue,
                    )
                }
            }

            PatternStrip(height = 16.dp)

            // Actions
            if (next != null) {
                KelmaButton(
                    text = if (nextUnlocked) "Niveau suivant : ${next.name}" else "Niveau suivant",
                    onClick = { if (nextUnlocked) onOpenGovernorate(next.id) else onBackToMap() },
                    variant = ButtonVariant.Primary,
                    enabled = nextUnlocked,
                    modifier = Modifier.fillMaxWidth(),
                )
                if (!nextUnlocked) {
                    Text(
                        text = "Termine les énigmes de ${governorate.name} pour ouvrir la suite.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MidnightBlue.copy(alpha = 0.7f),
                        textAlign = TextAlign.Center,
                    )
                }
            } else {
                Text(
                    text = "Tu as terminé le parcours disponible !",
                    style = MaterialTheme.typography.bodyMedium,
                    fontStyle = FontStyle.Italic,
                    color = MidnightBlue.copy(alpha = 0.75f),
                    textAlign = TextAlign.Center,
                )
            }

            KelmaButton(
                text = "Retour à la carte",
                onClick = onBackToMap,
                variant = ButtonVariant.Secondary,
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(Modifier.height(24.dp))
        }
    }
}
