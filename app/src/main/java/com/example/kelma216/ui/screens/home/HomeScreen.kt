package com.example.kelma216.ui.screens.home

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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kelma216.domain.GameRules
import com.example.kelma216.ui.components.ButtonVariant
import com.example.kelma216.ui.components.CoinBadge
import com.example.kelma216.ui.components.KelmaButton
import com.example.kelma216.ui.components.PatternStrip
import com.example.kelma216.ui.components.PlayIcon
import com.example.kelma216.ui.components.SettingsIcon
import com.example.kelma216.ui.theme.MidnightBlue
import com.example.kelma216.ui.theme.Turquoise
import com.example.kelma216.ui.theme.TunisianRed
import com.example.kelma216.ui.theme.Yellow
import com.example.kelma216.viewmodel.GameViewModel

/**
 * Home / accueil (presentation page 7): logo, "Jouer", profile access, coin balance,
 * settings, plus "Continuer" when a saved game exists.
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .safeDrawingPadding()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // Top bar: profile on the left, coins + settings on the right.
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            KelmaButton(
                text = "Profil",
                onClick = onProfile,
                variant = ButtonVariant.Outline,
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                CoinBadge(coins = gameState.coins)
                SettingsCircle(onClick = onSettings)
            }
        }

        Spacer(Modifier.height(8.dp))

        // Logo / title block
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .clip(CircleShape)
                    .background(TunisianRed),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "216",
                    color = Color(0xFFFFF4E8),
                    fontSize = 34.sp,
                    style = MaterialTheme.typography.headlineMedium,
                )
            }
            Spacer(Modifier.height(14.dp))
            Text(
                text = "KELMA 216",
                style = MaterialTheme.typography.displayLarge,
                color = MidnightBlue,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = "Le jeu des mots tunisiens",
                style = MaterialTheme.typography.titleMedium,
                color = Turquoise,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(10.dp))
            PatternStrip(height = 20.dp)
            Spacer(Modifier.height(10.dp))
            Text(
                text = "« Explore la Tunisie, un gouvernorat et un mot à la fois. »",
                style = MaterialTheme.typography.bodyLarge,
                color = MidnightBlue.copy(alpha = 0.85f),
                textAlign = TextAlign.Center,
            )
        }

        Spacer(Modifier.height(28.dp))

        // Actions
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            KelmaButton(
                text = "Jouer",
                onClick = onPlay,
                variant = ButtonVariant.Primary,
                modifier = Modifier.fillMaxWidth(),
                leading = { PlayIcon(tint = Color(0xFFFFF4E8)) },
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

        Spacer(Modifier.height(32.dp))

        // Small informative footer
        Text(
            text = "24 gouvernorats · 9 énigmes · 216 mots à découvrir",
            style = MaterialTheme.typography.bodyMedium,
            color = MidnightBlue.copy(alpha = 0.6f),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 24.dp),
        )
    }
}

@Composable
private fun SettingsCircle(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(56.dp)
            .clip(CircleShape)
            .background(Yellow)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        SettingsIcon(tint = MidnightBlue)
    }
}
