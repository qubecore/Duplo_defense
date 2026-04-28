package com.qubecore.bankdefense.ui;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;

public final class BankDefenseSuperSlotEventDataPayload {
    public static final BuilderCodec<BankDefenseSuperSlotEventDataPayload> CODEC =
        BuilderCodec.builder(BankDefenseSuperSlotEventDataPayload.class, BankDefenseSuperSlotEventDataPayload::new)
            .append(new KeyedCodec<>("Action", Codec.STRING), (entry, value) -> entry.action = value, entry -> entry.action)
            .add()
            .build();

    public String action;
}
