package com.github.lukebemish.excavated_variants;

import com.github.lukebemish.excavated_variants.data.BaseOre;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.github.lukebemish.dynamic_asset_generator.api.ResettingSupplier;
import com.github.lukebemish.dynamic_asset_generator.api.ServerPrePackRepository;
import net.minecraft.resources.ResourceLocation;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class MiningLevelTagGenerator implements ResettingSupplier<InputStream> {
    private final String level;
    private final ArrayList<CheckPair> toCheck = new ArrayList<>();

    private String internal;

    private record CheckPair(String full_id, String base_id) { }

    public MiningLevelTagGenerator(String level) {
        this.level = level;
    }

    public void add(String full_id, BaseOre ore) {
        toCheck.add(new CheckPair(full_id, ore.block_id.get(0).toString()));
    }

    @Override
    public InputStream get() {
        if (internal == null) {
            internal = "";
            try {
                List<InputStream> read = ServerPrePackRepository.getResources(new ResourceLocation("minecraft", "tags/blocks/needs_" + level + "_tool.json"));
                ArrayList<String> to_add = new ArrayList<>();
                for (InputStream is : read) {
                    StringBuilder textBuilder = new StringBuilder();
                    Reader reader = new BufferedReader(new InputStreamReader
                            (is, Charset.forName(StandardCharsets.UTF_8.name())));
                    int c = 0;
                    while ((c = reader.read()) != -1) {
                        textBuilder.append((char) c);
                    }
                    String readStr = textBuilder.toString();
                    JsonElement parser = JsonParser.parseString(readStr);
                    if (parser.isJsonObject()) {
                        JsonElement replace = parser.getAsJsonObject().get("replace");
                        if (!(replace == null) && replace.isJsonPrimitive() && replace.getAsJsonPrimitive().isBoolean() && replace.getAsBoolean()) {
                            to_add.clear();
                        }
                        JsonElement values = parser.getAsJsonObject().get("values");
                        if (values.isJsonArray()) {
                            for (JsonElement i : values.getAsJsonArray()) {
                                if (i.isJsonPrimitive()) {
                                    if (i.getAsJsonPrimitive().isString()) {
                                        String str = i.getAsJsonPrimitive().getAsString();
                                        for (CheckPair j : toCheck) {
                                            if (j.base_id.equals(str)) {
                                                to_add.add(j.full_id);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                if (to_add.size()==0) {
                    internal = "{\n  \"replace\":false,\n  \"values\":[]\n}";
                    final String finalStr = internal;
                    internal = null;
                    return new ByteArrayInputStream(finalStr.getBytes());
                }
                StringBuilder vals = new StringBuilder();
                boolean counter = false;
                for (String full_id : to_add) {
                    if (counter) {
                        vals.append(",\n    ");
                    }
                    vals.append("\"").append(ExcavatedVariants.MOD_ID).append(":").append(full_id).append("\"");
                    counter = true;
                }
                internal = "{\n  \"replace\":false,\n  \"values\":[\n    "+ vals +"\n  ]\n}";
            } catch (IOException e) {
                ExcavatedVariants.LOGGER.error("Could not load mining level tag for {}; erroring...\n{}", level, e);
                internal = "{\n  \"replace\":false,\n  \"values\":[]\n}";
                final String finalStr = internal;
                internal = null;
                return new ByteArrayInputStream(finalStr.getBytes());
            }
        }
        String finalStr = internal;
        return new ByteArrayInputStream(finalStr.getBytes());
    }

    @Override
    public void reset() {
        internal = null;
    }
}
