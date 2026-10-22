package com.example.kelma216.domain

import com.example.kelma216.data.content.ContentValidation
import com.example.kelma216.data.content.Governorates
import com.example.kelma216.data.content.PuzzleCatalog
import com.example.kelma216.data.content.WordContent
import com.example.kelma216.data.model.GameState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Unit tests for the KELMA 216 core rules: rewards, hints, unlock progression and content
 * integrity. These run on the host JVM (no Android needed) because [GameRules] is pure Kotlin.
 */
class GameRulesTest {

    private val brik = PuzzleCatalog.byId("tunis-01-brik")
        ?: error("BRIK puzzle missing from catalog")

    /** Every puzzle of the first governorate, in order. */
    private val tunis = PuzzleCatalog.playableByGovernorateId("tunis")

    /** A state where every puzzle of a governorate is solved. */
    private fun completing(governorateId: String): GameState =
        PuzzleCatalog.playableByGovernorateId(governorateId)
            .fold(GameState()) { state, puzzle -> GameRules.completePuzzle(state, puzzle) }

    // --- answers -----------------------------------------------------------

    @Test
    fun correctAnswerAccepted_caseInsensitively() {
        assertTrue(GameRules.isCorrect(brik, "BRIK"))
        assertTrue(GameRules.isCorrect(brik, "brik"))
        assertTrue(GameRules.isCorrect(brik, "  Brik  "))
    }

    @Test
    fun wrongAnswerRejected() {
        assertFalse(GameRules.isCorrect(brik, "brick"))
        assertFalse(GameRules.isCorrect(brik, "briek"))
        assertFalse(GameRules.isCorrect(brik, ""))
    }

    // --- rewards (exactly once) --------------------------------------------

    @Test
    fun rewardGrantedOnCompletion() {
        val before = GameState(coins = 50)
        val after = GameRules.completePuzzle(before, brik)
        assertEquals(50 + brik.reward, after.coins)
        assertTrue(after.isPuzzleCompleted(brik.id))
    }

    @Test
    fun rewardNotGrantedTwice() {
        val first = GameRules.completePuzzle(GameState(coins = 50), brik)
        val second = GameRules.completePuzzle(first, brik)
        // Coins must not increase a second time.
        assertEquals(first.coins, second.coins)
        assertEquals(1, second.completedPuzzleIds.size)
    }

    // --- hints --------------------------------------------------------------

    @Test
    fun hintSuccess_deductsCostOnce() {
        val state = GameState(coins = 50)
        val result = GameRules.purchaseHint(state, brik)
        assertNotNull(result)
        assertEquals(50 - GameConfig.HINT_COST, result!!.coins)
        assertTrue(result.isPuzzleHinted(brik.id))
    }

    @Test
    fun hintNotCharged_whenCannotAfford() {
        val poor = GameState(coins = GameConfig.HINT_COST - 1)
        val result = GameRules.purchaseHint(poor, brik)
        assertNull(result)
        // Source state is never mutated, so balance stays intact and non-negative.
        assertEquals(GameConfig.HINT_COST - 1, poor.coins)
        assertFalse(poor.isPuzzleHinted(brik.id))
    }

    @Test
    fun hintNotCharged_whenAlreadyOwned() {
        val owned = GameState(coins = 50, hintedPuzzleIds = setOf(brik.id))
        val result = GameRules.purchaseHint(owned, brik)
        assertNull(result)
        assertEquals(50, owned.coins)
    }

    @Test
    fun balanceNeverGoesNegative_onExactFunds() {
        val exact = GameState(coins = GameConfig.HINT_COST)
        assertTrue(GameRules.canAffordHint(exact))
        val after = GameRules.purchaseHint(exact, brik)
        assertNotNull(after)
        assertEquals(0, after!!.coins)
    }

    @Test
    fun cannotAffordWithZeroBalance() {
        val broke = GameState(coins = 0)
        assertFalse(GameRules.canAffordHint(broke))
        assertNull(GameRules.purchaseHint(broke, brik))
    }

    // --- unlock progression -------------------------------------------------

    @Test
    fun firstGovernorateIsInitiallyUnlocked() {
        val state = GameState()
        assertTrue(GameRules.isGovernorateUnlocked("tunis", state))
        assertFalse(GameRules.isGovernorateUnlocked("ariana", state))
    }

    @Test
    fun completingGovernorateUnlocksNext() {
        val state = completing("tunis") // all 9 Tunis puzzles solved
        assertTrue(GameRules.isGovernorateComplete("tunis", state))
        assertTrue(state.isGovernorateUnlocked("ariana"))
        // Ariana is now unlocked but not completed, so Ben Arous stays locked.
        assertFalse(state.isGovernorateUnlocked("ben_arous"))
    }

    @Test
    fun completingAllGovernoratesUnlocksAll() {
        var state = GameState()
        Governorates.all.forEach { gov ->
            PuzzleCatalog.playableByGovernorateId(gov.id).forEach { puzzle ->
                state = GameRules.completePuzzle(state, puzzle)
            }
        }
        val unlocked = GameRules.unlockedGovernorateIds(state)
        assertEquals(24, unlocked.size)
        assertEquals(216, state.completedPuzzleIds.size)
    }

    @Test
    fun tunisProgressTracksPlayableContent() {
        val before = GameRules.governorateProgress("tunis", GameState())
        assertEquals(9, before.totalPlayable)
        assertEquals(0, before.solvedPlayable)
        assertFalse(before.complete)

        val after = GameRules.governorateProgress("tunis", completing("tunis"))
        assertEquals(9, after.solvedPlayable)
        assertTrue(after.complete)
    }

    @Test
    fun puzzlesUnlockInOrderWithinGovernorate() {
        var state = GameState()
        // First puzzle is available; the second is not until the first is solved.
        assertTrue(GameRules.isPuzzlePositionAvailable("tunis", 1, state))
        assertFalse(GameRules.isPuzzlePositionAvailable("tunis", 2, state))

        state = GameRules.completePuzzle(state, tunis.first())
        assertTrue(GameRules.isPuzzlePositionAvailable("tunis", 2, state))
        assertFalse(GameRules.isPuzzlePositionAvailable("tunis", 3, state))
    }

    // --- content ------------------------------------------------------------

    @Test
    fun contentValidationFindsNoProblems() {
        val problems = ContentValidation.validate()
        assertTrue(problems.joinToString("\n"), problems.isEmpty())
    }

    @Test
    fun catalogHasExactly216PlayablePuzzles() {
        assertEquals(216, PuzzleCatalog.all().size)
        assertEquals(216, PuzzleCatalog.all().count { it.playable })
        assertEquals(216, WordContent.totalCount)
    }

    @Test
    fun everyGovernorateHasNinePuzzles() {
        Governorates.all.forEach { gov ->
            assertEquals(
                "governorate ${gov.id}",
                9,
                PuzzleCatalog.playableCount(gov.id),
            )
        }
    }

    @Test
    fun everyPuzzleIdIsUniqueAndWellFormed() {
        val ids = PuzzleCatalog.all().map { it.id }
        assertEquals(ids.size, ids.toSet().size)
        PuzzleCatalog.all().forEach { puzzle ->
            assertTrue("answer too long: ${puzzle.answer}", puzzle.answer.length <= TileGenerator.TILE_COUNT)
            assertTrue("answer must be letters: ${puzzle.answer}", puzzle.answer.all { it.isLetter() })
            assertTrue(puzzle.reward > 0)
        }
    }

    @Test
    fun brikPuzzleIsWellFormed() {
        assertEquals("BRIK", brik.answer)
        assertEquals("tunis", brik.governorateId)
        assertEquals(1, brik.position)
        assertTrue(brik.playable)
        assertTrue(brik.reward > 0)
        assertNotNull(brik.explanation)
    }

    @Test
    fun has24GovernoratesInPresentationOrder() {
        assertEquals(24, Governorates.all.size)
        assertEquals("Tunis", Governorates.all.first().name)
        assertEquals("Kébili", Governorates.all.last().name)
        assertEquals(1, Governorates.all.first().order)
        assertEquals(24, Governorates.all.last().order)
    }

    @Test
    fun resumeTargetsFirstUnfinishedUnlockedLevel() {
        assertEquals("tunis", GameRules.resumeGovernorateId(GameState()))
    }

    @Test
    fun totalProgressDenominatorIs216() {
        assertEquals(216, GameRules.totalWrittenPuzzles())
    }
}
