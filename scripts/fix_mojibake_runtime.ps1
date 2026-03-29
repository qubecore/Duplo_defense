$ErrorActionPreference = 'Stop'

$projectRoot = Split-Path -Parent $PSScriptRoot
$runtimePath = Join-Path $projectRoot 'src\java\com\qubecore\bankdefense\runtime\BankDefenseRuntime.java'
$utf8NoBom = [System.Text.UTF8Encoding]::new($false)

function Replace-Block {
    param(
        [string]$Text,
        [string]$Pattern,
        [string]$Replacement
    )

    $options = [System.Text.RegularExpressions.RegexOptions]::Singleline -bor
        [System.Text.RegularExpressions.RegexOptions]::Multiline

    if (-not [System.Text.RegularExpressions.Regex]::IsMatch($Text, $Pattern, $options)) {
        throw "Pattern not found: $Pattern"
    }

    return [System.Text.RegularExpressions.Regex]::Replace($Text, $Pattern, $Replacement, $options)
}

$text = [System.IO.File]::ReadAllText($runtimePath, $utf8NoBom)

$text = Replace-Block $text '(?ms)^    private ActionResult startGame\(World world, MatchContext context\) throws IOException \{.*?^    \}' @'
    private ActionResult startGame(World world, MatchContext context) throws IOException {
        if (context.state.gameStarted) {
            return ActionResult.fail("Матч уже запущен. Используй 'Начать волну' или дождись автозапуска.");
        }
        return this.beginWave(world, context, "Матч начат.", false);
    }
'@

$text = Replace-Block $text '(?ms)^    private ActionResult startPreparedWave\(World world, MatchContext context, boolean manualStart\) throws IOException \{.*?^    \}' @'
    private ActionResult startPreparedWave(World world, MatchContext context, boolean manualStart) throws IOException {
        if (context.state.gameState == GameState.Victory) {
            return ActionResult.fail("Матч уже завершён победой. Используй /bankdefense match reset.");
        }
        if (context.state.gameState == GameState.Defeat) {
            return ActionResult.fail("Матч уже завершён поражением. Используй /bankdefense match reset.");
        }
        if (!context.state.gameStarted) {
            return ActionResult.fail("Матч ещё не начат. Открой панель управления и нажми 'Начать игру'.");
        }
        if (!context.pendingRewardChoices.isEmpty()) {
            return ActionResult.fail("Сначала выбери модуль награды за волну.");
        }
        if (context.state.waveState != WaveState.BuildPhase || context.state.gameState != GameState.Ready) {
            return ActionResult.fail("Сейчас нельзя запустить волну. Текущее состояние: " + context.state.waveState + ".");
        }
        return this.beginWave(world, context, manualStart ? "Ручной старт." : null, manualStart);
    }
'@

$text = Replace-Block $text '(?ms)^    private ActionResult beginWave\(World world, MatchContext context, String prefaceMessage, boolean manualStart\) throws IOException \{.*?^    \}' @'
    private ActionResult beginWave(World world, MatchContext context, String prefaceMessage, boolean manualStart) throws IOException {
        WaveDefinition wave = this.resolveWaveDefinition(context, context.state.currentWave);
        if (wave == null) {
            return ActionResult.fail("Для волны " + context.state.currentWave + " не найден конфиг.");
        }
        int earlyStartBonus = this.calculateEarlyStartBonus(context, wave, manualStart);

        context.activeWaveNumber = context.state.currentWave;
        context.waveElapsedSeconds = 0.0;
        context.pendingSpawns.clear();
        context.enemies.clear();
        context.state.gameStarted = true;
        context.state.preparationRemainingSeconds = 0.0;
        if (earlyStartBonus > 0) {
            context.state.currency += earlyStartBonus;
        }
        for (WaveSpawn spawn : wave.spawns) {
            EnemyDefinition enemy = context.enemyById.get(spawn.enemyId);
            if (enemy == null) {
                continue;
            }
            ScheduledSpawn scheduledSpawn = this.createScheduledSpawn(context, enemy, spawn, wave.index);
            for (int i = 0; i < spawn.count; i++) {
                context.pendingSpawns.add(scheduledSpawn.withSpawnTime(spawn.startDelaySeconds + spawn.intervalSeconds * i));
            }
        }
        context.pendingSpawns.sort(Comparator.comparingDouble(scheduledSpawn -> scheduledSpawn.spawnAtSeconds));
        context.state.gameState = GameState.InMatch;
        context.state.waveState = WaveState.Spawning;
        context.state.currentWave = context.activeWaveNumber;

        this.refreshVisualizationIfEnabled(world);
        this.playWorldUiSound(world, SOUND_WAVE_START);
        if (earlyStartBonus > 0) {
            world.sendMessage(Message.raw("Волна " + context.activeWaveNumber + " началась раньше срока. Бонус: +" + earlyStartBonus + "."));
        } else {
            world.sendMessage(Message.raw("Волна " + context.activeWaveNumber + " началась."));
        }

        StringBuilder builder = new StringBuilder();
        if (prefaceMessage != null && !prefaceMessage.isBlank()) {
            builder.append(prefaceMessage).append(' ');
        }
        builder.append("Волна ")
            .append(context.activeWaveNumber)
            .append(" запущена. В очереди ")
            .append(context.pendingSpawns.size())
            .append(" врагов.");
        if (earlyStartBonus > 0) {
            builder.append(" Бонус за ранний старт: +").append(earlyStartBonus).append('.');
        }
        return ActionResult.ok(builder.toString());
    }
'@

$text = Replace-Block $text '(?ms)^    public ActionResult buildTower\(World world, Vec3i playerPosition, String towerId, int radius\) throws IOException \{.*?^    \}' @'
    public ActionResult buildTower(World world, Vec3i playerPosition, String towerId, int radius) throws IOException {
        MatchContext context = this.getOrCreateContext(world);
        TowerDefinition tower = context.towerById.get(towerId);
        if (tower == null) {
            return ActionResult.fail("Неизвестная башня: '" + towerId + "'. Используй /bankdefense towers.");
        }
        if (!context.snapshot.gameRules.allowBuildDuringWave && context.state.waveState != WaveState.BuildPhase) {
            return ActionResult.fail("Строить можно только в фазу подготовки.");
        }
        BuildSlot slot = this.findNearestAvailableSlot(context, playerPosition, radius);
        if (slot == null) {
            return ActionResult.fail("Рядом нет свободного слота строительства в радиусе " + radius + ".");
        }
        return this.buildTowerInSlot(world, context, slot, tower);
    }
'@

$text = Replace-Block $text '(?ms)^    public ActionResult upgradeTower\(World world, Vec3i playerPosition, int radius\) throws IOException \{.*?^    \}' @'
    public ActionResult upgradeTower(World world, Vec3i playerPosition, int radius) throws IOException {
        MatchContext context = this.getOrCreateContext(world);
        if (!context.snapshot.gameRules.allowUpgradeDuringWave && context.state.waveState != WaveState.BuildPhase) {
            return ActionResult.fail("Улучшать можно только в фазу подготовки.");
        }
        TowerInstance instance = this.findNearestPlacedTower(context, playerPosition, radius);
        if (instance == null) {
            return ActionResult.fail("Рядом нет башни для улучшения в радиусе " + radius + ".");
        }
        return this.upgradeTowerInstance(world, context, instance);
    }
'@

$text = Replace-Block $text '(?ms)^    public ActionResult buildOrUpgradeTower\(World world, Vec3i targetPosition, String towerId, int radius\) throws IOException \{.*?^    \}' @'
    public ActionResult buildOrUpgradeTower(World world, Vec3i targetPosition, String towerId, int radius) throws IOException {
        MatchContext context = this.getOrCreateContext(world);
        TowerDefinition tower = context.towerById.get(towerId);
        if (tower == null) {
            return ActionResult.fail("Неизвестная башня: '" + towerId + "'. Используй /bankdefense towers.");
        }
        BuildSlot slot = this.findNearestSlot(context, targetPosition, radius);
        if (slot == null) {
            return ActionResult.fail("Рядом нет слота строительства в радиусе " + radius + ".");
        }
        TowerInstance existingTower = context.placedTowers.get(slot.id);
        if (existingTower == null) {
            if (!context.snapshot.gameRules.allowBuildDuringWave && context.state.waveState != WaveState.BuildPhase) {
                return ActionResult.fail("Строить можно только в фазу подготовки.");
            }
            return this.buildTowerInSlot(world, context, slot, tower);
        }
        if (!existingTower.definition.id.equals(tower.id)) {
            return ActionResult.fail(
                "В слоте " + slot.id + " уже стоит " + existingTower.definition.displayName
                    + ". Выбери такую же башню для улучшения или сначала продай текущую."
            );
        }
        if (!context.snapshot.gameRules.allowUpgradeDuringWave && context.state.waveState != WaveState.BuildPhase) {
            return ActionResult.fail("Улучшать можно только в фазу подготовки.");
        }
        return this.upgradeTowerInstance(world, context, existingTower);
    }
'@

$text = Replace-Block $text '(?ms)^    public ActionResult sellTower\(World world, Vec3i playerPosition, int radius\) throws IOException \{.*?^    \}' @'
    public ActionResult sellTower(World world, Vec3i playerPosition, int radius) throws IOException {
        MatchContext context = this.getOrCreateContext(world);
        TowerInstance instance = this.findNearestPlacedTower(context, playerPosition, radius);
        if (instance == null) {
            return ActionResult.fail("Рядом нет башни для продажи в радиусе " + radius + ".");
        }
        return this.sellTowerInstance(world, context, instance);
    }
'@

$text = Replace-Block $text '(?ms)^    public ActionResult sellTowerAt\(World world, Vec3i targetPosition, int radius\) throws IOException \{.*?^    \}' @'
    public ActionResult sellTowerAt(World world, Vec3i targetPosition, int radius) throws IOException {
        MatchContext context = this.getOrCreateContext(world);
        TowerInstance instance = this.findNearestPlacedTower(context, targetPosition, radius);
        if (instance == null) {
            return ActionResult.fail("Рядом нет башни для продажи в радиусе " + radius + ".");
        }
        return this.sellTowerInstance(world, context, instance);
    }
'@

$text = Replace-Block $text '(?ms)^    public ActionResult equipTowerModule\(World world, Vec3i targetPosition, String moduleId, int radius\) throws IOException \{.*?^    \}' @'
    public ActionResult equipTowerModule(World world, Vec3i targetPosition, String moduleId, int radius) throws IOException {
        MatchContext context = this.getOrCreateContext(world);
        if (context.state.waveState != WaveState.BuildPhase) {
            return ActionResult.fail("Менять модули можно только в фазу подготовки.");
        }
        TowerInstance instance = this.findNearestPlacedTower(context, targetPosition, radius);
        if (instance == null) {
            return ActionResult.fail("Рядом нет башни для установки модуля в радиусе " + radius + ".");
        }
        ModuleDefinition module = context.moduleById.get(moduleId);
        if (module == null) {
            return ActionResult.fail("Неизвестный модуль: '" + moduleId + "'.");
        }
        int available = context.moduleInventory.getOrDefault(moduleId, 0);
        if (available <= 0) {
            return ActionResult.fail("Модуля " + module.displayName + " нет в запасе.");
        }
        if (moduleId.equals(instance.equippedModuleId)) {
            return ActionResult.fail(module.displayName + " уже установлен в этой башне.");
        }
        if (instance.equippedModuleId != null) {
            return ActionResult.fail("В этой башне уже установлен модуль. Сначала сними текущий.");
        }
        this.addModuleToInventory(context, moduleId, -1);
        instance.equippedModuleId = moduleId;
        this.refreshVisualizationIfEnabled(world);
        return ActionResult.ok(instance.definition.displayName + " получил модуль " + module.displayName + ".");
    }
'@

$text = Replace-Block $text '(?ms)^    public ActionResult removeTowerModule\(World world, Vec3i targetPosition, int radius\) throws IOException \{.*?^    \}' @'
    public ActionResult removeTowerModule(World world, Vec3i targetPosition, int radius) throws IOException {
        MatchContext context = this.getOrCreateContext(world);
        if (context.state.waveState != WaveState.BuildPhase) {
            return ActionResult.fail("Менять модули можно только в фазу подготовки.");
        }
        TowerInstance instance = this.findNearestPlacedTower(context, targetPosition, radius);
        if (instance == null) {
            return ActionResult.fail("Рядом нет башни для снятия модуля в радиусе " + radius + ".");
        }
        if (instance.equippedModuleId == null) {
            return ActionResult.fail("В этой башне нет установленного модуля.");
        }
        ModuleDefinition module = context.moduleById.get(instance.equippedModuleId);
        String displayName = module != null ? module.displayName : instance.equippedModuleId;
        this.addModuleToInventory(context, instance.equippedModuleId, 1);
        instance.equippedModuleId = null;
        this.refreshVisualizationIfEnabled(world);
        return ActionResult.ok("Модуль " + displayName + " снят и возвращён в запас.");
    }
'@

$text = Replace-Block $text '(?ms)^    public ActionResult claimModuleReward\(World world, String moduleId\) throws IOException \{.*?^    \}' @'
    public ActionResult claimModuleReward(World world, String moduleId) throws IOException {
        MatchContext context = this.getOrCreateContext(world);
        if (context.pendingRewardChoices.isEmpty()) {
            return ActionResult.fail("Сейчас нет награды за волну.");
        }
        if (!context.pendingRewardChoices.contains(moduleId)) {
            return ActionResult.fail("Этого модуля нет в текущем выборе награды.");
        }
        ModuleDefinition module = context.moduleById.get(moduleId);
        String displayName = module != null ? module.displayName : moduleId;
        this.addModuleToInventory(context, moduleId, 1);
        context.pendingRewardChoices.clear();
        this.playWorldUiSound(world, SOUND_REWARD);
        this.refreshVisualizationIfEnabled(world);
        return ActionResult.ok("Получен модуль: " + displayName + ".");
    }
'@

$text = Replace-Block $text '(?ms)^    public ActionResult addCurrency\(World world, int amount\) throws IOException \{.*?^    \}' @'
    public ActionResult addCurrency(World world, int amount) throws IOException {
        if (amount <= 0) {
            return ActionResult.fail("Сумма должна быть больше 0.");
        }
        return this.changeCurrency(world, amount);
    }
'@

$text = Replace-Block $text '(?ms)^    public ActionResult changeCurrency\(World world, int amountDelta\) throws IOException \{.*?^    \}' @'
    public ActionResult changeCurrency(World world, int amountDelta) throws IOException {
        if (amountDelta == 0) {
            return ActionResult.fail("Изменение средств не может быть 0.");
        }
        MatchContext context = this.getOrCreateContext(world);
        int nextBalance = context.state.currency + amountDelta;
        if (nextBalance < 0) {
            return ActionResult.fail("Нельзя уйти в минус. Минимум: 0.");
        }
        context.state.currency = nextBalance;
        this.refreshVisualizationIfEnabled(world);
        if (amountDelta > 0) {
            return ActionResult.ok("Добавлено средств: " + amountDelta + ". Баланс: " + context.state.currency + ".");
        }
        return ActionResult.ok("Списано средств: " + Math.abs(amountDelta) + ". Баланс: " + context.state.currency + ".");
    }
'@

$text = Replace-Block $text '(?ms)^    public ActionResult setCurrency\(World world, int amount\) throws IOException \{.*?^    \}' @'
    public ActionResult setCurrency(World world, int amount) throws IOException {
        if (amount < 0) {
            return ActionResult.fail("Баланс не может быть отрицательным.");
        }
        MatchContext context = this.getOrCreateContext(world);
        context.state.currency = amount;
        this.refreshVisualizationIfEnabled(world);
        return ActionResult.ok("Баланс установлен: " + context.state.currency + ".");
    }
'@

$text = Replace-Block $text '(?ms)^    public ActionResult refreshVisualization\(World world\) throws IOException \{.*?^    \}' @'
    public ActionResult refreshVisualization(World world) throws IOException {
        PresentationState presentation = this.presentationsByWorld.computeIfAbsent(this.worldKey(world), ignored -> new PresentationState());
        presentation.enabled = true;
        presentation.accumulatedSeconds = 0.0;
        presentation.orphanedEnemySweepRemainingSeconds = 0.0;
        int rendered = this.fullRefreshPresentation(world, presentation);
        return ActionResult.ok("Визуализация обновлена. Сущностей: " + rendered + ".");
    }
'@

$text = Replace-Block $text '(?ms)^    public ActionResult clearVisualization\(World world\) \{.*?^    \}' @'
    public ActionResult clearVisualization(World world) {
        PresentationState presentation = this.presentationsByWorld.computeIfAbsent(this.worldKey(world), ignored -> new PresentationState());
        presentation.enabled = false;
        presentation.accumulatedSeconds = 0.0;
        presentation.orphanedEnemySweepRemainingSeconds = 0.0;
        int removed = this.clearVisualEntities(world, presentation);
        removed += this.purgeDetachedEnemyVisuals(world, Collections.emptySet());
        removed += this.purgeDetachedTowerVisuals(world, Collections.emptySet());
        return ActionResult.ok("Визуализация очищена. Удалено сущностей: " + removed + ".");
    }
'@

$text = Replace-Block $text '(?ms)^    public void tickWorld\(World world, double deltaSeconds\) \{.*?^    \}\r?\n\r?\n    public void tickVisualization\(World world, double deltaSeconds\) \{' @'
    public void tickWorld(World world, double deltaSeconds) {
        MatchContext context = this.matchesByWorld.get(this.worldKey(world));
        if (context == null) {
            return;
        }
        if (context.state.gameState == GameState.Victory || context.state.gameState == GameState.Defeat) {
            this.clearExpiredVisualEffects(world, context);
            this.tickProjectiles(world, deltaSeconds);
            return;
        }
        if (context.state.gameState == GameState.Ready && context.state.waveState == WaveState.BuildPhase) {
            if (context.state.gameStarted && context.pendingRewardChoices.isEmpty() && context.state.preparationRemainingSeconds > 0.0) {
                context.state.preparationRemainingSeconds = Math.max(0.0, context.state.preparationRemainingSeconds - deltaSeconds);
                if (context.state.preparationRemainingSeconds <= 0.0) {
                    try {
                        this.startPreparedWave(world, context, false);
                    } catch (IOException ignored) {
                    }
                }
            }
            this.clearExpiredVisualEffects(world, context);
            this.tickProjectiles(world, deltaSeconds);
            return;
        }
        if (context.state.gameState != GameState.InMatch) {
            this.clearExpiredVisualEffects(world, context);
            this.tickProjectiles(world, deltaSeconds);
            return;
        }
        if (context.routeLength <= 0.0) {
            context.state.gameState = GameState.Defeat;
            context.state.waveState = WaveState.Finished;
            world.sendMessage(Message.raw("Маршрут врагов не настроен. Матч остановлен."));
            this.clearExpiredVisualEffects(world, context);
            this.tickProjectiles(world, deltaSeconds);
            return;
        }

        context.waveElapsedSeconds += deltaSeconds;
        this.spawnEnemies(world, context);
        this.tickTowers(world, context, deltaSeconds);
        this.tickEnemies(world, context, deltaSeconds);
        this.applyEnemyEffects(world, context, deltaSeconds);
        this.tickTowerDisableEffects(world, context, deltaSeconds);
        this.clearExpiredVisualEffects(world, context);
        this.tickProjectiles(world, deltaSeconds);

        if (context.state.bankHp <= 0) {
            context.state.bankHp = 0;
            context.state.gameState = GameState.Defeat;
            context.state.waveState = WaveState.Finished;
            context.pendingSpawns.clear();
            context.enemies.clear();
            world.sendMessage(Message.raw("Дупло разрушено. Матч проигран."));
            this.playWorldUiSound(world, SOUND_DEFEAT);
            this.refreshVisualizationIfEnabled(world);
            return;
        }

        if (context.pendingSpawns.isEmpty() && context.enemies.isEmpty()) {
            WaveDefinition completedWave = this.resolveWaveDefinition(context, context.activeWaveNumber);
            int completedWaveNumber = completedWave == null ? context.activeWaveNumber : completedWave.index;
            if (completedWave != null) {
                context.state.currency += completedWave.bonusCurrency;
            }
            if (!context.snapshot.gameRules.endlessMode && context.activeWaveNumber >= context.snapshot.gameRules.totalWaves) {
                context.state.gameState = GameState.Victory;
                context.state.waveState = WaveState.Finished;
                world.sendMessage(Message.raw("Дупло устояло. Победа!"));
                this.playWorldUiSound(world, SOUND_VICTORY);
                this.refreshVisualizationIfEnabled(world);
                return;
            }
            this.playWorldUiSound(world, SOUND_WAVE_END);
            context.state.currentWave = context.activeWaveNumber + 1;
            context.state.gameState = GameState.Ready;
            context.state.waveState = WaveState.BuildPhase;
            context.state.gameStarted = true;
            context.state.preparationRemainingSeconds = Math.max(0.0, context.snapshot.gameRules.interWavePrepSeconds);
            this.queueRewardChoicesIfNeeded(world, context, completedWaveNumber);
            context.activeWaveNumber = 0;
            String prefix = "Волна " + completedWaveNumber + " завершена. ";
            if (!context.pendingRewardChoices.isEmpty()) {
                world.sendMessage(Message.raw(prefix + "Выбери 1 модуль награды."));
            } else if (context.state.preparationRemainingSeconds > 0.0) {
                world.sendMessage(Message.raw(
                    prefix + "Следующая волна: " + context.state.currentWave
                        + ". Подготовка " + (int)Math.ceil(context.state.preparationRemainingSeconds)
                        + "с."
                ));
            } else {
                world.sendMessage(Message.raw(prefix + "Следующая волна начинается."));
                try {
                    this.startPreparedWave(world, context, false);
                } catch (IOException ignored) {
                }
            }
            this.refreshVisualizationIfEnabled(world);
        }
    }

    public void tickVisualization(World world, double deltaSeconds) {
'@

$text = Replace-Block $text '(?ms)^    private ActionResult buildTowerInSlot\(World world, MatchContext context, BuildSlot slot, TowerDefinition tower\) \{.*?^    \}' @'
    private ActionResult buildTowerInSlot(World world, MatchContext context, BuildSlot slot, TowerDefinition tower) {
        if (slot.allowedTowerIds != null && !slot.allowedTowerIds.isEmpty() && !slot.allowedTowerIds.contains(tower.id)) {
            return ActionResult.fail("Башня " + tower.displayName + " не подходит для слота " + slot.id + ".");
        }
        TowerLevel level = tower.levels.get(0);
        if (context.state.currency < level.unlockCost) {
            return ActionResult.fail("Недостаточно средств. Нужно: " + level.unlockCost + ", сейчас: " + context.state.currency + ".");
        }
        context.state.currency -= level.unlockCost;
        TowerInstance instance = new TowerInstance(slot, tower);
        if (context.snapshot.map.vaultPoint != null) {
            instance.visualRotation = this.rotationTowards(
                this.towerWorldPosition(slot),
                this.objectiveWorldPosition(context.snapshot.map.vaultPoint, 0.05)
            );
        }
        context.placedTowers.put(slot.id, instance);
        this.playSound3d(world, SOUND_BUILD, this.towerWorldPosition(slot));
        this.refreshVisualizationIfEnabled(world);
        return ActionResult.ok(
            "Построен " + tower.displayName + " в слоте " + slot.id
                + " за " + level.unlockCost
                + ". Баланс: " + context.state.currency + "."
        );
    }
'@

$text = Replace-Block $text '(?ms)^    private ActionResult upgradeTowerInstance\(World world, MatchContext context, TowerInstance instance\) \{.*?^    \}' @'
    private ActionResult upgradeTowerInstance(World world, MatchContext context, TowerInstance instance) {
        if (instance.levelIndex >= instance.definition.levels.size() - 1) {
            return ActionResult.fail(instance.definition.displayName + " уже максимального уровня.");
        }
        TowerLevel nextLevel = instance.definition.levels.get(instance.levelIndex + 1);
        if (context.state.currency < nextLevel.unlockCost) {
            return ActionResult.fail("Недостаточно средств. Нужно: " + nextLevel.unlockCost + ", сейчас: " + context.state.currency + ".");
        }
        context.state.currency -= nextLevel.unlockCost;
        instance.levelIndex++;
        this.playSound3d(world, SOUND_BUILD, this.towerWorldPosition(instance.slot));
        this.refreshVisualizationIfEnabled(world);
        return ActionResult.ok(
            "Улучшен " + instance.definition.displayName + " в слоте " + instance.slot.id
                + " до ур. " + instance.getCurrentLevel().level
                + ". Баланс: " + context.state.currency + "."
        );
    }
'@

$text = Replace-Block $text '(?ms)^    private ActionResult sellTowerInstance\(World world, MatchContext context, TowerInstance instance\) \{.*?^    \}' @'
    private ActionResult sellTowerInstance(World world, MatchContext context, TowerInstance instance) {
        if (context.state.waveState != WaveState.BuildPhase) {
            return ActionResult.fail("Продавать башни можно только в фазу подготовки.");
        }
        int refund = (int)Math.floor(this.totalInvestedCost(instance) * context.snapshot.gameRules.sellRefundRate);
        String returnedModule = instance.equippedModuleId;
        if (returnedModule != null) {
            this.addModuleToInventory(context, returnedModule, 1);
        }
        context.placedTowers.remove(instance.slot.id);
        context.state.currency += refund;
        this.playSound3d(world, SOUND_BUILD, this.towerWorldPosition(instance.slot));
        this.refreshVisualizationIfEnabled(world);
        String moduleSuffix = returnedModule != null ? " Модуль возвращён в запас." : "";
        return ActionResult.ok(
            "Продан " + instance.definition.displayName + " из слота " + instance.slot.id
                + ". Возврат: " + refund
                + ". Баланс: " + context.state.currency + "." + moduleSuffix
        );
    }
'@

$text = Replace-Block $text '(?ms)^    private void queueRewardChoicesIfNeeded\(World world, MatchContext context, int completedWaveNumber\) \{.*?^    \}' @'
    private void queueRewardChoicesIfNeeded(World world, MatchContext context, int completedWaveNumber) {
        if (completedWaveNumber <= 0 || context.snapshot.gameRules.moduleRewardIntervalWaves <= 0) {
            return;
        }
        if (completedWaveNumber % context.snapshot.gameRules.moduleRewardIntervalWaves != 0) {
            return;
        }
        List<String> pool = new ArrayList<>();
        for (ModuleDefinition module : context.snapshot.modules.modules) {
            if (module.id != null && !module.id.isBlank()) {
                pool.add(module.id);
            }
        }
        if (pool.isEmpty()) {
            return;
        }
        Collections.shuffle(pool, ThreadLocalRandom.current());
        context.pendingRewardChoices.clear();
        int choiceCount = Math.min(Math.max(1, context.snapshot.gameRules.moduleRewardChoiceCount), pool.size());
        for (int i = 0; i < choiceCount; i++) {
            context.pendingRewardChoices.add(pool.get(i));
        }
        this.playWorldUiSound(world, SOUND_REWARD);
        world.sendMessage(Message.raw("Волна " + completedWaveNumber + " завершена. Выбери 1 модуль награды."));
        this.openRewardPages(world);
    }
'@

$text = Replace-Block $text '(?ms)^    public List<String> validateWorld\(World world\) throws IOException \{.*?^    \}\r?\n\r?\n    private MatchContext getOrCreateContext\(World world\) throws IOException \{' @'
    public List<String> validateWorld(World world) throws IOException {
        BankDefenseRepository.Snapshot snapshot = this.repository.loadSnapshot(world);
        List<String> issues = new ArrayList<>();

        if (snapshot.towers.towers.size() != 6) {
            issues.add("Ожидалось 6 башен, найдено: " + snapshot.towers.towers.size() + ".");
        }
        for (TowerDefinition tower : snapshot.towers.towers) {
            if (tower.id == null || tower.id.isBlank()) {
                issues.add("У башни без имени отсутствует id.");
                continue;
            }
            if (tower.levels.size() != snapshot.gameRules.maxTowerLevel) {
                issues.add(
                    "У башни '" + tower.id + "' уровней " + tower.levels.size()
                        + ", ожидалось: " + snapshot.gameRules.maxTowerLevel + "."
                );
            }
        }

        if (snapshot.modules.modules.size() != 9) {
            issues.add("Ожидалось 9 модулей, найдено: " + snapshot.modules.modules.size() + ".");
        }

        if (snapshot.enemies.enemies.size() < 9) {
            issues.add("Ожидалось минимум 9 врагов, найдено: " + snapshot.enemies.enemies.size() + ".");
        }

        Set<String> enemyIds = new HashSet<>();
        for (EnemyDefinition enemy : snapshot.enemies.enemies) {
            if (enemy.id == null || enemy.id.isBlank()) {
                issues.add("У врага без имени отсутствует id.");
                continue;
            }
            if (!enemyIds.add(enemy.id)) {
                issues.add("Повторяется id врага: '" + enemy.id + "'.");
            }
        }

        if (snapshot.waves.waves.size() != snapshot.gameRules.totalWaves) {
            issues.add(
                "Ожидалось волн: " + snapshot.gameRules.totalWaves
                    + ", найдено: " + snapshot.waves.waves.size() + "."
            );
        }

        int bossWaveCount = 0;
        for (WaveDefinition wave : snapshot.waves.waves) {
            if (wave.bossWave) {
                bossWaveCount++;
            }
            if (wave.spawns.isEmpty()) {
                issues.add("Волна " + wave.index + " не содержит спавнов.");
            }
            for (WaveSpawn spawn : wave.spawns) {
                if (!enemyIds.contains(spawn.enemyId)) {
                    issues.add("Волна " + wave.index + " ссылается на неизвестного врага '" + spawn.enemyId + "'.");
                }
            }
        }
        if (bossWaveCount != 1) {
            issues.add("Должна быть ровно 1 босс-волна, найдено: " + bossWaveCount + ".");
        }

        if (snapshot.map.spawnPoint == null) {
            issues.add("Не задан marker spawn.");
        }
        if (snapshot.map.bankCenter == null) {
            issues.add("Не задан marker bank.");
        }
        if (snapshot.map.vaultPoint == null) {
            issues.add("Не задан marker vault.");
        }
        if (snapshot.map.playerStart == null) {
            issues.add("Не задан marker playerstart.");
        }
        if (snapshot.map.routePoints.isEmpty()) {
            issues.add("Маршрут врагов пуст. Добавь точки через /bankdefense route add.");
        }

        Set<String> slotIds = new HashSet<>();
        Set<String> slotPositions = new HashSet<>();
        for (BuildSlot slot : snapshot.buildSlots.slots) {
            if (slot.id == null || slot.id.isBlank()) {
                issues.add("Найден слот без id.");
            } else if (!slotIds.add(slot.id)) {
                issues.add("Повторяется id слота: '" + slot.id + "'.");
            }
            if (slot.position == null) {
                issues.add("У слота '" + (slot.id == null ? "<без id>" : slot.id) + "' не задана позиция.");
            } else {
                String positionKey = this.key(slot.position);
                if (!slotPositions.add(positionKey)) {
                    issues.add("Повторяется позиция слота: " + positionKey + ".");
                }
            }
        }

        return issues;
    }

    private MatchContext getOrCreateContext(World world) throws IOException {
'@

$text = [System.Text.RegularExpressions.Regex]::Replace(
    $text,
    'this\.spawnObjectiveVisual\(world, presentation, KEY_CONSOLE_START, this\.startConsolePoint\(snapshot\.map\), MODEL_SPAWN, 0\.78f, 0\.05, ".*?"\);',
    'this.spawnObjectiveVisual(world, presentation, KEY_CONSOLE_START, this.startConsolePoint(snapshot.map), MODEL_SPAWN, 0.78f, 0.05, "Начать волну");'
)
$text = [System.Text.RegularExpressions.Regex]::Replace(
    $text,
    'this\.spawnObjectiveVisual\(world, presentation, KEY_CONSOLE_MENU, this\.menuConsolePoint\(snapshot\.map\), MODEL_WARP, 0\.82f, 0\.05, ".*?"\);',
    'this.spawnObjectiveVisual(world, presentation, KEY_CONSOLE_MENU, this.menuConsolePoint(snapshot.map), MODEL_WARP, 0.82f, 0.05, "Чит-меню");'
)
$text = [System.Text.RegularExpressions.Regex]::Replace(
    $text,
    'this\.ensureObjectiveVisual\(world, presentation, KEY_CONSOLE_START, this\.startConsolePoint\(snapshot\.map\), MODEL_SPAWN, 0\.78f, 0\.05, ".*?"\);',
    'this.ensureObjectiveVisual(world, presentation, KEY_CONSOLE_START, this.startConsolePoint(snapshot.map), MODEL_SPAWN, 0.78f, 0.05, "Начать волну");'
)
$text = [System.Text.RegularExpressions.Regex]::Replace(
    $text,
    'this\.ensureObjectiveVisual\(world, presentation, KEY_CONSOLE_MENU, this\.menuConsolePoint\(snapshot\.map\), MODEL_WARP, 0\.82f, 0\.05, ".*?"\);',
    'this.ensureObjectiveVisual(world, presentation, KEY_CONSOLE_MENU, this.menuConsolePoint(snapshot.map), MODEL_WARP, 0.82f, 0.05, "Чит-меню");'
)
$text = [System.Text.RegularExpressions.Regex]::Replace(
    $text,
    'String nameplate = tower\.definition\.displayName \+ ".*?" \+ tower\.getCurrentLevel\(\)\.level;',
    'String nameplate = tower.definition.displayName + " ур. " + tower.getCurrentLevel().level;'
)

[System.IO.File]::WriteAllText($runtimePath, $text, $utf8NoBom)
Write-Output "Patched runtime text: $runtimePath"
