$ErrorActionPreference = 'Stop'

$repoRoot = Split-Path -Parent $PSScriptRoot
$reportPath = Join-Path $repoRoot 'localization_audit_report.txt'

$rawCyrillicArgs = @(
    '-n',
    '\p{Cyrillic}',
    (Join-Path $repoRoot 'src\java'),
    (Join-Path $repoRoot 'src\resources'),
    '-g', '!**/BankDefenseLocalization.java',
    '-g', '!**/BankDefenseTextSupport.java',
    '-g', '!**/BankDefenseCommand.java',
    '-g', '!**/defaults/**'
)

$serverMetadataArgs = @(
    '-n',
    '"(Name|Description|InteractionHint)"',
    (Join-Path $repoRoot 'src\resources\Server\Item\Items\Utility\BankDefense')
)

$rawCyrillic = (& rg @rawCyrillicArgs) 2>$null
$serverMetadata = (& rg @serverMetadataArgs) 2>$null

$mojibakePatterns = @(
    ([char]0x0420).ToString() + '.',
    ([char]0x0421).ToString() + '.',
    ([char]0xFFFD).ToString()
)

$textFiles = Get-ChildItem (Join-Path $repoRoot 'src') -Recurse -File | Where-Object {
    $_.FullName -notlike '*BankDefenseLocalization.java' `
        -and $_.FullName -notlike '*BankDefenseTextSupport.java' `
        -and $_.FullName -notlike '*defaults*'
}

$mojibake = foreach ($file in $textFiles) {
    Select-String -Path $file.FullName -Pattern $mojibakePatterns | ForEach-Object {
        '{0}:{1}:{2}' -f $_.Path, $_.LineNumber, $_.Line.Trim()
    }
}

if ($rawCyrillic) {
    $rawCyrillic = $rawCyrillic | Where-Object {
        $_ -notmatch 'choose\(' `
            -and $_ -notmatch 'BankDefenseLocalization\.choose\(' `
            -and $_ -notmatch 'localizedNpcLabel\(' `
            -and $_ -notmatch 'DifficultyProfile\(' `
            -and $_ -notmatch 'issues\.add\(' `
            -and $_ -notmatch 'case "' `
            -and $_ -notmatch 'contains\(" ур\. "\)'
    }
}

$lines = @(
    'Bank Defense Localization Audit',
    ('Generated: {0:yyyy-MM-dd HH:mm:ss}' -f (Get-Date)),
    '',
    'Excluded:',
    '- BankDefenseLocalization.java',
    '- BankDefenseTextSupport.java',
    '- BankDefenseCommand.java',
    '- src/resources/defaults/**',
    '',
    'Raw Cyrillic outside the localization layer:',
    ($rawCyrillic | Out-String).Trim(),
    '',
    'Suspicious mojibake patterns:',
    ($mojibake | Out-String).Trim(),
    '',
    'Server item metadata (static, should default to English if not localizable):',
    ($serverMetadata | Out-String).Trim()
)

Set-Content -Path $reportPath -Value $lines -Encoding UTF8
Write-Output "Localization audit written to: $reportPath"
