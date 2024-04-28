package dev.lukebemish.excavatedvariants.impl.neoforge;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.common.world.ModifiableBiomeInfo;

public record AddFeatureLateModifier(HolderSet<PlacedFeature> feature) implements BiomeModifier {

    @Override
    public void modify(Holder<Biome> biome, Phase phase, ModifiableBiomeInfo.BiomeInfo.Builder builder) {
        if (phase == Phase.AFTER_EVERYTHING) {
            feature.forEach(holder ->
                    builder.getGenerationSettings().addFeature(GenerationStep.Decoration.TOP_LAYER_MODIFICATION, holder));
        }
    }

    @Override
    public MapCodec<? extends BiomeModifier> codec() {
        return ExcavatedVariantsNeoForge.ADD_FEATURE_LATE_MODIFIER.value();
    }
}
