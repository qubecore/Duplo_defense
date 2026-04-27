package com.qubecore.bankdefense.ui;

import com.hypixel.hytale.server.core.entity.entities.player.hud.CustomUIHud;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.qubecore.bankdefense.runtime.support.BankDefenseLocalization;
import java.util.Objects;

public final class BankDefenseHud extends CustomUIHud {
    private static final String UI_HUD = "Hud/BankDefenseHud.ui";
    private static final long FLASH_DURATION_MS = 900L;
    private static final int SUPER_ICON_HEART = 1;
    private static final int SUPER_ICON_MONOLITH = 2;
    private static final int SUPER_ICON_IDOL = 3;
    private String title = "Duplo TD";
    private String creator = "By QubeCore";
    private String wave = "Wave 1";
    private String status = "Loading";
    private String prompt = "Preparing hollow systems.";
    private boolean waveVisible = true;
    private boolean statusVisible = true;
    private boolean promptVisible = true;
    private boolean cursePanelVisible;
    private String cursePanelTitle = "";
    private String curseLine1 = "";
    private String curseLine2 = "";
    private String curseLine3 = "";
    private boolean rightStatsVisible = true;
    private boolean superMiniVisible;
    private String moneyValue = "0";
    private boolean moneyPaused;
    private String coresValue = "0";
    private String bankValue = "20 / 20";
    private String bankState = "Stable";
    private int bankDangerStage;
    private String enemiesValue = "0 / 0";
    private String enemiesState = "Waiting for wave";
    private int enemyPressureStage;
    private boolean tutorialQuestVisible;
    private String tutorialQuestText = "";
    private boolean nextWavePanelVisible;
    private String nextWaveText = "Wave -";
    private String countdownText = "Starts in -";
    private int nextWaveDifficultyLevel = 1;
    private boolean interactionPromptVisible;
    private String interactionPromptText = "";
    private String superHeartState = "inactive";
    private String superMonolithState = "inactive";
    private String superIdolState = "inactive";
    private int superHeartStage;
    private String superHeartMiniLabel = "-";
    private int superMonolithStage;
    private String superMonolithMiniLabel = "-";
    private int superIdolStage;
    private String superIdolMiniLabel = "-";
    private boolean eventToastVisible;
    private String eventToastText = "";
    private int eventToastStage;
    private int eventToastIconType = 1;
    private boolean duoInfoVisible;
    private int duoTeamIconKind;
    private String duoTeamLabel = "";
    private String duoPartnerMoneyValue = "-";
    private boolean defeatBannerVisible;
    private String defeatBannerText = "";
    private String defeatBannerHint = "";
    private String defeatBannerShardsValue = "0";

    private long moneyFlashUntilMs;
    private long coresFlashUntilMs;
    private long bankFlashUntilMs;
    private int moneyFlashStage;
    private int coresFlashStage;
    private int bankFlashStage;
    private boolean initialized;
    private boolean dirty = true;
    private final PlayerRef playerRef;

    public BankDefenseHud(PlayerRef playerRef) {
        super(playerRef);
        this.playerRef = playerRef;
    }

    public void setState(
        String title,
        String creator,
        String wave,
        String status,
        String prompt,
        boolean waveVisible,
        boolean statusVisible,
        boolean promptVisible,
        boolean cursePanelVisible,
        String cursePanelTitle,
        String curseLine1,
        String curseLine2,
        String curseLine3,
        boolean rightStatsVisible,
        boolean superMiniVisible,
        String moneyValue,
        boolean moneyPaused,
        String coresValue,
        String bankValue,
        String bankState,
        int bankDangerStage,
        String enemiesValue,
        String enemiesState,
        int enemyPressureStage,
        boolean tutorialQuestVisible,
        String tutorialQuestText,
        boolean nextWavePanelVisible,
        String nextWaveText,
        String countdownText,
        int nextWaveDifficultyLevel,
        boolean interactionPromptVisible,
        String interactionPromptText,
        String superHeartState,
        String superMonolithState,
        String superIdolState,
        int superHeartStage,
        String superHeartMiniLabel,
        int superMonolithStage,
        String superMonolithMiniLabel,
        int superIdolStage,
        String superIdolMiniLabel,
        boolean eventToastVisible,
        String eventToastText,
        int eventToastStage,
        int eventToastIconType,
        boolean duoInfoVisible,
        int duoTeamIconKind,
        String duoTeamLabel,
        String duoPartnerMoneyValue,
        boolean defeatBannerVisible,
        String defeatBannerText,
        String defeatBannerHint,
        String defeatBannerShardsValue
    ) {
        long now = System.currentTimeMillis();

        String resolvedTitle = Objects.requireNonNullElse(title, this.title);
        String resolvedCreator = Objects.requireNonNullElse(creator, this.creator);
        String resolvedWave = Objects.requireNonNullElse(wave, this.wave);
        String resolvedStatus = Objects.requireNonNullElse(status, this.status);
        String resolvedPrompt = Objects.requireNonNullElse(prompt, this.prompt);
        String resolvedCursePanelTitle = Objects.requireNonNullElse(cursePanelTitle, "");
        String resolvedCurseLine1 = Objects.requireNonNullElse(curseLine1, "");
        String resolvedCurseLine2 = Objects.requireNonNullElse(curseLine2, "");
        String resolvedCurseLine3 = Objects.requireNonNullElse(curseLine3, "");
        String resolvedSuperHeartMiniLabel = Objects.requireNonNullElse(superHeartMiniLabel, this.superHeartMiniLabel);
        String resolvedSuperMonolithMiniLabel = Objects.requireNonNullElse(superMonolithMiniLabel, this.superMonolithMiniLabel);
        String resolvedSuperIdolMiniLabel = Objects.requireNonNullElse(superIdolMiniLabel, this.superIdolMiniLabel);
        String resolvedMoneyValue = Objects.requireNonNullElse(moneyValue, this.moneyValue);
        String resolvedCoresValue = Objects.requireNonNullElse(coresValue, this.coresValue);
        String resolvedBankValue = Objects.requireNonNullElse(bankValue, this.bankValue);
        String resolvedBankState = Objects.requireNonNullElse(bankState, this.bankState);
        String resolvedEnemiesValue = Objects.requireNonNullElse(enemiesValue, this.enemiesValue);
        String resolvedEnemiesState = Objects.requireNonNullElse(enemiesState, this.enemiesState);
        String resolvedTutorialQuestText = Objects.requireNonNullElse(tutorialQuestText, "");
        String resolvedNextWaveText = Objects.requireNonNullElse(nextWaveText, this.nextWaveText);
        String resolvedCountdownText = Objects.requireNonNullElse(countdownText, this.countdownText);
        String resolvedInteractionPromptText = Objects.requireNonNullElse(interactionPromptText, "");
        String resolvedSuperHeartState = Objects.requireNonNullElse(superHeartState, this.superHeartState);
        String resolvedSuperMonolithState = Objects.requireNonNullElse(superMonolithState, this.superMonolithState);
        String resolvedSuperIdolState = Objects.requireNonNullElse(superIdolState, this.superIdolState);
        String resolvedEventToastText = Objects.requireNonNullElse(eventToastText, "");
        String resolvedDuoTeamLabel = Objects.requireNonNullElse(duoTeamLabel, "");
        String resolvedDuoPartnerMoneyValue = Objects.requireNonNullElse(duoPartnerMoneyValue, "-");
        String resolvedDefeatBannerText = Objects.requireNonNullElse(defeatBannerText, "");
        String resolvedDefeatBannerHint = Objects.requireNonNullElse(defeatBannerHint, "");
        String resolvedDefeatBannerShardsValue = Objects.requireNonNullElse(defeatBannerShardsValue, "0");
        int resolvedBankDangerStage = Math.max(0, Math.min(2, bankDangerStage));
        int resolvedEnemyPressureStage = Math.max(0, Math.min(3, enemyPressureStage));
        int resolvedDifficultyLevel = Math.max(1, Math.min(3, nextWaveDifficultyLevel));
        int resolvedToastStage = Math.max(0, Math.min(3, eventToastStage));
        int resolvedToastIconType = Math.max(1, Math.min(3, eventToastIconType));
        int resolvedDuoTeamIconKind = Math.max(0, Math.min(2, duoTeamIconKind));

        if (this.initialized) {
            if (!Objects.equals(this.moneyValue, resolvedMoneyValue)) {
                this.moneyFlashUntilMs = now + FLASH_DURATION_MS;
            }
            if (!Objects.equals(this.coresValue, resolvedCoresValue)) {
                this.coresFlashUntilMs = now + FLASH_DURATION_MS;
            }
            if (!Objects.equals(this.bankValue, resolvedBankValue)) {
                this.bankFlashUntilMs = now + FLASH_DURATION_MS;
            }
        }

        int resolvedMoneyFlashStage = this.flashStage(now, this.moneyFlashUntilMs);
        int resolvedCoresFlashStage = this.flashStage(now, this.coresFlashUntilMs);
        int resolvedBankFlashStage = this.flashStage(now, this.bankFlashUntilMs);

        boolean changed = false;
        changed |= !Objects.equals(this.title, resolvedTitle);
        changed |= !Objects.equals(this.creator, resolvedCreator);
        changed |= !Objects.equals(this.wave, resolvedWave);
        changed |= !Objects.equals(this.status, resolvedStatus);
        changed |= !Objects.equals(this.prompt, resolvedPrompt);
        changed |= this.waveVisible != waveVisible;
        changed |= this.statusVisible != statusVisible;
        changed |= this.promptVisible != promptVisible;
        changed |= this.cursePanelVisible != cursePanelVisible;
        changed |= !Objects.equals(this.cursePanelTitle, resolvedCursePanelTitle);
        changed |= !Objects.equals(this.curseLine1, resolvedCurseLine1);
        changed |= !Objects.equals(this.curseLine2, resolvedCurseLine2);
        changed |= !Objects.equals(this.curseLine3, resolvedCurseLine3);
        changed |= this.rightStatsVisible != rightStatsVisible;
        changed |= this.superMiniVisible != superMiniVisible;
        changed |= !Objects.equals(this.moneyValue, resolvedMoneyValue);
        changed |= this.moneyPaused != moneyPaused;
        changed |= !Objects.equals(this.coresValue, resolvedCoresValue);
        changed |= !Objects.equals(this.bankValue, resolvedBankValue);
        changed |= !Objects.equals(this.bankState, resolvedBankState);
        changed |= this.bankDangerStage != resolvedBankDangerStage;
        changed |= !Objects.equals(this.enemiesValue, resolvedEnemiesValue);
        changed |= !Objects.equals(this.enemiesState, resolvedEnemiesState);
        changed |= this.enemyPressureStage != resolvedEnemyPressureStage;
        changed |= this.tutorialQuestVisible != tutorialQuestVisible;
        changed |= !Objects.equals(this.tutorialQuestText, resolvedTutorialQuestText);
        changed |= this.nextWavePanelVisible != nextWavePanelVisible;
        changed |= !Objects.equals(this.nextWaveText, resolvedNextWaveText);
        changed |= !Objects.equals(this.countdownText, resolvedCountdownText);
        changed |= this.nextWaveDifficultyLevel != resolvedDifficultyLevel;
        changed |= this.interactionPromptVisible != interactionPromptVisible;
        changed |= !Objects.equals(this.interactionPromptText, resolvedInteractionPromptText);
        changed |= !Objects.equals(this.superHeartState, resolvedSuperHeartState);
        changed |= !Objects.equals(this.superMonolithState, resolvedSuperMonolithState);
        changed |= !Objects.equals(this.superIdolState, resolvedSuperIdolState);
        changed |= this.superHeartStage != superHeartStage;
        changed |= !Objects.equals(this.superHeartMiniLabel, resolvedSuperHeartMiniLabel);
        changed |= this.superMonolithStage != superMonolithStage;
        changed |= !Objects.equals(this.superMonolithMiniLabel, resolvedSuperMonolithMiniLabel);
        changed |= this.superIdolStage != superIdolStage;
        changed |= !Objects.equals(this.superIdolMiniLabel, resolvedSuperIdolMiniLabel);
        changed |= this.eventToastVisible != eventToastVisible;
        changed |= !Objects.equals(this.eventToastText, resolvedEventToastText);
        changed |= this.eventToastStage != resolvedToastStage;
        changed |= this.eventToastIconType != resolvedToastIconType;
        changed |= this.duoInfoVisible != duoInfoVisible;
        changed |= this.duoTeamIconKind != resolvedDuoTeamIconKind;
        changed |= !Objects.equals(this.duoTeamLabel, resolvedDuoTeamLabel);
        changed |= !Objects.equals(this.duoPartnerMoneyValue, resolvedDuoPartnerMoneyValue);
        changed |= this.defeatBannerVisible != defeatBannerVisible;
        changed |= !Objects.equals(this.defeatBannerText, resolvedDefeatBannerText);
        changed |= !Objects.equals(this.defeatBannerHint, resolvedDefeatBannerHint);
        changed |= !Objects.equals(this.defeatBannerShardsValue, resolvedDefeatBannerShardsValue);
        changed |= this.moneyFlashStage != resolvedMoneyFlashStage;
        changed |= this.coresFlashStage != resolvedCoresFlashStage;
        changed |= this.bankFlashStage != resolvedBankFlashStage;

        this.title = resolvedTitle;
        this.creator = resolvedCreator;
        this.wave = resolvedWave;
        this.status = resolvedStatus;
        this.prompt = resolvedPrompt;
        this.waveVisible = waveVisible;
        this.statusVisible = statusVisible;
        this.promptVisible = promptVisible;
        this.cursePanelVisible = cursePanelVisible;
        this.cursePanelTitle = resolvedCursePanelTitle;
        this.curseLine1 = resolvedCurseLine1;
        this.curseLine2 = resolvedCurseLine2;
        this.curseLine3 = resolvedCurseLine3;
        this.rightStatsVisible = rightStatsVisible;
        this.superMiniVisible = superMiniVisible;
        this.moneyValue = resolvedMoneyValue;
        this.moneyPaused = moneyPaused;
        this.coresValue = resolvedCoresValue;
        this.bankValue = resolvedBankValue;
        this.bankState = resolvedBankState;
        this.bankDangerStage = resolvedBankDangerStage;
        this.enemiesValue = resolvedEnemiesValue;
        this.enemiesState = resolvedEnemiesState;
        this.enemyPressureStage = resolvedEnemyPressureStage;
        this.tutorialQuestVisible = tutorialQuestVisible;
        this.tutorialQuestText = resolvedTutorialQuestText;
        this.nextWavePanelVisible = nextWavePanelVisible;
        this.nextWaveText = resolvedNextWaveText;
        this.countdownText = resolvedCountdownText;
        this.nextWaveDifficultyLevel = resolvedDifficultyLevel;
        this.interactionPromptVisible = interactionPromptVisible;
        this.interactionPromptText = resolvedInteractionPromptText;
        this.superHeartState = resolvedSuperHeartState;
        this.superMonolithState = resolvedSuperMonolithState;
        this.superIdolState = resolvedSuperIdolState;
        this.superHeartStage = superHeartStage;
        this.superHeartMiniLabel = resolvedSuperHeartMiniLabel;
        this.superMonolithStage = superMonolithStage;
        this.superMonolithMiniLabel = resolvedSuperMonolithMiniLabel;
        this.superIdolStage = superIdolStage;
        this.superIdolMiniLabel = resolvedSuperIdolMiniLabel;
        this.eventToastVisible = eventToastVisible;
        this.eventToastText = resolvedEventToastText;
        this.eventToastStage = resolvedToastStage;
        this.eventToastIconType = resolvedToastIconType;
        this.duoInfoVisible = duoInfoVisible;
        this.duoTeamIconKind = resolvedDuoTeamIconKind;
        this.duoTeamLabel = resolvedDuoTeamLabel;
        this.duoPartnerMoneyValue = resolvedDuoPartnerMoneyValue;
        this.defeatBannerVisible = defeatBannerVisible;
        this.defeatBannerText = resolvedDefeatBannerText;
        this.defeatBannerHint = resolvedDefeatBannerHint;
        this.defeatBannerShardsValue = resolvedDefeatBannerShardsValue;
        this.moneyFlashStage = resolvedMoneyFlashStage;
        this.coresFlashStage = resolvedCoresFlashStage;
        this.bankFlashStage = resolvedBankFlashStage;
        this.initialized = true;

        if (changed) {
            this.dirty = true;
        }
    }

    public void pushIfDirty() {
        if (!this.dirty) {
            return;
        }
        UICommandBuilder ui = new UICommandBuilder();
        this.apply(ui);
        this.update(false, ui);
        this.dirty = false;
    }

    @Override
    protected void build(UICommandBuilder ui) {
        ui.append(UI_HUD);
        this.apply(ui);
        this.dirty = false;
    }

    private int flashStage(long now, long untilMs) {
        long remaining = untilMs - now;
        if (remaining <= 0L) {
            return 0;
        }
        return remaining > FLASH_DURATION_MS / 2L ? 2 : 1;
    }

    private void apply(UICommandBuilder ui) {
        boolean bottomDimmed = this.nextWavePanelVisible;
        String waveText = BankDefenseLocalization.translateFreeform(this.playerRef, this.wave);
        String statusText = BankDefenseLocalization.translateFreeform(this.playerRef, this.status);
        String promptText = BankDefenseLocalization.translateFreeform(this.playerRef, this.prompt);
        String cursePanelTitleText = BankDefenseLocalization.translateFreeform(this.playerRef, this.cursePanelTitle);
        String curseLine1Text = BankDefenseLocalization.translateFreeform(this.playerRef, this.curseLine1);
        String curseLine2Text = BankDefenseLocalization.translateFreeform(this.playerRef, this.curseLine2);
        String curseLine3Text = BankDefenseLocalization.translateFreeform(this.playerRef, this.curseLine3);
        String bankStateText = BankDefenseLocalization.translateFreeform(this.playerRef, this.bankState);
        String enemiesStateText = BankDefenseLocalization.translateFreeform(this.playerRef, this.enemiesState);
        String tutorialQuestText = BankDefenseLocalization.translateFreeform(this.playerRef, this.tutorialQuestText);
        String nextWaveText = BankDefenseLocalization.translateFreeform(this.playerRef, this.nextWaveText);
        String countdownText = BankDefenseLocalization.translateFreeform(this.playerRef, this.countdownText);
        String interactionPromptText = BankDefenseLocalization.translateFreeform(this.playerRef, this.interactionPromptText);
        String superHeartStateText = BankDefenseLocalization.translateFreeform(this.playerRef, this.superHeartState);
        String superMonolithStateText = BankDefenseLocalization.translateFreeform(this.playerRef, this.superMonolithState);
        String superIdolStateText = BankDefenseLocalization.translateFreeform(this.playerRef, this.superIdolState);
        String superHeartMiniLabelText = BankDefenseLocalization.translateFreeform(this.playerRef, this.superHeartMiniLabel);
        String superMonolithMiniLabelText = BankDefenseLocalization.translateFreeform(this.playerRef, this.superMonolithMiniLabel);
        String superIdolMiniLabelText = BankDefenseLocalization.translateFreeform(this.playerRef, this.superIdolMiniLabel);
        String eventToastText = BankDefenseLocalization.translateFreeform(this.playerRef, this.eventToastText);
        String duoTeamLabelText = BankDefenseLocalization.translateFreeform(this.playerRef, this.duoTeamLabel);
        String defeatBannerText = BankDefenseLocalization.translateFreeform(this.playerRef, this.defeatBannerText);
        String defeatBannerHintText = BankDefenseLocalization.translateFreeform(this.playerRef, this.defeatBannerHint);

        ui.set("#Title.Text", this.title);
        ui.set("#Creator.Text", this.creator);
        ui.set("#Wave.Text", waveText);
        ui.set("#Wave.Visible", this.waveVisible);
        ui.set("#Status.Text", statusText);
        ui.set("#StatusPill.Visible", this.statusVisible);
        ui.set("#Prompt.Text", promptText);
        ui.set("#Prompt.Visible", this.promptVisible);
        ui.set("#CursePanel.Visible", this.cursePanelVisible);
        ui.set("#CursePanelTitle.Text", cursePanelTitleText);
        ui.set("#CurseLine1.Text", curseLine1Text);
        ui.set("#CurseLine2.Text", curseLine2Text);
        ui.set("#CurseLine3.Text", curseLine3Text);
        ui.set("#CurseLine1.Visible", !curseLine1Text.isBlank());
        ui.set("#CurseLine2.Visible", !curseLine2Text.isBlank());
        ui.set("#CurseLine3.Visible", !curseLine3Text.isBlank());
        ui.set("#RightStats.Visible", this.rightStatsVisible && !this.defeatBannerVisible);
        ui.set("#SuperMiniPanel.Visible", false);
        SuperCard[] superCards = this.visibleSuperCards(
            superHeartStateText,
            superHeartMiniLabelText,
            this.superHeartStage,
            superMonolithStateText,
            superMonolithMiniLabelText,
            this.superMonolithStage,
            superIdolStateText,
            superIdolMiniLabelText,
            this.superIdolStage
        );
        int superCardCount = 0;
        for (SuperCard card : superCards) {
            if (card != null) {
                superCardCount++;
            }
        }
        boolean superPanelVisible = this.superMiniVisible && superCardCount > 0;
        ui.set("#SuperStatusPanel.Visible", superPanelVisible);
        ui.set("#SuperStatusBg1.Visible", false);
        ui.set("#SuperStatusBg2.Visible", false);
        ui.set("#SuperStatusBg3.Visible", false);
        ui.set("#SuperStatusCaption.Text", BankDefenseLocalization.tr(this.playerRef, "hud.super.caption"));

        SuperCard row1 = superCards.length > 0 ? superCards[0] : null;
        SuperCard row2 = superCards.length > 1 ? superCards[1] : null;
        SuperCard row3 = superCards.length > 2 ? superCards[2] : null;

        ui.set("#SuperHeartRow.Visible", row1 != null);
        ui.set("#SuperHeartName.Text", row1 == null ? "" : row1.title);
        ui.set("#SuperHeartState.Text", row1 == null ? "" : row1.detail);
        ui.set("#SuperHeartIconHeart.Visible", row1 != null && row1.iconKind == SUPER_ICON_HEART);
        ui.set("#SuperHeartIconMonolith.Visible", row1 != null && row1.iconKind == SUPER_ICON_MONOLITH);
        ui.set("#SuperHeartIconIdol.Visible", row1 != null && row1.iconKind == SUPER_ICON_IDOL);

        ui.set("#SuperMonolithRow.Visible", row2 != null);
        ui.set("#SuperMonolithName.Text", row2 == null ? "" : row2.title);
        ui.set("#SuperMonolithState.Text", row2 == null ? "" : row2.detail);
        ui.set("#SuperMonolithIconHeart.Visible", row2 != null && row2.iconKind == SUPER_ICON_HEART);
        ui.set("#SuperMonolithIconMonolith.Visible", row2 != null && row2.iconKind == SUPER_ICON_MONOLITH);
        ui.set("#SuperMonolithIconIdol.Visible", row2 != null && row2.iconKind == SUPER_ICON_IDOL);

        ui.set("#SuperIdolRow.Visible", row3 != null);
        ui.set("#SuperIdolName.Text", row3 == null ? "" : row3.title);
        ui.set("#SuperIdolState.Text", row3 == null ? "" : row3.detail);
        ui.set("#SuperIdolIconHeart.Visible", row3 != null && row3.iconKind == SUPER_ICON_HEART);
        ui.set("#SuperIdolIconMonolith.Visible", row3 != null && row3.iconKind == SUPER_ICON_MONOLITH);
        ui.set("#SuperIdolIconIdol.Visible", row3 != null && row3.iconKind == SUPER_ICON_IDOL);

        ui.set("#MoneyActive.Text", this.moneyValue);
        ui.set("#MoneyPaused.Text", this.moneyValue);
        ui.set("#MoneyIconGold.Visible", !this.moneyPaused);
        ui.set("#MoneyIconGray.Visible", this.moneyPaused);
        ui.set("#MoneyActive.Visible", !this.moneyPaused);
        ui.set("#MoneyPaused.Visible", this.moneyPaused);
        ui.set("#MoneyPausedNote.Visible", this.moneyPaused);
        ui.set("#MoneyFlashStage1.Visible", this.moneyFlashStage == 1);
        ui.set("#MoneyFlashStage2.Visible", this.moneyFlashStage == 2);
        ui.set("#MoneyInterwaveShade.Visible", bottomDimmed);
        ui.set("#MoneyHeading.Text", BankDefenseLocalization.tr(this.playerRef, "hud.money"));
        ui.set("#MoneyPausedNote.Text", BankDefenseLocalization.tr(this.playerRef, "hud.paused"));

        ui.set("#CoresValue.Text", this.coresValue);
        ui.set("#CoresFlashStage1.Visible", this.coresFlashStage == 1);
        ui.set("#CoresFlashStage2.Visible", this.coresFlashStage == 2);
        ui.set("#CoresInterwaveShade.Visible", bottomDimmed);
        ui.set("#CoresHeading.Text", BankDefenseLocalization.tr(this.playerRef, "hud.cores"));

        ui.set("#BankValue.Text", this.bankValue);
        ui.set("#BankState.Text", bankStateText);
        ui.set("#BankDangerSoft.Visible", this.bankDangerStage == 1);
        ui.set("#BankDangerHard.Visible", this.bankDangerStage == 2);
        ui.set("#BankFlashStage1.Visible", this.bankFlashStage == 1);
        ui.set("#BankFlashStage2.Visible", this.bankFlashStage == 2);
        ui.set("#BankInterwaveShade.Visible", bottomDimmed);
        ui.set("#BankHeading.Text", BankDefenseLocalization.tr(this.playerRef, "hud.bank"));

        ui.set("#EnemiesValue.Text", this.enemiesValue);
        ui.set("#EnemiesState.Text", enemiesStateText);
        ui.set("#EnemiesPressureLow.Visible", this.enemyPressureStage == 1);
        ui.set("#EnemiesPressureMid.Visible", this.enemyPressureStage == 2);
        ui.set("#EnemiesPressureHigh.Visible", this.enemyPressureStage == 3);
        ui.set("#EnemiesInterwaveShade.Visible", bottomDimmed);
        ui.set("#EnemiesHeading.Text", BankDefenseLocalization.tr(this.playerRef, "hud.enemies"));

        ui.set("#TutorialQuestPanel.Visible", this.tutorialQuestVisible);
        ui.set("#TutorialQuestLabel.Text", BankDefenseLocalization.tr(this.playerRef, "hud.tutorial.quest"));
        ui.set("#TutorialQuestText.Text", tutorialQuestText);

        ui.set("#NextWavePanel.Visible", this.nextWavePanelVisible);
        ui.set("#NextWaveCaption.Text", BankDefenseLocalization.tr(this.playerRef, "hud.preparation"));
        ui.set("#NextWaveText.Text", nextWaveText);
        ui.set("#CountdownText.Text", countdownText);
        ui.set("#DifficultyText.Text", BankDefenseLocalization.tr(this.playerRef, "hud.wave.difficulty"));
        ui.set("#WaveDifficultyLevel1.Visible", this.nextWavePanelVisible && this.nextWaveDifficultyLevel == 1);
        ui.set("#WaveDifficultyLevel2.Visible", this.nextWavePanelVisible && this.nextWaveDifficultyLevel == 2);
        ui.set("#WaveDifficultyLevel3.Visible", this.nextWavePanelVisible && this.nextWaveDifficultyLevel == 3);

        ui.set("#InteractionPrompt.Visible", this.interactionPromptVisible);
        ui.set("#InteractionPromptText.Text", interactionPromptText);
        ui.set("#IdolToastText1.Text", eventToastText);
        ui.set("#IdolToastText2.Text", eventToastText);
        ui.set("#IdolToastText3.Text", eventToastText);
        ui.set("#IdolToastStage1.Visible", this.eventToastVisible && this.eventToastStage == 1);
        ui.set("#IdolToastStage2.Visible", this.eventToastVisible && this.eventToastStage == 2);
        ui.set("#IdolToastStage3.Visible", this.eventToastVisible && this.eventToastStage == 3);
        ui.set("#EventToastMoneyIcon1.Visible", this.eventToastIconType == 1);
        ui.set("#EventToastMoneyIcon2.Visible", this.eventToastIconType == 1);
        ui.set("#EventToastMoneyIcon3.Visible", this.eventToastIconType == 1);
        ui.set("#EventToastCoresIcon1.Visible", this.eventToastIconType == 2);
        ui.set("#EventToastCoresIcon2.Visible", this.eventToastIconType == 2);
        ui.set("#EventToastCoresIcon3.Visible", this.eventToastIconType == 2);
        ui.set("#EventToastAlertIcon1.Visible", this.eventToastIconType == 3);
        ui.set("#EventToastAlertIcon2.Visible", this.eventToastIconType == 3);
        ui.set("#EventToastAlertIcon3.Visible", this.eventToastIconType == 3);

        ui.set("#DuoInfoPanel.Visible", this.duoInfoVisible);
        ui.set("#DuoTeamHeading.Text", BankDefenseLocalization.tr(this.playerRef, "hud.duo.team"));
        ui.set("#DuoTeamValue.Text", duoTeamLabelText);
        ui.set("#DuoTeamCoinBlue.Visible", this.duoTeamIconKind == 1);
        ui.set("#DuoTeamCoinGreen.Visible", this.duoTeamIconKind == 2);
        ui.set("#DuoPartnerHeading.Text", BankDefenseLocalization.tr(this.playerRef, "hud.duo.partner_money"));
        ui.set("#DuoPartnerValue.Text", this.duoPartnerMoneyValue);

        ui.set("#DefeatBanner.Visible", this.defeatBannerVisible);
        ui.set("#DefeatShardPanel.Visible", this.defeatBannerVisible);
        ui.set("#DefeatText.Text", defeatBannerText);
        ui.set("#DefeatHint.Text", defeatBannerHintText);
        ui.set("#DefeatShardHeading.Text", BankDefenseLocalization.tr(this.playerRef, "hud.defeat.cores"));
        ui.set("#DefeatShardValue.Text", this.defeatBannerShardsValue);
    }

    private SuperCard[] visibleSuperCards(
        String heartTitle,
        String heartDetail,
        int heartIconKind,
        String monolithTitle,
        String monolithDetail,
        int monolithIconKind,
        String idolTitle,
        String idolDetail,
        int idolIconKind
    ) {
        SuperCard[] ordered = new SuperCard[3];
        int index = 0;
        if (heartTitle != null && !heartTitle.isBlank()) {
            ordered[index++] = new SuperCard(heartTitle, Objects.requireNonNullElse(heartDetail, ""), heartIconKind);
        }
        if (monolithTitle != null && !monolithTitle.isBlank()) {
            ordered[index++] = new SuperCard(monolithTitle, Objects.requireNonNullElse(monolithDetail, ""), monolithIconKind);
        }
        if (idolTitle != null && !idolTitle.isBlank()) {
            ordered[index] = new SuperCard(idolTitle, Objects.requireNonNullElse(idolDetail, ""), idolIconKind);
        }
        return ordered;
    }

    private record SuperCard(String title, String detail, int iconKind) {
    }
}
