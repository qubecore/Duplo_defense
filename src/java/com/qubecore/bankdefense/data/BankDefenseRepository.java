package com.qubecore.bankdefense.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hypixel.hytale.server.core.universe.world.World;
import com.qubecore.bankdefense.data.BankDefenseTypes.BrandingConfig;
import com.qubecore.bankdefense.data.BankDefenseTypes.BuildSlotsConfig;
import com.qubecore.bankdefense.data.BankDefenseTypes.ContractCatalog;
import com.qubecore.bankdefense.data.BankDefenseTypes.EnemyCatalog;
import com.qubecore.bankdefense.data.BankDefenseTypes.GameRules;
import com.qubecore.bankdefense.data.BankDefenseTypes.MapConfig;
import com.qubecore.bankdefense.data.BankDefenseTypes.ModuleCatalog;
import com.qubecore.bankdefense.data.BankDefenseTypes.PlayerProgressionState;
import com.qubecore.bankdefense.data.BankDefenseTypes.ProgressionCatalog;
import com.qubecore.bankdefense.data.BankDefenseTypes.TowerCatalog;
import com.qubecore.bankdefense.data.BankDefenseTypes.WaveCatalog;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.Objects;

public final class BankDefenseRepository {
    private static final Gson GSON = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();
    private static final String DEFAULTS_ROOT = "/defaults/";

    private final Path globalConfigRoot;

    public BankDefenseRepository(Path globalConfigRoot) {
        this.globalConfigRoot = Objects.requireNonNull(globalConfigRoot, "globalConfigRoot");
    }

    public synchronized void ensureGlobalDefaults() throws IOException {
        Files.createDirectories(this.globalConfigRoot);
        this.copyDefaultIfMissing("game_rules.json");
        this.copyDefaultIfMissing("towers.json");
        this.copyDefaultIfMissing("enemies.json");
        this.copyDefaultIfMissing("modules.json");
        this.copyDefaultIfMissing("waves.json");
        this.copyDefaultIfMissing("branding.json");
        this.copyDefaultIfMissing("contracts.json");
        this.copyDefaultIfMissing("progression_tree.json");
    }

    public synchronized void ensureWorldFiles(World world) throws IOException {
        Path worldRoot = this.getWorldConfigRoot(world);
        Files.createDirectories(worldRoot);

        Path mapPath = worldRoot.resolve("map.json");
        if (!Files.exists(mapPath)) {
            MapConfig mapConfig = new MapConfig();
            mapConfig.worldName = world.getName();
            this.writeJson(mapPath, mapConfig);
        }

        Path slotsPath = worldRoot.resolve("build_slots.json");
        if (!Files.exists(slotsPath)) {
            this.writeJson(slotsPath, new BuildSlotsConfig());
        }
    }

    public synchronized Snapshot loadSnapshot(World world) throws IOException {
        this.ensureGlobalDefaults();
        this.ensureWorldFiles(world);
        GameRules gameRules = this.readJson(this.globalConfigRoot.resolve("game_rules.json"), GameRules.class, new GameRules());
        TowerCatalog towers = this.readJson(this.globalConfigRoot.resolve("towers.json"), TowerCatalog.class, new TowerCatalog());
        EnemyCatalog enemies = this.readJson(this.globalConfigRoot.resolve("enemies.json"), EnemyCatalog.class, new EnemyCatalog());
        ModuleCatalog modules = this.readJson(this.globalConfigRoot.resolve("modules.json"), ModuleCatalog.class, new ModuleCatalog());
        WaveCatalog waves = this.readJson(this.globalConfigRoot.resolve("waves.json"), WaveCatalog.class, new WaveCatalog());
        BrandingConfig branding = this.readJson(this.globalConfigRoot.resolve("branding.json"), BrandingConfig.class, new BrandingConfig());
        ContractCatalog contracts = this.readJson(this.globalConfigRoot.resolve("contracts.json"), ContractCatalog.class, new ContractCatalog());
        ProgressionCatalog progression = this.readJson(this.globalConfigRoot.resolve("progression_tree.json"), ProgressionCatalog.class, new ProgressionCatalog());
        MapConfig map = this.readJson(this.getWorldConfigRoot(world).resolve("map.json"), MapConfig.class, new MapConfig());
        BuildSlotsConfig buildSlots = this.readJson(this.getWorldConfigRoot(world).resolve("build_slots.json"), BuildSlotsConfig.class, new BuildSlotsConfig());
        if (map.worldName == null || map.worldName.isBlank()) {
            map.worldName = world.getName();
        }
        return new Snapshot(gameRules, towers, enemies, modules, waves, branding, contracts, progression, map, buildSlots);
    }

    public synchronized MapConfig loadMapConfig(World world) throws IOException {
        this.ensureWorldFiles(world);
        MapConfig mapConfig = this.readJson(this.getWorldConfigRoot(world).resolve("map.json"), MapConfig.class, new MapConfig());
        if (mapConfig.worldName == null || mapConfig.worldName.isBlank()) {
            mapConfig.worldName = world.getName();
        }
        return mapConfig;
    }

    public synchronized BuildSlotsConfig loadBuildSlotsConfig(World world) throws IOException {
        this.ensureWorldFiles(world);
        return this.readJson(this.getWorldConfigRoot(world).resolve("build_slots.json"), BuildSlotsConfig.class, new BuildSlotsConfig());
    }

    public synchronized void saveMapConfig(World world, MapConfig mapConfig) throws IOException {
        this.ensureWorldFiles(world);
        mapConfig.worldName = world.getName();
        this.writeJson(this.getWorldConfigRoot(world).resolve("map.json"), mapConfig);
    }

    public synchronized void saveBuildSlotsConfig(World world, BuildSlotsConfig buildSlotsConfig) throws IOException {
        this.ensureWorldFiles(world);
        this.writeJson(this.getWorldConfigRoot(world).resolve("build_slots.json"), buildSlotsConfig);
    }

    public synchronized PlayerProgressionState loadPlayerProgression(String playerUuid) throws IOException {
        this.ensureGlobalDefaults();
        Files.createDirectories(this.getProfilesRoot());
        Path path = this.getProfilesRoot().resolve(playerUuid + ".json");
        PlayerProgressionState fallback = new PlayerProgressionState();
        fallback.playerUuid = playerUuid;
        if (!Files.exists(path)) {
            this.writeJson(path, fallback);
            return fallback;
        }
        PlayerProgressionState state = this.readJson(path, PlayerProgressionState.class, fallback);
        if (state.playerUuid == null || state.playerUuid.isBlank()) {
            state.playerUuid = playerUuid;
        }
        if (state.activeContractId == null || state.activeContractId.isBlank()) {
            state.activeContractId = "none";
        }
        return state;
    }

    public synchronized void savePlayerProgression(PlayerProgressionState state) throws IOException {
        this.ensureGlobalDefaults();
        if (state == null || state.playerUuid == null || state.playerUuid.isBlank()) {
            throw new IOException("Player progression state is missing playerUuid.");
        }
        Files.createDirectories(this.getProfilesRoot());
        this.writeJson(this.getProfilesRoot().resolve(state.playerUuid + ".json"), state);
    }

    public synchronized ContractCatalog loadContractCatalog() throws IOException {
        this.ensureGlobalDefaults();
        return this.readJson(this.globalConfigRoot.resolve("contracts.json"), ContractCatalog.class, new ContractCatalog());
    }

    public synchronized ProgressionCatalog loadProgressionCatalog() throws IOException {
        this.ensureGlobalDefaults();
        return this.readJson(this.globalConfigRoot.resolve("progression_tree.json"), ProgressionCatalog.class, new ProgressionCatalog());
    }

    public Path getGlobalConfigRoot() {
        return this.globalConfigRoot;
    }

    public Path getWorldConfigRoot(World world) {
        return world.getSavePath().resolve("resources").resolve("bank_defense");
    }

    public Path getProfilesRoot() {
        return this.globalConfigRoot.resolve("profiles");
    }

    private void copyDefaultIfMissing(String fileName) throws IOException {
        Path targetPath = this.globalConfigRoot.resolve(fileName);
        if (Files.exists(targetPath)) {
            return;
        }
        try (InputStream in = BankDefenseRepository.class.getResourceAsStream(DEFAULTS_ROOT + fileName)) {
            if (in == null) {
                throw new IOException("Missing embedded default file: " + fileName);
            }
            Files.copy(in, targetPath, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    private <T> T readJson(Path path, Class<T> type, T fallback) throws IOException {
        String raw = Files.readString(path, StandardCharsets.UTF_8);
        T value = GSON.fromJson(raw, type);
        return value == null ? fallback : value;
    }

    private void writeJson(Path path, Object value) throws IOException {
        Path parent = path.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
        Files.writeString(
            path,
            GSON.toJson(value),
            StandardCharsets.UTF_8,
            StandardOpenOption.CREATE,
            StandardOpenOption.TRUNCATE_EXISTING,
            StandardOpenOption.WRITE
        );
    }

    public static final class Snapshot {
        public final GameRules gameRules;
        public final TowerCatalog towers;
        public final EnemyCatalog enemies;
        public final ModuleCatalog modules;
        public final WaveCatalog waves;
        public final BrandingConfig branding;
        public final ContractCatalog contracts;
        public final ProgressionCatalog progression;
        public final MapConfig map;
        public final BuildSlotsConfig buildSlots;

        public Snapshot(
            GameRules gameRules,
            TowerCatalog towers,
            EnemyCatalog enemies,
            ModuleCatalog modules,
            WaveCatalog waves,
            BrandingConfig branding,
            ContractCatalog contracts,
            ProgressionCatalog progression,
            MapConfig map,
            BuildSlotsConfig buildSlots
        ) {
            this.gameRules = gameRules;
            this.towers = towers;
            this.enemies = enemies;
            this.modules = modules;
            this.waves = waves;
            this.branding = branding;
            this.contracts = contracts;
            this.progression = progression;
            this.map = map;
            this.buildSlots = buildSlots;
        }
    }
}
