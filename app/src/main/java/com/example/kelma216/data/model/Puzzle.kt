package com.example.kelma216.data.model

import androidx.annotation.DrawableRes

/**
 * A single illustrated word puzzle.
 *
 * Data-driven by design: the UI renders any [Puzzle] it is given, so content can be
 * added up to the full 216 without touching screen code. See [com.example.kelma216.data.content].
 *
 * @property id stable identifier used for saving completion (e.g. "tunis-01-brik").
 * @property governorateId the governorate this puzzle belongs to.
 * @property position 1-based slot within its governorate (1..9), presentation page 9.
 * @property answer canonical expected answer, compared case-insensitively.
 * @property acceptedVariants extra acceptable spellings (presentation page 15 asks that
 *   transliteration variants be handled deliberately rather than blindly stripping accents).
 * @property clueRes illustration for the clue, or null when art is not available yet
 *   (the UI then shows a clearly labelled placeholder instead of pretending art exists).
 * @property clueCaption caption describing what the clue shows.
 * @property photoSourceUrl where the clue image came from, when it is third-party content
 *   (e.g. a Wikimedia Commons file page). Null for original artwork.
 * @property photoLicense licence the clue image is used under (e.g. "CC BY-SA 4.0", "Public domain").
 *   Null when unknown — the UI then flags the image as unverified.
 * @property photoAttribution short credit line shown with the image, if required by the licence.
 * @property reward coins awarded for solving; normally [com.example.kelma216.domain.GameConfig.CORRECT_REWARD].
 * @property explanation optional cultural note shown after a correct answer.
 * @property explanationStatus whether [explanation] has actually been checked.
 * @property playable false when the slot exists but content is not written yet.
 * @property hintLetter which letter to reveal when the player buys a hint.
 */
data class Puzzle(
    val id: String,
    val governorateId: String,
    val position: Int,
    val answer: String,
    val acceptedVariants: List<String> = emptyList(),
    @param:DrawableRes val clueRes: Int? = null,
    val clueCaption: String? = null,
    val photoSourceUrl: String? = null,
    val photoLicense: String? = null,
    val photoAttribution: String? = null,
    val reward: Int,
    val explanation: String? = null,
    val explanationStatus: VerificationStatus = VerificationStatus.NOT_PROVIDED,
    val playable: Boolean = true,
    val hintLetter: Char? = null,
) {
    /** All strings that count as a correct answer for this puzzle. */
    fun acceptedAnswers(): Set<String> =
        (listOf(answer) + acceptedVariants)
            .map { it.trim() }
            .filter { it.isNotEmpty() }
            .toSet()

    /** Normalises a candidate answer for comparison against [acceptedAnswers]. */
    fun normalize(candidate: String): String = candidate.trim()
}
