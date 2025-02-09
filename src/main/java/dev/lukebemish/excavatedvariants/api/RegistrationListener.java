package dev.lukebemish.excavatedvariants.api;

import dev.lukebemish.excavatedvariants.api.data.GroundType;
import dev.lukebemish.excavatedvariants.api.data.Ore;
import dev.lukebemish.excavatedvariants.api.data.Stone;
import dev.lukebemish.excavatedvariants.api.data.modifier.Modifier;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.BiConsumer;

/**
 * A listener used to register new ores and stones.
 */
public interface RegistrationListener extends Listener {
    /**
     * Register new variants. Ores, stones, and other variants can be registered through the consumers in the registrar.
     */
    void provideEntries(
            Registrar registrar
    );

    final class Registrar {
        public final BiConsumer<ResourceLocation, GroundType> groundTypes;
        public final BiConsumer<ResourceLocation, Stone> stones;
        public final BiConsumer<ResourceLocation, Ore> ores;
        public final BiConsumer<ResourceLocation, Modifier> modifiers;

        @ApiStatus.Internal
        public Registrar(BiConsumer<ResourceLocation, GroundType> groundTypes, BiConsumer<ResourceLocation, Stone> stones, BiConsumer<ResourceLocation, Ore> ores, BiConsumer<ResourceLocation, Modifier> modifiers) {
            this.groundTypes = groundTypes;
            this.stones = stones;
            this.ores = ores;
            this.modifiers = modifiers;
        }
    }
}
