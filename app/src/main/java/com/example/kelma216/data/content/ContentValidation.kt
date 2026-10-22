package com.example.kelma216.data.content

import com.example.kelma216.domain.GameConfig
import com.example.kelma216.domain.TileGenerator

/**
 * Validates puzzle + governorate content so bad data is caught at development time
 * instead of crashing or misbehaving at runtime (requirement 6: "Validate puzzle data
 * and handle missing images or incomplete content gracefully").
 */
object ContentValidation {

    /** Returns a list of human-readable problems; empty list means content is valid. */
    fun validate(): List<String> {
        val problems = mutableListOf<String>()

        // --- governorates -------------------------------------------------
        val governorates = Governorates.all
        if (governorates.size != GameConfig.GOVERNORATE_COUNT) {
            problems += "Expected ${GameConfig.GOVERNORATE_COUNT} governorates, found ${governorates.size}"
        }
        governorates.groupBy { it.id }.filterValues { it.size > 1 }.keys.forEach {
            problems += "Duplicate governorate id: $it"
        }
        governorates.forEach { gov ->
            if (gov.order !in 1..GameConfig.GOVERNORATE_COUNT) {
                problems += "Governorate ${gov.id} has out-of-range order ${gov.order}"
            }
        }

        // --- puzzles --------------------------------------------------------
        PuzzleCatalog.all().forEach { puzzle ->
            if (Governorates.byId(puzzle.governorateId) == null) {
                problems += "Puzzle ${puzzle.id} references unknown governorate ${puzzle.governorateId}"
            }
            if (puzzle.position !in 1..GameConfig.PUZZLES_PER_GOVERNORATE) {
                problems += "Puzzle ${puzzle.id} has out-of-range position ${puzzle.position}"
            }
            if (puzzle.playable && puzzle.answer.isBlank()) {
                problems += "Playable puzzle ${puzzle.id} has a blank answer"
            }
            if (puzzle.playable && puzzle.reward < 0) {
                problems += "Puzzle ${puzzle.id} has a negative reward"
            }
            if (puzzle.playable && puzzle.clueRes == null && puzzle.clueCaption.isNullOrBlank()) {
                problems += "Playable puzzle ${puzzle.id} has neither a clue photo nor a caption"
            }
            if (puzzle.playable) {
                val answer = puzzle.answer.trim()
                if (answer.length > TileGenerator.TILE_COUNT) {
                    problems += "Puzzle ${puzzle.id} answer '${puzzle.answer}' is longer than ${TileGenerator.TILE_COUNT} letters"
                }
                val tiles = TileGenerator.build(puzzle)
                if (tiles.size != TileGenerator.TILE_COUNT) {
                    problems += "Puzzle ${puzzle.id} does not build ${TileGenerator.TILE_COUNT} tiles (got ${tiles.size})"
                }
                if (tiles.map { it.id }.toSet().size != tiles.size) {
                    problems += "Puzzle ${puzzle.id} has duplicate tile ids"
                }
            }
        }

        // stable ids must be unique
        PuzzleCatalog.all().groupBy { it.id }.filterValues { it.size > 1 }.keys.forEach {
            problems += "Duplicate puzzle id: $it"
        }

        // no two puzzles of the same governorate may share a position
        PuzzleCatalog.all()
            .groupBy { it.governorateId to it.position }
            .filterValues { it.size > 1 }
            .keys.forEach { (gov, pos) ->
                problems += "Governorate $gov has multiple puzzles at position $pos"
            }

        return problems
    }

    /** Fails loudly in debug so bad content never ships silently. */
    fun requireValid() {
        val problems = validate()
        check(problems.isEmpty()) {
            "KELMA 216 content problems:\n" + problems.joinToString("\n")
        }
    }
}
