package dev.lukebemish.excavatedvariants.impl.platform.services;

import dev.lukebemish.excavatedvariants.impl.ExcavatedVariants;

import java.nio.file.Path;
import java.util.Set;

public interface Platform {

    Path getConfigFolder();
    Path getModDataFolder();

    String getModVersion();

    boolean isClient();
    Set<String> getModIds();

    void register(ExcavatedVariants.VariantFuture future);
}
