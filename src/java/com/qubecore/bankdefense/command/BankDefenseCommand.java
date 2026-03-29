package com.qubecore.bankdefense.command;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.protocol.MovementSettings;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.DefaultArg;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractCommandCollection;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractWorldCommand;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.movement.MovementManager;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.qubecore.bankdefense.BankDefensePlugin;
import com.qubecore.bankdefense.data.BankDefenseRepository;
import com.qubecore.bankdefense.data.BankDefenseTypes.BuildSlot;
import com.qubecore.bankdefense.data.BankDefenseTypes.BuildSlotsConfig;
import com.qubecore.bankdefense.data.BankDefenseTypes.MapConfig;
import com.qubecore.bankdefense.data.BankDefenseTypes.Vec3i;
import com.qubecore.bankdefense.runtime.BankDefenseRuntime;
import com.qubecore.bankdefense.runtime.BankDefenseRuntime.MatchState;
import com.qubecore.bankdefense.runtime.BankDefenseRuntime.ProgressionUiState;
import com.qubecore.bankdefense.runtime.BankDefenseRuntime.RuntimeStatus;
import com.qubecore.bankdefense.ui.BankDefenseControlPage;
import com.qubecore.bankdefense.ui.BankDefenseProgressionPage;
import com.qubecore.bankdefense.ui.BankDefenseRewardPage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public final class BankDefenseCommand extends AbstractCommandCollection {
    private static final String SLOT_INTERACTION_BLOCK = "Utility_BankDefense_SlotMarker";
    private static final String SLOT_INTERACTION_BLOCK_OCCUPIED = "Utility_BankDefense_SlotMarkerOccupied";
    private static final String SLOT_INTERACTION_BLOCK_BLUE = "Utility_BankDefense_SlotMarkerBlue";
    private static final String SLOT_INTERACTION_BLOCK_BLUE_OCCUPIED = "Utility_BankDefense_SlotMarkerBlueOccupied";
    private static final String SLOT_INTERACTION_BLOCK_GREEN = "Utility_BankDefense_SlotMarkerGreen";
    private static final String SLOT_INTERACTION_BLOCK_GREEN_OCCUPIED = "Utility_BankDefense_SlotMarkerGreenOccupied";
    private static final String TRAP_SLOT_INTERACTION_BLOCK = "Utility_BankDefense_TrapSlotMarker";
    private static final String TRAP_SLOT_INTERACTION_BLOCK_OCCUPIED = "Utility_BankDefense_TrapSlotMarkerOccupied";
    private static final String SUPER_SLOT_INTERACTION_BLOCK = "Utility_BankDefense_SuperSlotMarker";

    public BankDefenseCommand(BankDefenseRepository repository, BankDefenseRuntime runtime) {
        super("bankdefense", "РРЅСЃС‚СЂСѓРјРµРЅС‚С‹ QubeCore: DUPLO Defense");
        this.addSubCommand(new StatusCommand(repository, runtime));
        this.addSubCommand(new DuoStatusCommand(runtime));
        this.addSubCommand(new DuoTestCommand(runtime));
        this.addSubCommand(new HudCommand(runtime));
        this.addSubCommand(new DuoFillMaxCommand(runtime));
        this.addSubCommand(new ValidateCommand(runtime));
        this.addSubCommand(new MatchCommand(runtime));
        this.addSubCommand(new MenuCommand(runtime));
        this.addSubCommand(new ProgressionCommand(runtime));
        this.addSubCommand(new ProgressResetCommand(runtime));
        this.addSubCommand(new StatsResetCommand(runtime));
        this.addSubCommand(new TutorialResetCommand(runtime));
        this.addSubCommand(new TutorialSkipCommand(runtime));
        this.addSubCommand(new NoBuildCommand());
        this.addSubCommand(new BuilderToolsCommand());
        this.addSubCommand(new MobilityCommand());
        this.addSubCommand(new MarkDuoCommand(repository));
        this.addSubCommand(new ClearDuoCommand(repository));
        this.addSubCommand(new MarkStatsCommand(repository));
        this.addSubCommand(new ClearStatsCommand(repository));
        this.addSubCommand(new RewardCommand(runtime));
        this.addSubCommand(new RewardMenuCommand(runtime));
        this.addSubCommand(new RangePreviewAliasCommand(runtime));
        this.addSubCommand(new MoneyCommand(runtime));
        this.addSubCommand(new TowersCommand(runtime));
        this.addSubCommand(new BuildCommand(runtime));
        this.addSubCommand(new UpgradeCommand(runtime));
        this.addSubCommand(new SellCommand(runtime));
        this.addSubCommand(new VisualCommand(runtime));
        this.addSubCommand(new RefreshAliasCommand(runtime));
        this.addSubCommand(new VisualClearAliasCommand(runtime));
        this.addSubCommand(new VisualStatusAliasCommand(runtime));
        this.addSubCommand(new MarkerCommand(repository));
        this.addSubCommand(new RouteCommand(repository));
        this.addSubCommand(new SlotCommand(repository));
        this.addSubCommand(new ResetAliasCommand(runtime));
        this.addSubCommand(new StartAliasCommand(runtime));
        this.addSubCommand(new MarkerSetAliasCommand(repository));
        this.addSubCommand(new MarkerClearAliasCommand(repository));
        this.addSubCommand(new FixedMarkerSetCommand(repository, "tutorialstart", "marktutorialstart", "РџРѕСЃС‚Р°РІРёС‚СЊ СЃС‚Р°СЂС‚ РѕР±СѓС‡РµРЅРёСЏ"));
        this.addSubCommand(new FixedMarkerSetCommand(repository, "tutorialstart", "marktutorialplayerstart", "РџРѕСЃС‚Р°РІРёС‚СЊ С‚РѕС‡РєСѓ СЃС‚Р°СЂС‚Р° РёРіСЂРѕРєР° РІ РѕР±СѓС‡РµРЅРёРё"));
        this.addSubCommand(new FixedMarkerSetCommand(repository, "tutorialspawna", "marktutorialspawna", "РџРѕСЃС‚Р°РІРёС‚СЊ tutorial spawn A"));
        this.addSubCommand(new FixedMarkerSetCommand(repository, "tutorialspawnb", "marktutorialspawnb", "РџРѕСЃС‚Р°РІРёС‚СЊ tutorial spawn B"));
        this.addSubCommand(new FixedMarkerSetCommand(repository, "tutorialspawnc", "marktutorialspawnc", "РџРѕСЃС‚Р°РІРёС‚СЊ tutorial spawn C"));
        this.addSubCommand(new FixedMarkerSetCommand(repository, "tutorialbank", "marktutorialbank", "РџРѕСЃС‚Р°РІРёС‚СЊ С†РµРЅС‚СЂ РѕР±СѓС‡РµРЅРёСЏ"));
        this.addSubCommand(new FixedMarkerSetCommand(repository, "tutorialvault", "marktutorialvault", "РџРѕСЃС‚Р°РІРёС‚СЊ РґСѓРїР»Рѕ РѕР±СѓС‡РµРЅРёСЏ"));
        this.addSubCommand(new FixedMarkerSetCommand(repository, "tutorialcontrol", "marktutorialcontrol", "РџРѕСЃС‚Р°РІРёС‚СЊ tutorial operator"));
        this.addSubCommand(new FixedMarkerSetCommand(repository, "tutorialcontrol", "marktutorialoperator", "РџРѕСЃС‚Р°РІРёС‚СЊ РѕРїРµСЂР°С‚РѕСЂР° РѕР±СѓС‡РµРЅРёСЏ"));
        this.addSubCommand(new FixedMarkerSetCommand(repository, "tutorialvendor", "marktutorialvendor", "РџРѕСЃС‚Р°РІРёС‚СЊ tutorial keeper"));
        this.addSubCommand(new FixedMarkerSetCommand(repository, "tutorialvendor", "marktutorialkeeper", "РџРѕСЃС‚Р°РІРёС‚СЊ С…СЂР°РЅРёС‚РµР»СЏ РѕР±СѓС‡РµРЅРёСЏ"));
        this.addSubCommand(new FixedMarkerSetCommand(repository, "wizardintro", "markwizardintro", "РџРѕСЃС‚Р°РІРёС‚СЊ РІСЃС‚СѓРїРёС‚РµР»СЊРЅРѕРіРѕ РљРІРёР±РµРєР°"));
        this.addSubCommand(new FixedMarkerSetCommand(repository, "tutorialwizard", "marktutorialwizard", "РџРѕСЃС‚Р°РІРёС‚СЊ РљРІРёР±РµРєР° РѕР±СѓС‡РµРЅРёСЏ"));
        this.addSubCommand(new TutorialChestSetCommand(repository));
        this.addSubCommand(new FixedMarkerClearCommand(repository, "tutorialstart", "cleartutorialstart", "РћС‡РёСЃС‚РёС‚СЊ СЃС‚Р°СЂС‚ РѕР±СѓС‡РµРЅРёСЏ"));
        this.addSubCommand(new FixedMarkerClearCommand(repository, "tutorialstart", "cleartutorialplayerstart", "РћС‡РёСЃС‚РёС‚СЊ С‚РѕС‡РєСѓ СЃС‚Р°СЂС‚Р° РёРіСЂРѕРєР° РІ РѕР±СѓС‡РµРЅРёРё"));
        this.addSubCommand(new FixedMarkerClearCommand(repository, "tutorialspawna", "cleartutorialspawna", "РћС‡РёСЃС‚РёС‚СЊ tutorial spawn A"));
        this.addSubCommand(new FixedMarkerClearCommand(repository, "tutorialspawnb", "cleartutorialspawnb", "РћС‡РёСЃС‚РёС‚СЊ tutorial spawn B"));
        this.addSubCommand(new FixedMarkerClearCommand(repository, "tutorialspawnc", "cleartutorialspawnc", "РћС‡РёСЃС‚РёС‚СЊ tutorial spawn C"));
        this.addSubCommand(new FixedMarkerClearCommand(repository, "tutorialbank", "cleartutorialbank", "РћС‡РёСЃС‚РёС‚СЊ С†РµРЅС‚СЂ РѕР±СѓС‡РµРЅРёСЏ"));
        this.addSubCommand(new FixedMarkerClearCommand(repository, "tutorialvault", "cleartutorialvault", "РћС‡РёСЃС‚РёС‚СЊ РґСѓРїР»Рѕ РѕР±СѓС‡РµРЅРёСЏ"));
        this.addSubCommand(new FixedMarkerClearCommand(repository, "tutorialcontrol", "cleartutorialcontrol", "РћС‡РёСЃС‚РёС‚СЊ tutorial operator"));
        this.addSubCommand(new FixedMarkerClearCommand(repository, "tutorialcontrol", "cleartutorialoperator", "РћС‡РёСЃС‚РёС‚СЊ РѕРїРµСЂР°С‚РѕСЂР° РѕР±СѓС‡РµРЅРёСЏ"));
        this.addSubCommand(new FixedMarkerClearCommand(repository, "tutorialvendor", "cleartutorialvendor", "РћС‡РёСЃС‚РёС‚СЊ tutorial keeper"));
        this.addSubCommand(new FixedMarkerClearCommand(repository, "tutorialvendor", "cleartutorialkeeper", "РћС‡РёСЃС‚РёС‚СЊ С…СЂР°РЅРёС‚РµР»СЏ РѕР±СѓС‡РµРЅРёСЏ"));
        this.addSubCommand(new FixedMarkerClearCommand(repository, "wizardintro", "clearwizardintro", "РћС‡РёСЃС‚РёС‚СЊ РІСЃС‚СѓРїРёС‚РµР»СЊРЅРѕРіРѕ РљРІРёР±РµРєР°"));
        this.addSubCommand(new FixedMarkerClearCommand(repository, "tutorialwizard", "cleartutorialwizard", "РћС‡РёСЃС‚РёС‚СЊ РљРІРёР±РµРєР° РѕР±СѓС‡РµРЅРёСЏ"));
        this.addSubCommand(new TutorialChestClearCommand(repository));
        this.addSubCommand(new FixedMarkerSetCommand(repository, "spawn_a", "markspawna", "РџРѕСЃС‚Р°РІРёС‚СЊ spawn A"));
        this.addSubCommand(new FixedMarkerSetCommand(repository, "spawn_b", "markspawnb", "РџРѕСЃС‚Р°РІРёС‚СЊ spawn B"));
        this.addSubCommand(new FixedMarkerSetCommand(repository, "spawn_c", "markspawnc", "РџРѕСЃС‚Р°РІРёС‚СЊ spawn C"));
        this.addSubCommand(new FixedMarkerSetCommand(repository, "bank", "markbank", "РџРѕСЃС‚Р°РІРёС‚СЊ РјР°СЂРєРµСЂ Р±Р°РЅРєР°"));
        this.addSubCommand(new FixedMarkerSetCommand(repository, "vault", "markvault", "РџРѕСЃС‚Р°РІРёС‚СЊ РјР°СЂРєРµСЂ СЏРґСЂР°"));
        this.addSubCommand(new FixedMarkerSetCommand(repository, "playerstart", "markplayerstart", "РџРѕСЃС‚Р°РІРёС‚СЊ СЃС‚Р°СЂС‚ РёРіСЂРѕРєР°"));
        this.addSubCommand(new FixedMarkerSetCommand(repository, "control", "markcontrol", "РџРѕСЃС‚Р°РІРёС‚СЊ control NPC"));
        this.addSubCommand(new FixedMarkerSetCommand(repository, "vendor", "markvendor", "РџРѕСЃС‚Р°РІРёС‚СЊ vendor NPC"));
        this.addSubCommand(new FixedMarkerSetCommand(repository, "mode", "markmode", "РџРѕСЃС‚Р°РІРёС‚СЊ NPC РІС‹Р±РѕСЂР° СЂРµР¶РёРјР°"));
        this.addSubCommand(new FixedMarkerSetCommand(repository, "qubecoresolo", "markqubecoresolo", "Поставить QubeCore на SOLO карте"));
        this.addSubCommand(new FixedMarkerSetCommand(repository, "qubecoreduo", "markqubecoreduo", "Поставить QubeCore на DUO карте"));
        this.addSubCommand(new FixedMarkerSetCommand(repository, "statistics", "marksolostats", "Поставить NPC статистики Solo"));
        this.addSubCommand(new FixedMarkerSetCommand(repository, "duoplayerblue", "markduoplayerblue", "Поставить старт синего игрока Duo"));
        this.addSubCommand(new FixedMarkerSetCommand(repository, "duoplayergreen", "markduoplayergreen", "Поставить старт зелёного игрока Duo"));
        this.addSubCommand(new FixedMarkerSetCommand(repository, "duospawnblue", "markduospawnblue", "Поставить spawn синего маршрута Duo"));
        this.addSubCommand(new FixedMarkerSetCommand(repository, "duospawngreen", "markduospawngreen", "Поставить spawn зелёного маршрута Duo"));
        this.addSubCommand(new FixedMarkerSetCommand(repository, "duobank", "markduobank", "Поставить центр Duo карты"));
        this.addSubCommand(new FixedMarkerSetCommand(repository, "duovault", "markduovault", "Поставить Hollow Duo карты"));
        this.addSubCommand(new FixedMarkerSetCommand(repository, "duosealblue", "markduosealblue", "Поставить seal node синего фронта"));
        this.addSubCommand(new FixedMarkerSetCommand(repository, "duosealgreen", "markduosealgreen", "Поставить seal node зелёного фронта"));
        this.addSubCommand(new FixedMarkerSetCommand(repository, "duostats", "markduostats", "Поставить NPC статистики Duo"));
        this.addSubCommand(new FixedMarkerClearCommand(repository, "spawn_a", "clearspawna", "РћС‡РёСЃС‚РёС‚СЊ spawn A"));
        this.addSubCommand(new FixedMarkerClearCommand(repository, "spawn_b", "clearspawnb", "РћС‡РёСЃС‚РёС‚СЊ spawn B"));
        this.addSubCommand(new FixedMarkerClearCommand(repository, "spawn_c", "clearspawnc", "РћС‡РёСЃС‚РёС‚СЊ spawn C"));
        this.addSubCommand(new FixedMarkerClearCommand(repository, "bank", "clearbank", "РћС‡РёСЃС‚РёС‚СЊ РјР°СЂРєРµСЂ Р±Р°РЅРєР°"));
        this.addSubCommand(new FixedMarkerClearCommand(repository, "vault", "clearvault", "РћС‡РёСЃС‚РёС‚СЊ РјР°СЂРєРµСЂ СЏРґСЂР°"));
        this.addSubCommand(new FixedMarkerClearCommand(repository, "playerstart", "clearplayerstart", "РћС‡РёСЃС‚РёС‚СЊ СЃС‚Р°СЂС‚ РёРіСЂРѕРєР°"));
        this.addSubCommand(new FixedMarkerClearCommand(repository, "control", "clearcontrol", "РћС‡РёСЃС‚РёС‚СЊ control NPC"));
        this.addSubCommand(new FixedMarkerClearCommand(repository, "vendor", "clearvendor", "РћС‡РёСЃС‚РёС‚СЊ vendor NPC"));
        this.addSubCommand(new FixedMarkerClearCommand(repository, "mode", "clearmode", "РћС‡РёСЃС‚РёС‚СЊ NPC РІС‹Р±РѕСЂР° СЂРµР¶РёРјР°"));
        this.addSubCommand(new FixedMarkerClearCommand(repository, "qubecoresolo", "clearqubecoresolo", "Очистить QubeCore на SOLO карте"));
        this.addSubCommand(new FixedMarkerClearCommand(repository, "qubecoreduo", "clearqubecoreduo", "Очистить QubeCore на DUO карте"));
        this.addSubCommand(new FixedMarkerClearCommand(repository, "statistics", "clearsolostats", "Очистить NPC статистики Solo"));
        this.addSubCommand(new FixedMarkerClearCommand(repository, "duoplayerblue", "clearduoplayerblue", "Очистить старт синего игрока Duo"));
        this.addSubCommand(new FixedMarkerClearCommand(repository, "duoplayergreen", "clearduoplayergreen", "Очистить старт зелёного игрока Duo"));
        this.addSubCommand(new FixedMarkerClearCommand(repository, "duospawnblue", "clearduospawnblue", "Очистить spawn синего маршрута Duo"));
        this.addSubCommand(new FixedMarkerClearCommand(repository, "duospawngreen", "clearduospawngreen", "Очистить spawn зелёного маршрута Duo"));
        this.addSubCommand(new FixedMarkerClearCommand(repository, "duobank", "clearduobank", "Очистить центр Duo карты"));
        this.addSubCommand(new FixedMarkerClearCommand(repository, "duovault", "clearduovault", "Очистить Hollow Duo карты"));
        this.addSubCommand(new FixedMarkerClearCommand(repository, "duosealblue", "clearduosealblue", "Очистить seal node синего фронта"));
        this.addSubCommand(new FixedMarkerClearCommand(repository, "duosealgreen", "clearduosealgreen", "Очистить seal node зелёного фронта"));
        this.addSubCommand(new FixedMarkerClearCommand(repository, "duostats", "clearduostats", "Очистить NPC статистики Duo"));
        this.addSubCommand(new FixedMarkerSetCommand(repository, "duoteam", "markduoteam", "Поставить NPC выбора стороны на DUO карте"));
        this.addSubCommand(new FixedMarkerSetCommand(repository, "duoteleportnpc", "markduoteleportnpc", "Поставить NPC телепорта на DUO карте"));
        this.addSubCommand(new FixedMarkerSetCommand(repository, "teleportduo", "markteleportduo", "Поставить точку телепорта на DUO карте"));
        this.addSubCommand(new FixedMarkerSetCommand(repository, "teleportsolo", "markteleportsolo", "Поставить точку телепорта на SOLO карте"));
        this.addSubCommand(new FixedMarkerClearCommand(repository, "duoteam", "clearduoteam", "Очистить NPC выбора стороны на DUO карте"));
        this.addSubCommand(new FixedMarkerClearCommand(repository, "duoteleportnpc", "clearduoteleportnpc", "Очистить NPC телепорта на DUO карте"));
        this.addSubCommand(new FixedMarkerClearCommand(repository, "teleportduo", "clearteleportduo", "Очистить точку телепорта на DUO карте"));
        this.addSubCommand(new FixedMarkerClearCommand(repository, "teleportsolo", "clearteleportsolo", "Очистить точку телепорта на SOLO карте"));
        this.addSubCommand(new RouteAddAliasCommand(repository));
        this.addSubCommand(new RouteAddLaneAliasCommand(repository, "a"));
        this.addSubCommand(new RouteAddLaneAliasCommand(repository, "b"));
        this.addSubCommand(new RouteAddLaneAliasCommand(repository, "c"));
        this.addSubCommand(new RouteAddLaneAliasCommand(repository, "ta"));
        this.addSubCommand(new RouteAddLaneAliasCommand(repository, "tb"));
        this.addSubCommand(new RouteAddLaneAliasCommand(repository, "tc"));
        this.addSubCommand(new RoutePopAliasCommand(repository));
        this.addSubCommand(new RoutePopLaneAliasCommand(repository, "a"));
        this.addSubCommand(new RoutePopLaneAliasCommand(repository, "b"));
        this.addSubCommand(new RoutePopLaneAliasCommand(repository, "c"));
        this.addSubCommand(new RoutePopLaneAliasCommand(repository, "ta"));
        this.addSubCommand(new RoutePopLaneAliasCommand(repository, "tb"));
        this.addSubCommand(new RoutePopLaneAliasCommand(repository, "tc"));
        this.addSubCommand(new RouteClearAliasCommand(repository));
        this.addSubCommand(new RouteClearLaneAliasCommand(repository, "a"));
        this.addSubCommand(new RouteClearLaneAliasCommand(repository, "b"));
        this.addSubCommand(new RouteClearLaneAliasCommand(repository, "c"));
        this.addSubCommand(new RouteClearLaneAliasCommand(repository, "ta"));
        this.addSubCommand(new RouteClearLaneAliasCommand(repository, "tb"));
        this.addSubCommand(new RouteClearLaneAliasCommand(repository, "tc"));
        this.addSubCommand(new RouteClearLaneAliasCommand(repository, "all"));
        this.addSubCommand(new RouteListAliasCommand(repository));
        this.addSubCommand(new RouteListLaneAliasCommand(repository, "a"));
        this.addSubCommand(new RouteListLaneAliasCommand(repository, "b"));
        this.addSubCommand(new RouteListLaneAliasCommand(repository, "c"));
        this.addSubCommand(new RouteListLaneAliasCommand(repository, "ta"));
        this.addSubCommand(new RouteListLaneAliasCommand(repository, "tb"));
        this.addSubCommand(new RouteListLaneAliasCommand(repository, "tc"));
        this.addSubCommand(new RouteListLaneAliasCommand(repository, "all"));
        this.addSubCommand(new DuoRouteAddAliasCommand(repository, "blue"));
        this.addSubCommand(new DuoRouteAddAliasCommand(repository, "green"));
        this.addSubCommand(new DuoRoutePopAliasCommand(repository, "blue"));
        this.addSubCommand(new DuoRoutePopAliasCommand(repository, "green"));
        this.addSubCommand(new DuoRouteClearAliasCommand(repository, "blue"));
        this.addSubCommand(new DuoRouteClearAliasCommand(repository, "green"));
        this.addSubCommand(new DuoRouteClearAliasCommand(repository, "all"));
        this.addSubCommand(new DuoRouteListAliasCommand(repository, "blue"));
        this.addSubCommand(new DuoRouteListAliasCommand(repository, "green"));
        this.addSubCommand(new DuoRouteListAliasCommand(repository, "all"));
        this.addSubCommand(new DuoResetRoutesCommand(repository));
        this.addSubCommand(new DuoResetStartsCommand(repository));
        this.addSubCommand(new SlotAddAliasCommand(repository));
        this.addSubCommand(new SlotAddTypeAliasCommand(repository, "standard"));
        this.addSubCommand(new SlotAddTypeAliasCommand(repository, "super"));
        this.addSubCommand(new SlotAddTypeAliasCommand(repository, "trap"));
        this.addSubCommand(new SlotAddTypeAliasCommand(repository, "tutorial"));
        this.addSubCommand(new SlotAddTypeAliasCommand(repository, "tutorial_super"));
        this.addSubCommand(new SlotAddTypeAliasCommand(repository, "tutorial_trap"));
        this.addSubCommand(new TutorialTrapAddCommand(repository, runtime));
        this.addSubCommand(new DuoSlotAddAliasCommand(repository, "slotaddblue", "standard", "blue", "blue_front"));
        this.addSubCommand(new DuoSlotAddAliasCommand(repository, "slotaddgreen", "standard", "green", "green_front"));
        this.addSubCommand(new DuoSlotAddAliasCommand(repository, "slotaddduoblue", "standard", "blue", "blue_front"));
        this.addSubCommand(new DuoSlotAddAliasCommand(repository, "slotaddduogreen", "standard", "green", "green_front"));
        this.addSubCommand(new DuoSlotAddAliasCommand(repository, "slotaddduotrapblue", "trap", "blue", "blue_trap"));
        this.addSubCommand(new DuoSlotAddAliasCommand(repository, "slotaddduotrapgreen", "trap", "green", "green_trap"));
        this.addSubCommand(new DuoSlotAddAliasCommand(repository, "slotaddduosuper", "super", "shared", "shared_super"));
        this.addSubCommand(new SlotTeamAliasCommand(repository, "slotteamblue", "blue"));
        this.addSubCommand(new SlotTeamAliasCommand(repository, "slotteamgreen", "green"));
        this.addSubCommand(new SlotTeamAliasCommand(repository, "slotteamshared", "shared"));
        this.addSubCommand(new SlotRemoveAliasCommand(repository));
        this.addSubCommand(new SlotClearAliasCommand(repository));
        this.addSubCommand(new SlotClearTypeAliasCommand(repository, "standard"));
        this.addSubCommand(new SlotClearTypeAliasCommand(repository, "super"));
        this.addSubCommand(new SlotClearTypeAliasCommand(repository, "trap"));
        this.addSubCommand(new SlotClearTypeAliasCommand(repository, "tutorial"));
        this.addSubCommand(new SlotClearTypeAliasCommand(repository, "tutorial_super"));
        this.addSubCommand(new SlotClearTypeAliasCommand(repository, "tutorial_trap"));
        this.addSubCommand(new TutorialTrapClearCommand(repository, runtime));
        this.addSubCommand(new SlotClearTypeAliasCommand(repository, "all"));
        this.addSubCommand(new SlotListAliasCommand(repository));
        this.addSubCommand(new ChestAddAliasCommand(repository));
        this.addSubCommand(new ChestRemoveAliasCommand(repository));
        this.addSubCommand(new ChestClearAliasCommand(repository));
        this.addSubCommand(new ChestListAliasCommand(repository));
        this.addSubCommand(new DuoChestAddAliasCommand(repository));
        this.addSubCommand(new DuoChestRemoveAliasCommand(repository));
        this.addSubCommand(new DuoChestClearAliasCommand(repository));
        this.addSubCommand(new DuoChestListAliasCommand(repository));
    }

    private static Vec3i currentBlockPosition(Store<EntityStore> store, Ref<EntityStore> ref) {
        TransformComponent transformComponent = store.getComponent(ref, TransformComponent.getComponentType());
        if (transformComponent == null) {
            throw new IllegalStateException("РўСЂР°РЅСЃС„РѕСЂРјР°С†РёСЏ РёРіСЂРѕРєР° РЅРµРґРѕСЃС‚СѓРїРЅР°.");
        }
        Vector3d position = transformComponent.getPosition();
        return Vec3i.ofFloor(position.x, position.y, position.z);
    }

    private static void refreshWorldVisualization(World world) {
        if (world == null) {
            return;
        }
        BankDefensePlugin plugin = BankDefensePlugin.getInstance();
        if (plugin == null || plugin.getRuntime() == null) {
            return;
        }
        try {
            plugin.getRuntime().refreshVisualization(world);
        } catch (IOException ignored) {
        }
    }

    private static float currentYaw(Store<EntityStore> store, Ref<EntityStore> ref) {
        TransformComponent transformComponent = store.getComponent(ref, TransformComponent.getComponentType());
        if (transformComponent == null || transformComponent.getRotation() == null) {
            return 0.0f;
        }
        return transformComponent.getRotation().y;
    }

    private static String normalizeMarkerName(String input) {
        String normalized = input.toLowerCase(Locale.ROOT).replace("-", "").replace("_", "");
        return switch (normalized) {
            case "spawn", "spawna" -> "spawn_a";
            case "spawnb" -> "spawn_b";
            case "spawnc" -> "spawn_c";
            case "tutorialstart", "tutorialplayerstart", "tstart", "learnstart" -> "tutorialstart";
            case "tutorialspawna", "tspawna", "learnspawna" -> "tutorialspawna";
            case "tutorialspawnb", "tspawnb", "learnspawnb" -> "tutorialspawnb";
            case "tutorialspawnc", "tspawnc", "learnspawnc" -> "tutorialspawnc";
            case "bank", "bankcenter", "duplo", "keep" -> "bank";
            case "tutorialbank", "tbank", "learnbank" -> "tutorialbank";
            case "vault", "goal", "core" -> "vault";
            case "tutorialvault", "tvault", "learnvault" -> "tutorialvault";
            case "playerstart", "playerspawn", "defenderstart" -> "playerstart";
            case "control", "console", "operator" -> "control";
            case "tutorialcontrol", "tutorialoperator", "tcontrol", "learncontrol" -> "tutorialcontrol";
            case "vendor", "merchant", "progression" -> "vendor";
            case "tutorialvendor", "tutorialkeeper", "tvendor", "learnvendor" -> "tutorialvendor";
            case "mode", "gamemode", "modeoperator", "modeselector" -> "mode";
            case "statistics", "stats", "statnpc", "solostats" -> "statistics";
            case "duoplayerblue", "duostartblue", "duoblueplayer", "blueplayerduo" -> "duoplayerblue";
            case "duoplayergreen", "duostartgreen", "duogreenplayer", "greenplayerduo" -> "duoplayergreen";
            case "duospawnblue", "duobluespawn", "bluepathspawn" -> "duospawnblue";
            case "duospawngreen", "duogreenspawn", "greenpathspawn" -> "duospawngreen";
            case "duobank", "duocenter", "duobankcenter" -> "duobank";
            case "duovault", "duohollow", "duogoal" -> "duovault";
            case "duosealblue", "duoblueuseal", "blueseal" -> "duosealblue";
            case "duosealgreen", "duogreenseal", "greenseal" -> "duosealgreen";
            case "duostats", "statisticsduo", "statsduo", "duostatnpc" -> "duostats";
            case "duoteam", "duoteamnpc", "teamselectduo", "colorduo" -> "duoteam";
            case "duoteleportnpc", "duoteleport", "duotravelnpc", "duomode" -> "duoteleportnpc";
            case "teleportduo", "duotarget", "duoteleporttarget" -> "teleportduo";
            case "teleportsolo", "solotarget", "soloteleporttarget" -> "teleportsolo";
            case "wizardintro", "introwizard", "introquebec" -> "wizardintro";
            case "tutorialwizard", "wizard", "guidewizard", "guidequebec" -> "tutorialwizard";
            default -> null;
        };
    }

    private static String normalizeLane(String input, String fallback) {
        if (input == null || input.isBlank()) {
            return fallback;
        }
        String normalized = input.toLowerCase(Locale.ROOT).replace("-", "").replace("_", "");
        return switch (normalized) {
            case "a", "left", "north", "upper" -> "a";
            case "b", "mid", "center", "middle" -> "b";
            case "c", "right", "south", "lower" -> "c";
            case "ta", "tutoriala", "learna" -> "ta";
            case "tb", "tutorialb", "learnb" -> "tb";
            case "tc", "tutorialc", "learnc" -> "tc";
            case "all" -> "all";
            default -> fallback;
        };
    }

    private static String normalizeSlotType(String input, String fallback) {
        if (input == null || input.isBlank()) {
            return fallback;
        }
        String normalized = input.toLowerCase(Locale.ROOT).replace("-", "").replace("_", "");
        return switch (normalized) {
            case "super", "elite", "special" -> "super";
            case "trap", "traps", "mine", "trapslot" -> "trap";
            case "tutorial", "learn", "tutorialstandard", "learnslot" -> "tutorial";
            case "tutorialsuper", "learnsuper", "tutorialelite" -> "tutorial_super";
            case "tutorialtrap", "learntrap" -> "tutorial_trap";
            case "standard", "normal", "regular" -> "standard";
            case "all" -> "all";
            default -> fallback;
        };
    }

    private static String laneLabel(String laneId) {
        return switch (laneId) {
            case "a" -> "A";
            case "b" -> "B";
            case "c" -> "C";
            case "ta" -> "TA";
            case "tb" -> "TB";
            case "tc" -> "TC";
            default -> "РІСЃРµ";
        };
    }

    private static String formatValidation(List<String> issues) {
        if (issues.isEmpty()) {
            return "РџСЂРѕРІРµСЂРєР°: OK";
        }
        StringBuilder builder = new StringBuilder("РџСЂРѕРІРµСЂРєР°: ").append(issues.size()).append(" РїСЂРѕР±Р»РµРј");
        for (String issue : issues) {
            builder.append('\n').append("- ").append(issue);
        }
        return builder.toString();
    }

    private static String markerSummary(Vec3i point) {
        return point == null ? "РЅРµС‚" : point.toShortString();
    }

    private static List<String> allLaneIds() {
        return List.of("a", "b", "c", "ta", "tb", "tc");
    }

    private static List<Vec3i> duoRouteRef(MapConfig mapConfig, String teamId) {
        if (mapConfig == null) {
            return new ArrayList<>();
        }
        return "green".equalsIgnoreCase(teamId) ? mapConfig.duoRouteGreen : mapConfig.duoRouteBlue;
    }

    private static void clearAllRoutes(MapConfig mapConfig) {
        mapConfig.routePoints.clear();
        mapConfig.routePointsA.clear();
        mapConfig.routePointsB.clear();
        mapConfig.routePointsC.clear();
        mapConfig.tutorialRoutePointsA.clear();
        mapConfig.tutorialRoutePointsB.clear();
        mapConfig.tutorialRoutePointsC.clear();
    }

    private static void clearAllDuoRoutes(MapConfig mapConfig) {
        mapConfig.duoRouteBlue.clear();
        mapConfig.duoRouteGreen.clear();
    }

    private static void clearAllDuoRoutesAndSpawns(MapConfig mapConfig) {
        clearAllDuoRoutes(mapConfig);
        mapConfig.duoSpawnPointBlue = null;
        mapConfig.duoSpawnPointGreen = null;
    }

    private static void clearAllDuoStarts(MapConfig mapConfig) {
        mapConfig.duoPlayerStartBlue = null;
        mapConfig.duoPlayerStartBlueYaw = null;
        mapConfig.duoPlayerStartGreen = null;
        mapConfig.duoPlayerStartGreenYaw = null;
    }

    private static boolean applyMarker(MapConfig mapConfig, String markerName, Vec3i position, float yaw) {
        switch (markerName) {
            case "spawn_a" -> {
                mapConfig.spawnPoint = position;
                mapConfig.spawnPointA = position;
            }
            case "spawn_b" -> mapConfig.spawnPointB = position;
            case "spawn_c" -> mapConfig.spawnPointC = position;
            case "bank" -> mapConfig.bankCenter = position;
            case "vault" -> mapConfig.vaultPoint = position;
            case "playerstart" -> {
                mapConfig.playerStart = position;
                mapConfig.playerStartYaw = yaw;
            }
            case "control" -> {
                mapConfig.previousControlPoint = mapConfig.controlPoint;
                mapConfig.controlPoint = position;
                mapConfig.controlYaw = yaw;
            }
            case "vendor" -> {
                mapConfig.previousVendorPoint = mapConfig.vendorPoint;
                mapConfig.vendorPoint = position;
                mapConfig.vendorYaw = yaw;
            }
            case "mode" -> {
                mapConfig.previousModePoint = mapConfig.modePoint;
                mapConfig.modePoint = position;
                mapConfig.modeYaw = yaw;
            }
            case "statistics" -> {
                mapConfig.previousStatisticsPoint = mapConfig.statisticsPoint;
                mapConfig.statisticsPoint = position;
                mapConfig.statisticsYaw = yaw;
            }
            case "duoplayerblue" -> {
                mapConfig.duoPlayerStartBlue = position;
                mapConfig.duoPlayerStartBlueYaw = yaw;
            }
            case "duoplayergreen" -> {
                mapConfig.duoPlayerStartGreen = position;
                mapConfig.duoPlayerStartGreenYaw = yaw;
            }
            case "duospawnblue" -> mapConfig.duoSpawnPointBlue = position;
            case "duospawngreen" -> mapConfig.duoSpawnPointGreen = position;
            case "duobank" -> mapConfig.duoBankCenter = position;
            case "duovault" -> mapConfig.duoVaultPoint = position;
            case "duosealblue" -> mapConfig.duoSealNodeBluePoint = position;
            case "duosealgreen" -> mapConfig.duoSealNodeGreenPoint = position;
            case "duostats" -> {
                mapConfig.previousDuoStatisticsPoint = mapConfig.duoStatisticsPoint;
                mapConfig.duoStatisticsPoint = position;
                mapConfig.duoStatisticsYaw = yaw;
            }
            case "duoteam" -> {
                mapConfig.previousDuoTeamPoint = mapConfig.duoTeamPoint;
                mapConfig.duoTeamPoint = position;
                mapConfig.duoTeamYaw = yaw;
            }
            case "duoteleportnpc" -> {
                mapConfig.previousDuoTeleportPoint = mapConfig.duoTeleportPoint;
                mapConfig.duoTeleportPoint = position;
                mapConfig.duoTeleportYaw = yaw;
            }
            case "teleportduo" -> mapConfig.duoTeleportTarget = position;
            case "teleportsolo" -> mapConfig.soloTeleportTarget = position;
            case "tutorialstart" -> {
                mapConfig.tutorialStartPoint = position;
                mapConfig.tutorialStartYaw = yaw;
            }
            case "tutorialspawna" -> mapConfig.tutorialSpawnPointA = position;
            case "tutorialspawnb" -> mapConfig.tutorialSpawnPointB = position;
            case "tutorialspawnc" -> mapConfig.tutorialSpawnPointC = position;
            case "tutorialbank" -> mapConfig.tutorialBankCenter = position;
            case "tutorialvault" -> mapConfig.tutorialVaultPoint = position;
            case "tutorialcontrol" -> {
                mapConfig.tutorialControlPoint = position;
                mapConfig.tutorialControlYaw = yaw;
            }
            case "tutorialvendor" -> {
                mapConfig.tutorialVendorPoint = position;
                mapConfig.tutorialVendorYaw = yaw;
            }
            case "wizardintro" -> {
                mapConfig.tutorialWizardIntroPoint = position;
                mapConfig.tutorialWizardIntroYaw = yaw;
            }
            case "tutorialwizard" -> {
                mapConfig.tutorialWizardPoint = position;
                mapConfig.tutorialWizardYaw = yaw;
            }
            default -> {
                return false;
            }
        }
        return true;
    }

    private static boolean clearMarker(MapConfig mapConfig, String markerName) {
        switch (markerName) {
            case "spawn_a" -> {
                mapConfig.spawnPoint = null;
                mapConfig.spawnPointA = null;
            }
            case "spawn_b" -> mapConfig.spawnPointB = null;
            case "spawn_c" -> mapConfig.spawnPointC = null;
            case "bank" -> mapConfig.bankCenter = null;
            case "vault" -> mapConfig.vaultPoint = null;
            case "playerstart" -> {
                mapConfig.playerStart = null;
                mapConfig.playerStartYaw = null;
            }
            case "control" -> {
                mapConfig.previousControlPoint = mapConfig.controlPoint;
                mapConfig.controlPoint = null;
                mapConfig.controlYaw = null;
            }
            case "vendor" -> {
                mapConfig.previousVendorPoint = mapConfig.vendorPoint;
                mapConfig.vendorPoint = null;
                mapConfig.vendorYaw = null;
            }
            case "mode" -> {
                mapConfig.previousModePoint = mapConfig.modePoint;
                mapConfig.modePoint = null;
                mapConfig.modeYaw = null;
            }
            case "statistics" -> {
                mapConfig.previousStatisticsPoint = mapConfig.statisticsPoint;
                mapConfig.statisticsPoint = null;
                mapConfig.statisticsYaw = null;
            }
            case "duoplayerblue" -> {
                mapConfig.duoPlayerStartBlue = null;
                mapConfig.duoPlayerStartBlueYaw = null;
            }
            case "duoplayergreen" -> {
                mapConfig.duoPlayerStartGreen = null;
                mapConfig.duoPlayerStartGreenYaw = null;
            }
            case "duospawnblue" -> mapConfig.duoSpawnPointBlue = null;
            case "duospawngreen" -> mapConfig.duoSpawnPointGreen = null;
            case "duobank" -> mapConfig.duoBankCenter = null;
            case "duovault" -> mapConfig.duoVaultPoint = null;
            case "duosealblue" -> mapConfig.duoSealNodeBluePoint = null;
            case "duosealgreen" -> mapConfig.duoSealNodeGreenPoint = null;
            case "duostats" -> {
                mapConfig.previousDuoStatisticsPoint = mapConfig.duoStatisticsPoint;
                mapConfig.duoStatisticsPoint = null;
                mapConfig.duoStatisticsYaw = null;
            }
            case "duoteam" -> {
                mapConfig.previousDuoTeamPoint = mapConfig.duoTeamPoint;
                mapConfig.duoTeamPoint = null;
                mapConfig.duoTeamYaw = null;
            }
            case "duoteleportnpc" -> {
                mapConfig.previousDuoTeleportPoint = mapConfig.duoTeleportPoint;
                mapConfig.duoTeleportPoint = null;
                mapConfig.duoTeleportYaw = null;
            }
            case "teleportduo" -> mapConfig.duoTeleportTarget = null;
            case "teleportsolo" -> mapConfig.soloTeleportTarget = null;
            case "tutorialstart" -> {
                mapConfig.tutorialStartPoint = null;
                mapConfig.tutorialStartYaw = null;
            }
            case "tutorialspawna" -> mapConfig.tutorialSpawnPointA = null;
            case "tutorialspawnb" -> mapConfig.tutorialSpawnPointB = null;
            case "tutorialspawnc" -> mapConfig.tutorialSpawnPointC = null;
            case "tutorialbank" -> mapConfig.tutorialBankCenter = null;
            case "tutorialvault" -> mapConfig.tutorialVaultPoint = null;
            case "tutorialcontrol" -> {
                mapConfig.tutorialControlPoint = null;
                mapConfig.tutorialControlYaw = null;
            }
            case "tutorialvendor" -> {
                mapConfig.tutorialVendorPoint = null;
                mapConfig.tutorialVendorYaw = null;
            }
            case "wizardintro" -> {
                mapConfig.tutorialWizardIntroPoint = null;
                mapConfig.tutorialWizardIntroYaw = null;
            }
            case "tutorialwizard" -> {
                mapConfig.tutorialWizardPoint = null;
                mapConfig.tutorialWizardYaw = null;
            }
            default -> {
                return false;
            }
        }
        return true;
    }

    private static List<Vec3i> routeRef(MapConfig map, String laneId) {
        return switch (laneId) {
            case "b" -> map.routePointsB;
            case "c" -> map.routePointsC;
            case "ta" -> map.tutorialRoutePointsA;
            case "tb" -> map.tutorialRoutePointsB;
            case "tc" -> map.tutorialRoutePointsC;
            default -> map.routePointsA;
        };
    }

    private static boolean isSuperSlot(BuildSlot slot) {
        return slot != null && ("super".equalsIgnoreCase(slot.slotType) || "tutorial_super".equalsIgnoreCase(slot.slotType));
    }

    private static boolean isTrapSlot(BuildSlot slot) {
        return slot != null && ("trap".equalsIgnoreCase(slot.slotType) || "tutorial_trap".equalsIgnoreCase(slot.slotType));
    }

    private static Vec3i nearestPoint(List<Vec3i> points, Vec3i origin, double radius) {
        if (points == null || points.isEmpty() || origin == null || radius <= 0.0) {
            return null;
        }
        double bestDistanceSq = radius * radius;
        Vec3i best = null;
        for (Vec3i point : points) {
            if (point == null) {
                continue;
            }
            double distanceSq = point.distanceSquaredTo(origin);
            if (distanceSq <= bestDistanceSq) {
                bestDistanceSq = distanceSq;
                best = point;
            }
        }
        return best;
    }

    private static String slotTypeLabel(BuildSlot slot) {
        if (slot == null) {
            return "standard";
        }
        String normalized = normalizeSlotType(slot.slotType, "standard");
        return normalized == null ? "standard" : normalized;
    }

    private static String slotDebugLabel(BuildSlot slot) {
        if (slot == null) {
            return "standard";
        }
        String layout = slot.layout == null || slot.layout.isBlank() ? "solo" : slot.layout;
        String owner = slot.ownerTeam == null || slot.ownerTeam.isBlank() ? "shared" : slot.ownerTeam;
        String segment = slot.segment == null ? "" : slot.segment;
        String suffix = segment.isBlank() ? "" : " | segment=" + segment;
        return slotTypeLabel(slot) + " | layout=" + layout + " | owner=" + owner + suffix;
    }

    private static String normalizeOwnerTeam(String ownerTeam) {
        if (ownerTeam == null || ownerTeam.isBlank()) {
            return "shared";
        }
        return switch (ownerTeam.trim().toLowerCase(Locale.ROOT)) {
            case "blue" -> "blue";
            case "green" -> "green";
            default -> "shared";
        };
    }

    private static String slotMarkerBlockName(BuildSlot slot, boolean occupied) {
        if (slot == null) {
            return null;
        }
        if (isSuperLikeSlot(slot)) {
            return SUPER_SLOT_INTERACTION_BLOCK;
        }
        if (isTrapLikeSlot(slot)) {
            return occupied ? TRAP_SLOT_INTERACTION_BLOCK_OCCUPIED : TRAP_SLOT_INTERACTION_BLOCK;
        }
        return switch (normalizeOwnerTeam(slot.ownerTeam)) {
            case "blue" -> occupied ? SLOT_INTERACTION_BLOCK_BLUE_OCCUPIED : SLOT_INTERACTION_BLOCK_BLUE;
            case "green" -> occupied ? SLOT_INTERACTION_BLOCK_GREEN_OCCUPIED : SLOT_INTERACTION_BLOCK_GREEN;
            default -> occupied ? SLOT_INTERACTION_BLOCK_OCCUPIED : SLOT_INTERACTION_BLOCK;
        };
    }

    private static void placeImmediateSlotBlock(World world, BuildSlot slot) {
        if (world == null || slot == null || slot.position == null) {
            return;
        }
        String blockName = slotMarkerBlockName(slot, false);
        if (blockName == null || blockName.isBlank()) {
            return;
        }
        int blockId = BlockType.getBlockIdOrUnknown(blockName, "Failed to find block '%s' for slot placement.", blockName);
        if (blockId == Integer.MIN_VALUE || blockId == 0) {
            return;
        }
        BlockType blockType = BlockType.getAssetMap().getAsset(blockId);
        if (blockType == null) {
            return;
        }
        Vec3i point = slot.position;
        WorldChunk chunk = world.getChunkIfInMemory(ChunkUtil.indexChunkFromBlock(point.x, point.z));
        if (chunk == null) {
            return;
        }
        chunk.setBlock(point.x, point.y, point.z, blockId, blockType, 0, 0, 4);
    }

    private static void clearImmediateSlotBlock(World world, Vec3i point) {
        if (world == null || point == null) {
            return;
        }
        WorldChunk chunk = world.getChunkIfInMemory(ChunkUtil.indexChunkFromBlock(point.x, point.z));
        if (chunk == null) {
            return;
        }
        int existingBlockId = chunk.getBlock(point.x, point.y, point.z);
        if (!isSlotMarkerBlockId(existingBlockId)) {
            return;
        }
        chunk.breakBlock(point.x, point.y, point.z);
    }

    private static void clearImmediateSlotBlockNeighborhood(World world, Vec3i point) {
        if (world == null || point == null) {
            return;
        }
        for (int dy = -1; dy <= 1; dy++) {
            clearImmediateSlotBlock(world, new Vec3i(point.x, point.y + dy, point.z));
        }
    }

    private static boolean isSlotMarkerBlockId(int blockId) {
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
        int blockId = BlockType.getBlockIdOrUnknown(blockName, "Failed to find block '%s' for immediate slot cleanup.", blockName);
        return blockId == Integer.MIN_VALUE ? 0 : blockId;
    }

    private static Vec3i midpoint(Vec3i first, Vec3i second) {
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

    private static Vec3i firstPoint(List<Vec3i> points) {
        return points == null || points.isEmpty() ? null : points.get(0);
    }

    private static List<Vec3i> defaultTutorialTrapPoints(MapConfig mapConfig) {
        List<Vec3i> points = new ArrayList<>();
        if (mapConfig == null) {
            return points;
        }
        Vec3i laneATrap = midpoint(mapConfig.tutorialSpawnPointA, firstPoint(mapConfig.tutorialRoutePointsA));
        Vec3i laneBTrap = midpoint(mapConfig.tutorialSpawnPointB, firstPoint(mapConfig.tutorialRoutePointsB));
        Vec3i mergedEntry = mapConfig.tutorialRoutePointsA != null && mapConfig.tutorialRoutePointsA.size() > 1
            ? mapConfig.tutorialRoutePointsA.get(1)
            : firstPoint(mapConfig.tutorialRoutePointsA);
        Vec3i mergedTrap = midpoint(mergedEntry, mapConfig.tutorialVaultPoint);
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

    private static BuildSlot createBuildSlot(BuildSlotsConfig slotsConfig, String slotType, Vec3i position) {
        int nextIndex = 1;
        for (BuildSlot existing : slotsConfig.slots) {
            if (slotType.equals(normalizeSlotType(existing.slotType, "standard"))) {
                nextIndex++;
            }
        }
        BuildSlot slot = new BuildSlot();
        slot.id = switch (slotType) {
            case "super" -> String.format(Locale.ROOT, "super_%02d", nextIndex);
            case "trap" -> String.format(Locale.ROOT, "trap_%02d", nextIndex);
            case "tutorial" -> String.format(Locale.ROOT, "tutorial_%02d", nextIndex);
            case "tutorial_super" -> String.format(Locale.ROOT, "tutorial_super_%02d", nextIndex);
            case "tutorial_trap" -> String.format(Locale.ROOT, "tutorial_trap_%02d", nextIndex);
            default -> String.format(Locale.ROOT, "slot_%02d", nextIndex);
        };
        slot.label = switch (slotType) {
            case "super" -> "РЎСѓРїРµСЂ-СЃР»РѕС‚ " + nextIndex;
            case "trap" -> "Р›РѕРІСѓС€РєР° " + nextIndex;
            case "tutorial" -> "РЎР»РѕС‚ РѕР±СѓС‡РµРЅРёСЏ " + nextIndex;
            case "tutorial_super" -> "РЎСѓРїРµСЂ-СЃР»РѕС‚ РѕР±СѓС‡РµРЅРёСЏ " + nextIndex;
            case "tutorial_trap" -> "Р›РѕРІСѓС€РєР° РѕР±СѓС‡РµРЅРёСЏ " + nextIndex;
            default -> "РЎР»РѕС‚ СЃС‚СЂРѕРёС‚РµР»СЊСЃС‚РІР° " + nextIndex;
        };
        slot.position = position;
        slot.slotType = slotType;
        slot.modulesAllowed = !"super".equals(slotType) && !"trap".equals(slotType)
            && !"tutorial_super".equals(slotType) && !"tutorial_trap".equals(slotType);
        if ("super".equals(slotType) || "tutorial_super".equals(slotType)) {
            slot.allowedTowerIds = new ArrayList<>(superTowerIds());
        } else if ("trap".equals(slotType) || "tutorial_trap".equals(slotType)) {
            slot.allowedTowerIds = new ArrayList<>(trapTowerIds());
        }
        return slot;
    }

    private static void configureDuoSlot(BuildSlot slot, String ownerTeam, String segment) {
        if (slot == null) {
            return;
        }
        slot.layout = "duo";
        slot.ownerTeam = ownerTeam == null || ownerTeam.isBlank() ? "shared" : ownerTeam;
        slot.segment = segment == null ? "" : segment;
    }

    private static boolean isTrapLikeSlot(BuildSlot slot) {
        String slotType = slotTypeLabel(slot);
        return "trap".equals(slotType) || "tutorial_trap".equals(slotType);
    }

    private static boolean isSuperLikeSlot(BuildSlot slot) {
        String slotType = slotTypeLabel(slot);
        return "super".equals(slotType) || "tutorial_super".equals(slotType);
    }

    private static boolean isAutoDuoSegment(String segment) {
        if (segment == null || segment.isBlank()) {
            return false;
        }
        return switch (segment) {
            case "blue_front", "green_front", "blue_trap", "green_trap", "shared_front", "shared_trap", "shared_super" -> true;
            default -> false;
        };
    }

    private static String defaultDuoSegment(BuildSlot slot, String ownerTeam) {
        if ("blue".equals(ownerTeam)) {
            return isTrapLikeSlot(slot) ? "blue_trap" : "blue_front";
        }
        if ("green".equals(ownerTeam)) {
            return isTrapLikeSlot(slot) ? "green_trap" : "green_front";
        }
        if (isSuperLikeSlot(slot)) {
            return "shared_super";
        }
        if (isTrapLikeSlot(slot)) {
            return "shared_trap";
        }
        return "shared_front";
    }

    private static BuildSlot nearestBuildSlot(BuildSlotsConfig slotsConfig, Vec3i position, int radius) {
        if (slotsConfig == null || position == null || radius <= 0) {
            return null;
        }
        BuildSlot nearest = null;
        double bestDistance = (double) radius * radius;
        for (BuildSlot slot : slotsConfig.slots) {
            if (slot == null || slot.position == null) {
                continue;
            }
            double distance = slot.position.distanceSquaredTo(position);
            if (distance <= bestDistance) {
                bestDistance = distance;
                nearest = slot;
            }
        }
        return nearest;
    }

    private static List<String> superTowerIds() {
        return List.of("heart_of_roots", "storm_monolith", "seed_idol");
    }

    private static List<String> trapTowerIds() {
        return List.of("root_snare", "spore_mine", "frost_seal");
    }

    private static final class StatusCommand extends AbstractWorldCommand {
        private final BankDefenseRepository repository;
        private final BankDefenseRuntime runtime;

        private StatusCommand(BankDefenseRepository repository, BankDefenseRuntime runtime) {
            super("status", "РџРѕРєР°Р·Р°С‚СЊ РєРѕРЅС„РёРі Рё СЃРѕСЃС‚РѕСЏРЅРёРµ СЂРµР¶РёРјР°");
            this.repository = repository;
            this.runtime = runtime;
        }

        @Override
        protected void execute(CommandContext context, World world, Store<EntityStore> store) {
            try {
                BankDefenseRepository.Snapshot snapshot = this.runtime.loadSnapshot(world);
                List<String> issues = this.runtime.validateWorld(world);
                RuntimeStatus runtimeStatus = this.runtime.getRuntimeStatus(world);
                MatchState matchState = runtimeStatus.matchState;
                ProgressionUiState progression = this.runtime.getProgressionUiState(world);
                int superSlots = 0;
                for (BuildSlot slot : snapshot.buildSlots.slots) {
                    if (isSuperSlot(slot)) {
                        superSlots++;
                    }
                }
                int standardSlots = snapshot.buildSlots.slots.size() - superSlots;

                StringBuilder message = new StringBuilder();
                message.append(snapshot.branding.title).append('\n');
                message.append("РњРёСЂ: ").append(world.getName()).append('\n');
                message.append("Р“Р»РѕР±Р°Р»СЊРЅС‹Р№ РєРѕРЅС„РёРі: ").append(this.repository.getGlobalConfigRoot()).append('\n');
                message.append("РљРѕРЅС„РёРі РјРёСЂР°: ").append(this.repository.getWorldConfigRoot(world)).append('\n');
                message.append("Spawn A: ").append(markerSummary(snapshot.map.spawnPointA)).append('\n');
                message.append("Spawn B: ").append(markerSummary(snapshot.map.spawnPointB)).append('\n');
                message.append("Spawn C: ").append(markerSummary(snapshot.map.spawnPointC)).append('\n');
                message.append("Р‘Р°РЅРє: ").append(markerSummary(snapshot.map.bankCenter))
                    .append(" | Р”СѓРїР»Рѕ: ").append(markerSummary(snapshot.map.vaultPoint))
                    .append(" | РЎС‚Р°СЂС‚: ").append(markerSummary(snapshot.map.playerStart)).append('\n');
                message.append("Control: ").append(markerSummary(snapshot.map.controlPoint))
                    .append(" | Vendor: ").append(markerSummary(snapshot.map.vendorPoint))
                    .append(" | Mode: ").append(markerSummary(snapshot.map.modePoint))
                    .append(" | Qube Solo: ").append(markerSummary(snapshot.map.soloQubeCorePoint != null ? snapshot.map.soloQubeCorePoint : snapshot.map.duoPoint))
                    .append(" | Qube Duo: ").append(markerSummary(snapshot.map.duoQubeCorePoint))
                    .append(" | Stats: ").append(markerSummary(snapshot.map.statisticsPoint)).append('\n');
                message.append("Duo starts: blue=").append(markerSummary(snapshot.map.duoPlayerStartBlue))
                    .append(", green=").append(markerSummary(snapshot.map.duoPlayerStartGreen)).append('\n');
                message.append("Duo spawns: blue=").append(markerSummary(snapshot.map.duoSpawnPointBlue))
                    .append(", green=").append(markerSummary(snapshot.map.duoSpawnPointGreen))
                    .append(" | Duo bank=").append(markerSummary(snapshot.map.duoBankCenter))
                    .append(" | Duo hollow=").append(markerSummary(snapshot.map.duoVaultPoint)).append('\n');
                message.append("Duo seals: blue=").append(markerSummary(snapshot.map.duoSealNodeBluePoint))
                    .append(", green=").append(markerSummary(snapshot.map.duoSealNodeGreenPoint))
                    .append(" | Duo stats=").append(markerSummary(snapshot.map.duoStatisticsPoint)).append('\n');
                message.append("РњР°СЂС€СЂСѓС‚С‹: A=").append(snapshot.map.routePointsA.size())
                    .append(", B=").append(snapshot.map.routePointsB.size())
                    .append(", C=").append(snapshot.map.routePointsC.size())
                    .append(", DuoBlue=").append(snapshot.map.duoRouteBlue.size())
                    .append(", DuoGreen=").append(snapshot.map.duoRouteGreen.size())
                    .append(", РІСЃРµРіРѕ=").append(runtimeStatus.routePoints).append('\n');
                message.append("РЎР»РѕС‚С‹: standard=").append(standardSlots)
                    .append(", super=").append(superSlots)
                    .append(", РїРѕСЃС‚СЂРѕРµРЅРѕ=").append(runtimeStatus.placedTowers).append('\n');
                message.append("РњР°С‚С‡: ").append(matchState == null ? "РЅРµС‚" : matchState.gameState + " / " + matchState.waveState)
                    .append(", РІРѕР»РЅР°=").append(matchState == null ? 0 : matchState.currentWave)
                    .append(", СЃСЂРµРґСЃС‚РІР°=").append(matchState == null ? 0 : matchState.currency)
                    .append(", РґСѓРїР»Рѕ=").append(matchState == null ? 0 : matchState.bankHp)
                    .append(", РІСЂР°РіРё=").append(runtimeStatus.activeEnemies)
                    .append(", РѕС‡РµСЂРµРґСЊ=").append(runtimeStatus.pendingSpawns).append('\n');
                message.append("РџСЂРѕРіСЂРµСЃСЃРёСЏ: СЏРґСЂР°=").append(progression.cores)
                    .append(", СЂРµРєРѕСЂРґ=").append(progression.highestWave)
                    .append(", Р·Р°Р±РµРіРё=").append(progression.totalRuns)
                    .append(", РїРѕР±РµРґС‹=").append(progression.totalVictories)
                    .append(", РєРѕРЅС‚СЂР°РєС‚=").append(progression.activeContractName).append('\n');
                message.append("Р’РёР·СѓР°Р»РёР·Р°С†РёСЏ: enabled=").append(runtimeStatus.visualizationEnabled)
                    .append(", entities=").append(runtimeStatus.renderedOverlayBlocks).append('\n');
                message.append(formatValidation(issues));
                context.sendMessage(Message.raw(message.toString()));
            } catch (IOException e) {
                context.sendMessage(Message.raw("РќРµ СѓРґР°Р»РѕСЃСЊ Р·Р°РіСЂСѓР·РёС‚СЊ СЃРѕСЃС‚РѕСЏРЅРёРµ СЂРµР¶РёРјР°: " + e.getMessage()));
            }
        }
    }

    private static final class DuoStatusCommand extends AbstractWorldCommand {
        private final BankDefenseRuntime runtime;

        private DuoStatusCommand(BankDefenseRuntime runtime) {
            super("duostatus", "Show Duo-only map and runtime status");
            this.runtime = runtime;
        }

        @Override
        protected void execute(CommandContext context, World world, Store<EntityStore> store) {
            try {
                BankDefenseRepository.Snapshot rawSnapshot = this.runtime.loadSnapshot(world);
                BankDefenseRepository.Snapshot duoSnapshot = this.runtime.loadDuoSnapshot(world);
                List<String> issues = this.runtime.validateDuoWorld(world);
                RuntimeStatus runtimeStatus = this.runtime.getRuntimeStatus(world);
                MatchState matchState = runtimeStatus.matchState;

                int blueStandard = 0;
                int greenStandard = 0;
                int blueTrap = 0;
                int greenTrap = 0;
                int sharedSuper = 0;
                int sharedOther = 0;
                for (BuildSlot slot : duoSnapshot.buildSlots.slots) {
                    if (slot == null) {
                        continue;
                    }
                    String slotType = normalizeSlotType(slot.slotType, "standard");
                    String ownerTeam = normalizeOwnerTeam(slot.ownerTeam);
                    if ("super".equals(slotType) && "shared".equals(ownerTeam)) {
                        sharedSuper++;
                    } else if ("trap".equals(slotType) && "blue".equals(ownerTeam)) {
                        blueTrap++;
                    } else if ("trap".equals(slotType) && "green".equals(ownerTeam)) {
                        greenTrap++;
                    } else if ("standard".equals(slotType) && "blue".equals(ownerTeam)) {
                        blueStandard++;
                    } else if ("standard".equals(slotType) && "green".equals(ownerTeam)) {
                        greenStandard++;
                    } else if ("shared".equals(ownerTeam)) {
                        sharedOther++;
                    }
                }

                String selectedMode = rawSnapshot == null || rawSnapshot.map == null || rawSnapshot.map.selectedModeId == null || rawSnapshot.map.selectedModeId.isBlank()
                    ? "solo"
                    : rawSnapshot.map.selectedModeId;

                StringBuilder message = new StringBuilder();
                message.append("DUO STATUS").append('\n');
                message.append("world=").append(world.getName())
                    .append(" | selectedMode=").append(selectedMode)
                    .append(" | strictNpcMarkers=").append(duoSnapshot.map.strictNpcMarkers)
                    .append(" | soloTest=").append(this.runtime.isDuoSoloTestEnabled(world) ? "ON" : "OFF").append('\n');
                message.append("npcs: qube=").append(markerSummary(duoSnapshot.map.duoQubeCorePoint != null ? duoSnapshot.map.duoQubeCorePoint : duoSnapshot.map.duoPoint))
                    .append(" | team=").append(markerSummary(duoSnapshot.map.duoTeamPoint))
                    .append(" | teleport=").append(markerSummary(duoSnapshot.map.duoTeleportPoint))
                    .append(" | stats=").append(markerSummary(duoSnapshot.map.duoStatisticsPoint)).append('\n');
                message.append("starts: blue=").append(markerSummary(duoSnapshot.map.duoPlayerStartBlue))
                    .append(", green=").append(markerSummary(duoSnapshot.map.duoPlayerStartGreen)).append('\n');
                message.append("spawns: blue=").append(markerSummary(duoSnapshot.map.duoSpawnPointBlue))
                    .append(", green=").append(markerSummary(duoSnapshot.map.duoSpawnPointGreen)).append('\n');
                message.append("bank=").append(markerSummary(duoSnapshot.map.duoBankCenter))
                    .append(" | hollow=").append(markerSummary(duoSnapshot.map.duoVaultPoint)).append('\n');
                message.append("teleportTargets: duo=").append(markerSummary(duoSnapshot.map.duoTeleportTarget))
                    .append(" | solo=").append(markerSummary(duoSnapshot.map.soloTeleportTarget)).append('\n');
                message.append("routes: blue=").append(duoSnapshot.map.duoRouteBlue.size())
                    .append(", green=").append(duoSnapshot.map.duoRouteGreen.size())
                    .append(", chests=").append(duoSnapshot.map.duoChestSpawnPoints.size()).append('\n');
                message.append("slots: blueStd=").append(blueStandard)
                    .append(", greenStd=").append(greenStandard)
                    .append(", blueTrap=").append(blueTrap)
                    .append(", greenTrap=").append(greenTrap)
                    .append(", sharedSuper=").append(sharedSuper);
                if (sharedOther > 0) {
                    message.append(", sharedOther=").append(sharedOther);
                }
                message.append('\n');
                message.append("match: state=").append(matchState == null ? "none" : matchState.gameState + "/" + matchState.waveState)
                    .append(", wave=").append(matchState == null ? 0 : matchState.currentWave)
                    .append(", enemies=").append(runtimeStatus.activeEnemies)
                    .append(", pending=").append(runtimeStatus.pendingSpawns)
                    .append(", towers=").append(runtimeStatus.placedTowers)
                    .append(", pinnedSpawnChunks=").append(runtimeStatus.pinnedSpawnChunks).append('\n');
                if (issues.isEmpty()) {
                    message.append("validation=OK");
                } else {
                    message.append("validation=").append(issues.size()).append(" issue(s): ");
                    for (int i = 0; i < issues.size(); i++) {
                        if (i > 0) {
                            message.append(" | ");
                        }
                        message.append(issues.get(i));
                    }
                }
                context.sendMessage(Message.raw(message.toString()));
            } catch (IOException e) {
                context.sendMessage(Message.raw("Failed to load Duo status: " + e.getMessage()));
            }
        }
    }

    private static final class DuoTestCommand extends AbstractCommandCollection {
        private DuoTestCommand(BankDefenseRuntime runtime) {
            super("duotest", "Toggle Duo solo test mode");
            this.addSubCommand(new DuoTestOnCommand(runtime));
            this.addSubCommand(new DuoTestOffCommand(runtime));
            this.addSubCommand(new DuoTestStatusCommand(runtime));
        }
    }

    private static final class DuoTestOnCommand extends AbstractWorldCommand {
        private final BankDefenseRuntime runtime;

        private DuoTestOnCommand(BankDefenseRuntime runtime) {
            super("on", "Allow one player to run Duo tests alone");
            this.runtime = runtime;
        }

        @Override
        protected void execute(CommandContext context, World world, Store<EntityStore> store) {
            context.sendMessage(Message.raw(this.runtime.setDuoSoloTestEnabled(world, true).message));
        }
    }

    private static final class DuoTestOffCommand extends AbstractWorldCommand {
        private final BankDefenseRuntime runtime;

        private DuoTestOffCommand(BankDefenseRuntime runtime) {
            super("off", "Restore normal two-player Duo rules");
            this.runtime = runtime;
        }

        @Override
        protected void execute(CommandContext context, World world, Store<EntityStore> store) {
            context.sendMessage(Message.raw(this.runtime.setDuoSoloTestEnabled(world, false).message));
        }
    }

    private static final class DuoTestStatusCommand extends AbstractWorldCommand {
        private final BankDefenseRuntime runtime;

        private DuoTestStatusCommand(BankDefenseRuntime runtime) {
            super("status", "Show Duo solo test mode");
            this.runtime = runtime;
        }

        @Override
        protected void execute(CommandContext context, World world, Store<EntityStore> store) {
            context.sendMessage(Message.raw("Duo solo-test: " + (this.runtime.isDuoSoloTestEnabled(world) ? "ON" : "OFF")));
        }
    }

    private static final class DuoFillMaxCommand extends AbstractPlayerCommand {
        private final BankDefenseRuntime runtime;

        private DuoFillMaxCommand(BankDefenseRuntime runtime) {
            super("duofillmax", "Заполнить все duo-пады самыми дорогими башнями 10 уровня для тестов");
            this.runtime = runtime;
        }

        @Override
        protected void execute(CommandContext context, Store<EntityStore> store, Ref<EntityStore> ref, PlayerRef playerRef, World world) {
            try {
                context.sendMessage(Message.raw(this.runtime.fillDuoPadsWithMaxTowers(world, playerRef).message));
            } catch (IOException e) {
                context.sendMessage(Message.raw("Не удалось заполнить duo-пады тестовыми башнями: " + e.getMessage()));
            }
        }
    }

    private static final class HudCommand extends AbstractCommandCollection {
        private HudCommand(BankDefenseRuntime runtime) {
            super("hud", "Toggle the custom Bank Defense HUD for the current player");
            this.addSubCommand(new HudOnCommand(runtime));
            this.addSubCommand(new HudOffCommand(runtime));
            this.addSubCommand(new HudToggleCommand(runtime));
            this.addSubCommand(new HudStatusCommand(runtime));
        }
    }

    private static final class HudOnCommand extends AbstractPlayerCommand {
        private final BankDefenseRuntime runtime;

        private HudOnCommand(BankDefenseRuntime runtime) {
            super("on", "Show the custom Bank Defense HUD");
            this.runtime = runtime;
        }

        @Override
        protected void execute(CommandContext context, Store<EntityStore> store, Ref<EntityStore> ref, PlayerRef playerRef, World world) {
            context.sendMessage(Message.raw(this.runtime.setCustomHudHidden(world, playerRef, false).message));
        }
    }

    private static final class HudOffCommand extends AbstractPlayerCommand {
        private final BankDefenseRuntime runtime;

        private HudOffCommand(BankDefenseRuntime runtime) {
            super("off", "Hide the custom Bank Defense HUD");
            this.runtime = runtime;
        }

        @Override
        protected void execute(CommandContext context, Store<EntityStore> store, Ref<EntityStore> ref, PlayerRef playerRef, World world) {
            context.sendMessage(Message.raw(this.runtime.setCustomHudHidden(world, playerRef, true).message));
        }
    }

    private static final class HudToggleCommand extends AbstractPlayerCommand {
        private final BankDefenseRuntime runtime;

        private HudToggleCommand(BankDefenseRuntime runtime) {
            super("toggle", "Toggle the custom Bank Defense HUD");
            this.runtime = runtime;
        }

        @Override
        protected void execute(CommandContext context, Store<EntityStore> store, Ref<EntityStore> ref, PlayerRef playerRef, World world) {
            context.sendMessage(Message.raw(this.runtime.toggleCustomHud(world, playerRef).message));
        }
    }

    private static final class HudStatusCommand extends AbstractPlayerCommand {
        private final BankDefenseRuntime runtime;

        private HudStatusCommand(BankDefenseRuntime runtime) {
            super("status", "Show the custom Bank Defense HUD state");
            this.runtime = runtime;
        }

        @Override
        protected void execute(CommandContext context, Store<EntityStore> store, Ref<EntityStore> ref, PlayerRef playerRef, World world) {
            context.sendMessage(Message.raw("Custom HUD: " + (this.runtime.isCustomHudHidden(world, playerRef) ? "OFF" : "ON")));
        }
    }

    private static final class ValidateCommand extends AbstractWorldCommand {
        private final BankDefenseRuntime runtime;

        private ValidateCommand(BankDefenseRuntime runtime) {
            super("validate", "РџСЂРѕРІРµСЂРёС‚СЊ РґР°РЅРЅС‹Рµ СЂРµР¶РёРјР° Рё С„Р°Р№Р»С‹ РєР°СЂС‚С‹");
            this.runtime = runtime;
        }

        @Override
        protected void execute(CommandContext context, World world, Store<EntityStore> store) {
            try {
                context.sendMessage(Message.raw(formatValidation(this.runtime.validateWorld(world))));
            } catch (IOException e) {
                context.sendMessage(Message.raw("РџСЂРѕРІРµСЂРєР° Р·Р°РІРµСЂС€РёР»Р°СЃСЊ РѕС€РёР±РєРѕР№: " + e.getMessage()));
            }
        }
    }

    private static final class MatchCommand extends AbstractCommandCollection {
        private MatchCommand(BankDefenseRuntime runtime) {
            super("match", "РЈРїСЂР°РІР»РµРЅРёРµ РјР°С‚С‡РµРј");
            this.addSubCommand(new MatchResetCommand(runtime));
            this.addSubCommand(new MatchStartCommand(runtime));
            this.addSubCommand(new MatchModeCommand(runtime, "solo"));
            this.addSubCommand(new MatchModeCommand(runtime, "duo"));
        }
    }

    private static final class MenuCommand extends AbstractPlayerCommand {
        private final BankDefenseRuntime runtime;

        private MenuCommand(BankDefenseRuntime runtime) {
            super("menu", "РћС‚РєСЂС‹С‚СЊ С‡РёС‚-РјРµРЅСЋ");
            this.runtime = runtime;
        }

        @Override
        protected void execute(CommandContext context, Store<EntityStore> store, Ref<EntityStore> ref, PlayerRef playerRef, World world) {
            Player player = store.getComponent(ref, Player.getComponentType());
            if (player == null) {
                context.sendMessage(Message.raw("РРіСЂРѕРє РЅРµРґРѕСЃС‚СѓРїРµРЅ."));
                return;
            }
            player.getPageManager().openCustomPage(ref, store, new BankDefenseControlPage(playerRef, this.runtime));
            context.sendMessage(Message.raw("РћС‚РєСЂС‹С‚Рѕ С‡РёС‚-РјРµРЅСЋ."));
        }
    }

    private static final class ProgressionCommand extends AbstractPlayerCommand {
        private final BankDefenseRuntime runtime;

        private ProgressionCommand(BankDefenseRuntime runtime) {
            super("progression", "РћС‚РєСЂС‹С‚СЊ РґСЂРµРІРѕ РїСЂРѕРіСЂРµСЃСЃРёРё");
            this.runtime = runtime;
        }

        @Override
        protected void execute(CommandContext context, Store<EntityStore> store, Ref<EntityStore> ref, PlayerRef playerRef, World world) {
            Player player = store.getComponent(ref, Player.getComponentType());
            if (player == null) {
                context.sendMessage(Message.raw("РРіСЂРѕРє РЅРµРґРѕСЃС‚СѓРїРµРЅ."));
                return;
            }
            player.getPageManager().openCustomPage(ref, store, new BankDefenseProgressionPage(playerRef, this.runtime));
            context.sendMessage(Message.raw("РћС‚РєСЂС‹С‚Рѕ РґСЂРµРІРѕ РїСЂРѕРіСЂРµСЃСЃРёРё."));
        }
    }

    private static final class ProgressResetCommand extends AbstractWorldCommand {
        private final BankDefenseRuntime runtime;

        private ProgressResetCommand(BankDefenseRuntime runtime) {
            super("progressreset", "РЎР±СЂРѕСЃРёС‚СЊ РѕСЃРєРѕР»РєРё РґСѓРїР»Р° Рё РІСЃРµ СѓР»СѓС‡С€РµРЅРёСЏ РїСЂРѕРіСЂРµСЃСЃРёРё");
            this.runtime = runtime;
        }

        @Override
        protected void execute(CommandContext context, World world, Store<EntityStore> store) {
            try {
                context.sendMessage(Message.raw(this.runtime.resetProgression(world).message));
            } catch (IOException e) {
                context.sendMessage(Message.raw("РќРµ СѓРґР°Р»РѕСЃСЊ СЃР±СЂРѕСЃРёС‚СЊ РїСЂРѕРіСЂРµСЃСЃРёСЋ: " + e.getMessage()));
            }
        }
    }

    private static final class StatsResetCommand extends AbstractWorldCommand {
        private final BankDefenseRuntime runtime;

        private StatsResetCommand(BankDefenseRuntime runtime) {
            super("statsreset", "Reset Bank Defense statistics");
            this.runtime = runtime;
        }

        @Override
        protected void execute(CommandContext context, World world, Store<EntityStore> store) {
            try {
                context.sendMessage(Message.raw(this.runtime.resetStatistics(world).message));
            } catch (IOException e) {
                context.sendMessage(Message.raw("Failed to reset statistics: " + e.getMessage()));
            }
        }
    }

    private static final class TutorialResetCommand extends AbstractPlayerCommand {
        private final BankDefenseRuntime runtime;

        private TutorialResetCommand(BankDefenseRuntime runtime) {
            super("tutorialreset", "РџРµСЂРµР·Р°РїСѓСЃС‚РёС‚СЊ РѕР±СѓС‡РµРЅРёРµ Рё РІРµСЂРЅСѓС‚СЊ РёРіСЂРѕРєР° Рє Р’РѕР»С€РµР±РЅРѕРјСѓ РљРІРёР±РµРєСѓ");
            this.runtime = runtime;
        }

        @Override
        protected void execute(CommandContext context, Store<EntityStore> store, Ref<EntityStore> ref, PlayerRef playerRef, World world) {
            try {
                context.sendMessage(Message.raw(this.runtime.resetTutorial(world, ref).message));
            } catch (IOException e) {
                context.sendMessage(Message.raw("РќРµ СѓРґР°Р»РѕСЃСЊ РїРµСЂРµР·Р°РїСѓСЃС‚РёС‚СЊ РѕР±СѓС‡РµРЅРёРµ: " + e.getMessage()));
            }
        }
    }

    private static final class TutorialSkipCommand extends AbstractPlayerCommand {
        private final BankDefenseRuntime runtime;

        private TutorialSkipCommand(BankDefenseRuntime runtime) {
            super("tutorialskip", "РџСЂРѕРїСѓСЃС‚РёС‚СЊ РѕР±СѓС‡РµРЅРёРµ Рё РѕС‚РјРµС‚РёС‚СЊ РµРіРѕ РїСЂРѕР№РґРµРЅРЅС‹Рј");
            this.runtime = runtime;
        }

        @Override
        protected void execute(CommandContext context, Store<EntityStore> store, Ref<EntityStore> ref, PlayerRef playerRef, World world) {
            try {
                context.sendMessage(Message.raw(this.runtime.skipTutorial(world, ref).message));
            } catch (IOException e) {
                context.sendMessage(Message.raw("РќРµ СѓРґР°Р»РѕСЃСЊ РїСЂРѕРїСѓСЃС‚РёС‚СЊ РѕР±СѓС‡РµРЅРёРµ: " + e.getMessage()));
            }
        }
    }

    private static final class NoBuildCommand extends AbstractCommandCollection {
        private NoBuildCommand() {
            super("nobuild", "Toggle map edit lock");
            this.addSubCommand(new NoBuildOnCommand());
            this.addSubCommand(new NoBuildOffCommand());
            this.addSubCommand(new NoBuildStatusCommand());
        }
    }

    private static final class NoBuildOnCommand extends AbstractWorldCommand {
        private NoBuildOnCommand() {
            super("on", "Enable map edit lock");
        }

        @Override
        protected void execute(CommandContext context, World world, Store<EntityStore> store) {
            BankDefensePlugin.setNoBuildEnabled(true);
            context.sendMessage(Message.raw("No-build: ON"));
        }
    }

    private static final class NoBuildOffCommand extends AbstractWorldCommand {
        private NoBuildOffCommand() {
            super("off", "Disable map edit lock");
        }

        @Override
        protected void execute(CommandContext context, World world, Store<EntityStore> store) {
            BankDefensePlugin.setNoBuildEnabled(false);
            context.sendMessage(Message.raw("No-build: OFF"));
        }
    }

    private static final class NoBuildStatusCommand extends AbstractWorldCommand {
        private NoBuildStatusCommand() {
            super("status", "Show map edit lock state");
        }

        @Override
        protected void execute(CommandContext context, World world, Store<EntityStore> store) {
            context.sendMessage(Message.raw("No-build: " + (BankDefensePlugin.isNoBuildEnabled() ? "ON" : "OFF")));
        }
    }

    private static final class BuilderToolsCommand extends AbstractCommandCollection {
        private BuilderToolsCommand() {
            super("buildertools", "Toggle builder tools access");
            this.addSubCommand(new BuilderToolsOnCommand());
            this.addSubCommand(new BuilderToolsOffCommand());
            this.addSubCommand(new BuilderToolsStatusCommand());
        }
    }

    private static final class BuilderToolsOnCommand extends AbstractPlayerCommand {
        private BuilderToolsOnCommand() {
            super("on", "Enable builder tools for the current player");
        }

        @Override
        protected void execute(CommandContext context, Store<EntityStore> store, Ref<EntityStore> ref, PlayerRef playerRef, World world) {
            BankDefensePlugin.setBuilderToolsEnabled(true);
            Player player = store.getComponent(ref, Player.getComponentType());
            BankDefensePlugin.applyEditorPermissions(player);
            context.sendMessage(Message.raw("Builder tools: ON"));
        }
    }

    private static final class BuilderToolsOffCommand extends AbstractPlayerCommand {
        private BuilderToolsOffCommand() {
            super("off", "Disable builder tools for the current player");
        }

        @Override
        protected void execute(CommandContext context, Store<EntityStore> store, Ref<EntityStore> ref, PlayerRef playerRef, World world) {
            BankDefensePlugin.setBuilderToolsEnabled(false);
            Player player = store.getComponent(ref, Player.getComponentType());
            BankDefensePlugin.applyEditorPermissions(player);
            context.sendMessage(Message.raw("Builder tools: OFF"));
        }
    }

    private static final class BuilderToolsStatusCommand extends AbstractWorldCommand {
        private BuilderToolsStatusCommand() {
            super("status", "Show builder tools access state");
        }

        @Override
        protected void execute(CommandContext context, World world, Store<EntityStore> store) {
            context.sendMessage(Message.raw("Builder tools: " + (BankDefensePlugin.isBuilderToolsEnabled() ? "ON" : "OFF")));
        }
    }

    private static final class MobilityCommand extends AbstractCommandCollection {
        private MobilityCommand() {
            super("mobility", "Toggle fly and speed boost for the current player");
            this.addSubCommand(new MobilityOnCommand());
            this.addSubCommand(new MobilityOffCommand());
            this.addSubCommand(new MobilityStatusCommand());
        }
    }

    private static final class MobilityOnCommand extends AbstractPlayerCommand {
        private MobilityOnCommand() {
            super("on", "Enable fly and x5 speed for the current player");
        }

        @Override
        protected void execute(CommandContext context, Store<EntityStore> store, Ref<EntityStore> ref, PlayerRef playerRef, World world) {
            Player player = store.getComponent(ref, Player.getComponentType());
            MovementManager movementManager = store.getComponent(ref, MovementManager.getComponentType());
            if (player == null || movementManager == null || movementManager.getSettings() == null || player.getPlayerConnection() == null) {
                context.sendMessage(Message.raw("Mobility: unavailable"));
                return;
            }
            applyMobilityBoost(movementManager.getSettings(), movementManager.getDefaultSettings());
            movementManager.update(player.getPlayerConnection());
            context.sendMessage(Message.raw("Mobility: ON (fly + x5 speed)"));
        }
    }

    private static final class MobilityOffCommand extends AbstractPlayerCommand {
        private MobilityOffCommand() {
            super("off", "Disable fly and speed boost for the current player");
        }

        @Override
        protected void execute(CommandContext context, Store<EntityStore> store, Ref<EntityStore> ref, PlayerRef playerRef, World world) {
            Player player = store.getComponent(ref, Player.getComponentType());
            MovementManager movementManager = store.getComponent(ref, MovementManager.getComponentType());
            if (player == null || movementManager == null || player.getPlayerConnection() == null) {
                context.sendMessage(Message.raw("Mobility: unavailable"));
                return;
            }
            movementManager.resetDefaultsAndUpdate(ref, store);
            movementManager.update(player.getPlayerConnection());
            context.sendMessage(Message.raw("Mobility: OFF"));
        }
    }

    private static final class MobilityStatusCommand extends AbstractPlayerCommand {
        private MobilityStatusCommand() {
            super("status", "Show fly and speed boost state for the current player");
        }

        @Override
        protected void execute(CommandContext context, Store<EntityStore> store, Ref<EntityStore> ref, PlayerRef playerRef, World world) {
            MovementManager movementManager = store.getComponent(ref, MovementManager.getComponentType());
            if (movementManager == null || movementManager.getSettings() == null) {
                context.sendMessage(Message.raw("Mobility: unavailable"));
                return;
            }
            MovementSettings settings = movementManager.getSettings();
            MovementSettings defaults = movementManager.getDefaultSettings();
            float factor = defaults != null && defaults.baseSpeed > 0.001f
                ? settings.baseSpeed / defaults.baseSpeed
                : 1.0f;
            context.sendMessage(Message.raw(String.format(Locale.US, "Mobility: fly=%s, speed x%.1f", settings.canFly ? "ON" : "OFF", factor)));
        }
    }

    private static void applyMobilityBoost(MovementSettings settings, MovementSettings defaults) {
        if (settings == null) {
            return;
        }
        MovementSettings base = defaults == null ? new MovementSettings(settings) : defaults;
        float boostedBaseSpeed = Math.max(0.01f, base.baseSpeed * 5.0f);
        settings.canFly = true;
        settings.baseSpeed = boostedBaseSpeed;
        settings.jumpForce = Math.max(base.jumpForce, base.jumpForce * 2.2f);
        settings.swimJumpForce = Math.max(base.swimJumpForce, base.swimJumpForce * 2.2f);
        settings.fallJumpForce = Math.max(base.fallJumpForce, base.fallJumpForce * 2.2f);
        settings.horizontalFlySpeed = boostedBaseSpeed;
        settings.verticalFlySpeed = Math.max(boostedBaseSpeed, base.verticalFlySpeed * 1.8f);
        settings.maxSpeedMultiplier = Math.max(settings.maxSpeedMultiplier, 5.0f);
    }

    private static final class MarkDuoCommand extends AbstractPlayerCommand {
        private final BankDefenseRepository repository;

        private MarkDuoCommand(BankDefenseRepository repository) {
            super("markduo", "РџРѕСЃС‚Р°РІРёС‚СЊ NPC РџРµСЂРµС…РѕРґ РІ Duo Р РµР¶РёРј");
            this.repository = repository;
        }

        @Override
        protected void execute(CommandContext context, Store<EntityStore> store, Ref<EntityStore> ref, PlayerRef playerRef, World world) {
            try {
                MapConfig mapConfig = this.repository.loadMapConfig(world);
                Vec3i position = currentBlockPosition(store, ref);
                float yaw = currentYaw(store, ref);
                mapConfig.previousSoloQubeCorePoint = mapConfig.soloQubeCorePoint;
                mapConfig.soloQubeCorePoint = position;
                mapConfig.soloQubeCoreYaw = yaw;
                if (mapConfig.duoPoint == null) {
                    mapConfig.previousDuoPoint = mapConfig.duoPoint;
                    mapConfig.duoPoint = position;
                    mapConfig.duoYaw = yaw;
                }
                this.repository.saveMapConfig(world, mapConfig);
                refreshWorldVisualization(world);
                context.sendMessage(Message.raw("Маркер solo QubeCore установлен в " + position.toShortString() + "."));
            } catch (IOException e) {
                context.sendMessage(Message.raw("Не удалось поставить solo QubeCore: " + e.getMessage()));
            }
        }
    }

    private static final class ClearDuoCommand extends AbstractWorldCommand {
        private final BankDefenseRepository repository;

        private ClearDuoCommand(BankDefenseRepository repository) {
            super("clearduo", "РћС‡РёСЃС‚РёС‚СЊ NPC РџРµСЂРµС…РѕРґ РІ Duo Р РµР¶РёРј");
            this.repository = repository;
        }

        @Override
        protected void execute(CommandContext context, World world, Store<EntityStore> store) {
            try {
                MapConfig mapConfig = this.repository.loadMapConfig(world);
                mapConfig.previousSoloQubeCorePoint = mapConfig.soloQubeCorePoint;
                mapConfig.soloQubeCorePoint = null;
                mapConfig.soloQubeCoreYaw = null;
                this.repository.saveMapConfig(world, mapConfig);
                refreshWorldVisualization(world);
                context.sendMessage(Message.raw("Маркер solo QubeCore очищен."));
            } catch (IOException e) {
                context.sendMessage(Message.raw("Не удалось очистить solo QubeCore: " + e.getMessage()));
            }
        }
    }

    private static final class MarkStatsCommand extends AbstractPlayerCommand {
        private final BankDefenseRepository repository;

        private MarkStatsCommand(BankDefenseRepository repository) {
            super("markstats", "Поставить NPC статистики");
            this.repository = repository;
        }

        @Override
        protected void execute(CommandContext context, Store<EntityStore> store, Ref<EntityStore> ref, PlayerRef playerRef, World world) {
            try {
                MapConfig mapConfig = this.repository.loadMapConfig(world);
                Vec3i position = currentBlockPosition(store, ref);
                float yaw = currentYaw(store, ref);
                mapConfig.previousStatisticsPoint = mapConfig.statisticsPoint;
                mapConfig.statisticsPoint = position;
                mapConfig.statisticsYaw = yaw;
                this.repository.saveMapConfig(world, mapConfig);
                context.sendMessage(Message.raw("Маркер статистики установлен в " + position.toShortString() + "."));
            } catch (IOException e) {
                context.sendMessage(Message.raw("Не удалось поставить NPC статистики: " + e.getMessage()));
            }
        }
    }

    private static final class ClearStatsCommand extends AbstractWorldCommand {
        private final BankDefenseRepository repository;

        private ClearStatsCommand(BankDefenseRepository repository) {
            super("clearstats", "Очистить NPC статистики");
            this.repository = repository;
        }

        @Override
        protected void execute(CommandContext context, World world, Store<EntityStore> store) {
            try {
                MapConfig mapConfig = this.repository.loadMapConfig(world);
                mapConfig.previousStatisticsPoint = mapConfig.statisticsPoint;
                mapConfig.statisticsPoint = null;
                mapConfig.statisticsYaw = null;
                this.repository.saveMapConfig(world, mapConfig);
                context.sendMessage(Message.raw("Маркер статистики очищен."));
            } catch (IOException e) {
                context.sendMessage(Message.raw("Не удалось очистить NPC статистики: " + e.getMessage()));
            }
        }
    }

    private static final class RewardCommand extends AbstractPlayerCommand {
        private final BankDefenseRuntime runtime;

        private RewardCommand(BankDefenseRuntime runtime) {
            super("reward", "РћС‚РєСЂС‹С‚СЊ РІС‹Р±РѕСЂ РјРѕРґСѓР»СЊРЅРѕР№ РЅР°РіСЂР°РґС‹");
            this.runtime = runtime;
        }

        @Override
        protected void execute(CommandContext context, Store<EntityStore> store, Ref<EntityStore> ref, PlayerRef playerRef, World world) {
            Player player = store.getComponent(ref, Player.getComponentType());
            if (player == null) {
                context.sendMessage(Message.raw("РРіСЂРѕРє РЅРµРґРѕСЃС‚СѓРїРµРЅ."));
                return;
            }
            try {
                if (!this.runtime.getRewardUiState(world).rewardPending) {
                    context.sendMessage(Message.raw("РЎРµР№С‡Р°СЃ РЅРµС‚ Р°РєС‚РёРІРЅРѕР№ РЅР°РіСЂР°РґС‹ РјРѕРґСѓР»РµРј."));
                    return;
                }
            } catch (IOException e) {
                context.sendMessage(Message.raw("РќРµ СѓРґР°Р»РѕСЃСЊ Р·Р°РіСЂСѓР·РёС‚СЊ РЅР°РіСЂР°РґСѓ: " + e.getMessage()));
                return;
            }
            player.getPageManager().openCustomPage(ref, store, new BankDefenseRewardPage(playerRef, this.runtime));
            context.sendMessage(Message.raw("РћС‚РєСЂС‹С‚ РІС‹Р±РѕСЂ РјРѕРґСѓР»СЊРЅРѕР№ РЅР°РіСЂР°РґС‹."));
        }
    }

    private static final class RewardMenuCommand extends AbstractPlayerCommand {
        private final BankDefenseRuntime runtime;

        private RewardMenuCommand(BankDefenseRuntime runtime) {
            super("rewardmenu", "РћС‚РєСЂС‹С‚СЊ РјРµРЅСЋ РјРѕРґСѓР»РµР№ РІСЂСѓС‡РЅСѓСЋ");
            this.runtime = runtime;
        }

        @Override
        protected void execute(CommandContext context, Store<EntityStore> store, Ref<EntityStore> ref, PlayerRef playerRef, World world) {
            Player player = store.getComponent(ref, Player.getComponentType());
            if (player == null) {
                context.sendMessage(Message.raw("РРіСЂРѕРє РЅРµРґРѕСЃС‚СѓРїРµРЅ."));
                return;
            }
            try {
                BankDefenseRuntime.ActionResult result = this.runtime.ensureRewardChoicesForCommand(world);
                if (!result.success) {
                    context.sendMessage(Message.raw(result.message));
                    return;
                }
            } catch (IOException e) {
                context.sendMessage(Message.raw("РќРµ СѓРґР°Р»РѕСЃСЊ РїРѕРґРіРѕС‚РѕРІРёС‚СЊ РІС‹Р±РѕСЂ РјРѕРґСѓР»РµР№: " + e.getMessage()));
                return;
            }
            player.getPageManager().openCustomPage(ref, store, new BankDefenseRewardPage(playerRef, this.runtime));
            context.sendMessage(Message.raw("РћС‚РєСЂС‹С‚Рѕ РјРµРЅСЋ РїРѕР»СѓС‡РµРЅРёСЏ РјРѕРґСѓР»РµР№."));
        }
    }

    private static final class MatchResetCommand extends AbstractWorldCommand {
        private final BankDefenseRuntime runtime;

        private MatchResetCommand(BankDefenseRuntime runtime) {
            super("reset", "РЎР±СЂРѕСЃРёС‚СЊ РјР°С‚С‡");
            this.runtime = runtime;
        }

        @Override
        protected void execute(CommandContext context, World world, Store<EntityStore> store) {
            try {
                MatchState state = this.runtime.resetMatch(world);
                context.sendMessage(Message.raw("РњР°С‚С‡ СЃР±СЂРѕС€РµРЅ. Р’РѕР»РЅР°: " + state.currentWave + ", СЃСЂРµРґСЃС‚РІР°: " + state.currency + "."));
            } catch (IOException e) {
                context.sendMessage(Message.raw("РќРµ СѓРґР°Р»РѕСЃСЊ СЃР±СЂРѕСЃРёС‚СЊ РјР°С‚С‡: " + e.getMessage()));
            }
        }
    }

    private static final class MatchStartCommand extends AbstractWorldCommand {
        private final BankDefenseRuntime runtime;

        private MatchStartCommand(BankDefenseRuntime runtime) {
            super("start", "РќР°С‡Р°С‚СЊ РёРіСЂСѓ РёР»Рё СЃР»РµРґСѓСЋС‰СѓСЋ РІРѕР»РЅСѓ");
            this.runtime = runtime;
        }

        @Override
        protected void execute(CommandContext context, World world, Store<EntityStore> store) {
            try {
                context.sendMessage(Message.raw(this.runtime.startNextWave(world).message));
            } catch (IOException e) {
                context.sendMessage(Message.raw("РќРµ СѓРґР°Р»РѕСЃСЊ Р·Р°РїСѓСЃС‚РёС‚СЊ РІРѕР»РЅСѓ: " + e.getMessage()));
            }
        }
    }

    private static final class MatchModeCommand extends AbstractPlayerCommand {
        private final BankDefenseRuntime runtime;
        private final String modeId;

        private MatchModeCommand(BankDefenseRuntime runtime, String modeId) {
            super(modeId, "Переключить матч в режим " + modeId);
            this.runtime = runtime;
            this.modeId = modeId;
        }

        @Override
        protected void execute(CommandContext context, Store<EntityStore> store, Ref<EntityStore> ref, PlayerRef playerRef, World world) {
            try {
                context.sendMessage(Message.raw(this.runtime.selectGameMode(world, ref, this.modeId).message));
            } catch (IOException e) {
                context.sendMessage(Message.raw("Не удалось переключить режим: " + e.getMessage()));
            }
        }
    }

    private static final class RangePreviewAliasCommand extends AbstractWorldCommand {
        private final BankDefenseRuntime runtime;

        private RangePreviewAliasCommand(BankDefenseRuntime runtime) {
            super("ranges", "РџРµСЂРµРєР»СЋС‡РёС‚СЊ РїРѕРєР°Р· СЂР°РґРёСѓСЃРѕРІ Р±Р°С€РµРЅ");
            this.runtime = runtime;
        }

        @Override
        protected void execute(CommandContext context, World world, Store<EntityStore> store) {
            try {
                context.sendMessage(Message.raw(this.runtime.toggleTowerRangePreview(world).message));
            } catch (IOException e) {
                context.sendMessage(Message.raw("РќРµ СѓРґР°Р»РѕСЃСЊ РїРµСЂРµРєР»СЋС‡РёС‚СЊ СЂР°РґРёСѓСЃС‹ Р±Р°С€РµРЅ: " + e.getMessage()));
            }
        }
    }

    private static final class MoneyCommand extends AbstractCommandCollection {
        private MoneyCommand(BankDefenseRuntime runtime) {
            super("money", "РЈРїСЂР°РІР»РµРЅРёРµ СЃСЂРµРґСЃС‚РІР°РјРё");
            this.addSubCommand(new MoneyAddCommand(runtime));
            this.addSubCommand(new MoneySetCommand(runtime));
        }
    }

    private static final class MoneyAddCommand extends AbstractWorldCommand {
        private final BankDefenseRuntime runtime;
        private final RequiredArg<Integer> amountArg = this.withRequiredArg("amount", "РЎРєРѕР»СЊРєРѕ РґРѕР±Р°РІРёС‚СЊ", ArgTypes.INTEGER);

        private MoneyAddCommand(BankDefenseRuntime runtime) {
            super("add", "Р”РѕР±Р°РІРёС‚СЊ СЃСЂРµРґСЃС‚РІР°");
            this.runtime = runtime;
        }

        @Override
        protected void execute(CommandContext context, World world, Store<EntityStore> store) {
            try {
                context.sendMessage(Message.raw(this.runtime.addCurrency(world, this.amountArg.get(context)).message));
            } catch (IOException e) {
                context.sendMessage(Message.raw("РќРµ СѓРґР°Р»РѕСЃСЊ РёР·РјРµРЅРёС‚СЊ Р±Р°Р»Р°РЅСЃ: " + e.getMessage()));
            }
        }
    }

    private static final class MoneySetCommand extends AbstractWorldCommand {
        private final BankDefenseRuntime runtime;
        private final RequiredArg<Integer> amountArg = this.withRequiredArg("amount", "РќРѕРІС‹Р№ Р±Р°Р»Р°РЅСЃ", ArgTypes.INTEGER);

        private MoneySetCommand(BankDefenseRuntime runtime) {
            super("set", "РЈСЃС‚Р°РЅРѕРІРёС‚СЊ Р±Р°Р»Р°РЅСЃ");
            this.runtime = runtime;
        }

        @Override
        protected void execute(CommandContext context, World world, Store<EntityStore> store) {
            try {
                context.sendMessage(Message.raw(this.runtime.setCurrency(world, this.amountArg.get(context)).message));
            } catch (IOException e) {
                context.sendMessage(Message.raw("РќРµ СѓРґР°Р»РѕСЃСЊ СѓСЃС‚Р°РЅРѕРІРёС‚СЊ Р±Р°Р»Р°РЅСЃ: " + e.getMessage()));
            }
        }
    }

    private static final class TowersCommand extends AbstractWorldCommand {
        private final BankDefenseRuntime runtime;

        private TowersCommand(BankDefenseRuntime runtime) {
            super("towers", "РџРѕРєР°Р·Р°С‚СЊ РІСЃРµ id Р±Р°С€РµРЅ");
            this.runtime = runtime;
        }

        @Override
        protected void execute(CommandContext context, World world, Store<EntityStore> store) {
            try {
                context.sendMessage(Message.raw("Р‘Р°С€РЅРё: " + String.join(", ", this.runtime.getTowerIds(world))));
            } catch (IOException e) {
                context.sendMessage(Message.raw("РќРµ СѓРґР°Р»РѕСЃСЊ РїРѕР»СѓС‡РёС‚СЊ СЃРїРёСЃРѕРє Р±Р°С€РµРЅ: " + e.getMessage()));
            }
        }
    }

    private static final class BuildCommand extends AbstractPlayerCommand {
        private final BankDefenseRuntime runtime;
        private final RequiredArg<String> towerArg = this.withRequiredArg("tower", "Id Р±Р°С€РЅРё", ArgTypes.STRING);

        private BuildCommand(BankDefenseRuntime runtime) {
            super("build", "РџРѕСЃС‚СЂРѕРёС‚СЊ Р±Р°С€РЅСЋ РІ Р±Р»РёР¶Р°Р№С€РµРј СЃР»РѕС‚Рµ");
            this.runtime = runtime;
        }

        @Override
        protected void execute(CommandContext context, Store<EntityStore> store, Ref<EntityStore> ref, PlayerRef playerRef, World world) {
            try {
                context.sendMessage(Message.raw(this.runtime.buildTower(world, currentBlockPosition(store, ref), this.towerArg.get(context), 3).message));
            } catch (IOException e) {
                context.sendMessage(Message.raw("РќРµ СѓРґР°Р»РѕСЃСЊ РїРѕСЃС‚СЂРѕРёС‚СЊ Р±Р°С€РЅСЋ: " + e.getMessage()));
            }
        }
    }

    private static final class UpgradeCommand extends AbstractPlayerCommand {
        private final BankDefenseRuntime runtime;

        private UpgradeCommand(BankDefenseRuntime runtime) {
            super("upgrade", "РЈР»СѓС‡С€РёС‚СЊ Р±Р»РёР¶Р°Р№С€СѓСЋ Р±Р°С€РЅСЋ");
            this.runtime = runtime;
        }

        @Override
        protected void execute(CommandContext context, Store<EntityStore> store, Ref<EntityStore> ref, PlayerRef playerRef, World world) {
            try {
                context.sendMessage(Message.raw(this.runtime.upgradeTower(world, currentBlockPosition(store, ref), 3).message));
            } catch (IOException e) {
                context.sendMessage(Message.raw("РќРµ СѓРґР°Р»РѕСЃСЊ СѓР»СѓС‡С€РёС‚СЊ Р±Р°С€РЅСЋ: " + e.getMessage()));
            }
        }
    }

    private static final class SellCommand extends AbstractPlayerCommand {
        private final BankDefenseRuntime runtime;

        private SellCommand(BankDefenseRuntime runtime) {
            super("sell", "РџСЂРѕРґР°С‚СЊ Р±Р»РёР¶Р°Р№С€СѓСЋ Р±Р°С€РЅСЋ");
            this.runtime = runtime;
        }

        @Override
        protected void execute(CommandContext context, Store<EntityStore> store, Ref<EntityStore> ref, PlayerRef playerRef, World world) {
            try {
                context.sendMessage(Message.raw(this.runtime.sellTower(world, currentBlockPosition(store, ref), 3).message));
            } catch (IOException e) {
                context.sendMessage(Message.raw("РќРµ СѓРґР°Р»РѕСЃСЊ РїСЂРѕРґР°С‚СЊ Р±Р°С€РЅСЋ: " + e.getMessage()));
            }
        }
    }

    private static final class VisualCommand extends AbstractCommandCollection {
        private VisualCommand(BankDefenseRuntime runtime) {
            super("visual", "РЈРїСЂР°РІР»РµРЅРёРµ РІРёР·СѓР°Р»РёР·Р°С†РёРµР№");
            this.addSubCommand(new VisualRefreshCommand(runtime));
            this.addSubCommand(new VisualClearCommand(runtime));
            this.addSubCommand(new VisualStatusCommand(runtime));
        }
    }

    private static final class VisualRefreshCommand extends AbstractWorldCommand {
        private final BankDefenseRuntime runtime;

        private VisualRefreshCommand(BankDefenseRuntime runtime) {
            super("refresh", "РћР±РЅРѕРІРёС‚СЊ Рё РІРєР»СЋС‡РёС‚СЊ РІРёР·СѓР°Р»РёР·Р°С†РёСЋ");
            this.runtime = runtime;
        }

        @Override
        protected void execute(CommandContext context, World world, Store<EntityStore> store) {
            try {
                context.sendMessage(Message.raw(this.runtime.refreshVisualization(world).message));
            } catch (IOException e) {
                context.sendMessage(Message.raw("РќРµ СѓРґР°Р»РѕСЃСЊ РѕР±РЅРѕРІРёС‚СЊ РІРёР·СѓР°Р»РёР·Р°С†РёСЋ: " + e.getMessage()));
            }
        }
    }

    private static final class VisualClearCommand extends AbstractWorldCommand {
        private final BankDefenseRuntime runtime;

        private VisualClearCommand(BankDefenseRuntime runtime) {
            super("clear", "РћС‡РёСЃС‚РёС‚СЊ Рё РІС‹РєР»СЋС‡РёС‚СЊ РІРёР·СѓР°Р»РёР·Р°С†РёСЋ");
            this.runtime = runtime;
        }

        @Override
        protected void execute(CommandContext context, World world, Store<EntityStore> store) {
            context.sendMessage(Message.raw(this.runtime.clearVisualization(world).message));
        }
    }

    private static final class VisualStatusCommand extends AbstractWorldCommand {
        private final BankDefenseRuntime runtime;

        private VisualStatusCommand(BankDefenseRuntime runtime) {
            super("status", "РџРѕРєР°Р·Р°С‚СЊ СЃРѕСЃС‚РѕСЏРЅРёРµ РІРёР·СѓР°Р»РёР·Р°С†РёРё");
            this.runtime = runtime;
        }

        @Override
        protected void execute(CommandContext context, World world, Store<EntityStore> store) {
            try {
                RuntimeStatus status = this.runtime.getRuntimeStatus(world);
                context.sendMessage(Message.raw(
                    "visual enabled=" + status.visualizationEnabled
                        + ", entities=" + status.renderedOverlayBlocks
                        + ", enemies=" + status.activeEnemies
                        + ", towers=" + status.placedTowers
                        + ", pinnedSpawnChunks=" + status.pinnedSpawnChunks
                        + ", viewers=" + status.enemyVisualViewers
                        + ", wantedEnemyVisuals=" + status.wantedEnemyVisuals
                        + ", sweepRemoved=" + status.enemyVisualSweepRemoved
                        + ", epoch=" + status.enemyVisualEpoch
                ));
            } catch (IOException e) {
                context.sendMessage(Message.raw("РќРµ СѓРґР°Р»РѕСЃСЊ РїСЂРѕС‡РёС‚Р°С‚СЊ СЃРѕСЃС‚РѕСЏРЅРёРµ РІРёР·СѓР°Р»РёР·Р°С†РёРё: " + e.getMessage()));
            }
        }
    }

    private static final class RefreshAliasCommand extends AbstractWorldCommand {
        private final BankDefenseRuntime runtime;

        private RefreshAliasCommand(BankDefenseRuntime runtime) {
            super("refresh", "Р‘С‹СЃС‚СЂРѕ РѕР±РЅРѕРІРёС‚СЊ РІРёР·СѓР°Р»РёР·Р°С†РёСЋ");
            this.runtime = runtime;
        }

        @Override
        protected void execute(CommandContext context, World world, Store<EntityStore> store) {
            try {
                context.sendMessage(Message.raw(this.runtime.refreshVisualization(world).message));
            } catch (IOException e) {
                context.sendMessage(Message.raw("РќРµ СѓРґР°Р»РѕСЃСЊ РѕР±РЅРѕРІРёС‚СЊ РІРёР·СѓР°Р»РёР·Р°С†РёСЋ: " + e.getMessage()));
            }
        }
    }

    private static final class VisualClearAliasCommand extends AbstractWorldCommand {
        private final BankDefenseRuntime runtime;

        private VisualClearAliasCommand(BankDefenseRuntime runtime) {
            super("visualclear", "РћС‡РёСЃС‚РёС‚СЊ РІРёР·СѓР°Р»РёР·Р°С†РёСЋ");
            this.runtime = runtime;
        }

        @Override
        protected void execute(CommandContext context, World world, Store<EntityStore> store) {
            this.runtime.clearVisualization(world);
            context.sendMessage(Message.raw("Р’РёР·СѓР°Р»РёР·Р°С†РёСЏ РѕС‡РёС‰РµРЅР°."));
        }
    }

    private static final class VisualStatusAliasCommand extends AbstractWorldCommand {
        private final BankDefenseRuntime runtime;

        private VisualStatusAliasCommand(BankDefenseRuntime runtime) {
            super("visualstatus", "РџРѕРєР°Р·Р°С‚СЊ СЃС‚Р°С‚СѓСЃ РІРёР·СѓР°Р»РёР·Р°С†РёРё");
            this.runtime = runtime;
        }

        @Override
        protected void execute(CommandContext context, World world, Store<EntityStore> store) {
            try {
                RuntimeStatus status = this.runtime.getRuntimeStatus(world);
                context.sendMessage(Message.raw(
                    "Р’РёР·СѓР°Р»РёР·Р°С†РёСЏ: "
                        + (status.visualizationEnabled ? "РІРєР»СЋС‡РµРЅР°" : "РІС‹РєР»СЋС‡РµРЅР°")
                        + ", СЃСѓС‰РЅРѕСЃС‚Рё=" + status.renderedOverlayBlocks
                        + ", РІСЂР°РіРё=" + status.activeEnemies
                        + ", Р±Р°С€РЅРё=" + status.placedTowers
                ));
            } catch (IOException e) {
                context.sendMessage(Message.raw("РќРµ СѓРґР°Р»РѕСЃСЊ РїСЂРѕС‡РёС‚Р°С‚СЊ СЃРѕСЃС‚РѕСЏРЅРёРµ РІРёР·СѓР°Р»РёР·Р°С†РёРё: " + e.getMessage()));
            }
        }
    }

    private static final class MarkerCommand extends AbstractCommandCollection {
        private MarkerCommand(BankDefenseRepository repository) {
            super("marker", "РЈСЃС‚Р°РЅРѕРІРєР° Рё РѕС‡РёСЃС‚РєР° РјР°СЂРєРµСЂРѕРІ РјРёСЂР°");
            this.addSubCommand(new MarkerSetCommand(repository));
            this.addSubCommand(new MarkerClearCommand(repository));
        }
    }

    private static final class MarkerSetCommand extends AbstractPlayerCommand {
        private final BankDefenseRepository repository;
        private final RequiredArg<String> markerArg = this.withRequiredArg("marker", "Id РјР°СЂРєРµСЂР°", ArgTypes.STRING);

        private MarkerSetCommand(BankDefenseRepository repository) {
            super("set", "РџРѕСЃС‚Р°РІРёС‚СЊ РјР°СЂРєРµСЂ РІ С‚РµРєСѓС‰РµР№ РїРѕР·РёС†РёРё");
            this.repository = repository;
        }

        @Override
        protected void execute(CommandContext context, Store<EntityStore> store, Ref<EntityStore> ref, PlayerRef playerRef, World world) {
            String markerName = normalizeMarkerName(this.markerArg.get(context));
            if (markerName == null) {
                context.sendMessage(Message.raw("РќРµРёР·РІРµСЃС‚РЅС‹Р№ РјР°СЂРєРµСЂ. РСЃРїРѕР»СЊР·СѓР№ spawn_a, spawn_b, spawn_c, bank, vault, playerstart, control, vendor РёР»Рё mode."));
                return;
            }
            try {
                MapConfig mapConfig = this.repository.loadMapConfig(world);
                Vec3i position = currentBlockPosition(store, ref);
                float yaw = currentYaw(store, ref);
                switch (markerName) {
                    case "spawn_a" -> {
                        mapConfig.spawnPoint = position;
                        mapConfig.spawnPointA = position;
                    }
                    case "spawn_b" -> mapConfig.spawnPointB = position;
                    case "spawn_c" -> mapConfig.spawnPointC = position;
                    case "bank" -> mapConfig.bankCenter = position;
                    case "vault" -> mapConfig.vaultPoint = position;
                    case "playerstart" -> {
                        mapConfig.playerStart = position;
                        mapConfig.playerStartYaw = yaw;
                    }
                    case "control" -> {
                        mapConfig.previousControlPoint = mapConfig.controlPoint;
                        mapConfig.controlPoint = position;
                        mapConfig.controlYaw = yaw;
                    }
                    case "vendor" -> {
                        mapConfig.previousVendorPoint = mapConfig.vendorPoint;
                        mapConfig.vendorPoint = position;
                        mapConfig.vendorYaw = yaw;
                    }
                    case "mode" -> {
                        mapConfig.previousModePoint = mapConfig.modePoint;
                        mapConfig.modePoint = position;
                        mapConfig.modeYaw = yaw;
                    }
                    case "qubecoresolo" -> {
                        mapConfig.previousSoloQubeCorePoint = mapConfig.soloQubeCorePoint;
                        mapConfig.soloQubeCorePoint = position;
                        mapConfig.soloQubeCoreYaw = yaw;
                    }
                    case "qubecoreduo" -> {
                        mapConfig.previousDuoQubeCorePoint = mapConfig.duoQubeCorePoint;
                        mapConfig.duoQubeCorePoint = position;
                        mapConfig.duoQubeCoreYaw = yaw;
                    }
                    case "statistics" -> {
                        mapConfig.previousStatisticsPoint = mapConfig.statisticsPoint;
                        mapConfig.statisticsPoint = position;
                        mapConfig.statisticsYaw = yaw;
                    }
                    case "duoteam" -> {
                        mapConfig.previousDuoTeamPoint = mapConfig.duoTeamPoint;
                        mapConfig.duoTeamPoint = position;
                        mapConfig.duoTeamYaw = yaw;
                    }
                    case "duoteleportnpc" -> {
                        mapConfig.previousDuoTeleportPoint = mapConfig.duoTeleportPoint;
                        mapConfig.duoTeleportPoint = position;
                        mapConfig.duoTeleportYaw = yaw;
                    }
                    case "teleportduo" -> mapConfig.duoTeleportTarget = position;
                    case "teleportsolo" -> mapConfig.soloTeleportTarget = position;
                    case "tutorialstart" -> {
                        mapConfig.tutorialStartPoint = position;
                        mapConfig.tutorialStartYaw = yaw;
                    }
                    case "tutorialspawna" -> mapConfig.tutorialSpawnPointA = position;
                    case "tutorialspawnb" -> mapConfig.tutorialSpawnPointB = position;
                    case "tutorialspawnc" -> mapConfig.tutorialSpawnPointC = position;
                    case "tutorialbank" -> mapConfig.tutorialBankCenter = position;
                    case "tutorialvault" -> mapConfig.tutorialVaultPoint = position;
                    case "tutorialcontrol" -> {
                        mapConfig.tutorialControlPoint = position;
                        mapConfig.tutorialControlYaw = yaw;
                    }
                    case "tutorialvendor" -> {
                        mapConfig.tutorialVendorPoint = position;
                        mapConfig.tutorialVendorYaw = yaw;
                    }
                    case "wizardintro" -> {
                        mapConfig.tutorialWizardIntroPoint = position;
                        mapConfig.tutorialWizardIntroYaw = yaw;
                    }
                    case "tutorialwizard" -> {
                        mapConfig.tutorialWizardPoint = position;
                        mapConfig.tutorialWizardYaw = yaw;
                    }
                    default -> {
                        context.sendMessage(Message.raw("РњР°СЂРєРµСЂ РЅРµ РїРѕРґРґРµСЂР¶РёРІР°РµС‚СЃСЏ: " + markerName));
                        return;
                    }
                }
                this.repository.saveMapConfig(world, mapConfig);
                context.sendMessage(Message.raw("РњР°СЂРєРµСЂ '" + markerName + "' СѓСЃС‚Р°РЅРѕРІР»РµРЅ РІ " + position.toShortString() + "."));
            } catch (IOException e) {
                context.sendMessage(Message.raw("РќРµ СѓРґР°Р»РѕСЃСЊ СЃРѕС…СЂР°РЅРёС‚СЊ РјР°СЂРєРµСЂ: " + e.getMessage()));
            }
        }
    }

    private static final class MarkerClearCommand extends AbstractWorldCommand {
        private final BankDefenseRepository repository;
        private final RequiredArg<String> markerArg = this.withRequiredArg("marker", "Id РјР°СЂРєРµСЂР°", ArgTypes.STRING);

        private MarkerClearCommand(BankDefenseRepository repository) {
            super("clear", "РћС‡РёСЃС‚РёС‚СЊ РјР°СЂРєРµСЂ");
            this.repository = repository;
        }

        @Override
        protected void execute(CommandContext context, World world, Store<EntityStore> store) {
            String markerName = normalizeMarkerName(this.markerArg.get(context));
            if (markerName == null) {
                context.sendMessage(Message.raw("РќРµРёР·РІРµСЃС‚РЅС‹Р№ РјР°СЂРєРµСЂ. РСЃРїРѕР»СЊР·СѓР№ spawn_a, spawn_b, spawn_c, bank, vault, playerstart, control, vendor РёР»Рё mode."));
                return;
            }
            try {
                MapConfig mapConfig = this.repository.loadMapConfig(world);
                switch (markerName) {
                    case "spawn_a" -> {
                        mapConfig.spawnPoint = null;
                        mapConfig.spawnPointA = null;
                    }
                    case "spawn_b" -> mapConfig.spawnPointB = null;
                    case "spawn_c" -> mapConfig.spawnPointC = null;
                    case "bank" -> mapConfig.bankCenter = null;
                    case "vault" -> mapConfig.vaultPoint = null;
                    case "playerstart" -> {
                        mapConfig.playerStart = null;
                        mapConfig.playerStartYaw = null;
                    }
                    case "control" -> {
                        mapConfig.previousControlPoint = mapConfig.controlPoint;
                        mapConfig.controlPoint = null;
                        mapConfig.controlYaw = null;
                    }
                    case "vendor" -> {
                        mapConfig.previousVendorPoint = mapConfig.vendorPoint;
                        mapConfig.vendorPoint = null;
                        mapConfig.vendorYaw = null;
                    }
                    case "mode" -> {
                        mapConfig.previousModePoint = mapConfig.modePoint;
                        mapConfig.modePoint = null;
                        mapConfig.modeYaw = null;
                    }
                    case "qubecoresolo" -> {
                        mapConfig.previousSoloQubeCorePoint = mapConfig.soloQubeCorePoint;
                        mapConfig.soloQubeCorePoint = null;
                        mapConfig.soloQubeCoreYaw = null;
                    }
                    case "qubecoreduo" -> {
                        mapConfig.previousDuoQubeCorePoint = mapConfig.duoQubeCorePoint;
                        mapConfig.duoQubeCorePoint = null;
                        mapConfig.duoQubeCoreYaw = null;
                    }
                    case "statistics" -> {
                        mapConfig.previousStatisticsPoint = mapConfig.statisticsPoint;
                        mapConfig.statisticsPoint = null;
                        mapConfig.statisticsYaw = null;
                    }
                    case "duoteam" -> {
                        mapConfig.previousDuoTeamPoint = mapConfig.duoTeamPoint;
                        mapConfig.duoTeamPoint = null;
                        mapConfig.duoTeamYaw = null;
                    }
                    case "duoteleportnpc" -> {
                        mapConfig.previousDuoTeleportPoint = mapConfig.duoTeleportPoint;
                        mapConfig.duoTeleportPoint = null;
                        mapConfig.duoTeleportYaw = null;
                    }
                    case "teleportduo" -> mapConfig.duoTeleportTarget = null;
                    case "teleportsolo" -> mapConfig.soloTeleportTarget = null;
                    case "tutorialstart" -> {
                        mapConfig.tutorialStartPoint = null;
                        mapConfig.tutorialStartYaw = null;
                    }
                    case "tutorialspawna" -> mapConfig.tutorialSpawnPointA = null;
                    case "tutorialspawnb" -> mapConfig.tutorialSpawnPointB = null;
                    case "tutorialspawnc" -> mapConfig.tutorialSpawnPointC = null;
                    case "tutorialbank" -> mapConfig.tutorialBankCenter = null;
                    case "tutorialvault" -> mapConfig.tutorialVaultPoint = null;
                    case "tutorialcontrol" -> {
                        mapConfig.tutorialControlPoint = null;
                        mapConfig.tutorialControlYaw = null;
                    }
                    case "tutorialvendor" -> {
                        mapConfig.tutorialVendorPoint = null;
                        mapConfig.tutorialVendorYaw = null;
                    }
                    case "wizardintro" -> {
                        mapConfig.tutorialWizardIntroPoint = null;
                        mapConfig.tutorialWizardIntroYaw = null;
                    }
                    case "tutorialwizard" -> {
                        mapConfig.tutorialWizardPoint = null;
                        mapConfig.tutorialWizardYaw = null;
                    }
                    default -> {
                        context.sendMessage(Message.raw("РњР°СЂРєРµСЂ РЅРµ РїРѕРґРґРµСЂР¶РёРІР°РµС‚СЃСЏ: " + markerName));
                        return;
                    }
                }
                this.repository.saveMapConfig(world, mapConfig);
                context.sendMessage(Message.raw("РњР°СЂРєРµСЂ '" + markerName + "' РѕС‡РёС‰РµРЅ."));
            } catch (IOException e) {
                context.sendMessage(Message.raw("РќРµ СѓРґР°Р»РѕСЃСЊ РѕС‡РёСЃС‚РёС‚СЊ РјР°СЂРєРµСЂ: " + e.getMessage()));
            }
        }
    }

    private static final class FixedMarkerSetCommand extends AbstractPlayerCommand {
        private final BankDefenseRepository repository;
        private final String markerName;

        private FixedMarkerSetCommand(BankDefenseRepository repository, String markerName, String commandName, String description) {
            super(commandName, description);
            this.repository = repository;
            this.markerName = markerName;
        }

        @Override
        protected void execute(CommandContext context, Store<EntityStore> store, Ref<EntityStore> ref, PlayerRef playerRef, World world) {
            try {
                MapConfig mapConfig = this.repository.loadMapConfig(world);
                Vec3i position = currentBlockPosition(store, ref);
                float yaw = currentYaw(store, ref);
                switch (this.markerName) {
                    case "spawn_a" -> {
                        mapConfig.spawnPoint = position;
                        mapConfig.spawnPointA = position;
                    }
                    case "spawn_b" -> mapConfig.spawnPointB = position;
                    case "spawn_c" -> mapConfig.spawnPointC = position;
                    case "bank" -> mapConfig.bankCenter = position;
                    case "vault" -> mapConfig.vaultPoint = position;
                    case "playerstart" -> {
                        mapConfig.playerStart = position;
                        mapConfig.playerStartYaw = yaw;
                    }
                    case "control" -> {
                        mapConfig.previousControlPoint = mapConfig.controlPoint;
                        mapConfig.controlPoint = position;
                        mapConfig.controlYaw = yaw;
                    }
                    case "vendor" -> {
                        mapConfig.previousVendorPoint = mapConfig.vendorPoint;
                        mapConfig.vendorPoint = position;
                        mapConfig.vendorYaw = yaw;
                    }
                    case "mode" -> {
                        mapConfig.previousModePoint = mapConfig.modePoint;
                        mapConfig.modePoint = position;
                        mapConfig.modeYaw = yaw;
                    }
                    case "qubecoresolo" -> {
                        mapConfig.previousSoloQubeCorePoint = mapConfig.soloQubeCorePoint;
                        mapConfig.soloQubeCorePoint = position;
                        mapConfig.soloQubeCoreYaw = yaw;
                    }
                    case "qubecoreduo" -> {
                        mapConfig.previousDuoQubeCorePoint = mapConfig.duoQubeCorePoint;
                        mapConfig.duoQubeCorePoint = position;
                        mapConfig.duoQubeCoreYaw = yaw;
                    }
                    case "statistics" -> {
                        mapConfig.previousStatisticsPoint = mapConfig.statisticsPoint;
                        mapConfig.statisticsPoint = position;
                        mapConfig.statisticsYaw = yaw;
                    }
                    case "duoplayerblue" -> {
                        mapConfig.duoPlayerStartBlue = position;
                        mapConfig.duoPlayerStartBlueYaw = yaw;
                    }
                    case "duoplayergreen" -> {
                        mapConfig.duoPlayerStartGreen = position;
                        mapConfig.duoPlayerStartGreenYaw = yaw;
                    }
                    case "duospawnblue" -> mapConfig.duoSpawnPointBlue = position;
                    case "duospawngreen" -> mapConfig.duoSpawnPointGreen = position;
                    case "duobank" -> mapConfig.duoBankCenter = position;
                    case "duovault" -> mapConfig.duoVaultPoint = position;
                    case "duosealblue" -> mapConfig.duoSealNodeBluePoint = position;
                    case "duosealgreen" -> mapConfig.duoSealNodeGreenPoint = position;
                    case "duostats" -> {
                        mapConfig.previousDuoStatisticsPoint = mapConfig.duoStatisticsPoint;
                        mapConfig.duoStatisticsPoint = position;
                        mapConfig.duoStatisticsYaw = yaw;
                    }
                    case "duoteam" -> {
                        mapConfig.previousDuoTeamPoint = mapConfig.duoTeamPoint;
                        mapConfig.duoTeamPoint = position;
                        mapConfig.duoTeamYaw = yaw;
                    }
                    case "duoteleportnpc" -> {
                        mapConfig.previousDuoTeleportPoint = mapConfig.duoTeleportPoint;
                        mapConfig.duoTeleportPoint = position;
                        mapConfig.duoTeleportYaw = yaw;
                    }
                    case "teleportduo" -> mapConfig.duoTeleportTarget = position;
                    case "teleportsolo" -> mapConfig.soloTeleportTarget = position;
                    case "tutorialstart" -> {
                        mapConfig.tutorialStartPoint = position;
                        mapConfig.tutorialStartYaw = yaw;
                    }
                    case "tutorialspawna" -> mapConfig.tutorialSpawnPointA = position;
                    case "tutorialspawnb" -> mapConfig.tutorialSpawnPointB = position;
                    case "tutorialspawnc" -> mapConfig.tutorialSpawnPointC = position;
                    case "tutorialbank" -> mapConfig.tutorialBankCenter = position;
                    case "tutorialvault" -> mapConfig.tutorialVaultPoint = position;
                    case "tutorialcontrol" -> {
                        mapConfig.tutorialControlPoint = position;
                        mapConfig.tutorialControlYaw = yaw;
                    }
                    case "tutorialvendor" -> {
                        mapConfig.tutorialVendorPoint = position;
                        mapConfig.tutorialVendorYaw = yaw;
                    }
                    case "wizardintro" -> {
                        mapConfig.tutorialWizardIntroPoint = position;
                        mapConfig.tutorialWizardIntroYaw = yaw;
                    }
                    case "tutorialwizard" -> {
                        mapConfig.tutorialWizardPoint = position;
                        mapConfig.tutorialWizardYaw = yaw;
                    }
                    default -> {
                        context.sendMessage(Message.raw("Неизвестный маркер: " + this.markerName));
                        return;
                    }
                }
                this.repository.saveMapConfig(world, mapConfig);
                refreshWorldVisualization(world);
                context.sendMessage(Message.raw("Маркер '" + this.markerName + "' установлен в " + position.toShortString() + "."));
            } catch (IOException e) {
                context.sendMessage(Message.raw("Не удалось поставить маркер: " + e.getMessage()));
            }
        }
    }

    private static final class FixedMarkerClearCommand extends AbstractWorldCommand {
        private final BankDefenseRepository repository;
        private final String markerName;

        private FixedMarkerClearCommand(BankDefenseRepository repository, String markerName, String commandName, String description) {
            super(commandName, description);
            this.repository = repository;
            this.markerName = markerName;
        }

        @Override
        protected void execute(CommandContext context, World world, Store<EntityStore> store) {
            try {
                MapConfig mapConfig = this.repository.loadMapConfig(world);
                switch (this.markerName) {
                    case "spawn_a" -> {
                        mapConfig.spawnPoint = null;
                        mapConfig.spawnPointA = null;
                    }
                    case "spawn_b" -> mapConfig.spawnPointB = null;
                    case "spawn_c" -> mapConfig.spawnPointC = null;
                    case "bank" -> mapConfig.bankCenter = null;
                    case "vault" -> mapConfig.vaultPoint = null;
                    case "playerstart" -> {
                        mapConfig.playerStart = null;
                        mapConfig.playerStartYaw = null;
                    }
                    case "control" -> {
                        mapConfig.previousControlPoint = mapConfig.controlPoint;
                        mapConfig.controlPoint = null;
                        mapConfig.controlYaw = null;
                    }
                    case "vendor" -> {
                        mapConfig.previousVendorPoint = mapConfig.vendorPoint;
                        mapConfig.vendorPoint = null;
                        mapConfig.vendorYaw = null;
                    }
                    case "mode" -> {
                        mapConfig.previousModePoint = mapConfig.modePoint;
                        mapConfig.modePoint = null;
                        mapConfig.modeYaw = null;
                    }
                    case "qubecoresolo" -> {
                        mapConfig.previousSoloQubeCorePoint = mapConfig.soloQubeCorePoint;
                        mapConfig.soloQubeCorePoint = null;
                        mapConfig.soloQubeCoreYaw = null;
                    }
                    case "qubecoreduo" -> {
                        mapConfig.previousDuoQubeCorePoint = mapConfig.duoQubeCorePoint;
                        mapConfig.duoQubeCorePoint = null;
                        mapConfig.duoQubeCoreYaw = null;
                    }
                    case "statistics" -> {
                        mapConfig.previousStatisticsPoint = mapConfig.statisticsPoint;
                        mapConfig.statisticsPoint = null;
                        mapConfig.statisticsYaw = null;
                    }
                    case "duoplayerblue" -> {
                        mapConfig.duoPlayerStartBlue = null;
                        mapConfig.duoPlayerStartBlueYaw = null;
                    }
                    case "duoplayergreen" -> {
                        mapConfig.duoPlayerStartGreen = null;
                        mapConfig.duoPlayerStartGreenYaw = null;
                    }
                    case "duospawnblue" -> mapConfig.duoSpawnPointBlue = null;
                    case "duospawngreen" -> mapConfig.duoSpawnPointGreen = null;
                    case "duobank" -> mapConfig.duoBankCenter = null;
                    case "duovault" -> mapConfig.duoVaultPoint = null;
                    case "duosealblue" -> mapConfig.duoSealNodeBluePoint = null;
                    case "duosealgreen" -> mapConfig.duoSealNodeGreenPoint = null;
                    case "duostats" -> {
                        mapConfig.previousDuoStatisticsPoint = mapConfig.duoStatisticsPoint;
                        mapConfig.duoStatisticsPoint = null;
                        mapConfig.duoStatisticsYaw = null;
                    }
                    case "duoteam" -> {
                        mapConfig.previousDuoTeamPoint = mapConfig.duoTeamPoint;
                        mapConfig.duoTeamPoint = null;
                        mapConfig.duoTeamYaw = null;
                    }
                    case "duoteleportnpc" -> {
                        mapConfig.previousDuoTeleportPoint = mapConfig.duoTeleportPoint;
                        mapConfig.duoTeleportPoint = null;
                        mapConfig.duoTeleportYaw = null;
                    }
                    case "teleportduo" -> mapConfig.duoTeleportTarget = null;
                    case "teleportsolo" -> mapConfig.soloTeleportTarget = null;
                    case "tutorialstart" -> {
                        mapConfig.tutorialStartPoint = null;
                        mapConfig.tutorialStartYaw = null;
                    }
                    case "tutorialspawna" -> mapConfig.tutorialSpawnPointA = null;
                    case "tutorialspawnb" -> mapConfig.tutorialSpawnPointB = null;
                    case "tutorialspawnc" -> mapConfig.tutorialSpawnPointC = null;
                    case "tutorialbank" -> mapConfig.tutorialBankCenter = null;
                    case "tutorialvault" -> mapConfig.tutorialVaultPoint = null;
                    case "tutorialcontrol" -> {
                        mapConfig.tutorialControlPoint = null;
                        mapConfig.tutorialControlYaw = null;
                    }
                    case "tutorialvendor" -> {
                        mapConfig.tutorialVendorPoint = null;
                        mapConfig.tutorialVendorYaw = null;
                    }
                    case "wizardintro" -> {
                        mapConfig.tutorialWizardIntroPoint = null;
                        mapConfig.tutorialWizardIntroYaw = null;
                    }
                    case "tutorialwizard" -> {
                        mapConfig.tutorialWizardPoint = null;
                        mapConfig.tutorialWizardYaw = null;
                    }
                    default -> {
                        context.sendMessage(Message.raw("Неизвестный маркер: " + this.markerName));
                        return;
                    }
                }
                this.repository.saveMapConfig(world, mapConfig);
                refreshWorldVisualization(world);
                context.sendMessage(Message.raw("Маркер '" + this.markerName + "' очищен."));
            } catch (IOException e) {
                context.sendMessage(Message.raw("Не удалось очистить маркер: " + e.getMessage()));
            }
        }
    }

    private static final class TutorialChestSetCommand extends AbstractPlayerCommand {
        private final BankDefenseRepository repository;

        private TutorialChestSetCommand(BankDefenseRepository repository) {
            super("marktutorialchest", "Поставить точку учебного сундука");
            this.repository = repository;
        }

        @Override
        protected void execute(CommandContext context, Store<EntityStore> store, Ref<EntityStore> ref, PlayerRef playerRef, World world) {
            try {
                MapConfig mapConfig = this.repository.loadMapConfig(world);
                Vec3i position = currentBlockPosition(store, ref);
                mapConfig.tutorialChestPoint = position;
                this.repository.saveMapConfig(world, mapConfig);
                context.sendMessage(Message.raw("Точка учебного сундука установлена в " + position.toShortString() + "."));
            } catch (IOException e) {
                context.sendMessage(Message.raw("Не удалось поставить точку учебного сундука: " + e.getMessage()));
            }
        }
    }

    private static final class TutorialChestClearCommand extends AbstractWorldCommand {
        private final BankDefenseRepository repository;

        private TutorialChestClearCommand(BankDefenseRepository repository) {
            super("cleartutorialchest", "Очистить точку учебного сундука");
            this.repository = repository;
        }

        @Override
        protected void execute(CommandContext context, World world, Store<EntityStore> store) {
            try {
                MapConfig mapConfig = this.repository.loadMapConfig(world);
                mapConfig.tutorialChestPoint = null;
                this.repository.saveMapConfig(world, mapConfig);
                context.sendMessage(Message.raw("Точка учебного сундука очищена."));
            } catch (IOException e) {
                context.sendMessage(Message.raw("Не удалось очистить точку учебного сундука: " + e.getMessage()));
            }
        }
    }

    private static final class RouteCommand extends AbstractCommandCollection {
        private RouteCommand(BankDefenseRepository repository) {
            super("route", "РќР°СЃС‚СЂРѕР№РєР° РјР°СЂС€СЂСѓС‚РѕРІ");
            this.addSubCommand(new RouteAddCommand(repository));
            this.addSubCommand(new RoutePopCommand(repository));
            this.addSubCommand(new RouteClearCommand(repository));
            this.addSubCommand(new RouteListCommand(repository));
        }
    }

    private static final class RouteAddCommand extends AbstractPlayerCommand {
        private final BankDefenseRepository repository;
        private final DefaultArg<String> laneArg = this.withDefaultArg("lane", "Р›РёРЅРёСЏ РјР°СЂС€СЂСѓС‚Р°", ArgTypes.STRING, "a", "a");

        private RouteAddCommand(BankDefenseRepository repository) {
            super("add", "Р”РѕР±Р°РІРёС‚СЊ С‚РѕС‡РєСѓ РјР°СЂС€СЂСѓС‚Р°");
            this.repository = repository;
        }

        @Override
        protected void execute(CommandContext context, Store<EntityStore> store, Ref<EntityStore> ref, PlayerRef playerRef, World world) {
            String laneId = normalizeLane(this.laneArg.get(context), "a");
            try {
                MapConfig mapConfig = this.repository.loadMapConfig(world);
                Vec3i position = currentBlockPosition(store, ref);
                List<Vec3i> route = routeRef(mapConfig, laneId);
                route.add(position);
                this.repository.saveMapConfig(world, mapConfig);
                context.sendMessage(Message.raw("Р”РѕР±Р°РІР»РµРЅР° С‚РѕС‡РєР° РјР°СЂС€СЂСѓС‚Р° " + laneId.toUpperCase(Locale.ROOT) + " #" + route.size() + " РІ " + position.toShortString() + "."));
            } catch (IOException e) {
                context.sendMessage(Message.raw("РќРµ СѓРґР°Р»РѕСЃСЊ РґРѕР±Р°РІРёС‚СЊ С‚РѕС‡РєСѓ РјР°СЂС€СЂСѓС‚Р°: " + e.getMessage()));
            }
        }
    }

    private static final class RoutePopCommand extends AbstractWorldCommand {
        private final BankDefenseRepository repository;
        private final DefaultArg<String> laneArg = this.withDefaultArg("lane", "Р›РёРЅРёСЏ РјР°СЂС€СЂСѓС‚Р°", ArgTypes.STRING, "a", "a");

        private RoutePopCommand(BankDefenseRepository repository) {
            super("pop", "РЈРґР°Р»РёС‚СЊ РїРѕСЃР»РµРґРЅСЋСЋ С‚РѕС‡РєСѓ РјР°СЂС€СЂСѓС‚Р°");
            this.repository = repository;
        }

        @Override
        protected void execute(CommandContext context, World world, Store<EntityStore> store) {
            String laneId = normalizeLane(this.laneArg.get(context), "a");
            try {
                MapConfig mapConfig = this.repository.loadMapConfig(world);
                List<Vec3i> route = routeRef(mapConfig, laneId);
                if (route.isEmpty()) {
                    context.sendMessage(Message.raw("РњР°СЂС€СЂСѓС‚ " + laneId.toUpperCase(Locale.ROOT) + " СѓР¶Рµ РїСѓСЃС‚."));
                    return;
                }
                Vec3i removed = route.remove(route.size() - 1);
                this.repository.saveMapConfig(world, mapConfig);
                context.sendMessage(Message.raw("РЈРґР°Р»РµРЅР° С‚РѕС‡РєР° РјР°СЂС€СЂСѓС‚Р° " + laneId.toUpperCase(Locale.ROOT) + " РІ " + removed.toShortString() + "."));
            } catch (IOException e) {
                context.sendMessage(Message.raw("РќРµ СѓРґР°Р»РѕСЃСЊ СѓРґР°Р»РёС‚СЊ С‚РѕС‡РєСѓ РјР°СЂС€СЂСѓС‚Р°: " + e.getMessage()));
            }
        }
    }

    private static final class RouteClearCommand extends AbstractWorldCommand {
        private final BankDefenseRepository repository;
        private final DefaultArg<String> laneArg = this.withDefaultArg("lane", "Р›РёРЅРёСЏ РјР°СЂС€СЂСѓС‚Р°", ArgTypes.STRING, "all", "all");

        private RouteClearCommand(BankDefenseRepository repository) {
            super("clear", "РћС‡РёСЃС‚РёС‚СЊ РјР°СЂС€СЂСѓС‚");
            this.repository = repository;
        }

        @Override
        protected void execute(CommandContext context, World world, Store<EntityStore> store) {
            String laneId = normalizeLane(this.laneArg.get(context), "all");
            try {
                MapConfig mapConfig = this.repository.loadMapConfig(world);
                if ("all".equals(laneId)) {
                    clearAllRoutes(mapConfig);
                    this.repository.saveMapConfig(world, mapConfig);
                    context.sendMessage(Message.raw("Р’СЃРµ РјР°СЂС€СЂСѓС‚С‹ РѕС‡РёС‰РµРЅС‹."));
                    return;
                }
                routeRef(mapConfig, laneId).clear();
                this.repository.saveMapConfig(world, mapConfig);
                context.sendMessage(Message.raw("РњР°СЂС€СЂСѓС‚ " + laneId.toUpperCase(Locale.ROOT) + " РѕС‡РёС‰РµРЅ."));
            } catch (IOException e) {
                context.sendMessage(Message.raw("РќРµ СѓРґР°Р»РѕСЃСЊ РѕС‡РёСЃС‚РёС‚СЊ РјР°СЂС€СЂСѓС‚: " + e.getMessage()));
            }
        }
    }

    private static final class RouteListCommand extends AbstractWorldCommand {
        private final BankDefenseRepository repository;
        private final DefaultArg<String> laneArg = this.withDefaultArg("lane", "Р›РёРЅРёСЏ РјР°СЂС€СЂСѓС‚Р°", ArgTypes.STRING, "all", "all");

        private RouteListCommand(BankDefenseRepository repository) {
            super("list", "РџРѕРєР°Р·Р°С‚СЊ С‚РѕС‡РєРё РјР°СЂС€СЂСѓС‚Р°");
            this.repository = repository;
        }

        @Override
        protected void execute(CommandContext context, World world, Store<EntityStore> store) {
            String laneId = normalizeLane(this.laneArg.get(context), "all");
            try {
                MapConfig mapConfig = this.repository.loadMapConfig(world);
                if ("all".equals(laneId)) {
                    StringBuilder builder = new StringBuilder("РњР°СЂС€СЂСѓС‚С‹:");
                    for (String currentLane : allLaneIds()) {
                        List<Vec3i> route = routeRef(mapConfig, currentLane);
                        builder.append('\n').append(currentLane.toUpperCase(Locale.ROOT)).append(": ").append(route.size()).append(" С‚РѕС‡РµРє");
                        for (int i = 0; i < route.size(); i++) {
                            builder.append('\n').append("  ").append(i + 1).append(". ").append(route.get(i).toShortString());
                        }
                    }
                    context.sendMessage(Message.raw(builder.toString()));
                    return;
                }
                List<Vec3i> route = routeRef(mapConfig, laneId);
                if (route.isEmpty()) {
                    context.sendMessage(Message.raw("РњР°СЂС€СЂСѓС‚ " + laneId.toUpperCase(Locale.ROOT) + " РїСѓСЃС‚."));
                    return;
                }
                StringBuilder builder = new StringBuilder("РњР°СЂС€СЂСѓС‚ ").append(laneId.toUpperCase(Locale.ROOT)).append(": ").append(route.size()).append(" С‚РѕС‡РµРє");
                for (int i = 0; i < route.size(); i++) {
                    builder.append('\n').append(i + 1).append(". ").append(route.get(i).toShortString());
                }
                context.sendMessage(Message.raw(builder.toString()));
            } catch (IOException e) {
                context.sendMessage(Message.raw("РќРµ СѓРґР°Р»РѕСЃСЊ РїРѕРєР°Р·Р°С‚СЊ РјР°СЂС€СЂСѓС‚: " + e.getMessage()));
            }
        }
    }

    private static final class SlotCommand extends AbstractCommandCollection {
        private SlotCommand(BankDefenseRepository repository) {
            super("slot", "РќР°СЃС‚СЂРѕР№РєР° СЃР»РѕС‚РѕРІ");
            this.addSubCommand(new SlotAddCommand(repository));
            this.addSubCommand(new SlotRemoveCommand(repository));
            this.addSubCommand(new SlotClearCommand(repository));
            this.addSubCommand(new SlotListCommand(repository));
        }
    }

    private static final class SlotAddCommand extends AbstractPlayerCommand {
        private final BankDefenseRepository repository;
        private final DefaultArg<String> typeArg = this.withDefaultArg("type", "РўРёРї СЃР»РѕС‚Р°", ArgTypes.STRING, "standard", "standard");

        private SlotAddCommand(BankDefenseRepository repository) {
            super("add", "Р”РѕР±Р°РІРёС‚СЊ СЃР»РѕС‚ СЃС‚СЂРѕРёС‚РµР»СЊСЃС‚РІР°");
            this.repository = repository;
        }

        @Override
        protected void execute(CommandContext context, Store<EntityStore> store, Ref<EntityStore> ref, PlayerRef playerRef, World world) {
            String slotType = normalizeSlotType(this.typeArg.get(context), "standard");
            try {
                BuildSlotsConfig slotsConfig = this.repository.loadBuildSlotsConfig(world);
                Vec3i position = currentBlockPosition(store, ref);
                for (BuildSlot existing : slotsConfig.slots) {
                    if (existing.position != null && existing.position.distanceSquaredTo(position) == 0.0) {
                        context.sendMessage(Message.raw("РЎР»РѕС‚ СѓР¶Рµ СЃСѓС‰РµСЃС‚РІСѓРµС‚ РІ " + position.toShortString() + "."));
                        return;
                    }
                }
                int nextIndex = 1;
                for (BuildSlot slot : slotsConfig.slots) {
                    if (slotType.equals(normalizeSlotType(slot.slotType, "standard"))) {
                        nextIndex++;
                    }
                }
                BuildSlot slot = new BuildSlot();
                slot.id = String.format(Locale.ROOT, "%s_%02d", "super".equals(slotType) ? "super" : "trap".equals(slotType) ? "trap" : "slot", nextIndex);
                slot.label = "super".equals(slotType) ? "РЎСѓРїРµСЂ-СЃР»РѕС‚ " + nextIndex : "trap".equals(slotType) ? "Р›РѕРІСѓС€РєР° " + nextIndex : "РЎР»РѕС‚ СЃС‚СЂРѕРёС‚РµР»СЊСЃС‚РІР° " + nextIndex;
                slot.position = position;
                slot.slotType = slotType;
                slot.modulesAllowed = !"super".equals(slotType) && !"trap".equals(slotType);
                if ("tutorial".equals(slotType)) {
                    slot.id = String.format(Locale.ROOT, "tutorial_%02d", nextIndex);
                    slot.label = "РЎР»РѕС‚ РѕР±СѓС‡РµРЅРёСЏ " + nextIndex;
                } else if ("tutorial_super".equals(slotType)) {
                    slot.id = String.format(Locale.ROOT, "tutorial_super_%02d", nextIndex);
                    slot.label = "РЎСѓРїРµСЂ-СЃР»РѕС‚ РѕР±СѓС‡РµРЅРёСЏ " + nextIndex;
                    slot.modulesAllowed = false;
                } else if ("tutorial_trap".equals(slotType)) {
                    slot.id = String.format(Locale.ROOT, "tutorial_trap_%02d", nextIndex);
                    slot.label = "Р›РѕРІСѓС€РєР° РѕР±СѓС‡РµРЅРёСЏ " + nextIndex;
                    slot.modulesAllowed = false;
                }
                if ("super".equals(slotType)) {
                    slot.allowedTowerIds = new ArrayList<>(superTowerIds());
                } else if ("trap".equals(slotType)) {
                    slot.allowedTowerIds = new ArrayList<>(trapTowerIds());
                } else if ("tutorial_super".equals(slotType)) {
                    slot.allowedTowerIds = new ArrayList<>(superTowerIds());
                } else if ("tutorial_trap".equals(slotType)) {
                    slot.allowedTowerIds = new ArrayList<>(trapTowerIds());
                }
                slotsConfig.slots.add(slot);
                this.repository.saveBuildSlotsConfig(world, slotsConfig);
                placeImmediateSlotBlock(world, slot);
                context.sendMessage(Message.raw("Р”РѕР±Р°РІР»РµРЅ " + slotType + "-СЃР»РѕС‚ '" + slot.id + "' РІ " + position.toShortString() + "."));
            } catch (IOException e) {
                context.sendMessage(Message.raw("РќРµ СѓРґР°Р»РѕСЃСЊ РґРѕР±Р°РІРёС‚СЊ СЃР»РѕС‚: " + e.getMessage()));
            }
        }
    }

    private static final class SlotRemoveCommand extends AbstractPlayerCommand {
        private final BankDefenseRepository repository;
        private final DefaultArg<Integer> radiusArg = this.withDefaultArg("radius", "Р Р°РґРёСѓСЃ РїРѕРёСЃРєР°", ArgTypes.INTEGER, Integer.valueOf(3), "3");

        private SlotRemoveCommand(BankDefenseRepository repository) {
            super("remove", "РЈРґР°Р»РёС‚СЊ Р±Р»РёР¶Р°Р№С€РёР№ СЃР»РѕС‚");
            this.repository = repository;
        }

        @Override
        protected void execute(CommandContext context, Store<EntityStore> store, Ref<EntityStore> ref, PlayerRef playerRef, World world) {
            try {
                BuildSlotsConfig slotsConfig = this.repository.loadBuildSlotsConfig(world);
                if (slotsConfig.slots.isEmpty()) {
                    context.sendMessage(Message.raw("РЎР»РѕС‚РѕРІ РЅРµС‚."));
                    return;
                }
                Vec3i position = currentBlockPosition(store, ref);
                int radius = this.radiusArg.get(context);
                double maxDistanceSquared = (double)(radius * radius);
                BuildSlot nearest = null;
                double bestDistance = Double.MAX_VALUE;
                for (BuildSlot slot : slotsConfig.slots) {
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
                if (nearest == null) {
                    context.sendMessage(Message.raw("Р’ СЂР°РґРёСѓСЃРµ " + radius + " СЃР»РѕС‚С‹ РЅРµ РЅР°Р№РґРµРЅС‹."));
                    return;
                }
                slotsConfig.slots.remove(nearest);
                this.repository.saveBuildSlotsConfig(world, slotsConfig);
                clearImmediateSlotBlock(world, nearest.position);
                context.sendMessage(Message.raw("РЈРґР°Р»С‘РЅ " + slotTypeLabel(nearest) + "-СЃР»РѕС‚ '" + nearest.id + "' РІ " + nearest.position.toShortString() + "."));
            } catch (IOException e) {
                context.sendMessage(Message.raw("РќРµ СѓРґР°Р»РѕСЃСЊ СѓРґР°Р»РёС‚СЊ СЃР»РѕС‚: " + e.getMessage()));
            }
        }
    }

    private static final class SlotClearCommand extends AbstractWorldCommand {
        private final BankDefenseRepository repository;
        private final DefaultArg<String> typeArg = this.withDefaultArg("type", "РўРёРї СЃР»РѕС‚Р°", ArgTypes.STRING, "all", "all");

        private SlotClearCommand(BankDefenseRepository repository) {
            super("clear", "РћС‡РёСЃС‚РёС‚СЊ СЃР»РѕС‚С‹");
            this.repository = repository;
        }

        @Override
        protected void execute(CommandContext context, World world, Store<EntityStore> store) {
            String slotType = normalizeSlotType(this.typeArg.get(context), "all");
            try {
                BuildSlotsConfig slotsConfig = this.repository.loadBuildSlotsConfig(world);
                if ("all".equals(slotType)) {
                    List<Vec3i> removedPoints = new ArrayList<>();
                    for (BuildSlot slot : slotsConfig.slots) {
                        if (slot != null && slot.position != null) {
                            removedPoints.add(slot.position);
                        }
                    }
                    slotsConfig.slots.clear();
                    this.repository.saveBuildSlotsConfig(world, slotsConfig);
                    for (Vec3i point : removedPoints) {
                        clearImmediateSlotBlock(world, point);
                    }
                    context.sendMessage(Message.raw("Р’СЃРµ СЃР»РѕС‚С‹ РѕС‡РёС‰РµРЅС‹."));
                    return;
                }
                List<Vec3i> removedPoints = new ArrayList<>();
                slotsConfig.slots.removeIf(slot -> {
                    if (slotType.equals(slotTypeLabel(slot))) {
                        if (slot != null && slot.position != null) {
                            removedPoints.add(slot.position);
                        }
                        return true;
                    }
                    return false;
                });
                this.repository.saveBuildSlotsConfig(world, slotsConfig);
                for (Vec3i point : removedPoints) {
                    clearImmediateSlotBlock(world, point);
                }
                context.sendMessage(Message.raw("РћС‡РёС‰РµРЅС‹ СЃР»РѕС‚С‹ С‚РёРїР° " + slotType + "."));
            } catch (IOException e) {
                context.sendMessage(Message.raw("РќРµ СѓРґР°Р»РѕСЃСЊ РѕС‡РёСЃС‚РёС‚СЊ СЃР»РѕС‚С‹: " + e.getMessage()));
            }
        }
    }

    private static final class SlotListCommand extends AbstractWorldCommand {
        private final BankDefenseRepository repository;

        private SlotListCommand(BankDefenseRepository repository) {
            super("list", "РџРѕРєР°Р·Р°С‚СЊ С‚РµРєСѓС‰РёРµ СЃР»РѕС‚С‹");
            this.repository = repository;
        }

        @Override
        protected void execute(CommandContext context, World world, Store<EntityStore> store) {
            try {
                BuildSlotsConfig slotsConfig = this.repository.loadBuildSlotsConfig(world);
                if (slotsConfig.slots.isEmpty()) {
                    context.sendMessage(Message.raw("РЎР»РѕС‚С‹ РЅРµ Р·Р°РґР°РЅС‹."));
                    return;
                }
                StringBuilder builder = new StringBuilder("РЎР»РѕС‚РѕРІ: ").append(slotsConfig.slots.size());
                for (BuildSlot slot : slotsConfig.slots) {
                    builder.append('\n')
                        .append("- ")
                        .append(slot.id)
                        .append(" [").append(slotDebugLabel(slot)).append("] @ ")
                        .append(slot.position == null ? "РЅРµС‚ РїРѕР·РёС†РёРё" : slot.position.toShortString());
                }
                context.sendMessage(Message.raw(builder.toString()));
            } catch (IOException e) {
                context.sendMessage(Message.raw("РќРµ СѓРґР°Р»РѕСЃСЊ РїРѕРєР°Р·Р°С‚СЊ СЃР»РѕС‚С‹: " + e.getMessage()));
            }
        }
    }

    private static final class ResetAliasCommand extends AbstractWorldCommand {
        private final BankDefenseRuntime runtime;

        private ResetAliasCommand(BankDefenseRuntime runtime) {
            super("reset", "Р‘С‹СЃС‚СЂРѕ СЃР±СЂРѕСЃРёС‚СЊ РјР°С‚С‡");
            this.runtime = runtime;
        }

        @Override
        protected void execute(CommandContext context, World world, Store<EntityStore> store) {
            try {
                MatchState state = this.runtime.resetMatch(world);
                context.sendMessage(Message.raw("РњР°С‚С‡ СЃР±СЂРѕС€РµРЅ. Р’РѕР»РЅР°: " + state.currentWave + ", СЃСЂРµРґСЃС‚РІР°: " + state.currency + "."));
            } catch (IOException e) {
                context.sendMessage(Message.raw("РќРµ СѓРґР°Р»РѕСЃСЊ СЃР±СЂРѕСЃРёС‚СЊ РјР°С‚С‡: " + e.getMessage()));
            }
        }
    }

    private static final class StartAliasCommand extends AbstractWorldCommand {
        private final BankDefenseRuntime runtime;

        private StartAliasCommand(BankDefenseRuntime runtime) {
            super("start", "Р‘С‹СЃС‚СЂРѕ РЅР°С‡Р°С‚СЊ РёРіСЂСѓ РёР»Рё РІРѕР»РЅСѓ");
            this.runtime = runtime;
        }

        @Override
        protected void execute(CommandContext context, World world, Store<EntityStore> store) {
            try {
                context.sendMessage(Message.raw(this.runtime.startNextWave(world).message));
            } catch (IOException e) {
                context.sendMessage(Message.raw("РќРµ СѓРґР°Р»РѕСЃСЊ Р·Р°РїСѓСЃС‚РёС‚СЊ РІРѕР»РЅСѓ: " + e.getMessage()));
            }
        }
    }

    private static final class MarkerSetAliasCommand extends AbstractPlayerCommand {
        private final BankDefenseRepository repository;
        private final RequiredArg<String> markerArg = this.withRequiredArg("marker", "РњР°СЂРєРµСЂ", ArgTypes.STRING);

        private MarkerSetAliasCommand(BankDefenseRepository repository) {
            super("markerset", "РџРѕСЃС‚Р°РІРёС‚СЊ РјР°СЂРєРµСЂ");
            this.repository = repository;
        }

        @Override
        protected void execute(CommandContext context, Store<EntityStore> store, Ref<EntityStore> ref, PlayerRef playerRef, World world) {
            String markerName = normalizeMarkerName(this.markerArg.get(context));
            if (markerName == null) {
                context.sendMessage(Message.raw("РќРµРёР·РІРµСЃС‚РЅС‹Р№ РјР°СЂРєРµСЂ. РСЃРїРѕР»СЊР·СѓР№ spawn_a, spawn_b, spawn_c, bank, vault, playerstart, control, vendor РёР»Рё mode."));
                return;
            }
            try {
                MapConfig mapConfig = this.repository.loadMapConfig(world);
                Vec3i position = currentBlockPosition(store, ref);
                float yaw = currentYaw(store, ref);
                switch (markerName) {
                    case "spawn_a" -> {
                        mapConfig.spawnPoint = position;
                        mapConfig.spawnPointA = position;
                    }
                    case "spawn_b" -> mapConfig.spawnPointB = position;
                    case "spawn_c" -> mapConfig.spawnPointC = position;
                    case "bank" -> mapConfig.bankCenter = position;
                    case "vault" -> mapConfig.vaultPoint = position;
                    case "playerstart" -> {
                        mapConfig.playerStart = position;
                        mapConfig.playerStartYaw = yaw;
                    }
                    case "control" -> {
                        mapConfig.previousControlPoint = mapConfig.controlPoint;
                        mapConfig.controlPoint = position;
                        mapConfig.controlYaw = yaw;
                    }
                    case "vendor" -> {
                        mapConfig.previousVendorPoint = mapConfig.vendorPoint;
                        mapConfig.vendorPoint = position;
                        mapConfig.vendorYaw = yaw;
                    }
                    case "mode" -> {
                        mapConfig.previousModePoint = mapConfig.modePoint;
                        mapConfig.modePoint = position;
                        mapConfig.modeYaw = yaw;
                    }
                    case "statistics" -> {
                        mapConfig.previousStatisticsPoint = mapConfig.statisticsPoint;
                        mapConfig.statisticsPoint = position;
                        mapConfig.statisticsYaw = yaw;
                    }
                    case "duoplayerblue" -> {
                        mapConfig.duoPlayerStartBlue = position;
                        mapConfig.duoPlayerStartBlueYaw = yaw;
                    }
                    case "duoplayergreen" -> {
                        mapConfig.duoPlayerStartGreen = position;
                        mapConfig.duoPlayerStartGreenYaw = yaw;
                    }
                    case "duospawnblue" -> mapConfig.duoSpawnPointBlue = position;
                    case "duospawngreen" -> mapConfig.duoSpawnPointGreen = position;
                    case "duobank" -> mapConfig.duoBankCenter = position;
                    case "duovault" -> mapConfig.duoVaultPoint = position;
                    case "duosealblue" -> mapConfig.duoSealNodeBluePoint = position;
                    case "duosealgreen" -> mapConfig.duoSealNodeGreenPoint = position;
                    case "duostats" -> {
                        mapConfig.previousDuoStatisticsPoint = mapConfig.duoStatisticsPoint;
                        mapConfig.duoStatisticsPoint = position;
                        mapConfig.duoStatisticsYaw = yaw;
                    }
                    case "duoteam" -> {
                        mapConfig.previousDuoTeamPoint = mapConfig.duoTeamPoint;
                        mapConfig.duoTeamPoint = position;
                        mapConfig.duoTeamYaw = yaw;
                    }
                    case "duoteleportnpc" -> {
                        mapConfig.previousDuoTeleportPoint = mapConfig.duoTeleportPoint;
                        mapConfig.duoTeleportPoint = position;
                        mapConfig.duoTeleportYaw = yaw;
                    }
                    case "teleportduo" -> mapConfig.duoTeleportTarget = position;
                    case "teleportsolo" -> mapConfig.soloTeleportTarget = position;
                    case "tutorialstart" -> {
                        mapConfig.tutorialStartPoint = position;
                        mapConfig.tutorialStartYaw = yaw;
                    }
                    case "tutorialspawna" -> mapConfig.tutorialSpawnPointA = position;
                    case "tutorialspawnb" -> mapConfig.tutorialSpawnPointB = position;
                    case "tutorialspawnc" -> mapConfig.tutorialSpawnPointC = position;
                    case "tutorialbank" -> mapConfig.tutorialBankCenter = position;
                    case "tutorialvault" -> mapConfig.tutorialVaultPoint = position;
                    case "tutorialcontrol" -> {
                        mapConfig.tutorialControlPoint = position;
                        mapConfig.tutorialControlYaw = yaw;
                    }
                    case "tutorialvendor" -> {
                        mapConfig.tutorialVendorPoint = position;
                        mapConfig.tutorialVendorYaw = yaw;
                    }
                    case "wizardintro" -> {
                        mapConfig.tutorialWizardIntroPoint = position;
                        mapConfig.tutorialWizardIntroYaw = yaw;
                    }
                    case "tutorialwizard" -> {
                        mapConfig.tutorialWizardPoint = position;
                        mapConfig.tutorialWizardYaw = yaw;
                    }
                    default -> {
                        context.sendMessage(Message.raw("РњР°СЂРєРµСЂ РЅРµ РїРѕРґРґРµСЂР¶РёРІР°РµС‚СЃСЏ: " + markerName));
                        return;
                    }
                }
                this.repository.saveMapConfig(world, mapConfig);
                context.sendMessage(Message.raw("РњР°СЂРєРµСЂ '" + markerName + "' СѓСЃС‚Р°РЅРѕРІР»РµРЅ РІ " + position.toShortString() + "."));
            } catch (IOException e) {
                context.sendMessage(Message.raw("РќРµ СѓРґР°Р»РѕСЃСЊ СЃРѕС…СЂР°РЅРёС‚СЊ РјР°СЂРєРµСЂ: " + e.getMessage()));
            }
        }
    }

    private static final class MarkerClearAliasCommand extends AbstractWorldCommand {
        private final BankDefenseRepository repository;
        private final RequiredArg<String> markerArg = this.withRequiredArg("marker", "РњР°СЂРєРµСЂ", ArgTypes.STRING);

        private MarkerClearAliasCommand(BankDefenseRepository repository) {
            super("markerclear", "РћС‡РёСЃС‚РёС‚СЊ РјР°СЂРєРµСЂ");
            this.repository = repository;
        }

        @Override
        protected void execute(CommandContext context, World world, Store<EntityStore> store) {
            String markerName = normalizeMarkerName(this.markerArg.get(context));
            if (markerName == null) {
                context.sendMessage(Message.raw("РќРµРёР·РІРµСЃС‚РЅС‹Р№ РјР°СЂРєРµСЂ. РСЃРїРѕР»СЊР·СѓР№ spawn_a, spawn_b, spawn_c, bank, vault, playerstart, control, vendor РёР»Рё mode."));
                return;
            }
            try {
                MapConfig mapConfig = this.repository.loadMapConfig(world);
                switch (markerName) {
                    case "spawn_a" -> {
                        mapConfig.spawnPoint = null;
                        mapConfig.spawnPointA = null;
                    }
                    case "spawn_b" -> mapConfig.spawnPointB = null;
                    case "spawn_c" -> mapConfig.spawnPointC = null;
                    case "bank" -> mapConfig.bankCenter = null;
                    case "vault" -> mapConfig.vaultPoint = null;
                    case "playerstart" -> {
                        mapConfig.playerStart = null;
                        mapConfig.playerStartYaw = null;
                    }
                    case "control" -> {
                        mapConfig.previousControlPoint = mapConfig.controlPoint;
                        mapConfig.controlPoint = null;
                        mapConfig.controlYaw = null;
                    }
                    case "vendor" -> {
                        mapConfig.previousVendorPoint = mapConfig.vendorPoint;
                        mapConfig.vendorPoint = null;
                        mapConfig.vendorYaw = null;
                    }
                    case "mode" -> {
                        mapConfig.previousModePoint = mapConfig.modePoint;
                        mapConfig.modePoint = null;
                        mapConfig.modeYaw = null;
                    }
                    case "statistics" -> {
                        mapConfig.previousStatisticsPoint = mapConfig.statisticsPoint;
                        mapConfig.statisticsPoint = null;
                        mapConfig.statisticsYaw = null;
                    }
                    case "duoplayerblue" -> {
                        mapConfig.duoPlayerStartBlue = null;
                        mapConfig.duoPlayerStartBlueYaw = null;
                    }
                    case "duoplayergreen" -> {
                        mapConfig.duoPlayerStartGreen = null;
                        mapConfig.duoPlayerStartGreenYaw = null;
                    }
                    case "duospawnblue" -> mapConfig.duoSpawnPointBlue = null;
                    case "duospawngreen" -> mapConfig.duoSpawnPointGreen = null;
                    case "duobank" -> mapConfig.duoBankCenter = null;
                    case "duovault" -> mapConfig.duoVaultPoint = null;
                    case "duosealblue" -> mapConfig.duoSealNodeBluePoint = null;
                    case "duosealgreen" -> mapConfig.duoSealNodeGreenPoint = null;
                    case "duostats" -> {
                        mapConfig.previousDuoStatisticsPoint = mapConfig.duoStatisticsPoint;
                        mapConfig.duoStatisticsPoint = null;
                        mapConfig.duoStatisticsYaw = null;
                    }
                    case "duoteam" -> {
                        mapConfig.previousDuoTeamPoint = mapConfig.duoTeamPoint;
                        mapConfig.duoTeamPoint = null;
                        mapConfig.duoTeamYaw = null;
                    }
                    case "duoteleportnpc" -> {
                        mapConfig.previousDuoTeleportPoint = mapConfig.duoTeleportPoint;
                        mapConfig.duoTeleportPoint = null;
                        mapConfig.duoTeleportYaw = null;
                    }
                    case "teleportduo" -> mapConfig.duoTeleportTarget = null;
                    case "teleportsolo" -> mapConfig.soloTeleportTarget = null;
                    case "tutorialstart" -> {
                        mapConfig.tutorialStartPoint = null;
                        mapConfig.tutorialStartYaw = null;
                    }
                    case "tutorialspawna" -> mapConfig.tutorialSpawnPointA = null;
                    case "tutorialspawnb" -> mapConfig.tutorialSpawnPointB = null;
                    case "tutorialspawnc" -> mapConfig.tutorialSpawnPointC = null;
                    case "tutorialbank" -> mapConfig.tutorialBankCenter = null;
                    case "tutorialvault" -> mapConfig.tutorialVaultPoint = null;
                    case "tutorialcontrol" -> {
                        mapConfig.tutorialControlPoint = null;
                        mapConfig.tutorialControlYaw = null;
                    }
                    case "tutorialvendor" -> {
                        mapConfig.tutorialVendorPoint = null;
                        mapConfig.tutorialVendorYaw = null;
                    }
                    case "wizardintro" -> {
                        mapConfig.tutorialWizardIntroPoint = null;
                        mapConfig.tutorialWizardIntroYaw = null;
                    }
                    case "tutorialwizard" -> {
                        mapConfig.tutorialWizardPoint = null;
                        mapConfig.tutorialWizardYaw = null;
                    }
                    default -> {
                        context.sendMessage(Message.raw("РњР°СЂРєРµСЂ РЅРµ РїРѕРґРґРµСЂР¶РёРІР°РµС‚СЃСЏ: " + markerName));
                        return;
                    }
                }
                this.repository.saveMapConfig(world, mapConfig);
                context.sendMessage(Message.raw("РњР°СЂРєРµСЂ '" + markerName + "' РѕС‡РёС‰РµРЅ."));
            } catch (IOException e) {
                context.sendMessage(Message.raw("РќРµ СѓРґР°Р»РѕСЃСЊ РѕС‡РёСЃС‚РёС‚СЊ РјР°СЂРєРµСЂ: " + e.getMessage()));
            }
        }
    }

    private static final class RouteAddAliasCommand extends AbstractPlayerCommand {
        private final BankDefenseRepository repository;
        private final DefaultArg<String> laneArg = this.withDefaultArg("lane", "Р›РёРЅРёСЏ", ArgTypes.STRING, "a", "a");

        private RouteAddAliasCommand(BankDefenseRepository repository) {
            super("routeadd", "Р”РѕР±Р°РІРёС‚СЊ С‚РѕС‡РєСѓ РјР°СЂС€СЂСѓС‚Р°");
            this.repository = repository;
        }

        @Override
        protected void execute(CommandContext context, Store<EntityStore> store, Ref<EntityStore> ref, PlayerRef playerRef, World world) {
            String laneId = normalizeLane(this.laneArg.get(context), "a");
            try {
                MapConfig mapConfig = this.repository.loadMapConfig(world);
                Vec3i position = currentBlockPosition(store, ref);
                List<Vec3i> route = routeRef(mapConfig, laneId);
                route.add(position);
                this.repository.saveMapConfig(world, mapConfig);
                context.sendMessage(Message.raw("Р”РѕР±Р°РІР»РµРЅР° С‚РѕС‡РєР° РјР°СЂС€СЂСѓС‚Р° " + laneId.toUpperCase(Locale.ROOT) + " #" + route.size() + " РІ " + position.toShortString() + "."));
            } catch (IOException e) {
                context.sendMessage(Message.raw("РќРµ СѓРґР°Р»РѕСЃСЊ РґРѕР±Р°РІРёС‚СЊ С‚РѕС‡РєСѓ РјР°СЂС€СЂСѓС‚Р°: " + e.getMessage()));
            }
        }
    }

    private static final class RouteAddLaneAliasCommand extends AbstractPlayerCommand {
        private final BankDefenseRepository repository;
        private final String laneId;

        private RouteAddLaneAliasCommand(BankDefenseRepository repository, String laneId) {
            super("routeadd" + laneId, "Р”РѕР±Р°РІРёС‚СЊ С‚РѕС‡РєСѓ РјР°СЂС€СЂСѓС‚Р° " + laneId.toUpperCase(Locale.ROOT));
            this.repository = repository;
            this.laneId = laneId;
        }

        @Override
        protected void execute(CommandContext context, Store<EntityStore> store, Ref<EntityStore> ref, PlayerRef playerRef, World world) {
            try {
                MapConfig mapConfig = this.repository.loadMapConfig(world);
                Vec3i position = currentBlockPosition(store, ref);
                List<Vec3i> route = routeRef(mapConfig, this.laneId);
                route.add(position);
                this.repository.saveMapConfig(world, mapConfig);
                context.sendMessage(Message.raw("Р”РѕР±Р°РІР»РµРЅР° С‚РѕС‡РєР° РјР°СЂС€СЂСѓС‚Р° " + this.laneId.toUpperCase(Locale.ROOT) + " #" + route.size() + " РІ " + position.toShortString() + "."));
            } catch (IOException e) {
                context.sendMessage(Message.raw("РќРµ СѓРґР°Р»РѕСЃСЊ РґРѕР±Р°РІРёС‚СЊ С‚РѕС‡РєСѓ РјР°СЂС€СЂСѓС‚Р°: " + e.getMessage()));
            }
        }
    }

    private static final class RoutePopLaneAliasCommand extends AbstractWorldCommand {
        private final BankDefenseRepository repository;
        private final String laneId;

        private RoutePopLaneAliasCommand(BankDefenseRepository repository, String laneId) {
            super("routepop" + laneId, "РЈРґР°Р»РёС‚СЊ РїРѕСЃР»РµРґРЅСЋСЋ С‚РѕС‡РєСѓ РјР°СЂС€СЂСѓС‚Р° " + laneId.toUpperCase(Locale.ROOT));
            this.repository = repository;
            this.laneId = laneId;
        }

        @Override
        protected void execute(CommandContext context, World world, Store<EntityStore> store) {
            try {
                MapConfig mapConfig = this.repository.loadMapConfig(world);
                List<Vec3i> route = routeRef(mapConfig, this.laneId);
                if (route.isEmpty()) {
                    context.sendMessage(Message.raw("РњР°СЂС€СЂСѓС‚ " + this.laneId.toUpperCase(Locale.ROOT) + " СѓР¶Рµ РїСѓСЃС‚."));
                    return;
                }
                Vec3i removed = route.remove(route.size() - 1);
                this.repository.saveMapConfig(world, mapConfig);
                context.sendMessage(Message.raw("РЈРґР°Р»РµРЅР° С‚РѕС‡РєР° РјР°СЂС€СЂСѓС‚Р° " + this.laneId.toUpperCase(Locale.ROOT) + " РІ " + removed.toShortString() + "."));
            } catch (IOException e) {
                context.sendMessage(Message.raw("РќРµ СѓРґР°Р»РѕСЃСЊ СѓРґР°Р»РёС‚СЊ С‚РѕС‡РєСѓ РјР°СЂС€СЂСѓС‚Р°: " + e.getMessage()));
            }
        }
    }

    private static final class RouteClearLaneAliasCommand extends AbstractWorldCommand {
        private final BankDefenseRepository repository;
        private final String laneId;

        private RouteClearLaneAliasCommand(BankDefenseRepository repository, String laneId) {
            super("routeclear" + laneId, "РћС‡РёСЃС‚РёС‚СЊ РјР°СЂС€СЂСѓС‚ " + laneLabel(laneId));
            this.repository = repository;
            this.laneId = laneId;
        }

        @Override
        protected void execute(CommandContext context, World world, Store<EntityStore> store) {
            try {
                MapConfig mapConfig = this.repository.loadMapConfig(world);
                if ("all".equals(this.laneId)) {
                    clearAllRoutes(mapConfig);
                    this.repository.saveMapConfig(world, mapConfig);
                    context.sendMessage(Message.raw("Р’СЃРµ РјР°СЂС€СЂСѓС‚С‹ РѕС‡РёС‰РµРЅС‹."));
                    return;
                }
                routeRef(mapConfig, this.laneId).clear();
                this.repository.saveMapConfig(world, mapConfig);
                context.sendMessage(Message.raw("РњР°СЂС€СЂСѓС‚ " + this.laneId.toUpperCase(Locale.ROOT) + " РѕС‡РёС‰РµРЅ."));
            } catch (IOException e) {
                context.sendMessage(Message.raw("РќРµ СѓРґР°Р»РѕСЃСЊ РѕС‡РёСЃС‚РёС‚СЊ РјР°СЂС€СЂСѓС‚: " + e.getMessage()));
            }
        }
    }

    private static final class RouteListLaneAliasCommand extends AbstractWorldCommand {
        private final BankDefenseRepository repository;
        private final String laneId;

        private RouteListLaneAliasCommand(BankDefenseRepository repository, String laneId) {
            super("routelist" + laneId, "РџРѕРєР°Р·Р°С‚СЊ РјР°СЂС€СЂСѓС‚ " + laneLabel(laneId));
            this.repository = repository;
            this.laneId = laneId;
        }

        @Override
        protected void execute(CommandContext context, World world, Store<EntityStore> store) {
            try {
                MapConfig mapConfig = this.repository.loadMapConfig(world);
                if ("all".equals(this.laneId)) {
                    StringBuilder builder = new StringBuilder("РњР°СЂС€СЂСѓС‚С‹:");
                    for (String currentLane : allLaneIds()) {
                        List<Vec3i> route = routeRef(mapConfig, currentLane);
                        builder.append('\n').append(currentLane.toUpperCase(Locale.ROOT)).append(": ").append(route.size()).append(" С‚РѕС‡РµРє");
                    }
                    context.sendMessage(Message.raw(builder.toString()));
                    return;
                }
                List<Vec3i> route = routeRef(mapConfig, this.laneId);
                if (route.isEmpty()) {
                    context.sendMessage(Message.raw("РњР°СЂС€СЂСѓС‚ " + this.laneId.toUpperCase(Locale.ROOT) + " РїСѓСЃС‚."));
                    return;
                }
                StringBuilder builder = new StringBuilder("РњР°СЂС€СЂСѓС‚ ").append(this.laneId.toUpperCase(Locale.ROOT)).append(": ").append(route.size()).append(" С‚РѕС‡РµРє");
                for (int i = 0; i < route.size(); i++) {
                    builder.append('\n').append(i + 1).append(". ").append(route.get(i).toShortString());
                }
                context.sendMessage(Message.raw(builder.toString()));
            } catch (IOException e) {
                context.sendMessage(Message.raw("РќРµ СѓРґР°Р»РѕСЃСЊ РїРѕРєР°Р·Р°С‚СЊ РјР°СЂС€СЂСѓС‚: " + e.getMessage()));
            }
        }
    }

    private static final class DuoRouteAddAliasCommand extends AbstractPlayerCommand {
        private final BankDefenseRepository repository;
        private final String teamId;

        private DuoRouteAddAliasCommand(BankDefenseRepository repository, String teamId) {
            super("duorouteadd" + teamId, "Добавить точку duo-маршрута " + teamId);
            this.repository = repository;
            this.teamId = teamId;
        }

        @Override
        protected void execute(CommandContext context, Store<EntityStore> store, Ref<EntityStore> ref, PlayerRef playerRef, World world) {
            try {
                MapConfig mapConfig = this.repository.loadMapConfig(world);
                Vec3i position = currentBlockPosition(store, ref);
                List<Vec3i> route = duoRouteRef(mapConfig, this.teamId);
                route.add(position);
                this.repository.saveMapConfig(world, mapConfig);
                context.sendMessage(Message.raw("Добавлена точка duo-маршрута " + this.teamId + " #" + route.size() + " в " + position.toShortString() + "."));
            } catch (IOException e) {
                context.sendMessage(Message.raw("Не удалось добавить точку duo-маршрута: " + e.getMessage()));
            }
        }
    }

    private static final class DuoRoutePopAliasCommand extends AbstractWorldCommand {
        private final BankDefenseRepository repository;
        private final String teamId;

        private DuoRoutePopAliasCommand(BankDefenseRepository repository, String teamId) {
            super("duoroutepop" + teamId, "Удалить последнюю точку duo-маршрута " + teamId);
            this.repository = repository;
            this.teamId = teamId;
        }

        @Override
        protected void execute(CommandContext context, World world, Store<EntityStore> store) {
            try {
                MapConfig mapConfig = this.repository.loadMapConfig(world);
                List<Vec3i> route = duoRouteRef(mapConfig, this.teamId);
                if (route.isEmpty()) {
                    context.sendMessage(Message.raw("Duo-маршрут " + this.teamId + " уже пуст."));
                    return;
                }
                Vec3i removed = route.remove(route.size() - 1);
                this.repository.saveMapConfig(world, mapConfig);
                context.sendMessage(Message.raw("Удалена точка duo-маршрута " + this.teamId + " в " + removed.toShortString() + "."));
            } catch (IOException e) {
                context.sendMessage(Message.raw("Не удалось удалить точку duo-маршрута: " + e.getMessage()));
            }
        }
    }

    private static final class DuoRouteClearAliasCommand extends AbstractWorldCommand {
        private final BankDefenseRepository repository;
        private final String teamId;

        private DuoRouteClearAliasCommand(BankDefenseRepository repository, String teamId) {
            super("duorouteclear" + teamId, "Очистить duo-маршрут " + teamId);
            this.repository = repository;
            this.teamId = teamId;
        }

        @Override
        protected void execute(CommandContext context, World world, Store<EntityStore> store) {
            try {
                MapConfig mapConfig = this.repository.loadMapConfig(world);
                if ("all".equals(this.teamId)) {
                    clearAllDuoRoutes(mapConfig);
                    this.repository.saveMapConfig(world, mapConfig);
                    context.sendMessage(Message.raw("Все duo-маршруты очищены."));
                    return;
                }
                duoRouteRef(mapConfig, this.teamId).clear();
                this.repository.saveMapConfig(world, mapConfig);
                context.sendMessage(Message.raw("Duo-маршрут " + this.teamId + " очищен."));
            } catch (IOException e) {
                context.sendMessage(Message.raw("Не удалось очистить duo-маршрут: " + e.getMessage()));
            }
        }
    }

    private static final class DuoRouteListAliasCommand extends AbstractWorldCommand {
        private final BankDefenseRepository repository;
        private final String teamId;

        private DuoRouteListAliasCommand(BankDefenseRepository repository, String teamId) {
            super("duoroutelist" + teamId, "Показать duo-маршрут " + teamId);
            this.repository = repository;
            this.teamId = teamId;
        }

        @Override
        protected void execute(CommandContext context, World world, Store<EntityStore> store) {
            try {
                MapConfig mapConfig = this.repository.loadMapConfig(world);
                if ("all".equals(this.teamId)) {
                    StringBuilder builder = new StringBuilder("Duo-маршруты:");
                    for (String currentTeam : List.of("blue", "green")) {
                        List<Vec3i> route = duoRouteRef(mapConfig, currentTeam);
                        builder.append('\n').append(currentTeam).append(": ").append(route.size()).append(" точек");
                        for (int i = 0; i < route.size(); i++) {
                            builder.append('\n').append("  ").append(i + 1).append(". ").append(route.get(i).toShortString());
                        }
                    }
                    context.sendMessage(Message.raw(builder.toString()));
                    return;
                }
                List<Vec3i> route = duoRouteRef(mapConfig, this.teamId);
                if (route.isEmpty()) {
                    context.sendMessage(Message.raw("Duo-маршрут " + this.teamId + " пуст."));
                    return;
                }
                StringBuilder builder = new StringBuilder("Duo-маршрут ").append(this.teamId).append(": ").append(route.size()).append(" точек");
                for (int i = 0; i < route.size(); i++) {
                    builder.append('\n').append(i + 1).append(". ").append(route.get(i).toShortString());
                }
                context.sendMessage(Message.raw(builder.toString()));
            } catch (IOException e) {
                context.sendMessage(Message.raw("Не удалось показать duo-маршрут: " + e.getMessage()));
            }
        }
    }

    private static final class DuoResetRoutesCommand extends AbstractWorldCommand {
        private final BankDefenseRepository repository;

        private DuoResetRoutesCommand(BankDefenseRepository repository) {
            super("duoresetroutes", "Очистить duo-маршруты и spawn-точки");
            this.repository = repository;
        }

        @Override
        protected void execute(CommandContext context, World world, Store<EntityStore> store) {
            try {
                MapConfig mapConfig = this.repository.loadMapConfig(world);
                clearAllDuoRoutesAndSpawns(mapConfig);
                this.repository.saveMapConfig(world, mapConfig);
                context.sendMessage(Message.raw("Duo-маршруты и точки спавна очищены. Теперь заново поставь markduospawnblue, markduospawngreen и точки через duorouteaddblue / duorouteaddgreen."));
            } catch (IOException e) {
                context.sendMessage(Message.raw("Не удалось очистить duo-маршруты: " + e.getMessage()));
            }
        }
    }

    private static final class DuoResetStartsCommand extends AbstractWorldCommand {
        private final BankDefenseRepository repository;

        private DuoResetStartsCommand(BankDefenseRepository repository) {
            super("duoresetstarts", "Очистить стартовые точки игроков Duo");
            this.repository = repository;
        }

        @Override
        protected void execute(CommandContext context, World world, Store<EntityStore> store) {
            try {
                MapConfig mapConfig = this.repository.loadMapConfig(world);
                clearAllDuoStarts(mapConfig);
                this.repository.saveMapConfig(world, mapConfig);
                context.sendMessage(Message.raw("Duo-старты игроков очищены. Поставь их заново через markduoplayerblue и markduoplayergreen."));
            } catch (IOException e) {
                context.sendMessage(Message.raw("Не удалось очистить duo-старты: " + e.getMessage()));
            }
        }
    }

    private static final class RoutePopAliasCommand extends AbstractWorldCommand {
        private final BankDefenseRepository repository;
        private final DefaultArg<String> laneArg = this.withDefaultArg("lane", "Р›РёРЅРёСЏ", ArgTypes.STRING, "a", "a");

        private RoutePopAliasCommand(BankDefenseRepository repository) {
            super("routepop", "РЈРґР°Р»РёС‚СЊ РїРѕСЃР»РµРґРЅСЋСЋ С‚РѕС‡РєСѓ РјР°СЂС€СЂСѓС‚Р°");
            this.repository = repository;
        }

        @Override
        protected void execute(CommandContext context, World world, Store<EntityStore> store) {
            String laneId = normalizeLane(this.laneArg.get(context), "a");
            try {
                MapConfig mapConfig = this.repository.loadMapConfig(world);
                List<Vec3i> route = routeRef(mapConfig, laneId);
                if (route.isEmpty()) {
                    context.sendMessage(Message.raw("РњР°СЂС€СЂСѓС‚ " + laneId.toUpperCase(Locale.ROOT) + " СѓР¶Рµ РїСѓСЃС‚."));
                    return;
                }
                Vec3i removed = route.remove(route.size() - 1);
                this.repository.saveMapConfig(world, mapConfig);
                context.sendMessage(Message.raw("РЈРґР°Р»РµРЅР° С‚РѕС‡РєР° РјР°СЂС€СЂСѓС‚Р° " + laneId.toUpperCase(Locale.ROOT) + " РІ " + removed.toShortString() + "."));
            } catch (IOException e) {
                context.sendMessage(Message.raw("РќРµ СѓРґР°Р»РѕСЃСЊ СѓРґР°Р»РёС‚СЊ С‚РѕС‡РєСѓ РјР°СЂС€СЂСѓС‚Р°: " + e.getMessage()));
            }
        }
    }

    private static final class RouteClearAliasCommand extends AbstractWorldCommand {
        private final BankDefenseRepository repository;
        private final DefaultArg<String> laneArg = this.withDefaultArg("lane", "Р›РёРЅРёСЏ", ArgTypes.STRING, "all", "all");

        private RouteClearAliasCommand(BankDefenseRepository repository) {
            super("routeclear", "РћС‡РёСЃС‚РёС‚СЊ РјР°СЂС€СЂСѓС‚");
            this.repository = repository;
        }

        @Override
        protected void execute(CommandContext context, World world, Store<EntityStore> store) {
            String laneId = normalizeLane(this.laneArg.get(context), "all");
            try {
                MapConfig mapConfig = this.repository.loadMapConfig(world);
                if ("all".equals(laneId)) {
                    clearAllRoutes(mapConfig);
                    this.repository.saveMapConfig(world, mapConfig);
                    context.sendMessage(Message.raw("Р’СЃРµ РјР°СЂС€СЂСѓС‚С‹ РѕС‡РёС‰РµРЅС‹."));
                    return;
                }
                routeRef(mapConfig, laneId).clear();
                this.repository.saveMapConfig(world, mapConfig);
                context.sendMessage(Message.raw("РњР°СЂС€СЂСѓС‚ " + laneId.toUpperCase(Locale.ROOT) + " РѕС‡РёС‰РµРЅ."));
            } catch (IOException e) {
                context.sendMessage(Message.raw("РќРµ СѓРґР°Р»РѕСЃСЊ РѕС‡РёСЃС‚РёС‚СЊ РјР°СЂС€СЂСѓС‚: " + e.getMessage()));
            }
        }
    }

    private static final class RouteListAliasCommand extends AbstractWorldCommand {
        private final BankDefenseRepository repository;
        private final DefaultArg<String> laneArg = this.withDefaultArg("lane", "Р›РёРЅРёСЏ", ArgTypes.STRING, "all", "all");

        private RouteListAliasCommand(BankDefenseRepository repository) {
            super("routelist", "РџРѕРєР°Р·Р°С‚СЊ РјР°СЂС€СЂСѓС‚");
            this.repository = repository;
        }

        @Override
        protected void execute(CommandContext context, World world, Store<EntityStore> store) {
            String laneId = normalizeLane(this.laneArg.get(context), "all");
            try {
                MapConfig mapConfig = this.repository.loadMapConfig(world);
                if ("all".equals(laneId)) {
                    StringBuilder builder = new StringBuilder("РњР°СЂС€СЂСѓС‚С‹:");
                    for (String currentLane : allLaneIds()) {
                        List<Vec3i> route = routeRef(mapConfig, currentLane);
                        builder.append('\n').append(currentLane.toUpperCase(Locale.ROOT)).append(": ").append(route.size()).append(" С‚РѕС‡РµРє");
                        for (int i = 0; i < route.size(); i++) {
                            builder.append('\n').append("  ").append(i + 1).append(". ").append(route.get(i).toShortString());
                        }
                    }
                    context.sendMessage(Message.raw(builder.toString()));
                    return;
                }
                List<Vec3i> route = routeRef(mapConfig, laneId);
                if (route.isEmpty()) {
                    context.sendMessage(Message.raw("РњР°СЂС€СЂСѓС‚ " + laneId.toUpperCase(Locale.ROOT) + " РїСѓСЃС‚."));
                    return;
                }
                StringBuilder builder = new StringBuilder("РњР°СЂС€СЂСѓС‚ ").append(laneId.toUpperCase(Locale.ROOT)).append(": ").append(route.size()).append(" С‚РѕС‡РµРє");
                for (int i = 0; i < route.size(); i++) {
                    builder.append('\n').append(i + 1).append(". ").append(route.get(i).toShortString());
                }
                context.sendMessage(Message.raw(builder.toString()));
            } catch (IOException e) {
                context.sendMessage(Message.raw("РќРµ СѓРґР°Р»РѕСЃСЊ РїРѕРєР°Р·Р°С‚СЊ РјР°СЂС€СЂСѓС‚: " + e.getMessage()));
            }
        }
    }

    private static final class SlotAddAliasCommand extends AbstractPlayerCommand {
        private final BankDefenseRepository repository;
        private final DefaultArg<String> typeArg = this.withDefaultArg("type", "РўРёРї", ArgTypes.STRING, "standard", "standard");

        private SlotAddAliasCommand(BankDefenseRepository repository) {
            super("slotadd", "Р”РѕР±Р°РІРёС‚СЊ СЃР»РѕС‚");
            this.repository = repository;
        }

        @Override
        protected void execute(CommandContext context, Store<EntityStore> store, Ref<EntityStore> ref, PlayerRef playerRef, World world) {
            String slotType = normalizeSlotType(this.typeArg.get(context), "standard");
            try {
                BuildSlotsConfig slotsConfig = this.repository.loadBuildSlotsConfig(world);
                Vec3i position = currentBlockPosition(store, ref);
                for (BuildSlot existing : slotsConfig.slots) {
                    if (existing.position != null && existing.position.distanceSquaredTo(position) == 0.0) {
                        context.sendMessage(Message.raw("РЎР»РѕС‚ СѓР¶Рµ СЃСѓС‰РµСЃС‚РІСѓРµС‚ РІ " + position.toShortString() + "."));
                        return;
                    }
                }
                int nextIndex = 1;
                for (BuildSlot slot : slotsConfig.slots) {
                    if (slotType.equals(normalizeSlotType(slot.slotType, "standard"))) {
                        nextIndex++;
                    }
                }
                BuildSlot slot = new BuildSlot();
                slot.id = String.format(Locale.ROOT, "%s_%02d", "super".equals(slotType) ? "super" : "trap".equals(slotType) ? "trap" : "slot", nextIndex);
                slot.label = "super".equals(slotType) ? "РЎСѓРїРµСЂ-СЃР»РѕС‚ " + nextIndex : "trap".equals(slotType) ? "Р›РѕРІСѓС€РєР° " + nextIndex : "РЎР»РѕС‚ СЃС‚СЂРѕРёС‚РµР»СЊСЃС‚РІР° " + nextIndex;
                slot.position = position;
                slot.slotType = slotType;
                slot.modulesAllowed = !"super".equals(slotType) && !"trap".equals(slotType);
                if ("tutorial".equals(slotType)) {
                    slot.id = String.format(Locale.ROOT, "tutorial_%02d", nextIndex);
                    slot.label = "РЎР»РѕС‚ РѕР±СѓС‡РµРЅРёСЏ " + nextIndex;
                } else if ("tutorial_super".equals(slotType)) {
                    slot.id = String.format(Locale.ROOT, "tutorial_super_%02d", nextIndex);
                    slot.label = "РЎСѓРїРµСЂ-СЃР»РѕС‚ РѕР±СѓС‡РµРЅРёСЏ " + nextIndex;
                    slot.modulesAllowed = false;
                } else if ("tutorial_trap".equals(slotType)) {
                    slot.id = String.format(Locale.ROOT, "tutorial_trap_%02d", nextIndex);
                    slot.label = "Р›РѕРІСѓС€РєР° РѕР±СѓС‡РµРЅРёСЏ " + nextIndex;
                    slot.modulesAllowed = false;
                }
                if ("super".equals(slotType)) {
                    slot.allowedTowerIds = new ArrayList<>(superTowerIds());
                } else if ("trap".equals(slotType)) {
                    slot.allowedTowerIds = new ArrayList<>(trapTowerIds());
                } else if ("tutorial_super".equals(slotType)) {
                    slot.allowedTowerIds = new ArrayList<>(superTowerIds());
                } else if ("tutorial_trap".equals(slotType)) {
                    slot.allowedTowerIds = new ArrayList<>(trapTowerIds());
                }
                slotsConfig.slots.add(slot);
                this.repository.saveBuildSlotsConfig(world, slotsConfig);
                context.sendMessage(Message.raw("Р”РѕР±Р°РІР»РµРЅ " + slotType + "-СЃР»РѕС‚ '" + slot.id + "' РІ " + position.toShortString() + "."));
            } catch (IOException e) {
                context.sendMessage(Message.raw("РќРµ СѓРґР°Р»РѕСЃСЊ РґРѕР±Р°РІРёС‚СЊ СЃР»РѕС‚: " + e.getMessage()));
            }
        }
    }

    private static final class SlotAddTypeAliasCommand extends AbstractPlayerCommand {
        private final BankDefenseRepository repository;
        private final String slotType;

        private SlotAddTypeAliasCommand(BankDefenseRepository repository, String slotType) {
            super("slotadd" + slotType, "Р”РѕР±Р°РІРёС‚СЊ " + slotType + "-СЃР»РѕС‚");
            this.repository = repository;
            this.slotType = slotType;
        }

        @Override
        protected void execute(CommandContext context, Store<EntityStore> store, Ref<EntityStore> ref, PlayerRef playerRef, World world) {
            try {
                BuildSlotsConfig slotsConfig = this.repository.loadBuildSlotsConfig(world);
                Vec3i position = currentBlockPosition(store, ref);
                for (BuildSlot existing : slotsConfig.slots) {
                    if (existing.position != null && existing.position.distanceSquaredTo(position) == 0.0) {
                        context.sendMessage(Message.raw("РЎР»РѕС‚ СѓР¶Рµ СЃСѓС‰РµСЃС‚РІСѓРµС‚ РІ " + position.toShortString() + "."));
                        return;
                    }
                }
                int nextIndex = 1;
                for (BuildSlot slot : slotsConfig.slots) {
                    if (this.slotType.equals(normalizeSlotType(slot.slotType, "standard"))) {
                        nextIndex++;
                    }
                }
                BuildSlot slot = new BuildSlot();
                slot.id = String.format(Locale.ROOT, "%s_%02d", "super".equals(this.slotType) ? "super" : "trap".equals(this.slotType) ? "trap" : "slot", nextIndex);
                slot.label = "super".equals(this.slotType) ? "РЎСѓРїРµСЂ-СЃР»РѕС‚ " + nextIndex : "trap".equals(this.slotType) ? "Р›РѕРІСѓС€РєР° " + nextIndex : "РЎР»РѕС‚ СЃС‚СЂРѕРёС‚РµР»СЊСЃС‚РІР° " + nextIndex;
                slot.position = position;
                slot.slotType = this.slotType;
                slot.modulesAllowed = !"super".equals(this.slotType) && !"trap".equals(this.slotType);
                if ("tutorial".equals(this.slotType)) {
                    slot.id = String.format(Locale.ROOT, "tutorial_%02d", nextIndex);
                    slot.label = "РЎР»РѕС‚ РѕР±СѓС‡РµРЅРёСЏ " + nextIndex;
                } else if ("tutorial_super".equals(this.slotType)) {
                    slot.id = String.format(Locale.ROOT, "tutorial_super_%02d", nextIndex);
                    slot.label = "РЎСѓРїРµСЂ-СЃР»РѕС‚ РѕР±СѓС‡РµРЅРёСЏ " + nextIndex;
                    slot.modulesAllowed = false;
                } else if ("tutorial_trap".equals(this.slotType)) {
                    slot.id = String.format(Locale.ROOT, "tutorial_trap_%02d", nextIndex);
                    slot.label = "Р›РѕРІСѓС€РєР° РѕР±СѓС‡РµРЅРёСЏ " + nextIndex;
                    slot.modulesAllowed = false;
                }
                if ("super".equals(this.slotType)) {
                    slot.allowedTowerIds = new ArrayList<>(superTowerIds());
                } else if ("trap".equals(this.slotType)) {
                    slot.allowedTowerIds = new ArrayList<>(trapTowerIds());
                } else if ("tutorial_super".equals(this.slotType)) {
                    slot.allowedTowerIds = new ArrayList<>(superTowerIds());
                } else if ("tutorial_trap".equals(this.slotType)) {
                    slot.allowedTowerIds = new ArrayList<>(trapTowerIds());
                }
                slotsConfig.slots.add(slot);
                this.repository.saveBuildSlotsConfig(world, slotsConfig);
                placeImmediateSlotBlock(world, slot);
                context.sendMessage(Message.raw("Р”РѕР±Р°РІР»РµРЅ " + this.slotType + "-СЃР»РѕС‚ '" + slot.id + "' РІ " + position.toShortString() + "."));
            } catch (IOException e) {
                context.sendMessage(Message.raw("РќРµ СѓРґР°Р»РѕСЃСЊ РґРѕР±Р°РІРёС‚СЊ СЃР»РѕС‚: " + e.getMessage()));
            }
        }
    }

    private static final class TutorialTrapAddCommand extends AbstractPlayerCommand {
        private final BankDefenseRepository repository;
        private final BankDefenseRuntime runtime;

        private TutorialTrapAddCommand(BankDefenseRepository repository, BankDefenseRuntime runtime) {
            super("marktutorialtrap", "Поставить учебную ловушечную площадку");
            this.repository = repository;
            this.runtime = runtime;
        }

        @Override
        protected void execute(CommandContext context, Store<EntityStore> store, Ref<EntityStore> ref, PlayerRef playerRef, World world) {
            try {
                MapConfig mapConfig = this.repository.loadMapConfig(world);
                BuildSlotsConfig slotsConfig = this.repository.loadBuildSlotsConfig(world);
                Vec3i position = currentBlockPosition(store, ref);
                for (BuildSlot existing : slotsConfig.slots) {
                    if (existing.position != null && existing.position.distanceSquaredTo(position) == 0.0) {
                        context.sendMessage(Message.raw("Слот уже существует в " + position.toShortString() + "."));
                        return;
                    }
                }
                BuildSlot slot = createBuildSlot(slotsConfig, "tutorial_trap", position);
                slot.allowedTowerIds = new ArrayList<>(List.of("root_snare", "spore_mine", "frost_seal"));
                slotsConfig.slots.add(slot);
                mapConfig.tutorialTrapLayoutCustom = true;
                this.repository.saveMapConfig(world, mapConfig);
                this.repository.saveBuildSlotsConfig(world, slotsConfig);
                placeImmediateSlotBlock(world, slot);
                this.runtime.refreshVisualization(world);
                context.sendMessage(Message.raw("Учебная ловушка '" + slot.id + "' добавлена в " + position.toShortString() + "."));
            } catch (IOException e) {
                context.sendMessage(Message.raw("Не удалось добавить учебную ловушку: " + e.getMessage()));
            }
        }
    }

    private static final class DuoSlotAddAliasCommand extends AbstractPlayerCommand {
        private final BankDefenseRepository repository;
        private final String slotType;
        private final String ownerTeam;
        private final String segment;

        private DuoSlotAddAliasCommand(BankDefenseRepository repository, String commandName, String slotType, String ownerTeam, String segment) {
            super(commandName, "Добавить duo-slot " + ownerTeam + " / " + slotType);
            this.repository = repository;
            this.slotType = slotType;
            this.ownerTeam = ownerTeam;
            this.segment = segment;
        }

        @Override
        protected void execute(CommandContext context, Store<EntityStore> store, Ref<EntityStore> ref, PlayerRef playerRef, World world) {
            try {
                BuildSlotsConfig slotsConfig = this.repository.loadBuildSlotsConfig(world);
                Vec3i position = currentBlockPosition(store, ref);
                for (BuildSlot existing : slotsConfig.slots) {
                    if (existing.position != null && existing.position.distanceSquaredTo(position) == 0.0) {
                        context.sendMessage(Message.raw("Слот уже существует в " + position.toShortString() + "."));
                        return;
                    }
                }
                BuildSlot slot = createBuildSlot(slotsConfig, this.slotType, position);
                String resolvedOwnerTeam = this.ownerTeam;
                String resolvedSegment = this.segment;
                if ("trap".equals(this.slotType)) {
                    resolvedOwnerTeam = "shared";
                    resolvedSegment = "shared_trap";
                }
                configureDuoSlot(slot, resolvedOwnerTeam, resolvedSegment);
                if ("shared".equals(resolvedOwnerTeam)) {
                    slot.label = slot.label + " [Duo Shared]";
                } else if ("blue".equals(resolvedOwnerTeam)) {
                    slot.label = slot.label + " [Duo Blue]";
                } else if ("green".equals(resolvedOwnerTeam)) {
                    slot.label = slot.label + " [Duo Green]";
                }
                slotsConfig.slots.add(slot);
                this.repository.saveBuildSlotsConfig(world, slotsConfig);
                placeImmediateSlotBlock(world, slot);
                context.sendMessage(Message.raw("Добавлен duo-slot '" + slot.id + "' [" + slotDebugLabel(slot) + "] в " + position.toShortString() + "."));
            } catch (IOException e) {
                context.sendMessage(Message.raw("Не удалось добавить duo-slot: " + e.getMessage()));
            }
        }
    }

    private static final class SlotTeamAliasCommand extends AbstractPlayerCommand {
        private final BankDefenseRepository repository;
        private final String ownerTeam;
        private final DefaultArg<Integer> radiusArg = this.withDefaultArg("radius", "Радиус", ArgTypes.INTEGER, 3, "3");

        private SlotTeamAliasCommand(BankDefenseRepository repository, String commandName, String ownerTeam) {
            super(commandName, "Назначить владельца ближайшему слоту: " + ownerTeam);
            this.repository = repository;
            this.ownerTeam = ownerTeam;
        }

        @Override
        protected void execute(CommandContext context, Store<EntityStore> store, Ref<EntityStore> ref, PlayerRef playerRef, World world) {
            int radius = Math.max(1, this.radiusArg.get(context));
            try {
                BuildSlotsConfig slotsConfig = this.repository.loadBuildSlotsConfig(world);
                Vec3i position = currentBlockPosition(store, ref);
                BuildSlot nearest = nearestBuildSlot(slotsConfig, position, radius);
                if (nearest == null) {
                    context.sendMessage(Message.raw("Рядом нет слота в радиусе " + radius + "."));
                    return;
                }

                String previousLayout = nearest.layout == null || nearest.layout.isBlank() ? "solo" : nearest.layout;
                String previousOwner = nearest.ownerTeam == null || nearest.ownerTeam.isBlank() ? "shared" : nearest.ownerTeam;
                String previousSegment = nearest.segment == null ? "" : nearest.segment;

                nearest.layout = "duo";
                nearest.ownerTeam = this.ownerTeam;
                if (previousSegment.isBlank() || isAutoDuoSegment(previousSegment)) {
                    nearest.segment = defaultDuoSegment(nearest, this.ownerTeam);
                }

                this.repository.saveBuildSlotsConfig(world, slotsConfig);
                placeImmediateSlotBlock(world, nearest);

                StringBuilder message = new StringBuilder()
                    .append("Слот '").append(nearest.id).append("' теперь ")
                    .append(slotDebugLabel(nearest))
                    .append(" @ ").append(nearest.position == null ? position.toShortString() : nearest.position.toShortString())
                    .append(".");
                if (!"duo".equals(previousLayout) || !this.ownerTeam.equals(previousOwner)) {
                    message.append(" Было: layout=").append(previousLayout).append(", owner=").append(previousOwner).append(".");
                }
                if (isSuperLikeSlot(nearest) && !"shared".equals(this.ownerTeam)) {
                    message.append(" Внимание: super-пады обычно лучше оставлять shared.");
                }
                context.sendMessage(Message.raw(message.toString()));
            } catch (IOException e) {
                context.sendMessage(Message.raw("Не удалось обновить слот: " + e.getMessage()));
            }
        }
    }

    private static final class SlotRemoveAliasCommand extends AbstractPlayerCommand {
        private final BankDefenseRepository repository;
        private final DefaultArg<Integer> radiusArg = this.withDefaultArg("radius", "Р Р°РґРёСѓСЃ", ArgTypes.INTEGER, 3, "3");

        private SlotRemoveAliasCommand(BankDefenseRepository repository) {
            super("slotremove", "РЈРґР°Р»РёС‚СЊ Р±Р»РёР¶Р°Р№С€РёР№ СЃР»РѕС‚");
            this.repository = repository;
        }

        @Override
        protected void execute(CommandContext context, Store<EntityStore> store, Ref<EntityStore> ref, PlayerRef playerRef, World world) {
            int radius = Math.max(1, this.radiusArg.get(context));
            try {
                BuildSlotsConfig slotsConfig = this.repository.loadBuildSlotsConfig(world);
                Vec3i position = currentBlockPosition(store, ref);
                BuildSlot nearest = nearestBuildSlot(slotsConfig, position, radius);
                if (nearest == null) {
                    context.sendMessage(Message.raw("Р СЏРґРѕРј РЅРµС‚ СЃР»РѕС‚Р° РІ СЂР°РґРёСѓСЃРµ " + radius + "."));
                    return;
                }
                slotsConfig.slots.remove(nearest);
                this.repository.saveBuildSlotsConfig(world, slotsConfig);
                clearImmediateSlotBlock(world, nearest.position);
                context.sendMessage(Message.raw("Удалён слот '" + nearest.id + "'."));
            } catch (IOException e) {
                context.sendMessage(Message.raw("Не удалось удалить слот: " + e.getMessage()));
            }
        }
    }

    private static final class SlotClearAliasCommand extends AbstractWorldCommand {
        private final BankDefenseRepository repository;
        private final DefaultArg<String> typeArg = this.withDefaultArg("type", "РўРёРї", ArgTypes.STRING, "all", "all");

        private SlotClearAliasCommand(BankDefenseRepository repository) {
            super("slotclear", "РћС‡РёСЃС‚РёС‚СЊ СЃР»РѕС‚С‹");
            this.repository = repository;
        }

        @Override
        protected void execute(CommandContext context, World world, Store<EntityStore> store) {
            String slotType = normalizeSlotType(this.typeArg.get(context), "all");
            try {
                BuildSlotsConfig slotsConfig = this.repository.loadBuildSlotsConfig(world);
                if ("all".equals(slotType)) {
                    List<Vec3i> removedPoints = new ArrayList<>();
                    for (BuildSlot slot : slotsConfig.slots) {
                        if (slot != null && slot.position != null) {
                            removedPoints.add(slot.position);
                        }
                    }
                    slotsConfig.slots.clear();
                    this.repository.saveBuildSlotsConfig(world, slotsConfig);
                    for (Vec3i point : removedPoints) {
                        clearImmediateSlotBlock(world, point);
                    }
                    context.sendMessage(Message.raw("Р’СЃРµ СЃР»РѕС‚С‹ РѕС‡РёС‰РµРЅС‹."));
                    return;
                }
                List<Vec3i> removedPoints = new ArrayList<>();
                slotsConfig.slots.removeIf(slot -> {
                    if (slotType.equals(slotTypeLabel(slot))) {
                        if (slot != null && slot.position != null) {
                            removedPoints.add(slot.position);
                        }
                        return true;
                    }
                    return false;
                });
                this.repository.saveBuildSlotsConfig(world, slotsConfig);
                for (Vec3i point : removedPoints) {
                    clearImmediateSlotBlock(world, point);
                }
                context.sendMessage(Message.raw("РћС‡РёС‰РµРЅС‹ СЃР»РѕС‚С‹ С‚РёРїР° " + slotType + "."));
            } catch (IOException e) {
                context.sendMessage(Message.raw("РќРµ СѓРґР°Р»РѕСЃСЊ РѕС‡РёСЃС‚РёС‚СЊ СЃР»РѕС‚С‹: " + e.getMessage()));
            }
        }
    }

    private static final class SlotClearTypeAliasCommand extends AbstractWorldCommand {
        private final BankDefenseRepository repository;
        private final String slotType;

        private SlotClearTypeAliasCommand(BankDefenseRepository repository, String slotType) {
            super("slotclear" + slotType, "РћС‡РёСЃС‚РёС‚СЊ " + slotType + "-СЃР»РѕС‚С‹");
            this.repository = repository;
            this.slotType = slotType;
        }

        @Override
        protected void execute(CommandContext context, World world, Store<EntityStore> store) {
            try {
                BuildSlotsConfig slotsConfig = this.repository.loadBuildSlotsConfig(world);
                if ("all".equals(this.slotType)) {
                    List<Vec3i> removedPoints = new ArrayList<>();
                    for (BuildSlot slot : slotsConfig.slots) {
                        if (slot != null && slot.position != null) {
                            removedPoints.add(slot.position);
                        }
                    }
                    slotsConfig.slots.clear();
                    this.repository.saveBuildSlotsConfig(world, slotsConfig);
                    for (Vec3i point : removedPoints) {
                        clearImmediateSlotBlock(world, point);
                    }
                    context.sendMessage(Message.raw("Р’СЃРµ СЃР»РѕС‚С‹ РѕС‡РёС‰РµРЅС‹."));
                    return;
                }
                List<Vec3i> removedPoints = new ArrayList<>();
                slotsConfig.slots.removeIf(slot -> {
                    if (this.slotType.equals(slotTypeLabel(slot))) {
                        if (slot != null && slot.position != null) {
                            removedPoints.add(slot.position);
                        }
                        return true;
                    }
                    return false;
                });
                this.repository.saveBuildSlotsConfig(world, slotsConfig);
                for (Vec3i point : removedPoints) {
                    clearImmediateSlotBlock(world, point);
                }
                context.sendMessage(Message.raw("РћС‡РёС‰РµРЅС‹ СЃР»РѕС‚С‹ С‚РёРїР° " + this.slotType + "."));
            } catch (IOException e) {
                context.sendMessage(Message.raw("РќРµ СѓРґР°Р»РѕСЃСЊ РѕС‡РёСЃС‚РёС‚СЊ СЃР»РѕС‚С‹: " + e.getMessage()));
            }
        }
    }

    private static final class TutorialTrapClearCommand extends AbstractWorldCommand {
        private final BankDefenseRepository repository;
        private final BankDefenseRuntime runtime;

        private TutorialTrapClearCommand(BankDefenseRepository repository, BankDefenseRuntime runtime) {
            super("cleartutorialtraps", "Очистить все учебные ловушечные площадки");
            this.repository = repository;
            this.runtime = runtime;
        }

        @Override
        protected void execute(CommandContext context, World world, Store<EntityStore> store) {
            try {
                MapConfig mapConfig = this.repository.loadMapConfig(world);
                BuildSlotsConfig slotsConfig = this.repository.loadBuildSlotsConfig(world);
                Set<String> removedKeys = new LinkedHashSet<>();
                List<Vec3i> removedPoints = new ArrayList<>();
                int before = slotsConfig.slots.size();
                slotsConfig.slots.removeIf(slot -> {
                    if ("tutorial_trap".equals(slotTypeLabel(slot))) {
                        if (slot != null && slot.position != null) {
                            String key = slot.position.x + ":" + slot.position.y + ":" + slot.position.z;
                            if (removedKeys.add(key)) {
                                removedPoints.add(slot.position);
                            }
                        }
                        return true;
                    }
                    return false;
                });
                int removed = before - slotsConfig.slots.size();
                for (Vec3i point : defaultTutorialTrapPoints(mapConfig)) {
                    if (point == null) {
                        continue;
                    }
                    String key = point.x + ":" + point.y + ":" + point.z;
                    if (removedKeys.add(key)) {
                        removedPoints.add(point);
                    }
                }
                mapConfig.tutorialTrapLayoutCustom = true;
                this.repository.saveMapConfig(world, mapConfig);
                this.repository.saveBuildSlotsConfig(world, slotsConfig);
                for (Vec3i point : removedPoints) {
                    clearImmediateSlotBlockNeighborhood(world, point);
                }
                this.runtime.refreshVisualization(world);
                context.sendMessage(Message.raw("Удалено учебных ловушечных площадок: " + removed + "."));
            } catch (IOException e) {
                context.sendMessage(Message.raw("Не удалось очистить учебные ловушечные площадки: " + e.getMessage()));
            }
        }
    }

    private static final class SlotListAliasCommand extends AbstractWorldCommand {
        private final BankDefenseRepository repository;

        private SlotListAliasCommand(BankDefenseRepository repository) {
            super("slotlist", "РџРѕРєР°Р·Р°С‚СЊ СЃР»РѕС‚С‹");
            this.repository = repository;
        }

        @Override
        protected void execute(CommandContext context, World world, Store<EntityStore> store) {
            try {
                BuildSlotsConfig slotsConfig = this.repository.loadBuildSlotsConfig(world);
                if (slotsConfig.slots.isEmpty()) {
                    context.sendMessage(Message.raw("РЎР»РѕС‚С‹ РЅРµ Р·Р°РґР°РЅС‹."));
                    return;
                }
                StringBuilder builder = new StringBuilder("РЎР»РѕС‚РѕРІ: ").append(slotsConfig.slots.size());
                for (BuildSlot slot : slotsConfig.slots) {
                    builder.append('\n')
                        .append("- ")
                        .append(slot.id)
                        .append(" [").append(slotDebugLabel(slot)).append("] @ ")
                        .append(slot.position == null ? "РЅРµС‚ РїРѕР·РёС†РёРё" : slot.position.toShortString());
                }
                context.sendMessage(Message.raw(builder.toString()));
            } catch (IOException e) {
                context.sendMessage(Message.raw("РќРµ СѓРґР°Р»РѕСЃСЊ РїРѕРєР°Р·Р°С‚СЊ СЃР»РѕС‚С‹: " + e.getMessage()));
            }
        }
    }

    private static final class ChestAddAliasCommand extends AbstractPlayerCommand {
        private final BankDefenseRepository repository;

        private ChestAddAliasCommand(BankDefenseRepository repository) {
            super("chestadd", "Р”РѕР±Р°РІРёС‚СЊ С‚РѕС‡РєСѓ СЃРїР°РІРЅР° Р±РѕРЅСѓСЃРЅРѕРіРѕ СЃСѓРЅРґСѓРєР°");
            this.repository = repository;
        }

        @Override
        protected void execute(CommandContext context, Store<EntityStore> store, Ref<EntityStore> ref, PlayerRef playerRef, World world) {
            try {
                MapConfig mapConfig = this.repository.loadMapConfig(world);
                Vec3i position = currentBlockPosition(store, ref);
                for (Vec3i existing : mapConfig.chestSpawnPoints) {
                    if (existing != null && existing.distanceSquaredTo(position) == 0.0) {
                        context.sendMessage(Message.raw("РўРѕС‡РєР° СЃСѓРЅРґСѓРєР° СѓР¶Рµ СЃСѓС‰РµСЃС‚РІСѓРµС‚ РІ " + position.toShortString() + "."));
                        return;
                    }
                }
                mapConfig.chestSpawnPoints.add(position);
                this.repository.saveMapConfig(world, mapConfig);
                context.sendMessage(Message.raw("Р”РѕР±Р°РІР»РµРЅР° С‚РѕС‡РєР° СЃСѓРЅРґСѓРєР° #" + mapConfig.chestSpawnPoints.size() + " РІ " + position.toShortString() + "."));
            } catch (IOException e) {
                context.sendMessage(Message.raw("РќРµ СѓРґР°Р»РѕСЃСЊ РґРѕР±Р°РІРёС‚СЊ С‚РѕС‡РєСѓ СЃСѓРЅРґСѓРєР°: " + e.getMessage()));
            }
        }
    }

    private static final class ChestRemoveAliasCommand extends AbstractPlayerCommand {
        private final BankDefenseRepository repository;
        private final DefaultArg<Double> radiusArg = this.withDefaultArg("radius", "Р Р°РґРёСѓСЃ", ArgTypes.DOUBLE, 3.0, "3");

        private ChestRemoveAliasCommand(BankDefenseRepository repository) {
            super("chestremove", "РЈРґР°Р»РёС‚СЊ Р±Р»РёР¶Р°Р№С€СѓСЋ С‚РѕС‡РєСѓ СЃСѓРЅРґСѓРєР°");
            this.repository = repository;
        }

        @Override
        protected void execute(CommandContext context, Store<EntityStore> store, Ref<EntityStore> ref, PlayerRef playerRef, World world) {
            try {
                MapConfig mapConfig = this.repository.loadMapConfig(world);
                if (mapConfig.chestSpawnPoints.isEmpty()) {
                    context.sendMessage(Message.raw("РўРѕС‡РєРё СЃСѓРЅРґСѓРєРѕРІ РЅРµ Р·Р°РґР°РЅС‹."));
                    return;
                }
                Vec3i position = currentBlockPosition(store, ref);
                double radius = Math.max(0.5, this.radiusArg.get(context));
                Vec3i removed = nearestPoint(mapConfig.chestSpawnPoints, position, radius);
                if (removed == null) {
                    context.sendMessage(Message.raw("Р СЏРґРѕРј РЅРµС‚ С‚РѕС‡РєРё СЃСѓРЅРґСѓРєР° РІ СЂР°РґРёСѓСЃРµ " + radius + "."));
                    return;
                }
                mapConfig.chestSpawnPoints.removeIf(point -> point != null && point.distanceSquaredTo(removed) == 0.0);
                this.repository.saveMapConfig(world, mapConfig);
                context.sendMessage(Message.raw("РЈРґР°Р»РµРЅР° С‚РѕС‡РєР° СЃСѓРЅРґСѓРєР° РІ " + removed.toShortString() + "."));
            } catch (IOException e) {
                context.sendMessage(Message.raw("РќРµ СѓРґР°Р»РѕСЃСЊ СѓРґР°Р»РёС‚СЊ С‚РѕС‡РєСѓ СЃСѓРЅРґСѓРєР°: " + e.getMessage()));
            }
        }
    }

    private static final class ChestClearAliasCommand extends AbstractWorldCommand {
        private final BankDefenseRepository repository;

        private ChestClearAliasCommand(BankDefenseRepository repository) {
            super("chestclear", "РћС‡РёСЃС‚РёС‚СЊ РІСЃРµ С‚РѕС‡РєРё СЃСѓРЅРґСѓРєРѕРІ");
            this.repository = repository;
        }

        @Override
        protected void execute(CommandContext context, World world, Store<EntityStore> store) {
            try {
                MapConfig mapConfig = this.repository.loadMapConfig(world);
                mapConfig.chestSpawnPoints.clear();
                this.repository.saveMapConfig(world, mapConfig);
                context.sendMessage(Message.raw("Р’СЃРµ С‚РѕС‡РєРё СЃСѓРЅРґСѓРєРѕРІ РѕС‡РёС‰РµРЅС‹."));
            } catch (IOException e) {
                context.sendMessage(Message.raw("РќРµ СѓРґР°Р»РѕСЃСЊ РѕС‡РёСЃС‚РёС‚СЊ С‚РѕС‡РєРё СЃСѓРЅРґСѓРєРѕРІ: " + e.getMessage()));
            }
        }
    }

    private static final class ChestListAliasCommand extends AbstractWorldCommand {
        private final BankDefenseRepository repository;

        private ChestListAliasCommand(BankDefenseRepository repository) {
            super("chestlist", "РџРѕРєР°Р·Р°С‚СЊ С‚РѕС‡РєРё СЃРїР°РІРЅР° СЃСѓРЅРґСѓРєРѕРІ");
            this.repository = repository;
        }

        @Override
        protected void execute(CommandContext context, World world, Store<EntityStore> store) {
            try {
                MapConfig mapConfig = this.repository.loadMapConfig(world);
                if (mapConfig.chestSpawnPoints.isEmpty()) {
                    context.sendMessage(Message.raw("РўРѕС‡РєРё СЃСѓРЅРґСѓРєРѕРІ РЅРµ Р·Р°РґР°РЅС‹."));
                    return;
                }
                StringBuilder builder = new StringBuilder("РўРѕС‡РµРє СЃСѓРЅРґСѓРєРѕРІ: ").append(mapConfig.chestSpawnPoints.size());
                for (int i = 0; i < mapConfig.chestSpawnPoints.size(); i++) {
                    Vec3i point = mapConfig.chestSpawnPoints.get(i);
                    builder.append('\n').append(i + 1).append(". ").append(point == null ? "РЅРµС‚ РїРѕР·РёС†РёРё" : point.toShortString());
                }
                context.sendMessage(Message.raw(builder.toString()));
            } catch (IOException e) {
                context.sendMessage(Message.raw("РќРµ СѓРґР°Р»РѕСЃСЊ РїРѕРєР°Р·Р°С‚СЊ С‚РѕС‡РєРё СЃСѓРЅРґСѓРєРѕРІ: " + e.getMessage()));
            }
        }
    }

    private static final class DuoChestAddAliasCommand extends AbstractPlayerCommand {
        private final BankDefenseRepository repository;

        private DuoChestAddAliasCommand(BankDefenseRepository repository) {
            super("duochestadd", "Добавить точку duo-сундука");
            this.repository = repository;
        }

        @Override
        protected void execute(CommandContext context, Store<EntityStore> store, Ref<EntityStore> ref, PlayerRef playerRef, World world) {
            try {
                MapConfig mapConfig = this.repository.loadMapConfig(world);
                Vec3i position = currentBlockPosition(store, ref);
                for (Vec3i existing : mapConfig.duoChestSpawnPoints) {
                    if (existing != null && existing.distanceSquaredTo(position) == 0.0) {
                        context.sendMessage(Message.raw("Точка duo-сундука уже существует в " + position.toShortString() + "."));
                        return;
                    }
                }
                mapConfig.duoChestSpawnPoints.add(position);
                this.repository.saveMapConfig(world, mapConfig);
                context.sendMessage(Message.raw("Добавлена точка duo-сундука #" + mapConfig.duoChestSpawnPoints.size() + " в " + position.toShortString() + "."));
            } catch (IOException e) {
                context.sendMessage(Message.raw("Не удалось добавить точку duo-сундука: " + e.getMessage()));
            }
        }
    }

    private static final class DuoChestRemoveAliasCommand extends AbstractPlayerCommand {
        private final BankDefenseRepository repository;
        private final DefaultArg<Double> radiusArg = this.withDefaultArg("radius", "Радиус", ArgTypes.DOUBLE, 3.0, "3");

        private DuoChestRemoveAliasCommand(BankDefenseRepository repository) {
            super("duochestremove", "Удалить ближайшую точку duo-сундука");
            this.repository = repository;
        }

        @Override
        protected void execute(CommandContext context, Store<EntityStore> store, Ref<EntityStore> ref, PlayerRef playerRef, World world) {
            try {
                MapConfig mapConfig = this.repository.loadMapConfig(world);
                if (mapConfig.duoChestSpawnPoints.isEmpty()) {
                    context.sendMessage(Message.raw("Точки duo-сундуков не заданы."));
                    return;
                }
                Vec3i position = currentBlockPosition(store, ref);
                double radius = Math.max(0.5, this.radiusArg.get(context));
                Vec3i removed = nearestPoint(mapConfig.duoChestSpawnPoints, position, radius);
                if (removed == null) {
                    context.sendMessage(Message.raw("Рядом нет точки duo-сундука в радиусе " + radius + "."));
                    return;
                }
                mapConfig.duoChestSpawnPoints.removeIf(point -> point != null && point.distanceSquaredTo(removed) == 0.0);
                this.repository.saveMapConfig(world, mapConfig);
                context.sendMessage(Message.raw("Удалена точка duo-сундука в " + removed.toShortString() + "."));
            } catch (IOException e) {
                context.sendMessage(Message.raw("Не удалось удалить точку duo-сундука: " + e.getMessage()));
            }
        }
    }

    private static final class DuoChestClearAliasCommand extends AbstractWorldCommand {
        private final BankDefenseRepository repository;

        private DuoChestClearAliasCommand(BankDefenseRepository repository) {
            super("duochestclear", "Очистить все точки duo-сундуков");
            this.repository = repository;
        }

        @Override
        protected void execute(CommandContext context, World world, Store<EntityStore> store) {
            try {
                MapConfig mapConfig = this.repository.loadMapConfig(world);
                mapConfig.duoChestSpawnPoints.clear();
                this.repository.saveMapConfig(world, mapConfig);
                context.sendMessage(Message.raw("Все точки duo-сундуков очищены."));
            } catch (IOException e) {
                context.sendMessage(Message.raw("Не удалось очистить точки duo-сундуков: " + e.getMessage()));
            }
        }
    }

    private static final class DuoChestListAliasCommand extends AbstractWorldCommand {
        private final BankDefenseRepository repository;

        private DuoChestListAliasCommand(BankDefenseRepository repository) {
            super("duochestlist", "Показать точки duo-сундуков");
            this.repository = repository;
        }

        @Override
        protected void execute(CommandContext context, World world, Store<EntityStore> store) {
            try {
                MapConfig mapConfig = this.repository.loadMapConfig(world);
                if (mapConfig.duoChestSpawnPoints.isEmpty()) {
                    context.sendMessage(Message.raw("Точки duo-сундуков не заданы."));
                    return;
                }
                StringBuilder builder = new StringBuilder("Точек duo-сундуков: ").append(mapConfig.duoChestSpawnPoints.size());
                for (int i = 0; i < mapConfig.duoChestSpawnPoints.size(); i++) {
                    Vec3i point = mapConfig.duoChestSpawnPoints.get(i);
                    builder.append('\n').append(i + 1).append(". ").append(point == null ? "нет позиции" : point.toShortString());
                }
                context.sendMessage(Message.raw(builder.toString()));
            } catch (IOException e) {
                context.sendMessage(Message.raw("Не удалось показать точки duo-сундуков: " + e.getMessage()));
            }
        }
    }
}

