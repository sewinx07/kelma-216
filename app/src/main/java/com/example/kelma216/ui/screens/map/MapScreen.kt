package com.example.kelma216.ui.screens.map

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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.kelma216.domain.GameRules
import com.example.kelma216.domain.GovernorateProgress
import com.example.kelma216.ui.components.BackIcon
import com.example.kelma216.ui.components.CheckIcon
import com.example.kelma216.ui.components.CoinBadge
import com.example.kelma216.ui.components.LockIcon
import com.example.kelma216.ui.components.PuzzleDots
import com.example.kelma216.ui.theme.Cream
import com.example.kelma216.ui.theme.LockedGrey
import com.example.kelma216.ui.theme.MidnightBlue
import com.example.kelma216.ui.theme.SoftCream
import com.example.kelma216.ui.theme.Turquoise
import com.example.kelma216.ui.theme.TunisianRed
import com.example.kelma216.ui.theme.Yellow
import com.example.kelma216.viewmodel.GameViewModel

/**
 * Governorate selection / "carte" (presentation pages 6, 7, 22).
 *
 * Because no geographically accurate illustrated map asset exists yet, this is an honest,
 * clean selectable grid of all 24 governorates in level order rather than invented
 * geography (requirement 4B). The limitation is stated on screen.
 */
@Composable
fun MapScreen(
    viewModel: GameViewModel,
    onBack: () -> Unit,
    onOpenGovernorate: (String) -> Unit,
) {
    val gameState by viewModel.gameState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .safeDrawingPadding(),
    ) {
        // Top bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surface)
                    .clickable(onClick = onBack),
                contentAlignment = Alignment.Center,
            ) {
                BackIcon(tint = MidnightBlue)
            }
            Column(modifier = Modifier.weight(1f)) {
                Text("Carte de Tunisie", style = MaterialTheme.typography.titleLarge, color = MidnightBlue)
                Text(
                    text = "24 gouvernorats · 9 énigmes chacun",
                    style = MaterialTheme.typography.labelMedium,
                    color = MidnightBlue.copy(alpha = 0.7f),
                )
            }
            CoinBadge(coins = gameState.coins)
        }

        // Honest limitation note (presentation page 22 says the detailed map is to be finalised).
        Text(
            text = "Carte sélective : le découpage géographique illustré des 24 gouvernorats sera " +
                "finalisé en maquette. Sélectionnez un gouvernorat pour jouer.",
            style = MaterialTheme.typography.bodyMedium,
            color = MidnightBlue.copy(alpha = 0.75f),
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp),
        )

        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 150.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 12.dp),
        ) {
            items(Governorates.all, key = { it.id }) { gov ->
                val unlocked = GameRules.isGovernorateUnlocked(gov.id, gameState)
                val progress = GameRules.governorateProgress(gov.id, gameState)
                GovernorateCard(
                    governorate = gov,
                    unlocked = unlocked,
                    progress = progress,
                    onClick = { if (unlocked) onOpenGovernorate(gov.id) },
                )
            }
        }
    }
}

@Composable
private fun GovernorateCard(
    governorate: Governorate,
    unlocked: Boolean,
    progress: GovernorateProgress,
    onClick: () -> Unit,
) {
    val complete = progress.complete
    val container = when {
        complete -> Yellow
        unlocked -> SoftCream
        else -> Cream
    }
    val borderColor = when {
        complete -> Yellow
        unlocked -> Turquoise
        else -> LockedGrey
    }

    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(18.dp))
            .background(container)
            .border(2.dp, borderColor, RoundedCornerShape(18.dp))
            .clickable(enabled = unlocked, onClick = onClick)
            .padding(14.dp)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Order number
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(if (unlocked) MidnightBlue else LockedGrey),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "%02d".format(governorate.order),
                    color = Color(0xFFFFF4E8),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
            if (complete) {
                CheckIcon(tint = MidnightBlue)
            } else if (!unlocked) {
                LockIcon(tint = MidnightBlue.copy(alpha = 0.7f))
            }
        }

        Text(
            text = governorate.name,
            style = MaterialTheme.typography.titleMedium,
            color = MidnightBlue,
            maxLines = 1,
        )

        if (!unlocked) {
            Text(
                text = "À débloquer",
                style = MaterialTheme.typography.labelMedium,
                color = MidnightBlue.copy(alpha = 0.6f),
            )
        } else if (progress.totalPlayable == 0) {
            Text(
                text = "Contenu à venir",
                style = MaterialTheme.typography.labelMedium,
                color = MidnightBlue.copy(alpha = 0.6f),
            )
        } else {
            Text(
                text = "${progress.displaySolved} / ${progress.displayTotal}",
                style = MaterialTheme.typography.labelMedium,
                color = MidnightBlue.copy(alpha = 0.8f),
            )
            PuzzleDots(
                totalSlots = progress.displayTotal,
                solvedCount = progress.displaySolved,
                hasContent = progress.totalPlayable > 0,
            )
        }
    }
}
