package dev.lukebemish.excavatedvariants.impl.neoforge;


import dev.lukebemish.excavatedvariants.impl.worldgen.OreFinderUtil;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;

public class EventHandler {
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onServerStarting(ServerAboutToStartEvent event) {
        //Ore Gen
        OreFinderUtil.setupBlocks();
    }

    // Look into alternatives in the future
    /*
    @SubscribeEvent
    public static void mapMissingVariants(MissingMappingsEvent missingMappingsEvent) {
        missingMappingsEvent.getAllMappings(ForgeRegistries.Keys.ITEMS).forEach(EventHandler::remap);
        missingMappingsEvent.getAllMappings(ForgeRegistries.Keys.BLOCKS).forEach(EventHandler::remap);
    }


    private static <T> void remap(MissingMappingsEvent.Mapping<T> mapping) {
        if (mapping.getKey().getNamespace().equals(ExcavatedVariants.MOD_ID)) {
            List<ResourceLocation> locations = ExcavatedVariants.MAPPINGS_CACHE.get(mapping.getKey().getPath());
            T target = null;
            for (ResourceLocation location : locations) {
                T value = mapping.getRegistry().getValue(location);
                if (value != null) {
                    target = value;
                    break;
                }
            }
            if (target != null) {
                mapping.remap(target);
            }
        }
    }
    */
}
