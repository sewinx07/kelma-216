package com.example.kelma216.domain

import com.example.kelma216.data.model.Puzzle
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/** Unit tests for the fixed 10-tile tray generator. */
class TileGeneratorTest {

    private fun puzzle(id: String, answer: String) = Puzzle(
        id = id,
        governorateId = "tunis",
        position = 1,
        answer = answer,
        reward = 10,
    )

    @Test
    fun alwaysBuildsExactlyTenTiles() {
        assertEquals(10, TileGenerator.build(puzzle("a", "BRIK")).size)
        assertEquals(10, TileGenerator.build(puzzle("b", "MAKAROUNA")).size)
        assertEquals(10, TileGenerator.build(puzzle("c", "UN")).size)
    }

    @Test
    fun tilesContainEveryAnswerLetter() {
        val answer = "MAKAROUNA"
        val tiles = TileGenerator.build(puzzle("m", answer))
        val trayLetters = tiles.map { it.char }.toMutableList()
        answer.toList().forEach { needed ->
            assertTrue("tray must contain '$needed'", trayLetters.remove(needed))
        }
    }

    @Test
    fun tileIdsAreUnique() {
        val tiles = TileGenerator.build(puzzle("u", "BRIK"))
        assertEquals(tiles.size, tiles.map { it.id }.toSet().size)
    }

    @Test
    fun repeatedLettersNeedDistinctTiles() {
        // "BABA" needs two B and two A tiles, all individually unique.
        val tiles = TileGenerator.build(puzzle("r", "BABA"))
        assertEquals(10, tiles.size)
        assertEquals(2, tiles.count { it.char == 'B' })
        assertEquals(2, tiles.count { it.char == 'A' })
        assertEquals(10, tiles.map { it.id }.toSet().size)
    }

    @Test
    fun samePuzzleIdProducesSameTrayOrder() {
        val first = TileGenerator.build(puzzle("stable", "BRIK"))
        val second = TileGenerator.build(puzzle("stable", "BRIK"))
        assertEquals(first, second)
    }

    @Test
    fun answerLongerThanTenLettersIsRejected() {
        // Never silently truncate: an over-long answer yields no tiles.
        assertTrue(TileGenerator.build(puzzle("long", "ABCDEFGHIJK")).isEmpty())
    }
}
