package com.example.kelma216.domain

/**
 * Central place for every game rule constant, so no price or reward is scattered
 * through components (requirement 5.12).
 *
 * TEMPORARY GAME-DESIGN VALUES.
 * The presentation explicitly leaves these open (page 11: "Prix de l'indice, valeur des
 * récompenses ... à définir puis tester"; page 18 lists "Nombre de pièces gagné, prix d'aide,
 * temps et ordre des régions" as open parameters). The values below are prototype
 * decisions and must be validated with players before release.
 */
object GameConfig {

    /** Structure fixed by the presentation: 24 governorates x 9 puzzles = 216 (pages 3, 6). */
    const val GOVERNORATE_COUNT = 24
    const val PUZZLES_PER_GOVERNORATE = 9
    const val TOTAL_PUZZLES = GOVERNORATE_COUNT * PUZZLES_PER_GOVERNORATE // 216

    /** First governorate (presentation page 6, order 01 Tunis) is open from the start. */
    const val FIRST_GOVERNORATE_ID = "tunis"

    /** Starting balance so a new player can afford a first hint. (Prototype value.) */
    const val STARTING_COINS = 50

    /** Coins awarded once per solved puzzle. (Prototype value.) */
    const val CORRECT_REWARD = 10

    /** Coins charged for one paid hint, only on confirmed purchase. (Prototype value.) */
    const val HINT_COST = 10
}
