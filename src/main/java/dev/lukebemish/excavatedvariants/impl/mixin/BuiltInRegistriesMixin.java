package dev.lukebemish.excavatedvariants.impl.mixin;

import net.minecraft.core.WritableRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BuiltInRegistries.class)
public interface BuiltInRegistriesMixin {

    @Accessor("WRITABLE_REGISTRY")
    static WritableRegistry<WritableRegistry<?>> excavated_variants$getWritableRegistry() {
        throw new IllegalStateException();
    }
}
