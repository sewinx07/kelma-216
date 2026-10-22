package com.example.kelma216.data.model

/**
 * Trust level of a cultural fact (word meaning, regional association, tradition).
 *
 * The presentation stresses that Tunisian cultural accuracy must be checked
 * (page 15: "Vérifier l'exactitude des mots, lieux et références régionales").
 * Content is never presented as verified unless it actually has been.
 */
enum class VerificationStatus {
    /** Fact checked against a reliable source. */
    VERIFIED,

    /** Written down but not yet checked. Must be surfaced as such to the player. */
    NEEDS_VERIFICATION,

    /** Placeholder / not written yet. */
    NOT_PROVIDED,
}
