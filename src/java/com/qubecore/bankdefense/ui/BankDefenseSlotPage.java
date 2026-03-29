package com.qubecore.bankdefense.ui;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime;
import com.hypixel.hytale.protocol.packets.interface_.CustomUIEventBindingType;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.pages.InteractiveCustomUIPage;
import com.hypixel.hytale.server.core.ui.builder.EventData;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.ui.builder.UIEventBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.qubecore.bankdefense.runtime.BankDefenseRuntime;
import com.qubecore.bankdefense.runtime.BankDefenseRuntime.ActionResult;
import com.qubecore.bankdefense.runtime.BankDefenseRuntime.ModuleButtonState;
import com.qubecore.bankdefense.runtime.BankDefenseRuntime.SlotUiState;
import com.qubecore.bankdefense.runtime.BankDefenseRuntime.TowerButtonState;
import com.qubecore.bankdefense.runtime.support.BankDefenseLocalization;
import java.io.IOException;
import java.util.List;
import javax.annotation.Nonnull;

public final class BankDefenseSlotPage extends InteractiveCustomUIPage<BankDefenseSlotPage.EventDataPayload> {
    private static final String UI_PAGE = "Pages/BankDefenseSlotPage.ui";
    private static final List<String> TOWER_BUTTON_SELECTORS = List.of(
        "#GuardPost",
        "#SniperDesk",
        "#RapidSecurity",
        "#FreezeGate",
        "#ShockRelay",
        "#ArmorDrill"
    );
    private static final List<String> TOWER_CARD_SELECTORS = List.of(
        "#GuardPostCard",
        "#SniperDeskCard",
        "#RapidSecurityCard",
        "#FreezeGateCard",
        "#ShockRelayCard",
        "#ArmorDrillCard"
    );
    private static final List<String> TOWER_TITLE_SELECTORS = List.of(
        "#GuardPostTitle",
        "#SniperDeskTitle",
        "#RapidSecurityTitle",
        "#FreezeGateTitle",
        "#ShockRelayTitle",
        "#ArmorDrillTitle"
    );
    private static final List<String> TOWER_ICON_SELECTORS = List.of(
        "#GuardPostTowerImage",
        "#SniperDeskTowerImage",
        "#RapidSecurityTowerImage"
    );
    private static final List<String> TRAP_ICON_SELECTORS = List.of(
        "#GuardPostTrapImage",
        "#SniperDeskTrapImage",
        "#RapidSecurityTrapImage"
    );
    private static final List<String> CURRENT_TOWER_IMAGE_SELECTORS = List.of(
        "#CurrentTowerPlaceholder",
        "#CurrentTowerGuardPostImage",
        "#CurrentTowerSniperDeskImage",
        "#CurrentTowerRapidSecurityImage",
        "#CurrentTowerFreezeGateImage",
        "#CurrentTowerShockRelayImage",
        "#CurrentTowerArmorDrillImage",
        "#CurrentTowerRootSnareImage",
        "#CurrentTowerSporeMineImage",
        "#CurrentTowerFrostSealImage"
    );
    private static final List<String> MODULE_BUTTON_SELECTORS = List.of(
        "#Module01",
        "#Module02",
        "#Module03",
        "#Module04",
        "#Module05",
        "#Module06",
        "#Module07",
        "#Module08",
        "#Module09"
    );
    private static final List<String> MODULE_CARD_SELECTORS = List.of(
        "#ModuleCard01",
        "#ModuleCard02",
        "#ModuleCard03",
        "#ModuleCard04",
        "#ModuleCard05",
        "#ModuleCard06",
        "#ModuleCard07",
        "#ModuleCard08",
        "#ModuleCard09"
    );

    private final BankDefenseRuntime runtime;
    private final PlayerRef viewerRef;
    private final String slotId;
    private String hint = "";

    public BankDefenseSlotPage(PlayerRef playerRef, BankDefenseRuntime runtime, String slotId) {
        super(playerRef, CustomPageLifetime.CanDismiss, EventDataPayload.CODEC);
        this.runtime = runtime;
        this.viewerRef = playerRef;
        this.slotId = slotId;
    }

    @Override
    public void build(@Nonnull Ref<EntityStore> ref, @Nonnull UICommandBuilder commandBuilder, @Nonnull UIEventBuilder eventBuilder, @Nonnull Store<EntityStore> store) {
        commandBuilder.append(UI_PAGE);
        this.bindEvents(eventBuilder);
        this.syncState(ref, store, commandBuilder);
    }

    @Override
    public void handleDataEvent(@Nonnull Ref<EntityStore> ref, @Nonnull Store<EntityStore> store, @Nonnull EventDataPayload data) {
        if (data.action == null || data.action.isBlank()) {
            return;
        }

        Player player = store.getComponent(ref, Player.getComponentType());
        if (player == null || player.getWorld() == null) {
            return;
        }

        UICommandBuilder commandBuilder = new UICommandBuilder();
        UIEventBuilder eventBuilder = new UIEventBuilder();
        this.bindEvents(eventBuilder);

        try {
            ActionResult result = this.handleAction(player.getWorld(), data.action);
            this.hint = result.message;
            this.runtime.playUiActionFeedback(player.getWorld(), this.viewerRef, result);
            if (result.message != null && !result.message.isBlank()) {
                player.sendMessage(Message.raw(BankDefenseLocalization.translateFreeform(this.viewerRef, result.message)));
            }
        } catch (IOException e) {
            this.hint = BankDefenseLocalization.tr(this.viewerRef, "page.slot.error", e.getMessage());
            this.runtime.playUiErrorSound(player.getWorld(), this.viewerRef);
            player.sendMessage(Message.raw(BankDefenseLocalization.translateFreeform(this.viewerRef, this.hint)));
        }

        this.syncState(ref, store, commandBuilder);
        this.sendUpdate(commandBuilder, eventBuilder, false);
    }

    private void bindEvents(UIEventBuilder eventBuilder) {
        for (int index = 0; index < TOWER_BUTTON_SELECTORS.size(); index++) {
            this.bindAction(eventBuilder, TOWER_BUTTON_SELECTORS.get(index), "build_index:" + index);
        }
        this.bindAction(eventBuilder, "#UpgradeTower", "upgrade");
        this.bindAction(eventBuilder, "#SellTower", "sell");
        this.bindAction(eventBuilder, "#RemoveModule", "module:remove");
        this.bindAction(eventBuilder, "#Module01", "module:overload");
        this.bindAction(eventBuilder, "#Module02", "module:heavy_caliber");
        this.bindAction(eventBuilder, "#Module03", "module:long_optics");
        this.bindAction(eventBuilder, "#Module04", "module:cryo_capsule");
        this.bindAction(eventBuilder, "#Module05", "module:fragment_charge");
        this.bindAction(eventBuilder, "#Module06", "module:armor_scanner");
        this.bindAction(eventBuilder, "#Module07", "module:arc_splitter");
        this.bindAction(eventBuilder, "#Module08", "module:dual_camera");
        this.bindAction(eventBuilder, "#Module09", "module:reserve_capacitor");
    }

    private void bindAction(UIEventBuilder eventBuilder, String selector, String action) {
        eventBuilder.addEventBinding(CustomUIEventBindingType.Activating, selector, EventData.of("Action", action), false);
    }

    private void syncState(Ref<EntityStore> ref, Store<EntityStore> store, UICommandBuilder commandBuilder) {
        Player player = store.getComponent(ref, Player.getComponentType());
        World world = player == null ? null : player.getWorld();
        SlotUiState state = null;
        if (world != null) {
            try {
                state = this.runtime.getSlotUiState(world, this.viewerRef, this.slotId);
            } catch (IOException e) {
                this.hint = BankDefenseLocalization.tr(this.viewerRef, "page.slot.load_error", e.getMessage());
            }
        }

        boolean slotReady = state != null;
        boolean selectionVisible = slotReady && !state.towerPresent;
        boolean placedVisible = slotReady && state.towerPresent;
        boolean modulesVisible = placedVisible && state.modulesAllowed;

        commandBuilder.set("#TitleLabel.Text", slotReady ? this.slotHeader(state) : BankDefenseLocalization.tr(this.viewerRef, "page.slot.title"));
        commandBuilder.set(
            "#Subtitle.Text",
            slotReady
                ? (state.position == null
                    ? BankDefenseLocalization.tr(this.viewerRef, "label.position.none")
                    : BankDefenseLocalization.tr(this.viewerRef, "label.position", state.position.toShortString()))
                : BankDefenseLocalization.tr(this.viewerRef, "page.slot.info_unavailable")
        );
        commandBuilder.set("#Wave.Text", slotReady ? this.waveText(state) : BankDefenseLocalization.tr(this.viewerRef, "label.wave.none"));
        commandBuilder.set("#Balance.Text", slotReady ? BankDefenseLocalization.tr(this.viewerRef, "label.funds", state.currency) : BankDefenseLocalization.tr(this.viewerRef, "label.funds.none"));
        commandBuilder.set("#Bank.Text", slotReady ? BankDefenseLocalization.tr(this.viewerRef, "label.bank", state.bankHp) : BankDefenseLocalization.tr(this.viewerRef, "label.bank.none"));
        commandBuilder.set(
            "#SlotState.Text",
            slotReady
                ? BankDefenseLocalization.translateFreeform(this.viewerRef, safeText(state.slotSummary, this.emptySlotSummary(state)))
                : BankDefenseLocalization.tr(this.viewerRef, "page.slot.unavailable")
        );
        commandBuilder.set("#RewardHint.Text", slotReady ? BankDefenseLocalization.translateFreeform(this.viewerRef, safeText(state.pendingRewardSummary, "")) : "");
        commandBuilder.set(
            "#Hint.Text",
            BankDefenseLocalization.translateFreeform(
                this.viewerRef,
                this.hint == null || this.hint.isBlank() ? BankDefenseLocalization.tr(this.viewerRef, "page.slot.hint") : this.hint
            )
        );

        commandBuilder.set("#SelectionPanel.Visible", selectionVisible);
        commandBuilder.set("#PlacedPanel.Visible", placedVisible);
        commandBuilder.set("#ModulesPanel.Visible", modulesVisible);
        commandBuilder.set("#ModuleUnavailable.Visible", placedVisible && !modulesVisible);

        commandBuilder.set(
            "#BuildHeader.Text",
            slotReady
                ? (state.superSlot
                    ? BankDefenseLocalization.tr(this.viewerRef, "page.slot.build_header.super")
                    : state.trapSlot
                        ? BankDefenseLocalization.tr(this.viewerRef, "page.slot.build_header.traps")
                        : BankDefenseLocalization.tr(this.viewerRef, "page.slot.build_header.towers"))
                : BankDefenseLocalization.tr(this.viewerRef, "page.slot.build_header.towers")
        );

        this.applyTowerCards(commandBuilder, state);
        this.applyTrapSelectionImages(commandBuilder, state);
        this.applyCurrentTower(commandBuilder, state);
        this.applyCurrentTowerImage(commandBuilder, state);
        this.applyModuleButtons(commandBuilder, state);

        commandBuilder.set(
            "#UpgradeTower.Text",
            slotReady
                ? BankDefenseLocalization.translateFreeform(this.viewerRef, safeText(state.upgradeLabel, BankDefenseLocalization.tr(this.viewerRef, "page.slot.upgrade")))
                : BankDefenseLocalization.tr(this.viewerRef, "page.slot.upgrade")
        );
        commandBuilder.set(
            "#SellTower.Text",
            slotReady
                ? BankDefenseLocalization.translateFreeform(this.viewerRef, safeText(state.sellLabel, BankDefenseLocalization.tr(this.viewerRef, "page.slot.sell")))
                : BankDefenseLocalization.tr(this.viewerRef, "page.slot.sell")
        );
        commandBuilder.set(
            "#RemoveModule.Text",
            slotReady
                ? BankDefenseLocalization.translateFreeform(this.viewerRef, safeText(state.removeModuleLabel, BankDefenseLocalization.tr(this.viewerRef, "page.slot.module.remove")))
                : BankDefenseLocalization.tr(this.viewerRef, "page.slot.module.remove")
        );
        commandBuilder.set("#ModulesHeader.Text", BankDefenseLocalization.tr(this.viewerRef, "page.slot.modules_header"));
        commandBuilder.set("#ModuleUnavailable.Text", BankDefenseLocalization.tr(this.viewerRef, "page.slot.module.unavailable"));
    }

    private String slotHeader(SlotUiState state) {
        if (!state.superSlot && !state.trapSlot) {
            if (!state.towerPresent) {
                return BankDefenseLocalization.tr(this.viewerRef, "page.slot.build_tower");
            }
            String towerName = BankDefenseLocalization.towerDisplayName(this.viewerRef, state.towerId, state.towerName);
            return BankDefenseLocalization.tr(this.viewerRef, "page.slot.header.current", towerName);
        }
        if (state.superSlot) {
            return BankDefenseLocalization.tr(this.viewerRef, "page.slot.super");
        }
        if (state.trapSlot) {
            return BankDefenseLocalization.tr(this.viewerRef, "page.slot.trap");
        }
        return BankDefenseLocalization.tr(this.viewerRef, "page.slot.title");
    }

    private String emptySlotSummary(SlotUiState state) {
        if (state == null) {
            return BankDefenseLocalization.tr(this.viewerRef, "page.slot.empty");
        }
        if (state.superSlot) {
            return BankDefenseLocalization.tr(this.viewerRef, "page.slot.super_empty");
        }
        if (state.trapSlot) {
            return BankDefenseLocalization.tr(this.viewerRef, "page.slot.trap_empty");
        }
        return BankDefenseLocalization.tr(this.viewerRef, "page.slot.empty");
    }

    private String waveText(SlotUiState state) {
        String value = BankDefenseLocalization.tr(this.viewerRef, "label.wave", state.currentWave);
        if (state.preparationRemainingSeconds > 0.0) {
            value += " | " + BankDefenseLocalization.tr(this.viewerRef, "hud.preparation_s", (int)Math.ceil(state.preparationRemainingSeconds));
        }
        return value;
    }

    private void applyTowerCards(UICommandBuilder commandBuilder, SlotUiState state) {
        for (int index = 0; index < TOWER_CARD_SELECTORS.size(); index++) {
            boolean visible = state != null && index < state.towerButtons.size() && !state.towerPresent;
            commandBuilder.set(TOWER_CARD_SELECTORS.get(index) + ".Visible", visible);
            if (!visible) {
                continue;
            }

            TowerButtonState button = state.towerButtons.get(index);
            commandBuilder.set(
                TOWER_TITLE_SELECTORS.get(index) + ".Text",
                BankDefenseLocalization.towerDisplayName(this.viewerRef, button.towerId, safeText(button.displayName, this.shortTowerName(button.towerId, button.shortLabel)))
            );
            commandBuilder.set(TOWER_BUTTON_SELECTORS.get(index) + ".Text", this.buildTowerActionLabel(button));
        }
    }

    private String buildTowerActionLabel(TowerButtonState button) {
        if (!button.unlocked) {
            return BankDefenseLocalization.tr(this.viewerRef, "page.slot.unlock_in_progression");
        }
        if (button.cost > 0) {
            return BankDefenseLocalization.tr(this.viewerRef, "page.slot.place_for", button.cost);
        }
        return BankDefenseLocalization.tr(this.viewerRef, "page.slot.place");
    }

    private void applyCurrentTower(UICommandBuilder commandBuilder, SlotUiState state) {
        commandBuilder.set(
            "#CurrentTowerTitle.Text",
            state != null && state.towerPresent
                ? BankDefenseLocalization.towerDisplayName(this.viewerRef, state.towerId, safeText(state.towerName, "Tower"))
                : BankDefenseLocalization.tr(this.viewerRef, "page.slot.current.none")
        );
        commandBuilder.set("#CurrentTowerMeta.Text", state != null && state.towerPresent ? BankDefenseLocalization.translateFreeform(this.viewerRef, safeText(state.slotSummary, "")) : "");
        commandBuilder.set(
            "#CurrentTowerModule.Text",
            state != null && state.towerPresent
                ? BankDefenseLocalization.translateFreeform(this.viewerRef, safeText(state.moduleSummary, BankDefenseLocalization.tr(this.viewerRef, "page.slot.module.empty")))
                : BankDefenseLocalization.tr(this.viewerRef, "page.slot.module.empty")
        );
    }

    private void applyCurrentTowerImage(UICommandBuilder commandBuilder, SlotUiState state) {
        int visibleIndex = 0;
        if (state != null && state.towerPresent) {
            visibleIndex = switch (state.towerId) {
                case "guard_post" -> 1;
                case "sniper_desk" -> 2;
                case "rapid_security" -> 3;
                case "freeze_gate" -> 4;
                case "shock_relay" -> 5;
                case "armor_drill" -> 6;
                case "root_snare" -> 7;
                case "spore_mine" -> 8;
                case "frost_seal" -> 9;
                default -> 0;
            };
        }
        for (int index = 0; index < CURRENT_TOWER_IMAGE_SELECTORS.size(); index++) {
            commandBuilder.set(CURRENT_TOWER_IMAGE_SELECTORS.get(index) + ".Visible", index == visibleIndex);
        }
    }

    private void applyTrapSelectionImages(UICommandBuilder commandBuilder, SlotUiState state) {
        boolean trapSlot = state != null && state.trapSlot && !state.towerPresent;
        for (int index = 0; index < TOWER_ICON_SELECTORS.size(); index++) {
            commandBuilder.set(TOWER_ICON_SELECTORS.get(index) + ".Visible", !trapSlot);
            commandBuilder.set(TRAP_ICON_SELECTORS.get(index) + ".Visible", trapSlot);
        }
    }

    private void applyModuleButtons(UICommandBuilder commandBuilder, SlotUiState state) {
        for (int index = 0; index < MODULE_BUTTON_SELECTORS.size(); index++) {
            boolean visible = state != null && state.towerPresent && state.modulesAllowed && index < state.moduleButtons.size();
            commandBuilder.set(MODULE_CARD_SELECTORS.get(index) + ".Visible", visible);
            if (!visible) {
                continue;
            }

            ModuleButtonState button = state.moduleButtons.get(index);
            String shortName = BankDefenseLocalization.moduleShortName(this.viewerRef, button.moduleId, this.shortModuleName(button.moduleId, button.displayName));
            commandBuilder.set(MODULE_BUTTON_SELECTORS.get(index) + ".Text", shortName + " x" + button.count);
        }
    }

    private ActionResult handleAction(World world, String action) throws IOException {
        SlotUiState state = this.runtime.getSlotUiState(world, this.viewerRef, this.slotId);
        if (state == null || state.position == null) {
            return ActionResult.fail(BankDefenseLocalization.tr(this.viewerRef, "page.slot.unavailable"));
        }

        if (action.startsWith("build_index:")) {
            int index = Integer.parseInt(action.substring("build_index:".length()));
            if (index < 0 || index >= state.towerButtons.size()) {
                return ActionResult.fail(BankDefenseLocalization.tr(this.viewerRef, "page.slot.button_empty"));
            }
            TowerButtonState button = state.towerButtons.get(index);
            if (!button.enabled) {
                return ActionResult.fail(BankDefenseLocalization.tr(this.viewerRef, "page.slot.build_unavailable"));
            }
            return this.runtime.buildOrUpgradeTower(world, this.viewerRef, state.position, button.towerId, button.buildMode);
        }

        if ("upgrade".equals(action) && !state.upgradeAvailable) {
            return ActionResult.fail(safeText(state.upgradeLabel, BankDefenseLocalization.tr(this.viewerRef, "page.slot.upgrade_unavailable")));
        }

        if (action.startsWith("module:") && !"module:remove".equals(action)) {
            String moduleId = action.substring("module:".length());
            ModuleButtonState selected = state.moduleButtons.stream()
                .filter(button -> moduleId.equals(button.moduleId))
                .findFirst()
                .orElse(null);
            if (selected == null || !selected.enabled) {
                return ActionResult.fail(BankDefenseLocalization.tr(this.viewerRef, "page.slot.module_action_unavailable"));
            }
        }

        return switch (action) {
            case "upgrade" -> this.runtime.upgradeTower(world, this.viewerRef, state.position, 0);
            case "sell" -> this.runtime.sellTowerAt(world, this.viewerRef, state.position, 0);
            case "module:remove" -> this.runtime.removeTowerModule(world, this.viewerRef, state.position, 0);
            case "module:overload" -> this.runtime.equipTowerModule(world, this.viewerRef, state.position, "overload", 0);
            case "module:heavy_caliber" -> this.runtime.equipTowerModule(world, this.viewerRef, state.position, "heavy_caliber", 0);
            case "module:long_optics" -> this.runtime.equipTowerModule(world, this.viewerRef, state.position, "long_optics", 0);
            case "module:cryo_capsule" -> this.runtime.equipTowerModule(world, this.viewerRef, state.position, "cryo_capsule", 0);
            case "module:fragment_charge" -> this.runtime.equipTowerModule(world, this.viewerRef, state.position, "fragment_charge", 0);
            case "module:armor_scanner" -> this.runtime.equipTowerModule(world, this.viewerRef, state.position, "armor_scanner", 0);
            case "module:arc_splitter" -> this.runtime.equipTowerModule(world, this.viewerRef, state.position, "arc_splitter", 0);
            case "module:dual_camera" -> this.runtime.equipTowerModule(world, this.viewerRef, state.position, "dual_camera", 0);
            case "module:reserve_capacitor" -> this.runtime.equipTowerModule(world, this.viewerRef, state.position, "reserve_capacitor", 0);
            default -> ActionResult.fail(BankDefenseLocalization.tr(this.viewerRef, "page.slot.unknown_action", action));
        };
    }

    private static String safeText(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value;
    }

    private String shortTowerName(String towerId, String fallback) {
        return switch (towerId) {
            case "guard_post", "sniper_desk", "rapid_security", "freeze_gate", "shock_relay", "armor_drill",
                 "heart_of_roots", "storm_monolith", "seed_idol", "root_snare", "spore_mine", "frost_seal" ->
                BankDefenseLocalization.towerShortName(this.viewerRef, towerId, fallback == null || fallback.isBlank() ? towerId : fallback);
            default -> fallback == null || fallback.isBlank() ? towerId : fallback;
        };
    }

    private String shortModuleName(String moduleId, String fallback) {
        return switch (moduleId) {
            case "overload", "heavy_caliber", "long_optics", "cryo_capsule", "fragment_charge", "armor_scanner", "arc_splitter", "dual_camera", "reserve_capacitor" ->
                BankDefenseLocalization.moduleShortName(this.viewerRef, moduleId, fallback == null || fallback.isBlank() ? moduleId : fallback);
            default -> fallback == null || fallback.isBlank() ? moduleId : fallback;
        };
    }

    public static final class EventDataPayload {
        public static final BuilderCodec<EventDataPayload> CODEC = BuilderCodec.builder(EventDataPayload.class, EventDataPayload::new)
            .append(new KeyedCodec<>("Action", Codec.STRING), (entry, value) -> entry.action = value, entry -> entry.action)
            .add()
            .build();

        public String action;
    }
}
