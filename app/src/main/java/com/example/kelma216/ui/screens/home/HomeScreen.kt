package com.example.kelma216.ui.screens.home

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kelma216.R
import com.example.kelma216.data.content.Governorates
import com.example.kelma216.domain.GameConfig
import com.example.kelma216.domain.GameRules
import com.example.kelma216.ui.components.ButtonVariant
import com.example.kelma216.ui.components.CoinBadge
import com.example.kelma216.ui.components.KelmaButton
import com.example.kelma216.ui.components.KelmaCard
import com.example.kelma216.ui.components.MiniProgress
import com.example.kelma216.ui.components.PlayIcon
import com.example.kelma216.ui.components.SettingsIcon
import com.example.kelma216.ui.theme.Cream
import com.example.kelma216.ui.theme.MidnightBlue
import com.example.kelma216.ui.theme.SoftCream
import com.example.kelma216.ui.theme.Turquoise
import com.example.kelma216.ui.theme.TunisianRed
import com.example.kelma216.ui.theme.Yellow
import com.example.kelma216.viewmodel.GameViewModel

/**
 * Accueil — brand hero, one clear path into the game, and a live read-out of real
 * progress. Layout follows the supplied reference: cream canvas, logo hero, red
 * call-to-action, then progress and secondary actions.
 */
@Composable
fun HomeScreen(
    viewModel: GameViewModel,
    onPlay: () -> Unit,
    onContinue: (String) -> Unit,
    onProfile: () -> Unit,
    onSettings: () -> Unit,
) {
    val gameState by viewModel.gameState.collectAsState()
    val hasProgress = gameState.completedPuzzleIds.isNotEmpty()

    val completed = GameRules.completedCount(gameState)
    val solvedGovernorates = Governorates.all.count {
        GameRules.isGovernorateComplete(it.id, gameState)
    }
    val fraction = if (GameConfig.TOTAL_PUZZLES > 0) {
        completed.toFloat() / GameConfig.TOTAL_PUZZLES
    } else 0f

    // Lightweight entrance animation (single alpha/offset driver, no extra recompositions).
    val appear = remember { Animatable(0f) }
    LaunchedEffect(Unit) { appear.animateTo(1f, tween(durationMillis = 550)) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Cream, SoftCream, Cream),
                ),
            ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .verticalScroll(rememberScrollState())
                .alpha(appear.value)
                .padding(bottom = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            HomeTopBar(
                coins = gameState.coins,
                onProfile = onProfile,
                onSettings = onSettings,
            )

            Spacer(Modifier.height(10.dp))

            // --- Brand hero -------------------------------------------------
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 28.dp),
                contentAlignment = Alignment.Center,
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(190.dp)
                        .clip(MaterialTheme.shapes.large)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    Color.White.copy(alpha = 0.9f),
                                    Cream.copy(alpha = 0.4f),
                                ),
                            ),
                        ),
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.logo_kelma),
                        contentDescription = "KELMA 216 — Le jeu des mots tunisiens",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            Text(
                text = "Le jeu des mots tunisiens",
                style = MaterialTheme.typography.titleMedium,
                color = Turquoise,
                textAlign = TextAlign.Center,
            )

            Spacer(Modifier.height(22.dp))

            // --- Primary actions -------------------------------------------
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 28.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                KelmaButton(
                    text = "Jouer",
                    onClick = onPlay,
                    variant = ButtonVariant.Primary,
                    modifier = Modifier.fillMaxWidth(),
                    leading = { PlayIcon(tint = Cream) },
                )

                if (hasProgress) {
                    KelmaButton(
                        text = "Continuer",
                        onClick = { onContinue(GameRules.resumeGovernorateId(gameState)) },
                        variant = ButtonVariant.Secondary,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }

            Spacer(Modifier.height(18.dp))

            // --- Live progress ---------------------------------------------
            KelmaCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 28.dp),
                containerColor = Color.White,
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "Ta progression",
                        style = MaterialTheme.typography.titleMedium,
                        color = MidnightBlue,
                    )
                    Text(
                        text = "$completed / ${GameConfig.TOTAL_PUZZLES}",
                        style = MaterialTheme.typography.titleMedium,
                        color = TunisianRed,
                    )
                }
                Spacer(Modifier.height(10.dp))
                MiniProgress(fraction = fraction)
                Spacer(Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    MiniStat(
                        value = "$solvedGovernorates",
                        label = "gouvernorats\nterminés",
                        modifier = Modifier.weight(1f),
                    )
                    MiniStat(
                        value = "${gameState.coins}",
                        label = "pièces",
                        modifier = Modifier.weight(1f),
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            Text(
                text = "24 gouvernorats · 9 énigmes · 216 mots à découvrir",
                style = MaterialTheme.typography.bodyMedium,
                color = MidnightBlue.copy(alpha = 0.55f),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 24.dp),
            )
        }
    }
}

@Composable
private fun HomeTopBar(
    coins: Int,
    onProfile: () -> Unit,
    onSettings: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        // Profile avatar
        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(CircleShape)
                .background(
                    Brush.linearGradient(listOf(Turquoise, MidnightBlue)),
                )
                .clickable(onClick = onProfile),
            contentAlignment = Alignment.Center,
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_launcher_foreground),
                contentDescription = "Profil",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape),
            )
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            CoinBadge(coins = coins)
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(Yellow)
                    .clickable(onClick = onSettings)
                    .semantics { contentDescription = "Paramètres" },
                contentAlignment = Alignment.Center,
            ) {
                SettingsIcon(tint = MidnightBlue)
            }
        }
    }
}

@Composable
private fun MiniStat(
    value: String,
    label: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .clip(MaterialTheme.shapes.medium)
            .background(Turquoise.copy(alpha = 0.14f))
            .padding(vertical = 10.dp, horizontal = 8.dp),
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
