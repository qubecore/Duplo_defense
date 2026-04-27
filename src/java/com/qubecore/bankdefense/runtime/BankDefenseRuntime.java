package com.qubecore.bankdefense.runtime;

import com.hypixel.hytale.component.AddReason;
import com.hypixel.hytale.component.Holder;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.RemoveReason;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector3f;
import com.hypixel.hytale.protocol.Color;
import com.hypixel.hytale.protocol.AnimationSlot;
import com.hypixel.hytale.protocol.MovementStates;
import com.hypixel.hytale.protocol.PlayerSkin;
import com.hypixel.hytale.protocol.SoundCategory;
import com.hypixel.hytale.protocol.packets.interface_.HudComponent;
import com.hypixel.hytale.builtin.weather.resources.WeatherResource;
import com.hypixel.hytale.server.core.HytaleServer;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.asset.type.model.config.Model;
import com.hypixel.hytale.server.core.asset.type.model.config.ModelAsset;
import com.hypixel.hytale.server.core.asset.type.soundevent.config.SoundEvent;
import com.hypixel.hytale.server.core.entity.Frozen;
import com.hypixel.hytale.server.core.entity.AnimationUtils;
import com.hypixel.hytale.server.core.entity.UUIDComponent;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.nameplate.Nameplate;
import com.hypixel.hytale.server.core.modules.entity.EntityModule;
import com.hypixel.hytale.server.core.modules.entity.component.BoundingBox;
import com.hypixel.hytale.server.core.modules.entity.component.EntityScaleComponent;
import com.hypixel.hytale.server.core.modules.entity.component.HeadRotation;
import com.hypixel.hytale.server.core.modules.entity.component.ActiveAnimationComponent;
import com.hypixel.hytale.server.core.modules.entity.component.Interactable;
import com.hypixel.hytale.server.core.modules.entity.component.Intangible;
import com.hypixel.hytale.server.core.modules.entity.component.ModelComponent;
import com.hypixel.hytale.server.core.modules.entity.component.NewSpawnComponent;
import com.hypixel.hytale.server.core.modules.entity.component.PropComponent;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.entity.movement.MovementStatesComponent;
import com.hypixel.hytale.server.core.modules.entity.teleport.Teleport;
import com.hypixel.hytale.server.core.modules.entity.player.ChunkTracker;
import com.hypixel.hytale.server.core.modules.entity.player.PlayerSkinComponent;
import com.hypixel.hytale.server.core.modules.entity.tracker.EntityTrackerSystems;
import com.hypixel.hytale.server.core.modules.entity.tracker.NetworkId;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatMap;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatValue;
import com.hypixel.hytale.server.core.modules.entitystats.asset.DefaultEntityStatTypes;
import com.hypixel.hytale.server.core.modules.entitystats.modifier.Modifier;
import com.hypixel.hytale.server.core.modules.entitystats.modifier.StaticModifier;
import com.hypixel.hytale.server.core.modules.entityui.UIComponentList;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.ParticleUtil;
import com.hypixel.hytale.server.core.universe.world.SoundUtil;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.WorldConfig;
import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk;
import com.hypixel.hytale.server.core.universe.world.chunk.ChunkFlag;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.flock.Flock;
import com.hypixel.hytale.server.flock.FlockMembership;
import com.hypixel.hytale.server.flock.PersistentFlockData;
import com.hypixel.hytale.server.npc.NPCPlugin;
import com.hypixel.hytale.server.npc.components.SpawnBeaconReference;
import com.hypixel.hytale.server.npc.components.SpawnMarkerReference;
import com.hypixel.hytale.server.npc.entities.NPCEntity;
import com.hypixel.hytale.server.spawning.beacons.LegacySpawnBeaconEntity;
import com.hypixel.hytale.server.spawning.spawnmarkers.SpawnMarkerEntity;
import com.qubecore.bankdefense.data.BankDefenseRepository;
import com.qubecore.bankdefense.data.BankDefenseTypes.BuildSlot;
import com.qubecore.bankdefense.data.BankDefenseTypes.BuildSlotsConfig;
import com.qubecore.bankdefense.data.BankDefenseTypes.ContractDefinition;
import com.qubecore.bankdefense.data.BankDefenseTypes.EnemyDefinition;
import com.qubecore.bankdefense.data.BankDefenseTypes.GameRules;
import com.qubecore.bankdefense.data.BankDefenseTypes.MapConfig;
import com.qubecore.bankdefense.data.BankDefenseTypes.ModuleDefinition;
import com.qubecore.bankdefense.data.BankDefenseTypes.PlayerProgressionState;
import com.qubecore.bankdefense.data.BankDefenseTypes.ProgressionNodeDefinition;
import com.qubecore.bankdefense.data.BankDefenseTypes.TowerDefinition;
import com.qubecore.bankdefense.data.BankDefenseTypes.TowerLevel;
import com.qubecore.bankdefense.data.BankDefenseTypes.Vec3i;
import com.qubecore.bankdefense.data.BankDefenseTypes.WaveDefinition;
import com.qubecore.bankdefense.data.BankDefenseTypes.WaveSpawn;
import com.qubecore.bankdefense.runtime.support.BankDefenseLocalization;
import com.qubecore.bankdefense.runtime.support.BankDefenseMatchPhaseSupport;
import com.qubecore.bankdefense.runtime.support.BankDefenseTextSupport;
import com.qubecore.bankdefense.ui.BankDefenseHud;
import com.qubecore.bankdefense.ui.BankDefenseRewardPage;
import com.qubecore.bankdefense.ui.BankDefenseSuperSlotPage;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Predicate;

public final class BankDefenseRuntime {
    private static final String TUTORIAL_AVATAR_DEFAULT = "default";
    private static final String TUTORIAL_AVATAR_HELLO = "hello";
    private static final double PATH_SPEED_SCALE = 4.0;
    private static final double VISUAL_REFRESH_ACTIVE_SECONDS = 0.1;
    private static final double VISUAL_REFRESH_IDLE_SECONDS = 0.5;
    private static final double REALTIME_ENEMY_VISUAL_SYNC_SECONDS = 0.05;
    private static final double REALTIME_TOWER_VISUAL_SYNC_SECONDS = 0.10;
    private static final double REALTIME_RANGE_PREVIEW_SYNC_SECONDS = 0.30;
    private static final double COMBAT_VISUAL_RETURN_REBUILD_RADIUS = 220.0;
    private static final double ENEMY_VISUAL_GARBAGE_SWEEP_SECONDS = 0.5;
    private static final double ROUTE_MARKER_STEP = 12.0;
    private static final int BOSS_WAVE_INTERVAL = 10;
    private static final int MINIBOSS_START_WAVE = 8;
    private static final int LEGACY_SEARCH_HEIGHT = 12;
    private static final int LEGACY_SEARCH_RADIUS = 1;
    private static final String MODEL_PLAYER = "Player";
    private static final String MODEL_TUTORIAL_WIZARD = "BankDefense_TutorialWizard";
    private static final String MODEL_WARP = "Warp";
    private static final String MODEL_OBJECTIVE = "Objective_Location_Marker";
    private static final String MODEL_SPAWN = "NPC_Spawn_Marker";
    private static final String MODEL_PATH = "NPC_Path_Marker";
    private static final String MODEL_PROJECTILE_ARROW_LIGHT = "BankDefense_Projectile_ArrowLight";
    private static final String MODEL_PROJECTILE_ARROW_HEAVY = "BankDefense_Projectile_ArrowHeavy";
    private static final String MODEL_PROJECTILE_DART_BULLET = "BankDefense_Projectile_DartBullet";
    private static final String MODEL_PROJECTILE_ROOT_DRILL = "BankDefense_Projectile_RootDrill";
    private static final String MODEL_BOSS_RIFT_TWIN_ALPHA_ON = "BankDefense_Boss_RiftTwinAlpha_On";
    private static final String MODEL_BOSS_RIFT_TWIN_ALPHA_OFF = "BankDefense_Boss_RiftTwinAlpha_Off";
    private static final String MODEL_BOSS_RIFT_TWIN_BETA_ON = "BankDefense_Boss_RiftTwinBeta_On";
    private static final String MODEL_BOSS_RIFT_TWIN_BETA_OFF = "BankDefense_Boss_RiftTwinBeta_Off";
    private static final String MODEL_BOSS_NODE_ARBITER = "BankDefense_Boss_NodeArbiter";
    private static final String MODEL_BOSS_SEAL_MASTER_1 = "BankDefense_Boss_SealMaster_1";
    private static final String MODEL_BOSS_SEAL_MASTER_2 = "BankDefense_Boss_SealMaster_2";
    private static final String MODEL_BOSS_SEAL_MASTER_3 = "BankDefense_Boss_SealMaster_3";
    private static final String PARTICLE_SHOT = System.getProperty("bankdefense.particle.shot", "Potion_Stamina_Burst");
    private static final String PARTICLE_IMPACT = System.getProperty("bankdefense.particle.impact", "Potion_Health_Implosion");
    private static final String PARTICLE_DART_TRAIL = "Daggers_Stab_Trail";
    private static final String PARTICLE_BLADE_IMPACT = "Impact_Blade_01";
    private static final String PARTICLE_DART_IMPACT = "Impact_Dagger_Stab";
    private static final String SOUND_BUILD = "SFX_BankDefense_Build";
    private static final String SOUND_FIRE_ARCHER = "SFX_BankDefense_Fire_Archer";
    private static final String SOUND_FIRE_CROSSBOW = "SFX_BankDefense_Fire_Crossbow";
    private static final String SOUND_FIRE_DART = "SFX_BankDefense_Fire_Dart";
    private static final String SOUND_FIRE_FROST = "SFX_BankDefense_Fire_Frost";
    private static final String SOUND_FIRE_SHOCK = "SFX_BankDefense_Fire_Shock";
    private static final String SOUND_FIRE_DRILL = "SFX_BankDefense_Fire_Drill";
    private static final String SOUND_IMPACT = "SFX_BankDefense_Impact";
    private static final String SOUND_COIN = "SFX_BankDefense_Coin";
    private static final String SOUND_LEAK = "SFX_BankDefense_Leak";
    private static final String SOUND_WAVE_START = "SFX_BankDefense_WaveStart";
    private static final String SOUND_WAVE_END = "SFX_BankDefense_WaveEnd";
    private static final Set<HudComponent> BANK_DEFENSE_VISIBLE_HUD_COMPONENTS = Collections.unmodifiableSet(EnumSet.of(
        HudComponent.Reticle,
        HudComponent.Chat,
        HudComponent.Requests,
        HudComponent.Notifications,
        HudComponent.KillFeed,
        HudComponent.PlayerList,
        HudComponent.EventTitle,
        HudComponent.Compass,
        HudComponent.ObjectivePanel,
        HudComponent.PortalPanel,
        HudComponent.Speedometer,
        HudComponent.AmmoIndicator,
        HudComponent.Oxygen,
        HudComponent.Sleep
    ));
    private static final String SOUND_REWARD = "SFX_BankDefense_Reward";
    private static final String SOUND_VICTORY = "SFX_BankDefense_Victory";
    private static final String SOUND_DEFEAT = "SFX_BankDefense_Defeat";
    private static final String SOUND_TUTORIAL_STEP = "SFX_BankDefense_TutorialStep";
    private static final String SOUND_TUTORIAL_WARP = "SFX_BankDefense_TutorialWarp";
    private static final String SOUND_MONOLITH_BURST = "SFX_BankDefense_MonolithBurst";
    private static final String SOUND_CORE_UNLOCK = "SFX_BankDefense_CoreUnlock";
    private static final String SOUND_ALERT = "SFX_BankDefense_Alert";
    private static final String SOUND_SELL = "SFX_BankDefense_Sell";
    private static final String SOUND_UI_CLICK = "SFX_BankDefense_UiClick";
    private static final String SOUND_UI_ERROR = "SFX_BankDefense_UiError";
    private static final String SOUND_CHEST_LOOP = "SFX_BankDefense_ChestLoop";
    private static final String SOUND_CHEST_OPEN = "SFX_BankDefense_ChestOpen";
    private static final String SOUND_TRAP_PLACE = "SFX_BankDefense_TrapPlace";
    private static final String SOUND_WORLD_ENTER = "SFX_BankDefense_WorldEnter";
    private static final String SOUND_CURSE_UI = "SFX_BankDefense_CurseUi";
    private static final String SOUND_NOTIFICATION = "SFX_BankDefense_Notification";
    private static final String SOUND_TUTORIAL_COMPLETE = "SFX_BankDefense_TutorialComplete";
    private static final String SOUND_NODE_ARBITER_RESPAWN = "SFX_BankDefense_NodeArbiterRespawn";
    private static final String SOUND_ENEMY_SPAWN_GENERIC = "SFX_BankDefense_EnemySpawnGeneric";
    private static final String SOUND_ENEMY_SPAWN_GOBLIN = "SFX_BankDefense_EnemySpawnGoblin";
    private static final String SOUND_ENEMY_SPAWN_UNDEAD = "SFX_BankDefense_EnemySpawnUndead";
    private static final String SOUND_ENEMY_SPAWN_VOID = "SFX_BankDefense_EnemySpawnVoid";
    private static final String SOUND_ENEMY_DEATH_BOSS = "SFX_BankDefense_EnemyDeathBoss";
    private static final String SOUND_ENEMY_DEATH_SKELETON = "SFX_BankDefense_EnemyDeathSkeleton";
    private static final String SOUND_ENEMY_DEATH_GOBLIN = "SFX_BankDefense_EnemyDeathGoblin";
    private static final String SOUND_ENEMY_DEATH_UNDEAD = "SFX_BankDefense_EnemyDeathUndead";
    private static final String SOUND_ENEMY_DEATH_VOID = "SFX_BankDefense_EnemyDeathVoid";
    private static final String SOUND_GOBLIN_DESTROY = "SFX_BankDefense_GoblinDestroy";
    private static final String SOUND_GOBLIN_DOWNGRADE = "SFX_BankDefense_GoblinDowngrade";
    private static final String SOUND_IMPACT_ARROW = "SFX_BankDefense_Impact_Arrow";
    private static final String SOUND_TOWER_UPGRADE = "SFX_BankDefense_TowerUpgrade";
    private static final String SOUND_MODULE_INSTALL = "SFX_BankDefense_ModuleInstall";
    private static final String SOUND_MODULE_REMOVE = "SFX_BankDefense_ModuleRemove";
    private static final String SOUND_IDOL_MODE_SWITCH = "SFX_BankDefense_IdolModeSwitch";
    private static final String SOUND_RIFT_TWIN_SWAP = "SFX_BankDefense_RiftTwinSwap";
    private static final String SOUND_RIFT_TWIN_ENRAGE = "SFX_BankDefense_RiftTwinEnrage";
    private static final String SOUND_NODE_ARBITER_LIFE_BREAK = "SFX_BankDefense_NodeArbiterLifeBreak";
    private static final String SOUND_SEAL_MASTER_CAST = "SFX_BankDefense_SealMasterCast";
    private static final String SOUND_SEAL_MASTER_PHASE_1 = "SFX_BankDefense_SealMasterPhase1";
    private static final String SOUND_SEAL_MASTER_PHASE_2 = "SFX_BankDefense_SealMasterPhase2";
    private static final String SOUND_SUPER_REACTIVATE = "SFX_BankDefense_SuperReactivate";
    private static final String SOUND_HEART_ACTIVATE = "SFX_BankDefense_HeartActivate";
    private static final String SOUND_DUO_REWARD_ALLY_PICK = "SFX_BankDefense_DuoRewardAllyPick";
    private static final String SOUND_DUO_REWARD_TEAM_BONUS = "SFX_BankDefense_DuoRewardTeamBonus";
    private static final String SOUND_DUO_REWARD_BURN = "SFX_BankDefense_DuoRewardBurn";
    private static final String SOUND_ACID_STORM_THUNDER = "SFX_BankDefense_AcidStormThunder";
    private static final String SOUND_ACID_STORM_OMEN = "SFX_BankDefense_AcidStormOmen";
    private static final String SOUND_BOSS_CAST_VAULT_BREAKER = SOUND_FIRE_SHOCK;
    private static final String SOUND_BOSS_CAST_NECRO_KING = SOUND_SEAL_MASTER_CAST;
    private static final String SOUND_BOSS_CAST_GOBLIN = SOUND_ENEMY_SPAWN_GOBLIN;
    private static final String WEATHER_ACID_STORM = "BankDefense_AcidStorm";
    private static final String WEATHER_CRIMSON_STORM = "BankDefense_CrimsonStorm";
    private static final String PARTICLE_ACID_STORM_LIGHTNING = "Lightning";
    private static final String PARTICLE_ACID_STORM_LIGHTNING_START = "Lightning_Start";
    private static final String PARTICLE_ACID_STORM_LIGHTNING_TRAIL = "Lightning_Trail";
    private static final String PARTICLE_ACID_STORM_LIGHTNING_SPARKS = "Lightning_Sparks";
    private static final String PARTICLE_ACID_STORM_LIGHTNING_POOF = "Lightning_Poof";
    private static final String PARTICLE_ACID_STORM_LIGHTNING_SMOKE = "Lightning_Smoke";
    private static final String PARTICLE_ACID_STORM_BEAM_LIGHTNING = "Beam_Lightning";
    private static final String PARTICLE_ACID_STORM_BEAM_LIGHTNING_2 = "Beam_Lightning2";
    private static final String PARTICLE_ACID_STORM_VOID_LIGHTNING = "Void_Lightning";
    private static final String PARTICLE_ACID_STORM_VOID_LIGHTNING_RANDOM = "Void_Lightning_Random";
    private static final int ACID_STORM_START_WAVE = 31;
    private static final int CRIMSON_STORM_START_WAVE = 61;
    private static final double ACID_STORM_LIGHTNING_COOLDOWN_MIN_SECONDS = 0.8;
    private static final double ACID_STORM_LIGHTNING_COOLDOWN_MAX_SECONDS = 1.55;
    private static final double ACID_STORM_OMEN_COOLDOWN_MIN_SECONDS = 9.5;
    private static final double ACID_STORM_OMEN_COOLDOWN_MAX_SECONDS = 16.5;
    private static final int ACID_STORM_BURST_MIN_STRIKES = 5;
    private static final int ACID_STORM_BURST_MAX_STRIKES = 9;
    private static final double ACID_STORM_BURST_CLUSTER_RADIUS = 14.0;
    private static final float ACID_STORM_LIGHTNING_SCALE_START = 4.6f;
    private static final float ACID_STORM_LIGHTNING_SCALE_CORE = 6.8f;
    private static final float ACID_STORM_LIGHTNING_SCALE_TRAIL = 5.9f;
    private static final float ACID_STORM_LIGHTNING_SCALE_GROUND = 4.8f;
    private static final float ACID_STORM_LIGHTNING_SCALE_BEAM = 5.4f;
    private static final String BOSS_CAST_VAULT_BREAKER_DISABLE = "vault_breaker_disable";
    private static final String BOSS_CAST_NECRO_KING_RITUAL = "necro_king_ritual";
    private static final String BOSS_CAST_GOBLIN_SABOTAGE = "goblin_boss_sabotage";
    private static final double BOSS_CAST_VAULT_BREAKER_SECONDS = 1.35;
    private static final double BOSS_CAST_NECRO_KING_SECONDS = 1.85;
    private static final double BOSS_CAST_GOBLIN_SECONDS = 1.15;
    private static final double VAULT_BREAKER_DISABLE_INTERVAL_SECONDS = 12.0;
    private static final double VAULT_BREAKER_DISABLE_DURATION_SECONDS = 3.0;
    private static final double BOSS_CAST_AURA_SCALE_MULTIPLIER = 1.75;
    private static final String VISUAL_MAX_HP_MODIFIER = "QubeCore_BankDefense_VisualMaxHp";
    private static final double ENEMY_DEATH_LINGER_SECONDS = 0.55;
    private static final double BOSS_DEATH_LINGER_SECONDS = 1.25;
    private static final double ENEMY_VISUAL_STATE_INTERVAL_SECONDS = 0.18;
    private static final double ENEMY_VISUAL_STATE_DYING_INTERVAL_SECONDS = 0.08;
    private static final double ENEMY_AURA_INTERVAL_SECONDS = 0.16;
    private static final double ENEMY_AURA_CAST_INTERVAL_SECONDS = 0.08;
    private static final double WORLD_INTERACTION_SYNC_SECONDS = 0.25;
    private static final double PINNED_SPAWN_CHUNK_SYNC_SECONDS = 0.50;
    private static final double DUO_PLAYER_COUNT_CHECK_SECONDS = 0.25;
    private static final double SPAWN_CHUNK_KEEP_LOADED_ROUTE_DISTANCE = 96.0;
    private static final double VISUAL_RESPAWN_GRACE_SECONDS = 2.5;
    private static final double VISUAL_RESPAWN_RETRY_SECONDS = 0.25;
    private static final int ENEMY_VISUAL_RECLAIM_CHUNK_RADIUS = 2;
    private static final int SPAWN_CHUNK_KEEP_LOADED_RADIUS = 1;
    private static final int HOLLOW_CHUNK_KEEP_LOADED_RADIUS = 1;
    private static final int TOWER_VISUAL_RECLAIM_CHUNK_RADIUS = 1;
    private static final int MAX_PROJECTILE_VISUALS = 96;
    private static final String KEY_CONSOLE_START = "console:start";
    private static final String KEY_CONSOLE_MENU = "console:menu";
    private static final String KEY_CONTROL_OPERATOR = "control:operator";
    private static final String KEY_MODE_SELECTOR_OPERATOR = "mode:selector";
    private static final String KEY_DUO_TEAM_OPERATOR = "duo:team";
    private static final String KEY_DUO_TRANSITION_OPERATOR = "duo:transition";
    private static final String KEY_STATS_OPERATOR = "stats:operator";
    private static final String KEY_TUTORIAL_INTRO_WIZARD = "tutorial:intro_wizard";
    private static final String KEY_TUTORIAL_GUIDE_WIZARD = "tutorial:guide_wizard";
    private static final String KEY_SLOT_PREFIX = "slot:";
    private static final String KEY_TOWER_PREFIX = "tower:";
    private static final String KEY_RANGE_PREFIX = "range:";
    private static final String SLOT_INTERACTION_BLOCK = "Utility_BankDefense_SlotMarker";
    private static final String SLOT_INTERACTION_BLOCK_OCCUPIED = "Utility_BankDefense_SlotMarkerOccupied";
    private static final String SLOT_INTERACTION_BLOCK_BLUE = "Utility_BankDefense_SlotMarkerBlue";
    private static final String SLOT_INTERACTION_BLOCK_BLUE_OCCUPIED = "Utility_BankDefense_SlotMarkerBlueOccupied";
    private static final String SLOT_INTERACTION_BLOCK_GREEN = "Utility_BankDefense_SlotMarkerGreen";
    private static final String SLOT_INTERACTION_BLOCK_GREEN_OCCUPIED = "Utility_BankDefense_SlotMarkerGreenOccupied";
    private static final String TRAP_SLOT_INTERACTION_BLOCK = "Utility_BankDefense_TrapSlotMarker";
    private static final String TRAP_SLOT_INTERACTION_BLOCK_OCCUPIED = "Utility_BankDefense_TrapSlotMarkerOccupied";
    private static final String SUPER_SLOT_INTERACTION_BLOCK = "Utility_BankDefense_SuperSlotMarker";
    private static final String MONEY_CHEST_INTERACTION_BLOCK = "Utility_BankDefense_MoneyChest";
    private static final String CORE_CHEST_INTERACTION_BLOCK = "Utility_BankDefense_CoreChest";
    private static final String CONTROL_INTERACTION_BLOCK = "Utility_BankDefense_ControlMarker";
    private static final String VENDOR_INTERACTION_BLOCK = "Utility_BankDefense_VendorMarker";
    private static final String MODE_INTERACTION_BLOCK = "Utility_BankDefense_ModeMarker";
    private static final String DUO_TEAM_INTERACTION_BLOCK = "Utility_BankDefense_DuoTeamMarker";
    private static final String DUO_INTERACTION_BLOCK = "Utility_BankDefense_DuoMarker";
    private static final String STATS_INTERACTION_BLOCK = "Utility_BankDefense_StatsMarker";
    private static final String TUTORIAL_WIZARD_INTERACTION_BLOCK = "Utility_BankDefense_TutorialWizardMarker";
    private static final int NPC_INTERACTION_BURY_DEPTH = 0;
    private static final int NPC_INTERACTION_COLUMN_HEIGHT = 2;
    private static final int NPC_INTERACTION_CLEAR_HEIGHT = 10;
    private static final float CONTROL_OPERATOR_SCALE = 1.24f;
    private static final double CONTROL_OPERATOR_Y_OFFSET = 0.02;
    private static final String KEY_VENDOR_OPERATOR = "vendor:operator";
    private static final float MODE_SELECTOR_SCALE = 1.16f;
    private static final double MODE_SELECTOR_Y_OFFSET = 0.02;
    private static final float DUO_TRANSITION_SCALE = 1.16f;
    private static final double DUO_TRANSITION_Y_OFFSET = 0.02;
    private static final float STATS_OPERATOR_SCALE = 1.16f;
    private static final double STATS_OPERATOR_Y_OFFSET = 0.02;
    private static final float TUTORIAL_WIZARD_SCALE = 1.18f;
    private static final double TUTORIAL_WIZARD_Y_OFFSET = 0.02;
    private static final String KEY_SPAWN_A = "marker:spawn_a";
    private static final String KEY_SPAWN_B = "marker:spawn_b";
    private static final String KEY_SPAWN_C = "marker:spawn_c";
    private static final float VENDOR_OPERATOR_SCALE = 1.10f;
    private static final double VENDOR_OPERATOR_Y_OFFSET = 0.02;
    private static final int SEED_IDOL_MODE_DEFAULT = 0;
    private static final int SEED_IDOL_MODE_HARVEST = 1;
    private static final int SEED_IDOL_MODE_WARD = 2;
    private static final String EFFECT_SUPER_ACTIVATION_BONUS = "super_activation_bonus";
    private static final double ROOTS_HEART_DAMAGE_BUFF_MULTIPLIER = 1.30;
    private static final double SEED_IDOL_HARVEST_BONUS_MULTIPLIER = 1.10;
    private static final double SEED_IDOL_WARD_PAYOUT_MULTIPLIER = 1.50;
    private static final double DART_ARMOR_BREAK_DURATION_SECONDS = 5.0;
    private static final double DART_ARMOR_BREAK_FLAT = 5.0;
    private static final double DART_ARMOR_BREAK_PERCENT = 0.15;
    private static final double TRAP_TRIGGER_DELAY_SECONDS = 3.0;
    private static final double TRAP_BLINK_INTERVAL_START_SECONDS = 0.52;
    private static final double TRAP_BLINK_INTERVAL_END_SECONDS = 0.07;
    private static final int MONEY_CHEST_COUNT = 5;
    private static final int CORE_CHEST_COUNT = 5;
    private static final int MONEY_CHEST_REWARD = 45;
    private static final int CORE_CHEST_REWARD = 1;
    private static final int EVENT_TOAST_ICON_MONEY = 1;
    private static final int EVENT_TOAST_ICON_CORES = 2;
    private static final int EVENT_TOAST_ICON_ALERT = 3;
    private static final double HUD_INTERACTION_PROMPT_MAX_DISTANCE = 6.25;
    private static final double HUD_INTERACTION_PROMPT_STEP = 0.20;
    private static final double TUTORIAL_VAULT_BREAKER_HP = 150_000.0;
    private static final double TUTORIAL_TELEPORT_EFFECT_DELAY_MS = 180.0;
    private static final int TUTORIAL_TRAP_WAVE_NUMBER = 5;
    private static final double TUTORIAL_TRAP_NEXT_SPAWN_DELAY_SECONDS = 1.0;
    private static final double TUTORIAL_TRAP_RELEASE_DELAY_SECONDS = 1.5;
    private static final String TUTORIAL_CHEST_ID = "tutorial:gaia_chest";
    private static final String TUTORIAL_TRAP_ENEMY_ID = "thief";
    private static final String ENEMY_GOBLIN_SABOTEUR = "goblin_saboteur";
    private static final String ENEMY_NECRO_GUARDIAN = "necro_guardian";
    private static final String ENEMY_RIFT_TWIN_ALPHA = "rift_twin_alpha";
    private static final String ENEMY_RIFT_TWIN_BETA = "rift_twin_beta";
    private static final String ENEMY_NODE_ARBITER = "node_arbiter";
    private static final String ENEMY_SEAL_MASTER = "seal_master";
    private static final String ENEMY_SEAL_NODE = "seal_node";
    private static final double SABOTEUR_REFUND_RATE = 0.20;
    private static final double[] GOBLIN_BOSS_DOWNGRADE_THRESHOLDS = new double[]{0.30, 0.60, 0.90};
    private static final double DUO_RIFT_TWIN_SWAP_SECONDS = 6.5;
    private static final double DUO_SEAL_MASTER_FIRST_THRESHOLD = 0.33;
    private static final double DUO_SEAL_MASTER_SECOND_THRESHOLD = 0.66;
    private static final double DUO_SEAL_MASTER_SPEED_MULTIPLIER = 1.20;
    private static final int DUO_SEAL_MASTER_CURSE_WAVES = 5;
    private static final int DUO_SEAL_MASTER_FAST_KILL_CURSES = 3;
    private static final int DUO_SEAL_MASTER_MID_KILL_CURSES = 2;
    private static final int DUO_SEAL_MASTER_LATE_KILL_CURSES = 1;
    private static final double DUO_SEAL_CURSE_RANGE_MULTIPLIER = 0.88;
    private static final double DUO_SEAL_CURSE_FIRE_RATE_MULTIPLIER = 0.90;
    private static final double DUO_SEAL_CURSE_TRAP_DELAY_MULTIPLIER = 1.35;
    private static final double DUO_SEAL_CURSE_DAMAGE_MULTIPLIER = 0.88;
    private static final int DUO_SEAL_CURSE_PREPARATION_SECONDS = 15;
    private static final double DUO_SEAL_CURSE_ENEMY_SPEED_MULTIPLIER = 1.06;
    private static final double DUO_SEAL_CURSE_UPGRADE_COST_MULTIPLIER = 1.10;
    private static final double DUO_SEAL_CURSE_MODULE_EFFECT_MULTIPLIER = 0.85;
    private static final double DUO_SEAL_CURSE_GOLD_MULTIPLIER = 0.80;
    private static final double DUO_NODE_ARBITER_FINAL_RESPAWN_PROGRESS = 10.0;
    private static final double DUO_NODE_ARBITER_FINAL_LANE_THRESHOLD = 0.76;
    private static final String DIFFICULTY_EASY = "easy";
    private static final String DIFFICULTY_NORMAL = "normal";
    private static final String DIFFICULTY_HARD = "hard";
    private static final String DIFFICULTY_NIGHTMARE = "nightmare";
    private static final String MODE_SOLO = "solo";
    private static final String MODE_DUO = "duo";
    private static final String TEAM_BLUE = "blue";
    private static final String TEAM_GREEN = "green";
    private static final String TEAM_SHARED = "shared";
    private static final Map<String, Integer> LEGACY_PROGRESSION_REFUND_COSTS = createLegacyProgressionRefundCosts();

    private final BankDefenseRepository repository;
    private final Map<String, MatchContext> matchesByWorld = new ConcurrentHashMap<>();
    private final Map<String, PresentationState> presentationsByWorld = new ConcurrentHashMap<>();
    private final Map<UUID, String> hudInteractionPromptByPlayer = new ConcurrentHashMap<>();
    private final Map<String, String> selectedDifficultyIdsByWorld = new ConcurrentHashMap<>();
    private final Map<String, String> selectedContractIdsByWorld = new ConcurrentHashMap<>();
    private final Map<String, String> selectedModeIdsByWorld = new ConcurrentHashMap<>();
    private final Map<String, Map<UUID, String>> duoTeamSelectionsByWorld = new ConcurrentHashMap<>();
    private final Map<String, Boolean> duoSoloTestEnabledByWorld = new ConcurrentHashMap<>();
    private final Map<String, Set<UUID>> hiddenHudPlayersByWorld = new ConcurrentHashMap<>();
    private final Map<String, Integer> selectedStartWavesByWorld = new ConcurrentHashMap<>();
    private final Map<String, TutorialState> tutorialStatesByWorld = new ConcurrentHashMap<>();
    private final Map<String, Integer> soundIndexCache = new ConcurrentHashMap<>();
    private final Map<String, Set<Long>> pinnedSpawnChunkIndexesByWorld = new ConcurrentHashMap<>();
    private final Map<String, Double> pinnedSpawnChunkSyncAccumulatedSecondsByWorld = new ConcurrentHashMap<>();
    private final Set<String> loggedNpcVisualSuccess = ConcurrentHashMap.newKeySet();
    private final Set<String> loggedNpcVisualFailure = ConcurrentHashMap.newKeySet();
    private final PlayerSkin controlNpcSkin = this.defaultControlNpcSkin();
    private final PlayerSkin vendorNpcSkin = this.defaultVendorNpcSkin();
    private final PlayerSkin tutorialWizardNpcSkin = this.defaultTutorialWizardNpcSkin();
    private final PlayerSkin duoNpcSkin = this.defaultDuoNpcSkin();
    private final PlayerSkin duoTeamNpcSkin = this.defaultDuoTeamNpcSkin();
    private final PlayerSkin statsNpcSkin = this.defaultStatsNpcSkin();
    public BankDefenseRuntime(BankDefenseRepository repository) {
        this.repository = repository;
    }

    public BankDefenseRepository.Snapshot loadSnapshot(World world) throws IOException {
        return this.repository.loadSnapshot(world);
    }

    public BankDefenseRepository.Snapshot loadDuoSnapshot(World world) throws IOException {
        BankDefenseRepository.Snapshot baseSnapshot = this.repository.loadSnapshot(world);
        if (baseSnapshot == null) {
            return null;
        }
        MapConfig duoMap = this.buildDuoLayoutMap(baseSnapshot.map);
        BuildSlotsConfig duoSlots = this.filterBuildSlotsForMode(baseSnapshot.buildSlots, MatchMode.Duo, false);
        return new BankDefenseRepository.Snapshot(
            baseSnapshot.gameRules,
            baseSnapshot.towers,
            baseSnapshot.enemies,
            baseSnapshot.modules,
            baseSnapshot.waves,
            baseSnapshot.branding,
            baseSnapshot.contracts,
            baseSnapshot.progression,
            duoMap,
            duoSlots
        );
    }

    public MatchState getMatchState(World world) {
        MatchContext context = this.matchesByWorld.get(this.worldKey(world));
        return context == null ? null : context.state.copy();
    }

    public List<DifficultyButtonState> getDifficultyOptions() {
        return this.getDifficultyOptions(null);
    }

    public List<DifficultyButtonState> getDifficultyOptions(World world) {
        List<DifficultyButtonState> options = new ArrayList<>();
        boolean tutorialActive = world != null && this.isTutorialActive(world);
        for (DifficultyProfile profile : this.difficultyProfiles()) {
            DifficultyButtonState button = new DifficultyButtonState();
            button.difficultyId = profile.id;
            button.displayName = this.difficultyDisplayName(world, profile);
            button.note = this.difficultyNote(world, profile);
            if (tutorialActive && !DIFFICULTY_EASY.equals(profile.id)) {
                button.enabled = false;
                button.note = button.note + "\n" + this.tr(world, "tutorial.unavailable_in_tutorial");
            }
            options.add(button);
        }
        return options;
    }

    public String getSelectedDifficultyId(World world) {
        if (world != null && this.isTutorialActive(world)) {
            return DIFFICULTY_EASY;
        }
        return this.selectedDifficultyIdsByWorld.getOrDefault(this.worldKey(world), DIFFICULTY_NORMAL);
    }

    public String getSelectedDifficultyName(World world) {
        return this.difficultyDisplayName(world, this.resolveDifficultyProfile(this.getSelectedDifficultyId(world)));
    }

    public String getSelectedModeId(World world) {
        if (world == null) {
            return MODE_SOLO;
        }
        String worldKey = this.worldKey(world);
        String selectedModeId = this.selectedModeIdsByWorld.get(worldKey);
        if (selectedModeId != null && !selectedModeId.isBlank()) {
            return this.normalizeModeId(selectedModeId);
        }
        try {
            MapConfig mapConfig = this.repository.loadMapConfig(world);
            String persistedModeId = mapConfig == null ? MODE_SOLO : this.normalizeModeId(mapConfig.selectedModeId);
            this.selectedModeIdsByWorld.put(worldKey, persistedModeId);
            return persistedModeId;
        } catch (IOException ignored) {
            return MODE_SOLO;
        }
    }

    public String getEffectiveModeId(World world) {
        if (world == null) {
            return MODE_SOLO;
        }
        if (this.isTutorialActive(world)) {
            return "tutorial";
        }
        return MODE_DUO.equalsIgnoreCase(this.getSelectedModeId(world)) ? MODE_DUO : MODE_SOLO;
    }

    public boolean isDuoGameplayMode(World world) {
        return world != null && MODE_DUO.equalsIgnoreCase(this.getEffectiveModeId(world));
    }

    private MatchMode selectedMatchMode(World world) {
        return MODE_DUO.equalsIgnoreCase(this.getSelectedModeId(world)) ? MatchMode.Duo : MatchMode.Solo;
    }

    private String normalizeModeId(String modeId) {
        return MODE_DUO.equalsIgnoreCase(modeId) ? MODE_DUO : MODE_SOLO;
    }

    private boolean isDuoMode(MatchContext context) {
        return context != null && context.matchMode == MatchMode.Duo;
    }

    private boolean isDuoMode(World world) {
        return world != null && this.selectedMatchMode(world) == MatchMode.Duo;
    }

    public boolean isDuoSoloTestEnabled(World world) {
        return world != null && Boolean.TRUE.equals(this.duoSoloTestEnabledByWorld.get(this.worldKey(world)));
    }

    private boolean isDuoSoloTestEnabled(MatchContext context) {
        return context != null && context.duoSoloTestEnabled;
    }

    private String normalizeTeam(String teamId) {
        if (teamId == null || teamId.isBlank()) {
            return TEAM_SHARED;
        }
        String normalized = teamId.trim().toLowerCase(Locale.ROOT);
        return switch (normalized) {
            case TEAM_BLUE -> TEAM_BLUE;
            case TEAM_GREEN -> TEAM_GREEN;
            default -> TEAM_SHARED;
        };
    }

    private String slotOwnerTeam(BuildSlot slot) {
        if (slot == null) {
            return TEAM_SHARED;
        }
        if (this.isDuoLayoutSlot(slot) && this.isTrapSlot(slot)) {
            return TEAM_SHARED;
        }
        return this.normalizeTeam(slot.ownerTeam);
    }

    private String oppositeTeam(String teamId) {
        return switch (this.normalizeTeam(teamId)) {
            case TEAM_BLUE -> TEAM_GREEN;
            case TEAM_GREEN -> TEAM_BLUE;
            default -> TEAM_SHARED;
        };
    }

    private String laneForTeam(String teamId) {
        return switch (this.normalizeTeam(teamId)) {
            case TEAM_GREEN -> "c";
            case TEAM_BLUE -> "a";
            default -> "a";
        };
    }

    private String teamForPlayer(MatchContext context, PlayerRef playerRef) {
        if (context == null || playerRef == null || playerRef.getUuid() == null) {
            return TEAM_SHARED;
        }
        return context.teamByPlayerUuid.getOrDefault(playerRef.getUuid(), TEAM_SHARED);
    }

    private List<PlayerRef> duoPlayers(World world) {
        if (world == null) {
            return List.of();
        }
        List<PlayerRef> players = new ArrayList<>(world.getPlayerRefs());
        players.removeIf(playerRef -> playerRef == null || playerRef.getUuid() == null);
        players.sort(Comparator.comparing(playerRef -> playerRef.getUuid().toString()));
        if (players.size() <= 2) {
            return players;
        }
        return new ArrayList<>(players.subList(0, 2));
    }

    private Map<UUID, String> duoTeamSelections(World world) {
        if (world == null) {
            return new LinkedHashMap<>();
        }
        return this.duoTeamSelectionsByWorld.computeIfAbsent(this.worldKey(world), ignored -> new ConcurrentHashMap<>());
    }

    private void cleanupDuoTeamSelections(World world) {
        if (world == null) {
            return;
        }
        Set<UUID> onlinePlayerUuids = new HashSet<>();
        for (PlayerRef playerRef : world.getPlayerRefs()) {
            if (playerRef != null && playerRef.getUuid() != null) {
                onlinePlayerUuids.add(playerRef.getUuid());
            }
        }
        Map<UUID, String> selections = this.duoTeamSelections(world);
        selections.entrySet().removeIf(entry -> entry == null || entry.getKey() == null || !onlinePlayerUuids.contains(entry.getKey()));
    }

    private String claimedDuoTeam(World world, PlayerRef playerRef) {
        if (world == null || playerRef == null || playerRef.getUuid() == null) {
            return TEAM_SHARED;
        }
        this.cleanupDuoTeamSelections(world);
        return this.normalizeTeam(this.duoTeamSelections(world).get(playerRef.getUuid()));
    }

    private boolean isDuoTeamClaimed(World world, String teamId) {
        String normalizedTeam = this.normalizeTeam(teamId);
        if ((!TEAM_BLUE.equals(normalizedTeam) && !TEAM_GREEN.equals(normalizedTeam)) || world == null) {
            return false;
        }
        this.cleanupDuoTeamSelections(world);
        for (String selectedTeam : this.duoTeamSelections(world).values()) {
            if (normalizedTeam.equals(this.normalizeTeam(selectedTeam))) {
                return true;
            }
        }
        return false;
    }

    public boolean isDuoTeamSelectionComplete(World world) {
        if (!this.isDuoGameplayMode(world)) {
            return true;
        }
        this.cleanupDuoTeamSelections(world);
        return this.isDuoTeamClaimed(world, TEAM_BLUE) && this.isDuoTeamClaimed(world, TEAM_GREEN);
    }

    private boolean areDuoGameplayNpcsUnlocked(World world) {
        if (world == null || !this.isDuoGameplayMode(world)) {
            return true;
        }
        if (this.isDuoTeamSelectionComplete(world)) {
            return true;
        }
        return this.isDuoSoloTestEnabled(world)
            && (this.isDuoTeamClaimed(world, TEAM_BLUE) || this.isDuoTeamClaimed(world, TEAM_GREEN));
    }

    private boolean hasChosenDuoSide(World world, PlayerRef playerRef) {
        String claimedTeam = this.claimedDuoTeam(world, playerRef);
        return TEAM_BLUE.equals(claimedTeam) || TEAM_GREEN.equals(claimedTeam);
    }

    private String duoNpcLockedMessage(World world, PlayerRef playerRef) {
        if (world == null || !this.isDuoGameplayMode(world)) {
            return "";
        }
        if (!this.hasChosenDuoSide(world, playerRef)) {
            return this.choose(playerRef, "Сначала выберите сторону.", "Choose a side first.");
        }
        if (this.isDuoTeamSelectionComplete(world) || this.isDuoSoloTestEnabled(world)) {
            return "";
        }
        return this.choose(playerRef, "Ждём второго игрока: он должен выбрать другую сторону.", "Waiting for the second player: they must choose the other side.");
    }

    private void sendPlayerMessage(World world, PlayerRef playerRef, String text) {
        if (world == null || playerRef == null || playerRef.getUuid() == null || text == null || text.isBlank()) {
            return;
        }
        for (Player player : world.getPlayers()) {
            if (player == null || player.getPlayerRef() == null || !playerRef.getUuid().equals(player.getPlayerRef().getUuid())) {
                continue;
            }
            player.sendMessage(Message.raw(BankDefenseLocalization.translateFreeform(playerRef, text)));
            return;
        }
    }

    private void sendLocalizedWorldMessage(World world, String russian, String english) {
        this.sendLocalizedWorldMessage(world, playerRef -> this.choose(playerRef, russian, english));
    }

    private void sendLocalizedWorldMessage(World world, Function<PlayerRef, String> messageFactory) {
        if (world == null || messageFactory == null) {
            return;
        }
        for (Player player : world.getPlayers()) {
            if (player == null || player.getPlayerRef() == null) {
                continue;
            }
            PlayerRef playerRef = player.getPlayerRef();
            String text = messageFactory.apply(playerRef);
            if (text == null || text.isBlank()) {
                continue;
            }
            player.sendMessage(Message.raw(BankDefenseLocalization.translateFreeform(playerRef, text)));
        }
    }

    public boolean blockDuoNpcUiIfNeeded(World world, PlayerRef playerRef) {
        String message = this.duoNpcLockedMessage(world, playerRef);
        if (message == null || message.isBlank()) {
            return false;
        }
        this.sendPlayerMessage(world, playerRef, message);
        this.playUiErrorSound(world, playerRef);
        return true;
    }

    public boolean blockSlotUiIfNeeded(World world, PlayerRef playerRef, String slotId) throws IOException {
        String message = this.slotUiLockedMessage(world, playerRef, slotId);
        if (message == null || message.isBlank()) {
            return false;
        }
        this.sendPlayerMessage(world, playerRef, message);
        this.playUiErrorSound(world, playerRef);
        return true;
    }

    private String slotUiLockedMessage(World world, PlayerRef playerRef, String slotId) throws IOException {
        if (world == null || slotId == null || slotId.isBlank()) {
            return "";
        }
        MatchContext context = this.getOrCreateContext(world);
        if (context == null || context.tutorialActive || context.state == null || context.state.gameStarted) {
            return "";
        }
        BuildSlot slot = this.findSlotById(context.snapshot.buildSlots, slotId);
        if (slot == null || this.isSuperSlot(slot) || this.isTrapSlot(slot) || context.placedTowers.containsKey(slotId)) {
            return "";
        }
        return this.choose(playerRef, "Сначала начни игру у оператора.", "Start the game with the operator first.");
    }

    public void playUiActionFeedback(World world, PlayerRef playerRef, ActionResult result) {
        if (result == null || result.success) {
            this.playUiClickSound(world, playerRef);
            return;
        }
        this.playUiErrorSound(world, playerRef);
    }

    public void playUiClickSound(World world, PlayerRef playerRef) {
        this.playUiSoundForPlayer(world, playerRef, SOUND_UI_CLICK);
    }

    public void playUiErrorSound(World world, PlayerRef playerRef) {
        this.playUiSoundForPlayer(world, playerRef, SOUND_UI_ERROR);
    }

    public void playWorldEnterSound(World world, PlayerRef playerRef) {
        this.playUiSoundForPlayer(world, playerRef, SOUND_WORLD_ENTER);
    }

    private void playUiSoundForPlayer(World world, PlayerRef playerRef, String soundId) {
        if (world == null || playerRef == null || soundId == null || soundId.isBlank()) {
            return;
        }
        Integer soundIndex = this.resolveSoundIndex(soundId);
        if (soundIndex == null || soundIndex.intValue() < 0) {
            return;
        }
        SoundUtil.playSoundEvent2dToPlayer(playerRef, soundIndex.intValue(), SoundCategory.UI);
    }

    public ActionResult setDuoSoloTestEnabled(World world, boolean enabled) {
        if (world == null) {
            return ActionResult.fail("World is unavailable.");
        }
        String worldKey = this.worldKey(world);
        if (enabled) {
            this.duoSoloTestEnabledByWorld.put(worldKey, Boolean.TRUE);
        } else {
            this.duoSoloTestEnabledByWorld.remove(worldKey);
        }
        MatchContext context = this.matchesByWorld.get(worldKey);
        if (context != null) {
            context.duoSoloTestEnabled = enabled;
        }
        this.refreshVisualizationIfEnabled(world);
        return ActionResult.ok(enabled
            ? this.choose(world, "Duo solo-test: ВКЛ. Один игрок может выбрать сторону и запускать тесты в одиночку.", "Duo solo test: ON. One player can pick a side and run Duo tests alone.")
            : this.choose(world, "Duo solo-test: ВЫКЛ. Режим снова требует двух игроков.", "Duo solo test: OFF. Duo requires two players again."));
    }

    private Set<UUID> hiddenHudPlayers(World world) {
        if (world == null) {
            return Set.of();
        }
        return this.hiddenHudPlayersByWorld.computeIfAbsent(this.worldKey(world), ignored -> ConcurrentHashMap.newKeySet());
    }

    public boolean isCustomHudHidden(World world, PlayerRef playerRef) {
        return world != null
            && playerRef != null
            && playerRef.getUuid() != null
            && this.hiddenHudPlayers(world).contains(playerRef.getUuid());
    }

    public ActionResult setCustomHudHidden(World world, PlayerRef playerRef, boolean hidden) {
        if (world == null) {
            return ActionResult.fail(this.choose(playerRef, "Мир недоступен.", "World is unavailable."));
        }
        if (playerRef == null || playerRef.getUuid() == null) {
            return ActionResult.fail(this.choose((PlayerRef)null, "Игрок недоступен.", "The player is unavailable."));
        }
        Set<UUID> hiddenPlayers = this.hiddenHudPlayers(world);
        if (hidden) {
            hiddenPlayers.add(playerRef.getUuid());
        } else {
            hiddenPlayers.remove(playerRef.getUuid());
        }
        this.applyCustomHudVisibility(world, playerRef);
        return ActionResult.ok(hidden
            ? this.choose(playerRef, "Кастомный HUD скрыт.", "Custom HUD hidden.")
            : this.choose(playerRef, "Кастомный HUD показан.", "Custom HUD shown."));
    }

    public ActionResult toggleCustomHud(World world, PlayerRef playerRef) {
        boolean hidden = !this.isCustomHudHidden(world, playerRef);
        return this.setCustomHudHidden(world, playerRef, hidden);
    }

    private void applyCustomHudVisibility(World world, PlayerRef playerRef) {
        if (world == null || playerRef == null || playerRef.getReference() == null || !playerRef.getReference().isValid()) {
            return;
        }
        Store<EntityStore> store = world.getEntityStore().getStore();
        Player player = store.getComponent(playerRef.getReference(), Player.getComponentType());
        if (player == null) {
            return;
        }
        if (this.isCustomHudHidden(world, playerRef)) {
            if (player.getHudManager().getCustomHud() != null) {
                player.getHudManager().setCustomHud(playerRef, null);
            }
            return;
        }
        PresentationState presentation = this.presentationsByWorld.get(this.worldKey(world));
        if (presentation == null) {
            return;
        }
        BankDefenseHud hud = presentation.huds.get(playerRef.getUuid());
        if (hud == null) {
            return;
        }
        if (player.getHudManager().getCustomHud() != hud) {
            player.getHudManager().setCustomHud(playerRef, hud);
        }
        if (!player.getHudManager().getVisibleHudComponents().equals(BANK_DEFENSE_VISIBLE_HUD_COMPONENTS)) {
            player.getHudManager().setVisibleHudComponents(playerRef, BANK_DEFENSE_VISIBLE_HUD_COMPONENTS);
        }
    }

    public ActionResult fillDuoPadsWithMaxTowers(World world, PlayerRef actor) throws IOException {
        if (world == null) {
            return ActionResult.fail(this.choose(actor, "Мир недоступен.", "World is unavailable."));
        }
        MatchContext context = this.getOrCreateContext(world);
        if (context.snapshot == null || context.snapshot.buildSlots == null || context.snapshot.buildSlots.slots == null) {
            return ActionResult.fail(this.choose(actor, "Слоты карты не загружены.", "Map slots are not loaded."));
        }
        Vec3i objectiveAnchor = this.duoBuildObjectiveAnchor(context);
        int totalDuoSlots = 0;
        int placed = 0;
        int replaced = 0;
        int skipped = 0;
        String standardTowerId = null;
        String trapTowerId = null;
        String superTowerId = null;
        for (BuildSlot slot : context.snapshot.buildSlots.slots) {
            if (!this.isDuoLayoutSlot(slot)) {
                continue;
            }
            totalDuoSlots++;
            TowerDefinition targetTower = this.pickMostExpensiveTowerForSlot(context, slot);
            if (targetTower == null || targetTower.levels == null || targetTower.levels.isEmpty()) {
                skipped++;
                continue;
            }
            if (this.isSuperSlot(slot)) {
                superTowerId = targetTower.id;
            } else if (this.isTrapSlot(slot)) {
                trapTowerId = targetTower.id;
            } else {
                standardTowerId = targetTower.id;
            }
            TowerInstance existing = context.placedTowers.get(slot.id);
            if (existing != null) {
                if (existing.equippedModuleId != null && !existing.equippedModuleId.isBlank()) {
                    this.addModuleToInventory(context, existing.equippedModuleId, 1);
                }
                if ("seed_idol".equals(existing.definition.id) && existing.idolStoredCurrency > 0) {
                    this.grantTeamIncome(context, existing.idolStoredCurrency);
                }
                replaced++;
            } else {
                placed++;
            }
            TowerInstance instance = new TowerInstance(slot, targetTower, SEED_IDOL_MODE_DEFAULT);
            instance.levelIndex = Math.max(0, targetTower.levels.size() - 1);
            if (objectiveAnchor != null) {
                instance.visualRotation = this.rotationTowards(
                    this.towerWorldPosition(slot),
                    this.objectiveWorldPosition(objectiveAnchor, 0.05)
                );
            }
            context.placedTowers.put(slot.id, instance);
        }
        if (totalDuoSlots <= 0) {
            return ActionResult.fail(this.choose(actor, "На этой карте не найдено duo-падов.", "No Duo pads were found on this map."));
        }
        this.refreshVisualizationIfEnabled(world);
        return ActionResult.ok(
            this.choose(actor, "Тестовая расстановка Duo готова. Заполнено: ", "Duo test fill complete. Filled: ")
                + placed
                + this.choose(actor, ", заменено: ", ", replaced: ")
                + replaced
                + this.choose(actor, ", пропущено: ", ", skipped: ")
                + skipped
                + this.choose(actor, ". Башни: standard=", ". Towers: standard=")
                + (standardTowerId == null || standardTowerId.isBlank() ? "-" : standardTowerId)
                + ", trap=" + (trapTowerId == null || trapTowerId.isBlank() ? "-" : trapTowerId)
                + ", super=" + (superTowerId == null || superTowerId.isBlank() ? "-" : superTowerId)
                + this.choose(actor, ". Все duo-пады выставлены в максимальный уровень для тестов.", ". All Duo pads were set to their maximum test level.")
        );
    }

    public DuoTeamSelectUiState getDuoTeamSelectUiState(World world, PlayerRef viewerRef) {
        DuoTeamSelectUiState state = new DuoTeamSelectUiState();
        state.duoMode = this.isDuoGameplayMode(world);
        if (!state.duoMode) {
            return state;
        }
        this.cleanupDuoTeamSelections(world);
        state.viewerTeam = this.claimedDuoTeam(world, viewerRef);
        state.blueClaimed = this.isDuoTeamClaimed(world, TEAM_BLUE);
        state.greenClaimed = this.isDuoTeamClaimed(world, TEAM_GREEN);
        state.complete = state.blueClaimed && state.greenClaimed;
        state.blueAvailable = !state.blueClaimed || TEAM_BLUE.equals(state.viewerTeam);
        state.greenAvailable = !state.greenClaimed || TEAM_GREEN.equals(state.viewerTeam);
        return state;
    }

    public ActionResult selectDuoTeam(World world, PlayerRef playerRef, String teamId) throws IOException {
        if (world == null || playerRef == null || playerRef.getUuid() == null) {
            return ActionResult.fail(this.choose((PlayerRef)null, "Игрок недоступен.", "The player is unavailable."));
        }
        if (!this.isDuoGameplayMode(world)) {
            return ActionResult.fail(this.choose(playerRef, "Выбор стороны доступен только в Duo режиме.", "Side selection is only available in Duo mode."));
        }
        String normalizedTeam = this.normalizeTeam(teamId);
        if (!TEAM_BLUE.equals(normalizedTeam) && !TEAM_GREEN.equals(normalizedTeam)) {
            return ActionResult.fail(this.choose(playerRef, "Неизвестная команда.", "Unknown team."));
        }

        this.cleanupDuoTeamSelections(world);
        Map<UUID, String> selections = this.duoTeamSelections(world);
        for (Map.Entry<UUID, String> entry : selections.entrySet()) {
            if (entry == null || entry.getKey() == null || entry.getValue() == null) {
                continue;
            }
            if (!entry.getKey().equals(playerRef.getUuid()) && normalizedTeam.equals(this.normalizeTeam(entry.getValue()))) {
                return ActionResult.fail(
                    TEAM_BLUE.equals(normalizedTeam)
                        ? this.choose(playerRef, "Синяя команда уже занята.", "Blue team is already taken.")
                        : this.choose(playerRef, "Зелёная команда уже занята.", "Green team is already taken.")
                );
            }
        }

        selections.put(playerRef.getUuid(), normalizedTeam);
        MatchContext context = this.matchesByWorld.get(this.worldKey(world));
        if (context != null && this.isDuoMode(context)) {
            this.assignDuoTeams(world, context);
        }
        this.refreshVisualizationIfEnabled(world);
        this.playUiSoundForPlayer(world, playerRef, SOUND_ALERT);

        if (this.isDuoTeamSelectionComplete(world)) {
            return ActionResult.ok(this.choose(playerRef, "Стороны выбраны. Остальные NPC разблокированы.", "Sides locked in. The other NPCs are now available."));
        }
        if (this.isDuoSoloTestEnabled(world)) {
            return ActionResult.ok(
                TEAM_BLUE.equals(normalizedTeam)
                    ? this.choose(playerRef, "Ты выбрал синюю сторону. Duo solo-test активен: можно тестировать одному.", "You picked the blue side. Duo solo test is active, so you can test alone.")
                    : this.choose(playerRef, "Ты выбрал зелёную сторону. Duo solo-test активен: можно тестировать одному.", "You picked the green side. Duo solo test is active, so you can test alone.")
            );
        }
        return ActionResult.ok(
            TEAM_BLUE.equals(normalizedTeam)
                ? this.choose(playerRef, "Ты выбрал синюю сторону. Ждём второго игрока.", "You picked the blue side. Waiting for the second player.")
                : this.choose(playerRef, "Ты выбрал зелёную сторону. Ждём второго игрока.", "You picked the green side. Waiting for the second player.")
        );
    }

    private void assignDuoTeams(World world, MatchContext context) {
        if (world == null || context == null) {
            return;
        }
        context.teamByPlayerUuid.clear();
        this.cleanupDuoTeamSelections(world);
        for (Map.Entry<UUID, String> entry : this.duoTeamSelections(world).entrySet()) {
            if (entry == null || entry.getKey() == null) {
                continue;
            }
            String teamId = this.normalizeTeam(entry.getValue());
            if (TEAM_BLUE.equals(teamId) || TEAM_GREEN.equals(teamId)) {
                context.teamByPlayerUuid.put(entry.getKey(), teamId);
            }
        }
    }

    private int duoPlayerCount(World world, MatchContext context) {
        if (world == null || context == null) {
            return 0;
        }
        Set<UUID> onlinePlayerUuids = new HashSet<>();
        for (PlayerRef playerRef : world.getPlayerRefs()) {
            if (playerRef != null && playerRef.getUuid() != null) {
                onlinePlayerUuids.add(playerRef.getUuid());
            }
        }
        int count = 0;
        for (UUID uuid : context.teamByPlayerUuid.keySet()) {
            if (uuid != null && onlinePlayerUuids.contains(uuid)) {
                count++;
            }
        }
        return count;
    }

    private int currencyForPlayer(MatchContext context, PlayerRef playerRef) {
        if (context == null) {
            return 0;
        }
        if (!this.isDuoMode(context)) {
            return context.state.currency;
        }
        if (this.isDuoSoloTestEnabled(context)) {
            return context.blueCurrency + context.greenCurrency;
        }
        return switch (this.teamForPlayer(context, playerRef)) {
            case TEAM_GREEN -> context.greenCurrency;
            case TEAM_BLUE -> context.blueCurrency;
            default -> 0;
        };
    }

    private void syncTotalCurrency(MatchContext context) {
        if (context == null) {
            return;
        }
        if (!this.isDuoMode(context)) {
            context.state.currency = Math.max(0, context.state.currency);
            return;
        }
        context.blueCurrency = Math.max(0, context.blueCurrency);
        context.greenCurrency = Math.max(0, context.greenCurrency);
        context.state.currency = context.blueCurrency + context.greenCurrency;
    }

    private boolean spendCurrency(MatchContext context, PlayerRef playerRef, int amount) {
        if (context == null || amount < 0) {
            return false;
        }
        if (!this.isDuoMode(context)) {
            if (context.state.currency < amount) {
                return false;
            }
            context.state.currency -= amount;
            return true;
        }
        if (this.isDuoSoloTestEnabled(context)) {
            if ((context.blueCurrency + context.greenCurrency) < amount) {
                return false;
            }
            int spentFromBlue = Math.min(context.blueCurrency, amount);
            context.blueCurrency -= spentFromBlue;
            int remaining = amount - spentFromBlue;
            if (remaining > 0) {
                context.greenCurrency -= remaining;
            }
            this.syncTotalCurrency(context);
            return true;
        }
        String team = this.teamForPlayer(context, playerRef);
        if (TEAM_BLUE.equals(team)) {
            if (context.blueCurrency < amount) {
                return false;
            }
            context.blueCurrency -= amount;
        } else if (TEAM_GREEN.equals(team)) {
            if (context.greenCurrency < amount) {
                return false;
            }
            context.greenCurrency -= amount;
        } else {
            return false;
        }
        this.syncTotalCurrency(context);
        return true;
    }

    private void refundCurrencyToTeam(MatchContext context, String teamId, int amount) {
        if (context == null || amount <= 0) {
            return;
        }
        if (!this.isDuoMode(context)) {
            context.state.currency += amount;
            return;
        }
        switch (this.normalizeTeam(teamId)) {
            case TEAM_GREEN -> context.greenCurrency += amount;
            case TEAM_BLUE -> context.blueCurrency += amount;
            default -> {
                context.blueCurrency += amount / 2;
                context.greenCurrency += amount / 2;
                if ((amount & 1) != 0) {
                    if (context.oddIncomeRemainderToBlue) {
                        context.blueCurrency += 1;
                    } else {
                        context.greenCurrency += 1;
                    }
                    context.oddIncomeRemainderToBlue = !context.oddIncomeRemainderToBlue;
                }
            }
        }
        this.syncTotalCurrency(context);
    }

    private int grantTeamIncome(MatchContext context, int amount) {
        if (context == null || amount <= 0) {
            return 0;
        }
        if (!this.isDuoMode(context)) {
            context.state.currency += amount;
            return amount;
        }
        int half = amount / 2;
        context.blueCurrency += half;
        context.greenCurrency += half;
        if ((amount & 1) != 0) {
            if (context.oddIncomeRemainderToBlue) {
                context.blueCurrency += 1;
            } else {
                context.greenCurrency += 1;
            }
            context.oddIncomeRemainderToBlue = !context.oddIncomeRemainderToBlue;
        }
        this.syncTotalCurrency(context);
        return amount;
    }

    private boolean canPlayerUseSlot(World world, MatchContext context, PlayerRef playerRef, BuildSlot slot) {
        if (context == null || slot == null || !this.isDuoMode(context)) {
            return true;
        }
        if (this.isDuoSoloTestEnabled(context)) {
            return true;
        }
        String ownerTeam = this.slotOwnerTeam(slot);
        if (TEAM_SHARED.equals(ownerTeam)) {
            return true;
        }
        String playerTeam = this.teamForPlayer(context, playerRef);
        return ownerTeam.equals(playerTeam);
    }

    private String deniedSlotAccessMessage(World world, BuildSlot slot) {
        return this.deniedSlotAccessMessage(this.primaryPlayerRef(world), slot);
    }

    private String deniedSlotAccessMessage(PlayerRef playerRef, BuildSlot slot) {
        String ownerTeam = this.slotOwnerTeam(slot);
        return switch (ownerTeam) {
            case TEAM_BLUE -> this.choose(playerRef, "Этот слот принадлежит синему фронту.", "This slot belongs to the blue front.");
            case TEAM_GREEN -> this.choose(playerRef, "Этот слот принадлежит зелёному фронту.", "This slot belongs to the green front.");
            default -> this.choose(playerRef, "Этот слот сейчас недоступен.", "This slot is unavailable right now.");
        };
    }

    public void updateHoveredInteractionPrompt(World world, PlayerRef playerRef, Ref<EntityStore> targetRef, Vec3i targetBlock) {
        if (world == null || playerRef == null) {
            return;
        }
        String prompt = "";
        String worldKey = this.worldKey(world);
        MatchContext context = this.matchesByWorld.get(worldKey);
        PresentationState presentation = this.presentationsByWorld.get(worldKey);
        BankDefenseRepository.Snapshot snapshot = context == null ? null : context.snapshot;

        if (snapshot != null && snapshot.map != null) {
            String wizardKind = this.resolveTutorialWizardKind(snapshot, this.tutorialState(world), targetBlock);
            if (wizardKind != null) {
                prompt = this.tr(world, "hud.interact.wizard");
            } else {
                String presentationKey = this.findPresentationKey(presentation, targetRef);
                if (KEY_TUTORIAL_INTRO_WIZARD.equals(presentationKey) || KEY_TUTORIAL_GUIDE_WIZARD.equals(presentationKey)) {
                    prompt = this.tr(world, "hud.interact.wizard");
                } else {
                    InteractionTarget target = this.resolveInteractionTarget(snapshot, context, presentation, targetRef, targetBlock);
                    prompt = this.hudInteractionLabel(world, target, context);
                }
            }
        }

        this.hudInteractionPromptByPlayer.put(playerRef.getUuid(), prompt == null ? "" : prompt);
    }

    public boolean isTutorialActive(World world) {
        return world != null && this.tutorialState(world).active;
    }

    public String resolveTutorialWizardKind(World world, Vec3i targetBlock) {
        if (world == null || targetBlock == null) {
            return null;
        }
        try {
            return this.resolveTutorialWizardKind(this.repository.loadSnapshot(world), this.tutorialState(world), targetBlock);
        } catch (IOException ignored) {
            return null;
        }
    }

    private String resolveTutorialWizardKind(
        BankDefenseRepository.Snapshot snapshot,
        TutorialState tutorial,
        Vec3i targetBlock
    ) {
        if (snapshot == null || snapshot.map == null || tutorial == null || targetBlock == null) {
            return null;
        }
        if (!tutorial.active && this.matchesInteractionPoint(snapshot.map.tutorialWizardIntroPoint, targetBlock, 1)) {
            return "intro";
        }
        if (tutorial.active && this.matchesInteractionPoint(snapshot.map.tutorialWizardPoint, targetBlock, 1)) {
            return "guide";
        }
        return null;
    }

    public TutorialDialogUiState getTutorialDialogUiState(World world, String wizardKind) throws IOException {
        if (world == null || wizardKind == null || wizardKind.isBlank()) {
            return null;
        }
        BankDefenseRepository.Snapshot snapshot = this.repository.loadSnapshot(world);
        TutorialState tutorial = this.tutorialState(world);
        MatchContext matchContext = this.matchesByWorld.get(this.worldKey(world));
        TutorialDialogUiState state = new TutorialDialogUiState();
        state.visible = true;
        state.speakerName = this.tr(world, "npc.wizard");
        state.avatarKind = TUTORIAL_AVATAR_DEFAULT;

        if ("intro".equals(wizardKind)) {
            if (tutorial.active) {
                return null;
            }
            TutorialStage introStage = tutorial.stage == TutorialStage.IntroPageTwo
                ? TutorialStage.IntroPageTwo
                : TutorialStage.IntroPageOne;
            if (introStage == TutorialStage.IntroPageOne) {
                state.avatarKind = TUTORIAL_AVATAR_HELLO;
                state.bodyText = this.tr(world, "tutorial.dialog.intro.page1.body");
                state.buttonText = this.tr(world, "tutorial.dialog.intro.page1.button");
                state.hintText = this.tr(world, "tutorial.dialog.intro.page1.hint");
                state.closeOnAdvance = false;
                return state;
            }
            state.avatarKind = TUTORIAL_AVATAR_HELLO;
            state.bodyText = this.tr(world, "tutorial.dialog.intro.page2.body");
            state.buttonText = this.tr(world, "tutorial.dialog.intro.page2.button");
            state.hintText = snapshot.map.tutorialStartPoint == null
                ? this.tr(world, "tutorial.dialog.intro.page2.hint.missing_start")
                : this.tr(world, "tutorial.dialog.intro.page2.hint.ready");
            state.closeOnAdvance = true;
            return state;
        }

        if (!tutorial.active) {
            return null;
        }
        this.reconcileTutorialProgression(matchContext, tutorial);

        switch (tutorial.stage) {
            case FindGuideWizard -> {
                state.avatarKind = TUTORIAL_AVATAR_HELLO;
                state.bodyText = this.tr(world, "tutorial.dialog.find.body");
                state.buttonText = this.tr(world, "tutorial.dialog.find.button");
                state.hintText = this.tr(world, "tutorial.dialog.find.hint");
                state.closeOnAdvance = false;
            }
            case GuideToFirstTower -> {
                state.avatarKind = TUTORIAL_AVATAR_HELLO;
                state.bodyText = this.tr(world, "tutorial.dialog.first_tower.body");
                state.buttonText = this.tr(world, "tutorial.dialog.first_tower.button");
                state.hintText = this.tr(world, "tutorial.dialog.first_tower.hint");
                state.closeOnAdvance = true;
            }
            case ReturnAfterFirstTower -> {
                state.bodyText = this.tr(world, "tutorial.dialog.return_first_tower.body");
                state.buttonText = this.tr(world, "tutorial.dialog.return_first_tower.button");
                state.hintText = this.tr(world, "tutorial.dialog.return_first_tower.hint");
                state.closeOnAdvance = true;
            }
            case ReturnAfterFirstWave -> {
                state.bodyText = this.tr(world, "tutorial.dialog.return_first_wave.body");
                state.buttonText = this.tr(world, "tutorial.dialog.return_first_wave.button");
                state.hintText = this.tr(world, "tutorial.dialog.return_first_wave.hint");
                state.closeOnAdvance = true;
            }
            case ReturnAfterSecondTowerUpgrade -> {
                state.bodyText = this.tr(world, "tutorial.dialog.chest_intro.body");
                state.buttonText = this.tr(world, "tutorial.dialog.chest_intro.button");
                state.hintText = this.tr(world, "tutorial.dialog.chest_intro.hint");
                state.closeOnAdvance = true;
            }
            case ReturnAfterTutorialChest -> {
                state.bodyText = this.tr(world, "tutorial.dialog.return_chest.body");
                state.buttonText = this.tr(world, "tutorial.dialog.return_chest.button");
                state.hintText = this.tr(world, "tutorial.dialog.return_chest.hint");
                state.closeOnAdvance = true;
            }
            case ReturnAfterReward -> {
                state.bodyText = this.tr(world, "tutorial.dialog.return_reward.body");
                state.buttonText = this.tr(world, "tutorial.dialog.return_reward.button");
                state.hintText = this.tr(world, "tutorial.dialog.return_reward.hint");
                state.closeOnAdvance = true;
            }
            case ReturnAfterTwoLaneWave -> {
                state.bodyText = this.tr(world, "tutorial.dialog.return_two_lane.body");
                state.buttonText = this.tr(world, "tutorial.dialog.return_two_lane.button");
                state.hintText = this.tr(world, "tutorial.dialog.return_two_lane.hint");
                state.closeOnAdvance = true;
            }
            case ReturnAfterBossWave -> {
                state.bodyText = this.tr(world, "tutorial.dialog.trap_intro.body");
                state.buttonText = this.tr(world, "tutorial.dialog.trap_intro.button");
                state.hintText = this.tr(world, "tutorial.dialog.trap_intro.hint");
                state.closeOnAdvance = true;
            }
            case ReturnAfterTrapWave -> {
                state.bodyText = this.tr(world, "tutorial.dialog.return_traps.body");
                state.buttonText = this.tr(world, "tutorial.dialog.return_traps.button");
                state.hintText = this.tr(world, "tutorial.dialog.return_traps.hint");
                state.closeOnAdvance = true;
            }
            case ReturnAfterProgression -> {
                state.bodyText = this.tr(world, "tutorial.dialog.return_progression.body");
                state.buttonText = this.tr(world, "tutorial.dialog.return_progression.button");
                state.hintText = this.tr(world, "tutorial.dialog.return_progression.hint");
                state.closeOnAdvance = false;
            }
            case FinalPageTwo -> {
                state.bodyText = this.tr(world, "tutorial.dialog.final.body");
                state.buttonText = this.tr(world, "tutorial.dialog.final.button");
                state.hintText = this.tr(world, "tutorial.dialog.final.hint");
                state.closeOnAdvance = true;
            }
            default -> {
                state.bodyText = this.tr(world, "tutorial.dialog.finish_current.body", this.tutorialQuestText(world, tutorial));
                state.buttonText = this.tr(world, "tutorial.dialog.finish_current.button");
                state.hintText = this.tr(world, "tutorial.dialog.finish_current.hint");
            }
        }
        return state;
    }

    public ActionResult advanceTutorialDialog(World world, Ref<EntityStore> playerEntityRef, String wizardKind) throws IOException {
        if (world == null || wizardKind == null || wizardKind.isBlank()) {
            return ActionResult.fail(this.tr(world, "tutorial.dialog.unavailable"));
        }
        BankDefenseRepository.Snapshot snapshot = this.repository.loadSnapshot(world);
        TutorialState tutorial = this.tutorialState(world);
        MatchContext matchContext = this.matchesByWorld.get(this.worldKey(world));
        if ("intro".equals(wizardKind)) {
            if (tutorial.active) {
                return ActionResult.fail(this.tr(world, "tutorial.dialog.already_running"));
            }
            if (tutorial.stage == TutorialStage.IntroPageTwo) {
                return this.beginTutorial(world, snapshot, tutorial, playerEntityRef);
            }
            if (tutorial.stage == TutorialStage.Inactive || tutorial.stage == TutorialStage.Completed || tutorial.stage == TutorialStage.IntroPageOne) {
                tutorial.stage = TutorialStage.IntroPageTwo;
                this.playWorldUiSound(world, SOUND_TUTORIAL_STEP);
                return ActionResult.ok(this.tr(world, "tutorial.dialog.show_ground"));
            }
            return ActionResult.fail(this.tr(world, "tutorial.dialog.intro_not_finished"));
        }
        if (!tutorial.active) {
            return ActionResult.fail(this.tr(world, "tutorial.dialog.start_first"));
        }
        this.reconcileTutorialProgression(matchContext, tutorial);

        return switch (tutorial.stage) {
            case FindGuideWizard -> {
                tutorial.stage = TutorialStage.GuideToFirstTower;
                this.playWorldUiSound(world, SOUND_TUTORIAL_STEP);
                yield ActionResult.ok(this.tr(world, "tutorial.dialog.show_first_task"));
            }
            case GuideToFirstTower -> {
                tutorial.stage = TutorialStage.PlaceFirstTower;
                this.refreshVisualization(world);
                this.playWorldUiSound(world, SOUND_TUTORIAL_STEP);
                yield ActionResult.ok(this.tr(world, "tutorial.dialog.start_first_tower"));
            }
            case ReturnAfterFirstTower -> {
                tutorial.stage = TutorialStage.PrepareFirstWave;
                tutorial.firstWavePrepared = false;
                this.refreshVisualization(world);
                this.playWorldUiSound(world, SOUND_TUTORIAL_STEP);
                yield ActionResult.ok(this.tr(world, "tutorial.dialog.operator_ready"));
            }
            case ReturnAfterFirstWave -> {
                tutorial.stage = TutorialStage.BuildSecondTower;
                tutorial.secondTowerSlotId = null;
                this.ensureTutorialCurrency(world, 1000);
                this.refreshVisualization(world);
                this.playWorldUiSound(world, SOUND_TUTORIAL_STEP);
                yield ActionResult.ok(this.tr(world, "tutorial.dialog.second_tower_funds"));
            }
            case ReturnAfterSecondTowerUpgrade -> {
                MatchContext context = this.getOrCreateContext(world);
                this.spawnTutorialChest(world, context);
                tutorial.stage = TutorialStage.CollectTutorialChest;
                this.refreshVisualization(world);
                this.playWorldUiSound(world, SOUND_TUTORIAL_STEP);
                yield ActionResult.ok(this.tr(world, "tutorial.dialog.chest_spawned"));
            }
            case ReturnAfterTutorialChest -> {
                tutorial.stage = TutorialStage.WaitSecondWaveFinish;
                tutorial.secondWavePrepared = false;
                this.refreshVisualization(world);
                this.playWorldUiSound(world, SOUND_TUTORIAL_STEP);
                yield ActionResult.ok(this.tr(world, "tutorial.dialog.second_wave_ready"));
            }
            case ReturnAfterReward -> {
                tutorial.stage = TutorialStage.FillAllSlots;
                this.ensureTutorialCurrency(world, 10000);
                this.refreshVisualization(world);
                this.playWorldUiSound(world, SOUND_TUTORIAL_STEP);
                yield ActionResult.ok(this.tr(world, "tutorial.dialog.fill_all_slots"));
            }
            case ReturnAfterTwoLaneWave -> {
                tutorial.stage = TutorialStage.PlaceMonolith;
                this.refreshVisualization(world);
                this.playWorldUiSound(world, SOUND_TUTORIAL_STEP);
                yield ActionResult.ok(this.tr(world, "tutorial.dialog.place_monolith"));
            }
            case ReturnAfterBossWave -> {
                tutorial.stage = TutorialStage.PlaceUniqueTraps;
                tutorial.trapWavePrepared = false;
                this.ensureTutorialCurrency(world, 1000);
                this.refreshVisualization(world);
                this.playWorldUiSound(world, SOUND_TUTORIAL_STEP);
                yield ActionResult.ok(this.tr(world, "tutorial.dialog.traps_ready"));
            }
            case ReturnAfterTrapWave -> {
                MatchContext context = this.getOrCreateContext(world);
                if (context.progression != null) {
                    context.progression.cores += 12;
                    if (!context.tutorialActive) {
                        this.repository.savePlayerProgression(context.progression);
                    }
                }
                tutorial.stage = TutorialStage.SpendCores;
                this.refreshVisualization(world);
                this.playWorldUiSound(world, SOUND_TUTORIAL_STEP);
                yield ActionResult.ok(this.tr(world, "tutorial.dialog.given_cores"));
            }
            case ReturnAfterProgression -> {
                tutorial.stage = TutorialStage.FinalPageTwo;
                this.playWorldUiSound(world, SOUND_TUTORIAL_STEP);
                yield ActionResult.ok(this.tr(world, "tutorial.dialog.final_secret"));
            }
            case FinalPageTwo -> this.completeTutorial(world, snapshot, tutorial, playerEntityRef);
            default -> ActionResult.ok(this.tr(world, "tutorial.dialog.complete_current_first"));
        };
    }

    private TutorialState tutorialState(World world) {
        return this.tutorialStatesByWorld.computeIfAbsent(this.worldKey(world), ignored -> new TutorialState());
    }

    private ActionResult beginTutorial(
        World world,
        BankDefenseRepository.Snapshot snapshot,
        TutorialState tutorial,
        Ref<EntityStore> playerEntityRef
    ) throws IOException {
        String validationError = this.validateTutorialSetup(world, snapshot);
        if (validationError != null) {
            return ActionResult.fail(validationError);
        }
        tutorial.active = true;
        tutorial.stage = TutorialStage.FindGuideWizard;
        tutorial.firstWavePrepared = false;
        tutorial.secondWavePrepared = false;
        tutorial.twoLaneWavePrepared = false;
        tutorial.bossWavePrepared = false;
        tutorial.trapWavePrepared = false;
        tutorial.rewardChosen = false;
        tutorial.moduleEquipped = false;
        tutorial.secondTowerSlotId = null;
        this.selectedDifficultyIdsByWorld.remove(this.worldKey(world));
        this.selectedContractIdsByWorld.remove(this.worldKey(world));
        this.selectedStartWavesByWorld.put(this.worldKey(world), Integer.valueOf(1));
        this.forceSoloModeForTutorial(world);
        this.matchesByWorld.put(this.worldKey(world), this.createFreshContext(world));
        this.playTutorialTeleportSequence(world, snapshot.map.tutorialStartPoint);
        this.teleportAllPlayers(world, snapshot.map.tutorialStartPoint, this.playerStartRotation(this.buildTutorialLayoutMap(snapshot.map, tutorial), null));
        this.scheduleVisualizationRefreshAfterTeleport(world);
        this.playWorldUiSound(world, SOUND_TUTORIAL_WARP);
        this.refreshVisualization(world);
        return ActionResult.ok(this.tr(world, "tutorial.dialog.begin_ok"));
    }

    private ActionResult completeTutorial(
        World world,
        BankDefenseRepository.Snapshot snapshot,
        TutorialState tutorial,
        Ref<EntityStore> playerEntityRef
    ) throws IOException {
        PresentationState presentation = this.presentationsByWorld.get(this.worldKey(world));
        if (presentation != null) {
            this.clearVisualEntities(world, presentation);
        }
        tutorial.active = false;
        tutorial.stage = TutorialStage.Completed;
        this.forceSoloModeForTutorial(world);
        MatchContext context = this.createFreshContext(world);
        this.persistTutorialCompletionState(world, context, true);
        this.selectedDifficultyIdsByWorld.remove(this.worldKey(world));
        this.selectedContractIdsByWorld.remove(this.worldKey(world));
        this.selectedStartWavesByWorld.put(this.worldKey(world), Integer.valueOf(1));
        this.matchesByWorld.put(this.worldKey(world), context);
        this.playTutorialTeleportSequence(world, snapshot.map.playerStart);
        this.teleportAllPlayers(world, snapshot.map.playerStart, this.playerStartRotation(snapshot.map, null));
        this.scheduleVisualizationRefreshAfterTeleport(world);
        this.playWorldUiSound(world, SOUND_TUTORIAL_WARP);
        this.playWorldUiSound(world, SOUND_TUTORIAL_COMPLETE);
        this.refreshVisualization(world);
        return ActionResult.ok(this.tr(world, "tutorial.dialog.complete_ok"));
    }

    public ActionResult resetTutorial(World world, Ref<EntityStore> playerEntityRef) throws IOException {
        if (world == null) {
            return ActionResult.fail(this.tr(world, "tutorial.dialog.world_unavailable"));
        }
        BankDefenseRepository.Snapshot snapshot = this.repository.loadSnapshot(world);
        TutorialState tutorial = this.tutorialState(world);
        tutorial.stage = TutorialStage.Inactive;
        tutorial.active = false;
        tutorial.firstWavePrepared = false;
        tutorial.secondWavePrepared = false;
        tutorial.twoLaneWavePrepared = false;
        tutorial.bossWavePrepared = false;
        tutorial.trapWavePrepared = false;
        tutorial.rewardChosen = false;
        tutorial.moduleEquipped = false;
        tutorial.secondTowerSlotId = null;

        this.forceSoloModeForTutorial(world);
        MatchContext context = this.createFreshContext(world);
        this.persistTutorialCompletionState(world, context, false);
        this.matchesByWorld.put(this.worldKey(world), context);
        Vec3i targetPoint = snapshot.map.tutorialWizardIntroPoint != null
            ? snapshot.map.tutorialWizardIntroPoint
            : snapshot.map.playerStart;
        if (targetPoint != null) {
            this.playTutorialTeleportSequence(world, targetPoint);
            this.teleportAllPlayers(world, targetPoint, this.playerStartRotation(snapshot.map, null));
            this.scheduleVisualizationRefreshAfterTeleport(world);
            this.playWorldUiSound(world, SOUND_TUTORIAL_WARP);
        }
        this.refreshVisualization(world);
        return ActionResult.ok(this.tr(world, "tutorial.dialog.reset_ok"));
    }

    public ActionResult skipTutorial(World world, Ref<EntityStore> playerEntityRef) throws IOException {
        if (world == null) {
            return ActionResult.fail(this.tr(world, "tutorial.dialog.world_unavailable"));
        }
        BankDefenseRepository.Snapshot snapshot = this.repository.loadSnapshot(world);
        TutorialState tutorial = this.tutorialState(world);
        PresentationState presentation = this.presentationsByWorld.get(this.worldKey(world));
        if (presentation != null) {
            this.clearVisualEntities(world, presentation);
        }
        tutorial.stage = TutorialStage.Completed;
        tutorial.active = false;
        tutorial.firstWavePrepared = false;
        tutorial.secondWavePrepared = false;
        tutorial.twoLaneWavePrepared = false;
        tutorial.bossWavePrepared = false;
        tutorial.trapWavePrepared = false;
        tutorial.rewardChosen = false;
        tutorial.moduleEquipped = false;
        tutorial.secondTowerSlotId = null;

        this.forceSoloModeForTutorial(world);
        MatchContext context = this.createFreshContext(world);
        this.persistTutorialCompletionState(world, context, true);
        this.matchesByWorld.put(this.worldKey(world), context);
        this.selectedDifficultyIdsByWorld.remove(this.worldKey(world));
        this.selectedContractIdsByWorld.remove(this.worldKey(world));
        this.selectedStartWavesByWorld.put(this.worldKey(world), Integer.valueOf(1));
        if (snapshot.map != null && snapshot.map.playerStart != null) {
            this.playTutorialTeleportSequence(world, snapshot.map.playerStart);
            this.teleportAllPlayers(world, snapshot.map.playerStart, this.playerStartRotation(snapshot.map, null));
            this.scheduleVisualizationRefreshAfterTeleport(world);
            this.playWorldUiSound(world, SOUND_TUTORIAL_WARP);
            this.playWorldUiSound(world, SOUND_TUTORIAL_COMPLETE);
        }
        this.refreshVisualization(world);
        return ActionResult.ok(this.tr(world, "tutorial.dialog.skip_ok"));
    }

    private void ensureTutorialCurrency(World world, int minimumCurrency) throws IOException {
        MatchContext context = this.getOrCreateContext(world);
        if (context.state.currency < minimumCurrency) {
            context.state.currency = minimumCurrency;
        }
    }

    private String validateTutorialSetup(World world, BankDefenseRepository.Snapshot snapshot) {
        if (snapshot == null || snapshot.map == null) {
            return this.tr(world, "tutorial.validate.map_unavailable");
        }
        MapConfig map = snapshot.map;
        if (map.tutorialStartPoint == null) {
            return this.tr(world, "tutorial.validate.start_point");
        }
        if (map.tutorialWizardIntroPoint == null) {
            return this.tr(world, "tutorial.validate.wizard_intro");
        }
        if (map.tutorialWizardPoint == null) {
            return this.tr(world, "tutorial.validate.wizard_point");
        }
        if (map.tutorialControlPoint == null) {
            return this.tr(world, "tutorial.validate.control");
        }
        if (map.tutorialVendorPoint == null) {
            return this.tr(world, "tutorial.validate.vendor");
        }
        if (map.tutorialBankCenter == null || map.tutorialVaultPoint == null) {
            return this.tr(world, "tutorial.validate.bank_and_vault");
        }
        if (map.tutorialSpawnPointA == null || map.tutorialSpawnPointB == null) {
            return this.tr(world, "tutorial.validate.spawns");
        }
        if (map.tutorialRoutePointsA == null || map.tutorialRoutePointsA.isEmpty() || map.tutorialRoutePointsB == null || map.tutorialRoutePointsB.isEmpty()) {
            return this.tr(world, "tutorial.validate.routes");
        }
        int tutorialStandardSlots = 0;
        int tutorialSuperSlots = 0;
        for (BuildSlot slot : snapshot.buildSlots.slots) {
            if (slot == null) {
                continue;
            }
            String slotType = slot.slotType == null ? "standard" : slot.slotType.toLowerCase(Locale.ROOT);
            if (slotType.startsWith("tutorial")) {
                if (slotType.endsWith("super")) {
                    tutorialSuperSlots++;
                } else if (!slotType.endsWith("trap")) {
                    tutorialStandardSlots++;
                }
            }
        }
        if (tutorialStandardSlots < 6 || tutorialSuperSlots < 1) {
            return this.tr(world, "tutorial.validate.slots");
        }
        return null;
    }

    private String tutorialQuestText(World world, TutorialState tutorial) {
        if (tutorial == null || !tutorial.active) {
            return "";
        }
        return switch (tutorial.stage) {
            case FindGuideWizard -> this.tr(world, "tutorial.quest.find_wizard");
            case GuideToFirstTower, ReturnAfterFirstTower, ReturnAfterFirstWave, ReturnAfterSecondTowerUpgrade, ReturnAfterTutorialChest,
                ReturnAfterReward, ReturnAfterTwoLaneWave, ReturnAfterBossWave, ReturnAfterTrapWave -> this.tr(world, "tutorial.quest.approach_wizard");
            case PlaceFirstTower -> this.tr(world, "tutorial.quest.place_first_tower");
            case PrepareFirstWave -> this.tr(world, "tutorial.quest.start_wave");
            case WaitFirstWaveFinish -> this.tr(world, "tutorial.quest.repel_first_wave");
            case BuildSecondTower -> this.tr(world, "tutorial.quest.upgrade_any_tower");
            case CollectTutorialChest -> this.tr(world, "tutorial.quest.collect_chest");
            case WaitSecondWaveFinish -> this.tr(world, "tutorial.quest.hold_next_wave");
            case ChooseRewardModule -> this.tr(world, "tutorial.quest.choose_module");
            case FillAllSlots -> this.tr(world, "tutorial.quest.fill_all_slots");
            case WaitTwoLaneWaveFinish -> this.tr(world, "tutorial.quest.two_exit_wave");
            case PlaceMonolith -> this.tr(world, "tutorial.quest.place_monolith");
            case WaitBossWaveFinish -> this.tr(world, "tutorial.quest.withstand_ravager");
            case PlaceUniqueTraps -> this.tr(world, "tutorial.quest.place_unique_traps");
            case PrepareTrapWave -> this.tr(world, "tutorial.quest.start_wave");
            case WaitTrapWaveFinish -> this.tr(world, "tutorial.quest.watch_traps");
            case SpendCores -> this.tr(world, "tutorial.quest.spend_shards");
            case ReturnAfterProgression, FinalPageTwo -> this.tr(world, "tutorial.quest.talk_wizard");
            default -> "";
        };
    }

    private boolean hudTutorialQuestVisible(BankDefenseRepository.Snapshot snapshot, MatchContext context) {
        if (context != null && context.tutorialActive) {
            return true;
        }
        return snapshot != null
            && snapshot.map != null
            && snapshot.map.tutorialWizardIntroPoint != null
            && context != null
            && (context.progression == null || !context.progression.tutorialCompleted);
    }

    private String hudTutorialQuestText(World world, BankDefenseRepository.Snapshot snapshot, MatchContext context) {
        if (context != null && context.tutorialActive) {
            return this.tutorialQuestText(world, this.tutorialState(world));
        }
        if (this.hudTutorialQuestVisible(snapshot, context)) {
            return this.tr(world, "tutorial.quest.find_wizard");
        }
        return "";
    }

    private boolean hudPreTutorialIntro(BankDefenseRepository.Snapshot snapshot, MatchContext context) {
        MatchState state = context == null ? null : context.state;
        return snapshot != null
            && snapshot.map != null
            && snapshot.map.tutorialWizardIntroPoint != null
            && context != null
            && (context.progression == null || !context.progression.tutorialCompleted)
            && !context.tutorialActive
            && state != null
            && !state.gameStarted
            && BankDefenseMatchPhaseSupport.isWaitingForSetup(state);
    }

    private void updateTutorialProgressionFromContext(World world, MatchContext context) {
        if (world == null || context == null || !context.tutorialActive) {
            return;
        }
        TutorialState tutorial = this.tutorialState(world);
        boolean stageChanged = this.reconcileTutorialProgression(context, tutorial);
        switch (tutorial.stage) {
            case PlaceFirstTower -> {
                if (this.tutorialFirstTowerBuilt(context)) {
                    tutorial.stage = TutorialStage.ReturnAfterFirstTower;
                    stageChanged = true;
                }
            }
            case BuildSecondTower -> {
                if (this.tutorialSecondTowerReady(context)) {
                    tutorial.stage = TutorialStage.ReturnAfterSecondTowerUpgrade;
                    stageChanged = true;
                }
            }
            case FillAllSlots -> {
                if (this.tutorialAllStandardTowersReady(context) && this.tutorialAnyModuleEquipped(context)) {
                    tutorial.stage = TutorialStage.WaitTwoLaneWaveFinish;
                    tutorial.twoLaneWavePrepared = false;
                    stageChanged = true;
                }
            }
            case PlaceMonolith -> {
                if (this.tutorialMonolithPlaced(context)) {
                    tutorial.stage = TutorialStage.WaitBossWaveFinish;
                    tutorial.bossWavePrepared = false;
                    stageChanged = true;
                }
            }
            case PlaceUniqueTraps -> {
                if (this.tutorialTrapSetReady(context)) {
                    tutorial.stage = TutorialStage.PrepareTrapWave;
                    tutorial.trapWavePrepared = false;
                    stageChanged = true;
                }
            }
            default -> {
            }
        }
        if (stageChanged) {
            this.playWorldUiSound(world, SOUND_TUTORIAL_STEP);
            this.refreshVisualizationIfEnabled(world);
        }
    }

    private boolean tutorialFirstTowerBuilt(MatchContext context) {
        for (BuildSlot slot : this.tutorialStandardSlots(context)) {
            TowerInstance tower = context.placedTowers.get(slot.id);
            if (tower != null && "guard_post".equals(tower.definition.id)) {
                return true;
            }
        }
        return false;
    }

    private boolean tutorialSecondTowerReady(MatchContext context) {
        if (context == null) {
            return false;
        }
        for (TowerInstance tower : context.placedTowers.values()) {
            if (tower != null && !tower.definition.superTower && !tower.definition.trapTower && !"guard_post".equals(tower.definition.id) && tower.levelIndex >= 2) {
                return true;
            }
        }
        return false;
    }

    private boolean tutorialAllStandardTowersReady(MatchContext context) {
        List<BuildSlot> slots = this.tutorialStandardSlots(context);
        if (slots.size() < 6) {
            return false;
        }
        Set<String> requiredTowerIds = new HashSet<>(List.of(
            "guard_post",
            "rapid_security",
            "sniper_desk",
            "freeze_gate",
            "shock_relay",
            "armor_drill"
        ));
        Set<String> placedTowerIds = new HashSet<>();
        for (int index = 0; index < 6; index++) {
            BuildSlot slot = slots.get(index);
            TowerInstance tower = context.placedTowers.get(slot.id);
            if (tower == null || tower.levelIndex < 4) {
                return false;
            }
            if (!requiredTowerIds.contains(tower.definition.id) || !placedTowerIds.add(tower.definition.id)) {
                return false;
            }
        }
        return placedTowerIds.containsAll(requiredTowerIds);
    }

    private boolean tutorialAnyModuleEquipped(MatchContext context) {
        for (TowerInstance tower : context.placedTowers.values()) {
            if (tower != null && tower.equippedModuleId != null && !tower.equippedModuleId.isBlank()) {
                return true;
            }
        }
        return false;
    }

    private boolean tutorialTrapSetReady(MatchContext context) {
        List<BuildSlot> trapSlots = this.tutorialRequiredTrapSlots(context);
        if (trapSlots.size() < 3) {
            return false;
        }
        Set<String> placedTrapIds = new HashSet<>();
        for (int i = 0; i < 3; i++) {
            BuildSlot slot = trapSlots.get(i);
            TowerInstance tower = context.placedTowers.get(slot.id);
            if (tower == null || tower.definition == null || !tower.definition.trapTower || !placedTrapIds.add(tower.definition.id)) {
                return false;
            }
        }
        return true;
    }

    private boolean tutorialMonolithPlaced(MatchContext context) {
        BuildSlot superSlot = this.tutorialSuperSlot(context);
        if (superSlot == null) {
            return false;
        }
        TowerInstance tower = context.placedTowers.get(superSlot.id);
        return tower != null && "storm_monolith".equals(tower.definition.id);
    }

    private BuildSlot tutorialSuperSlot(MatchContext context) {
        if (context == null || context.snapshot == null || context.snapshot.buildSlots == null) {
            return null;
        }
        for (BuildSlot slot : context.snapshot.buildSlots.slots) {
            if (slot != null && this.isSuperSlot(slot)) {
                return slot;
            }
        }
        return null;
    }

    private BuildSlot tutorialStandardSlot(MatchContext context, int index) {
        List<BuildSlot> slots = this.tutorialStandardSlots(context);
        if (index < 0 || index >= slots.size()) {
            return null;
        }
        return slots.get(index);
    }

    private List<BuildSlot> tutorialTrapSlots(MatchContext context) {
        List<BuildSlot> slots = new ArrayList<>();
        if (context == null || context.snapshot == null || context.snapshot.buildSlots == null) {
            return slots;
        }
        for (BuildSlot slot : context.snapshot.buildSlots.slots) {
            if (slot != null && this.isTrapSlot(slot)) {
                slots.add(slot);
            }
        }
        slots.sort(Comparator.comparing(slot -> slot.id == null ? "" : slot.id));
        return slots;
    }

    private List<BuildSlot> tutorialRequiredTrapSlots(MatchContext context) {
        List<BuildSlot> slots = this.tutorialTrapSlots(context);
        if (slots.size() <= 3) {
            return slots;
        }
        return new ArrayList<>(slots.subList(0, 3));
    }

    private void spawnTutorialChest(World world, MatchContext context) {
        if (world == null || context == null || context.snapshot == null || context.snapshot.map == null) {
            return;
        }
        Vec3i chestPoint = firstPoint(context.snapshot.map.chestSpawnPoints);
        if (chestPoint == null) {
            return;
        }
        context.activeChests.put(TUTORIAL_CHEST_ID, new ActiveChest(TUTORIAL_CHEST_ID, chestPoint, ChestType.Money));
        this.syncChestInteractionBlocks(world, context.snapshot.map, context);
    }

    private String tutorialTrapReleaseSlotId(MatchContext context, int sequenceIndex) {
        List<BuildSlot> trapSlots = this.tutorialRequiredTrapSlots(context);
        if (trapSlots.isEmpty()) {
            return "";
        }
        int clampedIndex = Math.max(0, Math.min(sequenceIndex, trapSlots.size() - 1));
        BuildSlot slot = trapSlots.get(clampedIndex);
        return slot == null || slot.id == null ? "" : slot.id;
    }

    private String slotDisplayName(BuildSlot slot) {
        if (slot == null) {
            return "";
        }
        String label = slot.label == null || slot.label.isBlank() ? slot.id : slot.label;
        return label == null ? "" : this.text(label);
    }

    private String tutorialTrapLaneId(MatchContext context, int sequenceIndex) {
        if (sequenceIndex == 1 && this.hasLaneRoute(context, "b")) {
            return "b";
        }
        if (sequenceIndex == 2 && this.hasLaneRoute(context, "c")) {
            return "c";
        }
        return this.hasLaneRoute(context, "a") ? "a" : "b";
    }

    private List<BuildSlot> tutorialStandardSlots(MatchContext context) {
        List<BuildSlot> slots = new ArrayList<>();
        if (context == null || context.snapshot == null || context.snapshot.buildSlots == null) {
            return slots;
        }
        for (BuildSlot slot : context.snapshot.buildSlots.slots) {
            if (slot == null || this.isSuperSlot(slot) || this.isTrapSlot(slot)) {
                continue;
            }
            slots.add(slot);
        }
        slots.sort(Comparator.comparing(slot -> slot.id == null ? "" : slot.id));
        return slots;
    }

    private String tutorialAssignedTowerId(int slotIndex) {
        return switch (slotIndex) {
            case 0 -> "guard_post";
            case 1 -> "rapid_security";
            case 2 -> "sniper_desk";
            case 3 -> "freeze_gate";
            case 4 -> "shock_relay";
            case 5 -> "armor_drill";
            default -> null;
        };
    }

    private String tutorialAssignedTowerId(MatchContext context, BuildSlot slot) {
        if (context == null || slot == null) {
            return null;
        }
        List<BuildSlot> standardSlots = this.tutorialStandardSlots(context);
        for (int index = 0; index < standardSlots.size(); index++) {
            if (slot.id != null && slot.id.equals(standardSlots.get(index).id)) {
                return this.tutorialAssignedTowerId(index);
            }
        }
        return null;
    }

    private ActionResult validateTutorialBuildChoice(World world, MatchContext context, BuildSlot slot, TowerDefinition tower) {
        if (world == null || context == null || !context.tutorialActive || slot == null || tower == null) {
            return null;
        }
        TutorialState tutorial = this.tutorialState(world);
        if (this.isSuperSlot(slot)) {
            if (tutorial.stage != TutorialStage.PlaceMonolith && tutorial.stage != TutorialStage.WaitBossWaveFinish) {
                return ActionResult.fail(this.choose(world, "Супер-башня в обучении понадобится чуть позже.", "The super tower will be needed a little later in the tutorial."));
            }
            if (!"storm_monolith".equals(tower.id)) {
                return ActionResult.fail(this.choose(world, "В обучении на золотой площадке сейчас доступен только Монолит.", "Only the Monolith is available on the golden pad during the tutorial."));
            }
            return null;
        }
        if (this.isTrapSlot(slot)) {
            return switch (tutorial.stage) {
                case PlaceUniqueTraps, PrepareTrapWave, WaitTrapWaveFinish -> {
                    List<BuildSlot> tutorialTrapSlots = this.tutorialRequiredTrapSlots(context);
                    int slotIndex = -1;
                    for (int i = 0; i < tutorialTrapSlots.size(); i++) {
                        BuildSlot candidate = tutorialTrapSlots.get(i);
                        if (candidate != null && candidate.id != null && candidate.id.equals(slot.id)) {
                            slotIndex = i;
                            break;
                        }
                    }
                    if (slotIndex < 0 || slotIndex >= 3) {
                        yield ActionResult.fail(this.choose(world, "Сейчас используй только 3 учебные ловушки на маршруте.", "Use only the 3 training trap pads on the route right now."));
                    }
                    if (context.placedTowers.containsKey(slot.id)) {
                        yield ActionResult.fail(this.choose(world, "На этой ловушечной площадке уже стоит ловушка.", "There is already a trap on this pad."));
                    }
                    if (this.hasTutorialTrapTower(context, tower.id)) {
                        yield ActionResult.fail(this.choose(world, "В обучении сейчас нужны 3 разные ловушки без повторов.", "The tutorial now needs 3 different traps with no duplicates."));
                    }
                    yield null;
                }
                default -> ActionResult.fail(this.choose(world, "Ловушки в обучении понадобятся чуть позже.", "Traps will be needed a little later in the tutorial."));
            };
        }
        return switch (tutorial.stage) {
            case PlaceFirstTower -> {
                if (!"guard_post".equals(tower.id)) {
                    yield ActionResult.fail(this.choose(world, "На первом шаге обучения доступна только базовая башня за 80 монет.", "Only the basic 80-gold tower is available on the first tutorial step."));
                }
                yield null;
            }
            case BuildSecondTower -> {
                if (context.placedTowers.containsKey(slot.id)) {
                    yield ActionResult.fail(this.choose(world, "Сейчас нужна новая башня на свободной площадке.", "Place a new tower on an empty pad now."));
                }
                if (tutorial.secondTowerSlotId != null && !tutorial.secondTowerSlotId.isBlank()) {
                    yield ActionResult.fail(this.choose(world, "Вторая башня уже поставлена. Теперь улучши её до 3 уровня.", "The second tower is already placed. Upgrade it to level 3 now."));
                }
                if ("guard_post".equals(tower.id)) {
                    yield ActionResult.fail(this.choose(world, "Сейчас нужна другая башня, не лучник.", "You need a different tower now, not the archer."));
                }
                yield null;
            }
            case FillAllSlots, WaitTwoLaneWaveFinish -> {
                if (context.placedTowers.containsKey(slot.id)) {
                    yield ActionResult.fail(this.choose(world, "Эта площадка уже занята. Теперь улучшай башни или выбирай пустой пад.", "That pad is already occupied. Upgrade towers or choose an empty pad."));
                }
                if (this.hasTutorialStandardTower(context, tower.id)) {
                    yield ActionResult.fail(this.choose(world, "В обучении сейчас нужно собрать по одной башне каждого типа без повторов.", "In the tutorial you now need one tower of each type, with no duplicates."));
                }
                yield null;
            }
            default -> ActionResult.fail(this.choose(world, "Сейчас Квибек ждёт другое действие.", "Kweebec is waiting for a different action right now."));
        };
    }

    private ActionResult validateTutorialUpgradeChoice(World world, MatchContext context, TowerInstance instance) {
        if (world == null || context == null || !context.tutorialActive || instance == null) {
            return null;
        }
        TutorialState tutorial = this.tutorialState(world);
        return switch (tutorial.stage) {
            case BuildSecondTower -> {
                if (tutorial.secondTowerSlotId == null || tutorial.secondTowerSlotId.isBlank() || !tutorial.secondTowerSlotId.equals(instance.slot.id)) {
                    yield ActionResult.fail(this.choose(world, "Сейчас нужно улучшать только вторую башню.", "Only the second tower can be upgraded right now."));
                }
                yield null;
            }
            case FillAllSlots, WaitTwoLaneWaveFinish -> null;
            default -> context.placedTowers.containsKey(instance.slot.id)
                ? ActionResult.fail(this.choose(world, "Сейчас Квибек ждёт другое действие.", "Kweebec is waiting for a different action right now."))
                : null;
        };
    }

    private boolean tutorialCanLaunchWave(World world, MatchContext context) {
        if (context == null || !context.tutorialActive) {
            return true;
        }
        TutorialState tutorial = this.tutorialState(world);
        this.reconcileTutorialProgression(context, tutorial);
        return switch (tutorial.stage) {
            case PrepareFirstWave -> true;
            case WaitSecondWaveFinish -> !tutorial.secondWavePrepared;
            case WaitTwoLaneWaveFinish -> !tutorial.twoLaneWavePrepared;
            case WaitBossWaveFinish -> !tutorial.bossWavePrepared;
            case PrepareTrapWave -> !tutorial.trapWavePrepared;
            default -> false;
        };
    }

    private String tutorialLaunchFailureMessage(World world, TutorialState tutorial) {
        if (tutorial == null) {
            return this.choose(world, "Сейчас нельзя запускать волну обучения.", "The tutorial wave cannot be started right now.");
        }
        return switch (tutorial.stage) {
            case BuildSecondTower -> this.choose(world, "Сначала поставь вторую башню и улучши её до 3 уровня.", "Place the second tower and upgrade it to level 3 first.");
            case CollectTutorialChest -> this.choose(world, "Сначала открой сундук Гайи слева от Квибека.", "Open Gaia's chest to Kweebec's left first.");
            case FillAllSlots -> this.choose(world, "Сначала вставь модуль, заполни все площадки и доведи башни до 5 уровня.", "Insert a module, fill every pad, and upgrade the towers to level 5 first.");
            case PlaceMonolith -> this.choose(world, "Сначала поставь Монолит на золотую площадку.", "Place the Monolith on the golden pad first.");
            case PlaceUniqueTraps -> this.choose(world, "Сначала поставь 3 разные ловушки на учебные площадки маршрута.", "Place 3 different traps on the training route pads first.");
            default -> this.choose(world, "Сейчас Квибек ждёт другое действие.", "Kweebec is waiting for a different action right now.");
        };
    }

    private ActionResult validateTutorialWaveLaunch(World world, MatchContext context) {
        if (context == null || !context.tutorialActive) {
            return null;
        }
        TutorialState tutorial = this.tutorialState(world);
        this.reconcileTutorialProgression(context, tutorial);
        if (this.tutorialCanLaunchWave(world, context)) {
            return null;
        }
        return ActionResult.fail(this.tutorialLaunchFailureMessage(world, tutorial));
    }

    private boolean reconcileTutorialProgression(MatchContext context, TutorialState tutorial) {
        if (context == null || tutorial == null || !context.tutorialActive || !tutorial.active) {
            return false;
        }
        int completedWaves = this.completedTutorialWaveCount(context);
        if (completedWaves >= 2 && this.shouldPromoteTutorialToRewardStep(tutorial.stage)) {
            tutorial.firstWavePrepared = true;
            tutorial.secondWavePrepared = true;
            if (!context.pendingRewardChoices.isEmpty()) {
                tutorial.stage = TutorialStage.ChooseRewardModule;
                return true;
            }
            if (tutorial.rewardChosen || tutorial.moduleEquipped || !context.moduleInventory.isEmpty()) {
                tutorial.rewardChosen = true;
                tutorial.stage = TutorialStage.ReturnAfterReward;
                return true;
            }
        }
        if (completedWaves >= 1 && this.shouldPromoteTutorialToPostFirstWave(tutorial.stage)) {
            tutorial.firstWavePrepared = true;
            tutorial.stage = TutorialStage.ReturnAfterFirstWave;
            return true;
        }
        return false;
    }

    private int completedTutorialWaveCount(MatchContext context) {
        if (context == null) {
            return 0;
        }
        int completedWaves = Math.max(0, context.runWavesCleared);
        if (context.activeWaveNumber > 0) {
            completedWaves = Math.max(completedWaves, context.activeWaveNumber - 1);
        } else if (context.state != null && context.state.gameState != GameState.InMatch) {
            completedWaves = Math.max(completedWaves, context.state.currentWave - 1);
        }
        return Math.max(0, completedWaves);
    }

    private boolean shouldPromoteTutorialToPostFirstWave(TutorialStage stage) {
        if (stage == null) {
            return false;
        }
        return switch (stage) {
            case ReturnAfterFirstTower, PrepareFirstWave, WaitFirstWaveFinish -> true;
            default -> false;
        };
    }

    private boolean shouldPromoteTutorialToRewardStep(TutorialStage stage) {
        if (stage == null) {
            return false;
        }
        return switch (stage) {
            case ReturnAfterFirstTower, PrepareFirstWave, WaitFirstWaveFinish, ReturnAfterFirstWave, BuildSecondTower,
                ReturnAfterSecondTowerUpgrade, CollectTutorialChest, ReturnAfterTutorialChest, WaitSecondWaveFinish -> true;
            default -> false;
        };
    }

    private void onTutorialWaveStarted(World world, MatchContext context) {
        if (world == null || context == null || !context.tutorialActive) {
            return;
        }
        TutorialState tutorial = this.tutorialState(world);
        switch (tutorial.stage) {
            case PrepareFirstWave -> {
                tutorial.stage = TutorialStage.WaitFirstWaveFinish;
                tutorial.firstWavePrepared = true;
            }
            case WaitSecondWaveFinish -> tutorial.secondWavePrepared = true;
            case WaitTwoLaneWaveFinish -> tutorial.twoLaneWavePrepared = true;
            case WaitBossWaveFinish -> tutorial.bossWavePrepared = true;
            case PrepareTrapWave -> {
                tutorial.stage = TutorialStage.WaitTrapWaveFinish;
                tutorial.trapWavePrepared = true;
                context.tutorialTrapWaveSpawnedCount = 0;
            }
            default -> {
            }
        }
    }

    private void onTutorialWaveCompleted(World world, MatchContext context, int completedWaveNumber) {
        if (world == null || context == null || !context.tutorialActive) {
            return;
        }
        TutorialState tutorial = this.tutorialState(world);
        boolean stageChanged = false;
        switch (completedWaveNumber) {
            case 1 -> {
                if (tutorial.stage == TutorialStage.WaitFirstWaveFinish) {
                    tutorial.stage = TutorialStage.ReturnAfterFirstWave;
                    stageChanged = true;
                }
            }
            case 2 -> {
                if (tutorial.stage == TutorialStage.WaitSecondWaveFinish) {
                    tutorial.stage = TutorialStage.ChooseRewardModule;
                    stageChanged = true;
                }
            }
            case 3 -> {
                if (tutorial.stage == TutorialStage.WaitTwoLaneWaveFinish) {
                    tutorial.stage = TutorialStage.ReturnAfterTwoLaneWave;
                    stageChanged = true;
                }
            }
            case 4 -> {
                if (tutorial.stage == TutorialStage.WaitBossWaveFinish) {
                    tutorial.stage = TutorialStage.ReturnAfterBossWave;
                    stageChanged = true;
                }
            }
            case TUTORIAL_TRAP_WAVE_NUMBER -> {
                if (tutorial.stage == TutorialStage.WaitTrapWaveFinish) {
                    tutorial.stage = TutorialStage.ReturnAfterTrapWave;
                    stageChanged = true;
                }
            }
            default -> {
            }
        }
        if (!stageChanged) {
            stageChanged = this.reconcileTutorialProgression(context, tutorial);
        }
        if (stageChanged) {
            this.playWorldUiSound(world, SOUND_TUTORIAL_STEP);
            this.refreshVisualizationIfEnabled(world);
        }
    }

    private String towerNameForId(MatchContext context, String towerId) {
        TowerDefinition tower = context == null || towerId == null ? null : context.towerById.get(towerId);
        return tower == null || tower.displayName == null || tower.displayName.isBlank() ? towerId : tower.displayName;
    }

    public boolean hasActiveMatch(World world) {
        MatchContext context = this.matchesByWorld.get(this.worldKey(world));
        return context != null && (
            context.state.gameState == GameState.InMatch
                || (BankDefenseMatchPhaseSupport.canStartPreparedWave(context.state)
                    && context.state.preparationRemainingSeconds > 0.0)
        );
    }

    public boolean hasCombatVisualState(World world) {
        MatchContext context = this.matchesByWorld.get(this.worldKey(world));
        return this.hasCombatVisualState(context);
    }

    private boolean hasCombatVisualState(MatchContext context) {
        if (context == null || context.tutorialActive) {
            return false;
        }
        return !context.placedTowers.isEmpty() || !this.hasNoLogicalEnemies(context);
    }

    public boolean isVisualizationEnabled(World world) {
        PresentationState presentation = this.presentationsByWorld.get(this.worldKey(world));
        return presentation != null && presentation.enabled;
    }

    public boolean isTowerRangePreviewEnabled(World world) {
        PresentationState presentation = this.presentationsByWorld.get(this.worldKey(world));
        return presentation != null && presentation.towerRangePreviewEnabled;
    }

    public ActionResult toggleTowerRangePreview(World world) throws IOException {
        PresentationState presentation = this.presentationsByWorld.computeIfAbsent(this.worldKey(world), ignored -> new PresentationState());
        presentation.towerRangePreviewEnabled = !presentation.towerRangePreviewEnabled;
        if (!presentation.towerRangePreviewEnabled) {
            this.clearTowerRangePreview(world, presentation);
        } else {
            MatchContext context = this.matchesByWorld.get(this.worldKey(world));
            this.syncTowerRangePreview(world, presentation, context);
        }
        return ActionResult.ok(presentation.towerRangePreviewEnabled
            ? this.choose(world, "Показ радиусов башен включён.", "Tower range preview enabled.")
            : this.choose(world, "Показ радиусов башен выключен.", "Tower range preview disabled."));
    }

    public MatchState resetMatch(World world) throws IOException {
        this.deactivateAcidStorm(world, this.matchesByWorld.get(this.worldKey(world)));
        MatchContext context = this.createFreshContext(world);
        this.matchesByWorld.put(this.worldKey(world), context);
        this.refreshVisualizationIfEnabled(world);
        return context.state.copy();
    }

    public ActionResult endMatchAsDefeat(World world) throws IOException {
        MatchContext context = this.getOrCreateContext(world);
        if (!context.state.gameStarted && context.state.gameState != GameState.InMatch) {
            return ActionResult.fail(this.choose(world, "Сейчас нет активного матча для завершения.", "There is no active match to end right now."));
        }
        if (context.state.gameState == GameState.Defeat) {
            return ActionResult.ok(this.choose(world, "Матч уже завершён поражением.", "The match has already ended in defeat."));
        }
        if (context.state.gameState == GameState.Victory) {
            return ActionResult.fail(this.choose(world, "Матч уже завершён победой. Подготовь новый забег у оператора.", "The match has already ended in victory. Prepare a new run with the operator."));
        }
        this.finishMatchAsDefeat(world, context, "Матч завершён вручную. Засчитано поражение.", "The match was ended manually and counted as a defeat.");
        return ActionResult.ok(this.choose(world, "Матч завершён. Засчитано поражение.", "The match has ended and counts as a defeat."));
    }

    public RuntimeStatus getRuntimeStatus(World world) throws IOException {
        BankDefenseRepository.Snapshot snapshot = this.repository.loadSnapshot(world);
        MatchContext context = this.matchesByWorld.get(this.worldKey(world));
        PresentationState presentation = this.presentationsByWorld.get(this.worldKey(world));
        Set<Long> pinnedSpawnChunks = this.pinnedSpawnChunkIndexesByWorld.get(this.worldKey(world));
        RuntimeStatus status = new RuntimeStatus();
        status.matchState = context == null ? null : context.state.copy();
        status.totalSlots = snapshot.buildSlots.slots.size();
        status.routePoints = BankDefenseRuntime.totalRoutePointCount(snapshot.map);
        status.pendingSpawns = context == null ? 0 : context.pendingSpawns.size();
        status.activeEnemies = context == null ? 0 : context.enemies.size();
        status.placedTowers = context == null ? 0 : context.placedTowers.size();
        status.totalWaves = snapshot.gameRules.totalWaves;
        status.endlessMode = snapshot.gameRules.endlessMode;
        status.preparationRemainingSeconds = context == null ? 0.0 : context.state.preparationRemainingSeconds;
        status.visualizationEnabled = presentation != null && presentation.enabled;
        status.renderedOverlayBlocks = presentation == null ? 0 : presentation.totalVisualEntityCount();
        status.enemyVisualViewers = presentation == null ? 0 : presentation.lastEnemyViewerCount;
        status.wantedEnemyVisuals = presentation == null ? 0 : presentation.lastWantedEnemyVisuals;
        status.enemyVisualSweepRemoved = presentation == null ? 0 : presentation.lastDuplicateEnemyVisualsRemoved;
        status.enemyVisualEpoch = presentation == null ? "" : presentation.lastEnemyVisualEpoch;
        status.pinnedSpawnChunks = pinnedSpawnChunks == null ? 0 : pinnedSpawnChunks.size();
        return status;
    }

    private String text(String value) {
        return BankDefenseTextSupport.clean(value);
    }

    private String tr(PlayerRef playerRef, String key, Object... args) {
        return BankDefenseLocalization.tr(playerRef, key, args);
    }

    private String tr(World world, String key, Object... args) {
        return this.tr(this.primaryPlayerRef(world), key, args);
    }

    private String choose(PlayerRef playerRef, String russian, String english) {
        return BankDefenseLocalization.choose(playerRef, russian, english);
    }

    private String choose(World world, String russian, String english) {
        return this.choose(this.primaryPlayerRef(world), russian, english);
    }

    private String towerDisplayName(PlayerRef playerRef, TowerDefinition tower) {
        if (tower == null) {
            return "";
        }
        return BankDefenseLocalization.towerDisplayName(playerRef, tower.id, this.text(tower.displayName));
    }

    private String towerDisplayName(World world, TowerDefinition tower) {
        return this.towerDisplayName(this.primaryPlayerRef(world), tower);
    }

    private String enemyDisplayName(PlayerRef playerRef, EnemyDefinition enemy) {
        if (enemy == null) {
            return "";
        }
        return BankDefenseLocalization.enemyDisplayName(playerRef, enemy.id, this.text(enemy.displayName));
    }

    private String enemyDisplayName(World world, EnemyDefinition enemy) {
        return this.enemyDisplayName(this.primaryPlayerRef(world), enemy);
    }

    private String moduleDisplayName(PlayerRef playerRef, ModuleDefinition module) {
        if (module == null) {
            return "";
        }
        return BankDefenseLocalization.moduleDisplayName(playerRef, module.id, this.text(module.displayName));
    }

    private String moduleDisplayName(World world, ModuleDefinition module) {
        return this.moduleDisplayName(this.primaryPlayerRef(world), module);
    }

    private String moduleNote(PlayerRef playerRef, ModuleDefinition module) {
        if (module == null) {
            return "";
        }
        return BankDefenseLocalization.moduleNote(playerRef, module.id, this.text(module.note));
    }

    private String moduleNote(World world, ModuleDefinition module) {
        return this.moduleNote(this.primaryPlayerRef(world), module);
    }

    private String progressionDisplayName(World world, ProgressionNodeDefinition node) {
        if (node == null) {
            return "";
        }
        return BankDefenseLocalization.progressionDisplayName(this.primaryPlayerRef(world), node.id, this.text(node.displayName));
    }

    private String contractDisplayName(World world, ContractDefinition contract) {
        if (contract == null) {
            return this.tr(world, "contract.none");
        }
        return BankDefenseLocalization.contractDisplayName(this.primaryPlayerRef(world), contract.id, this.text(contract.displayName));
    }

    private String contractNote(World world, ContractDefinition contract) {
        if (contract == null) {
            return "";
        }
        return BankDefenseLocalization.contractNote(this.primaryPlayerRef(world), contract.id, this.text(contract.note));
    }

    private String difficultyDisplayName(World world, DifficultyProfile profile) {
        if (profile == null) {
            return "";
        }
        return BankDefenseLocalization.difficultyDisplayName(this.primaryPlayerRef(world), profile.id, profile.displayName);
    }

    private String difficultyNote(World world, DifficultyProfile profile) {
        if (profile == null) {
            return "";
        }
        return BankDefenseLocalization.difficultyNote(this.primaryPlayerRef(world), profile.id, profile.note);
    }

    private String seedIdolModeName(World world, int mode) {
        return this.seedIdolModeName(this.primaryPlayerRef(world), mode);
    }

    private String seedIdolModeName(PlayerRef playerRef, int mode) {
        return BankDefenseLocalization.seedIdolModeName(playerRef, mode);
    }

    public SlotUiState getSlotUiState(World world, String slotId) throws IOException {
        return this.getSlotUiState(world, this.primaryPlayerRef(world), slotId);
    }

    public SlotUiState getSlotUiState(World world, PlayerRef viewerRef, String slotId) throws IOException {
        MatchContext context = this.getOrCreateContext(world);
        BuildSlot slot = this.findSlotById(context.snapshot.buildSlots, slotId);
        if (slot == null) {
            return null;
        }

        SlotUiState state = new SlotUiState();
        state.slotId = slot.id;
        state.slotLabel = slot.label == null || slot.label.isBlank() ? slot.id : slot.label;
        state.position = slot.position;
        state.currency = this.currencyForPlayer(context, viewerRef);
        state.bankHp = context.state.bankHp;
        state.currentWave = context.state.currentWave;
        state.totalWaves = context.snapshot.gameRules.totalWaves;
        state.endlessMode = context.snapshot.gameRules.endlessMode;
        state.waveState = context.state.waveState;
        state.gameStarted = context.state.gameStarted;
        state.preparationRemainingSeconds = context.state.preparationRemainingSeconds;
        state.matchMode = context.matchMode;
        state.slotType = slot.slotType == null || slot.slotType.isBlank() ? "standard" : slot.slotType;
        state.ownerTeam = this.slotOwnerTeam(slot);
        state.playerTeam = this.teamForPlayer(context, viewerRef);
        state.segment = slot.segment == null ? "" : slot.segment;
        state.superSlot = this.isSuperSlot(slot);
        state.trapSlot = this.isTrapSlot(slot);
        state.modulesAllowed = slot.modulesAllowed && !state.superSlot && !state.trapSlot;
        state.slotOwnedByViewer = this.canPlayerUseSlot(world, context, viewerRef, slot);
        state.pendingRewardSummary = context.pendingRewardChoices.isEmpty()
            ? ""
            : this.tr(viewerRef, "page.reward.hint");

        if (state.superSlot && !state.trapSlot) {
            for (String towerId : List.of("heart_of_roots", "storm_monolith", "seed_idol")) {
                TowerDefinition tower = context.towerById.get(towerId);
                if (tower == null) {
                    continue;
                }
                if (slot.allowedTowerIds != null && !slot.allowedTowerIds.isEmpty() && !slot.allowedTowerIds.contains(tower.id)) {
                    continue;
                }
                state.towerButtons.add(this.createTowerButtonState(context, tower, SEED_IDOL_MODE_DEFAULT, null, this.towerDisplayName(viewerRef, tower)));
            }
        } else {
            for (TowerDefinition tower : context.snapshot.towers.towers) {
                if (tower.superTower != state.superSlot) {
                    continue;
                }
                if (tower.trapTower != state.trapSlot) {
                    continue;
                }
                if (slot.allowedTowerIds != null && !slot.allowedTowerIds.isEmpty() && !slot.allowedTowerIds.contains(tower.id)) {
                    continue;
                }
                state.towerButtons.add(this.createTowerButtonState(context, tower, SEED_IDOL_MODE_DEFAULT, null, this.towerDisplayName(viewerRef, tower)));
            }
        }

        if (state.modulesAllowed) {
            for (ModuleDefinition module : context.snapshot.modules.modules) {
                ModuleButtonState button = new ModuleButtonState();
                button.moduleId = module.id;
                button.displayName = this.moduleDisplayName(viewerRef, module);
                button.note = this.moduleNote(viewerRef, module);
                button.count = context.moduleInventory.getOrDefault(module.id, 0);
                button.enabled = button.count > 0;
                state.moduleButtons.add(button);
            }
        }

        if (context.tutorialActive) {
            this.applyTutorialSlotUiState(world, context, viewerRef, slot, state);
        }
        this.applyDuoSlotAccessUiState(world, context, viewerRef, slot, state);

        TowerInstance instance = context.placedTowers.get(slot.id);
        if (instance == null) {
            state.towerPresent = false;
            state.slotSummary = state.superSlot
                ? this.tr(viewerRef, "page.slot.super_empty")
                : state.trapSlot
                    ? this.tr(viewerRef, "page.slot.trap_empty")
                    : this.tr(viewerRef, "page.slot.empty");
            state.upgradeLabel = state.superSlot
                ? this.tr(viewerRef, "page.slot.build_super")
                : state.trapSlot
                    ? this.tr(viewerRef, "page.slot.build_trap")
                    : this.tr(viewerRef, "page.slot.build_tower");
            state.sellLabel = this.tr(viewerRef, "page.slot.sell_unavailable");
            state.moduleSummary = state.modulesAllowed
                ? this.tr(viewerRef, "page.slot.module.empty")
                : this.tr(viewerRef, "page.slot.module.unavailable");
            state.removeModuleLabel = state.modulesAllowed
                ? this.tr(viewerRef, "page.slot.module.remove")
                : this.tr(viewerRef, "page.slot.module.unavailable");
            if (context.tutorialActive) {
                this.applyTutorialSlotUiState(world, context, viewerRef, slot, state);
            }
            this.applyDuoSlotAccessUiState(world, context, viewerRef, slot, state);
            return state;
        }

        state.towerPresent = true;
        state.towerId = instance.definition.id;
        state.towerName = this.towerDisplayName(viewerRef, instance.definition);
        state.towerLevel = instance.getCurrentLevel().level;
        state.towerMaxLevel = instance.definition.levels.size();
        state.unlockedTowerLevelCap = this.unlockedTowerLevelCap(context, instance.definition);
        state.slotSummary =
            instance.definition.superTower
                ? this.towerDisplayName(viewerRef, instance.definition)
                : this.towerDisplayName(viewerRef, instance.definition)
                    + this.choose(viewerRef, " Ур. ", " Lvl. ")
                    + state.towerLevel
                    + " / "
                    + state.towerMaxLevel;
        if ("seed_idol".equals(instance.definition.id)) {
            state.slotSummary += " [" + this.seedIdolModeName(viewerRef, instance.specialMode) + "]";
            if (instance.specialMode == SEED_IDOL_MODE_WARD && instance.idolStoredCurrency > 0) {
                state.slotSummary += this.choose(viewerRef, " | Накоплено: ", " | Stored: ") + instance.idolStoredCurrency;
            }
            if (instance.specialMode == SEED_IDOL_MODE_WARD && instance.idolSavingsWavesRemaining > 0) {
                state.slotSummary += this.choose(viewerRef, " | До выплаты: ", " | Payout in: ")
                    + instance.idolSavingsWavesRemaining
                    + this.choose(viewerRef, " волн", " waves");
            }
            state.towerButtons.clear();
            state.towerButtons.add(this.createTowerButtonState(context, instance.definition, SEED_IDOL_MODE_HARVEST, this.choose(viewerRef, "Премия", "Bounty"), this.tr(viewerRef, "page.super.mode.harvest")));
            state.towerButtons.add(this.createTowerButtonState(context, instance.definition, SEED_IDOL_MODE_WARD, this.choose(viewerRef, "Накопление", "Savings"), this.tr(viewerRef, "page.super.mode.ward")));
        }

        if (instance.definition.superTower) {
            state.towerDescription = this.superTowerDescription(viewerRef, instance.definition.id);
            state.superReady = instance.superReady;
            state.superActivationsTotal = this.maxSuperActivations(context, instance);
            state.superActivationsUsed = instance.superActivationsUsed;
            state.superActivationsRemaining = this.remainingSuperActivations(context, instance);
            state.slotSummary = state.towerDescription;
            state.sellRefund = (int)Math.floor(this.totalInvestedCost(context, instance) * this.sellRefundRate(context));
            state.sellLabel = this.isDuoMode(context)
                ? this.choose(viewerRef, "Продать за ", "Sell for ") + state.sellRefund
                : this.tr(viewerRef, "page.slot.sell_unavailable");
            if ("heart_of_roots".equals(instance.definition.id)) {
                if (instance.superReady) {
                    state.upgradeAvailable = context.state.gameState == GameState.InMatch && context.activeWaveNumber > 0;
                    state.upgradeLabel = state.upgradeAvailable
                        ? this.tr(viewerRef, "page.super.activate")
                        : this.choose(viewerRef, "Активируется только во время волны", "Only activates during a wave");
                } else if (state.superActivationsRemaining > 0) {
                    state.upgradeAvailable = context.state.waveState == WaveState.BuildPhase;
                    state.upgradeLabel = state.upgradeAvailable
                        ? this.tr(viewerRef, "page.super.reactivate") + " (" + state.superActivationsRemaining + " " + this.choose(viewerRef, "осталось", "left") + ")"
                        : this.choose(viewerRef, "Реактивация только в подготовке", "Reactivation is only available during preparation");
                } else {
                    state.upgradeLabel = this.choose(viewerRef, "Все активации исчерпаны", "All activations are spent");
                }
            } else if ("storm_monolith".equals(instance.definition.id)) {
                if (!instance.superReady && state.superActivationsRemaining > 0) {
                    state.upgradeAvailable = context.state.waveState == WaveState.BuildPhase;
                    state.upgradeLabel = state.upgradeAvailable
                        ? this.tr(viewerRef, "page.super.reactivate") + " (" + state.superActivationsRemaining + " " + this.choose(viewerRef, "осталось", "left") + ")"
                        : this.choose(viewerRef, "Реактивация только в подготовке", "Reactivation is only available during preparation");
                } else if (instance.superReady) {
                    state.upgradeLabel = this.choose(viewerRef, "Монолит активен", "Monolith is active");
                } else {
                    state.upgradeLabel = this.choose(viewerRef, "Все активации исчерпаны", "All activations are spent");
                }
            } else {
                state.upgradeLabel = this.choose(viewerRef, "Уровни недоступны", "Levels unavailable");
            }
        } else if ("heart_of_roots".equals(instance.definition.id)) {
            state.upgradeAvailable = context.state.gameState == GameState.InMatch;
            state.upgradeLabel = context.state.gameState == GameState.InMatch
                ? this.choose(viewerRef, "Активировать: +30% урона", "Activate: +30% damage")
                : this.choose(viewerRef, "Активируется только в бою", "Only activates in combat");
        } else if (state.trapSlot || instance.definition.trapTower) {
            state.upgradeLabel = this.choose(viewerRef, "Ловушки не улучшаются", "Traps do not upgrade");
        } else if (instance.levelIndex + 1 < instance.definition.levels.size() && instance.levelIndex + 1 < state.unlockedTowerLevelCap) {
            state.upgradeAvailable = true;
            state.upgradeCost = this.towerLevelCost(context, instance.definition, instance.levelIndex + 1);
            state.upgradeLabel = this.choose(viewerRef, "Улучшить за ", "Upgrade for ") + state.upgradeCost;
        } else if (state.unlockedTowerLevelCap < state.towerMaxLevel && !instance.definition.superTower) {
            state.upgradeLabel = this.choose(viewerRef, "Нужна мета-прокачка", "Requires progression upgrade");
        } else {
            state.upgradeLabel = this.choose(viewerRef, "Максимальный уровень", "Maximum level");
        }

        ModuleDefinition equippedModule = instance.equippedModuleId == null ? null : context.moduleById.get(instance.equippedModuleId);
        if (instance.definition.superTower) {
            if ("seed_idol".equals(instance.definition.id)) {
                state.moduleSummary = this.choose(viewerRef, "Режим: ", "Mode: ") + this.seedIdolModeName(viewerRef, instance.specialMode)
                    + (instance.specialMode == SEED_IDOL_MODE_WARD
                        ? this.choose(viewerRef, " | Накоплено: ", " | Stored: ") + instance.idolStoredCurrency + this.choose(viewerRef, " монет", " gold")
                            + (instance.idolSavingsWavesRemaining > 0
                                ? this.choose(viewerRef, " | До выплаты: ", " | Payout in: ") + instance.idolSavingsWavesRemaining + this.choose(viewerRef, " волн", " waves")
                                : "")
                        : "");
            } else {
                state.moduleSummary = this.choose(viewerRef, "Заряды: ", "Charges: ") + state.superActivationsRemaining + this.choose(viewerRef, " из ", " / ") + state.superActivationsTotal
                    + (instance.superReady
                        ? this.choose(viewerRef, " | Готово", " | Ready")
                        : this.choose(viewerRef, " | Нужна реактивация", " | Needs reactivation"));
            }
            state.removeModuleLabel = this.tr(viewerRef, "page.slot.module.unavailable");
        } else if (!state.modulesAllowed) {
            state.moduleSummary = this.tr(viewerRef, "page.slot.module.unavailable");
            state.removeModuleLabel = this.tr(viewerRef, "page.slot.module.unavailable");
        } else if (equippedModule == null) {
            state.moduleSummary = this.tr(viewerRef, "page.slot.module.empty");
            state.removeModuleLabel = this.tr(viewerRef, "page.slot.module.remove");
        } else {
            state.moduleSummary = this.choose(viewerRef, "Модуль: ", "Module: ") + this.moduleDisplayName(viewerRef, equippedModule);
            state.removeModuleLabel = this.tr(viewerRef, "page.slot.module.remove");
        }

        state.sellRefund = (int)Math.floor(this.totalInvestedCost(context, instance) * this.sellRefundRate(context));
        if (!instance.definition.superTower) {
            state.sellLabel = this.choose(viewerRef, "Продать за ", "Sell for ") + state.sellRefund;
        }
        if (context.tutorialActive) {
            this.applyTutorialSlotUiState(world, context, viewerRef, slot, state);
        }
        this.applyDuoSlotAccessUiState(world, context, viewerRef, slot, state);
        return state;
    }

    @SuppressWarnings("unused")
    private SlotUiState getSlotUiStateLegacy(World world, String slotId) throws IOException {
        return this.getSlotUiState(world, this.primaryPlayerRef(world), slotId);
    }

    private TowerButtonState createTowerButtonState(
        MatchContext context,
        TowerDefinition tower,
        int buildMode,
        String shortLabel,
        String displayName
    ) {
        TowerButtonState button = new TowerButtonState();
        button.towerId = tower.id;
        button.buildMode = buildMode;
        button.shortLabel = shortLabel;
        button.displayName = displayName;
        button.cost = this.towerLevelCost(context, tower, 0);
        button.unlocked = this.isTowerUnlocked(context, tower);
        button.enabled = button.unlocked;
        return button;
    }

    private void applyTutorialSlotUiState(World world, MatchContext context, PlayerRef viewerRef, BuildSlot slot, SlotUiState state) {
        if (world == null || context == null || !context.tutorialActive || slot == null || state == null) {
            return;
        }
        TutorialState tutorial = this.tutorialState(world);
        for (TowerButtonState button : state.towerButtons) {
            button.enabled = button.unlocked && this.tutorialTowerButtonEnabled(tutorial, context, slot, state, button);
        }
        for (ModuleButtonState button : state.moduleButtons) {
            button.enabled = button.count > 0 && this.tutorialModuleButtonEnabled(tutorial, state);
        }
        if (state.towerPresent && !state.superSlot && !state.trapSlot && !this.tutorialUpgradeButtonEnabled(tutorial, context, slot)) {
            state.upgradeAvailable = false;
            state.upgradeCost = 0;
            if (tutorial.stage == TutorialStage.BuildSecondTower) {
                BuildSlot secondSlot = this.tutorialStandardSlot(context, 1);
                if (secondSlot == null || slot.id == null || !slot.id.equals(secondSlot.id)) {
                    state.upgradeLabel = this.choose(viewerRef, "Сейчас нужно улучшать только вторую башню.", "Only the second tower can be upgraded right now.");
                }
            } else if (tutorial.stage != TutorialStage.FillAllSlots && tutorial.stage != TutorialStage.WaitTwoLaneWaveFinish) {
                state.upgradeLabel = this.choose(viewerRef, "Сейчас Квибек ждёт другое действие.", "Kweebec is waiting for a different action right now.");
            }
        }
        if (state.modulesAllowed && !this.tutorialModuleButtonEnabled(tutorial, state)) {
            state.removeModuleLabel = this.choose(viewerRef, "Сначала выполни текущую задачу.", "Finish the current objective first.");
        }
    }

    private void applyDuoSlotAccessUiState(World world, MatchContext context, PlayerRef viewerRef, BuildSlot slot, SlotUiState state) {
        if (world == null || context == null || slot == null || state == null || !this.isDuoMode(context)) {
            return;
        }
        if (state.slotOwnedByViewer) {
            return;
        }
        for (TowerButtonState button : state.towerButtons) {
            button.enabled = false;
        }
        for (ModuleButtonState button : state.moduleButtons) {
            button.enabled = false;
        }
        state.upgradeAvailable = false;
        state.upgradeCost = 0;
        state.modulesAllowed = false;
        state.upgradeLabel = this.deniedSlotAccessMessage(viewerRef, slot);
        state.sellLabel = this.deniedSlotAccessMessage(viewerRef, slot);
        state.moduleSummary = this.deniedSlotAccessMessage(viewerRef, slot);
        state.removeModuleLabel = this.deniedSlotAccessMessage(viewerRef, slot);
        if (!state.towerPresent) {
            state.slotSummary = this.deniedSlotAccessMessage(viewerRef, slot);
        }
    }

    private boolean tutorialTowerButtonEnabled(
        TutorialState tutorial,
        MatchContext context,
        BuildSlot slot,
        SlotUiState state,
        TowerButtonState button
    ) {
        if (tutorial == null || context == null || slot == null || state == null || button == null) {
            return false;
        }
        if (state.superSlot) {
            return (tutorial.stage == TutorialStage.PlaceMonolith || tutorial.stage == TutorialStage.WaitBossWaveFinish)
                && "storm_monolith".equals(button.towerId);
        }
        if (state.trapSlot) {
            return switch (tutorial.stage) {
                case PlaceUniqueTraps, PrepareTrapWave, WaitTrapWaveFinish -> this.tutorialRequiredTrapSlots(context).stream().anyMatch(candidate -> candidate != null && candidate.id != null && candidate.id.equals(slot.id))
                    && !state.towerPresent
                    && button.towerId != null
                    && !this.hasTutorialTrapTower(context, button.towerId);
                default -> false;
            };
        }
        return switch (tutorial.stage) {
            case PlaceFirstTower -> {
                yield "guard_post".equals(button.towerId);
            }
            case BuildSecondTower -> {
                if (state.towerPresent) {
                    yield false;
                }
                if (tutorial.secondTowerSlotId != null && !tutorial.secondTowerSlotId.isBlank()) {
                    yield false;
                }
                yield button.towerId != null && !"guard_post".equals(button.towerId);
            }
            case FillAllSlots, WaitTwoLaneWaveFinish ->
                button.towerId != null && !this.hasTutorialStandardTower(context, button.towerId);
            default -> false;
        };
    }

    private boolean tutorialUpgradeButtonEnabled(TutorialState tutorial, MatchContext context, BuildSlot slot) {
        if (tutorial == null || context == null || slot == null) {
            return false;
        }
        return switch (tutorial.stage) {
            case BuildSecondTower -> {
                yield tutorial.secondTowerSlotId != null && slot.id != null && slot.id.equals(tutorial.secondTowerSlotId);
            }
            case FillAllSlots, WaitTwoLaneWaveFinish -> true;
            default -> false;
        };
    }

    private boolean hasTutorialStandardTower(MatchContext context, String towerId) {
        if (context == null || towerId == null || towerId.isBlank()) {
            return false;
        }
        for (BuildSlot slot : this.tutorialStandardSlots(context)) {
            TowerInstance tower = context.placedTowers.get(slot.id);
            if (tower != null && towerId.equals(tower.definition.id)) {
                return true;
            }
        }
        return false;
    }

    private boolean hasTutorialTrapTower(MatchContext context, String towerId) {
        if (context == null || towerId == null || towerId.isBlank()) {
            return false;
        }
        for (BuildSlot slot : this.tutorialTrapSlots(context)) {
            TowerInstance tower = context.placedTowers.get(slot.id);
            if (tower != null && tower.definition != null && towerId.equals(tower.definition.id)) {
                return true;
            }
        }
        return false;
    }

    private boolean tutorialModuleButtonEnabled(TutorialState tutorial, SlotUiState state) {
        if (tutorial == null || state == null || !state.modulesAllowed || !state.towerPresent) {
            return false;
        }
        return tutorial.stage == TutorialStage.FillAllSlots || tutorial.stage == TutorialStage.WaitTwoLaneWaveFinish;
    }

    public InteractionTarget resolveInteractionTarget(World world, Ref<EntityStore> targetRef, Vec3i targetBlock) throws IOException {
        BankDefenseRepository.Snapshot snapshot = this.repository.loadSnapshot(world);
        MatchContext context = this.matchesByWorld.get(this.worldKey(world));
        PresentationState presentation = this.presentationsByWorld.get(this.worldKey(world));
        return this.resolveInteractionTarget(snapshot, context, presentation, targetRef, targetBlock);
    }

    private InteractionTarget resolveInteractionTarget(
        BankDefenseRepository.Snapshot snapshot,
        MatchContext context,
        PresentationState presentation,
        Ref<EntityStore> targetRef,
        Vec3i targetBlock
    ) {
        if (snapshot == null || snapshot.map == null) {
            return InteractionTarget.none();
        }
        if (targetRef != null && targetRef.isValid() && presentation != null) {
            String key = this.findPresentationKey(presentation, targetRef);
            if (KEY_CONTROL_OPERATOR.equals(key)) {
                Vec3i position = this.controlInteractionPoint(snapshot.map);
                return position == null ? InteractionTarget.none() : InteractionTarget.controlOperator(position);
            }
            if (KEY_VENDOR_OPERATOR.equals(key)) {
                Vec3i position = this.vendorInteractionPoint(snapshot.map);
                return position == null ? InteractionTarget.none() : InteractionTarget.vendorOperator(position);
            }
            if (KEY_MODE_SELECTOR_OPERATOR.equals(key)) {
                Vec3i position = this.modeSelectorInteractionPoint(snapshot.map);
                return position == null ? InteractionTarget.none() : InteractionTarget.modeSelectorOperator(position);
            }
            if (KEY_CONSOLE_START.equals(key)) {
                Vec3i position = this.startConsolePoint(snapshot.map);
                return position == null ? InteractionTarget.none() : InteractionTarget.startConsole(position);
            }
            if (KEY_CONSOLE_MENU.equals(key)) {
                Vec3i position = this.menuConsolePoint(snapshot.map);
                return position == null ? InteractionTarget.none() : InteractionTarget.menuConsole(position);
            }
            if (key != null && (key.startsWith(KEY_SLOT_PREFIX) || key.startsWith(KEY_TOWER_PREFIX))) {
                String slotId = key.substring(key.indexOf(':') + 1);
                BuildSlot slot = this.findSlotById(snapshot.buildSlots, slotId);
                if (slot != null && slot.position != null) {
                    return InteractionTarget.slot(slot.id, slot.position);
                }
            }
        }

        if (targetBlock == null) {
            return InteractionTarget.none();
        }

        Vec3i startConsole = this.startConsolePoint(snapshot.map);
        if (this.matchesInteractionPoint(startConsole, targetBlock, 2)) {
            return InteractionTarget.startConsole(startConsole);
        }
        Vec3i menuConsole = this.menuConsolePoint(snapshot.map);
        if (this.matchesInteractionPoint(menuConsole, targetBlock, 2)) {
            return InteractionTarget.menuConsole(menuConsole);
        }
        Vec3i modeSelectorOperator = this.modeSelectorInteractionPoint(snapshot.map);
        if (this.matchesInteractionPoint(modeSelectorOperator, targetBlock, 1)) {
            return InteractionTarget.modeSelectorOperator(modeSelectorOperator);
        }
        Vec3i controlOperator = this.controlInteractionPoint(snapshot.map);
        if (this.matchesInteractionPoint(controlOperator, targetBlock, 1)) {
            return InteractionTarget.controlOperator(controlOperator);
        }
        Vec3i vendorOperator = this.vendorInteractionPoint(snapshot.map);
        if (this.matchesInteractionPoint(vendorOperator, targetBlock, 1)) {
            return InteractionTarget.vendorOperator(vendorOperator);
        }

        if (context != null) {
            ActiveChest activeChest = this.findActiveChestAt(context.activeChests, targetBlock);
            if (activeChest != null) {
                return activeChest.type == ChestType.Core
                    ? InteractionTarget.coreChest(activeChest.position)
                    : InteractionTarget.moneyChest(activeChest.position);
            }
        }

        for (BuildSlot slot : snapshot.buildSlots.slots) {
            Vec3i interactionPoint = this.slotInteractionPoint(slot);
            if (interactionPoint != null && this.matchesInteractionPoint(interactionPoint, targetBlock, 0)) {
                return InteractionTarget.slot(slot.id, slot.position);
            }
            if (slot.position != null && this.matchesInteractionPoint(slot.position, targetBlock, 0)) {
                return InteractionTarget.slot(slot.id, slot.position);
            }
        }


        if (context != null) {
            for (TowerInstance tower : context.placedTowers.values()) {
                Vec3i interactionPoint = this.slotInteractionPoint(tower.slot);
                if (interactionPoint != null && this.matchesInteractionPoint(interactionPoint, targetBlock, 0)) {
                    return InteractionTarget.slot(tower.slot.id, tower.slot.position);
                }
                if (tower.slot.position != null && this.matchesInteractionPoint(tower.slot.position, targetBlock, 0)) {
                    return InteractionTarget.slot(tower.slot.id, tower.slot.position);
                }
            }
        }

        return InteractionTarget.none();
    }

    public ActionResult selectGameMode(World world, Ref<EntityStore> playerEntityRef, String modeId) throws IOException {
        if (modeId == null || modeId.isBlank()) {
            return ActionResult.fail(this.choose(world, "Режим не выбран.", "No mode selected."));
        }
        MatchContext existing = this.matchesByWorld.get(this.worldKey(world));
        if (existing != null && existing.state != null && existing.state.gameStarted && existing.state.gameState != GameState.Defeat && existing.state.gameState != GameState.Victory) {
            return ActionResult.fail(this.choose(world, "Сменить режим можно только вне активного матча.", "You can only change the mode outside an active match."));
        }
        return switch (modeId) {
            case MODE_SOLO -> this.teleportPlayerToSoloStart(world, playerEntityRef);
            case MODE_DUO -> this.teleportPlayersToDuoStart(world, playerEntityRef);
            default -> ActionResult.fail(this.choose(world, "Неизвестный режим: ", "Unknown mode: ") + modeId + ".");
        };
    }

    public ActionResult travelToMode(World world, Ref<EntityStore> playerEntityRef, String modeId) throws IOException {
        if (modeId == null || modeId.isBlank()) {
            return ActionResult.fail(this.choose(world, "Точка назначения не выбрана.", "No destination was selected."));
        }
        MatchContext existing = this.matchesByWorld.get(this.worldKey(world));
        if (existing != null && existing.state != null && existing.state.gameStarted && existing.state.gameState != GameState.Defeat && existing.state.gameState != GameState.Victory) {
            return ActionResult.fail(this.choose(world, "Телепорт недоступен во время активного матча.", "Travel is unavailable during an active match."));
        }
        return switch (this.normalizeModeId(modeId)) {
            case MODE_SOLO -> this.travelPlayerToSoloHub(world, playerEntityRef);
            case MODE_DUO -> this.travelPlayerToDuoHub(world, playerEntityRef);
            default -> ActionResult.fail(this.choose(world, "Неизвестная точка назначения.", "Unknown destination."));
        };
    }

    private ActionResult teleportPlayerToSoloStart(World world, Ref<EntityStore> playerEntityRef) throws IOException {
        if (world == null) {
            return ActionResult.fail("World is unavailable.");
        }
        BankDefenseRepository.Snapshot snapshot = this.repository.loadSnapshot(world);
        if (snapshot == null || snapshot.map == null || snapshot.map.playerStart == null) {
            return ActionResult.fail(this.choose(world, "Соло-старт не настроен. Нужен marker playerstart.", "Solo start is not configured. A playerstart marker is required."));
        }
        Ref<EntityStore> targetRef = this.resolveTeleportTargetRef(world, playerEntityRef);
        if (targetRef == null || !targetRef.isValid()) {
            return ActionResult.fail(this.choose(world, "Игрок недоступен для телепорта.", "The player is unavailable for teleport."));
        }
        String worldKey = this.worldKey(world);
        this.selectedModeIdsByWorld.put(worldKey, MODE_SOLO);
        this.persistSelectedMode(world, MODE_SOLO);
        this.duoTeamSelectionsByWorld.remove(worldKey);
        this.matchesByWorld.put(worldKey, this.createFreshContext(world));
        if (!this.queuePlayerTeleport(world, targetRef, this.playerStartWorldPosition(snapshot.map.playerStart), this.playerStartRotation(snapshot.map, null))) {
            return ActionResult.fail(this.choose(world, "Не удалось подготовить телепорт игрока.", "Failed to prepare player teleport."));
        }
        this.scheduleVisualizationRefreshAfterTeleport(world);
        return ActionResult.ok(this.choose(world, "Телепортация в соло-режим выполнена.", "Teleported to solo mode."));
    }

    private ActionResult teleportPlayersToDuoStart(World world, Ref<EntityStore> playerEntityRef) throws IOException {
        if (world == null) {
            return ActionResult.fail("World is unavailable.");
        }
        BankDefenseRepository.Snapshot snapshot = this.repository.loadSnapshot(world);
        MapConfig duoSetupMap = snapshot == null ? null : this.buildDuoLayoutMap(snapshot.map);
        String validationError = this.validateDuoSetup(duoSetupMap, snapshot == null ? null : snapshot.buildSlots);
        if (validationError != null) {
            return ActionResult.fail(validationError);
        }
        if (snapshot != null && snapshot.map != null && duoSetupMap != null) {
            this.persistDerivedDuoMapConfig(world, snapshot.map, duoSetupMap);
        }
        List<PlayerRef> duoPlayers = this.duoPlayers(world);
        if (duoPlayers.isEmpty()) {
            return ActionResult.fail(this.choose(world, "Для Duo режима нужен хотя бы один игрок в мире.", "Duo mode requires at least one player in the world."));
        }
        String worldKey = this.worldKey(world);
        if (!MODE_DUO.equalsIgnoreCase(this.getSelectedModeId(world))) {
            this.duoTeamSelectionsByWorld.remove(worldKey);
        }
        this.selectedModeIdsByWorld.put(worldKey, MODE_DUO);
        this.persistSelectedMode(world, MODE_DUO);
        MatchContext duoContext = this.createFreshContext(world);
        this.matchesByWorld.put(worldKey, duoContext);
        this.teleportPlayer(world, duoPlayers.get(0).getReference(), duoSetupMap.duoPlayerStartBlue, this.duoPlayerStartRotation(duoSetupMap, TEAM_BLUE));
        if (duoPlayers.size() > 1) {
            this.teleportPlayer(world, duoPlayers.get(1).getReference(), duoSetupMap.duoPlayerStartGreen, this.duoPlayerStartRotation(duoSetupMap, TEAM_GREEN));
            this.scheduleVisualizationRefreshAfterTeleport(world);
            return ActionResult.ok(this.choose(world, "Игроки телепортированы в Duo режим.", "Players were teleported into Duo mode."));
        }
        this.scheduleVisualizationRefreshAfterTeleport(world);
        return ActionResult.ok(this.choose(world, "Duo режим активирован для сборки карты. Один игрок телепортирован на синий старт.", "Duo mode was enabled for map authoring. One player was teleported to the blue start."));
    }

    private ActionResult travelPlayerToSoloHub(World world, Ref<EntityStore> playerEntityRef) throws IOException {
        if (world == null) {
            return ActionResult.fail("World is unavailable.");
        }
        BankDefenseRepository.Snapshot snapshot = this.repository.loadSnapshot(world);
        MapConfig map = snapshot == null ? null : snapshot.map;
        Vec3i targetPoint = map == null ? null : map.soloTeleportTarget != null ? map.soloTeleportTarget : map.playerStart;
        if (targetPoint == null) {
            return ActionResult.fail(this.choose(world, "Точка телепорта в SOLO не настроена.", "The SOLO teleport target is not configured."));
        }
        List<Ref<EntityStore>> playerRefs = this.collectPlayerEntityRefs(world);
        if (playerRefs.isEmpty()) {
            return ActionResult.fail(this.choose(world, "В мире нет игроков для телепорта.", "There are no players in the world to teleport."));
        }
        String worldKey = this.worldKey(world);
        this.selectedModeIdsByWorld.put(worldKey, MODE_SOLO);
        this.persistSelectedMode(world, MODE_SOLO);
        this.duoTeamSelectionsByWorld.remove(worldKey);
        this.matchesByWorld.put(worldKey, this.createFreshContext(world));
        Vector3d targetPosition = this.playerStartWorldPosition(targetPoint);
        Vector3f targetRotation = this.playerStartRotation(map, null);
        boolean queued = false;
        for (Ref<EntityStore> playerRef : playerRefs) {
            queued |= this.queuePlayerTeleport(world, playerRef, targetPosition, targetRotation);
        }
        if (!queued) {
            return ActionResult.fail(this.choose(world, "Не удалось подготовить телепорт в SOLO.", "Failed to prepare the SOLO teleport."));
        }
        this.scheduleVisualizationRefreshAfterTeleport(world);
        return ActionResult.ok(this.choose(world, "Телепорт в SOLO выполнен.", "Teleported to SOLO."));
    }

    private ActionResult travelPlayerToDuoHub(World world, Ref<EntityStore> playerEntityRef) throws IOException {
        if (world == null) {
            return ActionResult.fail("World is unavailable.");
        }
        BankDefenseRepository.Snapshot snapshot = this.repository.loadSnapshot(world);
        MapConfig duoMap = snapshot == null ? null : this.buildDuoLayoutMap(snapshot.map);
        List<Ref<EntityStore>> playerRefs = this.collectPlayerEntityRefs(world);
        if (playerRefs.isEmpty()) {
            return ActionResult.fail(this.choose(world, "В мире нет игроков для телепорта.", "There are no players in the world to teleport."));
        }
        Vec3i targetPoint = duoMap == null ? null : duoMap.duoTeleportTarget != null ? duoMap.duoTeleportTarget : duoMap.duoTeamPoint;
        if (targetPoint == null) {
            return ActionResult.fail(this.choose(world, "Точка телепорта в DUO не настроена.", "The DUO teleport target is not configured."));
        }
        if (snapshot != null && snapshot.map != null && duoMap != null) {
            this.persistDerivedDuoMapConfig(world, snapshot.map, duoMap);
        }
        String worldKey = this.worldKey(world);
        if (!MODE_DUO.equalsIgnoreCase(this.getSelectedModeId(world))) {
            this.duoTeamSelectionsByWorld.remove(worldKey);
        }
        this.selectedModeIdsByWorld.put(worldKey, MODE_DUO);
        this.persistSelectedMode(world, MODE_DUO);
        this.matchesByWorld.put(worldKey, this.createFreshContext(world));
        Vector3d targetPosition = this.playerStartWorldPosition(targetPoint);
        Vector3f targetRotation = this.duoPlayerStartRotation(duoMap, TEAM_BLUE);
        boolean queued = false;
        for (Ref<EntityStore> playerRef : playerRefs) {
            queued |= this.queuePlayerTeleport(world, playerRef, targetPosition, targetRotation);
        }
        if (!queued) {
            return ActionResult.fail(this.choose(world, "Не удалось подготовить телепорт в DUO.", "Failed to prepare the DUO teleport."));
        }
        this.scheduleVisualizationRefreshAfterTeleport(world);
        return ActionResult.ok(this.choose(world, "Телепорт в DUO выполнен.", "Teleported to DUO."));
    }

    private void persistSelectedMode(World world, String modeId) throws IOException {
        if (world == null) {
            return;
        }
        MapConfig mapConfig = this.repository.loadMapConfig(world);
        if (mapConfig == null) {
            return;
        }
        String normalizedModeId = this.normalizeModeId(modeId);
        if (normalizedModeId.equalsIgnoreCase(mapConfig.selectedModeId)) {
            return;
        }
        mapConfig.selectedModeId = normalizedModeId;
        this.repository.saveMapConfig(world, mapConfig);
    }

    private void forceSoloModeForTutorial(World world) throws IOException {
        if (world == null) {
            return;
        }
        String worldKey = this.worldKey(world);
        this.selectedModeIdsByWorld.put(worldKey, MODE_SOLO);
        this.persistSelectedMode(world, MODE_SOLO);
        this.duoTeamSelectionsByWorld.remove(worldKey);
    }

    private void persistDerivedDuoMapConfig(World world, MapConfig rawMap, MapConfig duoMap) throws IOException {
        if (world == null || rawMap == null || duoMap == null) {
            return;
        }
        boolean changed = false;
        if (rawMap.duoPlayerStartBlue == null && duoMap.duoPlayerStartBlue != null) {
            rawMap.duoPlayerStartBlue = duoMap.duoPlayerStartBlue;
            rawMap.duoPlayerStartBlueYaw = duoMap.duoPlayerStartBlueYaw;
            changed = true;
        }
        if (rawMap.duoPlayerStartGreen == null && duoMap.duoPlayerStartGreen != null) {
            rawMap.duoPlayerStartGreen = duoMap.duoPlayerStartGreen;
            rawMap.duoPlayerStartGreenYaw = duoMap.duoPlayerStartGreenYaw;
            changed = true;
        }
        if (rawMap.duoSpawnPointBlue == null && duoMap.duoSpawnPointBlue != null) {
            rawMap.duoSpawnPointBlue = duoMap.duoSpawnPointBlue;
            changed = true;
        }
        if (rawMap.duoSpawnPointGreen == null && duoMap.duoSpawnPointGreen != null) {
            rawMap.duoSpawnPointGreen = duoMap.duoSpawnPointGreen;
            changed = true;
        }
        if (rawMap.duoBankCenter == null && duoMap.duoBankCenter != null) {
            rawMap.duoBankCenter = duoMap.duoBankCenter;
            changed = true;
        }
        if (rawMap.duoVaultPoint == null && duoMap.duoVaultPoint != null) {
            rawMap.duoVaultPoint = duoMap.duoVaultPoint;
            changed = true;
        }
        if (rawMap.duoPoint == null && duoMap.duoPoint != null) {
            rawMap.previousDuoPoint = rawMap.duoPoint;
            rawMap.duoPoint = duoMap.duoPoint;
            rawMap.duoYaw = duoMap.duoYaw;
            changed = true;
        }
        if (rawMap.duoQubeCorePoint == null && duoMap.duoQubeCorePoint != null) {
            rawMap.previousDuoQubeCorePoint = rawMap.duoQubeCorePoint;
            rawMap.duoQubeCorePoint = duoMap.duoQubeCorePoint;
            rawMap.duoQubeCoreYaw = duoMap.duoQubeCoreYaw;
            changed = true;
        }
        if (rawMap.duoTeamPoint == null && duoMap.duoTeamPoint != null) {
            rawMap.previousDuoTeamPoint = rawMap.duoTeamPoint;
            rawMap.duoTeamPoint = duoMap.duoTeamPoint;
            rawMap.duoTeamYaw = duoMap.duoTeamYaw;
            changed = true;
        }
        if (rawMap.duoTeleportPoint == null && duoMap.duoTeleportPoint != null) {
            rawMap.previousDuoTeleportPoint = rawMap.duoTeleportPoint;
            rawMap.duoTeleportPoint = duoMap.duoTeleportPoint;
            rawMap.duoTeleportYaw = duoMap.duoTeleportYaw;
            changed = true;
        }
        if (rawMap.duoTeleportTarget == null && duoMap.duoTeleportTarget != null) {
            rawMap.duoTeleportTarget = duoMap.duoTeleportTarget;
            changed = true;
        }
        if (rawMap.soloTeleportTarget == null && duoMap.soloTeleportTarget != null) {
            rawMap.soloTeleportTarget = duoMap.soloTeleportTarget;
            changed = true;
        }
        if (rawMap.duoStatisticsPoint == null && duoMap.duoStatisticsPoint != null) {
            rawMap.previousDuoStatisticsPoint = rawMap.duoStatisticsPoint;
            rawMap.duoStatisticsPoint = duoMap.duoStatisticsPoint;
            rawMap.duoStatisticsYaw = duoMap.duoStatisticsYaw;
            changed = true;
        }
        if (changed) {
            this.repository.saveMapConfig(world, rawMap);
        }
    }

    private String validateDuoSetup(BankDefenseRepository.Snapshot snapshot) {
        return this.validateDuoSetup(snapshot == null ? null : this.buildDuoLayoutMap(snapshot.map), snapshot == null ? null : snapshot.buildSlots);
    }

    private String validateDuoSetup(MapConfig map, BuildSlotsConfig buildSlots) {
        if (map == null) {
            return this.choose((PlayerRef)null, "Duo карта недоступна.", "Duo map is unavailable.");
        }
        if (map.duoPlayerStartBlue == null || map.duoPlayerStartGreen == null) {
            return this.choose((PlayerRef)null, "Для Duo нужны duoPlayerStartBlue и duoPlayerStartGreen.", "Duo requires duoPlayerStartBlue and duoPlayerStartGreen.");
        }
        if (map.duoSpawnPointBlue == null || map.duoSpawnPointGreen == null) {
            return this.choose((PlayerRef)null, "Для Duo нужны duoSpawnPointBlue и duoSpawnPointGreen.", "Duo requires duoSpawnPointBlue and duoSpawnPointGreen.");
        }
        if (map.duoBankCenter == null || map.duoVaultPoint == null) {
            return this.choose((PlayerRef)null, "Для Duo нужны duoBankCenter и duoVaultPoint.", "Duo requires duoBankCenter and duoVaultPoint.");
        }
        if (map.duoRouteBlue == null || map.duoRouteBlue.isEmpty() || map.duoRouteGreen == null || map.duoRouteGreen.isEmpty()) {
            return this.choose((PlayerRef)null, "Для Duo нужно настроить duoRouteBlue и duoRouteGreen.", "Duo requires duoRouteBlue and duoRouteGreen.");
        }
        int duoSlots = 0;
        if (buildSlots != null && buildSlots.slots != null) {
            for (BuildSlot slot : buildSlots.slots) {
                if (slot != null && this.slotMatchesMode(slot, MatchMode.Duo)) {
                    duoSlots++;
                }
            }
        }
        if (duoSlots <= 0) {
            return this.choose((PlayerRef)null, "Для Duo не найдено ни одного duo-слота в build_slots.json.", "No duo slots were found in build_slots.json.");
        }
        return null;
    }

    private void teleportPlayer(World world, Ref<EntityStore> playerEntityRef, Vec3i point, Vector3f fallbackRotation) {
        if (world == null || point == null) {
            return;
        }
        Ref<EntityStore> targetRef = this.resolveTeleportTargetRef(world, playerEntityRef);
        if (targetRef == null || !targetRef.isValid()) {
            return;
        }
        this.queuePlayerTeleport(world, targetRef, this.playerStartWorldPosition(point), fallbackRotation);
    }

    private void teleportAllPlayers(World world, Vec3i point, Vector3f fallbackRotation) {
        if (world == null || point == null) {
            return;
        }
        if (!world.isInThread()) {
            Vector3f rotation = fallbackRotation == null ? null : new Vector3f(fallbackRotation);
            world.execute(() -> this.teleportAllPlayers(world, point, rotation));
            return;
        }
        Vector3d position = this.playerStartWorldPosition(point);
        Vector3f rotation = fallbackRotation == null ? null : new Vector3f(fallbackRotation);
        for (Ref<EntityStore> ref : this.collectPlayerEntityRefs(world)) {
            this.queuePlayerTeleport(world, ref, position, rotation);
        }
    }

    private void playTutorialTeleportSequence(World world, Vec3i destinationPoint) {
        if (world == null || destinationPoint == null) {
            return;
        }
        if (!world.isInThread()) {
            Vec3i pointCopy = new Vec3i(destinationPoint.x, destinationPoint.y, destinationPoint.z);
            world.execute(() -> this.playTutorialTeleportSequence(world, pointCopy));
            return;
        }
        this.spawnTutorialTeleportFxAtPlayers(world);
        Vec3i pointCopy = new Vec3i(destinationPoint.x, destinationPoint.y, destinationPoint.z);
        HytaleServer.SCHEDULED_EXECUTOR.schedule(() -> {
            try {
                world.execute(() -> this.spawnTutorialTeleportFxAtPoint(world, pointCopy));
            } catch (Throwable ignored) {
            }
        }, (long)TUTORIAL_TELEPORT_EFFECT_DELAY_MS, TimeUnit.MILLISECONDS);
    }

    private void spawnTutorialTeleportFxAtPlayers(World world) {
        if (world == null || !world.isInThread()) {
            return;
        }
        Store<EntityStore> store = world.getEntityStore().getStore();
        List<Ref<EntityStore>> playerRefs = this.collectPlayerEntityRefs(world);
        for (Ref<EntityStore> ref : playerRefs) {
            TransformComponent transform = store.getComponent(ref, TransformComponent.getComponentType());
            if (transform == null || transform.getPosition() == null) {
                continue;
            }
            Vector3d position = transform.getPosition();
            this.spawnTutorialTeleportFx(world, playerRefs, new Vector3d(position.x, position.y + 0.95, position.z));
        }
    }

    private void spawnTutorialTeleportFxAtPoint(World world, Vec3i point) {
        if (world == null || point == null || !world.isInThread()) {
            return;
        }
        List<Ref<EntityStore>> playerRefs = this.collectPlayerEntityRefs(world);
        this.spawnTutorialTeleportFx(world, playerRefs, new Vector3d(point.x + 0.5, point.y + 1.05, point.z + 0.5));
    }

    private void spawnTutorialTeleportFx(World world, List<Ref<EntityStore>> playerRefs, Vector3d center) {
        if (world == null || center == null) {
            return;
        }
        Store<EntityStore> store = world.getEntityStore().getStore();
        Vector3f rotation = new Vector3f(0.0f, 0.0f, 0.0f);
        Color primary = this.rgb(174, 92, 255);
        Color secondary = this.rgb(128, 232, 255);
        this.spawnColoredParticle(store, playerRefs, PARTICLE_IMPACT, center, rotation, 0.95f, primary);
        this.spawnColoredParticle(store, playerRefs, PARTICLE_SHOT, center, rotation, 1.10f, secondary);
        this.spawnParticleBurst(store, playerRefs, center, 0.44, 10, 0.08);
        this.playSound3d(world, SOUND_TUTORIAL_WARP, center);
    }

    private void persistTutorialCompletionState(World world, MatchContext context, boolean tutorialCompleted) throws IOException {
        if (context == null || context.progression == null || context.progression.playerUuid == null || context.progression.playerUuid.isBlank()) {
            return;
        }
        BankDefenseRepository.Snapshot snapshot = context.snapshot != null ? context.snapshot : this.repository.loadSnapshot(world);
        PlayerProgressionState persisted = this.repository.loadPlayerProgression(context.progression.playerUuid);
        persisted = this.migrateProgressionState(snapshot, persisted);
        persisted.tutorialCompleted = tutorialCompleted;
        this.repository.savePlayerProgression(persisted);
        context.progression.cores = persisted.cores;
        context.progression.activeContractId = persisted.activeContractId;
        context.progression.lifetimeHighestWave = persisted.lifetimeHighestWave;
        context.progression.totalRuns = persisted.totalRuns;
        context.progression.totalVictories = persisted.totalVictories;
        context.progression.tutorialCompleted = persisted.tutorialCompleted;
        context.progression.unlockedNodeIds.clear();
        context.progression.unlockedNodeIds.addAll(persisted.unlockedNodeIds);
    }

    private boolean queuePlayerTeleport(World world, Ref<EntityStore> ref, Vector3d position, Vector3f rotation) {
        if (world == null || ref == null || !ref.isValid() || position == null) {
            return false;
        }
        if (!world.isInThread()) {
            Vector3d targetPosition = new Vector3d(position);
            Vector3f targetRotation = rotation == null ? null : new Vector3f(rotation);
            world.execute(() -> this.queuePlayerTeleport(world, ref, targetPosition, targetRotation));
            return true;
        }
        Store<EntityStore> store = world.getEntityStore().getStore();
        TransformComponent transform = store.getComponent(ref, TransformComponent.getComponentType());
        if (transform == null) {
            return false;
        }
        Vector3f headRotation = rotation == null ? new Vector3f(transform.getRotation()) : new Vector3f(rotation);
        HeadRotation existingHeadRotation = store.getComponent(ref, HeadRotation.getComponentType());
        if (rotation == null && existingHeadRotation != null) {
            headRotation.assign(existingHeadRotation.getRotation());
        }
        Teleport teleport = store.getComponent(ref, Teleport.getComponentType());
        if (teleport == null) {
            store.addComponent(ref, Teleport.getComponentType(), Teleport.createForPlayer(position, headRotation));
            return true;
        }
        teleport.setPosition(position);
        teleport.setRotation(new Vector3f(0.0f, headRotation.getYaw(), 0.0f));
        teleport.setHeadRotation(headRotation);
        return true;
    }

    private void scheduleVisualizationRefreshAfterTeleport(World world) {
        if (world == null) {
            return;
        }
        this.runTeleportVisualizationRefresh(world);
        this.scheduleTeleportVisualizationRefresh(world, 150L);
        this.scheduleTeleportVisualizationRefresh(world, 500L);
    }

    private void scheduleTeleportVisualizationRefresh(World world, long delayMs) {
        if (world == null) {
            return;
        }
        HytaleServer.SCHEDULED_EXECUTOR.schedule(() -> this.runTeleportVisualizationRefresh(world), delayMs, TimeUnit.MILLISECONDS);
    }

    private void scheduleVisualizationRefreshIfEnabled(World world, long delayMs) {
        if (world == null) {
            return;
        }
        HytaleServer.SCHEDULED_EXECUTOR.schedule(() -> {
            Runnable task = () -> this.refreshVisualizationIfEnabled(world);
            if (world.isInThread()) {
                task.run();
                return;
            }
            world.execute(task);
        }, Math.max(0L, delayMs), TimeUnit.MILLISECONDS);
    }

    private void runTeleportVisualizationRefresh(World world) {
        if (world == null) {
            return;
        }
        Runnable task = () -> {
            try {
                this.resetPresentationState(world);
                this.refreshVisualization(world);
            } catch (IOException ignored) {
            }
        };
        if (world.isInThread()) {
            task.run();
            return;
        }
        world.execute(task);
    }

    private Ref<EntityStore> resolveTeleportTargetRef(World world, Ref<EntityStore> preferredRef) {
        if (preferredRef != null && preferredRef.isValid()) {
            Store<EntityStore> store = world.getEntityStore().getStore();
            if (store.getComponent(preferredRef, TransformComponent.getComponentType()) != null) {
                return preferredRef;
            }
        }
        for (Ref<EntityStore> ref : this.collectPlayerEntityRefs(world)) {
            if (ref == null || !ref.isValid()) {
                continue;
            }
            Store<EntityStore> store = world.getEntityStore().getStore();
            if (store.getComponent(ref, TransformComponent.getComponentType()) != null) {
                return ref;
            }
        }
        return null;
    }

    private Vector3d playerStartWorldPosition(Vec3i point) {
        if (point == null) {
            return new Vector3d();
        }
        return new Vector3d(point.x + 0.5, point.y + 0.05, point.z + 0.5);
    }

    private Vector3f playerStartRotation(MapConfig map, Vector3f fallbackRotation) {
        Vec3i start = map == null ? null : map.playerStart;
        if (start == null) {
            return fallbackRotation == null ? new Vector3f() : new Vector3f(fallbackRotation);
        }
        if (map != null && map.playerStartYaw != null) {
            return this.playerHeadRotationFromYaw(map.playerStartYaw.floatValue());
        }
        Vec3i lookTarget = map.bankCenter != null
            ? map.bankCenter
            : map.vaultPoint != null
                ? map.vaultPoint
                : map.spawnPoint;
        if (lookTarget == null) {
            return fallbackRotation == null ? new Vector3f() : new Vector3f(fallbackRotation);
        }
        return this.rotationTowards(
            this.playerStartWorldPosition(start),
            this.objectiveWorldPosition(lookTarget, 0.02)
        );
    }

    private Vector3f duoPlayerStartRotation(MapConfig map, String teamId) {
        if (map == null) {
            return new Vector3f();
        }
        String normalizedTeam = this.normalizeTeam(teamId);
        Vec3i start = TEAM_GREEN.equals(normalizedTeam) ? map.duoPlayerStartGreen : map.duoPlayerStartBlue;
        Float yaw = TEAM_GREEN.equals(normalizedTeam) ? map.duoPlayerStartGreenYaw : map.duoPlayerStartBlueYaw;
        if (start == null) {
            return this.playerStartRotation(map, null);
        }
        if (yaw != null) {
            return this.playerHeadRotationFromYaw(yaw.floatValue());
        }
        Vec3i lookTarget = map.duoBankCenter != null ? map.duoBankCenter : map.duoVaultPoint;
        if (lookTarget == null) {
            return this.playerStartRotation(map, null);
        }
        return this.rotationTowards(
            this.playerStartWorldPosition(start),
            this.objectiveWorldPosition(lookTarget, 0.02)
        );
    }

    public ActionResult startNextWave(World world) throws IOException {
        MatchContext context = this.getOrCreateContext(world);
        if (!context.state.gameStarted) {
            return this.startGame(world, context);
        }
        return this.startPreparedWave(world, context, true);
    }

    public ActionResult startGame(World world) throws IOException {
        MatchContext context = this.getOrCreateContext(world);
        return this.startGame(world, context);
    }

    public ActionResult startPreparedWave(World world) throws IOException {
        MatchContext context = this.getOrCreateContext(world);
        return this.startPreparedWave(world, context, true);
    }

    public ActionResult toggleInstantAutoStart(World world) throws IOException {
        MatchContext context = this.getOrCreateContext(world);
        context.state.instantAutoStart = !context.state.instantAutoStart;
        String autoStartToastText = this.choose(world, "Автостарт включён. Бонус +15% начнёт работать через одну волну.", "Auto-start enabled. The +15% bonus will start working after one wave.");
        if (context.state.instantAutoStart) {
            this.armInstantAutoStartWarmup(context);
        } else {
            this.clearInstantAutoStartWarmup(context);
        }
        this.refreshVisualizationIfEnabled(world);
        if (context.state.instantAutoStart
            && BankDefenseMatchPhaseSupport.canStartPreparedWave(context.state)
            && context.pendingRewardChoices.isEmpty()) {
            ActionResult result = this.startPreparedWave(world, context, false);
            this.scheduleEventToast(context, autoStartToastText, EVENT_TOAST_ICON_ALERT, 1.35);
            return ActionResult.ok(this.choose(world, "Автостарт без ожидания: ВКЛ. ", "Instant auto-start: ON. ") + result.message);
        }
        if (context.state.instantAutoStart) {
            this.scheduleEventToast(context, autoStartToastText, EVENT_TOAST_ICON_ALERT, 0.45);
        }
        return ActionResult.ok(this.choose(world, "Автостарт без ожидания: ", "Instant auto-start: ")
            + (context.state.instantAutoStart ? this.choose(world, "ВКЛ", "ON") : this.choose(world, "ВЫКЛ", "OFF")) + ".");
    }

    public boolean isMatchPaused(World world) {
        MatchContext context = world == null ? null : this.matchesByWorld.get(this.worldKey(world));
        return context != null && context.state != null && context.state.paused;
    }

    public ActionResult toggleMatchPause(World world) throws IOException {
        return this.toggleMatchPause(world, this.primaryPlayerRef(world));
    }

    public ActionResult toggleMatchPause(World world, PlayerRef actor) throws IOException {
        MatchContext context = this.getOrCreateContext(world);
        if (!context.state.gameStarted) {
            return ActionResult.fail(this.choose(actor, "Сначала подготовьте и начните матч.", "Prepare and start a match first."));
        }
        if (context.tutorialActive) {
            return ActionResult.fail(this.choose(actor, "В обучении пауза у оператора недоступна.", "Pause from the operator is unavailable in the tutorial."));
        }
        if (context.state.gameState == GameState.Defeat || context.state.gameState == GameState.Victory) {
            return ActionResult.fail(this.choose(actor, "После завершения матча пауза недоступна.", "Pause is unavailable after the match has ended."));
        }
        context.state.paused = !context.state.paused;
        this.refreshVisualizationIfEnabled(world);
        this.playWorldUiSound(world, context.state.paused ? SOUND_NOTIFICATION : SOUND_UI_CLICK);
        this.showEventToast(
            context,
            this.choose(world, context.state.paused ? "Матч поставлен на паузу." : "Пауза снята.", context.state.paused ? "Match paused." : "Match resumed."),
            EVENT_TOAST_ICON_ALERT
        );
        return ActionResult.ok(this.choose(actor, context.state.paused ? "Матч поставлен на паузу." : "Пауза снята.", context.state.paused ? "Match paused." : "Match resumed."));
    }

    public ActionResult reopenPendingRewardPage(World world, PlayerRef actor) throws IOException {
        if (world == null) {
            return ActionResult.fail("World is unavailable.");
        }
        MatchContext context = this.getOrCreateContext(world);
        RewardUiState rewardUiState = this.getRewardUiState(world, actor);
        ActionResult validation = this.validatePendingRewardPageOpen(context, rewardUiState, actor);
        if (!validation.success) {
            return validation;
        }
        Ref<EntityStore> actorRef = actor == null ? null : actor.getReference();
        if (actorRef == null || !actorRef.isValid()) {
            return ActionResult.fail(this.choose(actor, "Игрок недоступен для открытия награды.", "The player is unavailable to open the reward."));
        }
        Store<EntityStore> store = world.getEntityStore().getStore();
        Player player = store.getComponent(actorRef, Player.getComponentType());
        if (player == null) {
            return ActionResult.fail(this.choose(actor, "Игрок недоступен для открытия награды.", "The player is unavailable to open the reward."));
        }
        context.state.rewardPending = !context.pendingRewardChoices.isEmpty();
        if (actor != null && actor.getUuid() != null) {
            context.pendingRewardPageOpenDelayByPlayer.remove(actor.getUuid());
        }
        this.playUiSoundForPlayer(world, actor, SOUND_NOTIFICATION);
        player.getPageManager().openCustomPage(actorRef, store, new BankDefenseRewardPage(actor, this));
        this.showEventToast(
            context,
            this.choose(actor, "Выберите модуль у оператора.", "Choose the module at the operator."),
            EVENT_TOAST_ICON_ALERT
        );
        return ActionResult.ok(this.choose(actor, "Окно выбора модуля открыто.", "The module selection is open."));
    }

    public ActionResult schedulePendingRewardPageOpen(World world, PlayerRef actor, double delaySeconds) throws IOException {
        if (world == null) {
            return ActionResult.fail("World is unavailable.");
        }
        MatchContext context = this.getOrCreateContext(world);
        RewardUiState rewardUiState = this.getRewardUiState(world, actor);
        ActionResult validation = this.validatePendingRewardPageOpen(context, rewardUiState, actor);
        if (!validation.success) {
            return validation;
        }
        if (actor == null || actor.getUuid() == null) {
            return ActionResult.fail(this.choose(actor, "Игрок недоступен для открытия награды.", "The player is unavailable to open the reward."));
        }
        context.pendingRewardPageOpenDelayByPlayer.put(actor.getUuid(), Math.max(0.05, delaySeconds));
        return ActionResult.ok(this.choose(actor, "Открываю выбор модуля.", "Opening the module selection."));
    }

    private ActionResult validatePendingRewardPageOpen(MatchContext context, RewardUiState rewardUiState, PlayerRef actor) {
        if (context == null || rewardUiState == null || context.pendingRewardChoices.isEmpty() || !rewardUiState.rewardPending) {
            return ActionResult.fail(this.choose(actor, "Сейчас нет активной награды модулем.", "There is no active module reward right now."));
        }
        if (this.isDuoMode(context)
            && !this.isDuoSoloTestEnabled(context)
            && rewardUiState.activePickerTeam != null
            && !rewardUiState.activePickerTeam.isBlank()
            && !TEAM_SHARED.equals(this.normalizeTeam(rewardUiState.activePickerTeam))
            && !this.normalizeTeam(rewardUiState.viewerTeam).equals(this.normalizeTeam(rewardUiState.activePickerTeam))) {
            return ActionResult.fail(this.choose(actor, "Сейчас модуль выбирает другая команда.", "Another team is choosing the module right now."));
        }
        return ActionResult.ok("");
    }

    private ActionResult startGame(World world, MatchContext context) throws IOException {
        if (BankDefenseMatchPhaseSupport.isPrepared(context.state)) {
            return ActionResult.fail(this.choose(world, "Матч уже запущен. Используй 'Начать волну' или дождись автозапуска.", "The match is already prepared. Use 'Start wave' or wait for auto-start."));
        }
        ActionResult tutorialValidation = this.validateTutorialWaveLaunch(world, context);
        if (tutorialValidation != null) {
            return tutorialValidation;
        }
        return this.beginWave(world, context, this.choose(world, "Матч начат.", "Match started."), false);
    }

    private ActionResult startPreparedWave(World world, MatchContext context, boolean manualStart) throws IOException {
        if (context.state.gameState == GameState.Victory) {
            return ActionResult.fail(this.choose(world, "Матч уже завершён победой. Подготовь новый матч у оператора.", "The match already ended in victory. Prepare a new match with the operator."));
        }
        if (context.state.gameState == GameState.Defeat) {
            return ActionResult.fail(this.choose(world, "Матч уже завершён поражением. Подготовь новый матч у оператора.", "The match already ended in defeat. Prepare a new match with the operator."));
        }
        if (!context.state.gameStarted) {
            return ActionResult.fail(this.choose(world, "Сначала выберите сложность и контракт у оператора.", "Choose a difficulty and contract with the operator first."));
        }
        if (!context.pendingRewardChoices.isEmpty()) {
            return ActionResult.fail(this.choose(world, "Сначала выберите модуль награды за волну.", "Choose the wave reward module first."));
        }
        if (this.hasPendingIdolModeChoice(context)) {
            return ActionResult.fail(this.choose(world, "Сначала выберите режим Идола урожая.", "Choose the Harvest Idol mode first."));
        }
        if (!BankDefenseMatchPhaseSupport.isBuildPhase(context.state)) {
            return ActionResult.fail(this.choose(world, "Сейчас нельзя запустить волну. Текущее состояние: ", "The wave cannot be started right now. Current state: ") + context.state.waveState + ".");
        }
        ActionResult tutorialValidation = this.validateTutorialWaveLaunch(world, context);
        if (tutorialValidation != null) {
            return tutorialValidation;
        }
        if (!context.tutorialActive && !this.hasAnyPlacedTowerOrTrap(context)) {
            String message = this.choose(world, "Сначала поставьте хотя бы одну башню или ловушку.", "Place at least one tower or trap first.");
            this.showEventToast(context, message, EVENT_TOAST_ICON_ALERT);
            this.playWorldUiSound(world, SOUND_NOTIFICATION);
            return ActionResult.fail(message);
        }
        return this.beginWave(world, context, manualStart ? this.choose(world, "Ручной старт.", "Manual start.") : null, manualStart);
    }

    private ActionResult beginWave(World world, MatchContext context, String prefaceMessage, boolean manualStart) throws IOException {
        WaveDefinition wave = this.resolveWaveDefinition(context, context.state.currentWave);
        if (wave == null) {
            return ActionResult.fail(this.choose(world, "Для волны " + context.state.currentWave + " не найден конфиг.", "No config found for wave " + context.state.currentWave + "."));
        }
        this.ensureBonusChestsSpawned(context);
        boolean autoStartBonus = !manualStart && context.state.instantAutoStart && context.state.currentWave > 1;
        double startBonusPercent = this.startBonusPercent(context, manualStart);
        int earlyStartBonus = this.calculateEarlyStartBonus(context, wave, manualStart);
        if (context.instantAutoStartWarmupPending) {
            this.clearInstantAutoStartWarmup(context);
        }

        context.activeWaveNumber = context.state.currentWave;
        context.waveElapsedSeconds = 0.0;
        context.pendingSpawns.clear();
        context.enemies.clear();
        context.state.gameStarted = true;
        context.state.preparationRemainingSeconds = 0.0;
        if (earlyStartBonus > 0) {
            this.grantMatchIncome(world, context, earlyStartBonus, null);
        }
        List<WaveSpawn> effectiveSpawns = this.effectiveWaveSpawns(context, wave);
        int activeLaneCount = this.activeLaneCount(effectiveSpawns);
        for (WaveSpawn spawn : effectiveSpawns) {
            EnemyDefinition enemy = context.enemyById.get(spawn.enemyId);
            if (enemy == null) {
                continue;
            }
            ScheduledSpawn scheduledSpawn = this.createScheduledSpawn(context, enemy, spawn, wave.index);
            int adjustedCount = enemy.bossEnemy ? 1 : this.adjustedSpawnCount(context, spawn, enemy, wave.index, activeLaneCount);
            for (int i = 0; i < adjustedCount; i++) {
                context.pendingSpawns.add(scheduledSpawn.withSpawnTime(spawn.startDelaySeconds + spawn.intervalSeconds * i));
            }
        }
        context.pendingSpawns.sort(Comparator.comparingDouble(scheduledSpawn -> scheduledSpawn.spawnAtSeconds));
        context.activeWaveTotalEnemies = context.pendingSpawns.size();
        context.contractEffectCooldownRemaining = this.initialContractEffectCooldown(context);
        context.state.gameState = GameState.InMatch;
        context.state.waveState = WaveState.Spawning;
        context.state.currentWave = context.activeWaveNumber;
        this.syncAcidStormActivation(world, context);
        this.onTutorialWaveStarted(world, context);

        this.refreshVisualizationIfEnabled(world);
        this.playWorldUiSound(world, SOUND_WAVE_START);
        if (context.tutorialActive) {
            this.playWorldSfxSound(world, SOUND_WAVE_START);
        }
        PlayerRef playerRef = this.primaryPlayerRef(world);
        if (earlyStartBonus > 0) {
            int startBonusDisplayPercent = (int)Math.round(startBonusPercent * 100.0);
            this.showEventToast(
                context,
                (BankDefenseLocalization.choose(playerRef, autoStartBonus ? "Автостарт: +" : "Ранний старт: +", autoStartBonus ? "Auto-start: +" : "Early start: +"))
                    + earlyStartBonus
                    + BankDefenseLocalization.choose(playerRef, " золота (+", " gold (+")
                    + startBonusDisplayPercent + "%).",
                EVENT_TOAST_ICON_MONEY
            );
            this.sendLocalizedWorldMessage(world, viewerRef ->
                BankDefenseLocalization.choose(viewerRef, "Волна ", "Wave ")
                    + context.activeWaveNumber
                    + BankDefenseLocalization.choose(viewerRef, " началась. ", " has started. ")
                    + BankDefenseLocalization.choose(viewerRef, autoStartBonus ? "Бонус за автостарт: +" : "Бонус за ранний старт: +", autoStartBonus ? "Auto-start bonus: +" : "Early-start bonus: +")
                    + earlyStartBonus
                    + BankDefenseLocalization.choose(viewerRef, " золота (+", " gold (+")
                    + startBonusDisplayPercent + "%)."
            );
        } else {
            this.sendLocalizedWorldMessage(world, viewerRef ->
                BankDefenseLocalization.choose(viewerRef, "Волна ", "Wave ")
                    + context.activeWaveNumber
                    + BankDefenseLocalization.choose(viewerRef, " началась.", " has started.")
            );
        }
        if (wave.bossWave) {
            String bossName = null;
            String bossId = null;
            for (WaveSpawn spawn : wave.spawns) {
                EnemyDefinition enemy = context.enemyById.get(spawn.enemyId);
                if (enemy != null && enemy.bossEnemy) {
                    bossName = BankDefenseLocalization.enemyDisplayName(
                        playerRef,
                        enemy.id,
                        this.text(enemy.displayName)
                    );
                    bossId = enemy.id;
                    break;
                }
            }
            this.showEventToast(
                context,
                bossName == null
                    ? BankDefenseLocalization.choose(playerRef, "Босс-волна началась.", "Boss wave started.")
                    : BankDefenseLocalization.choose(playerRef, "Босс-волна: ", "Boss wave: ") + bossName + ".",
                EVENT_TOAST_ICON_ALERT
            );
            String resolvedBossId = bossId;
            this.sendLocalizedWorldMessage(world, viewerRef -> {
                String localizedBossName = null;
                if (resolvedBossId != null && !resolvedBossId.isBlank()) {
                    EnemyDefinition bossDefinition = context.enemyById.get(resolvedBossId);
                    localizedBossName = bossDefinition == null
                        ? resolvedBossId
                        : BankDefenseLocalization.enemyDisplayName(viewerRef, bossDefinition.id, this.text(bossDefinition.displayName));
                }
                return localizedBossName == null
                    ? BankDefenseLocalization.choose(viewerRef, "Внимание: босс-волна!", "Warning: boss wave!")
                    : BankDefenseLocalization.choose(viewerRef, "Внимание: босс-волна! ", "Warning: boss wave! ")
                        + localizedBossName
                        + " ["
                        + resolvedBossId
                        + "].";
            });
        }

        StringBuilder builder = new StringBuilder();
        if (prefaceMessage != null && !prefaceMessage.isBlank()) {
            builder.append(prefaceMessage).append(' ');
        }
        builder.append(this.choose(world, "Волна ", "Wave "))
            .append(context.activeWaveNumber)
            .append(this.choose(world, " запущена. В очереди ", " started. Queued enemies: "))
            .append(context.pendingSpawns.size())
            .append(this.choose(world, " врагов.", "."));
        if (earlyStartBonus > 0) {
            int startBonusDisplayPercent = (int)Math.round(startBonusPercent * 100.0);
            builder.append(autoStartBonus ? this.choose(world, " Бонус за автостарт: +", " Auto-start bonus: +") : this.choose(world, " Бонус за ранний старт: +", " Early-start bonus: +"))
                .append(earlyStartBonus)
                .append(this.choose(world, " золота (+", " gold (+"))
                .append(startBonusDisplayPercent)
                .append("%)")
                .append('.');
        }
        return ActionResult.ok(builder.toString());
    }

    private double startBonusPercent(MatchContext context, boolean manualStart) {
        if (context == null) {
            return 0.0;
        }
        if (!manualStart && context.state.instantAutoStart) {
            if (context.instantAutoStartWarmupPending) {
                return this.instantAutoStartWarmupBonusPercent(context);
            }
            if (context.state.preparationRemainingSeconds <= 0.0) {
                return 0.0;
            }
            return 0.15;
        }
        if (context.state.preparationRemainingSeconds <= 0.0) {
            return 0.0;
        }
        if (manualStart) {
            int tenSecondChunks = Math.max(0, (int)Math.floor(context.state.preparationRemainingSeconds / 10.0));
            return Math.min(0.12, tenSecondChunks * 0.03);
        }
        return 0.0;
    }

    private int calculateEarlyStartBonus(MatchContext context, WaveDefinition wave, boolean manualStart) {
        if (wave == null || context == null) {
            return 0;
        }
        double bonusPercent = this.startBonusPercent(context, manualStart);
        if (bonusPercent <= 0.0) {
            return 0;
        }
        int baseCurrency = this.isDuoMode(context)
            ? Math.max(0, (context.blueCurrency + context.greenCurrency) / 2)
            : Math.max(0, context.state.currency);
        int rawBonus = (int)Math.round(baseCurrency * bonusPercent * this.contractEarlyStartBonusMultiplier(context));
        return this.scaledNonKillIncome(context, Math.max(0, rawBonus));
    }

    private void armInstantAutoStartWarmup(MatchContext context) {
        this.clearInstantAutoStartWarmup(context);
        if (context == null || !context.state.instantAutoStart) {
            return;
        }
        if (!BankDefenseMatchPhaseSupport.isBuildPhase(context.state) || context.state.preparationRemainingSeconds <= 0.0) {
            return;
        }
        context.instantAutoStartWarmupPending = true;
        context.instantAutoStartWarmupSeconds = context.state.preparationRemainingSeconds;
    }

    private void clearInstantAutoStartWarmup(MatchContext context) {
        if (context == null) {
            return;
        }
        context.instantAutoStartWarmupPending = false;
        context.instantAutoStartWarmupSeconds = 0.0;
    }

    private double instantAutoStartWarmupBonusPercent(MatchContext context) {
        if (context == null || !context.instantAutoStartWarmupPending) {
            return 0.0;
        }
        double totalPreparationSeconds = Math.max(1.0, this.contractPreparationSeconds(context));
        double remainingSeconds = Math.max(0.0, Math.min(context.instantAutoStartWarmupSeconds, totalPreparationSeconds));
        return Math.min(0.15, (remainingSeconds / totalPreparationSeconds) * 0.15);
    }

    private WaveDefinition resolveWaveDefinition(MatchContext context, int waveNumber) {
        if (context == null || waveNumber <= 0) {
            return null;
        }
        if (context.tutorialActive) {
            WaveDefinition tutorialWave = this.generateTutorialWave(context, waveNumber);
            if (tutorialWave != null) {
                return tutorialWave;
            }
        }
        WaveDefinition cached = context.generatedWaveByIndex.get(waveNumber);
        if (cached != null) {
            return cached;
        }
        WaveDefinition generated = this.generateRandomWave(context, waveNumber);
        if (generated != null) {
            context.generatedWaveByIndex.put(waveNumber, generated);
        }
        return generated;
    }

    private WaveDefinition generateTutorialWave(MatchContext context, int waveNumber) {
        if (context == null || !context.tutorialActive) {
            return null;
        }
        WaveDefinition generated = new WaveDefinition();
        generated.index = waveNumber;
        generated.bonusCurrency = switch (waveNumber) {
            case 1 -> 90;
            case 2 -> 110;
            case 3 -> 140;
            case 4 -> 0;
            case TUTORIAL_TRAP_WAVE_NUMBER -> 0;
            default -> 0;
        };
        switch (waveNumber) {
            case 1 -> {
                generated.bossWave = false;
                this.addTutorialSpawn(generated, context, "a", "grave_spawn", 3, 1.15, 1.4);
                return generated;
            }
            case 2 -> {
                generated.bossWave = false;
                this.addTutorialSpawn(generated, context, "a", "grave_spawn", 6, 0.90, 1.2);
                return generated;
            }
            case 3 -> {
                generated.bossWave = false;
                this.addTutorialSpawn(generated, context, "a", "thief", 6, 0.48, 0.7);
                this.addTutorialSpawn(generated, context, "b", "runner", 5, 0.54, 1.4);
                this.addTutorialSpawn(generated, context, "a", "bruiser", 2, 0.0, 4.5);
                this.addTutorialSpawn(generated, context, "b", "grave_spawn", 4, 0.50, 5.2);
                return generated;
            }
            case 4 -> {
                generated.bossWave = true;
                this.addTutorialSpawn(generated, context, "a", "vault_breaker", 1, 0.0, 1.2);
                return generated;
            }
            case TUTORIAL_TRAP_WAVE_NUMBER -> {
                generated.bossWave = false;
                this.addTutorialSpawn(generated, context, this.tutorialTrapLaneId(context, 0), TUTORIAL_TRAP_ENEMY_ID, 1, 0.0, 0.9);
                return generated;
            }
            default -> {
                return null;
            }
        }
    }

    private void addTutorialSpawn(WaveDefinition wave, MatchContext context, String laneId, String enemyId, int count, double intervalSeconds, double startDelaySeconds) {
        if (wave == null || context == null || enemyId == null || count <= 0) {
            return;
        }
        if (!context.enemyById.containsKey(enemyId)) {
            return;
        }
        WaveSpawn spawn = new WaveSpawn();
        spawn.enemyId = enemyId;
        spawn.count = count;
        spawn.intervalSeconds = intervalSeconds;
        spawn.startDelaySeconds = startDelaySeconds;
        spawn.laneId = laneId;
        wave.spawns.add(spawn);
    }

    private void queueNextTutorialTrapEnemy(MatchContext context, int nextSequenceIndex) {
        if (context == null || nextSequenceIndex < 0 || nextSequenceIndex >= 3) {
            return;
        }
        EnemyDefinition enemy = context.enemyById.get(TUTORIAL_TRAP_ENEMY_ID);
        if (enemy == null) {
            return;
        }
        WaveSpawn spawn = new WaveSpawn();
        spawn.enemyId = TUTORIAL_TRAP_ENEMY_ID;
        spawn.count = 1;
        spawn.intervalSeconds = 0.0;
        spawn.startDelaySeconds = 0.0;
        spawn.laneId = this.tutorialTrapLaneId(context, nextSequenceIndex);
        ScheduledSpawn scheduledSpawn = this.createScheduledSpawn(context, enemy, spawn, TUTORIAL_TRAP_WAVE_NUMBER);
        context.pendingSpawns.add(scheduledSpawn.withSpawnTime(context.waveElapsedSeconds + TUTORIAL_TRAP_NEXT_SPAWN_DELAY_SECONDS));
        context.pendingSpawns.sort(Comparator.comparingDouble(candidate -> candidate.spawnAtSeconds));
        context.activeWaveTotalEnemies = Math.max(context.activeWaveTotalEnemies, nextSequenceIndex + 1);
        if (context.state.waveState == WaveState.Cleanup) {
            context.state.waveState = WaveState.Spawning;
        }
    }

    private WaveDefinition generateRandomWave(MatchContext context, int waveNumber) {
        List<String> availableLanes = this.availableLaneIds(context);
        if (availableLanes.isEmpty()) {
            return null;
        }
        Random random = new Random(this.waveSeed(context, waveNumber));
        boolean bossWave = waveNumber % BOSS_WAVE_INTERVAL == 0;
        int laneCount = this.laneCountForWave(context, availableLanes.size(), waveNumber, bossWave, random);
        List<String> lanes = this.pickWaveLanes(availableLanes, laneCount, random);

        WaveDefinition generated = new WaveDefinition();
        generated.index = waveNumber;
        generated.bossWave = bossWave;
        if (bossWave) {
            this.populateBossWave(generated, context, waveNumber, lanes, random);
        } else {
            this.populateRegularWave(generated, context, waveNumber, lanes, random);
        }
        generated.bonusCurrency = this.generatedWaveBonusCurrency(context, waveNumber, lanes.size(), bossWave);
        return generated;
    }

    private long waveSeed(MatchContext context, int waveNumber) {
        long seed = 0x9E3779B97F4A7C15L ^ (long)waveNumber * 0x632BE59BD9B4E019L;
        if (context.progressionOwnerUuid != null) {
            seed ^= (long)context.progressionOwnerUuid.hashCode() * 0x94D049BB133111EBL;
        }
        if (context.snapshot != null && context.snapshot.map != null && context.snapshot.map.worldName != null) {
            seed ^= (long)context.snapshot.map.worldName.hashCode() << 21;
        }
        return seed;
    }

    private List<String> availableLaneIds(MatchContext context) {
        List<String> lanes = new ArrayList<>();
        String[] laneOrder = this.isDuoMode(context) ? new String[]{"a", "c"} : new String[]{"a", "b", "c"};
        for (String laneId : laneOrder) {
            List<Vec3i> route = context.routesByLane.get(laneId);
            Double routeLength = context.routeLengthByLane.get(laneId);
            if (route != null && route.size() >= 2 && routeLength != null && routeLength.doubleValue() > 0.0) {
                lanes.add(laneId);
            }
        }
        if (lanes.isEmpty() && context.route != null && context.route.size() >= 2) {
            lanes.add("a");
        }
        return lanes;
    }

    private int laneCountForWave(MatchContext context, int availableLaneCount, int waveNumber, boolean bossWave, Random random) {
        if (this.isDuoMode(context)) {
            return Math.max(1, Math.min(2, availableLaneCount));
        }
        int pressureWave = this.pressureWaveNumber(context, waveNumber);
        if (availableLaneCount <= 1) {
            return 1;
        }
        boolean tripleBreach = this.isContractActive(context, "triple_breach");
        if (bossWave) {
            if (tripleBreach) {
                if (pressureWave < 20) {
                    return Math.min(2, availableLaneCount);
                }
                if (availableLaneCount == 2) {
                    return 2;
                }
                return random.nextDouble() < 0.65 ? 3 : 2;
            }
            if (pressureWave < 20) {
                return 1;
            }
            if (availableLaneCount == 2 || pressureWave < 36) {
                return Math.min(2, availableLaneCount);
            }
            return random.nextDouble() < 0.35 ? 3 : 2;
        }
        if (pressureWave <= 4) {
            if (tripleBreach && pressureWave >= 3 && random.nextDouble() < 0.28) {
                return Math.min(2, availableLaneCount);
            }
            return 1;
        }
        if (pressureWave <= 8) {
            if (tripleBreach) {
                return random.nextDouble() < 0.62 ? Math.min(2, availableLaneCount) : 1;
            }
            return random.nextDouble() < 0.20 ? Math.min(2, availableLaneCount) : 1;
        }
        if (pressureWave <= 14) {
            if (tripleBreach && availableLaneCount >= 3 && pressureWave >= 12 && random.nextDouble() < 0.22) {
                return 3;
            }
            return Math.min(2, availableLaneCount);
        }
        if (pressureWave <= 20) {
            if (tripleBreach && availableLaneCount >= 3) {
                return random.nextDouble() < 0.42 ? 3 : 2;
            }
            if (availableLaneCount == 2) {
                return 2;
            }
            return random.nextDouble() < 0.15 ? 3 : 2;
        }
        if (availableLaneCount == 2) {
            return 2;
        }
        if (tripleBreach) {
            return random.nextDouble() < 0.55 ? 3 : 2;
        }
        return random.nextDouble() < 0.30 ? 3 : 2;
    }

    private List<String> pickWaveLanes(List<String> availableLanes, int requestedLaneCount, Random random) {
        List<String> pool = new ArrayList<>(availableLanes);
        Collections.shuffle(pool, random);
        int laneCount = Math.max(1, Math.min(requestedLaneCount, pool.size()));
        return new ArrayList<>(pool.subList(0, laneCount));
    }

    private void populateRegularWave(WaveDefinition generated, MatchContext context, int waveNumber, List<String> lanes, Random random) {
        if (this.isDuoMode(context) && lanes != null && lanes.contains("a") && lanes.contains("c")) {
            this.populateMirroredDuoRegularWave(generated, context, waveNumber, random);
            return;
        }
        int pressureWave = this.pressureWaveNumber(context, waveNumber);
        double lanePressureMultiplier = this.multiLanePressureMultiplier(lanes.size());
        for (int laneIndex = 0; laneIndex < lanes.size(); laneIndex++) {
            String laneId = lanes.get(laneIndex);
            double laneShare = (lanePressureMultiplier / Math.max(1, lanes.size())) * (0.92 + random.nextDouble() * 0.16);
            double timeCursor = laneIndex * 0.85;

            String openingEnemy = pressureWave >= 3 && random.nextDouble() < 0.34 ? "runner" : "thief";
            int openingCount = Math.max(4, (int)Math.round((4.5 + pressureWave * 0.55) * laneShare));
            this.addGeneratedSpawn(generated, laneId, openingEnemy, openingCount, this.spawnIntervalForEnemy(openingEnemy, waveNumber), timeCursor);
            timeCursor += Math.max(1.1, openingCount * 0.09 + 1.0);

            if (pressureWave >= 2) {
                String supportEnemy = random.nextDouble() < 0.55 ? "runner" : "thief";
                int supportCount = Math.max(0, (int)Math.round((1.0 + pressureWave * 0.18) * laneShare));
                if (supportCount > 0) {
                    this.addGeneratedSpawn(generated, laneId, supportEnemy, supportCount, this.spawnIntervalForEnemy(supportEnemy, waveNumber), timeCursor);
                    timeCursor += Math.max(0.9, supportCount * 0.08 + 0.8);
                }
            }

            boolean bruiserLane = laneIndex == 0
                || (pressureWave < 12
                    ? random.nextDouble() < 0.24
                    : pressureWave < 20
                        ? random.nextDouble() < 0.50
                        : random.nextDouble() < 0.65);
            if (pressureWave >= 4 && bruiserLane) {
                int bruiserCount = Math.max(1, (int)Math.round((0.28 + pressureWave * 0.045) * laneShare));
                this.addGeneratedSpawn(generated, laneId, "bruiser", bruiserCount, this.spawnIntervalForEnemy("bruiser", waveNumber), timeCursor);
                timeCursor += 1.8;
            }

            if (pressureWave >= 5) {
                String mediumEnemy = this.pickUnlockedEnemy(context, random, this.unlockedMediumEnemies(pressureWave));
                if (mediumEnemy != null) {
                    int mediumCount = Math.max(1, this.mediumPackCount(mediumEnemy, pressureWave, laneShare, random));
                    this.addGeneratedSpawn(generated, laneId, mediumEnemy, mediumCount, this.spawnIntervalForEnemy(mediumEnemy, waveNumber), timeCursor);
                }
            }
        }

        int minibossCount = this.minibossCountForWave(pressureWave, lanes.size(), random);
        for (int i = 0; i < minibossCount; i++) {
            String laneId = lanes.get(random.nextInt(lanes.size()));
            String minibossId = this.pickUnlockedEnemy(context, random, this.unlockedMinibossEnemies(pressureWave));
            if (minibossId == null) {
                continue;
            }
            double startDelay = 5.5 + (i * 2.4) + random.nextDouble() * 2.2;
            this.addGeneratedSpawn(generated, laneId, minibossId, 1, 0.0, startDelay);
        }
        if (this.shouldSpawnGoblinSaboteur(waveNumber) && context.enemyById.containsKey(ENEMY_GOBLIN_SABOTEUR)) {
            String laneId = lanes.get(random.nextInt(lanes.size()));
            double startDelay = 4.4 + random.nextDouble() * 2.6;
            this.addGeneratedSpawn(generated, laneId, ENEMY_GOBLIN_SABOTEUR, 1, 0.0, startDelay);
        }
    }

    private void populateMirroredDuoRegularWave(WaveDefinition generated, MatchContext context, int waveNumber, Random random) {
        int pressureWave = this.pressureWaveNumber(context, waveNumber);
        String laneId = "b";
        double laneShare = (this.multiLanePressureMultiplier(2) / 2.0) * (0.94 + random.nextDouble() * 0.12);
        if (pressureWave == 14) {
            laneShare *= 0.90;
        } else if (pressureWave == 15) {
            laneShare *= 0.95;
        }
        double timeCursor = 0.0;

        String openingEnemy = pressureWave >= 3 && random.nextDouble() < 0.34 ? "runner" : "thief";
        int openingCount = Math.max(4, (int)Math.round((4.5 + pressureWave * 0.55) * laneShare));
        this.addGeneratedSpawn(generated, laneId, openingEnemy, openingCount, this.spawnIntervalForEnemy(openingEnemy, waveNumber), timeCursor);
        timeCursor += Math.max(1.1, openingCount * 0.09 + 1.0);

        if (pressureWave >= 2) {
            String supportEnemy = random.nextDouble() < 0.55 ? "runner" : "thief";
            int supportCount = Math.max(0, (int)Math.round((1.0 + pressureWave * 0.18) * laneShare));
            if (supportCount > 0) {
                this.addGeneratedSpawn(generated, laneId, supportEnemy, supportCount, this.spawnIntervalForEnemy(supportEnemy, waveNumber), timeCursor);
                timeCursor += Math.max(0.9, supportCount * 0.08 + 0.8);
            }
        }

        boolean bruiserPack = pressureWave < 12
            ? random.nextDouble() < 0.32
            : pressureWave < 20
                ? random.nextDouble() < 0.56
                : random.nextDouble() < 0.70;
        if (pressureWave >= 4 && bruiserPack) {
            int bruiserCount = Math.max(1, (int)Math.round((0.28 + pressureWave * 0.045) * laneShare));
            this.addGeneratedSpawn(generated, laneId, "bruiser", bruiserCount, this.spawnIntervalForEnemy("bruiser", waveNumber), timeCursor);
            timeCursor += 1.8;
        }

        if (pressureWave >= 5) {
            String mediumEnemy = this.pickUnlockedEnemy(context, random, this.unlockedMediumEnemies(pressureWave));
            if (mediumEnemy != null) {
                if (pressureWave == 14 && "elite_robber".equals(mediumEnemy)) {
                    mediumEnemy = random.nextBoolean() ? "bone_guard" : "berserker_rotter";
                }
                int mediumCount = Math.max(1, this.mediumPackCount(mediumEnemy, pressureWave, laneShare, random));
                if (pressureWave == 14) {
                    mediumCount = Math.max(1, mediumCount - 1);
                }
                this.addGeneratedSpawn(generated, laneId, mediumEnemy, mediumCount, this.spawnIntervalForEnemy(mediumEnemy, waveNumber), timeCursor);
            }
        }

        int minibossCount = this.minibossCountForWave(pressureWave, 2, random);
        if (pressureWave == 14) {
            minibossCount = 0;
        }
        for (int i = 0; i < minibossCount; i++) {
            String minibossId = this.pickUnlockedEnemy(context, random, this.unlockedMinibossEnemies(pressureWave));
            if (minibossId == null) {
                continue;
            }
            double startDelay = 5.5 + (i * 2.4) + random.nextDouble() * 2.2;
            this.addGeneratedSpawn(generated, laneId, minibossId, 1, 0.0, startDelay);
        }
        if (this.shouldSpawnGoblinSaboteur(waveNumber) && context.enemyById.containsKey(ENEMY_GOBLIN_SABOTEUR)) {
            double startDelay = 4.4 + random.nextDouble() * 2.6;
            this.addGeneratedSpawn(generated, laneId, ENEMY_GOBLIN_SABOTEUR, 1, 0.0, startDelay);
        }
    }

    private void populateBossWave(WaveDefinition generated, MatchContext context, int waveNumber, List<String> lanes, Random random) {
        if (this.isDuoMode(context)) {
            this.populateDuoBossWave(generated, context, waveNumber, lanes, random);
            return;
        }
        int pressureWave = this.pressureWaveNumber(context, waveNumber);
        double lanePressureMultiplier = this.multiLanePressureMultiplier(lanes.size());
        String bossLane = lanes.get(random.nextInt(lanes.size()));
        List<String> bossPool = new ArrayList<>();
        for (String enemyId : this.bossEnemyPool(context)) {
            if (context.enemyById.containsKey(enemyId)) {
                bossPool.add(enemyId);
            }
        }
        String bossId = this.rotatingBossEnemyId(context, waveNumber, bossPool);
        if (bossId == null) {
            bossId = this.fallbackBossEnemyId(context);
        }
        if (bossId == null) {
            bossId = "vault_breaker";
        }

        for (int laneIndex = 0; laneIndex < lanes.size(); laneIndex++) {
            String laneId = lanes.get(laneIndex);
            double laneShare = (lanePressureMultiplier / Math.max(1, lanes.size())) * (laneId.equals(bossLane) ? 1.08 : 0.88);
            double timeCursor = laneIndex * 1.05;

            int openerCount = Math.max(4, (int)Math.round((4.0 + pressureWave * 0.32) * laneShare));
            this.addGeneratedSpawn(generated, laneId, random.nextDouble() < 0.5 ? "thief" : "runner", openerCount, 0.56, timeCursor);
            timeCursor += 1.6;

            int bruiserCount = Math.max(1, (int)Math.round((0.32 + pressureWave * 0.05) * laneShare));
            this.addGeneratedSpawn(generated, laneId, "bruiser", bruiserCount, this.spawnIntervalForEnemy("bruiser", waveNumber), timeCursor);
            timeCursor += 1.8;

            if (pressureWave >= MINIBOSS_START_WAVE + 2 && (laneId.equals(bossLane) || lanes.size() > 1)) {
                String minibossId = this.pickUnlockedEnemy(context, random, this.unlockedMinibossEnemies(pressureWave));
                if (minibossId != null) {
                    this.addGeneratedSpawn(generated, laneId, minibossId, 1, 0.0, timeCursor + random.nextDouble() * 1.5);
                }
            }

            if (laneId.equals(bossLane)) {
                String mediumEnemy = this.pickUnlockedEnemy(context, random, this.unlockedMediumEnemies(pressureWave));
                if (mediumEnemy != null) {
                    this.addGeneratedSpawn(generated, laneId, mediumEnemy, Math.max(1, this.mediumPackCount(mediumEnemy, pressureWave, laneShare, random)), this.spawnIntervalForEnemy(mediumEnemy, waveNumber), 6.0 + random.nextDouble() * 2.0);
                }
            }
        }

        if (this.shouldSpawnGoblinSaboteur(waveNumber) && context.enemyById.containsKey(ENEMY_GOBLIN_SABOTEUR)) {
            List<String> nonBossLanes = new ArrayList<>(lanes);
            if (nonBossLanes.size() > 1) {
                nonBossLanes.remove(bossLane);
            }
            String sabotageLane = nonBossLanes.get(random.nextInt(nonBossLanes.size()));
            this.addGeneratedSpawn(generated, sabotageLane, ENEMY_GOBLIN_SABOTEUR, 1, 0.0, 6.0 + random.nextDouble() * 2.0);
        }
        this.addGeneratedSpawn(generated, bossLane, bossId, 1, 0.0, 10.5 + random.nextDouble() * 1.8);
    }

    private void populateDuoBossWave(WaveDefinition generated, MatchContext context, int waveNumber, List<String> lanes, Random random) {
        int pressureWave = this.pressureWaveNumber(context, waveNumber);
        List<String> availableBosses = new ArrayList<>();
        for (String enemyId : this.bossEnemyPool(context)) {
            if (context.enemyById.containsKey(enemyId)) {
                availableBosses.add(enemyId);
            }
        }
        String bossId = this.rotatingBossEnemyId(context, waveNumber, availableBosses);
        if (bossId == null) {
            bossId = this.fallbackBossEnemyId(context);
        }
        if (bossId == null) {
            bossId = ENEMY_RIFT_TWIN_ALPHA;
        }

        String blueLane = lanes.contains("a") ? "a" : lanes.get(0);
        String greenLane = lanes.contains("c") ? "c" : lanes.get(lanes.size() - 1);
        double openerDelay = 0.8;
        int openerCount = Math.max(3, 3 + pressureWave / 5);
        String openerEnemy = random.nextBoolean() ? "thief" : "runner";
        this.addGeneratedSpawn(generated, blueLane, openerEnemy, openerCount, 0.52, openerDelay);
        this.addGeneratedSpawn(generated, greenLane, openerEnemy, openerCount, 0.52, openerDelay + 0.25);
        this.addGeneratedSpawn(generated, blueLane, "bruiser", Math.max(1, pressureWave / 12), 1.15, 4.0);
        this.addGeneratedSpawn(generated, greenLane, "bruiser", Math.max(1, pressureWave / 12), 1.15, 4.2);

        if (ENEMY_RIFT_TWIN_ALPHA.equals(bossId)) {
            this.addGeneratedSpawn(generated, blueLane, ENEMY_RIFT_TWIN_ALPHA, 1, 0.0, 9.0);
            if (context.enemyById.containsKey(ENEMY_RIFT_TWIN_BETA)) {
                this.addGeneratedSpawn(generated, greenLane, ENEMY_RIFT_TWIN_BETA, 1, 0.0, 11.0);
            }
            return;
        }

        String bossLane = random.nextBoolean() ? blueLane : greenLane;
        this.addGeneratedSpawn(generated, bossLane, bossId, 1, 0.0, 9.0);
        if (ENEMY_SEAL_MASTER.equals(bossId) || ENEMY_NODE_ARBITER.equals(bossId)) {
            String supportEnemy = pressureWave >= 18 ? "bone_guard" : "runner_bomber";
            this.addGeneratedSpawn(generated, blueLane, supportEnemy, 1, 0.0, 6.6);
            this.addGeneratedSpawn(generated, greenLane, supportEnemy, 1, 0.0, 6.85);
        }
    }

    private boolean shouldSpawnGoblinSaboteur(int waveNumber) {
        return waveNumber >= 15 && waveNumber % 5 == 0;
    }

    private String rotatingBossEnemyId(MatchContext context, int waveNumber, List<String> availableBosses) {
        if (availableBosses == null || availableBosses.isEmpty()) {
            return null;
        }
        List<String> ordered = new ArrayList<>();
        for (String bossId : this.bossEnemyPool(context)) {
            if (availableBosses.contains(bossId)) {
                ordered.add(bossId);
            }
        }
        for (String bossId : availableBosses) {
            if (!ordered.contains(bossId)) {
                ordered.add(bossId);
            }
        }
        if (ordered.size() == 1) {
            return ordered.get(0);
        }
        int bossOrdinal = Math.max(1, waveNumber / BOSS_WAVE_INTERVAL);
        int cycleSize = ordered.size();
        int index = Math.floorMod(bossOrdinal - 1, cycleSize);
        return ordered.get(index);
    }

    private int mediumPackCount(String enemyId, int waveNumber, double laneShare, Random random) {
        if ("runner_bomber".equals(enemyId)) {
            return Math.max(1, Math.min(3, (int)Math.round(1.0 + laneShare * 0.8 + random.nextDouble() * 0.8)));
        }
        if ("bone_guard".equals(enemyId) || "berserker_rotter".equals(enemyId)) {
            return Math.max(1, Math.min(2, (int)Math.round(1.0 + laneShare * 0.55 + random.nextDouble() * 0.55)));
        }
        if ("elite_robber".equals(enemyId)) {
            return waveNumber >= 28 && random.nextDouble() < 0.22 ? 2 : 1;
        }
        return 1;
    }

    private int minibossCountForWave(int waveNumber, int laneCount, Random random) {
        if (waveNumber < MINIBOSS_START_WAVE) {
            return 0;
        }
        if (waveNumber < 14) {
            return random.nextDouble() < 0.45 ? 1 : 0;
        }
        if (waveNumber < 22) {
            return 1;
        }
        if (waveNumber < 28) {
            return 1 + (laneCount > 1 && random.nextDouble() < 0.25 ? 1 : 0);
        }
        return 1 + (random.nextDouble() < 0.45 ? 1 : 0);
    }

    private int generatedWaveBonusCurrency(MatchContext context, int waveNumber, int laneCount, boolean bossWave) {
        GameRules rules = context.snapshot.gameRules;
        int base = 24 + waveNumber * 10 + Math.max(0, waveNumber - 14) * 2 + Math.max(0, laneCount - 1) * 14;
        if (bossWave) {
            base += 26 + waveNumber * 2;
        }
        int endlessOffset = Math.max(0, waveNumber - Math.max(1, rules.totalWaves));
        if (endlessOffset > 0) {
            base = Math.max(
                base + endlessOffset * 5,
                (int)Math.round(base * this.endlessMultiplier(rules.endlessWaveBonusScalePerWave, endlessOffset))
            );
        }
        return Math.max(26, base);
    }

    private double multiLanePressureMultiplier(int laneCount) {
        return switch (Math.max(1, laneCount)) {
            case 2 -> 1.18;
            case 3 -> 1.30;
            default -> 1.0;
        };
    }

    private int pressureWaveNumber(MatchContext context, int waveNumber) {
        int offset = 0;
        if (context != null && context.difficulty != null && context.difficulty.id != null) {
            offset = switch (context.difficulty.id) {
                case DIFFICULTY_EASY -> -4;
                case DIFFICULTY_NORMAL -> -2;
                case DIFFICULTY_HARD -> 1;
                case DIFFICULTY_NIGHTMARE -> 3;
                default -> 0;
            };
        }
        return Math.max(1, waveNumber + offset);
    }

    private boolean isContractActive(MatchContext context, String contractId) {
        return context != null
            && context.activeContract != null
            && contractId != null
            && contractId.equals(context.activeContract.id);
    }

    private boolean isFastEnemy(String enemyId) {
        return "thief".equals(enemyId)
            || "runner".equals(enemyId)
            || "runner_bomber".equals(enemyId)
            || "grave_spawn".equals(enemyId)
            || ENEMY_GOBLIN_SABOTEUR.equals(enemyId);
    }

    private boolean isNecroticSupportEnemy(String enemyId) {
        return "necro_thief".equals(enemyId)
            || "vault_priest".equals(enemyId)
            || "plague_standard".equals(enemyId)
            || "curse_weaver".equals(enemyId)
            || "bone_trumpeter".equals(enemyId)
            || "jammer".equals(enemyId);
    }

    private double contractEnemyCountMultiplier(MatchContext context, EnemyDefinition enemy) {
        if (context == null || context.activeContract == null || enemy == null) {
            return 1.0;
        }
        return switch (context.activeContract.id) {
            case "storm_front" -> this.isFastEnemy(enemy.id) ? 1.18 : 1.0;
            default -> 1.0;
        };
    }

    private double contractEnemySpeedMultiplier(MatchContext context, EnemyDefinition enemy) {
        if (context == null || context.activeContract == null || enemy == null) {
            return 1.0;
        }
        return switch (context.activeContract.id) {
            case "storm_front" -> this.isFastEnemy(enemy.id) ? 1.18 : 1.0;
            default -> 1.0;
        };
    }

    private double contractSlowEffectMultiplier(MatchContext context, EnemyInstance enemy) {
        if (context == null || context.activeContract == null || enemy == null) {
            return 1.0;
        }
        return switch (context.activeContract.id) {
            case "storm_front" -> this.isFastEnemy(enemy.definition.id) ? 0.60 : 1.0;
            default -> 1.0;
        };
    }

    private double contractAuraRadiusMultiplier(MatchContext context, EnemyDefinition source) {
        if (context == null || context.activeContract == null || source == null) {
            return 1.0;
        }
        return switch (context.activeContract.id) {
            case "necrotic_mist" -> this.isNecroticSupportEnemy(source.id) ? 1.18 : 1.0;
            default -> 1.0;
        };
    }

    private double contractAuraEffectMultiplier(MatchContext context, EnemyDefinition source) {
        if (context == null || context.activeContract == null || source == null) {
            return 1.0;
        }
        return switch (context.activeContract.id) {
            case "necrotic_mist" -> this.isNecroticSupportEnemy(source.id) ? 1.24 : 1.0;
            default -> 1.0;
        };
    }

    private double contractEarlyStartBonusMultiplier(MatchContext context) {
        if (context == null || context.activeContract == null) {
            return 1.0;
        }
        return switch (context.activeContract.id) {
            case "lean_purse" -> 0.20;
            default -> 1.0;
        };
    }

    private int contractMoneyChestCount(MatchContext context) {
        if (this.isContractActive(context, "lean_purse")) {
            return 0;
        }
        return MONEY_CHEST_COUNT;
    }

    private int contractPreparationSeconds(MatchContext context) {
        if (context == null) {
            return 0;
        }
        if (this.isNormalDifficulty(context)) {
            return this.applySealCursePreparationSeconds(context, 40);
        }
        if (context.snapshot == null || context.snapshot.gameRules == null) {
            return 0;
        }
        int base = Math.max(0, context.snapshot.gameRules.interWavePrepSeconds);
        if (this.isContractActive(context, "storm_front")) {
            return this.applySealCursePreparationSeconds(context, Math.max(8, (int)Math.round(base * 0.60)));
        }
        return this.applySealCursePreparationSeconds(context, base);
    }

    private int applySealCursePreparationSeconds(MatchContext context, int baseSeconds) {
        if (baseSeconds <= 0 || !this.hasActiveSealCurse(context, SealCurseType.Preparation)) {
            return baseSeconds;
        }
        return DUO_SEAL_CURSE_PREPARATION_SECONDS;
    }

    private int applySealCurseIncome(MatchContext context, int amount) {
        if (amount <= 0 || !this.hasActiveSealCurse(context, SealCurseType.Gold)) {
            return amount;
        }
        return Math.max(1, (int)Math.round(amount * DUO_SEAL_CURSE_GOLD_MULTIPLIER));
    }

    private double sealCurseEnemySpeedMultiplier(MatchContext context) {
        return this.hasActiveSealCurse(context, SealCurseType.EnemySpeed)
            ? DUO_SEAL_CURSE_ENEMY_SPEED_MULTIPLIER
            : 1.0;
    }

    private double sealCurseTowerFireRateMultiplier(MatchContext context) {
        return this.hasActiveSealCurse(context, SealCurseType.FireRate)
            ? DUO_SEAL_CURSE_FIRE_RATE_MULTIPLIER
            : 1.0;
    }

    private double trapTriggerDelaySeconds(MatchContext context) {
        return TRAP_TRIGGER_DELAY_SECONDS
            * (this.hasActiveSealCurse(context, SealCurseType.TrapDelay) ? DUO_SEAL_CURSE_TRAP_DELAY_MULTIPLIER : 1.0);
    }

    private double moduleEffectScale(MatchContext context) {
        return this.hasActiveSealCurse(context, SealCurseType.ModuleEffect)
            ? DUO_SEAL_CURSE_MODULE_EFFECT_MULTIPLIER
            : 1.0;
    }

    private double moduleRelativeMultiplier(MatchContext context, double value) {
        return 1.0 + ((value - 1.0) * this.moduleEffectScale(context));
    }

    private double moduleBonusValue(MatchContext context, double value) {
        return value * this.moduleEffectScale(context);
    }

    private boolean hasActiveSealCurse(MatchContext context, SealCurseType type) {
        if (context == null || type == null) {
            return false;
        }
        for (SealCurseState curse : context.activeSealCurses) {
            if (curse != null && curse.type == type && this.isSealCurseActive(context, curse)) {
                return true;
            }
        }
        return false;
    }

    private boolean isSealCurseActive(MatchContext context, SealCurseState curse) {
        if (context == null || curse == null || curse.remainingWaves <= 0) {
            return false;
        }
        return this.sealCurseReferenceWave(context) >= curse.startWave;
    }

    private int sealCurseReferenceWave(MatchContext context) {
        if (context == null || context.state == null) {
            return 0;
        }
        return context.activeWaveNumber > 0
            ? context.activeWaveNumber
            : Math.max(0, context.state.currentWave);
    }

    private List<SealCurseState> activeSealCurseStates(MatchContext context) {
        List<SealCurseState> active = new ArrayList<>();
        if (context == null) {
            return active;
        }
        for (SealCurseState curse : context.activeSealCurses) {
            if (this.isSealCurseActive(context, curse)) {
                active.add(curse);
            }
        }
        return active;
    }

    private String activeSealCurseHudCountLabel(PlayerRef playerRef, MatchContext context) {
        List<SealCurseState> active = this.activeSealCurseStates(context);
        if (active.isEmpty()) {
            return "";
        }
        return this.choose(playerRef, "Проклятия: ", "Curses: ") + active.size();
    }

    private String appendSealCurseHudSummary(PlayerRef playerRef, MatchContext context, String base) {
        String summary = this.activeSealCurseSummary(playerRef, context, true);
        if (summary.isBlank()) {
            return base;
        }
        return base + " | " + this.choose(playerRef, "Проклятия: ", "Curses: ") + summary;
    }

    private String activeSealCurseSummary(PlayerRef playerRef, MatchContext context, boolean shortLabels) {
        List<SealCurseState> active = this.activeSealCurseStates(context);
        if (active.isEmpty()) {
            return "";
        }
        List<String> parts = new ArrayList<>();
        for (SealCurseState curse : active) {
            String label = shortLabels
                ? this.sealCurseShortLabel(playerRef, curse.type)
                : this.sealCurseDisplayName(playerRef, curse.type);
            parts.add(label + " (" + curse.remainingWaves + ")");
        }
        return String.join(", ", parts);
    }

    private String sealCurseDisplayName(PlayerRef playerRef, SealCurseType type) {
        return switch (type) {
            case Range -> this.choose(playerRef, "Тусклые прицелы", "Dim Sights");
            case FireRate -> this.choose(playerRef, "Заедающие механизмы", "Jamming Mechanisms");
            case TrapDelay -> this.choose(playerRef, "Тяжёлые ловушки", "Heavy Traps");
            case Damage -> this.choose(playerRef, "Надломленные механизмы", "Cracked Mechanisms");
            case Preparation -> this.choose(playerRef, "Сжатая передышка", "Compressed Breather");
            case EnemySpeed -> this.choose(playerRef, "Тревожный марш", "Anxious March");
            case UpgradeCost -> this.choose(playerRef, "Давление печати", "Seal Pressure");
            case ModuleEffect -> this.choose(playerRef, "Слабый резонанс", "Faded Resonance");
            case Gold -> this.choose(playerRef, "Скудная добыча", "Meager Bounty");
        };
    }

    private String sealCurseShortLabel(PlayerRef playerRef, SealCurseType type) {
        return switch (type) {
            case Range -> this.choose(playerRef, "-12% дальн.", "-12% range");
            case FireRate -> this.choose(playerRef, "-10% скоростр.", "-10% fire rate");
            case TrapDelay -> this.choose(playerRef, "ловушки +35%", "traps +35%");
            case Damage -> this.choose(playerRef, "-12% урон", "-12% damage");
            case Preparation -> this.choose(playerRef, "подготовка 15с", "prep 15s");
            case EnemySpeed -> this.choose(playerRef, "враги +6% ск.", "enemies +6% spd");
            case UpgradeCost -> this.choose(playerRef, "апгрейд +10%", "upgrades +10%");
            case ModuleEffect -> this.choose(playerRef, "модули -15%", "modules -15%");
            case Gold -> this.choose(playerRef, "золото -20%", "gold -20%");
        };
    }

    private double sealMasterProgressRatio(MatchContext context, EnemyInstance enemy) {
        if (context == null || enemy == null) {
            return 0.0;
        }
        double routeLength = Math.max(0.01, this.routeLengthForLane(context, enemy.laneId));
        return Math.max(0.0, Math.min(1.0, enemy.progress / routeLength));
    }

    private int sealMasterCurseCountForProgress(double progressRatio) {
        if (progressRatio < DUO_SEAL_MASTER_FIRST_THRESHOLD) {
            return DUO_SEAL_MASTER_FAST_KILL_CURSES;
        }
        if (progressRatio < DUO_SEAL_MASTER_SECOND_THRESHOLD) {
            return DUO_SEAL_MASTER_MID_KILL_CURSES;
        }
        return DUO_SEAL_MASTER_LATE_KILL_CURSES;
    }

    private void applySealMasterDeathCurses(World world, MatchContext context, EnemyInstance enemy) {
        if (world == null || context == null || enemy == null || enemy.definition == null || !ENEMY_SEAL_MASTER.equals(enemy.definition.id)) {
            return;
        }
        double progressRatio = this.sealMasterProgressRatio(context, enemy);
        int curseCount = this.sealMasterCurseCountForProgress(progressRatio);
        List<SealCurseType> selected = this.pickSealCurseTypes(curseCount);
        if (selected.isEmpty()) {
            return;
        }
        this.playSound3d(world, SOUND_SEAL_MASTER_CAST, this.enemyWorldPosition(context, enemy));
        int startWave = Math.max(
            context.activeWaveNumber > 0 ? context.activeWaveNumber + 1 : context.state.currentWave + 1,
            1
        );
        for (SealCurseType type : selected) {
            context.activeSealCurses.removeIf(existing -> existing != null && existing.type == type);
            context.activeSealCurses.add(new SealCurseState(type, startWave, DUO_SEAL_MASTER_CURSE_WAVES));
        }
        String appliedText = this.choose(
            world,
            "Мастер Печати оставил " + curseCount + " проклят" + (curseCount == 1 ? "ие" : curseCount <= 4 ? "ия" : "ий") + " на следующие 5 волн.",
            "The Seal Master left " + curseCount + " curse" + (curseCount == 1 ? "" : "s") + " for the next 5 waves."
        );
        this.showEventToast(context, appliedText, EVENT_TOAST_ICON_ALERT);
        this.queueWorldUiSound(context, SOUND_CURSE_UI);
        this.sendLocalizedWorldMessage(world, viewerRef -> {
            List<String> localizedLabels = new ArrayList<>();
            for (SealCurseType type : selected) {
                localizedLabels.add(this.sealCurseDisplayName(viewerRef, type) + " (" + DUO_SEAL_MASTER_CURSE_WAVES + ")");
            }
            String localizedSummary = String.join(", ", localizedLabels);
            String localizedAppliedText = this.choose(
                viewerRef,
                "Мастер Печати оставил " + curseCount + " проклят" + (curseCount == 1 ? "ие" : curseCount <= 4 ? "ия" : "ий") + " на следующие 5 волн.",
                "The Seal Master left " + curseCount + " curse" + (curseCount == 1 ? "" : "s") + " for the next 5 waves."
            );
            return localizedAppliedText + " " + localizedSummary + ".";
        });
    }

    private List<SealCurseType> pickSealCurseTypes(int count) {
        if (count <= 0) {
            return List.of();
        }
        List<SealCurseType> pool = new ArrayList<>();
        Collections.addAll(pool, SealCurseType.values());
        Collections.shuffle(pool, ThreadLocalRandom.current());
        return new ArrayList<>(pool.subList(0, Math.min(count, pool.size())));
    }

    private void advanceSealCursesAfterWave(World world, MatchContext context, int completedWaveNumber) {
        if (world == null || context == null || context.activeSealCurses.isEmpty()) {
            return;
        }
        boolean removedAny = false;
        List<SealCurseState> remaining = new ArrayList<>();
        for (SealCurseState curse : context.activeSealCurses) {
            if (curse == null) {
                continue;
            }
            if (curse.startWave <= completedWaveNumber) {
                curse.remainingWaves = Math.max(0, curse.remainingWaves - 1);
            }
            if (curse.remainingWaves > 0) {
                remaining.add(curse);
            } else {
                removedAny = true;
            }
        }
        context.activeSealCurses.clear();
        context.activeSealCurses.addAll(remaining);
        if (removedAny && context.activeSealCurses.isEmpty()) {
            String message = this.choose(world, "Проклятия Мастера Печати рассеялись.", "The Seal Master's curses faded.");
            this.showEventToast(context, message, EVENT_TOAST_ICON_ALERT);
            this.sendLocalizedWorldMessage(world, "Проклятия Мастера Печати рассеялись.", "The Seal Master's curses faded.");
        }
    }

    private double initialContractEffectCooldown(MatchContext context) {
        if (this.isContractActive(context, "necrotic_mist")) {
            return 6.5;
        }
        return 0.0;
    }

    private void addGeneratedSpawn(
        WaveDefinition generated,
        String laneId,
        String enemyId,
        int count,
        double intervalSeconds,
        double startDelaySeconds
    ) {
        if (generated == null || enemyId == null || enemyId.isBlank() || count <= 0) {
            return;
        }
        WaveSpawn spawn = new WaveSpawn();
        spawn.enemyId = enemyId;
        spawn.laneId = canonicalLaneId(laneId);
        spawn.count = Math.max(1, count);
        spawn.intervalSeconds = Math.max(0.0, intervalSeconds);
        spawn.startDelaySeconds = Math.max(0.0, startDelaySeconds);
        generated.spawns.add(spawn);
    }

    private String pickUnlockedEnemy(MatchContext context, Random random, List<String> pool) {
        if (pool == null || pool.isEmpty()) {
            return null;
        }
        List<String> available = new ArrayList<>();
        for (String enemyId : pool) {
            if (enemyId != null && context.enemyById.containsKey(enemyId)) {
                available.add(enemyId);
            }
        }
        if (available.isEmpty()) {
            return null;
        }
        double totalWeight = 0.0;
        for (String enemyId : available) {
            totalWeight += this.contractEnemySelectionWeight(context, enemyId);
        }
        if (totalWeight <= 0.0) {
            return available.get(random.nextInt(available.size()));
        }
        double roll = random.nextDouble() * totalWeight;
        double cursor = 0.0;
        for (String enemyId : available) {
            cursor += this.contractEnemySelectionWeight(context, enemyId);
            if (roll <= cursor) {
                return enemyId;
            }
        }
        return available.get(available.size() - 1);
    }

    private double contractEnemySelectionWeight(MatchContext context, String enemyId) {
        if (enemyId == null || context == null || context.activeContract == null) {
            return 1.0;
        }
        return switch (context.activeContract.id) {
            case "necrotic_mist" -> switch (enemyId) {
                case "necro_thief" -> 3.4;
                case "vault_priest" -> 2.8;
                case "plague_standard" -> 2.5;
                case "curse_weaver" -> 2.3;
                case "bone_trumpeter" -> 1.7;
                case "jammer" -> 1.4;
                case "runner_bomber" -> 0.75;
                case "elite_robber" -> 0.80;
                default -> 1.0;
            };
            default -> 1.0;
        };
    }

    private List<String> unlockedMediumEnemies(int waveNumber) {
        List<String> pool = new ArrayList<>();
        if (waveNumber >= 5) {
            pool.add("runner_bomber");
        }
        if (waveNumber >= 7) {
            pool.add("bone_guard");
        }
        if (waveNumber >= 9) {
            pool.add("berserker_rotter");
        }
        if (waveNumber >= 14) {
            pool.add("elite_robber");
        }
        return pool;
    }

    private List<String> unlockedMinibossEnemies(int waveNumber) {
        List<String> pool = new ArrayList<>();
        if (waveNumber >= 9) {
            pool.add("jammer");
        }
        if (waveNumber >= 12) {
            pool.add("necro_thief");
        }
        if (waveNumber >= 13) {
            pool.add("vault_priest");
        }
        if (waveNumber >= 15) {
            pool.add("bone_trumpeter");
        }
        if (waveNumber >= 17) {
            pool.add("plague_standard");
        }
        if (waveNumber >= 19) {
            pool.add("curse_weaver");
        }
        return pool;
    }

    private List<String> bossEnemyPool(MatchContext context) {
        if (this.isDuoMode(context)) {
            return List.of(ENEMY_RIFT_TWIN_ALPHA, ENEMY_NODE_ARBITER, ENEMY_SEAL_MASTER);
        }
        return List.of("vault_breaker", "necro_king", "goblin_bomber_boss");
    }

    private boolean showEnemyNameplate(String enemyId, boolean bossEnemy) {
        return bossEnemy
            || "jammer".equals(enemyId)
            || "necro_thief".equals(enemyId)
            || "vault_priest".equals(enemyId)
            || "bone_trumpeter".equals(enemyId)
            || "plague_standard".equals(enemyId)
            || "curse_weaver".equals(enemyId)
            || ENEMY_GOBLIN_SABOTEUR.equals(enemyId)
            || ENEMY_NECRO_GUARDIAN.equals(enemyId);
    }

    private String fallbackBossEnemyId(MatchContext context) {
        for (String enemyId : this.bossEnemyPool(context)) {
            if (context.enemyById.containsKey(enemyId)) {
                return enemyId;
            }
        }
        for (EnemyDefinition enemy : context.snapshot.enemies.enemies) {
            if (enemy != null && enemy.bossEnemy && enemy.id != null && !enemy.id.isBlank()) {
                return enemy.id;
            }
        }
        return null;
    }

    private double spawnIntervalForEnemy(String enemyId, int waveNumber) {
        return switch (enemyId) {
            case "runner" -> Math.max(0.34, 0.84 - waveNumber * 0.011);
            case "bruiser" -> Math.max(1.18, 2.05 - waveNumber * 0.018);
            case "runner_bomber" -> Math.max(0.68, 1.08 - waveNumber * 0.010);
            case "bone_guard" -> Math.max(0.88, 1.45 - waveNumber * 0.012);
            case "berserker_rotter" -> Math.max(0.62, 1.02 - waveNumber * 0.010);
            case "elite_robber" -> Math.max(0.82, 1.46 - waveNumber * 0.010);
            default -> Math.max(0.40, 0.98 - waveNumber * 0.012);
        };
    }

    private ScheduledSpawn createScheduledSpawn(MatchContext context, EnemyDefinition enemy, WaveSpawn spawn, int waveNumber) {
        int highestConfiguredWave = this.highestConfiguredWave(context);
        int pressureWave = this.pressureWaveNumber(context, waveNumber);
        int endlessOffset = context.snapshot.gameRules.endlessMode ? Math.max(0, waveNumber - highestConfiguredWave) : 0;
        double hpScale = this.endlessMultiplier(context.snapshot.gameRules.endlessEnemyHpScalePerWave, endlessOffset)
            * this.waveEnemyHpMultiplier(context, waveNumber, enemy);
        double rewardScale = this.endlessMultiplier(context.snapshot.gameRules.endlessEnemyRewardScalePerWave, endlessOffset);
        double speedScale = this.waveEnemySpeedMultiplier(context, waveNumber, enemy)
            * this.contractEnemySpeedMultiplier(context, enemy)
            * (context.difficulty == null ? 1.0 : Math.max(0.1, context.difficulty.enemySpeedMultiplier));
        speedScale *= this.sealCurseEnemySpeedMultiplier(context);
        double flatDamageReduction = Math.max(0.0, enemy.flatDamageReduction + this.waveEnemyFlatDamageReduction(context, waveNumber, enemy));
        double damageReductionPercent = Math.max(0.0, Math.min(0.90, enemy.damageReductionPercent + this.waveEnemyDamageReductionPercent(context, waveNumber, enemy)));
        double slowResistancePercent = enemy.slowImmune
            ? 1.0
            : Math.max(0.0, Math.min(0.95, enemy.slowResistancePercent + this.waveEnemySlowResistance(context, waveNumber, enemy)));
        if (enemy.bossEnemy) {
            double defenseScale = this.bossDefenseScale(context, pressureWave, enemy.id);
            flatDamageReduction *= defenseScale;
            damageReductionPercent *= defenseScale;
            slowResistancePercent = enemy.slowImmune
                ? 1.0
                : Math.max(0.0, Math.min(0.95, slowResistancePercent * Math.min(1.05, defenseScale + 0.10)));
        }
        ContractDefinition activeContract = context.activeContract;
        if (activeContract != null) {
            hpScale *= Math.max(0.1, activeContract.enemyHpMultiplier);
            rewardScale *= Math.max(0.1, activeContract.matchIncomeMultiplier);
        }
        if (context.difficulty != null) {
            hpScale *= Math.max(0.1, context.difficulty.enemyHpMultiplier);
            rewardScale *= Math.max(0.1, context.difficulty.rewardMultiplier);
        }
        double maxHp = Math.max(1.0, enemy.maxHp * hpScale);
        double speed = Math.max(0.05, enemy.speed * speedScale);
        int reward = Math.max(enemy.reward, (int)Math.round(enemy.reward * rewardScale));
        if (context.tutorialActive) {
            if (waveNumber == 1) {
                maxHp = Math.min(maxHp, 30.0);
                speed = Math.min(speed, 0.38);
                reward = Math.min(reward, 2);
                flatDamageReduction = 0.0;
                damageReductionPercent = 0.0;
                slowResistancePercent = 0.0;
            } else if (waveNumber == 2) {
                maxHp = Math.min(maxHp, 42.0);
                speed = Math.min(speed, 0.48);
                reward = Math.min(reward, 3);
                flatDamageReduction = 0.0;
                damageReductionPercent = 0.0;
                slowResistancePercent = 0.0;
            } else if ("vault_breaker".equals(enemy.id)) {
                maxHp = TUTORIAL_VAULT_BREAKER_HP;
                flatDamageReduction = Math.min(flatDamageReduction, 2.0);
                damageReductionPercent = Math.min(damageReductionPercent, 0.08);
                slowResistancePercent = Math.min(slowResistancePercent, 0.25);
            }
        }
        return new ScheduledSpawn(
            enemy,
            maxHp,
            speed,
            reward,
            enemy.leakDamage,
            waveNumber,
            flatDamageReduction,
            damageReductionPercent,
            slowResistancePercent,
            spawn.startDelaySeconds,
            spawn.laneId
        );
    }

    private int activeLaneCount(WaveDefinition wave) {
        if (wave == null || wave.spawns.isEmpty()) {
            return 1;
        }
        return this.activeLaneCount(wave.spawns);
    }

    private int activeLaneCount(List<WaveSpawn> spawns) {
        if (spawns == null || spawns.isEmpty()) {
            return 1;
        }
        Set<String> laneIds = new HashSet<>();
        for (WaveSpawn spawn : spawns) {
            laneIds.add(canonicalLaneId(spawn.laneId));
        }
        return Math.max(1, laneIds.size());
    }

    private int activeLaneCount(MatchContext context, WaveDefinition wave) {
        return this.activeLaneCount(this.effectiveWaveSpawns(context, wave));
    }

    private List<WaveSpawn> effectiveWaveSpawns(MatchContext context, WaveDefinition wave) {
        if (context == null || wave == null || wave.spawns == null || wave.spawns.isEmpty() || !this.isDuoMode(context)) {
            return wave == null || wave.spawns == null ? List.of() : wave.spawns;
        }
        if (this.isDuoSpecialBossWave(wave)) {
            return this.mirrorDuoWaveSpawns(context, wave.spawns, true);
        }
        return this.mirrorDuoWaveSpawns(context, wave.spawns, false);
    }

    private List<WaveSpawn> mirrorDuoWaveSpawns(MatchContext context, List<WaveSpawn> spawns, boolean preserveBossLanes) {
        if (context == null || spawns == null || spawns.isEmpty()) {
            return spawns == null ? List.of() : spawns;
        }
        boolean hasBlueLane = this.hasLaneRoute(context, "a");
        boolean hasGreenLane = this.hasLaneRoute(context, "c");
        if (!hasBlueLane || !hasGreenLane) {
            return spawns;
        }
        boolean containsBlue = false;
        boolean containsGreen = false;
        for (WaveSpawn spawn : spawns) {
            String laneId = canonicalLaneId(spawn.laneId);
            if ("a".equals(laneId)) {
                containsBlue = true;
            } else if ("c".equals(laneId)) {
                containsGreen = true;
            }
        }
        if (containsBlue && containsGreen) {
            return spawns;
        }
        List<WaveSpawn> mirrored = new ArrayList<>();
        for (WaveSpawn spawn : spawns) {
            String laneId = canonicalLaneId(spawn.laneId);
            EnemyDefinition enemy = context.enemyById.get(spawn.enemyId);
            boolean preserveSingleLane = preserveBossLanes && enemy != null && enemy.bossEnemy;
            if ("b".equals(laneId) && !preserveSingleLane) {
                mirrored.add(this.copyWaveSpawnWithLane(spawn, "a"));
                mirrored.add(this.copyWaveSpawnWithLane(spawn, "c"));
                continue;
            }
            mirrored.add(this.copyWaveSpawnWithLane(spawn, laneId));
            if (preserveSingleLane) {
                continue;
            }
            if ("a".equals(laneId) && !containsGreen) {
                mirrored.add(this.copyWaveSpawnWithLane(spawn, "c"));
            } else if ("c".equals(laneId) && !containsBlue) {
                mirrored.add(this.copyWaveSpawnWithLane(spawn, "a"));
            }
        }
        return mirrored;
    }

    private boolean isDuoSpecialBossWave(WaveDefinition wave) {
        if (wave == null || wave.spawns == null) {
            return false;
        }
        for (WaveSpawn spawn : wave.spawns) {
            if (spawn == null || spawn.enemyId == null) {
                continue;
            }
            if (ENEMY_RIFT_TWIN_ALPHA.equals(spawn.enemyId)
                || ENEMY_RIFT_TWIN_BETA.equals(spawn.enemyId)
                || ENEMY_NODE_ARBITER.equals(spawn.enemyId)
                || ENEMY_SEAL_MASTER.equals(spawn.enemyId)) {
                return true;
            }
        }
        return false;
    }

    private boolean hasLaneRoute(MatchContext context, String laneId) {
        if (context == null) {
            return false;
        }
        List<Vec3i> route = context.routesByLane.get(canonicalLaneId(laneId));
        Double routeLength = context.routeLengthByLane.get(canonicalLaneId(laneId));
        return route != null && route.size() >= 2 && routeLength != null && routeLength.doubleValue() > 0.0;
    }

    private WaveSpawn copyWaveSpawnWithLane(WaveSpawn source, String laneId) {
        WaveSpawn copy = new WaveSpawn();
        copy.enemyId = source.enemyId;
        copy.count = source.count;
        copy.intervalSeconds = source.intervalSeconds;
        copy.startDelaySeconds = source.startDelaySeconds;
        copy.laneId = canonicalLaneId(laneId);
        return copy;
    }

    private int adjustedSpawnCount(MatchContext context, WaveSpawn spawn, EnemyDefinition enemy, int waveNumber, int activeLaneCount) {
        if (enemy != null && ENEMY_GOBLIN_SABOTEUR.equals(enemy.id)) {
            return 1;
        }
        double multiplier = 1.0;
        multiplier *= this.waveEnemyCountMultiplier(context, waveNumber, enemy);
        multiplier *= this.contractEnemyCountMultiplier(context, enemy);
        if (context.difficulty != null) {
            multiplier *= Math.max(0.1, context.difficulty.enemyCountMultiplier);
        }
        if (context.activeContract != null) {
            multiplier *= Math.max(0.1, context.activeContract.enemyCountMultiplier);
            if (activeLaneCount > 1) {
                multiplier *= Math.max(1.0, context.activeContract.multiLanePressureMultiplier);
            }
        }
        return Math.max(1, (int)Math.round(spawn.count * multiplier));
    }

    private double waveEnemyCountMultiplier(MatchContext context, int waveNumber, EnemyDefinition enemy) {
        int normalizedWave = this.pressureWaveNumber(context, waveNumber);
        double multiplier = 1.0
            + Math.max(0, normalizedWave - 1) * 0.012
            + Math.max(0, normalizedWave - 10) * 0.016
            + Math.max(0, normalizedWave - 20) * 0.020
            + Math.max(0, normalizedWave - 30) * 0.024;
        if (enemy != null) {
            if (enemy.specialEnemy) {
                multiplier *= 0.90;
            }
            if (this.isHeavyEnemy(enemy.id)) {
                multiplier *= 0.88;
            }
            if ("thief".equals(enemy.id) || "runner".equals(enemy.id) || "grave_spawn".equals(enemy.id)) {
                multiplier *= 1.08;
            }
        }
        return Math.max(1.0, Math.min(2.6, multiplier));
    }

    private double waveEnemyHpMultiplier(MatchContext context, int waveNumber, EnemyDefinition enemy) {
        int normalizedWave = this.pressureWaveNumber(context, waveNumber);
        double multiplier = 1.0
            + Math.max(0, normalizedWave - 1) * 0.040
            + Math.max(0, normalizedWave - 10) * 0.015
            + Math.max(0, normalizedWave - 20) * 0.020
            + Math.max(0, normalizedWave - 30) * 0.024;
        if (enemy != null) {
            if (enemy.specialEnemy) {
                multiplier *= 1.06;
            }
            if (this.isHeavyEnemy(enemy.id)) {
                multiplier *= this.heavyHpRoleMultiplier(context, normalizedWave, enemy.id);
            }
            if (enemy.bossEnemy) {
                multiplier *= this.bossHpRoleMultiplier(context, normalizedWave, enemy.id);
            }
            if (ENEMY_GOBLIN_SABOTEUR.equals(enemy.id)) {
                multiplier *= 1.08;
            }
            if (ENEMY_NECRO_GUARDIAN.equals(enemy.id)) {
                multiplier *= switch (context == null || context.difficulty == null ? DIFFICULTY_NORMAL : context.difficulty.id) {
                    case DIFFICULTY_EASY -> 0.70;
                    case DIFFICULTY_HARD -> 0.92;
                    case DIFFICULTY_NIGHTMARE -> 1.00;
                    default -> 0.80;
                };
            }
        }
        return Math.max(0.72, multiplier);
    }

    private double waveEnemySpeedMultiplier(MatchContext context, int waveNumber, EnemyDefinition enemy) {
        int normalizedWave = this.pressureWaveNumber(context, waveNumber);
        String difficultyId = context == null || context.difficulty == null ? DIFFICULTY_NORMAL : context.difficulty.id;
        double multiplier = 1.0
            + Math.max(0, normalizedWave - 1) * 0.0015
            + Math.max(0, normalizedWave - 10) * 0.0025
            + Math.max(0, normalizedWave - 20) * 0.0030
            + Math.max(0, normalizedWave - 30) * 0.0035;
        if (enemy != null) {
            if ("runner".equals(enemy.id) || "runner_bomber".equals(enemy.id)) {
                multiplier += 0.03;
            }
            if (ENEMY_GOBLIN_SABOTEUR.equals(enemy.id) || "goblin_bomber_boss".equals(enemy.id)) {
                multiplier += 0.02;
            }
            if ("grave_spawn".equals(enemy.id)) {
                multiplier += 0.01;
            }
            if (this.isHeavyEnemy(enemy.id) && !enemy.bossEnemy) {
                double heavyCap = switch (difficultyId) {
                    case DIFFICULTY_EASY -> 1.04;
                    case DIFFICULTY_HARD -> 1.16;
                    case DIFFICULTY_NIGHTMARE -> 1.22;
                    default -> 1.10;
                };
                multiplier = Math.min(multiplier, heavyCap);
            }
            if (enemy.bossEnemy) {
                double bossCap = "goblin_bomber_boss".equals(enemy.id)
                    ? switch (difficultyId) {
                        case DIFFICULTY_EASY -> 1.04;
                        case DIFFICULTY_HARD -> 1.16;
                        case DIFFICULTY_NIGHTMARE -> 1.22;
                        default -> 1.10;
                    }
                    : switch (difficultyId) {
                        case DIFFICULTY_EASY -> 1.02;
                        case DIFFICULTY_HARD -> 1.14;
                        case DIFFICULTY_NIGHTMARE -> 1.20;
                        default -> 1.08;
                    };
                multiplier = Math.min(multiplier, bossCap);
            }
        }
        double globalCap = switch (difficultyId) {
            case DIFFICULTY_EASY -> 1.16;
            case DIFFICULTY_HARD -> 1.30;
            case DIFFICULTY_NIGHTMARE -> 1.40;
            default -> 1.24;
        };
        return Math.max(1.0, Math.min(1.50, Math.min(globalCap, multiplier)));
    }

    private boolean isNormalDifficulty(MatchContext context) {
        return context != null
            && context.difficulty != null
            && DIFFICULTY_NORMAL.equals(context.difficulty.id);
    }

    private double heavyHpRoleMultiplier(MatchContext context, int pressureWave, String enemyId) {
        String difficultyId = context == null || context.difficulty == null ? DIFFICULTY_NORMAL : context.difficulty.id;
        double difficultyBase = switch (difficultyId) {
            case DIFFICULTY_EASY -> 0.90 + Math.min(0.10, Math.max(0, pressureWave - 18) * 0.005);
            case DIFFICULTY_HARD -> 1.08 + Math.min(0.16, Math.max(0, pressureWave - 14) * 0.007);
            case DIFFICULTY_NIGHTMARE -> 1.18 + Math.min(0.20, Math.max(0, pressureWave - 12) * 0.008);
            default -> 0.98 + Math.min(0.12, Math.max(0, pressureWave - 16) * 0.006);
        };
        double typeBase = switch (enemyId) {
            case "bone_guard" -> 0.92;
            case "elite_robber" -> 0.96;
            case ENEMY_GOBLIN_SABOTEUR -> 1.08;
            case ENEMY_NECRO_GUARDIAN -> switch (difficultyId) {
                case DIFFICULTY_EASY -> 0.34;
                case DIFFICULTY_HARD -> 0.62;
                case DIFFICULTY_NIGHTMARE -> 0.82;
                default -> 0.46;
            };
            default -> 1.0;
        };
        return Math.max(0.80, difficultyBase * typeBase);
    }

    private double bossHpRoleMultiplier(MatchContext context, int pressureWave, String enemyId) {
        String difficultyId = context == null || context.difficulty == null ? DIFFICULTY_NORMAL : context.difficulty.id;
        double difficultyBase = switch (difficultyId) {
            case DIFFICULTY_EASY -> 0.48 + Math.min(0.24, Math.max(0, pressureWave - 12) * 0.010);
            case DIFFICULTY_HARD -> 0.82 + Math.min(0.28, Math.max(0, pressureWave - 10) * 0.012);
            case DIFFICULTY_NIGHTMARE -> 1.02 + Math.min(0.34, Math.max(0, pressureWave - 8) * 0.013);
            default -> 0.62 + Math.min(0.26, Math.max(0, pressureWave - 12) * 0.011);
        };
        double typeBase = switch (enemyId) {
            case "vault_breaker" -> 0.90;
            case "goblin_bomber_boss" -> 1.04;
            default -> 1.0;
        };
        return Math.max(0.42, difficultyBase * typeBase);
    }

    private double bossDefenseScale(MatchContext context, int pressureWave, String enemyId) {
        String difficultyId = context == null || context.difficulty == null ? DIFFICULTY_NORMAL : context.difficulty.id;
        double difficultyBase = switch (difficultyId) {
            case DIFFICULTY_EASY -> 0.55 + Math.min(0.25, Math.max(0, pressureWave - 14) * 0.010);
            case DIFFICULTY_HARD -> 0.92 + Math.min(0.22, Math.max(0, pressureWave - 12) * 0.010);
            case DIFFICULTY_NIGHTMARE -> 1.08 + Math.min(0.24, Math.max(0, pressureWave - 10) * 0.010);
            default -> 0.72 + Math.min(0.28, Math.max(0, pressureWave - 14) * 0.011);
        };
        double typeBase = switch (enemyId) {
            case "vault_breaker" -> 0.92;
            case "goblin_bomber_boss" -> 1.04;
            default -> 1.0;
        };
        return Math.max(0.45, difficultyBase * typeBase);
    }

    private double normalHeavyHpRoleMultiplier(int normalizedWave, EnemyDefinition enemy) {
        if (enemy == null || !this.isHeavyEnemy(enemy.id)) {
            return 1.0;
        }
        double bonus = 1.04 + Math.min(0.12, Math.max(0, normalizedWave - 14) * 0.006);
        return Math.max(1.0, bonus);
    }

    private double normalBossHpRoleMultiplier(int normalizedWave, EnemyDefinition enemy) {
        if (enemy == null || !enemy.bossEnemy) {
            return 1.0;
        }
        double bonus = 1.08 + Math.min(0.30, Math.max(0, normalizedWave - 10) * 0.015);
        return Math.max(1.0, bonus) * this.normalBossSpecificHpTuningMultiplier(normalizedWave, enemy.id);
    }

    private double normalEarlyHpProfileMultiplier(int normalizedWave, String enemyId) {
        if (enemyId == null || enemyId.isBlank()) {
            return 1.0;
        }
        double earlyRatio = switch (enemyId) {
            case "bruiser" -> 540.0 / 620.0;
            case "bone_guard" -> 285.0 / 330.0;
            case "elite_robber" -> 430.0 / 500.0;
            case ENEMY_GOBLIN_SABOTEUR -> 740.0 / 880.0;
            default -> 1.0;
        };
        if (earlyRatio >= 0.999) {
            return 1.0;
        }
        double progressWindow = 24.0;
        double progress = Math.min(1.0, Math.max(0.0, (normalizedWave - 1) / progressWindow));
        return earlyRatio + (1.0 - earlyRatio) * progress;
    }

    private double normalBossDefenseTuningMultiplier(int normalizedWave, String enemyId) {
        if (enemyId == null || enemyId.isBlank()) {
            return 1.0;
        }
        double earlyRatio = switch (enemyId) {
            case "vault_breaker" -> 0.36;
            case "necro_king" -> 0.42;
            case "goblin_bomber_boss" -> 0.50;
            default -> 1.0;
        };
        if (earlyRatio >= 0.999) {
            return 1.0;
        }
        double progress = Math.min(1.0, Math.max(0.0, (normalizedWave - 1) / 28.0));
        return earlyRatio + (1.0 - earlyRatio) * progress;
    }

    private double normalBossSpecificHpTuningMultiplier(int normalizedWave, String enemyId) {
        if (enemyId == null || enemyId.isBlank()) {
            return 1.0;
        }
        double earlyRatio = switch (enemyId) {
            case "vault_breaker" -> 0.18;
            case "necro_king" -> 0.22;
            case "goblin_bomber_boss" -> 0.28;
            default -> 1.0;
        };
        if (earlyRatio >= 0.999) {
            return 1.0;
        }
        double progress = Math.min(1.0, Math.max(0.0, (normalizedWave - 1) / 32.0));
        return earlyRatio + (1.0 - earlyRatio) * progress;
    }

    private double waveEnemyFlatDamageReduction(MatchContext context, int waveNumber, EnemyDefinition enemy) {
        int normalizedWave = this.pressureWaveNumber(context, waveNumber);
        String difficultyId = context == null || context.difficulty == null ? DIFFICULTY_NORMAL : context.difficulty.id;
        if (enemy != null && ENEMY_NECRO_GUARDIAN.equals(enemy.id)) {
            double guardianBonus = Math.max(0, normalizedWave - 18) * 0.06;
            return switch (difficultyId) {
                case DIFFICULTY_EASY -> Math.min(1.2, guardianBonus);
                case DIFFICULTY_HARD -> Math.min(4.8, 1.2 + guardianBonus * 1.4);
                case DIFFICULTY_NIGHTMARE -> Math.min(7.2, 2.0 + guardianBonus * 1.8);
                default -> Math.min(2.8, 0.5 + guardianBonus);
            };
        }
        double bonus = Math.max(0, normalizedWave - 10) * 0.18;
        if (enemy != null) {
            if (this.isHeavyEnemy(enemy.id)) {
                bonus += Math.max(0, normalizedWave - 14) * 0.14;
            }
            if (enemy.specialEnemy) {
                bonus += Math.max(0, normalizedWave - 16) * 0.10;
            }
            if (enemy.bossEnemy) {
                bonus += 1.5 + normalizedWave * 0.10;
            }
            if (ENEMY_GOBLIN_SABOTEUR.equals(enemy.id)) {
                bonus += 1.0 + normalizedWave * 0.08;
            }
        }
        return Math.max(0.0, bonus);
    }

    private double waveEnemyDamageReductionPercent(MatchContext context, int waveNumber, EnemyDefinition enemy) {
        int normalizedWave = this.pressureWaveNumber(context, waveNumber);
        String difficultyId = context == null || context.difficulty == null ? DIFFICULTY_NORMAL : context.difficulty.id;
        if (enemy != null && ENEMY_NECRO_GUARDIAN.equals(enemy.id)) {
            double guardianBonus = normalizedWave >= 20 ? Math.min(0.04, (normalizedWave - 19) * 0.003) : 0.0;
            return switch (difficultyId) {
                case DIFFICULTY_EASY -> Math.min(0.05, 0.00 + guardianBonus);
                case DIFFICULTY_HARD -> Math.min(0.14, 0.05 + guardianBonus * 1.35);
                case DIFFICULTY_NIGHTMARE -> Math.min(0.22, 0.08 + guardianBonus * 1.8);
                default -> Math.min(0.08, 0.02 + guardianBonus);
            };
        }
        double bonus = 0.0;
        if (normalizedWave >= 14) {
            bonus += Math.min(0.08, (normalizedWave - 13) * 0.005);
        }
        if (enemy != null) {
            if (this.isHeavyEnemy(enemy.id)) {
                bonus += 0.02;
            }
            if (enemy.specialEnemy) {
                bonus += 0.015;
            }
            if (enemy.bossEnemy) {
                bonus += 0.035;
            }
            if (ENEMY_GOBLIN_SABOTEUR.equals(enemy.id)) {
                bonus += 0.03;
            }
        }
        return Math.max(0.0, Math.min(0.72, bonus));
    }

    private double waveEnemySlowResistance(MatchContext context, int waveNumber, EnemyDefinition enemy) {
        int normalizedWave = this.pressureWaveNumber(context, waveNumber);
        String difficultyId = context == null || context.difficulty == null ? DIFFICULTY_NORMAL : context.difficulty.id;
        if (enemy != null && ENEMY_NECRO_GUARDIAN.equals(enemy.id)) {
            double guardianBonus = 0.0;
            if (normalizedWave >= 18) {
                guardianBonus += Math.min(0.10, (normalizedWave - 17) * 0.008);
            }
            return switch (difficultyId) {
                case DIFFICULTY_EASY -> Math.min(0.22, 0.06 + guardianBonus * 0.7);
                case DIFFICULTY_HARD -> Math.min(0.46, 0.18 + guardianBonus * 1.2);
                case DIFFICULTY_NIGHTMARE -> Math.min(0.62, 0.24 + guardianBonus * 1.5);
                default -> Math.min(0.32, 0.10 + guardianBonus);
            };
        }
        double bonus = 0.0;
        if (normalizedWave >= 10) {
            bonus += Math.min(0.12, (normalizedWave - 9) * 0.010);
        }
        if (normalizedWave >= 20) {
            bonus += Math.min(0.08, (normalizedWave - 19) * 0.008);
        }
        if (enemy != null) {
            if (this.isHeavyEnemy(enemy.id)) {
                bonus += 0.04;
            }
            if (enemy.specialEnemy) {
                bonus += 0.03;
            }
            if (enemy.bossEnemy) {
                bonus += 0.05;
            }
            if ("runner_bomber".equals(enemy.id) || ENEMY_GOBLIN_SABOTEUR.equals(enemy.id)) {
                bonus += 0.05;
            }
        }
        return Math.max(0.0, Math.min(0.90, bonus));
    }

    private double endlessMultiplier(double scalePerWave, int endlessOffset) {
        if (endlessOffset <= 0 || scalePerWave <= 0.0) {
            return 1.0;
        }
        return Math.pow(1.0 + scalePerWave, endlessOffset);
    }

    @SuppressWarnings("unused")
    private double endlessSpawnIntervalMultiplier(GameRules gameRules, int cycleCount) {
        if (gameRules == null || cycleCount <= 0 || gameRules.endlessSpawnIntervalScalePerCycle <= 0.0) {
            return 1.0;
        }
        double clampedScale = Math.min(0.8, Math.max(0.0, gameRules.endlessSpawnIntervalScalePerCycle));
        return Math.pow(1.0 - clampedScale, cycleCount);
    }

    private int highestConfiguredWave(MatchContext context) {
        int highest = 0;
        for (Integer waveIndex : context.waveByIndex.keySet()) {
            if (waveIndex != null && waveIndex.intValue() > highest) {
                highest = waveIndex.intValue();
            }
        }
        return Math.max(highest, Math.max(1, context.snapshot.gameRules.totalWaves));
    }

    public ActionResult buildTower(World world, Vec3i playerPosition, String towerId, int radius) throws IOException {
        MatchContext context = this.getOrCreateContext(world);
        if (!context.state.gameStarted && !this.tutorialAllowsPrematchBuild(world, context)) {
            return ActionResult.fail(this.choose(world, "Сначала выберите сложность и контракт у оператора.", "Choose a difficulty and contract with the operator first."));
        }
        TowerDefinition tower = context.towerById.get(towerId);
        if (tower == null) {
            return ActionResult.fail(this.choose(world, "Неизвестная башня: '", "Unknown tower: '") + towerId + this.choose(world, "'. Используй /bankdefense towers.", "'. Use /bankdefense towers."));
        }
        if (!BankDefenseMatchPhaseSupport.canBuild(context.state, context.snapshot.gameRules.allowBuildDuringWave)) {
            return ActionResult.fail(this.choose(world, "Строить можно только в фазу подготовки.", "Building is only allowed during preparation."));
        }
        BuildSlot slot = this.findNearestAvailableSlot(context, playerPosition, radius);
        if (slot == null) {
            return ActionResult.fail(this.choose(world, "Рядом нет свободного слота строительства в радиусе ", "No free build slot found within radius ") + radius + ".");
        }
        return this.buildTowerInSlot(world, context, this.primaryPlayerRef(world), slot, tower, SEED_IDOL_MODE_DEFAULT);
    }

    public ActionResult upgradeTower(World world, Vec3i playerPosition, int radius) throws IOException {
        return this.upgradeTower(world, this.primaryPlayerRef(world), playerPosition, radius);
    }

    public ActionResult upgradeTower(World world, PlayerRef actor, Vec3i playerPosition, int radius) throws IOException {
        MatchContext context = this.getOrCreateContext(world);
        TowerInstance instance = this.findNearestPlacedTower(context, playerPosition, radius);
        if (instance == null) {
            return ActionResult.fail(this.choose(actor, "Рядом нет башни для улучшения в радиусе ", "No tower to upgrade found within radius ") + radius + ".");
        }
        if (instance.definition.superTower) {
            if ("seed_idol".equals(instance.definition.id)) {
                return ActionResult.fail(this.choose(actor, "Идол урожая не использует активации.", "The Harvest Idol does not use activations."));
            }
            if (instance.superReady) {
                if ("heart_of_roots".equals(instance.definition.id)) {
                    return this.activateHeartOfRoots(world, context, actor, instance);
                }
                return ActionResult.fail(this.towerDisplayName(actor, instance.definition) + this.choose(actor, " уже активен.", " is already active."));
            }
            return this.reactivateSuperTower(world, context, actor, instance);
        }
        if (!BankDefenseMatchPhaseSupport.canBuild(context.state, context.snapshot.gameRules.allowUpgradeDuringWave)) {
            return ActionResult.fail(this.choose(actor, "Улучшать можно только в фазу подготовки.", "Upgrades are only allowed during preparation."));
        }
        return this.upgradeTowerInstance(world, context, actor, instance);
    }

    public ActionResult activateSuperTowerAt(World world, Vec3i targetPosition, int radius) throws IOException {
        return this.activateSuperTowerAt(world, this.primaryPlayerRef(world), targetPosition, radius);
    }

    public ActionResult activateSuperTowerAt(World world, PlayerRef actor, Vec3i targetPosition, int radius) throws IOException {
        MatchContext context = this.getOrCreateContext(world);
        TowerInstance instance = this.findNearestPlacedTower(context, targetPosition, radius);
        if (instance == null || !instance.definition.superTower) {
            return ActionResult.fail(this.choose(actor, "Рядом нет супер-башни.", "No super tower found nearby."));
        }
        if (instance.slot != null && !this.canPlayerUseSlot(world, context, actor, instance.slot)) {
            return ActionResult.fail(this.deniedSlotAccessMessage(actor, instance.slot));
        }
        if ("heart_of_roots".equals(instance.definition.id)) {
            return this.activateHeartOfRoots(world, context, actor, instance);
        }
        return ActionResult.fail(this.choose(actor, "У этой супер-башни нет ручной активации.", "This super tower does not have a manual activation."));
    }

    public ActionResult reactivateSuperTowerAt(World world, Vec3i targetPosition, int radius) throws IOException {
        return this.reactivateSuperTowerAt(world, this.primaryPlayerRef(world), targetPosition, radius);
    }

    public ActionResult reactivateSuperTowerAt(World world, PlayerRef actor, Vec3i targetPosition, int radius) throws IOException {
        MatchContext context = this.getOrCreateContext(world);
        TowerInstance instance = this.findNearestPlacedTower(context, targetPosition, radius);
        if (instance == null || !instance.definition.superTower) {
            return ActionResult.fail(this.choose(actor, "Рядом нет супер-башни.", "No super tower found nearby."));
        }
        if (instance.slot != null && !this.canPlayerUseSlot(world, context, actor, instance.slot)) {
            return ActionResult.fail(this.deniedSlotAccessMessage(actor, instance.slot));
        }
        return this.reactivateSuperTower(world, context, actor, instance);
    }

    public ActionResult buildOrUpgradeTower(World world, Vec3i targetPosition, String towerId, int buildMode) throws IOException {
        return this.buildOrUpgradeTower(world, this.primaryPlayerRef(world), targetPosition, towerId, buildMode);
    }

    public ActionResult buildOrUpgradeTower(World world, PlayerRef actor, Vec3i targetPosition, String towerId, int buildMode) throws IOException {
        MatchContext context = this.getOrCreateContext(world);
        if (!context.state.gameStarted && !this.tutorialAllowsPrematchBuild(world, context)) {
            return ActionResult.fail(this.choose(actor, "Сначала выберите сложность и контракт у оператора.", "Choose a difficulty and contract with the operator first."));
        }
        TowerDefinition tower = context.towerById.get(towerId);
        if (tower == null) {
            return ActionResult.fail(this.choose(actor, "Неизвестная башня: '", "Unknown tower: '") + towerId + this.choose(actor, "'. Используй /bankdefense towers.", "'. Use /bankdefense towers."));
        }
        BuildSlot slot = this.findNearestSlot(context, targetPosition, 0);
        if (slot == null) {
            return ActionResult.fail(this.choose(actor, "Рядом нет слота строительства.", "No build slot found nearby."));
        }
        TowerInstance existingTower = context.placedTowers.get(slot.id);
        if (existingTower == null) {
            if (!BankDefenseMatchPhaseSupport.canBuild(context.state, context.snapshot.gameRules.allowBuildDuringWave)) {
                return ActionResult.fail(this.choose(actor, "Строить можно только в фазу подготовки.", "Building is only allowed during preparation."));
            }
            return this.buildTowerInSlot(world, context, actor, slot, tower, buildMode);
        }
        if ("seed_idol".equals(existingTower.definition.id)
            && buildMode != SEED_IDOL_MODE_DEFAULT
            && existingTower.specialMode != SEED_IDOL_MODE_DEFAULT
            && existingTower.specialMode != buildMode) {
            return ActionResult.fail(this.choose(actor, "Режим идола выбирается только при установке. Продай его и поставь заново.", "The idol mode can only be chosen when placing it. Sell it and build it again."));
        }
        if (!existingTower.definition.id.equals(tower.id)) {
            return ActionResult.fail(
                this.choose(actor, "На площадке ", "Pad ") + this.slotDisplayName(slot)
                    + this.choose(actor, " уже стоит ", " already contains ")
                    + this.towerDisplayName(actor, existingTower.definition)
                    + this.choose(actor, ". Выберите такую же башню для улучшения или сначала продайте текущую.", ". Choose the same tower to upgrade it, or sell the current one first.")
            );
        }
        if (!BankDefenseMatchPhaseSupport.canBuild(context.state, context.snapshot.gameRules.allowUpgradeDuringWave)) {
            return ActionResult.fail(this.choose(actor, "Улучшать можно только в фазу подготовки.", "Upgrades are only allowed during preparation."));
        }
        return this.upgradeTowerInstance(world, context, actor, existingTower);
    }

    private boolean tutorialAllowsPrematchBuild(World world, MatchContext context) {
        if (world == null || context == null || !context.tutorialActive) {
            return false;
        }
        TutorialState tutorial = this.tutorialState(world);
        return tutorial.stage == TutorialStage.PlaceFirstTower;
    }

    public ActionResult sellTower(World world, Vec3i playerPosition, int radius) throws IOException {
        MatchContext context = this.getOrCreateContext(world);
        TowerInstance instance = this.findNearestPlacedTower(context, playerPosition, radius);
        if (instance == null) {
            return ActionResult.fail(this.choose(world, "Рядом нет башни для продажи в радиусе ", "No tower to sell found within radius ") + radius + ".");
        }
        return this.sellTowerInstance(world, context, this.primaryPlayerRef(world), instance);
    }

    public ActionResult sellTowerAt(World world, Vec3i targetPosition, int radius) throws IOException {
        return this.sellTowerAt(world, this.primaryPlayerRef(world), targetPosition, radius);
    }

    public ActionResult sellTowerAt(World world, PlayerRef actor, Vec3i targetPosition, int radius) throws IOException {
        MatchContext context = this.getOrCreateContext(world);
        TowerInstance instance = this.findNearestPlacedTower(context, targetPosition, radius);
        if (instance == null) {
            return ActionResult.fail(this.choose(actor, "Рядом нет башни для продажи в радиусе ", "No tower to sell found within radius ") + radius + ".");
        }
        return this.sellTowerInstance(world, context, actor, instance);
    }

    public ActionResult equipTowerModule(World world, Vec3i targetPosition, String moduleId, int radius) throws IOException {
        return this.equipTowerModule(world, this.primaryPlayerRef(world), targetPosition, moduleId, radius);
    }

    public ActionResult equipTowerModule(World world, PlayerRef actor, Vec3i targetPosition, String moduleId, int radius) throws IOException {
        MatchContext context = this.getOrCreateContext(world);
        if (!BankDefenseMatchPhaseSupport.isBuildPhase(context.state)) {
            return ActionResult.fail(this.choose(actor, "Менять модули можно только в фазу подготовки.", "Modules can only be changed during preparation."));
        }
        if (context.tutorialActive) {
            TutorialState tutorial = this.tutorialState(world);
            if (tutorial.stage != TutorialStage.FillAllSlots && tutorial.stage != TutorialStage.WaitTwoLaneWaveFinish) {
                return ActionResult.fail(this.choose(actor, "Сейчас Квибек ждёт, пока ты доберёшься до шага с модулем.", "Kweebec is waiting until you reach the module step."));
            }
        }
        TowerInstance instance = this.findNearestPlacedTower(context, targetPosition, radius);
        if (instance == null) {
            return ActionResult.fail(this.choose(actor, "Рядом нет башни для установки модуля в радиусе ", "No tower to install a module into was found within radius ") + radius + ".");
        }
        if (!this.canPlayerUseSlot(world, context, actor, instance.slot)) {
            return ActionResult.fail(this.deniedSlotAccessMessage(actor, instance.slot));
        }
        ModuleDefinition module = context.moduleById.get(moduleId);
        if (module == null) {
            return ActionResult.fail(this.choose(actor, "Неизвестный модуль: '", "Unknown module: '") + moduleId + "'.");
        }
        int available = context.moduleInventory.getOrDefault(moduleId, 0);
        if (available <= 0) {
            return ActionResult.fail(this.choose(actor, "Модуля ", "No copies of ") + this.moduleDisplayName(actor, module) + this.choose(actor, " нет в запасе.", " are available in storage."));
        }
        if (moduleId.equals(instance.equippedModuleId)) {
            return ActionResult.fail(this.moduleDisplayName(actor, module) + this.choose(actor, " уже установлен в этой башне.", " is already installed in this tower."));
        }
        if (instance.equippedModuleId != null) {
            return ActionResult.fail(this.choose(actor, "В этой башне уже установлен модуль. Сначала сними текущий.", "A module is already installed in this tower. Remove it first."));
        }
        this.addModuleToInventory(context, moduleId, -1);
        instance.equippedModuleId = moduleId;
        this.trackLifetimeModuleInstalled(context);
        this.playSound3d(world, SOUND_MODULE_INSTALL, this.towerWorldPosition(instance.slot));
        this.showEventToast(
            context,
            this.choose(world, "Модуль ", "Module ") + this.moduleDisplayName(world, module) + this.choose(world, " установлен в ", " installed in ") + this.towerDisplayName(world, instance.definition) + ".",
            EVENT_TOAST_ICON_CORES
        );
        this.refreshVisualizationIfEnabled(world);
        TutorialState tutorial = this.tutorialState(world);
        if (context.tutorialActive && tutorial.stage == TutorialStage.FillAllSlots) {
            tutorial.moduleEquipped = true;
            this.updateTutorialProgressionFromContext(world, context);
        }
        return ActionResult.ok(this.towerDisplayName(actor, instance.definition) + this.choose(actor, " получил модуль ", " received module ") + this.moduleDisplayName(actor, module) + ".");
    }

    public ActionResult removeTowerModule(World world, Vec3i targetPosition, int radius) throws IOException {
        return this.removeTowerModule(world, this.primaryPlayerRef(world), targetPosition, radius);
    }

    public ActionResult removeTowerModule(World world, PlayerRef actor, Vec3i targetPosition, int radius) throws IOException {
        MatchContext context = this.getOrCreateContext(world);
        if (!BankDefenseMatchPhaseSupport.isBuildPhase(context.state)) {
            return ActionResult.fail(this.choose(actor, "Менять модули можно только в фазу подготовки.", "Modules can only be changed during preparation."));
        }
        TowerInstance instance = this.findNearestPlacedTower(context, targetPosition, radius);
        if (instance == null) {
            return ActionResult.fail(this.choose(actor, "Рядом нет башни для снятия модуля в радиусе ", "No tower to remove a module from was found within radius ") + radius + ".");
        }
        if (!this.canPlayerUseSlot(world, context, actor, instance.slot)) {
            return ActionResult.fail(this.deniedSlotAccessMessage(actor, instance.slot));
        }
        if (instance.equippedModuleId == null) {
            return ActionResult.fail(this.choose(actor, "В этой башне нет установленного модуля.", "This tower has no module installed."));
        }
        ModuleDefinition module = context.moduleById.get(instance.equippedModuleId);
        String displayName = module != null ? this.moduleDisplayName(actor, module) : instance.equippedModuleId;
        this.addModuleToInventory(context, instance.equippedModuleId, 1);
        instance.equippedModuleId = null;
        this.playSound3d(world, SOUND_MODULE_REMOVE, this.towerWorldPosition(instance.slot));
        this.refreshVisualizationIfEnabled(world);
        return ActionResult.ok(this.choose(actor, "Модуль ", "Module ") + displayName + this.choose(actor, " снят и возвращён в запас.", " was removed and returned to storage."));
    }

    public RewardUiState getRewardUiState(World world) throws IOException {
        return this.getRewardUiState(world, this.primaryPlayerRef(world));
    }

    public RewardUiState getRewardUiState(World world, PlayerRef viewerRef) throws IOException {
        MatchContext context = this.getOrCreateContext(world);
        RewardUiState state = new RewardUiState();
        state.currentWave = context.state.currentWave;
        state.currency = this.currencyForPlayer(context, viewerRef);
        state.bankHp = context.state.bankHp;
        state.matchMode = context.matchMode;
        state.viewerTeam = this.teamForPlayer(context, viewerRef);
        state.activePickerTeam = context.rewardPickerTeam;
        state.rewardPending = !context.pendingRewardChoices.isEmpty();
        if (this.isDuoMode(context) && state.rewardPending) {
            if (this.isDuoSoloTestEnabled(context)) {
                state.phaseText = this.choose(viewerRef, "Тестовый Duo: выбранный модуль будет выдан x2.", "Duo test: the selected module will be granted x2.");
            } else {
                String pickerLabel = TEAM_GREEN.equals(context.rewardPickerTeam)
                    ? this.choose(viewerRef, "Зелёный", "Green")
                    : this.choose(viewerRef, "Синий", "Blue");
                state.phaseText = context.rewardSelectionsByTeam.isEmpty()
                    ? this.choose(viewerRef, "Первый выбор: ", "First pick: ") + pickerLabel
                    : this.choose(viewerRef, "Следующий выбор: ", "Next pick: ") + pickerLabel;
            }
            if (context.rewardLastPickedModuleId != null
                && !context.rewardLastPickedModuleId.isBlank()
                && context.rewardLastPickedTeam != null
                && !context.rewardLastPickedTeam.isBlank()
                && !context.rewardLastPickedTeam.equals(state.viewerTeam)) {
                ModuleDefinition picked = context.moduleById.get(context.rewardLastPickedModuleId);
                String pickedName = picked == null ? context.rewardLastPickedModuleId : this.moduleDisplayName(viewerRef, picked);
                state.allyPickText = this.choose(viewerRef, "Союзник выбрал: ", "Ally picked: ") + pickedName;
            }
        }
        for (String moduleId : context.pendingRewardChoices) {
            ModuleDefinition module = context.moduleById.get(moduleId);
            if (module == null) {
                continue;
            }
            ModuleButtonState button = new ModuleButtonState();
            button.moduleId = module.id;
            button.displayName = this.moduleDisplayName(viewerRef, module);
            button.note = this.moduleNote(viewerRef, module);
            button.count = context.moduleInventory.getOrDefault(module.id, 0);
            button.enabled = !this.isDuoMode(context)
                || state.viewerTeam.equals(context.rewardPickerTeam)
                || this.isDuoSoloTestEnabled(context);
            state.choiceButtons.add(button);
        }
        return state;
    }

    public ActionResult ensureRewardChoicesForCommand(World world) throws IOException {
        MatchContext context = this.getOrCreateContext(world);
        if (!context.pendingRewardChoices.isEmpty()) {
            context.state.rewardPending = true;
            return ActionResult.ok(this.choose(world, "Используется текущий выбор модулей.", "Using the current module selection."));
        }
        List<String> pool = new ArrayList<>();
        for (ModuleDefinition module : context.snapshot.modules.modules) {
            if (module != null && module.id != null && !module.id.isBlank()) {
                pool.add(module.id);
            }
        }
        if (pool.isEmpty()) {
            return ActionResult.fail(this.choose(world, "В конфиге не найдено ни одного модуля.", "No modules were found in the config."));
        }
        Collections.shuffle(pool, ThreadLocalRandom.current());
        this.prepareRewardChoices(context, pool, context.state.currentWave);
        if (!context.state.rewardPending) {
            return ActionResult.fail(this.choose(world, "Не удалось подготовить выбор модулей.", "Failed to prepare the module selection."));
        }
        this.playWorldUiSound(world, SOUND_REWARD);
        return ActionResult.ok(this.choose(world, "Создан тестовый выбор модулей.", "A test module selection has been created."));
    }

    public ActionResult claimModuleReward(World world, String moduleId) throws IOException {
        return this.claimModuleReward(world, this.primaryPlayerRef(world), moduleId);
    }

    public ActionResult claimModuleReward(World world, PlayerRef actor, String moduleId) throws IOException {
        MatchContext context = this.getOrCreateContext(world);
        if (context.pendingRewardChoices.isEmpty()) {
            return ActionResult.fail(this.choose(actor, "Сейчас нет награды за волну.", "There is no wave reward available right now."));
        }
        if (!context.pendingRewardChoices.contains(moduleId)) {
            return ActionResult.fail(this.choose(actor, "Этого модуля нет в текущем выборе награды.", "That module is not in the current reward selection."));
        }
        ModuleDefinition module = context.moduleById.get(moduleId);
        String displayName = module != null ? this.moduleDisplayName(actor, module) : moduleId;
        if (this.isDuoMode(context)) {
            String actorTeam = this.teamForPlayer(context, actor);
            String effectivePickerTeam = actorTeam;
            if (this.isDuoSoloTestEnabled(context)) {
                effectivePickerTeam = this.normalizeTeam(context.rewardPickerTeam);
                if (!TEAM_BLUE.equals(effectivePickerTeam) && !TEAM_GREEN.equals(effectivePickerTeam)) {
                    return ActionResult.fail(this.choose(actor, "Сейчас нет активного выбора стороны.", "There is no active side pick right now."));
                }
            } else if (!TEAM_BLUE.equals(actorTeam) && !TEAM_GREEN.equals(actorTeam)) {
                return ActionResult.fail(this.choose(actor, "Игрок не привязан к команде Duo.", "The player is not assigned to a Duo team."));
            }
            if (!this.isDuoSoloTestEnabled(context) && !actorTeam.equals(context.rewardPickerTeam)) {
                return ActionResult.fail(this.choose(actor, "Сейчас выбирает союзник.", "It is your ally's turn to pick."));
            }
            if (this.isDuoSoloTestEnabled(context)) {
                this.addModuleToInventory(context, moduleId, 2);
                this.showEventToast(
                    context,
                    this.choose(world, "Тестовый Duo выбор: ", "Duo test pick: ") + displayName + " x2",
                    EVENT_TOAST_ICON_CORES
                );
                this.clearRewardDraftState(context);
                this.playWorldUiSound(world, SOUND_REWARD);
                this.refreshVisualizationIfEnabled(world);
                if (context.state.instantAutoStart
                    && BankDefenseMatchPhaseSupport.canStartPreparedWave(context.state)) {
                    ActionResult result = this.startPreparedWave(world, context, false);
                    return ActionResult.ok(this.choose(actor, "Тестовый Duo выбор завершён. ", "Duo test reward completed. ") + result.message);
                }
                return ActionResult.ok(this.choose(actor, "Тестовый Duo выбор завершён. ", "Duo test reward completed. ")
                    + this.choose(actor, "Модуль выдан дважды: ", "Module granted twice: ")
                    + displayName + ".");
            }
            this.addModuleToInventory(context, moduleId, 1);
            context.rewardSelectionsByTeam.put(effectivePickerTeam, moduleId);
            context.rewardLastPickedTeam = effectivePickerTeam;
            context.rewardLastPickedModuleId = moduleId;
            context.pendingRewardChoices.remove(moduleId);
            this.showEventToast(
                context,
                this.choose(world, TEAM_BLUE.equals(effectivePickerTeam) ? "Синий выбрал: " : "Зелёный выбрал: ", TEAM_BLUE.equals(effectivePickerTeam) ? "Blue picked: " : "Green picked: ") + displayName,
                EVENT_TOAST_ICON_CORES
            );
            if (context.rewardSelectionsByTeam.size() == 1) {
                context.rewardPickerTeam = TEAM_BLUE.equals(effectivePickerTeam) ? TEAM_GREEN : TEAM_BLUE;
                context.state.rewardPending = !context.pendingRewardChoices.isEmpty();
                this.queueWorldUiSound(context, SOUND_DUO_REWARD_ALLY_PICK);
                this.openRewardPages(world);
                return ActionResult.ok(this.choose(actor, "Выбор сохранён. Теперь ход союзника: ", "Pick saved. Your ally chooses next: ")
                    + displayName + ".");
            }
            this.finalizeDuoRewardDraft(world, context);
            this.refreshVisualizationIfEnabled(world);
            boolean idolModePromptPending = !context.pendingIdolModeSlotByPlayer.isEmpty();
            if (idolModePromptPending) {
                this.schedulePendingSeedIdolModeChoiceOpens(world, context, 0.12);
                return ActionResult.ok(this.choose(actor, "Duo награда завершена. Идол урожая ждёт выбора режима.", "Duo reward completed. The Harvest Idol is waiting for a mode choice."));
            }
            if (context.state.instantAutoStart
                && BankDefenseMatchPhaseSupport.canStartPreparedWave(context.state)) {
                ActionResult result = this.startPreparedWave(world, context, false);
                return ActionResult.ok(this.choose(actor, "Duo награда завершена. ", "Duo reward completed. ") + result.message);
            }
            return ActionResult.ok(this.choose(actor, "Duo награда завершена.", "Duo reward completed."));
        }

        this.addModuleToInventory(context, moduleId, 1);
        this.clearRewardDraftState(context);
        this.showEventToast(
            context,
            this.choose(world, "Получен модуль: ", "Module received: ") + displayName,
            EVENT_TOAST_ICON_CORES
        );
        if (context.tutorialActive) {
            TutorialState tutorial = this.tutorialState(world);
            this.reconcileTutorialProgression(context, tutorial);
            if (tutorial.stage == TutorialStage.ChooseRewardModule) {
                tutorial.rewardChosen = true;
                tutorial.stage = TutorialStage.ReturnAfterReward;
                this.playWorldUiSound(world, SOUND_TUTORIAL_STEP);
            }
        }
        this.playWorldUiSound(world, SOUND_REWARD);
        this.refreshVisualizationIfEnabled(world);
        boolean idolModePromptPending = !context.pendingIdolModeSlotByPlayer.isEmpty();
        if (idolModePromptPending) {
            return ActionResult.ok(this.choose(actor, "Получен модуль: ", "Module received: ") + displayName + ". " + this.choose(actor, "Идол урожая ждёт выбора режима.", "The Harvest Idol is waiting for a mode choice."));
        }
        if (context.state.instantAutoStart
            && BankDefenseMatchPhaseSupport.canStartPreparedWave(context.state)) {
            ActionResult result = this.startPreparedWave(world, context, false);
            return ActionResult.ok(this.choose(actor, "Получен модуль: ", "Module received: ") + displayName + ". " + result.message);
        }
        return ActionResult.ok(this.choose(actor, "Получен модуль: ", "Module received: ") + displayName + ".");
    }

    private void prepareRewardChoices(MatchContext context, List<String> pool, int completedWaveNumber) {
        if (context == null) {
            return;
        }
        this.clearRewardDraftState(context);
        context.lastRewardPreparedWave = completedWaveNumber;
        int choiceCount = Math.min(4, pool.size());
        for (int i = 0; i < choiceCount; i++) {
            context.pendingRewardChoices.add(pool.get(i));
        }
        context.state.rewardPending = !context.pendingRewardChoices.isEmpty();
        if (this.isDuoMode(context) && context.state.rewardPending) {
            int interval = Math.max(1, context.snapshot.gameRules.moduleRewardIntervalWaves);
            context.rewardDraftRound = Math.max(1, completedWaveNumber / interval);
            context.rewardPickerTeam = (context.rewardDraftRound % 2 == 1) ? TEAM_BLUE : TEAM_GREEN;
        }
    }

    private void clearRewardDraftState(MatchContext context) {
        if (context == null) {
            return;
        }
        context.pendingRewardChoices.clear();
        context.pendingRewardPageOpenDelayByPlayer.clear();
        context.rewardSelectionsByTeam.clear();
        context.rewardPickerTeam = TEAM_SHARED;
        context.rewardTeamBonusModuleId = "";
        context.rewardBurnedModuleId = "";
        context.rewardLastPickedTeam = "";
        context.rewardLastPickedModuleId = "";
        context.state.rewardPending = false;
    }

    private void finalizeDuoRewardDraft(World world, MatchContext context) {
        if (context == null) {
            return;
        }
        List<String> remaining = new ArrayList<>(context.pendingRewardChoices);
        context.pendingRewardChoices.clear();
        if (!remaining.isEmpty()) {
            context.rewardTeamBonusModuleId = remaining.get(0);
            this.addModuleToInventory(context, context.rewardTeamBonusModuleId, 1);
            this.queueWorldUiSound(context, SOUND_DUO_REWARD_TEAM_BONUS);
        }
        if (remaining.size() > 1) {
            context.rewardBurnedModuleId = remaining.get(1);
            this.queueWorldUiSound(context, SOUND_DUO_REWARD_BURN);
        }
        context.rewardPickerTeam = TEAM_SHARED;
        context.state.rewardPending = false;
        StringBuilder message = new StringBuilder();
        message.append(this.choose(world, "Duo выбор завершён. ", "Duo draft completed. "));
        if (!context.rewardSelectionsByTeam.isEmpty()) {
            String blueModule = context.rewardSelectionsByTeam.get(TEAM_BLUE);
            String greenModule = context.rewardSelectionsByTeam.get(TEAM_GREEN);
            if (blueModule != null) {
                ModuleDefinition blue = context.moduleById.get(blueModule);
                message.append(this.choose(world, "Синий: ", "Blue: "))
                    .append(blue == null ? blueModule : this.moduleDisplayName(world, blue))
                    .append(". ");
            }
            if (greenModule != null) {
                ModuleDefinition green = context.moduleById.get(greenModule);
                message.append(this.choose(world, "Зелёный: ", "Green: "))
                    .append(green == null ? greenModule : this.moduleDisplayName(world, green))
                    .append(". ");
            }
        }
        if (context.rewardTeamBonusModuleId != null && !context.rewardTeamBonusModuleId.isBlank()) {
            ModuleDefinition bonus = context.moduleById.get(context.rewardTeamBonusModuleId);
            message.append(this.choose(world, "Командный бонус: ", "Team bonus: "))
                .append(bonus == null ? context.rewardTeamBonusModuleId : this.moduleDisplayName(world, bonus))
                .append(". ");
        }
        if (context.rewardBurnedModuleId != null && !context.rewardBurnedModuleId.isBlank()) {
            ModuleDefinition burned = context.moduleById.get(context.rewardBurnedModuleId);
            message.append(this.choose(world, "Сгорело: ", "Burned: "))
                .append(burned == null ? context.rewardBurnedModuleId : this.moduleDisplayName(world, burned))
                .append(".");
        }
        this.showEventToast(context, message.toString().trim(), EVENT_TOAST_ICON_CORES);
        this.sendLocalizedWorldMessage(world, viewerRef -> {
            StringBuilder localized = new StringBuilder();
            localized.append(this.choose(viewerRef, "Duo выбор завершён. ", "Duo draft completed. "));
            if (!context.rewardSelectionsByTeam.isEmpty()) {
                String blueModule = context.rewardSelectionsByTeam.get(TEAM_BLUE);
                String greenModule = context.rewardSelectionsByTeam.get(TEAM_GREEN);
                if (blueModule != null) {
                    ModuleDefinition blue = context.moduleById.get(blueModule);
                    localized.append(this.choose(viewerRef, "Синий: ", "Blue: "))
                        .append(blue == null ? blueModule : this.moduleDisplayName(viewerRef, blue))
                        .append(". ");
                }
                if (greenModule != null) {
                    ModuleDefinition green = context.rewardSelectionsByTeam.get(TEAM_GREEN) == null ? null : context.moduleById.get(greenModule);
                    localized.append(this.choose(viewerRef, "Зелёный: ", "Green: "))
                        .append(green == null ? greenModule : this.moduleDisplayName(viewerRef, green))
                        .append(". ");
                }
            }
            if (context.rewardTeamBonusModuleId != null && !context.rewardTeamBonusModuleId.isBlank()) {
                ModuleDefinition bonus = context.moduleById.get(context.rewardTeamBonusModuleId);
                localized.append(this.choose(viewerRef, "Командный бонус: ", "Team bonus: "))
                    .append(bonus == null ? context.rewardTeamBonusModuleId : this.moduleDisplayName(viewerRef, bonus))
                    .append(". ");
            }
            if (context.rewardBurnedModuleId != null && !context.rewardBurnedModuleId.isBlank()) {
                ModuleDefinition burned = context.moduleById.get(context.rewardBurnedModuleId);
                localized.append(this.choose(viewerRef, "Сгорело: ", "Burned: "))
                    .append(burned == null ? context.rewardBurnedModuleId : this.moduleDisplayName(viewerRef, burned))
                    .append(".");
            }
            return localized.toString().trim();
        });
    }

    public ProgressionUiState getProgressionUiState(World world) throws IOException {
        MatchContext context = this.getOrCreateContext(world);
        ProgressionUiState state = new ProgressionUiState();
        state.matchActive = this.progressionLocked(context);
        String selectedContractId = this.selectedContractIdsByWorld.getOrDefault(this.worldKey(world), context.progression == null ? "none" : context.progression.activeContractId);
        if (context.progression != null) {
            state.cores = context.progression.cores;
            state.highestWave = context.progression.lifetimeHighestWave;
            state.totalRuns = context.progression.totalRuns;
            state.totalVictories = context.progression.totalVictories;
            state.activeContractId = selectedContractId;
        }
        if (state.activeContractId == null || state.activeContractId.isBlank()) {
            state.activeContractId = "none";
        }
        state.activeContractName = this.contractDisplayName(world, context.activeContract);

        Set<String> unlocked = context.progression == null
            ? Set.of()
            : new HashSet<>(context.progression.unlockedNodeIds);
        for (ProgressionNodeDefinition node : context.snapshot.progression.nodes) {
            ProgressionNodeButtonState button = new ProgressionNodeButtonState();
            button.nodeId = node.id;
            button.displayName = this.progressionDisplayName(world, node);
            button.note = this.text(node.note);
            button.branch = node.branch;
            button.tier = node.tier;
            button.cost = node.cost;
            button.value = node.value;
            button.targetId = node.targetId;
            button.unlocked = unlocked.contains(node.id);
            button.available = !button.unlocked && this.areRequirementsMet(unlocked, node.requires);
            state.nodes.add(button);
        }
        state.nodes.sort(Comparator
            .comparing((ProgressionNodeButtonState node) -> node.branch == null ? "" : node.branch)
            .thenComparingInt(node -> node.tier)
            .thenComparing(node -> node.nodeId == null ? "" : node.nodeId));

        for (ContractDefinition contract : context.snapshot.contracts.contracts) {
            ContractButtonState button = new ContractButtonState();
            button.contractId = contract.id;
            button.displayName = this.contractDisplayName(world, contract);
            String contractNote = this.contractNote(world, contract);
            String rewardNote = BankDefenseLocalization.choose(this.primaryPlayerRef(world), "Осколки дупла: ", "Hollow shards: ") + this.formatMultiplier(contract.rewardMultiplier);
            button.note = contractNote == null || contractNote.isBlank()
                ? rewardNote
                : contractNote + "\n" + rewardNote;
            button.active = contract.id != null && contract.id.equals(state.activeContractId);
            if (context.tutorialActive && !"none".equals(contract.id)) {
                button.enabled = false;
                button.note = button.note + "\n" + this.tr(world, "tutorial.unavailable_in_tutorial");
            }
            state.contracts.add(button);
        }
        return state;
    }

    public StatisticsUiState getStatisticsUiState(World world) throws IOException {
        MatchContext context = this.getOrCreateContext(world);
        StatisticsUiState state = new StatisticsUiState();
        if (context == null || context.progression == null) {
            return state;
        }
        PlayerProgressionState progression = context.progression;
        state.lifetimeWavesCleared = progression.lifetimeWavesCleared;
        state.lifetimeEnemyKills = progression.lifetimeEnemyKills;
        state.lifetimeSpentCurrency = progression.lifetimeSpentCurrency;
        state.lifetimeTrapsPlaced = progression.lifetimeTrapsPlaced;
        state.totalRuns = progression.totalRuns;
        state.lifetimeOpenedChests = progression.lifetimeOpenedChests;
        state.lifetimeModulesInstalled = progression.lifetimeModulesInstalled;
        state.bestRunWave = progression.bestRunWave;
        state.bestRunEarnedCurrency = progression.bestRunEarnedCurrency;
        state.bestRunEnemyKills = progression.bestRunEnemyKills;
        return state;
    }

    public ActionResult unlockProgressionNode(World world, String nodeId) throws IOException {
        MatchContext context = this.getOrCreateContext(world);
        if (context.progression == null) {
            return ActionResult.fail(this.choose(world, "Профиль прогрессии недоступен.", "Progression profile is unavailable."));
        }
        if (this.progressionLocked(context)) {
            return ActionResult.fail(this.choose(world, "Улучшения за осколки дупла доступны только вне матча.", "Hollow shard upgrades are only available outside a match."));
        }
        ProgressionNodeDefinition node = this.findProgressionNode(context.snapshot, nodeId);
        if (node == null) {
            return ActionResult.fail(this.choose(world, "Неизвестный узел: ", "Unknown node: ") + nodeId + ".");
        }
        Set<String> unlocked = new HashSet<>(context.progression.unlockedNodeIds);
        if (unlocked.contains(node.id)) {
            return ActionResult.fail(this.text(node.displayName) + this.choose(world, " уже открыто.", " is already unlocked."));
        }
        if (!this.areRequirementsMet(unlocked, node.requires)) {
            return ActionResult.fail(this.choose(world, "Сначала открой предыдущие узлы.", "Unlock the previous nodes first."));
        }
        if (context.progression.cores < node.cost) {
            return ActionResult.fail(this.choose(world, "Недостаточно Ядер дупла. Нужно: ", "Not enough Hollow Cores. Required: ") + node.cost + ".");
        }
        context.progression.cores -= node.cost;
        context.progression.unlockedNodeIds.add(node.id);
        if (!context.tutorialActive) {
            this.repository.savePlayerProgression(context.progression);
        }
        this.showEventToast(
            context,
            this.choose(world, "Открыто улучшение: ", "Upgrade unlocked: ") + this.text(node.displayName)
                + this.choose(world, " (-", " (-") + node.cost + this.choose(world, " осколков).", " shards)."),
            EVENT_TOAST_ICON_CORES
        );
        this.playWorldUiSound(world, SOUND_CORE_UNLOCK);
        if (context.tutorialActive) {
            TutorialState tutorial = this.tutorialState(world);
            if (tutorial.stage == TutorialStage.SpendCores) {
                tutorial.stage = TutorialStage.ReturnAfterProgression;
                this.playWorldUiSound(world, SOUND_TUTORIAL_STEP);
                this.refreshVisualization(world);
            }
        }
        return ActionResult.ok(this.choose(world, "Открыт узел: ", "Node unlocked: ") + this.text(node.displayName)
            + this.choose(world, ". Изменения применятся в следующем матче.", ". The changes will apply in the next match."));
    }

    public ActionResult resetProgression(World world) throws IOException {
        MatchContext context = this.getOrCreateContext(world);
        if (context.progression == null) {
            return ActionResult.fail(this.choose(world, "Профиль прогрессии недоступен.", "Progression profile is unavailable."));
        }
        if (this.progressionLocked(context)) {
            return ActionResult.fail(this.choose(world, "Сброс прогрессии доступен только вне матча.", "Progression reset is only available outside a match."));
        }

        int previousCores = context.progression.cores;
        int previousUnlocks = context.progression.unlockedNodeIds.size();
        context.progression.cores = 0;
        context.progression.unlockedNodeIds.clear();
        this.repository.savePlayerProgression(context.progression);

        MatchContext refreshed = this.createFreshContext(world);
        this.matchesByWorld.put(this.worldKey(world), refreshed);
        this.refreshVisualizationIfEnabled(world);
        return ActionResult.ok(this.choose(world, "Прогресс дупла сброшен. Осколки: ", "Hollow progression reset. Shards: ")
            + previousCores + " -> 0"
            + this.choose(world, ", улучшения: ", ", upgrades: ")
            + previousUnlocks + " -> 0.");
    }

    public ActionResult resetStatistics(World world) throws IOException {
        MatchContext context = this.getOrCreateContext(world);
        if (context.progression == null) {
            return ActionResult.fail(this.choose(world, "Профиль прогрессии недоступен.", "Progression profile is unavailable."));
        }
        if (this.progressionLocked(context)) {
            return ActionResult.fail(this.choose(world, "Сброс статистики доступен только вне матча.", "Statistics reset is only available outside a match."));
        }

        long previousWavesCleared = context.progression.lifetimeWavesCleared;
        long previousEnemyKills = context.progression.lifetimeEnemyKills;
        long previousSpentCurrency = context.progression.lifetimeSpentCurrency;
        long previousTrapsPlaced = context.progression.lifetimeTrapsPlaced;
        long previousOpenedChests = context.progression.lifetimeOpenedChests;
        long previousModulesInstalled = context.progression.lifetimeModulesInstalled;
        int previousTotalRuns = context.progression.totalRuns;
        int previousTotalVictories = context.progression.totalVictories;
        int previousHighestWave = context.progression.lifetimeHighestWave;
        int previousBestRunWave = context.progression.bestRunWave;

        context.progression.lifetimeHighestWave = 0;
        context.progression.lifetimeWavesCleared = 0;
        context.progression.lifetimeEnemyKills = 0;
        context.progression.lifetimeSpentCurrency = 0;
        context.progression.lifetimeTrapsPlaced = 0;
        context.progression.lifetimeOpenedChests = 0;
        context.progression.lifetimeModulesInstalled = 0;
        context.progression.totalRuns = 0;
        context.progression.totalVictories = 0;
        context.progression.bestRunWave = 0;
        context.progression.bestRunEarnedCurrency = 0;
        context.progression.bestRunEnemyKills = 0;
        this.repository.savePlayerProgression(context.progression);

        MatchContext refreshed = this.createFreshContext(world);
        this.matchesByWorld.put(this.worldKey(world), refreshed);
        this.refreshVisualizationIfEnabled(world);
        return ActionResult.ok(
            this.choose(world, "Статистика сброшена. Волны: ", "Statistics reset. Waves: ")
                + previousWavesCleared + " -> 0"
                + this.choose(world, ", убийства: ", ", kills: ") + previousEnemyKills + " -> 0"
                + this.choose(world, ", траты: ", ", spent: ") + previousSpentCurrency + " -> 0"
                + this.choose(world, ", ловушки: ", ", traps: ") + previousTrapsPlaced + " -> 0"
                + this.choose(world, ", сундуки: ", ", chests: ") + previousOpenedChests + " -> 0"
                + this.choose(world, ", модули: ", ", modules: ") + previousModulesInstalled + " -> 0"
                + this.choose(world, ", матчи: ", ", runs: ") + previousTotalRuns + " -> 0"
                + this.choose(world, ", победы: ", ", victories: ") + previousTotalVictories + " -> 0"
                + this.choose(world, ", рекорд волны: ", ", highest wave: ") + previousHighestWave + " -> 0"
                + this.choose(world, ", лучший забег: ", ", best run: ") + previousBestRunWave + " -> 0."
        );
    }

    public ActionResult setActiveContract(World world, String contractId) throws IOException {
        MatchContext context = this.getOrCreateContext(world);
        if (this.isDuoGameplayMode(world)) {
            return ActionResult.fail(this.choose(world, "Контракты в Duo режиме отключены.", "Contracts are disabled in Duo mode."));
        }
        if (context.progression == null) {
            return ActionResult.fail(this.choose(world, "Профиль прогрессии недоступен.", "Progression profile is unavailable."));
        }
        ContractDefinition selected = null;
        for (ContractDefinition contract : context.snapshot.contracts.contracts) {
            if (contract.id != null && contract.id.equals(contractId)) {
                selected = contract;
                break;
            }
        }
        if (selected == null) {
            return ActionResult.fail(this.choose(world, "Неизвестный контракт: ", "Unknown contract: ") + contractId + ".");
        }
        context.progression.activeContractId = selected.id;
        this.repository.savePlayerProgression(context.progression);
        return ActionResult.ok(this.choose(world, "Активный контракт: ", "Active contract: ") + this.text(selected.displayName)
            + this.choose(world, ". Сменится после сброса матча.", ". It will change after the match is reset."));
    }

    public ActionResult prepareMatch(World world, String difficultyId, String contractId) throws IOException {
        if (this.isTutorialActive(world)) {
            TutorialState tutorial = this.tutorialState(world);
            if (tutorial.stage != TutorialStage.PrepareFirstWave) {
                return ActionResult.fail(this.choose(world, "Сейчас обучение ждёт другой шаг, а не выбор матча.", "The tutorial is waiting for a different step, not match selection."));
            }
            difficultyId = DIFFICULTY_EASY;
            contractId = "none";
        }
        if (this.isDuoGameplayMode(world)) {
            boolean hasBlue = this.isDuoTeamClaimed(world, TEAM_BLUE);
            boolean hasGreen = this.isDuoTeamClaimed(world, TEAM_GREEN);
            if (!hasBlue && !hasGreen) {
                return ActionResult.fail(this.choose(world, "Сначала выберите сторону.", "Choose a side first."));
            }
            if (!this.isDuoSoloTestEnabled(world) && !this.isDuoTeamSelectionComplete(world)) {
                return ActionResult.fail(this.choose(world, "Ждём второго игрока: он должен выбрать другую сторону.", "Waiting for the second player: they must choose the other side."));
            }
            contractId = "none";
        }
        DifficultyProfile difficulty = this.resolveDifficultyProfile(difficultyId);
        String worldKey = this.worldKey(world);
        this.selectedDifficultyIdsByWorld.put(worldKey, difficulty.id);
        if (contractId != null && !contractId.isBlank()) {
            this.selectedContractIdsByWorld.put(worldKey, contractId);
        } else {
            this.selectedContractIdsByWorld.remove(worldKey);
        }
        MatchState state = this.resetMatch(world);
        MatchContext context = this.matchesByWorld.get(worldKey);
        if (context != null) {
            context.state.gameState = GameState.Ready;
            context.state.waveState = WaveState.BuildPhase;
            context.state.gameStarted = true;
            context.state.preparationRemainingSeconds = 0.0;
            this.clearInstantAutoStartWarmup(context);
            context.pendingSpawns.clear();
            context.enemies.clear();
            if (!context.tutorialActive) {
                String buildPrompt = this.choose(world, "Поставьте хотя бы одну башню или ловушку.", "Place at least one tower or trap.");
                this.showEventToast(context, buildPrompt, EVENT_TOAST_ICON_ALERT);
                this.playWorldUiSound(world, SOUND_NOTIFICATION);
            }
        }
        this.refreshVisualizationIfEnabled(world);
        if (this.isDuoGameplayMode(world)) {
            return ActionResult.ok(this.choose(world, "Duo матч подготовлен. Сложность: ", "Duo match prepared. Difficulty: ")
                + this.difficultyDisplayName(world, difficulty)
                + this.choose(world, ". Контракты отключены. Можно ставить башни. Баланс: ", ". Contracts are disabled. You can build towers. Balance: ")
                + state.currency + ".");
        }
        return ActionResult.ok(this.choose(world, "Матч подготовлен. Сложность: ", "Match prepared. Difficulty: ")
            + this.difficultyDisplayName(world, difficulty)
            + this.choose(world, ", контракт: ", ", contract: ")
            + this.activeContractName(world)
            + this.choose(world, ". Можно ставить башни. Баланс: ", ". You can build towers. Balance: ")
            + state.currency + ".");
    }

    private String formatMultiplier(double value) {
        return String.format(Locale.US, "x%.1f", value);
    }

    private boolean areRequirementsMet(Set<String> unlocked, List<String> requires) {
        if (requires == null || requires.isEmpty()) {
            return true;
        }
        if (unlocked == null || unlocked.isEmpty()) {
            return false;
        }
        for (String requirement : requires) {
            if (requirement == null || requirement.isBlank()) {
                continue;
            }
            if (!unlocked.contains(requirement)) {
                return false;
            }
        }
        return true;
    }

    private ProgressionNodeDefinition findProgressionNode(BankDefenseRepository.Snapshot snapshot, String nodeId) {
        if (snapshot == null || nodeId == null || nodeId.isBlank()) {
            return null;
        }
        for (ProgressionNodeDefinition node : snapshot.progression.nodes) {
            if (nodeId.equals(node.id)) {
                return node;
            }
        }
        return null;
    }

    public ActionResult addCurrency(World world, int amount) throws IOException {
        if (amount <= 0) {
            return ActionResult.fail(this.choose(world, "Сумма должна быть больше 0.", "The amount must be greater than 0."));
        }
        return this.changeCurrency(world, amount);
    }

    public ActionResult changeCurrency(World world, int amountDelta) throws IOException {
        if (amountDelta == 0) {
            return ActionResult.fail(this.choose(world, "Изменение средств не может быть 0.", "The currency change cannot be 0."));
        }
        MatchContext context = this.getOrCreateContext(world);
        if (this.isDuoMode(context)) {
            int nextBlue = context.blueCurrency + amountDelta;
            int nextGreen = context.greenCurrency + amountDelta;
            if (nextBlue < 0 || nextGreen < 0) {
                return ActionResult.fail(this.choose(world, "Нельзя уйти в минус. Минимум: 0.", "You cannot go below zero. Minimum: 0."));
            }
            context.blueCurrency = nextBlue;
            context.greenCurrency = nextGreen;
            this.syncTotalCurrency(context);
            this.refreshVisualizationIfEnabled(world);
            return ActionResult.ok(
                (amountDelta > 0
                    ? this.choose(world, "Добавлено средств каждому игроку: ", "Currency added to each player: ")
                    : this.choose(world, "Списано средств у каждого игрока: ", "Currency removed from each player: "))
                    + Math.abs(amountDelta)
                    + this.choose(world, ". Личный баланс: ", ". Personal balance: ")
                    + context.blueCurrency
                    + "."
            );
        }
        int nextBalance = context.state.currency + amountDelta;
        if (nextBalance < 0) {
            return ActionResult.fail(this.choose(world, "Нельзя уйти в минус. Минимум: 0.", "You cannot go below zero. Minimum: 0."));
        }
        context.state.currency = nextBalance;
        this.refreshVisualizationIfEnabled(world);
        if (amountDelta > 0) {
            return ActionResult.ok(this.choose(world, "Добавлено средств: ", "Currency added: ") + amountDelta
                + this.choose(world, ". Баланс: ", ". Balance: ") + context.state.currency + ".");
        }
        return ActionResult.ok(this.choose(world, "Списано средств: ", "Currency removed: ") + Math.abs(amountDelta)
            + this.choose(world, ". Баланс: ", ". Balance: ") + context.state.currency + ".");
    }

    public ActionResult setCurrency(World world, int amount) throws IOException {
        if (amount < 0) {
            return ActionResult.fail(this.choose(world, "Баланс не может быть отрицательным.", "The balance cannot be negative."));
        }
        MatchContext context = this.getOrCreateContext(world);
        if (this.isDuoMode(context)) {
            context.blueCurrency = amount;
            context.greenCurrency = amount;
            this.syncTotalCurrency(context);
            this.refreshVisualizationIfEnabled(world);
            return ActionResult.ok(this.choose(world, "Личный баланс игроков установлен: ", "Player personal balance set to: ") + amount + ".");
        }
        context.state.currency = amount;
        this.refreshVisualizationIfEnabled(world);
        return ActionResult.ok(this.choose(world, "Баланс установлен: ", "Balance set: ") + context.state.currency + ".");
    }

    public ActionResult grantAllModules(World world) throws IOException {
        MatchContext context = this.getOrCreateContext(world);
        int granted = 0;
        for (ModuleDefinition module : context.snapshot.modules.modules) {
            if (module == null || module.id == null || module.id.isBlank()) {
                continue;
            }
            this.addModuleToInventory(context, module.id, 1);
            granted++;
        }
        this.refreshVisualizationIfEnabled(world);
        return ActionResult.ok(this.choose(world, "Выдано модулей: ", "Modules granted: ") + granted
            + this.choose(world, ". По 1 экземпляру каждого.", ". One copy of each."));
    }

    public ActionResult grantProgressionCores(World world, int amount) throws IOException {
        if (amount <= 0) {
            return ActionResult.fail(this.choose(world, "Количество ядер должно быть больше 0.", "The number of cores must be greater than 0."));
        }
        MatchContext context = this.getOrCreateContext(world);
        if (context.progression == null) {
            return ActionResult.fail(this.choose(world, "Прогрессия для этого мира недоступна.", "Progression is unavailable for this world."));
        }
        context.progression.cores += amount;
        this.repository.savePlayerProgression(context.progression);
        this.refreshVisualizationIfEnabled(world);
        return ActionResult.ok(this.choose(world, "Выдано Ядер дупла: +", "Hollow Cores granted: +") + amount
            + this.choose(world, ". Всего: ", ". Total: ") + context.progression.cores + ".");
    }

    public ActionResult claimMoneyChest(World world, Vec3i position) throws IOException {
        return this.claimChest(world, position, ChestType.Money);
    }

    public ActionResult claimCoreChest(World world, Vec3i position) throws IOException {
        return this.claimChest(world, position, ChestType.Core);
    }

    private ActionResult claimChest(World world, Vec3i position, ChestType expectedType) throws IOException {
        MatchContext context = this.getOrCreateContext(world);
        if (!context.state.gameStarted) {
            return ActionResult.fail(this.choose(world, "Сундуки появляются только после начала матча.", "Chests only appear after the match starts."));
        }
        if (context.activeChests.isEmpty()) {
            return ActionResult.fail(this.choose(world, "Активных сундуков сейчас нет.", "There are no active chests right now."));
        }
        ActiveChest targetChest = null;
        for (ActiveChest chest : context.activeChests.values()) {
            if (chest.type == expectedType && this.samePoint(chest.position, position)) {
                targetChest = chest;
                break;
            }
        }
        if (targetChest == null) {
            return ActionResult.fail(this.choose(world, "Этот сундук уже открыт или недоступен.", "That chest is already opened or unavailable."));
        }
        context.activeChests.remove(targetChest.id);
        Vector3d effectPosition = new Vector3d(position.x + 0.5, position.y + 0.55, position.z + 0.5);
        this.playSound3d(world, SOUND_CHEST_OPEN, effectPosition);
        List<Ref<EntityStore>> playerRefs = this.collectPlayerEntityRefs(world);
        Store<EntityStore> store = world.getEntityStore().getStore();
        this.spawnParticleBurst(store, playerRefs, effectPosition, 0.42, 10, 0.10);
        if (expectedType == ChestType.Core) {
            this.spawnColoredParticle(store, playerRefs, PARTICLE_IMPACT, effectPosition, new Vector3f(0.0f, 0.0f, 0.0f), 0.65f, this.rgb(120, 255, 194));
            this.playSound3d(world, SOUND_REWARD, effectPosition);
            if (context.progression == null) {
                this.syncChestInteractionBlocks(world, context.snapshot.map, context);
                return ActionResult.fail(this.choose(world, "Прогрессия для этого мира недоступна.", "Progression is unavailable for this world."));
            }
            this.trackLifetimeChestOpened(context);
            context.progression.cores += CORE_CHEST_REWARD;
            this.repository.savePlayerProgression(context.progression);
            this.showEventToast(
                context,
                this.choose(world, "Сундук: +", "Chest: +") + CORE_CHEST_REWARD + this.choose(world, " осколок дупла", " hollow shard"),
                EVENT_TOAST_ICON_CORES
            );
            this.syncChestInteractionBlocks(world, context.snapshot.map, context);
            return ActionResult.ok(this.choose(world, "Открыт сундук с ядром: +", "Core chest opened: +") + CORE_CHEST_REWARD + this.choose(world, " осколок дупла. Всего: ", " hollow shard. Total: ") + context.progression.cores + ".");
        }
        this.spawnColoredParticle(store, playerRefs, PARTICLE_IMPACT, effectPosition, new Vector3f(0.0f, 0.0f, 0.0f), 0.65f, this.rgb(255, 222, 122));
        this.trackLifetimeChestOpened(context);
        int chestIncome = this.scaledNonKillIncome(context, MONEY_CHEST_REWARD);
        int grantedCurrency = this.grantMatchIncome(world, context, chestIncome, effectPosition);
        this.syncChestInteractionBlocks(world, context.snapshot.map, context);
        if (context.tutorialActive) {
            TutorialState tutorial = this.tutorialState(world);
            if (tutorial.stage == TutorialStage.CollectTutorialChest && TUTORIAL_CHEST_ID.equals(targetChest.id)) {
                tutorial.stage = TutorialStage.ReturnAfterTutorialChest;
                this.playWorldUiSound(world, SOUND_TUTORIAL_STEP);
                this.scheduleVisualizationRefreshIfEnabled(world, 25L);
            }
        }
        if (grantedCurrency > 0) {
            this.showEventToast(
                context,
                this.choose(world, "Сундук: +", "Chest: +") + grantedCurrency + this.choose(world, " монет", " gold"),
                EVENT_TOAST_ICON_MONEY
            );
            if (this.isDuoMode(context)) {
                return ActionResult.ok(
                    this.choose(world, "Открыт сундук с добычей: +", "Supply chest opened: +") + grantedCurrency
                        + this.choose(world, " монет. Балансы Duo: синий ", " gold. Duo balances: blue ")
                        + context.blueCurrency
                        + this.choose(world, ", зелёный ", ", green ")
                        + context.greenCurrency
                        + "."
                );
            }
            return ActionResult.ok(this.choose(world, "Открыт сундук с добычей: +", "Supply chest opened: +") + grantedCurrency + this.choose(world, " монет. Баланс: ", " gold. Balance: ") + context.state.currency + ".");
        }
        TowerInstance seedIdol = this.findSeedIdol(context);
        int stored = seedIdol == null ? chestIncome : seedIdol.idolStoredCurrency;
        this.showEventToast(
            context,
            this.choose(world, "Сундук: +", "Chest: +") + chestIncome + this.choose(world, " монет отправлено в накопление", " gold sent to storage"),
            EVENT_TOAST_ICON_MONEY
        );
        return ActionResult.ok(this.choose(world, "Открыт сундук с добычей: +", "Supply chest opened: +") + chestIncome + this.choose(world, " отправлено в запас идола. Накоплено: ", " sent to the idol reserve. Stored: ") + stored + ".");
    }

    public ActionResult setWave(World world, int waveNumber) throws IOException {
        if (waveNumber <= 0) {
            return ActionResult.fail(this.choose(world, "Номер волны должен быть больше 0.", "The wave number must be greater than 0."));
        }
        String worldKey = this.worldKey(world);
        MatchContext context = this.getOrCreateContext(world);
        boolean matchPrepared = context.state.gameStarted;
        if (context.state.gameState == GameState.InMatch) {
            return ActionResult.fail(this.choose(world, "Стартовую волну можно менять только в фазе подготовки.", "The starting wave can only be changed during preparation."));
        }
        if (context.state.gameState == GameState.Victory || context.state.gameState == GameState.Defeat) {
            return ActionResult.fail(this.choose(world, "Сначала сбрось матч, затем выбери стартовую волну.", "Reset the match first, then choose the starting wave."));
        }
        this.selectedStartWavesByWorld.put(worldKey, Integer.valueOf(waveNumber));
        context.pendingSpawns.clear();
        context.enemies.clear();
        context.activeWaveNumber = 0;
        context.activeWaveTotalEnemies = 0;
        context.waveElapsedSeconds = 0.0;
        this.clearRewardDraftState(context);
        context.lastRewardPreparedWave = 0;
        context.state.currentWave = waveNumber;
        context.state.gameState = GameState.Ready;
        context.state.waveState = WaveState.BuildPhase;
        context.state.gameStarted = matchPrepared;
        context.state.preparationRemainingSeconds = 0.0;
        this.clearInstantAutoStartWarmup(context);
        this.refreshVisualizationIfEnabled(world);
        return ActionResult.ok(
            matchPrepared
                ? this.choose(world, "Стартовая волна установлена: ", "Starting wave set to ") + waveNumber + this.choose(world, ". Можно строить башни и запускать волну.", ". You can build towers and start the wave.")
                : this.choose(world, "Стартовая волна сохранена: ", "Starting wave saved: ") + waveNumber + this.choose(world, ". Теперь выберите сложность и контракт у оператора.", ". Now choose a difficulty and contract with the operator.")
        );
    }

    public List<String> getTowerIds(World world) throws IOException {
        BankDefenseRepository.Snapshot snapshot = this.repository.loadSnapshot(world);
        List<String> towerIds = new ArrayList<>();
        for (TowerDefinition tower : snapshot.towers.towers) {
            towerIds.add(tower.id);
        }
        return towerIds;
    }

    public ActionResult refreshVisualization(World world) throws IOException {
        this.ensurePlayerBoundContext(world);
        PresentationState presentation = this.presentationsByWorld.computeIfAbsent(this.worldKey(world), ignored -> new PresentationState());
        presentation.enabled = true;
        presentation.accumulatedSeconds = 0.0;
        MatchContext context = this.matchesByWorld.get(this.worldKey(world));
        if (this.hasCombatVisualState(context)) {
            BankDefenseRepository.Snapshot rawSnapshot = this.repository.loadSnapshot(world);
            presentation.cachedRawSnapshot = rawSnapshot;
            this.rebuildCombatVisualLayer(world, presentation, context);
            this.syncHud(world, presentation, this.presentationSnapshot(rawSnapshot, context, this.tutorialState(world)), context);
            return ActionResult.ok(this.choose(world, "Боевой слой визуализации пересобран.", "Combat visualization rebuilt."));
        }
        int rendered = this.fullRefreshPresentation(world, presentation);
        return ActionResult.ok(this.choose(world, "Визуализация обновлена. Сущностей: ", "Visualization refreshed. Entities: ") + rendered + ".");
    }

    public void resetPresentationState(World world) {
        if (world == null) {
            return;
        }
        this.presentationsByWorld.remove(this.worldKey(world));
    }

    public ActionResult clearVisualization(World world) {
        PresentationState presentation = this.presentationsByWorld.computeIfAbsent(this.worldKey(world), ignored -> new PresentationState());
        presentation.enabled = false;
        presentation.accumulatedSeconds = 0.0;
        int removed = this.clearVisualEntities(world, presentation);
        MatchContext context = this.matchesByWorld.get(this.worldKey(world));
        if (context != null && context.snapshot != null) {
            removed += this.purgeOwnedCombatVisuals(world, context.snapshot, context);
            removed += this.purgeDetachedEnemyVisuals(world, context.snapshot, Collections.emptySet());
        } else {
            removed += this.purgeDetachedEnemyVisuals(world, Collections.emptySet());
        }
        removed += this.purgeDetachedTowerVisuals(world, Collections.emptySet());
        removed += this.purgeDetachedProjectileVisuals(world, Collections.emptySet());
        return ActionResult.ok(this.choose(world, "Визуализация очищена. Удалено сущностей: ", "Visualization cleared. Removed entities: ") + removed + ".");
    }

    public void tickWorld(World world, double deltaSeconds) {
        this.tickPinnedSpawnChunkSync(world, deltaSeconds);
        MatchContext context = this.matchesByWorld.get(this.worldKey(world));
        if (context == null) {
            this.clearStaleAcidStormWeather(world);
            return;
        }
        this.tickWorldInteractionSync(world, context, deltaSeconds);
        if (this.isDuoMode(context)
            && !this.isDuoSoloTestEnabled(context)
            && context.state.gameStarted
            && context.state.gameState != GameState.Defeat
            && context.state.gameState != GameState.Victory) {
            context.duoPlayerCountCheckAccumulatedSeconds += Math.max(0.0, deltaSeconds);
            if (context.duoPlayerCountCheckAccumulatedSeconds >= DUO_PLAYER_COUNT_CHECK_SECONDS
                && this.duoPlayerCount(world, context) < 2) {
                context.duoPlayerCountCheckAccumulatedSeconds = 0.0;
                this.finishMatchAsDefeat(world, context, "Союзник отключился. Duo матч автоматически проигран.", "An ally disconnected. The Duo match was automatically lost.");
                return;
            }
        } else {
            context.duoPlayerCountCheckAccumulatedSeconds = 0.0;
        }
        this.tickTransientUi(context, deltaSeconds);
        this.tickPendingRewardPageOpens(world, context, deltaSeconds);
        this.tickPendingSeedIdolModeChoiceOpens(world, context, deltaSeconds);
        this.tickActiveChestLoopAudio(world, context, deltaSeconds);
        this.flushPendingWorldUiSounds(world, context);
        if (context.state.paused) {
            return;
        }
        this.tickAcidStorm(world, context, deltaSeconds);
        if (context.state.gameState == GameState.Victory || context.state.gameState == GameState.Defeat) {
            this.clearExpiredVisualEffects(world, context);
            this.tickProjectiles(world, deltaSeconds);
            return;
        }
        if (BankDefenseMatchPhaseSupport.isBuildPhase(context.state)) {
            if (BankDefenseMatchPhaseSupport.isPrepared(context.state)
                && context.pendingRewardChoices.isEmpty()
                && !this.hasPendingIdolModeChoice(context)
                && context.state.preparationRemainingSeconds > 0.0) {
                if (context.state.instantAutoStart) {
                    try {
                        this.startPreparedWave(world, context, false);
                    } catch (IOException ignored) {
                    }
                } else {
                    context.state.preparationRemainingSeconds = Math.max(0.0, context.state.preparationRemainingSeconds - deltaSeconds);
                    if (context.state.preparationRemainingSeconds <= 0.0) {
                        try {
                            this.startPreparedWave(world, context, false);
                        } catch (IOException ignored) {
                        }
                    }
                }
            }
            this.clearExpiredVisualEffects(world, context);
            this.tickProjectiles(world, deltaSeconds);
            return;
        }
        if (context.state.gameState != GameState.InMatch) {
            this.clearExpiredVisualEffects(world, context);
            this.tickProjectiles(world, deltaSeconds);
            return;
        }
        if (context.routeLength <= 0.0) {
            context.state.gameState = GameState.Defeat;
            context.state.waveState = WaveState.Finished;
            if (world != null) {
                this.sendLocalizedWorldMessage(world, "Маршрут врагов не настроен. Матч остановлен.", "Enemy routes are not configured. The match was stopped.");
                this.clearExpiredVisualEffects(world, context);
                this.tickProjectiles(world, deltaSeconds);
                return;
            }
            this.clearExpiredVisualEffects(world, context);
            this.tickProjectiles(world, deltaSeconds);
            return;
        }

        context.waveElapsedSeconds += deltaSeconds;
        this.spawnEnemies(world, context);
        this.refreshEnemyAuraFlags(context);
        this.tickDuoBossState(world, context, deltaSeconds);
        this.tickTowers(world, context, deltaSeconds);
        this.tickEnemies(world, context, deltaSeconds);
        this.applyEnemyEffects(world, context, deltaSeconds);
        this.tickTowerDisableEffects(world, context, deltaSeconds);
        this.clearExpiredVisualEffects(world, context);
        this.tickProjectiles(world, deltaSeconds);

        if (context.state.bankHp <= 0) {
            this.finishMatchAsDefeat(world, context, "Дупло разрушено. Матч проигран.", "The Hollow was destroyed. Match lost.");
            return;
        }

        if (context.pendingSpawns.isEmpty() && context.enemies.isEmpty()) {
            WaveDefinition completedWave = this.resolveWaveDefinition(context, context.activeWaveNumber);
            int completedWaveNumber = completedWave == null ? context.activeWaveNumber : completedWave.index;
            this.trackRunWaveCleared(context);
            this.onTutorialWaveCompleted(world, context, completedWaveNumber);
            if (completedWave != null) {
                this.grantMatchIncome(world, context, this.scaledNonKillIncome(context, completedWave.bonusCurrency), null);
            }
            this.applySeedIdolWaveReward(world, context, completedWaveNumber);
            context.rootsHeartBuffWaveNumber = 0;
            context.rootsHeartBuffMultiplier = 1.0;
            if (!context.snapshot.gameRules.endlessMode && context.activeWaveNumber >= context.snapshot.gameRules.totalWaves) {
                context.state.gameState = GameState.Victory;
                context.state.waveState = WaveState.Finished;
                context.state.gameStarted = false;
                context.state.paused = false;
                context.state.preparationRemainingSeconds = 0.0;
                this.clearInstantAutoStartWarmup(context);
                this.deactivateAcidStorm(world, context);
                this.grantProgressionRewardIfNeeded(world, context, completedWaveNumber, true);
                context.activeWaveNumber = 0;
                context.state.currentWave = this.selectedStartWave(world);
                if (world != null) {
                    this.sendLocalizedWorldMessage(world, "Дупло устояло. Победа!", "The Hollow endured. Victory!");
                    this.playWorldUiSound(world, SOUND_VICTORY);
                    this.refreshVisualizationIfEnabled(world);
                    return;
                }
                this.playWorldUiSound(world, SOUND_VICTORY);
                this.refreshVisualizationIfEnabled(world);
                return;
            }
            this.playWorldUiSound(world, SOUND_WAVE_END);
            context.state.currentWave = context.activeWaveNumber + 1;
            context.state.gameState = GameState.Ready;
            context.state.waveState = WaveState.BuildPhase;
            context.state.gameStarted = true;
            context.state.preparationRemainingSeconds = context.tutorialActive
                ? 0.0
                : Math.max(0.0, this.contractPreparationSeconds(context));
            if (completedWaveNumber == CRIMSON_STORM_START_WAVE - 1) {
                this.activateAcidStorm(world, context, WEATHER_CRIMSON_STORM);
            } else if (completedWaveNumber == ACID_STORM_START_WAVE - 1) {
                this.activateAcidStorm(world, context, WEATHER_ACID_STORM);
            } else {
                this.syncAcidStormActivation(world, context);
            }
            this.advanceSealCursesAfterWave(world, context, completedWaveNumber);
            if (context.tutorialActive) {
                context.state.instantAutoStart = false;
                this.clearInstantAutoStartWarmup(context);
            }
            this.queueRewardChoicesIfNeeded(world, context, completedWaveNumber);
            boolean idolModePromptOpened = context.pendingRewardChoices.isEmpty() && this.openPendingSeedIdolModeChoices(world, context);
            context.activeWaveNumber = 0;
            context.activeWaveTotalEnemies = 0;
            if (world != null) {
                if (!context.tutorialActive) {
                    if (!context.pendingRewardChoices.isEmpty()) {
                        if (this.isDuoMode(context)) {
                            this.sendLocalizedWorldMessage(world, playerRef -> {
                                String firstPicker = TEAM_GREEN.equals(context.rewardPickerTeam)
                                    ? this.choose(playerRef, "зелёной", "green")
                                    : this.choose(playerRef, "синей", "blue");
                                return BankDefenseLocalization.choose(playerRef, "Волна ", "Wave ")
                                    + completedWaveNumber
                                    + BankDefenseLocalization.choose(playerRef, " завершена. Игрок ", " complete. The ")
                                    + firstPicker
                                    + this.choose(playerRef, " команды ещё не выбрал модуль. Его можно взять у оператора.", " team player has not chosen a module yet. It can be taken from the operator.");
                            });
                        } else {
                            this.sendLocalizedWorldMessage(world, playerRef ->
                                BankDefenseLocalization.choose(playerRef, "Волна ", "Wave ")
                                    + completedWaveNumber
                                    + BankDefenseLocalization.choose(playerRef, " завершена. Выберите 1 модуль награды у оператора.", " complete. Choose 1 reward module at the operator.")
                            );
                        }
                    } else if (idolModePromptOpened) {
                        this.sendLocalizedWorldMessage(world, playerRef ->
                            BankDefenseLocalization.choose(playerRef, "Волна ", "Wave ")
                                + completedWaveNumber
                                + BankDefenseLocalization.choose(playerRef, " завершена. Идол урожая ждёт выбора следующего режима.", " complete. The Harvest Idol is waiting for its next mode choice.")
                        );
                    } else if (context.state.instantAutoStart || context.state.preparationRemainingSeconds <= 0.0) {
                        this.sendLocalizedWorldMessage(world, playerRef ->
                            BankDefenseLocalization.choose(playerRef, "Волна ", "Wave ")
                                + completedWaveNumber
                                + BankDefenseLocalization.choose(playerRef, " завершена. Автостарт без ожидания включён. Следующая волна начинается.", " complete. Instant auto-start is enabled. The next wave begins now.")
                        );
                        try {
                            this.startPreparedWave(world, context, false);
                        } catch (IOException ignored) {
                        }
                    } else if (context.state.preparationRemainingSeconds > 0.0) {
                        this.sendLocalizedWorldMessage(world, playerRef ->
                            BankDefenseLocalization.choose(playerRef, "Волна ", "Wave ")
                                + completedWaveNumber
                                + BankDefenseLocalization.choose(playerRef, " завершена. Следующая волна: ", " complete. Next wave: ")
                                + context.state.currentWave
                                + ". "
                                + BankDefenseLocalization.choose(playerRef, "Подготовка ", "Preparation ")
                                + (int)Math.ceil(context.state.preparationRemainingSeconds)
                                + BankDefenseLocalization.choose(playerRef, "с.", "s.")
                        );
                    }
                }
                this.refreshVisualizationIfEnabled(world);
                return;
            }
            if (context.tutorialActive) {
                // В обучении хватает верхнего квеста и диалогов Квибека, без дублирующего чата.
            } else if (!context.pendingRewardChoices.isEmpty() || idolModePromptOpened) {
            } else if (context.state.instantAutoStart || context.state.preparationRemainingSeconds <= 0.0) {
                try {
                    this.startPreparedWave(world, context, false);
                } catch (IOException ignored) {
                }
            } else if (context.state.preparationRemainingSeconds > 0.0) {
            }
            this.refreshVisualizationIfEnabled(world);
        }
    }

    public void tickVisualization(World world, double deltaSeconds) {
        PresentationState presentation = this.presentationsByWorld.get(this.worldKey(world));
        if (presentation == null || !presentation.enabled) {
            return;
        }
        this.syncRealtimeVisuals(world, presentation, deltaSeconds);
        this.tickProjectileVisuals(world, presentation, deltaSeconds);
        presentation.accumulatedSeconds += deltaSeconds;
        double refreshInterval = this.hasActiveMatch(world) ? VISUAL_REFRESH_ACTIVE_SECONDS : VISUAL_REFRESH_IDLE_SECONDS;
        if (presentation.accumulatedSeconds < refreshInterval) {
            return;
        }
        presentation.accumulatedSeconds = 0.0;
        try {
            this.tickPresentation(world, presentation);
        } catch (IOException ignored) {
        }
    }

    public List<String> validateWorld(World world) throws IOException {
        BankDefenseRepository.Snapshot snapshot = this.repository.loadSnapshot(world);
        List<String> issues = new ArrayList<>();

        int regularTowerCount = 0;
        int superTowerCount = 0;
        int trapTowerCount = 0;
        Set<String> towerIds = new HashSet<>();
        for (TowerDefinition tower : snapshot.towers.towers) {
            if (tower.id == null || tower.id.isBlank()) {
                issues.add("У башни отсутствует id.");
                continue;
            }
            if (!towerIds.add(tower.id)) {
                issues.add("Повторяется id башни: '" + tower.id + "'.");
            }
            if (tower.levels.isEmpty()) {
                issues.add("У башни '" + tower.id + "' нет уровней.");
                continue;
            }
            if (tower.superTower) {
                superTowerCount++;
            } else if (tower.trapTower) {
                trapTowerCount++;
            } else {
                regularTowerCount++;
            }

            for (int index = 0; index < tower.levels.size(); index++) {
                TowerLevel level = tower.levels.get(index);
                if (level.level != index + 1) {
                    issues.add("У башни '" + tower.id + "' нарушен порядок уровней.");
                    break;
                }
                if (level.range < 0.0) {
                    issues.add("У башни '" + tower.id + "' найден отрицательный радиус атаки.");
                    break;
                }
            }
        }

        if (regularTowerCount < 6) {
            issues.add("Ожидалось минимум 6 обычных башен, найдено: " + regularTowerCount + ".");
        }
        if (superTowerCount < 3) {
            issues.add("Ожидалось минимум 3 супер-башни, найдено: " + superTowerCount + ".");
        }
        if (trapTowerCount < 3) {
            issues.add("Ожидалось минимум 3 ловушки, найдено: " + trapTowerCount + ".");
        }

        if (snapshot.modules.modules.size() != 9) {
            issues.add("Ожидалось 9 модулей, найдено: " + snapshot.modules.modules.size() + ".");
        }

        if (snapshot.enemies.enemies.size() < 9) {
            issues.add("Ожидалось минимум 9 врагов, найдено: " + snapshot.enemies.enemies.size() + ".");
        }

        Set<String> enemyIds = new HashSet<>();
        for (EnemyDefinition enemy : snapshot.enemies.enemies) {
            if (enemy.id == null || enemy.id.isBlank()) {
                issues.add("У врага без имени отсутствует id.");
                continue;
            }
            if (!enemyIds.add(enemy.id)) {
                issues.add("Повторяется id врага: '" + enemy.id + "'.");
            }
        }

        for (WaveDefinition wave : snapshot.waves.waves) {
            if (wave.spawns.isEmpty()) {
                issues.add("Волна " + wave.index + " не содержит спавнов.");
            }
            for (WaveSpawn spawn : wave.spawns) {
                if (!enemyIds.contains(spawn.enemyId)) {
                    issues.add("Волна " + wave.index + " ссылается на неизвестного врага '" + spawn.enemyId + "'.");
                }
            }
        }

        boolean hasAnySpawn = snapshot.map.spawnPoint != null
            || snapshot.map.spawnPointA != null
            || snapshot.map.spawnPointB != null
            || snapshot.map.spawnPointC != null;
        if (!hasAnySpawn) {
            issues.add("Не задан ни один spawn marker. Добавь хотя бы spawn_a.");
        }
        if (snapshot.map.bankCenter == null) {
            issues.add("Не задан marker bank.");
        }
        if (snapshot.map.vaultPoint == null) {
            issues.add("Не задан marker vault.");
        }
        if (snapshot.map.playerStart == null) {
            issues.add("Не задан marker playerstart.");
        }
        boolean hasAnyRoute = (snapshot.map.routePoints != null && !snapshot.map.routePoints.isEmpty())
            || (snapshot.map.routePointsA != null && !snapshot.map.routePointsA.isEmpty())
            || (snapshot.map.routePointsB != null && !snapshot.map.routePointsB.isEmpty())
            || (snapshot.map.routePointsC != null && !snapshot.map.routePointsC.isEmpty());
        if (!hasAnyRoute) {
            issues.add("Маршрут врагов пуст. Добавь точки маршрута хотя бы для линии A.");
        }

        Set<String> slotIds = new HashSet<>();
        Set<String> slotPositions = new HashSet<>();
        for (BuildSlot slot : snapshot.buildSlots.slots) {
            if (slot.id == null || slot.id.isBlank()) {
                issues.add("Найден слот без id.");
            } else if (!slotIds.add(slot.id)) {
                issues.add("Повторяется id слота: '" + slot.id + "'.");
            }
            if (slot.position == null) {
                issues.add("У слота '" + (slot.id == null ? "<без id>" : slot.id) + "' не задана позиция.");
            } else {
                String positionKey = this.key(slot.position);
                if (!slotPositions.add(positionKey)) {
                    issues.add("Повторяется позиция слота: " + positionKey + ".");
                }
            }
        }

        return issues;
    }

    public List<String> validateDuoWorld(World world) throws IOException {
        BankDefenseRepository.Snapshot snapshot = this.loadDuoSnapshot(world);
        List<String> issues = new ArrayList<>();
        if (snapshot == null) {
            issues.add("Duo snapshot is unavailable.");
            return issues;
        }
        String validationError = this.validateDuoSetup(snapshot.map, snapshot.buildSlots);
        if (validationError != null && !validationError.isBlank()) {
            issues.add(validationError);
        }
        if (this.duoTransitionPoint(snapshot.map) == null) {
            issues.add("Duo NPC point is missing.");
        }
        if (snapshot.map.duoTeamPoint == null) {
            issues.add("Duo team selector point is missing.");
        }
        if (snapshot.map.duoTeleportPoint == null) {
            issues.add("Duo teleporter NPC point is missing.");
        }
        if (snapshot.map.duoStatisticsPoint == null) {
            issues.add("Duo stats point is missing.");
        }
        if (snapshot.map.duoTeleportTarget == null) {
            issues.add("Duo teleport target is missing.");
        }
        if (snapshot.map.soloTeleportTarget == null) {
            issues.add("Solo teleport target is missing.");
        }
        if (snapshot.map.duoChestSpawnPoints == null || snapshot.map.duoChestSpawnPoints.isEmpty()) {
            issues.add("No Duo chest points were configured.");
        }
        boolean hasBlueSlots = false;
        boolean hasGreenSlots = false;
        boolean hasSharedSuper = false;
        for (BuildSlot slot : snapshot.buildSlots.slots) {
            if (slot == null) {
                continue;
            }
            String ownerTeam = this.slotOwnerTeam(slot);
            if (this.isSuperSlot(slot) && TEAM_SHARED.equals(ownerTeam)) {
                hasSharedSuper = true;
            } else if (TEAM_BLUE.equals(ownerTeam)) {
                hasBlueSlots = true;
            } else if (TEAM_GREEN.equals(ownerTeam)) {
                hasGreenSlots = true;
            }
        }
        if (!hasBlueSlots) {
            issues.add("No blue Duo slots were found.");
        }
        if (!hasGreenSlots) {
            issues.add("No green Duo slots were found.");
        }
        if (!hasSharedSuper) {
            issues.add("No shared Duo super slots were found.");
        }
        return issues;
    }

    private MatchContext getOrCreateContext(World world) throws IOException {
        MatchContext existing = this.matchesByWorld.get(this.worldKey(world));
        if (existing != null) {
            return existing;
        }
        MatchContext created = this.createFreshContext(world);
        this.matchesByWorld.put(this.worldKey(world), created);
        return created;
    }

    private void ensurePlayerBoundContext(World world) throws IOException {
        if (world == null || this.primaryPlayerRef(world) == null) {
            return;
        }
        MatchContext existing = this.matchesByWorld.get(this.worldKey(world));
        if (existing == null) {
            this.matchesByWorld.put(this.worldKey(world), this.createFreshContext(world));
            return;
        }
        String existingPlayerUuid = existing.progression == null ? null : existing.progression.playerUuid;
        if (existingPlayerUuid == null || existingPlayerUuid.isBlank() || "offline".equalsIgnoreCase(existingPlayerUuid)) {
            this.matchesByWorld.put(this.worldKey(world), this.createFreshContext(world));
        }
    }

    private MatchContext createFreshContext(World world) throws IOException {
        String baselineForcedWeatherId = this.normalizedForcedWeatherId(world.getWorldConfig().getForcedWeather());
        if (WEATHER_ACID_STORM.equals(baselineForcedWeatherId) || WEATHER_CRIMSON_STORM.equals(baselineForcedWeatherId)) {
            this.setWorldForcedWeather(world, null);
            baselineForcedWeatherId = null;
        }
        BankDefenseRepository.Snapshot rawSnapshot = this.repository.loadSnapshot(world);
        TutorialState tutorial = this.tutorialState(world);
        boolean tutorialActive = tutorial.active;
        MatchMode matchMode = tutorialActive ? MatchMode.Solo : this.selectedMatchMode(world);
        BankDefenseRepository.Snapshot snapshot = this.effectiveSnapshot(world, rawSnapshot, tutorial);
        PlayerProgressionState progression = this.loadProgressionForWorld(world);
        progression = this.migrateProgressionState(snapshot, progression);
        ContractDefinition activeContract = tutorialActive ? this.resolveSelectedContract(snapshot, world, null) : this.resolveSelectedContract(snapshot, world, progression);
        DifficultyProfile difficulty = tutorialActive
            ? this.resolveDifficultyProfile(DIFFICULTY_EASY)
            : this.resolveDifficultyProfile(this.selectedDifficultyIdsByWorld.getOrDefault(this.worldKey(world), DIFFICULTY_NORMAL));
        int startingCurrency = tutorialActive ? 80 : this.modifiedStartingCurrency(snapshot.gameRules, progression, activeContract, snapshot, difficulty);
        int maxBankHp = tutorialActive ? Math.max(1, snapshot.gameRules.bankHp) : this.modifiedBankHp(snapshot.gameRules, snapshot, progression);
        MatchContext context = new MatchContext(
            snapshot,
            progression == null ? null : progression.playerUuid,
            progression,
            activeContract,
            difficulty,
            maxBankHp,
            tutorialActive,
            matchMode
        );
        context.baselineForcedWeatherId = baselineForcedWeatherId;
        context.duoSoloTestEnabled = this.isDuoSoloTestEnabled(world);
        if (matchMode == MatchMode.Duo) {
            this.assignDuoTeams(world, context);
        }
        context.state.gameState = GameState.Ready;
        context.state.waveState = WaveState.BuildPhase;
        context.state.currentWave = this.selectedStartWave(world);
        if (matchMode == MatchMode.Duo) {
            context.blueCurrency = startingCurrency;
            context.greenCurrency = startingCurrency;
            this.syncTotalCurrency(context);
        } else {
            context.state.currency = startingCurrency;
        }
        context.state.bankHp = maxBankHp;
        context.state.maxBankHp = maxBankHp;
        context.state.gameStarted = false;
        context.state.paused = false;
        context.state.preparationRemainingSeconds = 0.0;
        return context;
    }

    private int selectedStartWave(World world) {
        return Math.max(1, this.selectedStartWavesByWorld.getOrDefault(this.worldKey(world), Integer.valueOf(1)).intValue());
    }

    private BankDefenseRepository.Snapshot effectiveSnapshot(World world, BankDefenseRepository.Snapshot baseSnapshot, TutorialState tutorial) {
        if (baseSnapshot == null || tutorial == null || !tutorial.active) {
            if (baseSnapshot == null) {
                return null;
            }
            if (this.selectedMatchMode(world) == MatchMode.Duo) {
                MapConfig duoMap = this.buildDuoLayoutMap(baseSnapshot.map);
                BuildSlotsConfig duoSlots = this.filterBuildSlotsForMode(baseSnapshot.buildSlots, MatchMode.Duo, false);
                return new BankDefenseRepository.Snapshot(
                    baseSnapshot.gameRules,
                    baseSnapshot.towers,
                    baseSnapshot.enemies,
                    baseSnapshot.modules,
                    baseSnapshot.waves,
                    baseSnapshot.branding,
                    baseSnapshot.contracts,
                    baseSnapshot.progression,
                    duoMap,
                    duoSlots
                );
            }
            BuildSlotsConfig filteredSlots = this.filterBuildSlotsForMode(baseSnapshot.buildSlots, MatchMode.Solo, false);
            return new BankDefenseRepository.Snapshot(
                baseSnapshot.gameRules,
                baseSnapshot.towers,
                baseSnapshot.enemies,
                baseSnapshot.modules,
                baseSnapshot.waves,
                baseSnapshot.branding,
                baseSnapshot.contracts,
                baseSnapshot.progression,
                baseSnapshot.map,
                filteredSlots
            );
        }
        MapConfig tutorialMap = this.buildTutorialLayoutMap(baseSnapshot.map, tutorial);
        BuildSlotsConfig tutorialSlots = this.buildTutorialSlots(baseSnapshot.buildSlots, tutorialMap);
        return new BankDefenseRepository.Snapshot(
            baseSnapshot.gameRules,
            baseSnapshot.towers,
            baseSnapshot.enemies,
            baseSnapshot.modules,
            baseSnapshot.waves,
            baseSnapshot.branding,
            baseSnapshot.contracts,
            baseSnapshot.progression,
            tutorialMap,
            tutorialSlots
        );
    }

    private MapConfig buildDuoLayoutMap(MapConfig source) {
        MapConfig map = new MapConfig();
        if (source == null) {
            return map;
        }
        Vec3i duoSpawnBlue = this.inferredDuoSpawnPoint(source, TEAM_BLUE);
        Vec3i duoSpawnGreen = this.inferredDuoSpawnPoint(source, TEAM_GREEN);
        Vec3i duoVaultPoint = this.inferredDuoVaultPoint(source);
        Vec3i duoBankCenter = this.inferredDuoBankCenter(source, duoSpawnBlue, duoSpawnGreen, duoVaultPoint);
        Vec3i duoPlayerStartBlue = source.duoPlayerStartBlue != null ? source.duoPlayerStartBlue : this.inferredDuoPlayerStart(duoBankCenter, TEAM_BLUE);
        Vec3i duoPlayerStartGreen = source.duoPlayerStartGreen != null ? source.duoPlayerStartGreen : this.inferredDuoPlayerStart(duoBankCenter, TEAM_GREEN);
        Vec3i duoControlPoint = this.preferDuoArenaPoint(source, source.controlPoint, this.offsetPoint(duoBankCenter, -10, 0, 0));
        Vec3i duoVendorPoint = this.preferDuoArenaPoint(source, source.vendorPoint, this.offsetPoint(duoBankCenter, -10, 0, 4));
        Vec3i duoTeamPoint = source.duoTeamPoint != null
            ? source.duoTeamPoint
            : this.preferDuoArenaPoint(source, source.modePoint, this.offsetPoint(duoBankCenter, -14, 0, 0));
        Vec3i duoModePoint = source.duoTeleportPoint != null
            ? source.duoTeleportPoint
            : this.preferDuoArenaPoint(source, null, this.offsetPoint(duoBankCenter, -14, 0, 4));
        Vec3i duoStatsPoint = source.duoStatisticsPoint != null
            ? source.duoStatisticsPoint
            : this.preferDuoArenaPoint(source, source.statisticsPoint, this.offsetPoint(duoBankCenter, -10, 0, -4));
        Vec3i duoNpcPoint = source.duoQubeCorePoint != null
            ? source.duoQubeCorePoint
            : this.preferDuoArenaPoint(source, source.duoPoint, this.offsetPoint(duoBankCenter, -18, 0, 0));
        Vec3i duoTeleportTarget = source.duoTeleportTarget != null
            ? source.duoTeleportTarget
            : duoTeamPoint != null
                ? this.offsetPoint(duoTeamPoint, 2, 0, 0)
                : duoPlayerStartBlue != null ? duoPlayerStartBlue : duoBankCenter;
        Vec3i soloTeleportTarget = source.soloTeleportTarget != null
            ? source.soloTeleportTarget
            : source.playerStart != null ? source.playerStart : source.bankCenter;
        map.strictNpcMarkers = source.strictNpcMarkers;
        map.worldName = source.worldName;
        map.selectedModeId = MODE_DUO;
        map.spawnPoint = duoSpawnBlue;
        map.spawnPointA = duoSpawnBlue;
        map.spawnPointB = null;
        map.spawnPointC = duoSpawnGreen;
        map.bankCenter = duoBankCenter != null ? duoBankCenter : source.bankCenter;
        map.vaultPoint = duoVaultPoint != null ? duoVaultPoint : source.vaultPoint;
        map.playerStart = duoPlayerStartBlue != null ? duoPlayerStartBlue : source.playerStart;
        map.playerStartYaw = source.duoPlayerStartBlueYaw != null ? source.duoPlayerStartBlueYaw : source.playerStartYaw;
        map.controlPoint = duoControlPoint;
        map.previousControlPoint = duoControlPoint;
        map.controlYaw = this.samePoint(duoControlPoint, source.controlPoint) ? source.controlYaw : null;
        map.vendorPoint = duoVendorPoint;
        map.previousVendorPoint = duoVendorPoint;
        map.vendorYaw = this.samePoint(duoVendorPoint, source.vendorPoint) ? source.vendorYaw : null;
        map.modePoint = duoModePoint;
        map.previousModePoint = duoModePoint;
        map.modeYaw = this.samePoint(duoModePoint, source.duoTeleportPoint)
            ? source.duoTeleportYaw
            : this.samePoint(duoModePoint, source.modePoint) ? source.modeYaw : null;
        map.duoPoint = duoNpcPoint;
        map.previousDuoPoint = duoNpcPoint;
        map.duoYaw = this.samePoint(duoNpcPoint, source.duoPoint) ? source.duoYaw : null;
        map.soloQubeCorePoint = source.soloQubeCorePoint != null ? source.soloQubeCorePoint : source.duoPoint;
        map.previousSoloQubeCorePoint = map.soloQubeCorePoint;
        map.soloQubeCoreYaw = source.soloQubeCorePoint != null ? source.soloQubeCoreYaw : source.duoYaw;
        map.duoQubeCorePoint = duoNpcPoint;
        map.previousDuoQubeCorePoint = duoNpcPoint;
        map.duoQubeCoreYaw = this.samePoint(duoNpcPoint, source.duoQubeCorePoint)
            ? source.duoQubeCoreYaw
            : this.samePoint(duoNpcPoint, source.duoPoint) ? source.duoYaw : null;
        map.duoTeamPoint = duoTeamPoint;
        map.previousDuoTeamPoint = duoTeamPoint;
        map.duoTeamYaw = this.samePoint(duoTeamPoint, source.duoTeamPoint)
            ? source.duoTeamYaw
            : this.samePoint(duoTeamPoint, source.modePoint) ? source.modeYaw : null;
        map.duoTeleportPoint = duoModePoint;
        map.previousDuoTeleportPoint = duoModePoint;
        map.duoTeleportYaw = this.samePoint(duoModePoint, source.duoTeleportPoint)
            ? source.duoTeleportYaw
            : this.samePoint(duoModePoint, source.modePoint) ? source.modeYaw : null;
        map.duoPlayerStartBlue = duoPlayerStartBlue;
        map.duoPlayerStartBlueYaw = source.duoPlayerStartBlueYaw;
        map.duoPlayerStartGreen = duoPlayerStartGreen;
        map.duoPlayerStartGreenYaw = source.duoPlayerStartGreenYaw;
        map.duoSpawnPointBlue = duoSpawnBlue;
        map.duoSpawnPointGreen = duoSpawnGreen;
        map.duoBankCenter = duoBankCenter;
        map.duoVaultPoint = duoVaultPoint;
        map.duoTeleportTarget = duoTeleportTarget;
        map.soloTeleportTarget = soloTeleportTarget;
        map.duoSealNodeBluePoint = source.duoSealNodeBluePoint;
        map.duoSealNodeGreenPoint = source.duoSealNodeGreenPoint;
        map.statisticsPoint = duoStatsPoint;
        map.previousStatisticsPoint = duoStatsPoint;
        map.statisticsYaw = this.samePoint(duoStatsPoint, source.duoStatisticsPoint)
            ? source.duoStatisticsYaw
            : this.samePoint(duoStatsPoint, source.statisticsPoint) ? source.statisticsYaw : null;
        map.duoStatisticsPoint = duoStatsPoint;
        map.previousDuoStatisticsPoint = duoStatsPoint;
        map.duoStatisticsYaw = this.samePoint(duoStatsPoint, source.duoStatisticsPoint)
            ? source.duoStatisticsYaw
            : this.samePoint(duoStatsPoint, source.statisticsPoint) ? source.statisticsYaw : null;
        map.duoRouteBlue = source.duoRouteBlue == null ? new ArrayList<>() : new ArrayList<>(source.duoRouteBlue);
        map.duoRouteGreen = source.duoRouteGreen == null ? new ArrayList<>() : new ArrayList<>(source.duoRouteGreen);
        map.duoChestSpawnPoints = source.duoChestSpawnPoints == null ? new ArrayList<>() : new ArrayList<>(source.duoChestSpawnPoints);
        map.routePoints = new ArrayList<>();
        map.routePointsA = new ArrayList<>(map.duoRouteBlue);
        map.routePointsB = new ArrayList<>();
        map.routePointsC = new ArrayList<>(map.duoRouteGreen);
        map.chestSpawnPoints = new ArrayList<>(map.duoChestSpawnPoints);
        return map;
    }

    private Vec3i inferredDuoSpawnPoint(MapConfig source, String teamId) {
        if (source == null) {
            return null;
        }
        Vec3i explicit = TEAM_GREEN.equals(this.normalizeTeam(teamId)) ? source.duoSpawnPointGreen : source.duoSpawnPointBlue;
        if (explicit != null) {
            return explicit;
        }
        return this.firstRoutePoint(TEAM_GREEN.equals(this.normalizeTeam(teamId)) ? source.duoRouteGreen : source.duoRouteBlue);
    }

    private Vec3i inferredDuoVaultPoint(MapConfig source) {
        if (source == null) {
            return null;
        }
        if (source.duoVaultPoint != null) {
            return source.duoVaultPoint;
        }
        Vec3i blueEnd = this.lastRoutePoint(source.duoRouteBlue);
        Vec3i greenEnd = this.lastRoutePoint(source.duoRouteGreen);
        Vec3i midpoint = this.midpoint(blueEnd, greenEnd);
        if (midpoint != null) {
            return midpoint;
        }
        return blueEnd != null ? blueEnd : greenEnd;
    }

    private Vec3i inferredDuoBankCenter(MapConfig source, Vec3i duoSpawnBlue, Vec3i duoSpawnGreen, Vec3i duoVaultPoint) {
        if (source == null) {
            return null;
        }
        if (source.duoBankCenter != null) {
            return source.duoBankCenter;
        }
        List<Vec3i> anchors = new ArrayList<>();
        this.appendPoints(anchors, source.duoRouteBlue);
        this.appendPoints(anchors, source.duoRouteGreen);
        Vec3i average = this.averagePoint(anchors);
        if (average != null) {
            return average;
        }
        Vec3i spawnMid = this.midpoint(duoSpawnBlue, duoSpawnGreen);
        Vec3i center = this.midpoint(spawnMid, duoVaultPoint);
        if (center != null) {
            return center;
        }
        return duoVaultPoint != null ? duoVaultPoint : spawnMid;
    }

    private Vec3i inferredDuoPlayerStart(Vec3i duoBankCenter, String teamId) {
        if (duoBankCenter == null) {
            return null;
        }
        String normalizedTeam = this.normalizeTeam(teamId);
        return TEAM_GREEN.equals(normalizedTeam)
            ? this.offsetPoint(duoBankCenter, -6, 0, 5)
            : this.offsetPoint(duoBankCenter, -6, 0, -5);
    }

    private Vec3i preferDuoArenaPoint(MapConfig source, Vec3i explicitPoint, Vec3i fallbackPoint) {
        if (explicitPoint != null && this.isPointNearDuoArena(source, explicitPoint)) {
            return explicitPoint;
        }
        return fallbackPoint;
    }

    private boolean isPointNearDuoArena(MapConfig source, Vec3i point) {
        if (source == null || point == null) {
            return false;
        }
        double radiusSquared = 96.0 * 96.0;
        if (source.duoRouteBlue != null) {
            for (Vec3i routePoint : source.duoRouteBlue) {
                if (routePoint != null && routePoint.distanceSquaredTo(point) <= radiusSquared) {
                    return true;
                }
            }
        }
        if (source.duoRouteGreen != null) {
            for (Vec3i routePoint : source.duoRouteGreen) {
                if (routePoint != null && routePoint.distanceSquaredTo(point) <= radiusSquared) {
                    return true;
                }
            }
        }
        return (source.duoBankCenter != null && source.duoBankCenter.distanceSquaredTo(point) <= radiusSquared)
            || (source.duoVaultPoint != null && source.duoVaultPoint.distanceSquaredTo(point) <= radiusSquared)
            || (source.duoSpawnPointBlue != null && source.duoSpawnPointBlue.distanceSquaredTo(point) <= radiusSquared)
            || (source.duoSpawnPointGreen != null && source.duoSpawnPointGreen.distanceSquaredTo(point) <= radiusSquared);
    }

    private void appendPoints(List<Vec3i> target, List<Vec3i> points) {
        if (target == null || points == null) {
            return;
        }
        for (Vec3i point : points) {
            if (point != null) {
                target.add(point);
            }
        }
    }

    private Vec3i averagePoint(List<Vec3i> points) {
        if (points == null || points.isEmpty()) {
            return null;
        }
        long sumX = 0L;
        long sumY = 0L;
        long sumZ = 0L;
        int count = 0;
        for (Vec3i point : points) {
            if (point == null) {
                continue;
            }
            sumX += point.x;
            sumY += point.y;
            sumZ += point.z;
            count++;
        }
        if (count <= 0) {
            return null;
        }
        return new Vec3i(
            (int)Math.round(sumX / (double)count),
            (int)Math.round(sumY / (double)count),
            (int)Math.round(sumZ / (double)count)
        );
    }

    private Vec3i averagePoint(Vec3i first, Vec3i second) {
        if (first == null && second == null) {
            return null;
        }
        if (first == null) {
            return second;
        }
        if (second == null) {
            return first;
        }
        return new Vec3i(
            (int)Math.round((first.x + second.x) / 2.0),
            (int)Math.round((first.y + second.y) / 2.0),
            (int)Math.round((first.z + second.z) / 2.0)
        );
    }

    private Vec3i firstRoutePoint(List<Vec3i> route) {
        if (route == null) {
            return null;
        }
        for (Vec3i point : route) {
            if (point != null) {
                return point;
            }
        }
        return null;
    }

    private Vec3i lastRoutePoint(List<Vec3i> route) {
        if (route == null) {
            return null;
        }
        for (int i = route.size() - 1; i >= 0; i--) {
            Vec3i point = route.get(i);
            if (point != null) {
                return point;
            }
        }
        return null;
    }

    private Vec3i midpoint(Vec3i a, Vec3i b) {
        if (a == null) {
            return b;
        }
        if (b == null) {
            return a;
        }
        return new Vec3i(
            (int)Math.round((a.x + b.x) / 2.0),
            (int)Math.round((a.y + b.y) / 2.0),
            (int)Math.round((a.z + b.z) / 2.0)
        );
    }

    private Vec3i offsetPoint(Vec3i base, int dx, int dy, int dz) {
        if (base == null) {
            return null;
        }
        return new Vec3i(base.x + dx, base.y + dy, base.z + dz);
    }

    private MapConfig buildTutorialLayoutMap(MapConfig source, TutorialState tutorial) {
        MapConfig map = new MapConfig();
        if (source == null) {
            return map;
        }
        map.strictNpcMarkers = true;
        map.worldName = source.worldName;
        map.spawnPoint = source.tutorialSpawnPointA;
        map.spawnPointA = source.tutorialSpawnPointA;
        map.spawnPointB = source.tutorialSpawnPointB;
        map.spawnPointC = source.tutorialSpawnPointC;
        map.bankCenter = source.tutorialBankCenter;
        map.vaultPoint = source.tutorialVaultPoint;
        map.playerStart = source.tutorialStartPoint;
        map.playerStartYaw = source.tutorialStartYaw;
        map.controlPoint = source.tutorialControlPoint;
        map.controlYaw = source.tutorialControlYaw;
        map.vendorPoint = source.tutorialVendorPoint;
        map.vendorYaw = source.tutorialVendorYaw;
        map.routePointsA = source.tutorialRoutePointsA == null ? new ArrayList<>() : new ArrayList<>(source.tutorialRoutePointsA);
        map.routePointsB = source.tutorialRoutePointsB == null ? new ArrayList<>() : new ArrayList<>(source.tutorialRoutePointsB);
        map.routePointsC = source.tutorialRoutePointsC == null ? new ArrayList<>() : new ArrayList<>(source.tutorialRoutePointsC);
        map.chestSpawnPoints = new ArrayList<>();
        Vec3i tutorialChestPoint = this.tutorialChestPoint(source);
        if (tutorialChestPoint != null) {
            map.chestSpawnPoints.add(tutorialChestPoint);
        }
        return map;
    }

    private BuildSlotsConfig buildTutorialSlots(BuildSlotsConfig source, MapConfig tutorialMap) {
        BuildSlotsConfig tutorialSlots = this.filterBuildSlotsForMode(source, MatchMode.Solo, true);
        int explicitTutorialTrapCount = 0;
        int tutorialTrapCount = 0;
        for (BuildSlot slot : tutorialSlots.slots) {
            if (slot != null && "tutorial_trap".equals(slot.slotType)) {
                explicitTutorialTrapCount++;
            }
            if (slot != null && this.isTrapSlot(slot)) {
                tutorialTrapCount++;
            }
        }
        if (tutorialMap != null && tutorialMap.tutorialTrapLayoutCustom) {
            return tutorialSlots;
        }
        if (explicitTutorialTrapCount > 0) {
            return tutorialSlots;
        }
        if (tutorialTrapCount >= 3) {
            return tutorialSlots;
        }
        List<Vec3i> fallbackTrapPoints = this.defaultTutorialTrapPoints(tutorialMap);
        int nextIndex = tutorialTrapCount + 1;
        for (Vec3i point : fallbackTrapPoints) {
            if (point == null || this.containsSlotAtPoint(tutorialSlots, point)) {
                continue;
            }
            BuildSlot slot = new BuildSlot();
            slot.id = String.format(Locale.ROOT, "tutorial_trap_%02d", nextIndex);
            slot.label = "Tutorial Trap " + nextIndex;
            slot.position = point;
            slot.slotType = "tutorial_trap";
            slot.modulesAllowed = false;
            slot.allowedTowerIds = new ArrayList<>(List.of("root_snare", "spore_mine", "frost_seal"));
            tutorialSlots.slots.add(slot);
            nextIndex++;
            if (nextIndex > 3) {
                break;
            }
        }
        return tutorialSlots;
    }

    private void reconcileContextWithSnapshot(MatchContext context, BankDefenseRepository.Snapshot snapshot) {
        if (context == null || snapshot == null || snapshot.buildSlots == null || snapshot.buildSlots.slots == null) {
            return;
        }
        Set<String> validSlotIds = new HashSet<>();
        for (BuildSlot slot : snapshot.buildSlots.slots) {
            if (slot != null && slot.id != null && !slot.id.isBlank()) {
                validSlotIds.add(slot.id);
            }
        }
        context.placedTowers.entrySet().removeIf(entry -> entry == null || entry.getKey() == null || !validSlotIds.contains(entry.getKey()));
    }

    private boolean containsSlotAtPoint(BuildSlotsConfig slots, Vec3i point) {
        if (slots == null || slots.slots == null || point == null) {
            return false;
        }
        for (BuildSlot slot : slots.slots) {
            if (slot != null && this.samePoint(slot.position, point)) {
                return true;
            }
        }
        return false;
    }

    private Vec3i tutorialChestPoint(MapConfig source) {
        if (source == null) {
            return null;
        }
        if (source.tutorialChestPoint != null) {
            return source.tutorialChestPoint;
        }
        if (source.tutorialWizardPoint == null) {
            return null;
        }
        return this.offsetPoint(source.tutorialWizardPoint, -4, 0, 0);
    }

    private List<Vec3i> defaultTutorialTrapPoints(MapConfig tutorialMap) {
        List<Vec3i> points = new ArrayList<>();
        if (tutorialMap == null) {
            return points;
        }
        Vec3i laneATrap = this.averagePoint(tutorialMap.spawnPointA, firstPoint(tutorialMap.routePointsA));
        Vec3i laneBTrap = this.averagePoint(tutorialMap.spawnPointB, firstPoint(tutorialMap.routePointsB));
        Vec3i mergedEntry = tutorialMap.routePointsA != null && tutorialMap.routePointsA.size() > 1
            ? tutorialMap.routePointsA.get(1)
            : firstPoint(tutorialMap.routePointsA);
        Vec3i mergedTrap = this.averagePoint(mergedEntry, tutorialMap.vaultPoint);
        if (laneATrap != null) {
            points.add(laneATrap);
        }
        if (laneBTrap != null) {
            points.add(laneBTrap);
        }
        if (mergedTrap != null) {
            points.add(mergedTrap);
        }
        return points;
    }

    private static Vec3i firstPoint(List<Vec3i> points) {
        return points == null || points.isEmpty() ? null : points.get(0);
    }

    private BuildSlotsConfig filterBuildSlotsForMode(BuildSlotsConfig source, MatchMode mode, boolean tutorialActive) {
        BuildSlotsConfig filtered = new BuildSlotsConfig();
        if (source == null || source.slots == null) {
            return filtered;
        }
        for (BuildSlot slot : source.slots) {
            if (slot == null) {
                continue;
            }
            boolean tutorialSlot = this.isTutorialSlot(slot);
            if (tutorialActive != tutorialSlot) {
                continue;
            }
            if (!tutorialActive && !this.slotMatchesMode(slot, mode)) {
                continue;
            }
            filtered.slots.add(slot);
        }
        return filtered;
    }

    private boolean slotMatchesMode(BuildSlot slot, MatchMode mode) {
        if (slot == null) {
            return false;
        }
        String layout = slot.layout == null ? "" : slot.layout.trim().toLowerCase(Locale.ROOT);
        if (layout.isBlank()) {
            layout = MODE_SOLO;
        }
        if ("both".equals(layout)) {
            return true;
        }
        if (mode == MatchMode.Duo) {
            return MODE_DUO.equals(layout);
        }
        return MODE_SOLO.equals(layout);
    }

    private boolean isTutorialSlot(BuildSlot slot) {
        if (slot == null || slot.slotType == null || slot.slotType.isBlank()) {
            return false;
        }
        return slot.slotType.toLowerCase(Locale.ROOT).startsWith("tutorial");
    }

    private boolean tutorialControlVisible(TutorialState tutorial) {
        if (tutorial == null || !tutorial.active) {
            return false;
        }
        return switch (tutorial.stage) {
            case PrepareFirstWave, WaitFirstWaveFinish, BuildSecondTower, WaitSecondWaveFinish, ChooseRewardModule,
                FillAllSlots, WaitTwoLaneWaveFinish, PlaceMonolith, WaitBossWaveFinish,
                PrepareTrapWave, WaitTrapWaveFinish, SpendCores, ReturnAfterProgression -> true;
            default -> false;
        };
    }

    private boolean tutorialVendorVisible(TutorialState tutorial) {
        if (tutorial == null || !tutorial.active) {
            return false;
        }
        return tutorial.stage == TutorialStage.SpendCores;
    }

    private void ensureBonusChestsSpawned(MatchContext context) {
        if (context == null || context.bonusChestsSpawned || context.tutorialActive) {
            return;
        }
        context.activeChests.clear();
        List<Vec3i> candidates = new ArrayList<>(context.snapshot.map.chestSpawnPoints);
        candidates.removeIf(point -> point == null);
        if (candidates.isEmpty()) {
            context.bonusChestsSpawned = true;
            return;
        }
        Collections.shuffle(candidates, ThreadLocalRandom.current());
        int index = 0;
        int moneyChestCount = this.contractMoneyChestCount(context);
        for (int i = 0; i < moneyChestCount && index < candidates.size(); i++, index++) {
            Vec3i point = candidates.get(index);
            context.activeChests.put("money:" + i, new ActiveChest("money:" + i, point, ChestType.Money));
        }
        for (int i = 0; i < CORE_CHEST_COUNT && index < candidates.size(); i++, index++) {
            Vec3i point = candidates.get(index);
            context.activeChests.put("core:" + i, new ActiveChest("core:" + i, point, ChestType.Core));
        }
        context.bonusChestsSpawned = true;
    }

    private PlayerProgressionState loadProgressionForWorld(World world) throws IOException {
        if (world != null && this.selectedMatchMode(world) == MatchMode.Duo) {
            String sharedProgressionId = "duo-world-" + UUID.nameUUIDFromBytes(this.worldKey(world).getBytes(StandardCharsets.UTF_8));
            return this.repository.loadPlayerProgression(sharedProgressionId);
        }
        PlayerRef owner = this.primaryPlayerRef(world);
        if (owner == null || owner.getUuid() == null) {
            PlayerProgressionState fallback = new PlayerProgressionState();
            fallback.playerUuid = "offline";
            fallback.activeContractId = "none";
            return fallback;
        }
        return this.repository.loadPlayerProgression(owner.getUuid().toString());
    }

    private PlayerProgressionState migrateProgressionState(BankDefenseRepository.Snapshot snapshot, PlayerProgressionState progression) throws IOException {
        if (snapshot == null || progression == null || progression.unlockedNodeIds == null || progression.unlockedNodeIds.isEmpty()) {
            return progression;
        }
        Set<String> validNodeIds = new HashSet<>();
        for (ProgressionNodeDefinition node : snapshot.progression.nodes) {
            if (node != null && node.id != null && !node.id.isBlank()) {
                validNodeIds.add(node.id);
            }
        }

        boolean changed = false;
        int refund = 0;
        List<String> retainedNodeIds = new ArrayList<>();
        Set<String> seenNodeIds = new LinkedHashSet<>();
        for (String nodeId : progression.unlockedNodeIds) {
            if (nodeId == null || nodeId.isBlank()) {
                changed = true;
                continue;
            }
            if (validNodeIds.contains(nodeId)) {
                if (seenNodeIds.add(nodeId)) {
                    retainedNodeIds.add(nodeId);
                } else {
                    changed = true;
                }
                continue;
            }
            refund += LEGACY_PROGRESSION_REFUND_COSTS.getOrDefault(nodeId, 0);
            changed = true;
        }

        if (!changed) {
            return progression;
        }

        progression.unlockedNodeIds.clear();
        progression.unlockedNodeIds.addAll(retainedNodeIds);
        if (refund > 0) {
            progression.cores += refund;
        }
        this.repository.savePlayerProgression(progression);
        return progression;
    }

    private PlayerRef primaryPlayerRef(World world) {
        if (world == null || world.getPlayerRefs().isEmpty()) {
            return null;
        }
        for (PlayerRef playerRef : world.getPlayerRefs()) {
            return playerRef;
        }
        return null;
    }

    private String localizedNpcLabel(World world, String russianName) {
        return switch (russianName) {
            case "Оператор" -> this.tr(world, "npc.operator");
            case "Хранитель улучшений" -> this.tr(world, "npc.keeper");
            case "Выбор режима", "Телепорт" -> this.tr(world, "npc.mode");
            case "Выбор цвета", "Выбор стороны" -> this.tr(world, "npc.duo_team");
            case "Волшебный Квибек" -> this.tr(world, "npc.wizard");
            case "Переход в Duo Режим", "QubeCore" -> this.tr(world, "npc.duo");
            case "Статистика" -> this.tr(world, "npc.stats");
            default -> this.text(russianName);
        };
    }

    private ContractDefinition resolveActiveContract(BankDefenseRepository.Snapshot snapshot, PlayerProgressionState progression) {
        if (snapshot == null || progression == null || progression.activeContractId == null || progression.activeContractId.isBlank()) {
            return null;
        }
        for (ContractDefinition contract : snapshot.contracts.contracts) {
            if (progression.activeContractId.equals(contract.id)) {
                return contract;
            }
        }
        return null;
    }

    private ContractDefinition resolveSelectedContract(BankDefenseRepository.Snapshot snapshot, World world, PlayerProgressionState progression) {
        if (snapshot != null && world != null) {
            String selectedId = this.selectedContractIdsByWorld.get(this.worldKey(world));
            if (selectedId != null && !selectedId.isBlank()) {
                for (ContractDefinition contract : snapshot.contracts.contracts) {
                    if (selectedId.equals(contract.id)) {
                        return contract;
                    }
                }
            }
        }
        return this.resolveActiveContract(snapshot, progression);
    }

    private String activeContractName(World world) throws IOException {
        MatchContext context = this.getOrCreateContext(world);
        return this.contractDisplayName(world, context.activeContract);
    }

    private List<DifficultyProfile> difficultyProfiles() {
        return List.of(
            new DifficultyProfile(DIFFICULTY_EASY, "Easy", "Relaxed mode: softer waves, a more generous start, and forgiving economy even without progression.", 0.78, 0.92, 0.86, 1.18, 10, 1.10),
            new DifficultyProfile(DIFFICULTY_NORMAL, "Normal", "Core mode: expects you to use the mechanics, but remains comfortably beatable without shards.", 0.90, 0.97, 0.94, 1.06, 0, 1.03),
            new DifficultyProfile(DIFFICULTY_HARD, "Hard", "Requires planning and good tempo: denser waves, tighter economy, and mistakes begin to hurt.", 1.08, 1.03, 1.06, 0.96, 0, 1.00),
            new DifficultyProfile(DIFFICULTY_NIGHTMARE, "Nightmare", "Challenge mode: high pressure, little room for mistakes, and maximum value only with perfect tempo.", 1.26, 1.08, 1.14, 0.88, 0, 1.03)
        );
    }

    private DifficultyProfile resolveDifficultyProfile(String difficultyId) {
        for (DifficultyProfile profile : this.difficultyProfiles()) {
            if (profile.id.equals(difficultyId)) {
                return profile;
            }
        }
        return this.difficultyProfiles().get(1);
    }

    private int modifiedStartingCurrency(
        GameRules rules,
        PlayerProgressionState progression,
        ContractDefinition activeContract,
        BankDefenseRepository.Snapshot snapshot,
        DifficultyProfile difficulty
    ) {
        int value = rules.startingCurrency;
        value += (int)Math.round(this.progressionValue(snapshot, progression, "starting_currency_flat", null));
        if (activeContract != null) {
            value += activeContract.startingCurrencyFlat;
            value = (int)Math.round(value * Math.max(0.1, activeContract.startingCurrencyMultiplier));
        }
        if (difficulty != null) {
            value += difficulty.startingCurrencyFlat;
            value = (int)Math.round(value * Math.max(0.1, difficulty.startingCurrencyMultiplier));
        }
        return Math.max(0, value);
    }

    private int modifiedBankHp(GameRules rules, BankDefenseRepository.Snapshot snapshot, PlayerProgressionState progression) {
        return Math.max(1, rules.bankHp + (int)Math.round(this.progressionValue(snapshot, progression, "bank_hp_flat", null)));
    }

    private void trackLifetimeSpentCurrency(MatchContext context, int amount) {
        if (context == null || context.tutorialActive || context.progression == null || amount <= 0) {
            return;
        }
        context.progression.lifetimeSpentCurrency += amount;
    }

    private void trackLifetimeTrapPlaced(MatchContext context) {
        if (context == null || context.tutorialActive || context.progression == null) {
            return;
        }
        context.progression.lifetimeTrapsPlaced += 1;
    }

    private void trackLifetimeChestOpened(MatchContext context) {
        if (context == null || context.tutorialActive || context.progression == null) {
            return;
        }
        context.progression.lifetimeOpenedChests += 1;
    }

    private void trackLifetimeModuleInstalled(MatchContext context) {
        if (context == null || context.tutorialActive || context.progression == null) {
            return;
        }
        context.progression.lifetimeModulesInstalled += 1;
    }

    private void trackRunEnemyKill(MatchContext context) {
        if (context == null || context.tutorialActive || context.progression == null) {
            return;
        }
        context.runEnemyKills += 1;
    }

    private void trackRunWaveCleared(MatchContext context) {
        if (context == null || context.tutorialActive || context.progression == null) {
            return;
        }
        context.runWavesCleared += 1;
    }

    private void finalizeRunStatistics(MatchContext context, int reachedWave) {
        if (context == null || context.tutorialActive || context.progression == null) {
            return;
        }
        int resolvedWave = Math.max(0, context.runWavesCleared);
        context.progression.lifetimeWavesCleared += Math.max(0, context.runWavesCleared);
        context.progression.lifetimeEnemyKills += Math.max(0, context.runEnemyKills);

        boolean betterBestRun = resolvedWave > context.progression.bestRunWave;
        if (!betterBestRun && resolvedWave == context.progression.bestRunWave) {
            if (context.runEarnedCurrency > context.progression.bestRunEarnedCurrency) {
                betterBestRun = true;
            } else if (context.runEarnedCurrency == context.progression.bestRunEarnedCurrency
                && context.runEnemyKills > context.progression.bestRunEnemyKills) {
                betterBestRun = true;
            }
        }
        if (betterBestRun) {
            context.progression.bestRunWave = resolvedWave;
            context.progression.bestRunEarnedCurrency = Math.max(0, context.runEarnedCurrency);
            context.progression.bestRunEnemyKills = Math.max(0, context.runEnemyKills);
        }
    }

    private void grantProgressionRewardIfNeeded(World world, MatchContext context, int completedWaveNumber, boolean victory) {
        if (context == null || context.progression == null || context.progressionRewardGranted || context.tutorialActive) {
            return;
        }
        GameRules rules = context.snapshot.gameRules;
        int completedWaves = Math.max(0, completedWaveNumber);
        int reward = 0;
        if (completedWaves >= 4) {
            reward = Math.max(0, rules.progressionPointBase + (completedWaves * rules.progressionPointPerWave));
            if (completedWaves > rules.totalWaves) {
                int endlessWaves = completedWaves - rules.totalWaves;
                reward += (endlessWaves / Math.max(1, rules.progressionEndlessCycleSize)) * rules.progressionPointPerEndlessCycle;
            }
            if (victory) {
                reward += rules.progressionPointWinBonus;
            }
            if (context.state.bankHp >= context.maxBankHp) {
                reward += rules.progressionPointPerfectBonus;
            }
            if (context.activeContract != null) {
                reward = (int)Math.round(reward * Math.max(1.0, context.activeContract.rewardMultiplier));
            }
        }
        context.progression.cores += Math.max(0, reward);
        context.progression.totalRuns += 1;
        if (victory) {
            context.progression.totalVictories += 1;
        }
        int reachedWave = Math.max(completedWaves, Math.max(context.state.currentWave, context.activeWaveNumber));
        this.finalizeRunStatistics(context, reachedWave);
        context.progression.lifetimeHighestWave = Math.max(context.progression.lifetimeHighestWave, reachedWave);
        context.progressionRewardGranted = true;
        try {
            this.repository.savePlayerProgression(context.progression);
        } catch (IOException ignored) {
        }
        if (reward > 0) {
            final int grantedReward = reward;
            if (world != null) {
                this.sendLocalizedWorldMessage(world, playerRef ->
                    this.choose(playerRef, "Прогрессия: получено Ядер дупла ", "Progression: Hollow Cores gained ")
                        + grantedReward
                        + this.choose(playerRef, ". Всего: ", ". Total: ")
                        + context.progression.cores
                        + "."
                );
            }
        }
    }

    private void finishMatchAsDefeat(World world, MatchContext context, String russian, String english) {
        this.finishMatchAsDefeat(world, context, this.choose(world, russian, english));
        if (world != null && russian != null && !russian.isBlank() && english != null && !english.isBlank()) {
            this.sendLocalizedWorldMessage(world, russian, english);
        }
    }

    private void finishMatchAsDefeat(World world, MatchContext context, String worldMessage) {
        if (context == null || context.state == null) {
            return;
        }
        context.state.bankHp = 0;
        context.state.gameState = GameState.Defeat;
        context.state.waveState = WaveState.Finished;
        context.state.gameStarted = false;
        context.state.paused = false;
        context.state.preparationRemainingSeconds = 0.0;
        this.clearInstantAutoStartWarmup(context);
        context.defeatBannerRemainingSeconds = 4.5;
        this.clearRewardDraftState(context);
        context.lastRewardPreparedWave = 0;
        context.pendingSpawns.clear();
        context.enemies.clear();
        context.activeWaveTotalEnemies = 0;
        context.rootsHeartBuffWaveNumber = 0;
        context.rootsHeartBuffMultiplier = 1.0;
        this.deactivateAcidStorm(world, context);
        this.grantProgressionRewardIfNeeded(world, context, Math.max(0, Math.max(context.state.currentWave - 1, context.activeWaveNumber)), false);
        context.activeWaveNumber = 0;
        context.state.currentWave = this.selectedStartWave(world);
        this.playWorldUiSound(world, SOUND_DEFEAT);
        this.refreshVisualizationIfEnabled(world);
    }

    private boolean hasSeedIdol(MatchContext context) {
        return this.findSeedIdol(context) != null;
    }

    private TowerInstance findSeedIdol(MatchContext context) {
        if (context == null) {
            return null;
        }
        for (TowerInstance tower : context.placedTowers.values()) {
            if ("seed_idol".equals(tower.definition.id)) {
                return tower;
            }
        }
        return null;
    }

    private int grantMatchIncome(World world, MatchContext context, int amount, Vector3d effectPosition) {
        if (world == null || context == null || amount <= 0) {
            return 0;
        }
        amount = this.applySealCurseIncome(context, amount);
        if (amount <= 0) {
            return 0;
        }
        TowerInstance seedIdol = this.findSeedIdol(context);
        if (seedIdol == null || seedIdol.specialMode == SEED_IDOL_MODE_DEFAULT) {
            this.grantTeamIncome(context, amount);
            this.playIncomeSound(world, context, effectPosition);
            if (!context.tutorialActive && context.progression != null) {
                context.runEarnedCurrency += amount;
            }
            return amount;
        }
        if (seedIdol.specialMode == SEED_IDOL_MODE_HARVEST) {
            int boosted = Math.max(1, (int)Math.round(amount * SEED_IDOL_HARVEST_BONUS_MULTIPLIER));
            this.grantTeamIncome(context, boosted);
            this.playIncomeSound(world, context, effectPosition);
            if (!context.tutorialActive && context.progression != null) {
                context.runEarnedCurrency += boosted;
            }
            return boosted;
        }
        seedIdol.idolStoredCurrency += amount;
        return 0;
    }

    private void playIncomeSound(World world, MatchContext context, Vector3d effectPosition) {
        if (world == null || context == null) {
            return;
        }
        // Gold gain should always be audible, regardless of where the player is on the map.
        this.playWorldUiSound(world, SOUND_COIN);
    }

    private int scaledNonKillIncome(MatchContext context, int amount) {
        if (context == null || amount <= 0) {
            return Math.max(0, amount);
        }
        double multiplier = context.difficulty == null ? 1.0 : Math.max(0.1, context.difficulty.rewardMultiplier);
        return Math.max(0, (int)Math.round(amount * multiplier));
    }

    private boolean seedIdolWardIncomePaused(MatchContext context) {
        TowerInstance seedIdol = this.findSeedIdol(context);
        return seedIdol != null && seedIdol.specialMode == SEED_IDOL_MODE_WARD;
    }

    private void showEventToast(MatchContext context, String text, int iconType) {
        if (context == null || text == null || text.isBlank()) {
            return;
        }
        context.eventToastText = text;
        context.eventToastRemainingSeconds = 5.0;
        this.queueWorldUiSound(context, SOUND_NOTIFICATION);
        context.eventToastIconType = switch (iconType) {
            case EVENT_TOAST_ICON_CORES -> EVENT_TOAST_ICON_CORES;
            case EVENT_TOAST_ICON_ALERT -> EVENT_TOAST_ICON_ALERT;
            default -> EVENT_TOAST_ICON_MONEY;
        };
    }

    private void scheduleEventToast(MatchContext context, String text, int iconType, double delaySeconds) {
        if (context == null || text == null || text.isBlank()) {
            return;
        }
        context.delayedEventToastText = text;
        context.delayedEventToastIconType = switch (iconType) {
            case EVENT_TOAST_ICON_CORES -> EVENT_TOAST_ICON_CORES;
            case EVENT_TOAST_ICON_ALERT -> EVENT_TOAST_ICON_ALERT;
            default -> EVENT_TOAST_ICON_MONEY;
        };
        context.delayedEventToastRemainingSeconds = Math.max(0.0, delaySeconds);
    }

    private void queueEventToast(MatchContext context, String text, int iconType) {
        if (context == null || text == null || text.isBlank()) {
            return;
        }
        context.queuedEventToastText = text;
        context.queuedEventToastIconType = switch (iconType) {
            case EVENT_TOAST_ICON_CORES -> EVENT_TOAST_ICON_CORES;
            case EVENT_TOAST_ICON_ALERT -> EVENT_TOAST_ICON_ALERT;
            default -> EVENT_TOAST_ICON_MONEY;
        };
    }

    private void queueWorldUiSound(MatchContext context, String soundId) {
        if (context == null || soundId == null || soundId.isBlank()) {
            return;
        }
        context.pendingWorldUiSounds.add(soundId);
    }

    private void flushPendingWorldUiSounds(World world, MatchContext context) {
        if (world == null || context == null || context.pendingWorldUiSounds.isEmpty()) {
            return;
        }
        List<String> pending = new ArrayList<>(context.pendingWorldUiSounds);
        context.pendingWorldUiSounds.clear();
        for (String soundId : pending) {
            this.playWorldUiSound(world, soundId);
        }
    }

    private void tickActiveChestLoopAudio(World world, MatchContext context, double deltaSeconds) {
        if (context == null) {
            return;
        }
        if (!context.state.gameStarted
            || context.state.gameState == GameState.Defeat
            || context.state.gameState == GameState.Victory
            || context.activeChests.isEmpty()) {
            context.chestLoopSoundCooldownRemaining = 0.0;
            return;
        }
        context.chestLoopSoundCooldownRemaining = Math.max(0.0, context.chestLoopSoundCooldownRemaining - Math.max(0.0, deltaSeconds));
        if (context.chestLoopSoundCooldownRemaining > 0.0) {
            return;
        }
        for (ActiveChest chest : context.activeChests.values()) {
            if (chest == null || chest.position == null) {
                continue;
            }
            this.playSound3d(world, SOUND_CHEST_LOOP, new Vector3d(chest.position.x + 0.5, chest.position.y + 0.55, chest.position.z + 0.5));
        }
        context.chestLoopSoundCooldownRemaining = 2.6;
    }

    private void showIdolPayoutToast(World world, MatchContext context, int payout, int stored) {
        if (context == null || payout <= 0 || stored <= 0) {
            return;
        }
        this.showEventToast(
            context,
            this.choose(world, "Идол урожая выплатил ", "Harvest Idol paid out ") + payout
                + this.choose(world, " монет (Было накоплено ", " gold (Stored ")
                + stored
                + ")",
            EVENT_TOAST_ICON_MONEY
        );
    }

    private void tickTransientUi(MatchContext context, double deltaSeconds) {
        if (context == null || deltaSeconds <= 0.0) {
            return;
        }
        if (context.defeatBannerRemainingSeconds > 0.0) {
            context.defeatBannerRemainingSeconds = Math.max(0.0, context.defeatBannerRemainingSeconds - deltaSeconds);
        }
        if (context.delayedEventToastRemainingSeconds > 0.0) {
            context.delayedEventToastRemainingSeconds = Math.max(0.0, context.delayedEventToastRemainingSeconds - deltaSeconds);
        }
        if (context.delayedEventToastRemainingSeconds <= 0.0 && context.delayedEventToastText != null && !context.delayedEventToastText.isBlank()) {
            String delayedText = context.delayedEventToastText;
            int delayedIconType = context.delayedEventToastIconType;
            context.delayedEventToastText = "";
            context.delayedEventToastIconType = EVENT_TOAST_ICON_MONEY;
            if (this.eventToastVisible(context)) {
                this.queueEventToast(context, delayedText, delayedIconType);
            } else {
                this.showEventToast(context, delayedText, delayedIconType);
            }
        }
        if (context.eventToastRemainingSeconds > 0.0) {
            context.eventToastRemainingSeconds = Math.max(0.0, context.eventToastRemainingSeconds - deltaSeconds);
            if (context.eventToastRemainingSeconds <= 0.0) {
                context.eventToastRemainingSeconds = 0.0;
                context.eventToastText = "";
                context.eventToastIconType = EVENT_TOAST_ICON_MONEY;
                if (context.queuedEventToastText != null && !context.queuedEventToastText.isBlank()) {
                    String queuedText = context.queuedEventToastText;
                    int queuedIconType = context.queuedEventToastIconType;
                    context.queuedEventToastText = "";
                    context.queuedEventToastIconType = EVENT_TOAST_ICON_MONEY;
                    this.showEventToast(context, queuedText, queuedIconType);
                }
            }
        }
    }

    private void refreshVisualizationIfEnabled(World world) {
        String worldKey = this.worldKey(world);
        MatchContext context = this.matchesByWorld.get(worldKey);
        this.syncImmediateInteractionBlocks(world, context);
        PresentationState presentation = this.presentationsByWorld.get(worldKey);
        if (presentation == null || !presentation.enabled) {
            return;
        }
        if (this.hasCombatVisualState(context)) {
            return;
        }
        try {
            this.fullRefreshPresentation(world, presentation);
        } catch (IOException ignored) {
        }
    }

    private void syncImmediateInteractionBlocks(World world, MatchContext context) {
        if (world == null || context == null || context.snapshot == null) {
            return;
        }
        this.syncSlotInteractionBlocks(world, context.snapshot.buildSlots, context);
        this.syncChestInteractionBlocks(world, context.snapshot.map, context);
    }

    private int fullRefreshPresentation(World world, PresentationState presentation) throws IOException {
        BankDefenseRepository.Snapshot rawSnapshot = this.repository.loadSnapshot(world);
        MatchContext context = this.matchesByWorld.get(this.worldKey(world));
        TutorialState tutorial = this.tutorialState(world);
        BankDefenseRepository.Snapshot snapshot = this.presentationSnapshot(rawSnapshot, context, tutorial);
        this.reconcileContextWithSnapshot(context, snapshot);
        presentation.cachedRawSnapshot = rawSnapshot;
        if (!presentation.visualGarbageSanitized) {
            this.purgeOneTimeVisualGarbage(world, rawSnapshot);
            presentation.visualGarbageSanitized = true;
        }
        if (!presentation.legacyDebugBlocksCleared) {
            this.cleanupLegacyDebugBlocks(world, rawSnapshot);
            presentation.legacyDebugBlocksCleared = true;
        }

        this.purgeLegacyNpcArtifacts(world, rawSnapshot.map);
        this.purgeOwnedCombatVisuals(world, rawSnapshot, context);
        this.purgeDetachedTowerVisuals(world, Collections.emptySet());
        this.purgeDetachedEnemyVisuals(world, rawSnapshot, Collections.emptySet());
        this.purgeDetachedProjectileVisuals(world, Collections.emptySet());
        this.clearVisualEntities(world, presentation);
        presentation.pendingVisualRespawnSeconds.clear();
        this.syncSlotInteractionBlocks(world, snapshot.buildSlots, context);
        this.cleanupUnusedTutorialTrapMarkers(world, snapshot.buildSlots, snapshot.map);
        this.syncChestInteractionBlocks(world, snapshot.map, context);
        this.spawnControlOperatorVisual(world, presentation, snapshot);
        this.spawnVendorOperatorVisual(world, presentation, snapshot);
        this.spawnModeSelectorVisual(world, presentation, snapshot);
        this.spawnDuoTeamVisual(world, presentation, snapshot);
        this.spawnDuoTransitionVisual(world, presentation, snapshot);
        this.spawnStatsVisual(world, presentation, snapshot);
        this.syncTutorialWizardVisuals(world, presentation, rawSnapshot, this.tutorialState(world));
        this.syncControlInteractionBlock(world, snapshot.map);
        this.syncVendorInteractionBlock(world, snapshot.map);
        this.syncModeSelectorInteractionBlock(world, snapshot.map);
        this.syncDuoTeamInteractionBlock(world, snapshot.map);
        this.syncDuoInteractionBlock(world, snapshot.map);
        this.syncStatsInteractionBlock(world, snapshot.map);
        this.syncTutorialWizardInteractionBlocks(world, rawSnapshot.map, this.tutorialState(world));
        this.spawnObjectiveVisual(world, presentation, "marker:vault", snapshot.map.vaultPoint, MODEL_OBJECTIVE, 0.85f, 0.05, null);
        this.spawnObjectiveVisual(world, presentation, KEY_SPAWN_A, snapshot.map.spawnPointA, MODEL_SPAWN, 0.65f, 0.05, null);
        this.spawnObjectiveVisual(world, presentation, KEY_SPAWN_B, snapshot.map.spawnPointB, MODEL_SPAWN, 0.65f, 0.05, null);
        this.spawnObjectiveVisual(world, presentation, KEY_SPAWN_C, snapshot.map.spawnPointC, MODEL_SPAWN, 0.65f, 0.05, null);
        this.spawnObjectiveVisual(world, presentation, KEY_CONSOLE_START, this.startConsolePoint(snapshot.map), MODEL_SPAWN, 0.78f, 0.05, this.choose(world, "Начать волну", "Start wave"));
        this.spawnObjectiveVisual(world, presentation, KEY_CONSOLE_MENU, this.menuConsolePoint(snapshot.map), MODEL_WARP, 0.82f, 0.05, this.choose(world, "Чит-меню", "Cheat menu"));

        if (context != null) {
            for (TowerInstance tower : context.placedTowers.values()) {
                String visualProfileKey = this.towerVisualProfileKey(tower);
                Ref<EntityStore> ref = this.spawnTowerVisual(world, tower, this.towerVisualOwnerKey(tower, visualProfileKey));
                if (ref != null) {
                    presentation.staticRefs.put(KEY_TOWER_PREFIX + tower.slot.id, ref);
                }
            }
            this.syncEnemyVisuals(world, presentation, context, 0.0);
        }

        this.syncHud(world, presentation, snapshot, context);
        return presentation.totalVisualEntityCount();
    }

    private void syncRealtimeVisuals(World world, PresentationState presentation, double deltaSeconds) {
        MatchContext context = this.matchesByWorld.get(this.worldKey(world));
        boolean shouldSyncEnemyVisuals = context == null
            ? !presentation.enemyRefs.isEmpty()
            : (!context.enemies.isEmpty() || !presentation.enemyRefs.isEmpty());
        if (!shouldSyncEnemyVisuals) {
            presentation.enemyRealtimeAccumulatedSeconds = 0.0;
        } else {
            presentation.enemyRealtimeAccumulatedSeconds += Math.max(0.0, deltaSeconds);
            if (presentation.enemyRealtimeAccumulatedSeconds >= REALTIME_ENEMY_VISUAL_SYNC_SECONDS) {
                double enemyDeltaSeconds = presentation.enemyRealtimeAccumulatedSeconds;
                presentation.enemyRealtimeAccumulatedSeconds = 0.0;
                this.syncEnemyVisuals(world, presentation, context, enemyDeltaSeconds);
            }
        }

        boolean shouldSyncTowerVisuals = context != null
            && (!context.placedTowers.isEmpty() || this.hasTrackedTowerVisuals(presentation));
        if (!shouldSyncTowerVisuals) {
            presentation.towerRealtimeAccumulatedSeconds = 0.0;
        } else {
            presentation.towerRealtimeAccumulatedSeconds += Math.max(0.0, deltaSeconds);
            if (presentation.towerRealtimeAccumulatedSeconds >= REALTIME_TOWER_VISUAL_SYNC_SECONDS) {
                double towerDeltaSeconds = presentation.towerRealtimeAccumulatedSeconds;
                presentation.towerRealtimeAccumulatedSeconds = 0.0;
                this.syncTowerVisualTransforms(world, presentation, context, towerDeltaSeconds);
            }
        }

        if (!presentation.towerRangePreviewEnabled) {
            presentation.rangePreviewAccumulatedSeconds = 0.0;
        } else {
            presentation.rangePreviewAccumulatedSeconds += Math.max(0.0, deltaSeconds);
            if (presentation.rangePreviewAccumulatedSeconds >= REALTIME_RANGE_PREVIEW_SYNC_SECONDS) {
                presentation.rangePreviewAccumulatedSeconds = 0.0;
                this.syncTowerRangePreview(world, presentation, context);
            }
        }
        boolean noLogicalEnemies = this.hasNoLogicalEnemies(context);
        if (noLogicalEnemies) {
            if (presentation.hadLogicalEnemiesPreviousTick) {
                Set<Ref<EntityStore>> staticKeepRefs = new HashSet<>(presentation.staticRefs.values());
                this.purgeDetachedProjectileVisuals(world, staticKeepRefs);
            }
            presentation.hadLogicalEnemiesPreviousTick = false;
            return;
        }
        presentation.hadLogicalEnemiesPreviousTick = true;
    }

    private boolean hasTrackedTowerVisuals(PresentationState presentation) {
        if (presentation == null || presentation.staticRefs.isEmpty()) {
            return false;
        }
        for (String key : presentation.staticRefs.keySet()) {
            if (key != null && key.startsWith(KEY_TOWER_PREFIX)) {
                return true;
            }
        }
        return false;
    }

    private void clearEnemyVisualLayerAwayFromCombat(World world, PresentationState presentation, MatchContext context) {
        if (world == null || presentation == null) {
            return;
        }
        Store<EntityStore> store = world.getEntityStore().getStore();
        for (Ref<EntityStore> ref : presentation.enemyRefs.values()) {
            this.removeVisualRef(store, ref);
        }
        presentation.enemyRefs.clear();
        if (context == null) {
            this.prunePendingVisualRespawns(presentation, "enemy:", Collections.emptySet());
            return;
        }
        Set<String> activeEnemyOwnerKeys = new HashSet<>();
        for (EnemyInstance enemy : context.enemies) {
            activeEnemyOwnerKeys.add(this.enemyVisualOwnerKey(enemy));
            enemy.visualRef = null;
            enemy.visualSpawnedOnce = false;
            enemy.currentMovementAnimationId = "";
        }
        if (context.snapshot != null) {
            this.purgeOwnedCombatVisuals(world, context.snapshot, context);
            this.purgeDetachedEnemyVisuals(world, context.snapshot, Collections.emptySet());
        }
        this.prunePendingVisualRespawns(presentation, "enemy:", activeEnemyOwnerKeys);
    }

    private void rebuildCombatVisualsOnReturn(World world, PresentationState presentation) {
        if (world == null || presentation == null) {
            return;
        }
        MatchContext context = this.matchesByWorld.get(this.worldKey(world));
        if (context == null || context.snapshot == null || context.snapshot.map == null) {
            presentation.playersNearCombatLastTick = false;
            return;
        }
        boolean hasCombatVisuals = !context.placedTowers.isEmpty() || !this.hasNoLogicalEnemies(context);
        if (!hasCombatVisuals) {
            presentation.playersNearCombatLastTick = false;
            return;
        }
        boolean playersNearCombat = this.arePlayersNearCombatForEnemyVisuals(world, context);
        if (!playersNearCombat) {
            presentation.playersNearCombatLastTick = false;
            return;
        }
        if (presentation.playersNearCombatLastTick) {
            return;
        }
        presentation.playersNearCombatLastTick = true;
        this.rebuildCombatVisualLayer(world, presentation, context);
    }

    private boolean arePlayersNearCombatForEnemyVisuals(World world, MatchContext context) {
        return context != null
            && context.snapshot != null
            && context.snapshot.map != null
            && this.isAnyPlayerNearCombatZone(world, context.snapshot.map, COMBAT_VISUAL_RETURN_REBUILD_RADIUS);
    }

    private boolean isAnyPlayerNearCombatZone(World world, MapConfig map, double radius) {
        if (world == null || map == null || radius <= 0.0) {
            return false;
        }
        double radiusSquared = radius * radius;
        Store<EntityStore> store = world.getEntityStore().getStore();
        for (PlayerRef playerRef : world.getPlayerRefs()) {
            if (playerRef == null || playerRef.getReference() == null || !playerRef.getReference().isValid()) {
                continue;
            }
            TransformComponent transform = store.getComponent(playerRef.getReference(), TransformComponent.getComponentType());
            if (transform == null || transform.getPosition() == null) {
                continue;
            }
            Vector3d position = transform.getPosition();
            if (this.isNearCombatAnchor(position, map.vaultPoint, radiusSquared)
                || this.isNearCombatAnchor(position, map.spawnPointA, radiusSquared)
                || this.isNearCombatAnchor(position, map.spawnPointB, radiusSquared)
                || this.isNearCombatAnchor(position, map.spawnPointC, radiusSquared)
                || this.isNearCombatAnchor(position, map.spawnPoint, radiusSquared)) {
                return true;
            }
        }
        return false;
    }

    private boolean isNearCombatAnchor(Vector3d position, Vec3i point, double radiusSquared) {
        if (position == null || point == null) {
            return false;
        }
        double dx = position.x - (point.x + 0.5);
        double dy = position.y - (point.y + 0.5);
        double dz = position.z - (point.z + 0.5);
        return (dx * dx) + (dy * dy) + (dz * dz) <= radiusSquared;
    }

    private void rebuildCombatVisualLayer(World world, PresentationState presentation, MatchContext context) {
        if (world == null || presentation == null || context == null || context.snapshot == null) {
            return;
        }
        this.reconcileContextWithSnapshot(context, context.snapshot);
        Store<EntityStore> store = world.getEntityStore().getStore();
        for (Ref<EntityStore> ref : presentation.enemyRefs.values()) {
            this.removeVisualRef(store, ref);
        }
        presentation.enemyRefs.clear();
        presentation.enemyVisualStates.clear();
        presentation.enemyVisualGarbageSweepRemaining = 0.0;
        List<String> towerKeys = new ArrayList<>();
        for (Map.Entry<String, Ref<EntityStore>> entry : presentation.staticRefs.entrySet()) {
            String key = entry.getKey();
            if (key != null && key.startsWith(KEY_TOWER_PREFIX)) {
                this.removeVisualRef(store, entry.getValue());
                towerKeys.add(key);
            }
        }
        for (String key : towerKeys) {
            presentation.staticRefs.remove(key);
        }
        this.purgeOwnedCombatVisuals(world, context.snapshot, context);
        this.purgeDetachedTowerVisuals(world, Collections.emptySet());
        this.purgeDetachedEnemyVisuals(world, context.snapshot, Collections.emptySet());

        for (TowerInstance tower : context.placedTowers.values()) {
            String visualProfileKey = this.towerVisualProfileKey(tower);
            tower.visualProfileKey = visualProfileKey;
            tower.visualSpawnedOnce = false;
            Ref<EntityStore> ref = this.spawnTowerVisual(world, tower, this.towerVisualOwnerKey(tower, visualProfileKey));
            if (ref != null) {
                presentation.staticRefs.put(KEY_TOWER_PREFIX + tower.slot.id, ref);
            }
        }
        this.syncEnemyVisuals(world, presentation, context, 0.0);
    }

    private void tickPresentation(World world, PresentationState presentation) throws IOException {
        MatchContext context = this.matchesByWorld.get(this.worldKey(world));
        TutorialState tutorial = this.tutorialState(world);
        BankDefenseRepository.Snapshot rawSnapshot = this.currentPresentationRawSnapshot(world, presentation);
        BankDefenseRepository.Snapshot snapshot = this.presentationSnapshot(rawSnapshot, context, tutorial);
        this.syncHud(world, presentation, snapshot, context);
    }

    private BankDefenseRepository.Snapshot presentationSnapshot(
        BankDefenseRepository.Snapshot rawSnapshot,
        MatchContext context,
        TutorialState tutorial
    ) {
        if (context != null && !context.tutorialActive) {
            return context.snapshot;
        }
        return this.effectiveSnapshot(null, rawSnapshot, tutorial);
    }

    private void syncObjectiveVisuals(
        World world,
        PresentationState presentation,
        BankDefenseRepository.Snapshot snapshot,
        BankDefenseRepository.Snapshot rawSnapshot
    ) {
        this.ensureControlOperatorVisual(world, presentation, snapshot);
        this.ensureVendorOperatorVisual(world, presentation, snapshot);
        this.ensureModeSelectorVisual(world, presentation, snapshot);
        this.ensureDuoTeamVisual(world, presentation, snapshot);
        this.ensureDuoTransitionVisual(world, presentation, snapshot);
        this.ensureStatsVisual(world, presentation, snapshot);
        this.syncTutorialWizardVisuals(world, presentation, rawSnapshot, this.tutorialState(world));
        this.syncControlInteractionBlock(world, snapshot.map);
        this.syncVendorInteractionBlock(world, snapshot.map);
        this.syncModeSelectorInteractionBlock(world, snapshot.map);
        this.syncDuoTeamInteractionBlock(world, snapshot.map);
        this.syncDuoInteractionBlock(world, snapshot.map);
        this.syncStatsInteractionBlock(world, snapshot.map);
        this.syncTutorialWizardInteractionBlocks(world, rawSnapshot.map, this.tutorialState(world));
        this.ensureObjectiveVisual(world, presentation, "marker:vault", snapshot.map.vaultPoint, MODEL_OBJECTIVE, 0.85f, 0.05, null);
        this.ensureObjectiveVisual(world, presentation, KEY_SPAWN_A, snapshot.map.spawnPointA, MODEL_SPAWN, 0.65f, 0.05, null);
        this.ensureObjectiveVisual(world, presentation, KEY_SPAWN_B, snapshot.map.spawnPointB, MODEL_SPAWN, 0.65f, 0.05, null);
        this.ensureObjectiveVisual(world, presentation, KEY_SPAWN_C, snapshot.map.spawnPointC, MODEL_SPAWN, 0.65f, 0.05, null);
        this.ensureObjectiveVisual(world, presentation, KEY_CONSOLE_START, this.startConsolePoint(snapshot.map), MODEL_SPAWN, 0.78f, 0.05, this.choose(world, "Начать волну", "Start wave"));
        this.ensureObjectiveVisual(world, presentation, KEY_CONSOLE_MENU, this.menuConsolePoint(snapshot.map), MODEL_WARP, 0.82f, 0.05, this.choose(world, "Чит-меню", "Cheat menu"));
    }

    private void ensureObjectiveVisual(
        World world,
        PresentationState presentation,
        String key,
        Vec3i point,
        String modelId,
        float scale,
        double yOffset,
        String nameplate
    ) {
        Ref<EntityStore> ref = presentation.staticRefs.get(key);
        if (point == null || !this.isChunkLoadedForPoint(world, point)) {
            this.removeVisualRef(world.getEntityStore().getStore(), ref);
            presentation.staticRefs.remove(key);
            return;
        }
        if (ref == null || !ref.isValid()) {
            this.spawnObjectiveVisual(world, presentation, key, point, modelId, scale, yOffset, nameplate);
            return;
        }
        if (this.isInteractivePresentationKey(key)) {
            this.ensureInteractableVisual(world.getEntityStore().getStore(), ref);
        }
    }

    private void spawnControlOperatorVisual(World world, PresentationState presentation, BankDefenseRepository.Snapshot snapshot) {
        Vec3i point = this.controlInteractionPoint(snapshot.map);
        if (!this.shouldShowStandardDuoNpc(world) || point == null || !this.isChunkLoadedForPoint(world, point)) {
            return;
        }
        this.purgeOperatorArea(world, point);
        Ref<EntityStore> existing = presentation.staticRefs.remove(KEY_CONTROL_OPERATOR);
        if (existing != null) {
            this.removeVisualRef(world.getEntityStore().getStore(), existing);
        }
        Ref<EntityStore> ref = this.createControlOperatorEntity(world, snapshot, point);
        if (ref != null) {
            presentation.staticRefs.put(KEY_CONTROL_OPERATOR, ref);
        }
    }

    private void ensureControlOperatorVisual(World world, PresentationState presentation, BankDefenseRepository.Snapshot snapshot) {
        Vec3i point = this.controlInteractionPoint(snapshot.map);
        Ref<EntityStore> ref = presentation.staticRefs.get(KEY_CONTROL_OPERATOR);
        if (!this.shouldShowStandardDuoNpc(world) || point == null || !this.isChunkLoadedForPoint(world, point)) {
            this.removeVisualRef(world.getEntityStore().getStore(), ref);
            presentation.staticRefs.remove(KEY_CONTROL_OPERATOR);
            return;
        }
        if (ref == null || !ref.isValid()) {
            this.spawnControlOperatorVisual(world, presentation, snapshot);
            return;
        }
        this.purgeOperatorArea(world, point, ref);
        this.updateTransform(world.getEntityStore().getStore(), ref, this.controlOperatorWorldPosition(point), this.controlOperatorRotation(snapshot.map));
        this.syncNameplate(
            world.getEntityStore().getStore(),
            ref,
            this.localizedNpcLabel(world, "Оператор")
        );
    }

    private void spawnVendorOperatorVisual(World world, PresentationState presentation, BankDefenseRepository.Snapshot snapshot) {
        Vec3i point = this.vendorInteractionPoint(snapshot.map);
        if (point == null || !this.isChunkLoadedForPoint(world, point)) {
            return;
        }
        this.purgeOperatorArea(world, point);
        Ref<EntityStore> existing = presentation.staticRefs.remove(KEY_VENDOR_OPERATOR);
        if (existing != null) {
            this.removeVisualRef(world.getEntityStore().getStore(), existing);
        }
        Ref<EntityStore> ref = this.createVendorOperatorEntity(world, snapshot, point);
        if (ref != null) {
            presentation.staticRefs.put(KEY_VENDOR_OPERATOR, ref);
        }
    }

    private void spawnModeSelectorVisual(World world, PresentationState presentation, BankDefenseRepository.Snapshot snapshot) {
        Vec3i point = this.modeSelectorInteractionPoint(snapshot.map);
        if (!this.shouldShowModeTravelNpc(world, snapshot.map) || point == null || !this.isChunkLoadedForPoint(world, point)) {
            return;
        }
        this.purgeOperatorArea(world, point);
        Ref<EntityStore> existing = presentation.staticRefs.remove(KEY_MODE_SELECTOR_OPERATOR);
        if (existing != null) {
            this.removeVisualRef(world.getEntityStore().getStore(), existing);
        }
        Ref<EntityStore> ref = this.createModeSelectorEntity(world, snapshot, point);
        if (ref != null) {
            presentation.staticRefs.put(KEY_MODE_SELECTOR_OPERATOR, ref);
        }
    }

    private void spawnDuoTeamVisual(World world, PresentationState presentation, BankDefenseRepository.Snapshot snapshot) {
        Vec3i point = this.duoTeamInteractionPoint(snapshot.map);
        if (!this.shouldShowDuoTeamSelectorNpc(world, snapshot.map) || point == null || !this.isChunkLoadedForPoint(world, point)) {
            return;
        }
        this.purgeOperatorArea(world, point);
        Ref<EntityStore> existing = presentation.staticRefs.remove(KEY_DUO_TEAM_OPERATOR);
        if (existing != null) {
            this.removeVisualRef(world.getEntityStore().getStore(), existing);
        }
        Ref<EntityStore> ref = this.createDuoTeamEntity(world, snapshot, point);
        if (ref != null) {
            presentation.staticRefs.put(KEY_DUO_TEAM_OPERATOR, ref);
        }
    }

    private void spawnDuoTransitionVisual(World world, PresentationState presentation, BankDefenseRepository.Snapshot snapshot) {
        Vec3i point = this.duoTransitionPoint(snapshot.map);
        if (!this.shouldShowStandardDuoNpc(world) || point == null || !this.isChunkLoadedForPoint(world, point)) {
            return;
        }
        this.purgeOperatorArea(world, point);
        Ref<EntityStore> existing = presentation.staticRefs.remove(KEY_DUO_TRANSITION_OPERATOR);
        if (existing != null) {
            this.removeVisualRef(world.getEntityStore().getStore(), existing);
        }
        Ref<EntityStore> ref = this.createDuoTransitionEntity(world, snapshot, point);
        if (ref != null) {
            presentation.staticRefs.put(KEY_DUO_TRANSITION_OPERATOR, ref);
        }
    }

    private void spawnStatsVisual(World world, PresentationState presentation, BankDefenseRepository.Snapshot snapshot) {
        Vec3i point = this.statsPoint(snapshot.map);
        if (point == null || !this.isChunkLoadedForPoint(world, point)) {
            return;
        }
        this.purgeOperatorArea(world, point);
        Ref<EntityStore> existing = presentation.staticRefs.remove(KEY_STATS_OPERATOR);
        if (existing != null) {
            this.removeVisualRef(world.getEntityStore().getStore(), existing);
        }
        Ref<EntityStore> ref = this.createStatsEntity(world, snapshot, point);
        if (ref != null) {
            presentation.staticRefs.put(KEY_STATS_OPERATOR, ref);
        }
    }

    private boolean hasNoLogicalEnemies(MatchContext context) {
        return context == null || (context.enemies.isEmpty() && context.pendingSpawns.isEmpty());
    }

    @SuppressWarnings("unused")
    private void removeStaticVisualRef(World world, PresentationState presentation, String key) {
        if (world == null || presentation == null || key == null || key.isBlank()) {
            return;
        }
        Ref<EntityStore> existing = presentation.staticRefs.remove(key);
        this.removeVisualRef(world.getEntityStore().getStore(), existing);
    }

    private boolean shouldShowStandardDuoNpc(World world) {
        return true;
    }

    private boolean shouldShowModeTravelNpc(World world, MapConfig map) {
        return map != null && this.shouldShowStandardDuoNpc(world);
    }

    private boolean shouldShowDuoTeamSelectorNpc(World world, MapConfig map) {
        return world != null && map != null && this.isDuoGameplayMode(world);
    }

    private void ensureVendorOperatorVisual(World world, PresentationState presentation, BankDefenseRepository.Snapshot snapshot) {
        Vec3i point = this.vendorInteractionPoint(snapshot.map);
        Ref<EntityStore> ref = presentation.staticRefs.get(KEY_VENDOR_OPERATOR);
        if (!this.shouldShowStandardDuoNpc(world) || point == null || !this.isChunkLoadedForPoint(world, point)) {
            this.removeVisualRef(world.getEntityStore().getStore(), ref);
            presentation.staticRefs.remove(KEY_VENDOR_OPERATOR);
            return;
        }
        if (ref == null || !ref.isValid()) {
            this.spawnVendorOperatorVisual(world, presentation, snapshot);
            return;
        }
        this.purgeOperatorArea(world, point, ref);
        this.updateTransform(world.getEntityStore().getStore(), ref, this.vendorOperatorWorldPosition(point), this.vendorOperatorRotation(snapshot.map));
        this.syncNameplate(world.getEntityStore().getStore(), ref, this.localizedNpcLabel(world, "Хранитель улучшений"));
    }

    private void ensureModeSelectorVisual(World world, PresentationState presentation, BankDefenseRepository.Snapshot snapshot) {
        Vec3i point = this.modeSelectorInteractionPoint(snapshot.map);
        Ref<EntityStore> ref = presentation.staticRefs.get(KEY_MODE_SELECTOR_OPERATOR);
        if (!this.shouldShowModeTravelNpc(world, snapshot.map) || point == null || !this.isChunkLoadedForPoint(world, point)) {
            this.removeVisualRef(world.getEntityStore().getStore(), ref);
            presentation.staticRefs.remove(KEY_MODE_SELECTOR_OPERATOR);
            return;
        }
        if (ref == null || !ref.isValid()) {
            this.spawnModeSelectorVisual(world, presentation, snapshot);
            return;
        }
        this.purgeOperatorArea(world, point, ref);
        this.updateTransform(world.getEntityStore().getStore(), ref, this.modeSelectorWorldPosition(point), this.modeSelectorRotation(snapshot.map));
        this.syncNameplate(world.getEntityStore().getStore(), ref, this.localizedNpcLabel(world, "Телепорт"));
    }

    private void ensureDuoTeamVisual(World world, PresentationState presentation, BankDefenseRepository.Snapshot snapshot) {
        Vec3i point = this.duoTeamInteractionPoint(snapshot.map);
        Ref<EntityStore> ref = presentation.staticRefs.get(KEY_DUO_TEAM_OPERATOR);
        if (!this.shouldShowDuoTeamSelectorNpc(world, snapshot.map) || point == null || !this.isChunkLoadedForPoint(world, point)) {
            this.removeVisualRef(world.getEntityStore().getStore(), ref);
            presentation.staticRefs.remove(KEY_DUO_TEAM_OPERATOR);
            return;
        }
        if (ref == null || !ref.isValid()) {
            this.spawnDuoTeamVisual(world, presentation, snapshot);
            return;
        }
        this.purgeOperatorArea(world, point, ref);
        this.updateTransform(world.getEntityStore().getStore(), ref, this.duoTeamWorldPosition(point), this.duoTeamRotation(snapshot.map));
        this.syncNameplate(world.getEntityStore().getStore(), ref, this.localizedNpcLabel(world, "Выбор стороны"));
    }

    private void ensureDuoTransitionVisual(World world, PresentationState presentation, BankDefenseRepository.Snapshot snapshot) {
        Vec3i point = this.duoTransitionPoint(snapshot.map);
        Ref<EntityStore> ref = presentation.staticRefs.get(KEY_DUO_TRANSITION_OPERATOR);
        if (!this.shouldShowStandardDuoNpc(world) || point == null || !this.isChunkLoadedForPoint(world, point)) {
            this.removeVisualRef(world.getEntityStore().getStore(), ref);
            presentation.staticRefs.remove(KEY_DUO_TRANSITION_OPERATOR);
            return;
        }
        if (ref == null || !ref.isValid()) {
            this.spawnDuoTransitionVisual(world, presentation, snapshot);
            return;
        }
        this.purgeOperatorArea(world, point, ref);
        this.updateTransform(world.getEntityStore().getStore(), ref, this.duoTransitionWorldPosition(point), this.duoTransitionRotation(snapshot.map));
        this.syncNameplate(world.getEntityStore().getStore(), ref, this.localizedNpcLabel(world, "QubeCore"));
    }

    private void ensureStatsVisual(World world, PresentationState presentation, BankDefenseRepository.Snapshot snapshot) {
        Vec3i point = this.statsPoint(snapshot.map);
        Ref<EntityStore> ref = presentation.staticRefs.get(KEY_STATS_OPERATOR);
        if (!this.shouldShowStandardDuoNpc(world) || point == null || !this.isChunkLoadedForPoint(world, point)) {
            this.removeVisualRef(world.getEntityStore().getStore(), ref);
            presentation.staticRefs.remove(KEY_STATS_OPERATOR);
            return;
        }
        if (ref == null || !ref.isValid()) {
            this.spawnStatsVisual(world, presentation, snapshot);
            return;
        }
        this.purgeOperatorArea(world, point, ref);
        this.updateTransform(world.getEntityStore().getStore(), ref, this.statsWorldPosition(point), this.statsRotation(snapshot.map));
        this.syncNameplate(world.getEntityStore().getStore(), ref, this.localizedNpcLabel(world, "Статистика"));
    }

    private String findPresentationKey(PresentationState presentation, Ref<EntityStore> targetRef) {
        for (Map.Entry<String, Ref<EntityStore>> entry : presentation.staticRefs.entrySet()) {
            Ref<EntityStore> ref = entry.getValue();
            if (ref != null && ref.equals(targetRef)) {
                return entry.getKey();
            }
        }
        return null;
    }

    private BuildSlot findSlotById(BuildSlotsConfig config, String slotId) {
        if (slotId == null || slotId.isBlank()) {
            return null;
        }
        for (BuildSlot slot : config.slots) {
            if (slotId.equals(slot.id)) {
                return slot;
            }
        }
        return null;
    }

    private Vec3i slotInteractionPoint(BuildSlot slot) {
        if (slot == null || slot.position == null) {
            return null;
        }
        return slot.position;
    }

    private Vec3i legacySlotInteractionPoint(BuildSlot slot) {
        if (slot == null || slot.position == null) {
            return null;
        }
        return new Vec3i(slot.position.x, slot.position.y + 1, slot.position.z);
    }

    private Vec3i controlInteractionPoint(MapConfig map) {
        if (map == null) {
            return null;
        }
        if (map.controlPoint != null) {
            return map.controlPoint;
        }
        if (map.strictNpcMarkers) {
            return null;
        }
        if (map.playerStart != null) {
            return map.playerStart;
        }
        if (map.bankCenter != null) {
            return map.bankCenter;
        }
        if (map.vaultPoint != null) {
            return map.vaultPoint;
        }
        return map.spawnPoint;
    }

    private Vector3d controlOperatorWorldPosition(Vec3i point) {
        return point == null ? new Vector3d() : this.objectiveWorldPosition(point, CONTROL_OPERATOR_Y_OFFSET);
    }

    private Vector3f controlOperatorRotation(MapConfig map) {
        if (map != null && map.controlYaw != null) {
            return new Vector3f(0.0f, map.controlYaw.floatValue(), 0.0f);
        }
        Vec3i anchor = this.controlInteractionPoint(map);
        if (anchor == null) {
            return new Vector3f();
        }
        Vec3i lookTarget = map != null && map.bankCenter != null ? map.bankCenter : map != null ? map.spawnPoint : null;
        if (lookTarget == null) {
            return new Vector3f();
        }
        return this.playerModelRotationTowards(
            this.controlOperatorWorldPosition(anchor),
            this.objectiveWorldPosition(lookTarget, CONTROL_OPERATOR_Y_OFFSET)
        );
    }

    private Vec3i vendorInteractionPoint(MapConfig map) {
        if (map == null) {
            return null;
        }
        if (map.vendorPoint != null) {
            return map.vendorPoint;
        }
        if (map.strictNpcMarkers) {
            return null;
        }
        if (map.controlPoint != null) {
            return new Vec3i(map.controlPoint.x + 2, map.controlPoint.y, map.controlPoint.z);
        }
        if (map.playerStart != null) {
            return new Vec3i(map.playerStart.x + 2, map.playerStart.y, map.playerStart.z);
        }
        if (map.bankCenter != null) {
            return new Vec3i(map.bankCenter.x + 2, map.bankCenter.y, map.bankCenter.z);
        }
        return null;
    }

    private Vec3i modeSelectorInteractionPoint(MapConfig map) {
        if (map == null) {
            return null;
        }
        if (map.strictNpcMarkers && map.modePoint == null) {
            return null;
        }
        return map.modePoint;
    }

    private Vec3i duoTeamInteractionPoint(MapConfig map) {
        if (map == null) {
            return null;
        }
        return map.duoTeamPoint;
    }

    private Vec3i duoTransitionPoint(MapConfig map) {
        if (map == null) {
            return null;
        }
        if (MODE_DUO.equalsIgnoreCase(map.selectedModeId)) {
            return map.duoQubeCorePoint != null ? map.duoQubeCorePoint : map.duoPoint;
        }
        Vec3i legacySoloPoint = this.legacySoloQubeCorePoint(map);
        return legacySoloPoint;
    }

    private Float duoTransitionYaw(MapConfig map) {
        if (map == null) {
            return null;
        }
        if (MODE_DUO.equalsIgnoreCase(map.selectedModeId)) {
            return map.duoQubeCoreYaw != null ? map.duoQubeCoreYaw : map.duoYaw;
        }
        if (map.soloQubeCoreYaw != null) {
            return map.soloQubeCoreYaw;
        }
        if (map.duoPoint != null && !this.isPointNearDuoArena(map, map.duoPoint)) {
            return map.duoYaw;
        }
        return null;
    }

    private Vec3i legacySoloQubeCorePoint(MapConfig map) {
        if (map == null) {
            return null;
        }
        if (map.soloQubeCorePoint != null) {
            return map.soloQubeCorePoint;
        }
        if (map.previousSoloQubeCorePoint != null) {
            return map.previousSoloQubeCorePoint;
        }
        if (map.duoPoint != null && !this.isPointNearDuoArena(map, map.duoPoint)) {
            return map.duoPoint;
        }
        return null;
    }

    private Vec3i statsPoint(MapConfig map) {
        if (map == null) {
            return null;
        }
        return map.statisticsPoint;
    }

    private Vector3d vendorOperatorWorldPosition(Vec3i point) {
        return point == null ? new Vector3d() : this.objectiveWorldPosition(point, VENDOR_OPERATOR_Y_OFFSET);
    }

    private Vector3f vendorOperatorRotation(MapConfig map) {
        if (map != null && map.vendorYaw != null) {
            return new Vector3f(0.0f, map.vendorYaw.floatValue(), 0.0f);
        }
        Vec3i anchor = this.vendorInteractionPoint(map);
        if (anchor == null) {
            return new Vector3f();
        }
        Vec3i lookTarget = map != null && map.bankCenter != null ? map.bankCenter : map != null ? map.vaultPoint : null;
        if (lookTarget == null) {
            return new Vector3f();
        }
        return this.playerModelRotationTowards(
            this.vendorOperatorWorldPosition(anchor),
            this.objectiveWorldPosition(lookTarget, VENDOR_OPERATOR_Y_OFFSET)
        );
    }

    private Vector3d modeSelectorWorldPosition(Vec3i point) {
        return point == null ? new Vector3d() : this.objectiveWorldPosition(point, MODE_SELECTOR_Y_OFFSET);
    }

    private Vector3d duoTeamWorldPosition(Vec3i point) {
        return point == null ? new Vector3d() : this.objectiveWorldPosition(point, MODE_SELECTOR_Y_OFFSET);
    }

    private Vector3f modeSelectorRotation(MapConfig map) {
        if (map != null && map.modeYaw != null) {
            return new Vector3f(0.0f, map.modeYaw.floatValue(), 0.0f);
        }
        Vec3i anchor = this.modeSelectorInteractionPoint(map);
        if (anchor == null) {
            return new Vector3f();
        }
        Vec3i lookTarget = map != null && map.bankCenter != null ? map.bankCenter : map != null ? map.vaultPoint : null;
        if (lookTarget == null) {
            return new Vector3f();
        }
        return this.playerModelRotationTowards(
            this.modeSelectorWorldPosition(anchor),
            this.objectiveWorldPosition(lookTarget, MODE_SELECTOR_Y_OFFSET)
        );
    }

    private Vector3f duoTeamRotation(MapConfig map) {
        if (map != null && map.duoTeamYaw != null) {
            return new Vector3f(0.0f, map.duoTeamYaw.floatValue(), 0.0f);
        }
        Vec3i anchor = this.duoTeamInteractionPoint(map);
        if (anchor == null) {
            return new Vector3f();
        }
        Vec3i lookTarget = map != null && map.duoBankCenter != null ? map.duoBankCenter : map != null ? map.duoVaultPoint : null;
        if (lookTarget == null) {
            return new Vector3f();
        }
        return this.playerModelRotationTowards(
            this.duoTeamWorldPosition(anchor),
            this.objectiveWorldPosition(lookTarget, MODE_SELECTOR_Y_OFFSET)
        );
    }

    private Vector3d duoTransitionWorldPosition(Vec3i point) {
        return point == null ? new Vector3d() : this.objectiveWorldPosition(point, DUO_TRANSITION_Y_OFFSET);
    }

    private Vector3d statsWorldPosition(Vec3i point) {
        return point == null ? new Vector3d() : this.objectiveWorldPosition(point, STATS_OPERATOR_Y_OFFSET);
    }

    private Vector3f duoTransitionRotation(MapConfig map) {
        Float explicitYaw = this.duoTransitionYaw(map);
        if (explicitYaw != null) {
            return new Vector3f(0.0f, explicitYaw.floatValue(), 0.0f);
        }
        Vec3i anchor = this.duoTransitionPoint(map);
        if (anchor == null) {
            return new Vector3f();
        }
        Vec3i lookTarget = map != null && map.bankCenter != null ? map.bankCenter : map != null ? map.vaultPoint : null;
        if (lookTarget == null) {
            return new Vector3f();
        }
        return this.playerModelRotationTowards(
            this.duoTransitionWorldPosition(anchor),
            this.objectiveWorldPosition(lookTarget, DUO_TRANSITION_Y_OFFSET)
        );
    }

    private Vector3f statsRotation(MapConfig map) {
        if (map != null && map.statisticsYaw != null) {
            return new Vector3f(0.0f, map.statisticsYaw.floatValue(), 0.0f);
        }
        Vec3i anchor = this.statsPoint(map);
        if (anchor == null) {
            return new Vector3f();
        }
        Vec3i lookTarget = map != null && map.bankCenter != null ? map.bankCenter : map != null ? map.vaultPoint : null;
        if (lookTarget == null) {
            return new Vector3f();
        }
        return this.playerModelRotationTowards(
            this.statsWorldPosition(anchor),
            this.objectiveWorldPosition(lookTarget, STATS_OPERATOR_Y_OFFSET)
        );
    }

    private void purgeLegacyNpcArtifacts(World world, MapConfig map) {
        if (world == null || map == null) {
            return;
        }
        List<Vec3i> candidates = new ArrayList<>();
        Set<String> seen = new LinkedHashSet<>();
        this.addNpcCleanupCandidate(candidates, seen, map.controlPoint);
        this.addNpcCleanupCandidate(candidates, seen, map.previousControlPoint);
        this.addNpcCleanupCandidate(candidates, seen, map.vendorPoint);
        this.addNpcCleanupCandidate(candidates, seen, map.previousVendorPoint);
        this.addNpcCleanupCandidate(candidates, seen, map.modePoint);
        this.addNpcCleanupCandidate(candidates, seen, map.previousModePoint);
        this.addNpcCleanupCandidate(candidates, seen, map.duoTeamPoint);
        this.addNpcCleanupCandidate(candidates, seen, map.previousDuoTeamPoint);
        this.addNpcCleanupCandidate(candidates, seen, map.statisticsPoint);
        this.addNpcCleanupCandidate(candidates, seen, map.previousStatisticsPoint);
        this.addNpcCleanupCandidate(candidates, seen, map.duoTeleportPoint);
        this.addNpcCleanupCandidate(candidates, seen, map.previousDuoTeleportPoint);
        this.addNpcCleanupCandidate(candidates, seen, this.duoTransitionPoint(map));
        this.addNpcCleanupCandidate(candidates, seen, map.previousDuoPoint);
        this.addNpcCleanupCandidate(candidates, seen, map.playerStart);
        this.addNpcCleanupCandidate(candidates, seen, map.bankCenter);
        this.addNpcCleanupCandidate(candidates, seen, map.vaultPoint);
        this.addNpcCleanupCandidate(candidates, seen, map.spawnPoint);
        this.addNpcCleanupCandidate(candidates, seen, map.spawnPointA);
        this.addNpcCleanupCandidate(candidates, seen, map.spawnPointB);
        this.addNpcCleanupCandidate(candidates, seen, map.spawnPointC);
        if (map.controlPoint != null) {
            this.addNpcCleanupCandidate(candidates, seen, new Vec3i(map.controlPoint.x + 2, map.controlPoint.y, map.controlPoint.z));
        }
        if (map.playerStart != null) {
            this.addNpcCleanupCandidate(candidates, seen, new Vec3i(map.playerStart.x + 2, map.playerStart.y, map.playerStart.z));
        }
        if (map.bankCenter != null) {
            this.addNpcCleanupCandidate(candidates, seen, new Vec3i(map.bankCenter.x + 2, map.bankCenter.y, map.bankCenter.z));
        }
        if (map.controlPoint != null) {
            this.addNpcCleanupCandidate(candidates, seen, new Vec3i(map.controlPoint.x - 2, map.controlPoint.y, map.controlPoint.z));
        }
        if (map.vendorPoint != null) {
            this.addNpcCleanupCandidate(candidates, seen, new Vec3i(map.vendorPoint.x - 2, map.vendorPoint.y, map.vendorPoint.z));
        }
        if (map.playerStart != null) {
            this.addNpcCleanupCandidate(candidates, seen, new Vec3i(map.playerStart.x - 2, map.playerStart.y, map.playerStart.z));
        }
        if (map.bankCenter != null) {
            this.addNpcCleanupCandidate(candidates, seen, new Vec3i(map.bankCenter.x - 2, map.bankCenter.y, map.bankCenter.z));
        }
        for (Vec3i candidate : candidates) {
            this.purgeOperatorArea(world, candidate);
        }
    }

    private void addNpcCleanupCandidate(List<Vec3i> candidates, Set<String> seen, Vec3i point) {
        if (point == null) {
            return;
        }
        String key = point.x + ":" + point.y + ":" + point.z;
        if (seen.add(key)) {
            candidates.add(point);
        }
    }

    private Vec3i controlInteractionBlockPoint(MapConfig map) {
        Vec3i operatorPoint = this.controlInteractionPoint(map);
        if (operatorPoint == null) {
            return null;
        }
        return operatorPoint;
    }

    private Vec3i vendorInteractionBlockPoint(MapConfig map) {
        Vec3i operatorPoint = this.vendorInteractionPoint(map);
        if (operatorPoint == null) {
            return null;
        }
        return operatorPoint;
    }

    private Vec3i modeSelectorInteractionBlockPoint(MapConfig map) {
        Vec3i operatorPoint = this.modeSelectorInteractionPoint(map);
        if (operatorPoint == null) {
            return null;
        }
        return operatorPoint;
    }

    private Vec3i duoTeamInteractionBlockPoint(MapConfig map) {
        Vec3i operatorPoint = this.duoTeamInteractionPoint(map);
        if (operatorPoint == null) {
            return null;
        }
        return operatorPoint;
    }

    private Vec3i duoTransitionInteractionBlockPoint(MapConfig map) {
        Vec3i operatorPoint = this.duoTransitionPoint(map);
        if (operatorPoint == null) {
            return null;
        }
        return operatorPoint;
    }

    private Vec3i statsInteractionBlockPoint(MapConfig map) {
        Vec3i operatorPoint = this.statsPoint(map);
        if (operatorPoint == null) {
            return null;
        }
        return operatorPoint;
    }

    private boolean matchesInteractionPoint(Vec3i point, Vec3i targetBlock, int radius) {
        if (point == null || targetBlock == null) {
            return false;
        }
        return point.distanceSquaredTo(targetBlock) <= radius * radius;
    }

    private Vec3i startConsolePoint(MapConfig map) {
        return null;
    }

    private Vec3i menuConsolePoint(MapConfig map) {
        return null;
    }

    private Ref<EntityStore> spawnObjectiveVisual(
        World world,
        PresentationState presentation,
        String key,
        Vec3i point,
        String modelId,
        float scale,
        double yOffset,
        String nameplate
    ) {
        Model model = this.resolveModel(modelId, scale);
        if (model == null || point == null || !this.isChunkLoadedForPoint(world, point)) {
            return null;
        }
        Ref<EntityStore> ref = this.createVisualEntity(
            world,
            model,
            this.objectiveWorldPosition(point, yOffset),
            new Vector3f(0.0f, 0.0f, 0.0f),
            nameplate
        );
        if (ref != null) {
            presentation.staticRefs.put(key, ref);
            if (this.isInteractivePresentationKey(key)) {
                this.ensureInteractableVisual(world.getEntityStore().getStore(), ref);
            }
        }
        return ref;
    }

    private boolean isChunkLoadedForPoint(World world, Vec3i point) {
        if (world == null || point == null) {
            return false;
        }
        return world.getChunkIfInMemory(ChunkUtil.indexChunkFromBlock(point.x, point.z)) != null;
    }

    private Ref<EntityStore> spawnTowerVisual(World world, TowerInstance tower, String ownerKey) {
        RoleModelProfile profile = this.towerProfile(tower);
        tower.visualProfileKey = this.towerVisualProfileKey(tower);
        Model model = this.resolveModel(profile.roleIds, profile.fallbackModelId, profile.scale);
        if (model == null) {
            tower.visualSpawnedOnce = false;
            return null;
        }
        String nameplate = this.towerNameplate(world, tower);
        Ref<EntityStore> ref = this.createVisualEntity(world, model, this.towerWorldPosition(tower.slot), tower.visualRotation, nameplate);
        this.ensureInteractableVisual(world.getEntityStore().getStore(), ref);
        this.syncTowerVisualState(world, world.getEntityStore().getStore(), ref, tower);
        this.tagVisualOwner(world.getEntityStore().getStore(), ref, this.visualOwnerUuid(world, ownerKey));
        tower.visualSpawnedOnce = ref != null && ref.isValid();
        return ref;
    }

    private Ref<EntityStore> spawnEnemyVisual(World world, MatchContext context, EnemyInstance enemy, String ownerKey, RoleModelProfile profile) {
        RoleModelProfile resolvedProfile = profile == null ? this.enemyProfile(context, enemy) : profile;
        String nameplate = this.enemyTrackedNameplate(world, enemy);
        Vector3d position = this.enemyWorldPosition(context, enemy);
        Ref<EntityStore> ref = null;
        if (resolvedProfile.roleIds != null && resolvedProfile.roleIds.length > 0) {
            ref = this.createNpcVisualEntity(world, resolvedProfile.roleIds, position, enemy.visualRotation, resolvedProfile.scale, nameplate);
        }
        if (ref == null && resolvedProfile.fallbackModelId != null && !resolvedProfile.fallbackModelId.isBlank()) {
            Model model = this.resolveAnimatedModel(resolvedProfile.fallbackModelId, resolvedProfile.scale);
            if (model != null) {
                ref = this.createVisualEntity(world, model, position, enemy.visualRotation, nameplate);
                this.ensureTrackedVisualEntity(world.getEntityStore().getStore(), ref);
            }
        }
        if (ref == null && resolvedProfile.roleIds != null && resolvedProfile.roleIds.length > 0) {
            ref = this.createNpcVisualEntity(world, new String[]{"Skeleton", "Zombie", "Goblin"}, position, enemy.visualRotation, resolvedProfile.scale, nameplate);
        }
        if (ref == null) {
            return null;
        }
        this.tagVisualOwner(world.getEntityStore().getStore(), ref, this.visualOwnerUuid(world, ownerKey));
        return ref;
    }

    private String towerVisualOwnerKey(TowerInstance tower, String visualProfileKey) {
        String slotId = tower == null || tower.slot == null || tower.slot.id == null ? "unknown" : tower.slot.id;
        return "tower:" + slotId + ":" + (visualProfileKey == null ? "" : visualProfileKey);
    }

    private String enemyVisualOwnerKey(EnemyInstance enemy) {
        return enemy == null ? "enemy:unknown" : "enemy:" + enemy.instanceId;
    }

    private String enemyVisualOwnerKey(MatchContext context, EnemyInstance enemy) {
        String epoch = context == null || context.visualEpoch == null || context.visualEpoch.isBlank()
            ? "default"
            : context.visualEpoch;
        return enemy == null ? "enemy:" + epoch + ":unknown" : "enemy:" + epoch + ":" + enemy.instanceId;
    }

    private UUID visualOwnerUuid(World world, String ownerKey) {
        String seed = this.worldKey(world) + "|" + ownerKey;
        return UUID.nameUUIDFromBytes(seed.getBytes(StandardCharsets.UTF_8));
    }

    private void tagVisualOwner(Store<EntityStore> store, Ref<EntityStore> ref, UUID ownerUuid) {
        if (store == null || ref == null || !ref.isValid() || ownerUuid == null) {
            return;
        }
        store.putComponent(ref, UUIDComponent.getComponentType(), new UUIDComponent(ownerUuid));
    }

    private boolean consumeVisualRespawnDelay(PresentationState presentation, String ownerKey, double deltaSeconds) {
        if (presentation == null || ownerKey == null || ownerKey.isBlank()) {
            return false;
        }
        Double existingDelay = presentation.pendingVisualRespawnSeconds.get(ownerKey);
        if (existingDelay == null) {
            return false;
        }
        double remaining = existingDelay.doubleValue();
        remaining -= Math.max(0.0, deltaSeconds);
        if (remaining > 0.0) {
            presentation.pendingVisualRespawnSeconds.put(ownerKey, remaining);
            return true;
        }
        presentation.pendingVisualRespawnSeconds.remove(ownerKey);
        return false;
    }

    private void deferVisualRespawn(PresentationState presentation, String ownerKey, double seconds) {
        if (presentation == null || ownerKey == null || ownerKey.isBlank()) {
            return;
        }
        presentation.pendingVisualRespawnSeconds.put(ownerKey, Math.max(0.0, seconds));
    }

    private void prunePendingVisualRespawns(PresentationState presentation, String prefix, Set<String> activeOwnerKeys) {
        if (presentation == null || presentation.pendingVisualRespawnSeconds.isEmpty() || prefix == null || prefix.isBlank()) {
            return;
        }
        List<String> staleKeys = new ArrayList<>();
        for (String ownerKey : presentation.pendingVisualRespawnSeconds.keySet()) {
            if (ownerKey != null && ownerKey.startsWith(prefix) && !activeOwnerKeys.contains(ownerKey)) {
                staleKeys.add(ownerKey);
            }
        }
        for (String ownerKey : staleKeys) {
            presentation.pendingVisualRespawnSeconds.remove(ownerKey);
        }
    }

    private boolean shouldRenderEnemyVisual(
        World world,
        MatchContext context,
        EnemyInstance enemy,
        Vector3d enemyPosition,
        boolean playersNearCombat
    ) {
        return world != null
            && context != null
            && enemy != null
            && enemyPosition != null
            && playersNearCombat
            && this.isChunkLoadedForPosition(world, enemyPosition);
    }

    private void clearEnemyVisualTracking(PresentationState presentation, Long enemyKey, EnemyInstance enemy) {
        if (presentation != null && enemyKey != null) {
            presentation.enemyRefs.remove(enemyKey);
        }
        if (enemy != null) {
            enemy.visualRef = null;
            enemy.visualSpawnedOnce = false;
            enemy.currentMovementAnimationId = "";
        }
    }

    private Ref<EntityStore> acquireEnemyVisual(
        World world,
        PresentationState presentation,
        MatchContext context,
        EnemyInstance enemy,
        Vector3d enemyPosition,
        double deltaSeconds,
        boolean playersNearCombat
    ) {
        if (world == null || presentation == null || context == null || enemy == null) {
            return null;
        }
        Store<EntityStore> store = world.getEntityStore().getStore();
        Long enemyKey = Long.valueOf(enemy.instanceId);
        String ownerKey = this.enemyVisualOwnerKey(enemy);
        Ref<EntityStore> ref = enemy.visualRef;
        if (!this.isUsableEnemyVisualRef(store, ref)) {
            ref = presentation.enemyRefs.get(enemyKey);
        }
        if (!this.isUsableEnemyVisualRef(store, ref)) {
            ref = null;
        }
        boolean hadVisualRecently = enemy.visualSpawnedOnce || (ref != null && ref.isValid());

        boolean shouldRender = this.shouldRenderEnemyVisual(world, context, enemy, enemyPosition, playersNearCombat);
        if (!shouldRender) {
            if (ref != null && ref.isValid()) {
                this.removeVisualRef(store, ref);
            }
            if (hadVisualRecently) {
                this.deferVisualRespawn(presentation, ownerKey, VISUAL_RESPAWN_RETRY_SECONDS);
            }
            this.clearEnemyVisualTracking(presentation, enemyKey, enemy);
            return null;
        }

        if (ref != null && !this.isVisualInLoadedChunk(world, store, ref)) {
            this.removeVisualRef(store, ref);
            if (hadVisualRecently) {
                this.deferVisualRespawn(presentation, ownerKey, VISUAL_RESPAWN_RETRY_SECONDS);
            }
            ref = null;
            this.clearEnemyVisualTracking(presentation, enemyKey, enemy);
        }

        if (ref == null) {
            ref = this.reclaimOwnedVisual(
                world,
                enemyPosition,
                ENEMY_VISUAL_RECLAIM_CHUNK_RADIUS,
                this.visualOwnerUuid(world, ownerKey),
                candidate -> this.isUsableEnemyVisualRef(store, candidate)
            );
            if (ref != null) {
                enemy.currentMovementAnimationId = "";
                enemy.visualStateSyncRemaining = 0.0;
            }
        }

        if (ref == null && this.consumeVisualRespawnDelay(presentation, ownerKey, deltaSeconds)) {
            this.clearEnemyVisualTracking(presentation, enemyKey, enemy);
            return null;
        }

        if (ref == null) {
            ref = this.spawnEnemyVisual(world, context, enemy, ownerKey, this.enemyProfile(context, enemy));
            if (ref == null) {
                this.clearEnemyVisualTracking(presentation, enemyKey, enemy);
                this.deferVisualRespawn(presentation, ownerKey, VISUAL_RESPAWN_RETRY_SECONDS);
                return null;
            }
        }

        presentation.enemyRefs.put(enemyKey, ref);
        enemy.visualRef = ref;
        enemy.visualSpawnedOnce = true;
        return ref;
    }

    private Ref<EntityStore> reclaimOwnedVisual(
        World world,
        Vector3d expectedPosition,
        int chunkRadius,
        UUID ownerUuid,
        Predicate<Ref<EntityStore>> usability
    ) {
        if (world == null || expectedPosition == null || ownerUuid == null || usability == null) {
            return null;
        }
        Store<EntityStore> store = world.getEntityStore().getStore();
        int baseBlockX = (int)Math.floor(expectedPosition.x);
        int baseBlockZ = (int)Math.floor(expectedPosition.z);
        Ref<EntityStore> bestRef = null;
        double bestDistanceSquared = Double.MAX_VALUE;
        List<Ref<EntityStore>> duplicates = new ArrayList<>();
        Set<Ref<EntityStore>> visited = new HashSet<>();
        for (int chunkOffsetX = -chunkRadius; chunkOffsetX <= chunkRadius; chunkOffsetX++) {
            for (int chunkOffsetZ = -chunkRadius; chunkOffsetZ <= chunkRadius; chunkOffsetZ++) {
                long chunkIndex = ChunkUtil.indexChunkFromBlock(baseBlockX + (chunkOffsetX * 16), baseBlockZ + (chunkOffsetZ * 16));
                WorldChunk chunk = world.getChunkIfInMemory(chunkIndex);
                if (chunk == null || chunk.getEntityChunk() == null) {
                    continue;
                }
                List<Object> entityRefs = new ArrayList<>(chunk.getEntityChunk().getEntityReferences());
                for (Object refObject : entityRefs) {
                    if (!(refObject instanceof Ref<?> rawRef)) {
                        continue;
                    }
                    @SuppressWarnings("unchecked")
                    Ref<EntityStore> ref = (Ref<EntityStore>) rawRef;
                    if (ref == null || !ref.isValid() || !visited.add(ref) || !usability.test(ref)) {
                        continue;
                    }
                    UUIDComponent uuidComponent = store.getComponent(ref, UUIDComponent.getComponentType());
                    if (uuidComponent == null || !ownerUuid.equals(uuidComponent.getUuid())) {
                        continue;
                    }
                    TransformComponent transform = store.getComponent(ref, TransformComponent.getComponentType());
                    double distanceSquared = Double.MAX_VALUE;
                    if (transform != null && transform.getPosition() != null) {
                        Vector3d position = transform.getPosition();
                        double dx = position.getX() - expectedPosition.x;
                        double dy = position.getY() - expectedPosition.y;
                        double dz = position.getZ() - expectedPosition.z;
                        distanceSquared = (dx * dx) + (dy * dy) + (dz * dz);
                    }
                    if (bestRef == null || distanceSquared < bestDistanceSquared) {
                        if (bestRef != null) {
                            duplicates.add(bestRef);
                        }
                        bestRef = ref;
                        bestDistanceSquared = distanceSquared;
                    } else {
                        duplicates.add(ref);
                    }
                }
            }
        }
        for (Ref<EntityStore> duplicate : duplicates) {
            if (duplicate != null && duplicate != bestRef) {
                this.removeVisualRef(store, duplicate);
            }
        }
        if (bestRef != null) {
            this.tagVisualOwner(store, bestRef, ownerUuid);
        }
        return bestRef;
    }

    private Ref<EntityStore> createNpcVisualEntity(
        World world,
        String[] roleIds,
        Vector3d position,
        Vector3f rotation,
        float scale,
        String nameplateText
    ) {
        if (roleIds == null || roleIds.length == 0) {
            return null;
        }
        NPCPlugin npcPlugin = NPCPlugin.get();
        Store<EntityStore> store = world.getEntityStore().getStore();
        String profileKey = String.join(" -> ", roleIds);
        List<String> failures = new ArrayList<>();
        for (String roleId : roleIds) {
            if (roleId == null || roleId.isBlank()) {
                continue;
            }
            int roleIndex = npcPlugin.getIndex(roleId);
            if (roleIndex == Integer.MIN_VALUE) {
                failures.add(roleId + ":missing");
                continue;
            }
            try {
                var npcPair = npcPlugin.spawnEntity(
                    store,
                    roleIndex,
                    new Vector3d(position),
                    new Vector3f(rotation),
                    null,
                    (npcEntity, holder, entityStore) -> {
                        holder.ensureComponent(Intangible.getComponentType());
                        holder.ensureComponent(EntityStore.REGISTRY.getNonSerializedComponentType());
                    },
                    (npcEntity, ref, entityStore) -> {
                        entityStore.ensureComponent(ref, Frozen.getComponentType());
                        entityStore.ensureComponent(ref, Intangible.getComponentType());
                        this.ensureTrackedVisualEntity(entityStore, ref);
                        if (scale > 0.0f && Math.abs(scale - 1.0f) > 0.01f) {
                            EntityScaleComponent scaleComponent = entityStore.ensureAndGetComponent(ref, EntityScaleComponent.getComponentType());
                            scaleComponent.setScale(scale);
                        }
                        if (nameplateText != null && !nameplateText.isBlank()) {
                            Nameplate nameplate = entityStore.ensureAndGetComponent(ref, Nameplate.getComponentType());
                            nameplate.setText(this.text(nameplateText));
                        }
                    }
                );
                if (npcPair != null && npcPair.first() != null && npcPair.first().isValid()) {
                    this.ensureTrackedVisualEntity(store, npcPair.first());
                    if (scale > 0.0f && Math.abs(scale - 1.0f) > 0.01f) {
                        EntityScaleComponent scaleComponent = store.ensureAndGetComponent(npcPair.first(), EntityScaleComponent.getComponentType());
                        scaleComponent.setScale(scale);
                    }
                    if (nameplateText != null && !nameplateText.isBlank()) {
                        Nameplate nameplate = store.ensureAndGetComponent(npcPair.first(), Nameplate.getComponentType());
                        nameplate.setText(this.text(nameplateText));
                    }
                    if (this.loggedNpcVisualSuccess.add(profileKey)) {
                        NPCPlugin.get().getLogger().at(java.util.logging.Level.INFO).log(
                            "BankDefense visual NPC resolved via role chain '%s' using '%s'.",
                            profileKey,
                            roleId
                        );
                    }
                    return npcPair.first();
                }
                failures.add(roleId + ":nullref");
            } catch (Throwable ignored) {
                failures.add(roleId + ":" + ignored.getClass().getSimpleName());
            }
        }
        if (this.loggedNpcVisualFailure.add(profileKey)) {
            NPCPlugin.get().getLogger().at(java.util.logging.Level.WARNING).log(
                "BankDefense visual NPC fallback for role chain '%s'. Failures: %s",
                profileKey,
                String.join(", ", failures)
            );
        }
        return null;
    }

    private Ref<EntityStore> createVisualEntity(
        World world,
        Model model,
        Vector3d position,
        Vector3f rotation,
        String nameplateText
    ) {
        Store<EntityStore> store = world.getEntityStore().getStore();
        Holder<EntityStore> holder = EntityStore.REGISTRY.newHolder();
        holder.addComponent(TransformComponent.getComponentType(), new TransformComponent(position, rotation));
        holder.addComponent(NetworkId.getComponentType(), new NetworkId(store.getExternalData().takeNextNetworkId()));
        holder.addComponent(UUIDComponent.getComponentType(), UUIDComponent.randomUUID());
        holder.ensureComponent(Intangible.getComponentType());
        holder.addComponent(PropComponent.getComponentType(), PropComponent.get());
        holder.ensureComponent(EntityStore.REGISTRY.getNonSerializedComponentType());
        EntityModule entityModule = EntityModule.get();
        if (entityModule != null) {
            holder.addComponent(entityModule.getVisibleComponentType(), new EntityTrackerSystems.Visible());
            holder.addComponent(entityModule.getNewSpawnComponentType(), new NewSpawnComponent(2.0f));
        }
        if (model.getBoundingBox() != null) {
            holder.addComponent(BoundingBox.getComponentType(), new BoundingBox(model.getBoundingBox()));
        }
        holder.addComponent(ModelComponent.getComponentType(), new ModelComponent(model));
        holder.addComponent(ActiveAnimationComponent.getComponentType(), new ActiveAnimationComponent());
        holder.addComponent(MovementStatesComponent.getComponentType(), new MovementStatesComponent());
        holder.addComponent(HeadRotation.getComponentType(), new HeadRotation(new Vector3f(rotation)));
        if (nameplateText != null && !nameplateText.isBlank()) {
            holder.addComponent(Nameplate.getComponentType(), new Nameplate(this.text(nameplateText)));
        }
        return store.addEntity(holder, AddReason.SPAWN);
    }

    @SuppressWarnings("unused")
    private Ref<EntityStore> createSkinnedPlayerVisualEntity(
        World world,
        Model model,
        Vector3d position,
        Vector3f rotation,
        PlayerSkin skin,
        String nameplateText
    ) {
        Store<EntityStore> store = world.getEntityStore().getStore();
        Holder<EntityStore> holder = EntityStore.REGISTRY.newHolder();
        holder.addComponent(TransformComponent.getComponentType(), new TransformComponent(position, rotation));
        holder.addComponent(NetworkId.getComponentType(), new NetworkId(store.getExternalData().takeNextNetworkId()));
        holder.addComponent(UUIDComponent.getComponentType(), UUIDComponent.randomUUID());
        holder.ensureComponent(Intangible.getComponentType());
        holder.addComponent(PropComponent.getComponentType(), PropComponent.get());
        holder.ensureComponent(EntityStore.REGISTRY.getNonSerializedComponentType());
        EntityModule entityModule = EntityModule.get();
        if (entityModule != null) {
            holder.addComponent(entityModule.getVisibleComponentType(), new EntityTrackerSystems.Visible());
            holder.addComponent(entityModule.getNewSpawnComponentType(), new NewSpawnComponent(2.0f));
        }
        if (model.getBoundingBox() != null) {
            holder.addComponent(BoundingBox.getComponentType(), new BoundingBox(model.getBoundingBox()));
        }
        holder.addComponent(ModelComponent.getComponentType(), new ModelComponent(model));
        holder.addComponent(PlayerSkinComponent.getComponentType(), new PlayerSkinComponent(new PlayerSkin(skin)));
        holder.addComponent(HeadRotation.getComponentType(), new HeadRotation(new Vector3f(rotation)));
        if (nameplateText != null && !nameplateText.isBlank()) {
            holder.addComponent(Nameplate.getComponentType(), new Nameplate(this.text(nameplateText)));
        }
        return store.addEntity(holder, AddReason.SPAWN);
    }

    private Ref<EntityStore> createInteractiveSkinnedNpcEntity(
        World world,
        Model model,
        Vector3d position,
        Vector3f rotation,
        PlayerSkin skin,
        String nameplateText
    ) {
        Store<EntityStore> store = world.getEntityStore().getStore();
        Holder<EntityStore> holder = EntityStore.REGISTRY.newHolder();
        holder.addComponent(TransformComponent.getComponentType(), new TransformComponent(position, rotation));
        holder.addComponent(NetworkId.getComponentType(), new NetworkId(store.getExternalData().takeNextNetworkId()));
        holder.addComponent(UUIDComponent.getComponentType(), UUIDComponent.randomUUID());
        holder.ensureComponent(Frozen.getComponentType());
        holder.addComponent(PropComponent.getComponentType(), PropComponent.get());
        holder.ensureComponent(EntityStore.REGISTRY.getNonSerializedComponentType());
        EntityModule entityModule = EntityModule.get();
        if (entityModule != null) {
            holder.addComponent(entityModule.getVisibleComponentType(), new EntityTrackerSystems.Visible());
            holder.addComponent(entityModule.getNewSpawnComponentType(), new NewSpawnComponent(2.0f));
        }
        if (model.getBoundingBox() != null) {
            holder.addComponent(BoundingBox.getComponentType(), new BoundingBox(model.getBoundingBox()));
        }
        holder.addComponent(ModelComponent.getComponentType(), new ModelComponent(model));
        holder.addComponent(PlayerSkinComponent.getComponentType(), new PlayerSkinComponent(new PlayerSkin(skin)));
        holder.addComponent(HeadRotation.getComponentType(), new HeadRotation(new Vector3f(rotation)));
        if (nameplateText != null && !nameplateText.isBlank()) {
            holder.addComponent(Nameplate.getComponentType(), new Nameplate(this.text(nameplateText)));
        }
        return store.addEntity(holder, AddReason.SPAWN);
    }

    private Ref<EntityStore> createControlOperatorEntity(World world, BankDefenseRepository.Snapshot snapshot, Vec3i point) {
        Model model = this.resolveModel(MODEL_PLAYER, CONTROL_OPERATOR_SCALE);
        if (model == null) {
            return null;
        }
        Ref<EntityStore> ref = this.createSkinnedPlayerVisualEntity(
            world,
            model,
            this.controlOperatorWorldPosition(point),
            this.controlOperatorRotation(snapshot.map),
            this.controlNpcSkin,
            this.localizedNpcLabel(world, "Оператор")
        );
        if (ref != null) {
            this.ensureTrackedVisualEntity(world.getEntityStore().getStore(), ref);
            this.updateTransform(
                world.getEntityStore().getStore(),
                ref,
                this.controlOperatorWorldPosition(point),
                this.controlOperatorRotation(snapshot.map)
            );
        }
        return ref;
    }

    private Ref<EntityStore> createVendorOperatorEntity(World world, BankDefenseRepository.Snapshot snapshot, Vec3i point) {
        Model model = this.resolveModel(MODEL_PLAYER, VENDOR_OPERATOR_SCALE);
        if (model == null) {
            return null;
        }
        Ref<EntityStore> ref = this.createSkinnedPlayerVisualEntity(
            world,
            model,
            this.vendorOperatorWorldPosition(point),
            this.vendorOperatorRotation(snapshot.map),
            this.vendorNpcSkin,
            this.localizedNpcLabel(world, "Хранитель улучшений")
        );
        if (ref != null) {
            this.ensureTrackedVisualEntity(world.getEntityStore().getStore(), ref);
            this.updateTransform(
                world.getEntityStore().getStore(),
                ref,
                this.vendorOperatorWorldPosition(point),
                this.vendorOperatorRotation(snapshot.map)
            );
        }
        return ref;
    }

    private Ref<EntityStore> createModeSelectorEntity(World world, BankDefenseRepository.Snapshot snapshot, Vec3i point) {
        Model model = this.resolveModel(new String[]{MODEL_TUTORIAL_WIZARD}, MODEL_PLAYER, TUTORIAL_WIZARD_SCALE);
        if (model == null) {
            return null;
        }
        Ref<EntityStore> ref = this.createVisualEntity(
            world,
            model,
            this.modeSelectorWorldPosition(point),
            this.modeSelectorRotation(snapshot.map),
            this.localizedNpcLabel(world, "Телепорт")
        );
        if (ref != null) {
            this.ensureTrackedVisualEntity(world.getEntityStore().getStore(), ref);
            this.updateTransform(
                world.getEntityStore().getStore(),
                ref,
                this.modeSelectorWorldPosition(point),
                this.modeSelectorRotation(snapshot.map)
            );
        }
        return ref;
    }

    private Ref<EntityStore> createDuoTeamEntity(World world, BankDefenseRepository.Snapshot snapshot, Vec3i point) {
        Model model = this.resolveModel(MODEL_PLAYER, MODE_SELECTOR_SCALE);
        if (model == null) {
            return null;
        }
        Ref<EntityStore> ref = this.createSkinnedPlayerVisualEntity(
            world,
            model,
            this.duoTeamWorldPosition(point),
            this.duoTeamRotation(snapshot.map),
            this.duoTeamNpcSkin,
            this.localizedNpcLabel(world, "Выбор стороны")
        );
        if (ref != null) {
            this.ensureTrackedVisualEntity(world.getEntityStore().getStore(), ref);
            this.updateTransform(
                world.getEntityStore().getStore(),
                ref,
                this.duoTeamWorldPosition(point),
                this.duoTeamRotation(snapshot.map)
            );
        }
        return ref;
    }

    private Ref<EntityStore> createDuoTransitionEntity(World world, BankDefenseRepository.Snapshot snapshot, Vec3i point) {
        Model model = this.resolveModel(MODEL_PLAYER, DUO_TRANSITION_SCALE);
        if (model == null) {
            return null;
        }
        Ref<EntityStore> ref = this.createSkinnedPlayerVisualEntity(
            world,
            model,
            this.duoTransitionWorldPosition(point),
            this.duoTransitionRotation(snapshot.map),
            this.duoNpcSkin,
            this.localizedNpcLabel(world, "QubeCore")
        );
        if (ref != null) {
            this.ensureTrackedVisualEntity(world.getEntityStore().getStore(), ref);
            this.updateTransform(
                world.getEntityStore().getStore(),
                ref,
                this.duoTransitionWorldPosition(point),
                this.duoTransitionRotation(snapshot.map)
            );
        }
        return ref;
    }

    private Ref<EntityStore> createStatsEntity(World world, BankDefenseRepository.Snapshot snapshot, Vec3i point) {
        Model model = this.resolveModel(MODEL_PLAYER, STATS_OPERATOR_SCALE);
        if (model == null) {
            return null;
        }
        Ref<EntityStore> ref = this.createSkinnedPlayerVisualEntity(
            world,
            model,
            this.statsWorldPosition(point),
            this.statsRotation(snapshot.map),
            this.statsNpcSkin,
            this.localizedNpcLabel(world, "Статистика")
        );
        if (ref != null) {
            this.ensureTrackedVisualEntity(world.getEntityStore().getStore(), ref);
            this.updateTransform(
                world.getEntityStore().getStore(),
                ref,
                this.statsWorldPosition(point),
                this.statsRotation(snapshot.map)
            );
        }
        return ref;
    }

    private void syncTutorialWizardVisuals(
        World world,
        PresentationState presentation,
        BankDefenseRepository.Snapshot rawSnapshot,
        TutorialState tutorial
    ) {
        if (world == null || presentation == null || rawSnapshot == null || rawSnapshot.map == null) {
            return;
        }
        this.syncTutorialWizardVisual(
            world,
            presentation,
            KEY_TUTORIAL_INTRO_WIZARD,
            tutorial != null && !tutorial.active ? rawSnapshot.map.tutorialWizardIntroPoint : null,
            rawSnapshot.map.tutorialWizardIntroYaw,
            rawSnapshot.map.playerStart
        );
        this.syncTutorialWizardVisual(
            world,
            presentation,
            KEY_TUTORIAL_GUIDE_WIZARD,
            tutorial != null && tutorial.active ? rawSnapshot.map.tutorialWizardPoint : null,
            rawSnapshot.map.tutorialWizardYaw,
            rawSnapshot.map.tutorialBankCenter
        );
    }

    private void syncTutorialWizardVisual(
        World world,
        PresentationState presentation,
        String key,
        Vec3i point,
        Float yaw,
        Vec3i lookTarget
    ) {
        Ref<EntityStore> ref = presentation.staticRefs.get(key);
        if (point == null || !this.isChunkLoadedForPoint(world, point)) {
            this.removeVisualRef(world.getEntityStore().getStore(), ref);
            presentation.staticRefs.remove(key);
            return;
        }
        if (ref == null || !ref.isValid()) {
            this.purgeOperatorArea(world, point);
            Ref<EntityStore> created = this.createTutorialWizardEntity(world, point, yaw, lookTarget);
            if (created != null) {
                presentation.staticRefs.put(key, created);
            }
            return;
        }
        this.purgeOperatorArea(world, point, ref);
        this.updateTransform(world.getEntityStore().getStore(), ref, this.tutorialWizardWorldPosition(point), this.tutorialWizardRotation(point, yaw, lookTarget));
        this.syncNameplate(world.getEntityStore().getStore(), ref, this.localizedNpcLabel(world, "Волшебный Квибек"));
    }

    private Ref<EntityStore> createTutorialWizardEntity(World world, Vec3i point, Float yaw, Vec3i lookTarget) {
        Model model = this.resolveModel(new String[]{MODEL_TUTORIAL_WIZARD}, MODEL_PLAYER, TUTORIAL_WIZARD_SCALE);
        if (model == null) {
            return null;
        }
        Ref<EntityStore> ref = this.createVisualEntity(
            world,
            model,
            this.tutorialWizardWorldPosition(point),
            this.tutorialWizardRotation(point, yaw, lookTarget),
            this.localizedNpcLabel(world, "Волшебный Квибек")
        );
        if (ref != null) {
            this.ensureTrackedVisualEntity(world.getEntityStore().getStore(), ref);
            this.updateTransform(world.getEntityStore().getStore(), ref, this.tutorialWizardWorldPosition(point), this.tutorialWizardRotation(point, yaw, lookTarget));
        }
        return ref;
    }

    private Vector3d tutorialWizardWorldPosition(Vec3i point) {
        return point == null ? new Vector3d() : this.objectiveWorldPosition(point, TUTORIAL_WIZARD_Y_OFFSET);
    }

    private Vector3f tutorialWizardRotation(Vec3i point, Float yaw, Vec3i lookTarget) {
        if (yaw != null) {
            return new Vector3f(0.0f, yaw.floatValue(), 0.0f);
        }
        if (point == null || lookTarget == null) {
            return new Vector3f();
        }
        return this.playerModelRotationTowards(
            this.tutorialWizardWorldPosition(point),
            this.objectiveWorldPosition(lookTarget, TUTORIAL_WIZARD_Y_OFFSET)
        );
    }

    private void ensureTrackedVisualEntity(Store<EntityStore> store, Ref<EntityStore> ref) {
        if (ref == null || !ref.isValid()) {
            return;
        }
        EntityModule entityModule = EntityModule.get();
        if (entityModule == null) {
            return;
        }
        if (store.getComponent(ref, entityModule.getNetworkIdComponentType()) == null) {
            store.addComponent(ref, entityModule.getNetworkIdComponentType(), new NetworkId(store.getExternalData().takeNextNetworkId()));
        }
        if (store.getComponent(ref, entityModule.getVisibleComponentType()) == null) {
            store.addComponent(ref, entityModule.getVisibleComponentType(), new EntityTrackerSystems.Visible());
        }
        if (store.getComponent(ref, entityModule.getNewSpawnComponentType()) == null) {
            store.addComponent(ref, entityModule.getNewSpawnComponentType(), new NewSpawnComponent(2.0f));
        }
    }

    private boolean isInteractivePresentationKey(String key) {
        if (key == null || key.isBlank()) {
            return false;
        }
        return KEY_CONSOLE_START.equals(key)
            || KEY_CONSOLE_MENU.equals(key)
            || key.startsWith(KEY_SLOT_PREFIX)
            || key.startsWith(KEY_TOWER_PREFIX);
    }

    private void ensureInteractableVisual(Store<EntityStore> store, Ref<EntityStore> ref) {
        if (store == null || ref == null || !ref.isValid()) {
            return;
        }
        store.ensureComponent(ref, Interactable.getComponentType());
    }

    private void syncSlotInteractionBlocks(World world, BuildSlotsConfig buildSlots, MatchContext context) {
        if (world == null || buildSlots == null) {
            return;
        }
        int slotBlockId = BlockType.getBlockIdOrUnknown(SLOT_INTERACTION_BLOCK, "Failed to find block '%s' in Bank Defense slot sync.", SLOT_INTERACTION_BLOCK);
        int slotOccupiedBlockId = BlockType.getBlockIdOrUnknown(SLOT_INTERACTION_BLOCK_OCCUPIED, "Failed to find block '%s' in Bank Defense slot sync.", SLOT_INTERACTION_BLOCK_OCCUPIED);
        int slotBlueBlockId = BlockType.getBlockIdOrUnknown(SLOT_INTERACTION_BLOCK_BLUE, "Failed to find block '%s' in Bank Defense slot sync.", SLOT_INTERACTION_BLOCK_BLUE);
        int slotBlueOccupiedBlockId = BlockType.getBlockIdOrUnknown(SLOT_INTERACTION_BLOCK_BLUE_OCCUPIED, "Failed to find block '%s' in Bank Defense slot sync.", SLOT_INTERACTION_BLOCK_BLUE_OCCUPIED);
        int slotGreenBlockId = BlockType.getBlockIdOrUnknown(SLOT_INTERACTION_BLOCK_GREEN, "Failed to find block '%s' in Bank Defense slot sync.", SLOT_INTERACTION_BLOCK_GREEN);
        int slotGreenOccupiedBlockId = BlockType.getBlockIdOrUnknown(SLOT_INTERACTION_BLOCK_GREEN_OCCUPIED, "Failed to find block '%s' in Bank Defense slot sync.", SLOT_INTERACTION_BLOCK_GREEN_OCCUPIED);
        int trapSlotBlockId = BlockType.getBlockIdOrUnknown(TRAP_SLOT_INTERACTION_BLOCK, "Failed to find block '%s' in Bank Defense slot sync.", TRAP_SLOT_INTERACTION_BLOCK);
        int trapSlotOccupiedBlockId = BlockType.getBlockIdOrUnknown(TRAP_SLOT_INTERACTION_BLOCK_OCCUPIED, "Failed to find block '%s' in Bank Defense slot sync.", TRAP_SLOT_INTERACTION_BLOCK_OCCUPIED);
        int superSlotBlockId = BlockType.getBlockIdOrUnknown(SUPER_SLOT_INTERACTION_BLOCK, "Failed to find block '%s' in Bank Defense slot sync.", SUPER_SLOT_INTERACTION_BLOCK);
        if (slotBlockId == Integer.MIN_VALUE || slotBlockId == 0
            || slotOccupiedBlockId == Integer.MIN_VALUE || slotOccupiedBlockId == 0
            || trapSlotBlockId == Integer.MIN_VALUE || trapSlotBlockId == 0
            || trapSlotOccupiedBlockId == Integer.MIN_VALUE || trapSlotOccupiedBlockId == 0
            || superSlotBlockId == Integer.MIN_VALUE || superSlotBlockId == 0) {
            return;
        }
        BlockType slotBlockType = BlockType.getAssetMap().getAsset(slotBlockId);
        BlockType slotOccupiedBlockType = BlockType.getAssetMap().getAsset(slotOccupiedBlockId);
        BlockType slotBlueBlockType = (slotBlueBlockId == Integer.MIN_VALUE || slotBlueBlockId == 0)
            ? null
            : BlockType.getAssetMap().getAsset(slotBlueBlockId);
        BlockType slotBlueOccupiedBlockType = (slotBlueOccupiedBlockId == Integer.MIN_VALUE || slotBlueOccupiedBlockId == 0)
            ? null
            : BlockType.getAssetMap().getAsset(slotBlueOccupiedBlockId);
        BlockType slotGreenBlockType = (slotGreenBlockId == Integer.MIN_VALUE || slotGreenBlockId == 0)
            ? null
            : BlockType.getAssetMap().getAsset(slotGreenBlockId);
        BlockType slotGreenOccupiedBlockType = (slotGreenOccupiedBlockId == Integer.MIN_VALUE || slotGreenOccupiedBlockId == 0)
            ? null
            : BlockType.getAssetMap().getAsset(slotGreenOccupiedBlockId);
        BlockType trapSlotBlockType = BlockType.getAssetMap().getAsset(trapSlotBlockId);
        BlockType trapSlotOccupiedBlockType = BlockType.getAssetMap().getAsset(trapSlotOccupiedBlockId);
        BlockType superSlotBlockType = BlockType.getAssetMap().getAsset(superSlotBlockId);
        if (slotBlockType == null || slotOccupiedBlockType == null
            || trapSlotBlockType == null || trapSlotOccupiedBlockType == null
            || superSlotBlockType == null) {
            return;
        }
        for (BuildSlot slot : buildSlots.slots) {
            Vec3i interactionPoint = this.slotInteractionPoint(slot);
            if (interactionPoint == null) {
                continue;
            }
            boolean occupied = context != null && context.placedTowers.containsKey(slot.id);
            int desiredBlockId;
            BlockType desiredBlockType;
            if (this.isSuperSlot(slot)) {
                desiredBlockId = superSlotBlockId;
                desiredBlockType = superSlotBlockType;
            } else if (this.isTrapSlot(slot)) {
                desiredBlockId = occupied ? trapSlotOccupiedBlockId : trapSlotBlockId;
                desiredBlockType = occupied ? trapSlotOccupiedBlockType : trapSlotBlockType;
            } else {
                String ownerTeam = this.slotOwnerTeam(slot);
                if (TEAM_BLUE.equals(ownerTeam)) {
                    desiredBlockId = occupied && slotBlueOccupiedBlockType != null ? slotBlueOccupiedBlockId : occupied ? slotOccupiedBlockId : slotBlueBlockType != null ? slotBlueBlockId : slotBlockId;
                    desiredBlockType = occupied && slotBlueOccupiedBlockType != null ? slotBlueOccupiedBlockType : occupied ? slotOccupiedBlockType : slotBlueBlockType != null ? slotBlueBlockType : slotBlockType;
                } else if (TEAM_GREEN.equals(ownerTeam)) {
                    desiredBlockId = occupied && slotGreenOccupiedBlockType != null ? slotGreenOccupiedBlockId : occupied ? slotOccupiedBlockId : slotGreenBlockType != null ? slotGreenBlockId : slotBlockId;
                    desiredBlockType = occupied && slotGreenOccupiedBlockType != null ? slotGreenOccupiedBlockType : occupied ? slotOccupiedBlockType : slotGreenBlockType != null ? slotGreenBlockType : slotBlockType;
                } else if (occupied) {
                    desiredBlockId = slotOccupiedBlockId;
                    desiredBlockType = slotOccupiedBlockType;
                } else {
                    desiredBlockId = slotBlockId;
                    desiredBlockType = slotBlockType;
                }
            }
            Vec3i legacyPoint = this.legacySlotInteractionPoint(slot);
            if (legacyPoint != null && !this.samePoint(legacyPoint, interactionPoint)) {
                this.clearStaleSlotBlock(world, legacyPoint, slotBlockId);
                this.clearStaleSlotBlock(world, legacyPoint, slotOccupiedBlockId);
                this.clearStaleSlotBlock(world, legacyPoint, slotBlueBlockId);
                this.clearStaleSlotBlock(world, legacyPoint, slotBlueOccupiedBlockId);
                this.clearStaleSlotBlock(world, legacyPoint, slotGreenBlockId);
                this.clearStaleSlotBlock(world, legacyPoint, slotGreenOccupiedBlockId);
                this.clearStaleSlotBlock(world, legacyPoint, trapSlotBlockId);
                this.clearStaleSlotBlock(world, legacyPoint, trapSlotOccupiedBlockId);
                this.clearStaleSlotBlock(world, legacyPoint, superSlotBlockId);
            }
            WorldChunk chunk = world.getChunkIfInMemory(ChunkUtil.indexChunkFromBlock(interactionPoint.x, interactionPoint.z));
            if (chunk == null) {
                continue;
            }
            int existingBlockId = chunk.getBlock(interactionPoint.x, interactionPoint.y, interactionPoint.z);
            if (existingBlockId == desiredBlockId) {
                continue;
            }
            chunk.setBlock(interactionPoint.x, interactionPoint.y, interactionPoint.z, desiredBlockId, desiredBlockType, 0, 0, 4);
        }
    }

    private void cleanupUnusedTutorialTrapMarkers(World world, BuildSlotsConfig buildSlots, MapConfig map) {
        if (world == null || map == null) {
            return;
        }
        Set<String> activeSlotKeys = new HashSet<>();
        if (buildSlots != null && buildSlots.slots != null) {
            for (BuildSlot slot : buildSlots.slots) {
                if (slot != null && slot.position != null) {
                    activeSlotKeys.add(this.key(slot.position));
                }
            }
        }
        for (Vec3i point : this.defaultTutorialTrapPoints(map)) {
            if (point == null || activeSlotKeys.contains(this.key(point))) {
                continue;
            }
            this.clearAnySlotMarkerBlock(world, point);
        }
    }

    private void clearAnySlotMarkerBlock(World world, Vec3i point) {
        if (world == null || point == null) {
            return;
        }
        for (int dy = -1; dy <= 1; dy++) {
            Vec3i candidate = new Vec3i(point.x, point.y + dy, point.z);
            WorldChunk chunk = world.getChunkIfInMemory(ChunkUtil.indexChunkFromBlock(candidate.x, candidate.z));
            if (chunk == null) {
                continue;
            }
            int blockId = chunk.getBlock(candidate.x, candidate.y, candidate.z);
            if (!this.isAnySlotMarkerBlockId(blockId)) {
                continue;
            }
            chunk.breakBlock(candidate.x, candidate.y, candidate.z);
        }
    }

    private boolean isAnySlotMarkerBlockId(int blockId) {
        if (blockId == 0 || blockId == Integer.MIN_VALUE) {
            return false;
        }
        return blockId == safeBlockId(SLOT_INTERACTION_BLOCK)
            || blockId == safeBlockId(SLOT_INTERACTION_BLOCK_OCCUPIED)
            || blockId == safeBlockId(SLOT_INTERACTION_BLOCK_BLUE)
            || blockId == safeBlockId(SLOT_INTERACTION_BLOCK_BLUE_OCCUPIED)
            || blockId == safeBlockId(SLOT_INTERACTION_BLOCK_GREEN)
            || blockId == safeBlockId(SLOT_INTERACTION_BLOCK_GREEN_OCCUPIED)
            || blockId == safeBlockId(TRAP_SLOT_INTERACTION_BLOCK)
            || blockId == safeBlockId(TRAP_SLOT_INTERACTION_BLOCK_OCCUPIED)
            || blockId == safeBlockId(SUPER_SLOT_INTERACTION_BLOCK);
    }

    private static int safeBlockId(String blockName) {
        int blockId = BlockType.getBlockIdOrUnknown(blockName, "Failed to find block '%s' in Bank Defense slot marker cleanup.", blockName);
        return blockId == Integer.MIN_VALUE ? 0 : blockId;
    }

    private void syncChestInteractionBlocks(World world, MapConfig map, MatchContext context) {
        if (world == null || map == null) {
            return;
        }
        int moneyChestBlockId = BlockType.getBlockIdOrUnknown(MONEY_CHEST_INTERACTION_BLOCK, "Failed to find block '%s' in Bank Defense chest sync.", MONEY_CHEST_INTERACTION_BLOCK);
        int coreChestBlockId = BlockType.getBlockIdOrUnknown(CORE_CHEST_INTERACTION_BLOCK, "Failed to find block '%s' in Bank Defense chest sync.", CORE_CHEST_INTERACTION_BLOCK);
        if (moneyChestBlockId == Integer.MIN_VALUE || moneyChestBlockId == 0 || coreChestBlockId == Integer.MIN_VALUE || coreChestBlockId == 0) {
            return;
        }
        BlockType moneyChestBlockType = BlockType.getAssetMap().getAsset(moneyChestBlockId);
        BlockType coreChestBlockType = BlockType.getAssetMap().getAsset(coreChestBlockId);
        if (moneyChestBlockType == null || coreChestBlockType == null) {
            return;
        }
        Map<String, ActiveChest> activeChests = context == null ? Collections.emptyMap() : context.activeChests;
        for (Vec3i point : map.chestSpawnPoints) {
            if (point == null) {
                continue;
            }
            ActiveChest activeChest = this.findActiveChestAt(activeChests, point);
            if (activeChest == null) {
                this.clearStaleSlotBlock(world, point, moneyChestBlockId);
                this.clearStaleSlotBlock(world, point, coreChestBlockId);
                continue;
            }
            int desiredBlockId = activeChest.type == ChestType.Core ? coreChestBlockId : moneyChestBlockId;
            BlockType desiredBlockType = activeChest.type == ChestType.Core ? coreChestBlockType : moneyChestBlockType;
            WorldChunk chunk = world.getChunkIfInMemory(ChunkUtil.indexChunkFromBlock(point.x, point.z));
            if (chunk == null) {
                continue;
            }
            int existingBlockId = chunk.getBlock(point.x, point.y, point.z);
            if (existingBlockId == desiredBlockId) {
                continue;
            }
            chunk.setBlock(point.x, point.y, point.z, desiredBlockId, desiredBlockType, 0, 0, 4);
        }
    }

    private ActiveChest findActiveChestAt(Map<String, ActiveChest> activeChests, Vec3i point) {
        if (activeChests == null || activeChests.isEmpty() || point == null) {
            return null;
        }
        for (ActiveChest chest : activeChests.values()) {
            if (this.samePoint(chest.position, point)) {
                return chest;
            }
        }
        return null;
    }

    private void syncControlInteractionBlock(World world, MapConfig map) {
        Vec3i operatorPoint = this.controlInteractionPoint(map);
        Vec3i point = this.controlInteractionBlockPoint(map);
        Vec3i buriedPoint = this.buriedInteractionColumnPoint(point);
        if (world == null) {
            return;
        }
        int controlBlockId = BlockType.getBlockIdOrUnknown(CONTROL_INTERACTION_BLOCK, "Failed to find block '%s' in Bank Defense control sync.", CONTROL_INTERACTION_BLOCK);
        if (controlBlockId == Integer.MIN_VALUE || controlBlockId == 0) {
            return;
        }
        BlockType controlBlockType = BlockType.getAssetMap().getAsset(controlBlockId);
        if (controlBlockType == null) {
            return;
        }
        if (operatorPoint != null) {
            this.clearInteractionColumn(world, operatorPoint, controlBlockId, NPC_INTERACTION_CLEAR_HEIGHT);
            this.clearInteractionColumn(world, this.buriedInteractionColumnPoint(operatorPoint), controlBlockId, NPC_INTERACTION_CLEAR_HEIGHT);
            Vec3i rearPoint = this.legacyRearControlInteractionBlockPointFor(operatorPoint, map);
            if (!this.samePoint(rearPoint, point)) {
                this.clearInteractionColumn(world, rearPoint, controlBlockId, NPC_INTERACTION_CLEAR_HEIGHT);
                this.clearInteractionColumn(world, this.buriedInteractionColumnPoint(rearPoint), controlBlockId, NPC_INTERACTION_CLEAR_HEIGHT);
            }
        }
        if (map != null && map.previousControlPoint != null) {
            this.clearInteractionColumn(world, map.previousControlPoint, controlBlockId, NPC_INTERACTION_CLEAR_HEIGHT);
            this.clearInteractionColumn(world, this.buriedInteractionColumnPoint(map.previousControlPoint), controlBlockId, NPC_INTERACTION_CLEAR_HEIGHT);
            Vec3i previousBlockPoint = this.controlInteractionBlockPointFor(map.previousControlPoint, map);
            if (!this.samePoint(previousBlockPoint, point)) {
                this.clearInteractionColumn(world, previousBlockPoint, controlBlockId, NPC_INTERACTION_CLEAR_HEIGHT);
                this.clearInteractionColumn(world, this.buriedInteractionColumnPoint(previousBlockPoint), controlBlockId, NPC_INTERACTION_CLEAR_HEIGHT);
            }
            Vec3i previousRearPoint = this.legacyRearControlInteractionBlockPointFor(map.previousControlPoint, map);
            if (!this.samePoint(previousRearPoint, point)) {
                this.clearInteractionColumn(world, previousRearPoint, controlBlockId, NPC_INTERACTION_CLEAR_HEIGHT);
                this.clearInteractionColumn(world, this.buriedInteractionColumnPoint(previousRearPoint), controlBlockId, NPC_INTERACTION_CLEAR_HEIGHT);
            }
        }
        if (map != null && map.strictNpcMarkers) {
            this.clearInteractionColumn(world, map.playerStart, controlBlockId, NPC_INTERACTION_CLEAR_HEIGHT);
            this.clearInteractionColumn(world, this.buriedInteractionColumnPoint(map.playerStart), controlBlockId, NPC_INTERACTION_CLEAR_HEIGHT);
            this.clearInteractionColumn(world, map.bankCenter, controlBlockId, NPC_INTERACTION_CLEAR_HEIGHT);
            this.clearInteractionColumn(world, this.buriedInteractionColumnPoint(map.bankCenter), controlBlockId, NPC_INTERACTION_CLEAR_HEIGHT);
            this.clearInteractionColumn(world, map.vaultPoint, controlBlockId, NPC_INTERACTION_CLEAR_HEIGHT);
            this.clearInteractionColumn(world, this.buriedInteractionColumnPoint(map.vaultPoint), controlBlockId, NPC_INTERACTION_CLEAR_HEIGHT);
            this.clearInteractionColumn(world, map.spawnPoint, controlBlockId, NPC_INTERACTION_CLEAR_HEIGHT);
            this.clearInteractionColumn(world, this.buriedInteractionColumnPoint(map.spawnPoint), controlBlockId, NPC_INTERACTION_CLEAR_HEIGHT);
        }
        if (buriedPoint != null && this.shouldShowStandardDuoNpc(world)) {
            this.placeInteractionColumn(world, buriedPoint, controlBlockId, controlBlockType, NPC_INTERACTION_COLUMN_HEIGHT);
        }
    }

    private void syncVendorInteractionBlock(World world, MapConfig map) {
        Vec3i point = this.vendorInteractionBlockPoint(map);
        Vec3i buriedPoint = this.buriedInteractionColumnPoint(point);
        if (world == null) {
            return;
        }
        int vendorBlockId = BlockType.getBlockIdOrUnknown(VENDOR_INTERACTION_BLOCK, "Failed to find block '%s' in Bank Defense vendor sync.", VENDOR_INTERACTION_BLOCK);
        if (vendorBlockId == Integer.MIN_VALUE || vendorBlockId == 0) {
            return;
        }
        BlockType vendorBlockType = BlockType.getAssetMap().getAsset(vendorBlockId);
        if (vendorBlockType == null) {
            return;
        }
        if (map != null && map.vendorPoint != null) {
            this.clearInteractionColumn(world, map.vendorPoint, vendorBlockId, NPC_INTERACTION_CLEAR_HEIGHT);
            this.clearInteractionColumn(world, this.buriedInteractionColumnPoint(map.vendorPoint), vendorBlockId, NPC_INTERACTION_CLEAR_HEIGHT);
        }
        if (map != null && map.strictNpcMarkers) {
            if (map.controlPoint != null) {
                Vec3i controlFallback = new Vec3i(map.controlPoint.x + 2, map.controlPoint.y, map.controlPoint.z);
                this.clearInteractionColumn(world, controlFallback, vendorBlockId, NPC_INTERACTION_CLEAR_HEIGHT);
                this.clearInteractionColumn(world, this.buriedInteractionColumnPoint(controlFallback), vendorBlockId, NPC_INTERACTION_CLEAR_HEIGHT);
            }
            if (map.playerStart != null) {
                Vec3i playerFallback = new Vec3i(map.playerStart.x + 2, map.playerStart.y, map.playerStart.z);
                this.clearInteractionColumn(world, playerFallback, vendorBlockId, NPC_INTERACTION_CLEAR_HEIGHT);
                this.clearInteractionColumn(world, this.buriedInteractionColumnPoint(playerFallback), vendorBlockId, NPC_INTERACTION_CLEAR_HEIGHT);
            }
            if (map.bankCenter != null) {
                Vec3i bankFallback = new Vec3i(map.bankCenter.x + 2, map.bankCenter.y, map.bankCenter.z);
                this.clearInteractionColumn(world, bankFallback, vendorBlockId, NPC_INTERACTION_CLEAR_HEIGHT);
                this.clearInteractionColumn(world, this.buriedInteractionColumnPoint(bankFallback), vendorBlockId, NPC_INTERACTION_CLEAR_HEIGHT);
            }
        }
        if (point != null) {
            this.clearInteractionColumn(world, point, vendorBlockId, NPC_INTERACTION_CLEAR_HEIGHT);
        }
        if (buriedPoint != null && this.shouldShowStandardDuoNpc(world)) {
            this.placeInteractionColumn(world, buriedPoint, vendorBlockId, vendorBlockType, NPC_INTERACTION_COLUMN_HEIGHT);
        }
    }

    private void syncModeSelectorInteractionBlock(World world, MapConfig map) {
        Vec3i point = this.modeSelectorInteractionBlockPoint(map);
        Vec3i buriedPoint = this.buriedInteractionColumnPoint(point);
        if (world == null) {
            return;
        }
        int modeBlockId = BlockType.getBlockIdOrUnknown(MODE_INTERACTION_BLOCK, "Failed to find block '%s' in Bank Defense mode selector sync.", MODE_INTERACTION_BLOCK);
        if (modeBlockId == Integer.MIN_VALUE || modeBlockId == 0) {
            return;
        }
        BlockType modeBlockType = BlockType.getAssetMap().getAsset(modeBlockId);
        if (modeBlockType == null) {
            return;
        }
        if (point != null) {
            this.clearInteractionColumn(world, point, modeBlockId, NPC_INTERACTION_CLEAR_HEIGHT);
            this.clearInteractionColumn(world, buriedPoint, modeBlockId, NPC_INTERACTION_CLEAR_HEIGHT);
        }
        if (map != null && map.previousModePoint != null && !this.samePoint(map.previousModePoint, point)) {
            this.clearInteractionColumn(world, map.previousModePoint, modeBlockId, NPC_INTERACTION_CLEAR_HEIGHT);
            this.clearInteractionColumn(world, this.buriedInteractionColumnPoint(map.previousModePoint), modeBlockId, NPC_INTERACTION_CLEAR_HEIGHT);
        }
        if (map != null && map.controlPoint != null) {
            Vec3i controlFallback = new Vec3i(map.controlPoint.x - 2, map.controlPoint.y, map.controlPoint.z);
            if (!this.samePoint(controlFallback, point)) {
                this.clearInteractionColumn(world, controlFallback, modeBlockId, NPC_INTERACTION_CLEAR_HEIGHT);
                this.clearInteractionColumn(world, this.buriedInteractionColumnPoint(controlFallback), modeBlockId, NPC_INTERACTION_CLEAR_HEIGHT);
            }
        }
        if (map != null && map.vendorPoint != null) {
            Vec3i vendorFallback = new Vec3i(map.vendorPoint.x - 2, map.vendorPoint.y, map.vendorPoint.z);
            if (!this.samePoint(vendorFallback, point)) {
                this.clearInteractionColumn(world, vendorFallback, modeBlockId, NPC_INTERACTION_CLEAR_HEIGHT);
                this.clearInteractionColumn(world, this.buriedInteractionColumnPoint(vendorFallback), modeBlockId, NPC_INTERACTION_CLEAR_HEIGHT);
            }
        }
        if (map != null && map.playerStart != null) {
            Vec3i playerFallback = new Vec3i(map.playerStart.x - 2, map.playerStart.y, map.playerStart.z);
            if (!this.samePoint(playerFallback, point)) {
                this.clearInteractionColumn(world, playerFallback, modeBlockId, NPC_INTERACTION_CLEAR_HEIGHT);
                this.clearInteractionColumn(world, this.buriedInteractionColumnPoint(playerFallback), modeBlockId, NPC_INTERACTION_CLEAR_HEIGHT);
            }
        }
        if (map != null && map.bankCenter != null) {
            Vec3i bankFallback = new Vec3i(map.bankCenter.x - 2, map.bankCenter.y, map.bankCenter.z);
            if (!this.samePoint(bankFallback, point)) {
                this.clearInteractionColumn(world, bankFallback, modeBlockId, NPC_INTERACTION_CLEAR_HEIGHT);
                this.clearInteractionColumn(world, this.buriedInteractionColumnPoint(bankFallback), modeBlockId, NPC_INTERACTION_CLEAR_HEIGHT);
            }
        }
        if (buriedPoint != null && this.shouldShowModeTravelNpc(world, map)) {
            this.placeInteractionColumn(world, buriedPoint, modeBlockId, modeBlockType, NPC_INTERACTION_COLUMN_HEIGHT);
        }
    }

    private void syncDuoTeamInteractionBlock(World world, MapConfig map) {
        Vec3i point = this.duoTeamInteractionPoint(map);
        Vec3i buriedPoint = this.buriedInteractionColumnPoint(point);
        if (world == null) {
            return;
        }
        int blockId = BlockType.getBlockIdOrUnknown(DUO_TEAM_INTERACTION_BLOCK, "Failed to find block '%s' in Bank Defense duo team sync.", DUO_TEAM_INTERACTION_BLOCK);
        if (blockId == Integer.MIN_VALUE || blockId == 0) {
            return;
        }
        BlockType blockType = BlockType.getAssetMap().getAsset(blockId);
        if (blockType == null) {
            return;
        }
        if (point != null) {
            this.clearInteractionColumn(world, point, blockId, NPC_INTERACTION_CLEAR_HEIGHT);
            this.clearInteractionColumn(world, buriedPoint, blockId, NPC_INTERACTION_CLEAR_HEIGHT);
        }
        if (map != null && map.previousDuoTeamPoint != null && !this.samePoint(map.previousDuoTeamPoint, point)) {
            this.clearInteractionColumn(world, map.previousDuoTeamPoint, blockId, NPC_INTERACTION_CLEAR_HEIGHT);
            this.clearInteractionColumn(world, this.buriedInteractionColumnPoint(map.previousDuoTeamPoint), blockId, NPC_INTERACTION_CLEAR_HEIGHT);
        }
        if (buriedPoint != null && this.shouldShowDuoTeamSelectorNpc(world, map)) {
            this.placeInteractionColumn(world, buriedPoint, blockId, blockType, NPC_INTERACTION_COLUMN_HEIGHT);
        }
    }

    private void syncDuoInteractionBlock(World world, MapConfig map) {
        Vec3i point = this.duoTransitionInteractionBlockPoint(map);
        Vec3i buriedPoint = this.buriedInteractionColumnPoint(point);
        if (world == null) {
            return;
        }
        int duoBlockId = BlockType.getBlockIdOrUnknown(DUO_INTERACTION_BLOCK, "Failed to find block '%s' in Bank Defense duo sync.", DUO_INTERACTION_BLOCK);
        if (duoBlockId == Integer.MIN_VALUE || duoBlockId == 0) {
            return;
        }
        BlockType duoBlockType = BlockType.getAssetMap().getAsset(duoBlockId);
        if (duoBlockType == null) {
            return;
        }
        if (point != null) {
            this.clearInteractionColumn(world, point, duoBlockId, NPC_INTERACTION_CLEAR_HEIGHT);
            this.clearInteractionColumn(world, buriedPoint, duoBlockId, NPC_INTERACTION_CLEAR_HEIGHT);
        }
        if (map != null && map.previousDuoPoint != null && !this.samePoint(map.previousDuoPoint, point)) {
            this.clearInteractionColumn(world, map.previousDuoPoint, duoBlockId, NPC_INTERACTION_CLEAR_HEIGHT);
            this.clearInteractionColumn(world, this.buriedInteractionColumnPoint(map.previousDuoPoint), duoBlockId, NPC_INTERACTION_CLEAR_HEIGHT);
        }
        if (buriedPoint != null && this.shouldShowStandardDuoNpc(world)) {
            this.placeInteractionColumn(world, buriedPoint, duoBlockId, duoBlockType, NPC_INTERACTION_COLUMN_HEIGHT);
        }
    }

    private void syncStatsInteractionBlock(World world, MapConfig map) {
        Vec3i point = this.statsInteractionBlockPoint(map);
        Vec3i buriedPoint = this.buriedInteractionColumnPoint(point);
        if (world == null) {
            return;
        }
        int statsBlockId = BlockType.getBlockIdOrUnknown(STATS_INTERACTION_BLOCK, "Failed to find block '%s' in Bank Defense stats sync.", STATS_INTERACTION_BLOCK);
        if (statsBlockId == Integer.MIN_VALUE || statsBlockId == 0) {
            return;
        }
        BlockType statsBlockType = BlockType.getAssetMap().getAsset(statsBlockId);
        if (statsBlockType == null) {
            return;
        }
        if (point != null) {
            this.clearInteractionColumn(world, point, statsBlockId, NPC_INTERACTION_CLEAR_HEIGHT);
            this.clearInteractionColumn(world, buriedPoint, statsBlockId, NPC_INTERACTION_CLEAR_HEIGHT);
        }
        if (map != null && map.previousStatisticsPoint != null && !this.samePoint(map.previousStatisticsPoint, point)) {
            this.clearInteractionColumn(world, map.previousStatisticsPoint, statsBlockId, NPC_INTERACTION_CLEAR_HEIGHT);
            this.clearInteractionColumn(world, this.buriedInteractionColumnPoint(map.previousStatisticsPoint), statsBlockId, NPC_INTERACTION_CLEAR_HEIGHT);
        }
        if (buriedPoint != null && this.shouldShowStandardDuoNpc(world)) {
            this.placeInteractionColumn(world, buriedPoint, statsBlockId, statsBlockType, NPC_INTERACTION_COLUMN_HEIGHT);
        }
    }

    private void syncTutorialWizardInteractionBlocks(World world, MapConfig rawMap, TutorialState tutorial) {
        if (world == null || rawMap == null) {
            return;
        }
        int blockId = BlockType.getBlockIdOrUnknown(
            TUTORIAL_WIZARD_INTERACTION_BLOCK,
            "Failed to find block '%s' in Bank Defense tutorial wizard sync.",
            TUTORIAL_WIZARD_INTERACTION_BLOCK
        );
        if (blockId == Integer.MIN_VALUE || blockId == 0) {
            return;
        }
        BlockType blockType = BlockType.getAssetMap().getAsset(blockId);
        if (blockType == null) {
            return;
        }

        Vec3i introPoint = tutorial != null && !tutorial.active ? rawMap.tutorialWizardIntroPoint : null;
        Vec3i guidePoint = tutorial != null && tutorial.active ? rawMap.tutorialWizardPoint : null;
        List<Vec3i> cleanupPoints = new ArrayList<>();
        if (rawMap.tutorialWizardIntroPoint != null) {
            cleanupPoints.add(rawMap.tutorialWizardIntroPoint);
        }
        if (rawMap.tutorialWizardPoint != null) {
            cleanupPoints.add(rawMap.tutorialWizardPoint);
        }
        for (Vec3i point : cleanupPoints) {
            if (point == null) {
                continue;
            }
            this.clearInteractionColumn(world, point, blockId, NPC_INTERACTION_CLEAR_HEIGHT);
            this.clearInteractionColumn(world, this.buriedInteractionColumnPoint(point), blockId, NPC_INTERACTION_CLEAR_HEIGHT);
        }
        List<Vec3i> activePoints = new ArrayList<>();
        if (introPoint != null) {
            activePoints.add(introPoint);
        }
        if (guidePoint != null) {
            activePoints.add(guidePoint);
        }
        for (Vec3i point : activePoints) {
            Vec3i buriedPoint = this.buriedInteractionColumnPoint(point);
            if (buriedPoint != null) {
                this.placeInteractionColumn(world, buriedPoint, blockId, blockType, NPC_INTERACTION_COLUMN_HEIGHT);
            }
        }
    }

    private Vec3i buriedInteractionColumnPoint(Vec3i basePoint) {
        if (basePoint == null) {
            return null;
        }
        return new Vec3i(basePoint.x, basePoint.y - NPC_INTERACTION_BURY_DEPTH, basePoint.z);
    }

    private void clearStaleSlotBlock(World world, Vec3i point, int slotBlockId) {
        if (world == null || point == null) {
            return;
        }
        WorldChunk chunk = world.getChunkIfInMemory(ChunkUtil.indexChunkFromBlock(point.x, point.z));
        if (chunk == null) {
            return;
        }
        int existingBlockId = chunk.getBlock(point.x, point.y, point.z);
        if (existingBlockId == 0) {
            return;
        }
        BlockType existingBlockType = chunk.getBlockType(point.x, point.y, point.z);
        if (existingBlockId == slotBlockId || this.isLegacyDebugBlock(existingBlockType)) {
            chunk.breakBlock(point.x, point.y, point.z);
        }
    }

    private void placeInteractionColumn(World world, Vec3i basePoint, int blockId, BlockType blockType, int height) {
        if (world == null || basePoint == null || blockId == Integer.MIN_VALUE || blockId == 0 || blockType == null) {
            return;
        }
        for (int i = 0; i < height; i++) {
            Vec3i point = new Vec3i(basePoint.x, basePoint.y + i, basePoint.z);
            WorldChunk chunk = world.getChunkIfInMemory(ChunkUtil.indexChunkFromBlock(point.x, point.z));
            if (chunk == null) {
                continue;
            }
            int existingBlockId = chunk.getBlock(point.x, point.y, point.z);
            if (existingBlockId != blockId) {
                chunk.setBlock(point.x, point.y, point.z, blockId, blockType, 0, 0, 4);
            }
        }
    }

    private void clearInteractionColumn(World world, Vec3i basePoint, int blockId, int height) {
        if (world == null || basePoint == null || height <= 0) {
            return;
        }
        for (int i = -1; i < height; i++) {
            this.clearStaleSlotBlock(world, new Vec3i(basePoint.x, basePoint.y + i, basePoint.z), blockId);
        }
    }

    @SafeVarargs
    private final void purgeOperatorArea(World world, Vec3i anchor, Ref<EntityStore>... keepRefs) {
        if (world == null || anchor == null) {
            return;
        }
        this.clearOperatorBlocks(world, anchor);
        Store<EntityStore> store = world.getEntityStore().getStore();
        Set<Ref<EntityStore>> keep = new HashSet<>();
        if (keepRefs != null) {
            for (Ref<EntityStore> ref : keepRefs) {
                if (ref != null && ref.isValid()) {
                    keep.add(ref);
                }
            }
        }
        double maxDistanceSquared = 3.75 * 3.75;
        Set<Ref<EntityStore>> visited = new HashSet<>();
        for (long chunkIndex : world.getChunkStore().getChunkIndexes()) {
            WorldChunk chunk = world.getChunkIfLoaded(chunkIndex);
            if (chunk == null || chunk.getEntityChunk() == null) {
                continue;
            }
            List<Object> entityRefs = new ArrayList<>(chunk.getEntityChunk().getEntityReferences());
            for (Object refObject : entityRefs) {
                if (!(refObject instanceof Ref<?> rawRef)) {
                    continue;
                }
                @SuppressWarnings("unchecked")
                Ref<EntityStore> ref = (Ref<EntityStore>) rawRef;
                if (ref == null || !ref.isValid() || !visited.add(ref) || keep.contains(ref)) {
                    continue;
                }
                if (store.getComponent(ref, Player.getComponentType()) != null) {
                    continue;
                }
                TransformComponent transform = store.getComponent(ref, TransformComponent.getComponentType());
                if (transform == null || transform.getPosition() == null) {
                    continue;
                }
                Vector3d position = transform.getPosition();
                double dx = position.getX() - (anchor.x + 0.5);
                double dy = position.getY() - anchor.y;
                double dz = position.getZ() - (anchor.z + 0.5);
                if ((dx * dx) + (dy * dy) + (dz * dz) > maxDistanceSquared) {
                    continue;
                }
                this.removeVisualRef(store, ref);
            }
        }
    }

    private void clearOperatorBlocks(World world, Vec3i anchor) {
        if (world == null || anchor == null) {
            return;
        }
        int controlBlockId = BlockType.getBlockIdOrUnknown(CONTROL_INTERACTION_BLOCK, "Failed to find block '%s' in Bank Defense control cleanup.", CONTROL_INTERACTION_BLOCK);
        int vendorBlockId = BlockType.getBlockIdOrUnknown(VENDOR_INTERACTION_BLOCK, "Failed to find block '%s' in Bank Defense vendor cleanup.", VENDOR_INTERACTION_BLOCK);
        int modeBlockId = BlockType.getBlockIdOrUnknown(MODE_INTERACTION_BLOCK, "Failed to find block '%s' in Bank Defense mode cleanup.", MODE_INTERACTION_BLOCK);
        int duoTeamBlockId = BlockType.getBlockIdOrUnknown(DUO_TEAM_INTERACTION_BLOCK, "Failed to find block '%s' in Bank Defense duo team cleanup.", DUO_TEAM_INTERACTION_BLOCK);
        int duoBlockId = BlockType.getBlockIdOrUnknown(DUO_INTERACTION_BLOCK, "Failed to find block '%s' in Bank Defense duo cleanup.", DUO_INTERACTION_BLOCK);
        int statsBlockId = BlockType.getBlockIdOrUnknown(STATS_INTERACTION_BLOCK, "Failed to find block '%s' in Bank Defense stats cleanup.", STATS_INTERACTION_BLOCK);
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                for (int dy = -6; dy <= 4; dy++) {
                    Vec3i point = new Vec3i(anchor.x + dx, anchor.y + dy, anchor.z + dz);
                    WorldChunk chunk = world.getChunkIfInMemory(ChunkUtil.indexChunkFromBlock(point.x, point.z));
                    if (chunk == null) {
                        continue;
                    }
                    int existingBlockId = chunk.getBlock(point.x, point.y, point.z);
                    if (existingBlockId == 0) {
                        continue;
                    }
                    BlockType existingBlockType = chunk.getBlockType(point.x, point.y, point.z);
                    if (existingBlockId == controlBlockId || existingBlockId == vendorBlockId || existingBlockId == modeBlockId || existingBlockId == duoTeamBlockId || existingBlockId == duoBlockId || existingBlockId == statsBlockId || this.isLegacyDebugBlock(existingBlockType)) {
                        chunk.breakBlock(point.x, point.y, point.z);
                    }
                }
            }
        }
    }

    private Vec3i controlInteractionBlockPointFor(Vec3i operatorPoint, MapConfig map) {
        if (operatorPoint == null) {
            return null;
        }
        return operatorPoint;
    }

    private Vec3i legacyRearControlInteractionBlockPointFor(Vec3i operatorPoint, MapConfig map) {
        if (operatorPoint == null) {
            return null;
        }
        Vec3i lookTarget = map != null && map.bankCenter != null ? map.bankCenter : map != null ? map.spawnPoint : null;
        if (lookTarget == null) {
            return operatorPoint;
        }
        int dx = Integer.compare(lookTarget.x, operatorPoint.x);
        int dz = Integer.compare(lookTarget.z, operatorPoint.z);
        if (dx == 0 && dz == 0) {
            return operatorPoint;
        }
        if (Math.abs(lookTarget.x - operatorPoint.x) >= Math.abs(lookTarget.z - operatorPoint.z)) {
            dz = 0;
        } else {
            dx = 0;
        }
        return new Vec3i(operatorPoint.x - dx, operatorPoint.y, operatorPoint.z - dz);
    }

    private void clearTrackedEnemyVisuals(World world, PresentationState presentation) {
        if (world == null || presentation == null) {
            return;
        }
        Store<EntityStore> store = world.getEntityStore().getStore();
        for (Ref<EntityStore> ref : presentation.enemyRefs.values()) {
            this.removeVisualRef(store, ref);
        }
        presentation.enemyRefs.clear();
        presentation.enemyVisualStates.clear();
        presentation.enemyVisualGarbageSweepRemaining = 0.0;
        presentation.lastWantedEnemyVisuals = 0;
    }

    private List<EnemyVisualViewer> collectEnemyVisualViewers(World world) {
        if (world == null) {
            return Collections.emptyList();
        }
        Store<EntityStore> store = world.getEntityStore().getStore();
        List<EnemyVisualViewer> viewers = new ArrayList<>();
        for (PlayerRef playerRef : world.getPlayerRefs()) {
            if (playerRef == null) {
                continue;
            }
            Ref<EntityStore> playerEntityRef = playerRef.getReference();
            if (playerEntityRef == null || !playerEntityRef.isValid()) {
                continue;
            }
            TransformComponent transform = store.getComponent(playerEntityRef, TransformComponent.getComponentType());
            EntityTrackerSystems.EntityViewer entityViewer = store.getComponent(playerEntityRef, EntityTrackerSystems.EntityViewer.getComponentType());
            ChunkTracker chunkTracker = store.getComponent(playerEntityRef, ChunkTracker.getComponentType());
            if (transform == null
                || transform.getPosition() == null
                || entityViewer == null
                || chunkTracker == null
                || !chunkTracker.isReadyForChunks()) {
                continue;
            }
            int viewRadiusBlocks = Math.max(0, entityViewer.viewRadiusBlocks);
            if (viewRadiusBlocks <= 0) {
                continue;
            }
            viewers.add(new EnemyVisualViewer(playerEntityRef, transform.getPosition(), viewRadiusBlocks, chunkTracker));
        }
        return viewers;
    }

    private boolean shouldRenderEnemyVisual(Vector3d enemyPosition, List<EnemyVisualViewer> viewers) {
        if (enemyPosition == null || viewers == null || viewers.isEmpty()) {
            return false;
        }
        long enemyChunkIndex = ChunkUtil.indexChunkFromBlock((int)Math.floor(enemyPosition.x), (int)Math.floor(enemyPosition.z));
        for (EnemyVisualViewer viewer : viewers) {
            if (viewer == null || viewer.chunkTracker() == null || !viewer.chunkTracker().isLoaded(enemyChunkIndex)) {
                continue;
            }
            double radius = viewer.viewRadiusBlocks();
            double dx = enemyPosition.x - viewer.position().x;
            double dy = enemyPosition.y - viewer.position().y;
            double dz = enemyPosition.z - viewer.position().z;
            if ((dx * dx) + (dy * dy) + (dz * dz) <= radius * radius) {
                return true;
            }
        }
        return false;
    }

    private EnemyVisualState enemyVisualState(PresentationState presentation, Long enemyId) {
        return presentation.enemyVisualStates.computeIfAbsent(enemyId, ignored -> new EnemyVisualState());
    }

    private int purgeOwnedEnemyVisualsExcept(
        World world,
        BankDefenseRepository.Snapshot snapshot,
        MatchContext context,
        Set<Ref<EntityStore>> keepRefs
    ) {
        if (world == null || snapshot == null || context == null || context.enemies.isEmpty()) {
            return 0;
        }
        Set<UUID> activeOwners = new HashSet<>();
        for (EnemyInstance enemy : context.enemies) {
            activeOwners.add(this.visualOwnerUuid(world, this.enemyVisualOwnerKey(context, enemy)));
        }
        if (activeOwners.isEmpty()) {
            return 0;
        }
        Set<Ref<EntityStore>> keep = keepRefs == null ? Collections.emptySet() : keepRefs;
        Store<EntityStore> store = world.getEntityStore().getStore();
        Set<Long> cleanupChunks = this.collectVisualCleanupChunkIndexes(snapshot);
        Set<Ref<EntityStore>> visited = new HashSet<>();
        int removed = 0;
        for (long chunkIndex : cleanupChunks) {
            WorldChunk chunk = this.resolveVisualCleanupChunk(world, chunkIndex);
            if (chunk == null || chunk.getEntityChunk() == null) {
                continue;
            }
            List<Object> entityRefs = new ArrayList<>(chunk.getEntityChunk().getEntityReferences());
            for (Object refObject : entityRefs) {
                if (!(refObject instanceof Ref<?> rawRef)) {
                    continue;
                }
                @SuppressWarnings("unchecked")
                Ref<EntityStore> ref = (Ref<EntityStore>) rawRef;
                if (ref == null || !ref.isValid() || !visited.add(ref) || keep.contains(ref)) {
                    continue;
                }
                UUIDComponent uuidComponent = store.getComponent(ref, UUIDComponent.getComponentType());
                if (uuidComponent == null || !activeOwners.contains(uuidComponent.getUuid())) {
                    continue;
                }
                if (this.removeVisualRef(store, ref)) {
                    removed++;
                }
            }
        }
        return removed;
    }

    private void syncEnemyVisuals(World world, PresentationState presentation, MatchContext context, double deltaSeconds) {
        Store<EntityStore> store = world.getEntityStore().getStore();
        if (context == null) {
            this.clearTrackedEnemyVisuals(world, presentation);
            presentation.lastEnemyViewerCount = 0;
            presentation.lastDuplicateEnemyVisualsRemoved = 0;
            presentation.lastEnemyVisualEpoch = "";
            BankDefenseRepository.Snapshot cachedSnapshot = presentation.cachedRawSnapshot;
            if (cachedSnapshot != null) {
                this.purgeDetachedEnemyVisuals(world, cachedSnapshot, Collections.emptySet());
            } else {
                this.purgeDetachedEnemyVisuals(world, Collections.emptySet());
            }
            return;
        }

        List<EnemyVisualViewer> viewers = this.collectEnemyVisualViewers(world);
        presentation.lastEnemyViewerCount = viewers.size();
        presentation.lastEnemyVisualEpoch = context.visualEpoch;
        if (viewers.isEmpty()) {
            this.clearTrackedEnemyVisuals(world, presentation);
            int removed = 0;
            if (context.snapshot != null) {
                removed += this.purgeOwnedEnemyVisualsExcept(world, context.snapshot, context, Collections.emptySet());
                removed += this.purgeDetachedEnemyVisuals(world, context.snapshot, Collections.emptySet());
            }
            presentation.lastDuplicateEnemyVisualsRemoved = removed;
            return;
        }

        presentation.enemyVisualGarbageSweepRemaining = Math.max(0.0, presentation.enemyVisualGarbageSweepRemaining - deltaSeconds);
        Set<Long> activeEnemyIds = new HashSet<>();
        Set<Ref<EntityStore>> keepRefs = new HashSet<>();
        List<Ref<EntityStore>> auraPlayerRefs = null;
        for (EnemyInstance enemy : context.enemies) {
            Long enemyId = Long.valueOf(enemy.instanceId);
            activeEnemyIds.add(enemyId);
            Vector3d enemyPosition = this.enemyWorldPosition(context, enemy);
            if (!this.shouldRenderEnemyVisual(enemyPosition, viewers)) {
                Ref<EntityStore> existingRef = presentation.enemyRefs.remove(enemyId);
                if (existingRef != null) {
                    this.removeVisualRef(store, existingRef);
                }
                presentation.enemyVisualStates.remove(enemyId);
                continue;
            }

            Ref<EntityStore> ref = presentation.enemyRefs.get(enemyId);
            EnemyVisualState visualState = this.enemyVisualState(presentation, enemyId);
            RoleModelProfile profile = this.enemyProfile(context, enemy);
            String visualProfileKey = this.enemyVisualProfileKey(profile);
            if (!this.isUsableEnemyVisualRef(store, ref)) {
                ref = null;
                visualState.resetLifecycle();
            }
            if (ref != null && !visualProfileKey.equals(visualState.visualProfileKey)) {
                this.removeVisualRef(store, ref);
                presentation.enemyRefs.remove(enemyId);
                ref = null;
                visualState.resetLifecycle();
            }
            if (ref == null) {
                ref = this.spawnEnemyVisual(world, context, enemy, this.enemyVisualOwnerKey(context, enemy), profile);
                if (ref == null) {
                    presentation.enemyRefs.remove(enemyId);
                    presentation.enemyVisualStates.remove(enemyId);
                    continue;
                }
                presentation.enemyRefs.put(enemyId, ref);
                visualState.resetLifecycle();
            }
            visualState.visualProfileKey = visualProfileKey;

            keepRefs.add(ref);
            this.updateTransform(store, ref, enemyPosition, enemy.visualRotation);
            visualState.currentMovementAnimationId = this.syncEnemyMovementAnimation(store, ref, enemy, visualState.currentMovementAnimationId);
            visualState.visualStateSyncRemaining = Math.max(0.0, visualState.visualStateSyncRemaining - deltaSeconds);
            if (visualState.visualStateSyncRemaining <= 0.0) {
                this.syncEnemyVisualState(world, store, ref, enemy);
                visualState.visualStateSyncRemaining = enemy.dying
                    ? ENEMY_VISUAL_STATE_DYING_INTERVAL_SECONDS
                    : ENEMY_VISUAL_STATE_INTERVAL_SECONDS;
            }
            visualState.auraVisualCooldownRemaining = Math.max(0.0, visualState.auraVisualCooldownRemaining - deltaSeconds);
            if (visualState.auraVisualCooldownRemaining <= 0.0 && this.shouldEmitEnemyAura(enemy)) {
                if (auraPlayerRefs == null) {
                    auraPlayerRefs = this.collectPlayerEntityRefs(world);
                }
                this.emitEnemyAura(world, enemy, enemyPosition, auraPlayerRefs);
                visualState.auraVisualCooldownRemaining = enemy.isBossCasting()
                    ? ENEMY_AURA_CAST_INTERVAL_SECONDS
                    : ENEMY_AURA_INTERVAL_SECONDS;
            }
        }

        List<Long> stale = new ArrayList<>();
        for (Map.Entry<Long, Ref<EntityStore>> entry : presentation.enemyRefs.entrySet()) {
            if (activeEnemyIds.contains(entry.getKey())) {
                continue;
            }
            this.removeVisualRef(store, entry.getValue());
            stale.add(entry.getKey());
        }
        for (Long enemyId : stale) {
            presentation.enemyRefs.remove(enemyId);
            presentation.enemyVisualStates.remove(enemyId);
        }
        presentation.enemyVisualStates.keySet().removeIf(enemyId -> !activeEnemyIds.contains(enemyId));
        presentation.lastWantedEnemyVisuals = keepRefs.size();

        int removed = 0;
        if (context.snapshot != null && presentation.enemyVisualGarbageSweepRemaining <= 0.0) {
            removed += this.purgeOwnedEnemyVisualsExcept(world, context.snapshot, context, keepRefs);
            removed += this.purgeDetachedEnemyVisuals(world, context.snapshot, keepRefs);
            presentation.enemyVisualGarbageSweepRemaining = ENEMY_VISUAL_GARBAGE_SWEEP_SECONDS;
        }
        presentation.lastDuplicateEnemyVisualsRemoved = removed;
    }

    private boolean shouldEmitEnemyAura(EnemyInstance enemy) {
        if (enemy == null || enemy.definition == null || enemy.definition.id == null) {
            return false;
        }
        return switch (enemy.definition.id) {
            case "elite_robber",
                "vault_breaker",
                "necro_king",
                "goblin_bomber_boss",
                ENEMY_SEAL_MASTER,
                "runner_bomber",
                "jammer",
                "necro_thief",
                "vault_priest",
                "bone_trumpeter",
                "plague_standard",
                "curse_weaver" -> true;
            default -> false;
        };
    }

    private BankDefenseRepository.Snapshot currentPresentationRawSnapshot(World world, PresentationState presentation) throws IOException {
        if (presentation == null || presentation.cachedRawSnapshot == null) {
            BankDefenseRepository.Snapshot snapshot = this.repository.loadSnapshot(world);
            if (presentation != null) {
                presentation.cachedRawSnapshot = snapshot;
            }
            return snapshot;
        }
        return presentation.cachedRawSnapshot;
    }

    private boolean isChunkLoadedForPosition(World world, Vector3d position) {
        if (world == null || position == null) {
            return false;
        }
        return world.getChunkIfInMemory(ChunkUtil.indexChunkFromBlock((int)Math.floor(position.x), (int)Math.floor(position.z))) != null;
    }

    private void syncTowerVisualTransforms(World world, PresentationState presentation, MatchContext context, double deltaSeconds) {
        if (context == null) {
            return;
        }
        Store<EntityStore> store = world.getEntityStore().getStore();
        Set<String> activeTowerKeys = new HashSet<>();
        for (TowerInstance tower : context.placedTowers.values()) {
            String key = KEY_TOWER_PREFIX + tower.slot.id;
            activeTowerKeys.add(key);
            String desiredVisualProfileKey = this.towerVisualProfileKey(tower);
            Ref<EntityStore> ref = presentation.staticRefs.get(key);
            boolean profileChanged = !desiredVisualProfileKey.equals(tower.visualProfileKey);
            if (profileChanged) {
                this.removeVisualRef(store, ref);
                presentation.staticRefs.remove(key);
                tower.visualProfileKey = desiredVisualProfileKey;
                tower.visualSpawnedOnce = false;
                ref = null;
            }
            if (!this.isUsableEnemyVisualRef(store, ref)) {
                presentation.staticRefs.remove(key);
                ref = null;
            }
            if ((ref == null || !ref.isValid()) && !tower.visualSpawnedOnce) {
                ref = this.spawnTowerVisual(world, tower, this.towerVisualOwnerKey(tower, desiredVisualProfileKey));
                if (ref == null) {
                    presentation.staticRefs.remove(key);
                    continue;
                }
            }
            presentation.staticRefs.put(key, ref);
            this.ensureInteractableVisual(store, ref);
            this.updateTransform(store, ref, this.towerWorldPosition(tower.slot), tower.visualRotation);
            this.syncTowerVisualState(world, store, ref, tower);
        }
        List<String> staleKeys = new ArrayList<>();
        for (String key : presentation.staticRefs.keySet()) {
            if (key != null && key.startsWith(KEY_TOWER_PREFIX) && !activeTowerKeys.contains(key)) {
                staleKeys.add(key);
            }
        }
        for (String key : staleKeys) {
            Ref<EntityStore> ref = presentation.staticRefs.remove(key);
            this.removeVisualRef(store, ref);
        }
    }

    private void syncTowerRangePreview(World world, PresentationState presentation, MatchContext context) {
        if (presentation == null) {
            return;
        }
        if (!presentation.towerRangePreviewEnabled || context == null) {
            this.clearTowerRangePreview(world, presentation);
            return;
        }

        Store<EntityStore> store = world.getEntityStore().getStore();
        Set<String> keepKeys = new HashSet<>();
        for (TowerInstance tower : context.placedTowers.values()) {
            double range = this.towerAttackRange(context, tower);
            Vector3d center = this.objectiveWorldPosition(tower.slot.position, 0.06);
            int points = 16;
            for (int index = 0; index < points; index++) {
                double angle = (Math.PI * 2.0 * index) / points;
                Vector3d markerPosition = new Vector3d(
                    center.x + Math.cos(angle) * range,
                    center.y,
                    center.z + Math.sin(angle) * range
                );
                String key = KEY_RANGE_PREFIX + tower.slot.id + ":" + index;
                keepKeys.add(key);
                Ref<EntityStore> ref = presentation.staticRefs.get(key);
                if (ref == null || !ref.isValid()) {
                    Model markerModel = this.resolveModel(MODEL_PATH, 0.18f);
                    if (markerModel == null) {
                        continue;
                    }
                    ref = this.createVisualEntity(world, markerModel, markerPosition, new Vector3f(0.0f, 0.0f, 0.0f), null);
                    if (ref != null) {
                        presentation.staticRefs.put(key, ref);
                    }
                } else {
                    this.updateTransform(store, ref, markerPosition, new Vector3f(0.0f, 0.0f, 0.0f));
                }
            }
        }

        List<String> staleKeys = new ArrayList<>();
        for (String key : presentation.staticRefs.keySet()) {
            if (key.startsWith(KEY_RANGE_PREFIX) && !keepKeys.contains(key)) {
                staleKeys.add(key);
            }
        }
        for (String key : staleKeys) {
            Ref<EntityStore> ref = presentation.staticRefs.remove(key);
            this.removeVisualRef(store, ref);
        }
    }

    private void clearTowerRangePreview(World world, PresentationState presentation) {
        if (world == null || presentation == null) {
            return;
        }
        Store<EntityStore> store = world.getEntityStore().getStore();
        List<String> staleKeys = new ArrayList<>();
        for (String key : presentation.staticRefs.keySet()) {
            if (key.startsWith(KEY_RANGE_PREFIX)) {
                staleKeys.add(key);
            }
        }
        for (String key : staleKeys) {
            Ref<EntityStore> ref = presentation.staticRefs.remove(key);
            this.removeVisualRef(store, ref);
        }
    }

    private String towerNameplate(World world, TowerInstance tower) {
        PlayerRef playerRef = this.primaryPlayerRef(world);
        String towerName = BankDefenseLocalization.towerDisplayName(playerRef, tower.definition.id, this.text(tower.definition.displayName));
        String status = tower.disabledRemaining > 0.0
            ? BankDefenseLocalization.choose(playerRef, " [ОТКЛ.]", " [DISABLED]")
            : "";
        return towerName + " " + BankDefenseLocalization.choose(playerRef, "Ур. ", "Lvl. ") + tower.getCurrentLevel().level + status;
    }

    private String enemyNameplate(World world, EnemyInstance enemy) {
        PlayerRef playerRef = this.primaryPlayerRef(world);
        return this.showEnemyNameplate(enemy.definition.id, enemy.definition.bossEnemy)
            ? BankDefenseLocalization.enemyDisplayName(playerRef, enemy.definition.id, this.text(enemy.definition.displayName)) + this.enemyNameplateSuffix(world, enemy)
            : "";
    }

    private String enemyNameplateSuffix(World world, EnemyInstance enemy) {
        if (enemy == null || enemy.definition == null || enemy.definition.id == null) {
            return "";
        }
        return switch (enemy.definition.id) {
            case ENEMY_RIFT_TWIN_ALPHA, ENEMY_RIFT_TWIN_BETA -> enemy.damageableTwin
                ? this.choose(world, " [УЯЗВ.]", " [OPEN]")
                : this.choose(world, " [ЩИТ]", " [SHIELDED]");
            case ENEMY_NODE_ARBITER -> " [" + this.romanLifeLabel(enemy.extraLivesRemaining + 1) + "]";
            case ENEMY_SEAL_NODE -> this.choose(world, " [УЗЕЛ]", " [NODE]");
            default -> "";
        };
    }

    private String enemyTrackedNameplate(World world, EnemyInstance enemy) {
        return enemy == null ? "" : this.enemyNameplate(world, enemy);
    }

    private void syncTowerVisualState(World world, Store<EntityStore> store, Ref<EntityStore> ref, TowerInstance tower) {
        this.syncNameplate(store, ref, this.towerNameplate(world, tower));
    }

    private void syncEnemyVisualState(World world, Store<EntityStore> store, Ref<EntityStore> ref, EnemyInstance enemy) {
        this.syncNameplate(store, ref, this.enemyTrackedNameplate(world, enemy));
        this.syncEnemyHealthBar(store, ref, enemy);
    }

    private void syncNameplate(Store<EntityStore> store, Ref<EntityStore> ref, String text) {
        if (ref == null || !ref.isValid()) {
            return;
        }
        Nameplate nameplate = store.ensureAndGetComponent(ref, Nameplate.getComponentType());
        nameplate.setText(this.text(text));
    }

    private boolean isUsableEnemyVisualRef(Store<EntityStore> store, Ref<EntityStore> ref) {
        if (store == null || ref == null || !ref.isValid()) {
            return false;
        }
        if (store.getComponent(ref, TransformComponent.getComponentType()) == null) {
            return false;
        }
        if (store.getComponent(ref, Intangible.getComponentType()) == null) {
            return false;
        }
        return store.getComponent(ref, ModelComponent.getComponentType()) != null
            || store.getComponent(ref, NPCEntity.getComponentType()) != null
            || store.getComponent(ref, PlayerSkinComponent.getComponentType()) != null;
    }

    private boolean isVisualInLoadedChunk(World world, Store<EntityStore> store, Ref<EntityStore> ref) {
        if (world == null || store == null || ref == null || !ref.isValid()) {
            return false;
        }
        TransformComponent transform = store.getComponent(ref, TransformComponent.getComponentType());
        return transform != null
            && transform.getPosition() != null
            && this.isChunkLoadedForPosition(world, transform.getPosition());
    }

    private void clearProjectileVisuals(World world, PresentationState presentation) {
        if (world == null || presentation == null || presentation.projectileVisuals.isEmpty()) {
            return;
        }
        Store<EntityStore> store = world.getEntityStore().getStore();
        for (ProjectileVisual projectile : presentation.projectileVisuals) {
            this.removeVisualRef(store, projectile.ref);
        }
        presentation.projectileVisuals.clear();
    }

    private int purgeDetachedTowerVisuals(World world, Set<Ref<EntityStore>> keepRefs) {
        Store<EntityStore> store = world.getEntityStore().getStore();
        int removed = 0;
        Set<Ref<EntityStore>> keep = keepRefs == null ? Collections.emptySet() : keepRefs;
        Set<Ref<EntityStore>> visited = new HashSet<>();
        for (long chunkIndex : world.getChunkStore().getChunkIndexes()) {
            WorldChunk chunk = world.getChunkIfLoaded(chunkIndex);
            if (chunk == null || chunk.getEntityChunk() == null) {
                continue;
            }
            List<Object> entityRefs = new ArrayList<>(chunk.getEntityChunk().getEntityReferences());
            for (Object refObject : entityRefs) {
                if (!(refObject instanceof Ref<?> rawRef)) {
                    continue;
                }
                @SuppressWarnings("unchecked")
                Ref<EntityStore> ref = (Ref<EntityStore>) rawRef;
                if (ref == null || !ref.isValid() || !visited.add(ref) || keep.contains(ref)) {
                    continue;
                }
                if (!this.isDetachedTowerVisual(store, ref)) {
                    continue;
                }
                if (this.removeVisualRef(store, ref)) {
                    removed++;
                }
            }
        }
        return removed;
    }

    private int purgeDetachedEnemyVisuals(World world, Set<Ref<EntityStore>> keepRefs) {
        Store<EntityStore> store = world.getEntityStore().getStore();
        int removed = 0;
        Set<Ref<EntityStore>> keep = keepRefs == null ? Collections.emptySet() : keepRefs;
        Set<Ref<EntityStore>> visited = new HashSet<>();
        for (long chunkIndex : world.getChunkStore().getChunkIndexes()) {
            WorldChunk chunk = world.getChunkIfLoaded(chunkIndex);
            if (chunk == null || chunk.getEntityChunk() == null) {
                continue;
            }
            List<Object> entityRefs = new ArrayList<>(chunk.getEntityChunk().getEntityReferences());
            for (Object refObject : entityRefs) {
                if (!(refObject instanceof Ref<?> rawRef)) {
                    continue;
                }
                @SuppressWarnings("unchecked")
                Ref<EntityStore> ref = (Ref<EntityStore>) rawRef;
                if (ref == null || !ref.isValid() || !visited.add(ref) || keep.contains(ref)) {
                    continue;
                }
                if (!this.isDetachedEnemyVisual(store, ref)) {
                    continue;
                }
                if (this.removeVisualRef(store, ref)) {
                    removed++;
                }
            }
        }
        return removed;
    }

    private int purgeDetachedEnemyVisuals(World world, BankDefenseRepository.Snapshot snapshot, Set<Ref<EntityStore>> keepRefs) {
        if (world == null || snapshot == null) {
            return 0;
        }
        Store<EntityStore> store = world.getEntityStore().getStore();
        int removed = 0;
        Set<Ref<EntityStore>> keep = keepRefs == null ? Collections.emptySet() : keepRefs;
        Set<Ref<EntityStore>> visited = new HashSet<>();
        for (long chunkIndex : this.collectVisualCleanupChunkIndexes(snapshot)) {
            WorldChunk chunk = this.resolveVisualCleanupChunk(world, chunkIndex);
            if (chunk == null || chunk.getEntityChunk() == null) {
                continue;
            }
            List<Object> entityRefs = new ArrayList<>(chunk.getEntityChunk().getEntityReferences());
            for (Object refObject : entityRefs) {
                if (!(refObject instanceof Ref<?> rawRef)) {
                    continue;
                }
                @SuppressWarnings("unchecked")
                Ref<EntityStore> ref = (Ref<EntityStore>) rawRef;
                if (ref == null || !ref.isValid() || !visited.add(ref) || keep.contains(ref)) {
                    continue;
                }
                if (!this.isDetachedEnemyVisual(store, ref)) {
                    continue;
                }
                if (this.removeVisualRef(store, ref)) {
                    removed++;
                }
            }
        }
        return removed;
    }

    private int purgeDetachedProjectileVisuals(World world, Set<Ref<EntityStore>> keepRefs) {
        Store<EntityStore> store = world.getEntityStore().getStore();
        int removed = 0;
        Set<Ref<EntityStore>> keep = keepRefs == null ? Collections.emptySet() : keepRefs;
        Set<Ref<EntityStore>> visited = new HashSet<>();
        for (long chunkIndex : world.getChunkStore().getChunkIndexes()) {
            WorldChunk chunk = world.getChunkIfLoaded(chunkIndex);
            if (chunk == null || chunk.getEntityChunk() == null) {
                continue;
            }
            List<Object> entityRefs = new ArrayList<>(chunk.getEntityChunk().getEntityReferences());
            for (Object refObject : entityRefs) {
                if (!(refObject instanceof Ref<?> rawRef)) {
                    continue;
                }
                @SuppressWarnings("unchecked")
                Ref<EntityStore> ref = (Ref<EntityStore>) rawRef;
                if (ref == null || !ref.isValid() || !visited.add(ref) || keep.contains(ref)) {
                    continue;
                }
                if (!this.isDetachedProjectileVisual(store, ref)) {
                    continue;
                }
                if (this.removeVisualRef(store, ref)) {
                    removed++;
                }
            }
        }
        return removed;
    }

    private int purgeOwnedCombatVisuals(World world, BankDefenseRepository.Snapshot snapshot, MatchContext context) {
        if (world == null || snapshot == null || context == null) {
            return 0;
        }
        Set<UUID> activeOwners = new HashSet<>();
        for (EnemyInstance enemy : context.enemies) {
            activeOwners.add(this.visualOwnerUuid(world, this.enemyVisualOwnerKey(context, enemy)));
        }
        for (TowerInstance tower : context.placedTowers.values()) {
            String visualProfileKey = this.towerVisualProfileKey(tower);
            activeOwners.add(this.visualOwnerUuid(world, this.towerVisualOwnerKey(tower, visualProfileKey)));
        }
        if (activeOwners.isEmpty()) {
            return 0;
        }
        Store<EntityStore> store = world.getEntityStore().getStore();
        Set<Long> cleanupChunks = this.collectVisualCleanupChunkIndexes(snapshot);
        Set<Ref<EntityStore>> visited = new HashSet<>();
        int removed = 0;
        for (long chunkIndex : cleanupChunks) {
            WorldChunk chunk = this.resolveVisualCleanupChunk(world, chunkIndex);
            if (chunk == null || chunk.getEntityChunk() == null) {
                continue;
            }
            List<Object> entityRefs = new ArrayList<>(chunk.getEntityChunk().getEntityReferences());
            for (Object refObject : entityRefs) {
                if (!(refObject instanceof Ref<?> rawRef)) {
                    continue;
                }
                @SuppressWarnings("unchecked")
                Ref<EntityStore> ref = (Ref<EntityStore>) rawRef;
                if (ref == null || !ref.isValid() || !visited.add(ref)) {
                    continue;
                }
                UUIDComponent uuidComponent = store.getComponent(ref, UUIDComponent.getComponentType());
                if (uuidComponent == null || !activeOwners.contains(uuidComponent.getUuid())) {
                    continue;
                }
                if (this.removeVisualRef(store, ref)) {
                    removed++;
                }
            }
        }
        return removed;
    }

    private WorldChunk resolveVisualCleanupChunk(World world, long chunkIndex) {
        if (world == null) {
            return null;
        }
        WorldChunk chunk = world.getChunkIfLoaded(chunkIndex);
        if (chunk == null) {
            chunk = world.getChunkIfInMemory(chunkIndex);
        }
        if (chunk == null) {
            chunk = world.getChunkIfNonTicking(chunkIndex);
        }
        return chunk;
    }

    private int purgeOneTimeVisualGarbage(World world, BankDefenseRepository.Snapshot snapshot) {
        if (world == null || snapshot == null) {
            return 0;
        }
        Set<Long> cleanupChunks = this.collectVisualCleanupChunkIndexes(snapshot);
        if (cleanupChunks.isEmpty()) {
            return 0;
        }
        Store<EntityStore> store = world.getEntityStore().getStore();
        Set<Ref<EntityStore>> visited = new HashSet<>();
        int removed = 0;
        for (long chunkIndex : cleanupChunks) {
            WorldChunk chunk = world.getChunkIfNonTicking(chunkIndex);
            if (chunk == null || chunk.getEntityChunk() == null) {
                continue;
            }
            List<Object> entityRefs = new ArrayList<>(chunk.getEntityChunk().getEntityReferences());
            for (Object refObject : entityRefs) {
                if (!(refObject instanceof Ref<?> rawRef)) {
                    continue;
                }
                @SuppressWarnings("unchecked")
                Ref<EntityStore> ref = (Ref<EntityStore>) rawRef;
                if (ref == null || !ref.isValid() || !visited.add(ref)) {
                    continue;
                }
                if (!this.isBankDefenseVisualGarbageCandidate(store, ref)) {
                    continue;
                }
                if (this.removeVisualRef(store, ref)) {
                    removed++;
                }
            }
        }
        return removed;
    }

    private Set<Long> collectVisualCleanupChunkIndexes(BankDefenseRepository.Snapshot snapshot) {
        Set<Long> chunkIndexes = new LinkedHashSet<>();
        if (snapshot == null) {
            return chunkIndexes;
        }
        MapConfig map = snapshot.map;
        BuildSlotsConfig buildSlots = snapshot.buildSlots;
        if (map != null) {
            this.addChunkIndex(chunkIndexes, map.spawnPoint);
            this.addChunkIndex(chunkIndexes, map.spawnPointA);
            this.addChunkIndex(chunkIndexes, map.spawnPointB);
            this.addChunkIndex(chunkIndexes, map.spawnPointC);
            this.addChunkIndex(chunkIndexes, map.bankCenter);
            this.addChunkIndex(chunkIndexes, map.vaultPoint);
            this.addChunkIndex(chunkIndexes, map.playerStart);
            this.addChunkIndex(chunkIndexes, map.controlPoint);
            this.addChunkIndex(chunkIndexes, map.previousControlPoint);
            this.addChunkIndex(chunkIndexes, map.vendorPoint);
            this.addChunkIndex(chunkIndexes, map.previousVendorPoint);
            this.addChunkIndex(chunkIndexes, map.modePoint);
            this.addChunkIndex(chunkIndexes, map.previousModePoint);
            this.addChunkIndex(chunkIndexes, map.duoTeamPoint);
            this.addChunkIndex(chunkIndexes, map.previousDuoTeamPoint);
            this.addChunkIndex(chunkIndexes, map.duoTeleportPoint);
            this.addChunkIndex(chunkIndexes, map.previousDuoTeleportPoint);
            this.addChunkIndex(chunkIndexes, map.duoTeleportTarget);
            this.addChunkIndex(chunkIndexes, map.soloTeleportTarget);
            this.addChunkIndex(chunkIndexes, this.duoTransitionPoint(map));
            this.addChunkIndex(chunkIndexes, map.previousDuoPoint);
            this.addChunkIndex(chunkIndexes, map.statisticsPoint);
            this.addChunkIndex(chunkIndexes, map.previousStatisticsPoint);
            this.addChunkIndex(chunkIndexes, map.duoStatisticsPoint);
            this.addChunkIndex(chunkIndexes, map.previousDuoStatisticsPoint);
            this.addChunkIndex(chunkIndexes, map.tutorialStartPoint);
            this.addChunkIndex(chunkIndexes, map.tutorialSpawnPointA);
            this.addChunkIndex(chunkIndexes, map.tutorialSpawnPointB);
            this.addChunkIndex(chunkIndexes, map.tutorialSpawnPointC);
            this.addChunkIndex(chunkIndexes, map.tutorialBankCenter);
            this.addChunkIndex(chunkIndexes, map.tutorialVaultPoint);
            this.addChunkIndex(chunkIndexes, map.tutorialControlPoint);
            this.addChunkIndex(chunkIndexes, map.tutorialVendorPoint);
            this.addChunkIndex(chunkIndexes, map.tutorialWizardIntroPoint);
            this.addChunkIndex(chunkIndexes, map.tutorialWizardPoint);
            for (Vec3i point : map.chestSpawnPoints) {
                this.addChunkIndex(chunkIndexes, point);
            }
            this.addRouteChunkIndexes(chunkIndexes, map.spawnPointA != null ? map.spawnPointA : map.spawnPoint, map.routePointsA, map.vaultPoint);
            this.addRouteChunkIndexes(chunkIndexes, map.spawnPointB, map.routePointsB, map.vaultPoint);
            this.addRouteChunkIndexes(chunkIndexes, map.spawnPointC, map.routePointsC, map.vaultPoint);
            this.addRouteChunkIndexes(chunkIndexes, map.tutorialSpawnPointA, map.tutorialRoutePointsA, map.tutorialVaultPoint);
            this.addRouteChunkIndexes(chunkIndexes, map.tutorialSpawnPointB, map.tutorialRoutePointsB, map.tutorialVaultPoint);
            this.addRouteChunkIndexes(chunkIndexes, map.tutorialSpawnPointC, map.tutorialRoutePointsC, map.tutorialVaultPoint);
        }
        if (buildSlots != null && buildSlots.slots != null) {
            for (BuildSlot slot : buildSlots.slots) {
                if (slot == null) {
                    continue;
                }
                this.addChunkIndex(chunkIndexes, slot.position);
                this.addChunkIndex(chunkIndexes, this.slotInteractionPoint(slot));
            }
        }
        return chunkIndexes;
    }

    private void addRouteChunkIndexes(Set<Long> chunkIndexes, Vec3i spawnPoint, List<Vec3i> routePoints, Vec3i vaultPoint) {
        if (chunkIndexes == null) {
            return;
        }
        List<Vec3i> route = new ArrayList<>();
        if (spawnPoint != null) {
            route.add(spawnPoint);
        }
        if (routePoints != null) {
            for (Vec3i point : routePoints) {
                if (point != null) {
                    route.add(point);
                }
            }
        }
        if (vaultPoint != null) {
            route.add(vaultPoint);
        }
        if (route.isEmpty()) {
            return;
        }
        List<Vec3i> sampled = this.sampleRoute(route, 8.0);
        if (sampled.isEmpty()) {
            sampled = route;
        }
        for (Vec3i point : sampled) {
            this.addChunkIndex(chunkIndexes, point);
        }
    }

    private void addChunkIndex(Set<Long> chunkIndexes, Vec3i point) {
        if (chunkIndexes == null || point == null) {
            return;
        }
        chunkIndexes.add(Long.valueOf(ChunkUtil.indexChunkFromBlock(point.x, point.z)));
    }

    private Set<Long> collectSpawnKeepLoadedChunkIndexes(MapConfig map) {
        Set<Long> chunkIndexes = new HashSet<>();
        if (map == null) {
            return chunkIndexes;
        }
        this.addSpawnRouteKeepLoadedChunks(chunkIndexes, buildFullRoute(map, "a"));
        this.addSpawnRouteKeepLoadedChunks(chunkIndexes, buildFullRoute(map, "b"));
        this.addSpawnRouteKeepLoadedChunks(chunkIndexes, buildFullRoute(map, "c"));
        this.addSpawnRouteKeepLoadedChunks(chunkIndexes, this.buildDuoSpawnKeepLoadedRoute(map.duoSpawnPointBlue, map.duoRouteBlue));
        this.addSpawnRouteKeepLoadedChunks(chunkIndexes, this.buildDuoSpawnKeepLoadedRoute(map.duoSpawnPointGreen, map.duoRouteGreen));
        if (chunkIndexes.isEmpty()) {
            this.addBufferedChunkIndexes(chunkIndexes, map.spawnPointA != null ? map.spawnPointA : map.spawnPoint, SPAWN_CHUNK_KEEP_LOADED_RADIUS);
            this.addBufferedChunkIndexes(chunkIndexes, map.spawnPointB, SPAWN_CHUNK_KEEP_LOADED_RADIUS);
            this.addBufferedChunkIndexes(chunkIndexes, map.spawnPointC, SPAWN_CHUNK_KEEP_LOADED_RADIUS);
            this.addBufferedChunkIndexes(chunkIndexes, map.duoSpawnPointBlue, SPAWN_CHUNK_KEEP_LOADED_RADIUS);
            this.addBufferedChunkIndexes(chunkIndexes, map.duoSpawnPointGreen, SPAWN_CHUNK_KEEP_LOADED_RADIUS);
        }
        this.addHollowKeepLoadedChunkIndexes(chunkIndexes, map);
        return chunkIndexes;
    }

    private void addHollowKeepLoadedChunkIndexes(Set<Long> chunkIndexes, MapConfig map) {
        if (chunkIndexes == null || map == null) {
            return;
        }
        this.addBufferedChunkIndexes(chunkIndexes, map.bankCenter, HOLLOW_CHUNK_KEEP_LOADED_RADIUS);
        this.addBufferedChunkIndexes(chunkIndexes, map.vaultPoint, HOLLOW_CHUNK_KEEP_LOADED_RADIUS);
        this.addBufferedChunkIndexes(chunkIndexes, map.duoBankCenter, HOLLOW_CHUNK_KEEP_LOADED_RADIUS);
        this.addBufferedChunkIndexes(chunkIndexes, map.duoVaultPoint, HOLLOW_CHUNK_KEEP_LOADED_RADIUS);
    }

    private List<Vec3i> buildDuoSpawnKeepLoadedRoute(Vec3i spawnPoint, List<Vec3i> routePoints) {
        List<Vec3i> route = new ArrayList<>();
        if (spawnPoint != null) {
            route.add(spawnPoint);
        }
        if (routePoints != null) {
            for (Vec3i point : routePoints) {
                if (point == null) {
                    continue;
                }
                if (!route.isEmpty() && route.get(route.size() - 1).equals(point)) {
                    continue;
                }
                route.add(point);
            }
        }
        return route;
    }

    private void addSpawnRouteKeepLoadedChunks(Set<Long> chunkIndexes, List<Vec3i> route) {
        if (chunkIndexes == null || route == null || route.isEmpty()) {
            return;
        }
        List<Vec3i> sampled = this.sampleRoute(route, 8.0);
        if (sampled.isEmpty()) {
            sampled = route;
        }
        Vec3i previous = null;
        double travelled = 0.0;
        for (Vec3i point : sampled) {
            if (point == null) {
                continue;
            }
            if (previous != null) {
                travelled += distanceBetween(previous, point);
            }
            if (travelled > SPAWN_CHUNK_KEEP_LOADED_ROUTE_DISTANCE) {
                break;
            }
            this.addBufferedChunkIndexes(chunkIndexes, point, SPAWN_CHUNK_KEEP_LOADED_RADIUS);
            previous = point;
        }
        if (previous == null && !sampled.isEmpty()) {
            this.addBufferedChunkIndexes(chunkIndexes, sampled.get(0), SPAWN_CHUNK_KEEP_LOADED_RADIUS);
        }
    }

    private void addBufferedChunkIndexes(Set<Long> chunkIndexes, Vec3i point, int chunkRadius) {
        if (chunkIndexes == null || point == null) {
            return;
        }
        long baseIndex = ChunkUtil.indexChunkFromBlock(point.x, point.z);
        int baseChunkX = ChunkUtil.xOfChunkIndex(baseIndex);
        int baseChunkZ = ChunkUtil.zOfChunkIndex(baseIndex);
        for (int chunkOffsetX = -chunkRadius; chunkOffsetX <= chunkRadius; chunkOffsetX++) {
            for (int chunkOffsetZ = -chunkRadius; chunkOffsetZ <= chunkRadius; chunkOffsetZ++) {
                chunkIndexes.add(Long.valueOf(ChunkUtil.indexChunk(baseChunkX + chunkOffsetX, baseChunkZ + chunkOffsetZ)));
            }
        }
    }

    private void syncPinnedSpawnChunks(World world) {
        if (world == null) {
            return;
        }
        String key = this.worldKey(world);
        MatchContext context = this.matchesByWorld.get(key);
        Set<Long> desired = context == null || context.snapshot == null || context.snapshot.map == null
            ? Collections.emptySet()
            : this.collectSpawnKeepLoadedChunkIndexes(context.snapshot.map);
        Set<Long> pinned = this.pinnedSpawnChunkIndexesByWorld.computeIfAbsent(key, ignored -> new HashSet<>());
        List<Long> stale = new ArrayList<>();
        for (Long chunkIndex : pinned) {
            if (!desired.contains(chunkIndex)) {
                stale.add(chunkIndex);
            }
        }
        for (Long chunkIndex : stale) {
            this.releasePinnedSpawnChunk(world, chunkIndex.longValue());
            pinned.remove(chunkIndex);
        }
        for (Long chunkIndex : desired) {
            if (pinned.add(chunkIndex)) {
                this.pinSpawnChunk(world, key, chunkIndex.longValue());
            } else {
                this.keepPinnedSpawnChunkActive(world, chunkIndex.longValue());
            }
        }
        if (desired.isEmpty() && pinned.isEmpty()) {
            this.pinnedSpawnChunkIndexesByWorld.remove(key);
        }
    }

    private void tickPinnedSpawnChunkSync(World world, double deltaSeconds) {
        if (world == null) {
            return;
        }
        String key = this.worldKey(world);
        if (!this.matchesByWorld.containsKey(key) && !this.pinnedSpawnChunkIndexesByWorld.containsKey(key)) {
            this.pinnedSpawnChunkSyncAccumulatedSecondsByWorld.remove(key);
            return;
        }
        double accumulated = this.pinnedSpawnChunkSyncAccumulatedSecondsByWorld.getOrDefault(key, PINNED_SPAWN_CHUNK_SYNC_SECONDS);
        accumulated += Math.max(0.0, deltaSeconds);
        if (accumulated < PINNED_SPAWN_CHUNK_SYNC_SECONDS) {
            this.pinnedSpawnChunkSyncAccumulatedSecondsByWorld.put(key, accumulated);
            return;
        }
        this.pinnedSpawnChunkSyncAccumulatedSecondsByWorld.put(key, 0.0);
        this.syncPinnedSpawnChunks(world);
    }

    private void tickWorldInteractionSync(World world, MatchContext context, double deltaSeconds) {
        if (world == null || context == null || context.snapshot == null) {
            return;
        }
        context.worldInteractionSyncAccumulatedSeconds += Math.max(0.0, deltaSeconds);
        if (context.worldInteractionSyncAccumulatedSeconds < WORLD_INTERACTION_SYNC_SECONDS) {
            return;
        }
        context.worldInteractionSyncAccumulatedSeconds = 0.0;
        this.syncSlotInteractionBlocks(world, context.snapshot.buildSlots, context);
        this.syncChestInteractionBlocks(world, context.snapshot.map, context);
    }

    private void pinSpawnChunk(World world, String worldKey, long chunkIndex) {
        if (world == null) {
            return;
        }
        WorldChunk existing = world.getChunkIfInMemory(chunkIndex);
        if (existing != null) {
            existing.addKeepLoaded();
            this.keepSpawnChunkActive(existing);
            return;
        }
        world.getChunkAsync(chunkIndex).thenAccept(chunk -> {
            if (chunk == null) {
                return;
            }
            world.execute(() -> {
                Set<Long> pinned = this.pinnedSpawnChunkIndexesByWorld.get(worldKey);
                if (pinned == null || !pinned.contains(Long.valueOf(chunkIndex))) {
                    return;
                }
                chunk.addKeepLoaded();
                this.keepSpawnChunkActive(chunk);
            });
        });
    }

    private void keepPinnedSpawnChunkActive(World world, long chunkIndex) {
        if (world == null) {
            return;
        }
        WorldChunk chunk = world.getChunkIfInMemory(chunkIndex);
        if (chunk == null) {
            return;
        }
        this.keepSpawnChunkActive(chunk);
    }

    private void keepSpawnChunkActive(WorldChunk chunk) {
        if (chunk == null) {
            return;
        }
        chunk.resetKeepAlive();
        chunk.resetActiveTimer();
        if (!chunk.is(ChunkFlag.TICKING)) {
            chunk.setFlag(ChunkFlag.TICKING, true);
        }
    }

    private void releasePinnedSpawnChunk(World world, long chunkIndex) {
        if (world == null) {
            return;
        }
        WorldChunk chunk = world.getChunkIfInMemory(chunkIndex);
        if (chunk == null) {
            return;
        }
        chunk.removeKeepLoaded();
    }

    private boolean isBankDefenseVisualGarbageCandidate(Store<EntityStore> store, Ref<EntityStore> ref) {
        if (store == null || ref == null || !ref.isValid()) {
            return false;
        }
        if (store.getComponent(ref, Player.getComponentType()) != null) {
            return false;
        }
        if (store.getComponent(ref, TransformComponent.getComponentType()) == null) {
            return false;
        }
        boolean modelVisual = store.getComponent(ref, ModelComponent.getComponentType()) != null
            || store.getComponent(ref, PlayerSkinComponent.getComponentType()) != null;
        boolean trackedVisual = store.getComponent(ref, EntityStore.REGISTRY.getNonSerializedComponentType()) != null
            || store.getComponent(ref, PropComponent.getComponentType()) != null
            || store.getComponent(ref, Intangible.getComponentType()) != null
            || store.getComponent(ref, Frozen.getComponentType()) != null
            || store.getComponent(ref, Nameplate.getComponentType()) != null
            || store.getComponent(ref, UIComponentList.getComponentType()) != null
            || store.getComponent(ref, EntityStatMap.getComponentType()) != null;
        if (!trackedVisual) {
            return false;
        }
        if (store.getComponent(ref, NPCEntity.getComponentType()) != null) {
            return true;
        }
        return modelVisual;
    }

    private boolean isDetachedTowerVisual(Store<EntityStore> store, Ref<EntityStore> ref) {
        if (ref == null || !ref.isValid()) {
            return false;
        }
        if (store.getComponent(ref, Player.getComponentType()) != null) {
            return false;
        }
        Nameplate nameplate = store.getComponent(ref, Nameplate.getComponentType());
        String text = nameplate == null ? null : nameplate.getText();
        if (text == null || text.isBlank()) {
            return false;
        }
        return text.contains(" \u0423\u0440. ") || text.contains(" Lvl. ");
    }

    private boolean isDetachedEnemyVisual(Store<EntityStore> store, Ref<EntityStore> ref) {
        if (ref == null || !ref.isValid()) {
            return false;
        }
        if (store.getComponent(ref, Player.getComponentType()) != null) {
            return false;
        }
        if (store.getComponent(ref, UUIDComponent.getComponentType()) == null) {
            return false;
        }
        boolean npcVisual = store.getComponent(ref, NPCEntity.getComponentType()) != null;
        boolean modelEnemyVisual = (store.getComponent(ref, ModelComponent.getComponentType()) != null
            || store.getComponent(ref, PlayerSkinComponent.getComponentType()) != null)
            && store.getComponent(ref, PropComponent.getComponentType()) != null
            && store.getComponent(ref, Intangible.getComponentType()) != null
            && store.getComponent(ref, UIComponentList.getComponentType()) != null
            && store.getComponent(ref, EntityStatMap.getComponentType()) != null;
        if (!npcVisual && !modelEnemyVisual) {
            return false;
        }
        if (npcVisual
            && (store.getComponent(ref, Frozen.getComponentType()) == null
                || store.getComponent(ref, Intangible.getComponentType()) == null)) {
            return false;
        }
        Nameplate nameplate = store.getComponent(ref, Nameplate.getComponentType());
        String text = nameplate == null ? null : nameplate.getText();
        if (text == null || text.isBlank()) {
            return true;
        }
        return !text.contains(" Lvl. ") && !text.contains(" \u0423\u0440. ");
    }

    private boolean isDetachedProjectileVisual(Store<EntityStore> store, Ref<EntityStore> ref) {
        if (ref == null || !ref.isValid()) {
            return false;
        }
        if (store.getComponent(ref, Player.getComponentType()) != null) {
            return false;
        }
        if (store.getComponent(ref, PropComponent.getComponentType()) == null
            || store.getComponent(ref, Intangible.getComponentType()) == null) {
            return false;
        }
        if (store.getComponent(ref, ModelComponent.getComponentType()) == null) {
            return false;
        }
        if (store.getComponent(ref, EntityStatMap.getComponentType()) != null) {
            return false;
        }
        if (store.getComponent(ref, PlayerSkinComponent.getComponentType()) != null) {
            return false;
        }
        Nameplate nameplate = store.getComponent(ref, Nameplate.getComponentType());
        String text = nameplate == null ? null : nameplate.getText();
        return text == null || text.isBlank();
    }

    private void syncEnemyHealthBar(Store<EntityStore> store, Ref<EntityStore> ref, EnemyInstance enemy) {
        if (ref == null || !ref.isValid()) {
            return;
        }
        UIComponentList uiComponentList = store.ensureAndGetComponent(ref, UIComponentList.getComponentType());
        uiComponentList.update();

        EntityStatMap statMap = store.ensureAndGetComponent(ref, EntityStatMap.getComponentType());
        statMap.update();
        int healthIndex = DefaultEntityStatTypes.getHealth();
        if (healthIndex == Integer.MIN_VALUE) {
            return;
        }

        EntityStatValue health = statMap.get(healthIndex);
        if (health == null) {
            return;
        }

        float targetMax = (float)Math.max(1.0, enemy.maxHp);
        Modifier existingModifier = statMap.getModifier(healthIndex, VISUAL_MAX_HP_MODIFIER);
        float existingAmount = existingModifier instanceof StaticModifier staticModifier
            && staticModifier.getTarget() == Modifier.ModifierTarget.MAX
            && staticModifier.getCalculationType() == StaticModifier.CalculationType.ADDITIVE
                ? staticModifier.getAmount()
                : 0.0f;
        float baseMax = health.getMax() - existingAmount;
        float targetAmount = targetMax - baseMax;

        boolean needsModifier = !(existingModifier instanceof StaticModifier staticModifier
            && staticModifier.getTarget() == Modifier.ModifierTarget.MAX
            && staticModifier.getCalculationType() == StaticModifier.CalculationType.ADDITIVE
            && Math.abs(staticModifier.getAmount() - targetAmount) < 0.01f);
        if (needsModifier) {
            statMap.putModifier(
                healthIndex,
                VISUAL_MAX_HP_MODIFIER,
                new StaticModifier(Modifier.ModifierTarget.MAX, StaticModifier.CalculationType.ADDITIVE, targetAmount)
            );
        }
        float displayHp = enemy.dying
            ? Math.max(0.05f, targetMax * 0.03f)
            : Math.max(0.05f, Math.min(targetMax, (float)enemy.hp));
        statMap.setStatValue(healthIndex, displayHp);
    }

    private void updateTransform(Store<EntityStore> store, Ref<EntityStore> ref, Vector3d position, Vector3f rotation) {
        if (ref == null || !ref.isValid()) {
            return;
        }
        TransformComponent transform = store.getComponent(ref, TransformComponent.getComponentType());
        if (transform == null) {
            return;
        }
        transform.teleportPosition(position);
        transform.teleportRotation(rotation);
        HeadRotation headRotation = store.getComponent(ref, HeadRotation.getComponentType());
        if (headRotation != null) {
            headRotation.getRotation().assign(rotation);
        }
    }

    private int clearVisualEntities(World world, PresentationState presentation) {
        Store<EntityStore> store = world.getEntityStore().getStore();
        int removed = 0;
        for (Ref<EntityStore> ref : presentation.staticRefs.values()) {
            if (this.removeVisualRef(store, ref)) {
                removed++;
            }
        }
        for (Ref<EntityStore> ref : presentation.enemyRefs.values()) {
            if (this.removeVisualRef(store, ref)) {
                removed++;
            }
        }
        for (ProjectileVisual projectile : presentation.projectileVisuals) {
            if (this.removeVisualRef(store, projectile.ref)) {
                removed++;
            }
        }
        presentation.staticRefs.clear();
        presentation.enemyRefs.clear();
        presentation.enemyVisualStates.clear();
        presentation.projectileVisuals.clear();
        presentation.pendingVisualRespawnSeconds.clear();
        presentation.enemyVisualGarbageSweepRemaining = 0.0;
        presentation.lastEnemyViewerCount = 0;
        presentation.lastWantedEnemyVisuals = 0;
        presentation.lastDuplicateEnemyVisualsRemoved = 0;
        presentation.lastEnemyVisualEpoch = "";
        return removed;
    }

    private boolean removeVisualRef(Store<EntityStore> store, Ref<EntityStore> ref) {
        if (ref == null || !ref.isValid()) {
            return false;
        }
        store.removeEntity(ref, RemoveReason.REMOVE);
        return true;
    }

    private void syncHud(
        World world,
        PresentationState presentation,
        BankDefenseRepository.Snapshot snapshot,
        MatchContext context
    ) {
        Store<EntityStore> store = world.getEntityStore().getStore();
        Set<UUID> activePlayers = new HashSet<>();
        MatchState state = context == null ? null : context.state;

        for (PlayerRef playerRef : world.getPlayerRefs()) {
            Ref<EntityStore> playerEntity = playerRef.getReference();
            if (playerEntity == null || !playerEntity.isValid()) {
                continue;
            }
            Player player = store.getComponent(playerEntity, Player.getComponentType());
            if (player == null) {
                continue;
            }
            activePlayers.add(playerRef.getUuid());
            if (this.isCustomHudHidden(world, playerRef)) {
                if (player.getHudManager().getCustomHud() != null) {
                    player.getHudManager().setCustomHud(playerRef, null);
                }
                continue;
            }
            BankDefenseHud hud = presentation.huds.computeIfAbsent(playerRef.getUuid(), ignored -> new BankDefenseHud(playerRef));
            hud.setState(
                "Duplo TD",
                BankDefenseLocalization.tr(playerRef, "hud.creator"),
                this.hudWaveLabel(playerRef, snapshot, context),
                this.hudStatusLabel(world, playerRef, context),
                this.hudPromptLabel(playerRef, context),
                this.hudWaveVisible(snapshot, context),
                this.hudStatusVisible(snapshot, context),
                this.hudPromptVisible(snapshot, context),
                this.hudSealCursePanelVisible(context),
                this.hudSealCursePanelTitle(playerRef, context),
                this.hudSealCursePanelLine(playerRef, context, 0),
                this.hudSealCursePanelLine(playerRef, context, 1),
                this.hudSealCursePanelLine(playerRef, context, 2),
                this.hudRightStatsVisible(snapshot, context),
                this.hudSuperMiniVisible(context, playerRef),
                this.moneyLabel(playerRef, context),
                this.seedIdolWardIncomePaused(context),
                this.coresLabel(context),
                this.bankLabel(state, snapshot.gameRules.bankHp),
                this.bankStateLabel(playerRef, state, snapshot.gameRules.bankHp),
                this.bankDangerStage(state, snapshot.gameRules.bankHp),
                this.waveEnemiesLabel(context),
                this.enemyStateLabel(playerRef, context),
                this.enemyPressureStage(context),
                this.hudTutorialQuestVisible(snapshot, context),
                this.hudTutorialQuestText(world, snapshot, context),
                this.nextWavePanelVisible(context),
                this.nextWaveLabel(playerRef, context),
                this.nextWaveCountdownLabel(playerRef, context),
                this.nextWaveDifficultyLevel(context),
                false,
                "",
                this.superHudTitle(playerRef, context, 0),
                this.superHudTitle(playerRef, context, 1),
                this.superHudTitle(playerRef, context, 2),
                this.superHudIconKind(context, playerRef, 0),
                this.superHudDetailClean(playerRef, context, 0),
                this.superHudIconKind(context, playerRef, 1),
                this.superHudDetailClean(playerRef, context, 1),
                this.superHudIconKind(context, playerRef, 2),
                this.superHudDetailClean(playerRef, context, 2),
                this.eventToastVisible(context),
                BankDefenseLocalization.translateFreeform(playerRef, this.eventToastText(context)),
                this.eventToastStage(context),
                this.eventToastIconType(context),
                this.duoHudVisible(context, playerRef),
                this.duoHudTeamIconKind(context, playerRef),
                this.duoHudTeamLabel(playerRef, context),
                this.duoPartnerMoneyLabel(context, playerRef),
                this.defeatBannerVisible(context),
                this.defeatBannerText(playerRef, context),
                this.defeatBannerHint(playerRef, context),
                this.defeatBannerShardsValue(context)
            );
            if (player.getHudManager().getCustomHud() != hud) {
                player.getHudManager().setCustomHud(playerRef, hud);
                if (!player.getHudManager().getVisibleHudComponents().equals(BANK_DEFENSE_VISIBLE_HUD_COMPONENTS)) {
                    player.getHudManager().setVisibleHudComponents(playerRef, BANK_DEFENSE_VISIBLE_HUD_COMPONENTS);
                }
            } else {
                hud.pushIfDirty();
            }
        }

        List<UUID> stale = new ArrayList<>();
        for (UUID uuid : presentation.huds.keySet()) {
            if (!activePlayers.contains(uuid)) {
                stale.add(uuid);
            }
        }
        for (UUID uuid : stale) {
            presentation.huds.remove(uuid);
            this.hudInteractionPromptByPlayer.remove(uuid);
        }
        this.hiddenHudPlayers(world).removeIf(uuid -> uuid == null || !activePlayers.contains(uuid));
    }

    private String moneyLabel(PlayerRef playerRef, MatchContext context) {
        if (context == null) {
            return "0";
        }
        return String.valueOf(this.currencyForPlayer(context, playerRef));
    }

    private boolean duoHudVisible(MatchContext context, PlayerRef playerRef) {
        if (context == null || playerRef == null || context.tutorialActive || this.hudPostMatch(context) || !this.isDuoMode(context)) {
            return false;
        }
        String team = this.normalizeTeam(this.teamForPlayer(context, playerRef));
        return TEAM_BLUE.equals(team) || TEAM_GREEN.equals(team);
    }

    private int duoHudTeamIconKind(MatchContext context, PlayerRef playerRef) {
        String team = this.normalizeTeam(this.teamForPlayer(context, playerRef));
        return switch (team) {
            case TEAM_BLUE -> 1;
            case TEAM_GREEN -> 2;
            default -> 0;
        };
    }

    private String duoHudTeamLabel(PlayerRef playerRef, MatchContext context) {
        String team = this.normalizeTeam(this.teamForPlayer(context, playerRef));
        return switch (team) {
            case TEAM_BLUE -> BankDefenseLocalization.tr(playerRef, "hud.duo.team.blue");
            case TEAM_GREEN -> BankDefenseLocalization.tr(playerRef, "hud.duo.team.green");
            default -> BankDefenseLocalization.tr(playerRef, "hud.duo.team.none");
        };
    }

    private String duoPartnerMoneyLabel(MatchContext context, PlayerRef playerRef) {
        if (context == null || !this.isDuoMode(context)) {
            return "-";
        }
        String viewerTeam = this.normalizeTeam(this.teamForPlayer(context, playerRef));
        if (TEAM_BLUE.equals(viewerTeam)) {
            return this.hasAssignedDuoTeam(context, TEAM_GREEN) ? String.valueOf(Math.max(0, context.greenCurrency)) : "-";
        }
        if (TEAM_GREEN.equals(viewerTeam)) {
            return this.hasAssignedDuoTeam(context, TEAM_BLUE) ? String.valueOf(Math.max(0, context.blueCurrency)) : "-";
        }
        return "-";
    }

    private boolean hasAssignedDuoTeam(MatchContext context, String teamId) {
        if (context == null || teamId == null) {
            return false;
        }
        String normalizedTeam = this.normalizeTeam(teamId);
        for (String assigned : context.teamByPlayerUuid.values()) {
            if (normalizedTeam.equals(this.normalizeTeam(assigned))) {
                return true;
            }
        }
        return false;
    }

    private boolean hudPostMatch(MatchContext context) {
        return context != null
            && context.state != null
            && (context.state.gameState == GameState.Defeat || context.state.gameState == GameState.Victory);
    }

    private boolean hudWaveVisible(BankDefenseRepository.Snapshot snapshot, MatchContext context) {
        return !this.hudPostMatch(context);
    }

    private boolean hudStatusVisible(BankDefenseRepository.Snapshot snapshot, MatchContext context) {
        return !this.hudPostMatch(context) && !this.hudPreTutorialIntro(snapshot, context);
    }

    private boolean hudPromptVisible(BankDefenseRepository.Snapshot snapshot, MatchContext context) {
        return !this.hudPreTutorialIntro(snapshot, context);
    }

    private boolean hudRightStatsVisible(BankDefenseRepository.Snapshot snapshot, MatchContext context) {
        return !this.hudPostMatch(context) && !this.hudPreTutorialIntro(snapshot, context);
    }

    private boolean hudSealCursePanelVisible(MatchContext context) {
        return context != null && !this.activeSealCurseStates(context).isEmpty();
    }

    private String hudSealCursePanelTitle(PlayerRef playerRef, MatchContext context) {
        if (!this.hudSealCursePanelVisible(context)) {
            return "";
        }
        return this.choose(playerRef, "Проклятия Печати", "Seal Curses");
    }

    private String hudSealCursePanelLine(PlayerRef playerRef, MatchContext context, int index) {
        List<SealCurseState> active = this.activeSealCurseStates(context);
        if (index < 0 || index >= active.size()) {
            return "";
        }
        SealCurseState curse = active.get(index);
        return this.sealCurseDisplayName(playerRef, curse.type)
            + " • "
            + this.sealCurseShortLabel(playerRef, curse.type)
            + " • "
            + curse.remainingWaves
            + this.choose(playerRef, " волн", " waves left");
    }

    private boolean hudSuperMiniVisible(MatchContext context, PlayerRef playerRef) {
        return context != null
            && !context.tutorialActive
            && context.state != null
            && context.state.gameStarted
            && context.state.gameState != GameState.Defeat
            && context.state.gameState != GameState.Victory
            && !this.visibleSuperTowersForHud(context, playerRef).isEmpty();
    }

    private boolean eventToastVisible(MatchContext context) {
        return context != null
            && context.eventToastText != null
            && !context.eventToastText.isBlank()
            && context.eventToastRemainingSeconds > 0.0;
    }

    private String eventToastText(MatchContext context) {
        return this.eventToastVisible(context) ? context.eventToastText : "";
    }

    private int eventToastStage(MatchContext context) {
        if (!this.eventToastVisible(context)) {
            return 0;
        }
        double remaining = context.eventToastRemainingSeconds;
        if (remaining > 4.6 || remaining <= 0.4) {
            return 1;
        }
        if (remaining > 4.2 || remaining <= 0.8) {
            return 2;
        }
        return 3;
    }

    private int eventToastIconType(MatchContext context) {
        if (!this.eventToastVisible(context)) {
            return EVENT_TOAST_ICON_MONEY;
        }
        return switch (context.eventToastIconType) {
            case EVENT_TOAST_ICON_CORES -> EVENT_TOAST_ICON_CORES;
            case EVENT_TOAST_ICON_ALERT -> EVENT_TOAST_ICON_ALERT;
            default -> EVENT_TOAST_ICON_MONEY;
        };
    }

    private String hudInteractionPromptText(
        World world,
        BankDefenseRepository.Snapshot snapshot,
        MatchContext context,
        PresentationState presentation,
        Ref<EntityStore> playerEntity,
        Store<EntityStore> store
    ) {
        if (world == null || snapshot == null || playerEntity == null || !playerEntity.isValid() || store == null) {
            return "";
        }
        TransformComponent transform = store.getComponent(playerEntity, TransformComponent.getComponentType());
        if (transform == null || transform.getPosition() == null || transform.getRotation() == null) {
            return "";
        }
        Vector3f rotation = transform.getRotation();
        HeadRotation headRotation = store.getComponent(playerEntity, HeadRotation.getComponentType());
        if (headRotation != null && headRotation.getRotation() != null) {
            rotation = headRotation.getRotation();
        }
        Vector3d origin = new Vector3d(transform.getPosition());
        origin.y += 1.55;
        Vector3d direction = this.lookDirection(rotation);
        if ((direction.x * direction.x) + (direction.y * direction.y) + (direction.z * direction.z) <= 0.0001) {
            return "";
        }
        String directLabel = this.hudDirectInteractionPromptText(world, snapshot, context, origin, direction);
        if (!directLabel.isBlank()) {
            return directLabel;
        }
        Set<String> visitedBlocks = new HashSet<>();
        for (double distance = 0.65; distance <= HUD_INTERACTION_PROMPT_MAX_DISTANCE; distance += HUD_INTERACTION_PROMPT_STEP) {
            Vector3d sample = new Vector3d(
                origin.x + direction.x * distance,
                origin.y + direction.y * distance,
                origin.z + direction.z * distance
            );
            Vec3i block = this.worldToBlock(sample);
            for (Vec3i candidate : this.hudPromptCandidateBlocks(block)) {
                String blockKey = candidate.x + ":" + candidate.y + ":" + candidate.z;
                if (!visitedBlocks.add(blockKey)) {
                    continue;
                }
                String wizardKind = this.resolveTutorialWizardKind(snapshot, this.tutorialState(world), candidate);
                if (wizardKind != null) {
                    return this.tr(world, "hud.interact.wizard");
                }
                InteractionTarget target = this.resolveInteractionTarget(snapshot, context, presentation, null, candidate);
                String label = this.hudInteractionLabel(world, target, context);
                if (!label.isBlank()) {
                    return label;
                }
            }
        }
        return "";
    }

    private String hudDirectInteractionPromptText(
        World world,
        BankDefenseRepository.Snapshot snapshot,
        MatchContext context,
        Vector3d origin,
        Vector3d direction
    ) {
        if (world == null || snapshot == null || snapshot.map == null) {
            return "";
        }
        TutorialState tutorial = this.tutorialState(world);
        double bestDistance = Double.POSITIVE_INFINITY;
        String bestLabel = "";

        if (!tutorial.active) {
            bestDistance = this.hudPromptPreferNpc(bestDistance, origin, direction, snapshot.map.tutorialWizardIntroPoint);
            if (bestDistance < Double.POSITIVE_INFINITY) {
                bestLabel = this.tr(world, "hud.interact.wizard");
            }
        } else {
            double wizardDistance = this.hudPromptPreferNpc(Double.POSITIVE_INFINITY, origin, direction, snapshot.map.tutorialWizardPoint);
            if (wizardDistance < bestDistance) {
                bestDistance = wizardDistance;
                bestLabel = this.tr(world, "hud.interact.wizard");
            }
        }

        double modeDistance = this.hudPromptPreferNpc(bestDistance, origin, direction, this.modeSelectorInteractionPoint(snapshot.map));
        if (modeDistance < bestDistance) {
            bestDistance = modeDistance;
            bestLabel = this.tr(world, "hud.interact.mode");
        }

        double duoTeamDistance = this.hudPromptPreferNpc(bestDistance, origin, direction, this.duoTeamInteractionPoint(snapshot.map));
        if (duoTeamDistance < bestDistance) {
            bestDistance = duoTeamDistance;
            bestLabel = this.tr(world, "hud.interact.team");
        }

        double controlDistance = this.hudPromptPreferNpc(bestDistance, origin, direction, this.controlInteractionPoint(snapshot.map));
        if (controlDistance < bestDistance) {
            bestDistance = controlDistance;
            bestLabel = this.tr(world, "hud.interact.operator");
        }

        double vendorDistance = this.hudPromptPreferNpc(bestDistance, origin, direction, this.vendorInteractionPoint(snapshot.map));
        if (vendorDistance < bestDistance) {
            bestDistance = vendorDistance;
            bestLabel = this.tr(world, "hud.interact.keeper");
        }

        double statsDistance = this.hudPromptPreferNpc(bestDistance, origin, direction, this.statsPoint(snapshot.map));
        if (statsDistance < bestDistance) {
            bestDistance = statsDistance;
            bestLabel = this.tr(world, "hud.interact.stats");
        }

        if (context != null && context.activeChests != null) {
            for (ActiveChest chest : context.activeChests.values()) {
                if (chest == null || chest.position == null) {
                    continue;
                }
                double chestDistance = this.hudPromptPreferChest(bestDistance, origin, direction, chest.position);
                if (chestDistance < bestDistance) {
                    bestDistance = chestDistance;
                    bestLabel = this.tr(world, "hud.interact.chest");
                }
            }
        }

        if (snapshot.buildSlots != null && snapshot.buildSlots.slots != null) {
            for (BuildSlot slot : snapshot.buildSlots.slots) {
                if (slot == null || slot.position == null) {
                    continue;
                }
                double slotDistance = this.hudPromptPreferPad(bestDistance, origin, direction, slot.position);
                if (slotDistance < bestDistance) {
                    bestDistance = slotDistance;
                    bestLabel = this.hudSlotInteractionLabel(world, slot.id, context);
                }
            }
        }

        return bestLabel;
    }

    private double hudPromptPreferNpc(double currentBestDistance, Vector3d origin, Vector3d direction, Vec3i point) {
        double best = currentBestDistance;
        best = Math.min(best, this.hudPromptPrefer(best, origin, direction, point, 0.55, 1.55));
        best = Math.min(best, this.hudPromptPrefer(best, origin, direction, point, 1.05, 1.75));
        best = Math.min(best, this.hudPromptPrefer(best, origin, direction, point, 1.55, 1.45));
        return best;
    }

    private double hudPromptPreferPad(double currentBestDistance, Vector3d origin, Vector3d direction, Vec3i point) {
        double best = currentBestDistance;
        best = Math.min(best, this.hudPromptPrefer(best, origin, direction, point, 0.08, 1.75));
        best = Math.min(best, this.hudPromptPrefer(best, origin, direction, point, 0.30, 1.55));
        return best;
    }

    private double hudPromptPreferChest(double currentBestDistance, Vector3d origin, Vector3d direction, Vec3i point) {
        double best = currentBestDistance;
        best = Math.min(best, this.hudPromptPrefer(best, origin, direction, point, 0.45, 1.20));
        best = Math.min(best, this.hudPromptPrefer(best, origin, direction, point, 0.85, 1.10));
        return best;
    }

    private double hudPromptPrefer(
        double currentBestDistance,
        Vector3d origin,
        Vector3d direction,
        Vec3i point,
        double yOffset,
        double radius
    ) {
        if (origin == null || direction == null || point == null) {
            return Double.POSITIVE_INFINITY;
        }
        double targetX = point.x + 0.5;
        double targetY = point.y + yOffset;
        double targetZ = point.z + 0.5;
        double toTargetX = targetX - origin.x;
        double toTargetY = targetY - origin.y;
        double toTargetZ = targetZ - origin.z;
        double alongRay = toTargetX * direction.x + toTargetY * direction.y + toTargetZ * direction.z;
        if (alongRay < 0.45 || alongRay > HUD_INTERACTION_PROMPT_MAX_DISTANCE || alongRay >= currentBestDistance) {
            return Double.POSITIVE_INFINITY;
        }
        double closestX = origin.x + direction.x * alongRay;
        double closestY = origin.y + direction.y * alongRay;
        double closestZ = origin.z + direction.z * alongRay;
        double dx = targetX - closestX;
        double dy = targetY - closestY;
        double dz = targetZ - closestZ;
        double radiusSquared = radius * radius;
        double distanceSquared = dx * dx + dy * dy + dz * dz;
        if (distanceSquared > radiusSquared) {
            return Double.POSITIVE_INFINITY;
        }
        return alongRay;
    }

    private List<Vec3i> hudPromptCandidateBlocks(Vec3i block) {
        List<Vec3i> candidates = new ArrayList<>(7);
        if (block == null) {
            return candidates;
        }
        candidates.add(block);
        candidates.add(new Vec3i(block.x, block.y - 1, block.z));
        candidates.add(new Vec3i(block.x, block.y + 1, block.z));
        candidates.add(new Vec3i(block.x + 1, block.y, block.z));
        candidates.add(new Vec3i(block.x - 1, block.y, block.z));
        candidates.add(new Vec3i(block.x, block.y, block.z + 1));
        candidates.add(new Vec3i(block.x, block.y, block.z - 1));
        return candidates;
    }

    private Vector3d lookDirection(Vector3f rotation) {
        if (rotation == null) {
            return new Vector3d();
        }
        double yaw = rotation.getYaw();
        double pitch = rotation.getPitch();
        double horizontal = Math.cos(pitch);
        return new Vector3d(
            Math.sin(yaw) * horizontal,
            -Math.sin(pitch),
            Math.cos(yaw) * horizontal
        );
    }

    private Vec3i worldToBlock(Vector3d position) {
        return new Vec3i(
            (int)Math.floor(position.x),
            (int)Math.floor(position.y),
            (int)Math.floor(position.z)
        );
    }

    private String hudInteractionLabel(World world, InteractionTarget target, MatchContext context) {
        if (target == null || target.kind == null) {
            return "";
        }
        return switch (target.kind) {
            case BuildSlot -> this.hudSlotInteractionLabel(world, target.slotId, context);
            case ControlOperator -> this.tr(world, "hud.interact.operator");
            case VendorOperator -> this.tr(world, "hud.interact.keeper");
            case ModeSelectorOperator -> this.tr(world, "hud.interact.mode");
            case StartConsole -> this.tr(world, "hud.interact.terminal.start");
            case MenuConsole -> this.tr(world, "hud.interact.terminal.menu");
            case MoneyChest, CoreChest -> this.tr(world, "hud.interact.chest");
            case None -> "";
        };
    }

    private String hudSlotInteractionLabel(World world, String slotId, MatchContext context) {
        if (slotId == null || slotId.isBlank()) {
            return this.tr(world, "hud.interact.slot");
        }
        if (context != null && context.placedTowers.containsKey(slotId)) {
            TowerInstance tower = context.placedTowers.get(slotId);
            if (tower != null && tower.definition != null && tower.definition.superTower) {
                return this.tr(world, "hud.interact.super_slot");
            }
            return this.tr(world, "hud.interact.tower");
        }
        return this.tr(world, "hud.interact.slot");
    }

    private List<TowerInstance> visibleSuperTowersForHud(MatchContext context, PlayerRef playerRef) {
        if (context == null || playerRef == null) {
            return List.of();
        }
        List<TowerInstance> towers = new ArrayList<>();
        for (TowerInstance tower : context.placedTowers.values()) {
            if (tower == null || tower.definition == null || !tower.definition.superTower || tower.slot == null) {
                continue;
            }
            if (this.isDuoMode(context) && !this.canPlayerUseSlot(null, context, playerRef, tower.slot)) {
                continue;
            }
            towers.add(tower);
        }
        return towers;
    }

    private TowerInstance superHudTower(MatchContext context, PlayerRef playerRef, int index) {
        List<TowerInstance> towers = this.visibleSuperTowersForHud(context, playerRef);
        if (index < 0 || index >= towers.size()) {
            return null;
        }
        return towers.get(index);
    }

    private String superHudTitle(PlayerRef playerRef, MatchContext context, int index) {
        TowerInstance tower = this.superHudTower(context, playerRef, index);
        if (tower == null || tower.definition == null) {
            return "";
        }
        return BankDefenseLocalization.towerDisplayName(playerRef, tower.definition.id, this.text(tower.definition.displayName));
    }

    private String superHudDetail(PlayerRef playerRef, MatchContext context, int index) {
        return this.superHudDetailClean(playerRef, context, index);
    }

    private String superHudDetailClean(PlayerRef playerRef, MatchContext context, int index) {
        TowerInstance tower = this.superHudTower(context, playerRef, index);
        if (tower == null || tower.definition == null) {
            return "";
        }
        String activations = this.remainingSuperActivations(context, tower) + "/" + this.maxSuperActivations(context, tower);
        if ("heart_of_roots".equals(tower.definition.id)) {
            return BankDefenseLocalization.choose(playerRef, "Заряды: ", "Charges: ")
                + activations
                + BankDefenseLocalization.choose(playerRef, tower.superReady ? " | Готово" : " | Перезарядка", tower.superReady ? " | Ready" : " | Recharging");
        }
        if ("storm_monolith".equals(tower.definition.id)) {
            return BankDefenseLocalization.choose(playerRef, "Заряды: ", "Charges: ")
                + activations
                + BankDefenseLocalization.choose(playerRef, tower.superReady ? " | Готово" : " | Перезарядка", tower.superReady ? " | Ready" : " | Recharging");
        }
        if ("seed_idol".equals(tower.definition.id)) {
            if (tower.specialMode == SEED_IDOL_MODE_WARD) {
                return BankDefenseLocalization.choose(playerRef, "Накоплено: ", "Stored: ")
                    + Math.max(0, tower.idolStoredCurrency)
                    + BankDefenseLocalization.choose(playerRef, " золота | Выплата через: ", " gold | Payout in: ")
                    + Math.max(0, tower.idolSavingsWavesRemaining)
                    + BankDefenseLocalization.choose(playerRef, " волн", " waves");
            }
            if (tower.specialMode == SEED_IDOL_MODE_HARVEST) {
                return BankDefenseLocalization.choose(playerRef, "+10% золота", "+10% gold");
            }
            return BankDefenseLocalization.choose(playerRef, "Выберите режим", "Choose a mode");
        }
        return "";
    }

    private int superHudIconKind(MatchContext context, PlayerRef playerRef, int index) {
        TowerInstance tower = this.superHudTower(context, playerRef, index);
        if (tower == null || tower.definition == null) {
            return 0;
        }
        return switch (tower.definition.id) {
            case "heart_of_roots" -> 1;
            case "storm_monolith" -> 2;
            case "seed_idol" -> 3;
            default -> 0;
        };
    }

    private String coresLabel(MatchContext context) {
        int cores = context == null || context.progression == null ? 0 : context.progression.cores;
        return String.valueOf(cores);
    }

    private String bankLabel(MatchState state, int maxBankHp) {
        int resolvedMaxHp = state != null && state.maxBankHp > 0 ? state.maxBankHp : maxBankHp;
        int current = state == null ? resolvedMaxHp : Math.max(0, state.bankHp);
        return current + " / " + resolvedMaxHp;
    }

    private String bankStateLabel(PlayerRef playerRef, MatchState state, int maxBankHp) {
        int resolvedMaxHp = state != null && state.maxBankHp > 0 ? state.maxBankHp : maxBankHp;
        if (resolvedMaxHp <= 0) {
            return BankDefenseLocalization.tr(playerRef, "hud.bank.stable");
        }
        int current = state == null ? resolvedMaxHp : Math.max(0, state.bankHp);
        if (state != null && state.gameState == GameState.Defeat) {
            return BankDefenseLocalization.tr(playerRef, "hud.bank.captured");
        }
        double ratio = current / (double)resolvedMaxHp;
        if (ratio <= 0.30) {
            return BankDefenseLocalization.tr(playerRef, "hud.bank.critical");
        }
        if (ratio <= 0.60) {
            return BankDefenseLocalization.tr(playerRef, "hud.bank.warning");
        }
        return BankDefenseLocalization.tr(playerRef, "hud.bank.stable");
    }

    private int bankDangerStage(MatchState state, int maxBankHp) {
        int resolvedMaxHp = state != null && state.maxBankHp > 0 ? state.maxBankHp : maxBankHp;
        if (resolvedMaxHp <= 0) {
            return 0;
        }
        int current = state == null ? resolvedMaxHp : Math.max(0, state.bankHp);
        double ratio = current / (double)resolvedMaxHp;
        if (ratio <= 0.30) {
            return 2;
        }
        if (ratio <= 0.60) {
            return 1;
        }
        return 0;
    }

    private boolean defeatBannerVisible(MatchContext context) {
        return context != null
            && context.state != null
            && context.state.gameState == GameState.Defeat
            && context.defeatBannerRemainingSeconds > 0.0;
    }

    private String defeatBannerText(PlayerRef playerRef, MatchContext context) {
        return this.defeatBannerVisible(context) ? BankDefenseLocalization.tr(playerRef, "hud.defeat.banner") : "";
    }

    private String defeatBannerHint(PlayerRef playerRef, MatchContext context) {
        return this.defeatBannerVisible(context) ? BankDefenseLocalization.tr(playerRef, "hud.defeat.hint") : "";
    }

    private String defeatBannerShardsValue(MatchContext context) {
        int shards = context == null || context.progression == null ? 0 : Math.max(0, context.progression.cores);
        return String.valueOf(shards);
    }

    private boolean nextWavePanelVisible(MatchContext context) {
        if (context != null && context.tutorialActive) {
            return false;
        }
        MatchState state = context == null ? null : context.state;
        return state != null
            && state.gameStarted
            && state.gameState == GameState.Ready
            && BankDefenseMatchPhaseSupport.isBuildPhase(state)
            && state.currentWave > 0
            && !state.rewardPending
            && !this.hasPendingIdolModeChoice(context)
            && state.preparationRemainingSeconds > 0.0;
    }

    private String nextWaveLabel(PlayerRef playerRef, MatchContext context) {
        MatchState state = context == null ? null : context.state;
        if (!this.nextWavePanelVisible(context)) {
            return BankDefenseLocalization.tr(playerRef, "hud.wave.none");
        }
        return BankDefenseLocalization.tr(playerRef, "hud.wave", state.currentWave);
    }

    private String nextWaveCountdownLabel(PlayerRef playerRef, MatchContext context) {
        MatchState state = context == null ? null : context.state;
        if (!this.nextWavePanelVisible(context)) {
            return BankDefenseLocalization.tr(playerRef, "hud.next_wave.starts_in", "-");
        }
        return BankDefenseLocalization.tr(playerRef, "hud.next_wave.starts_in", Math.max(0, (int)Math.ceil(state.preparationRemainingSeconds)));
    }

    private int nextWaveDifficultyLevel(MatchContext context) {
        MatchState state = context == null ? null : context.state;
        if (!this.nextWavePanelVisible(context)) {
            return 1;
        }

        WaveDefinition wave = this.resolveWaveDefinition(context, state.currentWave);
        if (wave == null || wave.spawns == null || wave.spawns.isEmpty()) {
            return 1;
        }
        if (wave.bossWave) {
            return 3;
        }

        int laneCount = this.activeLaneCount(context, wave);
        int totalEnemies = 0;
        int totalLeak = 0;
        int specialUnits = 0;
        int heavyUnits = 0;
        double totalHp = 0.0;
        double hpMultiplier = context.difficulty == null ? 1.0 : Math.max(0.1, context.difficulty.enemyHpMultiplier);
        double speedMultiplier = context.difficulty == null ? 1.0 : Math.max(0.1, context.difficulty.enemySpeedMultiplier);

        for (WaveSpawn spawn : this.effectiveWaveSpawns(context, wave)) {
            EnemyDefinition enemy = context.enemyById.get(spawn.enemyId);
            if (enemy == null) {
                continue;
            }
            int count = enemy.bossEnemy ? 1 : this.adjustedSpawnCount(context, spawn, enemy, wave.index, laneCount);
            totalEnemies += count;
            totalLeak += Math.max(1, enemy.leakDamage) * count;
            totalHp += enemy.maxHp * this.waveEnemyHpMultiplier(context, wave.index, enemy) * hpMultiplier * count;
            if (enemy.specialEnemy) {
                specialUnits += count;
            }
            if (enemy.leakDamage >= 2 || enemy.maxHp >= 250.0 || enemy.specialEnemy || enemy.flatDamageReduction > 0.0 || enemy.damageReductionPercent > 0.0) {
                heavyUnits += count;
            }
        }

        double score = 0.0;
        score += Math.max(0, laneCount - 1) * 1.35;
        score += totalEnemies / 18.0;
        score += totalLeak / 10.0;
        score += totalHp / 2600.0;
        score += specialUnits / 5.0;
        score += heavyUnits / 6.0;
        score += Math.max(0.0, speedMultiplier - 1.0) * 2.5;

        if (score >= 7.6) {
            return 3;
        }
        if (score >= 3.6) {
            return 2;
        }
        return 1;
    }

    private String waveEnemiesLabel(MatchContext context) {
        if (context == null || context.state == null) {
            return "0 / 0";
        }

        MatchState state = context.state;
        if (state.gameState == GameState.Defeat || state.gameState == GameState.Victory || state.gameState == GameState.Loading) {
            return "0 / 0";
        }

        if (context.activeWaveNumber > 0) {
            int remaining = context.enemies.size() + context.pendingSpawns.size();
            int total = Math.max(context.activeWaveTotalEnemies, remaining);
            return remaining + " / " + total;
        }

        int previewTotal = this.waveEnemyTotal(context, state.currentWave);
        return previewTotal + " / " + previewTotal;
    }

    private String enemyStateLabel(PlayerRef playerRef, MatchContext context) {
        if (context == null || context.state == null) {
            return BankDefenseLocalization.tr(playerRef, "hud.enemies.waiting");
        }
        MatchState state = context.state;
        if (state.gameState == GameState.Defeat) {
            return BankDefenseLocalization.tr(playerRef, "hud.bank.captured");
        }
        if (state.gameState == GameState.Victory) {
            return BankDefenseLocalization.tr(playerRef, "hud.victory");
        }
        if (context.activeWaveNumber > 0) {
            int remaining = context.enemies.size() + context.pendingSpawns.size();
            int total = Math.max(context.activeWaveTotalEnemies, remaining);
            if (remaining <= 0) {
                return BankDefenseLocalization.tr(playerRef, "hud.enemies.clearing");
            }
            double ratio = total <= 0 ? 0.0 : remaining / (double)total;
            if (ratio > 0.66) {
                return BankDefenseLocalization.tr(playerRef, "hud.enemies.pressure.high");
            }
            if (ratio > 0.33) {
                return BankDefenseLocalization.tr(playerRef, "hud.enemies.pressure.mid");
            }
            return BankDefenseLocalization.tr(playerRef, "hud.enemies.pressure.low");
        }
        if (this.nextWavePanelVisible(context)) {
            return BankDefenseLocalization.tr(playerRef, "hud.enemies.building");
        }
        return BankDefenseLocalization.tr(playerRef, "hud.enemies.waiting");
    }

    private int enemyPressureStage(MatchContext context) {
        if (context == null || context.state == null) {
            return 0;
        }
        MatchState state = context.state;
        if (context.activeWaveNumber > 0) {
            int remaining = context.enemies.size() + context.pendingSpawns.size();
            int total = Math.max(context.activeWaveTotalEnemies, remaining);
            if (remaining <= 0) {
                return 1;
            }
            double ratio = total <= 0 ? 0.0 : remaining / (double)total;
            if (ratio > 0.66) {
                return 3;
            }
            if (ratio > 0.33) {
                return 2;
            }
            return 1;
        }
        if (state.gameState == GameState.Defeat || state.gameState == GameState.Victory || state.gameState == GameState.Loading) {
            return 0;
        }
        if (this.nextWavePanelVisible(context)) {
            return 1;
        }
        return 0;
    }

    private int waveEnemyTotal(MatchContext context, int waveNumber) {
        if (context == null || waveNumber <= 0) {
            return 0;
        }

        WaveDefinition wave = this.resolveWaveDefinition(context, waveNumber);
        if (wave == null || wave.spawns == null || wave.spawns.isEmpty()) {
            return 0;
        }

        int total = 0;
        int activeLanes = this.activeLaneCount(context, wave);
        for (WaveSpawn spawn : this.effectiveWaveSpawns(context, wave)) {
            EnemyDefinition enemy = context.enemyById.get(spawn.enemyId);
            if (enemy == null) {
                continue;
            }
            total += enemy.bossEnemy ? 1 : this.adjustedSpawnCount(context, spawn, enemy, wave.index, activeLanes);
        }
        return total;
    }

    private String hudWaveLabel(PlayerRef playerRef, BankDefenseRepository.Snapshot snapshot, MatchContext context) {
        if ((context != null && context.tutorialActive) || this.hudPreTutorialIntro(snapshot, context)) {
            return BankDefenseLocalization.tr(playerRef, "hud.training");
        }
        MatchState state = context == null ? null : context.state;
        if (state == null || state.currentWave <= 0 || state.gameState == GameState.Defeat || state.gameState == GameState.Victory) {
            return BankDefenseLocalization.tr(playerRef, "hud.wave.none");
        }
        return BankDefenseLocalization.tr(playerRef, "hud.wave", state.currentWave);
    }

    private String hudStatusLabel(World world, PlayerRef playerRef, MatchContext context) {
        MatchState state = context == null ? null : context.state;
        if (state == null) {
            return BankDefenseLocalization.tr(playerRef, "hud.loading");
        }
        if (state.gameState == GameState.Defeat || state.gameState == GameState.Victory) {
            return "";
        }
        if (state.paused) {
            return BankDefenseLocalization.tr(playerRef, "hud.paused_match");
        }
        if (BankDefenseMatchPhaseSupport.isWaitingForSetup(state)) {
            return BankDefenseLocalization.tr(playerRef, "hud.start_waiting");
        }
        if (state.gameState == GameState.Ready && state.preparationRemainingSeconds > 0.0) {
            return BankDefenseLocalization.tr(playerRef, "hud.preparation_s", (int)Math.ceil(state.preparationRemainingSeconds));
        }
        String base = switch (state.gameState) {
            case Loading -> BankDefenseLocalization.tr(playerRef, "hud.loading");
            case Victory -> BankDefenseLocalization.tr(playerRef, "hud.victory");
            case Defeat -> BankDefenseLocalization.tr(playerRef, "hud.defeat");
            case InMatch -> state.waveState == WaveState.Cleanup
                ? BankDefenseLocalization.tr(playerRef, "hud.cleanup")
                : BankDefenseLocalization.tr(playerRef, "hud.in_wave");
            case Ready -> BankDefenseLocalization.tr(playerRef, "hud.ready");
        };
        return base;
    }

    private String hudPromptLabel(PlayerRef playerRef, MatchContext context) {
        MatchState state = context == null ? null : context.state;
        String base;
        if (state == null) {
            base = BankDefenseLocalization.tr(playerRef, "hud.prompt.init");
            return base;
        }
        if (state.gameState == GameState.Defeat || state.gameState == GameState.Victory) {
            base = BankDefenseLocalization.tr(playerRef, "hud.prompt.new_match");
            return base;
        }
        if (BankDefenseMatchPhaseSupport.isWaitingForSetup(state)) {
            base = BankDefenseLocalization.tr(playerRef, "hud.prompt.choose_match");
            return base;
        }
        if (state.rewardPending) {
            base = this.rewardPendingPrompt(playerRef, context);
            return base;
        }
        if (this.hasPendingIdolModeChoice(context, playerRef)) {
            return this.choose(playerRef, "Выберите режим Идола урожая.", "Choose the Harvest Idol mode.");
        }
        if (this.hasPendingIdolModeChoice(context)) {
            return this.choose(playerRef, "Союзник выбирает режим Идола урожая.", "Your ally is choosing the Harvest Idol mode.");
        }
        if (context != null
            && !context.tutorialActive
            && state.gameStarted
            && state.gameState == GameState.Ready
            && !this.hasAnyPlacedTowerOrTrap(context)) {
            base = BankDefenseLocalization.tr(playerRef, "hud.prompt.build");
            return base;
        }
        if (state.gameState == GameState.Ready && state.preparationRemainingSeconds > 0.0) {
            base = BankDefenseLocalization.tr(playerRef, "hud.prompt.prepare_and_start");
            return base;
        }
        if (state.gameState == GameState.InMatch && state.waveState == WaveState.Cleanup) {
            base = BankDefenseLocalization.tr(playerRef, "hud.prompt.cleanup");
            return base;
        }
        if (state.gameState == GameState.Victory) {
            base = BankDefenseLocalization.tr(playerRef, "hud.prompt.win");
            return base;
        }
        if (state.gameState == GameState.Defeat) {
            base = BankDefenseLocalization.tr(playerRef, "hud.prompt.loss");
            return base;
        }
        base = BankDefenseLocalization.tr(playerRef, "hud.prompt.default");
        return base;
    }

    private String rewardPendingPrompt(PlayerRef playerRef, MatchContext context) {
        if (context == null || context.state == null || !context.state.rewardPending) {
            return BankDefenseLocalization.tr(playerRef, "hud.prompt.choose_reward");
        }
        if (!this.isDuoMode(context) || this.isDuoSoloTestEnabled(context)) {
            return this.choose(playerRef, "Выберите модуль у оператора.", "Choose the module at the operator.");
        }
        String pickerTeam = this.normalizeTeam(context.rewardPickerTeam);
        String viewerTeam = this.normalizeTeam(this.teamForPlayer(context, playerRef));
        if (pickerTeam.equals(viewerTeam)) {
            return this.choose(playerRef, "Выберите модуль у оператора.", "Choose the module at the operator.");
        }
        String teamLabel = TEAM_GREEN.equals(pickerTeam)
            ? this.choose(playerRef, "зелёной", "green")
            : this.choose(playerRef, "синей", "blue");
        return this.choose(playerRef, "Игрок " + teamLabel + " команды ещё не выбрал модуль. Его можно выбрать у оператора.", "The " + teamLabel + " team player has not chosen a module yet. It can be chosen at the operator.");
    }

    private boolean hasAnyPlacedTowerOrTrap(MatchContext context) {
        return context != null && !context.placedTowers.isEmpty();
    }

    private boolean hasPendingIdolModeChoice(MatchContext context) {
        return context != null && !context.pendingIdolModeSlotByPlayer.isEmpty();
    }

    private boolean hasPendingIdolModeChoice(MatchContext context, PlayerRef playerRef) {
        return context != null
            && playerRef != null
            && playerRef.getUuid() != null
            && context.pendingIdolModeSlotByPlayer.containsKey(playerRef.getUuid());
    }

    private boolean progressionLocked(MatchContext context) {
        if (context != null && context.tutorialActive && context.state != null && context.state.gameState != GameState.InMatch) {
            return false;
        }
        return context != null
            && context.state != null
            && context.state.gameStarted
            && context.state.gameState != GameState.Victory
            && context.state.gameState != GameState.Defeat;
    }

    private PlayerSkin defaultControlNpcSkin() {
        PlayerSkin skin = new PlayerSkin();
        skin.bodyCharacteristic = "Muscular.05";
        skin.underwear = "Suit.Purple";
        skin.face = "Face_Sunken";
        skin.ears = "Default";
        skin.mouth = "Mouth_Default";
        skin.haircut = "ThickBraid.GreyPurple";
        skin.facialHair = "PirateGoatee.GreyPurple";
        skin.eyebrows = "Medium.GreyPurple";
        skin.eyes = "Large_Eyes.Purple";
        skin.pants = "StripedPants.Brown";
        skin.overpants = null;
        skin.undertop = "PastelFade.Black";
        skin.overtop = null;
        skin.shoes = "Gem_Shoes.Black";
        skin.headAccessory = "WitchHat.Lime";
        skin.faceAccessory = "BandageBlindfold.Black.Thin";
        skin.earAccessory = "SimpleEarring.Brass_Purple.Both";
        skin.skinFeature = null;
        skin.gloves = null;
        skin.cape = null;
        return skin;
    }

    private PlayerSkin defaultVendorNpcSkin() {
        PlayerSkin skin = new PlayerSkin();
        skin.bodyCharacteristic = "Muscular.10";
        skin.underwear = "Bra.Black";
        skin.face = "Face_MakeUp_Freckles";
        skin.ears = "Default";
        skin.mouth = "Mouth_Makeup";
        skin.haircut = "MagicalPigtails.BlueLight";
        skin.facialHair = null;
        skin.eyebrows = "Thin.BlueLight";
        skin.eyes = "Plain_Square_Eyes.GreenLight";
        skin.pants = "Skirt_Savanna.Green";
        skin.overpants = "LongSocks_BasicWrap.Green";
        skin.undertop = "Forest_Guardian_LongShirt.Lime";
        skin.overtop = "Forest_Guardian_Poncho.Mossy";
        skin.shoes = "DaisyShoes.Red";
        skin.headAccessory = "Goggles.Green";
        skin.faceAccessory = "MouthWheat.Green";
        skin.earAccessory = "SilverHoopsBead.Purple+Violet+Lime";
        skin.skinFeature = null;
        skin.gloves = "Bracer_Daisy.Red";
        skin.cape = "Cape_Forest_Guardian.Green.Neck_Piece";
        return skin;
    }

    private PlayerSkin defaultTutorialWizardNpcSkin() {
        PlayerSkin skin = new PlayerSkin();
        skin.bodyCharacteristic = "Muscular.05";
        skin.underwear = "Boxer.Black";
        skin.face = "Face_Sunken";
        skin.ears = "Default";
        skin.mouth = "Mouth_Default";
        skin.haircut = "RaiderMohawk.BlueDark";
        skin.facialHair = "Groomed_Large.BlueDark";
        skin.eyebrows = "Heavy.BlueDark";
        skin.eyes = "Medium_Eyes.Purple";
        skin.pants = "Skirt.Purple";
        skin.overpants = "KneePads.Silver_Blue";
        skin.undertop = null;
        skin.overtop = "Cheststrap.Purple";
        skin.shoes = "HeavyLeather.Black";
        skin.headAccessory = null;
        skin.faceAccessory = "SunGlasses.Purple";
        skin.earAccessory = "SilverHoopsBead.Purple+Violet+Lime";
        skin.skinFeature = null;
        skin.gloves = "Spiked_Bracelets.Black.Default";
        skin.cape = "Cape.Short.Purple";
        return skin;
    }

    private PlayerSkin defaultDuoNpcSkin() {
        PlayerSkin skin = new PlayerSkin();
        skin.bodyCharacteristic = "Muscular.05";
        skin.underwear = "Boxer.Purple";
        skin.face = "Face_Sunken";
        skin.ears = "Default";
        skin.mouth = "Mouth_Default";
        skin.haircut = "RaiderMohawk.BlueDark";
        skin.facialHair = "Groomed_Large.BlueDark";
        skin.eyebrows = "Heavy.BlueDark";
        skin.eyes = "Medium_Eyes.Purple";
        skin.pants = "Pants_Slim.Black";
        skin.overpants = null;
        skin.undertop = "Tshirt_Logo.Purple.Play_Dark";
        skin.overtop = "ShortTartan.Purple";
        skin.shoes = "HeavyLeather.Black";
        skin.headAccessory = null;
        skin.faceAccessory = "SunGlasses.Purple";
        skin.earAccessory = "SilverHoopsBead.Purple+Violet+Lime";
        skin.skinFeature = null;
        skin.gloves = "Spiked_Bracelets.Black.Default";
        skin.cape = null;
        return skin;
    }

    private PlayerSkin defaultDuoTeamNpcSkin() {
        PlayerSkin skin = new PlayerSkin();
        skin.bodyCharacteristic = "Muscular.10";
        skin.underwear = "Suit.Purple";
        skin.face = "Face_MakeUp_Freckles";
        skin.ears = "Default";
        skin.mouth = "Mouth_Makeup";
        skin.haircut = "SuperSlickback.Copper";
        skin.facialHair = null;
        skin.eyebrows = "Thick.Copper";
        skin.eyes = "Plain_Square_Eyes.GreenLight";
        skin.pants = "Pants_Slim.Black";
        skin.overpants = "LongSocks_BasicWrap.Green";
        skin.undertop = "Forest_Guardian_LongShirt.Lime";
        skin.overtop = "Forest_Guardian_Poncho.Mossy";
        skin.shoes = "LeatherBoots.BrownDark";
        skin.headAccessory = "Goggles.Green";
        skin.faceAccessory = null;
        skin.earAccessory = "SilverHoopsBead.Purple+Violet+Lime";
        skin.skinFeature = null;
        skin.gloves = "Bracer_Daisy.Red";
        skin.cape = "Cape_Forest_Guardian.Green.Neck_Piece";
        return skin;
    }

    private PlayerSkin defaultStatsNpcSkin() {
        PlayerSkin skin = new PlayerSkin();
        skin.bodyCharacteristic = "Muscular.05";
        skin.underwear = "Suit.Purple";
        skin.face = "Face_Sunken";
        skin.ears = "Default";
        skin.mouth = "Mouth_Default";
        skin.haircut = "SuperSlickback.Copper";
        skin.facialHair = "PirateGoatee.Copper";
        skin.eyebrows = "Thick.Copper";
        skin.eyes = "Medium_Eyes.BrownDark";
        skin.pants = "CostumePants.Black";
        skin.overpants = null;
        skin.undertop = "LongSleeveShirt_ButtonUp.Red";
        skin.overtop = "JacketLong.Red";
        skin.shoes = "LeatherBoots.BrownDark";
        skin.headAccessory = null;
        skin.faceAccessory = "BandageBlindfold.Black.Thin";
        skin.earAccessory = "SimpleEarring.Gold_Red.Right";
        skin.skinFeature = null;
        skin.gloves = null;
        skin.cape = null;
        return skin;
    }

    private void cleanupLegacyDebugBlocks(World world, BankDefenseRepository.Snapshot snapshot) {
        if (world == null || snapshot == null || snapshot.map == null) {
            return;
        }
        List<Vec3i> candidates = new ArrayList<>();
        if (snapshot.map.spawnPoint != null) {
            candidates.add(snapshot.map.spawnPoint);
        }
        if (snapshot.map.bankCenter != null) {
            candidates.add(snapshot.map.bankCenter);
        }
        if (snapshot.map.vaultPoint != null) {
            candidates.add(snapshot.map.vaultPoint);
        }
        if (snapshot.map.playerStart != null) {
            candidates.add(snapshot.map.playerStart);
        }
        if (snapshot.map.controlPoint != null) {
            candidates.add(snapshot.map.controlPoint);
        }
        if (snapshot.map.previousControlPoint != null) {
            candidates.add(snapshot.map.previousControlPoint);
        }
        if (snapshot.map.modePoint != null) {
            candidates.add(snapshot.map.modePoint);
        }
        if (snapshot.map.previousModePoint != null) {
            candidates.add(snapshot.map.previousModePoint);
        }
        List<Vec3i> route = BankDefenseRuntime.buildFullRoute(snapshot.map);
        candidates.addAll(route);
        candidates.addAll(this.sampleRoute(route, ROUTE_MARKER_STEP));
        for (BuildSlot slot : snapshot.buildSlots.slots) {
            if (slot.position != null) {
                candidates.add(slot.position);
            }
            Vec3i interactionPoint = this.slotInteractionPoint(slot);
            if (interactionPoint != null) {
                candidates.add(interactionPoint);
            }
            Vec3i legacyInteractionPoint = this.legacySlotInteractionPoint(slot);
            if (legacyInteractionPoint != null) {
                candidates.add(legacyInteractionPoint);
            }
        }
        Set<String> seen = new HashSet<>();
        for (Vec3i candidate : candidates) {
            if (candidate == null || !seen.add(this.key(candidate))) {
                continue;
            }
            this.cleanupLegacyColumn(world, candidate);
        }
    }

    private void cleanupLegacyColumn(World world, Vec3i base) {
        for (int dx = -LEGACY_SEARCH_RADIUS; dx <= LEGACY_SEARCH_RADIUS; dx++) {
            for (int dz = -LEGACY_SEARCH_RADIUS; dz <= LEGACY_SEARCH_RADIUS; dz++) {
                for (int dy = 0; dy <= LEGACY_SEARCH_HEIGHT; dy++) {
                    int x = base.x + dx;
                    int y = base.y + dy;
                    int z = base.z + dz;
                    WorldChunk chunk = this.getChunk(world, new Vec3i(x, y, z));
                    if (chunk == null) {
                        continue;
                    }
                    BlockType blockType = chunk.getBlockType(x, y, z);
                    if (this.isLegacyDebugBlock(blockType)) {
                        chunk.breakBlock(x, y, z);
                    }
                }
            }
        }
    }

    private boolean isLegacyDebugBlock(BlockType blockType) {
        return blockType != null
            && (
                this.sameBlockType(blockType, BlockType.DEBUG_CUBE)
                    || this.sameBlockType(blockType, BlockType.DEBUG_MODEL)
                    || this.sameBlockType(blockType, BlockType.UNKNOWN)
            );
    }

    private List<EnemyInstance> spawnEnemies(MatchContext context) {
        List<EnemyInstance> spawned = new ArrayList<>();
        while (!context.pendingSpawns.isEmpty() && context.pendingSpawns.get(0).spawnAtSeconds <= context.waveElapsedSeconds) {
            ScheduledSpawn spawn = context.pendingSpawns.remove(0);
            EnemyInstance enemy = new EnemyInstance(context.nextEnemyId++, spawn);
            this.initializeSpecialEnemyState(context, enemy);
            context.enemies.add(enemy);
            spawned.add(enemy);
        }
        if (context.pendingSpawns.isEmpty() && context.state.waveState == WaveState.Spawning) {
            context.state.waveState = WaveState.Cleanup;
        }
        return spawned;
    }

    private void initializeSpecialEnemyState(MatchContext context, EnemyInstance enemy) {
        if (context == null || enemy == null || enemy.definition == null || enemy.definition.id == null) {
            return;
        }
        if (context.tutorialActive && enemy.waveNumber == TUTORIAL_TRAP_WAVE_NUMBER) {
            int sequenceIndex = Math.max(0, context.tutorialTrapWaveSpawnedCount);
            context.tutorialTrapWaveSpawnedCount++;
            enemy.tutorialTrapSequenceIndex = sequenceIndex;
            enemy.tutorialTrapTargetLocked = true;
            enemy.tutorialTrapReleaseSlotId = this.tutorialTrapReleaseSlotId(context, sequenceIndex);
        }
        switch (enemy.definition.id) {
            case ENEMY_RIFT_TWIN_ALPHA, ENEMY_RIFT_TWIN_BETA -> {
                enemy.specialCooldownRemaining = DUO_RIFT_TWIN_SWAP_SECONDS;
                enemy.damageableTwin = ENEMY_RIFT_TWIN_ALPHA.equals(enemy.definition.id);
                this.linkRiftTwinPair(context, enemy);
            }
            case ENEMY_NODE_ARBITER -> {
                enemy.extraLivesRemaining = 4;
                enemy.variantStage = 0;
            }
            case ENEMY_SEAL_MASTER -> {
                enemy.specialCooldownRemaining = 0.0;
                enemy.variantStage = 0;
            }
            case ENEMY_SEAL_NODE -> {
                enemy.specialCooldownRemaining = 9999.0;
                enemy.remainingDisablePulses = 0;
            }
            default -> {
            }
        }
    }

    private void linkRiftTwinPair(MatchContext context, EnemyInstance enemy) {
        if (context == null || enemy == null || enemy.definition == null) {
            return;
        }
        String otherId = ENEMY_RIFT_TWIN_ALPHA.equals(enemy.definition.id) ? ENEMY_RIFT_TWIN_BETA : ENEMY_RIFT_TWIN_ALPHA;
        for (EnemyInstance candidate : context.enemies) {
            if (candidate == enemy || candidate.definition == null || !otherId.equals(candidate.definition.id)) {
                continue;
            }
            enemy.linkedBossInstanceId = candidate.instanceId;
            candidate.linkedBossInstanceId = enemy.instanceId;
            candidate.damageableTwin = !enemy.damageableTwin;
            return;
        }
    }

    private void tickEnemies(World world, MatchContext context, double deltaSeconds) {
        this.applyEnemySupportEffects(world, context, deltaSeconds);
        List<EnemyInstance> survivors = new ArrayList<>();
        List<EnemyInstance> summoned = new ArrayList<>();
        for (EnemyInstance enemy : context.enemies) {
            if (enemy.dying) {
                enemy.dyingRemaining -= deltaSeconds;
                if (enemy.dyingRemaining > 0.0) {
                    survivors.add(enemy);
                }
                continue;
            }
            if (enemy.slowRemaining > 0.0) {
                enemy.slowRemaining -= deltaSeconds;
                if (enemy.slowRemaining <= 0.0) {
                    enemy.slowRemaining = 0.0;
                    enemy.slowPercent = 0.0;
                }
            }
            if (enemy.armorBreakRemaining > 0.0) {
                enemy.armorBreakRemaining -= deltaSeconds;
                if (enemy.armorBreakRemaining <= 0.0) {
                    enemy.armorBreakRemaining = 0.0;
                    enemy.armorBreakFlatReduction = 0.0;
                    enemy.armorBreakPercentReduction = 0.0;
                }
            }
            if (enemy.rootedRemaining > 0.0) {
                enemy.rootedRemaining = Math.max(0.0, enemy.rootedRemaining - deltaSeconds);
            }
            if (enemy.tutorialTrapTargetLocked && enemy.tutorialTrapReleaseDelayRemaining > 0.0) {
                enemy.tutorialTrapReleaseDelayRemaining = Math.max(0.0, enemy.tutorialTrapReleaseDelayRemaining - deltaSeconds);
                if (enemy.tutorialTrapReleaseDelayRemaining <= 0.0) {
                    enemy.tutorialTrapTargetLocked = false;
                    enemy.tutorialTrapReleaseSlotId = "";
                }
            }
            if (enemy.specialCooldownRemaining > 0.0) {
                enemy.specialCooldownRemaining -= deltaSeconds;
            }
            if (enemy.isBossCasting()) {
                enemy.castRemaining = Math.max(0.0, enemy.castRemaining - deltaSeconds);
                if (enemy.castRemaining <= 0.0) {
                    this.resolveBossCastAbility(world, context, enemy, summoned);
                }
                if (!enemy.dying) {
                    survivors.add(enemy);
                }
                continue;
            }

            if ("necro_king".equals(enemy.definition.id)
                && enemy.linkedSummonPhaseActive
                && !this.hasLivingLinkedSummons(context, enemy.instanceId)) {
                enemy.linkedSummonPhaseActive = false;
                enemy.specialCooldownRemaining = Math.max(enemy.specialCooldownRemaining, 15.0);
            }

            if (this.handleEnemyAbility(world, context, enemy, summoned)) {
                if (!enemy.dying || enemy.dyingRemaining > 0.0) {
                    survivors.add(enemy);
                }
                continue;
            }

            double previousProgress = enemy.progress;
            double slowMultiplier = enemy.rootedRemaining > 0.0
                ? 0.0
                : (enemy.definition.slowImmune ? 1.0 : Math.max(0.1, 1.0 - enemy.slowPercent));
            double movementMultiplier = this.enemyMovementMultiplier(context, enemy);
            enemy.progress += enemy.speed * movementMultiplier * PATH_SPEED_SCALE * slowMultiplier * deltaSeconds;
            enemy.visualRotation = this.rotationTowards(
                this.pointAtProgress(context, enemy.laneId, previousProgress).toWorldVector(0.1),
                this.pointAtProgress(context, enemy.laneId, enemy.progress).toWorldVector(0.1)
            );
            if (enemy.progress >= this.routeLengthForLane(context, enemy.laneId)) {
                if ("goblin_bomber_boss".equals(enemy.definition.id)) {
                    this.applyGoblinBossProgressPenalty(world, context, enemy, 1.0);
                }
                if (this.consumeStormMonolithShield(world, context, enemy)) {
                    if (context.tutorialActive && "vault_breaker".equals(enemy.definition.id) && !enemy.dying) {
                        survivors.add(enemy);
                    }
                    continue;
                }
                context.state.bankHp -= Math.max(0, enemy.leakDamage);
                this.emitLeakFeedback(world, context, enemy);
                continue;
            }
            survivors.add(enemy);
        }
        context.enemies.clear();
        context.enemies.addAll(survivors);
        context.enemies.addAll(summoned);
        for (EnemyInstance enemy : summoned) {
            this.onEnemySpawned(world, context, enemy);
        }
    }

    private void tickTowers(World world, MatchContext context, double deltaSeconds) {
        for (TowerInstance tower : context.placedTowers.values()) {
            tower.idleSeconds += deltaSeconds;
            if (tower.ambientFxCooldownRemaining > 0.0) {
                tower.ambientFxCooldownRemaining = Math.max(0.0, tower.ambientFxCooldownRemaining - deltaSeconds);
            }
            if (tower.definition.superTower && tower.ambientFxCooldownRemaining <= 0.0) {
                this.emitSuperTowerAura(world, tower);
                tower.ambientFxCooldownRemaining = 0.38;
            }
            if (tower.trapPendingRemaining > 0.0) {
                tower.trapPendingRemaining = Math.max(0.0, tower.trapPendingRemaining - deltaSeconds);
                tower.trapBlinkAccumulator += deltaSeconds;
                double blinkInterval = this.trapBlinkIntervalSeconds(tower);
                if (tower.trapPendingRemaining > 0.0 && tower.trapBlinkAccumulator >= blinkInterval) {
                    tower.trapBlinkAccumulator = 0.0;
                    tower.trapBoomVisual = !tower.trapBoomVisual;
                }
                if (tower.trapPendingRemaining <= 0.0) {
                    this.detonateTrap(world, context, tower);
                }
                continue;
            }
            if (tower.disableFxCooldownRemaining > 0.0) {
                tower.disableFxCooldownRemaining = Math.max(0.0, tower.disableFxCooldownRemaining - deltaSeconds);
            }
            if (tower.disabledRemaining > 0.0) {
                tower.disabledRemaining = Math.max(0.0, tower.disabledRemaining - deltaSeconds);
                if (tower.disableFxCooldownRemaining <= 0.0) {
                    this.emitDisabledTowerFeedback(world, tower);
                    tower.disableFxCooldownRemaining = 0.55;
                }
                continue;
            }
            if (tower.cooldownRemaining > 0.0) {
                tower.cooldownRemaining -= deltaSeconds;
            }
            if (tower.cooldownRemaining > 0.0) {
                continue;
            }
            if ("seed_idol".equals(tower.definition.id) || "heart_of_roots".equals(tower.definition.id)) {
                tower.cooldownRemaining = 1.0;
                continue;
            }
            EnemyInstance target = this.pickTarget(context, tower);
            if (target == null) {
                continue;
            }
            Vector3d targetPosition = this.enemyWorldPosition(context, target);

            if ("root_snare".equals(tower.definition.id) || "spore_mine".equals(tower.definition.id) || "frost_seal".equals(tower.definition.id)) {
                this.beginTrapTrigger(world, context, tower, target, targetPosition);
                continue;
            }

            TowerLevel level = tower.getCurrentLevel();
            ModuleDefinition module = tower.definition.modulesAllowed && tower.slot.modulesAllowed ? this.equippedModule(context, tower) : null;
            double fireRate = Math.max(0.01, level.fireRate * this.moduleRelativeMultiplier(context, module == null ? 1.0 : module.fireRateMultiplier));
            double damage = level.damage * this.moduleRelativeMultiplier(context, module == null ? 1.0 : module.damageMultiplier);
            damage *= this.towerDamageMultiplier(context, tower);
            double slowPercent = level.slowPercent + this.moduleBonusValue(context, module == null ? 0.0 : module.slowPercentBonus);
            double slowDuration = level.slowDuration + this.moduleBonusValue(context, module == null ? 0.0 : module.slowDurationBonus);
            if ("freeze_gate".equals(tower.definition.id) && "cryo_capsule".equals(tower.equippedModuleId)) {
                slowPercent += 0.10;
                slowDuration += 0.5;
            }
            if ("armor_drill".equals(tower.definition.id) && this.isHeavyEnemy(target.definition.id)) {
                damage *= 1.4 + tower.levelIndex * 0.08;
            }
            double moduleBonusDamageToHeavy = this.moduleBonusValue(context, module == null ? 0.0 : module.bonusDamageToHeavy);
            if (module != null && moduleBonusDamageToHeavy > 0.0 && this.isHeavyEnemy(target.definition.id)) {
                damage *= 1.0 + moduleBonusDamageToHeavy;
            }
            if (module != null && module.chargeSeconds > 0.0 && tower.idleSeconds >= module.chargeSeconds) {
                damage *= this.moduleRelativeMultiplier(context, module.chargedDamageMultiplier);
            }
            Vector3d towerPosition = this.towerWorldPosition(tower.slot);
            fireRate *= this.enemyFireRateMultiplier(context, towerPosition);
            fireRate *= this.sealCurseTowerFireRateMultiplier(context);
            tower.visualRotation = this.rotationTowards(towerPosition, targetPosition);

            if ("storm_monolith".equals(tower.definition.id)) {
                this.applyStormMonolith(world, context, tower, towerPosition, targetPosition, damage);
            } else {
                this.applyTowerHit(world, context, tower, target, towerPosition, targetPosition, damage, slowPercent, slowDuration, true);
                if ("freeze_gate".equals(tower.definition.id)) {
                    double aoeRadius = 2.5 + tower.levelIndex * 0.45 + this.moduleBonusValue(context, module == null ? 0.0 : module.splashRadius * 0.20);
                    this.emitFreezeAoeFeedback(world, tower, targetPosition, aoeRadius);
                    this.applySplashDamage(world, context, tower, target, targetPosition, damage * 0.70, slowPercent, slowDuration, aoeRadius);
                } else if ("shock_relay".equals(tower.definition.id)) {
                    int extraTargets = 1 + (tower.levelIndex / 3) + (module == null ? 0 : module.chainBonusTargets);
                    double chainRange = 3.4 + tower.levelIndex * 0.45 + this.moduleBonusValue(context, module == null ? 0.0 : module.chainRangeBonus);
                    this.applyChainDamage(world, context, tower, target, towerPosition, targetPosition, extraTargets, chainRange, damage * 0.62, slowPercent * 0.55, slowDuration);
                } else if (module != null && module.chainBonusTargets > 0) {
                    double chainRange = 3.2 + this.moduleBonusValue(context, module.chainRangeBonus);
                    this.applyChainDamage(world, context, tower, target, towerPosition, targetPosition, module.chainBonusTargets, chainRange, damage * 0.5, slowPercent * 0.5, slowDuration * 0.8);
                }
                double splashDamageRatio = this.moduleBonusValue(context, module == null ? 0.0 : module.splashDamageRatio);
                double splashRadius = this.moduleBonusValue(context, module == null ? 0.0 : module.splashRadius);
                if (module != null && splashDamageRatio > 0.0 && splashRadius > 0.0) {
                    this.applySplashDamage(world, context, tower, target, targetPosition, damage * splashDamageRatio, slowPercent * 0.5, slowDuration * 0.5, splashRadius);
                }
                tower.shotsFired++;
                if (module != null && module.doubleShotEvery > 0 && tower.shotsFired % module.doubleShotEvery == 0) {
                    EnemyInstance extraTarget = target.dying ? this.pickTarget(context, tower) : target;
                    if (extraTarget != null) {
                        Vector3d extraTargetPosition = this.enemyWorldPosition(context, extraTarget);
                        this.applyTowerHit(
                            world,
                            context,
                            tower,
                            extraTarget,
                            towerPosition,
                            extraTargetPosition,
                            damage * this.moduleRelativeMultiplier(context, module.doubleShotDamageMultiplier),
                            slowPercent,
                            slowDuration,
                            true
                        );
                    }
                }
                this.emitShotFeedback(world, tower, towerPosition, targetPosition);
            }

            tower.cooldownRemaining = Math.max(0.1, 1.0 / fireRate);
            tower.idleSeconds = 0.0;
        }
    }

    private double towerDamageMultiplier(MatchContext context, TowerInstance tower) {
        double multiplier = 1.0;
        multiplier += this.progressionValue(context, "tower_damage_percent_branch", tower.definition.progressionBranch);
        multiplier += this.progressionValue(context, "tower_damage_percent_tower", tower.definition.id);
        if (tower.definition.superTower) {
            multiplier += this.progressionValue(context, "super_damage_percent", null);
        }
        if (context != null
            && context.rootsHeartBuffWaveNumber > 0
            && context.activeWaveNumber == context.rootsHeartBuffWaveNumber
            && context.state.gameState == GameState.InMatch) {
            multiplier *= Math.max(1.0, context.rootsHeartBuffMultiplier);
        }
        if (this.hasActiveSealCurse(context, SealCurseType.Damage)) {
            multiplier *= DUO_SEAL_CURSE_DAMAGE_MULTIPLIER;
        }
        return Math.max(0.1, multiplier);
    }

    @SuppressWarnings("unused")
    private void applyHeartOfRoots(
        World world,
        MatchContext context,
        TowerInstance tower,
        EnemyInstance target,
        Vector3d towerPosition,
        Vector3d targetPosition,
        double damage,
        double slowPercent,
        double slowDuration
    ) {
        double aoeRadius = 3.8 + tower.levelIndex * 0.65;
        double heavyDuration = slowDuration + 1.4;
        this.emitFreezeAoeFeedback(world, tower, targetPosition, aoeRadius);
        this.applyTowerHit(world, context, tower, target, towerPosition, targetPosition, damage * 1.10, 1.0, heavyDuration, true);
        target.rootedRemaining = Math.max(target.rootedRemaining, heavyDuration);
        double radiusSquared = aoeRadius * aoeRadius;
        for (EnemyInstance enemy : context.enemies) {
            if (enemy == target || enemy.dying) {
                continue;
            }
            Vector3d enemyPosition = this.enemyWorldPosition(context, enemy);
            double dx = enemyPosition.x - targetPosition.x;
            double dy = enemyPosition.y - targetPosition.y;
            double dz = enemyPosition.z - targetPosition.z;
            if ((dx * dx) + (dy * dy) + (dz * dz) > radiusSquared) {
                continue;
            }
            enemy.rootedRemaining = Math.max(enemy.rootedRemaining, heavyDuration);
            this.applyTowerHit(world, context, tower, enemy, targetPosition, enemyPosition, damage * 0.92, 1.0, heavyDuration, true);
        }
    }

    private void beginTrapTrigger(World world, MatchContext context, TowerInstance tower, EnemyInstance target, Vector3d targetPosition) {
        tower.trapPendingRemaining = this.trapTriggerDelaySeconds(context);
        tower.trapPendingTotal = tower.trapPendingRemaining;
        tower.trapBlinkAccumulator = 0.0;
        tower.trapBoomVisual = false;
        tower.visualRotation = this.rotationTowards(this.towerWorldPosition(tower.slot), targetPosition);
        if (context != null
            && context.tutorialActive
            && target != null
            && target.tutorialTrapTargetLocked
            && tower != null
            && tower.slot != null
            && tower.slot.id != null
            && tower.slot.id.equals(target.tutorialTrapReleaseSlotId)) {
            target.rootedRemaining = Math.max(
                target.rootedRemaining,
                tower.trapPendingRemaining + TUTORIAL_TRAP_RELEASE_DELAY_SECONDS
            );
        }
    }

    private void detonateTrap(World world, MatchContext context, TowerInstance tower) {
        TowerLevel level = tower.getCurrentLevel();
        Vector3d towerPosition = this.towerWorldPosition(tower.slot);
        tower.trapPendingRemaining = 0.0;
        tower.trapPendingTotal = 0.0;
        tower.trapBlinkAccumulator = 0.0;
        tower.trapBoomVisual = true;

        if ("root_snare".equals(tower.definition.id)) {
            double radius = 20.8;
            double rootDuration = Math.max(3.8, level.slowDuration + 1.2);
            this.playSound3d(world, SOUND_GOBLIN_DOWNGRADE, towerPosition);
            this.emitFreezeAoeFeedback(world, tower, towerPosition, radius);
            this.rootEnemiesInRadius(world, context, tower, towerPosition, radius, rootDuration, Math.max(0.0, level.damage));
            this.releaseTutorialTrapTargets(context, tower.slot.id, towerPosition, radius);
        } else if ("spore_mine".equals(tower.definition.id)) {
            double radius = 17.6;
            this.playSound3d(world, SOUND_GOBLIN_DESTROY, towerPosition);
            this.emitImpactFeedback(world, tower.definition.id, towerPosition);
            this.applySplashDamage(world, context, tower, null, towerPosition, level.damage * 1.05, 0.0, 0.0, radius);
            this.releaseTutorialTrapTargets(context, tower.slot.id, towerPosition, radius);
        } else if ("frost_seal".equals(tower.definition.id)) {
            double radius = 16.8;
            this.playSound3d(world, SOUND_FIRE_FROST, towerPosition);
            this.emitFreezeAoeFeedback(world, tower, towerPosition, radius);
            this.applySplashDamage(world, context, tower, null, towerPosition, level.damage, level.slowPercent, level.slowDuration, radius);
            this.releaseTutorialTrapTargets(context, tower.slot.id, towerPosition, radius);
        }

        this.consumeTrap(world, context, tower);
    }

    private void releaseTutorialTrapTargets(MatchContext context, String slotId, Vector3d center, double radius) {
        if (context == null || slotId == null || slotId.isBlank()) {
            return;
        }
        for (EnemyInstance enemy : context.enemies) {
            if (enemy == null || enemy.dying || !enemy.tutorialTrapTargetLocked) {
                continue;
            }
            if (enemy.tutorialTrapReleaseSlotId == null || !enemy.tutorialTrapReleaseSlotId.equals(slotId)) {
                continue;
            }
            enemy.rootedRemaining = Math.max(enemy.rootedRemaining, TUTORIAL_TRAP_RELEASE_DELAY_SECONDS);
            enemy.tutorialTrapReleaseDelayRemaining = Math.max(enemy.tutorialTrapReleaseDelayRemaining, TUTORIAL_TRAP_RELEASE_DELAY_SECONDS);
        }
    }

    private void rootEnemiesInRadius(
        World world,
        MatchContext context,
        TowerInstance tower,
        Vector3d center,
        double radius,
        double rootedDuration,
        double damage
    ) {
        double radiusSquared = radius * radius;
        for (EnemyInstance enemy : context.enemies) {
            if (enemy.dying) {
                continue;
            }
            Vector3d enemyPosition = this.enemyWorldPosition(context, enemy);
            double dx = enemyPosition.x - center.x;
            double dy = enemyPosition.y - center.y;
            double dz = enemyPosition.z - center.z;
            if ((dx * dx) + (dy * dy) + (dz * dz) > radiusSquared) {
                continue;
            }
            enemy.rootedRemaining = Math.max(enemy.rootedRemaining, rootedDuration);
            if (damage > 0.0) {
                this.applyTowerHit(world, context, tower, enemy, center, enemyPosition, damage, 1.0, rootedDuration, true);
            }
        }
    }

    private void consumeTrap(World world, MatchContext context, TowerInstance tower) {
        context.placedTowers.remove(tower.slot.id);
        this.playSound3d(world, SOUND_TRAP_PLACE, this.towerWorldPosition(tower.slot));
        this.refreshVisualizationIfEnabled(world);
    }

    private void applyStormMonolith(
        World world,
        MatchContext context,
        TowerInstance tower,
        Vector3d towerPosition,
        Vector3d targetPosition,
        double damage
    ) {
        tower.cooldownRemaining = 1.0;
    }

    @SuppressWarnings("unused")
    private Vector3d projectLineEnd(Vector3d from, Vector3d toward, double distance) {
        double dx = toward.x - from.x;
        double dy = toward.y - from.y;
        double dz = toward.z - from.z;
        double length = Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (length <= 0.0001) {
            return new Vector3d(from.x + distance, from.y, from.z);
        }
        double scale = distance / length;
        return new Vector3d(from.x + dx * scale, from.y + dy * scale, from.z + dz * scale);
    }

    @SuppressWarnings("unused")
    private double pointToSegmentDistance(Vector3d point, Vector3d start, Vector3d end) {
        double vx = end.x - start.x;
        double vy = end.y - start.y;
        double vz = end.z - start.z;
        double wx = point.x - start.x;
        double wy = point.y - start.y;
        double wz = point.z - start.z;
        double lengthSquared = vx * vx + vy * vy + vz * vz;
        if (lengthSquared <= 0.0001) {
            double dx = point.x - start.x;
            double dy = point.y - start.y;
            double dz = point.z - start.z;
            return Math.sqrt(dx * dx + dy * dy + dz * dz);
        }
        double t = Math.max(0.0, Math.min(1.0, (wx * vx + wy * vy + wz * vz) / lengthSquared));
        double px = start.x + vx * t;
        double py = start.y + vy * t;
        double pz = start.z + vz * t;
        double dx = point.x - px;
        double dy = point.y - py;
        double dz = point.z - pz;
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }

    private boolean consumeStormMonolithShield(World world, MatchContext context, EnemyInstance breachedEnemy) {
        List<TowerInstance> monoliths = new ArrayList<>();
        for (TowerInstance tower : context.placedTowers.values()) {
            if ("storm_monolith".equals(tower.definition.id) && tower.superReady && this.remainingSuperActivations(context, tower) > 0) {
                monoliths.add(tower);
            }
        }
        if (monoliths.isEmpty()) {
            return false;
        }
        TowerInstance monolith = monoliths.get(0);
        monolith.superActivationsUsed++;
        monolith.superReady = false;
        int remainingActivations = this.remainingSuperActivations(context, monolith);
        this.showEventToast(
            context,
            this.choose(world, "Штормовой монолит отбросил орду назад.", "The Storm Monolith threw the horde back."),
            EVENT_TOAST_ICON_CORES
        );
        this.playSound3d(world, SOUND_MONOLITH_BURST, this.towerWorldPosition(monolith.slot));
        if (context.tutorialActive && breachedEnemy != null && "vault_breaker".equals(breachedEnemy.definition.id)) {
            breachedEnemy.progress = 0.0;
            breachedEnemy.hp = Math.max(1.0, Math.min(breachedEnemy.hp, breachedEnemy.maxHp * 0.10));
            breachedEnemy.slowPercent = 0.0;
            breachedEnemy.slowRemaining = 0.0;
            breachedEnemy.rootedRemaining = 0.0;
            breachedEnemy.specialCooldownRemaining = breachedEnemy.initialCooldown(breachedEnemy.definition);
            this.sendLocalizedWorldMessage(
                world,
                remainingActivations > 0
                    ? "Штормовой монолит отбросил Разорителя назад и почти добил его. Добей босса, чтобы завершить волну."
                    : "Штормовой монолит отбросил Разорителя назад, но все его активации исчерпаны.",
                remainingActivations > 0
                    ? "The Storm Monolith threw the Ravager back and nearly finished it. Kill the boss to end the wave."
                    : "The Storm Monolith threw the Ravager back, but all of its activations are spent."
            );
        } else {
            for (EnemyInstance enemy : context.enemies) {
                if (enemy.dying) {
                    continue;
                }
                enemy.progress = 0.0;
                enemy.hp = Math.max(1.0, enemy.hp * 0.5);
                enemy.slowPercent = 0.0;
                enemy.slowRemaining = 0.0;
                enemy.rootedRemaining = 0.0;
                enemy.specialCooldownRemaining = enemy.initialCooldown(enemy.definition);
            }
            this.sendLocalizedWorldMessage(
                world,
                remainingActivations > 0
                    ? "Штормовой монолит принял удар. Дупло уцелело, орда отброшена назад и потеряла 50% текущего здоровья. Монолит можно реактивировать позже."
                    : "Штормовой монолит принял удар. Дупло уцелело, орда отброшена назад и потеряла 50% текущего здоровья, но все активации исчерпаны.",
                remainingActivations > 0
                    ? "The Storm Monolith absorbed the hit. The Hollow survived, the horde was pushed back, and each enemy lost 50% of its current health. The Monolith can be reactivated later."
                    : "The Storm Monolith absorbed the hit. The Hollow survived, the horde was pushed back, and each enemy lost 50% of its current health, but all activations are spent."
            );
        }
        this.playWorldUiSound(world, SOUND_LEAK);
        this.refreshVisualizationIfEnabled(world);
        return true;
    }

    private void applyEnemySupportEffects(World world, MatchContext context, double deltaSeconds) {
        if (context == null || !context.hasEnemyRegenAuras) {
            return;
        }
        for (EnemyInstance source : context.enemies) {
            if (source.dying
                || source.definition.regenPercentPerSecond <= 0.0
                || source.definition.regenAuraRadius <= 0.0
                || source.remainingRegenBudget <= 0.0) {
                continue;
            }
            Vector3d sourcePosition = this.enemyWorldPosition(context, source);
            double radius = source.definition.regenAuraRadius * this.contractAuraRadiusMultiplier(context, source.definition);
            double effectMultiplier = this.contractAuraEffectMultiplier(context, source.definition);
            double radiusSquared = radius * radius;
            for (EnemyInstance target : context.enemies) {
                if (source.remainingRegenBudget <= 0.0) {
                    break;
                }
                if (target.dying || target.hp >= target.maxHp) {
                    continue;
                }
                Vector3d targetPosition = this.enemyWorldPosition(context, target);
                double dx = targetPosition.x - sourcePosition.x;
                double dy = targetPosition.y - sourcePosition.y;
                double dz = targetPosition.z - sourcePosition.z;
                if ((dx * dx) + (dy * dy) + (dz * dz) > radiusSquared) {
                    continue;
                }
                double missingHp = Math.max(0.0, target.maxHp - target.hp);
                if (missingHp <= 0.0) {
                    continue;
                }
                double intendedHeal = target.maxHp * source.definition.regenPercentPerSecond * effectMultiplier * deltaSeconds;
                double appliedHeal = Math.min(missingHp, Math.min(source.remainingRegenBudget, intendedHeal));
                if (appliedHeal <= 0.0) {
                    continue;
                }
                target.hp = Math.min(target.maxHp, target.hp + appliedHeal);
                source.remainingRegenBudget = Math.max(0.0, source.remainingRegenBudget - appliedHeal);
            }
        }
    }

    private boolean handleEnemyAbility(World world, MatchContext context, EnemyInstance enemy, List<EnemyInstance> summoned) {
        return switch (enemy.definition.id) {
            case "runner_bomber" -> this.handleBomberAbility(world, context, enemy);
            case "necro_thief" -> this.handleSummonerAbility(context, enemy, summoned);
            case "necro_king" -> this.handleNecroKingAbility(world, context, enemy, summoned);
            case "vault_breaker" -> this.handleBossDisablePulse(world, context, enemy);
            case ENEMY_RIFT_TWIN_ALPHA, ENEMY_RIFT_TWIN_BETA -> this.handleRiftTwinAbility(world, context, enemy);
            default -> false;
        };
    }

    private EnemyInstance findEnemyByInstanceId(MatchContext context, long instanceId) {
        if (context == null || instanceId <= 0L) {
            return null;
        }
        for (EnemyInstance enemy : context.enemies) {
            if (enemy.instanceId == instanceId) {
                return enemy;
            }
        }
        return null;
    }

    private boolean handleRiftTwinAbility(World world, MatchContext context, EnemyInstance enemy) {
        if (context == null || enemy == null || enemy.specialCooldownRemaining > 0.0) {
            return false;
        }
        EnemyInstance linkedTwin = this.findEnemyByInstanceId(context, enemy.linkedBossInstanceId);
        if (linkedTwin == null || linkedTwin.dying) {
            enemy.damageableTwin = true;
            enemy.specialCooldownRemaining = DUO_RIFT_TWIN_SWAP_SECONDS;
            return false;
        }
        boolean nextDamageable = !enemy.damageableTwin;
        enemy.damageableTwin = nextDamageable;
        linkedTwin.damageableTwin = !nextDamageable;
        enemy.specialCooldownRemaining = DUO_RIFT_TWIN_SWAP_SECONDS;
        linkedTwin.specialCooldownRemaining = DUO_RIFT_TWIN_SWAP_SECONDS;
        EnemyInstance activeTwin = enemy.damageableTwin ? enemy : linkedTwin;
        this.playSound3d(world, SOUND_RIFT_TWIN_SWAP, this.enemyWorldPosition(context, activeTwin));
        this.showEventToast(
            context,
            this.choose(world, "Окно уязвимости: ", "Vulnerability window: ") + this.enemyDisplayName(world, activeTwin.definition) + ".",
            EVENT_TOAST_ICON_ALERT
        );
        return false;
    }

    private void tickDuoBossState(World world, MatchContext context, double deltaSeconds) {
        if (!this.isDuoMode(context) || context == null || context.state.gameState != GameState.InMatch) {
            return;
        }
        for (EnemyInstance enemy : context.enemies) {
            if (enemy == null || enemy.dying || enemy.definition == null || !ENEMY_SEAL_MASTER.equals(enemy.definition.id)) {
                continue;
            }
            this.tickSealMasterProgressState(world, context, enemy);
        }
    }

    private void tickSealMasterProgressState(World world, MatchContext context, EnemyInstance enemy) {
        if (world == null || context == null || enemy == null || enemy.definition == null || !ENEMY_SEAL_MASTER.equals(enemy.definition.id)) {
            return;
        }
        double progressRatio = this.sealMasterProgressRatio(context, enemy);
        if (enemy.variantStage < 1 && progressRatio >= DUO_SEAL_MASTER_FIRST_THRESHOLD) {
            enemy.variantStage = 1;
            this.emitSealMasterThresholdFeedback(world, context, enemy, 1);
        }
        if (enemy.variantStage < 2 && progressRatio >= DUO_SEAL_MASTER_SECOND_THRESHOLD) {
            enemy.variantStage = 2;
            this.emitSealMasterThresholdFeedback(world, context, enemy, 2);
        }
    }

    private void emitSealMasterThresholdFeedback(World world, MatchContext context, EnemyInstance enemy, int stage) {
        this.playWorldUiSound(world, SOUND_ALERT);
        this.playSound3d(world, stage >= 2 ? SOUND_SEAL_MASTER_PHASE_2 : SOUND_SEAL_MASTER_PHASE_1, this.enemyWorldPosition(context, enemy));
        this.emitSealMasterThresholdVisualPulse(world, context, enemy, stage);
        if (stage >= 2) {
            this.emitBossCastReleaseFeedback(world, context, enemy);
            String message = this.choose(
                world,
                "Мастер Печати прорвался глубже и ускорился.",
                "The Seal Master pushed deeper and accelerated."
            );
            this.showEventToast(context, message, EVENT_TOAST_ICON_ALERT);
            this.sendLocalizedWorldMessage(world, "Мастер Печати прорвался глубже и ускорился.", "The Seal Master pushed deeper and accelerated.");
            return;
        }
        this.emitBossCastStartFeedback(world, context, enemy);
        this.showEventToast(
            context,
            this.choose(world, "Печать пробуждается.", "The seal is awakening."),
            EVENT_TOAST_ICON_ALERT
        );
    }

    private void emitSealMasterThresholdVisualPulse(World world, MatchContext context, EnemyInstance enemy, int stage) {
        if (world == null || context == null || enemy == null) {
            return;
        }
        List<Ref<EntityStore>> playerRefs = this.collectPlayerEntityRefs(world);
        if (playerRefs.isEmpty()) {
            return;
        }
        Store<EntityStore> store = world.getEntityStore().getStore();
        Vector3d origin = this.enemyWorldPosition(context, enemy);
        Vector3d center = new Vector3d(origin.x, origin.y + 1.0, origin.z);
        Vector3f rotation = new Vector3f(0.0f, 0.0f, 0.0f);
        Color primary = stage >= 2 ? this.rgb(255, 112, 84) : this.rgb(178, 118, 255);
        Color secondary = stage >= 2 ? this.rgb(255, 220, 128) : this.rgb(242, 188, 255);
        float coreScale = stage >= 2 ? 1.95f : 1.45f;
        double ringRadius = stage >= 2 ? 1.58 : 1.14;
        int ringPoints = stage >= 2 ? 12 : 8;
        this.spawnColoredParticle(store, playerRefs, PARTICLE_SHOT, center, rotation, coreScale, primary);
        this.spawnColoredParticle(store, playerRefs, PARTICLE_IMPACT, center, rotation, coreScale * 1.08f, secondary);
        this.spawnColoredParticle(
            store,
            playerRefs,
            PARTICLE_IMPACT,
            new Vector3d(origin.x, origin.y + 0.42, origin.z),
            rotation,
            stage >= 2 ? 1.42f : 1.08f,
            primary
        );
        this.spawnParticleBurst(store, playerRefs, center, ringRadius * 0.42, stage >= 2 ? 14 : 9, 0.08);
        for (int i = 0; i < ringPoints; i++) {
            double angle = (Math.PI * 2.0 * i) / Math.max(1, ringPoints);
            double lift = (i % 2 == 0) ? 0.0 : 0.18;
            Vector3d ringPoint = new Vector3d(
                center.x + Math.cos(angle) * ringRadius,
                center.y + lift,
                center.z + Math.sin(angle) * ringRadius
            );
            this.spawnColoredParticle(
                store,
                playerRefs,
                PARTICLE_SHOT,
                ringPoint,
                rotation,
                stage >= 2 ? 0.84f : 0.62f,
                primary
            );
            this.spawnColoredParticle(
                store,
                playerRefs,
                PARTICLE_IMPACT,
                ringPoint,
                rotation,
                stage >= 2 ? 0.62f : 0.42f,
                secondary
            );
        }
    }

    private double closestRouteProgress(MatchContext context, String laneId, Vec3i point) {
        if (context == null || point == null) {
            return 0.0;
        }
        List<Vec3i> route = this.routeForLane(context, laneId);
        if (route.isEmpty()) {
            return 0.0;
        }
        double bestDistance = Double.MAX_VALUE;
        double bestProgress = 0.0;
        double progress = 0.0;
        for (int i = 0; i < route.size(); i++) {
            Vec3i routePoint = route.get(i);
            double distance = routePoint.distanceSquaredTo(point);
            if (distance < bestDistance) {
                bestDistance = distance;
                bestProgress = progress;
            }
            if (i < route.size() - 1) {
                progress += this.segmentLength(routePoint, route.get(i + 1));
            }
        }
        return Math.max(0.0, Math.min(bestProgress, Math.max(0.0, this.routeLengthForLane(context, laneId) - 0.2)));
    }

    private boolean handleBomberAbility(World world, MatchContext context, EnemyInstance enemy) {
        if (enemy.definition.towerStunRadius <= 0.0 || enemy.definition.towerStunDuration <= 0.0) {
            return false;
        }
        List<TowerInstance> targets = this.findTowersInRadius(context, this.enemyWorldPosition(context, enemy), enemy.definition.towerStunRadius);
        if (targets.isEmpty()) {
            return false;
        }
        int targetCount = enemy.definition.towerStunTargetCount <= 0
            ? targets.size()
            : Math.min(enemy.definition.towerStunTargetCount, targets.size());
        for (int i = 0; i < targetCount; i++) {
            this.applyTowerDisable(world, targets.get(i), enemy.definition.towerStunDuration, this.rgb(255, 168, 84), 0.75);
        }
        if (enemy.definition.selfDestructOnTowerStun) {
            this.beginEnemyDeath(world, context, enemy, this.enemyWorldPosition(context, enemy));
            return true;
        }
        enemy.specialCooldownRemaining = Math.max(enemy.specialCooldownRemaining, enemy.definition.towerStunDuration + 1.5);
        return false;
    }

    private boolean handleSummonerAbility(MatchContext context, EnemyInstance enemy, List<EnemyInstance> summoned) {
        if (enemy.definition.summonEnemyId == null
            || enemy.definition.summonEnemyId.isBlank()
            || enemy.definition.summonCount <= 0
            || enemy.definition.summonIntervalSeconds <= 0.0
            || enemy.specialCooldownRemaining > 0.0) {
            return false;
        }
        EnemyDefinition summonDefinition = context.enemyById.get(enemy.definition.summonEnemyId);
        if (summonDefinition == null) {
            return false;
        }
        for (int i = 0; i < enemy.definition.summonCount; i++) {
            double offset = enemy.definition.summonProgressOffset + (i * 0.15);
            EnemyInstance summonedEnemy = this.spawnSummonedEnemy(context, summonDefinition, enemy.laneId, Math.max(0.0, enemy.progress - offset));
            if (summonedEnemy != null) {
                summonedEnemy.summonerInstanceId = enemy.instanceId;
                summoned.add(summonedEnemy);
            }
        }
        enemy.specialCooldownRemaining = enemy.definition.summonIntervalSeconds;
        return false;
    }

    private boolean handleNecroKingAbility(World world, MatchContext context, EnemyInstance enemy, List<EnemyInstance> summoned) {
        if (enemy.definition.summonIntervalSeconds <= 0.0 || enemy.specialCooldownRemaining > 0.0) {
            return false;
        }
        if (this.hasLivingLinkedSummons(context, enemy.instanceId)) {
            return false;
        }
        EnemyDefinition guardianDefinition = context.enemyById.get(ENEMY_NECRO_GUARDIAN);
        if (guardianDefinition == null) {
            return false;
        }
        return this.beginBossCast(world, context, enemy, BOSS_CAST_NECRO_KING_RITUAL, BOSS_CAST_NECRO_KING_SECONDS);
    }

    private boolean hasLivingLinkedSummons(MatchContext context, long summonerInstanceId) {
        if (context == null || summonerInstanceId <= 0L) {
            return false;
        }
        for (EnemyInstance enemy : context.enemies) {
            if (enemy.dying || enemy.summonerInstanceId != summonerInstanceId) {
                continue;
            }
            return true;
        }
        return false;
    }

    private boolean handleBossDisablePulse(World world, MatchContext context, EnemyInstance enemy) {
        boolean vaultBreaker = enemy != null && "vault_breaker".equals(enemy.definition.id);
        if (enemy.definition.towerDisablePulseRadius <= 0.0
            || enemy.definition.towerDisablePulseDuration <= 0.0
            || enemy.definition.towerDisablePulsePercent <= 0.0
            || enemy.definition.towerDisablePulseIntervalSeconds <= 0.0
            || (!vaultBreaker && enemy.definition.towerDisablePulseMaxTriggers > 0 && enemy.remainingDisablePulses <= 0)
            || enemy.specialCooldownRemaining > 0.0) {
            return false;
        }
        List<TowerInstance> towers = vaultBreaker
            ? new ArrayList<>(context.placedTowers.values())
            : this.findTowersInRadius(context, this.enemyWorldPosition(context, enemy), enemy.definition.towerDisablePulseRadius);
        if (towers.isEmpty()) {
            enemy.specialCooldownRemaining = vaultBreaker
                ? VAULT_BREAKER_DISABLE_INTERVAL_SECONDS
                : Math.max(1.0, enemy.definition.towerDisablePulseIntervalSeconds * 0.5);
            return false;
        }
        return this.beginBossCast(world, context, enemy, BOSS_CAST_VAULT_BREAKER_DISABLE, BOSS_CAST_VAULT_BREAKER_SECONDS);
    }

    private boolean beginBossCast(
        World world,
        MatchContext context,
        EnemyInstance enemy,
        String abilityId,
        double durationSeconds
    ) {
        if (enemy == null || !enemy.definition.bossEnemy || enemy.isBossCasting()) {
            return false;
        }
        enemy.castAbilityId = abilityId == null ? "" : abilityId;
        enemy.castTotal = Math.max(0.2, durationSeconds);
        enemy.castRemaining = enemy.castTotal;
        enemy.currentMovementAnimationId = "";
        this.emitBossCastStartFeedback(world, context, enemy);
        return true;
    }

    private void resolveBossCastAbility(World world, MatchContext context, EnemyInstance enemy, List<EnemyInstance> summoned) {
        if (enemy == null) {
            return;
        }
        String abilityId = enemy.castAbilityId == null ? "" : enemy.castAbilityId;
        enemy.castAbilityId = "";
        enemy.castRemaining = 0.0;
        enemy.castTotal = 0.0;
        enemy.currentMovementAnimationId = "";
        switch (abilityId) {
            case BOSS_CAST_VAULT_BREAKER_DISABLE -> this.resolveVaultBreakerDisablePulse(world, context, enemy);
            case BOSS_CAST_NECRO_KING_RITUAL -> this.resolveNecroKingAbility(world, context, enemy, summoned);
            case BOSS_CAST_GOBLIN_SABOTAGE -> this.resolveGoblinBossSabotage(world, context, enemy);
            default -> {
            }
        }
        this.emitBossCastReleaseFeedback(world, context, enemy);
    }

    private void resolveNecroKingAbility(World world, MatchContext context, EnemyInstance enemy, List<EnemyInstance> summoned) {
        EnemyDefinition guardianDefinition = context.enemyById.get(ENEMY_NECRO_GUARDIAN);
        if (guardianDefinition == null) {
            return;
        }
        int spawned = 0;
        List<String> lanes = this.availableLaneIds(context);
        if (lanes.isEmpty()) {
            lanes = List.of(enemy.laneId);
        }
        Collections.shuffle(lanes, ThreadLocalRandom.current());
        String difficultyId = context.difficulty == null ? DIFFICULTY_NORMAL : context.difficulty.id;
        int guardianCount = switch (difficultyId) {
            case DIFFICULTY_EASY -> Math.max(5, enemy.definition.summonCount);
            case DIFFICULTY_HARD -> Math.max(8, enemy.definition.summonCount * 2);
            case DIFFICULTY_NIGHTMARE -> Math.max(10, enemy.definition.summonCount * 2);
            default -> Math.max(6, enemy.definition.summonCount + 1);
        };
        double maxProgress = Math.max(2.5, enemy.progress - 0.8);
        for (int i = 0; i < guardianCount; i++) {
            String laneId = lanes.get(i % lanes.size());
            double fraction = 0.10 + (0.80 * ((i + 0.5) / guardianCount));
            double jitter = ThreadLocalRandom.current().nextDouble(-1.1, 1.1);
            double progress = Math.max(0.0, maxProgress * fraction + jitter);
            EnemyInstance guardian = this.spawnSummonedEnemy(context, guardianDefinition, laneId, progress);
            if (guardian == null) {
                continue;
            }
            guardian.summonerInstanceId = enemy.instanceId;
            summoned.add(guardian);
            spawned++;
        }
        if (spawned > 0) {
            enemy.linkedSummonPhaseActive = true;
            this.playSound3d(world, SOUND_BOSS_CAST_NECRO_KING, this.enemyWorldPosition(context, enemy));
            this.showEventToast(
                context,
                this.choose(world, "Некрокороль призвал стражей бездны. Пока они живы, он неуязвим.", "The Necro King summoned abyss guardians. While they live, he is invulnerable."),
                EVENT_TOAST_ICON_ALERT
            );
            this.sendLocalizedWorldMessage(world, "Некрокороль поднял костяных стражей. Пока они живы, он неуязвим!", "The Necro King raised ritual guardians. While they live, he is invulnerable!");
            enemy.specialCooldownRemaining = enemy.definition.summonIntervalSeconds;
        }
    }

    private void resolveVaultBreakerDisablePulse(World world, MatchContext context, EnemyInstance enemy) {
        boolean vaultBreaker = enemy != null && "vault_breaker".equals(enemy.definition.id);
        List<TowerInstance> towers = vaultBreaker
            ? new ArrayList<>(context.placedTowers.values())
            : this.findTowersInRadius(context, this.enemyWorldPosition(context, enemy), enemy.definition.towerDisablePulseRadius);
        if (towers.isEmpty()) {
            enemy.specialCooldownRemaining = vaultBreaker
                ? VAULT_BREAKER_DISABLE_INTERVAL_SECONDS
                : Math.max(1.0, enemy.definition.towerDisablePulseIntervalSeconds * 0.5);
            return;
        }
        if (!vaultBreaker) {
            Collections.shuffle(towers, ThreadLocalRandom.current());
        }
        int disableCount = vaultBreaker
            ? towers.size()
            : Math.max(1, (int)Math.ceil(towers.size() * enemy.definition.towerDisablePulsePercent));
        double disableDuration = vaultBreaker
            ? VAULT_BREAKER_DISABLE_DURATION_SECONDS
            : enemy.definition.towerDisablePulseDuration;
        for (int i = 0; i < disableCount && i < towers.size(); i++) {
            this.applyTowerDisable(world, towers.get(i), disableDuration, this.rgb(255, 112, 64), 1.05);
        }
        this.playSound3d(world, SOUND_BOSS_CAST_VAULT_BREAKER, this.enemyWorldPosition(context, enemy));
        this.showEventToast(
            context,
            vaultBreaker
                ? this.choose(world, "Разоритель дупла отключил все башни на 3 секунды.", "The Hollow Ravager disabled all towers for 3 seconds.")
                : this.enemyDisplayName(world, enemy.definition) + this.choose(world, " отключил башни возле себя.", " disabled towers near itself."),
            EVENT_TOAST_ICON_ALERT
        );
        this.sendLocalizedWorldMessage(
            world,
            vaultBreaker
                ? "Разоритель дупла отключил все башни на 3 секунды!"
                : "Разоритель дупла отключил часть башен поблизости!",
            vaultBreaker
                ? "The Hollow Ravager disabled all towers for 3 seconds!"
                : "The Hollow Ravager disabled some nearby towers!"
        );
        if (!vaultBreaker && enemy.definition.towerDisablePulseMaxTriggers > 0) {
            enemy.remainingDisablePulses = Math.max(0, enemy.remainingDisablePulses - 1);
        }
        enemy.specialCooldownRemaining = vaultBreaker
            ? VAULT_BREAKER_DISABLE_INTERVAL_SECONDS
            : enemy.definition.towerDisablePulseIntervalSeconds;
    }

    private void emitBossCastStartFeedback(World world, MatchContext context, EnemyInstance enemy) {
        this.emitBossCastFeedback(world, context, enemy, 1.15f);
    }

    private void emitBossCastReleaseFeedback(World world, MatchContext context, EnemyInstance enemy) {
        this.emitBossCastFeedback(world, context, enemy, 1.45f);
    }

    private void emitBossCastFeedback(World world, MatchContext context, EnemyInstance enemy, float scaleMultiplier) {
        if (world == null || context == null || enemy == null) {
            return;
        }
        List<Ref<EntityStore>> playerRefs = this.collectPlayerEntityRefs(world);
        if (playerRefs.isEmpty()) {
            return;
        }
        Store<EntityStore> store = world.getEntityStore().getStore();
        Color color = this.enemyDeathColor(enemy.definition.id);
        Vector3d center = this.enemyWorldPosition(context, enemy);
        Vector3d upper = new Vector3d(center.x, center.y + 1.05, center.z);
        this.spawnColoredParticle(store, playerRefs, PARTICLE_IMPACT, upper, new Vector3f(0.0f, 0.0f, 0.0f), scaleMultiplier, color);
        this.spawnColoredParticle(store, playerRefs, PARTICLE_SHOT, upper, new Vector3f(0.0f, 0.0f, 0.0f), scaleMultiplier * 1.18f, color);
        this.spawnParticleBurst(store, playerRefs, upper, 0.30 * scaleMultiplier, 7, 0.06);
    }

    private EnemyInstance spawnSummonedEnemy(MatchContext context, EnemyDefinition definition, String laneId, double progress) {
        String resolvedLaneId = canonicalLaneId(laneId);
        ScheduledSpawn summonSpawn = this.createAbilitySpawn(context, definition, resolvedLaneId);
        EnemyInstance summoned = new EnemyInstance(context.nextEnemyId++, summonSpawn);
        this.initializeSpecialEnemyState(context, summoned);
        summoned.progress = Math.max(0.0, Math.min(progress, Math.max(0.0, this.routeLengthForLane(context, resolvedLaneId) - 0.2)));
        summoned.visualRotation = new Vector3f(0.0f, 0.0f, 0.0f);
        return summoned;
    }

    private ScheduledSpawn createAbilitySpawn(MatchContext context, EnemyDefinition enemy, String laneId) {
        int waveNumber = context.activeWaveNumber > 0 ? context.activeWaveNumber : Math.max(1, context.state.currentWave);
        int highestConfiguredWave = this.highestConfiguredWave(context);
        int endlessOffset = context.snapshot.gameRules.endlessMode ? Math.max(0, waveNumber - highestConfiguredWave) : 0;
        double hpScale = this.endlessMultiplier(context.snapshot.gameRules.endlessEnemyHpScalePerWave, endlessOffset)
            * this.waveEnemyHpMultiplier(context, waveNumber, enemy);
        double rewardScale = this.endlessMultiplier(context.snapshot.gameRules.endlessEnemyRewardScalePerWave, endlessOffset);
        double speedScale = this.waveEnemySpeedMultiplier(context, waveNumber, enemy)
            * this.contractEnemySpeedMultiplier(context, enemy)
            * (context.difficulty == null ? 1.0 : Math.max(0.1, context.difficulty.enemySpeedMultiplier));
        speedScale *= this.sealCurseEnemySpeedMultiplier(context);
        double flatDamageReduction = Math.max(0.0, enemy.flatDamageReduction + this.waveEnemyFlatDamageReduction(context, waveNumber, enemy));
        double damageReductionPercent = Math.max(0.0, Math.min(0.90, enemy.damageReductionPercent + this.waveEnemyDamageReductionPercent(context, waveNumber, enemy)));
        double slowResistancePercent = enemy.slowImmune
            ? 1.0
            : Math.max(0.0, Math.min(0.95, enemy.slowResistancePercent + this.waveEnemySlowResistance(context, waveNumber, enemy)));
        if (enemy.bossEnemy) {
            double defenseScale = this.bossDefenseScale(context, this.pressureWaveNumber(context, waveNumber), enemy.id);
            flatDamageReduction *= defenseScale;
            damageReductionPercent *= defenseScale;
            slowResistancePercent = enemy.slowImmune
                ? 1.0
                : Math.max(0.0, Math.min(0.95, slowResistancePercent * Math.min(1.05, defenseScale + 0.10)));
        }
        if (context.activeContract != null) {
            hpScale *= Math.max(0.1, context.activeContract.enemyHpMultiplier);
            rewardScale *= Math.max(0.1, context.activeContract.matchIncomeMultiplier);
        }
        if (context.difficulty != null) {
            hpScale *= Math.max(0.1, context.difficulty.enemyHpMultiplier);
            rewardScale *= Math.max(0.1, context.difficulty.rewardMultiplier);
        }
        return new ScheduledSpawn(
            enemy,
            Math.max(1.0, enemy.maxHp * hpScale),
            Math.max(0.05, enemy.speed * speedScale),
            Math.max(enemy.reward, (int)Math.round(enemy.reward * rewardScale)),
            enemy.leakDamage,
            waveNumber,
            flatDamageReduction,
            damageReductionPercent,
            slowResistancePercent,
            0.0,
            laneId
        );
    }

    private List<TowerInstance> findTowersInRadius(MatchContext context, Vector3d position, double radius) {
        List<TowerInstance> towers = new ArrayList<>();
        double radiusSquared = radius * radius;
        for (TowerInstance tower : context.placedTowers.values()) {
            Vector3d towerPosition = this.towerWorldPosition(tower.slot);
            double dx = towerPosition.x - position.x;
            double dy = towerPosition.y - position.y;
            double dz = towerPosition.z - position.z;
            if ((dx * dx) + (dy * dy) + (dz * dz) <= radiusSquared) {
                towers.add(tower);
            }
        }
        towers.sort(Comparator.comparingDouble(tower -> {
            Vector3d towerPosition = this.towerWorldPosition(tower.slot);
            double dx = towerPosition.x - position.x;
            double dy = towerPosition.y - position.y;
            double dz = towerPosition.z - position.z;
            return (dx * dx) + (dy * dy) + (dz * dz);
        }));
        return towers;
    }

    private void applyTowerDisable(World world, TowerInstance tower, double duration, Color color, double scale) {
        tower.disabledRemaining = Math.max(tower.disabledRemaining, duration);
        tower.cooldownRemaining = Math.max(tower.cooldownRemaining, 0.25);
        tower.disableFxCooldownRemaining = 0.0;
        Vector3d towerPosition = this.towerWorldPosition(tower.slot);
        List<Ref<EntityStore>> playerRefs = this.collectPlayerEntityRefs(world);
        if (playerRefs.isEmpty()) {
            return;
        }
        Store<EntityStore> store = world.getEntityStore().getStore();
        Vector3d burstPosition = new Vector3d(towerPosition.x, towerPosition.y + 1.0, towerPosition.z);
        this.spawnColoredParticle(store, playerRefs, PARTICLE_IMPACT, burstPosition, new Vector3f(0.0f, 0.0f, 0.0f), (float)scale, color);
        this.spawnParticleBurst(store, playerRefs, burstPosition, 0.28 * scale, 7, 0.12);
    }

    private void emitDisabledTowerFeedback(World world, TowerInstance tower) {
        List<Ref<EntityStore>> playerRefs = this.collectPlayerEntityRefs(world);
        if (playerRefs.isEmpty()) {
            return;
        }
        Store<EntityStore> store = world.getEntityStore().getStore();
        Vector3d towerPosition = this.towerWorldPosition(tower.slot);
        Vector3d burstPosition = new Vector3d(towerPosition.x, towerPosition.y + 1.0, towerPosition.z);
        Color color = this.rgb(255, 112, 64);
        this.spawnColoredParticle(store, playerRefs, PARTICLE_IMPACT, burstPosition, new Vector3f(0.0f, 0.0f, 0.0f), 0.72f, color);
        this.spawnParticleBurst(store, playerRefs, burstPosition, 0.18, 4, 0.08);
    }

    private void emitSuperTowerAura(World world, TowerInstance tower) {
        if (tower == null || tower.definition == null || !tower.definition.superTower) {
            return;
        }
        List<Ref<EntityStore>> playerRefs = this.collectPlayerEntityRefs(world);
        if (playerRefs.isEmpty()) {
            return;
        }
        Store<EntityStore> store = world.getEntityStore().getStore();
        Vector3d towerPosition = this.towerWorldPosition(tower.slot);
        Vector3d glowPosition = new Vector3d(towerPosition.x, towerPosition.y + 1.55, towerPosition.z);
        Color color;
        float scale;
        switch (tower.definition.id) {
            case "storm_monolith" -> {
                color = this.rgb(96, 170, 255);
                scale = 1.18f;
            }
            case "seed_idol" -> {
                color = this.rgb(255, 224, 122);
                scale = 1.08f;
            }
            default -> {
                color = this.rgb(120, 255, 194);
                scale = 1.12f;
            }
        }
        this.spawnColoredParticle(store, playerRefs, PARTICLE_SHOT, glowPosition, new Vector3f(0.0f, 0.0f, 0.0f), scale, color);
        this.spawnColoredParticle(store, playerRefs, PARTICLE_IMPACT, new Vector3d(glowPosition.x, glowPosition.y - 0.3, glowPosition.z), new Vector3f(0.0f, 0.0f, 0.0f), scale * 0.78f, color);
    }

    private double enemyFireRateMultiplier(MatchContext context, Vector3d towerPosition) {
        double multiplier = 1.0;
        if (context == null || !context.hasEnemyTowerFireRateAuras) {
            return multiplier;
        }
        for (EnemyInstance enemy : context.enemies) {
            if (enemy.dying || enemy.definition.towerFireRateSlowPercent <= 0.0 || enemy.definition.towerFireRateSlowRadius <= 0.0) {
                continue;
            }
            Vector3d enemyPosition = this.enemyWorldPosition(context, enemy);
            double dx = enemyPosition.x - towerPosition.x;
            double dy = enemyPosition.y - towerPosition.y;
            double dz = enemyPosition.z - towerPosition.z;
            double radius = enemy.definition.towerFireRateSlowRadius * this.contractAuraRadiusMultiplier(context, enemy.definition);
            if ((dx * dx) + (dy * dy) + (dz * dz) > radius * radius) {
                continue;
            }
            multiplier = Math.min(
                multiplier,
                Math.max(0.25, 1.0 - enemy.definition.towerFireRateSlowPercent * this.contractAuraEffectMultiplier(context, enemy.definition))
            );
        }
        return multiplier;
    }

    private double enemyRangeMultiplier(MatchContext context, Vector3d towerPosition) {
        double multiplier = 1.0;
        if (context == null || !context.hasEnemyTowerRangeAuras) {
            return multiplier;
        }
        for (EnemyInstance enemy : context.enemies) {
            if (enemy.dying || enemy.definition.towerRangeSlowPercent <= 0.0 || enemy.definition.towerRangeSlowRadius <= 0.0) {
                continue;
            }
            Vector3d enemyPosition = this.enemyWorldPosition(context, enemy);
            double dx = enemyPosition.x - towerPosition.x;
            double dy = enemyPosition.y - towerPosition.y;
            double dz = enemyPosition.z - towerPosition.z;
            double radius = enemy.definition.towerRangeSlowRadius * this.contractAuraRadiusMultiplier(context, enemy.definition);
            if ((dx * dx) + (dy * dy) + (dz * dz) > radius * radius) {
                continue;
            }
            multiplier = Math.min(
                multiplier,
                Math.max(0.35, 1.0 - enemy.definition.towerRangeSlowPercent * this.contractAuraEffectMultiplier(context, enemy.definition))
            );
        }
        return multiplier;
    }

    private double enemyMovementMultiplier(MatchContext context, EnemyInstance target) {
        double multiplier = 1.0;
        Vector3d targetPosition = this.enemyWorldPosition(context, target);
        if (context != null && context.hasEnemyAllySpeedAuras) {
            for (EnemyInstance enemy : context.enemies) {
                if (enemy == target
                    || enemy.dying
                    || enemy.definition.allySpeedAuraPercent <= 0.0
                    || enemy.definition.allySpeedAuraRadius <= 0.0) {
                    continue;
                }
                Vector3d sourcePosition = this.enemyWorldPosition(context, enemy);
                double dx = sourcePosition.x - targetPosition.x;
                double dy = sourcePosition.y - targetPosition.y;
                double dz = sourcePosition.z - targetPosition.z;
                double radius = enemy.definition.allySpeedAuraRadius * this.contractAuraRadiusMultiplier(context, enemy.definition);
                if ((dx * dx) + (dy * dy) + (dz * dz) > radius * radius) {
                    continue;
                }
                multiplier = Math.max(
                    multiplier,
                    1.0 + enemy.definition.allySpeedAuraPercent * this.contractAuraEffectMultiplier(context, enemy.definition)
                );
            }
        }
        if (target.definition.berserkHpThreshold > 0.0
            && target.definition.berserkSpeedMultiplier > 1.0
            && target.hp <= target.maxHp * target.definition.berserkHpThreshold) {
            multiplier = Math.max(multiplier, target.definition.berserkSpeedMultiplier);
        }
        if ((ENEMY_RIFT_TWIN_ALPHA.equals(target.definition.id) || ENEMY_RIFT_TWIN_BETA.equals(target.definition.id))
            && target.linkedBossInstanceId > 0L) {
            EnemyInstance linkedTwin = this.findEnemyByInstanceId(context, target.linkedBossInstanceId);
            if (linkedTwin == null || linkedTwin.dying) {
                multiplier = Math.max(multiplier, 1.25);
            }
        }
        if (ENEMY_SEAL_MASTER.equals(target.definition.id) && target.variantStage >= 2) {
            multiplier = Math.max(multiplier, DUO_SEAL_MASTER_SPEED_MULTIPLIER);
        }
        return multiplier;
    }

    private double adjustedDamageAgainstEnemy(MatchContext context, EnemyInstance enemy, double damage) {
        if (enemy != null && enemy.isBossCasting()) {
            return 0.0;
        }
        if (enemy != null
            && (ENEMY_RIFT_TWIN_ALPHA.equals(enemy.definition.id) || ENEMY_RIFT_TWIN_BETA.equals(enemy.definition.id))
            && !enemy.damageableTwin) {
            EnemyInstance linkedTwin = enemy.linkedBossInstanceId > 0L
                ? this.findEnemyByInstanceId(context, enemy.linkedBossInstanceId)
                : null;
            if (linkedTwin != null && !linkedTwin.dying) {
                return 0.0;
            }
        }
        if ("necro_king".equals(enemy.definition.id) && this.hasLivingLinkedSummons(context, enemy.instanceId)) {
            return 0.0;
        }
        double adjustedDamage = Math.max(0.0, damage);
        double effectiveFlatReduction = Math.max(0.0, enemy.flatDamageReduction - enemy.armorBreakFlatReduction);
        adjustedDamage = Math.max(0.0, adjustedDamage - effectiveFlatReduction);
        double auraMultiplier = 1.0;
        if (context != null && context.hasEnemyDamageReductionAuras) {
            Vector3d targetPosition = this.enemyWorldPosition(context, enemy);
            for (EnemyInstance source : context.enemies) {
                if (source == enemy
                    || source.dying
                    || source.definition.allyDamageReductionAuraPercent <= 0.0
                    || source.definition.allyDamageReductionAuraRadius <= 0.0) {
                    continue;
                }
                Vector3d sourcePosition = this.enemyWorldPosition(context, source);
                double dx = sourcePosition.x - targetPosition.x;
                double dy = sourcePosition.y - targetPosition.y;
                double dz = sourcePosition.z - targetPosition.z;
                double radius = source.definition.allyDamageReductionAuraRadius * this.contractAuraRadiusMultiplier(context, source.definition);
                if ((dx * dx) + (dy * dy) + (dz * dz) > radius * radius) {
                    continue;
                }
                auraMultiplier = Math.min(
                    auraMultiplier,
                    Math.max(0.35, 1.0 - source.definition.allyDamageReductionAuraPercent * this.contractAuraEffectMultiplier(context, source.definition))
                );
            }
        }
        double effectivePercentReduction = Math.max(0.0, Math.min(0.92, enemy.damageReductionPercent - enemy.armorBreakPercentReduction));
        double reductionMultiplier = 1.0 - effectivePercentReduction;
        return Math.max(0.0, adjustedDamage * reductionMultiplier * auraMultiplier);
    }

    private void refreshEnemyAuraFlags(MatchContext context) {
        if (context == null) {
            return;
        }
        context.hasEnemyRegenAuras = false;
        context.hasEnemyAllySpeedAuras = false;
        context.hasEnemyTowerFireRateAuras = false;
        context.hasEnemyTowerRangeAuras = false;
        context.hasEnemyDamageReductionAuras = false;
        for (EnemyInstance enemy : context.enemies) {
            if (enemy == null || enemy.dying || enemy.definition == null) {
                continue;
            }
            if (!context.hasEnemyRegenAuras
                && enemy.definition.regenPercentPerSecond > 0.0
                && enemy.definition.regenAuraRadius > 0.0) {
                context.hasEnemyRegenAuras = true;
            }
            if (!context.hasEnemyAllySpeedAuras
                && enemy.definition.allySpeedAuraPercent > 0.0
                && enemy.definition.allySpeedAuraRadius > 0.0) {
                context.hasEnemyAllySpeedAuras = true;
            }
            if (!context.hasEnemyTowerFireRateAuras
                && enemy.definition.towerFireRateSlowPercent > 0.0
                && enemy.definition.towerFireRateSlowRadius > 0.0) {
                context.hasEnemyTowerFireRateAuras = true;
            }
            if (!context.hasEnemyTowerRangeAuras
                && enemy.definition.towerRangeSlowPercent > 0.0
                && enemy.definition.towerRangeSlowRadius > 0.0) {
                context.hasEnemyTowerRangeAuras = true;
            }
            if (!context.hasEnemyDamageReductionAuras
                && enemy.definition.allyDamageReductionAuraPercent > 0.0
                && enemy.definition.allyDamageReductionAuraRadius > 0.0) {
                context.hasEnemyDamageReductionAuras = true;
            }
            if (context.hasEnemyRegenAuras
                && context.hasEnemyAllySpeedAuras
                && context.hasEnemyTowerFireRateAuras
                && context.hasEnemyTowerRangeAuras
                && context.hasEnemyDamageReductionAuras) {
                return;
            }
        }
    }

    private void emitShotFeedback(World world, TowerInstance tower, Vector3d from, Vector3d to) {
        this.playSound3d(world, this.fireSoundForTower(tower.definition.id), from);
        Store<EntityStore> store = world.getEntityStore().getStore();
        List<Ref<EntityStore>> playerRefs = this.collectPlayerEntityRefs(world);
        if (playerRefs.isEmpty()) {
            return;
        }
        Vector3d muzzle = new Vector3d(from.x, from.y + 1.05, from.z);
        Vector3d hit = new Vector3d(to.x, to.y + 0.85, to.z);
        Vector3f shotRotation = this.rotationTowards(muzzle, hit);
        Color tracerColor = this.towerTracerColor(tower.definition.id);
        float tracerScale = this.towerTracerScale(tower.definition.id);
        float impactScale = this.towerImpactScale(tower.definition.id);
        String shotParticleId = this.shotParticleId(tower.definition.id);
        String impactParticleId = this.impactParticleId(tower.definition.id);
        if ("freeze_gate".equals(tower.definition.id)) {
            this.spawnColoredParticle(store, playerRefs, shotParticleId, muzzle, shotRotation, 0.45f, tracerColor);
        } else if (this.useModelProjectile(tower.definition.id)) {
            this.spawnProjectileVisual(world, tower, muzzle, hit);
        } else {
            this.emitLinearTracer(store, playerRefs, shotParticleId, tower.definition.id, muzzle, hit, shotRotation, 0.36, 1.18f);
        }
        this.spawnColoredParticle(store, playerRefs, shotParticleId, muzzle, shotRotation, tracerScale * 1.05f, tracerColor);
        this.spawnColoredParticle(store, playerRefs, impactParticleId, hit, shotRotation, impactScale, tracerColor);
        this.spawnColoredParticle(store, playerRefs, shotParticleId, hit, shotRotation, impactScale * 0.55f, tracerColor);
        if ("shock_relay".equals(tower.definition.id)) {
            this.spawnParticleBurst(store, playerRefs, hit, 0.04, 1, 0.03);
        } else if (!"freeze_gate".equals(tower.definition.id)) {
            this.spawnParticleBurst(store, playerRefs, hit, 0.18, 4, 0.10);
        }
    }

    private void emitEnemyDeathFeedback(World world, EnemyInstance enemy, Vector3d position) {
        this.playSound3d(world, this.enemyDeathSoundFor(enemy), position);
        List<Ref<EntityStore>> playerRefs = this.collectPlayerEntityRefs(world);
        if (playerRefs.isEmpty()) {
            return;
        }
        Store<EntityStore> store = world.getEntityStore().getStore();
        Color deathColor = this.enemyDeathColor(enemy.definition.id);
        float deathScale = enemy.definition.bossEnemy ? 2.6f : 1.7f;
        Vector3d torso = new Vector3d(position.x, position.y + 0.85, position.z);
        this.spawnColoredParticle(store, playerRefs, PARTICLE_IMPACT, torso, new Vector3f(0.0f, 0.0f, 0.0f), deathScale, deathColor);
        this.spawnColoredParticle(store, playerRefs, PARTICLE_SHOT, torso, new Vector3f(0.0f, 0.0f, 0.0f), deathScale * 1.25f, deathColor);
        this.spawnParticleBurst(store, playerRefs, torso, enemy.definition.bossEnemy ? 0.85 : 0.45, enemy.definition.bossEnemy ? 12 : 8, 0.18);
        this.spawnParticleBurst(store, playerRefs, new Vector3d(position.x, position.y + 0.35, position.z), 0.36, 7, 0.10);
    }

    private void emitLeakFeedback(World world, MatchContext context, EnemyInstance enemy) {
        Vector3d alertPosition = context.snapshot.map.vaultPoint == null
            ? this.enemyWorldPosition(context, enemy)
            : this.objectiveWorldPosition(context.snapshot.map.vaultPoint, 0.05);
        this.playSound3d(world, SOUND_LEAK, alertPosition);
        this.playWorldUiSound(world, SOUND_LEAK);
        List<Ref<EntityStore>> playerRefs = this.collectPlayerEntityRefs(world);
        if (!playerRefs.isEmpty()) {
            this.spawnParticleBurst(world.getEntityStore().getStore(), playerRefs, alertPosition, 0.35, 6, 0.15);
        }
    }

    private void beginEnemyDeath(World world, MatchContext context, EnemyInstance enemy, Vector3d position) {
        if (enemy.dying) {
            return;
        }
        if (this.resolveNodeArbiterRespawn(world, context, enemy, position)) {
            return;
        }
        if ((ENEMY_RIFT_TWIN_ALPHA.equals(enemy.definition.id) || ENEMY_RIFT_TWIN_BETA.equals(enemy.definition.id))
            && enemy.linkedBossInstanceId > 0L) {
            EnemyInstance linkedTwin = this.findEnemyByInstanceId(context, enemy.linkedBossInstanceId);
            if (linkedTwin != null && !linkedTwin.dying) {
                linkedTwin.damageableTwin = true;
                linkedTwin.specialCooldownRemaining = Math.max(linkedTwin.specialCooldownRemaining, DUO_RIFT_TWIN_SWAP_SECONDS);
                this.playSound3d(world, SOUND_RIFT_TWIN_ENRAGE, this.enemyWorldPosition(context, linkedTwin));
            }
        }
        if (ENEMY_SEAL_MASTER.equals(enemy.definition.id)) {
            this.applySealMasterDeathCurses(world, context, enemy);
        }
        if (context.tutorialActive
            && enemy.waveNumber == TUTORIAL_TRAP_WAVE_NUMBER
            && enemy.tutorialTrapSequenceIndex >= 0
            && enemy.tutorialTrapSequenceIndex < 2) {
            this.queueNextTutorialTrapEnemy(context, enemy.tutorialTrapSequenceIndex + 1);
        }
        this.trackRunEnemyKill(context);
        enemy.dying = true;
        enemy.hp = 0.0;
        enemy.dyingRemaining = enemy.definition.bossEnemy ? BOSS_DEATH_LINGER_SECONDS : ENEMY_DEATH_LINGER_SECONDS;
        if (enemy.reward > 0) {
            this.grantMatchIncome(world, context, enemy.reward, position);
        }
        this.emitEnemyDeathFeedback(world, enemy, position);
    }

    private boolean resolveNodeArbiterRespawn(World world, MatchContext context, EnemyInstance enemy, Vector3d position) {
        if (enemy == null || !ENEMY_NODE_ARBITER.equals(enemy.definition.id) || enemy.extraLivesRemaining <= 0) {
            return false;
        }
        this.emitEnemyDeathFeedback(world, enemy, position);
        this.playSound3d(world, SOUND_NODE_ARBITER_LIFE_BREAK, position);
        enemy.extraLivesRemaining = Math.max(0, enemy.extraLivesRemaining - 1);
        enemy.variantStage = Math.min(3, enemy.variantStage + 1);
        double currentLaneLength = Math.max(0.01, this.routeLengthForLane(context, enemy.laneId));
        double progressRatio = enemy.progress / currentLaneLength;
        boolean onFinalLane = progressRatio >= DUO_NODE_ARBITER_FINAL_LANE_THRESHOLD;
        if (onFinalLane) {
            enemy.progress = Math.min(currentLaneLength - 0.2, enemy.progress + DUO_NODE_ARBITER_FINAL_RESPAWN_PROGRESS);
        } else {
            String previousLane = canonicalLaneId(enemy.laneId);
            String nextLane = "a".equals(previousLane) ? "c" : "a";
            double nextLaneLength = Math.max(0.01, this.routeLengthForLane(context, nextLane));
            enemy.laneId = nextLane;
            enemy.progress = Math.max(0.0, Math.min(nextLaneLength - 0.2, progressRatio * nextLaneLength));
        }
        enemy.hp = enemy.maxHp;
        enemy.slowPercent = 0.0;
        enemy.slowRemaining = 0.0;
        enemy.rootedRemaining = 0.0;
        enemy.specialCooldownRemaining = 1.6;
        enemy.castAbilityId = "";
        enemy.castRemaining = 0.0;
        enemy.castTotal = 0.0;
        enemy.currentMovementAnimationId = "";
        enemy.visualRotation = this.rotationTowards(
            this.pointAtProgress(context, enemy.laneId, Math.max(0.0, enemy.progress - 0.6)).toWorldVector(0.1),
            this.pointAtProgress(context, enemy.laneId, enemy.progress).toWorldVector(0.1)
        );
        this.playSound3d(world, SOUND_NODE_ARBITER_RESPAWN, this.enemyWorldPosition(context, enemy));
        String lifeLabel = this.romanLifeLabel(enemy.extraLivesRemaining + 1);
        String message = onFinalLane
            ? this.choose(world, "Арбитр Узлов возрождается ближе к дуплу. Форма ", "The Node Arbiter reforms closer to the Hollow. Form ")
            : this.choose(world, "Арбитр Узлов перескакивает на другой фронт. Форма ", "The Node Arbiter jumps to the other front. Form ");
        this.showEventToast(context, message + lifeLabel + ".", EVENT_TOAST_ICON_ALERT);
        this.sendLocalizedWorldMessage(world, viewerRef ->
            (onFinalLane
                ? this.choose(viewerRef, "Арбитр Узлов возрождается ближе к дуплу. Форма ", "The Node Arbiter reforms closer to the Hollow. Form ")
                : this.choose(viewerRef, "Арбитр Узлов перескакивает на другой фронт. Форма ", "The Node Arbiter jumps to the other front. Form "))
                + lifeLabel
                + "."
        );
        return true;
    }

    private String romanLifeLabel(int livesLeft) {
        return switch (Math.max(1, Math.min(4, livesLeft))) {
            case 4 -> "IV";
            case 3 -> "III";
            case 2 -> "II";
            default -> "I";
        };
    }

    private void spawnProjectileVisual(World world, TowerInstance tower, Vector3d from, Vector3d to) {
        PresentationState presentation = this.presentationsByWorld.get(this.worldKey(world));
        if (presentation == null || !presentation.enabled) {
            return;
        }
        while (presentation.projectileVisuals.size() >= MAX_PROJECTILE_VISUALS) {
            ProjectileVisual overflow = presentation.projectileVisuals.remove(0);
            this.removeVisualRef(world.getEntityStore().getStore(), overflow.ref);
        }

        Model model = this.resolveModel(this.projectileModelId(tower.definition.id), this.projectileScale(tower.definition.id));
        if (model == null) {
            return;
        }
        Vector3f rotation = this.rotationTowards(from, to);
        Ref<EntityStore> ref = this.createVisualEntity(world, model, from, rotation, null);
        if (ref == null) {
            return;
        }
        double durationSeconds = this.projectileDurationSeconds(tower.definition.id, from, to);
        presentation.projectileVisuals.add(
            new ProjectileVisual(
                ref,
                tower.definition.id,
                from,
                to,
                rotation,
                durationSeconds,
                "freeze_gate".equals(tower.definition.id)
            )
        );
    }

    private void tickProjectileVisuals(World world, PresentationState presentation, double deltaSeconds) {
        if (presentation.projectileVisuals.isEmpty()) {
            return;
        }
        Store<EntityStore> store = world.getEntityStore().getStore();
        List<Ref<EntityStore>> playerRefs = this.collectPlayerEntityRefs(world);
        List<ProjectileVisual> expired = new ArrayList<>();
        for (ProjectileVisual projectile : presentation.projectileVisuals) {
            if (projectile.ref == null || !projectile.ref.isValid()) {
                expired.add(projectile);
                continue;
            }
            Vector3d previousPosition = projectile.currentPosition;
            projectile.elapsedSeconds += deltaSeconds;
            double alpha = Math.min(1.0, projectile.elapsedSeconds / Math.max(0.01, projectile.durationSeconds));
            Vector3d nextPosition = this.lerp(projectile.from, projectile.to, alpha);
            this.updateTransform(store, projectile.ref, nextPosition, projectile.rotation);
            if (!playerRefs.isEmpty()) {
                if (projectile.trailing) {
                    this.spawnProjectileTrail(store, playerRefs, projectile, previousPosition, nextPosition);
                } else {
                    this.spawnColoredParticle(
                        store,
                        playerRefs,
                        this.shotParticleId(projectile.towerId),
                        nextPosition,
                        projectile.rotation,
                        this.projectileGlowScale(projectile.towerId),
                        this.towerTracerColor(projectile.towerId)
                    );
                }
            }
            projectile.currentPosition = nextPosition;
            if (alpha >= 1.0) {
                this.removeVisualRef(store, projectile.ref);
                expired.add(projectile);
            }
        }
        if (!expired.isEmpty()) {
            presentation.projectileVisuals.removeAll(expired);
        }
    }

    private void spawnProjectileTrail(
        Store<EntityStore> store,
        List<Ref<EntityStore>> playerRefs,
        ProjectileVisual projectile,
        Vector3d from,
        Vector3d to
    ) {
        Color color = this.towerTracerColor(projectile.towerId);
        float scale = this.projectileGlowScale(projectile.towerId);
        double dx = to.x - from.x;
        double dy = to.y - from.y;
        double dz = to.z - from.z;
        double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
        int segments = Math.max(1, (int)Math.ceil(distance / 0.45));
        for (int i = 0; i <= segments; i++) {
            double t = i / (double)Math.max(1, segments);
            Vector3d point = this.lerp(from, to, t);
            this.spawnColoredParticle(store, playerRefs, this.shotParticleId(projectile.towerId), point, projectile.rotation, scale, color);
        }
    }

    private void spawnColoredParticle(
        Store<EntityStore> store,
        List<Ref<EntityStore>> playerRefs,
        String particleId,
        Vector3d position,
        Vector3f rotation,
        float scale,
        Color color
    ) {
        if (particleId == null || particleId.isBlank()) {
            return;
        }
        ParticleUtil.spawnParticleEffect(
            particleId,
            position,
            rotation.getYaw(),
            rotation.getPitch(),
            rotation.getRoll(),
            scale,
            color,
            playerRefs,
            store
        );
    }

    private Color towerTracerColor(String towerId) {
        return switch (towerId) {
            case "guard_post" -> this.rgb(245, 214, 82);
            case "sniper_desk" -> this.rgb(255, 194, 112);
            case "rapid_security" -> this.rgb(245, 214, 82);
            case "freeze_gate" -> this.rgb(255, 255, 255);
            case "shock_relay" -> this.rgb(255, 236, 108);
            case "armor_drill" -> this.rgb(171, 124, 78);
            default -> this.rgb(255, 255, 255);
        };
    }

    private float towerTracerScale(String towerId) {
        return switch (towerId) {
            case "guard_post" -> 1.22f;
            case "sniper_desk" -> 1.9f;
            case "rapid_security" -> 0.82f;
            case "freeze_gate" -> 0.95f;
            case "shock_relay" -> 0.52f;
            case "armor_drill" -> 1.28f;
            default -> 1.35f;
        };
    }

    private float towerImpactScale(String towerId) {
        return switch (towerId) {
            case "guard_post" -> 1.65f;
            case "sniper_desk" -> 2.6f;
            case "rapid_security" -> 1.65f;
            case "freeze_gate" -> 1.55f;
            case "shock_relay" -> 0.72f;
            case "armor_drill" -> 2.35f;
            default -> 1.85f;
        };
    }

    private String projectileModelId(String towerId) {
        return switch (towerId) {
            case "guard_post" -> MODEL_PROJECTILE_ARROW_LIGHT;
            case "sniper_desk" -> MODEL_PROJECTILE_ARROW_HEAVY;
            case "rapid_security" -> MODEL_PROJECTILE_ARROW_LIGHT;
            case "freeze_gate" -> "Ice_Bolt";
            case "shock_relay" -> MODEL_WARP;
            case "armor_drill" -> MODEL_PROJECTILE_ROOT_DRILL;
            default -> MODEL_SPAWN;
        };
    }

    private float projectileScale(String towerId) {
        return switch (towerId) {
            case "guard_post" -> 0.46f;
            case "sniper_desk" -> 0.54f;
            case "rapid_security" -> 0.46f;
            case "freeze_gate" -> 0.34f;
            case "shock_relay" -> 0.46f;
            case "armor_drill" -> 0.62f;
            default -> 0.26f;
        };
    }

    private double projectileDurationSeconds(String towerId, Vector3d from, Vector3d to) {
        double dx = to.x - from.x;
        double dy = to.y - from.y;
        double dz = to.z - from.z;
        double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
        double speed = switch (towerId) {
            case "guard_post" -> 27.0;
            case "sniper_desk" -> 32.0;
            case "rapid_security" -> 31.0;
            case "freeze_gate" -> 17.0;
            case "shock_relay" -> 12.0;
            case "armor_drill" -> 16.0;
            default -> 20.0;
        };
        return Math.max(0.10, Math.min(0.45, distance / speed));
    }

    private float projectileGlowScale(String towerId) {
        return switch (towerId) {
            case "guard_post" -> 0.0f;
            case "sniper_desk" -> 0.0f;
            case "rapid_security" -> 0.0f;
            case "freeze_gate" -> 0.54f;
            case "shock_relay" -> 0.26f;
            case "armor_drill" -> 0.0f;
            default -> 0.70f;
        };
    }

    private String shotParticleId(String towerId) {
        return switch (towerId) {
            case "guard_post", "sniper_desk", "armor_drill" -> "";
            case "rapid_security" -> "";
            case "freeze_gate" -> PARTICLE_SHOT;
            default -> PARTICLE_SHOT;
        };
    }

    private String impactParticleId(String towerId) {
        return switch (towerId) {
            case "guard_post", "sniper_desk", "rapid_security", "armor_drill" -> PARTICLE_BLADE_IMPACT;
            case "freeze_gate" -> "Impact_Ice";
            default -> PARTICLE_IMPACT;
        };
    }

    private Color enemyDeathColor(String enemyId) {
        return switch (enemyId) {
            case "thief", "elite_robber" -> this.rgb(230, 230, 230);
            case "runner", "bruiser", "grave_spawn" -> this.rgb(118, 191, 116);
            case "runner_bomber" -> this.rgb(255, 168, 84);
            case ENEMY_GOBLIN_SABOTEUR -> this.rgb(255, 208, 72);
            case "jammer" -> this.rgb(164, 116, 255);
            case "necro_thief" -> this.rgb(112, 220, 126);
            case "vault_priest" -> this.rgb(178, 255, 126);
            case "bone_guard" -> this.rgb(206, 206, 206);
            case "berserker_rotter" -> this.rgb(154, 204, 112);
            case "bone_trumpeter" -> this.rgb(255, 228, 138);
            case "plague_standard" -> this.rgb(214, 124, 84);
            case "curse_weaver" -> this.rgb(142, 132, 255);
            case ENEMY_NECRO_GUARDIAN -> this.rgb(86, 202, 178);
            case ENEMY_RIFT_TWIN_ALPHA -> this.rgb(96, 194, 255);
            case ENEMY_RIFT_TWIN_BETA -> this.rgb(164, 128, 255);
            case ENEMY_NODE_ARBITER -> this.rgb(255, 148, 84);
            case ENEMY_SEAL_MASTER -> this.rgb(214, 94, 126);
            case ENEMY_SEAL_NODE -> this.rgb(255, 96, 150);
            case "vault_breaker" -> this.rgb(190, 82, 52);
            case "necro_king" -> this.rgb(116, 220, 170);
            case "goblin_bomber_boss" -> this.rgb(255, 192, 84);
            default -> this.rgb(255, 255, 255);
        };
    }

    private Color rgb(int red, int green, int blue) {
        return new Color((byte)red, (byte)green, (byte)blue);
    }

    private void spawnParticleBurst(
        Store<EntityStore> store,
        List<Ref<EntityStore>> playerRefs,
        Vector3d center,
        double radius,
        int count,
        double verticalStep
    ) {
        if (PARTICLE_SHOT == null || PARTICLE_SHOT.isBlank()) {
            return;
        }
        ParticleUtil.spawnParticleEffect(PARTICLE_SHOT, center, playerRefs, store);
        for (int i = 0; i < count; i++) {
            double angle = (Math.PI * 2.0 * i) / Math.max(1, count);
            double ring = radius * (0.7 + (i % 3) * 0.15);
            Vector3d burstPoint = new Vector3d(
                center.x + Math.cos(angle) * ring,
                center.y + verticalStep * (i % 3),
                center.z + Math.sin(angle) * ring
            );
            ParticleUtil.spawnParticleEffect(PARTICLE_SHOT, burstPoint, playerRefs, store);
        }
    }

    private void playSound3d(World world, String soundId, Vector3d position) {
        Integer soundIndex = this.resolveSoundIndex(soundId);
        if (soundIndex == null) {
            return;
        }
        SoundUtil.playSoundEvent3d(soundIndex.intValue(), SoundCategory.SFX, position.x, position.y, position.z, world.getEntityStore().getStore());
    }

    private void playWorldUiSound(World world, String soundId) {
        Integer soundIndex = this.resolveSoundIndex(soundId);
        if (soundIndex == null) {
            return;
        }
        for (PlayerRef playerRef : world.getPlayerRefs()) {
            SoundUtil.playSoundEvent2dToPlayer(playerRef, soundIndex.intValue(), SoundCategory.UI);
        }
    }

    private void playWorldSfxSound(World world, String soundId) {
        Integer soundIndex = this.resolveSoundIndex(soundId);
        if (soundIndex == null) {
            return;
        }
        for (PlayerRef playerRef : world.getPlayerRefs()) {
            SoundUtil.playSoundEvent2dToPlayer(playerRef, soundIndex.intValue(), SoundCategory.SFX);
        }
    }

    private void syncAcidStormActivation(World world, MatchContext context) {
        if (world == null || context == null) {
            return;
        }
        int upcomingWaveNumber = Math.max(
            Math.max(0, context.activeWaveNumber),
            Math.max(0, context.state.currentWave)
        );
        String desiredWeatherId = this.desiredStormWeatherId(upcomingWaveNumber);
        if (desiredWeatherId == null) {
            return;
        }
        this.activateAcidStorm(world, context, desiredWeatherId);
    }

    private String desiredStormWeatherId(int upcomingWaveNumber) {
        if (upcomingWaveNumber >= CRIMSON_STORM_START_WAVE) {
            return WEATHER_CRIMSON_STORM;
        }
        if (upcomingWaveNumber >= ACID_STORM_START_WAVE) {
            return WEATHER_ACID_STORM;
        }
        return null;
    }

    private void activateAcidStorm(World world, MatchContext context, String weatherId) {
        if (world == null || context == null) {
            return;
        }
        String normalizedWeatherId = this.normalizedForcedWeatherId(weatherId);
        if (normalizedWeatherId == null) {
            return;
        }
        if (normalizedWeatherId.equals(context.activeStormWeatherId)) {
            return;
        }
        context.acidStormActive = true;
        context.activeStormWeatherId = normalizedWeatherId;
        context.acidStormLightningCooldownRemaining = 0.0;
        context.acidStormOmenCooldownRemaining = ThreadLocalRandom.current().nextDouble(
            ACID_STORM_OMEN_COOLDOWN_MIN_SECONDS,
            ACID_STORM_OMEN_COOLDOWN_MAX_SECONDS
        );
        this.setWorldForcedWeather(world, normalizedWeatherId);
        this.playWorldSfxSound(world, SOUND_ACID_STORM_OMEN);
        this.playSound3d(world, SOUND_ACID_STORM_THUNDER, this.acidStormThunderPosition(context));
        this.triggerAcidStormLightning(world, context);
        this.showEventToast(
            context,
            WEATHER_CRIMSON_STORM.equals(normalizedWeatherId)
                ? this.choose(world, "61 волна. Небо стало багровым.", "Wave 61. The sky turns crimson.")
                : this.choose(world, "31 волна. Небо отравилось.", "Wave 31. The sky turns toxic."),
            EVENT_TOAST_ICON_ALERT
        );
        BankDefenseLocalization.sendWorldMessage(
            world,
            WEATHER_CRIMSON_STORM.equals(normalizedWeatherId)
                ? this.choose(world, "После 60 волны арену накрывает багровая буря.", "After wave 60, a crimson storm engulfs the arena.")
                : this.choose(world, "С 31 волны над ареной бушует кислотная буря.", "From wave 31 onward, an acid storm rages over the arena.")
        );
    }

    private void tickAcidStorm(World world, MatchContext context, double deltaSeconds) {
        if (world == null || context == null || !context.acidStormActive) {
            return;
        }
        if (context.state.gameState == GameState.Defeat || context.state.gameState == GameState.Victory) {
            return;
        }
        context.acidStormOmenCooldownRemaining = Math.max(0.0, context.acidStormOmenCooldownRemaining - Math.max(0.0, deltaSeconds));
        if (context.acidStormOmenCooldownRemaining <= 0.0) {
            this.playSound3d(world, SOUND_ACID_STORM_THUNDER, this.acidStormThunderPosition(context));
            context.acidStormOmenCooldownRemaining = ThreadLocalRandom.current().nextDouble(
                ACID_STORM_OMEN_COOLDOWN_MIN_SECONDS,
                ACID_STORM_OMEN_COOLDOWN_MAX_SECONDS
            );
        }
    }

    private void deactivateAcidStorm(World world, MatchContext context) {
        if (world == null) {
            return;
        }
        String baselineForcedWeatherId = context == null ? null : context.baselineForcedWeatherId;
        if (context != null) {
            context.acidStormActive = false;
            context.activeStormWeatherId = null;
            context.acidStormLightningCooldownRemaining = 0.0;
            context.acidStormOmenCooldownRemaining = 0.0;
        }
        this.setWorldForcedWeather(world, baselineForcedWeatherId);
    }

    private void clearStaleAcidStormWeather(World world) {
        if (world == null) {
            return;
        }
        String activeForcedWeather = this.normalizedForcedWeatherId(world.getWorldConfig().getForcedWeather());
        if (!WEATHER_ACID_STORM.equals(activeForcedWeather) && !WEATHER_CRIMSON_STORM.equals(activeForcedWeather)) {
            return;
        }
        this.setWorldForcedWeather(world, null);
    }

    private void setWorldForcedWeather(World world, String forcedWeatherId) {
        if (world == null) {
            return;
        }
        String normalizedWeatherId = this.normalizedForcedWeatherId(forcedWeatherId);
        String currentForcedWeatherId = this.normalizedForcedWeatherId(world.getWorldConfig().getForcedWeather());
        if ((normalizedWeatherId == null && currentForcedWeatherId == null)
            || (normalizedWeatherId != null && normalizedWeatherId.equals(currentForcedWeatherId))) {
            return;
        }
        Store<EntityStore> store = world.getEntityStore().getStore();
        WeatherResource weatherResource = store.getResource(WeatherResource.getResourceType());
        weatherResource.setForcedWeather(normalizedWeatherId);
        WorldConfig config = world.getWorldConfig();
        config.setForcedWeather(normalizedWeatherId);
        config.markChanged();
    }

    private String normalizedForcedWeatherId(String weatherId) {
        if (weatherId == null) {
            return null;
        }
        String normalized = weatherId.trim();
        return normalized.isEmpty() ? null : normalized;
    }

    private void triggerAcidStormLightning(World world, MatchContext context) {
        Vector3d primaryStrikePosition = this.acidStormBurstAnchorPosition(context);
        if (primaryStrikePosition == null) {
            return;
        }
        List<Ref<EntityStore>> playerRefs = this.collectPlayerEntityRefs(world);
        if (playerRefs.isEmpty()) {
            return;
        }
        Store<EntityStore> store = world.getEntityStore().getStore();
        Color acidColor = this.rgb(182, 255, 94);
        ThreadLocalRandom random = ThreadLocalRandom.current();
        int strikeCount = Math.max(7, random.nextInt(ACID_STORM_BURST_MIN_STRIKES, ACID_STORM_BURST_MAX_STRIKES + 1));
        this.spawnAcidStormLightningBolt(world, store, playerRefs, primaryStrikePosition, acidColor, ACID_STORM_LIGHTNING_SCALE_CORE * 1.08f);
        for (int i = 1; i < strikeCount; i++) {
            Vector3d chainedStrikePosition = this.offsetAcidStormStrikePosition(primaryStrikePosition, random, i);
            this.spawnAcidStormLightningBolt(world, store, playerRefs, chainedStrikePosition, acidColor, ACID_STORM_LIGHTNING_SCALE_CORE);
        }
        this.playSound3d(world, SOUND_ACID_STORM_THUNDER, new Vector3d(primaryStrikePosition.x, primaryStrikePosition.y + 6.0, primaryStrikePosition.z));
    }

    private void spawnAcidStormLightningBolt(
        World world,
        Store<EntityStore> store,
        List<Ref<EntityStore>> playerRefs,
        Vector3d strikePosition,
        Color acidColor,
        float coreScale
    ) {
        if (world == null || store == null || playerRefs == null || playerRefs.isEmpty() || strikePosition == null) {
            return;
        }
        Vector3f rotation = new Vector3f(0.0f, 0.0f, 0.0f);
        double skyBaseY = strikePosition.y + 16.0;
        Vector3d veryHigh = new Vector3d(strikePosition.x, skyBaseY + 9.0, strikePosition.z);
        Vector3d high = new Vector3d(strikePosition.x, skyBaseY + 6.1, strikePosition.z);
        Vector3d upperMid = new Vector3d(strikePosition.x, skyBaseY + 3.9, strikePosition.z);
        Vector3d mid = new Vector3d(strikePosition.x, skyBaseY + 2.1, strikePosition.z);
        Vector3d lowerAir = new Vector3d(strikePosition.x, skyBaseY + 0.7, strikePosition.z);
        Vector3d cloudCenter = new Vector3d(strikePosition.x, skyBaseY + 3.6, strikePosition.z);
        this.spawnColoredParticle(store, playerRefs, PARTICLE_ACID_STORM_LIGHTNING_START, veryHigh, rotation, ACID_STORM_LIGHTNING_SCALE_START * 1.08f, acidColor);
        this.spawnColoredParticle(store, playerRefs, PARTICLE_ACID_STORM_LIGHTNING_START, high, rotation, ACID_STORM_LIGHTNING_SCALE_START, acidColor);
        this.spawnColoredParticle(store, playerRefs, PARTICLE_ACID_STORM_LIGHTNING, high, rotation, coreScale * 0.92f, acidColor);
        this.spawnColoredParticle(store, playerRefs, PARTICLE_ACID_STORM_LIGHTNING, upperMid, rotation, coreScale, acidColor);
        this.spawnColoredParticle(store, playerRefs, PARTICLE_ACID_STORM_LIGHTNING, mid, rotation, coreScale * 0.98f, acidColor);
        this.spawnColoredParticle(store, playerRefs, PARTICLE_ACID_STORM_LIGHTNING, lowerAir, rotation, coreScale * 0.92f, acidColor);
        this.spawnColoredParticle(store, playerRefs, PARTICLE_ACID_STORM_LIGHTNING_TRAIL, new Vector3d(strikePosition.x, skyBaseY + 5.9, strikePosition.z), rotation, ACID_STORM_LIGHTNING_SCALE_TRAIL, acidColor);
        this.spawnColoredParticle(store, playerRefs, PARTICLE_ACID_STORM_LIGHTNING_TRAIL, new Vector3d(strikePosition.x, skyBaseY + 2.9, strikePosition.z), rotation, ACID_STORM_LIGHTNING_SCALE_TRAIL * 0.95f, acidColor);
        this.spawnColoredParticle(store, playerRefs, PARTICLE_ACID_STORM_BEAM_LIGHTNING, new Vector3d(strikePosition.x, skyBaseY + 5.2, strikePosition.z), rotation, ACID_STORM_LIGHTNING_SCALE_BEAM * 1.04f, acidColor);
        this.spawnColoredParticle(store, playerRefs, PARTICLE_ACID_STORM_BEAM_LIGHTNING_2, new Vector3d(strikePosition.x, skyBaseY + 3.4, strikePosition.z), rotation, ACID_STORM_LIGHTNING_SCALE_BEAM, acidColor);
        this.spawnColoredParticle(store, playerRefs, PARTICLE_ACID_STORM_VOID_LIGHTNING, new Vector3d(strikePosition.x, skyBaseY + 1.8, strikePosition.z), rotation, ACID_STORM_LIGHTNING_SCALE_BEAM * 0.9f, acidColor);
        this.spawnColoredParticle(store, playerRefs, PARTICLE_ACID_STORM_VOID_LIGHTNING_RANDOM, new Vector3d(strikePosition.x, skyBaseY + 4.7, strikePosition.z), rotation, ACID_STORM_LIGHTNING_SCALE_BEAM * 0.82f, acidColor);
        this.spawnColoredParticle(store, playerRefs, PARTICLE_ACID_STORM_LIGHTNING_SPARKS, cloudCenter, rotation, ACID_STORM_LIGHTNING_SCALE_GROUND * 0.88f, acidColor);
        this.spawnColoredParticle(store, playerRefs, PARTICLE_ACID_STORM_LIGHTNING_SPARKS, new Vector3d(cloudCenter.x, cloudCenter.y + 1.4, cloudCenter.z), rotation, ACID_STORM_LIGHTNING_SCALE_GROUND * 0.82f, acidColor);
        this.spawnColoredParticle(store, playerRefs, PARTICLE_ACID_STORM_LIGHTNING_POOF, new Vector3d(cloudCenter.x, cloudCenter.y + 0.6, cloudCenter.z), rotation, ACID_STORM_LIGHTNING_SCALE_GROUND * 0.72f, acidColor);
        this.spawnParticleBurst(store, playerRefs, new Vector3d(cloudCenter.x, cloudCenter.y + 0.8, cloudCenter.z), 4.2, 16, 0.18);
    }

    private Vector3d acidStormBurstAnchorPosition(MatchContext context) {
        if (context != null && context.snapshot != null && context.snapshot.map != null) {
            MapConfig map = context.snapshot.map;
            Vec3i anchor = this.isDuoMode(context)
                ? (map.duoBankCenter != null ? map.duoBankCenter : map.duoVaultPoint)
                : (map.bankCenter != null ? map.bankCenter : map.vaultPoint);
            if (anchor != null) {
                return new Vector3d(anchor.x + 0.5, anchor.y + 1.0, anchor.z + 0.5);
            }
        }
        return this.acidStormThunderPosition(context);
    }

    private Vector3d offsetAcidStormStrikePosition(Vector3d origin, ThreadLocalRandom random, int strikeIndex) {
        if (origin == null) {
            return null;
        }
        double angle = random.nextDouble(0.0, Math.PI * 2.0);
        double radialScale = 0.32 + (0.68 * strikeIndex / Math.max(1.0, ACID_STORM_BURST_MAX_STRIKES - 1.0));
        double distance = ACID_STORM_BURST_CLUSTER_RADIUS * radialScale * random.nextDouble(0.58, 1.0);
        return new Vector3d(
            origin.x + Math.cos(angle) * distance,
            origin.y + random.nextDouble(-0.15, 0.35),
            origin.z + Math.sin(angle) * distance
        );
    }

    private Vector3d acidStormAnchorStrikePosition(MatchContext context, ThreadLocalRandom random) {
        if (context == null || context.snapshot == null || context.snapshot.map == null) {
            return null;
        }
        MapConfig map = context.snapshot.map;
        Vec3i anchor = this.isDuoMode(context)
            ? (map.duoVaultPoint != null ? map.duoVaultPoint : map.duoBankCenter)
            : (map.vaultPoint != null ? map.vaultPoint : map.bankCenter);
        if (anchor == null) {
            return null;
        }
        return new Vector3d(
            anchor.x + 0.5 + random.nextDouble(-8.0, 8.0),
            anchor.y + 0.15,
            anchor.z + 0.5 + random.nextDouble(-8.0, 8.0)
        );
    }

    private Vector3d randomAcidStormStrikePosition(MatchContext context) {
        if (context == null) {
            return null;
        }
        ThreadLocalRandom random = ThreadLocalRandom.current();
        if (random.nextDouble() < 0.34) {
            Vector3d anchorStrike = this.acidStormAnchorStrikePosition(context, random);
            if (anchorStrike != null) {
                return anchorStrike;
            }
        }
        if (!context.enemies.isEmpty() && random.nextDouble() < 0.58) {
            EnemyInstance enemy = context.enemies.get(random.nextInt(context.enemies.size()));
            Vector3d enemyPosition = this.enemyWorldPosition(context, enemy);
            return new Vector3d(
                enemyPosition.x + random.nextDouble(-1.6, 1.6),
                enemyPosition.y,
                enemyPosition.z + random.nextDouble(-1.6, 1.6)
            );
        }

        List<String> laneIds = new ArrayList<>(context.routesByLane.keySet());
        if (!laneIds.isEmpty()) {
            String laneId = laneIds.get(random.nextInt(laneIds.size()));
            double routeLength = this.routeLengthForLane(context, laneId);
            if (routeLength > 0.0) {
                double progress = routeLength * random.nextDouble(0.18, 0.92);
                Vector3d lanePosition = this.pointAtProgress(context, laneId, progress).toWorldVector(0.1);
                return new Vector3d(
                    lanePosition.x + random.nextDouble(-2.2, 2.2),
                    lanePosition.y,
                    lanePosition.z + random.nextDouble(-2.2, 2.2)
                );
            }
        }

        List<Vec3i> fallbackAnchors = new ArrayList<>();
        if (context.snapshot != null && context.snapshot.map != null) {
            MapConfig map = context.snapshot.map;
            if (this.isDuoMode(context)) {
                if (map.duoBankCenter != null) {
                    fallbackAnchors.add(map.duoBankCenter);
                }
                if (map.duoVaultPoint != null) {
                    fallbackAnchors.add(map.duoVaultPoint);
                }
            } else {
                if (map.bankCenter != null) {
                    fallbackAnchors.add(map.bankCenter);
                }
                if (map.vaultPoint != null) {
                    fallbackAnchors.add(map.vaultPoint);
                }
            }
        }
        if (fallbackAnchors.isEmpty()) {
            return null;
        }
        Vec3i anchor = fallbackAnchors.get(random.nextInt(fallbackAnchors.size()));
        return new Vector3d(
            anchor.x + 0.5 + random.nextDouble(-2.5, 2.5),
            anchor.y + 0.2,
            anchor.z + 0.5 + random.nextDouble(-2.5, 2.5)
        );
    }

    private Vector3d acidStormThunderPosition(MatchContext context) {
        Vector3d strikePosition = this.randomAcidStormStrikePosition(context);
        if (strikePosition != null) {
            return new Vector3d(strikePosition.x, strikePosition.y + 4.0, strikePosition.z);
        }
        if (context != null && context.snapshot != null && context.snapshot.map != null) {
            MapConfig map = context.snapshot.map;
            Vec3i anchor = this.isDuoMode(context)
                ? (map.duoVaultPoint != null ? map.duoVaultPoint : map.duoBankCenter)
                : (map.vaultPoint != null ? map.vaultPoint : map.bankCenter);
            if (anchor != null) {
                return new Vector3d(anchor.x + 0.5, anchor.y + 4.0, anchor.z + 0.5);
            }
        }
        return new Vector3d(0.5, 80.0, 0.5);
    }

    private Vector3d acidStormOmenPosition(MatchContext context) {
        if (context != null && context.snapshot != null && context.snapshot.map != null) {
            MapConfig map = context.snapshot.map;
            Vec3i anchor = this.isDuoMode(context)
                ? (map.duoBankCenter != null ? map.duoBankCenter : map.duoVaultPoint)
                : (map.bankCenter != null ? map.bankCenter : map.vaultPoint);
            if (anchor != null) {
                return new Vector3d(anchor.x + 0.5, anchor.y + 1.1, anchor.z + 0.5);
            }
        }
        return this.acidStormThunderPosition(context);
    }

    private Integer resolveSoundIndex(String soundId) {
        Integer cached = this.soundIndexCache.get(soundId);
        if (cached != null) {
            return cached.intValue() < 0 ? null : cached;
        }
        String[] candidates = soundId != null && soundId.contains(":")
            ? new String[] { soundId }
            : new String[] {
                soundId,
                "QubeCore:" + soundId,
                "BankDefense:" + soundId,
                "QubeCore:BankDefense:" + soundId
            };
        for (String candidate : candidates) {
            if (candidate == null || candidate.isBlank()) {
                continue;
            }
            if (SoundEvent.getAssetMap().getAsset(candidate) == null) {
                continue;
            }
            int soundIndex = SoundEvent.getAssetMap().getIndex(candidate);
            this.soundIndexCache.put(soundId, Integer.valueOf(soundIndex));
            if (!candidate.equals(soundId)) {
                this.soundIndexCache.put(candidate, Integer.valueOf(soundIndex));
            }
            return Integer.valueOf(soundIndex);
        }
        this.soundIndexCache.put(soundId, Integer.valueOf(-1));
        return null;
    }

    private List<Ref<EntityStore>> collectPlayerEntityRefs(World world) {
        List<Ref<EntityStore>> refs = new ArrayList<>();
        for (PlayerRef playerRef : world.getPlayerRefs()) {
            Ref<EntityStore> ref = playerRef.getReference();
            if (ref != null && ref.isValid()) {
                refs.add(ref);
            }
        }
        return refs;
    }

    private ModuleDefinition equippedModule(MatchContext context, TowerInstance tower) {
        if (tower == null || tower.equippedModuleId == null) {
            return null;
        }
        return context.moduleById.get(tower.equippedModuleId);
    }

    private boolean isHeavyEnemy(String enemyId) {
        return "bruiser".equals(enemyId)
            || "bone_guard".equals(enemyId)
            || "elite_robber".equals(enemyId)
            || ENEMY_GOBLIN_SABOTEUR.equals(enemyId)
            || ENEMY_NECRO_GUARDIAN.equals(enemyId)
            || "vault_breaker".equals(enemyId)
            || "necro_king".equals(enemyId)
            || "goblin_bomber_boss".equals(enemyId);
    }

    private void applyTowerHit(
        World world,
        MatchContext context,
        TowerInstance tower,
        EnemyInstance enemy,
        Vector3d from,
        Vector3d to,
        double damage,
        double slowPercent,
        double slowDuration,
        boolean impactFeedback
    ) {
        if (enemy == null || enemy.dying) {
            return;
        }
        double adjustedDamage = this.adjustedDamageAgainstEnemy(context, enemy, damage);
        enemy.hp -= adjustedDamage;
        if (slowPercent > 0.0 && !enemy.definition.slowImmune) {
            double totalSlowResistance = Math.max(0.0, Math.min(0.95, enemy.slowResistancePercent));
            double appliedSlow = slowPercent * (1.0 - totalSlowResistance) * this.contractSlowEffectMultiplier(context, enemy);
            if (appliedSlow > 0.0) {
                boolean stackingCryo = tower != null
                    && "cryo_capsule".equals(tower.equippedModuleId);
                double nextSlow = stackingCryo
                    ? enemy.slowPercent + appliedSlow
                    : Math.max(enemy.slowPercent, appliedSlow);
                enemy.slowPercent = Math.min(0.95, nextSlow);
                enemy.slowRemaining = Math.max(enemy.slowRemaining, slowDuration);
            }
        }
        if (tower != null && tower.definition != null && "rapid_security".equals(tower.definition.id)) {
            enemy.armorBreakFlatReduction = Math.max(enemy.armorBreakFlatReduction, DART_ARMOR_BREAK_FLAT);
            enemy.armorBreakPercentReduction = Math.max(enemy.armorBreakPercentReduction, DART_ARMOR_BREAK_PERCENT);
            enemy.armorBreakRemaining = Math.max(enemy.armorBreakRemaining, DART_ARMOR_BREAK_DURATION_SECONDS);
        }
        if (impactFeedback) {
            this.emitImpactFeedback(world, tower.definition.id, to);
        }
        if (enemy.hp <= 0.0) {
            this.beginEnemyDeath(world, context, enemy, to);
        }
    }

    private void applySplashDamage(
        World world,
        MatchContext context,
        TowerInstance tower,
        EnemyInstance primaryTarget,
        Vector3d impactPosition,
        double splashDamage,
        double slowPercent,
        double slowDuration,
        double splashRadius
    ) {
        double splashRadiusSquared = splashRadius * splashRadius;
        for (EnemyInstance enemy : context.enemies) {
            if (enemy == primaryTarget || enemy.dying) {
                continue;
            }
            Vector3d enemyPosition = this.enemyWorldPosition(context, enemy);
            double dx = enemyPosition.x - impactPosition.x;
            double dy = enemyPosition.y - impactPosition.y;
            double dz = enemyPosition.z - impactPosition.z;
            if ((dx * dx) + (dy * dy) + (dz * dz) > splashRadiusSquared) {
                continue;
            }
            this.applyTowerHit(
                world,
                context,
                tower,
                enemy,
                impactPosition,
                enemyPosition,
                splashDamage,
                slowPercent,
                slowDuration,
                "freeze_gate".equals(tower.definition.id)
            );
        }
    }

    private void applyChainDamage(
        World world,
        MatchContext context,
        TowerInstance tower,
        EnemyInstance primaryTarget,
        Vector3d towerPosition,
        Vector3d primaryImpactPosition,
        int extraTargets,
        double chainRange,
        double chainDamage,
        double slowPercent,
        double slowDuration
    ) {
        if (extraTargets <= 0 || chainDamage <= 0.0) {
            return;
        }
        EnemyInstance current = primaryTarget;
        Vector3d currentPosition = primaryImpactPosition;
        Set<Long> hitIds = new HashSet<>();
        hitIds.add(Long.valueOf(primaryTarget.instanceId));
        for (int i = 0; i < extraTargets; i++) {
            EnemyInstance next = this.findNearestChainTarget(context, current, currentPosition, hitIds, chainRange);
            if (next == null) {
                return;
            }
            Vector3d nextPosition = this.enemyWorldPosition(context, next);
            this.emitChainFeedback(world, tower, currentPosition, nextPosition);
            this.applyTowerHit(world, context, tower, next, towerPosition, nextPosition, chainDamage, slowPercent, slowDuration, true);
            current = next;
            currentPosition = nextPosition;
            hitIds.add(Long.valueOf(next.instanceId));
            chainDamage *= 0.78;
            slowPercent *= 0.9;
        }
    }

    private EnemyInstance findNearestChainTarget(
        MatchContext context,
        EnemyInstance source,
        Vector3d sourcePosition,
        Set<Long> excludedIds,
        double chainRange
    ) {
        double bestDistanceSquared = Double.MAX_VALUE;
        EnemyInstance best = null;
        double chainRangeSquared = chainRange * chainRange;
        for (EnemyInstance candidate : context.enemies) {
            if (candidate == source || candidate.dying || excludedIds.contains(Long.valueOf(candidate.instanceId))) {
                continue;
            }
            Vector3d candidatePosition = this.enemyWorldPosition(context, candidate);
            double dx = candidatePosition.x - sourcePosition.x;
            double dy = candidatePosition.y - sourcePosition.y;
            double dz = candidatePosition.z - sourcePosition.z;
            double distanceSquared = (dx * dx) + (dy * dy) + (dz * dz);
            if (distanceSquared > chainRangeSquared || distanceSquared >= bestDistanceSquared) {
                continue;
            }
            bestDistanceSquared = distanceSquared;
            best = candidate;
        }
        return best;
    }

    private void emitImpactFeedback(World world, String towerId, Vector3d targetPosition) {
        this.playSound3d(world, this.impactSoundForTower(towerId), targetPosition);
        List<Ref<EntityStore>> playerRefs = this.collectPlayerEntityRefs(world);
        if (playerRefs.isEmpty()) {
            return;
        }
        Store<EntityStore> store = world.getEntityStore().getStore();
        Vector3f rotation = new Vector3f(0.0f, 0.0f, 0.0f);
        this.spawnColoredParticle(store, playerRefs, this.impactParticleId(towerId), targetPosition, rotation, this.towerImpactScale(towerId), this.towerTracerColor(towerId));
    }

    private void emitChainFeedback(World world, TowerInstance tower, Vector3d from, Vector3d to) {
        List<Ref<EntityStore>> playerRefs = this.collectPlayerEntityRefs(world);
        if (playerRefs.isEmpty()) {
            return;
        }
        Store<EntityStore> store = world.getEntityStore().getStore();
        Vector3f rotation = this.rotationTowards(from, to);
        if (this.useModelProjectile(tower.definition.id)) {
            this.spawnProjectileVisual(world, tower, from, to);
        }
        int segments = Math.max(2, (int)Math.ceil(Math.sqrt(
            (to.x - from.x) * (to.x - from.x)
                + (to.y - from.y) * (to.y - from.y)
                + (to.z - from.z) * (to.z - from.z)
        ) / 1.25));
        for (int i = 0; i <= segments; i++) {
            double t = i / (double)segments;
            Vector3d point = this.lerp(from, to, t);
            float scale = i % 2 == 0 ? 0.34f : 0.24f;
            this.spawnColoredParticle(store, playerRefs, this.shotParticleId(tower.definition.id), point, rotation, scale, this.towerTracerColor(tower.definition.id));
        }
        this.spawnColoredParticle(store, playerRefs, this.impactParticleId(tower.definition.id), to, rotation, 0.46f, this.towerTracerColor(tower.definition.id));
    }

    @SuppressWarnings("unused")
    private void emitStormLineFeedback(World world, Vector3d from, Vector3d to, float scale) {
        List<Ref<EntityStore>> playerRefs = this.collectPlayerEntityRefs(world);
        if (playerRefs.isEmpty()) {
            return;
        }
        Store<EntityStore> store = world.getEntityStore().getStore();
        Vector3f rotation = this.rotationTowards(from, to);
        this.emitLinearTracer(store, playerRefs, this.shotParticleId("shock_relay"), "shock_relay", from, to, rotation, 0.55, scale);
        this.spawnColoredParticle(store, playerRefs, this.impactParticleId("shock_relay"), to, rotation, Math.max(0.28f, scale), this.towerTracerColor("shock_relay"));
    }

    private void emitFreezeAoeFeedback(World world, TowerInstance tower, Vector3d center, double radius) {
        List<Ref<EntityStore>> playerRefs = this.collectPlayerEntityRefs(world);
        if (playerRefs.isEmpty()) {
            return;
        }
        Store<EntityStore> store = world.getEntityStore().getStore();
        Color color = this.towerTracerColor("freeze_gate");
        Vector3f rotation = new Vector3f(0.0f, 0.0f, 0.0f);
        Vector3d ringCenter = new Vector3d(center.x, center.y + 0.15, center.z);
        int ringPoints = Math.max(8, (int)Math.ceil(radius * 4.0));
        for (int i = 0; i < ringPoints; i++) {
            double angle = (Math.PI * 2.0 * i) / ringPoints;
            Vector3d point = new Vector3d(
                ringCenter.x + Math.cos(angle) * radius,
                ringCenter.y,
                ringCenter.z + Math.sin(angle) * radius
            );
            this.spawnColoredParticle(store, playerRefs, this.shotParticleId("freeze_gate"), point, rotation, 0.28f, color);
            if (i % 2 == 0) {
                this.emitLinearTracer(store, playerRefs, this.shotParticleId("freeze_gate"), tower.definition.id, ringCenter, point, rotation, 0.92, 0.24f);
            }
        }
        this.spawnColoredParticle(store, playerRefs, this.impactParticleId("freeze_gate"), ringCenter, rotation, 0.72f, color);
    }

    private void emitLinearTracer(
        Store<EntityStore> store,
        List<Ref<EntityStore>> playerRefs,
        String particleId,
        String towerId,
        Vector3d from,
        Vector3d to,
        Vector3f rotation,
        double step,
        float scaleMultiplier
    ) {
        double dx = to.x - from.x;
        double dy = to.y - from.y;
        double dz = to.z - from.z;
        double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
        int segments = Math.max(2, (int)Math.ceil(distance / Math.max(0.12, step)));
        float scale = this.projectileGlowScale(towerId) * scaleMultiplier;
        Color color = this.towerTracerColor(towerId);
        for (int i = 0; i <= segments; i++) {
            double t = i / (double)segments;
            Vector3d point = this.lerp(from, to, t);
            this.spawnColoredParticle(store, playerRefs, particleId, point, rotation, scale, color);
        }
    }

    private boolean useModelProjectile(String towerId) {
        return !"freeze_gate".equals(towerId) && !"shock_relay".equals(towerId);
    }

    private void emitEnemyAura(World world, EnemyInstance enemy, Vector3d position, List<Ref<EntityStore>> playerRefs) {
        if (!this.shouldEmitEnemyAura(enemy)) {
            return;
        }
        if (playerRefs == null || playerRefs.isEmpty()) {
            return;
        }
        Store<EntityStore> store = world.getEntityStore().getStore();
        Color color;
        float scale;
        switch (enemy.definition.id) {
            case "vault_breaker" -> {
                color = this.rgb(255, 112, 64);
                scale = 1.4f;
            }
            case "necro_king" -> {
                color = this.rgb(92, 224, 172);
                scale = 1.28f;
            }
            case "goblin_bomber_boss" -> {
                color = this.rgb(255, 196, 92);
                scale = 1.18f;
            }
            case ENEMY_SEAL_MASTER -> {
                if (enemy.variantStage >= 2) {
                    color = this.rgb(255, 128, 84);
                    scale = 1.34f;
                } else if (enemy.variantStage >= 1) {
                    color = this.rgb(204, 126, 255);
                    scale = 1.18f;
                } else {
                    color = this.rgb(156, 112, 255);
                    scale = 1.02f;
                }
            }
            case "runner_bomber" -> {
                color = this.rgb(255, 168, 84);
                scale = 0.88f;
            }
            case "jammer" -> {
                color = this.rgb(164, 116, 255);
                scale = 0.98f;
            }
            case "necro_thief" -> {
                color = this.rgb(112, 220, 126);
                scale = 0.92f;
            }
            case "vault_priest" -> {
                color = this.rgb(178, 255, 126);
                scale = 1.02f;
            }
            case "bone_trumpeter" -> {
                color = this.rgb(255, 224, 122);
                scale = 0.98f;
            }
            case "plague_standard" -> {
                color = this.rgb(214, 124, 84);
                scale = 1.02f;
            }
            case "curse_weaver" -> {
                color = this.rgb(144, 130, 255);
                scale = 0.96f;
            }
            default -> {
                color = this.rgb(189, 104, 255);
                scale = 0.9f;
            }
        }
        if (enemy.isBossCasting()) {
            float castScale = (float)(scale * BOSS_CAST_AURA_SCALE_MULTIPLIER);
            Vector3d center = new Vector3d(position.x, position.y + 1.05, position.z);
            this.spawnColoredParticle(store, playerRefs, PARTICLE_SHOT, center, new Vector3f(0.0f, 0.0f, 0.0f), castScale, color);
            this.spawnColoredParticle(store, playerRefs, PARTICLE_IMPACT, new Vector3d(center.x, center.y - 0.35, center.z), new Vector3f(0.0f, 0.0f, 0.0f), castScale * 0.95f, color);
            this.spawnParticleBurst(store, playerRefs, center, 0.22 + castScale * 0.12, 6, 0.05);
            return;
        }
        this.spawnColoredParticle(store, playerRefs, PARTICLE_SHOT, new Vector3d(position.x, position.y + 1.1, position.z), new Vector3f(0.0f, 0.0f, 0.0f), scale, color);
        if ("vault_breaker".equals(enemy.definition.id)
            || "vault_priest".equals(enemy.definition.id)
            || "jammer".equals(enemy.definition.id)
            || "necro_king".equals(enemy.definition.id)
            || "plague_standard".equals(enemy.definition.id)
            || "curse_weaver".equals(enemy.definition.id)) {
            this.spawnColoredParticle(store, playerRefs, PARTICLE_IMPACT, new Vector3d(position.x, position.y + 0.6, position.z), new Vector3f(0.0f, 0.0f, 0.0f), 1.2f, color);
        }
    }

    private EnemyInstance pickTarget(MatchContext context, TowerInstance tower) {
        Vector3d towerPosition = this.towerWorldPosition(tower.slot);
        double range = this.towerAttackRange(context, tower);
        EnemyInstance selected = null;
        double bestMetric = Double.NEGATIVE_INFINITY;
        for (EnemyInstance enemy : context.enemies) {
            if (enemy.dying || this.isUntargetableForTower(context, tower, enemy)) {
                continue;
            }
            InterpolatedPoint enemyPosition = this.pointAtProgress(context, enemy.laneId, enemy.progress);
            double dx = enemyPosition.x - towerPosition.x;
            double dy = enemyPosition.y - towerPosition.y;
            double dz = enemyPosition.z - towerPosition.z;
            double distanceSquared = (dx * dx) + (dy * dy) + (dz * dz);
            if (distanceSquared > range * range) {
                continue;
            }
            double metric = "strongest".equalsIgnoreCase(tower.definition.targeting) ? enemy.hp : enemy.progress;
            if (metric > bestMetric) {
                bestMetric = metric;
                selected = enemy;
            }
        }
        return selected;
    }

    private boolean isUntargetableForTower(MatchContext context, TowerInstance tower, EnemyInstance enemy) {
        if (enemy == null) {
            return true;
        }
        if (enemy.tutorialTrapTargetLocked && (tower == null || tower.definition == null || !tower.definition.trapTower)) {
            return true;
        }
        if (enemy.isBossCasting()) {
            return true;
        }
        if ((ENEMY_RIFT_TWIN_ALPHA.equals(enemy.definition.id) || ENEMY_RIFT_TWIN_BETA.equals(enemy.definition.id))
            && !enemy.damageableTwin) {
            return true;
        }
        return "necro_king".equals(enemy.definition.id) && this.hasLivingLinkedSummons(context, enemy.instanceId);
    }

    private double towerAttackRange(MatchContext context, TowerInstance tower) {
        TowerLevel level = tower.getCurrentLevel();
        ModuleDefinition module = this.equippedModule(context, tower);
        Vector3d towerPosition = this.towerWorldPosition(tower.slot);
        double range = (level.range * this.moduleRelativeMultiplier(context, module == null ? 1.0 : module.rangeMultiplier))
            + this.moduleBonusValue(context, module == null ? 0.0 : module.rangeBonus);
        range *= this.enemyRangeMultiplier(context, towerPosition);
        if (this.hasActiveSealCurse(context, SealCurseType.Range)) {
            range *= DUO_SEAL_CURSE_RANGE_MULTIPLIER;
        }
        return range;
    }

    private ActionResult buildTowerInSlot(World world, MatchContext context, PlayerRef actor, BuildSlot slot, TowerDefinition tower, int buildMode) {
        ActionResult tutorialValidation = this.validateTutorialBuildChoice(world, context, slot, tower);
        if (tutorialValidation != null && !tutorialValidation.success) {
            return tutorialValidation;
        }
        if (!this.canPlayerUseSlot(world, context, actor, slot)) {
            return ActionResult.fail(this.deniedSlotAccessMessage(actor, slot));
        }
        if (slot.allowedTowerIds != null && !slot.allowedTowerIds.isEmpty() && !slot.allowedTowerIds.contains(tower.id)) {
            return ActionResult.fail(
                this.choose(actor, "Башня ", "Tower ")
                    + this.towerDisplayName(actor, tower)
                    + this.choose(actor, " не подходит для площадки ", " cannot be built on pad ")
                    + this.slotDisplayName(slot)
                    + "."
            );
        }
        if (this.isTrapSlot(slot) != tower.trapTower) {
            return ActionResult.fail(this.isTrapSlot(slot)
                ? this.choose(actor, "На этом слоте можно ставить только ловушки.", "Only traps can be placed in this slot.")
                : this.choose(actor, "Ловушки ставятся только в ловушечные слоты.", "Traps can only be placed in trap slots."));
        }
        if ("seed_idol".equals(tower.id) && this.hasSeedIdol(context)) {
            return ActionResult.fail(this.choose(actor, "Идол урожая можно поставить только один раз за матч.", "The Harvest Idol can only be built once per match."));
        }
        int buildCost = this.towerLevelCost(context, tower, 0);
        int currentCurrency = this.currencyForPlayer(context, actor);
        if (currentCurrency < buildCost) {
            return ActionResult.fail(this.choose(actor, "Недостаточно средств. Нужно: ", "Not enough currency. Need: ") + buildCost + this.choose(actor, ", сейчас: ", ", current: ") + currentCurrency + ".");
        }
        if (!this.spendCurrency(context, actor, buildCost)) {
            return ActionResult.fail(this.choose(actor, "Недостаточно средств.", "Not enough currency."));
        }
        this.trackLifetimeSpentCurrency(context, buildCost);
        TowerInstance instance = new TowerInstance(slot, tower, buildMode);
        if (tower.trapTower) {
            this.trackLifetimeTrapPlaced(context);
        }
        if (context.tutorialActive) {
            TutorialState tutorial = this.tutorialState(world);
            if (tutorial.stage == TutorialStage.BuildSecondTower && tutorial.secondTowerSlotId == null && !"guard_post".equals(tower.id)) {
                tutorial.secondTowerSlotId = slot.id;
            }
        }
        if (context.snapshot.map.vaultPoint != null) {
            instance.visualRotation = this.rotationTowards(
                this.towerWorldPosition(slot),
                this.objectiveWorldPosition(context.snapshot.map.vaultPoint, 0.05)
            );
        }
        context.placedTowers.put(slot.id, instance);
        this.playSound3d(world, tower.trapTower ? SOUND_TRAP_PLACE : SOUND_BUILD, this.towerWorldPosition(slot));
        this.refreshVisualizationIfEnabled(world);
        String placedName = this.towerDisplayName(actor, tower);
        if ("seed_idol".equals(tower.id) && instance.specialMode != SEED_IDOL_MODE_DEFAULT) {
            placedName += " [" + this.seedIdolModeName(actor, instance.specialMode) + "]";
        }
        this.updateTutorialProgressionFromContext(world, context);
        return ActionResult.ok(
            this.choose(actor, "Башня построена: ", "Tower built: ")
                + placedName
                + this.choose(actor, ". Площадка: ", ". Pad: ")
                + this.slotDisplayName(slot)
                + this.choose(actor, ". Стоимость: ", ". Cost: ")
                + buildCost
                + this.choose(actor, ". Баланс: ", ". Balance: ")
                + this.currencyForPlayer(context, actor)
                + "."
        );
    }

    private ActionResult upgradeTowerInstance(World world, MatchContext context, PlayerRef actor, TowerInstance instance) {
        ActionResult tutorialValidation = this.validateTutorialUpgradeChoice(world, context, instance);
        if (tutorialValidation != null && !tutorialValidation.success) {
            return tutorialValidation;
        }
        if (instance != null && instance.slot != null && !this.canPlayerUseSlot(world, context, actor, instance.slot)) {
            return ActionResult.fail(this.deniedSlotAccessMessage(actor, instance.slot));
        }
        if ("heart_of_roots".equals(instance.definition.id)) {
            return this.activateHeartOfRoots(world, context, actor, instance);
        }
        if (instance.definition.superTower) {
            return ActionResult.fail(this.towerDisplayName(actor, instance.definition) + this.choose(actor, " не использует уровни.", " does not use levels."));
        }
        int unlockedCap = this.unlockedTowerLevelCap(context, instance.definition);
        if (instance.levelIndex + 1 >= unlockedCap) {
            if (unlockedCap < instance.definition.levels.size() && !instance.definition.superTower) {
                return ActionResult.fail(this.choose(actor, "Дальнейшие уровни для ", "Further levels for ") + this.towerDisplayName(actor, instance.definition) + this.choose(actor, " ещё не открыты в древе прогрессии.", " are not unlocked yet in the progression tree."));
            }
            return ActionResult.fail(this.towerDisplayName(actor, instance.definition) + this.choose(actor, " уже максимального уровня.", " is already at the maximum level."));
        }
        if (instance.levelIndex >= instance.definition.levels.size() - 1) {
            return ActionResult.fail(this.towerDisplayName(actor, instance.definition) + this.choose(actor, " уже максимального уровня.", " is already at the maximum level."));
        }
        int upgradeCost = this.towerLevelCost(context, instance.definition, instance.levelIndex + 1);
        int currentCurrency = this.currencyForPlayer(context, actor);
        if (currentCurrency < upgradeCost) {
            return ActionResult.fail(this.choose(actor, "Недостаточно средств. Нужно: ", "Not enough currency. Need: ") + upgradeCost + this.choose(actor, ", сейчас: ", ", current: ") + currentCurrency + ".");
        }
        if (!this.spendCurrency(context, actor, upgradeCost)) {
            return ActionResult.fail(this.choose(actor, "Недостаточно средств.", "Not enough currency."));
        }
        this.trackLifetimeSpentCurrency(context, upgradeCost);
        instance.levelIndex++;
        this.playSound3d(world, SOUND_TOWER_UPGRADE, this.towerWorldPosition(instance.slot));
        this.refreshVisualizationIfEnabled(world);
        this.updateTutorialProgressionFromContext(world, context);
        return ActionResult.ok(
            this.choose(actor, "Башня улучшена: ", "Tower upgraded: ")
                + this.towerDisplayName(actor, instance.definition)
                + this.choose(actor, ". Площадка: ", ". Pad: ")
                + this.slotDisplayName(instance.slot)
                + this.choose(actor, ". Ур. ", ". Lvl. ")
                + instance.getCurrentLevel().level
                + this.choose(actor, ". Баланс: ", ". Balance: ")
                + this.currencyForPlayer(context, actor)
                + "."
        );
    }

    private ActionResult activateHeartOfRoots(World world, MatchContext context, PlayerRef actor, TowerInstance instance) {
        if (instance == null || !"heart_of_roots".equals(instance.definition.id)) {
            return ActionResult.fail(this.choose(actor, "Сердце корней не найдено.", "Heart of Roots not found."));
        }
        if (!instance.superReady) {
            if (this.remainingSuperActivations(context, instance) > 0) {
                return ActionResult.fail(this.choose(actor, "Сердце корней истощено. Сначала реактивируй его.", "Heart of Roots is exhausted. Reactivate it first."));
            }
            return ActionResult.fail(this.choose(actor, "Все активации Сердца корней уже израсходованы.", "All Heart of Roots activations are already spent."));
        }
        if (context.state.gameState != GameState.InMatch || context.activeWaveNumber <= 0) {
            return ActionResult.fail(this.choose(actor, "Сердце корней можно активировать только во время волны.", "Heart of Roots can only be activated during a wave."));
        }
        context.rootsHeartBuffWaveNumber = context.activeWaveNumber;
        context.rootsHeartBuffMultiplier = ROOTS_HEART_DAMAGE_BUFF_MULTIPLIER;
        instance.superActivationsUsed++;
        instance.superReady = false;
        this.showEventToast(
            context,
            this.choose(world, "Сердце корней активировано.", "Heart of Roots activated."),
            EVENT_TOAST_ICON_CORES
        );
        this.playSound3d(world, SOUND_HEART_ACTIVATE, this.towerWorldPosition(instance.slot));
        this.sendLocalizedWorldMessage(world, "Сердце корней активировано: все башни получают +30% урона до конца текущей волны.", "Heart of Roots activated: all towers gain +30% damage until the end of the current wave.");
        this.refreshVisualizationIfEnabled(world);
        int remainingActivations = this.remainingSuperActivations(context, instance);
        return ActionResult.ok(
            remainingActivations > 0
                ? this.choose(actor, "Сердце корней раскрыло силу дупла. После волны его можно реактивировать. Осталось активаций: ", "Heart of Roots unleashed the Hollow's power. It can be reactivated after the wave. Activations left: ") + remainingActivations + "."
                : this.choose(actor, "Сердце корней раскрыло силу дупла. Все активации исчерпаны.", "Heart of Roots unleashed the Hollow's power. All activations are spent.")
        );
    }

    public ActionResult setSeedIdolModeAt(World world, Vec3i targetPosition, int mode, int radius) throws IOException {
        return this.setSeedIdolModeAt(world, this.primaryPlayerRef(world), targetPosition, mode, radius);
    }

    public ActionResult setSeedIdolModeAt(World world, PlayerRef actor, Vec3i targetPosition, int mode, int radius) throws IOException {
        MatchContext context = this.getOrCreateContext(world);
        TowerInstance instance = this.findNearestPlacedTower(context, targetPosition, radius);
        if (instance == null || !"seed_idol".equals(instance.definition.id)) {
            return ActionResult.fail(this.choose(actor, "Рядом нет Идола урожая.", "No Harvest Idol found nearby."));
        }
        if (!this.canPlayerUseSlot(world, context, actor, instance.slot)) {
            return ActionResult.fail(this.deniedSlotAccessMessage(actor, instance.slot));
        }
        if (mode != SEED_IDOL_MODE_HARVEST && mode != SEED_IDOL_MODE_WARD) {
            return ActionResult.fail(this.choose(actor, "Неизвестный режим идола.", "Unknown idol mode."));
        }
        if (instance.specialMode == mode) {
            return ActionResult.ok(this.choose(actor, "Идол уже работает в режиме [", "The idol is already in mode [") + this.seedIdolModeName(actor, mode) + "].");
        }
        if (instance.specialMode == SEED_IDOL_MODE_WARD && mode != SEED_IDOL_MODE_WARD && instance.idolSavingsWavesRemaining > 0) {
            return ActionResult.fail(
                this.choose(actor, "Режим [Накопление] нельзя отключить ещё ", "The [Savings] mode cannot be disabled for another ")
                    + instance.idolSavingsWavesRemaining
                    + this.choose(actor, " волн.", " waves.")
            );
        }
        if (instance.specialMode == SEED_IDOL_MODE_WARD && instance.idolStoredCurrency > 0) {
            this.grantTeamIncome(context, instance.idolStoredCurrency);
            this.sendLocalizedWorldMessage(world, playerRef ->
                this.choose(playerRef, "Идол урожая перевёл накопленные ", "The Harvest Idol moved ")
                    + instance.idolStoredCurrency
                    + this.choose(playerRef, " монет в баланс при смене режима.", " stored gold into the balance when changing modes.")
            );
            instance.idolStoredCurrency = 0;
        }
        instance.specialMode = mode;
        instance.idolSavingsWavesRemaining = mode == SEED_IDOL_MODE_WARD ? 5 : 0;
        context.pendingIdolModeSlotByPlayer.values().removeIf(instance.slot.id::equals);
        context.pendingIdolModeOpenDelayByPlayer.keySet().removeIf(uuid -> !context.pendingIdolModeSlotByPlayer.containsKey(uuid));
        this.showEventToast(
            context,
            this.choose(world, "Идол урожая: режим [", "Harvest Idol: mode [") + this.seedIdolModeName(world, mode) + "].",
            EVENT_TOAST_ICON_CORES
        );
        this.playSound3d(world, SOUND_IDOL_MODE_SWITCH, this.towerWorldPosition(instance.slot));
        return ActionResult.ok(this.choose(actor, "Идол урожая переключён в режим [", "Harvest Idol switched to mode [") + this.seedIdolModeName(actor, mode) + "].");
    }

    private ActionResult reactivateSuperTower(World world, MatchContext context, PlayerRef actor, TowerInstance instance) {
        if (instance == null || !instance.definition.superTower || "seed_idol".equals(instance.definition.id)) {
            return ActionResult.fail(this.choose(actor, "Эту башню нельзя реактивировать.", "This tower cannot be reactivated."));
        }
        if (instance.superReady) {
            return ActionResult.fail(this.towerDisplayName(actor, instance.definition) + this.choose(actor, " уже активен.", " is already active."));
        }
        if (!BankDefenseMatchPhaseSupport.isBuildPhase(context.state)) {
            return ActionResult.fail(this.choose(actor, "Реактивация доступна только в фазу подготовки.", "Reactivation is only available during preparation."));
        }
        int remainingActivations = this.remainingSuperActivations(context, instance);
        if (remainingActivations <= 0) {
            return ActionResult.fail(this.choose(actor, "Все дополнительные активации уже израсходованы.", "All extra activations are already spent."));
        }
        instance.superReady = true;
        this.showEventToast(
            context,
            this.towerDisplayName(world, instance.definition) + this.choose(world, " реактивирован.", " reactivated."),
            EVENT_TOAST_ICON_CORES
        );
        this.playSound3d(world, SOUND_SUPER_REACTIVATE, this.towerWorldPosition(instance.slot));
        this.refreshVisualizationIfEnabled(world);
        return ActionResult.ok(
            this.towerDisplayName(actor, instance.definition) + this.choose(actor, " реактивирован. Доступно активаций: ", " reactivated. Activations available: ") + remainingActivations + "."
        );
    }

    private ActionResult sellTowerInstance(World world, MatchContext context, PlayerRef actor, TowerInstance instance) {
        if (instance.definition.superTower && !this.isDuoMode(context)) {
            return ActionResult.fail(this.choose(actor, "Супер-башни продавать нельзя.", "Super towers cannot be sold."));
        }
        if (!BankDefenseMatchPhaseSupport.isBuildPhase(context.state)) {
            return ActionResult.fail(this.choose(actor, "Продавать башни можно только в фазу подготовки.", "Towers can only be sold during preparation."));
        }
        if (instance.slot != null && !this.canPlayerUseSlot(world, context, actor, instance.slot)) {
            return ActionResult.fail(this.deniedSlotAccessMessage(actor, instance.slot));
        }
        int refund = (int)Math.floor(this.totalInvestedCost(context, instance) * this.sellRefundRate(context));
        String returnedModule = instance.equippedModuleId;
        if (returnedModule != null) {
            this.addModuleToInventory(context, returnedModule, 1);
        }
        if ("seed_idol".equals(instance.definition.id) && instance.idolStoredCurrency > 0) {
            refund += instance.idolStoredCurrency;
            instance.idolStoredCurrency = 0;
        }
        context.placedTowers.remove(instance.slot.id);
        context.pendingIdolModeSlotByPlayer.values().removeIf(instance.slot.id::equals);
        this.refundCurrencyToTeam(context, this.slotOwnerTeam(instance.slot), refund);
        this.playSound3d(world, SOUND_SELL, this.towerWorldPosition(instance.slot));
        this.refreshVisualizationIfEnabled(world);
        String moduleSuffix = returnedModule != null ? this.choose(actor, " Модуль возвращён в запас.", " Module returned to storage.") : "";
        return ActionResult.ok(
            this.choose(actor, "Башня продана: ", "Tower sold: ")
                + this.towerDisplayName(actor, instance.definition)
                + this.choose(actor, ". Площадка: ", ". Pad: ")
                + this.slotDisplayName(instance.slot)
                + this.choose(actor, ". Возврат: ", ". Refund: ")
                + refund
                + this.choose(actor, ". Баланс: ", ". Balance: ")
                + this.currencyForPlayer(context, actor)
                + "."
                + moduleSuffix
        );
    }

    private double sellRefundRate(MatchContext context) {
        if (context == null || context.snapshot == null) {
            return 0.0;
        }
        return Math.max(0.0, Math.min(0.95, context.snapshot.gameRules.sellRefundRate + this.progressionValue(context, "sell_refund_bonus", null)));
    }

    private boolean isSuperSlot(BuildSlot slot) {
        if (slot == null || slot.slotType == null || slot.slotType.isBlank()) {
            return false;
        }
        String slotType = slot.slotType.toLowerCase(Locale.ROOT);
        return "super".equals(slotType) || "tutorial_super".equals(slotType);
    }

    private boolean isTrapSlot(BuildSlot slot) {
        if (slot == null || slot.slotType == null || slot.slotType.isBlank()) {
            return false;
        }
        String slotType = slot.slotType.toLowerCase(Locale.ROOT);
        return "trap".equals(slotType) || "tutorial_trap".equals(slotType);
    }

    private boolean isDuoLayoutSlot(BuildSlot slot) {
        return slot != null && slot.layout != null && "duo".equalsIgnoreCase(slot.layout.trim());
    }

    private Vec3i duoBuildObjectiveAnchor(MatchContext context) {
        if (context == null || context.snapshot == null || context.snapshot.map == null) {
            return null;
        }
        MapConfig map = context.snapshot.map;
        if (map.duoVaultPoint != null) {
            return map.duoVaultPoint;
        }
        if (map.duoBankCenter != null) {
            return map.duoBankCenter;
        }
        if (map.vaultPoint != null) {
            return map.vaultPoint;
        }
        return map.bankCenter;
    }

    private TowerDefinition pickMostExpensiveTowerForSlot(MatchContext context, BuildSlot slot) {
        if (context == null || slot == null) {
            return null;
        }
        boolean superSlot = this.isSuperSlot(slot);
        boolean trapSlot = this.isTrapSlot(slot);
        TowerDefinition selected = null;
        int bestCost = Integer.MIN_VALUE;
        for (TowerDefinition tower : context.towerById.values()) {
            if (tower == null || tower.levels == null || tower.levels.isEmpty()) {
                continue;
            }
            if (slot.allowedTowerIds != null && !slot.allowedTowerIds.isEmpty() && !slot.allowedTowerIds.contains(tower.id)) {
                continue;
            }
            if (superSlot != tower.superTower) {
                continue;
            }
            if (trapSlot != tower.trapTower) {
                continue;
            }
            if (!superSlot && !trapSlot && (tower.superTower || tower.trapTower)) {
                continue;
            }
            int totalCost = this.totalTowerBaseCost(tower);
            if (selected == null || totalCost > bestCost) {
                selected = tower;
                bestCost = totalCost;
            }
        }
        return selected;
    }

    private int totalTowerBaseCost(TowerDefinition tower) {
        if (tower == null || tower.levels == null || tower.levels.isEmpty()) {
            return 0;
        }
        int total = 0;
        for (TowerLevel level : tower.levels) {
            if (level != null) {
                total += Math.max(0, level.unlockCost);
            }
        }
        return total;
    }

    private int towerLevelCost(MatchContext context, TowerDefinition tower, int levelIndex) {
        if (tower == null || tower.levels == null || levelIndex < 0 || levelIndex >= tower.levels.size()) {
            return 0;
        }
        int baseCost = Math.max(0, tower.levels.get(levelIndex).unlockCost);
        double discount = this.progressionValue(context, "tower_cost_percent", null)
            + this.progressionValue(context, "super_cost_percent", null);
        discount = Math.max(0.0, Math.min(0.5, discount));
        double cost = Math.max(1.0, baseCost * (1.0 - discount));
        if (levelIndex > 0 && this.hasActiveSealCurse(context, SealCurseType.UpgradeCost)) {
            cost *= DUO_SEAL_CURSE_UPGRADE_COST_MULTIPLIER;
        }
        return Math.max(1, (int)Math.round(cost));
    }

    private boolean isTowerUnlocked(MatchContext context, TowerDefinition tower) {
        if (tower == null) {
            return false;
        }
        if (!tower.superTower) {
            return true;
        }
        if (context == null || context.snapshot == null || context.snapshot.progression == null) {
            return true;
        }
        boolean usesExplicitUnlocks = false;
        for (ProgressionNodeDefinition node : context.snapshot.progression.nodes) {
            if (node == null || !"super_tower_unlock".equals(node.effectType)) {
                continue;
            }
            if (tower.id != null && tower.id.equals(node.targetId)) {
                usesExplicitUnlocks = true;
                break;
            }
        }
        if (!usesExplicitUnlocks) {
            return true;
        }
        return this.progressionValue(context, "super_tower_unlock", tower.id) > 0.0;
    }

    private int unlockedTowerLevelCap(MatchContext context, TowerDefinition tower) {
        if (tower == null) {
            return 0;
        }
        int cap = Math.max(1, tower.baseUnlockedLevel);
        double branchCap = this.progressionValue(context, "tower_level_cap_branch", tower.progressionBranch);
        double towerCap = this.progressionValue(context, "tower_level_cap_tower", tower.id);
        cap = Math.max(cap, (int)Math.round(branchCap));
        cap = Math.max(cap, (int)Math.round(towerCap));
        return Math.min(Math.max(1, cap), tower.levels == null ? cap : tower.levels.size());
    }

    private int maxSuperActivations(MatchContext context, TowerInstance instance) {
        if (context == null || instance == null || !instance.definition.superTower || "seed_idol".equals(instance.definition.id)) {
            return 0;
        }
        return Math.max(1, 1 + (int)Math.round(this.progressionValue(context, EFFECT_SUPER_ACTIVATION_BONUS, instance.definition.id)));
    }

    private int remainingSuperActivations(MatchContext context, TowerInstance instance) {
        return Math.max(0, this.maxSuperActivations(context, instance) - instance.superActivationsUsed);
    }

    private String superTowerDescription(PlayerRef playerRef, String towerId) {
        return switch (towerId) {
            case "heart_of_roots" -> this.choose(playerRef,
                "Во время волны запускает древний ритуал и усиливает всю линию до конца текущей волны.",
                "During a wave, it triggers an ancient ritual and empowers the whole lane until the end of the current wave.");
            case "storm_monolith" -> this.choose(playerRef,
                "При активации мгновенно снимает 50% от текущего здоровья у всех живых врагов и помогает стабилизировать линию.",
                "When activated, it instantly removes 50% of the current health from all living enemies and helps stabilize the lane.");
            case "seed_idol" -> this.choose(playerRef,
                "Экономическая супер-башня. Режим [Премия] даёт постоянный бонус к золоту, а режим [Накопление] собирает доход несколько волн и затем выплачивает его разом.",
                "An economic super tower. [Bounty] grants a steady gold bonus, while [Savings] stores income for several waves and then pays it out in one burst.");
            default -> "";
        };
    }

    private int totalInvestedCost(MatchContext context, TowerInstance instance) {
        if (instance == null || instance.definition == null) {
            return 0;
        }
        int total = 0;
        for (int i = 0; i <= instance.levelIndex && i < instance.definition.levels.size(); i++) {
            total += this.towerLevelCost(context, instance.definition, i);
        }
        return total;
    }

    private void applySeedIdolWaveReward(World world, MatchContext context, int completedWaveNumber) {
        if (context == null || completedWaveNumber <= 0) {
            return;
        }
        for (TowerInstance tower : context.placedTowers.values()) {
            if (!"seed_idol".equals(tower.definition.id) || tower.specialMode != SEED_IDOL_MODE_WARD) {
                continue;
            }
            if (tower.idolSavingsWavesRemaining > 0) {
                tower.idolSavingsWavesRemaining = Math.max(0, tower.idolSavingsWavesRemaining - 1);
            }
            if (tower.idolSavingsWavesRemaining > 0) {
                continue;
            }
            int stored = tower.idolStoredCurrency;
            int payout = stored <= 0 ? 0 : Math.max(stored, (int)Math.round(stored * SEED_IDOL_WARD_PAYOUT_MULTIPLIER));
            tower.idolStoredCurrency = 0;
            tower.specialMode = SEED_IDOL_MODE_DEFAULT;
            if (payout > 0) {
                this.grantTeamIncome(context, payout);
                this.sendLocalizedWorldMessage(world, playerRef ->
                    this.choose(playerRef, "Идол урожая [Накопление] выдал ", "Harvest Idol [Savings] paid out ")
                        + payout
                        + this.choose(playerRef, " монет после накопления ", " gold after storing ")
                        + stored
                        + "."
                );
                this.showIdolPayoutToast(world, context, payout, stored);
                this.playWorldUiSound(world, SOUND_REWARD);
                this.playWorldSfxSound(world, SOUND_COIN);
            }
            this.queueSeedIdolModePrompt(world, context, tower.slot == null ? null : tower.slot.id);
        }
    }

    private String seedIdolModeName(int mode) {
        return switch (mode) {
            case SEED_IDOL_MODE_HARVEST -> "Bounty";
            case SEED_IDOL_MODE_WARD -> "Savings";
            default -> "Not selected";
        };
    }

    private BuildSlot findNearestAvailableSlot(MatchContext context, Vec3i position, int radius) {
        BuildSlot nearest = null;
        double bestDistance = Double.MAX_VALUE;
        double maxDistanceSquared = (double)(radius * radius);
        for (BuildSlot slot : context.snapshot.buildSlots.slots) {
            if (slot.position == null || context.placedTowers.containsKey(slot.id)) {
                continue;
            }
            double distance = slot.position.distanceSquaredTo(position);
            if (distance > maxDistanceSquared || distance >= bestDistance) {
                continue;
            }
            bestDistance = distance;
            nearest = slot;
        }
        return nearest;
    }

    private BuildSlot findNearestSlot(MatchContext context, Vec3i position, int radius) {
        BuildSlot nearest = null;
        double bestDistance = Double.MAX_VALUE;
        double maxDistanceSquared = (double)(radius * radius);
        for (BuildSlot slot : context.snapshot.buildSlots.slots) {
            if (slot.position == null) {
                continue;
            }
            double distance = slot.position.distanceSquaredTo(position);
            if (distance > maxDistanceSquared || distance >= bestDistance) {
                continue;
            }
            bestDistance = distance;
            nearest = slot;
        }
        return nearest;
    }

    private void clearExpiredVisualEffects(World world, MatchContext context) {
    }

    private void tickProjectiles(World world, double deltaSeconds) {
    }

    private void spawnEnemies(World world, MatchContext context) {
        for (EnemyInstance enemy : this.spawnEnemies(context)) {
            this.onEnemySpawned(world, context, enemy);
        }
    }

    private void applyEnemyEffects(World world, MatchContext context, double deltaSeconds) {
        for (EnemyInstance enemy : context.enemies) {
            if (enemy.dying) {
                continue;
            }
            if ("goblin_bomber_boss".equals(enemy.definition.id)) {
                double routeLength = Math.max(0.01, this.routeLengthForLane(context, enemy.laneId));
                this.applyGoblinBossProgressPenalty(world, context, enemy, Math.max(0.0, Math.min(0.999, enemy.progress / routeLength)));
            }
        }
        this.applyContractEffects(world, context, deltaSeconds);
    }

    private void applyContractEffects(World world, MatchContext context, double deltaSeconds) {
        if (context == null || context.activeContract == null || context.state.gameState != GameState.InMatch) {
            return;
        }
        if ("necrotic_mist".equals(context.activeContract.id)) {
            this.applyNecroticMistContract(world, context, deltaSeconds);
        }
    }

    private void applyNecroticMistContract(World world, MatchContext context, double deltaSeconds) {
        if (context == null) {
            return;
        }
        context.contractEffectCooldownRemaining = Math.max(0.0, context.contractEffectCooldownRemaining - deltaSeconds);
        if (context.contractEffectCooldownRemaining > 0.0) {
            return;
        }
        EnemyDefinition summonDefinition = context.enemyById.get("grave_spawn");
        if (summonDefinition == null) {
            context.contractEffectCooldownRemaining = 8.0;
            return;
        }
        double farthestProgress = 0.0;
        for (EnemyInstance enemy : context.enemies) {
            if (enemy.dying) {
                continue;
            }
            farthestProgress = Math.max(farthestProgress, enemy.progress);
        }
        if (farthestProgress < 3.0) {
            context.contractEffectCooldownRemaining = 6.0;
            return;
        }
        List<String> lanes = this.availableLaneIds(context);
        if (lanes.isEmpty()) {
            lanes = List.of("a");
        }
        int spawnCount = context.activeWaveNumber >= 18 ? 3 : 2;
        int spawned = 0;
        for (int i = 0; i < spawnCount; i++) {
            String laneId = lanes.get(ThreadLocalRandom.current().nextInt(lanes.size()));
            double routeLength = this.routeLengthForLane(context, laneId);
            double progress = Math.min(
                Math.max(0.0, farthestProgress * ThreadLocalRandom.current().nextDouble(0.18, 0.82)),
                Math.max(0.0, routeLength - 0.4)
            );
            EnemyInstance summoned = this.spawnSummonedEnemy(context, summonDefinition, laneId, progress);
            if (summoned == null) {
                continue;
            }
            context.enemies.add(summoned);
            spawned++;
        }
        context.contractEffectCooldownRemaining = context.activeWaveNumber >= 20 ? 7.0 : 8.5;
        if (spawned > 0) {
            this.playWorldUiSound(world, SOUND_LEAK);
        }
    }

    private void tickTowerDisableEffects(World world, MatchContext context, double deltaSeconds) {
    }

    private void onEnemySpawned(World world, MatchContext context, EnemyInstance enemy) {
        if (enemy == null || enemy.dying) {
            return;
        }
        Vector3d position = this.enemyWorldPosition(context, enemy);
        if (enemy.definition.bossEnemy) {
            this.playSound3d(world, SOUND_ENEMY_SPAWN_GENERIC, position);
            this.playSound3d(world, this.enemyAccentSpawnSoundFor(enemy), position);
        }
        if (ENEMY_GOBLIN_SABOTEUR.equals(enemy.definition.id)) {
            this.triggerGoblinSaboteur(world, context, enemy);
        }
    }

    private void triggerGoblinSaboteur(World world, MatchContext context, EnemyInstance enemy) {
        this.playSound3d(world, SOUND_ENEMY_SPAWN_GOBLIN, this.enemyWorldPosition(context, enemy));
        TowerInstance target = this.randomPlacedTower(context, false, context.lastSaboteurDestroyedSlotId);
        if (target == null) {
            this.sendLocalizedWorldMessage(world, "Гоблин-саботажник выскочил на поле, но не нашёл башню для подрыва.", "The goblin saboteur rushed onto the field but found no tower to blow up.");
            return;
        }
        int destroyedLevel = target.getCurrentLevel().level;
        int refund = this.destroyTowerWithRefund(world, context, target, SABOTEUR_REFUND_RATE, false);
        context.lastSaboteurDestroyedSlotId = target.slot == null ? "" : target.slot.id;
        this.sendLocalizedWorldMessage(world, playerRef ->
            this.choose(playerRef, "Гоблин-саботажник уничтожил ", "The goblin saboteur destroyed ")
                + this.towerDisplayName(playerRef, target.definition)
                + this.choose(playerRef, " (Ур. ", " (Lvl. ")
                + destroyedLevel
                + ")."
                + this.choose(playerRef, " Возврат: только ", " Refund: only ")
                + refund
                + this.choose(playerRef, " монет.", " gold.")
        );
        this.showEventToast(
            context,
            this.choose(world, "Гоблин-саботажник уничтожил ", "The goblin saboteur destroyed ")
                + this.towerDisplayName(world, target.definition)
                + this.choose(world, " (Ур. ", " (Lvl. ")
                + destroyedLevel
                + ").",
            EVENT_TOAST_ICON_ALERT
        );
        this.playWorldUiSound(world, SOUND_LEAK);
    }

    private int destroyTowerWithRefund(World world, MatchContext context, TowerInstance instance, double refundRate, boolean returnModule) {
        if (instance == null) {
            return 0;
        }
        int refund = Math.max(0, (int)Math.floor(this.totalInvestedCost(context, instance) * Math.max(0.0, refundRate)));
        if (returnModule && instance.equippedModuleId != null) {
            this.addModuleToInventory(context, instance.equippedModuleId, 1);
        }
        context.placedTowers.remove(instance.slot.id);
        if (refund > 0) {
            this.refundCurrencyToTeam(context, this.slotOwnerTeam(instance.slot), refund);
        }
        this.playSound3d(world, SOUND_GOBLIN_DESTROY, this.towerWorldPosition(instance.slot));
        this.refreshVisualizationIfEnabled(world);
        return refund;
    }

    private TowerInstance randomPlacedTower(MatchContext context, boolean includeSupers) {
        return this.randomPlacedTower(context, includeSupers, null);
    }

    private TowerInstance randomPlacedTower(MatchContext context, boolean includeSupers, String excludedSlotId) {
        List<TowerInstance> candidates = new ArrayList<>();
        for (TowerInstance tower : context.placedTowers.values()) {
            if (!includeSupers && tower.definition.superTower) {
                continue;
            }
            candidates.add(tower);
        }
        if (candidates.isEmpty()) {
            return null;
        }
        if (excludedSlotId != null && !excludedSlotId.isBlank() && candidates.size() > 1) {
            List<TowerInstance> filtered = new ArrayList<>();
            for (TowerInstance tower : candidates) {
                if (tower == null || tower.slot == null || excludedSlotId.equals(tower.slot.id)) {
                    continue;
                }
                filtered.add(tower);
            }
            if (!filtered.isEmpty()) {
                candidates = filtered;
            }
        }
        return candidates.get(ThreadLocalRandom.current().nextInt(candidates.size()));
    }

    private TowerInstance randomDowngradeTarget(MatchContext context) {
        return this.randomDowngradeTarget(context, null);
    }

    private TowerInstance randomDowngradeTarget(MatchContext context, String excludedSlotId) {
        List<TowerInstance> candidates = new ArrayList<>();
        for (TowerInstance tower : context.placedTowers.values()) {
            if (tower.definition.superTower || tower.levelIndex <= 0) {
                continue;
            }
            candidates.add(tower);
        }
        if (candidates.isEmpty()) {
            return null;
        }
        if (excludedSlotId != null && !excludedSlotId.isBlank() && candidates.size() > 1) {
            List<TowerInstance> filtered = new ArrayList<>();
            for (TowerInstance tower : candidates) {
                if (tower == null || tower.slot == null || excludedSlotId.equals(tower.slot.id)) {
                    continue;
                }
                filtered.add(tower);
            }
            if (!filtered.isEmpty()) {
                candidates = filtered;
            }
        }
        return candidates.get(ThreadLocalRandom.current().nextInt(candidates.size()));
    }

    private void applyGoblinBossProgressPenalty(World world, MatchContext context, EnemyInstance enemy, double progressRatio) {
        double clampedRatio = Math.max(0.0, Math.min(1.0, progressRatio));
        int totalAvailableCurrency = Math.max(0, context.state.currency + enemy.goblinCurrencyStolen);
        int desiredStolen = Math.max(enemy.goblinCurrencyStolen, (int)Math.floor(totalAvailableCurrency * clampedRatio));
        int delta = Math.max(0, desiredStolen - enemy.goblinCurrencyStolen);
        if (delta > 0) {
            int stolenNow = Math.min(context.state.currency, delta);
            context.state.currency -= stolenNow;
            enemy.goblinCurrencyStolen += stolenNow;
        }

        if (!enemy.isBossCasting()
            && enemy.goblinDowngradeStage < GOBLIN_BOSS_DOWNGRADE_THRESHOLDS.length
            && clampedRatio >= GOBLIN_BOSS_DOWNGRADE_THRESHOLDS[enemy.goblinDowngradeStage]) {
            this.beginBossCast(world, context, enemy, BOSS_CAST_GOBLIN_SABOTAGE, BOSS_CAST_GOBLIN_SECONDS);
        }
    }

    private void resolveGoblinBossSabotage(World world, MatchContext context, EnemyInstance enemy) {
        if (enemy.goblinDowngradeStage >= GOBLIN_BOSS_DOWNGRADE_THRESHOLDS.length) {
            return;
        }
        TowerInstance target = this.randomDowngradeTarget(context, context.lastGoblinBomberDowngradedSlotId);
        enemy.goblinDowngradeStage++;
        this.playSound3d(world, SOUND_BOSS_CAST_GOBLIN, this.enemyWorldPosition(context, enemy));
        if (target == null) {
            this.sendLocalizedWorldMessage(world, "Гоблин-босс разворошил защиту, но не нашёл башню для понижения уровня.", "The goblin boss disrupted the defense but found no tower to downgrade.");
            return;
        }
        context.lastGoblinBomberDowngradedSlotId = target.slot == null ? "" : target.slot.id;
        target.levelIndex = Math.max(0, target.levelIndex - 1);
        target.cooldownRemaining = Math.max(target.cooldownRemaining, 0.25);
        this.playSound3d(world, SOUND_GOBLIN_DOWNGRADE, this.towerWorldPosition(target.slot));
        this.refreshVisualizationIfEnabled(world);
        this.sendLocalizedWorldMessage(world, playerRef ->
            this.choose(playerRef, "Гоблин-босс ослабил ", "The goblin boss weakened ")
                + this.towerDisplayName(playerRef, target.definition)
                + this.choose(playerRef, " до Ур. ", " to Lvl. ")
                + target.getCurrentLevel().level
                + this.choose(playerRef, ". Украдено монет: ", ". Gold stolen: ")
                + enemy.goblinCurrencyStolen
                + "."
        );
        String downgradeToast = this.choose(world, "Гоблин-босс понизил ", "The goblin boss downgraded ")
            + this.towerDisplayName(world, target.definition)
            + this.choose(world, " до Ур. ", " to Lvl. ")
            + target.getCurrentLevel().level
            + ".";
        this.showEventToast(context, downgradeToast, EVENT_TOAST_ICON_ALERT);
        this.scheduleEventToast(context, downgradeToast, EVENT_TOAST_ICON_ALERT, 0.9);
        this.playWorldUiSound(world, SOUND_NOTIFICATION);
    }

    private double progressionValue(MatchContext context, String effectType, String targetId) {
        if (context == null) {
            return 0.0;
        }
        return this.progressionValue(context.snapshot, context.progression, effectType, targetId);
    }

    private double progressionValue(
        BankDefenseRepository.Snapshot snapshot,
        PlayerProgressionState progression,
        String effectType,
        String targetId
    ) {
        if (snapshot == null || progression == null || progression.unlockedNodeIds == null || progression.unlockedNodeIds.isEmpty()) {
            return 0.0;
        }
        Set<String> unlocked = new HashSet<>(progression.unlockedNodeIds);
        double total = 0.0;
        for (ProgressionNodeDefinition node : snapshot.progression.nodes) {
            if (node == null || node.id == null || !unlocked.contains(node.id)) {
                continue;
            }
            if (effectType != null && !effectType.equals(node.effectType)) {
                continue;
            }
            if (targetId != null && node.targetId != null && !targetId.equals(node.targetId)) {
                continue;
            }
            total += node.value;
        }
        return total;
    }

    private static Map<String, Integer> createLegacyProgressionRefundCosts() {
        Map<String, Integer> costs = new HashMap<>();
        costs.put("refund_1", 10);
        costs.put("refund_2", 18);
        costs.put("refund_3", 28);
        costs.put("archer_cap_6", 16);
        costs.put("archer_cap_7", 24);
        costs.put("archer_dmg_1", 12);
        costs.put("archer_dmg_2", 22);
        costs.put("guard_cap_10", 46);
        costs.put("sniper_cap_10", 46);
        costs.put("swarm_cap_6", 16);
        costs.put("swarm_cap_7", 24);
        costs.put("swarm_dmg_1", 12);
        costs.put("swarm_dmg_2", 22);
        costs.put("rapid_cap_10", 46);
        costs.put("shock_cap_10", 46);
        costs.put("frost_cap_6", 18);
        costs.put("frost_cap_7", 28);
        costs.put("frost_dmg_1", 14);
        costs.put("frost_dmg_2", 24);
        costs.put("freeze_cap_10", 52);
        costs.put("siege_cap_6", 18);
        costs.put("siege_cap_7", 28);
        costs.put("siege_dmg_1", 14);
        costs.put("siege_dmg_2", 24);
        costs.put("drill_cap_10", 52);
        costs.put("roots_unlock", 28);
        costs.put("monolith_unlock", 34);
        costs.put("idol_unlock", 30);
        costs.put("roots_charge_1", 36);
        costs.put("roots_charge_2", 58);
        costs.put("monolith_charge_1", 42);
        costs.put("monolith_charge_2", 68);
        costs.put("super_dmg_1", 24);
        costs.put("super_dmg_2", 38);
        return costs;
    }

    private void addModuleToInventory(MatchContext context, String moduleId, int delta) {
        if (moduleId == null || moduleId.isBlank() || delta == 0) {
            return;
        }
        int nextValue = context.moduleInventory.getOrDefault(moduleId, 0) + delta;
        if (nextValue <= 0) {
            context.moduleInventory.remove(moduleId);
        } else {
            context.moduleInventory.put(moduleId, nextValue);
        }
    }

    private void queueRewardChoicesIfNeeded(World world, MatchContext context, int completedWaveNumber) {
        if (completedWaveNumber <= 0 || context.snapshot.gameRules.moduleRewardIntervalWaves <= 0) {
            return;
        }
        if (context.tutorialActive) {
            if (completedWaveNumber != 2) {
                return;
            }
        } else if (completedWaveNumber % context.snapshot.gameRules.moduleRewardIntervalWaves != 0) {
            return;
        }
        if (context.lastRewardPreparedWave == completedWaveNumber) {
            return;
        }
        List<String> pool = new ArrayList<>();
        for (ModuleDefinition module : context.snapshot.modules.modules) {
            if (module.id != null && !module.id.isBlank()) {
                pool.add(module.id);
            }
        }
        if (pool.isEmpty()) {
            return;
        }
        Collections.shuffle(pool, ThreadLocalRandom.current());
        this.prepareRewardChoices(context, pool, completedWaveNumber);
        this.playWorldUiSound(world, SOUND_REWARD);
        if (world != null) {
            if (this.isDuoMode(context)) {
                this.sendLocalizedWorldMessage(world, playerRef -> {
                    String firstPicker = TEAM_GREEN.equals(context.rewardPickerTeam)
                        ? this.choose(playerRef, "зелёной", "green")
                        : this.choose(playerRef, "синей", "blue");
                    return this.choose(playerRef, "Волна ", "Wave ")
                        + completedWaveNumber
                        + this.choose(playerRef, " завершена. ", " completed. ")
                        + this.choose(playerRef, "Игрок ", "The ")
                        + firstPicker
                        + this.choose(playerRef, " команды должен выбрать модуль у оператора.", " team player must choose a module at the operator.");
                });
            } else {
                this.sendLocalizedWorldMessage(world, playerRef ->
                    this.choose(playerRef, "Волна ", "Wave ")
                        + completedWaveNumber
                        + this.choose(playerRef, " завершена. Выберите модуль у оператора.", " completed. Choose the module at the operator.")
                );
            }
            this.showEventToast(
                context,
                this.choose(world, "Выберите модуль у оператора.", "Choose the module at the operator."),
                EVENT_TOAST_ICON_ALERT
            );
            this.scheduleEventToast(
                context,
                this.choose(world, "Выберите модуль у оператора.", "Choose the module at the operator."),
                EVENT_TOAST_ICON_ALERT,
                1.0
            );
            this.openRewardPages(world);
            return;
        }
        this.openRewardPages(world);
    }

    private String fireSoundForTower(String towerId) {
        if ("sniper_desk".equals(towerId)) {
            return SOUND_FIRE_CROSSBOW;
        }
        if ("rapid_security".equals(towerId)) {
            return SOUND_FIRE_DART;
        }
        if ("freeze_gate".equals(towerId)) {
            return SOUND_FIRE_FROST;
        }
        if ("shock_relay".equals(towerId)) {
            return SOUND_FIRE_SHOCK;
        }
        if ("armor_drill".equals(towerId)) {
            return SOUND_FIRE_DRILL;
        }
        return SOUND_FIRE_ARCHER;
    }

    private String impactSoundForTower(String towerId) {
        if ("sniper_desk".equals(towerId) || "guard_post".equals(towerId)) {
            return SOUND_IMPACT_ARROW;
        }
        return SOUND_IMPACT;
    }

    private String enemyAccentSpawnSoundFor(EnemyInstance enemy) {
        if (enemy == null || enemy.definition == null || enemy.definition.id == null) {
            return SOUND_ENEMY_SPAWN_UNDEAD;
        }
        String enemyId = enemy.definition.id;
        if (ENEMY_SEAL_MASTER.equals(enemyId) || ENEMY_RIFT_TWIN_ALPHA.equals(enemyId) || ENEMY_RIFT_TWIN_BETA.equals(enemyId)) {
            return SOUND_ENEMY_SPAWN_VOID;
        }
        if (this.isGoblinEnemy(enemyId) || ENEMY_NODE_ARBITER.equals(enemyId)) {
            return SOUND_ENEMY_SPAWN_GOBLIN;
        }
        return SOUND_ENEMY_SPAWN_UNDEAD;
    }

    private String enemyDeathSoundFor(EnemyInstance enemy) {
        if (enemy == null || enemy.definition == null || enemy.definition.id == null) {
            return SOUND_ENEMY_DEATH_UNDEAD;
        }
        String enemyId = enemy.definition.id;
        if (enemy.definition.bossEnemy) {
            return SOUND_ENEMY_DEATH_BOSS;
        }
        if (this.isSkeletonFamilyEnemy(enemyId)) {
            return SOUND_ENEMY_DEATH_SKELETON;
        }
        if (this.isGoblinEnemy(enemyId)) {
            return SOUND_ENEMY_DEATH_GOBLIN;
        }
        if (this.isVoidEnemy(enemyId)) {
            return SOUND_ENEMY_DEATH_VOID;
        }
        return SOUND_ENEMY_DEATH_UNDEAD;
    }

    private boolean isGoblinEnemy(String enemyId) {
        return ENEMY_GOBLIN_SABOTEUR.equals(enemyId) || "goblin_bomber_boss".equals(enemyId);
    }

    private boolean isVoidEnemy(String enemyId) {
        return ENEMY_SEAL_MASTER.equals(enemyId) || ENEMY_RIFT_TWIN_ALPHA.equals(enemyId) || ENEMY_RIFT_TWIN_BETA.equals(enemyId);
    }

    private boolean isSkeletonFamilyEnemy(String enemyId) {
        return "thief".equals(enemyId)
            || "bone_guard".equals(enemyId)
            || "jammer".equals(enemyId)
            || "necro_thief".equals(enemyId)
            || "vault_priest".equals(enemyId)
            || "bone_trumpeter".equals(enemyId)
            || "curse_weaver".equals(enemyId)
            || "elite_robber".equals(enemyId)
            || "necro_guardian".equals(enemyId)
            || ENEMY_SEAL_NODE.equals(enemyId);
    }

    private void openRewardPages(World world) {
        Store<EntityStore> store = world.getEntityStore().getStore();
        for (PlayerRef playerRef : world.getPlayerRefs()) {
            Ref<EntityStore> ref = playerRef.getReference();
            if (ref == null || !ref.isValid()) {
                continue;
            }
            Player player = store.getComponent(ref, Player.getComponentType());
            if (player == null) {
                continue;
            }
            this.playUiSoundForPlayer(world, playerRef, SOUND_NOTIFICATION);
            player.getPageManager().openCustomPage(ref, store, new BankDefenseRewardPage(playerRef, this));
        }
    }

    private void tickPendingRewardPageOpens(World world, MatchContext context, double deltaSeconds) {
        if (world == null || context == null || context.pendingRewardPageOpenDelayByPlayer.isEmpty()) {
            return;
        }
        Iterator<Map.Entry<UUID, Double>> iterator = context.pendingRewardPageOpenDelayByPlayer.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<UUID, Double> entry = iterator.next();
            UUID playerUuid = entry.getKey();
            if (playerUuid == null) {
                iterator.remove();
                continue;
            }
            double remaining = Math.max(0.0, entry.getValue() - Math.max(0.0, deltaSeconds));
            if (remaining > 0.0) {
                entry.setValue(remaining);
                continue;
            }
            iterator.remove();
            if (context.pendingRewardChoices.isEmpty()) {
                continue;
            }
            PlayerRef target = null;
            for (PlayerRef playerRef : world.getPlayerRefs()) {
                if (playerRef != null && playerUuid.equals(playerRef.getUuid())) {
                    target = playerRef;
                    break;
                }
            }
            if (target == null) {
                continue;
            }
            try {
                this.reopenPendingRewardPage(world, target);
            } catch (IOException ignored) {
            }
        }
    }

    private void queueSeedIdolModePrompt(World world, MatchContext context, String slotId) {
        if (world == null || context == null || slotId == null || slotId.isBlank()) {
            return;
        }
        for (PlayerRef playerRef : world.getPlayerRefs()) {
            if (playerRef == null || playerRef.getUuid() == null) {
                continue;
            }
            TowerInstance tower = context.placedTowers.get(slotId);
            if (tower == null || tower.slot == null || !this.canPlayerUseSlot(world, context, playerRef, tower.slot)) {
                continue;
            }
            context.pendingIdolModeSlotByPlayer.put(playerRef.getUuid(), slotId);
        }
    }

    private void schedulePendingSeedIdolModeChoiceOpens(World world, MatchContext context, double delaySeconds) {
        if (world == null || context == null || context.pendingIdolModeSlotByPlayer.isEmpty()) {
            return;
        }
        double safeDelay = Math.max(0.05, delaySeconds);
        for (PlayerRef playerRef : world.getPlayerRefs()) {
            if (playerRef == null || playerRef.getUuid() == null) {
                continue;
            }
            if (!context.pendingIdolModeSlotByPlayer.containsKey(playerRef.getUuid())) {
                continue;
            }
            context.pendingIdolModeOpenDelayByPlayer.put(playerRef.getUuid(), safeDelay);
        }
    }

    public boolean openPendingSeedIdolModeChoice(World world, PlayerRef playerRef) throws IOException {
        MatchContext context = this.getOrCreateContext(world);
        return this.openPendingSeedIdolModeChoice(world, context, playerRef);
    }

    public void schedulePendingSeedIdolModeChoiceOpen(World world, PlayerRef playerRef, double delaySeconds) {
        if (world == null || playerRef == null || playerRef.getUuid() == null) {
            return;
        }
        MatchContext context = this.matchesByWorld.get(this.worldKey(world));
        if (context == null || !context.pendingIdolModeSlotByPlayer.containsKey(playerRef.getUuid())) {
            return;
        }
        context.pendingIdolModeOpenDelayByPlayer.put(playerRef.getUuid(), Math.max(0.05, delaySeconds));
    }

    private boolean openPendingSeedIdolModeChoices(World world, MatchContext context) {
        if (world == null || context == null || context.pendingIdolModeSlotByPlayer.isEmpty()) {
            return false;
        }
        boolean opened = false;
        for (PlayerRef playerRef : world.getPlayerRefs()) {
            opened |= this.openPendingSeedIdolModeChoice(world, context, playerRef);
        }
        return opened;
    }

    private boolean openPendingSeedIdolModeChoice(World world, MatchContext context, PlayerRef playerRef) {
        if (world == null || context == null || playerRef == null || playerRef.getUuid() == null) {
            return false;
        }
        String slotId = context.pendingIdolModeSlotByPlayer.get(playerRef.getUuid());
        if (slotId == null || slotId.isBlank()) {
            return false;
        }
        Store<EntityStore> store = world.getEntityStore().getStore();
        Ref<EntityStore> ref = playerRef.getReference();
        if (ref == null || !ref.isValid()) {
            context.pendingIdolModeSlotByPlayer.remove(playerRef.getUuid());
            return false;
        }
        Player player = store.getComponent(ref, Player.getComponentType());
        if (player == null) {
            context.pendingIdolModeSlotByPlayer.remove(playerRef.getUuid());
            return false;
        }
        context.pendingIdolModeOpenDelayByPlayer.remove(playerRef.getUuid());
        this.playUiSoundForPlayer(world, playerRef, SOUND_ALERT);
        player.getPageManager().openCustomPage(ref, store, new BankDefenseSuperSlotPage(playerRef, this, slotId));
        return true;
    }

    private void tickPendingSeedIdolModeChoiceOpens(World world, MatchContext context, double deltaSeconds) {
        if (world == null || context == null || context.pendingIdolModeOpenDelayByPlayer.isEmpty()) {
            return;
        }
        Iterator<Map.Entry<UUID, Double>> iterator = context.pendingIdolModeOpenDelayByPlayer.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<UUID, Double> entry = iterator.next();
            UUID playerUuid = entry.getKey();
            if (playerUuid == null || !context.pendingIdolModeSlotByPlayer.containsKey(playerUuid)) {
                iterator.remove();
                continue;
            }
            double remaining = Math.max(0.0, entry.getValue() - Math.max(0.0, deltaSeconds));
            if (remaining > 0.0) {
                entry.setValue(remaining);
                continue;
            }
            if (context.state != null && context.state.rewardPending) {
                entry.setValue(0.08);
                continue;
            }
            PlayerRef target = null;
            for (PlayerRef playerRef : world.getPlayerRefs()) {
                if (playerRef != null && playerUuid.equals(playerRef.getUuid())) {
                    target = playerRef;
                    break;
                }
            }
            iterator.remove();
            if (target == null) {
                continue;
            }
            this.openPendingSeedIdolModeChoice(world, context, target);
        }
    }

    private TowerInstance findNearestPlacedTower(MatchContext context, Vec3i position, int radius) {
        TowerInstance nearest = null;
        double bestDistance = Double.MAX_VALUE;
        double maxDistanceSquared = (double)(radius * radius);
        for (TowerInstance tower : context.placedTowers.values()) {
            double distance = tower.slot.position.distanceSquaredTo(position);
            if (distance > maxDistanceSquared || distance >= bestDistance) {
                continue;
            }
            bestDistance = distance;
            nearest = tower;
        }
        return nearest;
    }

    private Vector3d towerWorldPosition(BuildSlot slot) {
        double yOffset = this.isTrapSlot(slot) ? 0.72 : 0.25;
        return new Vector3d(slot.position.x + 0.5, slot.position.y + yOffset, slot.position.z + 0.5);
    }

    private double trapBlinkIntervalSeconds(TowerInstance tower) {
        if (tower == null || tower.trapPendingRemaining <= 0.0) {
            return TRAP_BLINK_INTERVAL_END_SECONDS;
        }
        double trapDelay = Math.max(0.1, tower.trapPendingTotal <= 0.0 ? TRAP_TRIGGER_DELAY_SECONDS : tower.trapPendingTotal);
        double progress = 1.0 - Math.max(0.0, Math.min(1.0, tower.trapPendingRemaining / trapDelay));
        return TRAP_BLINK_INTERVAL_START_SECONDS
            + (TRAP_BLINK_INTERVAL_END_SECONDS - TRAP_BLINK_INTERVAL_START_SECONDS) * progress;
    }

    private Vector3d objectiveWorldPosition(Vec3i point, double yOffset) {
        return new Vector3d(point.x + 0.5, point.y + yOffset, point.z + 0.5);
    }

    private Vector3d enemyWorldPosition(MatchContext context, EnemyInstance enemy) {
        return this.pointAtProgress(context, enemy.laneId, enemy.progress).toWorldVector(0.1);
    }

    private void syncEnemyMovementAnimation(Store<EntityStore> store, Ref<EntityStore> ref, EnemyInstance enemy) {
        enemy.currentMovementAnimationId = this.syncEnemyMovementAnimation(store, ref, enemy, enemy.currentMovementAnimationId);
    }

    private String syncEnemyMovementAnimation(
        Store<EntityStore> store,
        Ref<EntityStore> ref,
        EnemyInstance enemy,
        String currentMovementAnimationId
    ) {
        if (store == null || ref == null || !ref.isValid() || enemy == null) {
            return currentMovementAnimationId == null ? "" : currentMovementAnimationId;
        }
        ModelComponent modelComponent = store.getComponent(ref, ModelComponent.getComponentType());
        if (modelComponent == null || modelComponent.getModel() == null) {
            return currentMovementAnimationId == null ? "" : currentMovementAnimationId;
        }
        String animationId = this.enemyMovementAnimationId(modelComponent.getModel(), enemy);
        this.syncEnemyMovementStates(store, ref, enemy, animationId);
        if (animationId == null || animationId.isBlank()) {
            return currentMovementAnimationId == null ? "" : currentMovementAnimationId;
        }
        if (animationId.equals(currentMovementAnimationId)) {
            return animationId;
        }
        AnimationUtils.playAnimation(
            ref,
            enemy.isBossCasting() ? AnimationSlot.Action : AnimationSlot.Movement,
            animationId,
            store
        );
        return animationId;
    }

    private String enemyMovementAnimationId(Model model, EnemyInstance enemy) {
        if (model == null || enemy == null) {
            return null;
        }
        if (enemy.isBossCasting()) {
            String cast = model.getFirstBoundAnimationId(new String[]{"Attack", "Cast", "Spell", "Special", "Roar", "Shout", "Smash", "Slam", "Melee", "Punch", "Action"});
            if (cast != null && !cast.isBlank()) {
                return cast;
            }
            String idle = model.getFirstBoundAnimationId(new String[]{"Idle"});
            if (idle != null && !idle.isBlank()) {
                return idle;
            }
        }
        if (enemy.dying || enemy.rootedRemaining > 0.0) {
            return model.getFirstBoundAnimationId(new String[]{"Idle"});
        }
        if ("runner".equals(enemy.definition.id) || "runner_bomber".equals(enemy.definition.id) || enemy.speed >= 1.1) {
            String run = model.getFirstBoundAnimationId(new String[]{"Run", "Sprint", "Walk"});
            if (run != null && !run.isBlank()) {
                return run;
            }
        }
        String walk = model.getFirstBoundAnimationId(new String[]{"Walk", "Idle"});
        if (walk != null && !walk.isBlank()) {
            return walk;
        }
        return model.getFirstBoundAnimationId(new String[]{"Idle"});
    }

    private void syncEnemyMovementStates(Store<EntityStore> store, Ref<EntityStore> ref, EnemyInstance enemy, String animationId) {
        if (store == null || ref == null || !ref.isValid() || enemy == null) {
            return;
        }
        MovementStatesComponent movementStatesComponent = store.getComponent(ref, MovementStatesComponent.getComponentType());
        if (movementStatesComponent == null) {
            return;
        }
        MovementStates current = movementStatesComponent.getMovementStates();
        MovementStates updated = current == null ? new MovementStates() : new MovementStates(current);

        boolean immobile = enemy.dying || enemy.rootedRemaining > 0.0 || enemy.isBossCasting();
        boolean running = !immobile && (
            "runner".equals(enemy.definition.id)
                || "runner_bomber".equals(enemy.definition.id)
                || enemy.speed >= 1.1
                || (animationId != null && (animationId.contains("Run") || animationId.contains("Sprint")))
        );

        updated.idle = immobile;
        updated.horizontalIdle = immobile;
        updated.walking = !immobile && !running;
        updated.running = running;
        updated.sprinting = !immobile && (enemy.speed >= 1.35 || (animationId != null && animationId.contains("Sprint")));
        updated.onGround = true;
        updated.jumping = false;
        updated.flying = false;
        updated.crouching = false;
        updated.forcedCrouching = false;
        updated.falling = false;
        updated.climbing = false;
        updated.inFluid = false;
        updated.swimming = false;
        updated.swimJumping = false;
        updated.mantling = false;
        updated.sliding = false;
        updated.mounting = false;
        updated.rolling = false;
        updated.sitting = false;
        updated.gliding = false;
        updated.sleeping = false;

        if (!updated.equals(current)) {
            movementStatesComponent.setMovementStates(updated);
        }
    }

    private Vector3f rotationTowards(Vector3d from, Vector3d to) {
        Vector3d relative = new Vector3d(to.x - from.x, to.y - from.y, to.z - from.z);
        if (Math.abs(relative.x) < 0.0001 && Math.abs(relative.y) < 0.0001 && Math.abs(relative.z) < 0.0001) {
            return new Vector3f(0.0f, 0.0f, 0.0f);
        }
        return Vector3f.lookAt(relative);
    }

    private Vector3f playerModelRotationTowards(Vector3d from, Vector3d to) {
        Vector3d relative = new Vector3d(to.x - from.x, to.y - from.y, to.z - from.z);
        if (Math.abs(relative.x) < 0.0001 && Math.abs(relative.y) < 0.0001 && Math.abs(relative.z) < 0.0001) {
            return new Vector3f(0.0f, 0.0f, 0.0f);
        }
        Vector3d corrected = new Vector3d(relative.z, relative.y, -relative.x);
        return Vector3f.lookAt(corrected);
    }

    private Vector3f playerHeadRotationFromYaw(float yawRadians) {
        return new Vector3f(0.0f, yawRadians, 0.0f);
    }

    private Vector3f playerModelRotationFromYaw(float yawRadians) {
        Vector3d from = new Vector3d(0.0, 0.0, 0.0);
        Vector3d to = new Vector3d(Math.sin(yawRadians), 0.0, Math.cos(yawRadians));
        return this.playerModelRotationTowards(from, to);
    }

    private Vector3d lerp(Vector3d from, Vector3d to, double t) {
        return new Vector3d(
            from.x + (to.x - from.x) * t,
            from.y + (to.y - from.y) * t,
            from.z + (to.z - from.z) * t
        );
    }

    private Model resolveModel(String modelId, float scale) {
        float safeScale = Math.max(0.01f, scale);
        String[] candidates = switch (modelId) {
            case MODEL_PLAYER -> new String[]{MODEL_PLAYER, MODEL_OBJECTIVE};
            case MODEL_WARP -> new String[]{MODEL_WARP, MODEL_OBJECTIVE};
            case MODEL_PATH -> new String[]{MODEL_PATH, MODEL_OBJECTIVE};
            case MODEL_SPAWN -> new String[]{MODEL_SPAWN, MODEL_OBJECTIVE};
            default -> new String[]{modelId, MODEL_OBJECTIVE};
        };
        for (String candidate : candidates) {
            ModelAsset asset = ModelAsset.getAssetMap().getAsset(candidate);
            if (asset != null) {
                return Model.createStaticScaledModel(asset, safeScale);
            }
        }
        return null;
    }

    private Model resolveModel(String[] preferredModelIds, String fallbackModelId, float scale) {
        if (preferredModelIds != null) {
            for (String modelId : preferredModelIds) {
                if (modelId == null || modelId.isBlank()) {
                    continue;
                }
                Model model = this.resolveModel(modelId, scale);
                if (model != null) {
                    return model;
                }
            }
        }
        if (fallbackModelId == null || fallbackModelId.isBlank()) {
            return null;
        }
        return this.resolveModel(fallbackModelId, scale);
    }

    private Model resolveAnimatedModel(String modelId, float scale) {
        float safeScale = Math.max(0.01f, scale);
        String[] candidates = switch (modelId) {
            case MODEL_PLAYER -> new String[]{MODEL_PLAYER, MODEL_OBJECTIVE};
            case MODEL_WARP -> new String[]{MODEL_WARP, MODEL_OBJECTIVE};
            case MODEL_PATH -> new String[]{MODEL_PATH, MODEL_OBJECTIVE};
            case MODEL_SPAWN -> new String[]{MODEL_SPAWN, MODEL_OBJECTIVE};
            default -> new String[]{modelId, MODEL_OBJECTIVE};
        };
        for (String candidate : candidates) {
            ModelAsset asset = ModelAsset.getAssetMap().getAsset(candidate);
            if (asset != null) {
                return Model.createScaledModel(asset, safeScale);
            }
        }
        return null;
    }

    private Model resolveAnimatedModel(String[] preferredModelIds, String fallbackModelId, float scale) {
        if (preferredModelIds != null) {
            for (String modelId : preferredModelIds) {
                if (modelId == null || modelId.isBlank()) {
                    continue;
                }
                Model model = this.resolveAnimatedModel(modelId, scale);
                if (model != null) {
                    return model;
                }
            }
        }
        if (fallbackModelId == null || fallbackModelId.isBlank()) {
            return null;
        }
        return this.resolveAnimatedModel(fallbackModelId, scale);
    }

    private RoleModelProfile towerProfile(TowerInstance tower) {
        int level = Math.max(0, Math.min(tower.levelIndex, 9));
        return switch (tower.definition.id) {
            case "guard_post" -> switch (level) {
                case 0 -> new RoleModelProfile(new String[0], "BankDefense_Seedling_Custom", 1.0f);
                case 1 -> new RoleModelProfile(new String[0], "BankDefense_Seedling_Custom", 1.5f);
                case 2 -> new RoleModelProfile(new String[0], "BankDefense_Seedling_Custom", 3.0f);
                case 3 -> new RoleModelProfile(new String[]{"Kweebec_Sproutling_Lime"}, MODEL_OBJECTIVE, 1.0f);
                case 4 -> new RoleModelProfile(new String[]{"Kweebec_Sproutling_Blue"}, MODEL_OBJECTIVE, 1.0f);
                case 5 -> new RoleModelProfile(new String[]{"Kweebec_Sapling_Treesinger"}, MODEL_OBJECTIVE, 1.0f);
                case 6 -> new RoleModelProfile(new String[]{"Kweebec_Sapling_Red"}, MODEL_OBJECTIVE, 1.5f);
                case 7 -> new RoleModelProfile(new String[]{"Kweebec_Sapling_Brown"}, MODEL_OBJECTIVE, 1.5f);
                case 8 -> new RoleModelProfile(new String[]{"Kweebec_Sapling_Yellow"}, MODEL_OBJECTIVE, 1.5f);
                default -> new RoleModelProfile(new String[]{"Kweebec_Rootling"}, MODEL_OBJECTIVE, 1.0f);
            };
            case "sniper_desk" -> switch (level) {
                case 0 -> new RoleModelProfile(new String[]{"Feran_Cub"}, MODEL_OBJECTIVE, 0.5f);
                case 1 -> new RoleModelProfile(new String[]{"Feran_Cub"}, MODEL_OBJECTIVE, 1.0f);
                case 2 -> new RoleModelProfile(new String[]{"Feran_Cub"}, MODEL_OBJECTIVE, 1.5f);
                case 3 -> new RoleModelProfile(new String[]{"Feran_Civilian"}, MODEL_OBJECTIVE, 1.0f);
                case 4 -> new RoleModelProfile(new String[]{"Feran_Windwalker"}, MODEL_OBJECTIVE, 1.0f);
                case 5 -> new RoleModelProfile(new String[]{"Feran_Longtooth"}, MODEL_OBJECTIVE, 1.0f);
                case 6 -> new RoleModelProfile(new String[]{"Feran_Longtooth"}, MODEL_OBJECTIVE, 1.0f);
                case 7 -> new RoleModelProfile(new String[]{"Feran_Sharptooth"}, MODEL_OBJECTIVE, 1.0f);
                case 8 -> new RoleModelProfile(new String[]{"Feran_Sharptooth"}, MODEL_OBJECTIVE, 1.0f);
                default -> new RoleModelProfile(new String[]{"Feran_Burrower"}, MODEL_OBJECTIVE, 1.0f);
            };
            case "rapid_security" -> switch (level) {
                case 0 -> new RoleModelProfile(new String[0], "BankDefense_Seedling_Custom", 1.0f);
                case 1 -> new RoleModelProfile(new String[0], "BankDefense_Seedling_Custom", 1.5f);
                case 2 -> new RoleModelProfile(new String[0], "BankDefense_Seedling_Custom", 3.0f);
                case 3 -> new RoleModelProfile(new String[]{"Kweebec_Sapling_Razorleaf"}, MODEL_OBJECTIVE, 1.0f);
                case 4 -> new RoleModelProfile(new String[]{"Kweebec_Sapling_Razorleaf"}, MODEL_OBJECTIVE, 1.0f);
                case 5 -> new RoleModelProfile(new String[]{"Kweebec_Sapling_Razorleaf"}, MODEL_OBJECTIVE, 1.0f);
                case 6 -> new RoleModelProfile(new String[]{"Kweebec_Sapling_Razorleaf"}, MODEL_OBJECTIVE, 1.0f);
                case 7 -> new RoleModelProfile(new String[]{"Kweebec_Sapling_Razorleaf"}, MODEL_OBJECTIVE, 1.0f);
                case 8 -> new RoleModelProfile(new String[]{"Kweebec_Sapling_Razorleaf"}, MODEL_OBJECTIVE, 1.0f);
                default -> new RoleModelProfile(new String[0], "BankDefense_Seedling_Custom", 6.0f);
            };
            case "freeze_gate" -> switch (level) {
                case 0 -> new RoleModelProfile(new String[]{"Snail_Frost"}, MODEL_OBJECTIVE, 1.0f);
                case 1 -> new RoleModelProfile(new String[]{"Snail_Frost"}, MODEL_OBJECTIVE, 1.5f);
                case 2 -> new RoleModelProfile(new String[]{"Snail_Frost"}, MODEL_OBJECTIVE, 3.0f);
                case 3 -> new RoleModelProfile(new String[]{"Spirit_Frost"}, MODEL_OBJECTIVE, 1.0f);
                case 4 -> new RoleModelProfile(new String[]{"Golem_Crystal_Frost"}, MODEL_OBJECTIVE, 0.7f);
                case 5 -> new RoleModelProfile(new String[]{"Golem_Crystal_Frost"}, MODEL_OBJECTIVE, 1.0f);
                case 6 -> new RoleModelProfile(new String[]{"Golem_Crystal_Frost"}, MODEL_OBJECTIVE, 1.2f);
                case 7 -> new RoleModelProfile(new String[]{"Dragon_Frost"}, MODEL_OBJECTIVE, 0.2f);
                case 8 -> new RoleModelProfile(new String[]{"Dragon_Frost"}, MODEL_OBJECTIVE, 0.4f);
                default -> new RoleModelProfile(new String[]{"Dragon_Frost"}, MODEL_OBJECTIVE, 0.6f);
            };
            case "shock_relay" -> switch (level) {
                case 0 -> new RoleModelProfile(new String[]{"Slothian_Kid"}, MODEL_OBJECTIVE, 0.5f);
                case 1 -> new RoleModelProfile(new String[]{"Slothian_Kid"}, MODEL_OBJECTIVE, 1.0f);
                case 2 -> new RoleModelProfile(new String[]{"Slothian_Kid"}, MODEL_OBJECTIVE, 1.5f);
                case 3 -> new RoleModelProfile(new String[]{"Slothian"}, MODEL_OBJECTIVE, 1.0f);
                case 4 -> new RoleModelProfile(new String[]{"Slothian_Elder"}, MODEL_OBJECTIVE, 1.0f);
                case 5 -> new RoleModelProfile(new String[]{"Slothian_Scout"}, MODEL_OBJECTIVE, 1.0f);
                case 6 -> new RoleModelProfile(new String[]{"Slothian_Villager"}, MODEL_OBJECTIVE, 1.0f);
                case 7 -> new RoleModelProfile(new String[]{"Slothian_Monk"}, MODEL_OBJECTIVE, 1.0f);
                case 8 -> new RoleModelProfile(new String[]{"Slothian_Kid"}, MODEL_OBJECTIVE, 1.5f);
                default -> new RoleModelProfile(new String[]{"Slothian_Warrior"}, MODEL_OBJECTIVE, 1.0f);
            };
            case "armor_drill" -> switch (level) {
                case 0 -> new RoleModelProfile(new String[0], "BankDefense_AncientKweebec_1", 2.2f);
                case 1 -> new RoleModelProfile(new String[0], "BankDefense_AncientKweebec_2", 2.2f);
                case 2 -> new RoleModelProfile(new String[0], "BankDefense_AncientKweebec_3", 2.2f);
                case 3 -> new RoleModelProfile(new String[0], "BankDefense_AncientKweebec_4", 2.2f);
                case 4 -> new RoleModelProfile(new String[0], "BankDefense_AncientKweebec_5", 2.2f);
                case 5 -> new RoleModelProfile(new String[0], "BankDefense_AncientKweebec_6", 2.2f);
                case 6 -> new RoleModelProfile(new String[0], "BankDefense_AncientKweebec_7", 2.2f);
                case 7 -> new RoleModelProfile(new String[0], "BankDefense_AncientKweebec_8", 2.2f);
                case 8 -> new RoleModelProfile(new String[0], "BankDefense_AncientKweebec_9", 2.2f);
                default -> new RoleModelProfile(new String[0], "BankDefense_AncientKweebec_10", 2.2f);
            };
            case "heart_of_roots" -> new RoleModelProfile(
                new String[0],
                "BankDefense_SuperTower_Heart",
                2.5f
            );
            case "storm_monolith" -> new RoleModelProfile(
                new String[0],
                "BankDefense_SuperTower_Monolith",
                2.5f
            );
            case "seed_idol" -> new RoleModelProfile(
                new String[0],
                "BankDefense_SuperTower_Idol",
                2.5f
            );
            case "root_snare" -> new RoleModelProfile(
                new String[0],
                tower.trapBoomVisual ? "BankDefense_Trap_Green_Boom" : "BankDefense_Trap_Green_Default",
                1.7f
            );
            case "spore_mine" -> new RoleModelProfile(
                new String[0],
                tower.trapBoomVisual ? "BankDefense_Trap_Red_Boom" : "BankDefense_Trap_Red_Default",
                1.7f
            );
            case "frost_seal" -> new RoleModelProfile(
                new String[0],
                tower.trapBoomVisual ? "BankDefense_Trap_Blue_Boom" : "BankDefense_Trap_Blue_Default",
                1.7f
            );
            default -> new RoleModelProfile(new String[]{"Outlander_Hunter"}, MODEL_OBJECTIVE, 0.82f);
        };
    }

    private String towerVisualProfileKey(TowerInstance tower) {
        return tower.definition.id
            + ":" + tower.levelIndex
            + ":" + tower.trapBoomVisual
            + ":" + (tower.trapPendingRemaining > 0.0);
    }

    private RoleModelProfile enemyProfile(MatchContext context, EnemyInstance enemy) {
        if (enemy == null || enemy.definition == null || enemy.definition.id == null) {
            return new RoleModelProfile(new String[]{"Skeleton", "Zombie"}, MODEL_PLAYER, 0.96f);
        }
        return switch (enemy.definition.id) {
            case ENEMY_RIFT_TWIN_ALPHA -> new RoleModelProfile(
                new String[0],
                this.isTwinCurrentlyOpen(enemy, context) ? MODEL_BOSS_RIFT_TWIN_ALPHA_ON : MODEL_BOSS_RIFT_TWIN_ALPHA_OFF,
                this.isRiftTwinEmpowered(context, enemy) ? 1.98f : 1.32f
            );
            case ENEMY_RIFT_TWIN_BETA -> new RoleModelProfile(
                new String[0],
                this.isTwinCurrentlyOpen(enemy, context) ? MODEL_BOSS_RIFT_TWIN_BETA_ON : MODEL_BOSS_RIFT_TWIN_BETA_OFF,
                this.isRiftTwinEmpowered(context, enemy) ? 1.98f : 1.32f
            );
            case ENEMY_NODE_ARBITER -> new RoleModelProfile(new String[0], MODEL_BOSS_NODE_ARBITER, 1.36f);
            case ENEMY_SEAL_MASTER -> new RoleModelProfile(new String[0], this.sealMasterModelId(enemy), 1.28f);
            default -> this.enemyProfile(enemy.definition.id);
        };
    }

    private RoleModelProfile enemyProfile(String enemyId) {
        return switch (enemyId) {
            case "thief" -> new RoleModelProfile(new String[]{"Skeleton_Scout", "Skeleton"}, MODEL_PLAYER, 0.92f);
            case "runner" -> new RoleModelProfile(new String[]{"Zombie_Sand", "Zombie"}, MODEL_PLAYER, 0.88f);
            case "runner_bomber" -> new RoleModelProfile(new String[]{"Zombie_Burnt", "Zombie"}, MODEL_PLAYER, 0.94f);
            case "bruiser" -> new RoleModelProfile(new String[]{"Skeleton_Burnt_Praetorian", "Skeleton_Knight", "Skeleton"}, MODEL_PLAYER, 1.10f);
            case "bone_guard" -> new RoleModelProfile(new String[]{"Skeleton_Soldier", "Skeleton"}, MODEL_PLAYER, 1.00f);
            case "berserker_rotter" -> new RoleModelProfile(new String[]{"Zombie_Frost", "Zombie"}, MODEL_PLAYER, 0.98f);
            case ENEMY_GOBLIN_SABOTEUR -> new RoleModelProfile(new String[]{"Skeleton_Sand_Assassin", "Skeleton_Scout", "Skeleton"}, MODEL_PLAYER, 0.98f);
            case "jammer" -> new RoleModelProfile(new String[]{"Skeleton_Mage", "Skeleton"}, MODEL_PLAYER, 1.02f);
            case "necro_thief" -> new RoleModelProfile(new String[]{"Skeleton_Ranger", "Skeleton"}, MODEL_PLAYER, 0.98f);
            case "vault_priest" -> new RoleModelProfile(new String[]{"Skeleton_Archmage", "Skeleton_Mage", "Skeleton"}, MODEL_PLAYER, 1.04f);
            case "bone_trumpeter" -> new RoleModelProfile(new String[]{"Skeleton_Burnt_Gunner", "Skeleton_Archer", "Skeleton"}, MODEL_PLAYER, 1.02f);
            case "plague_standard" -> new RoleModelProfile(new String[]{"Zombie", "Zombie_Sand"}, MODEL_PLAYER, 1.08f);
            case "curse_weaver" -> new RoleModelProfile(new String[]{"Skeleton_Burnt_Wizard", "Skeleton_Mage", "Skeleton"}, MODEL_PLAYER, 1.00f);
            case "elite_robber" -> new RoleModelProfile(new String[]{"Skeleton_Fighter", "Skeleton"}, MODEL_PLAYER, 1.12f);
            case "grave_spawn" -> new RoleModelProfile(new String[]{"Skeleton_Sand_Scout", "Skeleton_Scout", "Skeleton"}, MODEL_PLAYER, 0.74f);
            case ENEMY_NECRO_GUARDIAN -> new RoleModelProfile(new String[]{"Skeleton_Burnt_Soldier", "Skeleton"}, MODEL_PLAYER, 1.04f);
            case ENEMY_RIFT_TWIN_ALPHA -> new RoleModelProfile(new String[]{"Zombie_Aberrant", "Zombie"}, MODEL_PLAYER, 1.32f);
            case ENEMY_RIFT_TWIN_BETA -> new RoleModelProfile(new String[]{"Zombie", "Zombie_Aberrant"}, MODEL_PLAYER, 1.32f);
            case ENEMY_NODE_ARBITER -> new RoleModelProfile(new String[]{"Skeleton_burnt_soldier", "Skeleton"}, MODEL_PLAYER, 1.36f);
            case ENEMY_SEAL_MASTER -> new RoleModelProfile(new String[]{"Skeleton_burnt_soldier", "Skeleton"}, MODEL_PLAYER, 1.28f);
            case ENEMY_SEAL_NODE -> new RoleModelProfile(new String[]{"Skeleton_Incandescent_Head", "Skeleton"}, MODEL_PLAYER, 0.78f);
            case "vault_breaker" -> new RoleModelProfile(new String[]{"Zombie_Aberrant"}, MODEL_PLAYER, 1.45f);
            case "necro_king" -> new RoleModelProfile(new String[]{"Skeleton_burnt_soldier", "Zombie_Aberrant"}, MODEL_PLAYER, 1.34f);
            case "goblin_bomber_boss" -> new RoleModelProfile(new String[]{"Goblin_Duke", "Goblin"}, MODEL_PLAYER, 1.42f);
            default -> new RoleModelProfile(new String[]{"Skeleton", "Zombie"}, MODEL_PLAYER, 0.96f);
        };
    }

    private String enemyVisualProfileKey(RoleModelProfile profile) {
        if (profile == null) {
            return "";
        }
        String roles = profile.roleIds == null || profile.roleIds.length == 0
            ? ""
            : String.join("|", profile.roleIds);
        return roles + "||" + (profile.fallbackModelId == null ? "" : profile.fallbackModelId) + "||" + profile.scale;
    }

    private boolean isTwinCurrentlyOpen(EnemyInstance enemy, MatchContext context) {
        return enemy != null && (enemy.damageableTwin || this.isRiftTwinEmpowered(context, enemy));
    }

    private boolean isRiftTwinEmpowered(MatchContext context, EnemyInstance enemy) {
        if (enemy == null) {
            return false;
        }
        if (enemy.linkedBossInstanceId <= 0L) {
            return true;
        }
        EnemyInstance linkedTwin = this.findEnemyByInstanceId(context, enemy.linkedBossInstanceId);
        return linkedTwin == null || linkedTwin.dying || linkedTwin.hp <= 0.0;
    }

    private String sealMasterModelId(EnemyInstance enemy) {
        int stage = enemy == null ? 0 : Math.max(0, Math.min(2, enemy.variantStage));
        return switch (stage) {
            case 0 -> MODEL_BOSS_SEAL_MASTER_1;
            case 1 -> MODEL_BOSS_SEAL_MASTER_2;
            default -> MODEL_BOSS_SEAL_MASTER_3;
        };
    }

    private static List<Vec3i> buildFullRoute(MapConfig map) {
        return buildFullRoute(map, "a");
    }

    private static List<Vec3i> buildFullRoute(MapConfig map, String laneId) {
        List<Vec3i> fullRoute = new ArrayList<>();
        Vec3i spawnPoint = spawnPointForLane(map, laneId);
        if (spawnPoint != null) {
            fullRoute.add(spawnPoint);
        }
        fullRoute.addAll(routePointsForLane(map, laneId));
        if (map.vaultPoint != null) {
            fullRoute.add(map.vaultPoint);
        }
        List<Vec3i> deduped = new ArrayList<>();
        Vec3i last = null;
        for (Vec3i point : fullRoute) {
            if (point == null) {
                continue;
            }
            if (last != null && last.distanceSquaredTo(point) == 0.0) {
                continue;
            }
            deduped.add(point);
            last = point;
        }
        return deduped;
    }

    private static Map<String, List<Vec3i>> buildAllRoutes(MapConfig map) {
        Map<String, List<Vec3i>> routes = new LinkedHashMap<>();
        for (String laneId : new String[]{"a", "b", "c"}) {
            List<Vec3i> route = buildFullRoute(map, laneId);
            if (!route.isEmpty()) {
                routes.put(laneId, route);
            }
        }
        if (routes.isEmpty()) {
            routes.put("a", buildFullRoute(map));
        }
        return routes;
    }

    private static String canonicalLaneId(String laneId) {
        if (laneId == null || laneId.isBlank()) {
            return "a";
        }
        String normalized = laneId.trim().toLowerCase();
        return switch (normalized) {
            case "a", "left", "north", "upper" -> "a";
            case "b", "mid", "center", "middle" -> "b";
            case "c", "right", "south", "lower" -> "c";
            default -> "a";
        };
    }

    private static Vec3i spawnPointForLane(MapConfig map, String laneId) {
        if (map == null) {
            return null;
        }
        return switch (canonicalLaneId(laneId)) {
            case "b" -> map.spawnPointB;
            case "c" -> map.spawnPointC;
            default -> map.spawnPointA != null ? map.spawnPointA : map.spawnPoint;
        };
    }

    private static List<Vec3i> routePointsForLane(MapConfig map, String laneId) {
        if (map == null) {
            return List.of();
        }
        return switch (canonicalLaneId(laneId)) {
            case "b" -> map.routePointsB == null ? List.of() : map.routePointsB;
            case "c" -> map.routePointsC == null ? List.of() : map.routePointsC;
            default -> {
                if (map.routePointsA != null && !map.routePointsA.isEmpty()) {
                    yield map.routePointsA;
                }
                yield map.routePoints == null ? List.of() : map.routePoints;
            }
        };
    }

    private static int totalRoutePointCount(MapConfig map) {
        int total = 0;
        for (List<Vec3i> route : buildAllRoutes(map).values()) {
            total += route.size();
        }
        return total;
    }

    private List<Vec3i> sampleRoute(List<Vec3i> route, double step) {
        List<Vec3i> sampled = new ArrayList<>();
        if (route.isEmpty()) {
            return sampled;
        }
        sampled.add(route.get(0));
        for (int i = 0; i < route.size() - 1; i++) {
            Vec3i from = route.get(i);
            Vec3i to = route.get(i + 1);
            double segmentLength = this.segmentLength(from, to);
            if (segmentLength <= 0.0) {
                continue;
            }
            for (double distance = step; distance < segmentLength; distance += step) {
                double t = distance / segmentLength;
                sampled.add(new Vec3i(
                    (int)Math.round(from.x + (to.x - from.x) * t),
                    (int)Math.round(from.y + (to.y - from.y) * t),
                    (int)Math.round(from.z + (to.z - from.z) * t)
                ));
            }
            sampled.add(to);
        }
        List<Vec3i> deduped = new ArrayList<>();
        Set<String> keys = new LinkedHashSet<>();
        for (Vec3i point : sampled) {
            if (keys.add(this.key(point))) {
                deduped.add(point);
            }
        }
        return deduped;
    }

    @SuppressWarnings("unused")
    private InterpolatedPoint pointAtProgress(MatchContext context, double progress) {
        return this.pointAtProgress(context, "a", progress);
    }

    private InterpolatedPoint pointAtProgress(MatchContext context, String laneId, double progress) {
        List<Vec3i> route = this.routeForLane(context, laneId);
        if (route.isEmpty()) {
            return new InterpolatedPoint(0.0, 0.0, 0.0);
        }
        if (route.size() == 1) {
            return new InterpolatedPoint(route.get(0));
        }
        double remaining = Math.max(0.0, progress);
        for (int i = 0; i < route.size() - 1; i++) {
            Vec3i from = route.get(i);
            Vec3i to = route.get(i + 1);
            double segmentLength = this.segmentLength(from, to);
            if (remaining <= segmentLength) {
                double t = segmentLength <= 0.0 ? 0.0 : remaining / segmentLength;
                return new InterpolatedPoint(
                    from.x + (to.x - from.x) * t,
                    from.y + (to.y - from.y) * t,
                    from.z + (to.z - from.z) * t
                );
            }
            remaining -= segmentLength;
        }
        return new InterpolatedPoint(route.get(route.size() - 1));
    }

    private double segmentLength(Vec3i from, Vec3i to) {
        long dx = (long)to.x - from.x;
        long dy = (long)to.y - from.y;
        long dz = (long)to.z - from.z;
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }

    private List<Vec3i> routeForLane(MatchContext context, String laneId) {
        if (context == null) {
            return List.of();
        }
        String normalizedLane = canonicalLaneId(laneId);
        List<Vec3i> route = context.routesByLane.get(normalizedLane);
        if (route != null && !route.isEmpty()) {
            return route;
        }
        return context.route;
    }

    private double routeLengthForLane(MatchContext context, String laneId) {
        if (context == null) {
            return 0.0;
        }
        String normalizedLane = canonicalLaneId(laneId);
        Double routeLength = context.routeLengthByLane.get(normalizedLane);
        if (routeLength != null && routeLength.doubleValue() > 0.0) {
            return routeLength.doubleValue();
        }
        return context.routeLength;
    }

    private String key(Vec3i position) {
        return position.x + "," + position.y + "," + position.z;
    }

    private String worldKey(World world) {
        return world.getSavePath().normalize().toString();
    }

    private boolean sameBlockType(BlockType left, BlockType right) {
        if (left == right) {
            return true;
        }
        if (left == null || right == null) {
            return false;
        }
        return left.getId().equals(right.getId());
    }

    private boolean samePoint(Vec3i left, Vec3i right) {
        return left != null
            && right != null
            && left.x == right.x
            && left.y == right.y
            && left.z == right.z;
    }

    private WorldChunk getChunk(World world, Vec3i position) {
        return world.getChunkIfNonTicking(ChunkUtil.indexChunkFromBlock(position.x, position.z));
    }

    public enum GameState {
        Loading,
        Ready,
        InMatch,
        Victory,
        Defeat
    }

    public enum MatchMode {
        Solo,
        Duo
    }

    public enum WaveState {
        BuildPhase,
        Spawning,
        Cleanup,
        Finished
    }

    private enum SealCurseType {
        Range,
        FireRate,
        TrapDelay,
        Damage,
        Preparation,
        EnemySpeed,
        UpgradeCost,
        ModuleEffect,
        Gold
    }

    public static final class MatchState {
        public GameState gameState = GameState.Loading;
        public MatchMode matchMode = MatchMode.Solo;
        public WaveState waveState = WaveState.BuildPhase;
        public int currentWave = 0;
        public int currency = 0;
        public int bankHp = 0;
        public int maxBankHp = 0;
        public boolean gameStarted = false;
        public boolean rewardPending = false;
        public boolean instantAutoStart = false;
        public boolean paused = false;
        public double preparationRemainingSeconds = 0.0;

        public MatchState copy() {
            MatchState copy = new MatchState();
            copy.gameState = this.gameState;
            copy.matchMode = this.matchMode;
            copy.waveState = this.waveState;
            copy.currentWave = this.currentWave;
            copy.currency = this.currency;
            copy.bankHp = this.bankHp;
            copy.maxBankHp = this.maxBankHp;
            copy.gameStarted = this.gameStarted;
            copy.rewardPending = this.rewardPending;
            copy.instantAutoStart = this.instantAutoStart;
            copy.paused = this.paused;
            copy.preparationRemainingSeconds = this.preparationRemainingSeconds;
            return copy;
        }
    }

    public static final class RuntimeStatus {
        public MatchState matchState;
        public int placedTowers;
        public int activeEnemies;
        public int pendingSpawns;
        public int totalSlots;
        public int routePoints;
        public int totalWaves;
        public boolean endlessMode;
        public double preparationRemainingSeconds;
        public boolean visualizationEnabled;
        public int renderedOverlayBlocks;
        public int enemyVisualViewers;
        public int wantedEnemyVisuals;
        public int enemyVisualSweepRemoved;
        public String enemyVisualEpoch;
        public int pinnedSpawnChunks;
    }

    public static final class TowerButtonState {
        public String towerId;
        public String displayName;
        public String shortLabel;
        public int cost;
        public boolean unlocked;
        public boolean enabled = true;
        public int buildMode;
    }

    public static final class ModuleButtonState {
        public String moduleId;
        public String displayName;
        public String note;
        public int count;
        public boolean enabled = true;
    }

    public static final class SlotUiState {
        public String slotId;
        public String slotLabel;
        public Vec3i position;
        public String slotType;
        public String ownerTeam;
        public String playerTeam;
        public String segment;
        public MatchMode matchMode = MatchMode.Solo;
        public boolean superSlot;
        public boolean trapSlot;
        public boolean modulesAllowed;
        public boolean slotOwnedByViewer = true;
        public int currency;
        public int bankHp;
        public int currentWave;
        public int totalWaves;
        public boolean endlessMode;
        public WaveState waveState;
        public boolean gameStarted;
        public double preparationRemainingSeconds;
        public boolean towerPresent;
        public String towerId;
        public String towerName;
        public int towerLevel;
        public int towerMaxLevel;
        public int unlockedTowerLevelCap;
        public boolean superReady;
        public int superActivationsTotal;
        public int superActivationsUsed;
        public int superActivationsRemaining;
        public String towerDescription;
        public boolean upgradeAvailable;
        public int upgradeCost;
        public int sellRefund;
        public String slotSummary;
        public String upgradeLabel;
        public String sellLabel;
        public String moduleSummary;
        public String removeModuleLabel;
        public String pendingRewardSummary;
        public List<TowerButtonState> towerButtons = new ArrayList<>();
        public List<ModuleButtonState> moduleButtons = new ArrayList<>();
    }

    public static final class RewardUiState {
        public int currentWave;
        public int currency;
        public int bankHp;
        public boolean rewardPending;
        public MatchMode matchMode = MatchMode.Solo;
        public String viewerTeam = TEAM_SHARED;
        public String activePickerTeam = TEAM_SHARED;
        public String phaseText = "";
        public String allyPickText = "";
        public List<ModuleButtonState> choiceButtons = new ArrayList<>();
    }

    public static final class ProgressionNodeButtonState {
        public String nodeId;
        public String displayName;
        public String note;
        public String branch;
        public int tier;
        public int cost;
        public double value;
        public String targetId;
        public boolean unlocked;
        public boolean available;
    }

    private static final class SealCurseState {
        private final SealCurseType type;
        private final int startWave;
        private int remainingWaves;

        private SealCurseState(SealCurseType type, int startWave, int remainingWaves) {
            this.type = type;
            this.startWave = Math.max(1, startWave);
            this.remainingWaves = Math.max(0, remainingWaves);
        }
    }

    public static final class ContractButtonState {
        public String contractId;
        public String displayName;
        public String note;
        public boolean active;
        public boolean enabled = true;
    }

    public static final class DifficultyButtonState {
        public String difficultyId;
        public String displayName;
        public String note;
        public boolean enabled = true;
    }

    public static final class ProgressionUiState {
        public int cores;
        public int highestWave;
        public int totalRuns;
        public int totalVictories;
        public String activeContractId;
        public String activeContractName;
        public boolean matchActive;
        public List<ProgressionNodeButtonState> nodes = new ArrayList<>();
        public List<ContractButtonState> contracts = new ArrayList<>();
    }

    public static final class DuoTeamSelectUiState {
        public boolean duoMode;
        public boolean blueClaimed;
        public boolean greenClaimed;
        public boolean blueAvailable = true;
        public boolean greenAvailable = true;
        public boolean complete;
        public String viewerTeam = TEAM_SHARED;
    }

    public static final class StatisticsUiState {
        public long lifetimeWavesCleared;
        public long lifetimeEnemyKills;
        public long lifetimeSpentCurrency;
        public long lifetimeTrapsPlaced;
        public int totalRuns;
        public long lifetimeOpenedChests;
        public long lifetimeModulesInstalled;
        public int bestRunWave;
        public long bestRunEarnedCurrency;
        public long bestRunEnemyKills;
    }

    public static final class TutorialDialogUiState {
        public boolean visible;
        public String speakerName = "Wizard Kweebec";
        public String bodyText = "";
        public String buttonText = "Continue";
        public String hintText = "";
        public String avatarKind = TUTORIAL_AVATAR_DEFAULT;
        public boolean closeOnAdvance;
    }

    public static final class ActionResult {
        public final boolean success;
        public final String message;

        private ActionResult(boolean success, String message) {
            this.success = success;
            this.message = message;
        }

        public static ActionResult ok(String message) {
            return new ActionResult(true, message);
        }

        public static ActionResult fail(String message) {
            return new ActionResult(false, message);
        }
    }

    public enum InteractionTargetKind {
        None,
        BuildSlot,
        StartConsole,
        MenuConsole,
        ControlOperator,
        VendorOperator,
        ModeSelectorOperator,
        MoneyChest,
        CoreChest
    }

    public static final class InteractionTarget {
        public final InteractionTargetKind kind;
        public final String slotId;
        public final Vec3i position;

        private InteractionTarget(InteractionTargetKind kind, String slotId, Vec3i position) {
            this.kind = kind;
            this.slotId = slotId;
            this.position = position;
        }

        public static InteractionTarget none() {
            return new InteractionTarget(InteractionTargetKind.None, null, null);
        }

        public static InteractionTarget slot(String slotId, Vec3i position) {
            return new InteractionTarget(InteractionTargetKind.BuildSlot, slotId, position);
        }

        public static InteractionTarget startConsole(Vec3i position) {
            return new InteractionTarget(InteractionTargetKind.StartConsole, null, position);
        }

        public static InteractionTarget menuConsole(Vec3i position) {
            return new InteractionTarget(InteractionTargetKind.MenuConsole, null, position);
        }

        public static InteractionTarget controlOperator(Vec3i position) {
            return new InteractionTarget(InteractionTargetKind.ControlOperator, null, position);
        }

        public static InteractionTarget vendorOperator(Vec3i position) {
            return new InteractionTarget(InteractionTargetKind.VendorOperator, null, position);
        }

        public static InteractionTarget modeSelectorOperator(Vec3i position) {
            return new InteractionTarget(InteractionTargetKind.ModeSelectorOperator, null, position);
        }

        public static InteractionTarget moneyChest(Vec3i position) {
            return new InteractionTarget(InteractionTargetKind.MoneyChest, null, position);
        }

        public static InteractionTarget coreChest(Vec3i position) {
            return new InteractionTarget(InteractionTargetKind.CoreChest, null, position);
        }
    }

    private enum ChestType {
        Money,
        Core
    }

    private static final class ActiveChest {
        private final String id;
        private final Vec3i position;
        private final ChestType type;

        private ActiveChest(String id, Vec3i position, ChestType type) {
            this.id = id;
            this.position = position;
            this.type = type;
        }
    }

    private static final class MatchContext {
        private final BankDefenseRepository.Snapshot snapshot;
        private final boolean tutorialActive;
        private final MatchMode matchMode;
        private final MatchState state = new MatchState();
        private final Map<String, TowerDefinition> towerById = new HashMap<>();
        private final Map<String, EnemyDefinition> enemyById = new HashMap<>();
        private final Map<String, ModuleDefinition> moduleById = new HashMap<>();
        private final Map<Integer, WaveDefinition> waveByIndex = new HashMap<>();
        private final Map<Integer, WaveDefinition> generatedWaveByIndex = new HashMap<>();
        private final Map<String, TowerInstance> placedTowers = new LinkedHashMap<>();
        private final Map<String, Integer> moduleInventory = new LinkedHashMap<>();
        private final Map<String, ActiveChest> activeChests = new LinkedHashMap<>();
        private final List<String> pendingRewardChoices = new ArrayList<>();
        private final Map<UUID, Double> pendingRewardPageOpenDelayByPlayer = new LinkedHashMap<>();
        private final Map<UUID, String> pendingIdolModeSlotByPlayer = new LinkedHashMap<>();
        private final Map<UUID, Double> pendingIdolModeOpenDelayByPlayer = new LinkedHashMap<>();
        private final Map<UUID, String> teamByPlayerUuid = new LinkedHashMap<>();
        private final Map<String, String> rewardSelectionsByTeam = new LinkedHashMap<>();
        private final List<ScheduledSpawn> pendingSpawns = new ArrayList<>();
        private final List<EnemyInstance> enemies = new ArrayList<>();
        private final List<SealCurseState> activeSealCurses = new ArrayList<>();
        private final Map<String, List<Vec3i>> routesByLane = new LinkedHashMap<>();
        private final Map<String, Double> routeLengthByLane = new LinkedHashMap<>();
        private final List<Vec3i> route;
        private final double routeLength;
        private final String progressionOwnerUuid;
        private final PlayerProgressionState progression;
        private final ContractDefinition activeContract;
        private final DifficultyProfile difficulty;
        private final int maxBankHp;
        private final String visualEpoch;
        private double waveElapsedSeconds;
        private int activeWaveNumber;
        private int activeWaveTotalEnemies;
        private int runWavesCleared;
        private int runEnemyKills;
        private int runEarnedCurrency;
        private int blueCurrency;
        private int greenCurrency;
        private int rewardDraftRound;
        private int lastRewardPreparedWave;
        private String lastSaboteurDestroyedSlotId = "";
        private String lastGoblinBomberDowngradedSlotId = "";
        private boolean oddIncomeRemainderToBlue = true;
        private boolean duoSoloTestEnabled;
        private String rewardPickerTeam = TEAM_SHARED;
        private String rewardTeamBonusModuleId = "";
        private String rewardBurnedModuleId = "";
        private String rewardLastPickedTeam = "";
        private String rewardLastPickedModuleId = "";
        private int rootsHeartBuffWaveNumber;
        private double rootsHeartBuffMultiplier;
        private double contractEffectCooldownRemaining;
        private long nextEnemyId = 1L;
        private boolean bonusChestsSpawned;
        private boolean progressionRewardGranted;
        private boolean hasEnemyRegenAuras;
        private boolean hasEnemyAllySpeedAuras;
        private boolean hasEnemyTowerFireRateAuras;
        private boolean hasEnemyTowerRangeAuras;
        private boolean hasEnemyDamageReductionAuras;
        private double defeatBannerRemainingSeconds;
        private String eventToastText = "";
        private double eventToastRemainingSeconds;
        private int eventToastIconType = EVENT_TOAST_ICON_MONEY;
        private String delayedEventToastText = "";
        private double delayedEventToastRemainingSeconds;
        private int delayedEventToastIconType = EVENT_TOAST_ICON_MONEY;
        private String queuedEventToastText = "";
        private int queuedEventToastIconType = EVENT_TOAST_ICON_MONEY;
        private int tutorialTrapWaveSpawnedCount;
        private boolean instantAutoStartWarmupPending;
        private double instantAutoStartWarmupSeconds;
        private final List<String> pendingWorldUiSounds = new ArrayList<>();
        private double chestLoopSoundCooldownRemaining;
        private String baselineForcedWeatherId;
        private boolean acidStormActive;
        private String activeStormWeatherId;
        private double acidStormLightningCooldownRemaining;
        private double acidStormOmenCooldownRemaining;
        private double worldInteractionSyncAccumulatedSeconds;
        private double duoPlayerCountCheckAccumulatedSeconds;

        private MatchContext(
            BankDefenseRepository.Snapshot snapshot,
            String progressionOwnerUuid,
            PlayerProgressionState progression,
            ContractDefinition activeContract,
            DifficultyProfile difficulty,
            int maxBankHp,
            boolean tutorialActive,
            MatchMode matchMode
        ) {
            this.snapshot = snapshot;
            this.progressionOwnerUuid = progressionOwnerUuid;
            this.progression = progression;
            this.activeContract = activeContract;
            this.difficulty = difficulty;
            this.maxBankHp = maxBankHp;
            this.tutorialActive = tutorialActive;
            this.matchMode = matchMode == null ? MatchMode.Solo : matchMode;
            this.visualEpoch = UUID.randomUUID().toString();
            for (TowerDefinition tower : snapshot.towers.towers) {
                this.towerById.put(tower.id, tower);
            }
            for (EnemyDefinition enemy : snapshot.enemies.enemies) {
                this.enemyById.put(enemy.id, enemy);
            }
            for (ModuleDefinition module : snapshot.modules.modules) {
                this.moduleById.put(module.id, module);
            }
            for (WaveDefinition wave : snapshot.waves.waves) {
                this.waveByIndex.put(wave.index, wave);
            }
            Map<String, List<Vec3i>> routes = BankDefenseRuntime.buildAllRoutes(snapshot.map);
            for (Map.Entry<String, List<Vec3i>> entry : routes.entrySet()) {
                List<Vec3i> routeValue = new ArrayList<>(entry.getValue());
                this.routesByLane.put(entry.getKey(), routeValue);
                double laneLength = 0.0;
                for (int i = 0; i < routeValue.size() - 1; i++) {
                    laneLength += BankDefenseRuntime.distanceBetween(routeValue.get(i), routeValue.get(i + 1));
                }
                this.routeLengthByLane.put(entry.getKey(), laneLength);
            }
            this.route = this.routesByLane.getOrDefault("a", this.routesByLane.values().stream().findFirst().orElseGet(ArrayList::new));
            this.routeLength = this.routeLengthByLane.getOrDefault("a", this.routeLengthByLane.values().stream().findFirst().orElse(0.0));
            this.runWavesCleared = 0;
            this.runEnemyKills = 0;
            this.runEarnedCurrency = 0;
            this.rootsHeartBuffWaveNumber = 0;
            this.rootsHeartBuffMultiplier = 1.0;
            this.contractEffectCooldownRemaining = 0.0;
            this.state.matchMode = this.matchMode;
        }
    }

    private static final class DifficultyProfile {
        private final String id;
        private final String displayName;
        private final String note;
        private final double enemyHpMultiplier;
        private final double enemySpeedMultiplier;
        private final double enemyCountMultiplier;
        private final double startingCurrencyMultiplier;
        private final int startingCurrencyFlat;
        private final double rewardMultiplier;

        private DifficultyProfile(
            String id,
            String displayName,
            String note,
            double enemyHpMultiplier,
            double enemySpeedMultiplier,
            double enemyCountMultiplier,
            double startingCurrencyMultiplier,
            int startingCurrencyFlat,
            double rewardMultiplier
        ) {
            this.id = id;
            this.displayName = displayName;
            this.note = note;
            this.enemyHpMultiplier = enemyHpMultiplier;
            this.enemySpeedMultiplier = enemySpeedMultiplier;
            this.enemyCountMultiplier = enemyCountMultiplier;
            this.startingCurrencyMultiplier = startingCurrencyMultiplier;
            this.startingCurrencyFlat = startingCurrencyFlat;
            this.rewardMultiplier = rewardMultiplier;
        }
    }

    private enum TutorialStage {
        Inactive,
        IntroPageOne,
        IntroPageTwo,
        FindGuideWizard,
        GuideToFirstTower,
        PlaceFirstTower,
        ReturnAfterFirstTower,
        PrepareFirstWave,
        WaitFirstWaveFinish,
        ReturnAfterFirstWave,
        BuildSecondTower,
        ReturnAfterSecondTowerUpgrade,
        CollectTutorialChest,
        ReturnAfterTutorialChest,
        WaitSecondWaveFinish,
        ChooseRewardModule,
        ReturnAfterReward,
        FillAllSlots,
        WaitTwoLaneWaveFinish,
        ReturnAfterTwoLaneWave,
        PlaceMonolith,
        WaitBossWaveFinish,
        ReturnAfterBossWave,
        PlaceUniqueTraps,
        PrepareTrapWave,
        WaitTrapWaveFinish,
        ReturnAfterTrapWave,
        SpendCores,
        ReturnAfterProgression,
        FinalPageOne,
        FinalPageTwo,
        Completed
    }

    private static final class TutorialState {
        private TutorialStage stage = TutorialStage.Inactive;
        private boolean active;
        private boolean firstWavePrepared;
        private boolean secondWavePrepared;
        private boolean twoLaneWavePrepared;
        private boolean bossWavePrepared;
        private boolean trapWavePrepared;
        private boolean rewardChosen;
        private boolean moduleEquipped;
        private String secondTowerSlotId;
    }

    private static final class PresentationState {
        private final Map<String, Ref<EntityStore>> staticRefs = new LinkedHashMap<>();
        private final Map<Long, Ref<EntityStore>> enemyRefs = new LinkedHashMap<>();
        private final Map<Long, EnemyVisualState> enemyVisualStates = new HashMap<>();
        private final Map<String, Double> pendingVisualRespawnSeconds = new HashMap<>();
        private final List<ProjectileVisual> projectileVisuals = new ArrayList<>();
        private final Map<UUID, BankDefenseHud> huds = new HashMap<>();
        private boolean enabled;
        private boolean towerRangePreviewEnabled;
        private boolean hadLogicalEnemiesPreviousTick;
        private boolean playersNearCombatLastTick;
        private double accumulatedSeconds;
        private double enemyRealtimeAccumulatedSeconds;
        private double towerRealtimeAccumulatedSeconds;
        private double rangePreviewAccumulatedSeconds;
        private double enemyVisualGarbageSweepRemaining;
        private boolean legacyDebugBlocksCleared;
        private boolean visualGarbageSanitized;
        private int lastEnemyViewerCount;
        private int lastWantedEnemyVisuals;
        private int lastDuplicateEnemyVisualsRemoved;
        private String lastEnemyVisualEpoch = "";
        private BankDefenseRepository.Snapshot cachedRawSnapshot;

        private int totalVisualEntityCount() {
            return this.staticRefs.size() + this.enemyRefs.size() + this.projectileVisuals.size();
        }
    }

    private static final class EnemyVisualState {
        private String currentMovementAnimationId = "";
        private String visualProfileKey = "";
        private double visualStateSyncRemaining;
        private double auraVisualCooldownRemaining;

        private void resetLifecycle() {
            this.currentMovementAnimationId = "";
            this.visualProfileKey = "";
            this.visualStateSyncRemaining = 0.0;
            this.auraVisualCooldownRemaining = 0.0;
        }
    }

    private record RoleModelProfile(String[] roleIds, String fallbackModelId, float scale) {
    }

    private record EnemyVisualViewer(
        Ref<EntityStore> playerRef,
        Vector3d position,
        int viewRadiusBlocks,
        ChunkTracker chunkTracker
    ) {
    }

    private static final class ProjectileVisual {
        private final Ref<EntityStore> ref;
        private final String towerId;
        private final Vector3d from;
        private final Vector3d to;
        private final Vector3f rotation;
        private final double durationSeconds;
        private final boolean trailing;
        private double elapsedSeconds;
        private Vector3d currentPosition;

        private ProjectileVisual(
            Ref<EntityStore> ref,
            String towerId,
            Vector3d from,
            Vector3d to,
            Vector3f rotation,
            double durationSeconds,
            boolean trailing
        ) {
            this.ref = ref;
            this.towerId = towerId;
            this.from = from;
            this.to = to;
            this.rotation = rotation;
            this.durationSeconds = durationSeconds;
            this.trailing = trailing;
            this.elapsedSeconds = 0.0;
            this.currentPosition = from;
        }
    }

    private static final class ScheduledSpawn {
        private final EnemyDefinition enemy;
        private final double maxHp;
        private final double speed;
        private final int reward;
        private final int leakDamage;
        private final int waveNumber;
        private final double flatDamageReduction;
        private final double damageReductionPercent;
        private final double slowResistancePercent;
        private final double spawnAtSeconds;
        private final String laneId;

        private ScheduledSpawn(
            EnemyDefinition enemy,
            double maxHp,
            double speed,
            int reward,
            int leakDamage,
            int waveNumber,
            double flatDamageReduction,
            double damageReductionPercent,
            double slowResistancePercent,
            double spawnAtSeconds,
            String laneId
        ) {
            this.enemy = enemy;
            this.maxHp = maxHp;
            this.speed = speed;
            this.reward = reward;
            this.leakDamage = leakDamage;
            this.waveNumber = waveNumber;
            this.flatDamageReduction = flatDamageReduction;
            this.damageReductionPercent = damageReductionPercent;
            this.slowResistancePercent = slowResistancePercent;
            this.spawnAtSeconds = spawnAtSeconds;
            this.laneId = canonicalLaneId(laneId);
        }

        private ScheduledSpawn withSpawnTime(double spawnAtSeconds) {
            return new ScheduledSpawn(
                this.enemy,
                this.maxHp,
                this.speed,
                this.reward,
                this.leakDamage,
                this.waveNumber,
                this.flatDamageReduction,
                this.damageReductionPercent,
                this.slowResistancePercent,
                spawnAtSeconds,
                this.laneId
            );
        }
    }

    private static final class EnemyInstance {
        private final long instanceId;
        private final EnemyDefinition definition;
        private final double maxHp;
        private final double speed;
        private final int reward;
        private final int leakDamage;
        private final int waveNumber;
        private String laneId;
        private final double flatDamageReduction;
        private final double damageReductionPercent;
        private final double slowResistancePercent;
        private double hp;
        private double progress;
        private double slowPercent;
        private double slowRemaining;
        private double armorBreakFlatReduction;
        private double armorBreakPercentReduction;
        private double armorBreakRemaining;
        private double rootedRemaining;
        private double specialCooldownRemaining;
        private int remainingDisablePulses;
        private double remainingRegenBudget;
        private long summonerInstanceId;
        private long linkedBossInstanceId;
        private int extraLivesRemaining;
        private int variantStage;
        private int goblinDowngradeStage;
        private int goblinCurrencyStolen;
        private boolean linkedSummonPhaseActive;
        private boolean damageableTwin;
        private boolean tutorialTrapTargetLocked;
        private double tutorialTrapReleaseDelayRemaining;
        private int tutorialTrapSequenceIndex;
        private String tutorialTrapReleaseSlotId;
        private String castAbilityId;
        private double castRemaining;
        private double castTotal;
        private boolean dying;
        private double dyingRemaining;
        private Vector3f visualRotation = new Vector3f(0.0f, 0.0f, 0.0f);
        private String currentMovementAnimationId = "";
        private Ref<EntityStore> visualRef;
        private boolean visualSpawnedOnce;
        private double visualStateSyncRemaining;
        private double auraVisualCooldownRemaining;

        private EnemyInstance(long instanceId, ScheduledSpawn spawn) {
            this.instanceId = instanceId;
            this.definition = spawn.enemy;
            this.maxHp = spawn.maxHp;
            this.speed = spawn.speed;
            this.reward = spawn.reward;
            this.leakDamage = spawn.leakDamage;
            this.waveNumber = spawn.waveNumber;
            this.laneId = spawn.laneId;
            this.flatDamageReduction = spawn.flatDamageReduction;
            this.damageReductionPercent = spawn.damageReductionPercent;
            this.slowResistancePercent = spawn.slowResistancePercent;
            this.hp = spawn.maxHp;
            this.progress = 0.0;
            this.slowPercent = 0.0;
            this.slowRemaining = 0.0;
            this.armorBreakFlatReduction = 0.0;
            this.armorBreakPercentReduction = 0.0;
            this.armorBreakRemaining = 0.0;
            this.rootedRemaining = 0.0;
            this.specialCooldownRemaining = this.initialCooldown(this.definition);
            this.remainingDisablePulses = Math.max(0, this.definition.towerDisablePulseMaxTriggers);
            this.remainingRegenBudget = Math.max(0.0, spawn.maxHp * Math.max(0.0, this.definition.regenTotalBudgetMultiplier));
            this.summonerInstanceId = 0L;
            this.linkedBossInstanceId = 0L;
            this.extraLivesRemaining = 0;
            this.variantStage = 0;
            this.goblinDowngradeStage = 0;
            this.goblinCurrencyStolen = 0;
            this.linkedSummonPhaseActive = false;
            this.damageableTwin = true;
            this.tutorialTrapTargetLocked = false;
            this.tutorialTrapReleaseDelayRemaining = 0.0;
            this.tutorialTrapSequenceIndex = -1;
            this.tutorialTrapReleaseSlotId = "";
            this.castAbilityId = "";
            this.castRemaining = 0.0;
            this.castTotal = 0.0;
            this.dying = false;
            this.dyingRemaining = 0.0;
            this.visualRef = null;
            this.visualSpawnedOnce = false;
            this.visualStateSyncRemaining = 0.0;
            this.auraVisualCooldownRemaining = 0.0;
        }

        private boolean isBossCasting() {
            return this.castRemaining > 0.0 && this.castAbilityId != null && !this.castAbilityId.isBlank();
        }

        private double initialCooldown(EnemyDefinition definition) {
            if (definition == null) {
                return 0.0;
            }
            if ("vault_breaker".equals(definition.id)) {
                return VAULT_BREAKER_DISABLE_INTERVAL_SECONDS;
            }
            if (definition.towerDisablePulseIntervalSeconds > 0.0) {
                return definition.towerDisablePulseIntervalSeconds * 0.9;
            }
            if (definition.summonIntervalSeconds > 0.0) {
                return Math.max(1.5, definition.summonIntervalSeconds * 0.65);
            }
            return 0.0;
        }
    }

    private static final class TowerInstance {
        private final BuildSlot slot;
        private final TowerDefinition definition;
        private int levelIndex;
        private double cooldownRemaining;
        private double idleSeconds;
        private double ambientFxCooldownRemaining;
        private double disabledRemaining;
        private double disableFxCooldownRemaining;
        private int shotsFired;
        private int specialMode;
        private int idolStoredCurrency;
        private int idolSavingsWavesRemaining;
        private int superActivationsUsed;
        private boolean superReady;
        private String equippedModuleId;
        private double trapPendingRemaining;
        private double trapPendingTotal;
        private double trapBlinkAccumulator;
        private boolean trapBoomVisual;
        private String visualProfileKey;
        private boolean visualSpawnedOnce;
        private Vector3f visualRotation = new Vector3f(0.0f, 0.0f, 0.0f);

        private TowerInstance(BuildSlot slot, TowerDefinition definition, int specialMode) {
            this.slot = slot;
            this.definition = definition;
            this.levelIndex = 0;
            this.cooldownRemaining = 0.0;
            this.idleSeconds = 0.0;
            this.ambientFxCooldownRemaining = 0.0;
            this.disabledRemaining = 0.0;
            this.disableFxCooldownRemaining = 0.0;
            this.shotsFired = 0;
            this.specialMode = specialMode;
            this.idolStoredCurrency = 0;
            this.idolSavingsWavesRemaining = 0;
            this.superActivationsUsed = 0;
            this.superReady = definition.superTower && !"seed_idol".equals(definition.id);
            this.equippedModuleId = null;
            this.trapPendingRemaining = 0.0;
            this.trapPendingTotal = 0.0;
            this.trapBlinkAccumulator = 0.0;
            this.trapBoomVisual = false;
            this.visualProfileKey = null;
            this.visualSpawnedOnce = false;
        }

        private TowerLevel getCurrentLevel() {
            return this.definition.levels.get(this.levelIndex);
        }
    }

    private static final class InterpolatedPoint {
        private final double x;
        private final double y;
        private final double z;

        private InterpolatedPoint(Vec3i point) {
            this(point.x, point.y, point.z);
        }

        private InterpolatedPoint(double x, double y, double z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        @SuppressWarnings("unused")
        private Vec3i toVec3i() {
            return new Vec3i((int)Math.round(this.x), (int)Math.round(this.y), (int)Math.round(this.z));
        }

        private Vector3d toWorldVector(double yOffset) {
            return new Vector3d(this.x + 0.5, this.y + yOffset, this.z + 0.5);
        }
    }

    private static double distanceBetween(Vec3i from, Vec3i to) {
        long dx = (long)to.x - from.x;
        long dy = (long)to.y - from.y;
        long dz = (long)to.z - from.z;
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }
}


