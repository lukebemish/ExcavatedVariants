package dev.lukebemish.excavatedvariants.impl.mixin;

import dev.lukebemish.excavatedvariants.impl.KnownTiers;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TieredItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TieredItem.class)
public class TieredItemMixin {
    @Inject(
            method = "<init>(Lnet/minecraft/world/item/Tier;Lnet/minecraft/world/item/Item$Properties;)V",
            at = @At("RETURN")
    )
    private void onInit(Tier tier, Item.Properties properties, CallbackInfo ci) {
        KnownTiers.KNOWN_TIERS.put(tier.getIncorrectBlocksForDrops(), tier);
    }
}
