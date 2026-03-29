$ErrorActionPreference = "Stop"

$base = "e:\Hytale mods\bank_defense_autogen\src\resources\Server\Audio\SoundEvents\SFX\BankDefense"
New-Item -ItemType Directory -Force -Path $base | Out-Null

function Write-SoundEvent {
    param(
        [Parameter(Mandatory = $true)][string]$Name,
        [Parameter(Mandatory = $true)][string[]]$Files,
        [double]$Volume = 0.0,
        [string]$Parent = "SFX_Attn_Moderate",
        [int]$MaxInstance = 6,
        [bool]$PreventSoundInterruption = $true,
        [double]$MinPitch = 0.0,
        [double]$MaxPitch = 0.0,
        [double]$MinVolume = 0.0,
        [double]$MaxVolume = 0.0,
        [double]$LayerVolume = 0.0,
        [bool]$Looping = $false,
        [double]$StartDelay = 0.0
    )

    $payload = [ordered]@{
        Layers = @(
            [ordered]@{
                Files = $Files
                RandomSettings = [ordered]@{
                    MinPitch = $MinPitch
                    MaxPitch = $MaxPitch
                    MinVolume = $MinVolume
                    MaxVolume = $MaxVolume
                }
                StartDelay = $StartDelay
                Looping = $Looping
                Volume = $LayerVolume
            }
        )
        Volume = $Volume
        PreventSoundInterruption = $PreventSoundInterruption
        MaxInstance = $MaxInstance
        Parent = $Parent
    }

    $json = $payload | ConvertTo-Json -Depth 8
    $path = Join-Path $base $Name
    $utf8NoBom = New-Object System.Text.UTF8Encoding($false)
    [System.IO.File]::WriteAllText($path, $json, $utf8NoBom)
}

Write-SoundEvent "SFX_BankDefense_Build.json" @(
    "Sounds/Blocks/Building/Destruct_1.ogg"
) -Volume 1.4 -Parent "SFX_Attn_Loud" -MaxInstance 10 -MinPitch -0.10 -MaxPitch 0.16

Write-SoundEvent "SFX_BankDefense_Coin.json" @(
    "Sounds/Movement/Landing/FS_Land_Coins_01.ogg",
    "Sounds/Movement/Landing/FS_Land_Coins_02.ogg",
    "Sounds/Movement/Landing/FS_Land_Coins_03.ogg",
    "Sounds/Movement/Landing/FS_Land_Coins_04.ogg",
    "Sounds/Movement/Landing/FS_Land_Coins_05.ogg"
) -Volume 0.6 -Parent "SFX_Attn_Loud" -MaxInstance 10 -MinPitch -0.08 -MaxPitch 0.08

Write-SoundEvent "SFX_BankDefense_Leak.json" @(
    "Sounds/Weapons/Shield/Shield_T1_Impact_01.ogg",
    "Sounds/Weapons/Shield/Shield_T1_Impact_02.ogg",
    "Sounds/Weapons/Shield/Shield_T1_Impact_03.ogg"
) -Volume 1.2 -Parent "SFX_Attn_VeryLoud" -MaxInstance 5 -MinPitch -0.06 -MaxPitch 0.04

Write-SoundEvent "SFX_BankDefense_Fire_Archer.json" @(
    "Sounds/Weapons/Bow/Bow_T1_Shoot_01.ogg",
    "Sounds/Weapons/Bow/Bow_T1_Shoot_02.ogg",
    "Sounds/Weapons/Bow/Bow_T1_Shoot_03.ogg",
    "Sounds/Weapons/Bow/Bow_T1_Shoot_Stereo_01.ogg",
    "Sounds/Weapons/Bow/Bow_T1_Shoot_Stereo_02.ogg",
    "Sounds/Weapons/Bow/Bow_T1_Shoot_Stereo_03.ogg"
) -Volume 0.1 -Parent "SFX_Attn_Loud" -MaxInstance 18 -MinPitch -0.10 -MaxPitch 0.12

Write-SoundEvent "SFX_BankDefense_Fire_Crossbow.json" @(
    "Sounds/Weapons/Spear/Spear_Gore_01.ogg",
    "Sounds/Weapons/Spear/Spear_Gore_02.ogg",
    "Sounds/Weapons/Spear/Spear_Gore_03.ogg"
) -Volume 0.2 -Parent "SFX_Attn_Loud" -MaxInstance 18 -MinPitch -0.12 -MaxPitch 0.10

Write-SoundEvent "SFX_BankDefense_Fire_Dart.json" @(
    "Sounds/Projectiles/Arrow/Arrow_Whistle_01.ogg",
    "Sounds/Projectiles/Arrow/Arrow_Whistle_02.ogg",
    "Sounds/Projectiles/Arrow/Arrow_Whistle_03.ogg",
    "Sounds/Projectiles/Arrow/Arrow_Whistle_04.ogg"
) -Volume -0.8 -Parent "SFX_Attn_Moderate" -MaxInstance 18 -MinPitch -0.10 -MaxPitch 0.10

Write-SoundEvent "SFX_BankDefense_Fire_Drill.json" @(
    "Sounds/Blocks/Metal/Metal_Hit_01.ogg",
    "Sounds/Blocks/Metal/Metal_Hit_02.ogg",
    "Sounds/Blocks/Metal/Metal_Hit_03.ogg"
) -Volume 0.2 -Parent "SFX_Attn_Loud" -MaxInstance 18 -MinPitch -0.08 -MaxPitch 0.06

Write-SoundEvent "SFX_BankDefense_Fire_Frost.json" @(
    "Sounds/Weapons/Wand/IceBoltWandShot_1.ogg"
) -Volume 0.1 -Parent "SFX_Attn_Loud" -MaxInstance 14 -MinPitch -0.08 -MaxPitch 0.04

Write-SoundEvent "SFX_BankDefense_Fire_Shock.json" @(
    "Sounds/Magic/Ping_01.ogg"
) -Volume -0.2 -Parent "SFX_Attn_Loud" -MaxInstance 16 -MinPitch -0.12 -MaxPitch 0.18

Write-SoundEvent "SFX_BankDefense_Impact.json" @(
    "Sounds/Blocks/Generic/Block_Hit_Weight.ogg"
) -Volume 0.0 -Parent "SFX_Attn_Loud" -MaxInstance 20 -MinPitch -0.12 -MaxPitch 0.12

Write-SoundEvent "SFX_BankDefense_Reward.json" @(
    "Sounds/CreativePlay/Default/Shimmer_Sweeps_Stereo.ogg"
) -Volume 0.6 -Parent "SFX_Attn_Loud" -MaxInstance 4 -MinPitch -0.06 -MaxPitch 0.08

Write-SoundEvent "SFX_BankDefense_Victory.json" @(
    "Sounds/CreativePlay/Default/Shimmer_Sweeps_Stereo.ogg"
) -Volume 1.2 -Parent "SFX_Attn_VeryLoud" -MaxInstance 2

Write-SoundEvent "SFX_BankDefense_Defeat.json" @(
    "Sounds/PlayerActions/Sleep/Sleep_Fail_Stereo_01.ogg"
) -Volume 1.2 -Parent "SFX_Attn_VeryLoud" -MaxInstance 2

Write-SoundEvent "SFX_BankDefense_TutorialStep.json" @(
    "Sounds/CreativePlay/Default/Chimes_Dry_Stereo.ogg"
) -Volume -0.4 -Parent "SFX_Attn_Moderate" -MaxInstance 6 -MinPitch -0.04 -MaxPitch 0.05

Write-SoundEvent "SFX_BankDefense_TutorialWarp.json" @(
    "Sounds/CreativePlay/Default/EyedropperSelect_Stereo.ogg",
    "Sounds/CreativePlay/Default/Paste_Stereo.ogg"
) -Volume 0.2 -Parent "SFX_Attn_Loud" -MaxInstance 6 -MinPitch -0.04 -MaxPitch 0.05

Write-SoundEvent "SFX_BankDefense_MonolithBurst.json" @(
    "Sounds/Magic/Heal.ogg"
) -Volume 0.9 -Parent "SFX_Attn_VeryLoud" -MaxInstance 6 -MinPitch -0.08 -MaxPitch 0.08

Write-SoundEvent "SFX_BankDefense_CoreUnlock.json" @(
    "Sounds/Magic/Heal.ogg"
) -Volume 0.5 -Parent "SFX_Attn_Loud" -MaxInstance 5 -MinPitch -0.05 -MaxPitch 0.05

Write-SoundEvent "SFX_BankDefense_Alert.json" @(
    "Sounds/CreativePlay/Default/Base_Chimes_Stereo.ogg"
) -Volume -0.1 -Parent "SFX_Attn_Loud" -MaxInstance 6 -MinPitch -0.05 -MaxPitch 0.06

Write-SoundEvent "SFX_BankDefense_TowerFire.json" @(
    "Sounds/Weapons/Bow/Bow_T1_Shoot_01.ogg",
    "Sounds/Weapons/Bow/Bow_T1_Shoot_02.ogg",
    "Sounds/Weapons/Bow/Bow_T1_Shoot_03.ogg"
) -Volume -0.8 -Parent "SFX_Attn_Moderate" -MaxInstance 16 -MinPitch -0.08 -MaxPitch 0.10

Write-SoundEvent "SFX_BankDefense_WaveStart.json" @(
    "Sounds/Magic/Portals/Portal_Open_01.ogg"
) -Volume 0.9 -Parent "SFX_Attn_VeryLoud" -MaxInstance 3 -MinPitch -0.04 -MaxPitch 0.04

Write-SoundEvent "SFX_BankDefense_WaveEnd.json" @(
    "Sounds/PlayerActions/Sleep/Sleep_Notification_Stereo_01.ogg"
) -Volume 0.7 -Parent "SFX_Attn_Loud" -MaxInstance 3 -MinPitch -0.03 -MaxPitch 0.03

Write-SoundEvent "SFX_BankDefense_Sell.json" @(
    "Sounds/Blocks/Building/Clone_1.ogg"
) -Volume 1.2 -Parent "SFX_Attn_Loud" -MaxInstance 8 -MinPitch -0.10 -MaxPitch 0.12

Write-SoundEvent "SFX_BankDefense_UiClick.json" @(
    "Sounds/Blocks/Cactus/Cactus_Hit_01.ogg",
    "Sounds/Blocks/Cactus/Cactus_Hit_02.ogg",
    "Sounds/Blocks/Cactus/Cactus_Hit_03.ogg",
    "Sounds/Blocks/Cactus/Cactus_Hit_04.ogg",
    "Sounds/Blocks/Cactus/Cactus_Hit_05.ogg",
    "Sounds/Blocks/Cactus/Cactus_Hit_06.ogg"
) -Volume 4.0 -Parent "SFX_Attn_Moderate" -MaxInstance 8 -MinPitch -0.02 -MaxPitch 0.18

Write-SoundEvent "SFX_BankDefense_UiError.json" @(
    "Sounds/CreativePlay/Default/ErrorSound_Stereo.ogg"
) -Volume 0.0 -Parent "SFX_Attn_Moderate" -MaxInstance 2 -LayerVolume 6.0

Write-SoundEvent "SFX_BankDefense_ChestLoop.json" @(
    "Sounds/Items/Chest/Chest_Legendary_LOOP.ogg"
) -Volume -2.2 -Parent "SFX_Attn_Quiet" -MaxInstance 8 -PreventSoundInterruption $false

Write-SoundEvent "SFX_BankDefense_ChestOpen.json" @(
    "Sounds/Items/Chest/Chest_Legendary_Open_Player_Stereo_01.ogg",
    "Sounds/Items/Chest/Chest_Legendary_Open_Stereo_01.ogg"
) -Volume -2.6 -Parent "SFX_Attn_Quiet" -MaxInstance 8 -MinPitch -0.04 -MaxPitch 0.06

Write-SoundEvent "SFX_BankDefense_TrapPlace.json" @(
    "Sounds/Items/Crops/Crops_Grow_01.ogg",
    "Sounds/Items/Crops/Crops_Grow_02.ogg",
    "Sounds/Items/Crops/Crops_Grow_03.ogg",
    "Sounds/Items/Crops/Crops_Grow_04.ogg",
    "Sounds/Items/Crops/Crops_Grow_05.ogg",
    "Sounds/Items/Crops/Crops_Grow_06.ogg",
    "Sounds/Items/Crops/Crops_Grow_07.ogg",
    "Sounds/Items/Crops/Crops_Grow_08.ogg",
    "Sounds/Items/Crops/Crops_Grow_09.ogg",
    "Sounds/Items/Crops/Crops_Grow_10.ogg",
    "Sounds/Items/Crops/Crops_Grow_11.ogg",
    "Sounds/Items/Crops/Crops_Grow_12.ogg",
    "Sounds/Items/Crops/Crops_Grow_13.ogg",
    "Sounds/Items/Crops/Crops_Grow_14.ogg"
) -Volume 0.8 -Parent "SFX_Attn_Loud" -MaxInstance 10 -MinPitch -0.12 -MaxPitch 0.12

Write-SoundEvent "SFX_BankDefense_WorldEnter.json" @(
    "Sounds/Magic/Avatar/Avatar_Powers_Disable_Alt_01.ogg",
    "Sounds/Magic/Avatar/Avatar_Powers_Disable_Alt_Stereo_01.ogg"
) -Volume 0.7 -Parent "SFX_Attn_Loud" -MaxInstance 3 -MinPitch -0.04 -MaxPitch 0.05

Write-SoundEvent "SFX_BankDefense_CurseUi.json" @(
    "Sounds/Magic/Ping_01.ogg"
) -Volume 0.2 -Parent "SFX_Attn_Moderate" -MaxInstance 4 -MinPitch -0.08 -MaxPitch 0.10

Write-SoundEvent "SFX_BankDefense_Notification.json" @(
    "Sounds/PlayerActions/Pickup/Player_Item_Pickup_01.ogg"
) -Volume -1.0 -Parent "SFX_Attn_Moderate" -MaxInstance 8 -MinPitch -0.08 -MaxPitch 0.10

Write-SoundEvent "SFX_BankDefense_TutorialComplete.json" @(
    "Sounds/PlayerActions/Sleep/Sleep_Notification_Stereo_01.ogg"
) -Volume 0.1 -Parent "SFX_Attn_Loud" -MaxInstance 3

Write-SoundEvent "SFX_BankDefense_NodeArbiterRespawn.json" @(
    "Sounds/PlayerActions/Sleep/Sleep_Fail_Stereo_01.ogg"
) -Volume 0.4 -Parent "SFX_Attn_Loud" -MaxInstance 4 -MinPitch -0.05 -MaxPitch 0.05

Write-SoundEvent "SFX_BankDefense_EnemySpawnGeneric.json" @(
    "Sounds/Blocks/Generic/Block_Break_Success_01.ogg"
) -Volume 0.0 -Parent "SFX_Attn_Loud" -MaxInstance 24 -MinPitch -0.35 -MaxPitch 0.42

Write-SoundEvent "SFX_BankDefense_EnemySpawnGoblin.json" @(
    "Sounds/NPC/Intelligent/Goblins/Goblin_Alerted_01.ogg",
    "Sounds/NPC/Intelligent/Goblins/Goblin_Alerted_02.ogg",
    "Sounds/NPC/Intelligent/Goblins/Goblin_Alerted_03.ogg",
    "Sounds/NPC/Intelligent/Goblins/Goblin_Alerted_04.ogg",
    "Sounds/NPC/Intelligent/Goblins/Goblin_Alerted_05.ogg",
    "Sounds/NPC/Intelligent/Goblins/Goblin_Alerted_06.ogg",
    "Sounds/NPC/Intelligent/Goblins/Goblin_Alerted_07.ogg",
    "Sounds/NPC/Intelligent/Goblins/Goblin_Alerted_08.ogg",
    "Sounds/NPC/Intelligent/Goblins/Goblin_Alerted_09.ogg",
    "Sounds/NPC/Intelligent/Goblins/Goblin_Alerted_10.ogg"
) -Volume -0.8 -Parent "SFX_Attn_Loud" -MaxInstance 10 -MinPitch -0.10 -MaxPitch 0.12

Write-SoundEvent "SFX_BankDefense_EnemySpawnUndead.json" @(
    "Sounds/NPC/Undead/Skeleton/Skeleton_Alerted_VO_01.ogg",
    "Sounds/NPC/Undead/Skeleton/Skeleton_Alerted_VO_02.ogg",
    "Sounds/NPC/Undead/Skeleton/Skeleton_Alerted_VO_03.ogg",
    "Sounds/NPC/Undead/Skeleton/Skeleton_Alerted_VO_04.ogg"
) -Volume -0.9 -Parent "SFX_Attn_Loud" -MaxInstance 10 -MinPitch -0.08 -MaxPitch 0.08

Write-SoundEvent "SFX_BankDefense_EnemySpawnVoid.json" @(
    "Sounds/NPC/Void/Crawler_Void/Crawler_Void_Spawn2_01.ogg",
    "Sounds/NPC/Void/Crawler_Void/Crawler_Void_Spawn2_02.ogg",
    "Sounds/NPC/Void/Crawler_Void/Crawler_Void_Spawn2_03.ogg"
) -Volume -0.4 -Parent "SFX_Attn_Loud" -MaxInstance 8 -MinPitch -0.08 -MaxPitch 0.08

Write-SoundEvent "SFX_BankDefense_EnemyDeathBoss.json" @(
    "Sounds/Blocks/Tombstone/Tombstone_Break_01.ogg"
) -Volume 0.3 -Parent "SFX_Attn_VeryLoud" -MaxInstance 4 -MinPitch -0.08 -MaxPitch 0.06

Write-SoundEvent "SFX_BankDefense_EnemyDeathSkeleton.json" @(
    "Sounds/NPC/Undead/Skeleton/Skeleton_Death_01_Foley_01.ogg",
    "Sounds/NPC/Undead/Skeleton/Skeleton_Death_01_Foley_02.ogg",
    "Sounds/NPC/Undead/Skeleton/Skeleton_Death_01_Foley_03.ogg",
    "Sounds/NPC/Undead/Skeleton/Skeleton_Death_02_Foley_01.ogg",
    "Sounds/NPC/Undead/Skeleton/Skeleton_Death_02_Foley_02.ogg",
    "Sounds/NPC/Undead/Skeleton/Skeleton_Death_02_Foley_03.ogg",
    "Sounds/NPC/Undead/Skeleton/Skeleton_Death_03_Foley_01.ogg",
    "Sounds/NPC/Undead/Skeleton/Skeleton_Death_03_Foley_02.ogg",
    "Sounds/NPC/Undead/Skeleton/Skeleton_Death_03_Foley_03.ogg",
    "Sounds/NPC/Undead/Skeleton/Skeleton_Death_04_Foley_01.ogg",
    "Sounds/NPC/Undead/Skeleton/Skeleton_Death_04_Foley_02.ogg",
    "Sounds/NPC/Undead/Skeleton/Skeleton_Death_04_Foley_03.ogg"
) -Volume -0.1 -Parent "SFX_Attn_Moderate" -MaxInstance 16 -MinPitch -0.10 -MaxPitch 0.10

Write-SoundEvent "SFX_BankDefense_EnemyDeathGoblin.json" @(
    "Sounds/NPC/Intelligent/Goblins/Goblin_Death_01.ogg",
    "Sounds/NPC/Intelligent/Goblins/Goblin_Death_02.ogg",
    "Sounds/NPC/Intelligent/Goblins/Goblin_Death_03.ogg"
) -Volume -0.2 -Parent "SFX_Attn_Moderate" -MaxInstance 12 -MinPitch -0.10 -MaxPitch 0.10

Write-SoundEvent "SFX_BankDefense_EnemyDeathUndead.json" @(
    "Sounds/NPC/Undead/Skeleton/Skeleton_Death_VO_01.ogg",
    "Sounds/NPC/Undead/Skeleton/Skeleton_Death_VO_02.ogg",
    "Sounds/NPC/Undead/Skeleton/Skeleton_Death_VO_03.ogg"
) -Volume -0.5 -Parent "SFX_Attn_Moderate" -MaxInstance 12 -MinPitch -0.10 -MaxPitch 0.10

Write-SoundEvent "SFX_BankDefense_EnemyDeathVoid.json" @(
    "Sounds/NPC/Void/Spawn_Void/Spawn_Void_Death_01.ogg",
    "Sounds/NPC/Void/Spawn_Void/Spawn_Void_Death_02.ogg",
    "Sounds/NPC/Void/Spawn_Void/Spawn_Void_Death_03.ogg",
    "Sounds/NPC/Void/Spawn_Void/Spawn_Void_Death_04.ogg"
) -Volume -0.1 -Parent "SFX_Attn_Moderate" -MaxInstance 10 -MinPitch -0.08 -MaxPitch 0.08

Write-SoundEvent "SFX_BankDefense_GoblinDestroy.json" @(
    "Sounds/Blocks/Crystal/Crystal_Break_01.ogg",
    "Sounds/Blocks/Crystal/Crystal_Break_02.ogg"
) -Volume 0.7 -Parent "SFX_Attn_Loud" -MaxInstance 8 -MinPitch -0.08 -MaxPitch 0.08

Write-SoundEvent "SFX_BankDefense_GoblinDowngrade.json" @(
    "Sounds/Blocks/Gem/Gem_Break_Layer_01.ogg",
    "Sounds/Blocks/Gem/Gem_Break_Layer_02.ogg",
    "Sounds/Blocks/Gem/Gem_Break_Layer_03.ogg"
) -Volume 0.2 -Parent "SFX_Attn_Loud" -MaxInstance 8 -MinPitch -0.08 -MaxPitch 0.08

Write-SoundEvent "SFX_BankDefense_Impact_Arrow.json" @(
    "Sounds/Projectiles/Arrow/Arrow_Impact_01.ogg",
    "Sounds/Projectiles/Arrow/Arrow_Impact_02.ogg",
    "Sounds/Projectiles/Arrow/Arrow_Impact_03.ogg",
    "Sounds/Projectiles/Arrow/Arrow_Impact_04.ogg"
) -Volume 0.0 -Parent "SFX_Attn_Loud" -MaxInstance 20 -MinPitch -0.08 -MaxPitch 0.10

Write-SoundEvent "SFX_BankDefense_TowerUpgrade.json" @(
    "Sounds/Weapons/Shield/Shield_T1_Raise_01.ogg",
    "Sounds/Weapons/Shield/Shield_T1_Raise_02.ogg",
    "Sounds/Weapons/Shield/Shield_T1_Raise_03.ogg"
) -Volume 0.2 -Parent "SFX_Attn_Moderate" -MaxInstance 12 -MinPitch -0.06 -MaxPitch 0.10

Write-SoundEvent "SFX_BankDefense_ModuleInstall.json" @(
    "Sounds/CreativePlay/Default/Rotate_Yaw_Stereo.ogg"
) -Volume -0.4 -Parent "SFX_Attn_Moderate" -MaxInstance 10 -MinPitch -0.02 -MaxPitch 0.08

Write-SoundEvent "SFX_BankDefense_ModuleRemove.json" @(
    "Sounds/CreativePlay/Default/Rotate_Roll_Stereo.ogg"
) -Volume -0.4 -Parent "SFX_Attn_Moderate" -MaxInstance 10 -MinPitch -0.02 -MaxPitch 0.08

Write-SoundEvent "SFX_BankDefense_IdolModeSwitch.json" @(
    "Sounds/CreativePlay/Default/Rotate_Pitch_Stereo.ogg"
) -Volume -0.2 -Parent "SFX_Attn_Moderate" -MaxInstance 8 -MinPitch -0.03 -MaxPitch 0.10

Write-SoundEvent "SFX_BankDefense_RiftTwinSwap.json" @(
    "Sounds/Magic/Portals/Portal_Teleport_Stereo_01.ogg"
) -Volume 0.0 -Parent "SFX_Attn_Loud" -MaxInstance 6 -MinPitch -0.03 -MaxPitch 0.06

Write-SoundEvent "SFX_BankDefense_RiftTwinEnrage.json" @(
    "Sounds/Weapons/Shield/Shield_Break_01.ogg",
    "Sounds/Weapons/Shield/Shield_Break_02.ogg",
    "Sounds/Weapons/Shield/Shield_Break_03.ogg"
) -Volume 0.2 -Parent "SFX_Attn_Loud" -MaxInstance 6 -MinPitch -0.06 -MaxPitch 0.08

Write-SoundEvent "SFX_BankDefense_NodeArbiterLifeBreak.json" @(
    "Sounds/Blocks/Gem/Gem_Break_Layer_01.ogg",
    "Sounds/Blocks/Gem/Gem_Break_Layer_02.ogg",
    "Sounds/Blocks/Gem/Gem_Break_Layer_03.ogg"
) -Volume -0.2 -Parent "SFX_Attn_Moderate" -MaxInstance 6 -MinPitch -0.05 -MaxPitch 0.07

Write-SoundEvent "SFX_BankDefense_SealMasterCast.json" @(
    "Sounds/Magic/Portals/Portal_Open_01.ogg"
) -Volume 0.1 -Parent "SFX_Attn_Loud" -MaxInstance 5 -MinPitch -0.04 -MaxPitch 0.05

Write-SoundEvent "SFX_BankDefense_SealMasterPhase1.json" @(
    "Sounds/Weapons/Shield/Shield_T2_Raise_Layer_01.ogg",
    "Sounds/Weapons/Shield/Shield_T2_Raise_Layer_02.ogg",
    "Sounds/Weapons/Shield/Shield_T2_Raise_Layer_03.ogg"
) -Volume 0.0 -Parent "SFX_Attn_Loud" -MaxInstance 4 -MinPitch -0.03 -MaxPitch 0.05

Write-SoundEvent "SFX_BankDefense_SealMasterPhase2.json" @(
    "Sounds/Weapons/Shield/Shield_T2_Raise_Layer_Stereo_01.ogg",
    "Sounds/Weapons/Shield/Shield_T2_Raise_Layer_Stereo_02.ogg",
    "Sounds/Weapons/Shield/Shield_T2_Raise_Layer_Stereo_03.ogg"
) -Volume 0.2 -Parent "SFX_Attn_VeryLoud" -MaxInstance 4 -MinPitch -0.03 -MaxPitch 0.05

Write-SoundEvent "SFX_BankDefense_SuperReactivate.json" @(
    "Sounds/Weapons/Shield/Shield_T1_Raise_Stereo_01.ogg",
    "Sounds/Weapons/Shield/Shield_T1_Raise_Stereo_02.ogg",
    "Sounds/Weapons/Shield/Shield_T1_Raise_Stereo_03.ogg"
) -Volume 0.2 -Parent "SFX_Attn_Loud" -MaxInstance 6 -MinPitch -0.04 -MaxPitch 0.08

Write-SoundEvent "SFX_BankDefense_HeartActivate.json" @(
    "Sounds/Magic/Heal.ogg"
) -Volume 0.8 -Parent "SFX_Attn_VeryLoud" -MaxInstance 4 -MinPitch -0.04 -MaxPitch 0.04

Write-SoundEvent "SFX_BankDefense_DuoRewardAllyPick.json" @(
    "Sounds/CreativePlay/Default/Rotate_Pitch_Stereo.ogg"
) -Volume -0.5 -Parent "SFX_Attn_Moderate" -MaxInstance 6 -MinPitch -0.03 -MaxPitch 0.08

Write-SoundEvent "SFX_BankDefense_DuoRewardTeamBonus.json" @(
    "Sounds/CreativePlay/Default/Shimmer_Sweeps_Stereo.ogg"
) -Volume 0.4 -Parent "SFX_Attn_Loud" -MaxInstance 4 -MinPitch -0.03 -MaxPitch 0.05

Write-SoundEvent "SFX_BankDefense_DuoRewardBurn.json" @(
    "Sounds/CreativePlay/Default/SelectionDrag_Stereo.ogg"
) -Volume -0.2 -Parent "SFX_Attn_Moderate" -MaxInstance 4 -MinPitch -0.02 -MaxPitch 0.04
