import os
import re

base = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
src = open(
    os.path.join(base, "app", "src", "main", "java", "com", "example", "kelma216",
                 "data", "content", "Governorates.kt"),
    encoding="utf-8",
).read()
ids = re.findall(r'Governorate\("([a-z_]+)"', src)
print("ids", len(ids), ids)

overrides = {"le_kef": "kef"}
exts = ["png", "jpg", "jpeg", "webp"]

root = os.path.join(base, "app", "src", "assets", "governorates")
dirs = sorted(os.listdir(root))
print("folders", len(dirs), dirs)

problems = []
for gid in ids:
    slug = overrides.get(gid, gid.replace("_", "-"))
    if not os.path.isdir(os.path.join(root, slug)):
        problems.append((gid, slug))
    found = [e for e in exts if os.path.exists(os.path.join(root, slug, "cover." + e))]
    print(f"{gid:14s} -> {slug:14s} covers={found}")

print("missing folders:", problems)
