package com.qubecore.bankdefense.input;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.pages.CustomUIPage;
import com.hypixel.hytale.server.core.event.events.player.PlayerInteractEvent;
import com.hypixel.hytale.server.core.event.events.player.PlayerMouseButtonEvent;
import com.hypixel.hytale.server.core.event.events.player.PlayerMouseMotionEvent;
import com.hypixel.hytale.server.core.event.events.player.PlayerReadyEvent;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.qubecore.bankdefense.data.BankDefenseTypes.Vec3i;
import com.qubecore.bankdefense.runtime.BankDefenseRuntime;
import com.qubecore.bankdefense.runtime.BankDefenseRuntime.ActionResult;
import com.qubecore.bankdefense.runtime.BankDefenseRuntime.InteractionTarget;
import com.qubecore.bankdefense.runtime.BankDefenseRuntime.InteractionTargetKind;
import com.qubecore.bankdefense.runtime.support.BankDefenseLocalization;
import com.qubecore.bankdefense.ui.BankDefenseControlPage;
import java.io.IOException;

public final class BankDefenseToolController {
    private final BankDefenseRuntime runtime;

    public BankDefenseToolController(BankDefenseRuntime runtime) {
        this.runtime = runtime;
    }

    public void onPlayerReady(PlayerReadyEvent event) {
        Player player = event.getPlayer();
        this.resetHoverPrompt(event == null ? null : event.getPlayerRef(), player);
    }

    public void onPlayerMouseButton(PlayerMouseButtonEvent event) {
    }

    public void onPlayerMouseMotion(PlayerMouseMotionEvent event) {
        if (event == null) {
            return;
        }
        Player player = event.getPlayer();
        World world = player == null ? null : player.getWorld();
        if (player == null || world == null) {
            return;
        }
        Store<EntityStore> store = world.getEntityStore().getStore();
        PlayerRef playerRef = store.getComponent(event.getPlayerRef(), PlayerRef.getComponentType());
        if (playerRef == null) {
            return;
        }
        Ref<EntityStore> targetRef = null;
        if (event.getTargetEntity() != null) {
            targetRef = event.getTargetEntity().getReference();
        }
        this.runtime.updateHoveredInteractionPrompt(world, playerRef, targetRef, this.toVec3i(event.getTargetBlock()));
    }

    @SuppressWarnings("deprecation")
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event == null || event.getActionType() != InteractionType.Use) {
            return;
        }
        Ref<EntityStore> targetRef = event.getTargetRef();
        if (targetRef == null || !targetRef.isValid()) {
            return;
        }
        Player player = event.getPlayer();
        World world = player == null ? null : player.getWorld();
        if (player == null || world == null) {
            return;
        }
        Store<EntityStore> store = world.getEntityStore().getStore();
        PlayerRef playerRef = store.getComponent(event.getPlayerRef(), PlayerRef.getComponentType());
        if (playerRef == null) {
            return;
        }
        try {
            InteractionTarget target = this.runtime.resolveInteractionTarget(world, targetRef, this.toVec3i(event));
            if (target == null
                || target.kind == InteractionTargetKind.None
                || target.kind == InteractionTargetKind.BuildSlot
                || target.kind == InteractionTargetKind.ControlOperator
                || target.kind == InteractionTargetKind.VendorOperator
                || target.kind == InteractionTargetKind.ModeSelectorOperator
                || target.kind == InteractionTargetKind.MoneyChest
                || target.kind == InteractionTargetKind.CoreChest) {
                return;
            }
            event.setCancelled(true);
            switch (target.kind) {
                case MenuConsole -> this.openPage(player, event.getPlayerRef(), store, new BankDefenseControlPage(playerRef, this.runtime));
                case StartConsole -> this.sendResult(player, this.runtime.startNextWave(world));
                default -> {
                }
            }
        } catch (IOException e) {
            String error = BankDefenseLocalization.choose(playerRef, "Не удалось открыть взаимодействие: ", "Failed to open the interaction: ") + e.getMessage();
            player.sendMessage(Message.raw(error));
            this.runtime.playUiErrorSound(world, playerRef);
        }
    }

    private void resetHoverPrompt(Ref<EntityStore> playerEntityRef, Player player) {
        World world = player == null ? null : player.getWorld();
        if (playerEntityRef == null || player == null || world == null) {
            return;
        }
        Store<EntityStore> store = world.getEntityStore().getStore();
        PlayerRef playerRef = store.getComponent(playerEntityRef, PlayerRef.getComponentType());
        if (playerRef != null) {
            this.runtime.updateHoveredInteractionPrompt(world, playerRef, null, null);
        }
    }

    @SuppressWarnings("deprecation")
    private Vec3i toVec3i(PlayerInteractEvent event) {
        var targetBlock = event.getTargetBlock();
        return this.toVec3i(targetBlock);
    }

    private Vec3i toVec3i(com.hypixel.hytale.math.vector.Vector3i targetBlock) {
        if (targetBlock == null) {
            return null;
        }
        return new Vec3i(targetBlock.x, targetBlock.y, targetBlock.z);
    }

    private void openPage(Player player, Ref<EntityStore> playerEntityRef, Store<EntityStore> store, CustomUIPage page) {
        if (player == null || playerEntityRef == null || page == null) {
            return;
        }
        player.getPageManager().openCustomPage(playerEntityRef, store, page);
    }

    private void sendResult(Player player, ActionResult result) {
        if (player == null || result == null || result.message == null || result.message.isBlank()) {
            return;
        }
        World world = player.getWorld();
        PlayerRef playerRef = player.getPlayerRef();
        if (world != null && playerRef != null) {
            this.runtime.playUiActionFeedback(world, playerRef, result);
        }
        player.sendMessage(Message.raw(BankDefenseLocalization.translateFreeform(player.getPlayerRef(), result.message)));
    }
}
