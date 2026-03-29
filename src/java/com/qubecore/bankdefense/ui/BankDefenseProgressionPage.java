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
import com.qubecore.bankdefense.runtime.BankDefenseRuntime.ProgressionNodeButtonState;
import com.qubecore.bankdefense.runtime.BankDefenseRuntime.ProgressionUiState;
import com.qubecore.bankdefense.runtime.support.BankDefenseLocalization;
import java.io.IOException;
import java.util.List;
import javax.annotation.Nonnull;

public final class BankDefenseProgressionPage extends InteractiveCustomUIPage<BankDefenseProgressionPage.EventDataPayload> {
    private static final String UI_PAGE = "Pages/BankDefenseProgressionPage.ui";
    private static final List<UpgradeLineConfig> UPGRADE_LINES = List.of(
        new UpgradeLineConfig("economy", List.of("economy_1", "economy_2", "economy_3", "economy_4", "economy_5"), "#Card01Title", "#Card01Description", "#Card01Button", "#Card01CostIcon"),
        new UpgradeLineConfig("duplo", List.of("duplo_hp_1", "duplo_hp_2", "duplo_hp_3", "duplo_hp_4", "duplo_hp_5"), "#Card02Title", "#Card02Description", "#Card02Button", "#Card02CostIcon"),
        new UpgradeLineConfig("towers", List.of("tower_cap_1", "tower_cap_2", "tower_cap_3", "tower_cap_4", "tower_cap_5"), "#Card03Title", "#Card03Description", "#Card03Button", "#Card03CostIcon"),
        new UpgradeLineConfig("super", List.of("super_reactivation_1", "super_reactivation_2", "super_reactivation_3"), "#Card04Title", "#Card04Description", "#Card04Button", "#Card04CostIcon"),
        new UpgradeLineConfig("discount", List.of("super_cost_1", "super_cost_2", "super_cost_3", "super_cost_4", "super_cost_5"), "#Card05Title", "#Card05Description", "#Card05Button", "#Card05CostIcon")
    );

    private final BankDefenseRuntime runtime;
    private final PlayerRef viewerRef;
    private String hint = "";

    public BankDefenseProgressionPage(PlayerRef playerRef, BankDefenseRuntime runtime) {
        super(playerRef, CustomPageLifetime.CanDismiss, EventDataPayload.CODEC);
        this.runtime = runtime;
        this.viewerRef = playerRef;
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
            this.hint = BankDefenseLocalization.tr(this.viewerRef, "page.progression.error", e.getMessage());
            this.runtime.playUiErrorSound(player.getWorld(), this.viewerRef);
            player.sendMessage(Message.raw(BankDefenseLocalization.translateFreeform(this.viewerRef, this.hint)));
        }

        this.syncState(ref, store, commandBuilder);
        this.sendUpdate(commandBuilder, eventBuilder, false);
    }

    private void bindEvents(UIEventBuilder eventBuilder) {
        for (UpgradeLineConfig line : UPGRADE_LINES) {
            this.bindAction(eventBuilder, line.buttonSelector, "upgrade:" + line.id);
        }
    }

    private void bindAction(UIEventBuilder eventBuilder, String selector, String action) {
        eventBuilder.addEventBinding(CustomUIEventBindingType.Activating, selector, EventData.of("Action", action), false);
    }

    private void syncState(Ref<EntityStore> ref, Store<EntityStore> store, UICommandBuilder commandBuilder) {
        Player player = store.getComponent(ref, Player.getComponentType());
        World world = player == null ? null : player.getWorld();
        ProgressionUiState state = null;
        if (world != null) {
            try {
                state = this.runtime.getProgressionUiState(world);
            } catch (IOException e) {
                this.hint = BankDefenseLocalization.tr(this.viewerRef, "page.progression.load_error", e.getMessage());
            }
        }

        commandBuilder.set("#TitleLabel.Text", BankDefenseLocalization.tr(this.viewerRef, "page.progression.title"));
        commandBuilder.set("#Subtitle.Text", BankDefenseLocalization.tr(this.viewerRef, "page.progression.subtitle"));
        commandBuilder.set("#Cores.Text", BankDefenseLocalization.tr(this.viewerRef, "page.progression.cores", state == null ? 0 : state.cores));
        commandBuilder.set("#Stats.Text", this.statsText(state));
        commandBuilder.set(
            "#Hint.Text",
            state != null && state.matchActive
                ? BankDefenseLocalization.tr(this.viewerRef, "page.progression.hint.locked")
                : BankDefenseLocalization.translateFreeform(
                    this.viewerRef,
                    this.hint == null || this.hint.isBlank() ? BankDefenseLocalization.tr(this.viewerRef, "page.progression.hint") : this.hint
                )
        );

        for (UpgradeLineConfig line : UPGRADE_LINES) {
            UpgradeCardState card = this.buildCardState(state, line);
            commandBuilder.set(line.titleSelector + ".Text", card.title);
            commandBuilder.set(line.descriptionSelector + ".Text", card.description);
            commandBuilder.set(line.buttonSelector + ".Text", card.buttonText);
            commandBuilder.set(line.iconSelector + ".Visible", card.showCostIcon);
        }
    }

    private String statsText(ProgressionUiState state) {
        if (state == null) {
            return BankDefenseLocalization.tr(this.viewerRef, "page.progression.stats", 0, 0, 0);
        }
        return BankDefenseLocalization.tr(this.viewerRef, "page.progression.stats", state.highestWave, state.totalRuns, state.totalVictories);
    }

    private UpgradeCardState buildCardState(ProgressionUiState state, UpgradeLineConfig line) {
        int currentLevel = this.currentLevel(state, line);
        ProgressionNodeButtonState nextNode = this.nextNode(state, line);
        String title = this.lineDisplayName(line.id) + " " + currentLevel + "/" + line.nodeIds.size();
        String description = switch (line.id) {
            case "economy" -> this.economyDescription(state, line, nextNode);
            case "duplo" -> this.duploDescription(state, line, nextNode);
            case "towers" -> this.towerCapDescription(state, line, currentLevel, nextNode);
            case "super" -> this.superDescription(state, line, nextNode);
            case "discount" -> this.discountDescription(state, line, nextNode);
            default -> BankDefenseLocalization.tr(this.viewerRef, "page.progression.desc.default");
        };

        String buttonText;
        if (nextNode == null) {
            buttonText = BankDefenseLocalization.tr(this.viewerRef, "page.progression.maximum");
        } else if (state != null && state.matchActive) {
            buttonText = BankDefenseLocalization.tr(this.viewerRef, "page.progression.after_match");
        } else {
            buttonText = String.valueOf(nextNode.cost);
        }
        boolean showCostIcon = nextNode != null && (state == null || !state.matchActive);
        return new UpgradeCardState(title, description, buttonText, showCostIcon);
    }

    private String lineDisplayName(String lineId) {
        return switch (lineId) {
            case "economy" -> BankDefenseLocalization.tr(this.viewerRef, "page.progression.economy");
            case "duplo" -> BankDefenseLocalization.tr(this.viewerRef, "page.progression.duplo");
            case "towers" -> BankDefenseLocalization.tr(this.viewerRef, "page.progression.towers");
            case "super" -> BankDefenseLocalization.tr(this.viewerRef, "page.progression.super");
            case "discount" -> BankDefenseLocalization.tr(this.viewerRef, "page.progression.discount");
            default -> lineId;
        };
    }

    private String economyDescription(ProgressionUiState state, UpgradeLineConfig line, ProgressionNodeButtonState nextNode) {
        int currentBonus = (int)Math.round(this.unlockedValue(state, line));
        String text = BankDefenseLocalization.tr(this.viewerRef, "page.progression.desc.economy.current", currentBonus);
        if (nextNode != null) {
            text += BankDefenseLocalization.tr(this.viewerRef, "page.progression.desc.economy.next", (int)Math.round(nextNode.value));
        } else {
            text += BankDefenseLocalization.tr(this.viewerRef, "page.progression.line.full");
        }
        return text;
    }

    private String duploDescription(ProgressionUiState state, UpgradeLineConfig line, ProgressionNodeButtonState nextNode) {
        int currentHp = (int)Math.round(this.unlockedValue(state, line));
        String text = BankDefenseLocalization.tr(this.viewerRef, "page.progression.desc.duplo.current", currentHp);
        if (nextNode != null) {
            text += BankDefenseLocalization.tr(this.viewerRef, "page.progression.desc.duplo.next", (int)Math.round(nextNode.value));
        } else {
            text += BankDefenseLocalization.tr(this.viewerRef, "page.progression.line.full");
        }
        return text;
    }

    private String towerCapDescription(ProgressionUiState state, UpgradeLineConfig line, int currentLevel, ProgressionNodeButtonState nextNode) {
        int currentCap = 5;
        if (currentLevel > 0) {
            ProgressionNodeButtonState currentNode = this.findNode(state, line.nodeIds.get(currentLevel - 1));
            if (currentNode != null) {
                currentCap = (int)Math.round(currentNode.value);
            }
        }
        String text = BankDefenseLocalization.tr(this.viewerRef, "page.progression.desc.towers.current", currentCap);
        if (nextNode != null) {
            text += BankDefenseLocalization.tr(this.viewerRef, "page.progression.desc.towers.next", (int)Math.round(nextNode.value));
        } else {
            text += BankDefenseLocalization.tr(this.viewerRef, "page.progression.desc.towers.full");
        }
        return text;
    }

    private String superDescription(ProgressionUiState state, UpgradeLineConfig line, ProgressionNodeButtonState nextNode) {
        int currentCharges = (int)Math.round(this.unlockedValue(state, line));
        String text = BankDefenseLocalization.tr(this.viewerRef, "page.progression.desc.super.current", currentCharges);
        if (nextNode != null) {
            text += BankDefenseLocalization.tr(this.viewerRef, "page.progression.desc.super.next");
        } else {
            text += BankDefenseLocalization.tr(this.viewerRef, "page.progression.line.full");
        }
        return text;
    }

    private String discountDescription(ProgressionUiState state, UpgradeLineConfig line, ProgressionNodeButtonState nextNode) {
        int currentDiscount = (int)Math.round(this.unlockedValue(state, line) * 100.0);
        String text = BankDefenseLocalization.tr(this.viewerRef, "page.progression.desc.discount.current", currentDiscount);
        if (nextNode != null) {
            text += BankDefenseLocalization.tr(this.viewerRef, "page.progression.desc.discount.next", (int)Math.round(nextNode.value * 100.0));
        } else {
            text += BankDefenseLocalization.tr(this.viewerRef, "page.progression.desc.discount.full");
        }
        return text;
    }

    private int currentLevel(ProgressionUiState state, UpgradeLineConfig line) {
        int level = 0;
        for (String nodeId : line.nodeIds) {
            ProgressionNodeButtonState node = this.findNode(state, nodeId);
            if (node == null || !node.unlocked) {
                break;
            }
            level++;
        }
        return level;
    }

    private double unlockedValue(ProgressionUiState state, UpgradeLineConfig line) {
        double total = 0.0;
        for (String nodeId : line.nodeIds) {
            ProgressionNodeButtonState node = this.findNode(state, nodeId);
            if (node != null && node.unlocked) {
                total += node.value;
            }
        }
        return total;
    }

    private ProgressionNodeButtonState nextNode(ProgressionUiState state, UpgradeLineConfig line) {
        for (String nodeId : line.nodeIds) {
            ProgressionNodeButtonState node = this.findNode(state, nodeId);
            if (node != null && !node.unlocked) {
                return node;
            }
        }
        return null;
    }

    private ProgressionNodeButtonState findNode(ProgressionUiState state, String nodeId) {
        if (state == null || state.nodes == null || nodeId == null) {
            return null;
        }
        for (ProgressionNodeButtonState node : state.nodes) {
            if (nodeId.equals(node.nodeId)) {
                return node;
            }
        }
        return null;
    }

    private ActionResult handleAction(World world, String action) throws IOException {
        if (!action.startsWith("upgrade:")) {
            return ActionResult.fail(BankDefenseLocalization.tr(this.viewerRef, "page.progression.unknown_action", action));
        }
        String lineId = action.substring("upgrade:".length());
        UpgradeLineConfig line = this.findLine(lineId);
        if (line == null) {
            return ActionResult.fail(BankDefenseLocalization.tr(this.viewerRef, "page.progression.unknown_line", lineId));
        }
        ProgressionUiState state = this.runtime.getProgressionUiState(world);
        ProgressionNodeButtonState nextNode = this.nextNode(state, line);
        if (nextNode == null) {
            return ActionResult.fail(BankDefenseLocalization.tr(this.viewerRef, "page.progression.line_maxed", this.lineDisplayName(line.id)));
        }
        return this.runtime.unlockProgressionNode(world, nextNode.nodeId);
    }

    private UpgradeLineConfig findLine(String lineId) {
        for (UpgradeLineConfig line : UPGRADE_LINES) {
            if (line.id.equals(lineId)) {
                return line;
            }
        }
        return null;
    }

    private static final class UpgradeLineConfig {
        private final String id;
        private final List<String> nodeIds;
        private final String titleSelector;
        private final String descriptionSelector;
        private final String buttonSelector;
        private final String iconSelector;

        private UpgradeLineConfig(String id, List<String> nodeIds, String titleSelector, String descriptionSelector, String buttonSelector, String iconSelector) {
            this.id = id;
            this.nodeIds = nodeIds;
            this.titleSelector = titleSelector;
            this.descriptionSelector = descriptionSelector;
            this.buttonSelector = buttonSelector;
            this.iconSelector = iconSelector;
        }
    }

    private static final class UpgradeCardState {
        private final String title;
        private final String description;
        private final String buttonText;
        private final boolean showCostIcon;

        private UpgradeCardState(String title, String description, String buttonText, boolean showCostIcon) {
            this.title = title;
            this.description = description;
            this.buttonText = buttonText;
            this.showCostIcon = showCostIcon;
        }
    }

    public static final class EventDataPayload {
        public static final BuilderCodec<EventDataPayload> CODEC = BuilderCodec.builder(EventDataPayload.class, EventDataPayload::new)
            .append(new KeyedCodec<>("Action", Codec.STRING), (entry, value) -> entry.action = value, entry -> entry.action)
            .add()
            .build();

        public String action;
    }
}
