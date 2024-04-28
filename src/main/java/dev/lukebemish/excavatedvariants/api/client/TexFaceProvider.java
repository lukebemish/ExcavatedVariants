package dev.lukebemish.excavatedvariants.api.client;

import org.jspecify.annotations.NonNull;

/**
 * Represents the model of an ore block; used to provide a {@link TextureProducer} for any face of the block.
 */
@FunctionalInterface
public interface TexFaceProvider {
    @NonNull TextureProducer get(Face face);
}
