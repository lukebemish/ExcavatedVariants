package dev.lukebemish.excavatedvariants.impl;

import com.google.gson.JsonParser;
import com.mojang.serialization.JsonOps;
import dev.lukebemish.dynamicassetgenerator.api.ResourceGenerationContext;
import dev.lukebemish.dynamicassetgenerator.api.sources.TagSupplier;
import dev.lukebemish.dynamicassetgenerator.api.templates.TagFile;
import dev.lukebemish.excavatedvariants.api.data.Ore;
import dev.lukebemish.excavatedvariants.api.data.Stone;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagEntry;
import net.minecraft.tags.TagKey;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class MiningLevelTagHolder implements TagSupplier {
    private final ArrayList<CheckPair> toCheck = new ArrayList<>();
    public void add(String fullId, Ore ore, Stone stone) {
        toCheck.add(new CheckPair(fullId, ore, stone));
    }

    private Set<ResourceLocation> getMiningLevels(ResourceGenerationContext context) {
        return KnownTiers.KNOWN_TIERS.keySet().stream().map(TagKey::location).collect(Collectors.toSet());
    }

    @Override
    public Map<ResourceLocation, Set<ResourceLocation>> apply(ResourceGenerationContext context) {
        Map<ResourceLocation, Set<ResourceLocation>> tags = new HashMap<>();

        Set<ResourceLocation> tagNames = getMiningLevels(context);
        Map<ResourceLocation, Set<ResourceLocation>> tagToMemberMap = tagNames.stream().collect(Collectors.toMap(Function.identity(), name -> getTagMembers(name, context), (l1, l2) -> {
            Set<ResourceLocation> out = new HashSet<>(l1);
            out.addAll(l2);
            return out;
        }));

        for (ResourceLocation tierTag : tagNames) {
            var members = tagToMemberMap.get(tierTag);
            for (var pair : toCheck) {
                if (members.contains(pair.stone.block.location()) || pair.ore.getBlocks().keySet().stream().map(ResourceKey::location).allMatch(members::contains)) {
                    tags.computeIfAbsent(tierTag, k->new HashSet<>()).add(new ResourceLocation(ExcavatedVariants.MOD_ID, pair.fullId));
                }
            }
        }

        return tags;
    }

    private record CheckPair(String fullId, Ore ore, Stone stone) {
    }

    private Set<ResourceLocation> getTagMembers(ResourceLocation location, ResourceGenerationContext context) {
        String type = location.getPath().split("/")[0];
        Set<ResourceLocation> members = new HashSet<>();
        var toRead = new ResourceLocation(location.getNamespace(), "tags/"+location.getPath()+".json");
        var foundResources = context.getResourceSource().getResourceStack(toRead);
        for (var ioSupplier : foundResources) {
            try (var is = ioSupplier.get();
                 var reader = new InputStreamReader(is, StandardCharsets.UTF_8)) {
                var parser = JsonParser.parseReader(reader);
                try {
                    TagFile file = TagFile.CODEC.parse(JsonOps.INSTANCE, parser).getOrThrow();
                    if (file.replace())
                        members.clear();
                    file.values().forEach(value ->
                            value.build(new TagEntry.Lookup<ResourceLocation>() {
                                @Override
                                public ResourceLocation element(ResourceLocation elementLocation) {
                                    return elementLocation;
                                }

                                @Override
                                public Collection<ResourceLocation> tag(ResourceLocation tagLocation) {
                                    return getTagMembers(new ResourceLocation(tagLocation.getNamespace(), type+"/"+tagLocation.getPath()), context);
                                }
                            }, members::add)
                    );
                } catch (RuntimeException e) {
                    ExcavatedVariants.LOGGER.error("Issue parsing tag at '{}':",toRead,e);
                }
            } catch (IOException e) {
                ExcavatedVariants.LOGGER.error("Issue reading tag at '{}':",toRead,e);
            }
        }
        return members;
    }
}
