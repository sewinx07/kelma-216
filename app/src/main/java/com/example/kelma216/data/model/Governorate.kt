package com.example.kelma216.data.model

/**
 * A single Tunisian governorate, which doubles as one level of the game.
 *
 * [id] is stable and is what progress is tracked against (never array indexes).
 * [order] is the canonical level order defined by the KELMA 216 presentation (1..24).
 */
data class Governorate(
    val id: String,
    val order: Int,
    val name: String,
)
