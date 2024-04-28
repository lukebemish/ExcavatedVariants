package dev.lukebemish.excavatedvariants.impl.fabriquilt.client;

import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.block.Block;

public class RenderTypeClientExecutor {
    public static void setMipped(Block block) {
        BlockRenderLayerMap.INSTANCE.putBlock(block, RenderType.cutoutMipped());
    }
}
