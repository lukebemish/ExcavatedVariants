package io.github.lukebemish.excavated_variants.forge;

import io.github.lukebemish.excavated_variants.ModifiedOreBlock;
import io.github.lukebemish.excavated_variants.data.BaseOre;
import io.github.lukebemish.excavated_variants.data.BaseStone;
import io.github.lukebemish.excavated_variants.platform.Services;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.extensions.IForgeBlock;
import org.jetbrains.annotations.NotNull;

public class ForgeOreBlock extends ModifiedOreBlock implements IForgeBlock {
    public ForgeOreBlock(BaseOre ore, BaseStone stone) {
        super(ore, stone);
    }

    @Override
    public int getExpDrop(@NotNull BlockState state, @NotNull LevelReader level, @NotNull BlockPos pos, int fortune, int silktouch) {
        Block target = Services.REGISTRY_UTIL.getBlockById(ore.block_id.get(0));
        if (target != null) {
            return target.getExpDrop(target.defaultBlockState(), level, pos, fortune, silktouch);
        } else {
            return super.getExpDrop(state,level,pos,fortune,silktouch);
        }
    }
}
