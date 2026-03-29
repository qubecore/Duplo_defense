package com.qubecore.bankdefense.runtime.support;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class BankDefenseLocalization {
    public enum Lang {
        RU,
        EN
    }

    private static final Map<String, String> TOWER_RU = Map.ofEntries(
        Map.entry("guard_post", "Лучник дупла"),
        Map.entry("sniper_desk", "Арбалетчик"),
        Map.entry("rapid_security", "Дротикомёт"),
        Map.entry("freeze_gate", "Ледяной тотем"),
        Map.entry("shock_relay", "Электрический Ленивец"),
        Map.entry("armor_drill", "Корнеплюй"),
        Map.entry("heart_of_roots", "Сердце корней"),
        Map.entry("storm_monolith", "Штормовой монолит"),
        Map.entry("seed_idol", "Идол урожая"),
        Map.entry("root_snare", "Корневая хватка"),
        Map.entry("spore_mine", "Споровая мина"),
        Map.entry("frost_seal", "Ледяная печать")
    );

    private static final Map<String, String> TOWER_EN = Map.ofEntries(
        Map.entry("guard_post", "Hollow Archer"),
        Map.entry("sniper_desk", "Crossbowman"),
        Map.entry("rapid_security", "Dart Thrower"),
        Map.entry("freeze_gate", "Frost Totem"),
        Map.entry("shock_relay", "Electric Sloth"),
        Map.entry("armor_drill", "Rootspitter"),
        Map.entry("heart_of_roots", "Heart of Roots"),
        Map.entry("storm_monolith", "Storm Monolith"),
        Map.entry("seed_idol", "Harvest Idol"),
        Map.entry("root_snare", "Root Snare"),
        Map.entry("spore_mine", "Spore Mine"),
        Map.entry("frost_seal", "Frost Sigil")
    );

    private static final Map<String, String> TOWER_SHORT_RU = Map.ofEntries(
        Map.entry("guard_post", "Лучник"),
        Map.entry("sniper_desk", "Арбалетчик"),
        Map.entry("rapid_security", "Дротикомёт"),
        Map.entry("freeze_gate", "Ледяной тотем"),
        Map.entry("shock_relay", "Электрический Ленивец"),
        Map.entry("armor_drill", "Корнеплюй"),
        Map.entry("heart_of_roots", "Сердце корней"),
        Map.entry("storm_monolith", "Штормовой монолит"),
        Map.entry("seed_idol", "Идол урожая"),
        Map.entry("root_snare", "Корневая хватка"),
        Map.entry("spore_mine", "Споровая мина"),
        Map.entry("frost_seal", "Ледяная печать")
    );

    private static final Map<String, String> TOWER_SHORT_EN = Map.ofEntries(
        Map.entry("guard_post", "Archer"),
        Map.entry("sniper_desk", "Crossbowman"),
        Map.entry("rapid_security", "Dart Thrower"),
        Map.entry("freeze_gate", "Frost Totem"),
        Map.entry("shock_relay", "Electric Sloth"),
        Map.entry("armor_drill", "Rootspitter"),
        Map.entry("heart_of_roots", "Heart of Roots"),
        Map.entry("storm_monolith", "Storm Monolith"),
        Map.entry("seed_idol", "Harvest Idol"),
        Map.entry("root_snare", "Root Snare"),
        Map.entry("spore_mine", "Spore Mine"),
        Map.entry("frost_seal", "Frost Sigil")
    );

    private static final Map<String, String> MODULE_RU = Map.ofEntries(
        Map.entry("overload", "Перегрузка"),
        Map.entry("heavy_caliber", "Тяжёлый калибр"),
        Map.entry("long_optics", "Дальняя оптика"),
        Map.entry("cryo_capsule", "Крио-капсула"),
        Map.entry("fragment_charge", "Осколочный заряд"),
        Map.entry("armor_scanner", "Сканер брони"),
        Map.entry("arc_splitter", "Дуговой разветвитель"),
        Map.entry("dual_camera", "Двойная камера"),
        Map.entry("reserve_capacitor", "Резервный конденсатор")
    );

    private static final Map<String, String> MODULE_EN = Map.ofEntries(
        Map.entry("overload", "Overload"),
        Map.entry("heavy_caliber", "Heavy Caliber"),
        Map.entry("long_optics", "Long Optics"),
        Map.entry("cryo_capsule", "Cryo Capsule"),
        Map.entry("fragment_charge", "Fragment Charge"),
        Map.entry("armor_scanner", "Armor Scanner"),
        Map.entry("arc_splitter", "Arc Splitter"),
        Map.entry("dual_camera", "Dual Chamber"),
        Map.entry("reserve_capacitor", "Reserve Capacitor")
    );

    private static final Map<String, String> MODULE_SHORT_RU = Map.ofEntries(
        Map.entry("overload", "Перегрузка"),
        Map.entry("heavy_caliber", "Калибр"),
        Map.entry("long_optics", "Оптика"),
        Map.entry("cryo_capsule", "Крио"),
        Map.entry("fragment_charge", "Осколки"),
        Map.entry("armor_scanner", "Бронескан"),
        Map.entry("arc_splitter", "Дуга"),
        Map.entry("dual_camera", "Двойник"),
        Map.entry("reserve_capacitor", "Конденсатор")
    );

    private static final Map<String, String> MODULE_SHORT_EN = Map.ofEntries(
        Map.entry("overload", "Overload"),
        Map.entry("heavy_caliber", "Caliber"),
        Map.entry("long_optics", "Optics"),
        Map.entry("cryo_capsule", "Cryo"),
        Map.entry("fragment_charge", "Fragments"),
        Map.entry("armor_scanner", "Armor Scan"),
        Map.entry("arc_splitter", "Arc"),
        Map.entry("dual_camera", "Dual"),
        Map.entry("reserve_capacitor", "Capacitor")
    );

    private static final Map<String, String> MODULE_NOTE_RU = Map.ofEntries(
        Map.entry("overload", "+20% к скорострельности."),
        Map.entry("heavy_caliber", "+30% к урону, но -12% к скорострельности."),
        Map.entry("long_optics", "+дальность для прицельных башен."),
        Map.entry("cryo_capsule", "Атаки дополнительно замедляют цель."),
        Map.entry("fragment_charge", "Попадания наносят 35% урона по площади в широком радиусе вокруг цели."),
        Map.entry("armor_scanner", "+40% урона по тяжёлым целям и боссам."),
        Map.entry("arc_splitter", "Выстрел перескакивает ещё на одну цель."),
        Map.entry("dual_camera", "Каждый 5-й выстрел даёт дополнительный выстрел на 120% урона."),
        Map.entry("reserve_capacitor", "После 2.2 секунд простоя следующий выстрел получает +90% урона.")
    );

    private static final Map<String, String> MODULE_NOTE_EN = Map.ofEntries(
        Map.entry("overload", "+20% attack speed."),
        Map.entry("heavy_caliber", "+30% damage, but -12% attack speed."),
        Map.entry("long_optics", "+range for precise towers."),
        Map.entry("cryo_capsule", "Hits apply an extra slow."),
        Map.entry("fragment_charge", "Hits deal 35% splash damage in a wide radius around the target."),
        Map.entry("armor_scanner", "+40% damage against heavy enemies and bosses."),
        Map.entry("arc_splitter", "Each shot chains to one extra target."),
        Map.entry("dual_camera", "Every 5th shot fires an extra shot for 120% damage."),
        Map.entry("reserve_capacitor", "After 2.2 seconds of idle time, the next shot gains +90% damage.")
    );

    private static final Map<String, String> ENEMY_RU = Map.ofEntries(
        Map.entry("thief", "Шатун"),
        Map.entry("runner", "Бегун"),
        Map.entry("bruiser", "Громила"),
        Map.entry("runner_bomber", "Бегун-подрывник"),
        Map.entry("bone_guard", "Костяной латник"),
        Map.entry("berserker_rotter", "Берсерк-чумник"),
        Map.entry("elite_robber", "Проклятый вожак"),
        Map.entry("goblin_saboteur", "Гоблин-саботажник"),
        Map.entry("jammer", "Глушитель роя"),
        Map.entry("necro_thief", "Некровор"),
        Map.entry("vault_priest", "Жрец порчи"),
        Map.entry("bone_trumpeter", "Костяной трубач"),
        Map.entry("plague_standard", "Чумной знаменосец"),
        Map.entry("curse_weaver", "Плетельщик проклятий"),
        Map.entry("grave_spawn", "Поднятый прислужник"),
        Map.entry("necro_guardian", "Некро-страж"),
        Map.entry("vault_breaker", "Разоритель дупла"),
        Map.entry("necro_king", "Некрокороль"),
        Map.entry("goblin_bomber_boss", "Гоблин-бомбила"),
        Map.entry("rift_twin_alpha", "Близнец Разлома: Альфа"),
        Map.entry("rift_twin_beta", "Близнец Разлома: Бета"),
        Map.entry("node_arbiter", "Арбитр Узлов"),
        Map.entry("seal_master", "Мастер Печати"),
        Map.entry("seal_node", "Узел Печати")
    );

    private static final Map<String, String> ENEMY_EN = Map.ofEntries(
        Map.entry("thief", "Shambler"),
        Map.entry("runner", "Runner"),
        Map.entry("bruiser", "Bruiser"),
        Map.entry("runner_bomber", "Bomber Runner"),
        Map.entry("bone_guard", "Bone Guard"),
        Map.entry("berserker_rotter", "Plague Berserker"),
        Map.entry("elite_robber", "Cursed Packleader"),
        Map.entry("goblin_saboteur", "Goblin Saboteur"),
        Map.entry("jammer", "Swarm Jammer"),
        Map.entry("necro_thief", "Necro Thief"),
        Map.entry("vault_priest", "Priest of Blight"),
        Map.entry("bone_trumpeter", "Bone Trumpeter"),
        Map.entry("plague_standard", "Plague Standard Bearer"),
        Map.entry("curse_weaver", "Curse Weaver"),
        Map.entry("grave_spawn", "Raised Thrall"),
        Map.entry("necro_guardian", "Necro Guardian"),
        Map.entry("vault_breaker", "Hollow Ravager"),
        Map.entry("necro_king", "Necro King"),
        Map.entry("goblin_bomber_boss", "Goblin Bomber"),
        Map.entry("rift_twin_alpha", "Rift Twin: Alpha"),
        Map.entry("rift_twin_beta", "Rift Twin: Beta"),
        Map.entry("node_arbiter", "Node Arbiter"),
        Map.entry("seal_master", "Seal Master"),
        Map.entry("seal_node", "Seal Node")
    );

    private static final Map<String, String> CONTRACT_RU = Map.ofEntries(
        Map.entry("none", "Без контракта"),
        Map.entry("triple_breach", "Тройной прорыв"),
        Map.entry("empty_purse", "Пустой кошель"),
        Map.entry("necrotic_mist", "Некротический туман"),
        Map.entry("storm_front", "Грозовой фронт")
    );

    private static final Map<String, String> CONTRACT_EN = Map.ofEntries(
        Map.entry("none", "No Contract"),
        Map.entry("triple_breach", "Triple Breach"),
        Map.entry("empty_purse", "Empty Purse"),
        Map.entry("necrotic_mist", "Necrotic Mist"),
        Map.entry("storm_front", "Storm Front")
    );

    private static final Map<String, String> CONTRACT_NOTE_EN = Map.ofEntries(
        Map.entry("none", "A clean run with no extra modifiers."),
        Map.entry("triple_breach", "Two-lane and three-lane waves arrive earlier, and bosses are escorted by neighboring lanes more often."),
        Map.entry("empty_purse", "Starting money is cut, gold chests are disabled, and the early-start bonus is almost gone."),
        Map.entry("necrotic_mist", "Necro thieves and priests appear more often, their auras are stronger, and the mist raises fodder along cleared route segments."),
        Map.entry("storm_front", "Preparation between waves is shorter, fast enemies appear more often and move faster, and slows affect them less.")
    );

    private static final Map<String, String> DIFFICULTY_RU = Map.ofEntries(
        Map.entry("easy", "Лёгкий"),
        Map.entry("normal", "Нормальный"),
        Map.entry("hard", "Тяжёлый"),
        Map.entry("nightmare", "Кошмар")
    );

    private static final Map<String, String> DIFFICULTY_EN = Map.ofEntries(
        Map.entry("easy", "Easy"),
        Map.entry("normal", "Normal"),
        Map.entry("hard", "Hard"),
        Map.entry("nightmare", "Nightmare")
    );

    private static final Map<String, String> PROGRESSION_RU = Map.ofEntries(
        Map.entry("economy_1", "Экономика I"),
        Map.entry("economy_2", "Экономика II"),
        Map.entry("economy_3", "Экономика III"),
        Map.entry("economy_4", "Экономика IV"),
        Map.entry("economy_5", "Экономика V"),
        Map.entry("duplo_hp_1", "Крепость дупла I"),
        Map.entry("duplo_hp_2", "Крепость дупла II"),
        Map.entry("duplo_hp_3", "Крепость дупла III"),
        Map.entry("duplo_hp_4", "Крепость дупла IV"),
        Map.entry("duplo_hp_5", "Крепость дупла V"),
        Map.entry("tower_cap_1", "Башни I"),
        Map.entry("tower_cap_2", "Башни II"),
        Map.entry("tower_cap_3", "Башни III"),
        Map.entry("tower_cap_4", "Башни IV"),
        Map.entry("tower_cap_5", "Башни V"),
        Map.entry("super_reactivation_1", "Супер-башни I"),
        Map.entry("super_reactivation_2", "Супер-башни II"),
        Map.entry("super_reactivation_3", "Супер-башни III"),
        Map.entry("super_cost_1", "Скидка на башни I"),
        Map.entry("super_cost_2", "Скидка на башни II"),
        Map.entry("super_cost_3", "Скидка на башни III"),
        Map.entry("super_cost_4", "Скидка на башни IV"),
        Map.entry("super_cost_5", "Скидка на башни V")
    );

    private static final Map<String, String> PROGRESSION_EN = Map.ofEntries(
        Map.entry("economy_1", "Economy I"),
        Map.entry("economy_2", "Economy II"),
        Map.entry("economy_3", "Economy III"),
        Map.entry("economy_4", "Economy IV"),
        Map.entry("economy_5", "Economy V"),
        Map.entry("duplo_hp_1", "Hollow Fortitude I"),
        Map.entry("duplo_hp_2", "Hollow Fortitude II"),
        Map.entry("duplo_hp_3", "Hollow Fortitude III"),
        Map.entry("duplo_hp_4", "Hollow Fortitude IV"),
        Map.entry("duplo_hp_5", "Hollow Fortitude V"),
        Map.entry("tower_cap_1", "Towers I"),
        Map.entry("tower_cap_2", "Towers II"),
        Map.entry("tower_cap_3", "Towers III"),
        Map.entry("tower_cap_4", "Towers IV"),
        Map.entry("tower_cap_5", "Towers V"),
        Map.entry("super_reactivation_1", "Super Towers I"),
        Map.entry("super_reactivation_2", "Super Towers II"),
        Map.entry("super_reactivation_3", "Super Towers III"),
        Map.entry("super_cost_1", "Tower Discount I"),
        Map.entry("super_cost_2", "Tower Discount II"),
        Map.entry("super_cost_3", "Tower Discount III"),
        Map.entry("super_cost_4", "Tower Discount IV"),
        Map.entry("super_cost_5", "Tower Discount V")
    );

    private static final Pattern PATTERN_WAVE_STARTED = Pattern.compile("^Волна (\\d+) началась\\.$");
    private static final Pattern PATTERN_WAVE_COMPLETE_REWARD = Pattern.compile("^Волна (\\d+) завершена\\. Выбери 1 модуль награды\\.$");
    private static final Pattern PATTERN_RECEIVED_MODULE = Pattern.compile("^Получен модуль: (.+?)\\.?$");
    private static final Pattern PATTERN_OPENED_UPGRADE = Pattern.compile("^Открыто улучшение: (.+?) \\(-?(\\d+) осколков\\)\\.$");
    private static final Pattern PATTERN_MODULE_INSTALLED = Pattern.compile("^Модуль (.+?) установлен в (.+?)\\.$");
    private static final Pattern PATTERN_MODULE_REMOVED = Pattern.compile("^Модуль (.+?) снят и возвращён в запас\\.$");
    private static final Pattern PATTERN_BUILT = Pattern.compile("^Построен (.+?) в слоте (.+?) за (\\d+)\\. Баланс: (\\d+)\\.$");
    private static final Pattern PATTERN_UPGRADED = Pattern.compile("^Улучшен (.+?) в слоте (.+?) до ур\\. (\\d+)\\. Баланс: (\\d+)\\.$");
    private static final Pattern PATTERN_SOLD = Pattern.compile("^Продан (.+?) из слота (.+?)\\. Возврат: (\\d+)\\. Баланс: (\\d+)\\.(.*)$");
    private static final Pattern PATTERN_NOT_ENOUGH = Pattern.compile("^Недостаточно средств\\. Нужно: (\\d+), сейчас: (\\d+)\\.$");
    private static final Pattern PATTERN_GOBLIN_SABOTEUR = Pattern.compile("^Гоблин-саботажник уничтожил (.+?)\\.$");
    private static final Pattern PATTERN_GOBLIN_BOSS = Pattern.compile("^Гоблин-босс понизил (.+?) до ур\\. (\\d+)\\.$");
    private static final Pattern PATTERN_DISABLED_TOWERS = Pattern.compile("^(.+?) отключил башни возле себя\\.$");

    private static final Pattern PATTERN_SUPER_ACTIVE_READY = Pattern.compile("^активная • готова • (\\d+)/(\\d+)$");
    private static final Pattern PATTERN_SUPER_ACTIVE_SPENT = Pattern.compile("^активная • истощена • (\\d+)/(\\d+)$");
    private static final Pattern PATTERN_SUPER_MONOLITH_READY = Pattern.compile("^активная • готов • (\\d+)/(\\d+)$");
    private static final Pattern PATTERN_SUPER_MONOLITH_SPENT = Pattern.compile("^активная • истощён • (\\d+)/(\\d+)$");
    private static final Pattern PATTERN_IDOL_SAVINGS = Pattern.compile("^активная • накопление • (\\d+) мон\\.$");

    private static final Pattern PATTERN_WAVE_STARTED_CLEAN = Pattern.compile("^Волна (\\d+) началась\\.$");
    private static final Pattern PATTERN_WAVE_COMPLETE_REWARD_CLEAN = Pattern.compile("^Волна (\\d+) завершена\\. Выбери 1 модуль награды\\.$");
    private static final Pattern PATTERN_RECEIVED_MODULE_CLEAN = Pattern.compile("^Получен модуль: (.+?)\\.?$");
    private static final Pattern PATTERN_OPENED_UPGRADE_CLEAN = Pattern.compile("^Открыто улучшение: (.+?) \\(-?(\\d+) осколков\\)\\.$");
    private static final Pattern PATTERN_MODULE_INSTALLED_CLEAN = Pattern.compile("^Модуль (.+?) установлен в (.+?)\\.$");
    private static final Pattern PATTERN_MODULE_REMOVED_CLEAN = Pattern.compile("^Модуль (.+?) снят и возвращён в запас\\.$");
    private static final Pattern PATTERN_BUILT_CLEAN = Pattern.compile("^Построен (.+?) в слоте (.+?) за (\\d+)\\. Баланс: (\\d+)\\.$");
    private static final Pattern PATTERN_UPGRADED_CLEAN = Pattern.compile("^Улучшен (.+?) в слоте (.+?) до ур\\. (\\d+)\\. Баланс: (\\d+)\\.$");
    private static final Pattern PATTERN_SOLD_CLEAN = Pattern.compile("^Продан (.+?) из слота (.+?)\\. Возврат: (\\d+)\\. Баланс: (\\d+)\\.(.*)$");
    private static final Pattern PATTERN_NOT_ENOUGH_CLEAN = Pattern.compile("^Недостаточно средств\\. Нужно: (\\d+), сейчас: (\\d+)\\.$");
    private static final Pattern PATTERN_GOBLIN_SABOTEUR_CLEAN = Pattern.compile("^Гоблин-саботажник уничтожил (.+?)\\.$");
    private static final Pattern PATTERN_GOBLIN_BOSS_CLEAN = Pattern.compile("^Гоблин-босс понизил (.+?) до ур\\. (\\d+)\\.$");
    private static final Pattern PATTERN_DISABLED_TOWERS_CLEAN = Pattern.compile("^(.+?) отключил башни возле себя\\.$");
    private static final Pattern PATTERN_SUPER_ACTIVE_READY_CLEAN = Pattern.compile("^активная • готова • (\\d+)/(\\d+)$");
    private static final Pattern PATTERN_SUPER_ACTIVE_SPENT_CLEAN = Pattern.compile("^активная • истощена • (\\d+)/(\\d+)$");
    private static final Pattern PATTERN_SUPER_MONOLITH_READY_CLEAN = Pattern.compile("^активная • готов • (\\d+)/(\\d+)$");
    private static final Pattern PATTERN_SUPER_MONOLITH_SPENT_CLEAN = Pattern.compile("^активная • истощён • (\\d+)/(\\d+)$");
    private static final Pattern PATTERN_IDOL_SAVINGS_CLEAN = Pattern.compile("^активная • накопление • (\\d+) мон\\.$");
    private static final Pattern PATTERN_FINISH_TASK_AND_OBJECTIVE = Pattern.compile("^Сначала закончи текущую задачу\\.\\n\\n(.+)$", Pattern.DOTALL);
    private static final Pattern PATTERN_UPGRADE_FOR_CLEAN = Pattern.compile("^Улучшить за (\\d+)$");
    private static final Pattern PATTERN_SELL_FOR_CLEAN = Pattern.compile("^Продать за (\\d+)$");

    private BankDefenseLocalization() {
    }

    public static Lang language(PlayerRef playerRef) {
        if (playerRef == null) {
            return Lang.EN;
        }
        return language(playerRef.getLanguage());
    }

    public static Lang language(String languageTag) {
        if (languageTag == null || languageTag.isBlank()) {
            return Lang.EN;
        }
        String normalized = languageTag.trim().toLowerCase(Locale.ROOT);
        return normalized.startsWith("ru") ? Lang.RU : Lang.EN;
    }

    public static boolean isRussian(PlayerRef playerRef) {
        return language(playerRef) == Lang.RU;
    }

    public static String choose(PlayerRef playerRef, String ru, String en) {
        return choose(language(playerRef), ru, en);
    }

    public static String choose(Lang lang, String ru, String en) {
        return lang == Lang.RU ? ru : en;
    }

    public static String tr(PlayerRef playerRef, String key, Object... args) {
        return tr(language(playerRef), key, args);
    }

    public static String tr(Lang lang, String key, Object... args) {
        String template = switch (key) {
            case "hud.creator" -> "By QubeCore";
            case "hud.money" -> lang == Lang.RU ? "Монеты" : "Gold";
            case "hud.cores" -> lang == Lang.RU ? "Осколки" : "Shards";
            case "hud.bank" -> lang == Lang.RU ? "Здоровье Дупла" : "Hollow Health";
            case "hud.enemies" -> lang == Lang.RU ? "Враги волны" : "Wave Enemies";
            case "hud.tutorial.quest" -> lang == Lang.RU ? "Задача обучения" : "Tutorial Objective";
            case "hud.preparation" -> lang == Lang.RU ? "Подготовка" : "Preparation";
            case "hud.wave.difficulty" -> lang == Lang.RU ? "Сложность волны" : "Wave Threat";
            case "hud.paused" -> lang == Lang.RU ? "Пауза дохода" : "Income paused";
            case "hud.wave.none" -> lang == Lang.RU ? "Волна -" : "Wave -";
            case "hud.wave" -> lang == Lang.RU ? "Волна {0}" : "Wave {0}";
            case "hud.training" -> lang == Lang.RU ? "Обучение" : "Tutorial";
            case "hud.loading" -> lang == Lang.RU ? "Загрузка" : "Loading";
            case "hud.start_waiting" -> lang == Lang.RU ? "Ожидание старта" : "Waiting to start";
            case "hud.ready" -> lang == Lang.RU ? "Готовность" : "Ready";
            case "hud.cleanup" -> lang == Lang.RU ? "Зачистка" : "Cleanup";
            case "hud.in_wave" -> lang == Lang.RU ? "Волна в бою" : "Wave in progress";
            case "hud.victory" -> lang == Lang.RU ? "Победа" : "Victory";
            case "hud.defeat" -> lang == Lang.RU ? "Поражение" : "Defeat";
            case "hud.preparation_s" -> lang == Lang.RU ? "Подготовка {0}с" : "Preparation {0}s";
            case "hud.prompt.init" -> lang == Lang.RU ? "Подготовка систем дупла." : "Preparing hollow systems.";
            case "hud.prompt.new_match" -> lang == Lang.RU ? "Начните новый матч у оператора." : "Start a new match with the operator.";
            case "hud.prompt.choose_match" -> lang == Lang.RU ? "Выбери сложность и контракт у оператора." : "Choose a difficulty and contract at the operator.";
            case "hud.prompt.choose_reward" -> lang == Lang.RU ? "Выберите модуль награды у оператора." : "Choose the reward module at the operator.";
            case "hud.prompt.build" -> lang == Lang.RU ? "Поставьте хотя бы 1 башню или ловушку." : "Place at least 1 tower or trap.";
            case "page.match.open_reward" -> lang == Lang.RU ? "Выбрать модуль" : "Choose module";
            case "hud.prompt.prepare_and_start" -> lang == Lang.RU ? "Подготовь защиту и запусти волну у оператора." : "Prepare your defense and start the wave at the operator.";
            case "hud.prompt.cleanup" -> lang == Lang.RU ? "Удерживай маршрут. Мертвяки прорываются к сердцу дупла." : "Hold the route. The dead are pushing toward the hollow core.";
            case "hud.prompt.win" -> lang == Lang.RU ? "Дупло устояло. Собери награду и запусти новый матч." : "The hollow held. Take your reward and start a new match.";
            case "hud.prompt.loss" -> lang == Lang.RU ? "Потратьте осколки у Хранителя и начните новый матч." : "Spend your shards at the Keeper and start a new match.";
            case "hud.prompt.default" -> lang == Lang.RU ? "Защищай дупло и улучшай башни между волнами." : "Defend the hollow and upgrade your towers between waves.";
            case "hud.bank.stable" -> lang == Lang.RU ? "Стабильно" : "Stable";
            case "hud.enemies.waiting" -> lang == Lang.RU ? "Ожидание волны" : "Waiting for wave";
            case "hud.enemies.building" -> lang == Lang.RU ? "Подготовка обороны" : "Preparing defense";
            case "hud.enemies.clearing" -> lang == Lang.RU ? "Зачистка" : "Cleanup";
            case "hud.enemies.pressure.low" -> lang == Lang.RU ? "Финиш зачистки" : "Final cleanup";
            case "hud.enemies.pressure.mid" -> lang == Lang.RU ? "Давление держится" : "Pressure holding";
            case "hud.enemies.pressure.high" -> lang == Lang.RU ? "Орда на поле" : "Horde on the field";
            case "hud.super.inactive" -> lang == Lang.RU ? "неактивная" : "inactive";
            case "hud.super.active" -> lang == Lang.RU ? "активная" : "active";
            case "hud.super.ready" -> lang == Lang.RU ? "готова" : "ready";
            case "hud.super.ready.monolith" -> lang == Lang.RU ? "готов" : "ready";
            case "hud.super.spent" -> lang == Lang.RU ? "истощена" : "spent";
            case "hud.super.spent.monolith" -> lang == Lang.RU ? "истощён" : "spent";
            case "hud.super.state" -> lang == Lang.RU ? "{0} • {1} • {2}/{3}" : "{0} • {1} • {2}/{3}";
            case "hud.super.idol.bounty" -> lang == Lang.RU ? "активная • премия • +10% доход" : "active • bounty • +10% income";
            case "hud.super.idol.savings" -> lang == Lang.RU ? "активная • накопление • {0} мон." : "active • savings • {0} gold";
            case "hud.super.idol.none" -> lang == Lang.RU ? "активная • режим не выбран" : "active • no mode selected";
            case "hud.bank.captured" -> lang == Lang.RU ? "Дупло захвачено" : "The Hollow was taken";
            case "hud.bank.warning" -> lang == Lang.RU ? "Прорывы опасны" : "Breaches are dangerous";
            case "hud.bank.critical" -> lang == Lang.RU ? "Критическая угроза" : "Critical threat";
            case "hud.next_wave.starts_in" -> lang == Lang.RU ? "Старт через {0}с" : "Starts in {0}s";
            case "hud.defeat.banner" -> lang == Lang.RU ? "ДУПЛО ЗАХВАЧЕНО" : "THE HOLLOW HAS FALLEN";
            case "hud.defeat.hint" -> lang == Lang.RU ? "Потратьте осколки у Хранителя и начните новый матч" : "Spend your shards at the Keeper and start a new match";
            case "hud.defeat.cores" -> lang == Lang.RU ? "Ваши осколки" : "Your shards";
            case "hud.duo.team" -> lang == Lang.RU ? "Ваша сторона" : "Your side";
            case "hud.duo.team.blue" -> lang == Lang.RU ? "Синяя сторона" : "Blue side";
            case "hud.duo.team.green" -> lang == Lang.RU ? "Зелёная сторона" : "Green side";
            case "hud.duo.team.none" -> lang == Lang.RU ? "Сторона не выбрана" : "Side not selected";
            case "hud.duo.partner_money" -> lang == Lang.RU ? "Монеты союзника" : "Ally gold";
            case "hud.interact.wizard" -> lang == Lang.RU ? "Поговорить с Волшебным Квибеком" : "Talk to Wizard Kweebec";
            case "hud.interact.operator" -> lang == Lang.RU ? "Поговорить с оператором" : "Talk to the operator";
            case "hud.interact.keeper" -> lang == Lang.RU ? "Поговорить с хранителем" : "Talk to the keeper";
            case "hud.interact.stats" -> lang == Lang.RU ? "Открыть статистику" : "Open statistics";
            case "hud.interact.mode" -> lang == Lang.RU ? "Телепорт" : "Teleport";
            case "hud.interact.team" -> lang == Lang.RU ? "Выбрать сторону" : "Choose side";
            case "hud.interact.terminal.start" -> lang == Lang.RU ? "Использовать терминал" : "Use terminal";
            case "hud.interact.terminal.menu" -> lang == Lang.RU ? "Открыть меню" : "Open menu";
            case "hud.interact.chest" -> lang == Lang.RU ? "Открыть сундук" : "Open chest";
            case "hud.interact.slot" -> lang == Lang.RU ? "Открыть слот" : "Open slot";
            case "hud.interact.tower" -> lang == Lang.RU ? "Открыть башню" : "Open tower";
            case "hud.interact.super_slot" -> lang == Lang.RU ? "Открыть супер-слот" : "Open super slot";

            case "npc.operator" -> lang == Lang.RU ? "Оператор" : "Operator";
            case "npc.keeper" -> lang == Lang.RU ? "Хранитель улучшений" : "Upgrade Keeper";
            case "npc.mode" -> lang == Lang.RU ? "Телепорт" : "Teleport";
            case "npc.duo_team" -> lang == Lang.RU ? "Выбор стороны" : "Choose Side";
            case "npc.wizard" -> lang == Lang.RU ? "Волшебный Квибек" : "Wizard Kweebec";
            case "npc.duo" -> "QubeCore";
            case "npc.stats" -> lang == Lang.RU ? "Статистика" : "Statistics";
            case "contract.none" -> lang == Lang.RU ? "Без контракта" : "No Contract";

            case "tutorial.unavailable_in_tutorial" -> lang == Lang.RU ? "Недоступно в обучении." : "Unavailable in the tutorial.";

            case "tutorial.quest.find_wizard" -> lang == Lang.RU ? "Найдите волшебного квибека" : "Find Wizard Kweebec";
            case "tutorial.quest.approach_wizard" -> lang == Lang.RU ? "Подойдите к Волшебному Квибеку" : "Approach Wizard Kweebec";
            case "tutorial.quest.place_first_tower" -> lang == Lang.RU ? "Поставьте башню на площадку" : "Place a tower on a pad";
            case "tutorial.quest.start_wave" -> lang == Lang.RU ? "Запустите волну у оператора" : "Start the wave at the operator";
            case "tutorial.quest.repel_first_wave" -> lang == Lang.RU ? "Отразите первую волну" : "Repel the first wave";
            case "tutorial.quest.upgrade_any_tower" -> lang == Lang.RU ? "Поставьте любую башню и улучшите её до 3 уровня" : "Place any tower and upgrade it to level 3";
            case "tutorial.quest.collect_chest" -> lang == Lang.RU ? "Откройте сундук Гайи" : "Open Gaia's chest";
            case "tutorial.quest.hold_next_wave" -> lang == Lang.RU ? "Запустите следующую волну и удержите линию" : "Start the next wave and hold the line";
            case "tutorial.quest.choose_module" -> lang == Lang.RU ? "Выберите любой модуль" : "Choose any module";
            case "tutorial.quest.fill_all_slots" -> lang == Lang.RU ? "Установите модуль, заполните все площадки и улучшите башни до 5 уровня" : "Install a module, fill every pad, and upgrade your towers to level 5";
            case "tutorial.quest.two_exit_wave" -> lang == Lang.RU ? "Запустите волну с двумя выходами и победите" : "Start the wave with two exits and win";
            case "tutorial.quest.place_monolith" -> lang == Lang.RU ? "Поставьте Монолит на золотую площадку" : "Place the Monolith on the golden pad";
            case "tutorial.quest.withstand_ravager" -> lang == Lang.RU ? "Запустите волну и выдержите натиск Разорителя" : "Start the wave and withstand the Ravager";
            case "tutorial.quest.place_unique_traps" -> lang == Lang.RU ? "Поставьте 3 разные ловушки" : "Place 3 different traps";
            case "tutorial.quest.watch_traps" -> lang == Lang.RU ? "Следите за работой ловушек" : "Watch the traps work";
            case "tutorial.quest.spend_shards" -> lang == Lang.RU ? "Потратьте осколки у Хранителя" : "Spend shards at the Keeper";
            case "tutorial.quest.talk_wizard" -> lang == Lang.RU ? "Поговорите с Волшебным Квибеком" : "Talk to Wizard Kweebec";

            case "tutorial.dialog.intro.page1.body" -> lang == Lang.RU
                ? "О, новый защитник. Как раз вовремя. Введу тебя в курс дела. Твоя задача - защитить наше волшебное дупло от захвата бездны. Это Дупло хранит силу нашего божества - Гайи. И за этой силой охотится Варин, владыка тьмы. И если эта сила попадет не в те руки..."
                : "Ah, a new defender. Right on time. Let me bring you up to speed. Your task is to protect our magical Hollow from the Abyss. This Hollow preserves the power of our deity, Gaia. Varin, the lord of darkness, hunts that power. And if it falls into the wrong hands...";
            case "tutorial.dialog.intro.page1.button" -> lang == Lang.RU ? "Дальше" : "Continue";
            case "tutorial.dialog.intro.page1.hint" -> lang == Lang.RU ? "Квибек хочет быстро посвятить тебя в происходящее." : "Kweebec wants to quickly bring you up to speed.";
            case "tutorial.dialog.intro.page2.body" -> lang == Lang.RU ? "В общем, пойдём со мной. Покажу, как всё работает." : "Come with me and I'll show you how all of this works.";
            case "tutorial.dialog.intro.page2.button" -> lang == Lang.RU ? "Начать обучение" : "Start tutorial";
            case "tutorial.dialog.intro.page2.hint.ready" -> lang == Lang.RU ? "Кнопка отправит тебя на учебную площадку." : "This button will send you to the training ground.";
            case "tutorial.dialog.intro.page2.hint.missing_start" -> lang == Lang.RU ? "Для обучения ещё не настроен marker tutorialstart." : "The tutorial marker tutorialstart has not been configured yet.";
            case "tutorial.dialog.find.body" -> lang == Lang.RU ? "Это площадка для тренировки. Сейчас я тебе покажу, как здесь всё работает." : "This is the training ground. I'll show you how everything works here.";
            case "tutorial.dialog.find.button" -> lang == Lang.RU ? "Дальше" : "Continue";
            case "tutorial.dialog.find.hint" -> lang == Lang.RU ? "Квибек готов начать учебный забег." : "Kweebec is ready to begin the training run.";
            case "tutorial.dialog.first_tower.body" -> lang == Lang.RU ? "Начни с башни. Поставь её на любую площадку и дай врагам понять, что дорога сюда больше не свободна." : "Start with a tower. Place it on any pad and show the enemy that this road is no longer open.";
            case "tutorial.dialog.first_tower.button" -> lang == Lang.RU ? "Понял" : "Got it";
            case "tutorial.dialog.first_tower.hint" -> lang == Lang.RU ? "После кнопки появится первая задача обучения." : "The first tutorial objective will appear after this.";
            case "tutorial.dialog.return_first_tower.body" -> lang == Lang.RU ? "Молодец, теперь - запусти волну. Не бойся, эта нечисть под нашим контролем. Мы используем их, чтобы обучать наших защитников. Волну нужно запустить у специально обученного человека, он прямо справа от тебя, кстати." : "Good. Now start the wave. Don't worry, these creatures are under our control. We use them to train our defenders. The wave is started by a specially trained operator, right to your right.";
            case "tutorial.dialog.return_first_tower.button" -> lang == Lang.RU ? "Понял" : "Got it";
            case "tutorial.dialog.return_first_tower.hint" -> lang == Lang.RU ? "После закрытия диалога появится оператор обучения." : "The training operator will appear after you close the dialog.";
            case "tutorial.dialog.return_first_wave.body" -> lang == Lang.RU ? "У тебя получилось, ура! Но этого всё ещё мало: нужно ставить разные башни и улучшать их. Кликни по любой площадке, поставь другую башню и сразу улучши её до 3 уровня, чтобы она справилась со следующей волной. И да, после неё тебе дадут кое-какую награду." : "You did it! But that's still not enough: you need different towers, and you need to upgrade them. Click any pad, place another tower, and bring it straight to level 3 so it can handle the next wave. And yes, you'll get a reward after that.";
            case "tutorial.dialog.return_first_wave.button" -> lang == Lang.RU ? "За дело" : "Let's do it";
            case "tutorial.dialog.return_first_wave.hint" -> lang == Lang.RU ? "Квибек уже подготовил монеты на вторую башню." : "Kweebec has already prepared the coins for the second tower.";
            case "tutorial.dialog.chest_intro.body" -> lang == Lang.RU ? "Слушай, мне нужно кое-что ещё тебе рассказать. Во время волн ты также можешь побродить по округе и найти дары от Гайи, которые помогут тебе в бою. Обычно это выглядит как непримечательный деревянный сундук. Вот, попробуй." : "Listen, there is one more thing I need to tell you. During waves you can also roam the area and find gifts from Gaia that will help you in battle. They usually look like an unremarkable wooden chest. Here, give it a try.";
            case "tutorial.dialog.chest_intro.button" -> lang == Lang.RU ? "Попробую" : "I'll try";
            case "tutorial.dialog.chest_intro.hint" -> lang == Lang.RU ? "Слева от Квибека появился учебный сундук." : "A training chest has appeared to Kweebec's left.";
            case "tutorial.dialog.return_chest.body" -> lang == Lang.RU ? "Отлично. Такие дары Гайи могут выручить в трудную минуту. А теперь вернёмся к обороне: запусти следующую волну и удержи линию." : "Excellent. Gifts from Gaia can save you in a rough moment. Now let's get back to the defense: start the next wave and hold the line.";
            case "tutorial.dialog.return_chest.button" -> lang == Lang.RU ? "К волне" : "To the wave";
            case "tutorial.dialog.return_chest.hint" -> lang == Lang.RU ? "Оператор снова готов запустить следующую волну." : "The operator is ready to launch the next wave again.";
            case "tutorial.dialog.return_reward.body" -> lang == Lang.RU ? "Вот теперь похоже на оборону. Враги любят искать слабые места. Наша задача - сделать так, чтобы слабых мест не осталось. В конце этой волны тебе выдали модуль. Вставь его в любую из башен. И заодно, я выдам тебе много монет. Поставь во все площадки по одной башне каждого типа." : "Now this looks like a defense. Enemies love to search for weak spots. Our job is to make sure there are none. At the end of that wave, you received a module. Insert it into any tower. And while we're at it, I'll give you plenty of gold. Put one tower of each type on every pad.";
            case "tutorial.dialog.return_reward.button" -> lang == Lang.RU ? "Продолжаем" : "Continue";
            case "tutorial.dialog.return_reward.hint" -> lang == Lang.RU ? "Заполни все учебные площадки и доведи башни до 5 уровня." : "Fill all training pads and bring your towers to level 5.";
            case "tutorial.dialog.return_two_lane.body" -> lang == Lang.RU ? "Ты молодец, у тебя хорошо получается. А сейчас я тебе покажу наши секретные орудия. Это древняя сила, которую мы пробуждаем лишь в нужный момент. Поставь супер-башню на золотую площадку. Скоро поймёшь, почему мы не тратим такие вещи по пустякам. Крайне советую поставить Монолит, он поможет тебе на этой волне..." : "You're doing well. Now let me show you our secret weapons. This is an ancient power that we awaken only at the right moment. Place a super tower on the golden pad. You'll soon understand why we don't waste such things on trifles. I strongly recommend the Monolith for this wave...";
            case "tutorial.dialog.return_two_lane.button" -> lang == Lang.RU ? "Понял" : "Got it";
            case "tutorial.dialog.return_two_lane.hint" -> lang == Lang.RU ? "На золотом слоте обучения доступен только Монолит." : "Only the Monolith is available on the golden training slot.";
            case "tutorial.dialog.trap_intro.body" -> lang == Lang.RU ? "Чувствую, что наши ряды пополнятся ещё одним прекрасным защитником. Кстати, башни - не единственное, чем ты можешь защищать Дупло. По маршруту можно расставлять ловушки, которые по-разному мешают противникам. Попробуй поставить 3 ловушки и увидеть их в действии." : "I can feel our ranks are about to gain another fine defender. By the way, towers are not the only way to protect the Hollow. You can place traps along the route, and each of them hinders enemies in its own way. Try placing 3 traps and see them in action.";
            case "tutorial.dialog.trap_intro.button" -> lang == Lang.RU ? "Попробую" : "I'll try";
            case "tutorial.dialog.trap_intro.hint" -> lang == Lang.RU ? "Поставь 3 разные ловушки на учебные площадки маршрута." : "Place 3 different traps on the training route pads.";
            case "tutorial.dialog.return_traps.body" -> lang == Lang.RU ? "Вот видишь? Ловушки отлично закрывают слабые места и помогают контролировать маршрут. А теперь пора к Хранителю: он покажет, как Осколки Дупла усиливают будущие матчи." : "See? Traps are great for covering weak spots and controlling the route. Now it is time to visit the Keeper: they will show you how Hollow Shards make your future matches stronger.";
            case "tutorial.dialog.return_traps.button" -> lang == Lang.RU ? "К хранителю" : "To the Keeper";
            case "tutorial.dialog.return_traps.hint" -> lang == Lang.RU ? "Квибек готов выдать тебе первые 12 осколков." : "Kweebec is ready to give you your first 12 shards.";
            case "tutorial.dialog.return_boss.body" -> lang == Lang.RU ? "После каждой завершённой защиты тебе достаются Осколки Дупла. Они не тратятся в бою, а улучшают твои будущие матчи. Хранитель поможет превратить Осколки Дупла в улучшения, подойди и убедись." : "After every successful defense, you receive Hollow Shards. They are not spent in battle; they improve your future matches. The Keeper can turn Hollow Shards into upgrades. Go see for yourself.";
            case "tutorial.dialog.return_boss.button" -> lang == Lang.RU ? "К хранителю" : "To the Keeper";
            case "tutorial.dialog.return_boss.hint" -> lang == Lang.RU ? "Квибек прямо сейчас выдаст тебе первые 12 осколков." : "Kweebec is about to give you your first 12 shards.";
            case "tutorial.dialog.return_progression.body" -> lang == Lang.RU ? "Ты увидел всё, что тебе нужно: строй, усиливай, переживай волны, береги Дупло и становись сильнее после каждого забега. Дальше всё будет по-настоящему.\nЯ открою тебе путь туда, где оборона уже не учебная. Да прибудет с тобой Гайя!" : "You've seen everything you need: build, strengthen, survive the waves, protect the Hollow, and grow stronger after every run. From here on, it becomes real.\nI will open the path to a place where the defense is no longer a lesson. May Gaia be with you!";
            case "tutorial.dialog.return_progression.button" -> lang == Lang.RU ? "Дальше" : "Continue";
            case "tutorial.dialog.return_progression.hint" -> lang == Lang.RU ? "Квибек готов отпустить тебя на настоящую карту." : "Kweebec is ready to send you to the real map.";
            case "tutorial.dialog.final.body" -> lang == Lang.RU ? "Теперь ты знаешь всё главное: башни, модули, супер-орудия, ловушки, сундуки и Осколки Дупла. Дальше тебе предстоит защищать Дупло уже по-настоящему.\nНу что ж, удачи тебе!" : "Now you know the essentials: towers, modules, super weapons, traps, chests, and Hollow Shards. From here on, you will defend the Hollow for real.\nWell then, good luck!";
            case "tutorial.dialog.final.button" -> lang == Lang.RU ? "Закончить обучение" : "Finish tutorial";
            case "tutorial.dialog.final.hint" -> lang == Lang.RU ? "После этой кнопки ты отправишься на обычную карту." : "After this button, you will be sent to the main map.";
            case "tutorial.dialog.finish_current.body" -> lang == Lang.RU ? "Сначала закончи текущую задачу.\n\n{0}" : "Finish your current task first.\n\n{0}";
            case "tutorial.dialog.finish_current.button" -> lang == Lang.RU ? "Понял" : "Got it";
            case "tutorial.dialog.finish_current.hint" -> lang == Lang.RU ? "Квибек ждёт, пока ты выполнишь текущее поручение." : "Kweebec is waiting for you to complete the current task.";
            case "tutorial.dialog.unavailable" -> lang == Lang.RU ? "Диалог обучения недоступен." : "The tutorial dialog is unavailable.";
            case "tutorial.dialog.already_running" -> lang == Lang.RU ? "Обучение уже запущено." : "The tutorial is already running.";
            case "tutorial.dialog.show_ground" -> lang == Lang.RU ? "Квибек готов показать тебе учебную площадку." : "Kweebec is ready to show you the training grounds.";
            case "tutorial.dialog.intro_not_finished" -> lang == Lang.RU ? "Квибек ещё не закончил вступление." : "Kweebec hasn't finished the introduction yet.";
            case "tutorial.dialog.start_first" -> lang == Lang.RU ? "Сначала начни обучение у Волшебного Квибека." : "Start the tutorial with Wizard Kweebec first.";
            case "tutorial.dialog.show_first_task" -> lang == Lang.RU ? "Квибек показывает первую задачу." : "Kweebec is showing you the first task.";
            case "tutorial.dialog.start_first_tower" -> lang == Lang.RU ? "Начни с первой башни." : "Start with the first tower.";
            case "tutorial.dialog.operator_ready" -> lang == Lang.RU ? "Оператор обучения готов принять запуск первой волны." : "The training operator is ready to start the first wave.";
            case "tutorial.dialog.second_tower_funds" -> lang == Lang.RU ? "Монеты выданы. Поставь вторую башню и доведи её до 3 уровня." : "Coins granted. Place the second tower and upgrade it to level 3.";
            case "tutorial.dialog.chest_spawned" -> lang == Lang.RU ? "Учебный сундук появился слева от Квибека." : "A training chest has appeared to Kweebec's left.";
            case "tutorial.dialog.second_wave_ready" -> lang == Lang.RU ? "Оператор снова готов запустить следующую волну." : "The operator is ready to launch the next wave again.";
            case "tutorial.dialog.fill_all_slots" -> lang == Lang.RU ? "Заполни все площадки, вставь модуль и доведи башни до 5 уровня." : "Fill all pads, insert a module, and bring your towers to level 5.";
            case "tutorial.dialog.place_monolith" -> lang == Lang.RU ? "На золотом слоте ждёт Монолит." : "The Monolith is waiting on the golden slot.";
            case "tutorial.dialog.traps_ready" -> lang == Lang.RU ? "Учебные площадки ловушек готовы. Поставь 3 разные ловушки." : "The training trap pads are ready. Place 3 different traps.";
            case "tutorial.dialog.given_cores" -> lang == Lang.RU ? "Выдано 12 осколков дупла. Потрать их у Хранителя." : "12 hollow shards granted. Spend them at the Keeper.";
            case "tutorial.dialog.final_secret" -> lang == Lang.RU ? "Квибек готов завершить обучение." : "Kweebec is ready to finish the tutorial.";
            case "tutorial.dialog.complete_current_first" -> lang == Lang.RU ? "Сначала выполни текущую задачу." : "Finish your current task first.";
            case "tutorial.dialog.begin_ok" -> lang == Lang.RU ? "Квибек переносит тебя на учебную площадку." : "Kweebec sends you to the training ground.";
            case "tutorial.dialog.complete_ok" -> lang == Lang.RU ? "Обучение завершено. Квибек перенёс тебя на боевую карту." : "Tutorial complete. Kweebec has sent you to the main map.";
            case "tutorial.dialog.world_unavailable" -> lang == Lang.RU ? "Мир обучения недоступен." : "The tutorial world is unavailable.";
            case "tutorial.dialog.reset_ok" -> lang == Lang.RU ? "Обучение перезапущено. Поговори с Волшебным Квибеком снова." : "Tutorial reset. Talk to Wizard Kweebec again.";
            case "tutorial.dialog.skip_ok" -> lang == Lang.RU ? "Обучение пропущено. Игрок возвращён на основную карту." : "Tutorial skipped. The player has been returned to the main map.";
            case "tutorial.validate.map_unavailable" -> lang == Lang.RU ? "Карта обучения недоступна." : "The tutorial map is unavailable.";
            case "tutorial.validate.start_point" -> lang == Lang.RU ? "Для обучения не настроен marker tutorialstart." : "The marker tutorialstart is not configured.";
            case "tutorial.validate.wizard_intro" -> lang == Lang.RU ? "Для обучения не настроен marker wizardintro." : "The marker wizardintro is not configured.";
            case "tutorial.validate.wizard_point" -> lang == Lang.RU ? "Для обучения не настроен marker tutorialwizard." : "The marker tutorialwizard is not configured.";
            case "tutorial.validate.control" -> lang == Lang.RU ? "Для обучения не настроен marker tutorialcontrol." : "The marker tutorialcontrol is not configured.";
            case "tutorial.validate.vendor" -> lang == Lang.RU ? "Для обучения не настроен marker tutorialvendor." : "The marker tutorialvendor is not configured.";
            case "tutorial.validate.bank_and_vault" -> lang == Lang.RU ? "Для обучения нужны markers tutorialbank и tutorialvault." : "The tutorial requires the markers tutorialbank and tutorialvault.";
            case "tutorial.validate.spawns" -> lang == Lang.RU ? "Для обучения нужны markers tutorialspawna и tutorialspawnb." : "The tutorial requires the markers tutorialspawna and tutorialspawnb.";
            case "tutorial.validate.routes" -> lang == Lang.RU ? "Для обучения нужно настроить маршруты ta и tb." : "The tutorial routes ta and tb must be configured.";
            case "tutorial.validate.slots" -> lang == Lang.RU ? "Для обучения нужно минимум 6 tutorial-слотов и 1 tutorial_super-слот." : "The tutorial requires at least 6 tutorial slots and 1 tutorial super slot.";

            case "label.wave" -> lang == Lang.RU ? "Волна: {0}" : "Wave: {0}";
            case "label.wave.none" -> lang == Lang.RU ? "Волна: -" : "Wave: -";
            case "label.funds" -> lang == Lang.RU ? "Средства: {0}" : "Funds: {0}";
            case "label.funds.none" -> lang == Lang.RU ? "Средства: -" : "Funds: -";
            case "label.bank" -> lang == Lang.RU ? "Прочность дупла: {0}" : "Hollow Health: {0}";
            case "label.bank.none" -> lang == Lang.RU ? "Прочность дупла: -" : "Hollow Health: -";
            case "label.status" -> lang == Lang.RU ? "Статус: {0}" : "Status: {0}";
            case "label.status.none" -> lang == Lang.RU ? "Статус: -" : "Status: -";
            case "label.position" -> lang == Lang.RU ? "Позиция: {0}" : "Position: {0}";
            case "label.position.none" -> lang == Lang.RU ? "Позиция не задана." : "Position is not set.";

            case "page.mode.title" -> "Duplo TD: " + (lang == Lang.RU ? "телепорт" : "teleport");
            case "page.mode.header" -> lang == Lang.RU ? "Куда телепортироваться?" : "Where do you want to travel?";
            case "page.mode.header.duo" -> lang == Lang.RU ? "Куда перейти из Duo?" : "Where do you want to travel from Duo?";
            case "page.mode.header.unavailable" -> lang == Lang.RU ? "Телепорт недоступен" : "Teleport unavailable";
            case "page.mode.confirm.duo" -> lang == Lang.RU ? "Вы хотите переместиться в DUO режим?" : "Do you want to travel to DUO mode?";
            case "page.mode.confirm.solo" -> lang == Lang.RU ? "Вы хотите переместиться в SOLO режим?" : "Do you want to travel to SOLO mode?";
            case "page.mode.body.unavailable" -> lang == Lang.RU ? "Точка телепорта сейчас недоступна." : "The teleport target is currently unavailable.";
            case "page.mode.body.duo" -> lang == Lang.RU ? "После подтверждения ты будешь телепортирован на DUO карту." : "After confirming, you will be teleported to the DUO map.";
            case "page.mode.body.solo" -> lang == Lang.RU ? "После подтверждения ты будешь телепортирован на SOLO карту." : "After confirming, you will be teleported to the SOLO map.";
            case "page.mode.hint" -> lang == Lang.RU ? "Да — перемещает, Нет — закрывает окно." : "Yes teleports you, No closes the window.";
            case "page.mode.solo" -> lang == Lang.RU ? "К SOLO карте" : "To SOLO";
            case "page.mode.duo" -> lang == Lang.RU ? "К DUO карте" : "To DUO";
            case "page.mode.yes" -> lang == Lang.RU ? "Да" : "Yes";
            case "page.mode.no" -> lang == Lang.RU ? "Нет" : "No";
            case "page.mode.error" -> lang == Lang.RU ? "Ошибка телепорта: {0}" : "Teleport error: {0}";
            case "page.mode.unknown_action" -> lang == Lang.RU ? "Неизвестное действие телепорта: {0}" : "Unknown teleport action: {0}";

            case "page.duo.title" -> "Duplo TD: QubeCore";
            case "page.duo.header.solo" -> lang == Lang.RU ? "Спасибо, что играешь в Duplo TD" : "Thanks for playing Duplo TD";
            case "page.duo.header.duo" -> lang == Lang.RU ? "Спасибо, что играете в Duplo TD" : "Thanks for playing Duplo TD";
            case "page.duo.body.solo" -> lang == Lang.RU
                ? "Очень ценю, что ты заглянул в этот режим. Если хочешь следить за новостями, апдейтами и разработкой карты, заглядывай в мои сервисы ниже."
                : "I really appreciate you checking this mode out. If you want to follow news, updates, and development progress, the links below are the best place to keep up.";
            case "page.duo.body.duo" -> lang == Lang.RU
                ? "Спасибо, что тестируете Duplo TD вместе. Ниже оставил мои сервисы: там будут новости, апдейты, заметки по разработке и всё важное по карте."
                : "Thanks for checking out Duplo TD together. The links below are where I post updates, development notes, and the latest news about the map.";
            case "page.duo.hint" -> lang == Lang.RU ? "Кнопки ниже по-прежнему ведут на Telegram, YouTube и Discord." : "The buttons below still open Telegram, YouTube, and Discord.";
            case "page.duo.opening" -> lang == Lang.RU ? "Открываю {0}." : "Opening {0}.";
            case "page.duo.open_failed" -> lang == Lang.RU ? "Не удалось открыть ссылку автоматически. {0}: {1}" : "Couldn't open the link automatically. {0}: {1}";
            case "page.duo.unknown" -> lang == Lang.RU ? "Неизвестное действие: {0}" : "Unknown action: {0}";

            case "page.duoteam.title" -> "Duplo TD: " + (lang == Lang.RU ? "выбор стороны" : "side select");
            case "page.duoteam.header" -> lang == Lang.RU ? "Выбери свою сторону" : "Choose your side";
            case "page.duoteam.header.complete" -> lang == Lang.RU ? "Стороны уже выбраны" : "Teams already selected";
            case "page.duoteam.selected" -> lang == Lang.RU ? "Выбрано" : "Selected";
            case "page.duoteam.taken" -> lang == Lang.RU ? "Занято" : "Taken";
            case "page.duoteam.select" -> lang == Lang.RU ? "Выбрать" : "Choose";
            case "page.duoteam.not_duo" -> lang == Lang.RU ? "Эта панель доступна только в Duo режиме." : "This panel is only available in Duo mode.";
            case "page.duoteam.hint" -> lang == Lang.RU ? "Один игрок берёт синюю сторону, второй — зелёную. Пока обе стороны не выбраны, остальные NPC только подскажут, что нужно выбрать сторону." : "One player takes the blue side, the other takes the green side. Until both sides are chosen, the other NPCs only remind you to choose a side first.";
            case "page.duoteam.hint.blue_wait" -> lang == Lang.RU ? "Синяя сторона уже выбрана. Ждём игрока на зелёной стороне." : "The blue side has already been chosen. Waiting for the green side.";
            case "page.duoteam.hint.green_wait" -> lang == Lang.RU ? "Зелёная сторона уже выбрана. Ждём игрока на синей стороне." : "The green side has already been chosen. Waiting for the blue side.";
            case "page.duoteam.hint.complete" -> lang == Lang.RU ? "Обе стороны выбраны. Теперь можно пользоваться остальными NPC и начинать матч." : "Both teams are selected. The rest of the NPCs are now unlocked and the match can begin.";
            case "page.duoteam.error" -> lang == Lang.RU ? "Ошибка выбора стороны: {0}" : "Side select error: {0}";
            case "page.duoteam.unknown_action" -> lang == Lang.RU ? "Неизвестное действие выбора стороны: {0}" : "Unknown side select action: {0}";

            case "page.stats.title" -> "Duplo TD: " + (lang == Lang.RU ? "статистика" : "statistics");
            case "page.stats.header" -> lang == Lang.RU ? "Статистика защитника" : "Defender statistics";
            case "page.stats.lifetime.title" -> lang == Lang.RU ? "Общая статистика" : "Lifetime stats";
            case "page.stats.best.title" -> lang == Lang.RU ? "Лучший забег" : "Best run";
            case "page.stats.hint" -> lang == Lang.RU ? "Статистика обновляется по ходу матча и сохраняется после его завершения." : "Statistics update during a match and are saved after it ends.";
            case "page.stats.total_waves" -> lang == Lang.RU ? "Всего пройдено волн" : "Total waves cleared";
            case "page.stats.total_kills" -> lang == Lang.RU ? "Всего убито мобов" : "Total enemies killed";
            case "page.stats.total_spent" -> lang == Lang.RU ? "Всего потрачено монет" : "Total gold spent";
            case "page.stats.total_traps" -> lang == Lang.RU ? "Поставлено ловушек" : "Traps placed";
            case "page.stats.total_runs" -> lang == Lang.RU ? "Сыграно матчей" : "Matches played";
            case "page.stats.total_chests" -> lang == Lang.RU ? "Открыто сундуков" : "Chests opened";
            case "page.stats.total_modules" -> lang == Lang.RU ? "Установлено модулей" : "Modules installed";
            case "page.stats.best_waves" -> lang == Lang.RU ? "Пройдено волн" : "Waves cleared";
            case "page.stats.best_earned" -> lang == Lang.RU ? "Заработано монет" : "Gold earned";
            case "page.stats.best_kills" -> lang == Lang.RU ? "Убито врагов" : "Enemies killed";

            case "page.tutorial.title" -> "Duplo TD: " + (lang == Lang.RU ? "обучение" : "tutorial");
            case "page.tutorial.default_speaker" -> lang == Lang.RU ? "Волшебный Квибек" : "Wizard Kweebec";
            case "page.tutorial.ok" -> lang == Lang.RU ? "Понял" : "Got it";
            case "page.tutorial.hint" -> lang == Lang.RU ? "Волшебный Квибек готов объяснить, что происходит вокруг." : "Wizard Kweebec is ready to explain what is going on around you.";
            case "page.tutorial.continue" -> lang == Lang.RU ? "Дальше" : "Continue";
            case "page.tutorial.error" -> lang == Lang.RU ? "Ошибка диалога обучения: {0}" : "Tutorial dialog error: {0}";
            case "page.tutorial.load_error" -> lang == Lang.RU ? "Не удалось загрузить диалог обучения: {0}" : "Couldn't load the tutorial dialog: {0}";

            case "page.match.title" -> "Duplo TD: " + (lang == Lang.RU ? "старт матча" : "match setup");
            case "page.match.title.tutorial" -> "Duplo TD: " + (lang == Lang.RU ? "обучение" : "tutorial");
            case "page.match.hint" -> lang == Lang.RU ? "Выбери сложность матча, затем контракт." : "Choose a match difficulty, then a contract.";
            case "page.match.hint.duo" -> lang == Lang.RU ? "Выбери сложность. Контракты в Duo режиме отключены." : "Choose a difficulty. Contracts are disabled in Duo mode.";
            case "page.match.hint.tutorial" -> lang == Lang.RU ? "Оператор обучения запускает волны вручную. Сложность уже выбрана, контракт отключён." : "The training operator starts waves manually. Difficulty is already selected and contracts are disabled.";
            case "page.match.step1" -> lang == Lang.RU ? "Шаг 1: выбери уровень сложности." : "Step 1: choose a difficulty.";
            case "page.match.step2" -> lang == Lang.RU ? "Шаг 2: выбери контракт. Сложность: {0}." : "Step 2: choose a contract. Difficulty: {0}.";
            case "page.match.step2.duo" -> lang == Lang.RU ? "Контракты в Duo режиме отключены" : "Contracts are disabled in Duo mode";
            case "page.match.training_wave" -> lang == Lang.RU ? "Учебная волна" : "Training wave";
            case "page.match.prepared" -> lang == Lang.RU ? "Матч подготовлен" : "Match prepared";
            case "page.match.start_wave" -> lang == Lang.RU ? "Начать волну" : "Start wave";
            case "page.match.end_match" -> lang == Lang.RU ? "Завершить матч" : "End match";
            case "page.match.contracts_disabled_duo" -> lang == Lang.RU ? "Контракты в Duo режиме отключены." : "Contracts are disabled in Duo mode.";
            case "page.match.auto_start_on" -> lang == Lang.RU ? "Автостарт без ожидания: ВКЛ" : "Instant auto-start: ON";
            case "page.match.auto_start_off" -> lang == Lang.RU ? "Автостарт без ожидания: ВЫКЛ" : "Instant auto-start: OFF";
            case "page.match.status.tutorial.ready" -> lang == Lang.RU ? "Статус: всё готово к первой учебной волне" : "Status: everything is ready for the first training wave";
            case "page.match.status.tutorial.active" -> lang == Lang.RU ? "Статус: учебная волна идёт" : "Status: a training wave is in progress";
            case "page.match.status.tutorial.manual" -> lang == Lang.RU ? "Статус: следующая учебная волна запускается только вручную" : "Status: the next training wave must be started manually";
            case "page.match.status.tutorial.stopped" -> lang == Lang.RU ? "Статус: обучение остановлено" : "Status: tutorial stopped";
            case "page.match.start_waiting" -> lang == Lang.RU ? "Ожидание старта" : "Waiting to start";
            case "page.match.build_first_wave" -> lang == Lang.RU ? "Построй башни и начни первую волну" : "Build towers and start the first wave";
            case "page.match.error" -> lang == Lang.RU ? "Ошибка панели матча: {0}" : "Match panel error: {0}";
            case "page.match.read_error" -> lang == Lang.RU ? "Не удалось прочитать состояние матча: {0}" : "Couldn't read the match state: {0}";
            case "page.match.selected" -> lang == Lang.RU ? "Выбрано" : "Selected";
            case "page.match.select" -> lang == Lang.RU ? "Выбрать" : "Select";
            case "page.match.select_active" -> lang == Lang.RU ? "Выбрать [активен]" : "Select [active]";
            case "page.match.tutorial.auto_disabled" -> lang == Lang.RU ? "В обучении автостарт отключён." : "Instant auto-start is disabled in the tutorial.";
            case "page.match.tutorial.end_unavailable" -> lang == Lang.RU ? "В обучении завершение матча у оператора недоступно." : "Ending the match from the operator is unavailable in the tutorial.";
            case "page.match.tutorial.only_start_available" -> lang == Lang.RU ? "В обучении сейчас доступна только кнопка запуска волны." : "Only the start-wave button is available during the tutorial.";
            case "page.match.difficulty_unavailable" -> lang == Lang.RU ? "Эта сложность сейчас недоступна." : "This difficulty is currently unavailable.";
            case "page.match.difficulty_selected" -> lang == Lang.RU ? "Сложность выбрана. Теперь выбери контракт." : "Difficulty selected. Now choose a contract.";
            case "page.match.contract_not_found" -> lang == Lang.RU ? "Контракт не найден." : "Contract not found.";
            case "page.match.contract_unavailable" -> lang == Lang.RU ? "Этот контракт сейчас недоступен." : "This contract is currently unavailable.";
            case "page.match.unknown_action" -> lang == Lang.RU ? "Неизвестное действие панели матча: {0}" : "Unknown match panel action: {0}";

            case "page.slot.title" -> lang == Lang.RU ? "Слот" : "Slot";
            case "page.slot.hint" -> lang == Lang.RU ? "Выбери башню для слота." : "Choose a tower for this slot.";
            case "page.slot.super" -> lang == Lang.RU ? "Супер-слот" : "Super slot";
            case "page.slot.trap" -> lang == Lang.RU ? "Слот ловушки" : "Trap slot";
            case "page.slot.empty" -> lang == Lang.RU ? "Слот пуст." : "The slot is empty.";
            case "page.slot.super_empty" -> lang == Lang.RU ? "Супер-слот пуст." : "The super slot is empty.";
            case "page.slot.trap_empty" -> lang == Lang.RU ? "Ловушка не установлена." : "No trap is installed.";
            case "page.slot.build_tower" -> lang == Lang.RU ? "Построй башню" : "Build a tower";
            case "page.slot.build_super" -> lang == Lang.RU ? "Построй супер-башню" : "Build a super tower";
            case "page.slot.build_trap" -> lang == Lang.RU ? "Установи ловушку" : "Place a trap";
            case "page.slot.sell_unavailable" -> lang == Lang.RU ? "Продажа недоступна" : "Selling unavailable";
            case "page.slot.module.empty" -> lang == Lang.RU ? "Модуль: пусто" : "Module: empty";
            case "page.slot.module.unavailable" -> lang == Lang.RU ? "Модули недоступны" : "Modules unavailable";
            case "page.slot.module.remove" -> lang == Lang.RU ? "Снять модуль" : "Remove module";
            case "page.slot.modules_header" -> lang == Lang.RU ? "Модули" : "Modules";
            case "page.slot.build_header.towers" -> lang == Lang.RU ? "Башни" : "Towers";
            case "page.slot.build_header.super" -> lang == Lang.RU ? "Супер-башни" : "Super towers";
            case "page.slot.build_header.traps" -> lang == Lang.RU ? "Ловушки" : "Traps";
            case "page.slot.current.none" -> lang == Lang.RU ? "Башня не установлена" : "No tower installed";
            case "page.slot.unlock_in_progression" -> lang == Lang.RU ? "Открыть в прогрессии" : "Unlock in progression";
            case "page.slot.place" -> lang == Lang.RU ? "Поставить" : "Place";
            case "page.slot.place_for" -> lang == Lang.RU ? "Поставить • {0}" : "Place • {0}";
            case "page.slot.upgrade" -> lang == Lang.RU ? "Улучшить" : "Upgrade";
            case "page.slot.sell" -> lang == Lang.RU ? "Продать" : "Sell";
            case "page.slot.error" -> lang == Lang.RU ? "Ошибка слота: {0}" : "Slot error: {0}";
            case "page.slot.load_error" -> lang == Lang.RU ? "Не удалось обновить слот: {0}" : "Couldn't refresh the slot: {0}";
            case "page.slot.info_unavailable" -> lang == Lang.RU ? "Информация о слоте недоступна." : "Slot information is unavailable.";
            case "page.slot.unavailable" -> lang == Lang.RU ? "Слот недоступен." : "Slot unavailable.";
            case "page.slot.header.current" -> lang == Lang.RU ? "Слот: {0}" : "Slot: {0}";
            case "page.slot.button_empty" -> lang == Lang.RU ? "Эта кнопка сейчас пуста." : "This button is currently empty.";
            case "page.slot.build_unavailable" -> lang == Lang.RU ? "Эта башня сейчас недоступна для установки." : "This tower is currently unavailable for placement.";
            case "page.slot.upgrade_unavailable" -> lang == Lang.RU ? "Эту башню сейчас нельзя улучшить." : "This tower can't be upgraded right now.";
            case "page.slot.module_action_unavailable" -> lang == Lang.RU ? "Этот модуль сейчас недоступен." : "This module is currently unavailable.";
            case "page.slot.unknown_action" -> lang == Lang.RU ? "Неизвестное действие слота: {0}" : "Unknown slot action: {0}";

            case "page.super.title" -> lang == Lang.RU ? "Супер-слот" : "Super slot";
            case "page.super.hint" -> lang == Lang.RU ? "Выбери супер-башню для слота." : "Choose a super tower for this slot.";
            case "page.super.buy_for" -> lang == Lang.RU ? "Купить за {0}" : "Buy for {0}";
            case "page.super.unavailable" -> lang == Lang.RU ? "Недоступно" : "Unavailable";
            case "page.super.unlock_in_progression" -> lang == Lang.RU ? "Открыть в прогрессии" : "Unlock in progression";
            case "page.super.card.heart.title" -> lang == Lang.RU ? "Сердце корней" : "Heart of Roots";
            case "page.super.card.heart.description" -> lang == Lang.RU ? "Во время волны даёт всем башням +30% урона до конца текущей волны." : "During a wave, grants all towers +30% damage until the wave ends.";
            case "page.super.card.monolith.title" -> lang == Lang.RU ? "Штормовой монолит" : "Storm Monolith";
            case "page.super.card.monolith.description" -> lang == Lang.RU ? "Автоматически спасает дупло от прорыва и отбрасывает орду к спавнам." : "Automatically saves the Hollow from a breach and throws the horde back to the spawns.";
            case "page.super.card.idol.title" -> lang == Lang.RU ? "Идол урожая" : "Harvest Idol";
            case "page.super.card.idol.description" -> lang == Lang.RU ? "Экономическая супер-башня. После покупки выбери режим работы." : "An economic super tower. After building it, choose its operating mode.";
            case "page.super.current.none" -> lang == Lang.RU ? "Супер-башня" : "Super tower";
            case "page.super.activate" -> lang == Lang.RU ? "Активировать способность" : "Activate ability";
            case "page.super.reactivate" -> lang == Lang.RU ? "Реактивировать" : "Reactivate";
            case "page.super.mode.harvest" -> lang == Lang.RU ? "Режим: +10% монет" : "Mode: +10% gold";
            case "page.super.mode.ward" -> lang == Lang.RU ? "Режим: накопление" : "Mode: savings";
            case "page.super.mode.harvest.description" -> lang == Lang.RU ? "Постоянно усиливает весь доход: все получаемые монеты увеличены на 10%." : "Permanently boosts all income: all gold you gain is increased by 10%.";
            case "page.super.mode.ward.description" -> lang == Lang.RU ? "4 волны копит весь доход, а после 5-й волны выплачивает 1.5x от накопленного." : "Stores all income for 4 waves, then pays out 1.5x of the stored amount after the 5th wave.";
            case "page.super.status" -> lang == Lang.RU ? "Статус: {0} • Зарядов всего: {1} • Осталось: {2}" : "Status: {0} • Total charges: {1} • Remaining: {2}";
            case "page.super.ready" -> lang == Lang.RU ? "готова" : "ready";
            case "page.super.spent" -> lang == Lang.RU ? "истощена" : "spent";
            case "page.super.error" -> lang == Lang.RU ? "Ошибка супер-слота: {0}" : "Super slot error: {0}";
            case "page.super.load_error" -> lang == Lang.RU ? "Не удалось обновить супер-слот: {0}" : "Couldn't refresh the super slot: {0}";
            case "page.super.unavailable_full" -> lang == Lang.RU ? "Супер-слот недоступен." : "Super slot unavailable.";
            case "page.super.build_unavailable" -> lang == Lang.RU ? "Эта супер-башня сейчас недоступна." : "This super tower is currently unavailable.";
            case "page.super.unknown_action" -> lang == Lang.RU ? "Неизвестное действие супер-слота: {0}" : "Unknown super slot action: {0}";
            case "page.super.no_primary_command" -> lang == Lang.RU ? "Для этой супер-башни нет основной команды." : "This super tower has no primary command.";

            case "page.reward.title" -> "Duplo TD: " + (lang == Lang.RU ? "модуль" : "module reward");
            case "page.reward.hint" -> lang == Lang.RU ? "Каждые 5 волн выбирай один модуль для запаса." : "Every 5 waves, choose one module for your reserve.";
            case "page.reward.subtitle" -> lang == Lang.RU ? "Выбери один модуль. Он попадёт в общий запас и его можно будет вставить в башню во время подготовки." : "Choose one module. It will go into your shared reserve and can be inserted into a tower during preparation.";
            case "page.reward.choice_header" -> lang == Lang.RU ? "Выбор модуля" : "Module Selection";
            case "page.reward.empty" -> lang == Lang.RU ? "Пусто" : "Empty";
            case "page.reward.empty.note" -> lang == Lang.RU ? "Этот слот сейчас пуст." : "This reward slot is currently empty.";
            case "page.reward.unavailable" -> lang == Lang.RU ? "Недоступно" : "Unavailable";
            case "page.reward.select" -> lang == Lang.RU ? "Выбрать" : "Select";
            case "page.reward.error" -> lang == Lang.RU ? "Ошибка выбора модуля: {0}" : "Module selection error: {0}";
            case "page.reward.load_error" -> lang == Lang.RU ? "Не удалось загрузить награду: {0}" : "Couldn't load the reward: {0}";
            case "page.reward.none_active" -> lang == Lang.RU ? "Сейчас нет активной награды модулем." : "There is no active module reward right now.";
            case "page.reward.slot_empty" -> lang == Lang.RU ? "Этот слот награды сейчас пуст." : "This reward slot is currently empty.";

            case "page.progression.title" -> "Duplo TD: " + (lang == Lang.RU ? "Улучшения дупла" : "Hollow upgrades");
            case "page.progression.subtitle" -> lang == Lang.RU ? "Пять постоянных улучшений за осколки дупла. Эффекты применяются со следующего матча." : "Five permanent upgrades purchased with hollow shards. Effects apply from the next match onward.";
            case "page.progression.cores" -> lang == Lang.RU ? "Осколки дупла: {0}" : "Hollow shards: {0}";
            case "page.progression.stats" -> lang == Lang.RU ? "Рекорд: {0} | Забегов: {1} | Побед: {2}" : "Record: {0} | Runs: {1} | Wins: {2}";
            case "page.progression.hint" -> lang == Lang.RU ? "Открывай постоянные улучшения за осколки дупла между матчами." : "Unlock permanent upgrades with hollow shards between matches.";
            case "page.progression.hint.locked" -> lang == Lang.RU ? "Улучшения за осколки дупла доступны только между матчами." : "Hollow shard upgrades are only available between matches.";
            case "page.progression.economy" -> lang == Lang.RU ? "Экономика" : "Economy";
            case "page.progression.duplo" -> lang == Lang.RU ? "Крепость дупла" : "Hollow Fortitude";
            case "page.progression.towers" -> lang == Lang.RU ? "Башни" : "Towers";
            case "page.progression.super" -> lang == Lang.RU ? "Супер-башни" : "Super towers";
            case "page.progression.discount" -> lang == Lang.RU ? "Скидка на башни" : "Tower discount";
            case "page.progression.maximum" -> lang == Lang.RU ? "Максимум" : "Maximum";
            case "page.progression.after_match" -> lang == Lang.RU ? "После матча" : "After match";
            case "page.progression.error" -> lang == Lang.RU ? "Ошибка улучшений: {0}" : "Upgrade error: {0}";
            case "page.progression.load_error" -> lang == Lang.RU ? "Не удалось загрузить прогрессию: {0}" : "Couldn't load progression: {0}";
            case "page.progression.unknown_action" -> lang == Lang.RU ? "Неизвестное действие улучшений: {0}" : "Unknown upgrade action: {0}";
            case "page.progression.unknown_line" -> lang == Lang.RU ? "Неизвестная линия улучшений: {0}" : "Unknown upgrade line: {0}";
            case "page.progression.line_maxed" -> lang == Lang.RU ? "{0} уже прокачана до максимума." : "{0} is already fully upgraded.";
            case "page.progression.line.full" -> lang == Lang.RU ? "Линия прокачана полностью." : "This line is fully upgraded.";
            case "page.progression.desc.default" -> lang == Lang.RU ? "Улучшение." : "Upgrade.";
            case "page.progression.desc.economy.current" -> lang == Lang.RU ? "Стартовый запас монет перед матчем.\nСейчас: +{0} монет." : "Extra starting gold before a match.\nCurrent: +{0} gold.";
            case "page.progression.desc.economy.next" -> lang == Lang.RU ? "\nСледующий уровень: +{0} монет." : "\nNext level: +{0} gold.";
            case "page.progression.desc.duplo.current" -> lang == Lang.RU ? "Повышает запас здоровья дупла.\nСейчас: +{0} HP." : "Increases the Hollow's health pool.\nCurrent: +{0} HP.";
            case "page.progression.desc.duplo.next" -> lang == Lang.RU ? "\nСледующий уровень: +{0} HP." : "\nNext level: +{0} HP.";
            case "page.progression.desc.towers.current" -> lang == Lang.RU ? "Открывает более высокие уровни сразу для всех башен.\nСейчас доступен максимум: {0} ур." : "Unlocks higher starting level caps for all towers.\nCurrent maximum: lvl {0}.";
            case "page.progression.desc.towers.next" -> lang == Lang.RU ? "\nСледующий уровень откроет {0} ур." : "\nNext level unlocks lvl {0}.";
            case "page.progression.desc.towers.full" -> lang == Lang.RU ? "\nОткрыт полный кап до 10 уровня." : "\nThe full cap up to level 10 is unlocked.";
            case "page.progression.desc.super.current" -> lang == Lang.RU ? "Даёт дополнительные реактивации Сердцу корней и Штормовому монолиту.\nСейчас: +{0} реактивации для каждой." : "Grants extra reactivations to Heart of Roots and the Storm Monolith.\nCurrent: +{0} reactivations for each.";
            case "page.progression.desc.super.next" -> lang == Lang.RU ? "\nСледующий уровень: ещё +1 реактивация." : "\nNext level: another +1 reactivation.";
            case "page.progression.desc.discount.current" -> lang == Lang.RU ? "Снижает стоимость покупки и улучшения всех башен.\nСейчас: -{0}% к цене." : "Reduces the purchase and upgrade cost of all towers.\nCurrent: -{0}% cost.";
            case "page.progression.desc.discount.next" -> lang == Lang.RU ? "\nСледующий уровень: ещё -{0}%." : "\nNext level: another -{0}%.";
            case "page.progression.desc.discount.full" -> lang == Lang.RU ? "\nДостигнута максимальная скидка в 10%." : "\nMaximum 10% discount reached.";

            case "page.control.title" -> "Duplo TD: " + (lang == Lang.RU ? "чит-меню" : "cheat panel");
            case "page.control.subtitle" -> lang == Lang.RU ? "Деньги, ядра дупла, модули, радиусы башен и быстрый переход к нужной волне." : "Gold, hollow cores, modules, tower ranges, and a quick jump to the desired wave.";
            case "page.control.hint" -> lang == Lang.RU ? "Чит-меню: деньги, ядра дупла, модули, радиусы башен и стартовая волна." : "Cheat panel: gold, hollow cores, modules, tower ranges, and the starting wave.";
            case "page.control.cores" -> lang == Lang.RU ? "Ядра дупла: {0}" : "Hollow cores: {0}";
            case "page.control.money.header" -> lang == Lang.RU ? "Деньги" : "Gold";
            case "page.control.cores.header" -> lang == Lang.RU ? "Ядра дупла" : "Hollow cores";
            case "page.control.modules.header" -> lang == Lang.RU ? "Модули и отладка" : "Modules and debug";
            case "page.control.wave.header" -> lang == Lang.RU ? "Стартовая волна" : "Starting wave";
            case "page.control.modules.grant" -> lang == Lang.RU ? "Выдать все по 1" : "Grant one of each";
            case "page.control.ranges.on" -> lang == Lang.RU ? "Радиусы башен: ВКЛ" : "Tower ranges: ON";
            case "page.control.ranges.off" -> lang == Lang.RU ? "Радиусы башен: ВЫКЛ" : "Tower ranges: OFF";
            case "page.control.set_wave" -> lang == Lang.RU ? "Установить волну" : "Set wave";
            case "page.control.error" -> lang == Lang.RU ? "Ошибка чит-меню: {0}" : "Cheat panel error: {0}";
            case "page.control.read_error" -> lang == Lang.RU ? "Не удалось прочитать состояние матча: {0}" : "Couldn't read the match state: {0}";
            case "page.control.unknown_action" -> lang == Lang.RU ? "Неизвестное действие чит-меню: {0}" : "Unknown cheat panel action: {0}";
            case "page.control.wave.placeholder" -> lang == Lang.RU ? "Например: 30" : "Example: 30";

            default -> key;
        };
        return format(BankDefenseTextSupport.clean(template), args);
    }

    public static String towerDisplayName(PlayerRef playerRef, String towerId, String fallback) {
        return localizedById(language(playerRef), towerId, fallback, TOWER_RU, TOWER_EN);
    }

    public static String towerShortName(PlayerRef playerRef, String towerId, String fallback) {
        return localizedById(language(playerRef), towerId, fallback, TOWER_SHORT_RU, TOWER_SHORT_EN);
    }

    public static String moduleDisplayName(PlayerRef playerRef, String moduleId, String fallback) {
        return localizedById(language(playerRef), moduleId, fallback, MODULE_RU, MODULE_EN);
    }

    public static String moduleShortName(PlayerRef playerRef, String moduleId, String fallback) {
        return localizedById(language(playerRef), moduleId, fallback, MODULE_SHORT_RU, MODULE_SHORT_EN);
    }

    public static String moduleNote(PlayerRef playerRef, String moduleId, String fallback) {
        return localizedById(language(playerRef), moduleId, fallback, MODULE_NOTE_RU, MODULE_NOTE_EN);
    }

    public static String enemyDisplayName(PlayerRef playerRef, String enemyId, String fallback) {
        return localizedById(language(playerRef), enemyId, fallback, ENEMY_RU, ENEMY_EN);
    }

    public static String contractDisplayName(PlayerRef playerRef, String contractId, String fallback) {
        return localizedById(language(playerRef), contractId, fallback, CONTRACT_RU, CONTRACT_EN);
    }

    public static String contractNote(PlayerRef playerRef, String contractId, String fallback) {
        if (language(playerRef) == Lang.RU) {
            return cleanedOrFallback(fallback, "");
        }
        return cleanedOrFallback(CONTRACT_NOTE_EN.get(contractId), fallback);
    }

    public static String progressionDisplayName(PlayerRef playerRef, String nodeId, String fallback) {
        return localizedById(language(playerRef), nodeId, fallback, PROGRESSION_RU, PROGRESSION_EN);
    }

    public static String difficultyDisplayName(PlayerRef playerRef, String difficultyId, String fallback) {
        return localizedById(language(playerRef), difficultyId, fallback, DIFFICULTY_RU, DIFFICULTY_EN);
    }

    public static String difficultyNote(PlayerRef playerRef, String difficultyId, String fallback) {
        if (language(playerRef) == Lang.RU) {
            return cleanedOrFallback(fallback, "");
        }
        return switch (difficultyId) {
            case "easy" -> "A relaxed mode: gentler waves, a richer start, and forgiving economy even without progression.";
            case "normal" -> "The core mode: expects you to use the mechanics, but stays comfortable even without shard upgrades.";
            case "hard" -> "Demands better tactics and cleaner pacing: denser waves, tighter economy, and mistakes are punished.";
            case "nightmare" -> "Challenge mode: heavy pressure, little room for mistakes, and full rewards only for perfect tempo.";
            default -> cleanedOrFallback(fallback, "");
        };
    }

    public static String seedIdolModeName(PlayerRef playerRef, int mode) {
        Lang lang = language(playerRef);
        return switch (mode) {
            case 1 -> lang == Lang.RU ? "Премия" : "Bounty";
            case 2 -> lang == Lang.RU ? "Накопление" : "Savings";
            default -> lang == Lang.RU ? "Не выбран" : "Not selected";
        };
    }

    public static String translateFreeform(PlayerRef playerRef, String text) {
        return translateFreeform(language(playerRef), text);
    }

    public static String translateFreeform(Lang lang, String text) {
        String cleaned = BankDefenseTextSupport.clean(text);
        if (lang == Lang.RU || cleaned.isBlank()) {
            return cleaned;
        }

        String exact = translateExactEn(cleaned);
        if (exact != null) {
            return exact;
        }

        Matcher matcher = PATTERN_FINISH_TASK_AND_OBJECTIVE.matcher(cleaned);
        if (matcher.matches()) {
            return "Finish your current task first.\n\n" + translateFreeform(lang, matcher.group(1));
        }
        matcher = PATTERN_UPGRADE_FOR_CLEAN.matcher(cleaned);
        if (matcher.matches()) {
            return format("Upgrade for {0}", matcher.group(1));
        }
        matcher = PATTERN_SELL_FOR_CLEAN.matcher(cleaned);
        if (matcher.matches()) {
            return format("Sell for {0}", matcher.group(1));
        }
        matcher = PATTERN_WAVE_STARTED_CLEAN.matcher(cleaned);
        if (matcher.matches()) {
            return format("Wave {0} has started.", matcher.group(1));
        }
        matcher = PATTERN_WAVE_COMPLETE_REWARD_CLEAN.matcher(cleaned);
        if (matcher.matches()) {
            return format("Wave {0} is complete. Choose 1 module reward.", matcher.group(1));
        }
        matcher = PATTERN_RECEIVED_MODULE_CLEAN.matcher(cleaned);
        if (matcher.matches()) {
            return format("Module received: {0}.", translateKnownNameEn(matcher.group(1)));
        }
        matcher = PATTERN_OPENED_UPGRADE_CLEAN.matcher(cleaned);
        if (matcher.matches()) {
            return format("Upgrade unlocked: {0} (-{1} shards).", translateKnownNameEn(matcher.group(1)), matcher.group(2));
        }
        matcher = PATTERN_MODULE_INSTALLED_CLEAN.matcher(cleaned);
        if (matcher.matches()) {
            return format("{0} was installed into {1}.", translateKnownNameEn(matcher.group(1)), translateKnownNameEn(matcher.group(2)));
        }
        matcher = PATTERN_MODULE_REMOVED_CLEAN.matcher(cleaned);
        if (matcher.matches()) {
            return format("Module {0} was removed and returned to storage.", translateKnownNameEn(matcher.group(1)));
        }
        matcher = PATTERN_BUILT_CLEAN.matcher(cleaned);
        if (matcher.matches()) {
            return format("Built {0} in slot {1} for {2}. Balance: {3}.", translateKnownNameEn(matcher.group(1)), matcher.group(2), matcher.group(3), matcher.group(4));
        }
        matcher = PATTERN_UPGRADED_CLEAN.matcher(cleaned);
        if (matcher.matches()) {
            return format("Upgraded {0} in slot {1} to lvl {2}. Balance: {3}.", translateKnownNameEn(matcher.group(1)), matcher.group(2), matcher.group(3), matcher.group(4));
        }
        matcher = PATTERN_SOLD_CLEAN.matcher(cleaned);
        if (matcher.matches()) {
            String suffix = matcher.group(5) == null ? "" : matcher.group(5).trim();
            if (!suffix.isBlank()) {
                suffix = " Module returned to storage.";
            }
            return format("Sold {0} from slot {1}. Refund: {2}. Balance: {3}.{4}", translateKnownNameEn(matcher.group(1)), matcher.group(2), matcher.group(3), matcher.group(4), suffix);
        }
        matcher = PATTERN_NOT_ENOUGH_CLEAN.matcher(cleaned);
        if (matcher.matches()) {
            return format("Not enough funds. Need: {0}, current: {1}.", matcher.group(1), matcher.group(2));
        }
        matcher = PATTERN_GOBLIN_SABOTEUR_CLEAN.matcher(cleaned);
        if (matcher.matches()) {
            return format("Goblin Saboteur destroyed {0}.", translateKnownNameEn(matcher.group(1)));
        }
        matcher = PATTERN_GOBLIN_BOSS_CLEAN.matcher(cleaned);
        if (matcher.matches()) {
            return format("Goblin Bomber lowered {0} to lvl {1}.", translateKnownNameEn(matcher.group(1)), matcher.group(2));
        }
        matcher = PATTERN_DISABLED_TOWERS_CLEAN.matcher(cleaned);
        if (matcher.matches()) {
            return format("{0} disabled nearby towers.", translateKnownNameEn(matcher.group(1)));
        }
        matcher = PATTERN_SUPER_ACTIVE_READY_CLEAN.matcher(cleaned);
        if (matcher.matches()) {
            return format("active • ready • {0}/{1}", matcher.group(1), matcher.group(2));
        }
        matcher = PATTERN_SUPER_ACTIVE_SPENT_CLEAN.matcher(cleaned);
        if (matcher.matches()) {
            return format("active • exhausted • {0}/{1}", matcher.group(1), matcher.group(2));
        }
        matcher = PATTERN_SUPER_MONOLITH_READY_CLEAN.matcher(cleaned);
        if (matcher.matches()) {
            return format("active • ready • {0}/{1}", matcher.group(1), matcher.group(2));
        }
        matcher = PATTERN_SUPER_MONOLITH_SPENT_CLEAN.matcher(cleaned);
        if (matcher.matches()) {
            return format("active • exhausted • {0}/{1}", matcher.group(1), matcher.group(2));
        }
        matcher = PATTERN_IDOL_SAVINGS_CLEAN.matcher(cleaned);
        if (matcher.matches()) {
            return format("active • savings • {0} gold", matcher.group(1));
        }

        matcher = PATTERN_WAVE_STARTED.matcher(cleaned);
        if (matcher.matches()) {
            return format("Wave {0} has started.", matcher.group(1));
        }
        matcher = PATTERN_WAVE_COMPLETE_REWARD.matcher(cleaned);
        if (matcher.matches()) {
            return format("Wave {0} is complete. Choose 1 module reward.", matcher.group(1));
        }
        matcher = PATTERN_RECEIVED_MODULE.matcher(cleaned);
        if (matcher.matches()) {
            return format("Module received: {0}.", translateKnownNameEn(matcher.group(1)));
        }
        matcher = PATTERN_OPENED_UPGRADE.matcher(cleaned);
        if (matcher.matches()) {
            return format("Upgrade unlocked: {0} (-{1} shards).", translateKnownNameEn(matcher.group(1)), matcher.group(2));
        }
        matcher = PATTERN_MODULE_INSTALLED.matcher(cleaned);
        if (matcher.matches()) {
            return format("{0} was installed into {1}.", translateKnownNameEn(matcher.group(1)), translateKnownNameEn(matcher.group(2)));
        }
        matcher = PATTERN_MODULE_REMOVED.matcher(cleaned);
        if (matcher.matches()) {
            return format("Module {0} was removed and returned to storage.", translateKnownNameEn(matcher.group(1)));
        }
        matcher = PATTERN_BUILT.matcher(cleaned);
        if (matcher.matches()) {
            return format("Built {0} in slot {1} for {2}. Balance: {3}.", translateKnownNameEn(matcher.group(1)), matcher.group(2), matcher.group(3), matcher.group(4));
        }
        matcher = PATTERN_UPGRADED.matcher(cleaned);
        if (matcher.matches()) {
            return format("Upgraded {0} in slot {1} to lvl {2}. Balance: {3}.", translateKnownNameEn(matcher.group(1)), matcher.group(2), matcher.group(3), matcher.group(4));
        }
        matcher = PATTERN_SOLD.matcher(cleaned);
        if (matcher.matches()) {
            String suffix = matcher.group(5) == null ? "" : matcher.group(5).trim();
            if (!suffix.isBlank()) {
                suffix = " Module returned to storage.";
            }
            return format("Sold {0} from slot {1}. Refund: {2}. Balance: {3}.{4}", translateKnownNameEn(matcher.group(1)), matcher.group(2), matcher.group(3), matcher.group(4), suffix);
        }
        matcher = PATTERN_NOT_ENOUGH.matcher(cleaned);
        if (matcher.matches()) {
            return format("Not enough funds. Need: {0}, current: {1}.", matcher.group(1), matcher.group(2));
        }
        matcher = PATTERN_GOBLIN_SABOTEUR.matcher(cleaned);
        if (matcher.matches()) {
            return format("Goblin Saboteur destroyed {0}.", translateKnownNameEn(matcher.group(1)));
        }
        matcher = PATTERN_GOBLIN_BOSS.matcher(cleaned);
        if (matcher.matches()) {
            return format("Goblin Bomber lowered {0} to lvl {1}.", translateKnownNameEn(matcher.group(1)), matcher.group(2));
        }
        matcher = PATTERN_DISABLED_TOWERS.matcher(cleaned);
        if (matcher.matches()) {
            return format("{0} disabled nearby towers.", translateKnownNameEn(matcher.group(1)));
        }
        matcher = PATTERN_SUPER_ACTIVE_READY.matcher(cleaned);
        if (matcher.matches()) {
            return format("active • ready • {0}/{1}", matcher.group(1), matcher.group(2));
        }
        matcher = PATTERN_SUPER_ACTIVE_SPENT.matcher(cleaned);
        if (matcher.matches()) {
            return format("active • exhausted • {0}/{1}", matcher.group(1), matcher.group(2));
        }
        matcher = PATTERN_SUPER_MONOLITH_READY.matcher(cleaned);
        if (matcher.matches()) {
            return format("active • ready • {0}/{1}", matcher.group(1), matcher.group(2));
        }
        matcher = PATTERN_SUPER_MONOLITH_SPENT.matcher(cleaned);
        if (matcher.matches()) {
            return format("active • exhausted • {0}/{1}", matcher.group(1), matcher.group(2));
        }
        matcher = PATTERN_IDOL_SAVINGS.matcher(cleaned);
        if (matcher.matches()) {
            return format("active • savings • {0} gold", matcher.group(1));
        }
        return cleaned;
    }

    public static void sendWorldMessage(World world, String text) {
        if (world == null || text == null || text.isBlank()) {
            return;
        }
        for (Player player : world.getPlayers()) {
            if (player == null || player.getPlayerRef() == null) {
                continue;
            }
            player.sendMessage(Message.raw(translateFreeform(player.getPlayerRef(), text)));
        }
    }

    private static String translateExactEn(String cleaned) {
        return switch (cleaned) {
            case "Дальше" -> "Next";
            case "Понял" -> "Got it";
            case "За дело" -> "Let's do it";
            case "Продолжаем" -> "Continue";
            case "К хранителю" -> "To the Keeper";
            case "Закончить обучение" -> "Finish tutorial";
            case "Начать обучение" -> "Start tutorial";
            case "Обучение" -> "Tutorial";
            case "Ожидание старта" -> "Waiting to start";
            case "Волна -" -> "Wave -";
            case "Загрузка" -> "Loading";
            case "Выбери башню для слота." -> "Choose a tower for this slot.";
            case "Выбор модуля" -> "Module Selection";
            case "Каждые 5 волн выбирай один модуль для запаса." -> "Every 5 waves, choose one module for your reserve.";
            case "Выбери один модуль. Он попадёт в общий запас и его можно будет вставить в башню во время подготовки." -> "Choose one module. It will go into your shared reserve and can be inserted into a tower during preparation.";
            case "Продать" -> "Sell";
            case "Снять модуль" -> "Remove module";
            case "Улучшить" -> "Upgrade";
            case "Нужна мета-прокачка" -> "Requires progression upgrade";
            case "Продажа недоступна" -> "Selling unavailable";
            case "Выбери 1 модуль награды за волну." -> "Choose 1 module reward for the wave.";
            case "Сначала выбери модуль награды за волну." -> "Choose a wave reward module first.";
            case "Этот модуль сейчас недоступен." -> "This module is currently unavailable.";
            case "Сейчас Квибек ждёт другое действие." -> "Kweebec is waiting for a different action right now.";
            case "Сначала вставь модуль, заполни все площадки и доведи башни до 5 уровня." -> "First insert a module, fill all pads, and bring your towers to level 5.";
            case "Квибек хочет быстро посвятить тебя в происходящее." -> "Kweebec wants to quickly bring you up to speed.";
            case "Кнопка отправит тебя на учебную площадку." -> "This button will send you to the training ground.";
            case "Квибек готов начать учебный забег." -> "Kweebec is ready to begin the training run.";
            case "После кнопки появится первая задача обучения." -> "The first tutorial objective will appear after this.";
            case "После закрытия диалога появится оператор обучения." -> "The training operator will appear after you close the dialog.";
            case "Квибек уже подготовил монеты на вторую башню." -> "Kweebec has already prepared the coins for the second tower.";
            case "Заполни все учебные площадки и доведи башни до 5 уровня." -> "Fill all training pads and bring your towers to level 5.";
            case "На золотом слоте обучения доступен только Монолит." -> "Only the Monolith is available on the golden training slot.";
            case "Квибек прямо сейчас выдаст тебе первые 12 осколков." -> "Kweebec is about to give you your first 12 shards.";
            case "Квибек готов отпустить тебя на настоящую карту." -> "Kweebec is ready to send you to the real map.";
            case "После этой кнопки ты отправишься на обычную карту." -> "After this button, you will be sent to the main map.";
            case "Сначала закончи текущую задачу." -> "Finish your current task first.";
            case "Квибек ждёт, пока ты выполнишь текущее поручение." -> "Kweebec is waiting for you to complete the current task.";
            case "Оператор" -> "Operator";
            case "Хранитель улучшений" -> "Upgrade Keeper";
            case "Выбор режима" -> "Mode Select";
            case "Волшебный Квибек" -> "Wizard Kweebec";
            case "Переход в Duo Режим" -> "Switch to Duo Mode";
            case "Подготовка систем дупла." -> "Preparing hollow systems.";
            case "Начните новый матч у оператора." -> "Start a new match at the operator.";
            case "Выбери сложность и контракт у оператора." -> "Choose a difficulty and contract at the operator.";
            case "Поставьте башни на площадки." -> "Place towers on the pads.";
            case "Подготовь защиту и запусти волну у оператора." -> "Prepare your defense and start the wave at the operator.";
            case "Удерживай маршрут. Мертвяки прорываются к сердцу дупла." -> "Hold the route. The dead are pushing toward the hollow's heart.";
            case "Защищай дупло и улучшай башни между волнами." -> "Defend the hollow and upgrade your towers between waves.";
            case "Дупло устояло. Победа!" -> "The hollow stood strong. Victory!";
            case "Маршрут врагов не настроен. Матч остановлен." -> "The enemy route is not configured. The match has been stopped.";
            case "Сердце корней активировано: все башни получают +30% урона до конца текущей волны." -> "Heart of Roots activated: all towers gain +30% damage until the end of the current wave.";
            case "Некрокороль поднял костяных стражей. Пока они живы, он неуязвим!" -> "The Necro King has raised bone guardians. While they live, he is invulnerable!";
            case "Разоритель дупла отключил часть башен поблизости!" -> "The Hollow Ravager disabled some of the nearby towers!";
            case "Гоблин-саботажник выскочил на поле, но не нашёл башню для подрыва." -> "The Goblin Saboteur jumped onto the field but found no tower to destroy.";
            case "Гоблин-босс разворошил защиту, но не нашёл башню для понижения уровня." -> "The Goblin Bomber rattled the defenses but found no tower to downgrade.";
            case "Квибек готов показать тебе учебную площадку." -> "Kweebec is ready to show you the training grounds.";
            case "Квибек ещё не закончил вступление." -> "Kweebec hasn't finished the introduction yet.";
            case "Сначала начни обучение у Волшебного Квибека." -> "Start the tutorial with Wizard Kweebec first.";
            case "Квибек показывает первую задачу." -> "Kweebec is showing you the first task.";
            case "Начни с первой башни." -> "Start with the first tower.";
            case "Оператор обучения готов принять запуск первой волны." -> "The training operator is ready to start the first wave.";
            case "Монеты выданы. Поставь вторую башню и доведи её до 3 уровня." -> "Coins granted. Place the second tower and upgrade it to level 3.";
            case "Заполни все площадки, вставь модуль и доведи башни до 5 уровня." -> "Fill all pads, insert a module, and bring your towers to level 5.";
            case "На золотом слоте ждёт Монолит." -> "The Monolith is waiting on the golden slot.";
            case "Выдано 12 осколков дупла. Потрать их у Хранителя." -> "12 hollow shards granted. Spend them at the Keeper.";
            case "Квибек делится последним секретом." -> "Kweebec is ready to finish the tutorial.";
            case "Сначала выполни текущую задачу." -> "Finish your current task first.";
            case "Диалог обучения недоступен." -> "The tutorial dialog is unavailable.";
            case "Обучение уже запущено." -> "The tutorial is already running.";
            case "Обучение завершено. Квибек перенёс тебя на боевую карту." -> "Tutorial complete. Kweebec has sent you to the main map.";
            case "Мир обучения недоступен." -> "The tutorial world is unavailable.";
            case "Обучение перезапущено. Поговори с Волшебным Квибеком снова." -> "Tutorial reset. Talk to Wizard Kweebec again.";
            case "Обучение пропущено. Игрок возвращён на основную карту." -> "Tutorial skipped. The player has been returned to the main map.";
            case "Автостарт без ожидания: ВКЛ." -> "Instant auto-start: ON.";
            case "Автостарт без ожидания: ВЫКЛ." -> "Instant auto-start: OFF.";
            case "Найдите волшебного квибека" -> "Find Wizard Kweebec";
            case "Подойдите к Волшебному Квибеку" -> "Approach Wizard Kweebec";
            case "Поговорите с Волшебным Квибеком" -> "Talk to Wizard Kweebec";
            case "Поставьте башню на площадку" -> "Place a tower on a pad";
            case "Запустите волну у оператора" -> "Start the wave at the operator";
            case "Отразите первую волну" -> "Repel the first wave";
            case "Поставьте любую башню и улучшите её до 3 уровня" -> "Place any tower and upgrade it to level 3";
            case "Запустите следующую волну и удержите линию" -> "Start the next wave and hold the line";
            case "Выберите любой модуль" -> "Choose any module";
            case "Установите модуль, заполните все площадки и улучшите башни до 5 уровня" -> "Install a module, fill every pad, and upgrade your towers to level 5";
            case "Запустите волну с двумя выходами и победите" -> "Start the wave with two exits and win";
            case "Поставьте Монолит на золотую площадку" -> "Place the Monolith on the golden pad";
            case "Запустите волну и выдержите натиск Разорителя" -> "Start the wave and withstand the Ravager";
            case "Потратьте осколки у Хранителя" -> "Spend shards at the Keeper";
            case "неактивная" -> "inactive";
            case "активная • премия • +10% доход" -> "active • bounty • +10% income";
            case "активная • режим не выбран" -> "active • no mode selected";
            case "О, новый защитник. Как раз вовремя. Введу тебя в курс дела. Твоя задача - защитить наше волшебное дупло от захвата бездны. Это Дупло хранит силу нашего божества - Гайи. И за этой силой охотится Варин, владыка тьмы. И если эта сила попадет не в те руки..." -> "Ah, a new defender. Right on time. Let me bring you up to speed. Your task is to protect our magical hollow from the Abyss. This Hollow preserves the power of our deity, Gaia. Varin, the lord of darkness, hunts that power. And if it falls into the wrong hands...";
            case "В общем, пойдём со мной. Покажу, как всё работает." -> "Come with me and I'll show you how all of this works.";
            case "Это площадка для тренировки. Сейчас я тебе покажу, как здесь всё работает." -> "This is the training ground. I'll show you how everything works here.";
            case "Начни с башни. Поставь её на любую площадку и дай врагам понять, что дорога сюда больше не свободна." -> "Start with a tower. Place it on any pad and show the enemy that this road is no longer open.";
            case "Молодец, теперь - запусти волну. Не бойся, эта нечисть под нашим контролем. Мы используем их, чтобы обучать наших защитников. Волну нужно запустить у специально обученного человека, он прямо справа от тебя, кстати." -> "Good. Now start the wave. Don't worry, these creatures are under our control. We use them to train our defenders. The wave is started by a specially trained operator, right to your right.";
            case "У тебя получилось, ура! Но этого всё ещё мало: нужно ставить разные башни и улучшать их. Кликни по любой площадке, поставь другую башню и сразу улучши её до 3 уровня, чтобы она справилась со следующей волной. И да, после неё тебе дадут кое-какую награду." -> "You did it! But that's still not enough: you need different towers, and you need to upgrade them. Click any pad, place another tower, and bring it straight to level 3 so it can handle the next wave. And yes, you'll get a reward after that.";
            case "Вот теперь похоже на оборону. Враги любят искать слабые места. Наша задача - сделать так, чтобы слабых мест не осталось. В конце этой волны тебе выдали модуль. Вставь его в любую из башен. И заодно, я выдам тебе много монет. Поставь во все площадки по одной башне каждого типа." -> "Now this looks like a defense. Enemies love to search for weak spots. Our job is to make sure there are none. At the end of that wave, you received a module. Insert it into any tower. And while we're at it, I'll give you plenty of gold. Put one tower of each type on every pad.";
            case "Ты молодец, у тебя хорошо получается. А сейчас я тебе покажу наши секретные орудия. Это древняя сила, которую мы пробуждаем лишь в нужный момент. Поставь супер-башню на золотую площадку. Скоро поймёшь, почему мы не тратим такие вещи по пустякам. Крайне советую поставить Монолит, он поможет тебе на этой волне..." -> "You're doing well. Now let me show you our secret weapons. This is an ancient power that we awaken only at the right moment. Place a super tower on the golden pad. You'll soon understand why we don't waste such things on trifles. I strongly recommend the Monolith for this wave...";
            case "После каждой завершённой защиты тебе достаются Осколки Дупла. Они не тратятся в бою, а улучшают твои будущие матчи. Хранитель поможет превратить Осколки Дупла в улучшения, подойди и убедись." -> "After every successful defense, you receive Hollow Shards. They are not spent in battle; they improve your future matches. The Keeper can turn Hollow Shards into upgrades. Go see for yourself.";
            case "Ты увидел всё, что тебе нужно: строй, усиливай, переживай волны, береги Дупло и становись сильнее после каждого забега. Дальше всё будет по-настоящему.\nЯ открою тебе путь туда, где оборона уже не учебная. Да прибудет с тобой Гайя!" -> "You've seen everything you need: build, strengthen, survive the waves, protect the Hollow, and grow stronger after every run. From here on, it becomes real.\nI will open the path to a place where the defense is no longer a lesson. May Gaia be with you!";
            case "А ещё скажу тебе один секретик. Когда ты будешь начинать новый матч, советую бегать по локации и искать секретики. Ты можешь найти сундуки с монетами и осколками дупла. Иногда без этих сундуков бывает крайне сложно.\nНу что ж, удачи тебе!" -> "Now you know the essentials: towers, modules, super weapons, traps, chests, and Hollow Shards. From here on, you will defend the Hollow for real.\nWell then, good luck!";
            default -> null;
        };
    }

    private static String translateKnownNameEn(String value) {
        String cleaned = BankDefenseTextSupport.clean(value);
        if (cleaned.isBlank()) {
            return cleaned;
        }

        String translated = translateMapValue(cleaned, TOWER_RU, TOWER_EN);
        if (translated != null) {
            return translated;
        }
        translated = translateMapValue(cleaned, TOWER_SHORT_RU, TOWER_SHORT_EN);
        if (translated != null) {
            return translated;
        }
        translated = translateMapValue(cleaned, MODULE_RU, MODULE_EN);
        if (translated != null) {
            return translated;
        }
        translated = translateMapValue(cleaned, MODULE_SHORT_RU, MODULE_SHORT_EN);
        if (translated != null) {
            return translated;
        }
        translated = translateMapValue(cleaned, ENEMY_RU, ENEMY_EN);
        if (translated != null) {
            return translated;
        }
        translated = translateMapValue(cleaned, CONTRACT_RU, CONTRACT_EN);
        if (translated != null) {
            return translated;
        }
        translated = translateMapValue(cleaned, DIFFICULTY_RU, DIFFICULTY_EN);
        if (translated != null) {
            return translated;
        }
        translated = translateMapValue(cleaned, PROGRESSION_RU, PROGRESSION_EN);
        if (translated != null) {
            return translated;
        }
        return switch (cleaned) {
            case "Оператор" -> "Operator";
            case "Хранитель улучшений" -> "Upgrade Keeper";
            case "Выбор режима" -> "Mode Select";
            case "Волшебный Квибек" -> "Wizard Kweebec";
            case "Переход в Duo Режим" -> "Switch to Duo Mode";
            default -> cleaned;
        };
    }

    private static String translateMapValue(String cleaned, Map<String, String> ruValues, Map<String, String> enValues) {
        for (Map.Entry<String, String> entry : ruValues.entrySet()) {
            if (Objects.equals(BankDefenseTextSupport.clean(entry.getValue()), cleaned)) {
                return enValues.getOrDefault(entry.getKey(), cleaned);
            }
        }
        return null;
    }

    private static String localizedById(
        Lang lang,
        String id,
        String fallback,
        Map<String, String> ruValues,
        Map<String, String> enValues
    ) {
        if (id == null || id.isBlank()) {
            return BankDefenseTextSupport.clean(fallback);
        }
        if (lang == Lang.RU) {
            return cleanedOrFallback(ruValues.get(id), fallback);
        }
        return cleanedOrFallback(enValues.get(id), fallback);
    }

    private static String cleanedOrFallback(String value, String fallback) {
        String cleaned = BankDefenseTextSupport.clean(value);
        if (!cleaned.isBlank()) {
            return cleaned;
        }
        return BankDefenseTextSupport.clean(fallback);
    }

    private static String format(String template, Object... args) {
        String value = template == null ? "" : template;
        if (args == null || args.length == 0) {
            return value;
        }
        for (int index = 0; index < args.length; index++) {
            value = value.replace("{" + index + "}", Objects.toString(args[index], ""));
        }
        return value;
    }
}
