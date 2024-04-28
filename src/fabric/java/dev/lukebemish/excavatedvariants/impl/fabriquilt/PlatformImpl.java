package dev.lukebemish.excavatedvariants.impl.fabriquilt;

import com.google.auto.service.AutoService;
import dev.lukebemish.excavatedvariants.impl.ExcavatedVariants;
import dev.lukebemish.excavatedvariants.impl.platform.services.Platform;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;

import java.nio.file.Path;
import java.util.Set;

@AutoService(Platform.class)
public class PlatformImpl implements Platform {

    @Override
    public Set<String> getModIds() {
        return FabriQuiltPlatform.getInstance().getModIds();
    }

    public Path getConfigFolder() {
        return FabricLoader.getInstance().getConfigDir();
    }

    @Override
    public Path getModDataFolder() {
        return FabriQuiltPlatform.getInstance().getCacheFolder().resolve(ExcavatedVariants.MOD_ID);
    }

    @Override
    public String getModVersion() {
        return FabriQuiltPlatform.getInstance().getModVersion();
    }

    @Override
    public boolean isClient() {
        return FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT;
    }

    @Override
    public void register(ExcavatedVariants.VariantFuture future) {
        ExcavatedVariants.registerBlockAndItem(
                (rlr, bl) -> Registry.register(BuiltInRegistries.BLOCK, rlr, bl),
                (rlr, it) -> {
                    Item out = Registry.register(BuiltInRegistries.ITEM, rlr, it.get());
                    return () -> out;
                }, future);
    }

}
