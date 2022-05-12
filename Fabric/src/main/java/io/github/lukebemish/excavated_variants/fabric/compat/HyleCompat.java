package io.github.lukebemish.excavated_variants.fabric.compat;

import io.github.lukebemish.excavated_variants.ExcavatedVariants;
import io.github.lukebemish.excavated_variants.data.BaseOre;
import io.github.lukebemish.excavated_variants.data.BaseStone;
import io.github.lukebemish.excavated_variants.platform.Services;
import io.github.lukebemish.excavated_variants.util.Pair;
import lilypuree.hyle.compat.StoneTypeCallback;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Map;

public class HyleCompat {
    private static Map<Block, Pair<BaseOre, HashSet<BaseStone>>> excavatedVariantsOres;

    public static void init() {
        StoneTypeCallback.EVENT.register(stoneType -> {
            if (excavatedVariantsOres == null) {
                excavatedVariantsOres = new IdentityHashMap<>();
                for (Pair<BaseOre, HashSet<BaseStone>> p : ExcavatedVariants.oreStoneList) {
                    for (ResourceLocation block_id : p.first().block_id) {
                        Block baseOreBlock = Services.REGISTRY_UTIL.getBlockById(block_id);
                        excavatedVariantsOres.put(baseOreBlock, p);
                    }
                }
            }

            Block baseBlock = stoneType.getBaseBlock().getBlock();
            excavatedVariantsOres.forEach((baseOreBlock, pair) -> {
                HashSet<BaseStone> stones = pair.last();
                stones.stream().filter(stone -> Services.REGISTRY_UTIL.getBlockById(stone.block_id) == baseBlock).findAny().ifPresent(stone -> {
                    Block oreBlock = Services.REGISTRY_UTIL.getBlockById(new ResourceLocation(ExcavatedVariants.MOD_ID, stone.id + "_" + pair.first().id));
                    if (!stoneType.getOreMap().containsKey(baseOreBlock))
                        stoneType.getOreMap().put(baseOreBlock, oreBlock.defaultBlockState());
                });
            });
            return true;
        });
    }
}
