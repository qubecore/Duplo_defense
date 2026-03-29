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
import com.qubecore.bankdefense.runtime.support.BankDefenseLocalization;
import java.io.IOException;
import javax.annotation.Nonnull;

public final class BankDefenseModeSelectPage extends InteractiveCustomUIPage<BankDefenseModeSelectPage.EventDataPayload> {
    private static final String UI_PAGE = "Pages/BankDefenseModeSelectPage.ui";
    private static final String MODE_SOLO = "solo";
    private static final String MODE_DUO = "duo";

    private final BankDefenseRuntime runtime;
    private final PlayerRef viewerRef;
    private String hint = "";

    public BankDefenseModeSelectPage(PlayerRef playerRef, BankDefenseRuntime runtime) {
        super(playerRef, CustomPageLifetime.CanDismiss, EventDataPayload.CODEC);
        this.runtime = runtime;
        this.viewerRef = playerRef;
    }

    @Override
    public void build(
        @Nonnull Ref<EntityStore> ref,
        @Nonnull UICommandBuilder commandBuilder,
        @Nonnull UIEventBuilder eventBuilder,
        @Nonnull Store<EntityStore> store
    ) {
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
            ActionResult result = this.handleAction(player.getWorld(), ref, data.action);
            this.hint = result.message;
            this.runtime.playUiActionFeedback(player.getWorld(), this.viewerRef, result);
            if (result.message != null && !result.message.isBlank()) {
                player.sendMessage(Message.raw(BankDefenseLocalization.translateFreeform(this.viewerRef, result.message)));
            }
        } catch (IOException e) {
            this.hint = BankDefenseLocalization.tr(this.viewerRef, "page.mode.error", e.getMessage());
            this.runtime.playUiErrorSound(player.getWorld(), this.viewerRef);
            player.sendMessage(Message.raw(BankDefenseLocalization.translateFreeform(this.viewerRef, this.hint)));
        }

        this.syncState(ref, store, commandBuilder);
        this.sendUpdate(commandBuilder, eventBuilder, false);
    }

    private void bindEvents(UIEventBuilder eventBuilder) {
        this.bindAction(eventBuilder, "#ConfirmButton", "travel:confirm");
        this.bindAction(eventBuilder, "#CancelButton", "travel:cancel");
    }

    private void bindAction(UIEventBuilder eventBuilder, String selector, String action) {
        eventBuilder.addEventBinding(CustomUIEventBindingType.Activating, selector, EventData.of("Action", action), false);
    }

    private void syncState(Ref<EntityStore> ref, Store<EntityStore> store, UICommandBuilder commandBuilder) {
        Player player = store.getComponent(ref, Player.getComponentType());
        World world = player == null ? null : player.getWorld();
        String targetMode = this.targetMode(world);
        boolean targetDuo = MODE_DUO.equals(targetMode);
        commandBuilder.set("#TitleLabel.Text", BankDefenseLocalization.tr(this.viewerRef, "page.mode.title"));
        commandBuilder.set(
            "#Header.Text",
            world == null
                ? BankDefenseLocalization.tr(this.viewerRef, "page.mode.header.unavailable")
                : targetDuo
                    ? BankDefenseLocalization.tr(this.viewerRef, "page.mode.confirm.duo")
                    : BankDefenseLocalization.tr(this.viewerRef, "page.mode.confirm.solo")
        );
        commandBuilder.set(
            "#Body.Text",
            world == null
                ? BankDefenseLocalization.tr(this.viewerRef, "page.mode.body.unavailable")
                : targetDuo
                    ? BankDefenseLocalization.tr(this.viewerRef, "page.mode.body.duo")
                    : BankDefenseLocalization.tr(this.viewerRef, "page.mode.body.solo")
        );
        commandBuilder.set("#ConfirmButton.Text", BankDefenseLocalization.tr(this.viewerRef, "page.mode.yes"));
        commandBuilder.set("#CancelButton.Text", BankDefenseLocalization.tr(this.viewerRef, "page.mode.no"));
        commandBuilder.set(
            "#Hint.Text",
            BankDefenseLocalization.translateFreeform(
                this.viewerRef,
                this.hint == null || this.hint.isBlank() ? BankDefenseLocalization.tr(this.viewerRef, "page.mode.hint") : this.hint
            )
        );
    }

    private ActionResult handleAction(World world, Ref<EntityStore> playerEntityRef, String action) throws IOException {
        if ("travel:cancel".equals(action)) {
            this.close();
            return ActionResult.ok("");
        }
        if ("travel:confirm".equals(action)) {
            String targetMode = this.targetMode(world);
            ActionResult result = this.runtime.travelToMode(world, playerEntityRef, targetMode);
            if (result != null && result.success) {
                this.close();
            }
            return result;
        }
        return ActionResult.fail(BankDefenseLocalization.tr(this.viewerRef, "page.mode.unknown_action", action));
    }

    private String targetMode(World world) {
        if (world != null && this.runtime.isDuoGameplayMode(world)) {
            return MODE_SOLO;
        }
        return MODE_DUO;
    }

    public static final class EventDataPayload {
        public static final BuilderCodec<EventDataPayload> CODEC = BuilderCodec.builder(EventDataPayload.class, EventDataPayload::new)
            .append(new KeyedCodec<>("Action", Codec.STRING), (entry, value) -> entry.action = value, entry -> entry.action)
            .add()
            .build();

        private String action;
    }
}
