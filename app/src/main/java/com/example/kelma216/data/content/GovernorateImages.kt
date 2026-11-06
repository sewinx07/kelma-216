package com.example.kelma216.data.content

/**
 * Maps a stable governorate id to the hand-managed asset folder that holds its
 * cover photo (app/src/assets/governorates/<slug>/cover.*).
 *
 * The folder layout is documented in app/src/assets/README.md. Nothing here needs to
 * change when a photo is added or replaced: only the file on disk does.
 */
object GovernorateImages {

    /** Folder overrides where the file name differs from the underscore id. */
    private val slugOverrides = mapOf(
        "le_kef" to "kef",
    )

    /** Extensions tried in order, so cover.png or cover.jpg both work. */
    private val extensions = listOf("png", "jpg", "jpeg", "webp")

    fun slugFor(governorateId: String): String =
        slugOverrides[governorateId] ?: governorateId.replace('_', '-')

    /** Candidate asset paths for this governorate, most preferred first. */
    fun candidatePaths(governorateId: String): List<String> {
        val slug = slugFor(governorateId)
        return extensions.map { "governorates/$slug/cover.$it" }
    }
}
