package dev.lukebemish.excavatedvariants.api;

import org.jspecify.annotations.NonNull;

/**
 * An implementation of any number of different events that can be fired by Excavated Variants, which mods can provide
 * their own implementations of to listen to. Different subclasses of this interface are used for different types of
 * events, and are discovered as services by Excavated Variants.
 */
public interface Listener extends Comparable<Listener> {
    default int priority() {
        return 0;
    }

    @Override
    default int compareTo(@NonNull Listener o) {
        return o.priority() - this.priority();
    }
}
