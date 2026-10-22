package com.example.kelma216.data.model

/**
 * One selectable letter tile in the puzzle tray.
 *
 * Every tile carries a unique [id] so repeated letters in an answer are handled
 * correctly: each physical tile can only be used once, even when the answer needs
 * the same letter twice.
 *
 * @property id stable, unique-within-puzzle identifier (e.g. "tile-0").
 * @property char the letter shown on the tile.
 */
data class LetterTile(
    val id: String,
    val char: Char,
)
