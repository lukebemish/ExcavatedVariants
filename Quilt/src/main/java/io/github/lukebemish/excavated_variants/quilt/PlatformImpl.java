package io.github.lukebemish.excavated_variants.quilt;

import com.google.auto.service.AutoService;
import io.github.lukebemish.dynamic_asset_generator.api.ServerPrePackRepository;
import io.github.lukebemish.excavated_variants.IPlatform;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.mininglevel.v1.MiningLevelManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.tags.BlockTags;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.loader.api.ModMetadata;
import org.quiltmc.loader.api.QuiltLoader;
import org.quiltmc.loader.api.minecraft.MinecraftQuiltLoader;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@AutoService(IPlatform.class)
public class PlatformImpl implements IPlatform {
    public boolean isQuilt() {
        return true;
    }

    public boolean isForge() {
        return false;
    }

    public Collection<String> getModIds() {
        return QuiltLoader.getAllMods().stream().map(ModContainer::metadata).map(ModMetadata::id).toList();
    }

    public Path getConfigFolder() {
        return QuiltLoader.getConfigDir().toAbsolutePath().normalize();
    }

    @Override
    public Path getModDataFolder() {
        return QuiltLoader.getGameDir().resolve("mod_data/excavated_variants").toAbsolutePath().normalize();
    }

    @Override
    public boolean isClient() {
        return MinecraftQuiltLoader.getEnvironmentType() == EnvType.CLIENT;
    }

    @Override
    public List<ResourceLocation> getMiningLevels() {
        List<ResourceLocation> out = new ArrayList<>();
        out.add(BlockTags.NEEDS_STONE_TOOL.location());
        out.add(BlockTags.NEEDS_IRON_TOOL.location());
        out.add(BlockTags.NEEDS_STONE_TOOL.location());
        ServerPrePackRepository.getResources().stream().flatMap(resources ->
           resources.getResources(PackType.SERVER_DATA, "fabric", "minecraft/tags/blocks/", s->{
               if (!s.getPath().startsWith("needs_tool_level_")) return false;
               try {
                   String[] parts = s.getPath().split("/");
                   Integer.parseInt(parts[parts.length-1].replace("needs_tool_level_",""));
                   return true;
               } catch (NumberFormatException e) {
                   return false;
               }
           }).stream().map(l->{
               String[] parts = l.getPath().split("/");
               return Integer.parseInt(parts[parts.length-1].replace("needs_tool_level_",""));
           }).filter(it->it!=0)
        ).distinct().sorted().forEach(it->out.add(MiningLevelManager.getBlockTag(it).location()));
        return out;
    }
}
