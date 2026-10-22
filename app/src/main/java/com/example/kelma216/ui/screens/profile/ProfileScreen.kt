package com.example.kelma216.ui.screens.profile

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
import com.example.kelma216.data.content.PuzzleCatalog
import com.example.kelma216.domain.GameConfig
import com.example.kelma216.domain.GameRules
import com.example.kelma216.ui.components.ButtonVariant
import com.example.kelma216.ui.components.CoinBadge
import com.example.kelma216.ui.components.KelmaButton
import com.example.kelma216.ui.components.KelmaCard
import com.example.kelma216.ui.components.KelmaTopBar
import com.example.kelma216.ui.components.MiniProgress
import com.example.kelma216.ui.components.StarIcon
import com.example.kelma216.ui.theme.MidnightBlue
import com.example.kelma216.ui.theme.Turquoise
import com.example.kelma216.ui.theme.Yellow
import com.example.kelma216.viewmodel.GameViewModel

/**
 * Profile / progression (presentation pages 7 and 20): completed puzzles, total progress,
 * current coin balance — all read from the persisted save.
 */
@Composable
fun ProfileScreen(
    viewModel: GameViewModel,
    onBack: () -> Unit,
) {
    val gameState by viewModel.gameState.collectAsState()

    val completed = GameRules.completedCount(gameState)
    val written = GameRules.totalWrittenPuzzles()
    val solvedGovernorates = Governorates.all.count {
        GameRules.isGovernorateComplete(it.id, gameState)
    }
    val unlockedCount = GameRules.unlockedGovernorateIds(gameState).size
    // Progress shown against the full 216 target from the presentation.
    val overallFraction = if (GameConfig.TOTAL_PUZZLES > 0) {
        completed.toFloat() / GameConfig.TOTAL_PUZZLES
    } else 0f

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .safeDrawingPadding(),
    ) {
        KelmaTopBar(
            title = "Profil",
            onBack = onBack,
            trailing = { CoinBadge(coins = gameState.coins) },
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            // Coin balance hero
            KelmaCard(
                modifier = Modifier.fillMaxWidth(),
                containerColor = Yellow,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                ) {
                    com.example.kelma216.ui.components.CoinDot(size = 44)
                    Column {
                        Text(
                            text = "${gameState.coins}",
                            style = MaterialTheme.typography.headlineLarge,
                            color = MidnightBlue,
                        )
                        Text(
                            text = "pièces disponibles",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MidnightBlue.copy(alpha = 0.75f),
                        )
                    }
                }
            }

            // Overall progress against 216
            KelmaCard(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Progression totale",
                    style = MaterialTheme.typography.titleLarge,
                    color = MidnightBlue,
                )
                Spacer(Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Stat("Énigmes résolues", "$completed / ${GameConfig.TOTAL_PUZZLES}")
                    Stat("Gouvernorats terminés", "$solvedGovernorates / ${GameConfig.GOVERNORATE_COUNT}")
                }
                Spacer(Modifier.height(12.dp))
                MiniProgress(fraction = overallFraction)
                Spacer(Modifier.height(6.dp))
                if (written < GameConfig.TOTAL_PUZZLES) {
                    Text(
                        text = "Contenu disponible actuel : $written énigmes sur 216 " +
                            "(le reste sera ajouté avec des contenus vérifiés).",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MidnightBlue.copy(alpha = 0.7f),
                        textAlign = TextAlign.Center,
                    )
                }
            }

            // Quick stats
            KelmaCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    StatChip(
                        value = "$unlockedCount",
                        label = "niveaux ouverts",
                        modifier = Modifier.weight(1f),
                    )
                    StatChip(
                        value = "${gameState.hintedPuzzleIds.size}",
                        label = "indices utilisés",
                        modifier = Modifier.weight(1f),
                    )
                }
            }

            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun Stat(label: String, value: String) {
    Column {
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            color = MidnightBlue,
            fontWeight = FontWeight.Bold,
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MidnightBlue.copy(alpha = 0.65f),
        )
    }
}

@Composable
private fun StatChip(value: String, label: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Turquoise.copy(alpha = 0.18f))
            .padding(14.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(contentAlignment = Alignment.Center) {
            StarIcon(
                modifier = Modifier.size(20.dp),
                tint = Yellow,
            )
        }
        Spacer(Modifier.height(6.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.headlineMedium,
            color = MidnightBlue,
            fontSize = 24.sp,
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MidnightBlue.copy(alpha = 0.7f),
            textAlign = TextAlign.Center,
        )
    }
}
