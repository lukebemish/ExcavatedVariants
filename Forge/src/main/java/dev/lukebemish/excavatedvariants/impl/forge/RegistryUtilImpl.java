/*
 * Copyright (C) 2023 Luke Bemish and contributors
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */

package dev.lukebemish.excavatedvariants.impl.forge;

import com.google.auto.service.AutoService;
import com.google.common.base.Suppliers;
import dev.lukebemish.excavatedvariants.impl.ExcavatedVariants;
import dev.lukebemish.excavatedvariants.impl.platform.Services;
import dev.lukebemish.excavatedvariants.impl.platform.services.RegistryUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

@AutoService(RegistryUtil.class)
public class RegistryUtilImpl implements RegistryUtil {
    public static Map<ResourceLocation, Block> blockCache = new ConcurrentHashMap<>();
    public static Map<Block, ResourceLocation> blockRlCache = new ConcurrentHashMap<>();
    public static Map<ResourceLocation, Item> itemCache = new ConcurrentHashMap<>();

    public void reset() {
        blockCache.clear();
        blockRlCache.clear();
        itemCache.clear();
    }

    public Block getBlockById(ResourceLocation rl) {
        if (blockCache.containsKey(rl)) {
            return blockCache.get(rl);
        }
        if (ForgeRegistries.BLOCKS.containsKey(rl)) {
            Block out = ForgeRegistries.BLOCKS.getValue(rl);
            blockCache.put(rl, out);
            return out;
        }
        return null;
    }

    public Item getItemById(ResourceLocation rl) {
        if (itemCache.containsKey(rl)) {
            return itemCache.get(rl);
        }
        if (ForgeRegistries.ITEMS.containsKey(rl)) {
            Item out = ForgeRegistries.ITEMS.getValue(rl);
            itemCache.put(rl, out);
            return out;
        }
        return null;
    }

    public ResourceLocation getRlByBlock(Block block) {
        if (blockRlCache.containsKey(block)) {
            return blockRlCache.get(block);
        }
        ResourceLocation rl = ForgeRegistries.BLOCKS.getKey(block);
        blockRlCache.put(block, rl);
        return rl;
    }

    public Iterable<Block> getAllBlocks() {
        return ForgeRegistries.BLOCKS.getValues();
    }

    private static final Supplier<ModContainer> EV_CONTAINER = Suppliers.memoize(() -> ModList.get().getModContainerById(ExcavatedVariants.MOD_ID).orElseThrow());

    public void register(ExcavatedVariants.VariantFuture future) {
        ExcavatedVariants.registerBlockAndItem((rlr, bl) -> {
            final ModContainer activeContainer = ModLoadingContext.get().getActiveContainer();
            ModLoadingContext.get().setActiveContainer(EV_CONTAINER.get());
            ForgeRegistries.BLOCKS.register(rlr, bl);
            ModLoadingContext.get().setActiveContainer(activeContainer);
        }, (rlr, it) -> {
            ExcavatedVariantsForge.TO_REGISTER.register(rlr.getPath(), it);
            return () -> Services.REGISTRY_UTIL.getItemById(rlr);
        }, future);
    }
}
