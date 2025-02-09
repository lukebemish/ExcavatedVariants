package dev.lukebemish.excavatedvariants.impl.fabriquilt.client;

import com.google.auto.service.AutoService;
import dev.lukebemish.excavatedvariants.impl.client.RenderTypeHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.world.level.block.Block;

@AutoService(RenderTypeHandler.class)
public class RenderTypeHandlerImpl implements RenderTypeHandler {
    public void setRenderTypeMipped(Block block) {
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
            RenderTypeClientExecutor.setMipped(block);
        }
    }
}