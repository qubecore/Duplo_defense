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
import com.qubecore.bankdefense.runtime.BankDefenseRuntime.RewardUiState;
import com.qubecore.bankdefense.runtime.support.BankDefenseLocalization;
import java.io.IOException;
import javax.annotation.Nonnull;

public final class BankDefenseRewardPage extends InteractiveCustomUIPage<BankDefenseRewardPage.EventDataPayload> {
    private static final String UI_PAGE = "Pages/BankDefenseRewardPage.ui";

    private final BankDefenseRuntime runtime;
    private final PlayerRef viewerRef;
    private String hint = "";

    public BankDefenseRewardPage(PlayerRef playerRef, BankDefenseRuntime runtime) {
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
            if (result.success && data.action.startsWith("reward:")) {
                this.close();
                return;
            }
        } catch (IOException e) {
            this.hint = BankDefenseLocalization.tr(this.viewerRef, "page.reward.error", e.getMessage());
            this.runtime.playUiErrorSound(player.getWorld(), this.viewerRef);
            player.sendMessage(Message.raw(BankDefenseLocalization.translateFreeform(this.viewerRef, this.hint)));
        }

        this.syncState(ref, store, commandBuilder);
        this.sendUpdate(commandBuilder, eventBuilder, false);
    }

    private void bindEvents(UIEventBuilder eventBuilder) {
        this.bindAction(eventBuilder, "#ChoiceAButton", "reward:0");
        this.bindAction(eventBuilder, "#ChoiceBButton", "reward:1");
        this.bindAction(eventBuilder, "#ChoiceCButton", "reward:2");
        this.bindAction(eventBuilder, "#ChoiceDButton", "reward:3");
    }

    private void bindAction(UIEventBuilder eventBuilder, String selector, String action) {
        eventBuilder.addEventBinding(CustomUIEventBindingType.Activating, selector, EventData.of("Action", action), false);
    }

    private void syncState(Ref<EntityStore> ref, Store<EntityStore> store, UICommandBuilder commandBuilder) {
        Player player = store.getComponent(ref, Player.getComponentType());
        World world = player == null ? null : player.getWorld();
        RewardUiState state = null;
        if (world != null) {
            try {
                state = this.runtime.getRewardUiState(world, this.viewerRef);
            } catch (IOException e) {
                this.hint = BankDefenseLocalization.tr(this.viewerRef, "page.reward.load_error", e.getMessage());
            }
        }

        commandBuilder.set("#TitleLabel.Text", BankDefenseLocalization.tr(this.viewerRef, "page.reward.title"));
        commandBuilder.set("#Subtitle.Text", BankDefenseLocalization.tr(this.viewerRef, "page.reward.subtitle"));
        commandBuilder.set("#ChoiceHeader.Text", BankDefenseLocalization.tr(this.viewerRef, "page.reward.choice_header"));
        commandBuilder.set("#Wave.Text", state == null ? BankDefenseLocalization.tr(this.viewerRef, "label.wave.none") : BankDefenseLocalization.tr(this.viewerRef, "label.wave", state.currentWave));
        commandBuilder.set("#Balance.Text", state == null ? BankDefenseLocalization.tr(this.viewerRef, "label.funds.none") : BankDefenseLocalization.tr(this.viewerRef, "label.funds", state.currency));
        commandBuilder.set("#Bank.Text", state == null ? BankDefenseLocalization.tr(this.viewerRef, "label.bank.none") : BankDefenseLocalization.tr(this.viewerRef, "label.bank", state.bankHp));
        commandBuilder.set("#Phase.Text", state == null ? "" : BankDefenseLocalization.translateFreeform(this.viewerRef, state.phaseText));
        commandBuilder.set("#AllyPick.Text", state == null ? "" : BankDefenseLocalization.translateFreeform(this.viewerRef, state.allyPickText));

        this.syncChoice(commandBuilder, state, 0, "A");
        this.syncChoice(commandBuilder, state, 1, "B");
        this.syncChoice(commandBuilder, state, 2, "C");
        this.syncChoice(commandBuilder, state, 3, "D");

        commandBuilder.set(
            "#Hint.Text",
            BankDefenseLocalization.translateFreeform(
                this.viewerRef,
                this.hint == null || this.hint.isBlank() ? BankDefenseLocalization.tr(this.viewerRef, "page.reward.hint") : this.hint
            )
        );
    }

    private void syncChoice(UICommandBuilder commandBuilder, RewardUiState state, int index, String suffix) {
        ModuleButtonState button = state != null && index >= 0 && index < state.choiceButtons.size()
            ? state.choiceButtons.get(index)
            : null;

        String title = button == null
            ? BankDefenseLocalization.tr(this.viewerRef, "page.reward.empty")
            : BankDefenseLocalization.moduleDisplayName(this.viewerRef, button.moduleId, button.displayName);
        String note = button == null
            ? BankDefenseLocalization.tr(this.viewerRef, "page.reward.empty.note")
            : BankDefenseLocalization.moduleNote(this.viewerRef, button.moduleId, button.note);
        String buttonLabel = button == null
            ? BankDefenseLocalization.tr(this.viewerRef, "page.reward.unavailable")
            : button.enabled
                ? BankDefenseLocalization.tr(this.viewerRef, "page.reward.select")
                : BankDefenseLocalization.tr(this.viewerRef, "page.reward.unavailable");

        commandBuilder.set("#Choice" + suffix + "Title.Text", title);
        commandBuilder.set("#Choice" + suffix + "Note.Text", note);
        commandBuilder.set("#Choice" + suffix + "Button.Text", buttonLabel);

        String moduleId = button == null ? "" : button.moduleId;
        this.setIconVisibility(commandBuilder, suffix, "Placeholder", moduleId.isBlank());
        this.setIconVisibility(commandBuilder, suffix, "Overload", "overload".equals(moduleId));
        this.setIconVisibility(commandBuilder, suffix, "HeavyCaliber", "heavy_caliber".equals(moduleId));
        this.setIconVisibility(commandBuilder, suffix, "LongOptics", "long_optics".equals(moduleId));
        this.setIconVisibility(commandBuilder, suffix, "CryoCapsule", "cryo_capsule".equals(moduleId));
        this.setIconVisibility(commandBuilder, suffix, "FragmentCharge", "fragment_charge".equals(moduleId));
        this.setIconVisibility(commandBuilder, suffix, "ArmorScanner", "armor_scanner".equals(moduleId));
        this.setIconVisibility(commandBuilder, suffix, "ArcSplitter", "arc_splitter".equals(moduleId));
        this.setIconVisibility(commandBuilder, suffix, "DualCamera", "dual_camera".equals(moduleId));
        this.setIconVisibility(commandBuilder, suffix, "ReserveCapacitor", "reserve_capacitor".equals(moduleId));

        if (!moduleId.isBlank()
            && !"overload".equals(moduleId)
            && !"heavy_caliber".equals(moduleId)
            && !"long_optics".equals(moduleId)
            && !"cryo_capsule".equals(moduleId)
            && !"fragment_charge".equals(moduleId)
            && !"armor_scanner".equals(moduleId)
            && !"arc_splitter".equals(moduleId)
            && !"dual_camera".equals(moduleId)
            && !"reserve_capacitor".equals(moduleId)) {
            this.setIconVisibility(commandBuilder, suffix, "Placeholder", true);
        }
    }

    private void setIconVisibility(UICommandBuilder commandBuilder, String suffix, String iconKey, boolean visible) {
        commandBuilder.set("#Choice" + suffix + "Image" + iconKey + ".Visible", visible);
    }

    private ActionResult handleAction(World world, String action) throws IOException {
        RewardUiState state = this.runtime.getRewardUiState(world, this.viewerRef);
        if (state == null || !state.rewardPending) {
            return ActionResult.fail(BankDefenseLocalization.tr(this.viewerRef, "page.reward.none_active"));
        }

        int index = switch (action) {
            case "reward:0" -> 0;
            case "reward:1" -> 1;
            case "reward:2" -> 2;
            case "reward:3" -> 3;
            default -> -1;
        };

        if (index < 0 || index >= state.choiceButtons.size()) {
            return ActionResult.fail(BankDefenseLocalization.tr(this.viewerRef, "page.reward.slot_empty"));
        }

        return this.runtime.claimModuleReward(world, this.viewerRef, state.choiceButtons.get(index).moduleId);
    }

    public static final class EventDataPayload {
        public static final BuilderCodec<EventDataPayload> CODEC = BuilderCodec.builder(EventDataPayload.class, EventDataPayload::new)
            .append(new KeyedCodec<>("Action", Codec.STRING), (entry, value) -> entry.action = value, entry -> entry.action)
            .add()
            .build();

        private String action;
    }
}
