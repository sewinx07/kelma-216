# KELMA 216 - fetch specific curated Commons files and merge them into the manifest.
#
# Used to repair entries lost to network throttling or to pin verified files for
# subjects the automatic search could not resolve. Provenance (licence, author,
# source URL) is read from the Commons API - never invented.
#
# Usage: powershell -ExecutionPolicy Bypass -File tools/fetch_specific.ps1

$ErrorActionPreference = "Stop"
$Api = "https://commons.wikimedia.org/w/api.php"
$OutDir = Join-Path $PSScriptRoot "photos"
$ManifestPath = Join-Path $PSScriptRoot "photo_manifest.json"
$ReviewPath = Join-Path $PSScriptRoot "photo_review.json"
$OverridesPath = Join-Path $PSScriptRoot "photo_overrides.json"
$SubjectsPath = Join-Path $PSScriptRoot "subjects.json"

if (-not (Test-Path $OutDir)) { New-Item -ItemType Directory -Path $OutDir -Force | Out-Null }

$overrides = @{}
$o = Get-Content $OverridesPath -Raw -Encoding UTF8 | ConvertFrom-Json
$o.PSObject.Properties | ForEach-Object { $overrides[$_.Name] = $_.Value }

$subjects = @(Get-Content $SubjectsPath -Raw -Encoding UTF8 | ConvertFrom-Json)

function Url-Encode($s) {
    $bytes = [System.Text.Encoding]::UTF8.GetBytes($s)
    $safe = @(32,34,37,38,43,47,58,59,60,61,63,64,91,93,94,96,123,124,125)
    ($bytes | ForEach-Object {
        if ($_ -lt 128 -and ($safe -notcontains $_)) { [char]$_ } else { '%' + ('{0:X2}' -f $_) }
    }) -join ''
}

function Strip-Html($text) {
    if (-not $text) { return $null }
    if ($text -is [array]) { $text = ($text -join ', ') }
    $t = $text -replace '<[^>]+>', ''
    $t = [System.Net.WebUtility]::HtmlDecode($t)
    return $t.Trim()
}

$manifest = @()
if (Test-Path $ManifestPath) { $manifest = @(Get-Content $ManifestPath -Raw -Encoding UTF8 | ConvertFrom-Json) }
$review = @()
if (Test-Path $ReviewPath) { $review = @(Get-Content $ReviewPath -Raw -Encoding UTF8 | ConvertFrom-Json) }

foreach ($answer in ($overrides.Keys | Sort-Object)) {
    $title = $overrides[$answer]
    $enc = Url-Encode $title
    $url = "${Api}?action=query&titles=$enc&prop=imageinfo&iiprop=url|extmetadata|mime&iiurlwidth=800&format=json"
    $json = $null
    for ($a = 1; $a -le 3; $a++) {
        try {
            $json = (Invoke-WebRequest -Uri $url -UseBasicParsing -TimeoutSec 40 `
                -Headers @{ "User-Agent" = "Kelma216PhotoBot/1.0 (offline puzzle content build)" }).Content | ConvertFrom-Json
            break
        } catch { Start-Sleep -Seconds 3 }
    }
    if (-not $json -or -not $json.query.pages) { Write-Host "$answer : API failed"; continue }
    $page = $json.query.pages.PSObject.Properties | ForEach-Object { $_.Value } | Select-Object -First 1
    if (-not $page -or $page.missing -ne $null) { Write-Host "$answer : missing on Commons"; continue }
    $info = $page.imageinfo | Select-Object -First 1
    if ($info.mime -notmatch '^image/(jpeg|png)$') { Write-Host "$answer : not an image ($($info.mime))"; continue }
    $lic = $info.extmetadata.LicenseShortName.value
    if ($lic -notmatch 'CC0|Public domain|PD-|CC BY') { Write-Host "$answer : licence not reusable ($lic)"; continue }

    $thumb = if ($info.thumburl) { $info.thumburl } else { $info.url }
    $drawableName = "photo_" + $answer.ToLower()
    $ext = if ($thumb -match '\.png') { ".png" } else { ".jpg" }
    $dest = Join-Path $OutDir ($drawableName + $ext)
    try {
        Invoke-WebRequest -Uri $thumb -OutFile $dest -UseBasicParsing -TimeoutSec 60 `
            -Headers @{ "User-Agent" = "Kelma216PhotoBot/1.0 (offline puzzle content build)" } | Out-Null
    } catch {
        Write-Host "$answer : download failed"
        continue
    }

    $entry = [pscustomobject]@{
        answer       = $answer
        drawableName = $drawableName
        sourceUrl    = $info.descriptionurl
        license      = $lic
        artist       = (Strip-Html $info.extmetadata.Artist.value)
        fileTitle    = $page.title
    }
    $manifest = @($manifest | Where-Object { $_.answer -ne $answer }) + @($entry)
    $review = @($review | Where-Object { $_.answer -ne $answer })
    Write-Host ("{0}: OK ({1}) {2}" -f $answer, $lic, $page.title)
    Start-Sleep -Milliseconds 400
}

$manifest = @($manifest)
$review = @($review)
$tsv = Join-Path $PSScriptRoot "_manifest.tsv"
$lines = New-Object System.Collections.Generic.List[string]
foreach ($r in $manifest) {
    $f = @($r.answer, $r.drawableName, $r.sourceUrl, $r.license, $r.artist, $r.fileTitle) |
        ForEach-Object { ($_ -replace "`t", " " -replace "`r?`n", " ") }
    $lines.Add(($f -join "`t"))
}
Set-Content -Path $tsv -Value ($lines -join "`n") -Encoding UTF8

$tsvR = Join-Path $PSScriptRoot "_review.tsv"
$linesR = New-Object System.Collections.Generic.List[string]
foreach ($r in $review) {
    $f = @($r.answer, $r.subject, $r.reason) | ForEach-Object { ($_ -replace "`t", " " -replace "`r?`n", " ") }
    $linesR.Add(($f -join "`t"))
}
Set-Content -Path $tsvR -Value ($linesR -join "`n") -Encoding UTF8

$py = (Get-Command python -ErrorAction SilentlyContinue).Source
if ($py) {
    & $py (Join-Path $PSScriptRoot "tsv_to_json.py") $tsv $ManifestPath $tsvR $ReviewPath
} else {
    Write-Warning "python not found; leaving TSV files in place"
}
Write-Host ""
Write-Host ("Manifest: {0} photos, {1} unique answers" -f $manifest.Count, (@($manifest.answer | Sort-Object -Unique)).Count)
Write-Host ("Review:   {0} without photo" -f $review.Count)
