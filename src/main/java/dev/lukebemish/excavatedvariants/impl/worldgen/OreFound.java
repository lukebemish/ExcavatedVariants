package dev.lukebemish.excavatedvariants.impl.worldgen;

import dev.lukebemish.excavatedvariants.api.data.Ore;
import dev.lukebemish.excavatedvariants.api.data.Stone;
import org.jspecify.annotations.Nullable;

public interface OreFound {
    @Nullable Ore excavated_variants$getOre();

    void excavated_variants$setOre(Ore o);

    @Nullable Stone excavated_variants$getOreStone();

    void excavated_variants$setOreStone(Stone o);

    @Nullable Stone excavated_variants$getStone();

    void excavated_variants$setStone(Stone stone);
}
