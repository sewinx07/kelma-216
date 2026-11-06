import json, os

base = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
man_path = os.path.join(base, "tools", "photo_manifest.json")
draw = os.path.join(base, "app", "src", "main", "res", "drawable")

rows = json.load(open(man_path, encoding="utf-8-sig"))
if isinstance(rows, dict) and "value" in rows:
    rows = rows["value"]

present = {
    os.path.splitext(n)[0]
    for n in os.listdir(draw)
    if n.lower().endswith((".jpg", ".png"))
}

kept, dropped = [], []
for r in rows:
    (kept if r.get("drawableName") in present else dropped).append(r)

json.dump(kept, open(man_path, "w", encoding="utf-8"), ensure_ascii=False, indent=2)
print("kept", len(kept), "dropped", [r.get("answer") for r in dropped])
