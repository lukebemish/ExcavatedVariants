package dev.lukebemish.excavatedvariants.impl.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.List;

@Mixin(BlockBehaviour.class)
public interface BlockBehaviorAccessor {
    @Invoker("getDrops")
    List<ItemStack> excavated_variants$getDrops(BlockState state, LootParams.Builder params);

    @Invoker("spawnAfterBreak")
    void excavated_variants$spawnAfterBreak(BlockState state, ServerLevel level, BlockPos pos, ItemStack stack, boolean bl);
}
