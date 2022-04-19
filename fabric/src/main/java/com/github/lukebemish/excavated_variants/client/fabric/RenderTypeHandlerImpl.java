package com.github.lukebemish.excavated_variants.client.fabric;

import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.block.Block;

public class RenderTypeHandlerImpl {
    public static void setRenderTypeMipped(Block block) {
        BlockRenderLayerMap.INSTANCE.putBlock(block, RenderType.cutoutMipped());
    }
}