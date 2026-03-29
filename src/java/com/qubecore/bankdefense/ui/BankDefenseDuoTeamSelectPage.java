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
import com.qubecore.bankdefense.runtime.BankDefenseRuntime.DuoTeamSelectUiState;
import com.qubecore.bankdefense.runtime.support.BankDefenseLocalization;
import java.io.IOException;
import javax.annotation.Nonnull;

public final class BankDefenseDuoTeamSelectPage extends InteractiveCustomUIPage<BankDefenseDuoTeamSelectPage.EventDataPayload> {
    private static final String UI_PAGE = "Pages/BankDefenseDuoTeamSelectPage.ui";

    private final BankDefenseRuntime runtime;
    private final PlayerRef viewerRef;
    private String hint = "";

    public BankDefenseDuoTeamSelectPage(PlayerRef playerRef, BankDefenseRuntime runtime) {
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
            this.hint = BankDefenseLocalization.tr(this.viewerRef, "page.duoteam.error", e.getMessage());
            this.runtime.playUiErrorSound(player.getWorld(), this.viewerRef);
            player.sendMessage(Message.raw(BankDefenseLocalization.translateFreeform(this.viewerRef, this.hint)));
        }

        this.syncState(ref, store, commandBuilder);
        this.sendUpdate(commandBuilder, eventBuilder, false);
    }

    private void bindEvents(UIEventBuilder eventBuilder) {
        this.bindAction(eventBuilder, "#BlueButton", "team:blue");
        this.bindAction(eventBuilder, "#GreenButton", "team:green");
    }

    private void bindAction(UIEventBuilder eventBuilder, String selector, String action) {
        eventBuilder.addEventBinding(CustomUIEventBindingType.Activating, selector, EventData.of("Action", action), false);
    }

    private void syncState(Ref<EntityStore> ref, Store<EntityStore> store, UICommandBuilder commandBuilder) {
        Player player = store.getComponent(ref, Player.getComponentType());
        World world = player == null ? null : player.getWorld();
        DuoTeamSelectUiState state = world == null ? new DuoTeamSelectUiState() : this.runtime.getDuoTeamSelectUiState(world, this.viewerRef);

        commandBuilder.set("#TitleLabel.Text", BankDefenseLocalization.tr(this.viewerRef, "page.duoteam.title"));
        commandBuilder.set(
            "#Header.Text",
            state.complete
                ? BankDefenseLocalization.tr(this.viewerRef, "page.duoteam.header.complete")
                : BankDefenseLocalization.tr(this.viewerRef, "page.duoteam.header")
        );
        commandBuilder.set("#BlueButton.Text", this.buttonText(state, "blue"));
        commandBuilder.set("#GreenButton.Text", this.buttonText(state, "green"));
        commandBuilder.set(
            "#Hint.Text",
            BankDefenseLocalization.translateFreeform(
                this.viewerRef,
                this.hint == null || this.hint.isBlank()
                    ? this.defaultHint(state)
                    : this.hint
            )
        );
    }

    private String buttonText(DuoTeamSelectUiState state, String teamId) {
        boolean selected = teamId.equals(state.viewerTeam);
        boolean available = "blue".equals(teamId) ? state.blueAvailable : state.greenAvailable;
        if (selected) {
            return BankDefenseLocalization.tr(this.viewerRef, "page.duoteam.selected");
        }
        if (!available) {
            return BankDefenseLocalization.tr(this.viewerRef, "page.duoteam.taken");
        }
        return BankDefenseLocalization.tr(this.viewerRef, "page.duoteam.select");
    }

    private String defaultHint(DuoTeamSelectUiState state) {
        if (!state.duoMode) {
            return BankDefenseLocalization.tr(this.viewerRef, "page.duoteam.not_duo");
        }
        if (state.complete) {
            return BankDefenseLocalization.tr(this.viewerRef, "page.duoteam.hint.complete");
        }
        if ("blue".equals(state.viewerTeam)) {
            return BankDefenseLocalization.tr(this.viewerRef, "page.duoteam.hint.blue_wait");
        }
        if ("green".equals(state.viewerTeam)) {
            return BankDefenseLocalization.tr(this.viewerRef, "page.duoteam.hint.green_wait");
        }
        return BankDefenseLocalization.tr(this.viewerRef, "page.duoteam.hint");
    }

    private ActionResult handleAction(World world, String action) throws IOException {
        return switch (action) {
            case "team:blue" -> this.runtime.selectDuoTeam(world, this.viewerRef, "blue");
            case "team:green" -> this.runtime.selectDuoTeam(world, this.viewerRef, "green");
            default -> ActionResult.fail(BankDefenseLocalization.tr(this.viewerRef, "page.duoteam.unknown_action", action));
        };
    }

    public static final class EventDataPayload {
        public static final BuilderCodec<EventDataPayload> CODEC = BuilderCodec.builder(EventDataPayload.class, EventDataPayload::new)
            .append(new KeyedCodec<>("Action", Codec.STRING), (entry, value) -> entry.action = value, entry -> entry.action)
            .add()
            .build();

        private String action;
    }
}
