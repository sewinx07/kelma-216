package com.example.kelma216.ui.components

import android.content.res.AssetManager
import android.graphics.BitmapFactory
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.produceState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.kelma216.data.content.GovernorateImages
import com.example.kelma216.ui.theme.MidnightBlue
import com.example.kelma216.ui.theme.SoftCream
import com.example.kelma216.ui.theme.Turquoise
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException

/**
 * Loads the hand-managed cover for a governorate from the APK assets. Returns null when
 * the player has not dropped a photo yet (never throws, never borrows another region's
 * image).
 */
@Composable
fun rememberGovernorateCover(governorateId: String): ImageBitmap? {
    val assets = LocalContext.current.assets
    return produceState<ImageBitmap?>(initialValue = null, governorateId) {
        value = withContext(Dispatchers.IO) { loadCover(assets, governorateId) }
    }.value
}

private fun loadCover(assets: AssetManager, governorateId: String): ImageBitmap? {
    for (path in GovernorateImages.candidatePaths(governorateId)) {
        try {
            assets.open(path).use { stream ->
                return BitmapFactory.decodeStream(stream)?.asImageBitmap()
            }
        } catch (_: IOException) {
            // Try the next supported extension.
        }
    }
    return null
}

/**
 * Cover photo with rounded corners, cropped to fill. Falls back to a neutral illustrated
 * placeholder so the layout stays stable while photos are still being added.
 */
@Composable
fun GovernorateCover(
    governorateId: String,
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 16.dp,
    contentDescription: String? = null,
) {
    val cover = rememberGovernorateCover(governorateId)
    Box(modifier = modifier.clip(RoundedCornerShape(cornerRadius))) {
        if (cover != null) {
            Image(
                bitmap = cover,
                contentDescription = contentDescription,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )
        } else {
            CoverPlaceholder(Modifier.fillMaxSize())
        }
    }
}

/** Neutral placeholder: soft cream/turquoise gradient with a simple landscape glyph. */
@Composable
private fun CoverPlaceholder(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.background(
            Brush.linearGradient(listOf(SoftCream, Turquoise.copy(alpha = 0.35f))),
        ),
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            val cx = w / 2f
            val cy = h / 2f
            val r = minOf(w, h) * 0.16f

            // Sun
            drawCircle(color = Color.White.copy(alpha = 0.85f), radius = r * 0.55f, center = Offset(cx + r * 0.9f, cy - r * 0.7f))
            // Mountains
            val path = androidx.compose.ui.graphics.Path().apply {
                moveTo(cx - r * 2.1f, cy + r * 0.9f)
                lineTo(cx - r * 0.5f, cy - r * 0.3f)
                lineTo(cx + r * 0.4f, cy + r * 0.35f)
                lineTo(cx + r * 1.1f, cy - r * 0.15f)
                lineTo(cx + r * 2.1f, cy + r * 0.9f)
                close()
            }
            drawPath(path, color = MidnightBlue.copy(alpha = 0.18f))
            drawPath(
                path,
                color = MidnightBlue.copy(alpha = 0.30f),
                style = Stroke(width = 2f),
            )
        }
    }
}
