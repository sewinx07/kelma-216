package com.example.kelma216.domain

import com.example.kelma216.data.model.LetterTile
import com.example.kelma216.data.model.Puzzle
import kotlin.random.Random

/**
 * Builds the tray of letter tiles for one puzzle.
 *
 * Requirement: the tray always holds exactly [TILE_COUNT] tiles. The tiles are the
 * letters of the answer plus enough decoy letters to reach the count. Every tile has a
 * unique [LetterTile.id] so an answer containing the same letter twice still needs two
 * physical tiles.
 *
 * If an answer is longer than [TILE_COUNT] characters the tray cannot hold it and the
 * puzzle content itself must be fixed; [build] then returns an empty list and callers
 * treat the puzzle as not playable (it is flagged in [ContentValidation], never silently
 * truncated).
 */
object TileGenerator {

    /** The fixed number of selectable tiles shown for every puzzle. */
    const val TILE_COUNT = 10

    /** Letters used as decoys when the answer is shorter than [TILE_COUNT]. */
    private val DISTRACTOR_POOL: List<Char> = "AEILNOPRSTUVBCDFGKMQXYZ".toList()

    /**
     * Builds a deterministic tray for [puzzle]. The same puzzle id always yields the same
     * tray order, so rotating the screen never reshuffles the letters.
     */
    fun build(puzzle: Puzzle): List<LetterTile> {
        val answer = puzzle.answer.trim().uppercase()
        if (answer.isEmpty() || answer.length > TILE_COUNT) return emptyList()

        val rng = Random(puzzle.id.hashCode())
        val letters = mutableListOf<Char>()
        letters += answer.toList()
        while (letters.size < TILE_COUNT) {
            letters += DISTRACTOR_POOL[rng.nextInt(DISTRACTOR_POOL.size)]
        }
        return letters
            .shuffled(rng)
            .mapIndexed { index, char -> LetterTile(id = "${puzzle.id}-t$index", char = char) }
    }
}
