#!/usr/bin/env python3
"""KELMA 216 - repair and report the photo manifest.

The PowerShell pipeline that produced the manifest could emit malformed JSON
(powerShell 5.1 ConvertTo-Json pipeline quirks), so this tool normalises the
file: it unwraps a {value: [...], Count: n} envelope if present, de-duplicates
by answer, and writes clean JSON.

Usage:
    python tools/fix_manifest.py          # repair + report
    python tools/fix_manifest.py --stats  # report only
"""

import json
import os
import sys

HERE = os.path.dirname(os.path.abspath(__file__))
MANIFEST = os.path.join(HERE, "photo_manifest.json")
REVIEW = os.path.join(HERE, "photo_review.json")
DRAWABLE = os.path.join(
    os.path.dirname(HERE), "app", "src", "main", "res", "drawable"
)


def load(path):
    if not os.path.exists(path):
        return []
    with open(path, "r", encoding="utf-8-sig") as fh:
        data = json.load(fh)
    # Unwrap the PowerShell envelope {value: [...], Count: n}
    if isinstance(data, dict) and "value" in data:
        data = data["value"]
    if isinstance(data, dict):
        data = [data]
    return data


def save(path, rows):
    with open(path, "w", encoding="utf-8") as fh:
        json.dump(rows, fh, ensure_ascii=False, indent=2)
        fh.write("\n")


def main():
    stats_only = "--stats" in sys.argv

    manifest = load(MANIFEST)
    review = load(REVIEW)

    # De-duplicate by answer, keeping the last occurrence (newest run wins).
    by_answer = {}
    for row in manifest:
        answer = row.get("answer")
        if answer:
            by_answer[answer] = row
    manifest = list(by_answer.values())

    if not stats_only:
        save(MANIFEST, manifest)
        save(REVIEW, review)

    # Cross-check the drawable files actually present.
    present = set()
    if os.path.isdir(DRAWABLE):
        for name in os.listdir(DRAWABLE):
            base, ext = os.path.splitext(name)
            if base.startswith("photo_") and ext.lower() in (".jpg", ".png"):
                present.add(base)

    missing_file = [r["answer"] for r in manifest if r.get("drawableName") not in present]
    orphans = sorted(present - {r.get("drawableName") for r in manifest})
    no_license = [r["answer"] for r in manifest if not r.get("license")]

    print("manifest entries      : %d" % len(manifest))
    print("unique answers        : %d" % len({r["answer"] for r in manifest}))
    print("review (no photo)     : %d" % len(review))
    print("drawable photo files  : %d" % len(present))
    print("manifest w/o file     : %d %s" % (len(missing_file), missing_file[:10]))
    print("files not in manifest : %d %s" % (len(orphans), orphans[:10]))
    print("entries w/o licence   : %d %s" % (len(no_license), no_license[:10]))


if __name__ == "__main__":
    main()
