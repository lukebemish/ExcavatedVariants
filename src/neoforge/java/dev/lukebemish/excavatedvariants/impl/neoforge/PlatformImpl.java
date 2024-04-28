package dev.lukebemish.excavatedvariants.impl.neoforge;

import com.google.auto.service.AutoService;
import com.google.common.base.Suppliers;
import dev.lukebemish.excavatedvariants.impl.ExcavatedVariants;
import dev.lukebemish.excavatedvariants.impl.platform.services.Platform;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforgespi.language.IModInfo;

import java.nio.file.Path;
import java.util.Set;
import java.util.function.Supplier;

@AutoService(Platform.class)
public class PlatformImpl implements Platform {
    private static final String MOD_VERSION = ModList.get().getModFileById(ExcavatedVariants.MOD_ID).versionString();

    @Override
    public Path getConfigFolder() {
        return FMLPaths.CONFIGDIR.get();
    }
    @Override
    public Path getModDataFolder() {
        return FMLPaths.GAMEDIR.get().resolve(".cache").resolve(ExcavatedVariants.MOD_ID);
    }

    @Override
    public String getModVersion() {
        return MOD_VERSION;
    }

    @Override
    public boolean isClient() {
        return FMLLoader.getDist() == Dist.CLIENT;
    }

    private static final Supplier<Set<String>> MOD_IDS = Suppliers.memoize(() -> Set.copyOf(ModList.get().getMods().stream().map(IModInfo::getModId).toList()));
    @Override
    public Set<String> getModIds() {
        return MOD_IDS.get();
    }

    public void register(ExcavatedVariants.VariantFuture future) {
        ExcavatedVariants.registerBlockAndItem((rlr, bl) -> {
            Registry.register(BuiltInRegistries.BLOCK, rlr, bl);
        }, (rlr, it) -> ExcavatedVariantsNeoForge.TO_REGISTER.register(rlr.getPath(), it), future);
    }
}
