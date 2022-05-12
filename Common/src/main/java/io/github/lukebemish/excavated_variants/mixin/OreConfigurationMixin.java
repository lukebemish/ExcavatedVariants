package io.github.lukebemish.excavated_variants.mixin;

import io.github.lukebemish.excavated_variants.ExcavatedVariants;
import io.github.lukebemish.excavated_variants.data.BaseOre;
import io.github.lukebemish.excavated_variants.data.BaseStone;
import io.github.lukebemish.excavated_variants.platform.Services;
import io.github.lukebemish.excavated_variants.util.Pair;
import io.github.lukebemish.excavated_variants.worldgen.IOreFound;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockMatchTest;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Mixin(OreConfiguration.class)
public abstract class OreConfigurationMixin {

    @ModifyVariable(method="<init>",at=@At(value = "HEAD"),argsOnly = true)
    private static List<OreConfiguration.TargetBlockState> excavated_variants_oreConfigInit(List<OreConfiguration.TargetBlockState> targetStates) {
        if (ExcavatedVariants.getConfig().attempt_ore_generation_insertion) {
            Pair<BaseOre, HashSet<BaseStone>> pair = null;
            for (OreConfiguration.TargetBlockState tbs : targetStates) {
                pair = ((IOreFound)tbs.state.getBlock()).excavated_variants$get_pair();
                if (pair != null) {
                    break;
                }
            }
            if (pair!=null) {
                HashSet<BaseStone> stoneList = pair.last();
                BaseOre ore = pair.first();
                ArrayList<OreConfiguration.TargetBlockState> outList = new ArrayList<>(targetStates);
                for (BaseStone stone : stoneList) {
                    Block oreBlock = Services.REGISTRY_UTIL.getBlockById(new ResourceLocation(ExcavatedVariants.MOD_ID, stone.id + "_" + ore.id));
                    Block stoneBlock = Services.REGISTRY_UTIL.getBlockById(stone.block_id);
                    if (oreBlock != null && stoneBlock != null) {
                        OreConfiguration.TargetBlockState state = OreConfiguration.target(new BlockMatchTest(stoneBlock), oreBlock.defaultBlockState());
                        outList.add(0,state);
                    }
                }
                targetStates = outList;
            }
        }
        return targetStates;
    }
}
