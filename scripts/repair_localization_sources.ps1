[Console]::OutputEncoding = [System.Text.UTF8Encoding]::new($false)

$files = @()
$files += Get-ChildItem 'E:\Hytale mods\bank_defense_autogen\src\java\com\qubecore\bankdefense' -Recurse -File -Filter *.java | Select-Object -ExpandProperty FullName
$files += Get-ChildItem 'E:\Hytale mods\bank_defense_autogen\src\resources\defaults' -Recurse -File -Filter *.json | Select-Object -ExpandProperty FullName
$files += Get-ChildItem 'E:\Hytale mods\bank_defense_autogen\src\resources\Common\UI\Custom' -Recurse -File -Filter *.ui | Select-Object -ExpandProperty FullName
$files = $files | Where-Object { $_ -notlike '*BankDefenseTextSupport.java' }

$cp1251 = [System.Text.Encoding]::GetEncoding(1251)
$latin1 = [System.Text.Encoding]::GetEncoding(28591)
$ibm866 = [System.Text.Encoding]::GetEncoding(866)
$utf8 = [System.Text.Encoding]::UTF8
$safePunctuation = ',.!?:;+-/%()[]{}<>`"''=#_|'
$literalRegex = [regex]::new('"(?:\\.|[^"\\])*"')

function Decode-WithEncoding {
    param(
        [string]$Value,
        [System.Text.Encoding]$Encoding
    )

    if ([string]::IsNullOrWhiteSpace($Value)) {
        return $Value
    }

    try {
        return $utf8.GetString($Encoding.GetBytes($Value))
    } catch {
        return $Value
    }
}

function Get-SuspiciousScore {
    param([string]$Value)

    if ([string]::IsNullOrWhiteSpace($Value)) {
        return 100000
    }

    $score = 0
    $questionRun = 0
    $chars = $Value.ToCharArray()

    for ($i = 0; $i -lt $chars.Length; $i++) {
        $code = [int][char]$chars[$i]

        if ((($code -ge 0x0400 -and $code -le 0x040F) -or ($code -ge 0x0450 -and $code -le 0x045F)) -and $code -ne 0x0401 -and $code -ne 0x0451) {
            $score += 18
        }
        if ($code -eq 0x00D0 -or $code -eq 0x00D1 -or $code -eq 0x00C2 -or $code -eq 0xFFFD) {
            $score += 25
        }

        if ($chars[$i] -eq '?') {
            $questionRun++
        } else {
            if ($questionRun -ge 2) {
                $score += 15
            }
            $questionRun = 0
        }

        if ($i -lt $chars.Length - 1) {
            $nextCode = [int][char]$chars[$i + 1]
            if ((($code -eq 0x0420 -or $code -eq 0x0421) -and (($nextCode -ge 0x0410 -and $nextCode -le 0x044F) -or $nextCode -eq 0x0401 -or $nextCode -eq 0x0451))) {
                $score += 8
            }
            if ($code -eq 0x00D0 -or $code -eq 0x00D1) {
                $score += 8
            }
        }
    }

    if ($questionRun -ge 2) {
        $score += 15
    }

    return $score
}

function Get-ReadabilityScore {
    param([string]$Value)

    if ([string]::IsNullOrWhiteSpace($Value)) {
        return -100000
    }

    $score = 0
    foreach ($ch in $Value.ToCharArray()) {
        $code = [int][char]$ch
        if (($code -ge 0x0410 -and $code -le 0x044F) -or $code -eq 0x0401 -or $code -eq 0x0451) {
            $score += 4
        } elseif (($code -ge 0x0041 -and $code -le 0x005A) -or ($code -ge 0x0061 -and $code -le 0x007A) -or ($code -ge 0x0030 -and $code -le 0x0039)) {
            $score += 1
        } elseif ([char]::IsWhiteSpace($ch) -or $safePunctuation.Contains([string]$ch)) {
            $score += 1
        }
    }

    return $score - ((Get-SuspiciousScore $Value) * 5)
}

function Is-BetterCandidate {
    param(
        [string]$Candidate,
        [string]$CurrentBest
    )

    $candidateSuspicious = Get-SuspiciousScore $Candidate
    $bestSuspicious = Get-SuspiciousScore $CurrentBest
    if ($candidateSuspicious -lt $bestSuspicious) {
        return $true
    }
    if ($candidateSuspicious -gt $bestSuspicious) {
        return $false
    }
    return (Get-ReadabilityScore $Candidate) -gt ((Get-ReadabilityScore $CurrentBest) + 2)
}

function Repair-Fragment {
    param([string]$Value)

    if ([string]::IsNullOrWhiteSpace($Value)) {
        return $Value
    }

    $best = $Value
    for ($i = 0; $i -lt 6; $i++) {
        $cp1 = Decode-WithEncoding -Value $best -Encoding $cp1251
        $latin = Decode-WithEncoding -Value $best -Encoding $latin1
        $ibm = Decode-WithEncoding -Value $best -Encoding $ibm866

        $candidates = @(
            $best,
            $cp1,
            (Decode-WithEncoding -Value $cp1 -Encoding $cp1251),
            $latin,
            (Decode-WithEncoding -Value $latin -Encoding $cp1251),
            $ibm
        )

        $next = $best
        foreach ($candidate in $candidates) {
            if (Is-BetterCandidate -Candidate $candidate -CurrentBest $next) {
                $next = $candidate
            }
        }

        if ($next -eq $best) {
            break
        }

        $best = $next
    }

    return $best
}

foreach ($file in $files) {
    $text = [System.IO.File]::ReadAllText($file, [System.Text.Encoding]::UTF8)
    $changes = 0
    $updated = $literalRegex.Replace($text, {
        param($match)

        $literal = $match.Value
        $body = $literal.Substring(1, $literal.Length - 2)
        $repaired = Repair-Fragment $body

        if ($repaired -eq $body) {
            return $literal
        }

        $bodySuspicious = Get-SuspiciousScore $body
        $repairedSuspicious = Get-SuspiciousScore $repaired
        $bodyReadable = Get-ReadabilityScore $body
        $repairedReadable = Get-ReadabilityScore $repaired

        if ($repairedSuspicious -gt $bodySuspicious) {
            return $literal
        }
        if ($repairedSuspicious -eq $bodySuspicious -and $repairedReadable -le ($bodyReadable + 2)) {
            return $literal
        }

        $script:changes++
        $escaped = $repaired.Replace('\', '\\').Replace('"', '\"')
        return ('"' + $escaped + '"')
    })

    if ($changes -gt 0) {
        [System.IO.File]::WriteAllText($file, $updated, [System.Text.UTF8Encoding]::new($false))
    }

    Write-Output ((Split-Path $file -Leaf) + ': ' + $changes)
}
