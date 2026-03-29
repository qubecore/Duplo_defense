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
import com.qubecore.bankdefense.runtime.BankDefenseRuntime.ContractButtonState;
import com.qubecore.bankdefense.runtime.BankDefenseRuntime.DifficultyButtonState;
import com.qubecore.bankdefense.runtime.BankDefenseRuntime.MatchState;
import com.qubecore.bankdefense.runtime.BankDefenseRuntime.ProgressionUiState;
import com.qubecore.bankdefense.runtime.BankDefenseRuntime.RewardUiState;
import com.qubecore.bankdefense.runtime.BankDefenseRuntime.RuntimeStatus;
import com.qubecore.bankdefense.runtime.support.BankDefenseLocalization;
import java.io.IOException;
import java.util.List;
import javax.annotation.Nonnull;

public final class BankDefenseMatchControlPage extends InteractiveCustomUIPage<BankDefenseMatchControlPage.EventDataPayload> {
    private static final String UI_PAGE = "Pages/BankDefenseMatchControlPage.ui";
    private static final List<String> DIFFICULTY_CARD_SELECTORS = List.of(
        "#DifficultyCard01",
        "#DifficultyCard02",
        "#DifficultyCard03",
        "#DifficultyCard04"
    );
    private static final List<String> DIFFICULTY_TITLE_SELECTORS = List.of(
        "#DifficultyTitle01",
        "#DifficultyTitle02",
        "#DifficultyTitle03",
        "#DifficultyTitle04"
    );
    private static final List<String> DIFFICULTY_NOTE_SELECTORS = List.of(
        "#DifficultyNote01",
        "#DifficultyNote02",
        "#DifficultyNote03",
        "#DifficultyNote04"
    );
    private static final List<String> DIFFICULTY_BUTTON_SELECTORS = List.of(
        "#DifficultyButton01",
        "#DifficultyButton02",
        "#DifficultyButton03",
        "#DifficultyButton04"
    );
    private static final List<String> CONTRACT_CARD_SELECTORS = List.of(
        "#ContractCard01",
        "#ContractCard02",
        "#ContractCard03",
        "#ContractCard04",
        "#ContractCard05"
    );
    private static final List<String> CONTRACT_TITLE_SELECTORS = List.of(
        "#ContractTitle01",
        "#ContractTitle02",
        "#ContractTitle03",
        "#ContractTitle04",
        "#ContractTitle05"
    );
    private static final List<String> CONTRACT_NOTE_SELECTORS = List.of(
        "#ContractNote01",
        "#ContractNote02",
        "#ContractNote03",
        "#ContractNote04",
        "#ContractNote05"
    );
    private static final List<String> CONTRACT_BUTTON_SELECTORS = List.of(
        "#ContractButton01",
        "#ContractButton02",
        "#ContractButton03",
        "#ContractButton04",
        "#ContractButton05"
    );

    private static final String STAGE_DIFFICULTY = "difficulty";
    private static final String STAGE_CONTRACT = "contract";
    private static final String STAGE_CONTROL = "control";

    private final BankDefenseRuntime runtime;
    private final PlayerRef viewerRef;
    private String hint = "";
    private String stage = STAGE_DIFFICULTY;
    private String selectedDifficultyId = "normal";

    public BankDefenseMatchControlPage(PlayerRef playerRef, BankDefenseRuntime runtime) {
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
            boolean closeAfterPrepare = result.success && (data.action.startsWith("contract:") || (data.action.startsWith("difficulty:") && this.runtime.isDuoGameplayMode(player.getWorld())));
            boolean closeAfterRewardOpen = result.success && "match:reward".equals(data.action);
            if (closeAfterPrepare || closeAfterRewardOpen) {
                this.close();
                return;
            }
        } catch (IOException e) {
            this.hint = BankDefenseLocalization.tr(this.viewerRef, "page.match.error", e.getMessage());
            this.runtime.playUiErrorSound(player.getWorld(), this.viewerRef);
            player.sendMessage(Message.raw(BankDefenseLocalization.translateFreeform(this.viewerRef, this.hint)));
        }

        this.syncState(ref, store, commandBuilder);
        this.sendUpdate(commandBuilder, eventBuilder, false);
    }

    private void bindEvents(UIEventBuilder eventBuilder) {
        this.bindAction(eventBuilder, "#DifficultyButton01", "difficulty:easy");
        this.bindAction(eventBuilder, "#DifficultyButton02", "difficulty:normal");
        this.bindAction(eventBuilder, "#DifficultyButton03", "difficulty:hard");
        this.bindAction(eventBuilder, "#DifficultyButton04", "difficulty:nightmare");
        this.bindAction(eventBuilder, "#ContractButton01", "contract:0");
        this.bindAction(eventBuilder, "#ContractButton02", "contract:1");
        this.bindAction(eventBuilder, "#ContractButton03", "contract:2");
        this.bindAction(eventBuilder, "#ContractButton04", "contract:3");
        this.bindAction(eventBuilder, "#ContractButton05", "contract:4");
        this.bindAction(eventBuilder, "#RewardButton", "match:reward");
        this.bindAction(eventBuilder, "#StartWave", "match:start_wave");
        this.bindAction(eventBuilder, "#AutoStartToggle", "match:auto_toggle");
        this.bindAction(eventBuilder, "#ResetMatch", "match:end");
    }

    private void bindAction(UIEventBuilder eventBuilder, String selector, String action) {
        eventBuilder.addEventBinding(CustomUIEventBindingType.Activating, selector, EventData.of("Action", action), false);
    }

    private void syncState(Ref<EntityStore> ref, Store<EntityStore> store, UICommandBuilder commandBuilder) {
        Player player = store.getComponent(ref, Player.getComponentType());
        World world = player == null ? null : player.getWorld();
        boolean tutorialActive = world != null && this.runtime.isTutorialActive(world);
        boolean duoMode = world != null && this.runtime.isDuoGameplayMode(world);

        RuntimeStatus status = null;
        ProgressionUiState progression = null;
        RewardUiState rewardState = null;
        List<DifficultyButtonState> difficulties = this.runtime.getDifficultyOptions(world);
        String difficultyName = BankDefenseLocalization.difficultyDisplayName(this.viewerRef, "normal", "Normal");
        boolean matchPrepared = false;
        String waveText = BankDefenseLocalization.tr(this.viewerRef, "label.wave.none");
        String balanceText = BankDefenseLocalization.tr(this.viewerRef, "label.funds.none");
        String bankText = BankDefenseLocalization.tr(this.viewerRef, "label.bank.none");
        String statusText = BankDefenseLocalization.tr(this.viewerRef, "label.status.none");
        String autoStartText = BankDefenseLocalization.tr(this.viewerRef, "page.match.auto_start_off");

        if (world != null) {
            try {
                this.selectedDifficultyId = this.runtime.getSelectedDifficultyId(world);
                difficultyName = BankDefenseLocalization.translateFreeform(this.viewerRef, this.runtime.getSelectedDifficultyName(world));
                status = this.runtime.getRuntimeStatus(world);
                progression = this.runtime.getProgressionUiState(world);
                rewardState = this.runtime.getRewardUiState(world, this.viewerRef);
                MatchState match = status.matchState;
                if (match != null) {
                    matchPrepared = match.gameStarted
                        && match.gameState != BankDefenseRuntime.GameState.Defeat
                        && match.gameState != BankDefenseRuntime.GameState.Victory;
                    waveText = BankDefenseLocalization.tr(this.viewerRef, "label.wave", match.currentWave);
                    balanceText = BankDefenseLocalization.tr(this.viewerRef, "label.funds", match.currency);
                    bankText = BankDefenseLocalization.tr(this.viewerRef, "label.bank", match.bankHp + " / " + match.maxBankHp);
                    statusText = BankDefenseLocalization.tr(this.viewerRef, "label.status", this.describeMatchState(match));
                    autoStartText = BankDefenseLocalization.tr(this.viewerRef, match.instantAutoStart ? "page.match.auto_start_on" : "page.match.auto_start_off");
                }
            } catch (IOException e) {
                this.hint = BankDefenseLocalization.tr(this.viewerRef, "page.match.read_error", e.getMessage());
            }
        }

        if (matchPrepared || tutorialActive) {
            this.stage = STAGE_CONTROL;
        }

        boolean showDifficulty = !tutorialActive && !matchPrepared && STAGE_DIFFICULTY.equals(this.stage);
        boolean showContract = !tutorialActive && !matchPrepared && !duoMode && STAGE_CONTRACT.equals(this.stage);
        boolean showControl = tutorialActive || STAGE_CONTROL.equals(this.stage);
        boolean rewardButtonVisible = showControl && !tutorialActive && rewardState != null && rewardState.rewardPending && this.viewerCanChooseReward(rewardState);

        commandBuilder.set("#TitleLabel.Text", BankDefenseLocalization.tr(this.viewerRef, tutorialActive ? "page.match.title.tutorial" : "page.match.title"));
        commandBuilder.set("#SetupPanel.Visible", !tutorialActive && !showControl);
        commandBuilder.set("#DifficultyPanel.Visible", showDifficulty);
        commandBuilder.set("#ContractPanel.Visible", showContract);
        commandBuilder.set("#ControlPanel.Visible", showControl);
        commandBuilder.set(
            "#Hint.Text",
            tutorialActive
                ? BankDefenseLocalization.tr(this.viewerRef, "page.match.hint.tutorial")
                : BankDefenseLocalization.translateFreeform(
                    this.viewerRef,
                    this.hint == null || this.hint.isBlank()
                        ? BankDefenseLocalization.tr(this.viewerRef, duoMode ? "page.match.hint.duo" : "page.match.hint")
                        : this.hint
                )
        );
        commandBuilder.set("#DifficultySubtitle.Text", BankDefenseLocalization.tr(this.viewerRef, "page.match.step1"));
        commandBuilder.set(
            "#ContractSubtitle.Text",
            BankDefenseLocalization.tr(this.viewerRef, duoMode ? "page.match.step2.duo" : "page.match.step2", difficultyName)
        );

        this.syncDifficultyCards(commandBuilder, difficulties);
        this.syncContractCards(commandBuilder, progression);

        commandBuilder.set("#Wave.Text", waveText);
        commandBuilder.set("#Balance.Text", balanceText);
        commandBuilder.set("#Bank.Text", bankText);
        commandBuilder.set(
            "#Status.Text",
            tutorialActive
                ? BankDefenseLocalization.tr(this.viewerRef, "label.status", this.describeTutorialStatus(status == null ? null : status.matchState))
                : statusText
        );
        commandBuilder.set("#ActionHeader.Text", BankDefenseLocalization.tr(this.viewerRef, tutorialActive ? "page.match.training_wave" : "page.match.prepared"));
        commandBuilder.set("#RewardButton.Text", BankDefenseLocalization.tr(this.viewerRef, "page.match.open_reward"));
        commandBuilder.set("#RewardButton.Visible", rewardButtonVisible);
        commandBuilder.set("#StartWave.Text", BankDefenseLocalization.tr(this.viewerRef, "page.match.start_wave"));
        commandBuilder.set("#AutoStartToggle.Text", autoStartText);
        commandBuilder.set("#ResetMatch.Text", BankDefenseLocalization.tr(this.viewerRef, "page.match.end_match"));
        commandBuilder.set("#AutoStartToggle.Visible", !tutorialActive);
        commandBuilder.set("#ResetMatch.Visible", !tutorialActive);
    }

    private void syncDifficultyCards(UICommandBuilder commandBuilder, List<DifficultyButtonState> difficulties) {
        for (int index = 0; index < DIFFICULTY_CARD_SELECTORS.size(); index++) {
            boolean visible = difficulties != null && index < difficulties.size();
            commandBuilder.set(DIFFICULTY_CARD_SELECTORS.get(index) + ".Visible", visible);
            if (!visible) {
                continue;
            }
            DifficultyButtonState difficulty = difficulties.get(index);
            commandBuilder.set(
                DIFFICULTY_TITLE_SELECTORS.get(index) + ".Text",
                BankDefenseLocalization.difficultyDisplayName(this.viewerRef, difficulty.difficultyId, safeText(difficulty.displayName, "Difficulty"))
            );
            commandBuilder.set(
                DIFFICULTY_NOTE_SELECTORS.get(index) + ".Text",
                BankDefenseLocalization.difficultyNote(this.viewerRef, difficulty.difficultyId, safeText(difficulty.note, ""))
            );
            commandBuilder.set(
                DIFFICULTY_BUTTON_SELECTORS.get(index) + ".Text",
                !difficulty.enabled
                    ? BankDefenseLocalization.tr(this.viewerRef, "page.super.unavailable")
                    : difficulty.difficultyId != null && difficulty.difficultyId.equals(this.selectedDifficultyId)
                        ? BankDefenseLocalization.tr(this.viewerRef, "page.match.selected")
                        : BankDefenseLocalization.tr(this.viewerRef, "page.match.select")
            );
        }
    }

    private void syncContractCards(UICommandBuilder commandBuilder, ProgressionUiState progression) {
        List<ContractButtonState> contracts = progression == null ? List.of() : progression.contracts;
        for (int index = 0; index < CONTRACT_CARD_SELECTORS.size(); index++) {
            boolean visible = index < contracts.size();
            commandBuilder.set(CONTRACT_CARD_SELECTORS.get(index) + ".Visible", visible);
            if (!visible) {
                continue;
            }
            ContractButtonState contract = contracts.get(index);
            commandBuilder.set(
                CONTRACT_TITLE_SELECTORS.get(index) + ".Text",
                BankDefenseLocalization.contractDisplayName(this.viewerRef, contract.contractId, safeText(contract.displayName, "Contract"))
            );
            commandBuilder.set(
                CONTRACT_NOTE_SELECTORS.get(index) + ".Text",
                BankDefenseLocalization.contractNote(this.viewerRef, contract.contractId, safeText(contract.note, ""))
            );
            commandBuilder.set(
                CONTRACT_BUTTON_SELECTORS.get(index) + ".Text",
                !contract.enabled
                    ? BankDefenseLocalization.tr(this.viewerRef, "page.super.unavailable")
                    : contract.active
                        ? BankDefenseLocalization.tr(this.viewerRef, "page.match.select_active")
                        : BankDefenseLocalization.tr(this.viewerRef, "page.match.select")
            );
        }
    }

    private String describeMatchState(MatchState match) {
        if (match.gameState == BankDefenseRuntime.GameState.Ready && !match.gameStarted) {
            return BankDefenseLocalization.tr(this.viewerRef, "page.match.start_waiting");
        }
        if (match.gameState == BankDefenseRuntime.GameState.Ready && match.preparationRemainingSeconds > 0.0) {
            return BankDefenseLocalization.tr(this.viewerRef, "hud.preparation_s", (int)Math.ceil(match.preparationRemainingSeconds));
        }
        if (match.gameState == BankDefenseRuntime.GameState.Ready && match.gameStarted) {
            return BankDefenseLocalization.tr(this.viewerRef, "page.match.build_first_wave");
        }
        return BankDefenseLocalization.translateFreeform(this.viewerRef, match.gameState + " / " + match.waveState);
    }

    private String describeTutorialStatus(MatchState match) {
        if (match == null || !match.gameStarted) {
            return BankDefenseLocalization.tr(this.viewerRef, "page.match.status.tutorial.ready");
        }
        if (match.gameState == BankDefenseRuntime.GameState.InMatch) {
            return BankDefenseLocalization.tr(this.viewerRef, "page.match.status.tutorial.active");
        }
        if (match.gameState == BankDefenseRuntime.GameState.Ready) {
            return BankDefenseLocalization.tr(this.viewerRef, "page.match.status.tutorial.manual");
        }
        if (match.gameState == BankDefenseRuntime.GameState.Defeat) {
            return BankDefenseLocalization.tr(this.viewerRef, "page.match.status.tutorial.stopped");
        }
        return this.describeMatchState(match);
    }

    private ActionResult handleAction(World world, String action) throws IOException {
        if (world != null && this.runtime.isTutorialActive(world)) {
            return switch (action) {
                case "match:start_wave" -> this.runtime.startNextWave(world);
                case "match:auto_toggle" -> ActionResult.fail(BankDefenseLocalization.tr(this.viewerRef, "page.match.tutorial.auto_disabled"));
                case "match:end" -> ActionResult.fail(BankDefenseLocalization.tr(this.viewerRef, "page.match.tutorial.end_unavailable"));
                default -> ActionResult.fail(BankDefenseLocalization.tr(this.viewerRef, "page.match.tutorial.only_start_available"));
            };
        }
        if (action.startsWith("difficulty:")) {
            String difficultyId = action.substring("difficulty:".length());
            DifficultyButtonState selected = this.runtime.getDifficultyOptions(world).stream()
                .filter(option -> option.difficultyId != null && option.difficultyId.equals(difficultyId))
                .findFirst()
                .orElse(null);
            if (selected == null || !selected.enabled) {
                return ActionResult.fail(BankDefenseLocalization.tr(this.viewerRef, "page.match.difficulty_unavailable"));
            }
            this.selectedDifficultyId = difficultyId;
            if (this.runtime.isDuoGameplayMode(world)) {
                this.stage = STAGE_CONTROL;
                return this.runtime.prepareMatch(world, this.selectedDifficultyId, "none");
            }
            this.stage = STAGE_CONTRACT;
            return ActionResult.ok(BankDefenseLocalization.tr(this.viewerRef, "page.match.difficulty_selected"));
        }
        if (action.startsWith("contract:")) {
            if (this.runtime.isDuoGameplayMode(world)) {
                return ActionResult.fail(BankDefenseLocalization.tr(this.viewerRef, "page.match.contracts_disabled_duo"));
            }
            ProgressionUiState state = this.runtime.getProgressionUiState(world);
            int index = Integer.parseInt(action.substring("contract:".length()));
            if (index < 0 || index >= state.contracts.size()) {
                return ActionResult.fail(BankDefenseLocalization.tr(this.viewerRef, "page.match.contract_not_found"));
            }
            if (!state.contracts.get(index).enabled) {
                return ActionResult.fail(BankDefenseLocalization.tr(this.viewerRef, "page.match.contract_unavailable"));
            }
            this.stage = STAGE_CONTROL;
            return this.runtime.prepareMatch(world, this.selectedDifficultyId, state.contracts.get(index).contractId);
        }
        return switch (action) {
            case "match:reward" -> this.runtime.reopenPendingRewardPage(world, this.viewerRef);
            case "match:start_wave" -> this.runtime.startPreparedWave(world);
            case "match:auto_toggle" -> this.runtime.toggleInstantAutoStart(world);
            case "match:end" -> {
                this.stage = STAGE_DIFFICULTY;
                yield this.runtime.endMatchAsDefeat(world);
            }
            default -> ActionResult.fail(BankDefenseLocalization.tr(this.viewerRef, "page.match.unknown_action", action));
        };
    }

    private static String safeText(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value;
    }

    private boolean viewerCanChooseReward(RewardUiState rewardState) {
        if (rewardState == null || !rewardState.rewardPending) {
            return false;
        }
        return rewardState.choiceButtons.stream().anyMatch(button -> button != null && button.enabled);
    }

    public static final class EventDataPayload {
        public static final BuilderCodec<EventDataPayload> CODEC = BuilderCodec.builder(EventDataPayload.class, EventDataPayload::new)
            .append(new KeyedCodec<>("Action", Codec.STRING), (entry, value) -> entry.action = value, entry -> entry.action)
            .add()
            .build();

        private String action;
    }
}
