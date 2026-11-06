from PIL import Image
import sys

def band_report(path, red=(232, 42, 56), tol=60):
    im = Image.open(path).convert("RGB")
    w, h = im.size
    px = im.load()
    print("=" * 70)
    print(path, w, "x", h)
    prev = None
    start = 0
    for y in range(h):
        rs = gs = bs = 0
        redn = 0
        darkn = 0
        for x in range(0, w, max(1, w // 60)):
            r, g, b = px[x, y]
            rs += r; gs += g; bs += b
            if abs(r - red[0]) < tol and abs(g - red[1]) < tol and abs(b - red[2]) < tol:
                redn += 1
            if r < 90 and g < 90 and b < 90:
                darkn += 1
        n = len(range(0, w, max(1, w // 60)))
        avg = (rs // n, gs // n, bs // n)
        cls = "red" if redn / n > 0.4 else ("dark" if darkn / n > 0.3 else "bg")
        key = (cls, avg[0] // 24, avg[1] // 24, avg[2] // 24)
        if key != prev:
            if prev is not None:
                print("  y %3d-%3d  %-4s  #%02X%02X%02X" % (start, y - 1, prev[0], *prev_avg))
            prev = key
            prev_avg = avg
            start = y
    print("  y %3d-%3d  %-4s  #%02X%02X%02X" % (start, h - 1, prev[0], *prev_avg))

for p in sys.argv[1:]:
    band_report(p)
