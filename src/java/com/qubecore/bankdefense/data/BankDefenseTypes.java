package com.qubecore.bankdefense.data;

import java.util.ArrayList;
import java.util.List;

public final class BankDefenseTypes {
    private BankDefenseTypes() {
    }

    public static final class GameRules {
        public int version = 1;
        public String modeName = "DUPLO Defense by QubeCore";
        public int bankHp = 20;
        public int startingCurrency = 300;
        public boolean allowBuildDuringWave = false;
        public boolean allowUpgradeDuringWave = false;
        public int totalWaves = 13;
        public int maxTowerLevel = 5;
        public int interWavePrepSeconds = 20;
        public boolean endlessMode = true;
        public int endlessTemplateStartWave = 10;
        public double endlessEnemyHpScalePerWave = 0.18;
        public double endlessEnemyRewardScalePerWave = 0.02;
        public double endlessWaveBonusScalePerWave = 0.03;
        public int endlessExtraCountPerCycle = 2;
        public double endlessSpawnIntervalScalePerCycle = 0.10;
        public double endlessMinimumSpawnIntervalSeconds = 0.35;
        public int moduleRewardIntervalWaves = 5;
        public int moduleRewardChoiceCount = 3;
        public double earlyStartBonusMaxPercent = 0.10;
        public int earlyStartBonusCurrencyCap = 8;
        public int earlyStartBonusGraceSeconds = 3;
        public double sellRefundRate = 0.75;
        public int progressionPointBase = 6;
        public int progressionPointPerWave = 2;
        public int progressionPointWinBonus = 12;
        public int progressionPointPerfectBonus = 6;
        public int progressionPointPerEndlessCycle = 4;
        public int progressionEndlessCycleSize = 5;
    }

    public static final class BrandingConfig {
        public int version = 1;
        public String title = "DUPLO Defense by QubeCore";
        public String shortTitle = "DUPLO Defense";
        public String creator = "QubeCore";
        public String mascot = "QubeCore Keeper";
        public String bankFaction = "Guardians of the Hollow";
        public String uiAccent = "#d6b451";
        public String secondaryAccent = "#0b213f";
    }

    public static final class TowerCatalog {
        public int version = 1;
        public List<TowerDefinition> towers = new ArrayList<>();
    }

    public static final class TowerDefinition {
        public String id;
        public String displayName;
        public String role;
        public String targeting;
        public String note;
        public String progressionBranch = "general";
        public boolean superTower = false;
        public boolean trapTower = false;
        public boolean modulesAllowed = true;
        public int baseUnlockedLevel = 5;
        public List<TowerLevel> levels = new ArrayList<>();
    }

    public static final class TowerLevel {
        public int level;
        public int unlockCost;
        public double range;
        public double fireRate;
        public double damage;
        public double slowPercent;
        public double slowDuration;
    }

    public static final class EnemyCatalog {
        public int version = 1;
        public List<EnemyDefinition> enemies = new ArrayList<>();
    }

    public static final class ModuleCatalog {
        public int version = 1;
        public List<ModuleDefinition> modules = new ArrayList<>();
    }

    public static final class ModuleDefinition {
        public String id;
        public String displayName;
        public String note;
        public double fireRateMultiplier = 1.0;
        public double damageMultiplier = 1.0;
        public double rangeMultiplier = 1.0;
        public double rangeBonus = 0.0;
        public double slowPercentBonus = 0.0;
        public double slowDurationBonus = 0.0;
        public double splashDamageRatio = 0.0;
        public double splashRadius = 0.0;
        public double bonusDamageToHeavy = 0.0;
        public int chainBonusTargets = 0;
        public double chainRangeBonus = 0.0;
        public int doubleShotEvery = 0;
        public double doubleShotDamageMultiplier = 0.0;
        public double chargeSeconds = 0.0;
        public double chargedDamageMultiplier = 1.0;
    }

    public static final class EnemyDefinition {
        public String id;
        public String displayName;
        public String role;
        public double maxHp;
        public double speed;
        public int reward;
        public int leakDamage;
        public boolean specialEnemy = false;
        public boolean bossEnemy = false;
        public double towerStunRadius = 0.0;
        public double towerStunDuration = 0.0;
        public int towerStunTargetCount = 0;
        public boolean selfDestructOnTowerStun = false;
        public double towerFireRateSlowRadius = 0.0;
        public double towerFireRateSlowPercent = 0.0;
        public String summonEnemyId;
        public double summonIntervalSeconds = 0.0;
        public int summonCount = 0;
        public double summonProgressOffset = 0.0;
        public double regenAuraRadius = 0.0;
        public double regenPercentPerSecond = 0.0;
        public double regenTotalBudgetMultiplier = 0.0;
        public double towerDisablePulseRadius = 0.0;
        public double towerDisablePulseDuration = 0.0;
        public double towerDisablePulsePercent = 0.0;
        public double towerDisablePulseIntervalSeconds = 0.0;
        public int towerDisablePulseMaxTriggers = 0;
        public boolean slowImmune = false;
        public double slowResistancePercent = 0.0;
        public double flatDamageReduction = 0.0;
        public double damageReductionPercent = 0.0;
        public double berserkHpThreshold = 0.0;
        public double berserkSpeedMultiplier = 1.0;
        public double allySpeedAuraRadius = 0.0;
        public double allySpeedAuraPercent = 0.0;
        public double allyDamageReductionAuraRadius = 0.0;
        public double allyDamageReductionAuraPercent = 0.0;
        public double towerRangeSlowRadius = 0.0;
        public double towerRangeSlowPercent = 0.0;
    }

    public static final class WaveCatalog {
        public int version = 1;
        public List<WaveDefinition> waves = new ArrayList<>();
    }

    public static final class WaveDefinition {
        public int index;
        public int bonusCurrency;
        public boolean bossWave;
        public List<WaveSpawn> spawns = new ArrayList<>();
    }

    public static final class WaveSpawn {
        public String enemyId;
        public int count;
        public double intervalSeconds;
        public double startDelaySeconds;
        public String laneId = "a";
    }

    public static final class MapConfig {
        public int version = 1;
        public String worldName = "default";
        public String selectedModeId = "solo";
        public Vec3i spawnPoint;
        public Vec3i spawnPointA;
        public Vec3i spawnPointB;
        public Vec3i spawnPointC;
        public Vec3i bankCenter;
        public Vec3i vaultPoint;
        public Vec3i playerStart;
        public Float playerStartYaw;
        public Vec3i controlPoint;
        public Vec3i previousControlPoint;
        public Float controlYaw;
        public Vec3i vendorPoint;
        public Vec3i previousVendorPoint;
        public Float vendorYaw;
        public Vec3i modePoint;
        public Vec3i previousModePoint;
        public Float modeYaw;
        public Vec3i soloQubeCorePoint;
        public Vec3i previousSoloQubeCorePoint;
        public Float soloQubeCoreYaw;
        public Vec3i duoPoint;
        public Vec3i previousDuoPoint;
        public Float duoYaw;
        public Vec3i duoQubeCorePoint;
        public Vec3i previousDuoQubeCorePoint;
        public Float duoQubeCoreYaw;
        public Vec3i duoTeamPoint;
        public Vec3i previousDuoTeamPoint;
        public Float duoTeamYaw;
        public Vec3i duoTeleportPoint;
        public Vec3i previousDuoTeleportPoint;
        public Float duoTeleportYaw;
        public Vec3i duoPlayerStartBlue;
        public Float duoPlayerStartBlueYaw;
        public Vec3i duoPlayerStartGreen;
        public Float duoPlayerStartGreenYaw;
        public Vec3i duoSpawnPointBlue;
        public Vec3i duoSpawnPointGreen;
        public Vec3i duoBankCenter;
        public Vec3i duoVaultPoint;
        public Vec3i duoTeleportTarget;
        public Vec3i soloTeleportTarget;
        public Vec3i duoSealNodeBluePoint;
        public Vec3i duoSealNodeGreenPoint;
        public Vec3i statisticsPoint;
        public Vec3i previousStatisticsPoint;
        public Float statisticsYaw;
        public Vec3i duoStatisticsPoint;
        public Vec3i previousDuoStatisticsPoint;
        public Float duoStatisticsYaw;
        public Vec3i tutorialStartPoint;
        public Float tutorialStartYaw;
        public Vec3i tutorialSpawnPointA;
        public Vec3i tutorialSpawnPointB;
        public Vec3i tutorialSpawnPointC;
        public Vec3i tutorialBankCenter;
        public Vec3i tutorialVaultPoint;
        public Vec3i tutorialControlPoint;
        public Float tutorialControlYaw;
        public Vec3i tutorialVendorPoint;
        public Float tutorialVendorYaw;
        public Vec3i tutorialWizardIntroPoint;
        public Float tutorialWizardIntroYaw;
        public Vec3i tutorialWizardPoint;
        public Float tutorialWizardYaw;
        public Vec3i tutorialChestPoint;
        public boolean tutorialTrapLayoutCustom;
        public boolean strictNpcMarkers;
        public List<Vec3i> routePoints = new ArrayList<>();
        public List<Vec3i> routePointsA = new ArrayList<>();
        public List<Vec3i> routePointsB = new ArrayList<>();
        public List<Vec3i> routePointsC = new ArrayList<>();
        public List<Vec3i> tutorialRoutePointsA = new ArrayList<>();
        public List<Vec3i> tutorialRoutePointsB = new ArrayList<>();
        public List<Vec3i> tutorialRoutePointsC = new ArrayList<>();
        public List<Vec3i> chestSpawnPoints = new ArrayList<>();
        public List<Vec3i> duoRouteBlue = new ArrayList<>();
        public List<Vec3i> duoRouteGreen = new ArrayList<>();
        public List<Vec3i> duoChestSpawnPoints = new ArrayList<>();
    }

    public static final class BuildSlotsConfig {
        public int version = 1;
        public List<BuildSlot> slots = new ArrayList<>();
    }

    public static final class BuildSlot {
        public String id;
        public String label;
        public Vec3i position;
        public String slotType = "standard";
        public String layout = "solo";
        public String ownerTeam = "shared";
        public String segment = "";
        public boolean modulesAllowed = true;
        public List<String> allowedTowerIds = new ArrayList<>();
    }

    public static final class ContractCatalog {
        public int version = 1;
        public List<ContractDefinition> contracts = new ArrayList<>();
    }

    public static final class ContractDefinition {
        public String id;
        public String displayName;
        public String note;
        public double rewardMultiplier = 1.0;
        public double matchIncomeMultiplier = 1.0;
        public double enemyHpMultiplier = 1.0;
        public double enemyCountMultiplier = 1.0;
        public double startingCurrencyMultiplier = 1.0;
        public int startingCurrencyFlat = 0;
        public double multiLanePressureMultiplier = 1.0;
    }

    public static final class ProgressionCatalog {
        public int version = 1;
        public List<ProgressionNodeDefinition> nodes = new ArrayList<>();
    }

    public static final class ProgressionNodeDefinition {
        public String id;
        public String displayName;
        public String note;
        public String branch;
        public int tier = 1;
        public int cost = 0;
        public String effectType;
        public String targetId;
        public double value = 0.0;
        public List<String> requires = new ArrayList<>();
    }

    public static final class PlayerProgressionState {
        public int version = 1;
        public String playerUuid;
        public int cores = 0;
        public String activeContractId = "none";
        public int lifetimeHighestWave = 0;
        public long lifetimeWavesCleared = 0;
        public long lifetimeEnemyKills = 0;
        public long lifetimeSpentCurrency = 0;
        public long lifetimeTrapsPlaced = 0;
        public long lifetimeOpenedChests = 0;
        public long lifetimeModulesInstalled = 0;
        public int totalRuns = 0;
        public int totalVictories = 0;
        public int bestRunWave = 0;
        public long bestRunEarnedCurrency = 0;
        public long bestRunEnemyKills = 0;
        public boolean tutorialCompleted = false;
        public List<String> unlockedNodeIds = new ArrayList<>();
    }

    public static final class Vec3i {
        public int x;
        public int y;
        public int z;

        public Vec3i() {
        }

        public Vec3i(int x, int y, int z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public static Vec3i ofFloor(double x, double y, double z) {
            return new Vec3i((int)Math.floor(x), (int)Math.floor(y), (int)Math.floor(z));
        }

        public double distanceSquaredTo(Vec3i other) {
            if (other == null) {
                return Double.MAX_VALUE;
            }
            long dx = (long)this.x - other.x;
            long dy = (long)this.y - other.y;
            long dz = (long)this.z - other.z;
            return (double)(dx * dx + dy * dy + dz * dz);
        }

        public String toShortString() {
            return this.x + ", " + this.y + ", " + this.z;
        }
    }
}
