package com.example.kelma216.ui.navigation

/** Central place for navigation route strings (type safety is kept by helpers below). */
object Routes {
    const val HOME = "home"
    const val MAP = "map"
    const val PROFILE = "profile"
    const val SETTINGS = "settings"

    const val GOVERNORATE = "governorate/{govId}"
    const val PUZZLE = "puzzle/{puzzleId}"
    const val COMPLETION = "completion/{govId}"

    fun governorate(govId: String) = "governorate/$govId"
    fun puzzle(puzzleId: String) = "puzzle/$puzzleId"
    fun completion(govId: String) = "completion/$govId"
}
