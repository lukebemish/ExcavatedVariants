package com.github.lukebemish.excavated_variants;

import com.github.lukebemish.dynamic_asset_generator.client.api.DynAssetGeneratorClientAPI;
import com.github.lukebemish.excavated_variants.client.BlockStateAssembler;
import com.github.lukebemish.excavated_variants.data.BaseOre;
import com.github.lukebemish.excavated_variants.data.BaseStone;
import com.github.lukebemish.excavated_variants.data.ModData;
import com.github.lukebemish.excavated_variants.util.Pair;
import dev.architectury.platform.Platform;
import net.minecraft.resources.ResourceLocation;

import java.util.*;

public class ExcavatedVariantsClient {
    public static void init() {
        LangBuilder langBuilder = new LangBuilder();
        Collection<String> modids = Platform.getModIds();
        Map<String, BaseStone> stoneMap = new HashMap<>();
        Map<String, List<BaseOre>> oreMap = new HashMap<>();
        for (ModData mod : ExcavatedVariants.getConfig().mods) {
            if (modids.contains(mod.mod_id)) {
                for (BaseStone stone : mod.provided_stones) {
                    if (!ExcavatedVariants.getConfig().blacklist_stones.contains(stone.id)) {
                        stoneMap.put(stone.id, stone);
                    }
                }
                for (BaseOre ore : mod.provided_ores) {
                    if (!ExcavatedVariants.getConfig().blacklist_ores.contains(ore.id)) {
                        oreMap.computeIfAbsent(ore.id, k -> new ArrayList<>());
                        oreMap.get(ore.id).add(ore);
                    }
                }
            }
        }
        List<String> extractedOres = new ArrayList<>();
        Map<String, Pair<BaseOre,BaseStone>> extractorMap = new HashMap<>();
        for (ModData mod : ExcavatedVariants.getConfig().mods) {
            if (modids.contains(mod.mod_id)) {
                Map<String, BaseStone> internalStoneMap = new HashMap<>();
                for (BaseStone stone : mod.provided_stones) {
                    if (!ExcavatedVariants.getConfig().blacklist_stones.contains(stone.id)) {
                        internalStoneMap.put(stone.id, stone);
                    }
                }
                for (BaseOre ore : mod.provided_ores) {
                    if (!extractedOres.contains(ore.id)) {
                        for (String stone_id : ore.stone) {
                            if (internalStoneMap.get(stone_id) != null && extractorMap.get(ore.id) == null) {
                                var extractor = new Pair<>(ore,internalStoneMap.get(stone_id));
                                extractorMap.put(ore.id, extractor);
                            } else if (stoneMap.get(stone_id) != null && extractorMap.get(ore.id) == null) {
                                var extractor = new Pair<>(ore,stoneMap.get(stone_id));
                                extractorMap.put(ore.id, extractor);
                            }
                        }
                        extractedOres.add(ore.id);
                    }
                }
            }
        }
        for (List<BaseOre> oreList : oreMap.values()) {
            BaseOre ore = oreList.get(0);
            for (String stone_id : ore.stone) {
                if (stoneMap.get(stone_id) != null && extractorMap.get(ore.id) == null) {
                    var extractor = new Pair<>(ore,stoneMap.get(stone_id));
                    extractorMap.put(ore.id, extractor);
                }
            }
        }
        List<Pair<BaseOre,BaseStone>> to_make = new ArrayList<>();
        for (String id : oreMap.keySet()) {
            List<BaseOre> oreList = oreMap.get(id);
            List<String> stones = new ArrayList<>();
            for (BaseOre ore : oreList) {
                stones.addAll(ore.stone);
            }
            for (BaseStone stone : stoneMap.values()) {
                if (!stones.contains(stone.id) && oreList.stream().anyMatch((x)->x.types.stream().anyMatch(stone.types::contains))) {
                    String full_id = stone.id+"_"+id;
                    if (!ExcavatedVariants.getConfig().blacklist_ids.contains(full_id)) {
                        to_make.add(new Pair<>(oreList.get(0),stone));
                        DynAssetGeneratorClientAPI.planLoadingStream(new ResourceLocation(ExcavatedVariants.MOD_ID, "models/item/" + full_id + ".json"),
                                JsonHelper.getItemModel(full_id));
                        langBuilder.add(full_id, stone, oreList.get(0));
                    }
                }
            }
        }
        DynAssetGeneratorClientAPI.planLoadingStream(new ResourceLocation(ExcavatedVariants.MOD_ID, "lang/en_us.json"),langBuilder.build());
        BlockStateAssembler.setupClientAssets(extractorMap.values(),to_make);
    }
}
