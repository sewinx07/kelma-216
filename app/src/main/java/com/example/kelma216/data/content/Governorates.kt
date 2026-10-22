package com.example.kelma216.data.content

import com.example.kelma216.data.model.Governorate

/**
 * The 24 Tunisian governorates in the exact level order given by the KELMA 216
 * presentation (page 6). Each is one level of 9 puzzles.
 */
object Governorates {

    val all: List<Governorate> = listOf(
        Governorate("tunis", 1, "Tunis"),
        Governorate("ariana", 2, "Ariana"),
        Governorate("ben_arous", 3, "Ben Arous"),
        Governorate("manouba", 4, "Manouba"),
        Governorate("nabeul", 5, "Nabeul"),
        Governorate("zaghouan", 6, "Zaghouan"),
        Governorate("bizerte", 7, "Bizerte"),
        Governorate("beja", 8, "Béja"),
        Governorate("jendouba", 9, "Jendouba"),
        Governorate("le_kef", 10, "Le Kef"),
        Governorate("siliana", 11, "Siliana"),
        Governorate("sousse", 12, "Sousse"),
        Governorate("monastir", 13, "Monastir"),
        Governorate("mahdia", 14, "Mahdia"),
        Governorate("sfax", 15, "Sfax"),
        Governorate("kairouan", 16, "Kairouan"),
        Governorate("kasserine", 17, "Kasserine"),
        Governorate("sidi_bouzid", 18, "Sidi Bouzid"),
        Governorate("gabes", 19, "Gabès"),
        Governorate("medenine", 20, "Médenine"),
        Governorate("tataouine", 21, "Tataouine"),
        Governorate("gafsa", 22, "Gafsa"),
        Governorate("tozeur", 23, "Tozeur"),
        Governorate("kebili", 24, "Kébili"),
    )

    private val byId: Map<String, Governorate> = all.associateBy { it.id }

    fun byId(id: String): Governorate? = byId[id]

    /** Positions the presentation's list to look up by level order (1..24). */
    fun byOrder(order: Int): Governorate? = all.firstOrNull { it.order == order }
}
