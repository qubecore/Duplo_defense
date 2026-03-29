Set-StrictMode -Version Latest
$ErrorActionPreference = 'Stop'

$scriptRoot = Split-Path -Parent $MyInvocation.MyCommand.Path
$projectRoot = Split-Path -Parent $scriptRoot
$outputDir = Join-Path $projectRoot 'src\resources\Common\Sounds\BankDefense'

function Resolve-Ffmpeg {
    $candidates = @()
    $command = Get-Command ffmpeg -ErrorAction SilentlyContinue
    if ($command) {
        $candidates += $command.Source
    }
    $candidates += @(
        'C:\Program Files\Common Files\AEJuice\ffmpeg.exe',
        'C:\Program Files\BlueStacks_nxt\ffmpeg.exe',
        'C:\Program Files\Common Files\AEJuice\NeonMind AI\Tools\ffmpeg.exe',
        'C:\Users\QubeCore\AppData\Local\Programs\Cliply\resources\binaries\ffmpeg.exe'
    )
    foreach ($candidate in $candidates | Select-Object -Unique) {
        if ($candidate -and (Test-Path $candidate)) {
            return $candidate
        }
    }
    throw 'ffmpeg was not found on this machine.'
}

function New-OggMix {
    param(
        [Parameter(Mandatory = $true)][string]$Ffmpeg,
        [Parameter(Mandatory = $true)][string]$Name,
        [Parameter(Mandatory = $true)][string[]]$Inputs,
        [Parameter(Mandatory = $true)][string]$Filter
    )

    $outPath = Join-Path $outputDir $Name
    $args = @('-y')
    foreach ($input in $Inputs) {
        $args += @('-f', 'lavfi', '-i', $input)
    }
    $args += @(
        '-filter_complex', ($Filter + ';[out]volume=24dB,alimiter=limit=0.97[outnorm]'),
        '-map', '[outnorm]',
        '-ac', '1',
        '-ar', '48000',
        '-c:a', 'libvorbis',
        '-qscale:a', '5',
        $outPath
    )

    & $Ffmpeg @args | Out-Null
    if ($LASTEXITCODE -ne 0) {
        throw "ffmpeg failed while generating $Name"
    }
}

New-Item -ItemType Directory -Path $outputDir -Force | Out-Null
$ffmpeg = Resolve-Ffmpeg

New-OggMix -Ffmpeg $ffmpeg -Name 'Build.ogg' -Inputs @(
    'sine=frequency=720:sample_rate=48000:duration=0.14',
    'anoisesrc=color=white:sample_rate=48000:duration=0.14'
) -Filter '[0:a]volume=0.18,afade=t=in:st=0:d=0.004,afade=t=out:st=0.05:d=0.09[a0];[1:a]highpass=f=1800,lowpass=f=6200,volume=0.015,afade=t=in:st=0:d=0.004,afade=t=out:st=0.04:d=0.10[a1];[a0][a1]amix=inputs=2:normalize=0,alimiter=limit=0.84[out]'

New-OggMix -Ffmpeg $ffmpeg -Name 'Fire_Archer.ogg' -Inputs @(
    'sine=frequency=880:sample_rate=48000:duration=0.12',
    'sine=frequency=1320:sample_rate=48000:duration=0.07',
    'anoisesrc=color=white:sample_rate=48000:duration=0.16'
) -Filter '[0:a]volume=0.16,afade=t=in:st=0:d=0.002,afade=t=out:st=0.05:d=0.07[a0];[1:a]volume=0.09,afade=t=out:st=0.02:d=0.05[a1];[2:a]highpass=f=3500,lowpass=f=10500,volume=0.012,afade=t=in:st=0:d=0.003,afade=t=out:st=0.04:d=0.10[a2];[a0][a1][a2]amix=inputs=3:normalize=0,alimiter=limit=0.88[out]'

New-OggMix -Ffmpeg $ffmpeg -Name 'Fire_Crossbow.ogg' -Inputs @(
    'sine=frequency=240:sample_rate=48000:duration=0.18',
    'sine=frequency=1460:sample_rate=48000:duration=0.06',
    'anoisesrc=color=white:sample_rate=48000:duration=0.15'
) -Filter '[0:a]volume=0.20,afade=t=out:st=0.06:d=0.12[a0];[1:a]volume=0.08,adelay=delays=14:all=1,afade=t=out:st=0.02:d=0.04[a1];[2:a]highpass=f=2100,lowpass=f=9000,volume=0.012,afade=t=out:st=0.03:d=0.10[a2];[a0][a1][a2]amix=inputs=3:normalize=0,alimiter=limit=0.88[out]'

New-OggMix -Ffmpeg $ffmpeg -Name 'Fire_Dart.ogg' -Inputs @(
    'sine=frequency=1080:sample_rate=48000:duration=0.07',
    'anoisesrc=color=white:sample_rate=48000:duration=0.10'
) -Filter '[0:a]volume=0.16,afade=t=in:st=0:d=0.001,afade=t=out:st=0.018:d=0.05[a0];[1:a]highpass=f=4500,lowpass=f=12000,volume=0.011,afade=t=out:st=0.015:d=0.07[a1];[a0][a1]amix=inputs=2:normalize=0,alimiter=limit=0.90[out]'

New-OggMix -Ffmpeg $ffmpeg -Name 'Fire_Frost.ogg' -Inputs @(
    'sine=frequency=620:sample_rate=48000:duration=0.18',
    'sine=frequency=1180:sample_rate=48000:duration=0.11',
    'anoisesrc=color=white:sample_rate=48000:duration=0.24'
) -Filter '[0:a]volume=0.10,afade=t=in:st=0:d=0.01,afade=t=out:st=0.07:d=0.10[a0];[1:a]volume=0.07,adelay=delays=18:all=1,afade=t=out:st=0.03:d=0.08[a1];[2:a]highpass=f=1800,lowpass=f=6200,volume=0.015,afade=t=in:st=0:d=0.01,afade=t=out:st=0.08:d=0.14[a2];[a0][a1][a2]amix=inputs=3:normalize=0,alimiter=limit=0.86[out]'

New-OggMix -Ffmpeg $ffmpeg -Name 'Fire_Shock.ogg' -Inputs @(
    'sine=frequency=980:sample_rate=48000:duration=0.13',
    'sine=frequency=1840:sample_rate=48000:duration=0.11',
    'anoisesrc=color=white:sample_rate=48000:duration=0.18'
) -Filter '[0:a]volume=0.13,aecho=0.6:0.25:16:0.18,afade=t=out:st=0.05:d=0.08[a0];[1:a]volume=0.08,afade=t=out:st=0.03:d=0.08[a1];[2:a]highpass=f=2600,lowpass=f=11000,volume=0.013,aecho=0.5:0.2:12:0.22,afade=t=out:st=0.04:d=0.12[a2];[a0][a1][a2]amix=inputs=3:normalize=0,alimiter=limit=0.87[out]'

New-OggMix -Ffmpeg $ffmpeg -Name 'Fire_Drill.ogg' -Inputs @(
    'sine=frequency=150:sample_rate=48000:duration=0.22',
    'sine=frequency=320:sample_rate=48000:duration=0.18',
    'anoisesrc=color=white:sample_rate=48000:duration=0.20'
) -Filter '[0:a]volume=0.24,afade=t=out:st=0.08:d=0.14[a0];[1:a]volume=0.09,afade=t=out:st=0.05:d=0.11[a1];[2:a]highpass=f=700,lowpass=f=2600,volume=0.02,afade=t=in:st=0:d=0.002,afade=t=out:st=0.05:d=0.15[a2];[a0][a1][a2]amix=inputs=3:normalize=0,alimiter=limit=0.90[out]'

New-OggMix -Ffmpeg $ffmpeg -Name 'Impact.ogg' -Inputs @(
    'sine=frequency=260:sample_rate=48000:duration=0.14',
    'anoisesrc=color=white:sample_rate=48000:duration=0.16'
) -Filter '[0:a]volume=0.20,afade=t=out:st=0.03:d=0.11[a0];[1:a]highpass=f=800,lowpass=f=4200,volume=0.025,afade=t=in:st=0:d=0.001,afade=t=out:st=0.03:d=0.12[a1];[a0][a1]amix=inputs=2:normalize=0,alimiter=limit=0.92[out]'

New-OggMix -Ffmpeg $ffmpeg -Name 'Coin.ogg' -Inputs @(
    'sine=frequency=2140:sample_rate=48000:duration=0.12',
    'sine=frequency=3160:sample_rate=48000:duration=0.10'
) -Filter '[0:a]volume=0.10,afade=t=in:st=0:d=0.001,afade=t=out:st=0.04:d=0.08[a0];[1:a]volume=0.06,adelay=delays=22:all=1,afade=t=out:st=0.03:d=0.07[a1];[a0][a1]amix=inputs=2:normalize=0,alimiter=limit=0.75[out]'

New-OggMix -Ffmpeg $ffmpeg -Name 'Leak.ogg' -Inputs @(
    'sine=frequency=360:sample_rate=48000:duration=0.20',
    'sine=frequency=280:sample_rate=48000:duration=0.20'
) -Filter '[0:a]volume=0.13,afade=t=in:st=0:d=0.005,afade=t=out:st=0.08:d=0.12[a0];[1:a]volume=0.11,adelay=delays=120:all=1,afade=t=in:st=0.12:d=0.005,afade=t=out:st=0.20:d=0.10[a1];[a0][a1]amix=inputs=2:normalize=0,alimiter=limit=0.82[out]'

New-OggMix -Ffmpeg $ffmpeg -Name 'WaveStart.ogg' -Inputs @(
    'sine=frequency=660:sample_rate=48000:duration=0.38',
    'sine=frequency=990:sample_rate=48000:duration=0.38',
    'sine=frequency=1320:sample_rate=48000:duration=0.38'
) -Filter '[0:a]volume=0.11,afade=t=out:st=0.08:d=0.08[a0];[1:a]volume=0.10,adelay=delays=95:all=1,afade=t=out:st=0.18:d=0.08[a1];[2:a]volume=0.09,adelay=delays=190:all=1,afade=t=out:st=0.28:d=0.10[a2];[a0][a1][a2]amix=inputs=3:normalize=0,alimiter=limit=0.86[out]'

New-OggMix -Ffmpeg $ffmpeg -Name 'WaveEnd.ogg' -Inputs @(
    'sine=frequency=980:sample_rate=48000:duration=0.25',
    'sine=frequency=1280:sample_rate=48000:duration=0.25'
) -Filter '[0:a]volume=0.10,afade=t=out:st=0.07:d=0.10[a0];[1:a]volume=0.08,adelay=delays=70:all=1,afade=t=out:st=0.12:d=0.10[a1];[a0][a1]amix=inputs=2:normalize=0,alimiter=limit=0.83[out]'

New-OggMix -Ffmpeg $ffmpeg -Name 'Reward.ogg' -Inputs @(
    'sine=frequency=1380:sample_rate=48000:duration=0.48',
    'sine=frequency=1840:sample_rate=48000:duration=0.48',
    'sine=frequency=2460:sample_rate=48000:duration=0.48'
) -Filter '[0:a]volume=0.08,afade=t=out:st=0.08:d=0.10[a0];[1:a]volume=0.07,adelay=delays=80:all=1,afade=t=out:st=0.18:d=0.10[a1];[2:a]volume=0.06,adelay=delays=160:all=1,afade=t=out:st=0.28:d=0.14[a2];[a0][a1][a2]amix=inputs=3:normalize=0,alimiter=limit=0.82[out]'

New-OggMix -Ffmpeg $ffmpeg -Name 'Victory.ogg' -Inputs @(
    'sine=frequency=780:sample_rate=48000:duration=0.95',
    'sine=frequency=1040:sample_rate=48000:duration=0.95',
    'sine=frequency=1560:sample_rate=48000:duration=0.95',
    'sine=frequency=2080:sample_rate=48000:duration=0.95'
) -Filter '[0:a]volume=0.08,afade=t=out:st=0.16:d=0.10[a0];[1:a]volume=0.07,adelay=delays=120:all=1,afade=t=out:st=0.32:d=0.10[a1];[2:a]volume=0.07,adelay=delays=260:all=1,afade=t=out:st=0.54:d=0.14[a2];[3:a]volume=0.06,adelay=delays=420:all=1,afade=t=out:st=0.74:d=0.18[a3];[a0][a1][a2][a3]amix=inputs=4:normalize=0,alimiter=limit=0.85[out]'

New-OggMix -Ffmpeg $ffmpeg -Name 'Defeat.ogg' -Inputs @(
    'sine=frequency=420:sample_rate=48000:duration=0.85',
    'sine=frequency=300:sample_rate=48000:duration=0.85',
    'sine=frequency=210:sample_rate=48000:duration=0.85'
) -Filter '[0:a]volume=0.12,afade=t=out:st=0.12:d=0.12[a0];[1:a]volume=0.11,adelay=delays=150:all=1,afade=t=out:st=0.36:d=0.14[a1];[2:a]volume=0.10,adelay=delays=320:all=1,afade=t=out:st=0.62:d=0.18[a2];[a0][a1][a2]amix=inputs=3:normalize=0,alimiter=limit=0.85[out]'

Write-Host "Generated BankDefense SFX into $outputDir"
