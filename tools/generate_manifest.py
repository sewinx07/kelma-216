#!/usr/bin/env python3
"""Generate PhotoManifest.kt from tools/photo_manifest.json.

Only entries whose drawable file actually exists are emitted, so the Kotlin file can
never reference a missing R.drawable symbol.
"""

import json
import os

BASE = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
MANIFEST = os.path.join(BASE, "tools", "photo_manifest.json")
DRAWABLE = os.path.join(BASE, "app", "src", "main", "res", "drawable")
OUT = os.path.join(
    BASE, "app", "src", "main", "java", "com", "example", "kelma216",
    "data", "content", "PhotoManifest.kt",
)


def kt(s):
    if s is None:
        return "null"
    s = str(s).replace("\\", "\\\\").replace('"', '\\"')
    s = s.replace("\r", " ").replace("\n", " ")
    return '"' + s + '"'


def main():
    rows = json.load(open(MANIFEST, encoding="utf-8-sig"))
    if isinstance(rows, dict) and "value" in rows:
        rows = rows["value"]
    present = {
        os.path.splitext(n)[0]
        for n in os.listdir(DRAWABLE)
        if n.lower().endswith((".jpg", ".png"))
    }

    by_answer = {}
    for r in rows:
        name = r.get("drawableName")
        if name in present and r.get("answer"):
            by_answer[r["answer"]] = r

    lines = []
    lines.append("package com.example.kelma216.data.content")
    lines.append("")
    lines.append("import com.example.kelma216.R")
    lines.append("")
    lines.append("/**")
    lines.append(" * Clue photo look-up table, generated from tools/photo_manifest.json")
    lines.append(" * by tools/generate_manifest.py. Do not edit by hand.")
    lines.append(" *")
    lines.append(" * Every entry here points at a drawable file that is actually bundled and")
    lines.append(" * carries its real Wikimedia Commons source, licence and author. An answer")
    lines.append(" * missing here has no photo yet: the puzzle stays playable and shows a")
    lines.append(" * text prompt instead. Nothing is fabricated.")
    lines.append(" */")
    lines.append("object PhotoManifest {")
    lines.append("")
    lines.append("    private val assets: Map<String, PhotoAsset> = mapOf(")

    for answer in sorted(by_answer):
        r = by_answer[answer]
        lines.append("        %s to PhotoAsset(" % kt(answer))
        lines.append("            answer = %s," % kt(answer))
        lines.append("            drawableRes = R.drawable.%s," % r["drawableName"])
        lines.append("            sourceUrl = %s," % kt(r.get("sourceUrl")))
        lines.append("            license = %s," % kt(r.get("license")))
        lines.append("            artist = %s," % kt(r.get("artist")))
        lines.append("        ),")
    lines.append("    )")
    lines.append("")
    lines.append("    fun forAnswer(answer: String): PhotoAsset? = assets[answer.uppercase()]")
    lines.append("")
    lines.append("    val withPhotoCount: Int get() = assets.size")
    lines.append("")
    lines.append("    fun hasPhoto(answer: String): Boolean = assets.containsKey(answer.uppercase())")
    lines.append("}")
    lines.append("")

    with open(OUT, "w", encoding="utf-8") as fh:
        fh.write("\n".join(lines))
    print("wrote %d photo entries -> %s" % (len(by_answer), OUT))


if __name__ == "__main__":
    main()
