package com.example.kelma216.data.model

import com.example.kelma216.domain.GameConfig

/**
 * The whole persisted player state. This is the single source of truth that gets
 * written to / read from local storage between app launches (presentation page 7:
 * "Sauvegarde — Conserver les réponses validées et la progression").
 *
 * Everything here is keyed by stable IDs, never by array index.
 *
 * @property completedPuzzleIds puzzle IDs already solved. A puzzle in this set can
 *   never award coins again (idempotent completion).
 * @property hintedPuzzleIds puzzle IDs whose paid hint was already bought, so a player
 *   is never charged twice for the same hint.
 * @property coins current balance. Never allowed to go negative.
 * @property unlockedGovernorateIds governorates the player may open.
 * @property settings sound/music preferences.
 */
data class GameState(
    val completedPuzzleIds: Set<String> = emptySet(),
    val hintedPuzzleIds: Set<String> = emptySet(),
    val coins: Int = GameConfig.STARTING_COINS,
    val unlockedGovernorateIds: Set<String> = setOf(GameConfig.FIRST_GOVERNORATE_ID),
    val settings: GameSettings = GameSettings(),
) {
    fun isPuzzleCompleted(puzzleId: String): Boolean = puzzleId in completedPuzzleIds

    fun isPuzzleHinted(puzzleId: String): Boolean = puzzleId in hintedPuzzleIds

    fun isGovernorateUnlocked(governorateId: String): Boolean =
        governorateId in unlockedGovernorateIds

    fun canAfford(amount: Int): Boolean = coins >= amount
}
