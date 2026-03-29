package com.qubecore.bankdefense.ui;

import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime;
import com.hypixel.hytale.server.core.entity.entities.player.pages.InteractiveCustomUIPage;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.ui.builder.UIEventBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.qubecore.bankdefense.runtime.BankDefenseRuntime;
import com.qubecore.bankdefense.runtime.BankDefenseRuntime.StatisticsUiState;
import com.qubecore.bankdefense.runtime.support.BankDefenseLocalization;
import javax.annotation.Nonnull;

public final class BankDefenseStatsPage extends InteractiveCustomUIPage<BankDefenseStatsPage.EventDataPayload> {
    private static final String UI_PAGE = "Pages/BankDefenseStatsPage.ui";

    private final PlayerRef viewerRef;
    private final BankDefenseRuntime runtime;

    public BankDefenseStatsPage(PlayerRef playerRef, BankDefenseRuntime runtime) {
        super(playerRef, CustomPageLifetime.CanDismiss, EventDataPayload.CODEC);
        this.viewerRef = playerRef;
        this.runtime = runtime;
    }

    @Override
    public void build(
        @Nonnull Ref<EntityStore> ref,
        @Nonnull UICommandBuilder commandBuilder,
        @Nonnull UIEventBuilder eventBuilder,
        @Nonnull Store<EntityStore> store
    ) {
        commandBuilder.append(UI_PAGE);
        this.syncState(ref, commandBuilder, store);
    }

    @Override
    public void handleDataEvent(@Nonnull Ref<EntityStore> ref, @Nonnull Store<EntityStore> store, @Nonnull EventDataPayload data) {
        UICommandBuilder commandBuilder = new UICommandBuilder();
        UIEventBuilder eventBuilder = new UIEventBuilder();
        this.syncState(ref, commandBuilder, store);
        this.sendUpdate(commandBuilder, eventBuilder, false);
    }

    private void syncState(Ref<EntityStore> ref, UICommandBuilder commandBuilder, Store<EntityStore> store) {
        commandBuilder.set("#TitleLabel.Text", BankDefenseLocalization.tr(this.viewerRef, "page.stats.title"));
        commandBuilder.set("#Header.Text", BankDefenseLocalization.tr(this.viewerRef, "page.stats.header"));
        commandBuilder.set("#LifetimeTitle.Text", BankDefenseLocalization.tr(this.viewerRef, "page.stats.lifetime.title"));
        commandBuilder.set("#BestRunTitle.Text", BankDefenseLocalization.tr(this.viewerRef, "page.stats.best.title"));
        commandBuilder.set("#Hint.Text", BankDefenseLocalization.tr(this.viewerRef, "page.stats.hint"));

        StatisticsUiState state = new StatisticsUiState();
        World world = ref.getStore().getExternalData().getWorld();
        if (world != null && this.runtime != null) {
            try {
                state = this.runtime.getStatisticsUiState(world);
            } catch (Exception ignored) {
            }
        }

        commandBuilder.set("#LifetimeBody.Text", this.buildLifetimeText(state));
        commandBuilder.set("#BestRunBody.Text", this.buildBestRunText(state));
    }

    private String buildLifetimeText(StatisticsUiState state) {
        return String.join(
            "\n",
            this.line("page.stats.total_waves", state.lifetimeWavesCleared),
            this.line("page.stats.total_kills", state.lifetimeEnemyKills),
            this.line("page.stats.total_spent", state.lifetimeSpentCurrency),
            this.line("page.stats.total_traps", state.lifetimeTrapsPlaced),
            this.line("page.stats.total_runs", state.totalRuns),
            this.line("page.stats.total_chests", state.lifetimeOpenedChests),
            this.line("page.stats.total_modules", state.lifetimeModulesInstalled)
        );
    }

    private String buildBestRunText(StatisticsUiState state) {
        return String.join(
            "\n",
            this.line("page.stats.best_waves", state.bestRunWave),
            this.line("page.stats.best_earned", state.bestRunEarnedCurrency),
            this.line("page.stats.best_kills", state.bestRunEnemyKills)
        );
    }

    private String line(String key, long value) {
        return BankDefenseLocalization.tr(this.viewerRef, key) + ": " + value;
    }

    public static final class EventDataPayload {
        public static final BuilderCodec<EventDataPayload> CODEC =
            BuilderCodec.builder(EventDataPayload.class, EventDataPayload::new).build();
    }
}
