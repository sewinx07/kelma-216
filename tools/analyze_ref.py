from PIL import Image
import sys, os

def nearest_name(rgb):
    palette = {
        "cream": (255, 244, 232),
        "red": (232, 42, 56),
        "midnight": (21, 42, 81),
        "turquoise": (73, 197, 198),
        "yellow": (255, 199, 75),
        "white": (255, 255, 255),
        "black": (0, 0, 0),
        "grey": (128, 128, 128),
    }
    best, bd = "?", 1e9
    for n, c in palette.items():
        d = sum((a - b) ** 2 for a, b in zip(rgb, c))
        if d < bd:
            bd, best = d, n
    return best

def analyze(path):
    im = Image.open(path).convert("RGB")
    w, h = im.size
    print("=" * 60)
    print(os.path.basename(path), "size:", w, "x", h, "ratio", round(w / h, 3))
    # dominant colours
    small = im.resize((160, int(160 * h / w)))
    q = small.quantize(colors=10, method=Image.Quantize.MEDIANCUT).convert("RGB")
    counts = {}
    for px in q.getdata():
        counts[px] = counts.get(px, 0) + 1
    total = sum(counts.values())
    print("dominant colours:")
    for c, n in sorted(counts.items(), key=lambda kv: -kv[1])[:8]:
        print("   #%02X%02X%02X" % c, "(%5.1f%%)" % (100 * n / total), nearest_name(c))
    # layout grid 12 rows x 8 cols
    rows, cols = 12, 8
    print("layout grid (12 rows x 8 cols):")
    for r in range(rows):
        line = []
        for c in range(cols):
            box = (int(c * w / cols), int(r * h / rows),
                   int((c + 1) * w / cols), int((r + 1) * h / rows))
            region = im.crop(box).resize((1, 1)).getpixel((0, 0))
            line.append("#%02X%02X%02X" % region)
        print("  r%02d " % r, " ".join(line))
    print()

for p in sys.argv[1:]:
    analyze(p)
