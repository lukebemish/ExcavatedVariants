package dev.lukebemish.excavatedvariants.impl.fabriquilt;

import com.google.auto.service.AutoService;
import dev.lukebemish.excavatedvariants.impl.ExcavatedVariants;
import dev.lukebemish.excavatedvariants.impl.platform.services.CreativeTabLoader;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

@SuppressWarnings("unused")
@AutoService(CreativeTabLoader.class)
public class CreativeTabLoaderImpl implements CreativeTabLoader {
    private static final int MAX_INT_VAL = (int) Math.sqrt(Integer.MAX_VALUE);
    public static @Nullable CreativeModeTab EXCAVATED_VARIANTS_TAB;

    @Override
    public void registerCreativeTab() {
        EXCAVATED_VARIANTS_TAB = FabricItemGroup.builder()
                .icon(() -> new ItemStack(Items.DEEPSLATE_COPPER_ORE))
                .displayItems((displayParameters, output) -> {
                    for (var supplier : ExcavatedVariants.ITEMS) {
                        output.accept(supplier.get());
                    }
                })
                .title(Component.translatable("itemGroup.excavated_variants.excavated_variants"))
                .build();
        Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, CREATIVE_TAB_ID, EXCAVATED_VARIANTS_TAB);
    }

    public CreativeModeTab getCreativeTab() {
        return Objects.requireNonNull(EXCAVATED_VARIANTS_TAB);
    }
}
