package dev.lukebemish.excavatedvariants.impl.fabriquilt;

import dev.lukebemish.excavatedvariants.impl.network.SyncOresPayload;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientConfigurationNetworking;
import net.minecraft.network.chat.Component;

public class ExcavatedVariantsClientFabriQuilt implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientConfigurationNetworking.registerGlobalReceiver(SyncOresPayload.TYPE, (msg, context) ->
                msg.consumeMessage(string -> context.responseSender().disconnect(Component.literal(string)))
        );
    }
}
