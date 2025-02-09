package dev.lukebemish.excavatedvariants.impl.client;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import dev.lukebemish.dynamicassetgenerator.api.client.generators.texsources.OverlaySource;
import dev.lukebemish.dynamicassetgenerator.api.client.generators.texsources.TextureReaderSource;
import dev.lukebemish.excavatedvariants.api.client.ModelData;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

class StoneModelData implements ModelData {
    private final ParsedModel parsedModel;
    private final Map<String, ParsedModel.SideInformation> sides;

    public StoneModelData(ParsedModel parsedModel, Map<String, ParsedModel.SideInformation> sides) {
        this.parsedModel = parsedModel;
        this.sides = sides;
    }

    @Override
    public JsonElement assembleModel(Map<String, ResourceLocation> textures) {
        Map<String, String> textureMap = new HashMap<>(parsedModel.textures());
        for (Map.Entry<String, ResourceLocation> entry : textures.entrySet()) {
            textureMap.put(entry.getKey(), entry.getValue().toString());
        }
        ParsedModel newModel = new ParsedModel(parsedModel.parent(), textureMap, parsedModel.elements(), parsedModel.children());
        return ParsedModel.CODEC.encodeStart(JsonOps.INSTANCE, newModel).result().orElse(new JsonObject());
    }

    @Override
    public void produceTextures(TextureConsumer textureProducerConsumer) {
        for (Map.Entry<String, ParsedModel.SideInformation> entry : sides.entrySet()) {
            String name = entry.getKey();
            if (!parsedModel.textures().containsKey(name)) {
                continue;
            }

            ParsedModel.SideInformation info = entry.getValue();
            List<ResourceLocation> stack = info.textureStack();
            if (stack.isEmpty()) {
                continue;
            }

            textureProducerConsumer.accept(name, sourceWrapper ->
                    new OverlaySource.Builder()
                            .setSources(stack.stream()
                                    .map(rl ->
                                            sourceWrapper.wrap(
                                                    new TextureReaderSource.Builder()
                                                            .setPath(rl)
                                                            .build()))
                                    .toList()).build(), info.faces());
        }
    }
}
