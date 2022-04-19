package com.github.lukebemish.excavated_variants.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.Optional;

public class BaseStone {
    public static final Codec<BaseStone> CODEC = RecordCodecBuilder.create((instance)-> instance.group(
            Codec.STRING.fieldOf("id").forGetter(bs->bs.id),
            ResourceLocation.CODEC.listOf().optionalFieldOf("texture_location").forGetter(bs->Optional.empty()),
            Codec.STRING.fieldOf("en_name").forGetter(bs-> bs.en_name),
            ResourceLocation.CODEC.fieldOf("block_id").forGetter(bs->bs.block_id),
            Codec.STRING.listOf().optionalFieldOf("types").forGetter(bs->Optional.of(bs.types)),
            Codec.STRING.listOf().optionalFieldOf("type").forGetter(bs->Optional.empty()),
            Codec.INT.optionalFieldOf("texture_count",5).forGetter(bs->bs.texture_count)
    ).apply(instance,BaseStone::new));

    public String id;
    public String en_name;
    public ResourceLocation block_id;
    public List<String> types;
    public int texture_count = 5;

    public BaseStone(String id, Optional<List<ResourceLocation>> texture_location, String en_name, ResourceLocation block_id, Optional<List<String>> type, Optional<List<String>> types, int texture_count) {
        this.id = id;
        this.en_name = en_name;
        this.block_id = block_id;
        this.types = types.isEmpty()?type.get():types.get();
        this.texture_count = texture_count;
    }

}
