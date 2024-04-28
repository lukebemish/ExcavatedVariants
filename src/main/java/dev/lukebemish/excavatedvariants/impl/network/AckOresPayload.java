package dev.lukebemish.excavatedvariants.impl.network;

import dev.lukebemish.excavatedvariants.impl.ExcavatedVariants;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record AckOresPayload() implements CustomPacketPayload {
    public static final StreamCodec<FriendlyByteBuf, AckOresPayload> CODEC = StreamCodec.unit(new AckOresPayload());
    public static final CustomPacketPayload.Type<AckOresPayload> TYPE = new CustomPacketPayload.Type<>(ExcavatedVariants.id("ack_ores"));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
