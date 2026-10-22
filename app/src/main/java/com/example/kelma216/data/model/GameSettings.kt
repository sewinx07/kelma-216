package com.example.kelma216.data.model

/**
 * Sound/music preferences. Kept separate from progress so settings survive
 * independently and can be extended without touching completion data.
 */
data class GameSettings(
    val soundEnabled: Boolean = true,
    val musicEnabled: Boolean = false,
)
