package dev.lukebemish.excavatedvariants.client;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import blue.endless.jankson.JsonObject;
import blue.endless.jankson.api.SyntaxError;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.lukebemish.dynamicassetgenerator.api.client.generators.ITexSource;
import dev.lukebemish.dynamicassetgenerator.api.client.generators.texsources.AnimationFrameCapture;
import dev.lukebemish.dynamicassetgenerator.api.client.generators.texsources.AnimationSplittingSource;
import dev.lukebemish.dynamicassetgenerator.api.client.generators.texsources.ForegroundTransfer;
import dev.lukebemish.dynamicassetgenerator.api.client.generators.texsources.Overlay;
import dev.lukebemish.dynamicassetgenerator.api.client.generators.texsources.TextureReader;
import dev.lukebemish.excavatedvariants.ExcavatedVariants;
import dev.lukebemish.excavatedvariants.api.client.Face;
import dev.lukebemish.excavatedvariants.api.client.ModelData;
import dev.lukebemish.excavatedvariants.api.client.TexFaceProvider;
import dev.lukebemish.excavatedvariants.codecs.JanksonOps;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;

// ONLY fit to be used for parsing, not for writing
public record ParsedModel(Optional<ResourceLocation> parent, Map<String, String> textures,
                          List<ElementDefinition> elements, Optional<Map<String, ParsedModel>> children) {

    public static @Nullable ResourceLocation resolveTexture(Map<String, String> map, String texName) {
        texName = texName.substring(1);
        String found = map.get(texName);
        if (found == null) return null;
        if (found.startsWith("#")) return resolveTexture(map, found);
        return ResourceLocation.of(found, ':');
    }

    public static String resolveTextureSymbol(Map<String, String> map, String texName) {
        texName = texName.substring(1);
        String found = map.get(texName);
        if (found == null) return texName;
        if (found.startsWith("#")) {
            return resolveTextureSymbol(map, found);
        }
        return texName;
    }

    public static final Codec<ParsedModel> CODEC = ExtraCodecs.lazyInitializedCodec(() -> RecordCodecBuilder.create(i -> i.group(
            ResourceLocation.CODEC.optionalFieldOf("parent").forGetter(ParsedModel::parent),
            Codec.unboundedMap(Codec.STRING, Codec.STRING).optionalFieldOf("textures", Map.of()).forGetter(ParsedModel::textures),
            ElementDefinition.CODEC.listOf().optionalFieldOf("elements", List.of()).forGetter(ParsedModel::elements),
            Codec.unboundedMap(Codec.STRING, ParsedModel.CODEC).optionalFieldOf("children").forGetter(ParsedModel::children)
    ).apply(i, ParsedModel::new)));

    @NotNull
    public static ParsedModel getFromLocation(ResourceLocation rl) throws IOException {
        try (InputStream is = BackupFetcher.getModelFile(rl)) {
            JsonObject json = ExcavatedVariants.JANKSON.load(is);
            return ParsedModel.CODEC.parse(JanksonOps.INSTANCE, json).getOrThrow(false, e -> {});
        } catch (SyntaxError | IOException | RuntimeException e) {
            throw new IOException("Could not read model " + rl, e);
        }
    }

    public Map<String, String> getTextureMap() throws IOException {
        Map<String, String> textures = new HashMap<>();
        ParsedModel parent = parent().isEmpty() ? null : getFromLocation(parent().get());
        if (parent != null)
            textures.putAll(parent.getTextureMap());
        textures.putAll(this.textures());
        return textures;
    }

    private Map<LocationKey, NamedResourceList> getRlMapForSide(String side) throws IOException {
        return getRlMapForSide(side, Map.of());
    }

    private Map<LocationKey, NamedResourceList> getRlMapForSide(String side, Map<String, String> oldTexMap) throws IOException {
        Map<LocationKey, NamedResourceList> map = new HashMap<>();

        var texMap = new HashMap<>(oldTexMap);
        texMap.putAll(getTextureMap());

        if (parent().isPresent()) {
            map.putAll(getFromLocation(parent().get()).getRlMapForSide(side, texMap));
        }

        if (children().isEmpty() || children().get().isEmpty()) {
            for (ElementDefinition definition : this.elements()) {
                if (definition.faces.containsKey(side)) {
                    String texName = resolveTextureSymbol(texMap, definition.faces.get(side).texture());
                    LocationKey key = definition.getLocationKey();
                    NamedResourceList rls = map.computeIfAbsent(key, k -> new NamedResourceList(texName));
                    rls.name = texName;
                    ResourceLocation location = resolveTexture(texMap, "#"+texName);
                    if (location != null)
                        rls.resources.add(location);
                }
            }
        } else {
            for (var h : children().get().values()) {
                var m = h.getRlMapForSide(side, texMap);
                m.forEach((key, rls) -> map.merge(key, rls, (l1, l2) -> {
                    NamedResourceList out = new NamedResourceList(l1.name);
                    out.resources.addAll(l1.resources);
                    out.resources.addAll(l2.resources);
                    return out;
                }));
            }
        }
        return map;
    }

    public record SideInformation(Set<Face> faces, List<ResourceLocation> textureStack) {}

    private Map<String, SideInformation> processIntoSides() throws IOException {
        Map<String, SideInformation> sides = new HashMap<>();
        for (Face face : Face.values()) {
            var map = getRlMapForSide(face.faceName);
            for (NamedResourceList value : map.values()) {
                sides.computeIfAbsent(value.name, k -> new SideInformation(new HashSet<>(), new ArrayList<>(value.resources)))
                        .faces.add(face);
            }
        }
        return sides;
    }

    public ModelData makeStoneModel() throws IOException {
        Map<String, SideInformation> sides = processIntoSides();
        return new StoneModelData(this, sides);
    }

    public TexFaceProvider makeTextureProvider() throws IOException {
        Map<Face, List<ResourceLocation>> map = new HashMap<>();
        for (Face face : Face.values()) {
            var rlMap = getRlMapForSide(face.faceName);
            map.put(face, rlMap.values().stream().findFirst().map(it -> it.resources).orElse(List.of()));
        }
        return face -> (newStone, oldStone) -> {
            List<ResourceLocation> oreTextures = map.get(face);
            int[] c = new int[] {0};
            Map<String, AnimationSplittingSource.TimeAwareSource> sourceMap = new HashMap<>();

            ITexSource newStoneSource = newStone.apply(source -> {
                String name = "stoneNew"+c[0];
                c[0] += 1;
                sourceMap.put(name, new AnimationSplittingSource.TimeAwareSource(source.cached(), 1));
                return new AnimationFrameCapture(name);
            });
            c[0] = 0;

            ITexSource oldStoneSource = oldStone.apply(source -> {
                String name = "stoneOld"+c[0];
                c[0] += 1;
                sourceMap.put(name, new AnimationSplittingSource.TimeAwareSource(source.cached(), 1));
                return new AnimationFrameCapture(name);
            });
            c[0] = 0;

            List<ITexSource> oreSources = new ArrayList<>();
            for (ResourceLocation location : map.get(face)) {
                String name = "ore"+c[0];
                c[0] += 1;
                sourceMap.put(name, new AnimationSplittingSource.TimeAwareSource(new TextureReader(location).cached(), 1));
                oreSources.add(new AnimationFrameCapture(name));
            }

            return new Pair<>(new AnimationSplittingSource(sourceMap, new ForegroundTransfer(
                    oldStoneSource,
                    new Overlay(oreSources),
                    newStoneSource,
                    6,
                    true,
                    true,
                    true,
                    0.2
            )).cached(), oreTextures);
        };
    }

    public record ElementDefinition(Map<String, FaceDefinition> faces, List<Integer> from, List<Integer> to,
                                    RotationDefinition rotation) {
        public static final Codec<ElementDefinition> CODEC = RecordCodecBuilder.create(i -> i.group(
                Codec.unboundedMap(Codec.STRING, FaceDefinition.CODEC).optionalFieldOf("faces", Map.of()).forGetter(ElementDefinition::faces),
                Codec.INT.listOf().optionalFieldOf("from", List.of(0, 0, 0)).forGetter(ElementDefinition::from),
                Codec.INT.listOf().optionalFieldOf("to", List.of(16, 16, 16)).forGetter(ElementDefinition::to),
                RotationDefinition.CODEC.optionalFieldOf("rotation", new RotationDefinition(List.of(0, 0, 0), "x", 0)).forGetter(ElementDefinition::rotation)
        ).apply(i, ElementDefinition::new));

        public LocationKey getLocationKey() {
            List<Integer> from = Stream.of(0, 1, 2).map(i -> Math.min(from().get(i), to().get(i))).toList();
            List<Integer> to = Stream.of(0, 1, 2).map(i -> Math.max(from().get(i), to().get(i))).toList();
            return new LocationKey(rotation().origin(), rotation().axis(), rotation().angle(),
                    from, to);
        }
    }

    public record RotationDefinition(List<Integer> origin, String axis, float angle) {
        public static final Codec<RotationDefinition> CODEC = RecordCodecBuilder.create(i -> i.group(
                Codec.INT.listOf().fieldOf("origin").forGetter(RotationDefinition::origin),
                Codec.STRING.fieldOf("axis").forGetter(RotationDefinition::axis),
                Codec.FLOAT.fieldOf("angle").forGetter(RotationDefinition::angle)
        ).apply(i, RotationDefinition::new));
    }

    public record LocationKey(List<Integer> origin, String axis, float angle, List<Integer> of, List<Integer> to) {}

    public static class NamedResourceList {
        public String name;
        public final List<ResourceLocation> resources;

        public NamedResourceList(String name) {
            this.name = name;
            this.resources = new ArrayList<>();
        }
    }

    public record FaceDefinition(String texture) {
        public static final Codec<FaceDefinition> CODEC = RecordCodecBuilder.create(i -> i.group(
                Codec.STRING.fieldOf("texture").forGetter(FaceDefinition::texture)
        ).apply(i, FaceDefinition::new));
    }


}
