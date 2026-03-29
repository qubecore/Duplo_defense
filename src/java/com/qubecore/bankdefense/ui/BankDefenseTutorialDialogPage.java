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
import com.qubecore.bankdefense.runtime.BankDefenseRuntime.TutorialDialogUiState;
import com.qubecore.bankdefense.runtime.support.BankDefenseLocalization;
import java.io.IOException;
import javax.annotation.Nonnull;

public final class BankDefenseTutorialDialogPage extends InteractiveCustomUIPage<BankDefenseTutorialDialogPage.EventDataPayload> {
    private static final String UI_PAGE = "Pages/BankDefenseTutorialDialogPage.ui";
    private static final String AVATAR_DEFAULT = "default";
    private static final String AVATAR_HELLO = "hello";

    private final BankDefenseRuntime runtime;
    private final String wizardKind;
    private final PlayerRef viewerRef;
    private String hint = "";

    public BankDefenseTutorialDialogPage(PlayerRef playerRef, BankDefenseRuntime runtime, String wizardKind) {
        super(playerRef, CustomPageLifetime.CanDismiss, EventDataPayload.CODEC);
        this.runtime = runtime;
        this.wizardKind = wizardKind;
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

        TutorialDialogUiState currentState = null;
        try {
            currentState = this.runtime.getTutorialDialogUiState(player.getWorld(), this.wizardKind);
        } catch (IOException ignored) {
        }

        UICommandBuilder commandBuilder = new UICommandBuilder();
        UIEventBuilder eventBuilder = new UIEventBuilder();
        this.bindEvents(eventBuilder);

        try {
            ActionResult result = this.runtime.advanceTutorialDialog(player.getWorld(), ref, this.wizardKind);
            this.hint = result.message;
            this.runtime.playUiActionFeedback(player.getWorld(), this.viewerRef, result);
            if (result.message != null && !result.message.isBlank()) {
                player.sendMessage(Message.raw(BankDefenseLocalization.translateFreeform(this.viewerRef, result.message)));
            }
            if (result.success && currentState != null && currentState.closeOnAdvance) {
                this.close();
                return;
            }
        } catch (IOException e) {
            this.hint = BankDefenseLocalization.tr(this.viewerRef, "page.tutorial.error", e.getMessage());
            this.runtime.playUiErrorSound(player.getWorld(), this.viewerRef);
            player.sendMessage(Message.raw(BankDefenseLocalization.translateFreeform(this.viewerRef, this.hint)));
        }

        TutorialDialogUiState state;
        try {
            state = this.runtime.getTutorialDialogUiState(player.getWorld(), this.wizardKind);
        } catch (IOException e) {
            state = null;
        }
        if (state == null || !state.visible) {
            this.close();
            return;
        }

        this.syncState(ref, store, commandBuilder);
        this.sendUpdate(commandBuilder, eventBuilder, false);
    }

    private void bindEvents(UIEventBuilder eventBuilder) {
        eventBuilder.addEventBinding(CustomUIEventBindingType.Activating, "#AdvanceButton", EventData.of("Action", "advance"), false);
    }

    private void syncState(Ref<EntityStore> ref, Store<EntityStore> store, UICommandBuilder commandBuilder) {
        Player player = store.getComponent(ref, Player.getComponentType());
        World world = player == null ? null : player.getWorld();
        TutorialDialogUiState state = null;
        if (world != null) {
            try {
                state = this.runtime.getTutorialDialogUiState(world, this.wizardKind);
            } catch (IOException e) {
                this.hint = BankDefenseLocalization.tr(this.viewerRef, "page.tutorial.load_error", e.getMessage());
            }
        }

        commandBuilder.set("#TitleLabel.Text", BankDefenseLocalization.tr(this.viewerRef, "page.tutorial.title"));
        commandBuilder.set(
            "#Speaker.Text",
            state == null
                ? BankDefenseLocalization.tr(this.viewerRef, "page.tutorial.default_speaker")
                : BankDefenseLocalization.translateFreeform(this.viewerRef, state.speakerName)
        );
        commandBuilder.set("#Body.Text", state == null ? "" : BankDefenseLocalization.translateFreeform(this.viewerRef, state.bodyText));
        commandBuilder.set(
            "#AdvanceButton.Text",
            state == null
                ? BankDefenseLocalization.tr(this.viewerRef, "page.tutorial.continue")
                : BankDefenseLocalization.translateFreeform(this.viewerRef, state.buttonText)
        );
        commandBuilder.set(
            "#Hint.Text",
            state == null
                ? BankDefenseLocalization.translateFreeform(
                    this.viewerRef,
                    this.hint == null || this.hint.isBlank() ? BankDefenseLocalization.tr(this.viewerRef, "page.tutorial.hint") : this.hint
                )
                : BankDefenseLocalization.translateFreeform(this.viewerRef, state.hintText)
        );
        String avatarKind = state == null || state.avatarKind == null || state.avatarKind.isBlank() ? AVATAR_DEFAULT : state.avatarKind;
        commandBuilder.set("#AvatarDefault.Visible", AVATAR_DEFAULT.equals(avatarKind));
        commandBuilder.set("#AvatarHello.Visible", AVATAR_HELLO.equals(avatarKind));
    }

    public static final class EventDataPayload {
        public static final BuilderCodec<EventDataPayload> CODEC = BuilderCodec.builder(EventDataPayload.class, EventDataPayload::new)
            .append(new KeyedCodec<>("Action", Codec.STRING), (entry, value) -> entry.action = value, entry -> entry.action)
            .add()
            .build();

        private String action;
    }
}
