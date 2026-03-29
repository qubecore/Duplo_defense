param()

$ErrorActionPreference = "Stop"

$projectRoot = (Resolve-Path (Join-Path $PSScriptRoot "..")).Path
$logPath = Join-Path $projectRoot "SETUP_LOG.md"
$modVersion = "0.2.1"
$modArtifactName = "BankDefense-$modVersion.jar"
$installArtifactName = "DuploTD-$modVersion.jar"

function Add-Log {
    param([string]$Message)
    $timestamp = Get-Date -Format "yyyy-MM-dd HH:mm:ss"
    Add-Content -Path $logPath -Value "- $timestamp $Message"
}

function Write-Utf8NoBom {
    param(
        [string]$Path,
        [string]$Content
    )
    $directory = Split-Path -Parent $Path
    if ($directory) {
        New-Item -ItemType Directory -Path $directory -Force | Out-Null
    }
    [System.IO.File]::WriteAllText(
        $Path,
        $Content,
        [System.Text.UTF8Encoding]::new($false)
    )
}

function Backup-IfExists {
    param(
        [string]$PathToBackup,
        [string]$BackupRoot
    )
    if (-not (Test-Path $PathToBackup)) {
        return $null
    }
    $suffix = Get-Date -Format "yyyyMMdd-HHmmss-fff"
    if ($BackupRoot) {
        New-Item -ItemType Directory -Path $BackupRoot -Force | Out-Null
        $leaf = Split-Path -Leaf $PathToBackup
        $backupPath = Join-Path $BackupRoot "$leaf.backup-$suffix"
    } else {
        $backupPath = "$PathToBackup.backup-$suffix"
    }
    $counter = 1
    while (Test-Path $backupPath) {
        if ($BackupRoot) {
            $backupPath = Join-Path $BackupRoot "$leaf.backup-$suffix-$counter"
        } else {
            $backupPath = "$PathToBackup.backup-$suffix-$counter"
        }
        $counter += 1
    }
    Move-Item -Path $PathToBackup -Destination $backupPath
    Add-Log "Created backup: $backupPath"
    return $backupPath
}

$hytaleRoot = "C:\Users\QubeCore\AppData\Roaming\Hytale"
$userDataRoot = Join-Path $hytaleRoot "UserData"
$modsRoot = Join-Path $userDataRoot "Mods"
$savesRoot = Join-Path $userDataRoot "Saves"
$installJarPath = Join-Path $modsRoot $installArtifactName
$savePaths = @()
$fallbackSavePaths = @(
    (Join-Path $savesRoot "Bank Defense"),
    (Join-Path $savesRoot "QubeCore Bank Defense")
)
$hytaleServerJar = Join-Path $hytaleRoot "install\release\package\game\latest\Server\HytaleServer.jar"
$javaExe = Join-Path $hytaleRoot "install\release\package\jre\latest\bin\java.exe"
$ecjJar = Join-Path $projectRoot "tools\ecj-3.44.0.jar"
$sourceJavaRoot = Join-Path $projectRoot "src\java"
$resourceRoot = Join-Path $projectRoot "src\resources"
$buildRoot = Join-Path $projectRoot "build"
$classesRoot = Join-Path $buildRoot "classes"
$stageRoot = Join-Path $buildRoot "stage"
$distRoot = Join-Path $projectRoot "dist"
$distJar = Join-Path $distRoot $modArtifactName
$backupRoot = Join-Path $projectRoot "install_backups"
$modBackupRoot = Join-Path $backupRoot "mods"
$saveConfigBackupRoot = Join-Path $backupRoot "save_configs"
$pluginConfigBackupRoot = Join-Path $backupRoot "plugin_configs"

Add-Log "Starting build_and_install.ps1."
Add-Log "Using Hytale root: $hytaleRoot"
Add-Log "Using server jar: $hytaleServerJar"
Add-Log "Using Java runtime: $javaExe"

if (-not (Test-Path $javaExe)) { throw "Java runtime not found: $javaExe" }
if (-not (Test-Path $hytaleServerJar)) { throw "HytaleServer.jar not found: $hytaleServerJar" }
if (-not (Test-Path $ecjJar)) { throw "ECJ jar not found: $ecjJar" }

New-Item -ItemType Directory -Path $distRoot -Force | Out-Null
New-Item -ItemType Directory -Path $modsRoot -Force | Out-Null

if (Test-Path $savesRoot) {
    foreach ($saveDir in Get-ChildItem -Path $savesRoot -Directory -ErrorAction SilentlyContinue) {
        $saveConfigCandidate = Join-Path $saveDir.FullName "config.json"
        $pluginConfigCandidate = Join-Path $saveDir.FullName "mods\QubeCore_BankDefense\config"
        $includeSave = $false
        if (Test-Path $saveConfigCandidate) {
            try {
                $saveConfigJson = Get-Content -Path $saveConfigCandidate -Raw | ConvertFrom-Json
                $hasMods = $saveConfigJson -and $null -ne $saveConfigJson.PSObject.Properties["Mods"]
                $hasBankDefenseMod = $hasMods -and $null -ne $saveConfigJson.Mods.PSObject.Properties["QubeCore:BankDefense"]
                $bankDefenseEnabled = $hasBankDefenseMod -and $saveConfigJson.Mods."QubeCore:BankDefense".Enabled
                if ($bankDefenseEnabled) {
                    $includeSave = $true
                }
            } catch {
                Add-Log "Skipped malformed save config while discovering saves: $saveConfigCandidate"
            }
        }
        if (-not $includeSave -and (Test-Path $pluginConfigCandidate)) {
            $includeSave = $true
        }
        if ($includeSave) {
            $savePaths += $saveDir.FullName
        }
    }
}

if ($savePaths.Count -eq 0) {
    $savePaths = $fallbackSavePaths
}
$savePaths = $savePaths | Select-Object -Unique
Add-Log ("Discovered save targets: " + (($savePaths | ForEach-Object { "'$_'" }) -join ", "))
if (Test-Path $buildRoot) {
    Remove-Item -Path $buildRoot -Recurse -Force
}
New-Item -ItemType Directory -Path $classesRoot -Force | Out-Null
New-Item -ItemType Directory -Path $stageRoot -Force | Out-Null

$sourceFiles = Get-ChildItem -Path $sourceJavaRoot -Recurse -Filter "*.java" | ForEach-Object { $_.FullName }
if ($sourceFiles.Count -eq 0) {
    throw "No Java source files found in $sourceJavaRoot"
}

Add-Log "Compiling Java plugin sources from $sourceJavaRoot"
& $javaExe -jar $ecjJar -17 -encoding UTF-8 -proc:none -cp $hytaleServerJar -d $classesRoot $sourceFiles
if ($LASTEXITCODE -ne 0) {
    throw "Java compilation failed."
}
Add-Log "Compiled plugin classes into $classesRoot"

Copy-Item -Path (Join-Path $classesRoot "*") -Destination $stageRoot -Recurse -Force
Copy-Item -Path (Join-Path $resourceRoot "*") -Destination $stageRoot -Recurse -Force

$legacyTextureRoots = @(
    (Join-Path $stageRoot "Common\Textures\Utility"),
    (Join-Path $stageRoot "Common\Resources\Utility\Invisible_1x1.png")
)
foreach ($legacyPath in $legacyTextureRoots) {
    if (Test-Path $legacyPath) {
        Remove-Item -Path $legacyPath -Recurse -Force
        Add-Log "Removed legacy staging asset: $legacyPath"
    }
}

if (Test-Path $distJar) {
    Remove-Item -Path $distJar -Force
}

Add-Type -AssemblyName System.IO.Compression
Add-Type -AssemblyName System.IO.Compression.FileSystem
$fileStream = [System.IO.File]::Open($distJar, [System.IO.FileMode]::Create)
try {
    $archive = [System.IO.Compression.ZipArchive]::new($fileStream, [System.IO.Compression.ZipArchiveMode]::Create, $false)
    try {
        Get-ChildItem -Path $stageRoot -Recurse -File | ForEach-Object {
            $relativePath = $_.FullName.Substring($stageRoot.Length).TrimStart("\", "/") -replace "\\", "/"
            [System.IO.Compression.ZipFileExtensions]::CreateEntryFromFile(
                $archive,
                $_.FullName,
                $relativePath,
                [System.IO.Compression.CompressionLevel]::Optimal
            ) | Out-Null
        }
    } finally {
        $archive.Dispose()
    }
} finally {
    $fileStream.Dispose()
}
Add-Log "Packaged plugin jar at $distJar"

$zip = [IO.Compression.ZipFile]::OpenRead($distJar)
try {
    $manifestEntry = $zip.Entries | Where-Object { $_.FullName -eq "manifest.json" }
    $classEntry = $zip.Entries | Where-Object { $_.FullName -eq "com/qubecore/bankdefense/BankDefensePlugin.class" }
    $defaultsEntry = $zip.Entries | Where-Object { $_.FullName -eq "defaults/game_rules.json" }
    if ($null -eq $manifestEntry -or $null -eq $classEntry -or $null -eq $defaultsEntry) {
        throw "Packaged jar is missing one or more required entries."
    }
} finally {
    $zip.Dispose()
}
Add-Log "Verified packaged jar contains manifest, class, and default data entries with Java-compatible paths."

@("BankDefense-*.jar", "DuploTD-*.jar") | ForEach-Object {
    Get-ChildItem -Path $modsRoot -Filter $_ -ErrorAction SilentlyContinue | ForEach-Object {
        Backup-IfExists -PathToBackup $_.FullName -BackupRoot $modBackupRoot | Out-Null
    }
}
Copy-Item -Path $distJar -Destination $installJarPath -Force
Add-Log "Installed mod jar to $installJarPath"

$reportedSaveConfig = "not found"
foreach ($savePath in $savePaths) {
    if (-not (Test-Path $savePath)) {
        Add-Log "Skipped save config update because save was not found: $savePath"
        continue
    }
    $saveConfigPath = Join-Path $savePath "config.json"
    $pluginConfigRoot = Join-Path $savePath "mods\QubeCore_BankDefense\config"
    $existingConfigText = if (Test-Path $saveConfigPath) { Get-Content -Path $saveConfigPath -Raw } else { $null }
    if ($existingConfigText -ne $null) {
        Backup-IfExists -PathToBackup $saveConfigPath -BackupRoot $saveConfigBackupRoot | Out-Null
        $saveConfig = $existingConfigText | ConvertFrom-Json
    } else {
        $saveConfig = [pscustomobject]@{
            Mods = [pscustomobject]@{}
            Version = 4
        }
    }

    if ($null -eq $saveConfig.PSObject.Properties["Mods"]) {
        $saveConfig | Add-Member -MemberType NoteProperty -Name "Mods" -Value ([pscustomobject]@{})
    }
    if ($null -eq $saveConfig.Mods.PSObject.Properties["QubeCore:BankDefense"]) {
        $saveConfig.Mods | Add-Member -MemberType NoteProperty -Name "QubeCore:BankDefense" -Value ([pscustomobject]@{ Enabled = $true })
    } else {
        $saveConfig.Mods."QubeCore:BankDefense".Enabled = $true
    }

    Write-Utf8NoBom -Path $saveConfigPath -Content ($saveConfig | ConvertTo-Json -Depth 20)
    Add-Log "Enabled QubeCore:BankDefense in save config at $saveConfigPath"
    if ($reportedSaveConfig -eq "not found") {
        $reportedSaveConfig = $saveConfigPath
    }

    New-Item -ItemType Directory -Path $pluginConfigRoot -Force | Out-Null
    @("game_rules.json", "towers.json", "enemies.json", "modules.json", "waves.json", "branding.json", "contracts.json", "progression_tree.json") | ForEach-Object {
        $sourceDefault = Join-Path $resourceRoot "defaults\$_"
        $targetConfig = Join-Path $pluginConfigRoot $_
        if (Test-Path $targetConfig) {
            Backup-IfExists -PathToBackup $targetConfig -BackupRoot $pluginConfigBackupRoot | Out-Null
        }
        Copy-Item -Path $sourceDefault -Destination $targetConfig -Force
        Add-Log "Synced plugin config to $targetConfig"
    }
}

Add-Log "Installation complete."

Write-Host ""
Write-Host "Installed mod : $installJarPath"
Write-Host "Save config : $reportedSaveConfig"
