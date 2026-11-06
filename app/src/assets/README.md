# KELMA 216 — assets manuels

Ce dossier est empaqueté dans l'APK (voir `sourceSets` dans `app/build.gradle.kts`).
Aucun code Kotlin n'est à modifier pour ajouter une image.

## Photos des gouvernorats

Placez une photo par gouvernorat ici :

```
app/src/assets/governorates/<slug>/cover.png
```

Les 24 dossiers existent déjà. `<slug>` vaut exactement :

| Gouvernorat | dossier |
|---|---|
| Tunis | `tunis` |
| Ariana | `ariana` |
| Ben Arous | `ben-arous` |
| Manouba | `manouba` |
| Nabeul | `nabeul` |
| Zaghouan | `zaghouan` |
| Bizerte | `bizerte` |
| Béja | `beja` |
| Jendouba | `jendouba` |
| Le Kef | `kef` |
| Siliana | `siliana` |
| Sousse | `sousse` |
| Monastir | `monastir` |
| Mahdia | `mahdia` |
| Sfax | `sfax` |
| Kairouan | `kairouan` |
| Kasserine | `kasserine` |
| Sidi Bouzid | `sidi-bouzid` |
| Gabès | `gabes` |
| Médenine | `medenine` |
| Tataouine | `tataouine` |
| Gafsa | `gafsa` |
| Tozeur | `tozeur` |
| Kébili | `kebili` |

- **Format conseillé** : PNG (le nom exact `cover.png`), JPG/JPEG et WEBP sont aussi
  acceptés (le chargeur essaie `cover.png`, `cover.jpg`, `cover.jpeg`, `cover.webp`).
- **Ratio** : paysage 4:3 ou 16:9. L'image est recadrée (`ContentScale.Crop`) sans
  déformation.
- **Remplacer** : écrasez simplement le fichier, puis relancez le build.
- **Absence** : si `cover.*` manque, un visuel de secours neutre s'affiche — jamais
  l'image d'un autre gouvernorat, et l'application ne plante pas.

## Logo et icône

- `logo/logo_kelma.png` : logo affiché sur l'accueil (copie dans `res/drawable-nodpi`).
- `logo/icon_kelma.png` : icône du lanceur (déclinée en mipmaps + icône adaptative).

Ces fichiers d'origine ne sont jamais modifiés par le build. Pour les mettre à jour,
remplacez-les puis lancez `python tools/prepare_assets.py`.

## Images du quiz

`image-quiz/` contient les visuels de référence du jeu. Les photos d'énigmes réellement
utilisées par l'application sont dans `app/src/main/res/drawable/` (préfixe `photo_`).
