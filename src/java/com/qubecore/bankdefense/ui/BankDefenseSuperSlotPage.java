package com.qubecore.bankdefense.ui;

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
import com.qubecore.bankdefense.runtime.BankDefenseRuntime.SlotUiState;
import com.qubecore.bankdefense.runtime.BankDefenseRuntime.TowerButtonState;
import com.qubecore.bankdefense.runtime.support.BankDefenseLocalization;
import java.io.IOException;
import java.util.List;
import javax.annotation.Nonnull;

public final class BankDefenseSuperSlotPage extends InteractiveCustomUIPage<BankDefenseSuperSlotEventDataPayload> {
    private static final String UI_PAGE = "Pages/BankDefenseSuperSlotPage.ui";
    private static final List<String> SUPER_CARD_SELECTORS = List.of("#HeartCard", "#MonolithCard", "#IdolCard");
    private static final List<String> SUPER_BUTTON_SELECTORS = List.of("#HeartBuild", "#MonolithBuild", "#IdolBuild");

    private final BankDefenseRuntime runtime;
    private final PlayerRef viewerRef;
    private final String slotId;
    private String hint = "";

    public BankDefenseSuperSlotPage(PlayerRef playerRef, BankDefenseRuntime runtime, String slotId) {
        super(playerRef, CustomPageLifetime.CanDismiss, BankDefenseSuperSlotEventDataPayload.CODEC);
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
    public void handleDataEvent(@Nonnull Ref<EntityStore> ref, @Nonnull Store<EntityStore> store, @Nonnull BankDefenseSuperSlotEventDataPayload data) {
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
            if (result.success && data.action.startsWith("idol_mode:")) {
                this.close();
                return;
            }
        } catch (IOException e) {
            this.hint = BankDefenseLocalization.tr(this.viewerRef, "page.super.error", e.getMessage());
            this.runtime.playUiErrorSound(player.getWorld(), this.viewerRef);
            player.sendMessage(Message.raw(BankDefenseLocalization.translateFreeform(this.viewerRef, this.hint)));
        }

        this.syncState(ref, store, commandBuilder);
        this.sendUpdate(commandBuilder, eventBuilder, false);
    }

    private void bindEvents(UIEventBuilder eventBuilder) {
        this.bindAction(eventBuilder, "#HeartBuild", "build:heart_of_roots");
        this.bindAction(eventBuilder, "#MonolithBuild", "build:storm_monolith");
        this.bindAction(eventBuilder, "#IdolBuild", "build:seed_idol");
        this.bindAction(eventBuilder, "#BuiltPrimaryAction", "built:primary");
        this.bindAction(eventBuilder, "#BuiltSellAction", "built:sell");
        this.bindAction(eventBuilder, "#IdolModeHarvest", "idol_mode:harvest");
        this.bindAction(eventBuilder, "#IdolModeWard", "idol_mode:ward");
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
                this.hint = BankDefenseLocalization.tr(this.viewerRef, "page.super.load_error", e.getMessage());
            }
        }

        boolean slotReady = state != null;
        boolean selectionVisible = slotReady && !state.towerPresent;
        boolean idolModeVisible = slotReady && state.towerPresent && "seed_idol".equals(state.towerId);
        boolean builtInfoVisible = slotReady && state.towerPresent && !"seed_idol".equals(state.towerId);

        commandBuilder.set("#TitleLabel.Text", slotReady ? this.pageTitle(state) : BankDefenseLocalization.tr(this.viewerRef, "page.super.title"));
        commandBuilder.set(
            "#Subtitle.Text",
            slotReady && state.position != null
                ? BankDefenseLocalization.tr(this.viewerRef, "label.position", state.position.toShortString())
                : BankDefenseLocalization.tr(this.viewerRef, "page.super.unavailable_full")
        );
        commandBuilder.set("#SelectionPanel.Visible", selectionVisible);
        commandBuilder.set("#BuiltInfoPanel.Visible", builtInfoVisible);
        commandBuilder.set("#IdolModePanel.Visible", idolModeVisible);
        commandBuilder.set(
            "#Hint.Text",
            BankDefenseLocalization.translateFreeform(
                this.viewerRef,
                this.hint == null || this.hint.isBlank() ? BankDefenseLocalization.tr(this.viewerRef, "page.super.hint") : this.hint
            )
        );

        this.applySelection(commandBuilder, state);
        this.applyBuiltInfo(commandBuilder, state);
        this.applyIdolMode(commandBuilder, state);
    }

    private void applySelection(UICommandBuilder commandBuilder, SlotUiState state) {
        TowerButtonState heart = this.findTowerButton(state, "heart_of_roots");
        TowerButtonState monolith = this.findTowerButton(state, "storm_monolith");
        TowerButtonState idol = this.findTowerButton(state, "seed_idol");
        TowerButtonState[] ordered = new TowerButtonState[]{heart, monolith, idol};

        commandBuilder.set("#HeartTitle.Text", BankDefenseLocalization.tr(this.viewerRef, "page.super.card.heart.title"));
        commandBuilder.set("#HeartDescription.Text", BankDefenseLocalization.tr(this.viewerRef, "page.super.card.heart.description"));
        commandBuilder.set("#MonolithTitle.Text", BankDefenseLocalization.tr(this.viewerRef, "page.super.card.monolith.title"));
        commandBuilder.set("#MonolithDescription.Text", BankDefenseLocalization.tr(this.viewerRef, "page.super.card.monolith.description"));
        commandBuilder.set("#IdolTitleSelection.Text", BankDefenseLocalization.tr(this.viewerRef, "page.super.card.idol.title"));
        commandBuilder.set("#IdolDescription.Text", BankDefenseLocalization.tr(this.viewerRef, "page.super.card.idol.description"));

        for (int index = 0; index < SUPER_CARD_SELECTORS.size(); index++) {
            TowerButtonState button = ordered[index];
            boolean visible = state != null && !state.towerPresent && button != null;
            commandBuilder.set(SUPER_CARD_SELECTORS.get(index) + ".Visible", visible);
            if (!visible) {
                continue;
            }
            commandBuilder.set(
                SUPER_BUTTON_SELECTORS.get(index) + ".Text",
                !button.enabled
                    ? BankDefenseLocalization.tr(this.viewerRef, "page.super.unavailable")
                    : button.unlocked
                        ? BankDefenseLocalization.tr(this.viewerRef, "page.super.buy_for", button.cost)
                        : BankDefenseLocalization.tr(this.viewerRef, "page.super.unlock_in_progression")
            );
        }
    }

    private void applyBuiltInfo(UICommandBuilder commandBuilder, SlotUiState state) {
        String towerId = state == null ? null : state.towerId;
        String towerName = state == null
            ? BankDefenseLocalization.tr(this.viewerRef, "page.super.current.none")
            : BankDefenseLocalization.towerDisplayName(this.viewerRef, state.towerId, safeText(state.towerName, BankDefenseLocalization.tr(this.viewerRef, "page.super.current.none")));
        commandBuilder.set("#BuiltInfoTitle.Text", towerName);
        commandBuilder.set("#BuiltInfoText.Text", state == null ? "" : BankDefenseLocalization.translateFreeform(this.viewerRef, safeText(state.towerDescription, "")));
        commandBuilder.set("#BuiltStatusText.Text", this.builtStatusText(state));
        this.applyCurrentSuperImage(commandBuilder, towerId, "#BuiltInfo");

        boolean showPrimaryAction = false;
        String primaryActionText = "";
        boolean showSellAction = state != null && state.sellRefund > 0;
        if ("heart_of_roots".equals(towerId)) {
            showPrimaryAction = true;
            primaryActionText = state == null
                ? BankDefenseLocalization.tr(this.viewerRef, "page.super.activate")
                : BankDefenseLocalization.translateFreeform(this.viewerRef, safeText(state.upgradeLabel, BankDefenseLocalization.tr(this.viewerRef, "page.super.activate")));
        } else if ("storm_monolith".equals(towerId) && state != null && !state.superReady && state.superActivationsRemaining > 0) {
            showPrimaryAction = true;
            primaryActionText = BankDefenseLocalization.translateFreeform(this.viewerRef, safeText(state.upgradeLabel, BankDefenseLocalization.tr(this.viewerRef, "page.super.reactivate")));
        }

        commandBuilder.set("#BuiltPrimaryAction.Visible", showPrimaryAction);
        if (showPrimaryAction) {
            commandBuilder.set("#BuiltPrimaryAction.Text", primaryActionText);
        }
        commandBuilder.set("#BuiltSellAction.Visible", showSellAction);
        if (showSellAction) {
            commandBuilder.set("#BuiltSellAction.Text", BankDefenseLocalization.translateFreeform(this.viewerRef, safeText(state.sellLabel, BankDefenseLocalization.tr(this.viewerRef, "page.slot.sell_unavailable"))));
        }
    }

    private void applyIdolMode(UICommandBuilder commandBuilder, SlotUiState state) {
        commandBuilder.set(
            "#IdolTitle.Text",
            state == null
                ? BankDefenseLocalization.towerDisplayName(this.viewerRef, "seed_idol", "Harvest Idol")
                : BankDefenseLocalization.towerDisplayName(this.viewerRef, "seed_idol", safeText(state.towerName, "Harvest Idol"))
        );
        commandBuilder.set("#IdolInfoText.Text", state == null ? "" : BankDefenseLocalization.translateFreeform(this.viewerRef, safeText(state.towerDescription, "")));
        commandBuilder.set("#IdolModeText.Text", state == null ? "" : BankDefenseLocalization.translateFreeform(this.viewerRef, safeText(state.moduleSummary, safeText(state.slotSummary, ""))));
        this.applyCurrentSuperImage(commandBuilder, "seed_idol", "#Idol");

        String harvestLabel = BankDefenseLocalization.tr(this.viewerRef, "page.super.mode.harvest");
        String wardLabel = BankDefenseLocalization.tr(this.viewerRef, "page.super.mode.ward");
        if (state != null) {
            for (TowerButtonState button : state.towerButtons) {
                if (button.buildMode == 1) {
                    harvestLabel = BankDefenseLocalization.translateFreeform(this.viewerRef, safeText(button.displayName, harvestLabel));
                } else if (button.buildMode == 2) {
                    wardLabel = BankDefenseLocalization.translateFreeform(this.viewerRef, safeText(button.displayName, wardLabel));
                }
            }
        }
        commandBuilder.set("#IdolModeHarvest.Text", harvestLabel);
        commandBuilder.set("#IdolModeWard.Text", wardLabel);
        commandBuilder.set("#IdolModeHarvestDescription.Text", BankDefenseLocalization.tr(this.viewerRef, "page.super.mode.harvest.description"));
        commandBuilder.set("#IdolModeWardDescription.Text", BankDefenseLocalization.tr(this.viewerRef, "page.super.mode.ward.description"));
    }

    private void applyCurrentSuperImage(UICommandBuilder commandBuilder, String towerId, String prefix) {
        commandBuilder.set(prefix + "HeartImage.Visible", "heart_of_roots".equals(towerId));
        commandBuilder.set(prefix + "MonolithImage.Visible", "storm_monolith".equals(towerId));
        commandBuilder.set(prefix + "IdolImage.Visible", "seed_idol".equals(towerId));
        commandBuilder.set(prefix + "Placeholder.Visible", towerId == null || towerId.isBlank());
    }

    private String builtStatusText(SlotUiState state) {
        if (state == null) {
            return "";
        }
        if ("heart_of_roots".equals(state.towerId) || "storm_monolith".equals(state.towerId)) {
            return BankDefenseLocalization.tr(
                this.viewerRef,
                "page.super.status",
                BankDefenseLocalization.tr(this.viewerRef, state.superReady ? "page.super.ready" : "page.super.spent"),
                state.superActivationsTotal,
                state.superActivationsRemaining
            );
        }
        return BankDefenseLocalization.translateFreeform(this.viewerRef, safeText(state.slotSummary, ""));
    }

    private TowerButtonState findTowerButton(SlotUiState state, String towerId) {
        if (state == null) {
            return null;
        }
        for (TowerButtonState button : state.towerButtons) {
            if (towerId.equals(button.towerId)) {
                return button;
            }
        }
        return null;
    }

    private String pageTitle(SlotUiState state) {
        if (state == null || !state.towerPresent) {
            return BankDefenseLocalization.tr(this.viewerRef, "page.super.title");
        }
        return BankDefenseLocalization.towerDisplayName(this.viewerRef, state.towerId, safeText(state.towerName, BankDefenseLocalization.tr(this.viewerRef, "page.super.title")));
    }

    private ActionResult handleAction(World world, String action) throws IOException {
        SlotUiState state = this.runtime.getSlotUiState(world, this.viewerRef, this.slotId);
        if (state == null || state.position == null) {
            return ActionResult.fail(BankDefenseLocalization.tr(this.viewerRef, "page.super.unavailable_full"));
        }

        if (action.startsWith("build:")) {
            String towerId = action.substring("build:".length());
            TowerButtonState button = this.findTowerButton(state, towerId);
            if (button == null || !button.enabled) {
                return ActionResult.fail(BankDefenseLocalization.tr(this.viewerRef, "page.super.build_unavailable"));
            }
        }

        return switch (action) {
            case "build:heart_of_roots" -> this.runtime.buildOrUpgradeTower(world, this.viewerRef, state.position, "heart_of_roots", 0);
            case "build:storm_monolith" -> this.runtime.buildOrUpgradeTower(world, this.viewerRef, state.position, "storm_monolith", 0);
            case "build:seed_idol" -> this.runtime.buildOrUpgradeTower(world, this.viewerRef, state.position, "seed_idol", 0);
            case "built:primary" -> this.handleBuiltPrimary(world, state);
            case "built:sell" -> this.runtime.sellTowerAt(world, this.viewerRef, state.position, 0);
            case "idol_mode:harvest" -> this.runtime.setSeedIdolModeAt(world, this.viewerRef, state.position, 1, 0);
            case "idol_mode:ward" -> this.runtime.setSeedIdolModeAt(world, this.viewerRef, state.position, 2, 0);
            default -> ActionResult.fail(BankDefenseLocalization.tr(this.viewerRef, "page.super.unknown_action", action));
        };
    }

    private ActionResult handleBuiltPrimary(World world, SlotUiState state) throws IOException {
        if (state == null || state.position == null || state.towerId == null) {
            return ActionResult.fail(BankDefenseLocalization.tr(this.viewerRef, "page.super.unavailable_full"));
        }
        return switch (state.towerId) {
            case "heart_of_roots" -> this.runtime.activateSuperTowerAt(world, this.viewerRef, state.position, 0);
            case "storm_monolith" -> this.runtime.reactivateSuperTowerAt(world, this.viewerRef, state.position, 0);
            default -> ActionResult.fail(BankDefenseLocalization.tr(this.viewerRef, "page.super.no_primary_command"));
        };
    }

    private static String safeText(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value;
    }

}
