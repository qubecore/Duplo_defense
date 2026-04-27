package com.qubecore.bankdefense.command;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.protocol.MovementSettings;
import com.hypixel.hytale.server.core.Message;
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
import java.util.List;
import java.util.Locale;

public final class BankDefenseCommand extends AbstractCommandCollection {
    public BankDefenseCommand(BankDefenseRepository repository, BankDefenseRuntime runtime) {
        super("bankdefense", "РРЅСЃС‚СЂСѓРјРµРЅС‚С‹ QubeCore: DUPLO Defense");
        this.addSubCommand(new StatusCommand(repository, runtime));
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
        this.addSubCommand(new FixedMarkerSetCommand(repository, "spawn_a", "markspawna", "РџРѕСЃС‚Р°РІРёС‚СЊ spawn A"));
        this.addSubCommand(new FixedMarkerSetCommand(repository, "spawn_b", "markspawnb", "РџРѕСЃС‚Р°РІРёС‚СЊ spawn B"));
        this.addSubCommand(new FixedMarkerSetCommand(repository, "spawn_c", "markspawnc", "РџРѕСЃС‚Р°РІРёС‚СЊ spawn C"));
        this.addSubCommand(new FixedMarkerSetCommand(repository, "bank", "markbank", "РџРѕСЃС‚Р°РІРёС‚СЊ РјР°СЂРєРµСЂ Р±Р°РЅРєР°"));
        this.addSubCommand(new FixedMarkerSetCommand(repository, "vault", "markvault", "РџРѕСЃС‚Р°РІРёС‚СЊ РјР°СЂРєРµСЂ СЏРґСЂР°"));
        this.addSubCommand(new FixedMarkerSetCommand(repository, "playerstart", "markplayerstart", "РџРѕСЃС‚Р°РІРёС‚СЊ СЃС‚Р°СЂС‚ РёРіСЂРѕРєР°"));
        this.addSubCommand(new FixedMarkerSetCommand(repository, "control", "markcontrol", "РџРѕСЃС‚Р°РІРёС‚СЊ control NPC"));
        this.addSubCommand(new FixedMarkerSetCommand(repository, "vendor", "markvendor", "РџРѕСЃС‚Р°РІРёС‚СЊ vendor NPC"));
        this.addSubCommand(new FixedMarkerSetCommand(repository, "mode", "markmode", "РџРѕСЃС‚Р°РІРёС‚СЊ NPC РІС‹Р±РѕСЂР° СЂРµР¶РёРјР°"));
        this.addSubCommand(new FixedMarkerClearCommand(repository, "spawn_a", "clearspawna", "РћС‡РёСЃС‚РёС‚СЊ spawn A"));
        this.addSubCommand(new FixedMarkerClearCommand(repository, "spawn_b", "clearspawnb", "РћС‡РёСЃС‚РёС‚СЊ spawn B"));
        this.addSubCommand(new FixedMarkerClearCommand(repository, "spawn_c", "clearspawnc", "РћС‡РёСЃС‚РёС‚СЊ spawn C"));
        this.addSubCommand(new FixedMarkerClearCommand(repository, "bank", "clearbank", "РћС‡РёСЃС‚РёС‚СЊ РјР°СЂРєРµСЂ Р±Р°РЅРєР°"));
        this.addSubCommand(new FixedMarkerClearCommand(repository, "vault", "clearvault", "РћС‡РёСЃС‚РёС‚СЊ РјР°СЂРєРµСЂ СЏРґСЂР°"));
        this.addSubCommand(new FixedMarkerClearCommand(repository, "playerstart", "clearplayerstart", "РћС‡РёСЃС‚РёС‚СЊ СЃС‚Р°СЂС‚ РёРіСЂРѕРєР°"));
        this.addSubCommand(new FixedMarkerClearCommand(repository, "control", "clearcontrol", "РћС‡РёСЃС‚РёС‚СЊ control NPC"));
        this.addSubCommand(new FixedMarkerClearCommand(repository, "vendor", "clearvendor", "РћС‡РёСЃС‚РёС‚СЊ vendor NPC"));
        this.addSubCommand(new FixedMarkerClearCommand(repository, "mode", "clearmode", "РћС‡РёСЃС‚РёС‚СЊ NPC РІС‹Р±РѕСЂР° СЂРµР¶РёРјР°"));
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
        this.addSubCommand(new SlotAddAliasCommand(repository));
        this.addSubCommand(new SlotAddTypeAliasCommand(repository, "standard"));
        this.addSubCommand(new SlotAddTypeAliasCommand(repository, "super"));
        this.addSubCommand(new SlotAddTypeAliasCommand(repository, "trap"));
        this.addSubCommand(new SlotAddTypeAliasCommand(repository, "tutorial"));
        this.addSubCommand(new SlotAddTypeAliasCommand(repository, "tutorial_super"));
        this.addSubCommand(new SlotAddTypeAliasCommand(repository, "tutorial_trap"));
        this.addSubCommand(new SlotRemoveAliasCommand(repository));
        this.addSubCommand(new SlotClearAliasCommand(repository));
        this.addSubCommand(new SlotClearTypeAliasCommand(repository, "standard"));
        this.addSubCommand(new SlotClearTypeAliasCommand(repository, "super"));
        this.addSubCommand(new SlotClearTypeAliasCommand(repository, "trap"));
        this.addSubCommand(new SlotClearTypeAliasCommand(repository, "tutorial"));
        this.addSubCommand(new SlotClearTypeAliasCommand(repository, "tutorial_super"));
        this.addSubCommand(new SlotClearTypeAliasCommand(repository, "tutorial_trap"));
        this.addSubCommand(new SlotClearTypeAliasCommand(repository, "all"));
        this.addSubCommand(new SlotListAliasCommand(repository));
        this.addSubCommand(new ChestAddAliasCommand(repository));
        this.addSubCommand(new ChestRemoveAliasCommand(repository));
        this.addSubCommand(new ChestClearAliasCommand(repository));
        this.addSubCommand(new ChestListAliasCommand(repository));
    }

    private static Vec3i currentBlockPosition(Store<EntityStore> store, Ref<EntityStore> ref) {
        TransformComponent transformComponent = store.getComponent(ref, TransformComponent.getComponentType());
        if (transformComponent == null) {
            throw new IllegalStateException("РўСЂР°РЅСЃС„РѕСЂРјР°С†РёСЏ РёРіСЂРѕРєР° РЅРµРґРѕСЃС‚СѓРїРЅР°.");
        }
        Vector3d position = transformComponent.getPosition();
        return Vec3i.ofFloor(position.x, position.y, position.z);
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
            case "statistics", "stats", "statnpc" -> "statistics";
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

    private static void clearAllRoutes(MapConfig mapConfig) {
        mapConfig.routePoints.clear();
        mapConfig.routePointsA.clear();
        mapConfig.routePointsB.clear();
        mapConfig.routePointsC.clear();
        mapConfig.tutorialRoutePointsA.clear();
        mapConfig.tutorialRoutePointsB.clear();
        mapConfig.tutorialRoutePointsC.clear();
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
                    .append(" | Duo: ").append(markerSummary(snapshot.map.duoPoint))
                    .append(" | Stats: ").append(markerSummary(snapshot.map.statisticsPoint)).append('\n');
                message.append("РњР°СЂС€СЂСѓС‚С‹: A=").append(snapshot.map.routePointsA.size())
                    .append(", B=").append(snapshot.map.routePointsB.size())
                    .append(", C=").append(snapshot.map.routePointsC.size())
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
                mapConfig.previousDuoPoint = mapConfig.duoPoint;
                mapConfig.duoPoint = position;
                mapConfig.duoYaw = yaw;
                this.repository.saveMapConfig(world, mapConfig);
                context.sendMessage(Message.raw("РњР°СЂРєРµСЂ duo СѓСЃС‚Р°РЅРѕРІР»РµРЅ РІ " + position.toShortString() + "."));
            } catch (IOException e) {
                context.sendMessage(Message.raw("РќРµ СѓРґР°Р»РѕСЃСЊ РїРѕСЃС‚Р°РІРёС‚СЊ duo NPC: " + e.getMessage()));
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
                mapConfig.previousDuoPoint = mapConfig.duoPoint;
                mapConfig.duoPoint = null;
                mapConfig.duoYaw = null;
                this.repository.saveMapConfig(world, mapConfig);
                context.sendMessage(Message.raw("РњР°СЂРєРµСЂ duo РѕС‡РёС‰РµРЅ."));
            } catch (IOException e) {
                context.sendMessage(Message.raw("РќРµ СѓРґР°Р»РѕСЃСЊ РѕС‡РёСЃС‚РёС‚СЊ duo NPC: " + e.getMessage()));
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
                    case "statistics" -> {
                        mapConfig.previousStatisticsPoint = mapConfig.statisticsPoint;
                        mapConfig.statisticsPoint = position;
                        mapConfig.statisticsYaw = yaw;
                    }
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
                    case "statistics" -> {
                        mapConfig.previousStatisticsPoint = mapConfig.statisticsPoint;
                        mapConfig.statisticsPoint = null;
                        mapConfig.statisticsYaw = null;
                    }
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
                    case "statistics" -> {
                        mapConfig.previousStatisticsPoint = mapConfig.statisticsPoint;
                        mapConfig.statisticsPoint = position;
                        mapConfig.statisticsYaw = yaw;
                    }
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
                        context.sendMessage(Message.raw("РќРµРёР·РІРµСЃС‚РЅС‹Р№ РјР°СЂРєРµСЂ: " + this.markerName));
                        return;
                    }
                }
                this.repository.saveMapConfig(world, mapConfig);
                context.sendMessage(Message.raw("РњР°СЂРєРµСЂ '" + this.markerName + "' СѓСЃС‚Р°РЅРѕРІР»РµРЅ РІ " + position.toShortString() + "."));
            } catch (IOException e) {
                context.sendMessage(Message.raw("РќРµ СѓРґР°Р»РѕСЃСЊ РїРѕСЃС‚Р°РІРёС‚СЊ РјР°СЂРєРµСЂ: " + e.getMessage()));
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
                    case "statistics" -> {
                        mapConfig.previousStatisticsPoint = mapConfig.statisticsPoint;
                        mapConfig.statisticsPoint = null;
                        mapConfig.statisticsYaw = null;
                    }
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
                        context.sendMessage(Message.raw("РќРµРёР·РІРµСЃС‚РЅС‹Р№ РјР°СЂРєРµСЂ: " + this.markerName));
                        return;
                    }
                }
                this.repository.saveMapConfig(world, mapConfig);
                context.sendMessage(Message.raw("РњР°СЂРєРµСЂ '" + this.markerName + "' РѕС‡РёС‰РµРЅ."));
            } catch (IOException e) {
                context.sendMessage(Message.raw("РќРµ СѓРґР°Р»РѕСЃСЊ РѕС‡РёСЃС‚РёС‚СЊ РјР°СЂРєРµСЂ: " + e.getMessage()));
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
                    slotsConfig.slots.clear();
                    this.repository.saveBuildSlotsConfig(world, slotsConfig);
                    context.sendMessage(Message.raw("Р’СЃРµ СЃР»РѕС‚С‹ РѕС‡РёС‰РµРЅС‹."));
                    return;
                }
                slotsConfig.slots.removeIf(slot -> slotType.equals(slotTypeLabel(slot)));
                this.repository.saveBuildSlotsConfig(world, slotsConfig);
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
                        .append(" [").append(slotTypeLabel(slot)).append("] @ ")
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
                context.sendMessage(Message.raw("Р”РѕР±Р°РІР»РµРЅ " + this.slotType + "-СЃР»РѕС‚ '" + slot.id + "' РІ " + position.toShortString() + "."));
            } catch (IOException e) {
                context.sendMessage(Message.raw("РќРµ СѓРґР°Р»РѕСЃСЊ РґРѕР±Р°РІРёС‚СЊ СЃР»РѕС‚: " + e.getMessage()));
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
                BuildSlot nearest = null;
                double bestDistance = (double) radius * radius;
                for (BuildSlot slot : slotsConfig.slots) {
                    if (slot.position == null) {
                        continue;
                    }
                    double distance = slot.position.distanceSquaredTo(position);
                    if (distance <= bestDistance) {
                        bestDistance = distance;
                        nearest = slot;
                    }
                }
                if (nearest == null) {
                    context.sendMessage(Message.raw("Р СЏРґРѕРј РЅРµС‚ СЃР»РѕС‚Р° РІ СЂР°РґРёСѓСЃРµ " + radius + "."));
                    return;
                }
                slotsConfig.slots.remove(nearest);
                this.repository.saveBuildSlotsConfig(world, slotsConfig);
                context.sendMessage(Message.raw("РЈРґР°Р»С‘РЅ СЃР»РѕС‚ '" + nearest.id + "'."));
            } catch (IOException e) {
                context.sendMessage(Message.raw("РќРµ СѓРґР°Р»РѕСЃСЊ СѓРґР°Р»РёС‚СЊ СЃР»РѕС‚: " + e.getMessage()));
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
                    slotsConfig.slots.clear();
                    this.repository.saveBuildSlotsConfig(world, slotsConfig);
                    context.sendMessage(Message.raw("Р’СЃРµ СЃР»РѕС‚С‹ РѕС‡РёС‰РµРЅС‹."));
                    return;
                }
                slotsConfig.slots.removeIf(slot -> slotType.equals(slotTypeLabel(slot)));
                this.repository.saveBuildSlotsConfig(world, slotsConfig);
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
                    slotsConfig.slots.clear();
                    this.repository.saveBuildSlotsConfig(world, slotsConfig);
                    context.sendMessage(Message.raw("Р’СЃРµ СЃР»РѕС‚С‹ РѕС‡РёС‰РµРЅС‹."));
                    return;
                }
                slotsConfig.slots.removeIf(slot -> this.slotType.equals(slotTypeLabel(slot)));
                this.repository.saveBuildSlotsConfig(world, slotsConfig);
                context.sendMessage(Message.raw("РћС‡РёС‰РµРЅС‹ СЃР»РѕС‚С‹ С‚РёРїР° " + this.slotType + "."));
            } catch (IOException e) {
                context.sendMessage(Message.raw("РќРµ СѓРґР°Р»РѕСЃСЊ РѕС‡РёСЃС‚РёС‚СЊ СЃР»РѕС‚С‹: " + e.getMessage()));
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
                        .append(" [").append(slotTypeLabel(slot)).append("] @ ")
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
}
