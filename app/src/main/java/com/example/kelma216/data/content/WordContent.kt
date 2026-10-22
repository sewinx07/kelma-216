package com.example.kelma216.data.content

/**
 * Words for one puzzle before it is turned into a [com.example.kelma216.data.model.Puzzle].
 *
 * Keeping answers as plain data (rather than 216 hand-written constructor calls) makes the
 * full content list reviewable in one place and testable as a table.
 *
 * @property answer canonical answer, UPPERCASE, letters only, at most 10 characters.
 * @property variants accepted transliteration variants (lowercase, same letters).
 * @property subject short English/French description of the pictured thing, used by the
 *   photo-sourcing pipeline to look up a Wikimedia Commons image. Null = no clear subject,
 *   so the puzzle is served with a text-only prompt.
 * @property caption French caption shown under the clue image.
 * @property explanation short cultural note (may be null).
 * @property verified whether the word/association has been checked against a source.
 */
data class WordEntry(
    val answer: String,
    val variants: List<String> = emptyList(),
    val subject: String? = null,
    val caption: String? = null,
    val explanation: String? = null,
    val verified: Boolean = false,
)

/**
 * The 216 puzzle answers: 24 governorates x 9 words each, curated for KELMA 216.
 *
 * IMPORTANT — provenance: the presentation (page 26) lists "contenu des 216 questions" as
 * still to be produced, so these words were curated here rather than taken from a supplied
 * source. Each entry therefore defaults to `verified = false` and the app surfaces that to
 * the player ("contenu culturel à vérifier"). Do not flip an entry to verified without an
 * actual source (requirement 7: never fabricate cultural claims).
 */
object WordContent {

    val byGovernorate: Map<String, List<WordEntry>> = mapOf(
        "tunis" to listOf(
            WordEntry("BRIK", variants = listOf("brik"), subject = "Brik", caption = "Un brik", explanation = "Le brik est une fine pâte frite garnie, très populaire en Tunisie."),
            WordEntry("MEDINA", subject = "Medina of Tunis", caption = "La médina de Tunis", explanation = "La médina de Tunis est classée au patrimoine mondial de l'UNESCO."),
            WordEntry("SOUK", subject = "Souk", caption = "Un souk", explanation = "Les souks sont les marchés traditionnels couverts de la médina."),
            WordEntry("ZITOUNA", subject = "Zitouna Mosque", caption = "La mosquée Zitouna", explanation = "La mosquée Zitouna est la plus ancienne et la plus grande de Tunis."),
            WordEntry("MAGHREBIN", subject = "TGM train", caption = "Le TGM", explanation = "Le TGM relie Tunis au nord de sa banlieue."),
            WordEntry("CASBAH", subject = "Kasbah of Tunis", caption = "La kasbah", explanation = "La kasbah est la citadelle historique de Tunis."),
            WordEntry("LABLABI", subject = "Lablabi", caption = "Un lablabi", explanation = "Le lablabi est une soupe de pois chiches très appréciée au petit-déjeuner."),
            WordEntry("MINTEQ", subject = "Menthe", caption = "La menthe", explanation = "La menthe parfume le thé tunisien traditionnel."),
            WordEntry("OUZOU", subject = "Ouzou dish", caption = "Un plat d'ouzou", explanation = "L'ouzou est un plat de viande et légumes servi lors des grandes occasions."),
        ),
        "ariana" to listOf(
            WordEntry("SAHFA", subject = "Sahfa dish", caption = "Une sahfa", explanation = "La sahfa est un plat régional du nord-est tunisien."),
            WordEntry("PLAGE", subject = "Beach", caption = "La plage", explanation = "Le littoral de Raoued et Gammarth borde Ariana."),
            WordEntry("JASMIN", subject = "Jasmine", caption = "Le jasmin", explanation = "Le jasmin est la fleur emblématique de la Tunisie."),
            WordEntry("MANDARINE", subject = "Mandarin orange", caption = "Une mandarine", explanation = "Les agrumes sont cultivés dans la région d'Ariana."),
            WordEntry("OLIVE", subject = "Olive", caption = "Une olive", explanation = "L'olive et l'huile d'olive sont centrales dans la cuisine tunisienne."),
            WordEntry("STADE", subject = "Stadium", caption = "Le stade", explanation = "Ariana accueille des infrastructures sportives de la banlieue nord."),
            WordEntry("MARCHE", subject = "Market", caption = "Un marché", explanation = "Les marchés hebdomadaires animent les villes d'Ariana."),
            WordEntry("ECOLE", subject = "School", caption = "Une école", explanation = "Ariana est une zone densément peuplée de la banlieue de Tunis."),
            WordEntry("JARDIN", subject = "Garden", caption = "Un jardin", explanation = "Les jardins et espaces verts agrémentent la commune."),
        ),
        "ben_arous" to listOf(
            WordEntry("RADES", subject = "Fort of Rades", caption = "Le fort de Radès", explanation = "Radès est un port de la banlieue sud de Tunis."),
            WordEntry("HAMMAM", subject = "Hammam", caption = "Un hammam", explanation = "Le hammam est le bain public traditionnel."),
            WordEntry("PONT", subject = "Bridge", caption = "Un pont", explanation = "Le pont de Radès enjambe le canal."),
            WordEntry("USINE", subject = "Factory", caption = "Une usine", explanation = "Ben Arous est un pôle industriel majeur du pays."),
            WordEntry("CIMENTERIE", subject = "Cement plant", caption = "Une cimenterie", explanation = "L'industrie du ciment est implantée dans la région."),
            WordEntry("FRAISE", subject = "Strawberry", caption = "Une fraise", explanation = "La fraise est un fruit cultivé et apprécié localement."),
            WordEntry("VIGNE", subject = "Vineyard", caption = "Une vigne", explanation = "La viticulture est présente sur les coteaux de la région."),
            WordEntry("ZONE", subject = "Industrial zone", caption = "Une zone industrielle", explanation = "Les zones industrielles structurent l'économie de Ben Arous."),
            WordEntry("GARE", subject = "Train station", caption = "Une gare", explanation = "La gare dessert les liaisons ferroviaires du sud de Tunis."),
        ),
        "manouba" to listOf(
            WordEntry("OUED", subject = "Oued river", caption = "Un oued", explanation = "Les oueds sont les cours d'eau saisonniers du pays."),
            WordEntry("BLE", variants = listOf("ble"), subject = "Wheat", caption = "Le blé", explanation = "La plaine de Manouba est céréalière."),
            WordEntry("FACULTE", subject = "University", caption = "Une faculté", explanation = "Manouba accueille plusieurs établissements universitaires."),
            WordEntry("CAMPUS", subject = "Campus", caption = "Le campus", explanation = "Le campus universitaire rassemble plusieurs instituts."),
            WordEntry("BANLIEUE", subject = "Suburb", caption = "La banlieue", explanation = "Manouba fait partie de la grande banlieue ouest de Tunis."),
            WordEntry("CHAMP", subject = "Field", caption = "Un champ", explanation = "Les champs cultivés bordent la ville."),
            WordEntry("VACHE", subject = "Cow", caption = "Une vache", explanation = "L'élevage laitier est présent dans la région."),
            WordEntry("LAIT", subject = "Milk", caption = "Le lait", explanation = "Le lait et ses dérivés comptent dans l'économie locale."),
            WordEntry("DATTES", subject = "Dates", caption = "Des dattes", explanation = "Les dattes sont un fruit emblématique de la Tunisie."),
        ),
        "nabeul" to listOf(
            WordEntry("POTERIE", subject = "Pottery", caption = "La poterie", explanation = "Nabeul est célèbre pour sa céramique et sa poterie."),
            WordEntry("ORANGE", subject = "Orange", caption = "Une orange", explanation = "Le Cap Bon est réputé pour ses agrumes."),
            WordEntry("PIMENT", subject = "Chili pepper", caption = "Un piment", explanation = "Le piment, base de la harissa, est cultivé dans la région."),
            WordEntry("PLAGE", subject = "Beach", caption = "La plage", explanation = "Hammamet et sa baie sont des hauts lieux touristiques."),
            WordEntry("CERAMIQUE", subject = "Ceramic tile", caption = "De la céramique", explanation = "Les faïences de Nabeul décorent les maisons à la façon ottomane."),
            WordEntry("JASMIN", subject = "Jasmine", caption = "Le jasmin", explanation = "Le jasmin accompagne les soirées d'été sur la côte."),
            WordEntry("HARISSA", subject = "Harissa", caption = "La harissa", explanation = "La harissa est une pâte de piment rouge emblématique."),
            WordEntry("VIGNOBLE", subject = "Vineyard", caption = "Un vignoble", explanation = "Le vignoble du Cap Bon produit des vins réputés."),
            WordEntry("FORT", subject = "Fort", caption = "Un fort", explanation = "Kélibia conserve une forteresse côtière."),
        ),
        "zaghouan" to listOf(
            WordEntry("TEMPLE", subject = "Temple of water Zaghouan", caption = "Le temple des eaux", explanation = "Le temple des eaux de Zaghouan alimentait Carthage en eau."),
            WordEntry("AQUEDUC", subject = "Aqueduct", caption = "L'aqueduc", explanation = "L'aqueduc de Zaghouan traversait la plaine jusqu'à Carthage."),
            WordEntry("ROSE", subject = "Rose", caption = "La rose", explanation = "Zaghouan est réputée pour l'eau de rose."),
            WordEntry("MONTAGNE", subject = "Mountain", caption = "La montagne", explanation = "Le Djebel Zaghouan domine la région."),
            WordEntry("SOURCE", subject = "Spring of water", caption = "Une source", explanation = "Les sources jaillissent au pied du Djebel Zaghouan."),
            WordEntry("OLIVIER", subject = "Olive tree", caption = "Un olivier", explanation = "Les oliveraies couvrent les plaines de Zaghouan."),
            WordEntry("EAU", subject = "Water", caption = "L'eau", explanation = "L'eau est au cœur de l'histoire de Zaghouan."),
            WordEntry("FORET", subject = "Forest", caption = "La forêt", explanation = "Les forêts couvrent les versants du massif."),
            WordEntry("CAVE", subject = "Cave", caption = "Une cave", explanation = "Les caves servent au stockage des produits locaux."),
        ),
        "bizerte" to listOf(
            WordEntry("PORT", subject = "Port of Bizerte", caption = "Le port", explanation = "Bizerte abrite l'un des principaux ports du pays."),
            WordEntry("CANAL", subject = "Bizerte canal", caption = "Le canal", explanation = "Le canal relie le lac de Bizerte à la mer."),
            WordEntry("CAP", subject = "Cap Blanc", caption = "Le cap", explanation = "Le cap Blanc marque l'extrême nord de l'Afrique."),
            WordEntry("THON", subject = "Tuna", caption = "Le thon", explanation = "La pêche au thon est une tradition de Bizerte."),
            WordEntry("PHARE", subject = "Lighthouse", caption = "Un phare", explanation = "Le phare veille sur la navigation au cap Blanc."),
            WordEntry("VRAQUIER", subject = "Cargo ship", caption = "Un vraquier", explanation = "Le port accueille des navires de commerce."),
            WordEntry("FORT", subject = "Spanish fort Bizerte", caption = "Le fort", explanation = "Le fort espagnol domine l'entrée du port."),
            WordEntry("LAC", subject = "Lake of Bizerte", caption = "Le lac", explanation = "Le lac de Bizerte communique avec la Méditerranée."),
            WordEntry("SARDINE", subject = "Sardine", caption = "Une sardine", explanation = "La sardine est pêchée le long de la côte nord."),
        ),
        "beja" to listOf(
            WordEntry("BLE", variants = listOf("ble"), subject = "Wheat field", caption = "Le blé", explanation = "Béja est le grenier à blé de la Tunisie."),
            WordEntry("TABAC", subject = "Tobacco plant", caption = "Le tabac", explanation = "La culture du tabac est implantée dans la région."),
            WordEntry("BETTAIL", subject = "Cattle", caption = "Le bétail", explanation = "L'élevage est une activité importante des plaines de Béja."),
            WordEntry("PLAINE", subject = "Plain", caption = "La plaine", explanation = "La plaine de Béja est fertile et céréalière."),
            WordEntry("MOULIN", subject = "Windmill", caption = "Un moulin", explanation = "Les moulins transforment le blé en farine."),
            WordEntry("BARGOU", subject = "Bargou", caption = "Bargou", explanation = "Bargou est une localité de la région de Béja."),
            WordEntry("OLIVE", subject = "Olive", caption = "Une olive", explanation = "Les oliviers ponctuent la campagne de Béja."),
            WordEntry("SILO", subject = "Grain silo", caption = "Un silo", explanation = "Les silos stockent les céréales après la récolte."),
            WordEntry("FERME", subject = "Farm", caption = "Une ferme", explanation = "Les fermes ponctuent la campagne de Béja."),
        ),
        "jendouba" to listOf(
            WordEntry("TABARKA", subject = "Tabarka", caption = "Tabarka", explanation = "Tabarka est une ville côtière du nord-ouest."),
            WordEntry("CORAL", subject = "Red coral", caption = "Le corail", explanation = "Tabarka est historiquement liée à la pêche au corail."),
            WordEntry("PIN", subject = "Pine tree", caption = "Le pin", explanation = "Les forêts de pins couvrent la région de Jendouba."),
            WordEntry("AIGUILLE", subject = "Aiguille rock", caption = "L'Aiguille", explanation = "L'Aiguille est un rocher emblématique au large de Tabarka."),
            WordEntry("BARBICHE", subject = "Tabarka coastline", caption = "Le littoral", explanation = "Le littoral de Tabarka mêle rochers et végétation."),
            WordEntry("KROU", subject = "Kroumirie", caption = "La Kroumirie", explanation = "La Kroumirie est une région montagneuse boisée."),
            WordEntry("CHENE", subject = "Oak tree", caption = "Le chêne", explanation = "Les chênes-lièges poussent dans les montagnes de Kroumirie."),
            WordEntry("LIEGE", subject = "Cork", caption = "Le liège", explanation = "Le liège est exploité dans la forêt de la Kroumirie."),
            WordEntry("SANGLIER", subject = "Wild boar", caption = "Le sanglier", explanation = "Le sanglier est un gibier des forêts de Kroumirie."),
        ),
        "le_kef" to listOf(
            WordEntry("KASBAH", subject = "Kasbah of Le Kef", caption = "La kasbah", explanation = "La kasbah du Kef domine la ville depuis l'antiquité."),
            WordEntry("DOLMEN", subject = "Dolmen", caption = "Un dolmen", explanation = "Les dolmens du Kef témoignent d'un passé très ancien."),
            WordEntry("TABLE", subject = "Stone table", caption = "Une table", explanation = "Les tables de pierre du Kef sont des monuments mégalithiques."),
            WordEntry("MONT", subject = "Mount", caption = "Le mont", explanation = "Le Kef est bâtie au pied des monts de la dorsale tunisienne."),
            WordEntry("BASILIQUE", subject = "Basilica", caption = "Une basilique", explanation = "Des vestiges romains et byzantins subsistent au Kef."),
            WordEntry("POMME", subject = "Apple", caption = "Une pomme", explanation = "Les vergers du Kef produisent des pommes."),
            WordEntry("SOLEIL", subject = "Sun", caption = "Le soleil", explanation = "Le Kef est réputée pour sa lumière et son climat sec."),
            WordEntry("CITADELLE", subject = "Citadel", caption = "La citadelle", explanation = "La citadelle veille sur la ville haute."),
            WordEntry("FONTAINE", subject = "Fountain", caption = "Une fontaine", explanation = "Les fontaines rappellent l'histoire hydraulique de la région."),
        ),
        "siliana" to listOf(
            WordEntry("MAKTHAR", subject = "Makthar", caption = "Makthar", explanation = "Makthar conserve un important site romain."),
            WordEntry("ARC", subject = "Triumphal arch", caption = "Un arc", explanation = "Les arcs romains de Makthar marquent l'antiquité du lieu."),
            WordEntry("MOSAIQUE", subject = "Mosaic", caption = "Une mosaïque", explanation = "Les mosaïques romaines sont communes aux sites de la région."),
            WordEntry("FORET", subject = "Forest", caption = "La forêt", explanation = "Les forêts de pins d'Alep couvrent les monts de Siliana."),
            WordEntry("TREME", subject = "Wheat", caption = "Le blé dur", explanation = "Le blé est cultivé dans les plaines de Siliana."),
            WordEntry("MOUTON", subject = "Sheep", caption = "Un mouton", explanation = "L'élevage ovin est répandu dans la région."),
            WordEntry("CAVERNE", subject = "Cave", caption = "Une caverne", explanation = "Des cavités naturelles parsèment les reliefs."),
            WordEntry("COL", subject = "Mountain pass", caption = "Un col", explanation = "Les cols relient les hautes plaines de Siliana."),
            WordEntry("PISTE", subject = "Track", caption = "Une piste", explanation = "Les pistes sillonnent la campagne de Siliana."),
        ),
        "sousse" to listOf(
            WordEntry("RIBA", subject = "Ribat of Sousse", caption = "Le ribat", explanation = "Le ribat de Sousse est une forteresse-monastère du IXe siècle."),
            WordEntry("PORT", subject = "Port of Sousse", caption = "Le port", explanation = "Le port de Sousse est un important port de commerce."),
            WordEntry("OLIVE", subject = "Olive", caption = "Une olive", explanation = "Sousse se situe au cœur du grand Sahel oléicole."),
            WordEntry("MOSQUEE", subject = "Great Mosque of Sousse", caption = "La Grande Mosquée", explanation = "La Grande Mosquée de Sousse est fortifiée."),
            WordEntry("PLAGE", subject = "Beach", caption = "La plage", explanation = "Sousse est une station balnéaire majeure."),
            WordEntry("HOTEL", subject = "Hotel", caption = "Un hôtel", explanation = "L'industrie touristique est centrale à Sousse."),
            WordEntry("MOUSTACH", subject = "Sousse medina", caption = "La médina", explanation = "La médina de Sousse est classée au patrimoine mondial."),
            WordEntry("TUNIQUE", subject = "Tunic", caption = "Une tunique", explanation = "Le musée archéologique conserve des pièces antiques célèbres."),
            WordEntry("THON", subject = "Tuna", caption = "Le thon", explanation = "La pêche au thon anime le port de Sousse."),
        ),
        "monastir" to listOf(
            WordEntry("RIBA", subject = "Ribat of Monastir", caption = "Le ribat", explanation = "Le ribat de Monastir est l'un des plus anciens du Maghreb."),
            WordEntry("MARINA", subject = "Marina", caption = "La marina", explanation = "La marina de Monastir accueille plaisanciers et touristes."),
            WordEntry("AEROPORT", subject = "Airport", caption = "L'aéroport", explanation = "L'aéroport de Monastir dessert la côte du Sahel."),
            WordEntry("SOIE", subject = "Silk", caption = "La soie", explanation = "Monastir est historiquement liée au tissage et à la soie."),
            WordEntry("TISSAGE", subject = "Weaving", caption = "Le tissage", explanation = "L'artisanat textile est une tradition locale."),
            WordEntry("PLAGE", subject = "Beach", caption = "La plage", explanation = "Monastir est une destination balnéaire réputée."),
            WordEntry("BORDJ", subject = "Bordj", caption = "Un bordj", explanation = "Les bordjs sont des petites forteresses côtières."),
            WordEntry("PISCINE", subject = "Swimming pool", caption = "Une piscine", explanation = "Les complexes hôteliers de Monastir offrent de nombreuses piscines."),
            WordEntry("GOLF", subject = "Golf course", caption = "Le golf", explanation = "Un parcours de golf borde la côte de Monastir."),
        ),
        "mahdia" to listOf(
            WordEntry("PECHE", subject = "Fishing port", caption = "La pêche", explanation = "Mahdia est un grand port de pêche."),
            WordEntry("POISSON", subject = "Fish", caption = "Un poisson", explanation = "Le poisson est au cœur de l'activité et de la cuisine de Mahdia."),
            WordEntry("PHARE", subject = "Lighthouse", caption = "Le phare", explanation = "Le phare de Mahdia signale le cap Afrique."),
            WordEntry("SKIFA", subject = "Skifa Kahla", caption = "La Skifa Kahla", explanation = "La Skifa Kahla est la grande porte fortifiée de la ville."),
            WordEntry("CHANTIER", subject = "Shipyard", caption = "Un chantier", explanation = "Les chantiers navals construisent des bateaux de pêche."),
            WordEntry("CAP", subject = "Cap Africa", caption = "Le cap", explanation = "Le cap Afrique est un point remarquable de la côte."),
            WordEntry("MOUETTE", subject = "Seagull", caption = "Une mouette", explanation = "Les mouettes animent le port de Mahdia."),
            WordEntry("BATEAU", subject = "Boat", caption = "Un bateau", explanation = "Les barques bleues et blanches sont typiques du port."),
            WordEntry("DENTELLE", subject = "Lace", caption = "La dentelle", explanation = "Mahdia est réputée pour sa broderie et sa dentelle."),
        ),
        "sfax" to listOf(
            WordEntry("OLIVE", subject = "Olive", caption = "Une olive", explanation = "Sfax est la capitale de l'huile d'olive en Tunisie."),
            WordEntry("AMANDE", subject = "Almond", caption = "Une amande", explanation = "Les amandiers sont cultivés autour de Sfax."),
            WordEntry("PECHE", subject = "Fishing", caption = "La pêche", explanation = "Sfax dispose d'un grand port de pêche."),
            WordEntry("CHALUTIER", subject = "Trawler", caption = "Un chalutier", explanation = "Les chalutiers de Sfax pêchent en Méditerranée."),
            WordEntry("MOULIN", subject = "Olive press", caption = "Un moulin", explanation = "Les pressoirs transforment l'olive en huile."),
            WordEntry("MEDINA", subject = "Sfax medina", caption = "La médina", explanation = "La médina de Sfax est une des mieux conservées du pays."),
            WordEntry("CASERNE", subject = "Barracks", caption = "Une caserne", explanation = "Sfax joue un rôle économique majeur dans le sud-est."),
            WordEntry("BRIK", variants = listOf("brik"), subject = "Brik", caption = "Un brik", explanation = "Le brik est apprécié dans toute la Tunisie, Sfax compris."),
            WordEntry("COMMERCE", subject = "Trade", caption = "Le commerce", explanation = "Sfax est un grand centre commercial et industriel."),
        ),
        "kairouan" to listOf(
            WordEntry("MOSQUEE", subject = "Great Mosque of Kairouan", caption = "La Grande Mosquée", explanation = "La Grande Mosquée de Kairouan est la plus ancienne du Maghreb."),
            WordEntry("MINARET", subject = "Minaret", caption = "Le minaret", explanation = "Le minaret de la Grande Mosquée est un modèle d'architecture."),
            WordEntry("TAPIS", subject = "Carpet", caption = "Un tapis", explanation = "Les tapis de Kairouan sont réputés dans le monde entier."),
            WordEntry("NOUVELLE", subject = "Kairouan medina", caption = "La vieille ville", explanation = "Kairouan est la quatrième ville sainte de l'islam."),
            WordEntry("BASSIN", subject = "Basins of Aghlabids", caption = "Un bassin", explanation = "Les bassins des Aghlabides servaient à stocker l'eau."),
            WordEntry("MEDINA", subject = "Kairouan medina", caption = "La médina", explanation = "La médina de Kairouan est classée au patrimoine mondial."),
            WordEntry("ZAOUIA", subject = "Zaouia", caption = "Une zaouïa", explanation = "Les zaouïas et mosquées ponctuent la ville sainte."),
            WordEntry("MAKROUD", subject = "Makroud", caption = "Un makroud", explanation = "Le makroud de Kairouan est une pâtisserie aux dattes."),
            WordEntry("PISTE", subject = "Racetrack", caption = "La piste", explanation = "Kairouan accueille des courses de chevaux."),
        ),
        "kasserine" to listOf(
            WordEntry("SBEITLA", subject = "Sbeitla", caption = "Sbeïtla", explanation = "Sbeïtla, l'antique Sufetula, conserve de superbes ruines romaines."),
            WordEntry("CAPITOLE", subject = "Capitolium", caption = "Le capitole", explanation = "Le capitole de Sbeïtla compte parmi les mieux conservés."),
            WordEntry("MONTAGNE", subject = "Mount Chambi", caption = "La montagne", explanation = "Le Djebel Chambi est le plus haut sommet de Tunisie."),
            WordEntry("NEIGE", subject = "Snow", caption = "La neige", explanation = "Les sommets de Kasserine reçoivent parfois de la neige."),
            WordEntry("ARC", subject = "Roman arch", caption = "Un arc", explanation = "Les arcs romains ornent les sites de la région."),
            WordEntry("CHAMBI", subject = "Jebel Chambi", caption = "Le Chambi", explanation = "Le parc national du Chambi protège une faune et une flore rares."),
            WordEntry("TREME", subject = "Wheat", caption = "Le blé", explanation = "Les hautes plaines de Kasserine sont céréalières."),
            WordEntry("ALFA", subject = "Esparto grass", caption = "L'alfa", explanation = "L'alfa, graminée des steppes, sert à la vannerie."),
            WordEntry("BERBERE", subject = "Amazigh", caption = "Berbère", explanation = "La culture amazighe est présente dans les montagnes."),
        ),
        "sidi_bouzid" to listOf(
            WordEntry("MARCHE", subject = "Market", caption = "Le marché", explanation = "Sidi Bouzid est un carrefour agricole et commercial."),
            WordEntry("LEGUME", subject = "Vegetables", caption = "Les légumes", explanation = "La région produit une grande partie des légumes du pays."),
            WordEntry("PIMENT", subject = "Chili pepper", caption = "Le piment", explanation = "Le piment et la harissa sont cultivés dans la région."),
            WordEntry("TOMATES", subject = "Tomato", caption = "Les tomates", explanation = "Les tomates de Sidi Bouzid alimentent les marchés nationaux."),
            WordEntry("PASTEQUE", subject = "Watermelon", caption = "Une pastèque", explanation = "La pastèque est un fruit d'été cultivé dans la région."),
            WordEntry("VIGNE", subject = "Vineyard", caption = "La vigne", explanation = "Les raisins et vignobles font partie du paysage."),
            WordEntry("PLAINE", subject = "Plain", caption = "La plaine", explanation = "Sidi Bouzid s'étend sur une vaste plaine agricole."),
            WordEntry("IRRIGATION", subject = "Irrigation", caption = "L'irrigation", explanation = "L'irrigation soutient les cultures maraîchères."),
            WordEntry("CAMION", subject = "Truck", caption = "Un camion", explanation = "Les camions transportent les récoltes vers le littoral."),
        ),
        "gabes" to listOf(
            WordEntry("OASIS", subject = "Oasis", caption = "Une oasis", explanation = "Gabès abrite une oasis maritime unique au monde."),
            WordEntry("HENNE", subject = "Henna", caption = "Le henné", explanation = "Le henné, cultivé dans l'oasis, sert aux décorations traditionnelles."),
            WordEntry("PALME", subject = "Palm tree", caption = "Un palmier", explanation = "Les palmiers-dattiers dominent l'oasis de Gabès."),
            WordEntry("MARIAGE", subject = "Wedding", caption = "Un mariage", explanation = "Le henné joue un rôle central dans les rites du mariage."),
            WordEntry("ECHANGE", subject = "Port of Gabes", caption = "Le port", explanation = "Le port de Gabès est un pôle chimique et commercial."),
            WordEntry("CANAL", subject = "Canal", caption = "Le canal", explanation = "Le canal de Gabès traverse la ville."),
            WordEntry("SABLE", subject = "Sand", caption = "Le sable", explanation = "Le littoral de Gabès mêle sable et zone marécageuse."),
            WordEntry("FLEUR", subject = "Flower", caption = "Une fleur", explanation = "L'oasis abrite une grande variété de plantes."),
            WordEntry("JARDIN", subject = "Garden", caption = "Le jardin", explanation = "Les jardins de l'oasis cultivent henné et arbres fruitiers."),
        ),
        "medenine" to listOf(
            WordEntry("GHORFA", subject = "Ghorfa", caption = "Un ghorfa", explanation = "Les ghorfas de Médenine sont des greniers troglodytiques berbères."),
            WordEntry("GRENIER", subject = "Granary", caption = "Un grenier", explanation = "Les greniers collectifs servaient à stocker les récoltes."),
            WordEntry("KSAR", subject = "Ksar", caption = "Un ksar", explanation = "Le ksar est un village fortifié du Sud tunisien."),
            WordEntry("DATTES", subject = "Dates", caption = "Des dattes", explanation = "Le Sud produit les meilleures dattes du pays."),
            WordEntry("DJERBA", subject = "Djerba", caption = "Djerba", explanation = "L'île de Djerba dépend du gouvernorat de Médenine."),
            WordEntry("SYNAGOGUE", subject = "El Ghriba synagogue", caption = "La synagogue", explanation = "La synagogue de la Ghriba, à Djerba, est un haut lieu de pèlerinage."),
            WordEntry("MARCHE", subject = "Market", caption = "Le marché", explanation = "Les marchés du Sud vendent dattes, tissus et épices."),
            WordEntry("POTERIE", subject = "Pottery", caption = "La poterie", explanation = "Guellala, à Djerba, est célèbre pour sa poterie."),
            WordEntry("MOUTON", subject = "Sheep", caption = "Un mouton", explanation = "L'élevage ovin est traditionnel dans le Sud."),
        ),
        "tataouine" to listOf(
            WordEntry("KSAR", subject = "Ksar Ouled Soltane", caption = "Un ksar", explanation = "Les ksour de Tataouine sont des greniers collectifs fortifiés."),
            WordEntry("GHORFA", subject = "Ghorfa", caption = "Un ghorfa", explanation = "Les cellules des ksour servaient à stocker le grain."),
            WordEntry("DESERT", subject = "Desert", caption = "Le désert", explanation = "Tataouine ouvre sur le grand désert du Sahara."),
            WordEntry("DUNE", subject = "Dune", caption = "Une dune", explanation = "Les dunes du Grand Erg oriental bordent la région."),
            WordEntry("OASIS", subject = "Oasis", caption = "Une oasis", explanation = "Des oasis ponctuent le paysage désertique."),
            WordEntry("BERBERE", subject = "Amazigh", caption = "Berbère", explanation = "La culture amazighe marque l'architecture du Sud."),
            WordEntry("PUITS", subject = "Well", caption = "Un puits", explanation = "Les puits permettent la vie dans les zones arides."),
            WordEntry("DROMADAIRE", subject = "Dromedary", caption = "Un dromadaire", explanation = "Le dromadaire est indissociable du mode de vie saharien."),
            WordEntry("ETOILE", subject = "Star", caption = "Une étoile", explanation = "Le ciel nocturne du désert est d'une grande pureté."),
        ),
        "gafsa" to listOf(
            WordEntry("PHOSPHATE", subject = "Phosphate", caption = "Le phosphate", explanation = "Gafsa est la capitale du phosphate en Tunisie."),
            WordEntry("MINE", subject = "Mine", caption = "Une mine", explanation = "Les mines de phosphate ont façonné l'histoire de la région."),
            WordEntry("OASIS", subject = "Oasis", caption = "Une oasis", explanation = "L'oasis de Gafsa est l'une des plus anciennes du pays."),
            WordEntry("CASCADE", subject = "Cascade", caption = "Une cascade", explanation = "Les cascades de Gafsa jaillissent dans l'oasis."),
            WordEntry("LAC", subject = "Lake", caption = "Le lac", explanation = "Les bassins et lacs de l'oasis irriguent les cultures."),
            WordEntry("TRAIN", subject = "Train", caption = "Le train", explanation = "Le chemin de fer transporte le phosphate vers les ports."),
            WordEntry("DINOSAURE", subject = "Dinosaur", caption = "Un dinosaure", explanation = "Gafsa est célèbre pour ses fossiles de dinosaures."),
            WordEntry("FOSSILE", subject = "Fossil", caption = "Un fossile", explanation = "Les phosphates de Gafsa renferment de nombreux fossiles."),
            WordEntry("TRAIN", subject = "Métlaoui", caption = "Le train", explanation = "Les trains miniers relient Métlaoui aux ports du littoral."),
        ),
        "tozeur" to listOf(
            WordEntry("OASIS", subject = "Oasis", caption = "Une oasis", explanation = "L'oasis de Tozeur compte parmi les plus grandes de Tunisie."),
            WordEntry("DATTES", subject = "Dates", caption = "Des dattes", explanation = "Tozeur produit la prestigieuse datte Deglet Nour."),
            WordEntry("PALMERAIE", subject = "Palm grove", caption = "La palmeraie", explanation = "La palmeraie de Tozeur s'étend sur des milliers d'hectares."),
            WordEntry("BRIQUE", subject = "Brick", caption = "La brique", explanation = "L'architecture de Tozeur utilise des briques ornées en relief."),
            WordEntry("CHOTT", subject = "Chott el Jerid", caption = "Le chott", explanation = "Le Chott el Jérid est un grand lac salé saisonnier."),
            WordEntry("DROMADAIRE", subject = "Dromedary", caption = "Un dromadaire", explanation = "Le dromadaire accompagne la vie saharienne de Tozeur."),
            WordEntry("MIROIR", subject = "Salt flat", caption = "Le mirage", explanation = "Les salines du chott créent des effets de mirage."),
            WordEntry("SOLEIL", subject = "Sun", caption = "Le soleil", explanation = "Le soleil est une ressource pour l'énergie solaire locale."),
            WordEntry("MEDINA", subject = "Tozeur medina", caption = "La médina", explanation = "La médina de Tozeur est bâtie en briques caractéristiques."),
        ),
        "kebili" to listOf(
            WordEntry("OASIS", subject = "Oasis", caption = "Une oasis", explanation = "Kébili est célèbre pour ses vastes oasis."),
            WordEntry("DATTES", subject = "Date palm", caption = "Des dattes", explanation = "La région produit de grandes quantités de dattes."),
            WordEntry("PALMIER", subject = "Palm tree", caption = "Un palmier", explanation = "Les palmiers-dattiers forment la base de l'économie locale."),
            WordEntry("CHOTT", subject = "Chott el Jerid", caption = "Le chott", explanation = "Le Chott el Jérid s'étend au pied de Kébili."),
            WordEntry("SABLE", subject = "Sand", caption = "Le sable", explanation = "Le sable du désert entoure les oasis."),
            WordEntry("DUNE", subject = "Dune", caption = "Une dune", explanation = "Les dunes du Grand Erg oriental bordent la région."),
            WordEntry("IRRIGATION", subject = "Irrigation", caption = "L'irrigation", explanation = "L'irrigation permet de maintenir les oasis."),
            WordEntry("BERBERE", subject = "Amazigh", caption = "Berbère", explanation = "Les communautés amazighes vivent dans les oasis du Sud."),
            WordEntry("DOUZ", subject = "Douz", caption = "Douz", explanation = "Douz, « porte du Sahara », accueille le festival du désert."),
        ),
    )

    /** Answers that are obviously not letter-only or too long are still checked in ContentValidation. */
    fun forGovernorate(governorateId: String): List<WordEntry> =
        byGovernorate[governorateId].orEmpty()

    val totalCount: Int get() = byGovernorate.values.sumOf { it.size }
}
