package dev.lukebemish.excavatedvariants.impl.fabriquilt;

import dev.lukebemish.excavatedvariants.impl.ExcavatedVariants;
import dev.lukebemish.excavatedvariants.impl.ModLifecycle;
import net.fabricmc.api.ModInitializer;

public class StateCapturer implements ModInitializer {
    private static boolean INITIALIZED = false;

    @Override
    public void onInitialize() {
        INITIALIZED = true;
    }

    public static void checkState() {
        if (ModLifecycle.getLifecyclePhase().above(ModLifecycle.REGISTRATION) || !INITIALIZED) {
            var e = new RuntimeException("Something has gone very wrong with load ordering, and we have no clue what is going on. Please report this to Excavated Variants and be sure to provide a log!");
            ExcavatedVariants.LOGGER.error("...what the heck? Where are we? Lifecycle state: {}, initialized: {}", ModLifecycle.getLifecyclePhase(), INITIALIZED, e);
            throw e;
        }
    }
}
