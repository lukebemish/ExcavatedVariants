/*
 * Copyright (C) 2023 Luke Bemish and contributors
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */

package dev.lukebemish.excavatedvariants.api;

import dev.lukebemish.excavatedvariants.api.data.GroundType;
import dev.lukebemish.excavatedvariants.api.data.Ore;
import dev.lukebemish.excavatedvariants.api.data.Stone;
import dev.lukebemish.excavatedvariants.api.data.modifier.Modifier;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.BiConsumer;

// TODO: docs
public interface RegistryListener extends CommonListener {
    void provideEntries(
            Registrar registrar
    );

    final class Registrar {
        public final BiConsumer<ResourceLocation, GroundType> groundTypes;
        public final BiConsumer<ResourceLocation, Stone> stoneTypes;
        public final BiConsumer<ResourceLocation, Ore> oreTypes;
        public final BiConsumer<ResourceLocation, Modifier> modifiers;

        @ApiStatus.Internal
        public Registrar(BiConsumer<ResourceLocation, GroundType> groundTypes, BiConsumer<ResourceLocation, Stone> stoneTypes, BiConsumer<ResourceLocation, Ore> oreTypes, BiConsumer<ResourceLocation, Modifier> modifiers) {
            this.groundTypes = groundTypes;
            this.stoneTypes = stoneTypes;
            this.oreTypes = oreTypes;
            this.modifiers = modifiers;
        }
    }
}
