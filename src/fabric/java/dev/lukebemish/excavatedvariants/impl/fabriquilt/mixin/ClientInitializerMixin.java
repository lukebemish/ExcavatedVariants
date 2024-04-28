package dev.lukebemish.excavatedvariants.impl.fabriquilt.mixin;

import dev.lukebemish.excavatedvariants.impl.fabriquilt.ExcavatedVariantsFabriQuilt;
import net.minecraft.client.Minecraft;
import net.minecraft.client.main.GameConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class ClientInitializerMixin {
    // See where fabric's EntrypointPatch decides to shove fabric entrypoints...
    @Inject(method = "<init>(Lnet/minecraft/client/main/GameConfig;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Options;<init>(Lnet/minecraft/client/Minecraft;Ljava/io/File;)V"))
    private void postFabricInitialized(GameConfig gameConfig, CallbackInfo ci) {
        ExcavatedVariantsFabriQuilt.cleanup();
    }
}
