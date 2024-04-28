package dev.lukebemish.excavatedvariants.impl.client;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.mojang.serialization.JsonOps;
import dev.lukebemish.dynamicassetgenerator.api.PathAwareInputStreamSource;
import dev.lukebemish.dynamicassetgenerator.api.ResourceGenerationContext;
import dev.lukebemish.dynamicassetgenerator.api.client.generators.TexSource;
import dev.lukebemish.excavatedvariants.impl.ExcavatedVariants;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.IoSupplier;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class TextureAtlasBuilder implements PathAwareInputStreamSource {
    private final Map<ResourceLocation, TexSource> sources = new HashMap<>();

    @Override
    public @NonNull Set<ResourceLocation> getLocations(ResourceGenerationContext context) {
        return Set.of(new ResourceLocation("minecraft", "atlases/blocks.json"));
    }

    public void addSource(ResourceLocation rl, TexSource source) {
        sources.put(rl, source);
    }

    @Override
    public @Nullable IoSupplier<InputStream> get(ResourceLocation outRl, ResourceGenerationContext context) {
        return () -> {
            Map<ResourceLocation, JsonElement> jsonMap = new HashMap<>();
            for (var entry : sources.entrySet()) {
                TexSource source = entry.getValue();
                var json = TexSource.CODEC.encodeStart(JsonOps.INSTANCE, source);
                if (json.error().isPresent()) {
                    throw new IOException("Failed to encode " + entry.getKey() + " as json: " + json.error().get());
                } else if (json.result().isPresent()) {
                    jsonMap.put(entry.getKey(), json.result().get());
                }
            }
            JsonObject root = new JsonObject();
            JsonArray sources = new JsonArray();
            JsonObject source = new JsonObject();
            JsonObject nestedSources = new JsonObject();
            for (var entry : jsonMap.entrySet()) {
                nestedSources.add(entry.getKey().toString(), entry.getValue());
            }
            source.add("sources", nestedSources);
            source.add("type", new JsonPrimitive("dynamic_asset_generator:tex_sources"));
            sources.add(source);
            root.add("sources", sources);
            return new ByteArrayInputStream(ExcavatedVariants.GSON.toJson(root).getBytes(StandardCharsets.UTF_8));
        };
    }
}
