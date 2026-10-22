package com.example.kelma216.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.kelma216.ui.theme.MidnightBlue
import com.example.kelma216.ui.theme.Turquoise
import com.example.kelma216.ui.theme.TunisianRed
import com.example.kelma216.ui.theme.Yellow

/** Visual weight of a call-to-action button. */
enum class ButtonVariant { Primary, Secondary, Accent, Outline }

/**
 * Rounded, chunky call-to-action button with large touch targets (requirement 3:
 * "large readable typography and comfortable touch targets").
 */
@Composable
fun KelmaButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: ButtonVariant = ButtonVariant.Primary,
    enabled: Boolean = true,
    contentPadding: PaddingValues = PaddingValues(horizontal = 24.dp, vertical = 16.dp),
    leading: (@Composable RowScope.() -> Unit)? = null,
) {
    val colors = when (variant) {
        ButtonVariant.Primary -> ButtonDefaults.buttonColors(
            containerColor = TunisianRed,
            contentColor = Color(0xFFFFF4E8),
            disabledContainerColor = TunisianRed.copy(alpha = 0.4f),
            disabledContentColor = Color(0xFFFFF4E8).copy(alpha = 0.7f),
        )
        ButtonVariant.Secondary -> ButtonDefaults.buttonColors(
            containerColor = Turquoise,
            contentColor = MidnightBlue,
            disabledContainerColor = Turquoise.copy(alpha = 0.4f),
            disabledContentColor = MidnightBlue.copy(alpha = 0.6f),
        )
        ButtonVariant.Accent -> ButtonDefaults.buttonColors(
            containerColor = Yellow,
            contentColor = MidnightBlue,
            disabledContainerColor = Yellow.copy(alpha = 0.4f),
            disabledContentColor = MidnightBlue.copy(alpha = 0.6f),
        )
        ButtonVariant.Outline -> ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            contentColor = MidnightBlue,
            disabledContainerColor = Color.Transparent,
            disabledContentColor = MidnightBlue.copy(alpha = 0.4f),
        )
    }

    if (variant == ButtonVariant.Outline) {
        OutlinedButton(
            onClick = onClick,
            modifier = modifier.height(56.dp),
            enabled = enabled,
            shape = MaterialTheme.shapes.medium,
            border = BorderStroke(2.dp, MidnightBlue),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = MidnightBlue),
            contentPadding = contentPadding,
        ) {
            leading?.invoke(this)
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge,
                textAlign = TextAlign.Center,
            )
        }
    } else {
        Button(
            onClick = onClick,
            modifier = modifier.height(56.dp),
            enabled = enabled,
            shape = MaterialTheme.shapes.medium,
            colors = colors,
            contentPadding = contentPadding,
        ) {
            leading?.invoke(this)
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 4.dp),
            )
        }
    }
}
