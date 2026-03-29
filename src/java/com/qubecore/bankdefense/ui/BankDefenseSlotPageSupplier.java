package com.qubecore.bankdefense.ui;

import com.hypixel.hytale.component.ComponentAccessor;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.protocol.BlockPosition;
import com.hypixel.hytale.server.core.entity.InteractionContext;
import com.hypixel.hytale.server.core.entity.entities.player.pages.CustomUIPage;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.server.OpenCustomUIInteraction;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.qubecore.bankdefense.data.BankDefenseTypes.Vec3i;
import com.qubecore.bankdefense.runtime.BankDefenseRuntime;
import com.qubecore.bankdefense.runtime.BankDefenseRuntime.InteractionTarget;
import com.qubecore.bankdefense.runtime.BankDefenseRuntime.InteractionTargetKind;
import com.qubecore.bankdefense.runtime.BankDefenseRuntime.SlotUiState;
import java.io.IOException;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class BankDefenseSlotPageSupplier implements OpenCustomUIInteraction.CustomPageSupplier {
    public static final BuilderCodec<BankDefenseSlotPageSupplier> CODEC =
        BuilderCodec.builder(BankDefenseSlotPageSupplier.class, BankDefenseSlotPageSupplier::new).build();

    private static volatile BankDefenseRuntime runtime;

    public static void bindRuntime(BankDefenseRuntime value) {
        runtime = value;
    }

    public BankDefenseSlotPageSupplier() {
    }

    @Override
    @Nullable
    public CustomUIPage tryCreate(
        @Nonnull Ref<EntityStore> ref,
        @Nonnull ComponentAccessor<EntityStore> componentAccessor,
        @Nonnull PlayerRef playerRef,
        @Nonnull InteractionContext context
    ) {
        BankDefenseRuntime currentRuntime = runtime;
        if (currentRuntime == null) {
            return null;
        }

        BlockPosition targetBlock = context.getTargetBlock();
        if (targetBlock == null) {
            return null;
        }

        World world = ref.getStore().getExternalData().getWorld();
        try {
            InteractionTarget target = currentRuntime.resolveInteractionTarget(
                world,
                null,
                new Vec3i(targetBlock.x, targetBlock.y, targetBlock.z)
            );
            if (target.kind != InteractionTargetKind.BuildSlot || target.slotId == null || target.slotId.isBlank()) {
                return null;
            }
            if (currentRuntime.blockSlotUiIfNeeded(world, playerRef, target.slotId)) {
                return null;
            }
            SlotUiState state = currentRuntime.getSlotUiState(world, playerRef, target.slotId);
            if (state != null && state.superSlot) {
                return new BankDefenseSuperSlotPage(playerRef, currentRuntime, target.slotId);
            }
            return new BankDefenseSlotPage(playerRef, currentRuntime, target.slotId);
        } catch (IOException e) {
            return null;
        }
    }
}
