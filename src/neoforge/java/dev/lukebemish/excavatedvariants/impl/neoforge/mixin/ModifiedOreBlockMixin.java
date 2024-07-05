package dev.lukebemish.excavatedvariants.impl.neoforge.mixin;

import dev.lukebemish.excavatedvariants.impl.ModifiedOreBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DropExperienceBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.extensions.IBlockExtension;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = ModifiedOreBlock.class, remap = false)
public abstract class ModifiedOreBlockMixin extends DropExperienceBlock implements IBlockExtension {
    @Shadow
    @Final
    protected Block target;

    @Shadow
    @Final
    protected boolean delegateSpecialDrops;

    public ModifiedOreBlockMixin(IntProvider arg, Properties pProperties) {
        super(arg, pProperties);
        throw new IllegalStateException();
    }

    @Override
    public int getExpDrop(BlockState state, LevelAccessor level, BlockPos pos, @Nullable BlockEntity blockEntity, @Nullable Entity breaker, ItemStack tool) {
        if (this.delegateSpecialDrops) {
            return target.getExpDrop(target.defaultBlockState(), level, pos, blockEntity, breaker, tool);
        } else {
            return super.getExpDrop(state, level, pos, blockEntity, breaker, tool);
        }
    }
}
