package dev.lukebemish.excavatedvariants.impl.mixin;

import dev.lukebemish.excavatedvariants.impl.BlockAddedCallback;
import net.minecraft.core.Holder;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.RegistrationInfo;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MappedRegistry.class)
public abstract class MappedRegistryMixin<V> implements Registry<V> {
    @Inject(
            method = "register(Lnet/minecraft/resources/ResourceKey;Ljava/lang/Object;Lnet/minecraft/core/RegistrationInfo;)Lnet/minecraft/core/Holder$Reference;",
            at = @At("RETURN")
    )
    private void excavated_variants$onRegistry(ResourceKey<V> key, V entry, RegistrationInfo registrationInfo, CallbackInfoReturnable<Holder.Reference<V>> cir) {
        //noinspection RedundantCast,rawtypes
        if (this.key() == (ResourceKey) Registries.BLOCK) {
            //noinspection unchecked
            BlockAddedCallback.onRegister((Block) entry, (ResourceKey<Block>) key);
        }
    }
}
