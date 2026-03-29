package com.qubecore.bankdefense;

import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.dependency.Dependency;
import com.hypixel.hytale.component.dependency.RootDependency;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.EntityEventSystem;
import com.hypixel.hytale.protocol.GameMode;
import com.hypixel.hytale.protocol.MovementSettings;
import com.hypixel.hytale.server.core.HytaleServer;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.movement.MovementManager;
import com.hypixel.hytale.server.core.event.events.ecs.BreakBlockEvent;
import com.hypixel.hytale.server.core.event.events.ecs.DamageBlockEvent;
import com.hypixel.hytale.server.core.event.events.ecs.PlaceBlockEvent;
import com.hypixel.hytale.server.core.event.events.player.PlayerInteractEvent;
import com.hypixel.hytale.server.core.event.events.player.PlayerMouseButtonEvent;
import com.hypixel.hytale.server.core.event.events.player.PlayerMouseMotionEvent;
import com.hypixel.hytale.server.core.event.events.player.PlayerReadyEvent;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.server.OpenCustomUIInteraction;
import com.hypixel.hytale.server.core.permissions.HytalePermissions;
import com.hypixel.hytale.server.core.permissions.PermissionsModule;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.events.StartWorldEvent;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.qubecore.bankdefense.command.BankDefenseCommand;
import com.qubecore.bankdefense.data.BankDefenseRepository;
import com.qubecore.bankdefense.input.BankDefenseToolController;
import com.qubecore.bankdefense.runtime.BankDefenseRuntime;
import com.qubecore.bankdefense.ui.BankDefenseChestPageSupplier;
import com.qubecore.bankdefense.ui.BankDefenseDuoInfoPageSupplier;
import com.qubecore.bankdefense.ui.BankDefenseDuoTeamSelectPageSupplier;
import com.qubecore.bankdefense.ui.BankDefenseMatchControlPageSupplier;
import com.qubecore.bankdefense.ui.BankDefenseModeSelectPageSupplier;
import com.qubecore.bankdefense.ui.BankDefenseProgressionPageSupplier;
import com.qubecore.bankdefense.ui.BankDefenseSlotPageSupplier;
import com.qubecore.bankdefense.ui.BankDefenseStatsPageSupplier;
import com.qubecore.bankdefense.ui.BankDefenseTutorialDialogPageSupplier;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public final class BankDefensePlugin extends JavaPlugin {
    private static final long MATCH_TICK_MS = 50L;
    private static final Set<String> PROTECTED_WORLD_KEYS = ConcurrentHashMap.newKeySet();
    private static volatile BankDefensePlugin instance;
    private static volatile boolean noBuildEnabled = true;
    private static volatile boolean builderToolsEnabled = false;

    private BankDefenseRepository repository;
    private BankDefenseRuntime runtime;
    private BankDefenseToolController toolController;
    private ScheduledFuture<?> matchTicker;

    public BankDefensePlugin(JavaPluginInit init) {
        super(init);
        instance = this;
    }

    public static BankDefensePlugin getInstance() {
        return instance;
    }

    public BankDefenseRuntime getRuntime() {
        return this.runtime;
    }

    @Override
    @SuppressWarnings("deprecation")
    protected void setup() {
        try {
            Path configRoot = this.getDataDirectory().resolve("config");
            this.repository = new BankDefenseRepository(configRoot);
            this.repository.ensureGlobalDefaults();
            this.runtime = new BankDefenseRuntime(this.repository);
            this.toolController = new BankDefenseToolController(this.runtime);
        } catch (IOException e) {
            this.getLogger().at(Level.SEVERE).withCause(e).log("Failed to initialize QubeCore Bank Defense content.");
            return;
        }

        BankDefenseSlotPageSupplier.bindRuntime(this.runtime);
        BankDefenseChestPageSupplier.bindRuntime(this.runtime);
        BankDefenseMatchControlPageSupplier.bindRuntime(this.runtime);
        BankDefenseModeSelectPageSupplier.bindRuntime(this.runtime);
        BankDefenseDuoTeamSelectPageSupplier.bindRuntime(this.runtime);
        BankDefenseDuoInfoPageSupplier.bindRuntime(this.runtime);
        BankDefenseProgressionPageSupplier.bindRuntime(this.runtime);
        BankDefenseStatsPageSupplier.bindRuntime(this.runtime);
        BankDefenseTutorialDialogPageSupplier.bindRuntime(this.runtime);

        OpenCustomUIInteraction.registerCustomPageSupplier(
            this,
            BankDefenseSlotPageSupplier.class,
            "BankDefenseSlot",
            new BankDefenseSlotPageSupplier()
        );
        OpenCustomUIInteraction.registerCustomPageSupplier(
            this,
            BankDefenseChestPageSupplier.class,
            "BankDefenseChestClaim",
            new BankDefenseChestPageSupplier()
        );
        OpenCustomUIInteraction.registerCustomPageSupplier(
            this,
            BankDefenseMatchControlPageSupplier.class,
            "BankDefenseMatchControl",
            new BankDefenseMatchControlPageSupplier()
        );
        OpenCustomUIInteraction.registerCustomPageSupplier(
            this,
            BankDefenseModeSelectPageSupplier.class,
            "BankDefenseModeSelect",
            new BankDefenseModeSelectPageSupplier()
        );
        OpenCustomUIInteraction.registerCustomPageSupplier(
            this,
            BankDefenseDuoTeamSelectPageSupplier.class,
            "BankDefenseDuoTeamSelect",
            new BankDefenseDuoTeamSelectPageSupplier()
        );
        OpenCustomUIInteraction.registerCustomPageSupplier(
            this,
            BankDefenseProgressionPageSupplier.class,
            "BankDefenseProgression",
            new BankDefenseProgressionPageSupplier()
        );
        OpenCustomUIInteraction.registerCustomPageSupplier(
            this,
            BankDefenseTutorialDialogPageSupplier.class,
            "BankDefenseTutorialDialog",
            new BankDefenseTutorialDialogPageSupplier()
        );
        OpenCustomUIInteraction.registerCustomPageSupplier(
            this,
            BankDefenseDuoInfoPageSupplier.class,
            "BankDefenseDuoInfo",
            new BankDefenseDuoInfoPageSupplier()
        );
        OpenCustomUIInteraction.registerCustomPageSupplier(
            this,
            BankDefenseStatsPageSupplier.class,
            "BankDefenseStats",
            new BankDefenseStatsPageSupplier()
        );

        this.getCommandRegistry().registerCommand(new BankDefenseCommand(this.repository, this.runtime));
        this.getEventRegistry().registerGlobal(StartWorldEvent.class, this::onStartWorld);
        this.getEventRegistry().registerGlobal(PlayerReadyEvent.class, this::onPlayerReady);
        this.getEventRegistry().registerGlobal(PlayerMouseButtonEvent.class, this::onPlayerMouseButton);
        this.getEventRegistry().registerGlobal(PlayerMouseMotionEvent.class, this.toolController::onPlayerMouseMotion);
        this.getEventRegistry().registerGlobal(PlayerInteractEvent.class, this::onPlayerInteract);

        this.getEntityStoreRegistry().registerSystem(new NoBuildPlaceSystem());
        this.getEntityStoreRegistry().registerSystem(new NoBuildBreakSystem());
        this.getEntityStoreRegistry().registerSystem(new NoBuildDamageSystem());

        this.startMatchTicker();
        this.getLogger().at(Level.INFO).log("QubeCore Bank Defense runtime initialized.");
    }

    @Override
    protected void shutdown() {
        if (this.matchTicker != null) {
            this.matchTicker.cancel(false);
            this.matchTicker = null;
        }
    }

    private void onStartWorld(StartWorldEvent event) {
        World world = event.getWorld();
        if (world != null) {
            PROTECTED_WORLD_KEYS.add(worldKey(world));
        }
        Runnable setupTask = () -> {
            try {
                this.repository.ensureWorldFiles(world);
                this.clearWorldStreamingOverrides(world);
                this.runtime.resetPresentationState(world);
                this.runtime.refreshVisualization(world);
                this.getLogger().at(Level.INFO).log("Bank Defense world files ready for '%s'.", world.getSavePath());
            } catch (IOException e) {
                this.getLogger().at(Level.SEVERE).withCause(e).log("Failed to prepare Bank Defense files for '%s'.", world.getSavePath());
            }
        };
        if (world.isInThread()) {
            setupTask.run();
            return;
        }
        world.execute(setupTask);
    }

    private void onPlayerReady(PlayerReadyEvent event) {
        this.toolController.onPlayerReady(event);
        Player player = event.getPlayer();
        World world = player == null ? null : player.getWorld();
        if (world != null) {
            Player.setGameMode(event.getPlayerRef(), GameMode.Adventure, world.getEntityStore().getStore());
            this.applyDefaultMobility(event, player, world);
        }
        if (player != null) {
            applyEditorPermissions(player);
        }
        if (this.runtime == null || world == null) {
            return;
        }
        if (player != null && player.getPlayerRef() != null) {
            this.runtime.playWorldEnterSound(world, player.getPlayerRef());
        }
        Runnable refreshTask = () -> {
            try {
                if (this.runtime.isVisualizationEnabled(world) && !this.runtime.hasCombatVisualState(world)) {
                    this.runtime.refreshVisualization(world);
                }
            } catch (IOException e) {
                this.getLogger().at(Level.WARNING).withCause(e).log(
                    "Failed to refresh Bank Defense visuals for reconnect in world '%s'.",
                    world.getName()
                );
            }
        };
        if (world.isInThread()) {
            refreshTask.run();
            return;
        }
        world.execute(refreshTask);
    }

    private void applyDefaultMobility(PlayerReadyEvent event, Player player, World world) {
        if (event == null || player == null || world == null) {
            return;
        }
        Store<EntityStore> store = world.getEntityStore().getStore();
        MovementManager movementManager = store.getComponent(event.getPlayerRef(), MovementManager.getComponentType());
        if (movementManager == null || movementManager.getSettings() == null || player.getPlayerConnection() == null) {
            return;
        }
        MovementSettings settings = movementManager.getSettings();
        MovementSettings defaults = movementManager.getDefaultSettings();
        MovementSettings base = defaults == null ? new MovementSettings(settings) : defaults;
        float boostedBaseSpeed = Math.max(0.01f, base.baseSpeed * 5.0f);
        settings.canFly = true;
        settings.baseSpeed = boostedBaseSpeed;
        settings.jumpForce = Math.max(base.jumpForce, base.jumpForce * 2.2f);
        settings.swimJumpForce = Math.max(base.swimJumpForce, base.swimJumpForce * 2.2f);
        settings.fallJumpForce = Math.max(base.fallJumpForce, base.fallJumpForce * 2.2f);
        settings.horizontalFlySpeed = boostedBaseSpeed;
        settings.verticalFlySpeed = Math.max(boostedBaseSpeed, base.verticalFlySpeed * 1.8f);
        settings.maxSpeedMultiplier = Math.max(settings.maxSpeedMultiplier, 5.0f);
        movementManager.update(player.getPlayerConnection());
    }

    private void clearWorldStreamingOverrides(World world) throws IOException {
        if (world == null || this.repository == null) {
            return;
        }
        boolean changed = false;
        if (!world.getWorldConfig().canUnloadChunks()) {
            world.getWorldConfig().setCanUnloadChunks(true);
            changed = true;
        }
        if (world.getWorldConfig().getChunkConfig().getKeepLoadedRegion() != null) {
            world.getWorldConfig().getChunkConfig().setKeepLoadedRegion(null);
            changed = true;
        }
        if (!changed) {
            return;
        }
        world.getWorldConfig().markChanged();
        Universe.get().getWorldConfigProvider().save(world.getSavePath(), world.getWorldConfig(), world).join();
        this.getLogger().at(Level.INFO).log(
            "Cleared Duplo TD streaming overrides for '%s'.",
            world.getName()
        );
    }

    private void startMatchTicker() {
        this.matchTicker = HytaleServer.SCHEDULED_EXECUTOR.scheduleAtFixedRate(() -> {
            if (this.runtime == null || this.toolController == null) {
                return;
            }
            Universe.get().getWorlds().values().forEach(world -> {
                try {
                    world.execute(() -> {
                        if (this.runtime.hasActiveMatch(world)) {
                            this.runtime.tickWorld(world, MATCH_TICK_MS / 1000.0);
                        }
                        if (this.runtime.isVisualizationEnabled(world)) {
                            this.runtime.tickVisualization(world, MATCH_TICK_MS / 1000.0);
                        }
                    });
                } catch (Throwable t) {
                    this.getLogger().at(Level.WARNING).withCause(t).log("Failed to tick Bank Defense match in world '%s'.", world.getName());
                }
            });
        }, MATCH_TICK_MS, MATCH_TICK_MS, TimeUnit.MILLISECONDS);
    }

    private void onPlayerMouseButton(PlayerMouseButtonEvent event) {
        if (event != null) {
            this.toolController.onPlayerMouseButton(event);
        }
    }

    private void onPlayerInteract(PlayerInteractEvent event) {
        if (event != null) {
            this.toolController.onPlayerInteract(event);
        }
    }

    public static void applyEditorPermissions(Player player) {
        PermissionsModule permissions = PermissionsModule.get();
        if (permissions == null || player == null) {
            return;
        }
        if (builderToolsEnabled) {
            permissions.addUserPermission(player.getUuid(), Set.of(
                HytalePermissions.BUILDER_TOOLS_EDITOR,
                HytalePermissions.EDITOR_BRUSH_USE,
                HytalePermissions.EDITOR_BRUSH_CONFIG,
                HytalePermissions.EDITOR_PREFAB_USE,
                HytalePermissions.EDITOR_PREFAB_MANAGE,
                HytalePermissions.EDITOR_SELECTION_USE,
                HytalePermissions.EDITOR_SELECTION_CLIPBOARD,
                HytalePermissions.EDITOR_SELECTION_MODIFY
            ));
            return;
        }
        permissions.addUserPermission(player.getUuid(), Set.of(
            "-" + HytalePermissions.BUILDER_TOOLS_EDITOR,
            "-" + HytalePermissions.EDITOR_BRUSH_USE,
            "-" + HytalePermissions.EDITOR_BRUSH_CONFIG,
            "-" + HytalePermissions.EDITOR_PREFAB_USE,
            "-" + HytalePermissions.EDITOR_PREFAB_MANAGE,
            "-" + HytalePermissions.EDITOR_SELECTION_USE,
            "-" + HytalePermissions.EDITOR_SELECTION_CLIPBOARD,
            "-" + HytalePermissions.EDITOR_SELECTION_MODIFY
        ));
    }

    private static String worldKey(World world) {
        return world.getSavePath().normalize().toString();
    }

    public static BankDefensePlugin get() {
        return instance;
    }

    private static boolean isProtectedWorld(World world) {
        return noBuildEnabled && world != null && PROTECTED_WORLD_KEYS.contains(worldKey(world));
    }

    public static boolean isNoBuildEnabled() {
        return noBuildEnabled;
    }

    public static void setNoBuildEnabled(boolean enabled) {
        noBuildEnabled = enabled;
    }

    public static boolean isBuilderToolsEnabled() {
        return builderToolsEnabled;
    }

    public static void setBuilderToolsEnabled(boolean enabled) {
        builderToolsEnabled = enabled;
    }

    private abstract static class BaseNoBuildSystem<T extends com.hypixel.hytale.component.system.EcsEvent>
        extends EntityEventSystem<EntityStore, T> {

        protected BaseNoBuildSystem(Class<T> eventType) {
            super(eventType);
        }

        protected boolean shouldBlock(int entityIndex, ArchetypeChunk<EntityStore> chunk, Store<EntityStore> store) {
            Ref playerEntity = chunk.getReferenceTo(entityIndex);
            Player player = (Player) store.getComponent(playerEntity, Player.getComponentType());
            PlayerRef playerRef = (PlayerRef) store.getComponent(playerEntity, PlayerRef.getComponentType());
            if (player == null || playerRef == null) {
                return false;
            }
            EntityStore entityStore = (EntityStore) store.getExternalData();
            return entityStore != null && isProtectedWorld(entityStore.getWorld());
        }

        @Override
        public Query<EntityStore> getQuery() {
            return PlayerRef.getComponentType();
        }

        @Override
        public Set<Dependency<EntityStore>> getDependencies() {
            return Collections.singleton(RootDependency.first());
        }
    }

    private static final class NoBuildPlaceSystem extends BaseNoBuildSystem<PlaceBlockEvent> {
        private NoBuildPlaceSystem() {
            super(PlaceBlockEvent.class);
        }

        @Override
        public void handle(
            int entityIndex,
            ArchetypeChunk<EntityStore> chunk,
            Store<EntityStore> store,
            CommandBuffer<EntityStore> commandBuffer,
            PlaceBlockEvent event
        ) {
            if (event == null || event.isCancelled()) {
                return;
            }
            if (this.shouldBlock(entityIndex, chunk, store)) {
                event.setCancelled(true);
            }
        }
    }

    private static final class NoBuildBreakSystem extends BaseNoBuildSystem<BreakBlockEvent> {
        private NoBuildBreakSystem() {
            super(BreakBlockEvent.class);
        }

        @Override
        public void handle(
            int entityIndex,
            ArchetypeChunk<EntityStore> chunk,
            Store<EntityStore> store,
            CommandBuffer<EntityStore> commandBuffer,
            BreakBlockEvent event
        ) {
            if (event == null || event.isCancelled()) {
                return;
            }
            if (this.shouldBlock(entityIndex, chunk, store)) {
                event.setCancelled(true);
            }
        }
    }

    private static final class NoBuildDamageSystem extends BaseNoBuildSystem<DamageBlockEvent> {
        private NoBuildDamageSystem() {
            super(DamageBlockEvent.class);
        }

        @Override
        public void handle(
            int entityIndex,
            ArchetypeChunk<EntityStore> chunk,
            Store<EntityStore> store,
            CommandBuffer<EntityStore> commandBuffer,
            DamageBlockEvent event
        ) {
            if (event == null || event.isCancelled()) {
                return;
            }
            if (this.shouldBlock(entityIndex, chunk, store)) {
                event.setDamage(0.0f);
                event.setCancelled(true);
            }
        }
    }
}
