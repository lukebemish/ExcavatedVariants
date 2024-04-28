package dev.lukebemish.excavatedvariants.impl;

public enum ModLifecycle {
    PRE_INITIALIZATION,
    PRE_REGISTRATION,
    REGISTRATION,
    POST;

    private static ModLifecycle LOAD_STATE = PRE_INITIALIZATION;

    static synchronized void setLifecyclePhase(ModLifecycle state) {
        LOAD_STATE = state;
    }

    public static ModLifecycle getLifecyclePhase() {
        return LOAD_STATE;
    }

    public boolean above(ModLifecycle state) {
        return this.ordinal() > state.ordinal();
    }
}
