package com.example.kelma216.data.content

import com.example.kelma216.data.model.Puzzle
import com.example.kelma216.data.model.VerificationStatus
import com.example.kelma216.domain.GameConfig

/**
 * All written puzzle content, kept fully separate from UI (requirement 6).
 *
 * The full 216 puzzles (24 governorates x 9) are built from two data sources:
 *  - [WordContent]  — the curated answer/word table.
 *  - [PhotoManifest] — the provenance of every clue photo, keyed by answer.
 *
 * A puzzle with no photo entry is still playable: it shows a clearly labelled text-only
 * prompt instead of pretending art exists. Adding or fixing content is a data change here,
 * never a screen change.
 */
object PuzzleCatalog {

    /** Placeholder ID used when a slot has no content yet (never a real puzzle ID). */
    const val EMPTY_SLOT = "empty-slot"

    /** One puzzle per word, in governorate order then position order. */
    private val puzzles: List<Puzzle> = Governorates.all.flatMap { gov ->
        WordContent.forGovernorate(gov.id).mapIndexed { index, word ->
            val photo = PhotoManifest.forAnswer(word.answer)
            Puzzle(
                id = "${gov.id}-%02d-%s".format(index + 1, word.answer.lowercase()),
                governorateId = gov.id,
                position = index + 1,
                answer = word.answer,
                acceptedVariants = word.variants,
                clueRes = photo?.drawableRes,
                clueCaption = word.caption,
                photoSourceUrl = photo?.sourceUrl,
                photoLicense = photo?.license,
                photoAttribution = photo?.artist,
                reward = GameConfig.CORRECT_REWARD,
                explanation = word.explanation,
                explanationStatus = if (word.verified) {
                    VerificationStatus.VERIFIED
                } else {
                    VerificationStatus.NEEDS_VERIFICATION
                },
                playable = true,
                hintLetter = word.answer.firstOrNull(),
            )
        }
    }

    private val byId: Map<String, Puzzle> = puzzles.associateBy { it.id }

    private val byGovernorate: Map<String, List<Puzzle>> =
        puzzles.groupBy { it.governorateId }

    fun byId(id: String): Puzzle? = byId[id]

    fun byGovernorateId(governorateId: String): List<Puzzle> =
        byGovernorate[governorateId].orEmpty().sortedBy { it.position }

    fun playableByGovernorateId(governorateId: String): List<Puzzle> =
        byGovernorateId(governorateId).filter { it.playable }

    fun puzzleAt(governorateId: String, position: Int): Puzzle? =
        byGovernorateId(governorateId).firstOrNull { it.position == position }

    /** Number of playable puzzles written for a governorate (0..9). */
    fun playableCount(governorateId: String): Int =
        playableByGovernorateId(governorateId).size

    /** Every written puzzle, used by validation and total-progress reporting. */
    fun all(): List<Puzzle> = puzzles
}
