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
import com.qubecore.bankdefense.runtime.support.BankDefenseLocalization;
import java.io.IOException;
import javax.annotation.Nonnull;

public final class BankDefenseDuoInfoPage extends InteractiveCustomUIPage<BankDefenseDuoInfoPage.EventDataPayload> {
    private static final String UI_PAGE = "Pages/BankDefenseDuoInfoPage.ui";
    private static final String TG_URL = "https://t.me/qubecore";
    private static final String YOUTUBE_URL = "https://www.youtube.com/@QubeCore_Hytale";
    private static final String DISCORD_URL = "https://discord.gg/TMdmJfwhCH";

    private final BankDefenseRuntime runtime;
    private final PlayerRef viewerRef;
    private String hint = "";

    public BankDefenseDuoInfoPage(PlayerRef playerRef, BankDefenseRuntime runtime) {
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
        Player player = store.getComponent(ref, Player.getComponentType());
        this.syncState(commandBuilder, player == null ? null : player.getWorld());
    }

    @Override
    public void handleDataEvent(@Nonnull Ref<EntityStore> ref, @Nonnull Store<EntityStore> store, @Nonnull EventDataPayload data) {
        if (data.action == null || data.action.isBlank()) {
            return;
        }
        Player player = store.getComponent(ref, Player.getComponentType());
        if (player == null) {
            return;
        }

        switch (data.action) {
            case "open:tg" -> this.openUrl(player, TG_URL, "Telegram");
            case "open:youtube" -> this.openUrl(player, YOUTUBE_URL, "YouTube");
            case "open:discord" -> this.openUrl(player, DISCORD_URL, "Discord");
            default -> this.hint = BankDefenseLocalization.tr(this.viewerRef, "page.duo.unknown", data.action);
        }

        UICommandBuilder commandBuilder = new UICommandBuilder();
        UIEventBuilder eventBuilder = new UIEventBuilder();
        this.bindEvents(eventBuilder);
        this.syncState(commandBuilder, player.getWorld());
        this.sendUpdate(commandBuilder, eventBuilder, false);
    }

    private void bindEvents(UIEventBuilder eventBuilder) {
        eventBuilder.addEventBinding(CustomUIEventBindingType.Activating, "#TelegramButton", EventData.of("Action", "open:tg"), false);
        eventBuilder.addEventBinding(CustomUIEventBindingType.Activating, "#YouTubeButton", EventData.of("Action", "open:youtube"), false);
        eventBuilder.addEventBinding(CustomUIEventBindingType.Activating, "#DiscordButton", EventData.of("Action", "open:discord"), false);
    }

    private void syncState(UICommandBuilder commandBuilder, World world) {
        boolean duo = world != null && this.runtime != null && this.runtime.isDuoGameplayMode(world);
        commandBuilder.set("#TitleLabel.Text", BankDefenseLocalization.tr(this.viewerRef, "page.duo.title"));
        commandBuilder.set("#Header.Text", duo ? BankDefenseLocalization.tr(this.viewerRef, "page.duo.header.duo") : BankDefenseLocalization.tr(this.viewerRef, "page.duo.header.solo"));
        commandBuilder.set("#Body.Text", duo ? BankDefenseLocalization.tr(this.viewerRef, "page.duo.body.duo") : BankDefenseLocalization.tr(this.viewerRef, "page.duo.body.solo"));
        commandBuilder.set("#TelegramButton.Text", "Telegram");
        commandBuilder.set("#YouTubeButton.Text", "YouTube");
        commandBuilder.set("#DiscordButton.Text", "Discord");
        commandBuilder.set(
            "#Hint.Text",
            BankDefenseLocalization.translateFreeform(
                this.viewerRef,
                this.hint == null || this.hint.isBlank() ? BankDefenseLocalization.tr(this.viewerRef, "page.duo.hint") : this.hint
            )
        );
    }

    private void openUrl(Player player, String url, String label) {
        try {
            new ProcessBuilder("cmd", "/c", "start", "", url).start();
            this.hint = BankDefenseLocalization.tr(this.viewerRef, "page.duo.opening", label);
            return;
        } catch (IOException ignored) {
        }

        try {
            new ProcessBuilder("powershell", "-NoProfile", "-Command", "Start-Process '" + url + "'").start();
            this.hint = BankDefenseLocalization.tr(this.viewerRef, "page.duo.opening", label);
        } catch (IOException e) {
            this.hint = label + ": " + url;
            player.sendMessage(Message.raw(BankDefenseLocalization.tr(this.viewerRef, "page.duo.open_failed", label, url)));
        }
    }

    public static final class EventDataPayload {
        public static final BuilderCodec<EventDataPayload> CODEC = BuilderCodec.builder(EventDataPayload.class, EventDataPayload::new)
            .append(new KeyedCodec<>("Action", Codec.STRING), (entry, value) -> entry.action = value, entry -> entry.action)
            .add()
            .build();

        private String action;
    }
}
