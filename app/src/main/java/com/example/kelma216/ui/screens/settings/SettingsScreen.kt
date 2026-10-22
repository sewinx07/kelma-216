package com.example.kelma216.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.kelma216.ui.components.ButtonVariant
import com.example.kelma216.ui.components.KelmaButton
import com.example.kelma216.ui.components.KelmaCard
import com.example.kelma216.ui.components.KelmaTopBar
import com.example.kelma216.ui.theme.MidnightBlue
import com.example.kelma216.ui.theme.Turquoise
import com.example.kelma216.viewmodel.GameViewModel

/**
 * Settings (presentation pages 7 and 20): sound/music toggles, app information, and a
 * way back to the home screen.
 *
 * Audio playback is not implemented yet, so the toggles clearly say so rather than
 * pretending to work. They persist the preference so they apply once audio exists.
 */
@Composable
fun SettingsScreen(
    viewModel: GameViewModel,
    onBack: () -> Unit,
) {
    val gameState by viewModel.gameState.collectAsState()
    var showResetDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .safeDrawingPadding(),
    ) {
        KelmaTopBar(title = "Paramètres", onBack = onBack)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            // --- Audio -------------------------------------------------
            KelmaCard(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Jeu",
                    style = MaterialTheme.typography.titleLarge,
                    color = MidnightBlue,
                )
                Spacer(Modifier.height(4.dp))
                SettingToggle(
                    title = "Sons",
                    subtitle = "À venir — la lecture audio n'est pas encore implémentée",
                    checked = gameState.settings.soundEnabled,
                    onCheckedChange = viewModel::setSoundEnabled,
                )
                SettingToggle(
                    title = "Musique",
                    subtitle = "À venir — la lecture audio n'est pas encore implémentée",
                    checked = gameState.settings.musicEnabled,
                    onCheckedChange = viewModel::setMusicEnabled,
                )
            }

            // --- About -------------------------------------------------
            KelmaCard(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "À propos",
                    style = MaterialTheme.typography.titleLarge,
                    color = MidnightBlue,
                )
                Spacer(Modifier.height(8.dp))
                AboutLine("Application", "KELMA 216 — Le jeu des mots tunisiens")
                AboutLine("Slogan", "« Explore la Tunisie, un gouvernorat et un mot à la fois. »")
                AboutLine("Structure", "24 gouvernorats × 9 énigmes = 216 mots")
                AboutLine("Version", "Prototype 1.0")
                Spacer(Modifier.height(6.dp))
                Text(
                    text = "Contenu culturel : les explications sont marquées " +
                        "« à vérifier » tant qu'elles n'ont pas été validées.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MidnightBlue.copy(alpha = 0.7f),
                    textAlign = TextAlign.Start,
                )
            }

            // --- Reset -------------------------------------------------
            KelmaCard(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Sauvegarde",
                    style = MaterialTheme.typography.titleLarge,
                    color = MidnightBlue,
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "La progression est enregistrée localement et survit au redémarrage.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MidnightBlue.copy(alpha = 0.7f),
                )
                Spacer(Modifier.height(10.dp))
                KelmaButton(
                    text = "Réinitialiser la progression",
                    onClick = { showResetDialog = true },
                    variant = ButtonVariant.Outline,
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            KelmaButton(
                text = "Retour à l'accueil",
                onClick = onBack,
                variant = ButtonVariant.Primary,
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(Modifier.height(24.dp))
        }
    }

    if (showResetDialog) {
        androidx.compose.ui.window.Dialog(onDismissRequest = { showResetDialog = false }) {
            Column(
                modifier = Modifier
                    .clip(RoundedCornerShape(22.dp))
                    .background(Color.White)
                    .padding(22.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                Text(
                    text = "Tout réinitialiser ?",
                    style = MaterialTheme.typography.titleLarge,
                    color = MidnightBlue,
                )
                Text(
                    text = "Vos énigmes résolues, pièces et niveaux ouverts seront effacés.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MidnightBlue.copy(alpha = 0.75f),
                    textAlign = TextAlign.Center,
                )
                KelmaButton(
                    text = "Réinitialiser",
                    onClick = {
                        viewModel.resetProgress()
                        showResetDialog = false
                    },
                    variant = ButtonVariant.Primary,
                    modifier = Modifier.fillMaxWidth(),
                )
                KelmaButton(
                    text = "Annuler",
                    onClick = { showResetDialog = false },
                    variant = ButtonVariant.Outline,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}

@Composable
private fun SettingToggle(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.titleMedium, color = MidnightBlue)
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MidnightBlue.copy(alpha = 0.65f),
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedTrackColor = Turquoise,
                checkedThumbColor = MidnightBlue,
            ),
        )
    }
}

@Composable
private fun AboutLine(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp)) {
        Text(
            text = "$label : ",
            style = MaterialTheme.typography.bodyMedium,
            color = MidnightBlue.copy(alpha = 0.65f),
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = MidnightBlue,
        )
    }
}
