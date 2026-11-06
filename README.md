# KELMA 216 — Le jeu des mots tunisiens

**Explore la Tunisie, un gouvernorat et un mot à la fois.**

KELMA 216 est un jeu de mots tunisien natif Android : 24 gouvernorats, 9 énigmes par
gouvernorat, 216 mots à découvrir à partir de photographies réelles. Le joueur compose
chaque réponse avec des tuiles de lettres, gagne des pièces, achète des indices et
débloque progressivement tout le pays.

Application : `tn.kelma216.game` · Kotlin · Jetpack Compose · Material 3 · minSdk 24

## Aperçu

Captures réelles de l'application (émulateur Pixel, 1080 × 2424) :

- [Accueil](docs/screenshots/home.png)
- [Carte des gouvernorats](docs/screenshots/map.png)
- [Profil](docs/screenshots/profile.png)
- [Énigme](docs/screenshots/puzzle.png)

---

## Sommaire

- [État actuel du jeu](#état-actuel-du-jeu)
- [Fonctionnalités](#fonctionnalités)
- [Comment jouer](#comment-jouer)
- [Compiler et lancer](#compiler-et-lancer)
- [Structure du projet](#structure-du-projet)
- [Gérer les images des gouvernorats](#gérer-les-images-des-gouvernorats)
- [Logo et icône du lanceur](#logo-et-icône-du-lanceur)
- [Design system](#design-system)
- [Tests effectués](#tests-effectués)
- [Limites connues](#limites-connues)
- [Crédits et licence](#crédits-et-licence)
- [Auteur](#auteur)

---

## État actuel du jeu

| Élément | État |
|---|---|
| Gouvernorats | 24 / 24 |
| Énigmes (mots) | 216 / 216 |
| Photos d'énigmes intégrées | 134 / 216 (les 82 autres affichent une devinette texte) |
| Photos de gouvernorats | 0 / 24 — dossiers créés, à remplir manuellement |
| Logo | intégré (accueil + avatar) |
| Icône du lanceur | intégrée (adaptative + mipmaps) |
| Progression sauvegardée | oui, locale, survit au redémarrage |
| Audio | non implémenté (affiché honnêtement dans Réglages) |

Le contenu (réponses, niveaux, récompenses, indices) est la source de vérité du jeu :
la refonte visuelle n'a modifié aucune réponse ni aucune règle.

---

## Fonctionnalités

- **24 gouvernorats** dans l'ordre officiel, chacun avec 9 énigmes.
- **216 énigmes** avec photo d'indice quand une image libre de droits existe,
  sinon une devinette textuelle (jamais de fausse image).
- **10 tuiles de lettres par énigme**, avec gestion des lettres répétées.
- **Pièces** : +10 pièces par énigme résolue, une seule fois par énigme.
- **Indices payants** : 10 pièces, achat confirmé, jamais débité deux fois.
- **Déblocage progressif** des gouvernorats et des énigmes.
- **Progression persistée** localement (DataStore/stockage local de l'app).
- **Profil** avec statistiques réelles et succès calculés à partir de la sauvegarde.
- **Interface 100 % Compose**, palette tunisienne, animations légères.

---

## Comment jouer

1. **Jouer** ouvre la carte des 24 gouvernorats.
2. Choisis un gouvernorat débloqué, puis une énigme disponible.
3. Observe la photo (ou lis l'indice texte).
4. Touche les lettres pour remplir les cases ; touche une case remplie pour retirer sa lettre.
5. **Valider** vérifie la réponse. Une bonne réponse rapporte des pièces et débloque la suite.
6. **Indice** révèle une lettre contre 10 pièces (confirmation demandée).

---

## Compiler et lancer

### Prérequis

- Android Studio récent (AGP 9.x / Gradle 9.x) et un JDK 17+.
  Sur cette machine, Gradle doit utiliser le JBR d'Android Studio :
  `C:\Program Files\Android\Android Studio\jbr`.
- Un SDK Android avec `compileSdk 37`.

### PowerShell (Windows)

```powershell
cd C:\Users\iimas\Desktop\web\kelma216
$env:JAVA_HOME = "C:\Program Files\Android\Android Studio\jbr"

# APK de debug
.\gradlew.bat assembleDebug

# Tests unitaires
.\gradlew.bat testDebugUnitTest
```

APK généré :

```text
app\build\outputs\apk\debug\app-debug.apk
```

Installation sur un appareil/émulateur connecté :

```powershell
adb install -r app\build\outputs\apk\debug\app-debug.apk
adb shell am start -n tn.kelma216.game/com.example.kelma216.MainActivity
```

Build de release signé : voir [`docs/RELEASE.md`](docs/RELEASE.md) (le fichier
`keystore.properties` à la racine est ignoré par git et n'est jamais commité).

---

## Structure du projet

```text
kelma216/
├── app/
│   ├── src/
│   │   ├── assets/                     # ART MANUEL (empaqueté dans l'APK)
│   │   │   ├── governorates/<slug>/    # photos des 24 gouvernorats
│   │   │   ├── logo/                   # logo + icône d'origine (jamais modifiés)
│   │   │   ├── image-quiz/             # références visuelles du quiz
│   │   │   ├── page-ex/                # maquettes de référence (accueil, profil)
│   │   │   └── README.md               # guide des assets
│   │   ├── main/
│   │   │   ├── java/com/example/kelma216/
│   │   │   │   ├── data/               # contenu, modèles, persistance
│   │   │   │   ├── domain/             # règles du jeu, génération des tuiles
│   │   │   │   ├── ui/                 # écrans, composants, thème, navigation
│   │   │   │   ├── viewmodel/          # état de jeu partagé
│   │   │   │   └── MainActivity.kt
│   │   │   ├── res/                    # photos d'énigmes, icônes, XML
│   │   │   └── AndroidManifest.xml
│   │   └── test/                       # tests unitaires JVM
│   └── build.gradle.kts                # enregistre src/assets comme assets source
├── tools/                              # scripts Python d'assets, manifeste, QA
├── docs/RELEASE.md
└── README.md
```

### Écrans

| Écran | Fichier |
|---|---|
| Accueil | `ui/screens/home/HomeScreen.kt` |
| Carte des gouvernorats | `ui/screens/map/MapScreen.kt` |
| Détail d'un gouvernorat | `ui/screens/governorate/GovernorateScreen.kt` |
| Énigme | `ui/screens/puzzle/PuzzleScreen.kt` |
| Bilan de niveau | `ui/screens/completion/CompletionScreen.kt` |
| Profil | `ui/screens/profile/ProfileScreen.kt` |
| Réglages | `ui/screens/settings/SettingsScreen.kt` |

Composants réutilisables : `ui/components/` (boutons, cartes, tuiles de lettres,
barres de progression, icônes dessinées à la main, bandeau décoratif, chargeur de
photo de gouvernorat).

---

## Gérer les images des gouvernorats

**Aucun code Kotlin n'est à modifier.** Dépose simplement un fichier, relance le build.

```
app/src/assets/governorates/<slug>/cover.png
```

Les 24 dossiers existent déjà :

| Gouvernorat | Dossier | Gouvernorat | Dossier |
|---|---|---|---|
| Tunis | `tunis` | Sfax | `sfax` |
| Ariana | `ariana` | Kairouan | `kairouan` |
| Ben Arous | `ben-arous` | Kasserine | `kasserine` |
| Manouba | `manouba` | Sidi Bouzid | `sidi-bouzid` |
| Nabeul | `nabeul` | Gabès | `gabes` |
| Zaghouan | `zaghouan` | Médenine | `medenine` |
| Bizerte | `bizerte` | Tataouine | `tataouine` |
| Béja | `beja` | Gafsa | `gafsa` |
| Jendouba | `jendouba` | Tozeur | `tozeur` |
| Le Kef | `kef` | Kébili | `kebili` |
| Siliana | `siliana` | Sousse | `sousse` |
| Monastir | `monastir` | Mahdia | `mahdia` |

- **Nom exact** : `cover.png`. Les variantes `cover.jpg`, `cover.jpeg` et `cover.webp`
  sont aussi acceptées (essayées dans cet ordre).
- **Format conseillé** : paysage 4:3 ou 16:9. L'image est recadrée sans déformation
  (`ContentScale.Crop`), coins arrondis et léger dégradé pour la lisibilité du titre.
- **Remplacer** : écrase le fichier, puis `.\gradlew.bat assembleDebug`.
- **Absence** : un visuel de secours neutre (dégradé crème/turquoise + paysage stylisé)
  s'affiche. L'app ne plante pas et ne montre jamais la photo d'un autre gouvernorat.
- **Vérifier** les correspondances :

```powershell
python tools\check_governorate_assets.py
```

Cette commande liste les ids, les dossiers attendus, les fichiers `cover.*` trouvés et
signale tout dossier manquant.

> Le pipeline photos d'énigmes (`res/drawable/photo_*`, manifeste, licences) est décrit
> dans `app/src/assets/README.md` et généré par `tools/generate_manifest.py`.

---

## Logo et icône du lanceur

Les originaux sont dans `app/src/assets/logo/` et **ne sont jamais modifiés** :

- `logo_kelma.png` — logo complet (affiché sur l'accueil, copié dans
  `res/drawable-nodpi/logo_kelma.png`).
- `icon_kelma.png` — icône carrée (utilisée pour le lanceur : icône adaptative
  `mipmap-anydpi-v26` + mipmaps PNG 48→192 px, fond crème `#FFF4E8`).

Pour mettre à jour le logo ou l'icône : remplace les fichiers dans
`app/src/assets/logo/`, puis :

```powershell
python tools\prepare_assets.py
.\gradlew.bat assembleDebug
```

Le script recopie le logo redimensionné, régénère l'icône adaptative et les mipmaps,
et recrée les 24 dossiers de gouvernorats si nécessaire.

---

## Design system

| Couleur | Hex | Usage |
|---|---|---|
| Rouge tunisien | `#E82A38` | actions principales, accents |
| Bleu nuit | `#152A51` | texte, contours, profondeur |
| Crème | `#FFF4E8` | fond, surfaces |
| Turquoise | `#49C5C6` | tuiles, progression, secondaire |
| Jaune | `#FFC74B` | pièces, succès, indices |

- Typographie Material 3, forte hiérarchie, contrastes élevés.
- Cartes arrondies (16–22 dp), ombres douces, boutons épais faciles à toucher.
- Tuiles de lettres avec face en dégradé, base plus sombre (effet 3D), animation de
  pose et état « utilisée » atténué.
- Animations légères (fondu d'entrée, pop des lettres, barres animées) compatibles
  avec les appareils anciens.
- Mise en page adaptative (grille `GridCells.Adaptive`, `safeDrawingPadding`,
  contenu défilable).

Les maquettes de référence fournies (`app/src/assets/page-ex/main page.png` et
`profile.png`) ont guidé la composition (logo en héros, CTA rouge, carte de
progression, profil avec avatar, pièces, statistiques et succès). Les visuels de
`app/src/assets/image-quiz/` ont guidé le style photo et les tuiles.

---

## Tests effectués

Vérifications réellement exécutées sur ce dépôt :

| Vérification | Résultat |
|---|---|
| `.\gradlew.bat assembleDebug` | ✅ BUILD SUCCESSFUL |
| `.\gradlew.bat testDebugUnitTest` | ✅ 29 tests, 0 échec |
| Installation sur émulateur (`Pixel_10a`, API 37) | ✅ installée et lancée |
| Accueil : logo, pièces, « Jouer », « Continuer », progression | ✅ |
| Carte : 24 cartes, photos/placeholders, verrous, progression | ✅ |
| Gouvernorat : en-tête photo, 9 énigmes, états verrouillé/résolu | ✅ |
| Énigme : photo/indice, cases, tuiles, validation, retrait de lettre | ✅ |
| Réponse correcte : `CERAMIQUE`, +10 pièces, overlay « Bravo ! » | ✅ |
| Persistance après `force-stop` + relance | ✅ 450 pièces, 41/216 |
| Profil : statistiques et succès dérivés de la sauvegarde | ✅ |
| Réglages : navigation, textes, bouton retour | ✅ |
| Icône du lanceur présente dans l'APK (adaptative + mipmaps) | ✅ |
| Chargement d'une photo de gouvernorat (fixture temporaire) | ✅ (fixture supprimée du build final) |
| Placeholder quand `cover.*` est absent | ✅ (dégradé neutre, aucun crash) |

Le rendu a été contrôlé par hiérarchie d'interface (`uiautomator dump`) et analyse
pixel de captures d'écran, pas par comparaison visuelle côte à côte avec les maquettes.

---

## Limites connues

- **Photos d'énigmes : 134 / 216.** Les 82 énigmes restantes affichent une devinette
  textuelle honnête, sans image inventée.
- **Photos de gouvernorats : 0 / 24.** Les dossiers et le chargeur sont prêts ; il
  suffit d'ajouter les `cover.png`. En attendant, un placeholder neutre s'affiche.
- **Maquettes de référence** : la fidélité visuelle est une approximation construite à
  partir de la palette, des bandes de mise en page et des composants du projet ; elle
  n'a pas pu être validée par superposition pixel avec les PNG fournis.
- **Carte** : c'est une grille de gouvernorats, pas une carte géographique illustrée.
- **Audio** : sons et musique ne sont pas implémentés (indiqué dans Réglages).
- **Contenu culturel** : les explications non vérifiées sont marquées « à vérifier ».
- **Équilibrage** : récompenses et coût d'indice sont des valeurs prototype
  (`GameConfig`).

---

## Crédits et licence

- Code et contenu du jeu : projet KELMA 216.
- Photographies d'énigmes : Wikimedia Commons, une licence et un auteur réels par
  photo, stockés dans `tools/photo_manifest.json` et `PhotoManifest.kt`. Les crédits
  sont affichés dans l'écran d'énigme quand une photo est utilisée.
- Logo et icône : fournis par l'auteur du projet.
- Aucune image de gouvernorat n'est fournie ni téléchargée par ce dépôt.

Aucune licence n'a encore été choisie pour le code : ajoute un fichier `LICENSE` avant
une distribution publique. Les photographies tierces conservent leurs licences
respectives.

---

## Auteur

**Taha Gmir — Sewinx**

GitHub : [@sewinx07](https://github.com/sewinx07)

Projet : **KELMA 216 — Le jeu des mots tunisiens**

**Un pays. 24 gouvernorats. 216 mots à découvrir.**
