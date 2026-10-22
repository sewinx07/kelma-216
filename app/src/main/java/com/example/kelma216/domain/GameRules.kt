package com.example.kelma216.domain

import com.example.kelma216.data.content.Governorates
import com.example.kelma216.data.content.PuzzleCatalog
import com.example.kelma216.data.model.GameState
import com.example.kelma216.data.model.Governorate
import com.example.kelma216.data.model.Puzzle

/**
 * Pure game rules — no Android, no UI, no storage. Unit-testable.
 * Implements requirement section 5 consistently in one place.
 */
object GameRules {

    /** Governorates are unlocked in order; the first is always open. */
    fun isGovernorateUnlocked(governorateId: String, state: GameState): Boolean =
        state.isGovernorateUnlocked(governorateId)

    /**
     * Progress inside one governorate, as solved playable puzzles / total playable puzzles.
     *
     * Note on the unlock rule (requirement 5.11 says "nine puzzles ... unless the presentation
     * specifies a different rule"): the presentation marks 9/9 as "une règle proposée à valider"
     * (page 17). While only part of a governorate's content is written, we treat a governorate as
     * complete once every *playable* puzzle in it is solved. With full content (9 playable) this is
     * exactly 9/9; with a partial prototype it stays playable instead of dead-locking.
     */
    fun governorateProgress(
        governorateId: String,
        state: GameState,
    ): GovernorateProgress {
        val total = PuzzleCatalog.playableCount(governorateId)
        val solved = PuzzleCatalog.playableByGovernorateId(governorateId)
            .count { state.isPuzzleCompleted(it.id) }
        val displayTotal = GameConfig.PUZZLES_PER_GOVERNORATE
        return GovernorateProgress(
            governorateId = governorateId,
            solvedPlayable = solved,
            totalPlayable = total,
            displaySolved = solved,
            displayTotal = displayTotal,
            complete = total > 0 && solved == total,
        )
    }

    /** True when the governorate has content and all of it is solved. */
    fun isGovernorateComplete(governorateId: String, state: GameState): Boolean =
        governorateProgress(governorateId, state).complete

    /** The next governorate in presentation order, if any. */
    fun nextGovernorate(currentId: String): Governorate? {
        val current = Governorates.byId(currentId) ?: return null
        return Governorates.byOrder(current.order + 1)
    }

    /**
     * Which governorates should be unlocked given completions. Starting from the already
     * unlocked set, each completed governorate opens its successor. Never unlocks backwards.
     */
    fun unlockedGovernorateIds(state: GameState): Set<String> {
        val unlocked = mutableSetOf<String>()
        val first = GameConfig.FIRST_GOVERNORATE_ID
        unlocked += first
        var currentId: String? = first
        // Walk forward while each governorate is complete.
        while (currentId != null) {
            if (!isGovernorateComplete(currentId, state)) break
            val next = nextGovernorate(currentId) ?: break
            unlocked += next.id
            currentId = next.id
        }
        // Preserve anything already granted (defensive, keeps progress monotonic).
        unlocked += state.unlockedGovernorateIds
        return unlocked
    }

    /**
     * Attempts to complete a puzzle and award coins.
     *
     * Returns the state AFTER applying the reward. Awards exactly once (requirement 5.2 and 5.10):
     * if the puzzle is already completed, coins are untouched.
     */
    fun completePuzzle(state: GameState, puzzle: Puzzle): GameState {
        if (state.isPuzzleCompleted(puzzle.id)) return state // no double award
        val withPuzzle = state.copy(
            completedPuzzleIds = state.completedPuzzleIds + puzzle.id,
            coins = state.coins + puzzle.reward,
        )
        return withPuzzle.copy(unlockedGovernorateIds = unlockedGovernorateIds(withPuzzle))
    }

    /** Is this candidate string an accepted answer? Case/whitespace tolerant. */
    fun isCorrect(puzzle: Puzzle, candidate: String): Boolean {
        val normalized = puzzle.normalize(candidate)
        return puzzle.acceptedAnswers().any { it.equals(normalized, ignoreCase = true) }
    }

    /** Would buying the standard hint be affordable right now? */
    fun canAffordHint(state: GameState): Boolean =
        state.canAfford(GameConfig.HINT_COST) && GameConfig.HINT_COST >= 0

    /**
     * Buys a hint. Returns null when it cannot be charged (already owned, or cannot afford)
     * so the caller never deducts coins in those cases (requirements 5.5, 5.6, 5.7).
     */
    fun purchaseHint(state: GameState, puzzle: Puzzle): GameState? {
        if (!puzzle.playable) return null
        if (state.isPuzzleHinted(puzzle.id)) return null // already owned: never charge twice
        if (!state.canAfford(GameConfig.HINT_COST)) return null // insufficient funds
        return state.copy(
            coins = state.coins - GameConfig.HINT_COST,
            hintedPuzzleIds = state.hintedPuzzleIds + puzzle.id,
        )
    }

    /** Total solved puzzles across all content. */
    fun completedCount(state: GameState): Int = state.completedPuzzleIds.size

    /**
     * Where "Continuer" should take the player: the first unlocked governorate that still
     * has unsolved playable puzzles, else the first unlocked one.
     */
    fun resumeGovernorateId(state: GameState): String {
        val unlocked = unlockedGovernorateIds(state)
        return Governorates.all
            .filter { it.id in unlocked }
            .firstOrNull { !isGovernorateComplete(it.id, state) }
            ?.id
            ?: Governorates.all.firstOrNull { it.id in unlocked }?.id
            ?: GameConfig.FIRST_GOVERNORATE_ID
    }

    /**
     * Within a governorate, puzzles open in order (presentation page 19: 1/9 → suivante → 9/9).
     * A position is available once every *playable* puzzle before it is solved; positions with
     * no written content are treated as "à venir" rather than blocking progression.
     */
    fun isPuzzlePositionAvailable(governorateId: String, position: Int, state: GameState): Boolean =
        PuzzleCatalog.playableByGovernorateId(governorateId)
            .filter { it.position < position }
            .all { state.isPuzzleCompleted(it.id) }

    /** The next playable, available, unsolved puzzle in a governorate (where play starts). */
    fun firstAvailablePuzzle(governorateId: String, state: GameState): Puzzle? =
        PuzzleCatalog.playableByGovernorateId(governorateId)
            .filterNot { state.isPuzzleCompleted(it.id) }
            .firstOrNull { isPuzzlePositionAvailable(governorateId, it.position, state) }

    /** Overall progress denominator: 216 when content is complete, else written content. */
    fun totalWrittenPuzzles(): Int = PuzzleCatalog.all().count { it.playable }
}

/** Snapshot of one governorate's completion state. */
data class GovernorateProgress(
    val governorateId: String,
    val solvedPlayable: Int,
    val totalPlayable: Int,
    /** X/9 as displayed to the player (presentation page 9: "question actuelle / 9"). */
    val displaySolved: Int,
    val displayTotal: Int,
    val complete: Boolean,
)
