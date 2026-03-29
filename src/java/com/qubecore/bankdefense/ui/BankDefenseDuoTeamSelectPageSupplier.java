package com.qubecore.bankdefense.ui;

import com.hypixel.hytale.component.ComponentAccessor;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.server.core.entity.InteractionContext;
import com.hypixel.hytale.server.core.entity.entities.player.pages.CustomUIPage;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.server.OpenCustomUIInteraction;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.qubecore.bankdefense.runtime.BankDefenseRuntime;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class BankDefenseDuoTeamSelectPageSupplier implements OpenCustomUIInteraction.CustomPageSupplier {
    public static final BuilderCodec<BankDefenseDuoTeamSelectPageSupplier> CODEC =
        BuilderCodec.builder(BankDefenseDuoTeamSelectPageSupplier.class, BankDefenseDuoTeamSelectPageSupplier::new).build();

    private static volatile BankDefenseRuntime runtime;

    public static void bindRuntime(BankDefenseRuntime value) {
        runtime = value;
    }

    public BankDefenseDuoTeamSelectPageSupplier() {
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
        return new BankDefenseDuoTeamSelectPage(playerRef, currentRuntime);
    }
}
