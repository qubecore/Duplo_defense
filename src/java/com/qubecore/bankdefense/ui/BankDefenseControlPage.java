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
import com.qubecore.bankdefense.runtime.BankDefenseRuntime.MatchState;
import com.qubecore.bankdefense.runtime.BankDefenseRuntime.ProgressionUiState;
import com.qubecore.bankdefense.runtime.BankDefenseRuntime.RuntimeStatus;
import com.qubecore.bankdefense.runtime.support.BankDefenseLocalization;
import java.io.IOException;
import javax.annotation.Nonnull;

public final class BankDefenseControlPage extends InteractiveCustomUIPage<BankDefenseControlPage.EventDataPayload> {
    private static final String UI_PAGE = "Pages/BankDefenseControlPage.ui";

    private final BankDefenseRuntime runtime;
    private final PlayerRef viewerRef;
    private String hint = "";
    private String waveInputDraft = "1";
    private boolean waveInputTouched;

    public BankDefenseControlPage(PlayerRef playerRef, BankDefenseRuntime runtime) {
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
        if (data.waveInput != null) {
            String normalized = data.waveInput.trim();
            if (!normalized.isEmpty()) {
                this.waveInputDraft = normalized;
                this.waveInputTouched = true;
            }
        }
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
            ActionResult result = this.handleAction(player.getWorld(), data);
            this.hint = result.message;
            this.runtime.playUiActionFeedback(player.getWorld(), this.viewerRef, result);
            if (result.message != null && !result.message.isBlank()) {
                player.sendMessage(Message.raw(BankDefenseLocalization.translateFreeform(this.viewerRef, result.message)));
            }
        } catch (IOException e) {
            this.hint = BankDefenseLocalization.tr(this.viewerRef, "page.control.error", e.getMessage());
            this.runtime.playUiErrorSound(player.getWorld(), this.viewerRef);
            player.sendMessage(Message.raw(BankDefenseLocalization.translateFreeform(this.viewerRef, this.hint)));
        }

        this.syncState(ref, store, commandBuilder);
        this.sendUpdate(commandBuilder, eventBuilder, false);
    }

    private void bindEvents(UIEventBuilder eventBuilder) {
        this.bindAction(eventBuilder, "#MoneyPlus100", "money:+100");
        this.bindAction(eventBuilder, "#MoneyPlus1000", "money:+1000");
        this.bindAction(eventBuilder, "#MoneyPlus5000", "money:+5000");
        this.bindAction(eventBuilder, "#MoneyMinus100", "money:-100");
        this.bindAction(eventBuilder, "#MoneyMinus1000", "money:-1000");
        this.bindAction(eventBuilder, "#MoneyMinus5000", "money:-5000");
        this.bindAction(eventBuilder, "#CoresPlus10", "cores:+10");
        this.bindAction(eventBuilder, "#CoresPlus50", "cores:+50");
        this.bindAction(eventBuilder, "#CoresPlus100", "cores:+100");
        this.bindAction(eventBuilder, "#GrantModules", "modules:grant_all");
        this.bindAction(eventBuilder, "#ToggleRanges", "visual:toggle_ranges");
        eventBuilder.addEventBinding(
            CustomUIEventBindingType.ValueChanged,
            "#WaveInput",
            EventData.of("@WaveInput", "#WaveInput.Value"),
            false
        );
        eventBuilder.addEventBinding(
            CustomUIEventBindingType.Activating,
            "#SetWave",
            new EventData().append("Action", "wave:set").append("@WaveInput", "#WaveInput.Value"),
            false
        );
    }

    private void bindAction(UIEventBuilder eventBuilder, String selector, String action) {
        eventBuilder.addEventBinding(CustomUIEventBindingType.Activating, selector, EventData.of("Action", action), false);
    }

    private void syncState(Ref<EntityStore> ref, Store<EntityStore> store, UICommandBuilder commandBuilder) {
        Player player = store.getComponent(ref, Player.getComponentType());
        World world = player == null ? null : player.getWorld();
        String waveText = BankDefenseLocalization.tr(this.viewerRef, "label.wave.none");
        String balanceText = BankDefenseLocalization.tr(this.viewerRef, "label.funds.none");
        String bankText = BankDefenseLocalization.tr(this.viewerRef, "label.bank.none");
        String statusText = BankDefenseLocalization.tr(this.viewerRef, "label.status.none");
        String coresText = BankDefenseLocalization.tr(this.viewerRef, "page.control.cores", 0);
        String suggestedWaveInput = "1";
        boolean rangePreviewEnabled = false;

        if (world != null) {
            try {
                RuntimeStatus status = this.runtime.getRuntimeStatus(world);
                MatchState match = status.matchState;
                if (match != null) {
                    waveText = BankDefenseLocalization.tr(this.viewerRef, "label.wave", match.currentWave);
                    balanceText = BankDefenseLocalization.tr(this.viewerRef, "label.funds", match.currency);
                    bankText = BankDefenseLocalization.tr(this.viewerRef, "label.bank", match.bankHp);
                    statusText = BankDefenseLocalization.tr(this.viewerRef, "label.status", this.describeMatchState(match));
                    suggestedWaveInput = Integer.toString(Math.max(1, match.currentWave));
                }
                ProgressionUiState progression = this.runtime.getProgressionUiState(world);
                coresText = BankDefenseLocalization.tr(this.viewerRef, "page.control.cores", progression.cores);
                rangePreviewEnabled = this.runtime.isTowerRangePreviewEnabled(world);
            } catch (IOException e) {
                this.hint = BankDefenseLocalization.tr(this.viewerRef, "page.control.read_error", e.getMessage());
            }
        }

        if (!this.waveInputTouched || this.waveInputDraft == null || this.waveInputDraft.isBlank()) {
            this.waveInputDraft = suggestedWaveInput;
        }

        commandBuilder.set("#TitleLabel.Text", BankDefenseLocalization.tr(this.viewerRef, "page.control.title"));
        commandBuilder.set("#Subtitle.Text", BankDefenseLocalization.tr(this.viewerRef, "page.control.subtitle"));
        commandBuilder.set("#Wave.Text", waveText);
        commandBuilder.set("#Balance.Text", balanceText);
        commandBuilder.set("#Bank.Text", bankText);
        commandBuilder.set("#Status.Text", statusText);
        commandBuilder.set("#Cores.Text", coresText);
        commandBuilder.set("#MoneyHeader.Text", BankDefenseLocalization.tr(this.viewerRef, "page.control.money.header"));
        commandBuilder.set("#CoreHeader.Text", BankDefenseLocalization.tr(this.viewerRef, "page.control.cores.header"));
        commandBuilder.set("#ModuleHeader.Text", BankDefenseLocalization.tr(this.viewerRef, "page.control.modules.header"));
        commandBuilder.set("#WaveHeader.Text", BankDefenseLocalization.tr(this.viewerRef, "page.control.wave.header"));
        commandBuilder.set("#GrantModules.Text", BankDefenseLocalization.tr(this.viewerRef, "page.control.modules.grant"));
        commandBuilder.set(
            "#ToggleRanges.Text",
            BankDefenseLocalization.tr(this.viewerRef, rangePreviewEnabled ? "page.control.ranges.on" : "page.control.ranges.off")
        );
        commandBuilder.set("#SetWave.Text", BankDefenseLocalization.tr(this.viewerRef, "page.control.set_wave"));
        commandBuilder.set("#WaveInput.PlaceholderText", BankDefenseLocalization.tr(this.viewerRef, "page.control.wave.placeholder"));
        commandBuilder.set("#WaveInput.Value", this.waveInputDraft);
        commandBuilder.set(
            "#Hint.Text",
            BankDefenseLocalization.translateFreeform(
                this.viewerRef,
                this.hint == null || this.hint.isBlank() ? BankDefenseLocalization.tr(this.viewerRef, "page.control.hint") : this.hint
            )
        );
    }

    private String describeMatchState(MatchState match) {
        if (match.gameState == BankDefenseRuntime.GameState.Ready && !match.gameStarted) {
            return BankDefenseLocalization.tr(this.viewerRef, "page.match.start_waiting");
        }
        if (match.gameState == BankDefenseRuntime.GameState.Ready && match.preparationRemainingSeconds > 0.0) {
            return BankDefenseLocalization.tr(this.viewerRef, "hud.preparation_s", (int)Math.ceil(match.preparationRemainingSeconds));
        }
        return BankDefenseLocalization.translateFreeform(this.viewerRef, match.gameState + " / " + match.waveState);
    }

    private ActionResult handleAction(World world, EventDataPayload data) throws IOException {
        String action = data.action;
        if (action.startsWith("money:")) {
            int amount = Integer.parseInt(action.substring("money:".length()));
            return this.runtime.changeCurrency(world, amount);
        }
        if (action.startsWith("cores:")) {
            int amount = Integer.parseInt(action.substring("cores:".length()));
            return this.runtime.grantProgressionCores(world, amount);
        }
        if ("modules:grant_all".equals(action)) {
            return this.runtime.grantAllModules(world);
        }
        if ("visual:toggle_ranges".equals(action)) {
            return this.runtime.toggleTowerRangePreview(world);
        }
        if ("wave:set".equals(action)) {
            int wave = this.parseWaveInput(this.waveInputDraft);
            ActionResult result = this.runtime.setWave(world, wave);
            if (result.success) {
                this.waveInputDraft = Integer.toString(wave);
                this.waveInputTouched = true;
            }
            return result;
        }
        return ActionResult.fail(BankDefenseLocalization.tr(this.viewerRef, "page.control.unknown_action", action));
    }

    private int parseWaveInput(String value) {
        if (value == null || value.isBlank()) {
            return 1;
        }
        try {
            return Math.max(1, Integer.parseInt(value.trim()));
        } catch (NumberFormatException ignored) {
            return 1;
        }
    }

    public static final class EventDataPayload {
        public static final BuilderCodec<EventDataPayload> CODEC = BuilderCodec.builder(EventDataPayload.class, EventDataPayload::new)
            .append(new KeyedCodec<>("Action", Codec.STRING), (entry, value) -> entry.action = value, entry -> entry.action)
            .add()
            .append(new KeyedCodec<>("@WaveInput", Codec.STRING), (entry, value) -> entry.waveInput = value, entry -> entry.waveInput)
            .add()
            .build();

        private String action;
        private String waveInput;
    }
}
