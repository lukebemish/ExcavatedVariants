package dev.lukebemish.excavatedvariants.impl.fabriquilt.mixin;

import dev.lukebemish.excavatedvariants.impl.fabriquilt.ExcavatedVariantsFabriQuilt;
import net.minecraft.server.Main;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Main.class)
public class DedicatedServerInitializerMixin {
    // See where fabric's EntrypointPatch decides to shove fabric entrypoints...
    @Inject(method = "main([Ljava/lang/String;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/dedicated/DedicatedServerSettings;<init>(Ljava/nio/file/Path;)V"))
    private static void postFabricInitialized(String[] args, CallbackInfo ci) {
        ExcavatedVariantsFabriQuilt.cleanup();
    }
}
