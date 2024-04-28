package dev.lukebemish.excavatedvariants.api.client;

import dev.lukebemish.dynamicassetgenerator.api.client.generators.TexSource;

import java.util.function.Function;

/**
 * A function which produces a new ore texture source from the given texture sources.
 */
@FunctionalInterface
public interface TextureProducer {
    /**
     * Produces a new ore texture source from the given texture sources.
     * @param newStoneSource the texture source of the stone to be transferred to
     * @param oldStoneSource the texture source of the stone to be transferred from
     * @return the new texture source
     */
    TexSource produce(Function<SourceWrapper, TexSource> newStoneSource, Function<SourceWrapper, TexSource> oldStoneSource);

    interface SourceWrapper {
        TexSource wrap(TexSource source);
    }
}
