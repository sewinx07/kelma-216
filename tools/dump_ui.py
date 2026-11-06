import re
import sys
import xml.etree.ElementTree as ET

path = sys.argv[1]
tree = ET.parse(path)
root = tree.getroot()

rows = []
for node in root.iter("node"):
    text = node.get("text") or ""
    desc = node.get("content-desc") or ""
    if not text and not desc:
        continue
    bounds = node.get("bounds")
    rows.append((bounds, text, desc))

for bounds, text, desc in rows:
    print(f"{bounds} text={text!r} desc={desc!r}")
