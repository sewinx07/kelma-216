package com.example.kelma216.ui.screens.profile

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kelma216.R
import com.example.kelma216.data.content.Governorates
import com.example.kelma216.data.content.PuzzleCatalog
import com.example.kelma216.domain.GameConfig
import com.example.kelma216.domain.GameRules
import com.example.kelma216.ui.components.CoinBadge
import com.example.kelma216.ui.components.CoinDot
import com.example.kelma216.ui.components.KelmaCard
import com.example.kelma216.ui.components.KelmaTopBar
import com.example.kelma216.ui.components.MiniProgress
import com.example.kelma216.ui.components.StarIcon
import com.example.kelma216.ui.theme.Cream
import com.example.kelma216.ui.theme.LockedGrey
import com.example.kelma216.ui.theme.MidnightBlue
import com.example.kelma216.ui.theme.SoftCream
import com.example.kelma216.ui.theme.Turquoise
import com.example.kelma216.ui.theme.TunisianRed
import com.example.kelma216.ui.theme.Yellow
import com.example.kelma216.viewmodel.GameViewModel
import kotlin.math.min

/**
 * Profil — everything shown is derived from the persisted save: coins, solved puzzles,
 * finished governorates, hints used and progress. Milestones unlock from real counters,
 * so nothing here is invented.
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
    val currentLevel = min(solvedGovernorates + 1, GameConfig.GOVERNORATE_COUNT)
    val overallFraction = if (GameConfig.TOTAL_PUZZLES > 0) {
        completed.toFloat() / GameConfig.TOTAL_PUZZLES
    } else 0f
    val animatedFraction by animateFloatAsState(
        targetValue = overallFraction,
        animationSpec = tween(700),
        label = "overall",
    )

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
            // --- Identity header -------------------------------------------
            KelmaCard(modifier = Modifier.fillMaxWidth(), containerColor = Color.White) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .size(78.dp)
                            .clip(CircleShape)
                            .background(Brush.linearGradient(listOf(Turquoise, MidnightBlue))),
                        contentAlignment = Alignment.Center,
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_launcher_foreground),
                            contentDescription = "Avatar",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(66.dp)
                                .clip(CircleShape),
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Joueur KELMA",
                            style = MaterialTheme.typography.titleLarge,
                            color = MidnightBlue,
                        )
                        Spacer(Modifier.height(2.dp))
                        Text(
                            text = if (solvedGovernorates >= GameConfig.GOVERNORATE_COUNT) {
                                "Toute la Tunisie parcourue"
                            } else {
                                "Niveau $currentLevel · ${Governorates.byOrder(currentLevel)?.name ?: ""}"
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            color = Turquoise,
                        )
                    }
                }
            }

            // --- Coins + overall progress ---------------------------------
            KelmaCard(modifier = Modifier.fillMaxWidth(), containerColor = Yellow) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                ) {
                    CoinDot(size = 44)
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

            KelmaCard(modifier = Modifier.fillMaxWidth(), containerColor = Color.White) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        text = "Progression totale",
                        style = MaterialTheme.typography.titleMedium,
                        color = MidnightBlue,
                    )
                    Text(
                        text = "$completed / ${GameConfig.TOTAL_PUZZLES}",
                        style = MaterialTheme.typography.titleMedium,
                        color = TunisianRed,
                        fontWeight = FontWeight.Bold,
                    )
                }
                Spacer(Modifier.height(10.dp))
                MiniProgress(fraction = animatedFraction)
                Spacer(Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    StatTile(
                        value = "$completed",
                        label = "énigmes\nrésolues",
                        accent = TunisianRed,
                        modifier = Modifier.weight(1f),
                    )
                    StatTile(
                        value = "$solvedGovernorates/${GameConfig.GOVERNORATE_COUNT}",
                        label = "gouvernorats",
                        accent = Turquoise,
                        modifier = Modifier.weight(1f),
                    )
                    StatTile(
                        value = "${gameState.hintedPuzzleIds.size}",
                        label = "indices\nutilisés",
                        accent = Yellow,
                        modifier = Modifier.weight(1f),
                    )
                }
                Spacer(Modifier.height(10.dp))
                Text(
                    text = "$unlockedCount gouvernorats ouverts · " +
                        "$written énigmes disponibles sur ${GameConfig.TOTAL_PUZZLES}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MidnightBlue.copy(alpha = 0.6f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            // --- Real milestones -------------------------------------------
            KelmaCard(modifier = Modifier.fillMaxWidth(), containerColor = Color.White) {
                Text(
                    text = "Succès",
                    style = MaterialTheme.typography.titleMedium,
                    color = MidnightBlue,
                )
                Spacer(Modifier.height(10.dp))
                AchievementRow(
                    title = "Premier mot",
                    detail = "Résoudre 1 énigme",
                    unlocked = completed >= 1,
                )
                AchievementRow(
                    title = "Dix sur dix",
                    detail = "Résoudre 10 énigmes",
                    unlocked = completed >= 10,
                )
                AchievementRow(
                    title = "Un gouvernorat complet",
                    detail = "Terminer 1 gouvernorat",
                    unlocked = solvedGovernorates >= 1,
                )
                AchievementRow(
                    title = "Demi-Tunisie",
                    detail = "Terminer 12 gouvernorats",
                    unlocked = solvedGovernorates >= 12,
                )
                AchievementRow(
                    title = "KELMA 216",
                    detail = "Résoudre les 216 énigmes",
                    unlocked = completed >= GameConfig.TOTAL_PUZZLES,
                )
            }

            Text(
                text = "Progression sauvegardée sur cet appareil.",
                style = MaterialTheme.typography.labelMedium,
                color = MidnightBlue.copy(alpha = 0.5f),
                modifier = Modifier.padding(bottom = 16.dp),
            )
        }
    }
}

@Composable
private fun StatTile(
    value: String,
    label: String,
    accent: Color,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .clip(MaterialTheme.shapes.medium)
            .background(accent.copy(alpha = 0.14f))
            .padding(vertical = 12.dp, horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.headlineSmall,
            color = MidnightBlue,
            fontSize = 22.sp,
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MidnightBlue.copy(alpha = 0.7f),
            textAlign = TextAlign.Center,
            lineHeight = 13.sp,
        )
    }
}

@Composable
private fun AchievementRow(
    title: String,
    detail: String,
    unlocked: Boolean,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .alpha(if (unlocked) 1f else 0.45f),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(if (unlocked) Yellow else LockedGrey.copy(alpha = 0.4f)),
            contentAlignment = Alignment.Center,
        ) {
            StarIcon(
                modifier = Modifier.size(20.dp),
                tint = if (unlocked) MidnightBlue else MidnightBlue.copy(alpha = 0.5f),
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                color = MidnightBlue,
            )
            Text(
                text = if (unlocked) detail else "$detail — verrouillé",
                style = MaterialTheme.typography.labelMedium,
                color = MidnightBlue.copy(alpha = 0.65f),
            )
        }
    }
}
