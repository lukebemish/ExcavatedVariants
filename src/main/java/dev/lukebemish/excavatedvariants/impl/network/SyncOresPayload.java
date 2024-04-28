package dev.lukebemish.excavatedvariants.impl.network;

import dev.lukebemish.excavatedvariants.impl.ExcavatedVariants;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public record SyncOresPayload(Set<String> blocks) implements CustomPacketPayload {
    public static final StreamCodec<FriendlyByteBuf, SyncOresPayload> CODEC = StreamCodec.of(SyncOresPayload::encode, SyncOresPayload::decode);
    public static final CustomPacketPayload.Type<SyncOresPayload> TYPE = new CustomPacketPayload.Type<>(ExcavatedVariants.id("sync_ores"));

    private static SyncOresPayload decode(FriendlyByteBuf buffer) {
        ArrayList<String> blocks = new ArrayList<>();
        int i = buffer.readInt();
        for (int j = 0; j < i; j++) blocks.add(buffer.readUtf());
        return new SyncOresPayload(new HashSet<>(blocks));
    }

    private static void encode(FriendlyByteBuf buffer, SyncOresPayload payload) {
        buffer.writeInt(payload.blocks.size());
        payload.blocks.forEach(buffer::writeUtf);
    }

    public void consumeMessage(Consumer<String> disconnecter) {
        ExcavatedVariants.setupMap();
        Set<String> knownBlocks = ExcavatedVariants.COMPLETE_VARIANTS.stream().map(v -> v.fullId).collect(Collectors.toSet());
        var serverOnly = this.blocks.stream().filter(b -> !knownBlocks.contains(b)).collect(Collectors.toSet());
        var clientOnly = knownBlocks.stream().filter(b -> !this.blocks.contains(b)).collect(Collectors.toSet());

        if (clientOnly.isEmpty() && serverOnly.isEmpty()) {
            return;
        }
        String disconnect = "Connection closed - mismatched ore variant list";
        if (!clientOnly.isEmpty()) {
            String clientOnlyStr = String.join("\n    ", clientOnly.stream().toList());
            ExcavatedVariants.LOGGER.error("Client contains ore variants not present on server:\n    {}", clientOnlyStr);
            disconnect += "\nSee log for details";
        }
        if (!serverOnly.isEmpty()) {
            String serverOnlyStr = String.join("\n    ", serverOnly.stream().toList());
            ExcavatedVariants.LOGGER.error("Server contains ore variants not present on client:\n    {}", serverOnlyStr);
            disconnect += "\nSee log for details";
        }
        disconnecter.accept(disconnect);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
