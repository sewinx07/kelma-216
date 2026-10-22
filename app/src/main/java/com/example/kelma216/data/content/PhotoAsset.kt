package com.example.kelma216.data.content

import androidx.annotation.DrawableRes

/**
 * Provenance record for one downloaded clue photo.
 *
 * Produced by the offline photo-sourcing pipeline (see `tools/source_photos.ps1`) and
 * consumed by [PuzzleCatalog] to attach a drawable to a puzzle. Nothing here is invented
 * at runtime: if a puzzle has no entry, it simply shows a text-only prompt.
 *
 * @property answer the puzzle answer this photo illustrates.
 * @property drawableRes compiled resource id for the downloaded image.
 * @property sourceUrl the Wikimedia Commons file page, for attribution.
 * @property license short licence name (e.g. "CC BY-SA 4.0", "Public domain", "CC0").
 * @property artist author credit, if the licence requires attribution.
 */
data class PhotoAsset(
    val answer: String,
    @param:DrawableRes val drawableRes: Int,
    val sourceUrl: String,
    val license: String,
    val artist: String? = null,
)
