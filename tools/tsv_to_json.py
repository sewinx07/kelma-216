#!/usr/bin/env python3
"""Convert the pipeline's TSV output into well-formed JSON.

Usage: python tools/tsv_to_json.py <manifest.tsv> <manifest.json> [review.tsv] [review.json]
"""

import json
import sys


def read_tsv(path, fields):
    rows = []
    try:
        with open(path, "r", encoding="utf-8-sig") as fh:
            for line in fh:
                line = line.rstrip("\n")
                if not line.strip():
                    continue
                parts = line.split("\t")
                parts += [""] * (len(fields) - len(parts))
                rows.append(dict(zip(fields, parts)))
    except FileNotFoundError:
        pass
    return rows


def main():
    if len(sys.argv) < 3:
        print(__doc__)
        return 1

    manifest_fields = ["answer", "drawableName", "sourceUrl", "license", "artist", "fileTitle"]
    manifest = read_tsv(sys.argv[1], manifest_fields)
    # Keep the first row per answer, but a later row with a real licence wins.
    best = {}
    for row in manifest:
        answer = row["answer"]
        if not answer:
            continue
        if answer not in best or (not best[answer]["license"] and row["license"]):
            best[answer] = row
    manifest = list(best.values())

    with open(sys.argv[2], "w", encoding="utf-8") as fh:
        json.dump(manifest, fh, ensure_ascii=False, indent=2)
        fh.write("\n")

    if len(sys.argv) >= 5:
        review_fields = ["answer", "subject", "reason"]
        review = read_tsv(sys.argv[3], review_fields)
        seen = set()
        unique = []
        for row in review:
            key = (row["answer"], row["subject"])
            if key in seen:
                continue
            seen.add(key)
            unique.append(row)
        with open(sys.argv[4], "w", encoding="utf-8") as fh:
            json.dump(unique, fh, ensure_ascii=False, indent=2)
            fh.write("\n")
        print("review rows: %d" % len(unique))

    print("manifest rows: %d" % len(manifest))
    return 0


if __name__ == "__main__":
    sys.exit(main())
