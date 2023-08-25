/*
 * Copyright (C) 2023 Luke Bemish and contributors
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */

package dev.lukebemish.excavatedvariants.impl.forge;

import com.google.auto.service.AutoService;
import dev.lukebemish.excavatedvariants.impl.ExcavatedVariants;
import dev.lukebemish.excavatedvariants.impl.ModifiedOreBlock;
import dev.lukebemish.excavatedvariants.impl.platform.services.MainPlatformTarget;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.registries.DeferredRegister;

import java.util.function.Supplier;

@AutoService(MainPlatformTarget.class)
public class MainPlatformTargetImpl implements MainPlatformTarget {
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(Registries.RECIPE_SERIALIZER, ExcavatedVariants.MOD_ID);

    public void registerFeatures() {
    }

    public ModifiedOreBlock makeDefaultOreBlock(ExcavatedVariants.VariantFuture future) {
        return new ForgeOreBlock(future);
    }

    public <T extends Recipe<?>> Supplier<RecipeSerializer<T>> registerRecipeSerializer(String name, Supplier<RecipeSerializer<T>> supplier) {
        return RECIPE_SERIALIZERS.register(name, supplier);
    }
}
