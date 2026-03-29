package com.qubecore.bankdefense.ui;

import com.hypixel.hytale.component.ComponentAccessor;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.protocol.BlockPosition;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.InteractionContext;
import com.hypixel.hytale.server.core.entity.entities.player.pages.CustomUIPage;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.server.OpenCustomUIInteraction;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.qubecore.bankdefense.data.BankDefenseTypes.Vec3i;
import com.qubecore.bankdefense.runtime.BankDefenseRuntime;
import com.qubecore.bankdefense.runtime.BankDefenseRuntime.ActionResult;
import com.qubecore.bankdefense.runtime.support.BankDefenseLocalization;
import java.io.IOException;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class BankDefenseChestPageSupplier implements OpenCustomUIInteraction.CustomPageSupplier {
    private static final String MONEY_CHEST_BLOCK = "Utility_BankDefense_MoneyChest";
    private static final String CORE_CHEST_BLOCK = "Utility_BankDefense_CoreChest";
    public static final BuilderCodec<BankDefenseChestPageSupplier> CODEC =
        BuilderCodec.builder(BankDefenseChestPageSupplier.class, BankDefenseChestPageSupplier::new).build();

    private static volatile BankDefenseRuntime runtime;

    public static void bindRuntime(BankDefenseRuntime value) {
        runtime = value;
    }

    public BankDefenseChestPageSupplier() {
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
            Vec3i target = new Vec3i(targetBlock.x, targetBlock.y, targetBlock.z);
            int moneyChestBlockId = BlockType.getBlockIdOrUnknown(MONEY_CHEST_BLOCK, "Failed to find block '%s'.", MONEY_CHEST_BLOCK);
            int coreChestBlockId = BlockType.getBlockIdOrUnknown(CORE_CHEST_BLOCK, "Failed to find block '%s'.", CORE_CHEST_BLOCK);
            WorldChunk chunk = world.getChunkIfInMemory(com.hypixel.hytale.math.util.ChunkUtil.indexChunkFromBlock(target.x, target.z));
            if (chunk == null) {
                return null;
            }
            int blockId = chunk.getBlock(target.x, target.y, target.z);
            ActionResult result = null;
            if (blockId == moneyChestBlockId) {
                result = currentRuntime.claimMoneyChest(world, target);
            } else if (blockId == coreChestBlockId) {
                result = currentRuntime.claimCoreChest(world, target);
            }
            if (result != null && result.message != null && !result.message.isBlank()) {
                BankDefenseLocalization.sendWorldMessage(world, result.message);
            }
        } catch (IOException ignored) {
        }
        return null;
    }
}
