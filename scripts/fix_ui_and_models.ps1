$ErrorActionPreference = 'Stop'

$utf8NoBom = New-Object System.Text.UTF8Encoding($false)

function Replace-Section {
    param(
        [string]$Path,
        [string]$Pattern,
        [string]$Replacement
    )

    $text = [System.IO.File]::ReadAllText($Path)
    $newText = [System.Text.RegularExpressions.Regex]::Replace(
        $text,
        $Pattern,
        $Replacement,
        [System.Text.RegularExpressions.RegexOptions]::Singleline -bor [System.Text.RegularExpressions.RegexOptions]::Multiline
    )

    if ($newText -eq $text) {
        throw "Pattern not replaced in $Path"
    }

    [System.IO.File]::WriteAllText($Path, $newText, $utf8NoBom)
}

$runtime = 'e:\Hytale mods\bank_defense_autogen\src\java\com\qubecore\bankdefense\runtime\BankDefenseRuntime.java'
$slotPage = 'e:\Hytale mods\bank_defense_autogen\src\java\com\qubecore\bankdefense\ui\BankDefenseSlotPage.java'
$slotUi = 'e:\Hytale mods\bank_defense_autogen\src\resources\Common\UI\Custom\Pages\BankDefenseSlotPage.ui'

$equipMethod = @'
    public ActionResult equipTowerModule(World world, Vec3i targetPosition, String moduleId, int radius) throws IOException {
        MatchContext context = this.getOrCreateContext(world);
        if (context.state.waveState != WaveState.BuildPhase) {
            return ActionResult.fail("Модули можно менять только во время подготовки.");
        }
        TowerInstance instance = this.findNearestPlacedTower(context, targetPosition, radius);
        if (instance == null) {
            return ActionResult.fail("Сначала наведи слот на построенную башню.");
        }
        ModuleDefinition module = context.moduleById.get(moduleId);
        if (module == null) {
            return ActionResult.fail("Неизвестный модуль '" + moduleId + "'.");
        }
        int available = context.moduleInventory.getOrDefault(moduleId, 0);
        if (available <= 0) {
            return ActionResult.fail("В запасе нет модуля '" + module.displayName + "'.");
        }
        if (moduleId.equals(instance.equippedModuleId)) {
            return ActionResult.fail(module.displayName + " уже установлен в этой башне.");
        }
        if (instance.equippedModuleId != null) {
            return ActionResult.fail("В башне уже есть модуль. Сначала сними текущий.");
        }
        this.addModuleToInventory(context, moduleId, -1);
        instance.equippedModuleId = moduleId;
        this.refreshVisualizationIfEnabled(world);
        return ActionResult.ok(instance.definition.displayName + " получила модуль " + module.displayName + ".");
    }

'@
Replace-Section $runtime '(?ms)^    public ActionResult equipTowerModule\(World world, Vec3i targetPosition, String moduleId, int radius\) throws IOException \{.*?^    \}\r?\n\r?\n(?=^    public ActionResult removeTowerModule)' $equipMethod

$getSlotUiState = @'
    public SlotUiState getSlotUiState(World world, String slotId) throws IOException {
        MatchContext context = this.getOrCreateContext(world);
        BuildSlot slot = this.findSlotById(context.snapshot.buildSlots, slotId);
        if (slot == null) {
            return null;
        }

        SlotUiState state = new SlotUiState();
        state.slotId = slot.id;
        state.slotLabel = slot.label == null || slot.label.isBlank() ? slot.id : slot.label;
        state.position = slot.position;
        state.currency = context.state.currency;
        state.bankHp = context.state.bankHp;
        state.currentWave = context.state.currentWave;
        state.totalWaves = context.snapshot.gameRules.totalWaves;
        state.endlessMode = context.snapshot.gameRules.endlessMode;
        state.waveState = context.state.waveState;
        state.preparationRemainingSeconds = context.state.preparationRemainingSeconds;
        state.pendingRewardSummary = context.pendingRewardChoices.isEmpty()
            ? ""
            : "Ожидает выбор модуля: " + context.pendingRewardChoices.size() + " из 3.";

        for (TowerDefinition tower : context.snapshot.towers.towers) {
            if (slot.allowedTowerIds != null && !slot.allowedTowerIds.isEmpty() && !slot.allowedTowerIds.contains(tower.id)) {
                continue;
            }
            TowerButtonState button = new TowerButtonState();
            button.towerId = tower.id;
            button.displayName = tower.displayName;
            button.cost = tower.levels.isEmpty() ? 0 : tower.levels.get(0).unlockCost;
            state.towerButtons.add(button);
        }

        for (ModuleDefinition module : context.snapshot.modules.modules) {
            ModuleButtonState button = new ModuleButtonState();
            button.moduleId = module.id;
            button.displayName = module.displayName;
            button.note = module.note;
            button.count = context.moduleInventory.getOrDefault(module.id, 0);
            state.moduleButtons.add(button);
        }

        TowerInstance instance = context.placedTowers.get(slot.id);
        if (instance == null) {
            state.towerPresent = false;
            state.slotSummary = "Слот пуст. Выбери башню для постройки.";
            state.upgradeLabel = "Улучшение недоступно";
            state.sellLabel = "Продажа недоступна";
            state.moduleSummary = "Модуль: сначала построй башню.";
            state.removeModuleLabel = "Снять модуль недоступно";
            return state;
        }

        state.towerPresent = true;
        state.towerId = instance.definition.id;
        state.towerName = instance.definition.displayName;
        state.towerLevel = instance.getCurrentLevel().level;
        state.towerMaxLevel = instance.definition.levels.size();
        state.slotSummary = instance.definition.displayName + " ур. " + state.towerLevel + " / " + state.towerMaxLevel;
        if (instance.levelIndex + 1 < instance.definition.levels.size()) {
            state.upgradeAvailable = true;
            state.upgradeCost = instance.definition.levels.get(instance.levelIndex + 1).unlockCost;
            state.upgradeLabel = "Улучшить за " + state.upgradeCost;
        } else {
            state.upgradeLabel = "Макс. ур.";
        }
        ModuleDefinition equippedModule = instance.equippedModuleId == null ? null : context.moduleById.get(instance.equippedModuleId);
        state.moduleSummary = equippedModule == null ? "Модуль: пусто" : "Модуль: " + equippedModule.displayName;
        state.removeModuleLabel = equippedModule == null ? "Снять модуль недоступно" : "Снять модуль";
        state.sellRefund = (int)Math.floor(this.totalInvestedCost(instance) * context.snapshot.gameRules.sellRefundRate);
        state.sellLabel = "Продать за " + state.sellRefund;
        return state;
    }

'@
Replace-Section $runtime '(?ms)^    public SlotUiState getSlotUiState\(World world, String slotId\) throws IOException \{.*?^    \}\r?\n\r?\n(?=^    public InteractionTarget resolveInteractionTarget)' $getSlotUiState

$towerProfile = @'
    private RoleModelProfile towerProfile(TowerInstance tower) {
        int level = Math.max(0, Math.min(tower.levelIndex, 4));
        return switch (tower.definition.id) {
            case "guard_post" -> switch (level) {
                case 0 -> new RoleModelProfile(new String[]{"Temple_Kweebec", "Kweebec_Seedling", "Kweebec_Sproutling"}, MODEL_SPAWN, 0.82f);
                case 1 -> new RoleModelProfile(new String[]{"Kweebec_Rootling", "Temple_Kweebec", "Kweebec_Sproutling"}, MODEL_SPAWN, 0.88f);
                case 2 -> new RoleModelProfile(new String[]{"Kweebec_Merchant", "Temple_Kweebec", "Kweebec_Rootling"}, MODEL_SPAWN, 0.95f);
                case 3 -> new RoleModelProfile(new String[]{"Temple_Kweebec", "Kweebec_Merchant", "Kweebec_Rootling"}, MODEL_SPAWN, 1.03f);
                default -> new RoleModelProfile(new String[]{"Temple_Kweebec_Merchant", "Temple_Kweebec", "Kweebec_Merchant"}, MODEL_SPAWN, 1.12f);
            };
            case "sniper_desk" -> switch (level) {
                case 0 -> new RoleModelProfile(new String[]{"Feran_Civilian"}, MODEL_OBJECTIVE, 0.82f);
                case 1 -> new RoleModelProfile(new String[]{"Feran_Civilian", "Feran_Sharptooth"}, MODEL_OBJECTIVE, 0.87f);
                case 2 -> new RoleModelProfile(new String[]{"Feran_Sharptooth", "Feran_Longtooth"}, MODEL_OBJECTIVE, 0.93f);
                case 3 -> new RoleModelProfile(new String[]{"Feran_Longtooth", "Feran_Windwalker"}, MODEL_OBJECTIVE, 1.0f);
                default -> new RoleModelProfile(new String[]{"Feran_Burrower", "Feran_Windwalker"}, MODEL_OBJECTIVE, 1.08f);
            };
            case "rapid_security" -> switch (level) {
                case 0 -> new RoleModelProfile(new String[]{"Outlander_Hunter"}, MODEL_PATH, 0.88f);
                case 1 -> new RoleModelProfile(new String[]{"Outlander_Stalker", "Outlander_Hunter"}, MODEL_PATH, 0.94f);
                case 2 -> new RoleModelProfile(new String[]{"Outlander_Cultist", "Outlander_Stalker", "Outlander_Hunter"}, MODEL_PATH, 1.0f);
                case 3 -> new RoleModelProfile(new String[]{"Outlander_Priest", "Outlander_Cultist", "Outlander_Stalker"}, MODEL_PATH, 1.06f);
                default -> new RoleModelProfile(new String[]{"Outlander_Marauder", "Outlander_Priest", "Outlander_Cultist"}, MODEL_PATH, 1.12f);
            };
            case "freeze_gate" -> switch (level) {
                case 0 -> new RoleModelProfile(new String[]{"Golem_Crystal_Frost"}, MODEL_WARP, 0.76f);
                case 1 -> new RoleModelProfile(new String[]{"Golem_Crystal_Frost"}, MODEL_WARP, 0.90f);
                case 2 -> new RoleModelProfile(new String[]{"Golem_Crystal_Frost"}, MODEL_WARP, 1.04f);
                case 3 -> new RoleModelProfile(new String[]{"Golem_Crystal_Thunder", "Golem_Crystal_Frost"}, MODEL_WARP, 1.16f);
                default -> new RoleModelProfile(new String[]{"Golem_Crystal_Frost", "Golem_Crystal_Thunder"}, MODEL_WARP, 1.28f);
            };
            case "shock_relay" -> switch (level) {
                case 0 -> new RoleModelProfile(new String[]{"Slothian_Kid", "Slothian"}, "NPC/Intelligent/Slothian_Kid/Models/Model.blockymodel", 0.92f);
                case 1 -> new RoleModelProfile(new String[]{"Slothian", "Slothian_Villager", "Slothian_Kid"}, "NPC/Intelligent/Slothian/Models/Model.blockymodel", 0.99f);
                case 2 -> new RoleModelProfile(new String[]{"Slothian_Scout", "Slothian_Monk", "Slothian"}, "NPC/Intelligent/Slothian/Models/Model.blockymodel", 1.05f);
                case 3 -> new RoleModelProfile(new String[]{"Slothian_Monk", "Slothian_Elder", "Slothian"}, "NPC/Intelligent/Slothian_Elder/Models/Model.blockymodel", 1.12f);
                default -> new RoleModelProfile(new String[]{"Slothian_Warrior", "Slothian_Elder", "Slothian"}, "NPC/Intelligent/Slothian_Elder/Models/Model.blockymodel", 1.20f);
            };
            case "armor_drill" -> switch (level) {
                case 0 -> new RoleModelProfile(new String[]{"Trork_Hunter", "Trork_Guard"}, MODEL_OBJECTIVE, 0.94f);
                case 1 -> new RoleModelProfile(new String[]{"Trork_Sentry", "Trork_Guard"}, MODEL_OBJECTIVE, 1.0f);
                case 2 -> new RoleModelProfile(new String[]{"Trork_Warrior", "Trork_Sentry"}, MODEL_OBJECTIVE, 1.08f);
                case 3 -> new RoleModelProfile(new String[]{"Trork_Mauler", "Trork_Warrior"}, MODEL_OBJECTIVE, 1.16f);
                default -> new RoleModelProfile(new String[]{"Trork_Chieftain", "Trork_Mauler"}, MODEL_OBJECTIVE, 1.24f);
            };
            default -> new RoleModelProfile(new String[]{"Outlander_Hunter"}, MODEL_OBJECTIVE, 0.82f);
        };
    }

'@
Replace-Section $runtime '(?ms)^    private RoleModelProfile towerProfile\(TowerInstance tower\) \{.*?^    \}\r?\n\r?\n(?=^    private RoleModelProfile enemyProfile)' $towerProfile

$slotLabelBlock = @'
        commandBuilder.set("#GuardPost.Text", this.towerText(state, "guard_post", "Пост"));
        commandBuilder.set("#SniperDesk.Text", this.towerText(state, "sniper_desk", "Снайпер"));
        commandBuilder.set("#RapidSecurity.Text", this.towerText(state, "rapid_security", "Рапид"));
        commandBuilder.set("#FreezeGate.Text", this.towerText(state, "freeze_gate", "Мороз"));
        commandBuilder.set("#ShockRelay.Text", this.towerText(state, "shock_relay", "Шок"));
        commandBuilder.set("#ArmorDrill.Text", this.towerText(state, "armor_drill", "Бур"));
        commandBuilder.set("#Module01.Text", this.moduleText(state, "overload", "Перегр."));
        commandBuilder.set("#Module02.Text", this.moduleText(state, "heavy_caliber", "Калибр"));
        commandBuilder.set("#Module03.Text", this.moduleText(state, "long_optics", "Оптика"));
        commandBuilder.set("#Module04.Text", this.moduleText(state, "cryo_capsule", "Крио"));
        commandBuilder.set("#Module05.Text", this.moduleText(state, "fragment_charge", "Осколки"));
        commandBuilder.set("#Module06.Text", this.moduleText(state, "armor_scanner", "Бронескан"));
        commandBuilder.set("#Module07.Text", this.moduleText(state, "arc_splitter", "Дуга"));
        commandBuilder.set("#Module08.Text", this.moduleText(state, "dual_camera", "Двойник"));
        commandBuilder.set("#Module09.Text", this.moduleText(state, "reserve_capacitor", "Конденс."));
'@
Replace-Section $slotPage '(?ms)^        commandBuilder\.set\("#GuardPost\.Text".*?^        commandBuilder\.set\("#Module09\.Text".*?\r?\n' $slotLabelBlock

$helpersBlock = @'
    private String towerText(SlotUiState state, String towerId, String fallback) {
        if (state == null) {
            return fallback;
        }
        for (TowerButtonState button : state.towerButtons) {
            if (towerId.equals(button.towerId)) {
                return this.shortTowerName(towerId, fallback) + " " + button.cost;
            }
        }
        return this.shortTowerName(towerId, fallback) + " -";
    }

    private String moduleText(SlotUiState state, String moduleId, String fallback) {
        if (state == null) {
            return fallback;
        }
        for (ModuleButtonState button : state.moduleButtons) {
            if (moduleId.equals(button.moduleId)) {
                return this.shortModuleName(moduleId, fallback) + " x" + button.count;
            }
        }
        return fallback + " x0";
    }

    private String shortTowerName(String towerId, String fallback) {
        return switch (towerId) {
            case "guard_post" -> "Пост";
            case "sniper_desk" -> "Снайпер";
            case "rapid_security" -> "Рапид";
            case "freeze_gate" -> "Мороз";
            case "shock_relay" -> "Шок";
            case "armor_drill" -> "Бур";
            default -> fallback;
        };
    }

    private String shortModuleName(String moduleId, String fallback) {
        return switch (moduleId) {
            case "overload" -> "Перегр.";
            case "heavy_caliber" -> "Калибр";
            case "long_optics" -> "Оптика";
            case "cryo_capsule" -> "Крио";
            case "fragment_charge" -> "Осколки";
            case "armor_scanner" -> "Бронескан";
            case "arc_splitter" -> "Дуга";
            case "dual_camera" -> "Двойник";
            case "reserve_capacitor" -> "Конденс.";
            default -> fallback;
        };
    }

'@
Replace-Section $slotPage '(?ms)^    private String towerText\(SlotUiState state, String towerId, String fallback\) \{.*?^    \}\r?\n\r?\n^    private String moduleText\(SlotUiState state, String moduleId, String fallback\) \{.*?^    \}\r?\n\r?\n' $helpersBlock

$uiText = [System.IO.File]::ReadAllText($slotUi)
$uiText = $uiText.Replace('Anchor: (Width: 620, Height: 920);', 'Anchor: (Width: 636, Height: 920);')
$uiText = $uiText.Replace('Width: 190', 'Width: 196')
[System.IO.File]::WriteAllText($slotUi, $uiText, $utf8NoBom)
