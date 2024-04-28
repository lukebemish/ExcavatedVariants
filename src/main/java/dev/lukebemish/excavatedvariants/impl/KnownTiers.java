package dev.lukebemish.excavatedvariants.impl;

import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Tier;
import net.minecraft.world.level.block.Block;

import java.util.HashMap;
import java.util.Map;

public final class KnownTiers {
    private KnownTiers() {}

    public static final Map<TagKey<Block>, Tier> KNOWN_TIERS = new HashMap<>();
}
