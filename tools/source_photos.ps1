# KELMA 216 - Wikimedia Commons photo sourcing pipeline (offline build tool).
#
# For every puzzle subject it searches Wikimedia Commons restricted to Tunisia context,
# keeps only files whose licence allows reuse (CC0 / public domain / CC BY / CC BY-SA),
# requires a keyword from the subject to appear in the file title, downloads an 800px
# thumbnail into tools/photos and records provenance in tools/photo_manifest.json.
#
# Curated overrides: tools/photo_overrides.json maps an answer to an exact Commons file
# title (e.g. { "BRIK": "File:Brik Edennouni.JPG" }). Those win over search.
#
# Nothing is fabricated: a subject with no usable file is skipped and reported.
#
# Usage (from the repo root):
#   powershell -ExecutionPolicy Bypass -File tools/source_photos.ps1

param(
    [string]$RepoRoot = (Split-Path -Parent $PSScriptRoot),
    [string[]]$Only = @()
)

$ErrorActionPreference = "Stop"
$Api = "https://commons.wikimedia.org/w/api.php"
$OutDir = Join-Path $PSScriptRoot "photos"
$ManifestPath = Join-Path $PSScriptRoot "photo_manifest.json"
$ReviewPath = Join-Path $PSScriptRoot "photo_review.json"
$OverridesPath = Join-Path $PSScriptRoot "photo_overrides.json"

if (-not (Test-Path $OutDir)) { New-Item -ItemType Directory -Path $OutDir -Force | Out-Null }

$SubjectsPath = Join-Path $PSScriptRoot "subjects.json"
if (-not (Test-Path $SubjectsPath)) {
    throw "Missing $SubjectsPath (answer/subject list). Generate it from WordContent first."
}
$subjects = Get-Content $SubjectsPath -Raw | ConvertFrom-Json

$overrides = @{}
if (Test-Path $OverridesPath) {
    $o = Get-Content $OverridesPath -Raw | ConvertFrom-Json
    $o.PSObject.Properties | ForEach-Object { $overrides[$_.Name] = $_.Value }
}

# Stop-words we never require to match a file title.
$stopWords = @('of','the','a','an','de','la','le','les','un','une','des','el','al','tunisia','tunisien','tunisienne','dish','city','fort','train','port')

function Get-Keywords($subject) {
    if (-not $subject) { return @() }
    $parts = $subject -split '[^A-Za-z0-9]+'
    $parts | Where-Object { $_ -and ($_.Length -ge 3) -and ($stopWords -notcontains $_.ToLower()) } | Select-Object -Unique
}

function Get-UsableLicense($extmetadata) {
    $short = $extmetadata.LicenseShortName.value
    if (-not $short) { return $null }
    $s = $short.Trim()
    if ($s -match 'CC0') { return @{ label = "CC0"; ok = $true } }
    if ($s -match 'Public domain|PD-|No restrictions') { return @{ label = "Public domain"; ok = $true } }
    if ($s -match 'CC BY-SA') { return @{ label = $s; ok = $true } }
    if ($s -match 'CC BY') { return @{ label = $s; ok = $true } }
    return @{ label = $s; ok = $false }
}

function Strip-Html($text) {
    if (-not $text) { return $null }
    if ($text -is [array]) { $text = ($text -join ', ') }
    $t = $text -replace '<[^>]+>', ''
    $t = [System.Net.WebUtility]::HtmlDecode($t)
    return $t.Trim()
}

function Invoke-Commons($url) {
    for ($attempt = 1; $attempt -le 3; $attempt++) {
        try {
            return (Invoke-WebRequest -Uri $url -UseBasicParsing -TimeoutSec 40 `
                -Headers @{ "User-Agent" = "Kelma216PhotoBot/1.0 (offline puzzle content build)" }).Content | ConvertFrom-Json
        } catch {
            if ($attempt -eq 3) { return $null }
            Start-Sleep -Seconds 3
        }
    }
    return $null
}

function Page-IsUsableImage($page) {
    $info = $page.imageinfo | Select-Object -First 1
    if (-not $info) { return $null }
    if ($info.mime -notmatch '^image/(jpeg|png)$') { return $null }
    $lic = Get-UsableLicense $info.extmetadata
    if (-not $lic.ok) { return $null }
    $thumb = if ($info.thumburl) { $info.thumburl } else { $info.url }
    return @{
        title   = $page.title
        url     = $thumb
        license = $lic.label
        artist  = Strip-Html $info.extmetadata.Artist.value
        descUrl = $info.descriptionurl
    }
}

function Url-Encode($s) {
    # PowerShell 5.1's [uri]::EscapeDataString mangles non-ASCII; encode UTF-8 bytes
    # ourselves so accented file titles (é, è, ...) resolve correctly.
    $bytes = [System.Text.Encoding]::UTF8.GetBytes($s)
    $safe = @(32,34,37,38,43,47,58,59,60,61,63,64,91,93,94,96,123,124,125)
    ($bytes | ForEach-Object {
        if ($_ -lt 128 -and ($safe -notcontains $_)) { [char]$_ }
        else { '%' + ('{0:X2}' -f $_) }
    }) -join ''
}

function Get-ByTitle($title) {
    $t = Url-Encode $title
    $url = "${Api}?action=query&titles=$t&prop=imageinfo&iiprop=url|extmetadata|mime&iiurlwidth=800&format=json"
    $json = Invoke-Commons $url
    if (-not $json -or -not $json.query.pages) { return $null }
    $page = $json.query.pages.PSObject.Properties | ForEach-Object { $_.Value } | Select-Object -First 1
    if (-not $page -or $page.missing -ne $null) { return $null }
    return Page-IsUsableImage $page
}

function Search-Commons($query, $keywords) {
    $q = [uri]::EscapeDataString($query)
    $url = "${Api}?action=query&generator=search&gsrsearch=$q&gsrnamespace=6&gsrlimit=20" +
           "&prop=imageinfo&iiprop=url|extmetadata|mime&iiurlwidth=800&format=json"
    $json = Invoke-Commons $url
    if (-not $json -or -not $json.query -or -not $json.query.pages) { return $null }
    $pages = $json.query.pages.PSObject.Properties | ForEach-Object { $_.Value }
    foreach ($page in $pages) {
        $hit = Page-IsUsableImage $page
        if (-not $hit) { continue }
        $titleLower = $page.title.ToLower()
        $matched = $false
        foreach ($kw in $keywords) {
            if ($titleLower -like "*$($kw.ToLower())*") { $matched = $true; break }
        }
        if ($keywords.Count -gt 0 -and -not $matched) { continue }
        return $hit
    }
    return $null
}

function Find-Photo($subject, $keywords) {
    # Try several query templates in order of expected precision. The Tunisia-scoped
    # search biases toward the right country; subject- and keyword-only searches catch
    # generic nouns (DATTES, MANDARINE, PHARE...) that are not named in Tunisia files.
    $templates = @("$subject Tunisia")
    if ($subject -notmatch '(?i)tunis') { $templates += $subject }
    foreach ($kw in $keywords) { $templates += $kw }
    $seen = @{}
    foreach ($t in $templates) {
        if (-not $t) { continue }
        $key = $t.ToLower()
        if ($seen.ContainsKey($key)) { continue }
        $seen[$key] = $true
        $hit = Search-Commons $t $keywords
        if ($hit) { return $hit }
        Start-Sleep -Milliseconds 150
    }
    return $null
}

$manifest = @()
$review = @()
$total = @($subjects).Count
$i = 0

foreach ($entry in $subjects) {
    $i++
    $answer = $entry.answer
    $subject = $entry.subject
    if ($Only.Count -gt 0 -and ($Only -notcontains $answer)) { continue }
    if (-not $subject) {
        $review += [pscustomobject]@{ answer = $answer; subject = $null; reason = "no-subject" }
        Write-Host ("[{0}/{1}] {2}: no subject (text-only)" -f $i, $total, $answer)
        continue
    }

    $hit = $null
    if ($overrides.ContainsKey($answer)) {
        $hit = Get-ByTitle $overrides[$answer]
        if (-not $hit) {
            $review += [pscustomobject]@{ answer = $answer; subject = $subject; reason = "override-not-usable" }
            Write-Host ("[{0}/{1}] {2}: override unusable" -f $i, $total, $answer)
            continue
        }
    } else {
        $hit = Find-Photo $subject (Get-Keywords $subject)
    }

    if (-not $hit) {
        $review += [pscustomobject]@{ answer = $answer; subject = $subject; reason = "no-usable-photo" }
        Write-Host ("[{0}/{1}] {2}: no usable photo" -f $i, $total, $answer)
        Start-Sleep -Milliseconds 250
        continue
    }

    $drawableName = "photo_" + ($answer.ToLower())
    $ext = if ($hit.url -match '\.png') { ".png" } else { ".jpg" }
    $dest = Join-Path $OutDir ($drawableName + $ext)
    $downloaded = $false
    for ($try = 1; $try -le 3; $try++) {
        try {
            Invoke-WebRequest -Uri $hit.url -OutFile $dest -UseBasicParsing -TimeoutSec 60 `
                -Headers @{ "User-Agent" = "Kelma216PhotoBot/1.0 (offline puzzle content build)" } | Out-Null
            $downloaded = $true
            break
        } catch {
            Start-Sleep -Seconds (2 * $try)
        }
    }
    if (-not $downloaded) {
        $review += [pscustomobject]@{ answer = $answer; subject = $subject; reason = "download-failed" }
        Write-Host ("[{0}/{1}] {2}: download failed" -f $i, $total, $answer)
        continue
    }

    $manifest += [pscustomobject]@{
        answer       = $answer
        drawableName = $drawableName
        sourceUrl    = $hit.descUrl
        license      = $hit.license
        artist       = $hit.artist
        fileTitle    = $hit.title
    }
    Write-Host ("[{0}/{1}] {2}: OK ({3}) {4}" -f $i, $total, $answer, $hit.license, $hit.title)
    Start-Sleep -Milliseconds 250
}

if ($Only.Count -gt 0) {
    # Merged (incremental) mode: keep prior results for answers we did not retry.
    $prevManifest = @()
    if (Test-Path $ManifestPath) { $prevManifest = @(Get-Content $ManifestPath -Raw -Encoding UTF8 | ConvertFrom-Json) }
    $prevReview = @()
    if (Test-Path $ReviewPath) { $prevReview = @(Get-Content $ReviewPath -Raw -Encoding UTF8 | ConvertFrom-Json) }
    $retried = @{}; foreach ($e in $subjects) { if ($Only -contains $e.answer) { $retried[$e.answer] = $true } }
    $manifest = @($prevManifest | Where-Object { $_.answer -and -not $retried.ContainsKey($_.answer) }) + @($manifest)
    $review = @($prevReview | Where-Object { $_.answer -and -not $retried.ContainsKey($_.answer) }) + @($review)
    if ($manifest.Count -eq 0 -and $prevManifest.Count -gt 0) {
        throw "Refusing to write an empty manifest; -Only matched nothing. Nothing changed."
    }
}

# Write TSV first, then let Python produce well-formed JSON (PowerShell 5.1's
# ConvertTo-Json mangles arrays and non-ASCII).
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
Write-Host ("Manifest: {0} photos -> {1}" -f $manifest.Count, $ManifestPath)
Write-Host ("Review:   {0} without photo -> {1}" -f $review.Count, $ReviewPath)
