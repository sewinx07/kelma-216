#!/usr/bin/env python3
"""Prepare KELMA 216 art: logo drawable, launcher icons, governorate folders.

- Copies the supplied logo into res/drawable-nodpi for Compose.
- Builds legacy mipmap launcher icons and an adaptive-icon foreground from the
  supplied square icon. The originals in app/src/assets/logo are never modified.
- Creates app/src/assets/governorates/<slug>/ so covers can be dropped in by hand.

Run: python tools/prepare_assets.py
"""

import os
import shutil

from PIL import Image, ImageDraw

BASE = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
SRC = os.path.join(BASE, "app", "src", "assets")
RES = os.path.join(BASE, "app", "src", "main", "res")
NODPI = os.path.join(RES, "drawable-nodpi")

CREAM = (255, 244, 232, 255)

SLUGS = [
    "tunis", "ariana", "ben-arous", "manouba", "nabeul", "zaghouan", "bizerte",
    "beja", "jendouba", "kef", "siliana", "sousse", "monastir", "mahdia", "sfax",
    "kairouan", "kasserine", "sidi-bouzid", "gabes", "medenine", "tataouine",
    "gafsa", "tozeur", "kebili",
]

DENSITIES = {"mdpi": 48, "hdpi": 72, "xhdpi": 96, "xxhdpi": 144, "xxxhdpi": 192}


def ensure_dir(path):
    os.makedirs(path, exist_ok=True)


def rounded_mask(size, radius, circle=False):
    mask = Image.new("L", (size, size), 0)
    d = ImageDraw.Draw(mask)
    if circle:
        d.ellipse((0, 0, size - 1, size - 1), fill=255)
    else:
        d.rounded_rectangle((0, 0, size - 1, size - 1), radius=radius, fill=255)
    return mask


def place_icon(icon, canvas_size, inset_ratio, bg=None, radius_ratio=0.22, circle=False):
    """Draw icon centred on a canvas of canvas_size, keeping it inside the safe zone."""
    canvas = Image.new("RGBA", (canvas_size, canvas_size), (0, 0, 0, 0))
    if bg is not None:
        canvas.paste(Image.new("RGBA", (canvas_size, canvas_size), bg),
                     (0, 0), rounded_mask(canvas_size, int(canvas_size * radius_ratio), circle))
    inner = int(canvas_size * (1 - 2 * inset_ratio))
    art = icon.copy()
    art.thumbnail((inner, inner), Image.LANCZOS)
    x = (canvas_size - art.width) // 2
    y = (canvas_size - art.height) // 2
    canvas.alpha_composite(art, (x, y))
    return canvas


def main():
    ensure_dir(NODPI)
    logo = Image.open(os.path.join(SRC, "logo", "logo_kelma.png")).convert("RGBA")
    icon = Image.open(os.path.join(SRC, "logo", "icon_kelma.png")).convert("RGBA")

    # --- Logo for Compose (downscale the huge original for a smaller APK) ---
    logo_small = logo.copy()
    logo_small.thumbnail((900, 900), Image.LANCZOS)
    logo_small.save(os.path.join(NODPI, "logo_kelma.png"), optimize=True)
    print("logo_kelma.png ->", logo_small.size)

    # --- Adaptive icon foreground (432px, art within the 66% safe zone) ---
    fg = place_icon(icon, 432, inset_ratio=0.19, bg=None)
    fg.save(os.path.join(NODPI, "ic_launcher_foreground.png"), optimize=True)
    print("ic_launcher_foreground.png 432x432")

    # Remove the template vector foreground so the bitmap name does not collide.
    vector_fg = os.path.join(RES, "drawable", "ic_launcher_foreground.xml")
    if os.path.exists(vector_fg):
        os.remove(vector_fg)

    # Solid cream adaptive background.
    with open(os.path.join(RES, "drawable", "ic_launcher_background.xml"), "w", encoding="utf-8") as fh:
        fh.write(
            '<?xml version="1.0" encoding="utf-8"?>\n'
            '<shape xmlns:android="http://schemas.android.com/apk/res/android" '
            'android:shape="rectangle">\n'
            '    <solid android:color="#FFF4E8" />\n'
            '</shape>\n'
        )

    # --- Legacy mipmaps ---
    for density, px in DENSITIES.items():
        folder = os.path.join(RES, "mipmap-" + density)
        ensure_dir(folder)
        # Drop the template webp icons so there is exactly one icon per density.
        for old in ("ic_launcher.webp", "ic_launcher_round.webp"):
            p = os.path.join(folder, old)
            if os.path.exists(p):
                os.remove(p)
        square = place_icon(icon, px, inset_ratio=0.10, bg=CREAM, radius_ratio=0.22)
        square.save(os.path.join(folder, "ic_launcher.png"), optimize=True)
        round_icon = place_icon(icon, px, inset_ratio=0.12, bg=CREAM, circle=True)
        round_icon.save(os.path.join(folder, "ic_launcher_round.png"), optimize=True)
        print("mipmap-%s ic_launcher %dpx" % (density, px))

    # --- Governorate folders ---
    gov_root = os.path.join(SRC, "governorates")
    ensure_dir(gov_root)
    for slug in SLUGS:
        d = os.path.join(gov_root, slug)
        ensure_dir(d)
        keep = os.path.join(d, ".gitkeep")
        if not os.path.exists(keep):
            open(keep, "w", encoding="utf-8").close()
    print("governorate folders:", len(SLUGS))

    # --- Documentation for the art folders ---
    readme = os.path.join(SRC, "README.md")
    with open(readme, "w", encoding="utf-8") as fh:
        fh.write(GOV_README)


GOV_README = """# KELMA 216 — assets manuels

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
"""


if __name__ == "__main__":
    main()
