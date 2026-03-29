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
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class BankDefenseTutorialDialogPageSupplier implements OpenCustomUIInteraction.CustomPageSupplier {
    public static final BuilderCodec<BankDefenseTutorialDialogPageSupplier> CODEC =
        BuilderCodec.builder(BankDefenseTutorialDialogPageSupplier.class, BankDefenseTutorialDialogPageSupplier::new).build();

    private static volatile BankDefenseRuntime runtime;

    public static void bindRuntime(BankDefenseRuntime value) {
        runtime = value;
    }

    public BankDefenseTutorialDialogPageSupplier() {
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
        String wizardKind = currentRuntime.resolveTutorialWizardKind(
            world,
            new Vec3i(targetBlock.x, targetBlock.y, targetBlock.z)
        );
        if (wizardKind == null || wizardKind.isBlank()) {
            return null;
        }
        return new BankDefenseTutorialDialogPage(playerRef, currentRuntime, wizardKind);
    }
}
