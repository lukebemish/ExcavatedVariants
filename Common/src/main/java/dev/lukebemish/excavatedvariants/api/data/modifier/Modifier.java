/*
 * Copyright (C) 2023 Luke Bemish and contributors
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */

package dev.lukebemish.excavatedvariants.api.data.modifier;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.lukebemish.excavatedvariants.api.data.filter.VariantFilter;
import dev.lukebemish.excavatedvariants.impl.RegistriesImpl;
import dev.lukebemish.excavatedvariants.impl.data.modifier.BlockPropsModifierImpl;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import java.util.*;

public class Modifier {
    public static final Codec<Modifier> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            VariantFilter.CODEC.fieldOf("filter").forGetter(m -> m.variantFilter),
            BlockPropsModifierImpl.CODEC.<BlockPropsModifier>flatXmap(DataResult::success, p -> {
                if (p instanceof BlockPropsModifierImpl impl)
                    return DataResult.success(impl);
                return DataResult.error(() -> "Not a serializable modifier: " + p);
            }).optionalFieldOf("properties").forGetter(m -> Optional.ofNullable(m.properties)),
            Flag.CODEC.listOf().xmap(Set::copyOf, List::copyOf).optionalFieldOf("flags", Set.of()).forGetter(m -> m.flags),
            ResourceLocation.CODEC.listOf().optionalFieldOf("tags", List.of()).forGetter(m -> m.tags)
    ).apply(instance, (filter, properties, flags, tags) -> new Modifier(filter, properties.orElse(null), tags, flags)));

    public final VariantFilter variantFilter;
    public final BlockPropsModifier properties;
    public final List<ResourceLocation> tags;
    public final Set<Flag> flags;

    public Modifier(VariantFilter variantFilter, BlockPropsModifier properties, List<ResourceLocation> tags, Set<Flag> flags) {
        this.variantFilter = variantFilter;
        this.properties = properties;
        this.tags = tags;
        this.flags = flags;
    }

    public Holder<Modifier> getHolder() {
        return RegistriesImpl.MODIFIER_REGISTRY.wrapAsHolder(this);
    }

    public final ResourceKey<Modifier> getKeyOrThrow() {
        return getHolder().unwrapKey().orElseThrow(() -> new IllegalStateException("Unregistered modifier"));
    }

    public static class Builder {
        private VariantFilter variantFilter;
        private BlockPropsModifier properties;
        private final List<ResourceLocation> tags = new ArrayList<>();
        private final Set<Flag> flags = new HashSet<>();

        public Builder setVariantFilter(VariantFilter variantFilter) {
            this.variantFilter = variantFilter;
            return this;
        }

        public Builder setProperties(BlockPropsModifier properties) {
            this.properties = properties;
            return this;
        }

        public Builder setTags(List<ResourceLocation> tags) {
            this.tags.clear();
            this.tags.addAll(tags);
            return this;
        }

        public Builder addTag(ResourceLocation tag) {
            this.tags.add(tag);
            return this;
        }

        public Builder setFlags(Collection<Flag> flags) {
            this.flags.clear();
            this.flags.addAll(flags);
            return this;
        }

        public Builder addFlag(Flag flag) {
            this.flags.add(flag);
            return this;
        }

        public Modifier build() {
            Objects.requireNonNull(this.variantFilter);
            return new Modifier(variantFilter, properties, tags, flags);
        }
    }
}
